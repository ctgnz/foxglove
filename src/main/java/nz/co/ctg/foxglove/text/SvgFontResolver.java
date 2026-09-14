package nz.co.ctg.foxglove.text;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;

/**
 * Finds the SVG font a {@code font-family} names, if the document defines one (#61).
 * <p>
 * A font can be named in either of two shapes, and both are resolved here:
 * <ul>
 * <li><b>External</b> (#61) - a {@code <font-face>} whose {@code font-family} matches, pointing through
 * {@code <font-face-src><font-face-uri>} at another document holding the {@code <font>} and its glyph outlines.
 * Every one of the 525 documents in the W3C suite labels itself this way.
 * <li><b>Inline</b> (#137) - a {@code <font>} in this very document, identified by its own {@code <font-face>}
 * child. This is how a document that carries its own glyphs declares them, and how 15 of the 17 {@code fonts}
 * chapter tests are written.
 * </ul>
 * A locally declared {@code <font>} is preferred where both would match: it is the more specific declaration, and a
 * document carrying its own glyphs meant to use them.
 * <p>
 * {@code font-family} is a <b>list</b> ({@code "SVGFreeSansASCII,sans-serif"}), so each name is tried in turn and
 * the first that resolves wins. A family naming no SVG font at all - which is almost every real document - resolves
 * to empty and leaves text rendering exactly as it was, through the JavaFX text system.
 * <p>
 * Loaded fonts are cached by resolved URI for the life of the JVM. One 37KB font document is shared by all 525
 * suite documents, and re-parsing it per document would cost more than everything else the render does.
 */
public final class SvgFontResolver {

    private static final Map<URI, SvgFontGlyphs> CACHE = new ConcurrentHashMap<>();

    /**
     * Inline fonts, cached by the {@code <font>} element itself rather than by URI (#137).
     * <p>
     * The key matters: two documents can each declare a different font under the same family name, and one can
     * declare the same family twice, so anything keyed by name would serve one document's glyphs to another. The
     * element has identity semantics, which is exactly the right key. Weak, so a parsed document that is finished
     * with does not pin its fonts in memory for the life of the JVM.
     */
    private static final Map<SvgFont, SvgFontGlyphs> INLINE_CACHE =
        Collections.synchronizedMap(new WeakHashMap<>());

    private SvgFontResolver() {
    }

    /**
     * The SVG font {@code fontFamily} names, or null when it names none - the signal to render through the JavaFX
     * text system as before.
     */
    public static SvgFontGlyphs resolve(String fontFamily, RenderContext context) {
        if (StringUtils.isBlank(fontFamily) || context == null || context.getElementIndex() == null) {
            return null;
        }
        List<SvgFontFace> faces = context.getElementIndex().getElementsOfType(SvgFontFace.class);
        List<SvgFont> fonts = context.getElementIndex().getElementsOfType(SvgFont.class);
        if (faces.isEmpty() && fonts.isEmpty()) {
            return null;
        }
        for (String family : fontFamily.split(",")) {
            String wanted = unquote(family);
            if (wanted.isEmpty()) {
                continue;
            }
            // A <font> declared in this document is tried first: it is the more local declaration, and the document
            // went to the trouble of carrying the glyphs itself rather than pointing somewhere else for them.
            SvgFontGlyphs inline = inlineFont(wanted, fonts);
            if (inline != null) {
                return inline;
            }
            for (SvgFontFace face : faces) {
                if (!matchesFamily(wanted, face)) {
                    continue;
                }
                SvgFontGlyphs glyphs = load(face, context);
                if (glyphs != null && glyphs.isUsable()) {
                    return glyphs;
                }
            }
        }
        return null;
    }

    /**
     * A {@code <font>} in this document whose own {@code <font-face>} child names {@code family} (#137).
     * <p>
     * This is how an inline font is declared in practice - the {@code <font-face>} sits <i>inside</i> the
     * {@code <font>} and carries no {@code <font-face-src>}, there being nothing to point at. Every one of the 15
     * {@code fonts}-chapter documents that declares a font of its own is shaped this way.
     * <p>
     * Where two fonts claim the same family the first in document order wins, which is what
     * {@code fonts-desc-01-t} - the one suite document that declares the same family twice - expects.
     */
    private static SvgFontGlyphs inlineFont(String family, List<SvgFont> fonts) {
        for (SvgFont font : fonts) {
            for (ISvgElement child : font.getContent()) {
                if (child instanceof SvgFontFace face && matchesFamily(family, face)) {
                    SvgFontGlyphs glyphs = INLINE_CACHE.computeIfAbsent(font, SvgFontGlyphs::of);
                    if (glyphs.isUsable()) {
                        return glyphs;
                    }
                }
            }
        }
        return null;
    }

    private static boolean matchesFamily(String wanted, SvgFontFace face) {
        return wanted.equalsIgnoreCase(unquote(StringUtils.trimToEmpty(face.getFontFamily())));
    }

    /** Follows a {@code <font-face>}'s {@code <font-face-src><font-face-uri>} to the document holding its glyphs. */
    private static SvgFontGlyphs load(SvgFontFace face, RenderContext context) {
        for (ISvgElement child : face.getContent()) {
            if (!(child instanceof SvgFontFaceSrc src)) {
                continue;
            }
            for (ISvgElement srcChild : src.getContent()) {
                if (srcChild instanceof SvgFontFaceUri uri) {
                    SvgFontGlyphs glyphs = loadFrom(uri.getXlinkHref(), context);
                    if (glyphs != null) {
                        return glyphs;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Loads the font document {@code href} names, relative to the document's own base URI - the same resolution
     * {@code SvgImage} established for {@code <image>}, and with the same deliberate limitation: a reference that
     * needs fetching from somewhere unrelated to the document is not followed.
     */
    private static SvgFontGlyphs loadFrom(String href, RenderContext context) {
        String reference = StringUtils.trimToEmpty(href);
        if (reference.isEmpty()) {
            return null;
        }
        String fragmentId = StringUtils.substringAfter(reference, "#");
        String withoutFragment = StringUtils.substringBefore(reference, "#");
        if (withoutFragment.isEmpty()) {
            // "#id" alone names a <font> in this very document. No suite document does this - families there are
            // matched by name - but it is the plain reading of the reference and costs one lookup to honour.
            return context.getElementIndex().resolve(reference, SvgFont.class)
                .map(font -> INLINE_CACHE.computeIfAbsent(font, SvgFontGlyphs::of))
                .filter(SvgFontGlyphs::isUsable)
                .orElse(null);
        }

        URI resolved = context.getBaseUri().map(base -> base.resolve(withoutFragment)).orElse(null);
        if (resolved == null) {
            return null;
        }
        SvgFontGlyphs cached = CACHE.computeIfAbsent(resolved, uri -> parse(uri, fragmentId));
        return cached == SvgFontGlyphs.NONE ? null : cached;
    }

    /** Never throws: an unreadable or unparseable font document leaves text rendering as it was. */
    private static SvgFontGlyphs parse(URI uri, String fragmentId) {
        try (InputStream in = new URL(uri.toString()).openStream()) {
            SvgGraphic document = new FoxgloveParser().parse(in);
            List<SvgFont> fonts = document.getElementIndex().getElementsOfType(SvgFont.class);
            return fonts.isEmpty() ? SvgFontGlyphs.NONE : SvgFontGlyphs.firstFontIn(fonts, fragmentId);
        } catch (Exception e) {
            return SvgFontGlyphs.NONE;
        }
    }

    private static String unquote(String family) {
        String trimmed = StringUtils.trimToEmpty(family);
        if (trimmed.length() >= 2 && (trimmed.startsWith("'") && trimmed.endsWith("'")
            || trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
            return trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

}
