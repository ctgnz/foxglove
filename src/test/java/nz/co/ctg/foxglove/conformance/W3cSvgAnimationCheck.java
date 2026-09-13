package nz.co.ctg.foxglove.conformance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.AnimatedGraphic;
import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;

import static org.junit.jupiter.api.Assertions.fail;

import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * #112: renders every {@code animate-} document in the W3C suite <b>at chosen moments</b> and compares it against a
 * live browser engine seeked to the same moment.
 * <p>
 * The static check next door cannot cover this chapter at all, and says so in its own javadoc: it renders one frame
 * and compares it to a reference PNG captured at some point in the animation that nothing in the document records,
 * so a correct SMIL implementation and a broken one are equally likely to mismatch. That left ~200 unit tests
 * (#30-#34, #88) covering SMIL with no end-to-end signal whatsoever. Seeking both sides removes the problem
 * entirely, because "which frame?" stops being a guess.
 * <p>
 * Like {@link W3cSvgConformanceCheck}, this class's name deliberately does not match Surefire's default discovery
 * patterns, so it never runs in the default build - only under {@code mvn -Pconformance test}. That matters more
 * here than there: {@link WebViewReference} needs a <b>shown</b> window to render at all, which CI supplies through
 * {@code xvfb-run} and a developer's default build should not be opening.
 * <p>
 * <b>Read the numbers as a relative signal, not an absolute one.</b> The engine draws text with real fonts and this
 * renderer with a fallback, so every label in every document differs for reasons that have nothing to do with
 * animation - the same furniture problem the static check already documents, and the reason the {@code revision}
 * legend is cropped here too. What that noise cannot do is <i>vary with time</i>: a genuine animation error shows up
 * as divergence that changes across the sampled moments, which is the thing worth watching.
 */
public class W3cSvgAnimationCheck {

    /** Where in each animation to look. Endpoints included deliberately: {@code fill="freeze"} lives at the end. */
    private static final double[] SAMPLE_FRACTIONS = {0.0, 0.25, 0.5, 0.75, 1.0};

    /**
     * The window to sample when a document animates forever, which is common in this chapter -
     * {@code getTotalDuration()} is {@code INDEFINITE} if anything in it repeats indefinitely, and there is no
     * meaningful "100%" of that. Four seconds covers the usual few-second cycle without making the run crawl.
     */
    private static final Duration INDEFINITE_WINDOW = Duration.seconds(4);

    private static final Path MANIFEST = Path.of("src/test/resources/conformance/animation-manifest.properties");

    /**
     * What the browser paints behind the page. Both sides must agree on it: compare a transparent capture against an
     * opaque one and every background pixel differs on alpha alone, which reported 99% difference between images
     * that were nearly identical.
     */
    private static final Color PAGE_BACKGROUND = Color.WHITE;

    /** Room for the suite's usual 480x360 canvas plus the engine's own margins. */
    private static final int WIDTH = 480;
    private static final int HEIGHT = 360;

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @Test
    public void verifyAnimationAgainstABrowserEngine() throws Exception {
        Path svgDir = Path.of(System.getProperty("conformance.suite.dir", "target/w3c-svg-testsuite")).resolve("svg");
        List<Path> documents = listAnimationTests(svgDir);

        Map<String, Boolean> actual = new TreeMap<>();
        Map<String, String> notes = new TreeMap<>();
        WebViewReference reference = WebViewReference.open(WIDTH, HEIGHT);
        try {
            for (Path document : documents) {
                String name = baseName(document);
                try {
                    actual.put(name, runOne(document, name, reference, notes));
                } catch (Throwable e) {
                    // one document's failure must not take down the run, the same reasoning the static check needed
                    actual.put(name, false);
                    notes.put(name, String.valueOf(e));
                }
            }
        } finally {
            reference.close();
        }

        printSummary(actual, notes);

        if ("record".equals(System.getProperty("animation.mode", "verify"))) {
            ConformanceManifest.record(MANIFEST, actual,
                "# W3C SVG 1.1 animation baseline (#112) - one PASS/FAIL line per animate- test name.\n"
                    + "# Each is this renderer compared against a seeked WebView, not against the suite's own PNGs.\n"
                    + "# Regenerate deliberately with -Danimation.mode=record after reviewing what changed.\n");
            System.out.println("Recorded " + actual.size() + " animation results to the baseline manifest.");
            return;
        }

        ConformanceManifest.Diff diff = ConformanceManifest.diff(ConformanceManifest.load(MANIFEST), actual);
        if (!diff.newlyPassing().isEmpty()) {
            System.out.println("Newly passing, not yet reflected in the animation baseline: " + diff.newlyPassing());
        }
        if (diff.hasRegressions()) {
            fail("Animation regressions against the baseline manifest: " + diff.regressions());
        }
    }

    /**
     * Compares one document at every sampled moment, passing only if all of them do. The worst moment is the one
     * that matters: an animation that is right at rest and wrong in flight is still wrong.
     */
    private boolean runOne(Path document, String name, WebViewReference reference, Map<String, String> notes) throws Exception {
        SvgGraphic svg;
        try (InputStream in = Files.newInputStream(document)) {
            svg = new FoxgloveParser().parse(in);
        }

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        RenderContext context = RenderContext.root(svg.getElementIndex(), WIDTH, HEIGHT).withNodeRegistry(registry);
        AnimatedGraphic animated = JavaFxTestSupport.onFxThread(() -> svg.createAnimatedGraphic(context));
        int cropFromY = W3cSvgConformanceCheck.revisionCropFromY(svg, registry, HEIGHT);

        if (!reference.load(document)) {
            notes.put(name, "the browser engine could not load it");
            return false;
        }

        double tolerance = ConformanceTolerances.forTest(name);
        String debugTarget = System.getProperty("animation.debug");
        double worst = 0;
        boolean passed = true;

        for (Duration time : sampleTimes(animated)) {
            JavaFxTestSupport.onFxThread(() -> {
                animated.animations().seek(time);
                return null;
            });
            reference.seek(time);

            WritableImage ours = JavaFxTestSupport.onFxThread(
                () -> W3cSvgConformanceCheck.snapshot(animated.node(), WIDTH, HEIGHT, PAGE_BACKGROUND));
            WritableImage theirs = reference.snapshot();

            ConformanceComparator.Result result = ConformanceComparator.compare(ours, theirs, WIDTH, HEIGHT, cropFromY, tolerance);
            worst = Math.max(worst, result.differingRatio());
            passed &= result.passed();

            if (name.equals(debugTarget)) {
                String suffix = String.format("%.2fs", time.toSeconds());
                System.out.printf("DEBUG %s at %s: differingRatio=%.4f, comparedPixels=%d, tolerance=%.4f%n",
                    name, suffix, result.differingRatio(), result.comparedPixels(), tolerance);
                W3cSvgConformanceCheck.writePng(ours, WIDTH, HEIGHT, "target/debug-animation-" + suffix + "-actual.png");
                W3cSvgConformanceCheck.writePng(theirs, WIDTH, HEIGHT, "target/debug-animation-" + suffix + "-reference.png");
            }
        }

        notes.put(name, String.format("worst differing ratio %.4f across %d moments, %d animation(s) built",
            worst, SAMPLE_FRACTIONS.length, animated.animations().size()));
        return passed;
    }

    /**
     * The moments to compare, spanning {@link #INDEFINITE_WINDOW} whenever this renderer's own duration is no use:
     * indefinite (nothing has a meaningful "100%" of forever) <b>or zero</b>.
     * <p>
     * Zero matters more than it looks. It means this renderer built no animations for the document at all - and
     * deriving the window from our own duration would then sample {@code t=0} five times over, comparing a
     * stationary rendering against a reference that is also still at its start. The document would score well for
     * precisely the reason it should score badly. Sampling real time regardless is what makes "we animate nothing
     * here" visible instead of invisible.
     */
    static List<Duration> sampleTimes(AnimatedGraphic animated) {
        Duration total = animated.animations().getTotalDuration();
        if (total == null || total.isIndefinite() || total.lessThanOrEqualTo(Duration.ZERO)) {
            total = INDEFINITE_WINDOW;
        }
        List<Duration> times = new ArrayList<>();
        for (double fraction : SAMPLE_FRACTIONS) {
            times.add(total.multiply(fraction));
        }
        return times;
    }

    private static List<Path> listAnimationTests(Path svgDir) throws IOException {
        List<Path> tests = new ArrayList<>();
        try (var stream = Files.list(svgDir)) {
            stream.filter(p -> p.toString().endsWith(".svg"))
                .filter(p -> baseName(p).startsWith("animate-"))
                .sorted()
                .forEach(tests::add);
        }
        return tests;
    }

    private static void printSummary(Map<String, Boolean> actual, Map<String, String> notes) {
        long passed = actual.values().stream().filter(Boolean::booleanValue).count();
        System.out.printf("W3C SVG 1.1 animation results (seeked, against a browser engine): %d / %d (%.1f%%)%n",
            passed, actual.size(), actual.isEmpty() ? 0.0 : 100.0 * passed / actual.size());
        notes.forEach((name, note) -> System.out.println("  " + name + ": " + note));
    }

    private static String baseName(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }

}
