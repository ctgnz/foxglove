package nz.co.ctg.foxglove;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * A manual visual tool: renders an SVG file next to a real browser engine's own rendering of the same document, so a discrepancy is visible side by side rather than needing to be
 * spotted from a static image alone.
 * <p>
 * <b>Animation playback (#157).</b> A document with anything for {@link nz.co.ctg.foxglove.animate.SvgAnimationController} to manage gets Play/Pause and a seek slider; a document
 * with nothing to animate shows neither, unchanged from before this. Both panes are driven from a single master clock this class owns, rather than letting our own playback and the
 * WebView's own SMIL clock run independently and drift apart: every tick calls both {@code
 * animations.seek(elapsed)} and the WebView's own {@code setCurrentTime(elapsed)}, so "Play" means "the master clock is advancing," not "two separate clocks were both told to
 * start" - the same explicit-seek approach {@code
 * WebViewReference} already established for the animation conformance harness, chosen here for the same reason: exact, guaranteed sync beats two engines that happen to agree at
 * the start.
 * <p>
 * The WebView's own SMIL clock is paused once, right after each document loads ({@code pauseAnimations()}), and never unpaused - it only ever moves because this class explicitly
 * seeks it.
 */
public class FoxgloveParserPreview extends Application {

    /** How often the master clock advances while playing - responsive without being wasteful for a dev tool. */
    private static final Duration TICK = Duration.millis(1000.0 / 30);

    public static void main(String[] args) {
        Application.launch(FoxgloveParserPreview.class, args);
    }

    private Stage mainStage;
    private ScrollPane scrollPane;
    private Path defaultFile;
    private Path fileFolder;
    private FoxgloveParser parser;
    private WebView webView;

    private Button playPauseButton;
    private Slider seekSlider;

    private AnimatedGraphic currentAnimated;
    private Duration totalDuration = Duration.ZERO;
    private Duration elapsed = Duration.ZERO;
    private boolean playing;
    private long lastTickNanos;
    private AnimationTimer masterClock;
    /** Guards {@link #seekSlider}'s value listener against reacting to this class's own {@link #seekTo} calls. */
    private boolean updatingSliderProgrammatically;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.parser = new FoxgloveParser();
        this.mainStage = primaryStage;
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        defaultFile = Paths.get(SvgGraphic.class.getResource("/test.svg")
            .toURI());
        fileFolder = defaultFile.getParent();
        root.setTop(createToolbar());
        scrollPane = new ScrollPane();
        root.setCenter(scrollPane);
        webView = new WebView();
        webView.setMinSize(400, 800);
        root.setRight(webView);

        masterClock = new AnimationTimer() {
            @Override
            public void handle(long now) {
                tick(now);
            }
        };

        loadFile(defaultFile);

        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.setResizable(true);
        mainStage.setMaximized(true);
        mainStage.setTitle("Icon Previewer");
        mainStage.show();
    }

    private Node createToolbar() {
        ComboBox<Path> files = new ComboBox<>(findSvgFiles());
        files.getSelectionModel()
            .select(defaultFile);
        files.setOnAction(evt -> {
            Path filePath = files.getSelectionModel()
                .getSelectedItem();
            if (filePath != null) {
                loadFile(filePath);
            }
        });
        Button browse = new Button("Browse...");
        browse.setOnAction(evt -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(fileFolder.toFile());
            File selectedDir = chooser.showDialog(mainStage);
            if (selectedDir != null) {
                fileFolder = selectedDir.toPath();
                files.setItems(findSvgFiles());
            }
        });

        playPauseButton = new Button("Play");
        playPauseButton.setOnAction(evt -> {
            if (playing) {
                pausePlayback();
            } else {
                startPlayback();
            }
        });
        playPauseButton.setManaged(false);
        playPauseButton.setVisible(false);

        seekSlider = new Slider();
        seekSlider.setMinWidth(200);
        // Reacts to BOTH a drag and a plain click-to-jump on the track - Slider.isValueChanging() only reliably
        // reflects an ongoing drag, not a bare click, so this class's own updatingSliderProgrammatically flag (set
        // only around seekTo()'s own setValue call below) is what actually distinguishes "the user moved this" from
        // "this class just moved it while playing," uniformly across both interaction styles.
        seekSlider.valueProperty()
            .addListener((obs, was, value) -> {
                if (!updatingSliderProgrammatically) {
                    pausePlayback();
                    seekTo(Duration.millis(value.doubleValue()));
                }
            });
        seekSlider.setManaged(false);
        seekSlider.setVisible(false);

        return new ToolBar(browse, files, playPauseButton, seekSlider);
    }

    protected void loadFile(Path filePath) {
        try {
            pausePlayback();
            currentAnimated = createAnimatedGraphic(filePath);
            Pane region = new Pane(currentAnimated.node());
            region.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DOTTED, null, BorderStroke.THIN)));
            scrollPane.setContent(region);

            totalDuration = currentAnimated.animations()
                .getTotalDuration();
            elapsed = Duration.ZERO;
            configurePlaybackControls();

            // pauseAnimations() must wait for the new document to actually be loaded - calling it immediately would
            // race the WebView's own navigation and either no-op or apply to whatever it was previously showing.
            // One-shot and self-removing: switching files repeatedly must not accumulate a listener per load, each
            // one firing again (redundantly, though harmlessly - pauseAnimations()/seek(0) are idempotent) on every
            // later successful load for the rest of the session.
            Worker<Void> loadWorker = webView.getEngine()
                .getLoadWorker();
            loadWorker.stateProperty()
                .addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> obs, Worker.State was, Worker.State is) {
                        if (is != Worker.State.SUCCEEDED) {
                            return;
                        }
                        loadWorker.stateProperty()
                            .removeListener(this);
                        runScript("document.documentElement.pauseAnimations(); true;");
                        seekTo(Duration.ZERO);
                        if (hasAnimation()) {
                            startPlayback();
                        }
                    }
                });
            webView.getEngine()
                .load(filePath.toUri()
                    .toURL()
                    .toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasAnimation() {
        return currentAnimated != null && currentAnimated.animations()
            .size() > 0;
    }

    /**
     * Shows Play/Pause and the slider only when there is something to play, and only when it has a fixed length - an indefinitely-repeating document has no natural slider range to
     * show a position within (documented simplification, not an oversight: the master clock still advances it correctly via {@link #startPlayback}).
     */
    private void configurePlaybackControls() {
        boolean animated = hasAnimation();
        boolean finite = animated && !totalDuration.isIndefinite() && totalDuration.greaterThan(Duration.ZERO);

        playPauseButton.setManaged(animated);
        playPauseButton.setVisible(animated);
        playPauseButton.setText("Play");

        seekSlider.setManaged(finite);
        seekSlider.setVisible(finite);
        if (finite) {
            seekSlider.setMin(0);
            seekSlider.setMax(totalDuration.toMillis());
            updatingSliderProgrammatically = true;
            seekSlider.setValue(0);
            updatingSliderProgrammatically = false;
        }
    }

    private void startPlayback() {
        if (!hasAnimation() || playing) {
            return;
        }
        playing = true;
        playPauseButton.setText("Pause");
        lastTickNanos = 0;
        masterClock.start();
    }

    private void pausePlayback() {
        if (!playing) {
            return;
        }
        playing = false;
        playPauseButton.setText("Play");
        masterClock.stop();
    }

    /**
     * Throttled to {@link #TICK} rather than driven at the JavaFX pulse's own ~60Hz - {@code seekTo} executes a script against the WebView on every call, and doing that at full
     * pulse rate is needless overhead for a dev tool where {@link #TICK}'s own ~30Hz is already visually smooth. The delta advancing {@link #elapsed} is measured against the last
     * <i>acted-on</i> tick, not the last raw pulse - measuring against the pulse instead would only ever advance the clock by one pulse's worth of time per tick while discarding
     * the pulses skipped in between, running playback at roughly half real-time speed.
     */
    private void tick(long now) {
        if (lastTickNanos == 0) {
            lastTickNanos = now;
            return;
        }
        long elapsedNanos = now - lastTickNanos;
        if (elapsedNanos < TICK.toMillis() * 1_000_000) {
            return;
        }
        lastTickNanos = now;

        Duration next = elapsed.add(Duration.millis(elapsedNanos / 1_000_000.0));
        // A finite duration stops (rather than loops) at the end, so fill="freeze"/"remove" is shown exactly as
        // the document declares it, not hidden behind an artificial restart.
        if (!totalDuration.isIndefinite() && next.greaterThanOrEqualTo(totalDuration)) {
            seekTo(totalDuration);
            pausePlayback();
            return;
        }
        seekTo(next);
    }

    /** The single point that actually moves both panes - called by the master clock, by a manual seek, or on load. */
    private void seekTo(Duration time) {
        elapsed = time;
        if (currentAnimated != null) {
            currentAnimated.animations()
                .seek(time);
        }
        runScript("document.documentElement.setCurrentTime(" + time.toSeconds() + "); true;");
        if (seekSlider.isVisible()) {
            updatingSliderProgrammatically = true;
            seekSlider.setValue(time.toMillis());
            updatingSliderProgrammatically = false;
        }
    }

    /**
     * Runs {@code javascript} against the loaded document, swallowing any exception - a malformed or non-SVG file (a real possibility here, since {@code Browse...} lets this tool
     * point at any {@code .svg}-named file in a chosen folder) has no {@code document.documentElement.pauseAnimations()}/{@code setCurrentTime()} to call at all, and that must not
     * crash the JavaFX Application Thread. The same guard {@code WebViewReference} already applies to the identical calls in the animation conformance harness, for the identical
     * reason.
     */
    private void runScript(String javascript) {
        try {
            webView.getEngine()
                .executeScript(javascript);
        } catch (RuntimeException e) {
            // the WebView pane simply won't animate for this document - our own render is unaffected
        }
    }

    private ObservableList<Path> findSvgFiles() {
        try {
            return FXCollections.observableArrayList(Files.list(fileFolder)
                .filter(path -> path.toString()
                    .endsWith(".svg"))
                .collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return FXCollections.emptyObservableList();
        }
    }

    private AnimatedGraphic createAnimatedGraphic(Path filePath) throws Exception {
        SvgGraphic svgElement = parser.parse(Files.newInputStream(filePath));
        svgElement.setBaseUri(filePath.toUri());
        // wired up so <a> activation is actually exercisable by clicking in this preview, rather than only visible
        // via a unit test - printing here is a stand-in for whatever a real embedding application would do
        RenderContext context = RenderContext.root(svgElement.getElementIndex(), 0, 0)
            .withBaseUri(svgElement.getBaseUri())
            .withAnchorActivationHandler(anchor -> System.out.println(
                "<a> activated: xlink:href=" + anchor.getXlinkHref() + " target=" + anchor.getTarget()));
        return svgElement.createAnimatedGraphic(context);
    }

}
