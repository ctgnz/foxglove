package nz.co.ctg.foxglove;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public interface ISvgGraphicsAttributes extends ISvgAttributes {
    String GRAPHX_FILL = "fill";
    String GRAPHX_FILL_RULE = "fill-rule";
    String GRAPHX_STROKE = "stroke";
    String GRAPHX_STROKE_DASHARRAY = "stroke-dasharray";
    String GRAPHX_STROKE_DASHOFFSET = "stroke-dashoffset";
    String GRAPHX_STROKE_LINECAP = "stroke-linecap";
    String GRAPHX_STROKE_LINEJOIN = "stroke-linejoin";
    String GRAPHX_STROKE_MITERLIMIT = "stroke-miterlimit";
    String GRAPHX_STROKE_WIDTH = "stroke-width";
    String GRAPHX_COLOR = "color";
    String GRAPHX_COLOR_INTERPOLATION = "color-interpolation";
    String GRAPHX_COLOR_RENDERING = "color-rendering";
    String GRAPHX_OPACITY = "opacity";
    String GRAPHX_FILL_OPACITY = "fill-opacity";
    String GRAPHX_STROKE_OPACITY = "stroke-opacity";
    String GRAPHX_DISPLAY = "display";
    String GRAPHX_IMAGE_RENDERING = "image-rendering";
    String GRAPHX_POINTER_EVENTS = "pointer-events";
    String GRAPHX_SHAPE_RENDERING = "shape-rendering";
    String GRAPHX_TEXT_RENDERING = "text-rendering";
    String GRAPHX_VISIBILITY = "visibility";
    String GRAPHX_COLOR_PROFILE = "color-profile";
    String GRAPHX_STOP_COLOR = "stop-color";
    String GRAPHX_STOP_OPACITY = "stop-opacity";

    /*
     * The SVG initial values, which apply when neither the element nor any ancestor specifies the property. Several
     * differ from the JavaFX defaults - JavaFX starts a stroke SQUARE with a miter limit of 10, where SVG starts it
     * butt with a limit of 4 - so they are applied explicitly rather than left to the node.
     */
    Paint INITIAL_FILL = Color.BLACK;
    double INITIAL_STROKE_WIDTH = 1.0;
    double INITIAL_STROKE_MITER_LIMIT = 4.0;
    double INITIAL_STROKE_DASH_OFFSET = 0.0;
    StrokeLineCap INITIAL_STROKE_LINE_CAP = StrokeLineCap.BUTT;
    StrokeLineJoin INITIAL_STROKE_LINE_JOIN = StrokeLineJoin.MITER;

    default boolean isFilled() {
        // an unspecified fill is not an absent one - black is the initial value
        return getFill() != Color.TRANSPARENT;
    }

    /**
     * The fill this element specifies, or null if it specifies none. Absence matters: it is what lets the value be
     * inherited from an ancestor, so this deliberately does not substitute the initial value. Initial values are
     * applied once, at the end of resolution, in {@link #applyGraphicsProperties}.
     */
    default Paint getFill() {
        return get(GRAPHX_FILL);
    }

    default void setFill(Paint value) {
        set(GRAPHX_FILL, value);
    }

    default FillRule getFillRule() {
        return get(GRAPHX_FILL_RULE);
    }

    default void setFillRule(FillRule value) {
        set(GRAPHX_FILL_RULE, value);
    }

    default String getFillOpacity() {
        return get(GRAPHX_FILL_OPACITY);
    }

    default void setFillOpacity(String value) {
        set(GRAPHX_FILL_OPACITY, value);
    }

    default Paint getStroke() {
        return get(GRAPHX_STROKE);
    }

    default void setStroke(Paint value) {
        set(GRAPHX_STROKE, value);
    }

    default List<Double> getStrokeDashArray() {
        return get(GRAPHX_STROKE_DASHARRAY);
    }

    default void setStrokeDashArray(List<Double> value) {
        set(GRAPHX_STROKE_DASHARRAY, value);
    }

    default Double getStrokeDashOffset() {
        return get(GRAPHX_STROKE_DASHOFFSET);
    }

    default void setStrokeDashOffset(Double value) {
        set(GRAPHX_STROKE_DASHOFFSET, value);
    }

    default StrokeLineCap getStrokeLineCap() {
        return get(GRAPHX_STROKE_LINECAP);
    }

    default void setStrokeLineCap(StrokeLineCap value) {
        set(GRAPHX_STROKE_LINECAP, value);
    }

    default StrokeLineJoin getStrokeLineJoin() {
        return get(GRAPHX_STROKE_LINEJOIN);
    }

    default void setStrokeLineJoin(StrokeLineJoin value) {
        set(GRAPHX_STROKE_LINEJOIN, value);
    }

    default Double getStrokeMiterLimit() {
        return get(GRAPHX_STROKE_MITERLIMIT);
    }

    default void setStrokeMiterLimit(Double value) {
        set(GRAPHX_STROKE_MITERLIMIT, value);
    }

    default Double getStrokeWidth() {
        return get(GRAPHX_STROKE_WIDTH);
    }

    default void setStrokeWidth(Double value) {
        set(GRAPHX_STROKE_WIDTH, value);
    }

    default String getStrokeOpacity() {
        return get(GRAPHX_STROKE_OPACITY);
    }

    default void setStrokeOpacity(String value) {
        set(GRAPHX_STROKE_OPACITY, value);
    }

    default String getDisplay() {
        return get(GRAPHX_DISPLAY);
    }

    default void setDisplay(String value) {
        set(GRAPHX_DISPLAY, value);
    }

    default String getImageRendering() {
        return get(GRAPHX_IMAGE_RENDERING);
    }

    default void setImageRendering(String value) {
        set(GRAPHX_IMAGE_RENDERING, value);
    }

    default String getOpacity() {
        return get(GRAPHX_OPACITY);
    }

    default void setOpacity(String value) {
        set(GRAPHX_OPACITY, value);
    }

    default String getPointerEvents() {
        return get(GRAPHX_POINTER_EVENTS);
    }

    default void setPointerEvents(String value) {
        set(GRAPHX_POINTER_EVENTS, value);
    }

    default String getShapeRendering() {
        return get(GRAPHX_SHAPE_RENDERING);
    }

    default void setShapeRendering(String value) {
        set(GRAPHX_SHAPE_RENDERING, value);
    }

    default String getTextRendering() {
        return get(GRAPHX_TEXT_RENDERING);
    }

    default void setTextRendering(String value) {
        set(GRAPHX_TEXT_RENDERING, value);
    }

    default String getVisibility() {
        return get(GRAPHX_VISIBILITY);
    }

    default void setVisibility(String value) {
        set(GRAPHX_VISIBILITY, value);
    }

    default String getColor() {
        return get(GRAPHX_COLOR);
    }

    default void setColor(String value) {
        set(GRAPHX_COLOR, value);
    }

    default String getColorInterpolation() {
        return get(GRAPHX_COLOR_INTERPOLATION);
    }

    default void setColorInterpolation(String value) {
        set(GRAPHX_COLOR_INTERPOLATION, value);
    }

    default String getColorRendering() {
        return get(GRAPHX_COLOR_RENDERING);
    }

    default void setColorRendering(String value) {
        set(GRAPHX_COLOR_RENDERING, value);
    }

    default String getColorProfile() {
        return get(GRAPHX_COLOR_PROFILE);
    }

    default void setColorProfile(String value) {
        set(GRAPHX_COLOR_PROFILE, value);
    }

    default String getStopColor() {
        return get(GRAPHX_STOP_COLOR);
    }

    default void setStopColor(String value) {
        set(GRAPHX_STOP_COLOR, value);
    }

    default String getStopOpacity() {
        return get(GRAPHX_STOP_OPACITY);
    }

    default void setStopOpacity(String value) {
        set(GRAPHX_STOP_OPACITY, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(GRAPHX_FILL, getFill());
        builder.add(GRAPHX_FILL_RULE, getFillRule());
        builder.add(GRAPHX_STROKE, getStroke());
        builder.add(GRAPHX_STROKE_DASHARRAY, getStrokeDashArray());
        builder.add(GRAPHX_STROKE_DASHOFFSET, getStrokeDashOffset());
        builder.add(GRAPHX_STROKE_LINECAP, getStrokeLineCap());
        builder.add(GRAPHX_STROKE_LINEJOIN, getStrokeLineJoin());
        builder.add(GRAPHX_STROKE_MITERLIMIT, getStrokeMiterLimit());
        builder.add(GRAPHX_STROKE_WIDTH, getStrokeWidth());
        builder.add(GRAPHX_COLOR, getColor());
        builder.add(GRAPHX_COLOR_INTERPOLATION, getColorInterpolation());
        builder.add(GRAPHX_COLOR_RENDERING, getColorRendering());
        builder.add(GRAPHX_OPACITY, getOpacity());
        builder.add(GRAPHX_FILL_OPACITY, getFillOpacity());
        builder.add(GRAPHX_STROKE_OPACITY, getStrokeOpacity());
        builder.add(GRAPHX_DISPLAY, getDisplay());
        builder.add(GRAPHX_IMAGE_RENDERING, getImageRendering());
        builder.add(GRAPHX_POINTER_EVENTS, getPointerEvents());
        builder.add(GRAPHX_SHAPE_RENDERING, getShapeRendering());
        builder.add(GRAPHX_TEXT_RENDERING, getTextRendering());
        builder.add(GRAPHX_VISIBILITY, getVisibility());
        builder.add(GRAPHX_COLOR_PROFILE, getColorProfile());
        builder.add(GRAPHX_STOP_COLOR, getStopColor());
        builder.add(GRAPHX_STOP_OPACITY, getStopOpacity());
    }

    /**
     * Applies the resolved paint and stroke properties to a shape, along with the properties that apply to any node.
     *
     * @param parent the style inherited from the ancestors, already resolved - see {@link SvgInheritedStyle}
     */
    default void applyGraphicsProperties(ISvgStylable parent, Shape shape) {
        ISvgStylable style = SvgInheritedStyle.resolve(parent, this);

        shape.setFill(withOpacity(defaultIfNull(style.getFill(), INITIAL_FILL), style.getFillOpacity()));
        shape.setStroke(withOpacity(style.getStroke(), style.getStrokeOpacity()));
        shape.setStrokeWidth(defaultIfNull(style.getStrokeWidth(), INITIAL_STROKE_WIDTH));
        shape.setStrokeMiterLimit(defaultIfNull(style.getStrokeMiterLimit(), INITIAL_STROKE_MITER_LIMIT));
        shape.setStrokeDashOffset(defaultIfNull(style.getStrokeDashOffset(), INITIAL_STROKE_DASH_OFFSET));
        shape.setStrokeLineCap(defaultIfNull(style.getStrokeLineCap(), INITIAL_STROKE_LINE_CAP));
        shape.setStrokeLineJoin(defaultIfNull(style.getStrokeLineJoin(), INITIAL_STROKE_LINE_JOIN));
        if (style.getStrokeDashArray() != null) {
            shape.getStrokeDashArray().addAll(style.getStrokeDashArray());
        }
        applyFillRule(style.getFillRule(), shape);
        applyOpacity(shape);
        applyVisibility(style, shape);
    }

    /**
     * Applies the properties that apply to any node rather than to a shape's geometry, for elements such as
     * {@code <g>} that render to a container rather than to a {@link Shape}.
     * <p>
     * Deliberately does not apply {@code visibility}. A hidden element still takes part in rendering, and a
     * descendant may set itself visible again - but an invisible JavaFX parent hides its children unconditionally,
     * so hiding the group node would make that override impossible. Instead {@code visibility} travels down as an
     * inherited property and each leaf decides for itself.
     */
    default void applyNodeProperties(ISvgStylable parent, Node node) {
        applyOpacity(node);
    }

    /**
     * {@code opacity} is not inherited - it applies to the element that declares it, and on a group it composites
     * the whole subtree, which is what SVG group opacity means and what JavaFX already does for a {@code Group}.
     */
    private void applyOpacity(Node node) {
        Double opacity = parseOpacity(getOpacity());
        if (opacity != null) {
            node.setOpacity(opacity);
        }
    }

    /**
     * {@code visibility} is inherited, so it comes from the resolved style rather than from this element alone.
     * Unlike {@code display}, a hidden element keeps its place in the scene graph.
     */
    private static void applyVisibility(ISvgStylable style, Node node) {
        String visibility = style.getVisibility();
        if (visibility != null) {
            node.setVisible(!"hidden".equalsIgnoreCase(visibility) && !"collapse".equalsIgnoreCase(visibility));
        }
    }

    /**
     * JavaFX exposes a fill rule on {@code Path} and {@code SVGPath} only, so {@code fill-rule} cannot be honoured
     * on a {@code <polygon>} even though SVG defines it there.
     */
    private static void applyFillRule(FillRule fillRule, Shape shape) {
        if (fillRule == null) {
            return;
        }
        if (shape instanceof SVGPath svgPath) {
            svgPath.setFillRule(fillRule);
        } else if (shape instanceof Path path) {
            path.setFillRule(fillRule);
        }
    }

    /**
     * Folds {@code fill-opacity} or {@code stroke-opacity} into the paint, which is where JavaFX carries alpha. The
     * value multiplies any alpha the colour already has, per the specification. A gradient or pattern is returned
     * unchanged, as only a {@code Color} exposes a derivable opacity.
     */
    private static Paint withOpacity(Paint paint, String opacity) {
        Double alpha = parseOpacity(opacity);
        if (alpha != null && paint instanceof Color color) {
            return color.deriveColor(0, 1, 1, alpha);
        }
        return paint;
    }

    /**
     * Parses an opacity, accepting a number or a percentage, and clamping to the range the specification allows.
     */
    static Double parseOpacity(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String text = value.trim();
        try {
            boolean percentage = text.endsWith("%");
            double parsed = Double.parseDouble(percentage ? text.substring(0, text.length() - 1) : text);
            return Math.clamp(percentage ? parsed / 100.0 : parsed, 0.0, 1.0);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
