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

public interface SVGFEColorMatrixElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
    SVGAnimatedString getIn1();
    SVGAnimatedEnumeration getType();
    SVGAnimatedNumberList getValues();
    final short SVG_FECOLORMATRIX_TYPE_UNKNOWN = 0;
    final short SVG_FECOLORMATRIX_TYPE_MATRIX = 1;
    final short SVG_FECOLORMATRIX_TYPE_SATURATE = 2;
    final short SVG_FECOLORMATRIX_TYPE_HUEROTATE = 3;
    final short SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA = 4;
}
