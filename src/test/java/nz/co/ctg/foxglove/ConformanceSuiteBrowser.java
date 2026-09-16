package nz.co.ctg.foxglove;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * A manual visual tool: renders a W3C SVG 1.1 conformance test next to a real browser engine's own rendering of the same document, so a discrepancy is visible side by side rather
 * than needing to be spotted from a static image alone - a desktop counterpart to the published conformance dashboard (#92/#200), browsable test by test rather than one static
 * report.
 * <p>
 * <b>#224: fetches the W3C suite itself, into a delete-on-exit temp directory, rather than browsing an arbitrary folder.</b> This tool originally existed to check that an
 * arbitrary SVG file renders at all (hence a "Browse..." folder picker) - once #209 gave it a real live comparison against a modern reference engine, the more useful role is a
 * fixed, complete corpus browsable by chapter, not an open-ended file picker. Fetches the same tarball/URL/checksum {@code pom.xml}'s {@code conformance} Maven profile uses, but
 * at this tool's own launch time (it runs directly via {@code java}/an IDE, outside that profile, so it cannot assume {@code target/w3c-svg-testsuite} already exists) - never
 * vendored, same reasoning as the Maven-side fetch (the suite carries its own W3C copyright).
 * <p>
 * <b>#209: the reference pane is a real, separate headed Chromium window via Playwright</b>, not an embedded {@code WebView} - replacing this tool's original {@code WebView} pane
 * for the same reason {@code PlaywrightAnimationReference} replaced {@code WebViewReference} for the automated animation conformance harness (#208/#217): WebView's bundled, legacy
 * WebKit fork has real gaps (no {@code <animateColor>} support at all) a genuinely modern, actively-maintained engine does not. A headed Chromium window cannot be embedded inside
 * this tool's own {@link Scene} the way {@code WebView} could, so the two panes are two separate OS windows, positioned and sized to sit close together at launch rather than truly
 * embedded - the suite's own canvases are consistently small (mostly 480x360), so both windows stay modestly sized rather than each claiming half the screen. They do not stay
 * aligned if either window is later moved or resized by hand.
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
 * completion hands back to the FX thread via {@link Platform#runLater} once it is safe to touch playback controls again. The suite fetch runs on its own separate one-shot thread,
 * for the same reason - a multi-second network fetch has no business blocking either the FX thread or the Playwright executor.
 * <p>
 * <b>A killed-rather-than-closed JVM still leaks the Chromium process tree, and leaves the fetched suite on disk</b> - {@link #stop()} closes the browser and deletes the temp
 * directory cleanly on an ordinary window-close/{@code Platform.exit()}, but nothing short of that (a debugger's Stop button, a killed shell) gives it the chance to run. Check for
 * stray {@code chrome.exe}/{@code java.exe} processes, and a stray {@code conformance-suite-browser-*} temp directory, if a later build in this same environment behaves oddly.
 */
public class ConformanceSuiteBrowser extends Application {

    /** How often the master clock advances while playing - responsive without being wasteful for a dev tool. */
    private static final Duration TICK = Duration.millis(1000.0 / 30);

    /**
     * Same tarball/checksum {@code pom.xml}'s {@code conformance} profile pins - kept in sync by hand, the same convention {@link nz.co.ctg.foxglove.conformance} classes follow.
     */
    private static final String SUITE_URL = "https://www.w3.org/Graphics/SVG/Test/20110816/archives/W3C_SVG_11_TestSuite.tar.gz";
    private static final String SUITE_SHA256 = "b5f46cca1ad79b670f9179770b2366c57efd5c671d084144090feab4b7ff1030";

    public static void main(String[] args) {
        Application.launch(ConformanceSuiteBrowser.class, args);
    }

    private Stage mainStage;
    private ScrollPane scrollPane;
    private TreeView<String> tree;
    private FoxgloveParser parser;

    /** Deleted in {@link #stop()} - see the class javadoc on why that alone cannot cover every way the JVM might end. */
    private Path suiteDir;
    private final Map<String, Path> testsByName = new TreeMap<>();

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

        root.setTop(createToolbar());
        tree = new TreeView<>(new TreeItem<>("Fetching W3C SVG 1.1 test suite..."));
        tree.setPrefWidth(220);
        tree.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, was, item) -> onTestSelected(item));
        root.setLeft(tree);
        scrollPane = new ScrollPane();
        root.setCenter(scrollPane);

        masterClock = new AnimationTimer() {
            @Override
            public void handle(long now) {
                tick(now);
            }
        };

        // Modest, close-together sizing (#224) - the suite's own canvases are consistently small, unlike #209's
        // half-screen split sized for arbitrary, potentially much larger, real-world documents. Wide enough that
        // the tree (220px) plus a full 480px-wide canvas plus padding/scrollbar slack fit without the preview
        // itself needing to scroll horizontally for a typical test.
        Rectangle2D screen = Screen.getPrimary()
            .getVisualBounds();
        int mainWidth = 780;
        int mainHeight = 580;
        int referenceWidth = 520;
        int referenceHeight = 460;
        mainStage.setX(screen.getMinX());
        mainStage.setY(screen.getMinY());
        mainStage.setWidth(mainWidth);
        mainStage.setHeight(mainHeight);

        openReferenceWindow((int) (screen.getMinX() + mainWidth), (int) screen.getMinY(), referenceWidth, referenceHeight);
        fetchSuiteInBackground();

        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.setResizable(true);
        mainStage.setTitle("Conformance Suite Browser");
        mainStage.show();
    }

    /** Launches the reference browser positioned immediately to the right of the main window, at {@code (x, y)}, sized {@code width}x{@code height}. */
    private void openReferenceWindow(int x, int y, int width, int height) {
        playwrightExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "playwright-reference");
            thread.setDaemon(true);
            return thread;
        });
        playwrightExecutor.execute(() -> {
            playwright = Playwright.create();
            browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false)
                    .setArgs(List.of("--window-position=" + x + "," + y, "--window-size=" + width + "," + height)));
            browserContext = browser.newContext(new Browser.NewContextOptions().setViewportSize(width, height));
            page = browserContext.newPage();
        });
    }

    /**
     * Runs entirely off the FX thread - a multi-second network fetch has no business blocking it. Populates {@link #tree} and loads the first test once done.
     * <p>
     * Catches {@link Throwable}, not just {@link Exception} - confirmed the hard way why that distinction matters here specifically: a dependency version conflict
     * ({@code commons-compress} needing a newer {@code commons-lang3} than another dependency had already pulled in transitively) surfaced as a {@link NoClassDefFoundError}, which
     * a plain {@code catch (Exception e)} does not catch at all. An uncaught {@link Error} on a plain background {@link Thread} does not visibly crash the application - it just
     * silently ends that thread, which looked indistinguishable from a hung network fetch until diagnosed directly.
     */
    private void fetchSuiteInBackground() {
        Thread thread = new Thread(() -> {
            try {
                Path svgDir = fetchAndExtractSuite();
                Map<String, Path> tests = scanTests(svgDir);
                Platform.runLater(() -> {
                    testsByName.putAll(tests);
                    populateTree(tests);
                });
            } catch (Throwable e) {
                e.printStackTrace();
                Platform.runLater(() -> tree.setRoot(new TreeItem<>("Failed to fetch the test suite - see the console.")));
            }
        }, "suite-fetch");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Downloads the suite tarball to a plain temp file (deleted immediately after, regardless of outcome), verifies it against {@link #SUITE_SHA256}, then extracts only the
     * {@code svg/} entries (the only ones this tool needs - not the reference {@code png/} images, which this tool has its own live reference for, nor the {@code harness/} HTML)
     * into a fresh {@link #suiteDir}, deleted in {@link #stop()}.
     */
    private Path fetchAndExtractSuite() throws IOException, InterruptedException, NoSuchAlgorithmException {
        Path tarball = Files.createTempFile("conformance-suite-browser-", ".tar.gz");
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(SUITE_URL))
                .build();
            client.send(request, HttpResponse.BodyHandlers.ofFile(tarball));

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String actualSha256 = HexFormat.of()
                .formatHex(digest.digest(Files.readAllBytes(tarball)));
            if (!SUITE_SHA256.equals(actualSha256)) {
                throw new IOException("W3C suite checksum mismatch - expected " + SUITE_SHA256 + " but got " + actualSha256);
            }

            suiteDir = Files.createTempDirectory("conformance-suite-browser-");
            extractSvgEntries(tarball, suiteDir);
            return suiteDir.resolve("svg");
        } finally {
            Files.deleteIfExists(tarball);
        }
    }

    private static void extractSvgEntries(Path tarball, Path outputDir) throws IOException {
        try (InputStream fileIn = Files.newInputStream(tarball);
                        GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(fileIn);
                        TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextEntry()) != null) {
                if (!entry.isFile() || !entry.getName()
                    .startsWith("svg/")) {
                    continue;
                }
                Path target = outputDir.resolve(entry.getName())
                    .normalize();
                if (!target.startsWith(outputDir)) {
                    throw new IOException("Suspicious tar entry outside the output directory: " + entry.getName());
                }
                Files.createDirectories(target.getParent());
                Files.copy(tarIn, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static Map<String, Path> scanTests(Path svgDir) throws IOException {
        Map<String, Path> tests = new TreeMap<>();
        try (var stream = Files.list(svgDir)) {
            stream.filter(path -> path.toString()
                .endsWith(".svg"))
                .forEach(path -> tests.put(baseName(path), path));
        }
        return tests;
    }

    /**
     * Builds the tree grouped by chapter (the same {@code <chapter>-...} prefix convention {@code W3cSvgConformanceCheck.chapterOf} uses) - one branch per chapter, one leaf per
     * test. {@code tests} is already sorted by name ({@link TreeMap}), so both chapters and each chapter's own tests come out sorted automatically: every test sharing a chapter
     * prefix sorts contiguously next to each other, and chapters themselves sort in the same pass, with no separate grouping/sorting step needed.
     */
    private void populateTree(Map<String, Path> tests) {
        TreeItem<String> root = new TreeItem<>("W3C SVG 1.1 Test Suite");
        Map<String, TreeItem<String>> chapters = new TreeMap<>();
        for (String name : tests.keySet()) {
            TreeItem<String> chapterItem = chapters.computeIfAbsent(chapterOf(name), chapter -> {
                TreeItem<String> item = new TreeItem<>(chapter);
                root.getChildren()
                    .add(item);
                return item;
            });
            chapterItem.getChildren()
                .add(new TreeItem<>(name));
        }
        tree.setRoot(root);
        tree.setShowRoot(false);
        if (!root.getChildren()
            .isEmpty()) {
            TreeItem<String> firstTest = root.getChildren()
                .get(0)
                .getChildren()
                .get(0);
            tree.getSelectionModel()
                .select(firstTest);
        }
    }

    private void onTestSelected(TreeItem<String> item) {
        if (item == null || !item.isLeaf()) {
            return;
        }
        Path file = testsByName.get(item.getValue());
        if (file != null) {
            loadFile(file);
        }
    }

    private static String chapterOf(String testName) {
        int dash = testName.indexOf('-');
        return dash < 0 ? testName : testName.substring(0, dash);
    }

    private static String baseName(Path path) {
        String name = path.getFileName()
            .toString();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }

    private Node createToolbar() {
        playPauseButton = new Button("Play");
        playPauseButton.setOnAction(evt -> {
            if (playing) {
                pausePlayback();
            } else {
                startPlayback();
            }
        });
        // Always shown, not shown-only-when-animated (#224 tidy-up) - a toolbar that appears/disappears per
        // selection reads as a layout glitch when clicking through the tree; disabled communicates "nothing to
        // play here" just as clearly without the whole toolbar shifting.
        playPauseButton.setDisable(true);

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
        seekSlider.setDisable(true);

        return new ToolBar(playPauseButton, seekSlider);
    }

    /**
     * Catches {@link Throwable}, not just {@link Exception} - confirmed the hard way this matters here specifically: a stale build artifact from earlier in this project's own
     * history surfaced as a {@link java.lang.Error} ("Unresolved compilation problems") from deep inside JAXB unmarshalling for one real test document, which a plain
     * {@code catch (Exception e)} let straight through, leaving both panes silently blank with no diagnostic at all - see {@link #fetchSuiteInBackground} for the identical lesson
     * learned first, one method up.
     */
    protected void loadFile(Path filePath) {
        try {
            pausePlayback();
            SvgGraphic svgElement = parser.parse(Files.newInputStream(filePath));
            svgElement.setBaseUri(filePath.toUri());
            currentAnimated = createAnimatedGraphic(svgElement);
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
            Dimension2D intrinsicSize = svgElement.getIntrinsicSize();
            playwrightExecutor.execute(() -> {
                loadReference(url, intrinsicSize);
                Platform.runLater(() -> {
                    seekTo(Duration.ZERO);
                    if (hasAnimation()) {
                        startPlayback();
                    }
                });
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs on {@link #playwrightExecutor} only - navigates, then pauses the SMIL clock so nothing moves until this class explicitly seeks it.
     * <p>
     * <b>Forces the loaded document's own root to {@code intrinsicSize}, in pixels.</b> Confirmed directly, not assumed: without this, a document whose root declares
     * {@code width="100%" height="100%"} (the common case across the suite) resolves that percentage against the browser's own viewport, then its default
     * {@code preserveAspectRatio="xMidYMid meet"} scales and centres the actual content within that box - "fills the width, centred vertically," not remotely what this renderer's
     * own {@code RenderContext.root(index, 0, 0)} shows (the document's own intrinsic size, unscaled, anchored at the pane's origin). Overriding the root's inline
     * {@code style.width}/{@code style.height} to the exact pixel size {@link SvgGraphic#getIntrinsicSize()} itself resolves - the same authoritative source
     * {@link #createAnimatedGraphic} already renders against - removes the size mismatch that caused the scaling/centring in the first place, so both panes end up showing the same
     * document at the same pixel size, anchored the same way, which is the entire point of a side-by-side comparison.
     */
    private void loadReference(String url, Dimension2D intrinsicSize) {
        try {
            page.navigate(url);
            page.evaluate(String.format(Locale.ROOT,
                "document.documentElement.style.width='%.3fpx'; document.documentElement.style.height='%.3fpx'; "
                                                     + "document.documentElement.pauseAnimations(); true;",
                intrinsicSize.getWidth(), intrinsicSize.getHeight()));
        } catch (RuntimeException e) {
            // a malformed or non-SVG file - the reference pane simply won't animate; our own render is unaffected
        }
    }

    private boolean hasAnimation() {
        return currentAnimated != null && currentAnimated.animations()
            .size() > 0;
    }

    /**
     * Both controls stay permanently shown (#224) - only their enabled state reflects whether there is anything to play: Play/Pause disabled when the document has no animation at
     * all, the slider disabled whenever Play/Pause is too <i>or</i> whenever the animation is indefinite - it has no fixed length, so no natural slider range to show a position
     * within (documented simplification, not an oversight: the master clock still advances it correctly via {@link #startPlayback}).
     */
    private void configurePlaybackControls() {
        boolean animated = hasAnimation();
        boolean finite = animated && !totalDuration.isIndefinite() && totalDuration.greaterThan(Duration.ZERO);

        playPauseButton.setDisable(!animated);
        playPauseButton.setText("Play");

        seekSlider.setDisable(!finite);
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
        if (!seekSlider.isDisable()) {
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

    private AnimatedGraphic createAnimatedGraphic(SvgGraphic svgElement) {
        // wired up so <a> activation is actually exercisable by clicking in this preview, rather than only visible
        // via a unit test - printing here is a stand-in for whatever a real embedding application would do
        RenderContext context = RenderContext.root(svgElement.getElementIndex(), 0, 0)
            .withBaseUri(svgElement.getBaseUri())
            .withAnchorActivationHandler(anchor -> System.out.println(
                "<a> activated: xlink:href=" + anchor.getXlinkHref() + " target=" + anchor.getTarget()));
        return svgElement.createAnimatedGraphic(context);
    }

    /** Closes the reference browser and deletes the fetched suite cleanly - see the class javadoc on why this alone cannot cover every way the JVM might end. */
    @Override
    public void stop() throws Exception {
        masterClock.stop();
        if (playwrightExecutor != null) {
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
        deleteSuiteDir();
    }

    private void deleteSuiteDir() {
        if (suiteDir == null || !Files.exists(suiteDir)) {
            return;
        }
        try (var paths = Files.walk(suiteDir)) {
            paths.sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // best-effort - a leaked temp file/directory under the OS temp folder is not worth failing
                        // shutdown over
                    }
                });
        } catch (IOException e) {
            // as above
        }
    }

}
