package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.animate.ISvgAttributeAnimation;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "animations"
})
@XmlRootElement(name = "fePointLight")
public class FePointLight extends AbstractSvgElement implements ISvgFilterLightSource {

    @XmlAttribute(name = "x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String x;

    @XmlAttribute(name = "y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String y;

    @XmlAttribute(name = "z")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String z;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<ISvgAttributeAnimation> animations;

    /**
     * Gets the value of the x property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setX(String value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setY(String value) {
        this.y = value;
    }

    /**
     * Gets the value of the z property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getZ() {
        return z;
    }

    /**
     * Sets the value of the z property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setZ(String value) {
        this.z = value;
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
    @Override
    public List<ISvgAttributeAnimation> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<ISvgAttributeAnimation>();
        }
        return this.animations;
    }

}
