package nz.co.ctg.foxglove.conformance;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * #44: renders every test document in the W3C SVG 1.1 (Second Edition) conformance suite and fuzzy-compares it against its reference PNG, checking the result against a checked-in
 * baseline manifest ({@link ConformanceManifest}) rather than asserting on raw pass/fail counts - "400 tests fail" says nothing about whether that is progress or a regression.
 * <p>
 * <b>The suite itself is deliberately not vendored</b> - it carries its own W3C copyright/licence terms and is fetched by the {@code conformance} Maven profile ({@code pom.xml})
 * into {@code target/w3c-svg-testsuite}, from {@code https://www.w3.org/Graphics/SVG/Test/20110816/archives/W3C_SVG_11_TestSuite.tar.gz} (SHA-256 pinned in the profile for
 * integrity). This class's own name deliberately does not match Surefire's default test-discovery patterns, so it never runs as part of the default build - only
 * {@code mvn -Pconformance test} includes it.
 * <p>
 * <b>Every test's own "furniture" is a problem verified empirically, not assumed</b>: each of the 525 test documents renders a {@code <text id="revision">} legend near the bottom
 * of the canvas, styled with an embedded SVG font ({@code font-face}/{@code font-face-uri}). Its actual rendered bounds (looked up via {@link RenderContext#withNodeRegistry}, the
 * same mechanism #43's Cucumber {@code RenderingSteps} already uses) are cropped out of both images before comparing - not a hardcoded pixel offset, since 6 of the 525 tests don't
 * use the suite's usual 480x360 canvas at all.
 * <p>
 * That crop exists because SVG fonts were unsupported, so the legend rendered in a fallback font and mismatched the reference for nearly every test regardless of what was actually
 * being verified. #61 has since implemented them and the legend now draws from the font's own glyph outlines, but <b>the crop deliberately stays</b>: removing it would move every
 * baseline in the suite at once, and is worth deciding on its own evidence rather than as a side effect of the font work.
 * <p>
 * <b>The entire {@code animate-} chapter (and any other genuinely time-based test) is a known, documented harness limitation, not a rendering defect</b>: this check renders one
 * static frame via {@code createGraphic}, with no concept of playing or seeking an animation at all - a reference image for a test whose own pass criteria describe ongoing motion
 * ("the four green rectangles each animate their height...") isn't meaningfully comparable against a single unanimated snapshot regardless of how correct the underlying SMIL
 * implementation is (which #30-#34, #88 already cover with ~200 dedicated unit tests of their own). Actually exercising these would need first understanding what moment in time
 * each reference image was captured at - not obviously encoded in the SVG itself - which is real, separate work this issue doesn't attempt.
 * <p>
 * Every run also writes {@link ConformanceReport}'s static HTML dashboard to {@code target/conformance-report/} - unconditionally, before the pass/fail check below, so #92's
 * GitHub Actions publish step has something to deploy regardless of whether this run's own manifest-diff assertion passes.
 */
@Tag("conformance")
public class W3cSvgConformanceCheck {

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @Test
    public void verifyAgainstW3cSvgTestSuite() throws Exception {
        Path suiteDir = Path.of(System.getProperty("conformance.suite.dir", "target/w3c-svg-testsuite"));
        Path svgDir = suiteDir.resolve("svg");
        Path pngDir = suiteDir.resolve("png");
        List<Path> tests = listTests(svgDir, pngDir);

        Path reportDir = Path.of("target/conformance-report");
        Map<String, ConformanceResult> results = new TreeMap<>();
        JavaFxTestSupport.onFxThread(() -> {
            for (Path svgFile : tests) {
                String name = baseName(svgFile);
                try {
                    results.put(name, runOne(svgFile, pngDir.resolve(name + ".png"), reportDir));
                } catch (Throwable e) {
                    // a renderer bug (including a StackOverflowError from a real cycle-guard gap) is itself a
                    // conformance failure worth recording, not something that should crash the whole 525-test run
                    results.put(name, new ConformanceResult(name, false, 0, 0, ConformanceTolerances.DEFAULT_MAX_DIFFERING_RATIO,
                                                            passCriteria(svgFile), String.valueOf(e)));
                }
            }
            return null;
        }, 5, TimeUnit.MINUTES);

        Map<String, Boolean> actual = new TreeMap<>();
        results.forEach((name, result) -> actual.put(name, result.passed()));

        printChapterSummary(actual);
        List<ConformanceResult> threw = results.values()
            .stream()
            .filter(r -> r.failureReason() != null)
            .toList();
        if (!threw.isEmpty()) {
            System.out.println(threw.size() + " test(s) threw during render/compare:");
            threw.forEach(r -> System.out.println("  " + r.name() + ": " + r.failureReason()));
        }
        // A thrown exception on an already-failing document never trips the manifest-diff regression check below -
        // it was already recorded FAIL, for whatever reason - so #155's own bug (a Basic/Tiny-profile document's DTD
        // fetched over the network, HTTP 429'd by w3.org under CI's repeated runs) could throw on every single run
        // without ever failing the build, visible only as a scary exception dump buried in the log. Checked directly
        // against these real documents rather than a synthetic reproduction: nothing short of the genuine internal
        // subset + external DTD combination they declare was found to trigger the same resolution path at all.
        Set<String> nonFullProfile = Set.of("coords-viewattr-01-b", "coords-viewattr-02-b", "coords-viewattr-04-f",
            "render-elems-03-t");
        List<String> unexpectedlyThrew = threw.stream()
            .map(ConformanceResult::name)
            .filter(nonFullProfile::contains)
            .toList();
        if (!unexpectedlyThrew.isEmpty()) {
            fail("Basic/Tiny-profile document(s) threw rather than resolving their DTD locally: " + unexpectedlyThrew);
        }

        // Written unconditionally, before the pass/fail branch below - #92's dashboard needs to keep showing a
        // regression, not go stale because the run that found it also failed its own assertion.
        ConformanceReport.write(results, reportDir);

        String mode = System.getProperty("conformance.mode", "verify");
        if ("record".equals(mode)) {
            ConformanceManifest.record(actual);
            System.out.println("Recorded " + actual.size() + " results to the baseline manifest.");
            return;
        }

        Map<String, Boolean> baseline = ConformanceManifest.load();
        ConformanceManifest.Diff diff = ConformanceManifest.diff(baseline, actual);
        if (!diff.newlyPassing()
            .isEmpty()) {
            System.out.println("Newly passing, not yet reflected in the baseline manifest: " + diff.newlyPassing());
        }
        if (diff.hasRegressions()) {
            fail("Conformance regressions against the baseline manifest: " + diff.regressions());
        }
    }

    private static List<Path> listTests(Path svgDir, Path pngDir) throws IOException {
        List<Path> tests = new ArrayList<>();
        try (var stream = Files.list(svgDir)) {
            stream.filter(p -> p.toString()
                .endsWith(".svg"))
                .filter(p -> Files.exists(pngDir.resolve(baseName(p) + ".png")))
                .sorted()
                .forEach(tests::add);
        }
        return tests;
    }

    /**
     * Renders one test, compares it, and writes the two images its report page shows - this renderer's own output and the diff. They are written here, as each test is rendered,
     * rather than handed back for {@link ConformanceReport} to write later: 525 pairs of 480x360 images held at once would cost the better part of a gigabyte, and there is no
     * reason to hold them.
     */
    private ConformanceResult runOne(Path svgFile, Path pngFile, Path reportDir) throws Exception {
        Image reference = new Image(pngFile.toUri()
            .toString());
        int width = (int) Math.round(reference.getWidth());
        int height = (int) Math.round(reference.getHeight());

        SvgGraphic svg;
        try (InputStream in = Files.newInputStream(svgFile)) {
            svg = new FoxgloveParser().parse(in);
        }
        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        // The base URI is the test file's own location, so a reference out of the document resolves the way it does
        // for any other consumer (#20's <image>, #61's <font-face-uri>). Without it every such reference silently
        // resolved to nothing, and the harness scored documents as if the resource simply did not exist.
        RenderContext context = RenderContext.root(svg.getElementIndex(), width, height)
            .withBaseUri(svgFile.toUri())
            .withNodeRegistry(registry);
        Node built = svg.createGraphic(context);

        WritableImage actualImage = snapshot(built, width, height);
        WritableImage referenceImage = new WritableImage(reference.getPixelReader(), width, height);

        int cropFromY = revisionCropFromY(svg, registry, height);
        double tolerance = ConformanceTolerances.forTest(baseName(svgFile));
        ConformanceComparator.Result result = ConformanceComparator.compare(actualImage, referenceImage, width, height, cropFromY,
            tolerance);

        String name = baseName(svgFile);
        WritableImage diffImage = ConformanceComparator.diff(actualImage, referenceImage, width, height, cropFromY);
        Path chapterDir = reportDir.resolve(chapterOf(name));
        Files.createDirectories(chapterDir);
        writePng(actualImage, width, height, chapterDir.resolve(name + ".png")
            .toString());
        writePng(diffImage, width, height, chapterDir.resolve(name + "-diff.png")
            .toString());

        if (name.equals(System.getProperty("conformance.debug"))) {
            System.out.printf("DEBUG %s: %dx%d, cropFromY=%d, differingRatio=%.4f, comparedPixels=%d, tolerance=%.4f%n",
                name, width, height, cropFromY, result.differingRatio(), result.comparedPixels(), tolerance);
            writePng(actualImage, width, height, "target/debug-actual.png");
            writePng(referenceImage, width, height, "target/debug-reference.png");
        }
        return new ConformanceResult(name, result.passed(), result.contentSimilarity(), result.contentPixels(), tolerance,
                                     passCriteria(svgFile), null);
    }

    /** Never fatal: a report page missing its prose is far better than a run aborted over unreadable furniture. */
    private static String passCriteria(Path svgFile) {
        try {
            return ConformanceTestDescription.passCriteria(svgFile);
        } catch (RuntimeException e) {
            return "";
        }
    }

    /**
     * Renders {@code built} into a {@code width}x{@code height} image of the document's <b>viewport</b>.
     * <p>
     * The explicit viewport is the whole point, and is not optional (#111): {@code Node.snapshot} with none set renders from the node's own {@code boundsInLocal} origin, not from
     * {@code (0, 0)}. Any document whose content extends left of or above the origin - {@code coords-coord-01-t} draws a {@code stroke-width="5"} line along {@code x=0}, so its
     * ink reaches {@code x=-2.5} - would otherwise have its <i>entire</i> rendering displaced by that overhang, content and test frame alike, and mismatch the reference for a
     * reason that has nothing to do with how well it renders. {@code SvgFilterRasterPipeline.rasterizeSource} sets one for the same reason.
     */
    static WritableImage snapshot(Node built, int width, int height) {
        return snapshot(built, width, height, Color.TRANSPARENT);
    }

    /**
     * As {@link #snapshot(Node, int, int)}, over a chosen background. Transparent is right against the suite's own reference PNGs, which are themselves transparent - but #112
     * compares against a browser engine, which paints an opaque page. Mismatch the two and every background pixel differs on alpha alone, which reads as a 99% difference between
     * images that are in fact nearly identical.
     */
    static WritableImage snapshot(Node built, int width, int height, Color fill) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(fill);
        params.setViewport(new Rectangle2D(0, 0, width, height));
        return built.snapshot(params, new WritableImage(width, height));
    }

    /**
     * Writes a {@code WritableImage} to a plain PNG file for {@code -Dconformance.debug=<testname>} troubleshooting - via {@code java.awt.image.BufferedImage}/{@code ImageIO}
     * directly rather than {@code
     * javafx.embed.swing.SwingFXUtils}, since {@code javafx-swing} is only a transitive dependency here (pulled in by {@code javafx-web}) and its module wasn't reliably resolvable
     * at runtime under Surefire - a plain pixel-by-pixel copy needs nothing beyond {@code java.desktop}, already implicit on any JVM running AWT/Swing.
     */
    static void writePng(WritableImage image, int width, int height, String path) throws IOException {
        var reader = image.getPixelReader();
        var buffered = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffered.setRGB(x, y, reader.getArgb(x, y));
            }
        }
        javax.imageio.ImageIO.write(buffered, "png", new java.io.File(path));
    }

    /**
     * The y-coordinate at which the "revision" legend furniture begins, so rows from there to the bottom of the canvas can be excluded from comparison - {@code height} (no
     * cropping) if the test has no such element, since not every test in the suite carries one.
     */
    static int revisionCropFromY(SvgGraphic svg, Map<ISvgElement, Node> registry, int height) {
        Optional<ISvgElement> revision = svg.getElementIndex()
            .resolve("#revision");
        if (revision.isEmpty()) {
            return height;
        }
        Node revisionNode = registry.get(revision.get());
        if (revisionNode == null) {
            return height;
        }
        Bounds bounds = revisionNode.getBoundsInParent();
        return Math.max(0, (int) Math.floor(bounds.getMinY()) - 4);
    }

    private static void printChapterSummary(Map<String, Boolean> actual) {
        Map<String, int[]> chapters = new TreeMap<>();
        for (Map.Entry<String, Boolean> entry : actual.entrySet()) {
            int[] counts = chapters.computeIfAbsent(chapterOf(entry.getKey()), c -> new int[2]);
            counts[1]++;
            if (entry.getValue()) {
                counts[0]++;
            }
        }
        int totalPass = 0;
        int total = 0;
        System.out.println("W3C SVG 1.1 conformance results by chapter:");
        for (Map.Entry<String, int[]> entry : chapters.entrySet()) {
            int pass = entry.getValue()[0];
            int all = entry.getValue()[1];
            totalPass += pass;
            total += all;
            System.out.printf("  %-12s %4d / %4d (%.1f%%)%n", entry.getKey(), pass, all, 100.0 * pass / all);
        }
        System.out.printf("TOTAL: %d / %d (%.1f%%)%n", totalPass, total, total == 0 ? 0.0 : 100.0 * totalPass / total);
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

}
