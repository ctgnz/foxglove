package nz.co.ctg.foxglove.conformance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * #196: renders every W3C SVG 1.1 test document through headless Chromium and writes the result as a fresh reference PNG, to eventually replace the suite's own stale (circa-2011)
 * reference images - see the epic (#200) for why: a 1px stroke centred on an integer coordinate antialiases across two 50%-opacity pixel columns in both this renderer's and live
 * Chrome's output, but the suite's own reference PNG snaps it to one crisp opaque column instead, costing ~2% "ink" on every single test regardless of what is actually under test,
 * with no way to reach a meaningful 100% against it as a result.
 * <p>
 * Like {@link W3cSvgConformanceCheck}/{@link W3cSvgAnimationCheck}, this class's name deliberately does not match Surefire's default discovery patterns, so it never runs in the
 * default build - only under {@code mvn -Pconformance test -Dtest=W3cSvgReferenceGenerator}, reusing that profile purely for its suite-tarball-fetch step (Surefire's
 * {@code -Dtest} overrides the profile's own {@code <includes>}, exactly as {@link W3cSvgAnimationCheck} already relies on).
 * <p>
 * <b>Deliberately out of scope here</b> (see the sibling issues under the #200 epic): where this runs in CI and how its output becomes a durable, fetchable artifact (#197); wiring
 * {@link W3cSvgConformanceCheck} to actually compare against this output (#198); and which test categories are even meaningful to compare this way at all - {@code animate-*}
 * documents are screenshotted at whatever moment Chromium happens to settle at after load, not seeked to a chosen moment the way {@link W3cSvgAnimationCheck} does, and
 * scripting/DOM-interactive tests are screenshotted the same as everything else. Deciding which of those are meaningful is #201's job, not this class's - this class's only job is
 * "screenshot every test document, one to one."
 */
@Tag("conformance")
public class W3cSvgReferenceGenerator {

    /**
     * The suite tarball's own pinned checksum, from {@code pom.xml}'s {@code conformance} profile - duplicated here (not derived at runtime) purely to record, alongside each
     * generated run, which exact suite contents it was generated against. Keep in sync with {@code pom.xml} by hand; a mismatch here would only affect this recorded provenance
     * note, not correctness.
     */
    private static final String SUITE_SHA256 = "b5f46cca1ad79b670f9179770b2366c57efd5c671d084144090feab4b7ff1030";

    /** Large enough that no test's real content clips before its true intrinsic size is read back and the viewport is resized to match - see {@link #intrinsicSize}. */
    private static final int PROBE_VIEWPORT = 2000;

    private static final Path OUTPUT_DIR = Path.of("target/w3c-svg-references-generated");

    @Test
    public void generateReferenceImages() throws Exception {
        Path suiteDir = Path.of(System.getProperty("conformance.suite.dir", "target/w3c-svg-testsuite"));
        List<Path> tests = listTests(suiteDir.resolve("svg"));
        Files.createDirectories(OUTPUT_DIR);

        Map<String, String> notes = new TreeMap<>();
        Map<String, String> sizes = new TreeMap<>();
        String chromiumVersion;
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium()
                .launch();
            chromiumVersion = browser.version();
            try (BrowserContext context = browser.newContext(new Browser.NewContextOptions().setViewportSize(PROBE_VIEWPORT, PROBE_VIEWPORT))) {
                Page page = context.newPage();
                for (Path svgFile : tests) {
                    String name = baseName(svgFile);
                    try {
                        sizes.put(name, generateOne(page, svgFile, name));
                    } catch (Exception e) {
                        notes.put(name, String.valueOf(e));
                    }
                }
            }
        }

        writeGenerationManifest(sizes, chromiumVersion);
        printSummary(tests.size(), sizes.size(), notes);
    }

    /**
     * Navigates to {@code svgFile} directly as the top-level document - not via the suite's own {@code harness/htmlObjectApproved/*.html} wrapper pages, whose hardcoded
     * {@code width="480" height="360"} object embedding is wrong for the suite's own non-480x360-canvas tests, and which add HTML/CSS embedding semantics foxglove's own rendering
     * (a raw SVG document) has no equivalent of.
     * <p>
     * The canonical size is read back from the browser's own resolution of the document (see {@link #intrinsicSize}) rather than computed by reusing this renderer's own
     * {@code SvgGraphic#getIntrinsicSize()} logic - deliberately, so a genuine sizing bug in this renderer can't launder itself by sizing both sides of a future comparison
     * identically and hiding the mismatch.
     *
     * @return {@code <width>x<height>}, for the generation manifest
     */
    private static String generateOne(Page page, Path svgFile, String name) throws IOException {
        page.navigate(svgFile.toUri()
            .toString());
        Dimensions size = intrinsicSize(page);
        int width = size.roundedWidth();
        int height = size.roundedHeight();
        page.setViewportSize(width, height);

        byte[] png = page.screenshot(new Page.ScreenshotOptions().setOmitBackground(true));
        Files.write(OUTPUT_DIR.resolve(name + ".png"), png);
        return width + "x" + height;
    }

    private record Dimensions(double width, double height) {

        int roundedWidth() {
            return (int) Math.round(width);
        }

        int roundedHeight() {
            return (int) Math.round(height);
        }
    }

    /**
     * The root {@code <svg>}'s own {@code viewBox} dimensions, in user units - <b>not</b> its {@code width}/{@code height}, confirmed empirically (against the real suite, not
     * assumed) to be the only container-independent size the suite's own documents carry: every one of the 525 declares {@code width="100%" height="100%"}, so
     * {@code SVGAnimatedLength.baseVal.value} resolves against whatever viewport happens to contain it - here, {@link #PROBE_VIEWPORT}, the same number regardless of the document
     * - rather than anything intrinsic to the document itself. {@code viewBox} carries no such ambiguity: it is a plain declared attribute, independent of any container.
     * <p>
     * Falls back to 300x150 - the standard SVG UA default, matching this renderer's own {@code SvgGraphic.DEFAULT_WIDTH}/{@code DEFAULT_HEIGHT} - for the 2 of 525 documents
     * ({@code struct-frag-01-t}, {@code struct-frag-04-t}) with no {@code viewBox} at all; unlike the 523 with one, a top-level document navigation gives no way to recover a
     * meaningful size for these from the browser itself (there is always a containing block - the browser's own window - so nothing ever triggers the UA-default fallback a
     * genuinely constrained replaced element, such as an {@code <object>} embed, would get instead).
     */
    @SuppressWarnings("unchecked")
    private static Dimensions intrinsicSize(Page page) {
        Map<String, Object> size = (Map<String, Object>) page.evaluate(
            "() => { const vb = document.documentElement.viewBox && document.documentElement.viewBox.baseVal;"
                                                                       + " return (vb && vb.width > 0 && vb.height > 0) ? { width: vb.width, height: vb.height } : { width: 300, height: 150 }; }");
        return new Dimensions(((Number) size.get("width")).doubleValue(), ((Number) size.get("height")).doubleValue());
    }

    /**
     * A plain, sorted {@code Properties}-style file - the same shape {@link ConformanceManifest} already uses - recording each test's canonical size (so #198's own comparison can
     * read it back without depending on the suite's own {@code png/} directory for dimensions at all) behind a header noting exactly what produced it, so a later run - or a later
     * reader wondering why a number differs - can tell what changed.
     */
    private static void writeGenerationManifest(Map<String, String> sizes, String chromiumVersion) {
        StringBuilder content = new StringBuilder();
        content.append("# W3C SVG 1.1 browser-generated conformance references (#196).\n")
            .append("# One <testName>=<width>x<height> line per test - its canonical size, read back from the\n")
            .append("# browser's own resolution of the document, not computed by this renderer.\n")
            .append("# Generated ")
            .append(Instant.now())
            .append(" with Playwright/Chromium ")
            .append(chromiumVersion)
            .append(", W3C SVG 1.1 suite sha256 ")
            .append(SUITE_SHA256)
            .append(".\n");
        for (Map.Entry<String, String> entry : sizes.entrySet()) {
            content.append(entry.getKey())
                .append('=')
                .append(entry.getValue())
                .append('\n');
        }
        try {
            Files.writeString(OUTPUT_DIR.resolve("generation-manifest.properties"), content.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void printSummary(int total, int generated, Map<String, String> notes) {
        System.out.printf("Generated %d / %d W3C SVG 1.1 reference images via headless Chromium.%n", generated, total);
        if (!notes.isEmpty()) {
            System.out.println(notes.size() + " test(s) failed to generate:");
            notes.forEach((name, note) -> System.out.println("  " + name + ": " + note));
        }
    }

    private static List<Path> listTests(Path svgDir) throws IOException {
        List<Path> tests = new ArrayList<>();
        try (var stream = Files.list(svgDir)) {
            stream.filter(p -> p.toString()
                .endsWith(".svg"))
                .sorted()
                .forEach(tests::add);
        }
        return tests;
    }

    private static String baseName(Path path) {
        String name = path.getFileName()
            .toString();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }

}
