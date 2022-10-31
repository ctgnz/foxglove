package nz.co.ctg.foxglove;

import nz.co.ctg.foxglove.attributes.SvgEventListenerAttributes;

public interface ISvgEventListener {

    default String getOnFocusIn() {
        return getEventListenerAttributes().getOnFocusIn();
    }

    default void setOnFocusIn(String value) {
        getEventListenerAttributes().setOnFocusIn(value);
    }

    default String getOnFocusOut() {
        return getEventListenerAttributes().getOnFocusOut();
    }

    default void setOnFocusOut(String value) {
        getEventListenerAttributes().setOnFocusOut(value);
    }

    default String getOnActivate() {
        return getEventListenerAttributes().getOnActivate();
    }

    default void setOnActivate(String value) {
        getEventListenerAttributes().setOnActivate(value);
    }

    default String getOnClick() {
        return getEventListenerAttributes().getOnClick();
    }

    default void setOnClick(String value) {
        getEventListenerAttributes().setOnClick(value);
    }

    default String getOnMouseDown() {
        return getEventListenerAttributes().getOnMouseDown();
    }

    default void setOnMouseDown(String value) {
        getEventListenerAttributes().setOnMouseDown(value);
    }

    default String getOnMouseUp() {
        return getEventListenerAttributes().getOnMouseUp();
    }

    default void setOnMouseUp(String value) {
        getEventListenerAttributes().setOnMouseUp(value);
    }

    default String getOnMouseOver() {
        return getEventListenerAttributes().getOnMouseOver();
    }

    default void setOnMouseOver(String value) {
        getEventListenerAttributes().setOnMouseOver(value);
    }

    default String getOnMouseMove() {
        return getEventListenerAttributes().getOnMouseMove();
    }

    default void setOnMouseMove(String value) {
        getEventListenerAttributes().setOnMouseMove(value);
    }

    default String getOnMouseOut() {
        return getEventListenerAttributes().getOnMouseOut();
    }

    default void setOnMouseOut(String value) {
        getEventListenerAttributes().setOnMouseOut(value);
    }

    default String getOnLoad() {
        return getEventListenerAttributes().getOnLoad();
    }

    default void setOnLoad(String value) {
        getEventListenerAttributes().setOnLoad(value);
    }

    SvgEventListenerAttributes getEventListenerAttributes();

}