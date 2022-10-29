package nz.co.ctg.foxglove;

import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;

public interface ISvgLinkable {

    default String getXmlnsXlink() {
        return getLinkableAttributes().getXmlnsXlink();
    }

    default void setXmlnsXlink(String value) {
        getLinkableAttributes().setXmlnsXlink(value);
    }

    default String getXlinkType() {
        return getLinkableAttributes().getXlinkType();
    }

    default void setXlinkType(String value) {
        getLinkableAttributes().setXlinkType(value);
    }

    default String getXlinkHref() {
        return getLinkableAttributes().getXlinkHref();
    }

    default void setXlinkHref(String value) {
        getLinkableAttributes().setXlinkHref(value);
    }

    default String getXlinkRole() {
        return getLinkableAttributes().getXlinkRole();
    }

    default void setXlinkRole(String value) {
        getLinkableAttributes().setXlinkRole(value);
    }

    default String getXlinkArcrole() {
        return getLinkableAttributes().getXlinkArcrole();
    }

    default void setXlinkArcrole(String value) {
        getLinkableAttributes().setXlinkArcrole(value);
    }

    default String getXlinkTitle() {
        return getLinkableAttributes().getXlinkTitle();
    }

    default void setXlinkTitle(String value) {
        getLinkableAttributes().setXlinkTitle(value);
    }

    default String getXlinkShow() {
        return getLinkableAttributes().getXlinkShow();
    }

    default void setXlinkShow(String value) {
        getLinkableAttributes().setXlinkShow(value);
    }

    default String getXlinkActuate() {
        return getLinkableAttributes().getXlinkActuate();
    }

    default void setXlinkActuate(String value) {
        getLinkableAttributes().setXlinkActuate(value);
    }

    SvgLinkableAttributes getLinkableAttributes();

}