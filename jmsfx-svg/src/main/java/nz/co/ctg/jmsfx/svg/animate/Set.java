//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2021.03.25 at 03:40:09 PM NZDT
//


package nz.co.ctg.jmsfx.svg.animate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nz.co.ctg.jmsfx.svg.description.Desc;
import nz.co.ctg.jmsfx.svg.description.Metadata;
import nz.co.ctg.jmsfx.svg.description.Title;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "contents"
})
@XmlRootElement(name = "set")
public class Set extends FXVGAnimationElement {

    @XmlAttribute(name = "attributeName", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String attributeName;

    @XmlAttribute(name = "attributeType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String attributeType;

    @XmlElements({
        @XmlElement(name = "desc", type = Desc.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = Title.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = Metadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<Object> contents;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String value) {
        this.attributeName = value;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String value) {
        this.attributeType = value;
    }

    /**
     * Gets the value of the descOrTitleOrMetadata property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the descOrTitleOrMetadata property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescOrTitleOrMetadata().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Desc }
     * {@link Title }
     * {@link Metadata }
     *
     *
     */
    public List<Object> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return this.contents;
    }

}
