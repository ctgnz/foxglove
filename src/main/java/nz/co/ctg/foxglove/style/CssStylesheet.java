package nz.co.ctg.foxglove.style;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.SvgStyle;

/**
 * The parsed content of every {@code <style>} element in a document, matched against elements as they render.
 */
public final class CssStylesheet {

    private static final Pattern COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final CssStylesheet EMPTY = new CssStylesheet(List.of());

    /**
     * Parses every {@code <style>} element whose {@code type} is blank or {@code text/css} - the only type this
     * codebase, or the SVG specification, gives any meaning to. Elements of another type are ignored, as an
     * unrecognised stylesheet language is not this codebase's to interpret.
     */
    public static CssStylesheet of(List<SvgStyle> elements) {
        StringBuilder text = new StringBuilder();
        for (SvgStyle element : elements) {
            String type = element.getType();
            if (StringUtils.isBlank(type) || "text/css".equalsIgnoreCase(type)) {
                text.append(StringUtils.defaultString(element.getValue())).append('\n');
            }
        }
        return text.isEmpty() ? EMPTY : parse(text.toString());
    }

    private static CssStylesheet parse(String css) {
        String text = COMMENT.matcher(css).replaceAll("");
        List<CssRule> rules = new ArrayList<>();
        int position = 0;
        while (true) {
            int openBrace = text.indexOf('{', position);
            if (openBrace < 0) {
                break;
            }
            int closeBrace = text.indexOf('}', openBrace + 1);
            if (closeBrace < 0) {
                break;
            }
            String selectorText = text.substring(position, openBrace).trim();
            String body = text.substring(openBrace + 1, closeBrace);
            List<CssSelector> selectors = List.of(StringUtils.split(selectorText, ',')).stream()
                .map(CssSelector::parse)
                .collect(Collectors.toList());
            if (!selectors.isEmpty()) {
                rules.add(new CssRule(selectors, CssDeclarations.parse(body)));
            }
            position = closeBrace + 1;
        }
        return new CssStylesheet(rules);
    }

    private final List<CssRule> rules;

    private CssStylesheet(List<CssRule> rules) {
        this.rules = rules;
    }

    /**
     * Every declaration from every rule that matches {@code element}, ordered by that rule's matching specificity
     * ascending, then document order - the order the cascade should apply them in, so a later one always wins a
     * conflict over an earlier one.
     */
    public List<CssDeclaration> matchingDeclarations(AbstractSvgStylable element) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < rules.size(); i++) {
            CssRule rule = rules.get(i);
            int order = i;
            rule.matchingSpecificity(element).ifPresent(specificity -> matches.add(new Match(specificity, order, rule)));
        }
        matches.sort(Comparator.<Match>comparingInt(m -> m.specificity).thenComparingInt(m -> m.order));
        List<CssDeclaration> declarations = new ArrayList<>();
        for (Match match : matches) {
            declarations.addAll(match.rule.getDeclarations());
        }
        return declarations;
    }

    private record Match(int specificity, int order, CssRule rule) {
    }

}
