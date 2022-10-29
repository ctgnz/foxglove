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
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "animations"
})
public abstract class FeCompositeFunction extends AbstractSvgElement implements ISvgFilterFunction {

    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;

    @XmlAttribute(name = "tableValues")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String tableValues;

    @XmlAttribute(name = "slope")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String slope;

    @XmlAttribute(name = "intercept")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String intercept;

    @XmlAttribute(name = "amplitude")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String amplitude;

    @XmlAttribute(name = "exponent")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String exponent;

    @XmlAttribute(name = "offset")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String offset;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<ISvgAttributeAnimation> animations;

    public FeCompositeFunction() {
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the tableValues property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getTableValues() {
        return tableValues;
    }

    /**
     * Sets the value of the tableValues property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setTableValues(String value) {
        this.tableValues = value;
    }

    /**
     * Gets the value of the slope property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getSlope() {
        return slope;
    }

    /**
     * Sets the value of the slope property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setSlope(String value) {
        this.slope = value;
    }

    /**
     * Gets the value of the intercept property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getIntercept() {
        return intercept;
    }

    /**
     * Sets the value of the intercept property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setIntercept(String value) {
        this.intercept = value;
    }

    /**
     * Gets the value of the amplitude property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getAmplitude() {
        return amplitude;
    }

    /**
     * Sets the value of the amplitude property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setAmplitude(String value) {
        this.amplitude = value;
    }

    /**
     * Gets the value of the exponent property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getExponent() {
        return exponent;
    }

    /**
     * Sets the value of the exponent property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setExponent(String value) {
        this.exponent = value;
    }

    /**
     * Gets the value of the offset property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOffset(String value) {
        this.offset = value;
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