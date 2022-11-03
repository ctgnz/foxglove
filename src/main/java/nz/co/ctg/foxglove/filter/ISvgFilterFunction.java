package nz.co.ctg.foxglove.filter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgAttributes;
import nz.co.ctg.foxglove.ISvgElement;

public interface ISvgFilterFunction extends ISvgElement, ISvgAttributes {
    String FUNC_TYPE = "type";
    String FUNC_TABLE_VALUES = "tableValues";
    String FUNC_SLOPE = "slope";
    String FUNC_INTERCEPT = "intercept";
    String FUNC_AMPLITUDE = "amplitude";
    String FUNC_EXPONENT = "exponent";
    String FUNC_OFFSET = "offset";

    default String getType() {
        return get(FUNC_TYPE);
    }

    default void setType(String value) {
        set(FUNC_TYPE, value);
    }

    default String getTableValues() {
        return get(FUNC_TABLE_VALUES);
    }

    default void setTableValues(String value) {
        set(FUNC_TABLE_VALUES, value);
    }

    default String getSlope() {
        return get(FUNC_SLOPE);
    }

    default void setSlope(String value) {
        set(FUNC_SLOPE, value);
    }

    default String getIntercept() {
        return get(FUNC_INTERCEPT);
    }

    default void setIntercept(String value) {
        set(FUNC_INTERCEPT, value);
    }

    default String getAmplitude() {
        return get(FUNC_AMPLITUDE);
    }

    default void setAmplitude(String value) {
        set(FUNC_AMPLITUDE, value);
    }

    default String getExponent() {
        return get(FUNC_EXPONENT);
    }

    default void setExponent(String value) {
        set(FUNC_EXPONENT, value);
    }

    default String getOffset() {
        return get(FUNC_OFFSET);
    }

    default void setOffset(String value) {
        set(FUNC_OFFSET, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(FUNC_TYPE, getType());
        builder.add(FUNC_TABLE_VALUES, getTableValues());
        builder.add(FUNC_SLOPE, getSlope());
        builder.add(FUNC_INTERCEPT, getIntercept());
        builder.add(FUNC_AMPLITUDE, getAmplitude());
        builder.add(FUNC_EXPONENT, getExponent());
        builder.add(FUNC_OFFSET, getOffset());
    }

}