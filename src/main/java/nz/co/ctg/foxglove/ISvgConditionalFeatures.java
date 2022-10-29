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

import nz.co.ctg.foxglove.attributes.SvgConditionalFeaturesAttributes;

public interface ISvgConditionalFeatures {

    SvgConditionalFeaturesAttributes getConditionalFeaturesAttributes();

    default String getRequiredExtensions() {
        return getConditionalFeaturesAttributes().getRequiredExtensions();
    }

    default List<String> getRequiredExtensionsList() {
        return getConditionalFeaturesAttributes().getRequiredExtensionsList();
    }

    default String getRequiredFeatures() {
        return getConditionalFeaturesAttributes().getRequiredFeatures();
    }

    default List<String> getRequiredFeaturesList() {
        return getConditionalFeaturesAttributes().getRequiredFeaturesList();
    }

    default String getSystemLanguage() {
        return getConditionalFeaturesAttributes().getSystemLanguage();
    }

    default List<String> getSystemLanguageList() {
        return getConditionalFeaturesAttributes().getSystemLanguageList();
    }

    default boolean hasExtension(String extension) {
        return getConditionalFeaturesAttributes().hasExtension(extension);
    }

    default void setRequiredExtensions(String value) {
        getConditionalFeaturesAttributes().setRequiredExtensions(value);
    }

    default void setRequiredFeatures(String value) {
        getConditionalFeaturesAttributes().setRequiredFeatures(value);
    }

    default void setSystemLanguage(String value) {
        getConditionalFeaturesAttributes().setSystemLanguage(value);
    }

}
