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

package org.w3c.dom.svg.paint;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGURIReference;
import org.w3c.dom.svg.type.SVGAnimatedEnumeration;
import org.w3c.dom.svg.type.SVGAnimatedTransformList;
import org.w3c.dom.svg.type.SVGUnitTypes;

public interface SVGGradientElement extends SVGElement, SVGURIReference, SVGExternalResourcesRequired, SVGStylable, SVGUnitTypes {
    SVGAnimatedEnumeration getGradientUnits();
    SVGAnimatedTransformList getGradientTransform();
    SVGAnimatedEnumeration getSpreadMethod();
    final short SVG_SPREADMETHOD_UNKNOWN = 0;
    final short SVG_SPREADMETHOD_PAD = 1;
    final short SVG_SPREADMETHOD_REFLECT = 2;
    final short SVG_SPREADMETHOD_REPEAT = 3;
}
