//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.03.25 at 03:40:09 PM NZDT 
//


package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fontFaceSrcOrDescOrTitleOrMetadata"
})
@XmlRootElement(name = "font-face")
public class FontFace {

    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAttribute(name = "xml:base")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xmlBase;
    @XmlAttribute(name = "xml:lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xmlLang;
    @XmlAttribute(name = "xml:space")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xmlSpace;
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
    protected String stemv;
    @XmlAttribute(name = "stemh")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String stemh;
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
    protected String bbox;
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
        @XmlElement(name = "font-face-src", required = true, type = FontFaceSrc.class),
        @XmlElement(name = "desc", required = true, type = SvgDescription.class),
        @XmlElement(name = "title", required = true, type = SvgTitle.class),
        @XmlElement(name = "metadata", required = true, type = SvgMetadata.class)
    })
    protected List<Object> fontFaceSrcOrDescOrTitleOrMetadata;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the xmlBase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlBase() {
        return xmlBase;
    }

    /**
     * Sets the value of the xmlBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlBase(String value) {
        this.xmlBase = value;
    }

    /**
     * Gets the value of the xmlLang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlLang() {
        return xmlLang;
    }

    /**
     * Sets the value of the xmlLang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlLang(String value) {
        this.xmlLang = value;
    }

    /**
     * Gets the value of the xmlSpace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlSpace() {
        return xmlSpace;
    }

    /**
     * Sets the value of the xmlSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlSpace(String value) {
        this.xmlSpace = value;
    }

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
    public String getStemv() {
        return stemv;
    }

    /**
     * Sets the value of the stemv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStemv(String value) {
        this.stemv = value;
    }

    /**
     * Gets the value of the stemh property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStemh() {
        return stemh;
    }

    /**
     * Sets the value of the stemh property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStemh(String value) {
        this.stemh = value;
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
    public String getBbox() {
        return bbox;
    }

    /**
     * Sets the value of the bbox property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBbox(String value) {
        this.bbox = value;
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
     * Gets the value of the fontFaceSrcOrDescOrTitleOrMetadata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fontFaceSrcOrDescOrTitleOrMetadata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFontFaceSrcOrDescOrTitleOrMetadata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FontFaceSrc }
     * {@link SvgDescription }
     * {@link SvgTitle }
     * {@link SvgMetadata }
     * 
     * 
     */
    public List<Object> getFontFaceSrcOrDescOrTitleOrMetadata() {
        if (fontFaceSrcOrDescOrTitleOrMetadata == null) {
            fontFaceSrcOrDescOrTitleOrMetadata = new ArrayList<Object>();
        }
        return this.fontFaceSrcOrDescOrTitleOrMetadata;
    }

}
