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

public interface SVGPathSeg {
    short getPathSegType();
    String getPathSegTypeAsLetter();
    final short PATHSEG_UNKNOWN = 0;
    final short PATHSEG_CLOSEPATH = 1;
    final short PATHSEG_MOVETO_ABS = 2;
    final short PATHSEG_MOVETO_REL = 3;
    final short PATHSEG_LINETO_ABS = 4;
    final short PATHSEG_LINETO_REL = 5;
    final short PATHSEG_CURVETO_CUBIC_ABS = 6;
    final short PATHSEG_CURVETO_CUBIC_REL = 7;
    final short PATHSEG_CURVETO_QUADRATIC_ABS = 8;
    final short PATHSEG_CURVETO_QUADRATIC_REL = 9;
    final short PATHSEG_ARC_ABS = 10;
    final short PATHSEG_ARC_REL = 11;
    final short PATHSEG_LINETO_HORIZONTAL_ABS = 12;
    final short PATHSEG_LINETO_HORIZONTAL_REL = 13;
    final short PATHSEG_LINETO_VERTICAL_ABS = 14;
    final short PATHSEG_LINETO_VERTICAL_REL = 15;
    final short PATHSEG_CURVETO_CUBIC_SMOOTH_ABS = 16;
    final short PATHSEG_CURVETO_CUBIC_SMOOTH_REL = 17;
    final short PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS = 18;
    final short PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL = 19;
}
