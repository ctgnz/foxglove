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
@XmlRootElement(name = "feConvolveMatrix")
public class FeConvolveMatrix extends AbstractSvgFilterPrimitive {

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

    @XmlAttribute(name = "order")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String order;

    @XmlAttribute(name = "kernelMatrix", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String kernelMatrix;

    @XmlAttribute(name = "divisor")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String divisor;

    @XmlAttribute(name = "bias")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String bias;

    @XmlAttribute(name = "targetX")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String targetX;

    @XmlAttribute(name = "targetY")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String targetY;

    @XmlAttribute(name = "edgeMode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String edgeMode;

    @XmlAttribute(name = "kernelUnitLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String kernelUnitLength;

    @XmlAttribute(name = "preserveAlpha")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String preserveAlpha;

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
     * Gets the value of the order property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrder(String value) {
        this.order = value;
    }

    /**
     * Gets the value of the kernelMatrix property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKernelMatrix() {
        return kernelMatrix;
    }

    /**
     * Sets the value of the kernelMatrix property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKernelMatrix(String value) {
        this.kernelMatrix = value;
    }

    /**
     * Gets the value of the divisor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDivisor() {
        return divisor;
    }

    /**
     * Sets the value of the divisor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDivisor(String value) {
        this.divisor = value;
    }

    /**
     * Gets the value of the bias property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBias() {
        return bias;
    }

    /**
     * Sets the value of the bias property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBias(String value) {
        this.bias = value;
    }

    /**
     * Gets the value of the targetX property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTargetX() {
        return targetX;
    }

    /**
     * Sets the value of the targetX property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTargetX(String value) {
        this.targetX = value;
    }

    /**
     * Gets the value of the targetY property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTargetY() {
        return targetY;
    }

    /**
     * Sets the value of the targetY property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTargetY(String value) {
        this.targetY = value;
    }

    /**
     * Gets the value of the edgeMode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEdgeMode() {
        if (edgeMode == null) {
            return "duplicate";
        } else {
            return edgeMode;
        }
    }

    /**
     * Sets the value of the edgeMode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEdgeMode(String value) {
        this.edgeMode = value;
    }

    /**
     * Gets the value of the kernelUnitLength property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKernelUnitLength() {
        return kernelUnitLength;
    }

    /**
     * Sets the value of the kernelUnitLength property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKernelUnitLength(String value) {
        this.kernelUnitLength = value;
    }

    /**
     * Gets the value of the preserveAlpha property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPreserveAlpha() {
        return preserveAlpha;
    }

    /**
     * Sets the value of the preserveAlpha property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPreserveAlpha(String value) {
        this.preserveAlpha = value;
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
