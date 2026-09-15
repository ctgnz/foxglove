package nz.co.ctg.foxglove.style;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.AbstractSvgStylable;

/**
 * A single compound selector - an optional element type (or universal), any number of {@code .class} fragments, and an optional {@code #id} fragment, matched against one element
 * with no notion of ancestry.
 * <p>
 * This is deliberately not a general CSS selector: no combinators (descendant, child, sibling), pseudo-classes or attribute selectors, matching the reduced grammar #15 asks for -
 * "element, class and id selectors" - and there is nowhere yet to walk an ancestor chain from.
 */
public final class CssSelector {

    private static final Pattern CLASS_OR_ID = Pattern.compile("([.#])([-\\w]+)");

    /**
     * Parses one compound selector token, such as {@code rect}, {@code .warning}, {@code #title} or {@code rect.warning#title}. A blank or {@code *} type prefix is universal - it
     * matches any element but does not contribute to specificity.
     */
    public static CssSelector parse(String token) {
        String text = token.trim();
        Matcher matcher = CLASS_OR_ID.matcher(text);
        int firstFragment = text.length();
        List<String> classNames = new ArrayList<>();
        String id = null;
        while (matcher.find()) {
            firstFragment = Math.min(firstFragment, matcher.start());
            String name = matcher.group(2);
            if (".".equals(matcher.group(1))) {
                classNames.add(name);
            } else {
                id = name;
            }
        }
        String type = text.substring(0, firstFragment)
            .trim();
        if (type.isEmpty() || "*".equals(type)) {
            type = null;
        }
        return new CssSelector(type, classNames, id);
    }

    private final String type;
    private final List<String> classNames;
    private final String id;

    private CssSelector(String type, List<String> classNames, String id) {
        this.type = type;
        this.classNames = classNames;
        this.id = id;
    }

    /**
     * The standard CSS specificity count for a selector with no combinators: id count weighs 100, class count 10, a type name 1, and the universal selector 0.
     */
    public int specificity() {
        return (id != null ? 100 : 0) + classNames.size() * 10 + (type != null ? 1 : 0);
    }

    public boolean matches(AbstractSvgStylable element) {
        if (type != null && !type.equals(element.getElementName())) {
            return false;
        }
        if (id != null && !id.equals(element.getId())) {
            return false;
        }
        if (!classNames.isEmpty()) {
            List<String> elementClasses = List.of(StringUtils.split(StringUtils.defaultString(element.getClassName())));
            if (!elementClasses.containsAll(classNames)) {
                return false;
            }
        }
        return true;
    }

}
