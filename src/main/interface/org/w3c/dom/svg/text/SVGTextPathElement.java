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

package org.w3c.dom.svg.text;

import org.w3c.dom.svg.SVGURIReference;
import org.w3c.dom.svg.type.SVGAnimatedEnumeration;
import org.w3c.dom.svg.type.SVGAnimatedLength;

public interface SVGTextPathElement extends SVGTextContentElement, SVGURIReference {
    SVGAnimatedLength getStartOffset();
    SVGAnimatedEnumeration getMethod();
    SVGAnimatedEnumeration getSpacing();
    final short TEXTPATH_METHODTYPE_UNKNOWN = 0;
    final short TEXTPATH_METHODTYPE_ALIGN = 1;
    final short TEXTPATH_METHODTYPE_STRETCH = 2;
    final short TEXTPATH_SPACINGTYPE_UNKNOWN = 0;
    final short TEXTPATH_SPACINGTYPE_AUTO = 1;
    final short TEXTPATH_SPACINGTYPE_EXACT = 2;
}
