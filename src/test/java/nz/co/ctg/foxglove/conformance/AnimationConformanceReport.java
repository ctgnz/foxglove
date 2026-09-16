package nz.co.ctg.foxglove.conformance;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 * #220: the real per-test report for {@link W3cSvgAnimationCheck}'s seeked-against-a-live-browser comparison - a flat listing (name, pass/fail, and the same diagnostic note the
 * check already prints to its own console summary: worst differing ratio, how many moments were sampled, how many animations this renderer actually built), written into the same
 * published site {@link ConformanceReport} generates, under its own {@code animation/} directory.
 * <p>
 * Deliberately distinct from {@code animate/} - the existing STATIC single-frame chapter listing for {@code animate-*} tests (#201), left unchanged: that page shows what a static
 * comparison can (context only, explicitly marked "not meaningful"), this one shows the real number.
 * <p>
 * Deliberately no per-test detail pages or images here yet, unlike the static report's own per-test panels - showing the live reference engine's own screenshot would raise the
 * same "republishing a render of the W3C suite's copyrighted documents" question #197 worked through for the static reference set (foxglove's own render is already published on
 * every static per-test page, but a screenshot of a real browser rendering the same copyrighted document is a different thing, not yet resolved for this case). The notes column
 * already carries the most useful diagnostic (worst differing ratio, moments sampled, animations built) without it.
 */
public final class AnimationConformanceReport {

    private static final String W3C_SUITE_BASE = "https://www.w3.org/Graphics/SVG/Test/20110816";

    public static void write(Map<String, Boolean> results, Map<String, String> notes, Path outputDirectory) {
        long passed = results.values()
            .stream()
            .filter(Boolean::booleanValue)
            .count();

        StringBuilder rows = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : new TreeMap<>(results).entrySet()) {
            String name = entry.getKey();
            rows.append("<tr><td><a href=\"")
                .append(W3C_SUITE_BASE)
                .append("/svg/")
                .append(ConformanceReport.escape(name))
                .append(".svg\">")
                .append(ConformanceReport.escape(name))
                .append("</a></td><td>")
                .append(entry.getValue() ? "<span class=\"pass\">pass</span>" : "<span class=\"fail\">fail</span>")
                .append("</td><td>")
                .append(ConformanceReport.escape(notes.getOrDefault(name, "")))
                .append("</td></tr>\n");
        }

        StringBuilder html = ConformanceReport.page("Foxglove conformance - animation");
        html.append("<nav><a href=\"../index.html\">&larr; All chapters</a></nav>\n");
        html.append("<h1>animation</h1>\n");
        html.append("<div class=\"overall\">")
            .append(passed)
            .append(" / ")
            .append(results.size())
            .append(" tests passing</div>\n");
        html.append("""
                        <div class="note">
                        Unlike every other category on this dashboard, these results come from a <strong>live, seeked
                        comparison</strong> against a headless Chromium reference (#208), not a single static frame - each
                        <code>animate-*</code> test is rendered at several moments derived from its own real SMIL timing and
                        compared against the reference engine seeked to the same moments, the worst moment deciding pass/fail.
                        See <a href="https://github.com/ctgnz/foxglove/issues/208">ctgnz/foxglove#208</a> for methodology, and
                        the <a href="../animate/index.html">animate chapter's static listing</a> for context-only single-frame
                        renders of the same documents.
                        </div>
                        """);
        html.append("<table>\n<thead><tr><th>Test</th><th>Result</th><th>Notes</th></tr></thead>\n<tbody>\n")
            .append(rows)
            .append("</tbody>\n</table>\n");
        ConformanceReport.writeFile(outputDirectory.resolve("index.html"), html.append(ConformanceReport.footer("../"))
            .toString());
    }

    private AnimationConformanceReport() {
    }

}
