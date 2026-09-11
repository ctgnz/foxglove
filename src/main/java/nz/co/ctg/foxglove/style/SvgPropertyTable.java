package nz.co.ctg.foxglove.style;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import nz.co.ctg.foxglove.ISvgAttributes;
import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.adapter.DoubleListAdapter;
import nz.co.ctg.foxglove.adapter.FontPostureAdapter;
import nz.co.ctg.foxglove.adapter.FontWeightAdapter;
import nz.co.ctg.foxglove.adapter.StrokeLineCapAdapter;
import nz.co.ctg.foxglove.adapter.StrokeLineJoinAdapter;
import nz.co.ctg.foxglove.adapter.SvgPaintAdapter;

import static nz.co.ctg.foxglove.ISvgTextAttributes.TEXT_FONT_STYLE;
import static nz.co.ctg.foxglove.ISvgTextAttributes.TEXT_FONT_WEIGHT;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.scene.shape.FillRule;

/**
 * Converts a raw CSS-syntax value into the typed object a property's setter expects, the same way regardless of
 * whether it came from a stylesheet rule or the inline {@code style} attribute - the "table-driven property setter"
 * that replaced {@code AbstractSvgStylable}'s old twelve-case switch.
 * <p>
 * A property with no entry here is stored as the trimmed raw string, which covers every plain-string property in
 * {@code ISvgGraphicsAttributes}, {@code ISvgPresentationAttributes} and {@code ISvgTextAttributes} without needing
 * one entry per property - only the properties whose setter expects something other than a {@link String} need a
 * conversion registered.
 */
public final class SvgPropertyTable {

    private static final SvgPaintAdapter PAINT = new SvgPaintAdapter();
    private static final StrokeLineCapAdapter STROKE_LINE_CAP = new StrokeLineCapAdapter();
    private static final StrokeLineJoinAdapter STROKE_LINE_JOIN = new StrokeLineJoinAdapter();
    private static final DoubleListAdapter DOUBLE_LIST = new DoubleListAdapter();
    private static final FontWeightAdapter FONT_WEIGHT = new FontWeightAdapter();
    private static final FontPostureAdapter FONT_POSTURE = new FontPostureAdapter();

    private static final Function<String, Object> IDENTITY = String::trim;

    private static final Map<String, Function<String, Object>> CONVERTERS = ImmutableMap.<String, Function<String, Object>>builder()
        .put(ISvgGraphicsAttributes.GRAPHX_FILL, unmarshal(PAINT))
        .put(ISvgGraphicsAttributes.GRAPHX_STROKE, unmarshal(PAINT))
        .put(ISvgGraphicsAttributes.GRAPHX_FILL_RULE, SvgPropertyTable::parseFillRule)
        .put(ISvgGraphicsAttributes.GRAPHX_STROKE_DASHARRAY, unmarshal(DOUBLE_LIST))
        .put(ISvgGraphicsAttributes.GRAPHX_STROKE_DASHOFFSET, Double::valueOf)
        .put(ISvgGraphicsAttributes.GRAPHX_STROKE_MITERLIMIT, Double::valueOf)
        .put(ISvgGraphicsAttributes.GRAPHX_STROKE_WIDTH, Double::valueOf)
        .put(ISvgGraphicsAttributes.GRAPHX_STROKE_LINECAP, unmarshal(STROKE_LINE_CAP))
        .put(ISvgGraphicsAttributes.GRAPHX_STROKE_LINEJOIN, unmarshal(STROKE_LINE_JOIN))
        .put(TEXT_FONT_WEIGHT, unmarshal(FONT_WEIGHT))
        .put(TEXT_FONT_STYLE, unmarshal(FONT_POSTURE))
        .build();

    /**
     * Applies one raw declaration value to {@code target}: converts it via the table (or stores it as a trimmed
     * string if the property has no entry), silently doing nothing if the value fails to convert - an invalid
     * declaration is dropped, the same as CSS ignores one, rather than forced in as null.
     */
    public static void apply(ISvgAttributes target, String property, String rawValue) {
        try {
            Object value = CONVERTERS.getOrDefault(property, IDENTITY).apply(rawValue);
            if (value != null) {
                target.set(property, value);
            }
        } catch (Exception e) {
            // dropped - see above
        }
    }

    private static FillRule parseFillRule(String value) {
        return "evenodd".equalsIgnoreCase(value.trim()) ? FillRule.EVEN_ODD : FillRule.NON_ZERO;
    }

    private static <T> Function<String, Object> unmarshal(XmlAdapter<String, T> adapter) {
        return value -> {
            try {
                return adapter.unmarshal(value);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        };
    }

    private SvgPropertyTable() {
    }

}
