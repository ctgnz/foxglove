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

import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "animateOrSet"
})
@XmlRootElement(name = "feTile")
public class FeTile extends AbstractSvgFilterPrimitive {

    @XmlAttribute(name = "x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String x;
    @XmlAttribute(name = "y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String y;
    @XmlAttribute(name = "width")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String width;
    @XmlAttribute(name = "height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String height;
    @XmlAttribute(name = "result")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String result;
    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String in;
    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class),
        @XmlElement(name = "set", type = SvgSetAttribute.class)
    })
    protected List<Object> animateOrSet;

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
     * Gets the value of the width property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHeight(String value) {
        this.height = value;
    }

    /**
     * Gets the value of the result property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setResult(String value) {
        this.result = value;
    }

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
     * Gets the value of the animateOrSet property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the animateOrSet property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnimateOrSet().add(newItem);
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
    public List<Object> getAnimateOrSet() {
        if (animateOrSet == null) {
            animateOrSet = new ArrayList<Object>();
        }
        return this.animateOrSet;
    }

}
