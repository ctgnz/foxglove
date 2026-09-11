package nz.co.ctg.foxglove.style;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * Parses a CSS declaration block - the body of a stylesheet rule, or the inline {@code style} attribute - into
 * {@link CssDeclaration}s. The one parser is shared by both, since the grammar is identical.
 */
public final class CssDeclarations {

    private static final String IMPORTANT_SUFFIX = "!important";

    /**
     * Splits {@code text} on {@code ;} and then, for each part, on the first {@code :} - a value may legitimately
     * contain further colons. Property names are matched case insensitively, so are lower-cased here; values are
     * left as written, since each property's own conversion decides what is significant.
     */
    public static List<CssDeclaration> parse(String text) {
        List<CssDeclaration> declarations = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return declarations;
        }
        for (String part : StringUtils.split(text, ';')) {
            String[] pair = StringUtils.split(part, ":", 2);
            if (pair.length < 2) {
                continue;
            }
            String property = pair[0].trim().toLowerCase(Locale.ROOT);
            String value = pair[1].trim();
            boolean important = StringUtils.endsWithIgnoreCase(value, IMPORTANT_SUFFIX);
            if (important) {
                value = value.substring(0, value.length() - IMPORTANT_SUFFIX.length()).trim();
            }
            if (!value.isEmpty()) {
                declarations.add(new CssDeclaration(property, value, important));
            }
        }
        return declarations;
    }

    private CssDeclarations() {
    }

}
