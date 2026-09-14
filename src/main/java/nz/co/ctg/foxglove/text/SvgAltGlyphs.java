package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.RenderContext;

/**
 * Resolves an {@code <altGlyph>} to the glyphs it asks to be drawn in place of its own characters (#138).
 * <p>
 * The reference can name either a {@code <glyph>} directly or an {@code <altGlyphDef>}, and an {@code <altGlyphDef>}
 * comes in two shapes, both of which the suite exercises:
 * <ul>
 * <li>a flat list of {@code <glyphRef>} children, <b>all</b> of which must resolve
 * ({@code text-altglyph-01-b});
 * <li>a list of {@code <altGlyphItem>} alternatives, of which the <b>first whose every {@code <glyphRef>}
 * resolves</b> is used ({@code text-altglyph-02-b}, which salts its items with deliberate {@code #bad-link}
 * references to check exactly that).
 * </ul>
 * <b>Anything short of a complete resolution falls back to rendering the element's own character content</b>, which
 * is what the {@code <altGlyph>} carries it for. That is the specification's rule and also the only safe one: a
 * partial substitution would drop characters silently.
 */
final class SvgAltGlyphs {

    private SvgAltGlyphs() {
    }

    /** One resolved substitute: the glyph to draw and the font whose metrics scale it. */
    record Substitute(SvgGlyph glyph, SvgFontGlyphs font) {
    }

    /**
     * The glyphs {@code altGlyph} substitutes, or null when the reference does not resolve completely - the signal to
     * render its own characters instead, exactly as before #138.
     */
    static List<Substitute> resolve(SvgAltGlyph altGlyph, RenderContext context) {
        if (context == null || context.getElementIndex() == null) {
            return null;
        }
        String href = altGlyph.getXlinkHref();
        if (href == null || href.isBlank()) {
            return null;
        }
        // the reference may name a <glyph> outright, rather than going through an <altGlyphDef>
        List<Substitute> direct = substitutesFor(List.of(href), context);
        if (direct != null) {
            return direct;
        }
        return context.getElementIndex().resolve(href, SvgAltGlyphDef.class)
            .map(definition -> fromDefinition(definition, context))
            .orElse(null);
    }

    /**
     * A definition's glyphs: its {@code <altGlyphItem>} alternatives in order if it has any, otherwise its own
     * {@code <glyphRef>} children taken together.
     */
    private static List<Substitute> fromDefinition(SvgAltGlyphDef definition, RenderContext context) {
        List<String> references = new ArrayList<>();
        boolean hasItems = false;
        for (ISvgGlyphItem child : definition.getGlyphItems()) {
            if (child instanceof SvgAltGlyphItem item) {
                hasItems = true;
                List<Substitute> resolved = substitutesFor(referencesOf(item.getGlyphRef()), context);
                if (resolved != null) {
                    return resolved;
                }
            } else if (child instanceof SvgGlyphRef reference) {
                references.add(reference.getXlinkHref());
            }
        }
        // a definition offering alternatives and satisfying none of them substitutes nothing at all - it must not
        // fall through to whichever loose glyphRefs happened to sit alongside its items
        return hasItems ? null : substitutesFor(references, context);
    }

    private static List<String> referencesOf(List<SvgGlyphRef> glyphRefs) {
        return glyphRefs.stream().map(SvgGlyphRef::getXlinkHref).toList();
    }

    /** Every reference resolved to a glyph and its owning font, or null if any one of them fails. */
    private static List<Substitute> substitutesFor(List<String> references, RenderContext context) {
        if (references.isEmpty()) {
            return null;
        }
        List<Substitute> substitutes = new ArrayList<>();
        for (String reference : references) {
            SvgGlyph glyph = context.getElementIndex().resolve(reference, SvgGlyph.class).orElse(null);
            if (glyph == null) {
                return null;
            }
            SvgFontGlyphs font = fontOwning(glyph, context);
            if (font == null) {
                return null;
            }
            substitutes.add(new Substitute(glyph, font));
        }
        return substitutes;
    }

    /**
     * The {@code <font>} a glyph belongs to, found by looking for the one that contains it.
     * <p>
     * Needed because the scale and default advance come from the font, and a substituted glyph is routinely from a
     * different font than the text around it. The elements carry no parent pointer, so this walks the document's
     * fonts - there are a handful at most, and only an {@code <altGlyph>} ever asks.
     */
    private static SvgFontGlyphs fontOwning(SvgGlyph glyph, RenderContext context) {
        for (SvgFont font : context.getElementIndex().getElementsOfType(SvgFont.class)) {
            for (ISvgElement child : font.getContent()) {
                if (child == glyph) {
                    return SvgFontResolver.glyphsOf(font);
                }
            }
        }
        return null;
    }

}
