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

public interface SVGMatrix {
    float getA();
    void setA(float a);
    float getB();
    void setB(float b);
    float getC();
    void setC(float c);
    float getD();
    void setD(float d);
    float getE();
    void setE(float e);
    float getF();
    void setF(float f);
    SVGMatrix multiply(SVGMatrix secondMatrix);
    SVGMatrix inverse();
    SVGMatrix translate(float x, float y);
    SVGMatrix scale(float scaleFactor);
    SVGMatrix scaleNonUniform(float scaleFactorX, float scaleFactorY);
    SVGMatrix rotate(float angle);
    SVGMatrix rotateFromVector(float x, float y);
    SVGMatrix flipX();
    SVGMatrix flipY();
    SVGMatrix skewX(float angle);
    SVGMatrix skewY(float angle);
}
