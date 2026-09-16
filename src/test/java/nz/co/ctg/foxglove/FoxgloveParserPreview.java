package nz.co.ctg.foxglove;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * A manual visual tool: renders an SVG file next to a real browser engine's own rendering of the same document, so a discrepancy is visible side by side rather than needing to be
 * spotted from a static image alone.
 * <p>
 * <b>#209: the reference pane is a real, separate headed Chromium window via Playwright</b>, not an embedded {@code WebView} - replacing this tool's original {@code WebView} pane
 * for the same reason {@code PlaywrightAnimationReference} replaced {@code WebViewReference} for the automated animation conformance harness (#208/#217): WebView's bundled, legacy
 * WebKit fork has real gaps (no {@code <animateColor>} support at all) a genuinely modern, actively-maintained engine does not. The real cost, unlike the harness (which only ever
 * needs an off-screen snapshot): a headed Chromium window cannot be embedded inside this tool's own {@link Scene} the way {@code WebView} could, so the two panes are now two
 * separate OS windows rather than one - positioned and sized to sit side by side at launch (left half/right half of the primary screen) rather than truly embedded. They do not
 * stay aligned if either window is later moved or resized by hand; keeping them in sync live would need platform-specific window-tracking this tool does not attempt.
 * <p>
 * <b>Animation playback (#157).</b> A document with anything for {@link nz.co.ctg.foxglove.animate.SvgAnimationController} to manage gets Play/Pause and a seek slider; a document
 * with nothing to animate shows neither, unchanged from before this. Both panes are driven from a single master clock this class owns, rather than letting our own playback and the
 * reference engine's own SMIL clock run independently and drift apart: every tick calls both {@code animations.seek(elapsed)} and the reference page's own
 * {@code setCurrentTime(elapsed)}, so "Play" means "the master clock is advancing," not "two separate clocks were both told to start" - the same explicit-seek approach
 * {@code PlaywrightAnimationReference} uses for the automated harness.
 * <p>
 * <b>All Playwright calls run on their own single dedicated thread ({@link #playwrightExecutor}), never the JavaFX Application Thread.</b> A {@link Page} is not safe to call from
 * more than one thread, and unlike the harness (where blocking briefly is harmless - it is not driving a live UI), calling into a real, separate OS process synchronously from the
 * FX thread on every ~30Hz tick would risk visibly stuttering this tool's own rendering. Seeks are fire-and-forget submissions to that executor; the file-load navigation's
 * completion hands back to the FX thread via {@link Platform#runLater} once it is safe to touch playback controls again.
 * <p>
 * <b>A killed-rather-than-closed JVM still leaks the Chromium process tree</b> - {@link #stop()} closes it cleanly on an ordinary window-close/{@code Platform.exit()}, but nothing
 * short of that (a debugger's Stop button, a killed shell) gives it the chance to run. Check for stray {@code chrome.exe}/{@code java.exe} processes if a later build in this same
 * environment behaves oddly (a locked {@code target/} directory is the usual symptom).
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

    /** Owns every {@link Page}/{@link BrowserContext}/{@link Browser}/{@link Playwright} call - see the class javadoc on why. */
    private ExecutorService playwrightExecutor;
    private Playwright playwright;
    private Browser browser;
    private BrowserContext browserContext;
    private Page page;

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

        masterClock = new AnimationTimer() {
            @Override
            public void handle(long now) {
                tick(now);
            }
        };

        Rectangle2D screen = Screen.getPrimary()
            .getVisualBounds();
        double half = Math.floor(screen.getWidth() / 2);
        mainStage.setX(screen.getMinX());
        mainStage.setY(screen.getMinY());
        mainStage.setWidth(half);
        mainStage.setHeight(screen.getHeight());

        openReferenceWindow(screen, half);
        loadFile(defaultFile);

        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.setResizable(true);
        mainStage.setTitle("Icon Previewer");
        mainStage.show();
    }

    /**
     * Launches the reference browser positioned in the right half of the primary screen, sized to match - the closest a real, separate OS window can get to "docked" without
     * platform-specific window management. {@code --window-position}/{@code --window-size} are plain Chromium command-line switches, not part of Playwright's own Java API, which
     * has no first-class "move this browser window" call.
     */
    private void openReferenceWindow(Rectangle2D screen, double half) {
        playwrightExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "playwright-reference");
            thread.setDaemon(true);
            return thread;
        });
        int x = (int) (screen.getMinX() + half);
        int y = (int) screen.getMinY();
        int width = (int) half;
        int height = (int) screen.getHeight();
        playwrightExecutor.execute(() -> {
            playwright = Playwright.create();
            browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false)
                    .setArgs(List.of("--window-position=" + x + "," + y, "--window-size=" + width + "," + height)));
            browserContext = browser.newContext(new Browser.NewContextOptions().setViewportSize(width, height));
            page = browserContext.newPage();
        });
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

            // Runs on playwrightExecutor, never the FX thread - Playwright's own navigate() is a blocking call, and
            // this tool's UI must stay responsive while a file loads. seekTo(ZERO)/startPlayback() touch FX state
            // (currentAnimated, the slider, masterClock), so they hand back via Platform.runLater once navigation -
            // and the pauseAnimations() call that must follow it, not race it - has actually finished.
            String url = filePath.toUri()
                .toString();
            playwrightExecutor.execute(() -> {
                loadReference(url);
                Platform.runLater(() -> {
                    seekTo(Duration.ZERO);
                    if (hasAnimation()) {
                        startPlayback();
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Runs on {@link #playwrightExecutor} only - navigates, then pauses the SMIL clock so nothing moves until this class explicitly seeks it. */
    private void loadReference(String url) {
        try {
            page.navigate(url);
            page.evaluate("document.documentElement.pauseAnimations(); true;");
        } catch (RuntimeException e) {
            // a malformed or non-SVG file - the reference pane simply won't animate; our own render is unaffected
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
     * Throttled to {@link #TICK} rather than driven at the JavaFX pulse's own ~60Hz - {@code seekTo} submits a Playwright call on every tick, and doing that at full pulse rate is
     * needless overhead for a dev tool where {@link #TICK}'s own ~30Hz is already visually smooth. The delta advancing {@link #elapsed} is measured against the last
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

    /**
     * The single point that actually moves both panes - called by the master clock, by a manual seek, or on load. Must run on the FX thread (touches {@link #currentAnimated}'s own
     * {@code Animation}s and the slider); the reference pane's own seek is dispatched to {@link #playwrightExecutor} separately, fire-and-forget.
     */
    private void seekTo(Duration time) {
        elapsed = time;
        if (currentAnimated != null) {
            currentAnimated.animations()
                .seek(time);
        }
        seekReference(time);
        if (seekSlider.isVisible()) {
            updatingSliderProgrammatically = true;
            seekSlider.setValue(time.toMillis());
            updatingSliderProgrammatically = false;
        }
    }

    private void seekReference(Duration time) {
        if (playwrightExecutor == null) {
            return;
        }
        playwrightExecutor.execute(() -> {
            try {
                page.evaluate("document.documentElement.setCurrentTime(" + time.toSeconds() + "); true;");
            } catch (RuntimeException e) {
                // the reference pane simply won't animate for this document - our own render is unaffected
            }
        });
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

    /** Closes the reference browser cleanly - see the class javadoc on why this alone cannot cover every way the JVM might end. */
    @Override
    public void stop() throws Exception {
        masterClock.stop();
        if (playwrightExecutor == null) {
            return;
        }
        playwrightExecutor.execute(() -> {
            if (browserContext != null) {
                browserContext.close();
            }
            if (browser != null) {
                browser.close();
            }
            if (playwright != null) {
                playwright.close();
            }
        });
        playwrightExecutor.shutdown();
        playwrightExecutor.awaitTermination(5, TimeUnit.SECONDS);
    }

}
