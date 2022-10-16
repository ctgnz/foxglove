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

public interface SVGPathSegArcRel extends SVGPathSeg {
    float getX();
    void setX(float x);
    float getY();
    void setY(float y);
    float getR1();
    void setR1(float r1);
    float getR2();
    void setR2(float r2);
    float getAngle();
    void setAngle(float angle);
    boolean getLargeArcFlag();
    void setLargeArcFlag(boolean largeArcFlag);
    boolean getSweepFlag();
    void setSweepFlag(boolean sweepFlag);
}
