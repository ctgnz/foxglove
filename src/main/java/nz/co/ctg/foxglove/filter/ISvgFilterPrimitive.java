package nz.co.ctg.foxglove.filter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgAttributes;

public interface ISvgFilterPrimitive extends ISvgAttributes {
    String FE_X = "x";
    String FE_Y = "y";
    String FE_WIDTH = "width";
    String FE_HEIGHT = "height";
    String FE_RESULT = "result";

    default String getX() {
        return get(FE_X);
    }

    default void setX(String value) {
        set(FE_X, value);
    }

    default String getY() {
        return get(FE_Y);
    }

    default void setY(String value) {
        set(FE_Y, value);
    }

    default String getWidth() {
        return get(FE_WIDTH);
    }

    default void setWidth(String value) {
        set(FE_WIDTH, value);
    }

    default String getHeight() {
        return get(FE_HEIGHT);
    }

    default void setHeight(String value) {
        set(FE_HEIGHT, value);
    }

    default String getResult() {
        return get(FE_RESULT);
    }

    default void setResult(String value) {
        set(FE_RESULT, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(FE_X, getX());
        builder.add(FE_Y, getY());
        builder.add(FE_WIDTH, getWidth());
        builder.add(FE_HEIGHT, getHeight());
        builder.add(FE_RESULT, getResult());
    }

}
