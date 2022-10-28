package nz.co.ctg.foxglove.filter;

import java.util.List;

import nz.co.ctg.foxglove.animate.ISvgAttributeAnimation;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;

public interface ISvgFilterFunction {

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    String getType();

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    void setType(String value);

    /**
     * Gets the value of the tableValues property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    String getTableValues();

    /**
     * Sets the value of the tableValues property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    void setTableValues(String value);

    /**
     * Gets the value of the slope property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    String getSlope();

    /**
     * Sets the value of the slope property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    void setSlope(String value);

    /**
     * Gets the value of the intercept property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    String getIntercept();

    /**
     * Sets the value of the intercept property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    void setIntercept(String value);

    /**
     * Gets the value of the amplitude property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    String getAmplitude();

    /**
     * Sets the value of the amplitude property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    void setAmplitude(String value);

    /**
     * Gets the value of the exponent property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    String getExponent();

    /**
     * Sets the value of the exponent property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    void setExponent(String value);

    /**
     * Gets the value of the offset property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    String getOffset();

    /**
     * Sets the value of the offset property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    void setOffset(String value);

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
    List<ISvgAttributeAnimation> getAnimations();

}