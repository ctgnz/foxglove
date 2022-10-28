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
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "feDistantLightOrFePointLightOrFeSpotLight",
    "animateOrSetOrAnimateColor"
})
@XmlRootElement(name = "feDiffuseLighting")
public class FeDiffuseLighting extends AbstractSvgFilterPrimitive {

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

    @XmlAttribute(name = "surfaceScale")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String surfaceScale;

    @XmlAttribute(name = "diffuseConstant")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String diffuseConstant;

    @XmlAttribute(name = "kernelUnitLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String kernelUnitLength;

    @XmlElements({
        @XmlElement(name = "feDistantLight", required = true, type = FeDistantLight.class),
        @XmlElement(name = "fePointLight", required = true, type = FePointLight.class),
        @XmlElement(name = "feSpotLight", required = true, type = FeSpotLight.class)
    })
    protected List<Object> feDistantLightOrFePointLightOrFeSpotLight;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class),
        @XmlElement(name = "set", type = SvgSetAttribute.class),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class)
    })
    protected List<Object> animateOrSetOrAnimateColor;

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
     * Gets the value of the surfaceScale property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSurfaceScale() {
        return surfaceScale;
    }

    /**
     * Sets the value of the surfaceScale property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSurfaceScale(String value) {
        this.surfaceScale = value;
    }

    /**
     * Gets the value of the diffuseConstant property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDiffuseConstant() {
        return diffuseConstant;
    }

    /**
     * Sets the value of the diffuseConstant property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDiffuseConstant(String value) {
        this.diffuseConstant = value;
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
     * Gets the value of the feDistantLightOrFePointLightOrFeSpotLight property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the feDistantLightOrFePointLightOrFeSpotLight property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFeDistantLightOrFePointLightOrFeSpotLight().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FeDistantLight }
     * {@link FePointLight }
     * {@link FeSpotLight }
     *
     *
     */
    public List<Object> getFeDistantLightOrFePointLightOrFeSpotLight() {
        if (feDistantLightOrFePointLightOrFeSpotLight == null) {
            feDistantLightOrFePointLightOrFeSpotLight = new ArrayList<Object>();
        }
        return this.feDistantLightOrFePointLightOrFeSpotLight;
    }

    /**
     * Gets the value of the animateOrSetOrAnimateColor property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the animateOrSetOrAnimateColor property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnimateOrSetOrAnimateColor().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgAnimateAttribute }
     * {@link SvgSetAttribute }
     * {@link SvgAnimateColor }
     *
     *
     */
    public List<Object> getAnimateOrSetOrAnimateColor() {
        if (animateOrSetOrAnimateColor == null) {
            animateOrSetOrAnimateColor = new ArrayList<Object>();
        }
        return this.animateOrSetOrAnimateColor;
    }

}
