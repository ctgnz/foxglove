package nz.co.ctg.foxglove;

import com.google.common.base.MoreObjects.ToStringHelper;

public interface ISvgPresentationAttributes extends ISvgAttributes {
    String PRES_FLOOD_COLOR = "flood-color";
    String PRES_FLOOD_OPACITY = "flood-opacity";
    String PRES_LIGHTING_COLOR = "lighting-color";
    String PRES_ENABLE_BACKGROUND = "enable-background";
    String PRES_CLIP = "clip";
    String PRES_OVERFLOW = "overflow";
    String PRES_MARKER_START = "marker-start";
    String PRES_MARKER_MID = "marker-mid";
    String PRES_MARKER_END = "marker-end";
    String PRES_CLIP_PATH = "clip-path";
    String PRES_CLIP_RULE = "clip-rule";
    String PRES_MASK = "mask";
    String PRES_FILTER = "filter";
    String PRES_COLOR_INTERPOLATION_FILTERS = "color-interpolation-filters";
    String PRES_CURSOR = "cursor";

    default String getFloodColor() {
        return get(PRES_FLOOD_COLOR);
    }

    default void setFloodColor(String value) {
        set(PRES_FLOOD_COLOR, value);
    }

    default String getFloodOpacity() {
        return get(PRES_FLOOD_OPACITY);
    }

    default void setFloodOpacity(String value) {
        set(PRES_FLOOD_OPACITY, value);
    }

    default String getLightingColor() {
        return get(PRES_LIGHTING_COLOR);
    }

    default void setLightingColor(String value) {
        set(PRES_LIGHTING_COLOR, value);
    }

    default String getEnableBackground() {
        return get(PRES_ENABLE_BACKGROUND);
    }

    default void setEnableBackground(String value) {
        set(PRES_ENABLE_BACKGROUND, value);
    }

    default String getClip() {
        return get(PRES_CLIP);
    }

    default void setClip(String value) {
        set(PRES_CLIP, value);
    }

    default String getOverflow() {
        return get(PRES_OVERFLOW);
    }

    default void setOverflow(String value) {
        set(PRES_OVERFLOW, value);
    }

    default String getMarkerStart() {
        return get(PRES_MARKER_START);
    }

    default void setMarkerStart(String value) {
        set(PRES_MARKER_START, value);
    }

    default String getMarkerMid() {
        return get(PRES_MARKER_MID);
    }

    default void setMarkerMid(String value) {
        set(PRES_MARKER_MID, value);
    }

    default String getMarkerEnd() {
        return get(PRES_MARKER_END);
    }

    default void setMarkerEnd(String value) {
        set(PRES_MARKER_END, value);
    }

    default String getMask() {
        return get(PRES_MASK);
    }

    default void setMask(String value) {
        set(PRES_MASK, value);
    }

    default String getClipPath() {
        return get(PRES_CLIP_PATH);
    }

    default void setClipPath(String value) {
        set(PRES_CLIP_PATH, value);
    }

    default String getClipRule() {
        return get(PRES_CLIP_RULE);
    }

    default void setClipRule(String value) {
        set(PRES_CLIP_RULE, value);
    }

    default String getFilter() {
        return get(PRES_FILTER);
    }

    default void setFilter(String value) {
        set(PRES_FILTER, value);
    }

    default String getColorInterpolationFilters() {
        return get(PRES_COLOR_INTERPOLATION_FILTERS);
    }

    default void setColorInterpolationFilters(String value) {
        set(PRES_COLOR_INTERPOLATION_FILTERS, value);
    }

    default String getCursor() {
        return get(PRES_CURSOR);
    }

    default void setCursor(String value) {
        set(PRES_CURSOR, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(PRES_FLOOD_COLOR, getFloodColor());
        builder.add(PRES_FLOOD_OPACITY, getFloodOpacity());
        builder.add(PRES_LIGHTING_COLOR, getLightingColor());
        builder.add(PRES_ENABLE_BACKGROUND, getEnableBackground());
        builder.add(PRES_CLIP, getClip());
        builder.add(PRES_OVERFLOW, getOverflow());
        builder.add(PRES_MARKER_START, getMarkerStart());
        builder.add(PRES_MARKER_MID, getMarkerMid());
        builder.add(PRES_MARKER_END, getMarkerEnd());
        builder.add(PRES_CLIP_PATH, getClipPath());
        builder.add(PRES_CLIP_RULE, getClipRule());
        builder.add(PRES_MASK, getMask());
        builder.add(PRES_FILTER, getFilter());
        builder.add(PRES_COLOR_INTERPOLATION_FILTERS, getColorInterpolationFilters());
        builder.add(PRES_CURSOR, getCursor());
    }

}
