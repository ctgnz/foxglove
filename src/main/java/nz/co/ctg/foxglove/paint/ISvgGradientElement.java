package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.ISvgElement;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

/**
 * A paint server that produces a gradient.
 * <p>
 * The coordinate attributes are not resolved here beyond a number or a percentage. Under the default
 * {@code objectBoundingBox} units JavaFX resolves proportional coordinates against the shape itself, so the bounding
 * box is not needed; under {@code userSpaceOnUse} a percentage would have to resolve against the viewport, which
 * needs the rendering context from #13 and is not supported yet.
 */
public interface ISvgGradientElement extends ISvgElement {

    String USER_SPACE_ON_USE = "userSpaceOnUse";

    List<ISvgElement> getContent();

    String getGradientUnits();

    String getSpreadMethod();

    /**
     * Parsed, but not applied.
     * <p>
     * A JavaFX gradient carries no transform, so there is nowhere to put one. A translate or a scale along the
     * gradient axis could be baked into the computed coordinates, but a rotation or a skew changes the shape of the
     * gradient itself and cannot be represented at all. Rather than support a subset that silently produces the
     * wrong picture for everything else, this is left unimplemented and recorded as its own issue.
     */
    String getGradientTransform();

    /**
     * Builds the JavaFX paint for this gradient, or null when it has no stops and so paints nothing.
     */
    Paint createPaint();

    /**
     * Whether coordinates are fractions of the target's bounding box, which is the SVG default and what JavaFX calls
     * proportional, rather than absolute user units.
     */
    default boolean isProportional() {
        return !USER_SPACE_ON_USE.equalsIgnoreCase(StringUtils.trimToEmpty(getGradientUnits()));
    }

    /**
     * {@code spreadMethod} maps directly onto the JavaFX cycle method. The initial value is {@code pad}.
     */
    default CycleMethod getCycleMethod() {
        String spread = StringUtils.trimToEmpty(getSpreadMethod());
        if ("reflect".equalsIgnoreCase(spread)) {
            return CycleMethod.REFLECT;
        }
        if ("repeat".equalsIgnoreCase(spread)) {
            return CycleMethod.REPEAT;
        }
        return CycleMethod.NO_CYCLE;
    }

    /**
     * The stops in document order.
     * <p>
     * Offsets are clamped to the unit interval and forced to be non-decreasing, as the specification requires: a stop
     * whose offset is smaller than the one before takes the earlier value. {@code stop-color} and
     * {@code stop-opacity} are combined into the stop colour, since JavaFX carries alpha on the colour.
     */
    default List<Stop> getGradientStops() {
        List<Stop> stops = new ArrayList<>();
        double previous = 0.0;
        for (ISvgElement child : getContent()) {
            if (child instanceof SvgStop stop) {
                double offset = Math.max(previous, parseOffset(stop.getOffset()));
                previous = offset;
                stops.add(new Stop(offset, stopColour(stop)));
            }
        }
        return stops;
    }

    /**
     * A gradient with no stops paints nothing, and one with a single stop paints that colour flat. Both are cases
     * JavaFX will not build a gradient for, so they are handled before one is constructed.
     */
    default Paint getDegeneratePaint(List<Stop> stops) {
        return stops.isEmpty() ? null : stops.get(0).getColor();
    }

    private static Color stopColour(SvgStop stop) {
        Color colour = Color.BLACK;
        String stopColor = stop.getStopColor();
        if (StringUtils.isNotBlank(stopColor)) {
            try {
                colour = Color.web(stopColor.trim());
            } catch (RuntimeException e) {
                colour = Color.BLACK;
            }
        }
        Double opacity = parseNumberOrPercentage(stop.getStopOpacity());
        return opacity == null ? colour : colour.deriveColor(0, 1, 1, Math.clamp(opacity, 0.0, 1.0));
    }

    private static double parseOffset(String value) {
        Double offset = parseNumberOrPercentage(value);
        return offset == null ? 0.0 : Math.clamp(offset, 0.0, 1.0);
    }

    /**
     * Parses a coordinate or ratio, accepting a bare number or a percentage. Returns null when absent or malformed,
     * so the caller can apply the attribute's own initial value.
     */
    static Double parseNumberOrPercentage(String value) {
        String text = StringUtils.trimToEmpty(value);
        if (text.isEmpty()) {
            return null;
        }
        try {
            boolean percentage = text.endsWith("%");
            double parsed = Double.parseDouble(percentage ? text.substring(0, text.length() - 1) : text);
            return percentage ? parsed / 100.0 : parsed;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static double coordinate(String value, double initial) {
        Double parsed = parseNumberOrPercentage(value);
        return parsed == null ? initial : parsed;
    }

}
