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

package nz.co.ctg.jmsfx.svg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public interface FXVGConditionalFeatures {

    default List<String> getRequiredFeaturesList() {
        return getRequiredFeatures() != null ? Arrays.stream(getRequiredFeatures().split(",")).collect(toList()) : Collections.emptyList();
    }

    default List<String> getRequiredExtensionsList() {
        return getRequiredExtensions() != null ? Arrays.stream(getRequiredExtensions().split(",")).collect(toList()) : Collections.emptyList();
    }

    default List<String> getSystemLanguageList() {
        return getSystemLanguage() != null ? Arrays.stream(getSystemLanguage().split(",")).collect(toList()) : Collections.emptyList();
    }

    default boolean hasExtension(String extension) {
        return getRequiredExtensionsList().contains(extension);
    }

    void setSystemLanguage(String value);

    String getSystemLanguage();

    void setRequiredExtensions(String value);

    String getRequiredExtensions();

    void setRequiredFeatures(String value);

    String getRequiredFeatures();

}
