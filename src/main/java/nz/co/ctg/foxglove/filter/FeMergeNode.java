package nz.co.ctg.foxglove.filter;

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

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.animate.ISvgAttributeAnimation;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "animations"
})
@XmlRootElement(name = "feMergeNode")
public class FeMergeNode extends AbstractSvgElement {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String in;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class),
        @XmlElement(name = "set", type = SvgSetAttribute.class)
    })
    protected List<ISvgAttributeAnimation> animations;

    /**
     * Gets the value of the in property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIn() {
        return in;
    }

    /**
     * Sets the value of the in property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIn(String value) {
        this.in = value;
    }

    /**
     * Gets the value of the animations property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the animations property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnimations().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgAnimateAttribute }
     * {@link SvgSetAttribute }
     *
     *
     */
    public List<ISvgAttributeAnimation> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<ISvgAttributeAnimation>();
        }
        return this.animations;
    }

}
