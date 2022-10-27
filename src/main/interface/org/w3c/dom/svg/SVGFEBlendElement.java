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

public interface SVGFEBlendElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
    SVGAnimatedString getIn1();
    SVGAnimatedString getIn2();
    SVGAnimatedEnumeration getMode();
    final short SVG_FEBLEND_MODE_UNKNOWN = 0;
    final short SVG_FEBLEND_MODE_NORMAL = 1;
    final short SVG_FEBLEND_MODE_MULTIPLY = 2;
    final short SVG_FEBLEND_MODE_SCREEN = 3;
    final short SVG_FEBLEND_MODE_DARKEN = 4;
    final short SVG_FEBLEND_MODE_LIGHTEN = 5;
}
