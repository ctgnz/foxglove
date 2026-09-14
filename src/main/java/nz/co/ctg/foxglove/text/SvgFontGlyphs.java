package nz.co.ctg.foxglove.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.geometry.SvgPathData;

import javafx.scene.Node;
import javafx.scene.shape.Path;
import javafx.scene.transform.Scale;

/**
 * A loaded SVG font, in the form text layout actually needs it: a glyph per character, and how far each one
 * advances the cursor (#61).
 * <p>
 * SVG fonts define their glyphs as path outlines inline in a document rather than in a font file, so rendering one
 * means drawing each character as its own {@link Path} rather than handing a string to the text system. Two things
 * about that coordinate system matter and are easy to get wrong:
 * <ul>
 * <li>Outlines are expressed in <b>font units</b>, {@code units-per-em} of them to an em - 1000 here, so a glyph is
 * roughly 20x larger than its rendered size and has to be scaled by {@code fontSize / unitsPerEm}.
 * <li>They are <b>y-up</b>, where JavaFX is y-down, so the scale is negative vertically. Miss that and every glyph
 * renders upside down about its own baseline, which for a symmetric glyph looks perfectly fine.
 * </ul>
 * <b>Advances come from the font, not from measuring what was drawn.</b> Each glyph declares its own
 * {@code horiz-adv-x}, which is the whole point of a font's metrics: it is not the outline's width, and for a space
 * there is no outline at all. Measuring rendered bounds instead would silently collapse spaces and mis-space
 * everything that kerns or overhangs.
 */
public final class SvgFontGlyphs {

    /** The specification's default when {@code <font-face>} declares no {@code units-per-em}. */
    private static final double DEFAULT_UNITS_PER_EM = 1000;

    private final Map<String, SvgGlyph> glyphsByUnicode;
    private final SvgMissingGlyph missingGlyph;
    private final double unitsPerEm;
    private final double defaultAdvance;

    private SvgFontGlyphs(Map<String, SvgGlyph> glyphsByUnicode, SvgMissingGlyph missingGlyph, double unitsPerEm, double defaultAdvance) {
        this.glyphsByUnicode = glyphsByUnicode;
        this.missingGlyph = missingGlyph;
        this.unitsPerEm = unitsPerEm;
        this.defaultAdvance = defaultAdvance;
    }

    /** Indexes {@code font}'s glyphs by the character each one renders, taking metrics from its {@code <font-face>}. */
    static SvgFontGlyphs of(SvgFont font) {
        Map<String, SvgGlyph> glyphs = new HashMap<>();
        SvgMissingGlyph missing = null;
        SvgFontFace face = null;
        for (ISvgElement child : font.getContent()) {
            if (child instanceof SvgGlyph glyph) {
                String unicode = glyph.getUnicode();
                // a glyph with no unicode is reachable only by name, which is <altGlyph>'s business and out of scope
                if (StringUtils.isNotEmpty(unicode)) {
                    glyphs.putIfAbsent(unicode, glyph);
                }
            } else if (child instanceof SvgMissingGlyph candidate) {
                missing = candidate;
            } else if (child instanceof SvgFontFace candidate) {
                face = candidate;
            }
        }
        double unitsPerEm = face == null ? DEFAULT_UNITS_PER_EM : number(face.getUnitsPerEm(), DEFAULT_UNITS_PER_EM);
        return new SvgFontGlyphs(glyphs, missing, unitsPerEm, number(font.getHorizAdvX(), unitsPerEm));
    }

    /** Whether this font can draw {@code character} at all, ignoring the {@code <missing-glyph>} fallback. */
    public boolean hasGlyph(String character) {
        return glyphsByUnicode.containsKey(character);
    }

    /**
     * {@code character} as a renderable node at {@code fontSize}, or null when it has no outline - a space has a
     * real advance and nothing to draw, and returning an empty {@link Path} for it would add a pointless node per
     * space to every document.
     */
    public Node glyphFor(String character, double fontSize) {
        String outline = outlineOf(character);
        if (StringUtils.isBlank(outline)) {
            return null;
        }
        Path path = SvgPathData.toJavaFxPath(outline);
        double scale = fontSize / unitsPerEm;
        // negative vertically: font outlines are y-up, JavaFX is y-down
        path.getTransforms().add(new Scale(scale, -scale));
        return path;
    }

    /**
     * How far {@code character} advances the cursor at {@code fontSize} - the glyph's own {@code horiz-adv-x}, the
     * font's default if it declares none, scaled out of font units.
     */
    public double advanceFor(String character, double fontSize) {
        SvgGlyph glyph = glyphsByUnicode.get(character);
        double advance = glyph != null ? number(glyph.getHorizAdvX(), defaultAdvance)
            : missingGlyph != null ? number(missingGlyph.getHorizAdvX(), defaultAdvance)
            : defaultAdvance;
        return advance * fontSize / unitsPerEm;
    }

    public double getUnitsPerEm() {
        return unitsPerEm;
    }

    /** How many glyphs this font defines - mostly so a test can prove a font was loaded rather than defaulted. */
    public int size() {
        return glyphsByUnicode.size();
    }

    private String outlineOf(String character) {
        SvgGlyph glyph = glyphsByUnicode.get(character);
        if (glyph != null) {
            return glyph.getD();
        }
        return missingGlyph == null ? null : missingGlyph.getD();
    }

    private static double number(String value, double fallback) {
        String text = StringUtils.trimToEmpty(value);
        return NumberUtils.isParsable(text) ? NumberUtils.toDouble(text) : fallback;
    }

    /** For {@link SvgFontResolver}'s cache to record "this URI holds no usable font" without re-loading it. */
    static final SvgFontGlyphs NONE = new SvgFontGlyphs(Map.of(), null, DEFAULT_UNITS_PER_EM, DEFAULT_UNITS_PER_EM);

    static SvgFontGlyphs firstFontIn(List<SvgFont> fonts, String fragmentId) {
        for (SvgFont font : fonts) {
            if (StringUtils.isBlank(fragmentId) || fragmentId.equals(font.getId())) {
                return of(font);
            }
        }
        return fonts.isEmpty() ? NONE : of(fonts.get(0));
    }

}
