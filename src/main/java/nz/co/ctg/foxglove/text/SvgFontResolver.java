package nz.co.ctg.foxglove.text;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;

/**
 * Finds the SVG font a {@code font-family} names, if the document defines one (#61).
 * <p>
 * An SVG font is declared by a {@code <font-face>} whose {@code font-family} matches, pointing through
 * {@code <font-face-src><font-face-uri>} at a document holding the actual {@code <font>} and its glyph outlines.
 * That indirection is the common case by a wide margin: every one of the 525 documents in the W3C suite labels
 * itself this way, against 37 that declare a {@code <font>} inline - which is why the external path is what this
 * implements first.
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
        if (faces.isEmpty()) {
            return null;
        }
        for (String family : fontFamily.split(",")) {
            String wanted = unquote(family);
            if (wanted.isEmpty()) {
                continue;
            }
            for (SvgFontFace face : faces) {
                if (!wanted.equalsIgnoreCase(unquote(StringUtils.trimToEmpty(face.getFontFamily())))) {
                    continue;
                }
                SvgFontGlyphs glyphs = load(face, context);
                if (glyphs != null && glyphs.size() > 0) {
                    return glyphs;
                }
            }
        }
        return null;
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
            // "#id" alone means a <font> in this very document, which is the inline case this does not yet cover
            return null;
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
