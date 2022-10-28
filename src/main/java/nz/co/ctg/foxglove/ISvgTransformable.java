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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.parser.SvgTransformListHandler;

import javafx.scene.transform.Transform;

public interface ISvgTransformable {

    String getTransform();

    void setTransform(String value);

    default List<Transform> getTransformList() {
        if (StringUtils.isBlank(getTransform())) {
            return Collections.emptyList();
        }
        return new SvgTransformListHandler().parse(getTransform());
    }

}
