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

public interface SVGComponentTransferFunctionElement extends SVGElement {
    SVGAnimatedEnumeration getType();
    SVGAnimatedNumberList getTableValues();
    SVGAnimatedNumber getSlope();
    SVGAnimatedNumber getIntercept();
    SVGAnimatedNumber getAmplitude();
    SVGAnimatedNumber getExponent();
    SVGAnimatedNumber getOffset();
    final short SVG_FECOMPONENTTRANSFER_TYPE_UNKNOWN = 0;
    final short SVG_FECOMPONENTTRANSFER_TYPE_IDENTITY = 1;
    final short SVG_FECOMPONENTTRANSFER_TYPE_TABLE = 2;
    final short SVG_FECOMPONENTTRANSFER_TYPE_DISCRETE = 3;
    final short SVG_FECOMPONENTTRANSFER_TYPE_LINEAR = 4;
    final short SVG_FECOMPONENTTRANSFER_TYPE_GAMMA = 5;
}
