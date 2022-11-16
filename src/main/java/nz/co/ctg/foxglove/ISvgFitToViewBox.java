package nz.co.ctg.foxglove;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.type.ViewBox;

public interface ISvgFitToViewBox extends ISvgAttributes {
    String VB_VIEW_BOX = "viewBox";
    String VB_PRESERVE_ASPECT_RATIO = "preserveAspectRatio";

    default ViewBox getViewBox() {
        return get(VB_VIEW_BOX);
    }

    default void setViewBox(ViewBox value) {
        set(VB_VIEW_BOX, value);
    }

    default String getPreserveAspectRatio() {
        return get(VB_PRESERVE_ASPECT_RATIO);
    }

    default void setPreserveAspectRatio(String value) {
        set(VB_PRESERVE_ASPECT_RATIO, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(VB_VIEW_BOX, getViewBox());
        builder.add(VB_PRESERVE_ASPECT_RATIO, getPreserveAspectRatio());
    }

}