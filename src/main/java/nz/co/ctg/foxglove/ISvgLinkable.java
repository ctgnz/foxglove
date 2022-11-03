package nz.co.ctg.foxglove;

import com.google.common.base.MoreObjects.ToStringHelper;

public interface ISvgLinkable extends ISvgAttributes {
    String ATTR_XLINK_NAMESPACE = "xmlns:xlink";
    String ATTR_XLINK_TYPE = "xlink:type";
    String ATTR_XLINK_HREF = "xlink:href";
    String ATTR_XLINK_ROLE = "xlink:role";
    String ATTR_XLINK_ARCROLE = "xlink:arcrole";
    String ATTR_XLINK_TITLE = "xlink:title";
    String ATTR_XLINK_SHOW = "xlink:show";
    String ATTR_XLINK_ACTUATE = "xlink:actuate";

    default String getXlinkNamespace() {
        if (!getProperties().containsKey(ATTR_XLINK_NAMESPACE)) {
            return "http://www.w3.org/1999/xlink";
        }
        return get(ATTR_XLINK_NAMESPACE);
    }

    default void setXlinkNamespace(String value) {
        set(ATTR_XLINK_NAMESPACE, value);
    }

    default String getXlinkType() {
        return get(ATTR_XLINK_TYPE);
    }

    default void setXlinkType(String value) {
        set(ATTR_XLINK_TYPE, value);
    }

    default String getXlinkHref() {
        return get(ATTR_XLINK_HREF);
    }

    default void setXlinkHref(String value) {
        set(ATTR_XLINK_HREF, value);
    }

    default String getXlinkRole() {
        return get(ATTR_XLINK_ROLE);
    }

    default void setXlinkRole(String value) {
        set(ATTR_XLINK_ROLE, value);
    }

    default String getXlinkArcrole() {
        return get(ATTR_XLINK_ARCROLE);
    }

    default void setXlinkArcrole(String value) {
        set(ATTR_XLINK_ARCROLE, value);
    }

    default String getXlinkTitle() {
        return get(ATTR_XLINK_TITLE);
    }

    default void setXlinkTitle(String value) {
        set(ATTR_XLINK_TITLE, value);
    }

    default String getXlinkShow() {
        return get(ATTR_XLINK_SHOW);
    }

    default void setXlinkShow(String value) {
        set(ATTR_XLINK_SHOW, value);
    }

    default String getXlinkActuate() {
        return get(ATTR_XLINK_ACTUATE);
    }

    default void setXlinkActuate(String value) {
        set(ATTR_XLINK_ACTUATE, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_XLINK_NAMESPACE, getXlinkNamespace());
        builder.add(ATTR_XLINK_TYPE, getXlinkType());
        builder.add(ATTR_XLINK_HREF, getXlinkHref());
        builder.add(ATTR_XLINK_ROLE, getXlinkRole());
        builder.add(ATTR_XLINK_ARCROLE, getXlinkArcrole());
        builder.add(ATTR_XLINK_TITLE, getXlinkTitle());
        builder.add(ATTR_XLINK_SHOW, getXlinkShow());
        builder.add(ATTR_XLINK_ACTUATE, getXlinkActuate());
    }
}