package nz.co.ctg.foxglove;

import com.google.common.base.MoreObjects.ToStringHelper;

public interface ISvgEventListener extends ISvgAttributes {
    String EVT_ONLOAD = "onload";
    String EVT_ONMOUSEOUT = "onmouseout";
    String EVT_ONMOUSEMOVE = "onmousemove";
    String EVT_ONMOUSEOVER = "onmouseover";
    String EVT_ONMOUSEUP = "onmouseup";
    String EVT_ONMOUSEDOWN = "onmousedown";
    String EVT_ONCLICK = "onclick";
    String EVT_ONACTIVATE = "onactivate";
    String EVT_ONFOCUSOUT = "onfocusout";
    String EVT_ONFOCUSIN = "onfocusin";

    default String getOnFocusIn() {
        return get(EVT_ONFOCUSIN);
    }

    default void setOnFocusIn(String value) {
        set(EVT_ONFOCUSIN, value);
    }

    default String getOnFocusOut() {
        return get(EVT_ONFOCUSOUT);
    }

    default void setOnFocusOut(String value) {
        set(EVT_ONFOCUSOUT, value);
    }

    default String getOnActivate() {
        return get(EVT_ONACTIVATE);
    }

    default void setOnActivate(String value) {
        set(EVT_ONACTIVATE, value);
    }

    default String getOnClick() {
        return get(EVT_ONCLICK);
    }

    default void setOnClick(String value) {
        set(EVT_ONCLICK, value);
    }

    default String getOnMouseDown() {
        return get(EVT_ONMOUSEDOWN);
    }

    default void setOnMouseDown(String value) {
        set(EVT_ONMOUSEDOWN, value);
    }

    default String getOnMouseUp() {
        return get(EVT_ONMOUSEUP);
    }

    default void setOnMouseUp(String value) {
        set(EVT_ONMOUSEUP, value);
    }

    default String getOnMouseOver() {
        return get(EVT_ONMOUSEOVER);
    }

    default void setOnMouseOver(String value) {
        set(EVT_ONMOUSEOVER, value);
    }

    default String getOnMouseMove() {
        return get(EVT_ONMOUSEMOVE);
    }

    default void setOnMouseMove(String value) {
        set(EVT_ONMOUSEMOVE, value);
    }

    default String getOnMouseOut() {
        return get(EVT_ONMOUSEOUT);
    }

    default void setOnMouseOut(String value) {
        set(EVT_ONMOUSEOUT, value);
    }

    default String getOnLoad() {
        return get(EVT_ONLOAD);
    }

    default void setOnLoad(String value) {
        set(EVT_ONLOAD, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(EVT_ONFOCUSIN, getOnFocusIn());
        builder.add(EVT_ONFOCUSOUT, getOnFocusOut());
        builder.add(EVT_ONACTIVATE, getOnActivate());
        builder.add(EVT_ONCLICK, getOnClick());
        builder.add(EVT_ONMOUSEDOWN, getOnMouseDown());
        builder.add(EVT_ONMOUSEUP, getOnMouseUp());
        builder.add(EVT_ONMOUSEOVER, getOnMouseOver());
        builder.add(EVT_ONMOUSEMOVE, getOnMouseMove());
        builder.add(EVT_ONMOUSEOUT, getOnMouseOut());
    }

}