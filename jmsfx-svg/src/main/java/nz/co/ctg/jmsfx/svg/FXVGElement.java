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

package nz.co.ctg.jmsfx.svg;

import nz.co.ctg.jmsfx.svg.document.FXVGRootElement;

public interface FXVGElement {
    String getId();

    void setId(String id);

    String getXmlBase();

    void setXmlBase(String xmlBase);

    String getXmlLang();

    void setXmlLang(String xmlLang);

    String getXmlSpace();

    void setXmlSpace(String xmlSpace);

    default FXVGRootElement getOwnerSVGElement() {
        return null;
    }

    default FXVGElement getViewportElement() {
        return null;
    }

}
