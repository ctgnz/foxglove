package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import nz.co.ctg.foxglove.animate.ISvgAnimationElement;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "animations"
})
@XmlRootElement(name = "feComposite")
public class FeComposite extends AbstractSvgFilterPrimitive {

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

    @XmlAttribute(name = "in2", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String in2;

    @XmlAttribute(name = "operator")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String operator;

    @XmlAttribute(name = "k1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String k1;

    @XmlAttribute(name = "k2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String k2;

    @XmlAttribute(name = "k3")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String k3;

    @XmlAttribute(name = "k4")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String k4;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<ISvgAnimationElement> animations;

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
     * Gets the value of the in2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIn2() {
        return in2;
    }

    /**
     * Sets the value of the in2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIn2(String value) {
        this.in2 = value;
    }

    /**
     * Gets the value of the operator property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOperator() {
        if (operator == null) {
            return "over";
        } else {
            return operator;
        }
    }

    /**
     * Sets the value of the operator property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOperator(String value) {
        this.operator = value;
    }

    /**
     * Gets the value of the k1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getK1() {
        return k1;
    }

    /**
     * Sets the value of the k1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setK1(String value) {
        this.k1 = value;
    }

    /**
     * Gets the value of the k2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getK2() {
        return k2;
    }

    /**
     * Sets the value of the k2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setK2(String value) {
        this.k2 = value;
    }

    /**
     * Gets the value of the k3 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getK3() {
        return k3;
    }

    /**
     * Sets the value of the k3 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setK3(String value) {
        this.k3 = value;
    }

    /**
     * Gets the value of the k4 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getK4() {
        return k4;
    }

    /**
     * Sets the value of the k4 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setK4(String value) {
        this.k4 = value;
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
    public List<ISvgAnimationElement> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<>();
        }
        return this.animations;
    }

}
