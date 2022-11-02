package nz.co.ctg.foxglove;

import java.util.List;

import nz.co.ctg.foxglove.attributes.SvgGraphicsStyleAttributes;

import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public interface ISvgGraphicsAttributes {

    default Paint getFill() {
        return getGraphicsAttributes().getFill();
    }

    default void setFill(Paint value) {
        getGraphicsAttributes().setFill(value);
    }

    default String getFillRule() {
        return getGraphicsAttributes().getFillRule();
    }

    default void setFillRule(String value) {
        getGraphicsAttributes().setFillRule(value);
    }

    default String getFillOpacity() {
        return getGraphicsAttributes().getFillOpacity();
    }

    default void setFillOpacity(String value) {
        getGraphicsAttributes().setFillRule(value);
    }

    default Paint getStroke() {
        return getGraphicsAttributes().getStroke();
    }

    default void setStroke(Paint value) {
        getGraphicsAttributes().setStroke(value);
    }

    default List<Double> getStrokeDashArray() {
        return getGraphicsAttributes().getStrokeDashArray();
    }

    default void setStrokeDashArray(List<Double> value) {
        getGraphicsAttributes().setStrokeDashArray(value);
    }

    default double getStrokeDashOffset() {
        return getGraphicsAttributes().getStrokeDashOffset();
    }

    default void setStrokeDashOffset(double value) {
        getGraphicsAttributes().setStrokeDashOffset(value);
    }

    default StrokeLineCap getStrokeLineCap() {
        return getGraphicsAttributes().getStrokeLineCap();
    }

    default void setStrokeLineCap(StrokeLineCap value) {
        getGraphicsAttributes().setStrokeLineCap(value);
    }

    default StrokeLineJoin getStrokeLineJoin() {
        return getGraphicsAttributes().getStrokeLineJoin();
    }

    default void setStrokeLineJoin(StrokeLineJoin value) {
        getGraphicsAttributes().setStrokeLineJoin(value);
    }

    default double getStrokeMiterLimit() {
        return getGraphicsAttributes().getStrokeMiterLimit();
    }

    default void setStrokeMiterLimit(double value) {
        getGraphicsAttributes().setStrokeMiterLimit(value);
    }

    default double getStrokeWidth() {
        return getGraphicsAttributes().getStrokeWidth();
    }

    default void setStrokeWidth(double value) {
        getGraphicsAttributes().setStrokeWidth(value);
    }

    default String getStrokeOpacity() {
        return getGraphicsAttributes().getStrokeOpacity();
    }

    default void setStrokeOpacity(String value) {
        getGraphicsAttributes().setStrokeOpacity(value);
    }

    default String getDisplay() {
        return getGraphicsAttributes().getDisplay();
    }

    default void setDisplay(String value) {
        getGraphicsAttributes().setDisplay(value);
    }

    default String getImageRendering() {
        return getGraphicsAttributes().getImageRendering();
    }

    default void setImageRendering(String value) {
        getGraphicsAttributes().setImageRendering(value);
    }

    default String getOpacity() {
        return getGraphicsAttributes().getOpacity();
    }

    default void setOpacity(String value) {
        getGraphicsAttributes().setOpacity(value);
    }

    default String getPointerEvents() {
        return getGraphicsAttributes().getPointerEvents();
    }

    default void setPointerEvents(String value) {
        getGraphicsAttributes().setPointerEvents(value);
    }

    default String getShapeRendering() {
        return getGraphicsAttributes().getShapeRendering();
    }

    default void setShapeRendering(String value) {
        getGraphicsAttributes().setShapeRendering(value);
    }

    default String getTextRendering() {
        return getGraphicsAttributes().getTextRendering();
    }

    default void setTextRendering(String value) {
        getGraphicsAttributes().setTextRendering(value);
    }

    default String getVisibility() {
        return getGraphicsAttributes().getVisibility();
    }

    default void setVisibility(String value) {
        getGraphicsAttributes().setVisibility(value);
    }

    default String getColor() {
        return getGraphicsAttributes().getColor();
    }

    default void setColor(String value) {
        getGraphicsAttributes().setColor(value);
    }

    default String getColorInterpolation() {
        return getGraphicsAttributes().getColorInterpolation();
    }

    default void setColorInterpolation(String value) {
        getGraphicsAttributes().setColorInterpolation(value);
    }

    default String getColorRendering() {
        return getGraphicsAttributes().getColorRendering();
    }

    default void setColorRendering(String value) {
        getGraphicsAttributes().setColorRendering(value);
    }

    default String getColorProfile() {
        return getGraphicsAttributes().getColorProfile();
    }

    default void setColorProfile(String value) {
        getGraphicsAttributes().setColorProfile(value);
    }

    default String getStopColor() {
        return getGraphicsAttributes().getStopColor();
    }

    default void setStopColor(String value) {
        getGraphicsAttributes().setStopColor(value);
    }

    default String getStopOpacity() {
        return getGraphicsAttributes().getStopOpacity();
    }

    default void setStopOpacity(String value) {
        getGraphicsAttributes().setStopOpacity(value);
    }

    SvgGraphicsStyleAttributes getGraphicsAttributes();

}
