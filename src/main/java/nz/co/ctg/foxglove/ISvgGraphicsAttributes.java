package nz.co.ctg.foxglove;

import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import javafx.scene.paint.Paint;
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

    default Paint getFill() {
        return get(GRAPHX_FILL);
    }

    default void setFill(Paint value) {
        set(GRAPHX_FILL, value);
    }

    default String getFillRule() {
        return get(GRAPHX_FILL_RULE);
    }

    default void setFillRule(String value) {
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
        if (!getProperties().containsKey(GRAPHX_STROKE_DASHOFFSET)) {
            return 0.0;
        }
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
        if (!getProperties().containsKey(GRAPHX_STROKE_MITERLIMIT)) {
            return 0.0;
        }
        return get(GRAPHX_STROKE_MITERLIMIT);
    }

    default void setStrokeMiterLimit(Double value) {
        set(GRAPHX_STROKE_MITERLIMIT, value);
    }

    default Double getStrokeWidth() {
        if (!getProperties().containsKey(GRAPHX_STROKE_WIDTH)) {
            return 0.0;
        }
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

    default void applyGraphicsProperties(Shape shape) {
        shape.setFill(getFill());
        shape.setStroke(getStroke());
        shape.setStrokeWidth(getStrokeWidth());
        shape.setStrokeMiterLimit(getStrokeMiterLimit());
        shape.setStrokeLineCap(getStrokeLineCap());
        shape.setStrokeLineJoin(getStrokeLineJoin());
        shape.setStrokeDashOffset(getStrokeDashOffset());
        if (getStrokeDashArray() != null) {
            shape.getStrokeDashArray().addAll(getStrokeDashArray());
        }
    }

    default void parseGraphicsStyle(String style) {
    }

}
