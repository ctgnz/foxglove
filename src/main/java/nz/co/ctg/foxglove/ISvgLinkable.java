package nz.co.ctg.foxglove;

import nz.co.ctg.foxglove.helper.SvgLinkable;

public interface ISvgLinkable {

    default String getXmlnsXlink() {
        return getSvgLinkable().getXmlnsXlink();
    }

    default void setXmlnsXlink(String value) {
        getSvgLinkable().setXmlnsXlink(value);
    }

    default String getXlinkType() {
        return getSvgLinkable().getXlinkType();
    }

    default void setXlinkType(String value) {
        getSvgLinkable().setXlinkType(value);
    }

    default String getXlinkHref() {
        return getSvgLinkable().getXlinkHref();
    }

    default void setXlinkHref(String value) {
        getSvgLinkable().setXlinkHref(value);
    }

    default String getXlinkRole() {
        return getSvgLinkable().getXlinkRole();
    }

    default void setXlinkRole(String value) {
        getSvgLinkable().setXlinkRole(value);
    }

    default String getXlinkArcrole() {
        return getSvgLinkable().getXlinkArcrole();
    }

    default void setXlinkArcrole(String value) {
        getSvgLinkable().setXlinkArcrole(value);
    }

    default String getXlinkTitle() {
        return getSvgLinkable().getXlinkTitle();
    }

    default void setXlinkTitle(String value) {
        getSvgLinkable().setXlinkTitle(value);
    }

    default String getXlinkShow() {
        return getSvgLinkable().getXlinkShow();
    }

    default void setXlinkShow(String value) {
        getSvgLinkable().setXlinkShow(value);
    }

    default String getXlinkActuate() {
        return getSvgLinkable().getXlinkActuate();
    }

    default void setXlinkActuate(String value) {
        getSvgLinkable().setXlinkActuate(value);
    }

    SvgLinkable getSvgLinkable();

}