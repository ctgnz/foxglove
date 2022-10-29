package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

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
    "content"
})
@XmlRootElement(name = "font-face")
public class SvgFontFace extends AbstractSvgElement {

    @XmlAttribute(name = "font-family")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fontFamily;

    @XmlAttribute(name = "font-style")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fontStyle;

    @XmlAttribute(name = "font-variant")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fontVariant;

    @XmlAttribute(name = "font-weight")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fontWeight;

    @XmlAttribute(name = "font-stretch")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fontStretch;

    @XmlAttribute(name = "font-size")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fontSize;

    @XmlAttribute(name = "unicode-range")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String unicodeRange;

    @XmlAttribute(name = "units-per-em")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String unitsPerEm;

    @XmlAttribute(name = "panose-1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String panose1;

    @XmlAttribute(name = "stemv")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String stemWidthVertical;

    @XmlAttribute(name = "stemh")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String stemWidthHorizontal;

    @XmlAttribute(name = "slope")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String slope;

    @XmlAttribute(name = "cap-height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String capHeight;

    @XmlAttribute(name = "x-height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xHeight;

    @XmlAttribute(name = "accent-height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String accentHeight;

    @XmlAttribute(name = "ascent")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String ascent;

    @XmlAttribute(name = "descent")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String descent;

    @XmlAttribute(name = "widths")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String widths;

    @XmlAttribute(name = "bbox")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String boundingBox;

    @XmlAttribute(name = "ideographic")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String ideographic;

    @XmlAttribute(name = "alphabetic")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String alphabetic;

    @XmlAttribute(name = "mathematical")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String mathematical;

    @XmlAttribute(name = "hanging")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String hanging;

    @XmlAttribute(name = "v-ideographic")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String vIdeographic;

    @XmlAttribute(name = "v-alphabetic")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String vAlphabetic;

    @XmlAttribute(name = "v-mathematical")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String vMathematical;

    @XmlAttribute(name = "v-hanging")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String vHanging;

    @XmlAttribute(name = "underline-position")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String underlinePosition;

    @XmlAttribute(name = "underline-thickness")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String underlineThickness;

    @XmlAttribute(name = "strikethrough-position")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String strikethroughPosition;

    @XmlAttribute(name = "strikethrough-thickness")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String strikethroughThickness;

    @XmlAttribute(name = "overline-position")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String overlinePosition;

    @XmlAttribute(name = "overline-thickness")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String overlineThickness;

    @XmlElements({
        @XmlElement(name = "font-face-src", required = true, type = SvgFontFaceSrc.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "desc", required = true, type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", required = true, type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", required = true, type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<Object> content;

    /**
     * Gets the value of the fontFamily property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Sets the value of the fontFamily property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontFamily(String value) {
        this.fontFamily = value;
    }

    /**
     * Gets the value of the fontStyle property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontStyle() {
        return fontStyle;
    }

    /**
     * Sets the value of the fontStyle property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontStyle(String value) {
        this.fontStyle = value;
    }

    /**
     * Gets the value of the fontVariant property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontVariant() {
        return fontVariant;
    }

    /**
     * Sets the value of the fontVariant property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontVariant(String value) {
        this.fontVariant = value;
    }

    /**
     * Gets the value of the fontWeight property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontWeight() {
        return fontWeight;
    }

    /**
     * Sets the value of the fontWeight property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontWeight(String value) {
        this.fontWeight = value;
    }

    /**
     * Gets the value of the fontStretch property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontStretch() {
        return fontStretch;
    }

    /**
     * Sets the value of the fontStretch property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontStretch(String value) {
        this.fontStretch = value;
    }

    /**
     * Gets the value of the fontSize property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontSize() {
        return fontSize;
    }

    /**
     * Sets the value of the fontSize property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontSize(String value) {
        this.fontSize = value;
    }

    /**
     * Gets the value of the unicodeRange property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnicodeRange() {
        return unicodeRange;
    }

    /**
     * Sets the value of the unicodeRange property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnicodeRange(String value) {
        this.unicodeRange = value;
    }

    /**
     * Gets the value of the unitsPerEm property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnitsPerEm() {
        return unitsPerEm;
    }

    /**
     * Sets the value of the unitsPerEm property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnitsPerEm(String value) {
        this.unitsPerEm = value;
    }

    /**
     * Gets the value of the panose1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPanose1() {
        return panose1;
    }

    /**
     * Sets the value of the panose1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPanose1(String value) {
        this.panose1 = value;
    }

    /**
     * Gets the value of the stemv property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStemWidthVertical() {
        return stemWidthVertical;
    }

    /**
     * Sets the value of the stemv property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStemWidthVertical(String value) {
        this.stemWidthVertical = value;
    }

    /**
     * Gets the value of the stemh property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStemWidthHorizontal() {
        return stemWidthHorizontal;
    }

    /**
     * Sets the value of the stemh property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStemWidthHorizontal(String value) {
        this.stemWidthHorizontal = value;
    }

    /**
     * Gets the value of the slope property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
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
    public void setSlope(String value) {
        this.slope = value;
    }

    /**
     * Gets the value of the capHeight property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCapHeight() {
        return capHeight;
    }

    /**
     * Sets the value of the capHeight property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCapHeight(String value) {
        this.capHeight = value;
    }

    /**
     * Gets the value of the xHeight property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getXHeight() {
        return xHeight;
    }

    /**
     * Sets the value of the xHeight property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setXHeight(String value) {
        this.xHeight = value;
    }

    /**
     * Gets the value of the accentHeight property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccentHeight() {
        return accentHeight;
    }

    /**
     * Sets the value of the accentHeight property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccentHeight(String value) {
        this.accentHeight = value;
    }

    /**
     * Gets the value of the ascent property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAscent() {
        return ascent;
    }

    /**
     * Sets the value of the ascent property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAscent(String value) {
        this.ascent = value;
    }

    /**
     * Gets the value of the descent property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescent() {
        return descent;
    }

    /**
     * Sets the value of the descent property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescent(String value) {
        this.descent = value;
    }

    /**
     * Gets the value of the widths property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWidths() {
        return widths;
    }

    /**
     * Sets the value of the widths property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWidths(String value) {
        this.widths = value;
    }

    /**
     * Gets the value of the bbox property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the value of the bbox property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBoundingBox(String value) {
        this.boundingBox = value;
    }

    /**
     * Gets the value of the ideographic property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdeographic() {
        return ideographic;
    }

    /**
     * Sets the value of the ideographic property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdeographic(String value) {
        this.ideographic = value;
    }

    /**
     * Gets the value of the alphabetic property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAlphabetic() {
        return alphabetic;
    }

    /**
     * Sets the value of the alphabetic property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAlphabetic(String value) {
        this.alphabetic = value;
    }

    /**
     * Gets the value of the mathematical property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMathematical() {
        return mathematical;
    }

    /**
     * Sets the value of the mathematical property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMathematical(String value) {
        this.mathematical = value;
    }

    /**
     * Gets the value of the hanging property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHanging() {
        return hanging;
    }

    /**
     * Sets the value of the hanging property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHanging(String value) {
        this.hanging = value;
    }

    /**
     * Gets the value of the vIdeographic property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVIdeographic() {
        return vIdeographic;
    }

    /**
     * Sets the value of the vIdeographic property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVIdeographic(String value) {
        this.vIdeographic = value;
    }

    /**
     * Gets the value of the vAlphabetic property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVAlphabetic() {
        return vAlphabetic;
    }

    /**
     * Sets the value of the vAlphabetic property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVAlphabetic(String value) {
        this.vAlphabetic = value;
    }

    /**
     * Gets the value of the vMathematical property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVMathematical() {
        return vMathematical;
    }

    /**
     * Sets the value of the vMathematical property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVMathematical(String value) {
        this.vMathematical = value;
    }

    /**
     * Gets the value of the vHanging property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVHanging() {
        return vHanging;
    }

    /**
     * Sets the value of the vHanging property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVHanging(String value) {
        this.vHanging = value;
    }

    /**
     * Gets the value of the underlinePosition property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnderlinePosition() {
        return underlinePosition;
    }

    /**
     * Sets the value of the underlinePosition property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnderlinePosition(String value) {
        this.underlinePosition = value;
    }

    /**
     * Gets the value of the underlineThickness property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnderlineThickness() {
        return underlineThickness;
    }

    /**
     * Sets the value of the underlineThickness property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnderlineThickness(String value) {
        this.underlineThickness = value;
    }

    /**
     * Gets the value of the strikethroughPosition property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrikethroughPosition() {
        return strikethroughPosition;
    }

    /**
     * Sets the value of the strikethroughPosition property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrikethroughPosition(String value) {
        this.strikethroughPosition = value;
    }

    /**
     * Gets the value of the strikethroughThickness property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrikethroughThickness() {
        return strikethroughThickness;
    }

    /**
     * Sets the value of the strikethroughThickness property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrikethroughThickness(String value) {
        this.strikethroughThickness = value;
    }

    /**
     * Gets the value of the overlinePosition property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOverlinePosition() {
        return overlinePosition;
    }

    /**
     * Sets the value of the overlinePosition property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOverlinePosition(String value) {
        this.overlinePosition = value;
    }

    /**
     * Gets the value of the overlineThickness property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOverlineThickness() {
        return overlineThickness;
    }

    /**
     * Sets the value of the overlineThickness property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOverlineThickness(String value) {
        this.overlineThickness = value;
    }

    /**
     * Gets the value of the content property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgFontFaceSrc }
     * {@link SvgDescription }
     * {@link SvgTitle }
     * {@link SvgMetadata }
     *
     *
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

}
