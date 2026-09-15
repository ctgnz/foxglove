package nz.co.ctg.foxglove.conformance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Generates the static site #92 publishes to GitHub Pages on every push to master - since #110 a three-level drill-down rather than a single page: overall, then per chapter, then
 * per test.
 * <p>
 * The per-test level exists because a pass rate alone hides both of the things a reader actually wants. It cannot distinguish a test that renders almost perfectly from one that
 * renders nothing, and it cannot show progress at all until a test crosses the threshold - roughly a fifth of the suite currently sits within 2-5% of passing. So every level
 * reports how much of the <i>ink</i> matched alongside the pass count (see {@link ConformanceComparator#compare} for why it is measured that way and not over the whole canvas),
 * and each test page shows the reference, this renderer's own output and a diff, next to the test's own pass criteria.
 * <p>
 * <b>Reference images are linked from W3C, never republished.</b> Every suite document carries {@code Copyright
 * 2009 World Wide Web Consortium ... All Rights Reserved}, which is why the suite is fetched at build time rather than vendored (see {@link W3cSvgConformanceCheck}); copying 525
 * reference PNGs onto a public site would go further than that. W3C serves them itself at stable URLs, so the pages link there and publish only this renderer's own output and the
 * diff. Do not "simplify" this by copying the files in.
 * <p>
 * <b>#199/#200: this display-only W3C image is no longer what the pass/fail verdict is actually computed against</b> - since #198, that comparison runs against a fresh,
 * independently-rendered reference set (headless Chromium, #196), which for the exact same copyright reason above is never published here either (see #197's own reasoning for
 * choosing a GitHub Actions artifact over a public Release asset). The W3C image stays purely for visual context, captioned as such, alongside a note on the index page - do not
 * remove either without also removing this comment, since without them a reader has no way to know the number above a mismatched-looking W3C panel is correct.
 * <p>
 * Written unconditionally on every run, before the harness's own pass/fail assertion, so a regression keeps being visible rather than leaving the published site stale.
 */
public final class ConformanceReport {

    /** Where W3C serves the suite itself - see the class javadoc on why references are linked rather than copied. */
    private static final String W3C_SUITE_BASE = "https://www.w3.org/Graphics/SVG/Test/20110816";

    private static final String STYLE = """
                    body { font-family: system-ui, sans-serif; margin: 2rem; color: #222; }
                    h1 { font-size: 1.4rem; } h2 { font-size: 1.1rem; margin-top: 2rem; }
                    .overall { font-size: 2rem; font-weight: 600; margin: 1rem 0 0.25rem; }
                    .sub { color: #555; margin-bottom: 1rem; }
                    table { border-collapse: collapse; width: 100%; max-width: 760px; }
                    th, td { text-align: left; padding: 0.4rem 0.8rem; border-bottom: 1px solid #ddd; }
                    th { background: #f4f4f4; }
                    td.num, th.num { text-align: right; font-variant-numeric: tabular-nums; }
                    nav { margin-bottom: 1rem; font-size: 0.9rem; }
                    a { color: #1a5fb4; }
                    .pass { color: #1a7f37; font-weight: 600; }
                    .fail { color: #b42318; }
                    .note { background: #eff3fa; border-left: 3px solid #1a5fb4; padding: 0.6rem 0.9rem; margin: 1rem 0;
                            max-width: 760px; font-size: 0.9rem; }
                    .panels { display: flex; flex-wrap: wrap; gap: 1rem; margin: 1rem 0; }
                    .panel { flex: 1 1 320px; max-width: 480px; }
                    .panel h3 { font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.04em; color: #555; margin: 0 0 0.4rem; }
                    .panel img { width: 100%; border: 1px solid #ccc; background: #fff; }
                    .criteria { max-width: 760px; background: #fafafa; border: 1px solid #e5e5e5; padding: 0.8rem 1rem; }
                    footer { margin-top: 2rem; color: #888; font-size: 0.85rem; }
                    """;

    /** As {@link #write(Map, Path, String)}, with no reference-image provenance to show - the four existing {@code ConformanceReportTest} call sites keep compiling unchanged. */
    public static void write(Map<String, ConformanceResult> results, Path outputDirectory) {
        write(results, outputDirectory, null);
    }

    /**
     * Writes the whole site into {@code outputDirectory}: {@code index.html}, a {@code <chapter>/index.html} each, and a {@code <chapter>/<test>.html} each. The per-test PNGs are
     * not written here - the harness writes those as it renders, because holding 525 pairs of images in memory to pass into this method would cost the better part of a gigabyte.
     *
     * @param referenceProvenance
     *            #199: a short line naming what the pass/fail comparison was actually run against - {@link W3cSvgConformanceCheck} reads this from the reference set's own
     *            {@code generation-manifest.properties} (#196) and passes it through here so the published site says so, rather than a reader having to infer it. {@code null}
     *            omits the line entirely (used by {@link #write(Map, Path)}, and by any caller with no such manifest to read).
     */
    public static void write(Map<String, ConformanceResult> results, Path outputDirectory, String referenceProvenance) {
        Map<String, List<ConformanceResult>> chapters = new TreeMap<>();
        for (ConformanceResult result : new TreeMap<>(results).values()) {
            chapters.computeIfAbsent(result.chapter(), c -> new ArrayList<>())
                .add(result);
        }

        writeFile(outputDirectory.resolve("index.html"), indexPage(chapters, referenceProvenance));
        for (Map.Entry<String, List<ConformanceResult>> entry : chapters.entrySet()) {
            String chapter = entry.getKey();
            writeFile(outputDirectory.resolve(chapter)
                .resolve("index.html"), chapterPage(chapter, entry.getValue()));
            for (ConformanceResult result : entry.getValue()) {
                writeFile(outputDirectory.resolve(chapter)
                    .resolve(result.name() + ".html"), testPage(result));
            }
        }
    }

    // --- level 1: overall ----------------------------------------------------

    private static String indexPage(Map<String, List<ConformanceResult>> chapters, String referenceProvenance) {
        List<ConformanceResult> all = chapters.values()
            .stream()
            .flatMap(List::stream)
            .toList();

        StringBuilder rows = new StringBuilder();
        for (Map.Entry<String, List<ConformanceResult>> entry : chapters.entrySet()) {
            List<ConformanceResult> tests = entry.getValue();
            rows.append("<tr><td><a href=\"")
                .append(escape(entry.getKey()))
                .append("/index.html\">")
                .append(escape(entry.getKey()))
                .append("</a></td>")
                .append("<td class=\"num\">")
                .append(passCount(tests))
                .append(" / ")
                .append(tests.size())
                .append("</td>")
                .append("<td class=\"num\">")
                .append(percent(100.0 * passCount(tests) / tests.size()))
                .append("</td>")
                .append("<td class=\"num\">")
                .append(percent(meanSimilarity(tests)))
                .append("</td></tr>\n");
        }

        StringBuilder html = page("Foxglove - W3C SVG 1.1 conformance");
        html.append("<h1>Foxglove - W3C SVG 1.1 (Second Edition) conformance</h1>\n");
        html.append(headline(all));
        html.append("<table>\n<thead><tr><th>Chapter</th><th class=\"num\">Passing</th><th class=\"num\">%</th>")
            .append("<th class=\"num\">Ink matched</th></tr></thead>\n<tbody>\n")
            .append(rows)
            .append("</tbody>\n</table>\n");
        html.append("""
                        <div class="note">
                        <strong>Ink matched</strong> is the share of pixels carrying ink - in this renderer's output, the
                        reference, or both - that agree, averaged over the chapter. It counts only where something was drawn,
                        deliberately: most of a test canvas is blank, so measuring over the whole image would score a document
                        that rendered <em>nothing at all</em> at around 93% purely for agreeing about the background.
                        <p>It moves whenever rendering improves, which a pass count only does once a test crosses its threshold,
                        so it is the better progress signal of the two - and the worse verdict. Passing still means matching the
                        reference across the whole image, which is a stricter thing to ask.</p>
                        </div>
                        """);
        if (referenceProvenance != null) {
            html.append("<div class=\"note\"><strong>Reference images.</strong> The pass/fail verdict and ink-matched figure ")
                .append("above are computed against a fresh, independently-rendered reference set, not the W3C image shown ")
                .append("on each test's own page (that one is kept only because it is safely W3C-hosted, and may no longer ")
                .append("visually agree pixel-for-pixel with what was actually compared). ")
                .append("See <a href=\"https://github.com/ctgnz/foxglove/issues/200\">ctgnz/foxglove#200</a>.<br>")
                .append(escape(referenceProvenance))
                .append("</div>\n");
        }
        return html.append(footer(""))
            .toString();
    }

    // --- level 2: one chapter ------------------------------------------------

    private static String chapterPage(String chapter, List<ConformanceResult> tests) {
        // most-similar first: this is a progress report, so the near-misses belong at the top where they read as a
        // worklist, rather than buried alphabetically among tests that render nothing at all
        List<ConformanceResult> ordered = new ArrayList<>(tests);
        ordered.sort(Comparator.comparingDouble(ConformanceResult::similarity)
            .reversed()
            .thenComparing(ConformanceResult::name));

        StringBuilder rows = new StringBuilder();
        for (ConformanceResult result : ordered) {
            rows.append("<tr><td><a href=\"")
                .append(escape(result.name()))
                .append(".html\">")
                .append(escape(result.name()))
                .append("</a></td>")
                .append("<td>")
                .append(verdict(result))
                .append("</td>")
                .append("<td class=\"num\">")
                .append(percent(result.similarityPercent()))
                .append("</td></tr>\n");
        }

        StringBuilder html = page("Foxglove conformance - " + chapter);
        html.append("<nav><a href=\"../index.html\">&larr; All chapters</a></nav>\n");
        html.append("<h1>")
            .append(escape(chapter))
            .append("</h1>\n");
        html.append(headline(tests));
        html.append(unmeasurableNote(chapter));
        html.append("<table>\n<thead><tr><th>Test</th><th>Result</th><th class=\"num\">Ink matched</th></tr></thead>\n<tbody>\n")
            .append(rows)
            .append("</tbody>\n</table>\n");
        return html.append(footer("../"))
            .toString();
    }

    // --- level 3: one test ---------------------------------------------------

    private static String testPage(ConformanceResult result) {
        String name = result.name();
        StringBuilder html = page("Foxglove conformance - " + name);
        html.append("<nav><a href=\"../index.html\">All chapters</a> / <a href=\"index.html\">")
            .append(escape(result.chapter()))
            .append("</a></nav>\n");
        html.append("<h1>")
            .append(escape(name))
            .append("</h1>\n");
        html.append("<div class=\"overall\">")
            .append(percent(result.similarityPercent()))
            .append(" of the ink matches</div>\n");
        html.append("<div class=\"sub\">")
            .append(verdict(result))
            .append(" - measured over ")
            .append(result.contentPixels())
            .append(" pixels carrying ink in either image.</div>\n");

        html.append(unmeasurableNote(result.chapter()));
        if (result.failureReason() != null) {
            html.append("<div class=\"note\"><strong>This test did not finish rendering.</strong> ")
                .append(escape(result.failureReason()))
                .append(" - a crash is itself a conformance failure, recorded rather than allowed to abort the run.</div>\n");
        }

        // a test that threw never reached the point of producing images, so linking to them would only yield
        // broken panels - the reference still stands on its own, showing what should have been drawn
        boolean rendered = result.failureReason() == null;
        html.append("<div class=\"panels\">\n");
        html.append(panel("W3C reference (context only, see #200)", W3C_SUITE_BASE + "/png/" + name + ".png"));
        if (rendered) {
            html.append(panel("Foxglove", name + ".png"));
            html.append(panel("Difference", name + "-diff.png"));
        }
        html.append("</div>\n");
        if (rendered) {
            html.append("""
                            <div class="note">
                            In the difference panel, <span style="color:#b42318"><strong>red</strong></span> marks pixels that
                            differ beyond tolerance and grey marks matching content. The tinted band, where present, is the
                            <code>&lt;text id="revision"&gt;</code> legend every test carries: it renders in a fallback font this
                            library cannot match, so it is excluded from the comparison entirely rather than failing every test.
                            </div>
                            """);
        }

        if (!result.passCriteria()
            .isEmpty()) {
            html.append("<h2>What this test checks</h2>\n<p class=\"criteria\">")
                .append(escape(result.passCriteria()))
                .append("</p>\n");
        }
        html.append("<p><a href=\"")
            .append(W3C_SUITE_BASE)
            .append("/svg/")
            .append(escape(name))
            .append(".svg\">View the source document at W3C</a></p>\n");
        return html.append(footer("../"))
            .toString();
    }

    private static String panel(String title, String source) {
        return "<div class=\"panel\"><h3>" + escape(title) + "</h3><img alt=\"" + escape(title) + "\" src=\""
               + escape(source) + "\"></div>\n";
    }

    // --- shared pieces -------------------------------------------------------

    private static String headline(List<ConformanceResult> tests) {
        int passed = passCount(tests);
        return "<div class=\"overall\">" + passed + " / " + tests.size() + " tests passing ("
               + percent(tests.isEmpty() ? 0 : 100.0 * passed / tests.size()) + ")</div>\n"
               + "<div class=\"sub\">Ink matched " + percent(meanSimilarity(tests)) + "</div>\n";
    }

    /**
     * The {@code animate} chapter is not a rendering failure but an unanswerable question - see {@link ConformanceResult#staticallyComparable}. Saying so where the numbers appear
     * stops the report implying that 78 tests' worth of SMIL is broken, when what is actually missing is a way to measure it.
     */
    private static String unmeasurableNote(String chapter) {
        if (!"animate".equals(chapter)) {
            return "";
        }
        return """
                        <div class="note">
                        <strong>These results are not meaningful.</strong> This check renders a single static frame, and nothing
                        in a test document records which moment in time its reference image was captured at - so a correct SMIL
                        implementation and a broken one are equally likely to mismatch. Animation behaviour is covered by
                        dedicated unit tests instead; see
                        <a href="https://github.com/ctgnz/foxglove/issues/112">ctgnz/foxglove#112</a> for the comparison that
                        would actually work.
                        </div>
                        """;
    }

    private static String verdict(ConformanceResult result) {
        return result.passed() ? "<span class=\"pass\">pass</span>" : "<span class=\"fail\">fail</span>";
    }

    private static int passCount(List<ConformanceResult> tests) {
        return (int) tests.stream()
            .filter(ConformanceResult::passed)
            .count();
    }

    private static double meanSimilarity(List<ConformanceResult> tests) {
        return tests.stream()
            .mapToDouble(ConformanceResult::similarityPercent)
            .average()
            .orElse(0.0);
    }

    private static String percent(double value) {
        return String.format("%.1f%%", value);
    }

    private static StringBuilder page(String title) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
        html.append("<title>")
            .append(escape(title))
            .append("</title>\n<style>\n")
            .append(STYLE);
        html.append("</style>\n</head>\n<body>\n");
        return html;
    }

    private static String footer(String rootPrefix) {
        return "<footer>Generated " + Instant.now() + " from the latest master build. See "
               + "<a href=\"https://github.com/ctgnz/foxglove/issues/44\">ctgnz/foxglove#44</a> for methodology. "
               + "Reference images are served by <a href=\"" + W3C_SUITE_BASE + "/\">W3C</a>; "
               + "the test suite is &copy; World Wide Web Consortium.</footer>\n</body>\n</html>\n";
    }

    private static void writeFile(Path file, String content) {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }

    private ConformanceReport() {
    }

}
