package nz.co.ctg.foxglove.filter;

import java.util.List;

import nz.co.ctg.foxglove.animate.ISvgAnimationElement;

public interface ISvgFilterFunction {

    default String getType() {
        return getFunctionAttributes().getType();
    }

    default void setType(String value) {
        getFunctionAttributes().setType(value);
    }

    default String getTableValues() {
        return getFunctionAttributes().getTableValues();
    }

    default void setTableValues(String value) {
        getFunctionAttributes().setTableValues(value);
    }

    default String getSlope() {
        return getFunctionAttributes().getSlope();
    }

    default void setSlope(String value) {
        getFunctionAttributes().setSlope(value);
    }

    default String getIntercept() {
        return getFunctionAttributes().getIntercept();
    }

    default void setIntercept(String value) {
        getFunctionAttributes().setIntercept(value);
    }

    default String getAmplitude() {
        return getFunctionAttributes().getAmplitude();
    }

    default void setAmplitude(String value) {
        getFunctionAttributes().setAmplitude(value);
    }

    default String getExponent() {
        return getFunctionAttributes().getExponent();
    }

    default void setExponent(String value) {
        getFunctionAttributes().setExponent(value);
    }

    default String getOffset() {
        return getFunctionAttributes().getOffset();
    }

    default void setOffset(String value) {
        getFunctionAttributes().setOffset(value);
    }

    default List<ISvgAnimationElement> getAnimations() {
        return getFunctionAttributes().getAnimations();
    }

    FilterEffectCompositeFunction getFunctionAttributes();

}