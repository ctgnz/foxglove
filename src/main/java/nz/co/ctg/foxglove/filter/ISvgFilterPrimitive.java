package nz.co.ctg.foxglove.filter;

import nz.co.ctg.foxglove.ISvgStylable;

public interface ISvgFilterPrimitive extends ISvgStylable {

    default String getX() {
        return getFilterAttributes().getX();
    }

    default void setX(String value) {
        getFilterAttributes().setX(value);
    }

    default String getY() {
        return getFilterAttributes().getY();
    }

    default void setY(String value) {
        getFilterAttributes().setY(value);
    }

    default String getWidth() {
        return getFilterAttributes().getWidth();
    }

    default void setWidth(String value) {
        getFilterAttributes().setWidth(value);
    }

    default String getHeight() {
        return getFilterAttributes().getHeight();
    }

    default void setHeight(String value) {
        getFilterAttributes().setHeight(value);
    }

    default String getResult() {
        return getFilterAttributes().getResult();
    }

    default void setResult(String value) {
        getFilterAttributes().setResult(value);
    }

    SvgFilterAttributes getFilterAttributes();

}
