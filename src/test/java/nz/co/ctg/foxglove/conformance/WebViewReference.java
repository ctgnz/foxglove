package nz.co.ctg.foxglove.conformance;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import nz.co.ctg.foxglove.JavaFxTestSupport;

import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * A browser engine rendering the same document, seekable to a chosen moment - the reference #112 compares against.
 * <p>
 * It exists because the W3C suite's own reference PNGs cannot serve as one for animation: each was captured at some
 * moment in the animation that nothing in the document records, so a correct implementation and a broken one are
 * equally likely to mismatch. A live engine has no such problem, because both sides can be told which moment to be
 * at.
 * <p>
 * <b>The Stage is shown deliberately, and the check cannot work without it.</b> Verified before the design was
 * settled on: with a plain off-screen {@link Scene} every {@code snapshot} came back blank, while with
 * {@code stage.show()} a rect animating {@code x} from 0 to 80 over ten seconds rendered at exactly {@code 8t} at
 * every sample. That is the whole reason this lives behind the {@code conformance} profile rather than in the
 * default build - it needs a display, which CI already provides via {@code xvfb-run}.
 * <p>
 * One Stage and one {@link WebView} serve the entire run: creating either per document leaks, and each document is
 * loaded once and then seeked repeatedly rather than reloaded per sample.
 */
public final class WebViewReference implements AutoCloseable {

    /**
     * How long to let WebKit repaint after a seek before capturing. There is no "repainted" signal to await - the
     * seek returns as soon as the clock moves, not when the frame is on screen - so this is empirical. Too short and
     * captures show the previous frame, which would read as an animation error rather than as a timing artefact.
     */
    private static final long REPAINT_MILLIS = 150;

    private static final int LOAD_TIMEOUT_SECONDS = 30;

    private final Stage stage;
    private final WebView view;
    private final int width;
    private final int height;

    private WebViewReference(Stage stage, WebView view, int width, int height) {
        this.stage = stage;
        this.view = view;
        this.width = width;
        this.height = height;
    }

    /** Opens the shared window. Must be called on the JavaFX Application Thread. */
    public static WebViewReference open(int width, int height) throws Exception {
        return JavaFxTestSupport.onFxThread(() -> {
            WebView view = new WebView();
            view.setPrefSize(width, height);
            view.setMinSize(width, height);
            view.setMaxSize(width, height);
            Stage stage = new Stage();
            stage.setScene(new Scene(new StackPane(view), width, height));
            stage.show();
            return new WebViewReference(stage, view, width, height);
        });
    }

    /**
     * Loads {@code document} and pauses its animation clock, so nothing moves until {@link #seek} says so. Returns
     * whether it loaded at all - a document the engine rejects is a result worth recording, not an exception.
     */
    public boolean load(Path document) throws Exception {
        CompletableFuture<Boolean> loaded = new CompletableFuture<>();
        JavaFxTestSupport.onFxThread(() -> {
            view.getEngine().getLoadWorker().stateProperty().addListener((observable, was, is) -> {
                if (is == Worker.State.SUCCEEDED) {
                    loaded.complete(true);
                } else if (is == Worker.State.FAILED || is == Worker.State.CANCELLED) {
                    loaded.complete(false);
                }
            });
            view.getEngine().load(document.toUri().toString());
            return null;
        });
        if (!loaded.get(LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            return false;
        }
        return script("document.documentElement.pauseAnimations(); true;") != null;
    }

    /** Moves the loaded document's SMIL clock to {@code time} and waits for the engine to repaint. */
    public void seek(Duration time) throws Exception {
        script("document.documentElement.setCurrentTime(" + time.toSeconds() + "); true;");
        Thread.sleep(REPAINT_MILLIS);
    }

    /** What the engine is showing right now, at the size this reference was opened at. */
    public WritableImage snapshot() throws Exception {
        return JavaFxTestSupport.onFxThread(() -> {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            return view.snapshot(params, new WritableImage(width, height));
        });
    }

    /**
     * Runs {@code javascript} against the loaded document, returning null if it throws - a document without an SVG
     * root, or one the engine could not parse, should be recorded as a failure rather than abort the whole run.
     */
    private Object script(String javascript) throws Exception {
        return JavaFxTestSupport.onFxThread(() -> {
            try {
                return view.getEngine().executeScript(javascript);
            } catch (RuntimeException e) {
                return null;
            }
        });
    }

    @Override
    public void close() throws Exception {
        JavaFxTestSupport.onFxThread(() -> {
            stage.close();
            return null;
        });
    }

}
