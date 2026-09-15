package nz.co.ctg.foxglove;

import javafx.scene.transform.Transform;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.type.PreserveAspectRatio;
import nz.co.ctg.foxglove.type.ViewBox;

public interface ISvgFitToViewBox extends ISvgAttributes {
    String VB_VIEW_BOX = "viewBox";
    String VB_PRESERVE_ASPECT_RATIO = "preserveAspectRatio";

    /**
     * The transform mapping this element's {@code viewBox} onto a viewport of the given size, or null when no {@code viewBox} is set.
     */
    default Transform createViewportTransform(double viewportWidth, double viewportHeight) {
        ViewBox viewBox = getViewBox();
        if (viewBox == null) {
            return null;
        }
        return viewBox.createTransform(viewportWidth, viewportHeight, PreserveAspectRatio.parse(getPreserveAspectRatio()));
    }

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