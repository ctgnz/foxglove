package nz.co.ctg.foxglove;

import nz.co.ctg.foxglove.attributes.SvgPresentationStyleAttributes;

public interface ISvgPresentationAttributes {

    default String getFloodColor() {
        return getPresentationAttributes().getFloodColor();
    }

    default void setFloodColor(String value) {
        getPresentationAttributes().setFloodColor(value);
    }

    default String getFloodOpacity() {
        return getPresentationAttributes().getFloodOpacity();
    }

    default void setFloodOpacity(String value) {
        getPresentationAttributes().setFloodOpacity(value);
    }

    default String getLightingColor() {
        return getPresentationAttributes().getLightingColor();
    }

    default void setLightingColor(String value) {
        getPresentationAttributes().setLightingColor(value);
    }

    default String getColorInterpolationFilters() {
        return getPresentationAttributes().getColorInterpolationFilters();
    }

    default void setColorInterpolationFilters(String value) {
        getPresentationAttributes().setColorInterpolationFilters(value);
    }

    default String getFilter() {
        return getPresentationAttributes().getFilter();
    }

    default void setFilter(String value) {
        getPresentationAttributes().setFilter(value);
    }

    default String getMask() {
        return getPresentationAttributes().getMask();
    }

    default void setMask(String value) {
        getPresentationAttributes().setMask(value);
    }

    default String getClip() {
        return getPresentationAttributes().getClip();
    }

    default void setClip(String value) {
        getPresentationAttributes().setClip(value);
    }

    default String getClipPath() {
        return getPresentationAttributes().getClipPath();
    }

    default void setClipPath(String value) {
        getPresentationAttributes().setClipPath(value);
    }

    default String getClipRule() {
        return getPresentationAttributes().getClipRule();
    }

    default void setClipRule(String value) {
        getPresentationAttributes().setClipRule(value);
    }

    default String getOverflow() {
        return getPresentationAttributes().getOverflow();
    }

    default void setOverflow(String value) {
        getPresentationAttributes().setOverflow(value);
    }

    default String getMarkerStart() {
        return getPresentationAttributes().getMarkerStart();
    }

    default void setMarkerStart(String value) {
        getPresentationAttributes().setMarkerStart(value);
    }

    default String getMarkerMid() {
        return getPresentationAttributes().getMarkerMid();
    }

    default void setMarkerMid(String value) {
        getPresentationAttributes().setMarkerMid(value);
    }

    default String getMarkerEnd() {
        return getPresentationAttributes().getMarkerEnd();
    }

    default void setMarkerEnd(String value) {
        getPresentationAttributes().setMarkerEnd(value);
    }

    default String getEnableBackground() {
        return getPresentationAttributes().getEnableBackground();
    }

    default void setEnableBackground(String value) {
        getPresentationAttributes().setEnableBackground(value);
    }

    default String getCursor() {
        return getPresentationAttributes().getCursor();
    }

    default void setCursor(String value) {
        getPresentationAttributes().setCursor(value);
    }

    SvgPresentationStyleAttributes getPresentationAttributes();

}
