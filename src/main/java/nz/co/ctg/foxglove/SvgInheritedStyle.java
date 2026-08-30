package nz.co.ctg.foxglove;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * The presentation attribute values in force at a point in the document, resolved down the ancestor chain.
 * <p>
 * SVG property inheritance walks the whole chain of ancestors, but rendering only ever passes an element its
 * immediate parent. Rather than give every element a back reference to its parent, each container resolves its own
 * values against what it inherited and hands the result to its children, so the accumulated style descends with the
 * traversal and a child only ever needs to consult one object.
 * <p>
 * This implements {@link ISvgStylable} so it can stand in for the parent element wherever one is expected. Only the
 * properties SVG defines as inheritable are carried; {@code opacity}, {@code display}, {@code clip-path},
 * {@code mask} and {@code filter} apply to the element that declares them and are deliberately absent.
 */
public final class SvgInheritedStyle implements ISvgStylable {

    /**
     * The presentation attributes SVG 1.1 defines as inherited.
     */
    private static final Set<String> INHERITED = ImmutableSet.of(
        // graphics
        GRAPHX_FILL, GRAPHX_FILL_RULE, GRAPHX_FILL_OPACITY,
        GRAPHX_STROKE, GRAPHX_STROKE_DASHARRAY, GRAPHX_STROKE_DASHOFFSET, GRAPHX_STROKE_LINECAP,
        GRAPHX_STROKE_LINEJOIN, GRAPHX_STROKE_MITERLIMIT, GRAPHX_STROKE_WIDTH, GRAPHX_STROKE_OPACITY,
        GRAPHX_COLOR, GRAPHX_COLOR_INTERPOLATION, GRAPHX_COLOR_RENDERING, GRAPHX_COLOR_PROFILE,
        GRAPHX_IMAGE_RENDERING, GRAPHX_SHAPE_RENDERING, GRAPHX_TEXT_RENDERING,
        GRAPHX_POINTER_EVENTS, GRAPHX_VISIBILITY,
        // presentation
        PRES_CLIP_RULE, PRES_COLOR_INTERPOLATION_FILTERS, PRES_CURSOR,
        PRES_MARKER_START, PRES_MARKER_MID, PRES_MARKER_END,
        // text
        TEXT_WRITING_MODE, TEXT_DIRECTION, TEXT_LETTER_SPACING, TEXT_WORD_SPACING, TEXT_KERNING, TEXT_ANCHOR,
        TEXT_GLYPH_ORIENTATION_HORIZONTAL, TEXT_GLYPH_ORIENTATION_VERTICAL,
        TEXT_FONT_FAMILY, TEXT_FONT_SIZE, TEXT_FONT_SIZE_ADJUST, TEXT_FONT_STRETCH, TEXT_FONT_STYLE,
        TEXT_FONT_VARIANT, TEXT_FONT_WEIGHT);

    private static final SvgInheritedStyle ROOT = new SvgInheritedStyle(Collections.emptyMap());

    /**
     * The style in force outside any element, where nothing has been specified and every property falls back to its
     * initial value.
     */
    public static SvgInheritedStyle root() {
        return ROOT;
    }

    /**
     * The style that the children of {@code element} inherit: what {@code element} itself inherited, overlaid with
     * the inheritable properties it specifies. A null parent is treated as the root.
     * <p>
     * Both arguments are {@link ISvgAttributes} rather than {@link ISvgStylable} because resolution only reads
     * properties, and the callers include the attribute interfaces themselves, which sit above {@code ISvgStylable}.
     */
    public static SvgInheritedStyle resolve(ISvgAttributes parent, ISvgAttributes element) {
        Map<String, Object> resolved = new LinkedHashMap<>();
        if (parent != null) {
            for (String property : INHERITED) {
                Object value = parent.get(property);
                if (value != null) {
                    resolved.put(property, value);
                }
            }
        }
        if (element != null) {
            for (String property : INHERITED) {
                Object value = element.get(property);
                if (value != null) {
                    resolved.put(property, value);
                }
            }
        }
        return new SvgInheritedStyle(resolved);
    }

    private final Map<String, Object> properties;

    private SvgInheritedStyle(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String property) {
        return (T) properties.get(property);
    }

    /**
     * Unsupported: a resolved style is a snapshot taken during rendering, not somewhere to record new values.
     */
    @Override
    public void set(String property, Object value) {
        throw new UnsupportedOperationException("a resolved style is immutable");
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public String toString() {
        return "inheritedStyle" + properties;
    }

}
