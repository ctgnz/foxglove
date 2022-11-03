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

public interface ISvgExternalResources extends ISvgAttributes {
    String EXTERNAL_RESOURCES_REQUIRED = "externalResourcesRequired";

    default boolean isExternalResourcesRequired() {
        if (!getProperties().containsKey(EXTERNAL_RESOURCES_REQUIRED)) {
            return false;
        }
        return get(EXTERNAL_RESOURCES_REQUIRED);
    }

    default void setExternalResourcesRequired(boolean value) {
        set(EXTERNAL_RESOURCES_REQUIRED, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(EXTERNAL_RESOURCES_REQUIRED, isExternalResourcesRequired());
    }

}
