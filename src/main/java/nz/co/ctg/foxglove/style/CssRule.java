package nz.co.ctg.foxglove.style;

import java.util.List;
import java.util.Optional;

import nz.co.ctg.foxglove.AbstractSvgStylable;

/**
 * A stylesheet rule: a comma-separated group of selectors sharing one declaration block.
 */
public final class CssRule {

    private final List<CssSelector> selectors;
    private final List<CssDeclaration> declarations;

    public CssRule(List<CssSelector> selectors, List<CssDeclaration> declarations) {
        this.selectors = selectors;
        this.declarations = declarations;
    }

    public List<CssDeclaration> getDeclarations() {
        return declarations;
    }

    /**
     * The highest specificity among this rule's selectors that match {@code element}, or empty if none do - a rule matches, and its declarations apply, as soon as any one selector
     * in its comma-separated group matches.
     */
    public Optional<Integer> matchingSpecificity(AbstractSvgStylable element) {
        return selectors.stream()
            .filter(selector -> selector.matches(element))
            .map(CssSelector::specificity)
            .max(Integer::compareTo);
    }

}
