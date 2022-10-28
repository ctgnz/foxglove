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

package nz.co.ctg.foxglove;

import javax.xml.bind.annotation.XmlRootElement;

public interface ISvgElement {
    String getId();

    void setId(String id);

    String getXmlBase();

    void setXmlBase(String xmlBase);

    String getXmlLang();

    void setXmlLang(String xmlLang);

    String getXmlSpace();

    void setXmlSpace(String xmlSpace);

    default SvgGraphic getOwnerSVGElement() {
        return null;
    }

    default ISvgElement getViewportElement() {
        return null;
    }

    default String getElementName() {
        XmlRootElement annotation = getClass().getAnnotation(XmlRootElement.class);
        return annotation != null ? annotation.name() : getClass().getSimpleName();
    }

}
