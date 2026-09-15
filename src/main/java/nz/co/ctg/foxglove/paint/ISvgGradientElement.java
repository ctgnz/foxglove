package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.SvgElementIndex;

/**
 * A paint server that produces a gradient.
 * <p>
 * The coordinate attributes are not resolved here beyond a number or a percentage. Under the default {@code objectBoundingBox} units JavaFX resolves proportional coordinates
 * against the shape itself, so the bounding box is not needed; under {@code userSpaceOnUse} a percentage would have to resolve against the viewport, which needs the rendering
 * context from #13 and is not supported yet.
 */
public interface ISvgGradientElement extends ISvgElement, ISvgLinkable {

    String USER_SPACE_ON_USE = "userSpaceOnUse";

    List<ISvgElement> getContent();

    /**
     * Forwards to {@link ISvgLinkable#toStringDetail} - a concrete gradient class calls this via {@code ISvgGradientElement.super.toStringDetail(builder)} rather than
     * {@code ISvgLinkable.super...} directly, since extending {@code ISvgLinkable} here makes a class's own {@code implements ISvgLinkable} redundant, and javac rejects a super
     * call naming an interface that is only reachable transitively.
     */
    default void toStringDetail(ToStringHelper builder) {
        ISvgLinkable.super.toStringDetail(builder);
    }

    String getGradientUnits();

    String getSpreadMethod();

    /**
     * This element's own {@code gradientTransform}, unresolved - see {@link #getEffectiveGradientTransform}.
     * <p>
     * A JavaFX gradient carries no transform, so honouring one means folding it into the coordinates the gradient is built from. That is exact for some transforms and impossible
     * for others; {@link GradientTransform} draws the line and says why. Until #52 none of it was applied at all, on the reasoning that a subset which silently produced the wrong
     * picture elsewhere was worse than nothing - what changed is that the unsupported cases now say so rather than failing quietly.
     */
    String getGradientTransform();

    /**
     * As {@link #getGradientTransform()}, but resolving via the {@code xlink:href} chain - {@code gradientTransform} is inherited across a reference like {@code gradientUnits} and
     * {@code spreadMethod}, including across a linear/radial boundary, since it describes the gradient's coordinate system rather than its geometry.
     * <p>
     * This javadoc used to record the opposite, that it was deliberately not inherited because "there would be nothing for the effective value to do". That was true only while the
     * attribute was ignored.
     */
    default String getEffectiveGradientTransform(SvgElementIndex index) {
        return effectiveCommon(index, ISvgGradientElement::getGradientTransform);
    }

    /**
     * Builds the JavaFX paint for this gradient, resolving any attribute the element itself does not specify - and, absent stops of its own, the stops themselves - via its
     * {@code xlink:href} chain (#18). Returns null when there are no stops anywhere in the chain, so it paints nothing.
     *
     * @param index
     *            the document's element index, used to resolve the {@code xlink:href} chain, or null to resolve only this element's own attributes
     */
    Paint createPaint(SvgElementIndex index);

    /**
     * The {@code xlink:href} chain starting at this element, in reference order - unlike the concrete gradient classes' own chains, this follows through a change of gradient type,
     * since {@code gradientUnits}, {@code spreadMethod} and stops are inherited across a linear/radial boundary even though geometry is not. A null index resolves to a chain of
     * just this element, so a caller that does not have one still gets its own declared values.
     */
    default List<ISvgGradientElement> resolveHrefChain(SvgElementIndex index) {
        return index == null ? List.of(this) : index.resolveChain(this, ISvgGradientElement::getXlinkHref, ISvgGradientElement.class);
    }

    /**
     * The first non-empty set of stops in the {@code xlink:href} chain - the referencing element's own stops if it has any, per the specification, otherwise the first ancestor's.
     */
    default List<Stop> getEffectiveGradientStops(SvgElementIndex index) {
        for (ISvgGradientElement current : resolveHrefChain(index)) {
            List<Stop> stops = current.getGradientStops();
            if (!stops.isEmpty()) {
                return stops;
            }
        }
        return List.of();
    }

    /**
     * Whether coordinates are fractions of the target's bounding box, which is the SVG default and what JavaFX calls proportional, rather than absolute user units - considering
     * only this element's own {@code gradientUnits}.
     */
    default boolean isProportional() {
        return parseProportional(getGradientUnits());
    }

    /**
     * As {@link #isProportional()}, but resolving {@code gradientUnits} via the {@code xlink:href} chain first - this is one of the attributes common to both gradient types, so it
     * is inherited even across a linear/radial boundary.
     */
    default boolean isEffectivelyProportional(SvgElementIndex index) {
        return parseProportional(effectiveCommon(index, ISvgGradientElement::getGradientUnits));
    }

    private static boolean parseProportional(String gradientUnits) {
        return !USER_SPACE_ON_USE.equalsIgnoreCase(StringUtils.trimToEmpty(gradientUnits));
    }

    /**
     * {@code spreadMethod} maps directly onto the JavaFX cycle method. The initial value is {@code pad}. Considers only this element's own {@code spreadMethod}.
     */
    default CycleMethod getCycleMethod() {
        return parseCycleMethod(getSpreadMethod());
    }

    /**
     * As {@link #getCycleMethod()}, but resolving {@code spreadMethod} via the {@code xlink:href} chain first - the other attribute common to both gradient types.
     */
    default CycleMethod getEffectiveCycleMethod(SvgElementIndex index) {
        return parseCycleMethod(effectiveCommon(index, ISvgGradientElement::getSpreadMethod));
    }

    private static CycleMethod parseCycleMethod(String spreadMethod) {
        String spread = StringUtils.trimToEmpty(spreadMethod);
        if ("reflect".equalsIgnoreCase(spread)) {
            return CycleMethod.REFLECT;
        }
        if ("repeat".equalsIgnoreCase(spread)) {
            return CycleMethod.REPEAT;
        }
        return CycleMethod.NO_CYCLE;
    }

    /**
     * The first non-null value of an attribute common to both gradient types, walking the (cross-type) {@code xlink:href} chain.
     */
    private <T> T effectiveCommon(SvgElementIndex index, Function<ISvgGradientElement, T> getter) {
        for (ISvgGradientElement current : resolveHrefChain(index)) {
            T value = getter.apply(current);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * The stops in document order.
     * <p>
     * Offsets are clamped to the unit interval and forced to be non-decreasing, as the specification requires: a stop whose offset is smaller than the one before takes the earlier
     * value. {@code stop-color} and {@code stop-opacity} are combined into the stop colour, since JavaFX carries alpha on the colour.
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
     * A gradient with no stops paints nothing, and one with a single stop paints that colour flat. Both are cases JavaFX will not build a gradient for, so they are handled before
     * one is constructed.
     */
    default Paint getDegeneratePaint(List<Stop> stops) {
        return stops.isEmpty() ? null : stops.get(0)
            .getColor();
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
     * Parses a coordinate or ratio, accepting a bare number or a percentage. Returns null when absent or malformed, so the caller can apply the attribute's own initial value.
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
