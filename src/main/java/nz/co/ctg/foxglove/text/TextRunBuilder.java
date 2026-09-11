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

    /** One run ready to become a {@code Text} node. {@code enclosingPath} is non-null when it (or an ancestor) is a
     * {@code <textPath>}, meaning it lays out along a path (#29) rather than the ordinary linear flow. */
    record Run(AbstractSvgStylable owner, RenderContext ownerContext, String text, SvgTextPath enclosingPath) {
    }

    private record RawRun(AbstractSvgStylable owner, RenderContext ownerContext, String text, String spaceMode, SvgTextPath enclosingPath) {
        RawRun withText(String replacement) {
            return new RawRun(owner, ownerContext, replacement, spaceMode, enclosingPath);
        }

        boolean isPreserve() {
            return "preserve".equalsIgnoreCase(spaceMode);
        }
    }

    private TextRunBuilder() {
    }

    static List<Run> build(SvgText root, RenderContext context) {
        List<RawRun> raw = new ArrayList<>();
        walk(root, context, "default", null, raw);
        List<RawRun> processed = collapseWhitespace(raw);
        if (processed.isEmpty()) {
            return List.of(new Run(root, context, "", null));
        }
        return processed.stream().map(run -> new Run(run.owner(), run.ownerContext(), run.text(), run.enclosingPath())).toList();
    }

    /**
     * Walks {@code owner}'s content in document order, applying its style cascade once (the same call
     * {@code createGraphic} used to make directly) and resolving {@code xml:space}, which is inherited, before
     * descending into any child. {@code enclosingPath} carries the nearest {@code <textPath>} ancestor (including
     * {@code owner} itself) down to every run it contains, so a nested {@code tspan}/{@code tref}/{@code altGlyph}
     * inside a {@code <textPath>} still lays out along the path.
     */
    private static void walk(AbstractSvgStylable owner, RenderContext ownerContext, String inheritedSpaceMode, SvgTextPath enclosingPath,
        List<RawRun> out) {
        owner.applyStyle(ownerContext);
        String spaceMode = StringUtils.defaultIfBlank(owner.getXmlSpace(), inheritedSpaceMode);
        SvgTextPath path = owner instanceof SvgTextPath textPath ? textPath : enclosingPath;
        if (owner instanceof AbstractSvgTextContentElement container) {
            RenderContext childContext = ownerContext.resolveChild(owner);
            for (Object item : container.getContent()) {
                if (item instanceof String text) {
                    out.add(new RawRun(owner, ownerContext, text, spaceMode, path));
                } else if (item instanceof AbstractSvgStylable child) {
                    walk(child, childContext, spaceMode, path, out);
                }
            }
        } else if (owner instanceof SvgTextReference reference) {
            out.add(new RawRun(owner, ownerContext, resolveReferencedText(reference, ownerContext), spaceMode, path));
        } else if (owner instanceof SvgAltGlyph altGlyph) {
            out.add(new RawRun(owner, ownerContext, StringUtils.defaultString(altGlyph.getValue()), spaceMode, path));
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
