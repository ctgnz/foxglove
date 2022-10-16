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

package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGTransform {
    short getType();
    SVGMatrix getMatrix();
    float getAngle();
    void setMatrix(SVGMatrix matrix);
    void setTranslate(float tx, float ty);
    void setScale(float sx, float sy);
    void setRotate(float angle, float cx, float cy);
    void setSkewX(float angle);
    void setSkewY(float angle);
    final short SVG_TRANSFORM_UNKNOWN = 0;
    final short SVG_TRANSFORM_MATRIX = 1;
    final short SVG_TRANSFORM_TRANSLATE = 2;
    final short SVG_TRANSFORM_SCALE = 3;
    final short SVG_TRANSFORM_ROTATE = 4;
    final short SVG_TRANSFORM_SKEWX = 5;
    final short SVG_TRANSFORM_SKEWY = 6;
}
