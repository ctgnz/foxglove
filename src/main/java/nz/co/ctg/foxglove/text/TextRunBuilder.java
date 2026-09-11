package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.RenderContext;

/**
 * Flattens a {@link SvgText}'s mixed content (#1) into an ordered list of styled, whitespace-processed runs (#27).
 * <p>
 * Each run keeps the element that owns it (a {@code text}, {@code tspan}, {@code tref}, {@code textPath} or
 * {@code altGlyph}) and the rendering context to resolve that owner's style against, so the caller can build one
 * independently-styled {@link javafx.scene.text.Text} node per run.
 */
final class TextRunBuilder {

    /** One run ready to become a {@code Text} node. */
    record Run(AbstractSvgStylable owner, RenderContext ownerContext, String text) {
    }

    private record RawRun(AbstractSvgStylable owner, RenderContext ownerContext, String text, String spaceMode) {
        RawRun withText(String replacement) {
            return new RawRun(owner, ownerContext, replacement, spaceMode);
        }

        boolean isPreserve() {
            return "preserve".equalsIgnoreCase(spaceMode);
        }
    }

    private TextRunBuilder() {
    }

    static List<Run> build(SvgText root, RenderContext context) {
        List<RawRun> raw = new ArrayList<>();
        walk(root, context, "default", raw);
        List<RawRun> processed = collapseWhitespace(raw);
        if (processed.isEmpty()) {
            return List.of(new Run(root, context, ""));
        }
        return processed.stream().map(run -> new Run(run.owner(), run.ownerContext(), run.text())).toList();
    }

    /**
     * Walks {@code owner}'s content in document order, applying its style cascade once (the same call
     * {@code createGraphic} used to make directly) and resolving {@code xml:space}, which is inherited, before
     * descending into any child.
     */
    private static void walk(AbstractSvgStylable owner, RenderContext ownerContext, String inheritedSpaceMode, List<RawRun> out) {
        owner.applyStyle(ownerContext);
        String spaceMode = StringUtils.defaultIfBlank(owner.getXmlSpace(), inheritedSpaceMode);
        if (owner instanceof AbstractSvgTextContentElement container) {
            RenderContext childContext = ownerContext.resolveChild(owner);
            for (Object item : container.getContent()) {
                if (item instanceof String text) {
                    out.add(new RawRun(owner, ownerContext, text, spaceMode));
                } else if (item instanceof AbstractSvgStylable child) {
                    walk(child, childContext, spaceMode, out);
                }
            }
        } else if (owner instanceof SvgTextReference reference) {
            out.add(new RawRun(owner, ownerContext, resolveReferencedText(reference, ownerContext), spaceMode));
        } else if (owner instanceof SvgAltGlyph altGlyph) {
            out.add(new RawRun(owner, ownerContext, StringUtils.defaultString(altGlyph.getValue()), spaceMode));
        }
    }

    /**
     * The character data a {@code <tref>} inlines: its referenced element's own flattened value (#1's
     * {@link ISvgTextContentElement#getValue()}), not a recursive walk of that element's styling - the {@code tref}
     * site supplies the style, per the specification.
     */
    private static String resolveReferencedText(SvgTextReference reference, RenderContext context) {
        if (context.getElementIndex() == null) {
            return "";
        }
        return context.getElementIndex().resolve(reference.getXlinkHref(), ISvgTextContentElement.class)
            .map(ISvgTextContentElement::getValue)
            .map(StringUtils::defaultString)
            .orElse("");
    }

    /**
     * Applies {@code xml:space} across the whole flattened sequence: {@code default} collapses newlines/tabs to a
     * space, consolidates runs of spaces, drops a redundant leading space where one run's trailing space meets the
     * next run's leading one, and trims the very start and end of the sequence; {@code preserve} leaves a run - and
     * the boundary either side of it - untouched.
     */
    private static List<RawRun> collapseWhitespace(List<RawRun> raw) {
        List<RawRun> collapsed = new ArrayList<>();
        for (RawRun run : raw) {
            collapsed.add(run.isPreserve() ? run : run.withText(collapseInternal(run.text())));
        }
        for (int i = 1; i < collapsed.size(); i++) {
            RawRun previous = collapsed.get(i - 1);
            RawRun current = collapsed.get(i);
            if (!previous.isPreserve() && !current.isPreserve() && previous.text().endsWith(" ") && current.text().startsWith(" ")) {
                collapsed.set(i, current.withText(current.text().substring(1)));
            }
        }
        trimLeading(collapsed);
        trimTrailing(collapsed);
        return collapsed.stream().filter(run -> !run.text().isEmpty()).toList();
    }

    private static String collapseInternal(String text) {
        return text.replaceAll("[\\t\\r\\n]", " ").replaceAll(" {2,}", " ");
    }

    private static void trimLeading(List<RawRun> runs) {
        for (int i = 0; i < runs.size(); i++) {
            RawRun run = runs.get(i);
            if (run.isPreserve()) {
                return;
            }
            String trimmed = StringUtils.stripStart(run.text(), " ");
            runs.set(i, run.withText(trimmed));
            if (!trimmed.isEmpty()) {
                return;
            }
        }
    }

    private static void trimTrailing(List<RawRun> runs) {
        for (int i = runs.size() - 1; i >= 0; i--) {
            RawRun run = runs.get(i);
            if (run.isPreserve()) {
                return;
            }
            String trimmed = StringUtils.stripEnd(run.text(), " ");
            runs.set(i, run.withText(trimmed));
            if (!trimmed.isEmpty()) {
                return;
            }
        }
    }

}
