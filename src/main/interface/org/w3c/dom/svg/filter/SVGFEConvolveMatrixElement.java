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

package org.w3c.dom.svg.filter;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.type.SVGAnimatedBoolean;
import org.w3c.dom.svg.type.SVGAnimatedEnumeration;
import org.w3c.dom.svg.type.SVGAnimatedInteger;
import org.w3c.dom.svg.type.SVGAnimatedNumber;
import org.w3c.dom.svg.type.SVGAnimatedNumberList;
import org.w3c.dom.svg.type.SVGAnimatedString;

public interface SVGFEConvolveMatrixElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
    SVGAnimatedString getIn1();
    SVGAnimatedInteger getOrderX();
    SVGAnimatedInteger getOrderY();
    SVGAnimatedNumberList getKernelMatrix();
    SVGAnimatedNumber getDivisor();
    SVGAnimatedNumber getBias();
    SVGAnimatedInteger getTargetX();
    SVGAnimatedInteger getTargetY();
    SVGAnimatedEnumeration getEdgeMode();
    SVGAnimatedNumber getKernelUnitLengthX();
    SVGAnimatedNumber getKernelUnitLengthY();
    SVGAnimatedBoolean getPreserveAlpha();
    final short SVG_EDGEMODE_UNKNOWN = 0;
    final short SVG_EDGEMODE_DUPLICATE = 1;
    final short SVG_EDGEMODE_WRAP = 2;
    final short SVG_EDGEMODE_NONE = 3;
}
