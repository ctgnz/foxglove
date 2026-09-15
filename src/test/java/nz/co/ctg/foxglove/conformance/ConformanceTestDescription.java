package nz.co.ctg.foxglove.conformance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pulls a W3C test's own {@code <d:passCriteria>} prose out of its source document, for #110's per-test pages - a similarity percentage says how far apart two images are, never
 * what the test was trying to establish.
 * <p>
 * Read straight from the file text rather than through {@link nz.co.ctg.foxglove.FoxgloveParser}: the element lives in the suite's own {@code .../svg/testsuite/description/}
 * namespace, which the parser has no binding for and no reason to acquire one for. All 525 documents carry it, and its content is plain XHTML paragraphs.
 */
public final class ConformanceTestDescription {

    private static final Pattern PASS_CRITERIA = Pattern.compile("<d:passCriteria\\b[^>]*>(.*?)</d:passCriteria>", Pattern.DOTALL);
    private static final Pattern TAG = Pattern.compile("<[^>]*>");

    /** The pass criteria as flowing plain text, or an empty string if the document carries none. */
    public static String passCriteria(Path svgFile) {
        String source;
        try {
            source = Files.readString(svgFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return passCriteriaOf(source);
    }

    /** As {@link #passCriteria}, against a document already in memory. */
    static String passCriteriaOf(String source) {
        Matcher matcher = PASS_CRITERIA.matcher(source);
        if (!matcher.find()) {
            return "";
        }
        // the criteria are wrapped in XHTML <p> elements; flatten to text, since the report escapes what it renders
        return TAG.matcher(matcher.group(1))
            .replaceAll(" ")
            .replaceAll("\\s+", " ")
            .trim();
    }

    private ConformanceTestDescription() {
    }

}
