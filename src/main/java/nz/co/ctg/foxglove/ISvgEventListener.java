package nz.co.ctg.foxglove;

import nz.co.ctg.foxglove.attributes.SvgEventListener;

public interface ISvgEventListener {

    default String getOnFocusIn() {
        return getEventListener().getOnFocusIn();
    }

    default void setOnFocusIn(String value) {
        getEventListener().setOnFocusIn(value);
    }

    default String getOnFocusOut() {
        return getEventListener().getOnFocusOut();
    }

    default void setOnFocusOut(String value) {
        getEventListener().setOnFocusOut(value);
    }

    default String getOnActivate() {
        return getEventListener().getOnActivate();
    }

    default void setOnActivate(String value) {
        getEventListener().setOnActivate(value);
    }

    default String getOnClick() {
        return getEventListener().getOnClick();
    }

    default void setOnClick(String value) {
        getEventListener().setOnClick(value);
    }

    default String getOnMouseDown() {
        return getEventListener().getOnMouseDown();
    }

    default void setOnMouseDown(String value) {
        getEventListener().setOnMouseDown(value);
    }

    default String getOnMouseUp() {
        return getEventListener().getOnMouseUp();
    }

    default void setOnMouseUp(String value) {
        getEventListener().setOnMouseUp(value);
    }

    default String getOnMouseOver() {
        return getEventListener().getOnMouseOver();
    }

    default void setOnMouseOver(String value) {
        getEventListener().setOnMouseOver(value);
    }

    default String getOnMouseMove() {
        return getEventListener().getOnMouseMove();
    }

    default void setOnMouseMove(String value) {
        getEventListener().setOnMouseMove(value);
    }

    default String getOnMouseOut() {
        return getEventListener().getOnMouseOut();
    }

    default void setOnMouseOut(String value) {
        getEventListener().setOnMouseOut(value);
    }

    default String getOnLoad() {
        return getEventListener().getOnLoad();
    }

    default void setOnLoad(String value) {
        getEventListener().setOnLoad(value);
    }

    SvgEventListener getEventListener();

}