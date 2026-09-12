package nz.co.ctg.foxglove.conformance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

/**
 * Generates the static HTML dashboard #92 publishes to GitHub Pages on every push to master - an overall pass rate
 * plus a per-chapter breakdown, from the same {@code actual} results {@link W3cSvgConformanceCheck} itself computes
 * (independent of whether that run's own manifest-diff assertion passes or fails - a regression is exactly the kind
 * of thing the dashboard needs to keep showing, not hide by failing before it's written).
 * <p>
 * Deliberately a single, self-contained file - inline CSS, no external resources, nothing that could fail to load
 * once published - since it needs nothing beyond being copied as-is to Pages.
 */
public final class ConformanceReport {

    public static void write(Map<String, Boolean> actual, Path outputFile) {
        Map<String, int[]> chapters = new TreeMap<>();
        int totalPass = 0;
        int total = 0;
        for (Map.Entry<String, Boolean> entry : actual.entrySet()) {
            int[] counts = chapters.computeIfAbsent(chapterOf(entry.getKey()), c -> new int[2]);
            counts[1]++;
            total++;
            if (entry.getValue()) {
                counts[0]++;
                totalPass++;
            }
        }

        StringBuilder rows = new StringBuilder();
        for (Map.Entry<String, int[]> entry : chapters.entrySet()) {
            int pass = entry.getValue()[0];
            int all = entry.getValue()[1];
            double pct = all == 0 ? 0.0 : 100.0 * pass / all;
            rows.append("<tr><td>").append(escape(entry.getKey())).append("</td><td>").append(pass).append(" / ").append(all)
                .append("</td><td>").append(String.format("%.1f", pct)).append("%</td></tr>\n");
        }

        double overallPct = total == 0 ? 0.0 : 100.0 * totalPass / total;

        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n");
        html.append("<title>Foxglove - W3C SVG 1.1 conformance</title>\n<style>\n");
        html.append("body { font-family: system-ui, sans-serif; margin: 2rem; color: #222; }\n");
        html.append("h1 { font-size: 1.4rem; }\n");
        html.append(".overall { font-size: 2rem; font-weight: 600; margin: 1rem 0; }\n");
        html.append("table { border-collapse: collapse; width: 100%; max-width: 640px; }\n");
        html.append("th, td { text-align: left; padding: 0.4rem 0.8rem; border-bottom: 1px solid #ddd; }\n");
        html.append("th { background: #f4f4f4; }\n");
        html.append("footer { margin-top: 2rem; color: #888; font-size: 0.85rem; }\n");
        html.append("</style>\n</head>\n<body>\n");
        html.append("<h1>Foxglove - W3C SVG 1.1 (Second Edition) conformance</h1>\n");
        html.append("<div class=\"overall\">").append(totalPass).append(" / ").append(total).append(" tests passing (")
            .append(String.format("%.1f", overallPct)).append("%)</div>\n");
        html.append("<table>\n<thead><tr><th>Chapter</th><th>Pass / Total</th><th>%</th></tr></thead>\n<tbody>\n");
        html.append(rows);
        html.append("</tbody>\n</table>\n");
        html.append("<footer>Generated ").append(Instant.now()).append(" from the latest master build. See ")
            .append("<a href=\"https://github.com/ctgnz/foxglove/issues/44\">ctgnz/foxglove#44</a> for methodology and known ")
            .append("limitations (SMIL animation tests aren't meaningfully comparable via a single static frame).</footer>\n");
        html.append("</body>\n</html>\n");

        try {
            Files.createDirectories(outputFile.getParent());
            Files.writeString(outputFile, html.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String chapterOf(String testName) {
        int dash = testName.indexOf('-');
        return dash < 0 ? testName : testName.substring(0, dash);
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private ConformanceReport() {
    }

}
