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

public interface SVGTextContentElement extends SVGElement, SVGTests, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable {
    SVGAnimatedLength getTextLength();
    SVGAnimatedEnumeration getLengthAdjust();
    long getNumberOfChars();
    float getComputedTextLength();
    float getSubStringLength(long charnum, long nchars);
    SVGPoint getStartPositionOfChar(long charnum);
    SVGPoint getEndPositionOfChar(long charnum);
    SVGRect getExtentOfChar(long charnum);
    float getRotationOfChar(long charnum);
    long getCharNumAtPosition(SVGPoint point);
    void selectSubString(long charnum, long nchars);
    final short LENGTHADJUST_UNKNOWN = 0;
    final short LENGTHADJUST_SPACING = 1;
    final short LENGTHADJUST_SPACINGANDGLYPHS = 2;
}
