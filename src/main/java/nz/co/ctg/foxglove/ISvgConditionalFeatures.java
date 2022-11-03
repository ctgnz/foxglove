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

import com.google.common.base.MoreObjects.ToStringHelper;

public interface ISvgConditionalFeatures extends ISvgAttributes {
    String COND_REQUIRED_FEATURES = "requiredFeatures";
    String COND_REQUIRED_EXTENSIONS = "requiredExtensions";
    String COND_SYSTEM_LANGUAGE = "systemLanguage";

    default String getRequiredExtensions() {
        return get(COND_REQUIRED_EXTENSIONS);
    }

    default String getRequiredFeatures() {
        return get(COND_REQUIRED_FEATURES);
    }

    default String getSystemLanguage() {
        return get(COND_SYSTEM_LANGUAGE);
    }

    default boolean hasExtension(String extension) {
        return getRequiredExtensions().contains(extension);
    }

    default void setRequiredExtensions(String value) {
        set(COND_REQUIRED_EXTENSIONS, value);
    }

    default void setRequiredFeatures(String value) {
        set(COND_REQUIRED_FEATURES, value);
    }

    default void setSystemLanguage(String value) {
        set(COND_SYSTEM_LANGUAGE, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(COND_REQUIRED_FEATURES, getRequiredFeatures());
        builder.add(COND_REQUIRED_EXTENSIONS, getRequiredExtensions());
        builder.add(COND_SYSTEM_LANGUAGE, getSystemLanguage());
    }

}
