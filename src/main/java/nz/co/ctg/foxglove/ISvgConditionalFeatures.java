/*
 * Copyright (c) 2010 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */

package nz.co.ctg.foxglove;

import java.util.List;

import nz.co.ctg.foxglove.helper.SvgConditionalFeatures;

public interface ISvgConditionalFeatures {

    SvgConditionalFeatures getConditionalFeatures();

    default String getRequiredExtensions() {
        return getConditionalFeatures().getRequiredExtensions();
    }

    default List<String> getRequiredExtensionsList() {
        return getConditionalFeatures().getRequiredExtensionsList();
    }

    default String getRequiredFeatures() {
        return getConditionalFeatures().getRequiredFeatures();
    }

    default List<String> getRequiredFeaturesList() {
        return getConditionalFeatures().getRequiredFeaturesList();
    }

    default String getSystemLanguage() {
        return getConditionalFeatures().getSystemLanguage();
    }

    default List<String> getSystemLanguageList() {
        return getConditionalFeatures().getSystemLanguageList();
    }

    default boolean hasExtension(String extension) {
        return getConditionalFeatures().hasExtension(extension);
    }

    default void setRequiredExtensions(String value) {
        getConditionalFeatures().setRequiredExtensions(value);
    }

    default void setRequiredFeatures(String value) {
        getConditionalFeatures().setRequiredFeatures(value);
    }

    default void setSystemLanguage(String value) {
        getConditionalFeatures().setSystemLanguage(value);
    }

}
