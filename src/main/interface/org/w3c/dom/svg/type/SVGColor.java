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

package org.w3c.dom.svg.type;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;

public interface SVGColor extends CSSValue {
    short getColorType();
    RGBColor getRgbColor();
    SVGICCColor getIccColor();
    void setRGBColor(String rgbColor);
    void setRGBColorICCColor(String rgbColor, String iccColor);
    void setColor(short colorType, String rgbColor, String iccColor);
    final short SVG_COLORTYPE_UNKNOWN = 0;
    final short SVG_COLORTYPE_RGBCOLOR = 1;
    final short SVG_COLORTYPE_RGBCOLOR_ICCCOLOR = 2;
    final short SVG_COLORTYPE_CURRENTCOLOR = 3;
}
