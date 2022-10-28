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
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
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
@XmlRootElement(name = "feTurbulence")
public class FeTurbulence extends AbstractSvgFilterPrimitive {

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
    @XmlAttribute(name = "baseFrequency")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String baseFrequency;
    @XmlAttribute(name = "numOctaves")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String numOctaves;
    @XmlAttribute(name = "seed")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String seed;
    @XmlAttribute(name = "stitchTiles")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String stitchTiles;
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
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
     * Gets the value of the baseFrequency property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBaseFrequency() {
        return baseFrequency;
    }

    /**
     * Sets the value of the baseFrequency property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBaseFrequency(String value) {
        this.baseFrequency = value;
    }

    /**
     * Gets the value of the numOctaves property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNumOctaves() {
        return numOctaves;
    }

    /**
     * Sets the value of the numOctaves property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNumOctaves(String value) {
        this.numOctaves = value;
    }

    /**
     * Gets the value of the seed property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSeed() {
        return seed;
    }

    /**
     * Sets the value of the seed property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSeed(String value) {
        this.seed = value;
    }

    /**
     * Gets the value of the stitchTiles property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStitchTiles() {
        if (stitchTiles == null) {
            return "noStitch";
        } else {
            return stitchTiles;
        }
    }

    /**
     * Sets the value of the stitchTiles property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStitchTiles(String value) {
        this.stitchTiles = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        if (type == null) {
            return "turbulence";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
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
