package nz.co.ctg.foxglove;

import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;

public interface ISvgLinkable {

    default String getXlinkNamespace() {
        return getLinkableAttributes().getXlinkNamespace();
    }

    default void setXlinkNamespace(String value) {
        getLinkableAttributes().setXlinkNamespace(value);
    }

    default String getXlinkType() {
        return getLinkableAttributes().getType();
    }

    default void setXlinkType(String value) {
        getLinkableAttributes().setType(value);
    }

    default String getXlinkHref() {
        return getLinkableAttributes().getHref();
    }

    default void setXlinkHref(String value) {
        getLinkableAttributes().setHref(value);
    }

    default String getXlinkRole() {
        return getLinkableAttributes().getRole();
    }

    default void setXlinkRole(String value) {
        getLinkableAttributes().setRole(value);
    }

    default String getXlinkArcrole() {
        return getLinkableAttributes().getArcRole();
    }

    default void setXlinkArcrole(String value) {
        getLinkableAttributes().setArcRole(value);
    }

    default String getXlinkTitle() {
        return getLinkableAttributes().getTitle();
    }

    default void setXlinkTitle(String value) {
        getLinkableAttributes().setTitle(value);
    }

    default String getXlinkShow() {
        return getLinkableAttributes().getShow();
    }

    default void setXlinkShow(String value) {
        getLinkableAttributes().setShow(value);
    }

    default String getXlinkActuate() {
        return getLinkableAttributes().getActuate();
    }

    default void setXlinkActuate(String value) {
        getLinkableAttributes().setActuate(value);
    }

    SvgLinkableAttributes getLinkableAttributes();

}