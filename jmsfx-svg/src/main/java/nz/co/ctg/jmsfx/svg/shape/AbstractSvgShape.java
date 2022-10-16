package nz.co.ctg.jmsfx.svg.shape;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.jmsfx.svg.adapter.FXPaintAdapter;
import nz.co.ctg.jmsfx.svg.animate.Animate;
import nz.co.ctg.jmsfx.svg.animate.AnimateColor;
import nz.co.ctg.jmsfx.svg.animate.AnimateMotion;
import nz.co.ctg.jmsfx.svg.animate.AnimateTransform;
import nz.co.ctg.jmsfx.svg.animate.Set;
import nz.co.ctg.jmsfx.svg.document.Desc;
import nz.co.ctg.jmsfx.svg.document.Metadata;
import nz.co.ctg.jmsfx.svg.document.Title;

import javafx.scene.paint.Paint;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "elements"
})
public class AbstractSvgShape {

    protected static final int maxLen = 10;

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

    @XmlAttribute(name = "requiredFeatures")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredFeatures;

    @XmlAttribute(name = "requiredExtensions")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredExtensions;

    @XmlAttribute(name = "systemLanguage")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String systemLanguage;

    @XmlAttribute(name = "style")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String style;

    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String clazz;

    @XmlAttribute(name = "enable-background")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String enableBackground;

    @XmlAttribute(name = "clip")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String clip;

    @XmlAttribute(name = "overflow")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String overflow;

    @XmlAttribute(name = "writing-mode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String writingMode;

    @XmlAttribute(name = "alignment-baseline")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String alignmentBaseline;

    @XmlAttribute(name = "baseline-shift")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String baselineShift;

    @XmlAttribute(name = "direction")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String direction;

    @XmlAttribute(name = "dominant-baseline")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String dominantBaseline;

    @XmlAttribute(name = "glyph-orientation-horizontal")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String glyphOrientationHorizontal;

    @XmlAttribute(name = "glyph-orientation-vertical")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String glyphOrientationVertical;

    @XmlAttribute(name = "kerning")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String kerning;

    @XmlAttribute(name = "letter-spacing")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String letterSpacing;

    @XmlAttribute(name = "text-anchor")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String textAnchor;

    @XmlAttribute(name = "text-decoration")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String textDecoration;

    @XmlAttribute(name = "unicode-bidi")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String unicodeBidi;

    @XmlAttribute(name = "word-spacing")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String wordSpacing;

    @XmlAttribute(name = "font-family")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fontFamily;

    @XmlAttribute(name = "font-size")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fontSize;

    @XmlAttribute(name = "font-size-adjust")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fontSizeAdjust;

    @XmlAttribute(name = "font-stretch")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fontStretch;

    @XmlAttribute(name = "font-style")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fontStyle;

    @XmlAttribute(name = "font-variant")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fontVariant;

    @XmlAttribute(name = "font-weight")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fontWeight;

    @XmlAttribute(name = "fill")
    @XmlJavaTypeAdapter(FXPaintAdapter.class)
    protected Paint fill;

    @XmlAttribute(name = "fill-rule")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fillRule;

    @XmlAttribute(name = "stroke")
    @XmlJavaTypeAdapter(FXPaintAdapter.class)
    protected Paint stroke;

    @XmlAttribute(name = "stroke-dasharray")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String strokeDasharray;

    @XmlAttribute(name = "stroke-dashoffset")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String strokeDashoffset;

    @XmlAttribute(name = "stroke-linecap")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String strokeLinecap;

    @XmlAttribute(name = "stroke-linejoin")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String strokeLinejoin;

    @XmlAttribute(name = "stroke-miterlimit")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String strokeMiterlimit;

    @XmlAttribute(name = "stroke-width")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String strokeWidth;

    @XmlAttribute(name = "color")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String color;

    @XmlAttribute(name = "color-interpolation")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String colorInterpolation;

    @XmlAttribute(name = "color-rendering")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String colorRendering;

    @XmlAttribute(name = "opacity")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String opacity;

    @XmlAttribute(name = "fill-opacity")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fillOpacity;

    @XmlAttribute(name = "stroke-opacity")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String strokeOpacity;

    @XmlAttribute(name = "display")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String display;

    @XmlAttribute(name = "image-rendering")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String imageRendering;

    @XmlAttribute(name = "pointer-events")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pointerEvents;

    @XmlAttribute(name = "shape-rendering")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String shapeRendering;

    @XmlAttribute(name = "text-rendering")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String textRendering;

    @XmlAttribute(name = "visibility")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String visibility;

    @XmlAttribute(name = "marker-start")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String markerStart;

    @XmlAttribute(name = "marker-mid")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String markerMid;

    @XmlAttribute(name = "marker-end")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String markerEnd;

    @XmlAttribute(name = "color-profile")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String colorProfile;

    @XmlAttribute(name = "stop-color")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String stopColor;

    @XmlAttribute(name = "stop-opacity")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String stopOpacity;

    @XmlAttribute(name = "clip-path")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String clipPath;

    @XmlAttribute(name = "clip-rule")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String clipRule;

    @XmlAttribute(name = "mask")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String mask;

    @XmlAttribute(name = "filter")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String filter;

    @XmlAttribute(name = "color-interpolation-filters")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String colorInterpolationFilters;

    @XmlAttribute(name = "cursor")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String cursor;

    @XmlAttribute(name = "flood-color")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String floodColor;

    @XmlAttribute(name = "flood-opacity")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String floodOpacity;

    @XmlAttribute(name = "lighting-color")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String lightingColor;

    @XmlAttribute(name = "onfocusin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onfocusin;

    @XmlAttribute(name = "onfocusout")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onfocusout;

    @XmlAttribute(name = "onactivate")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onactivate;

    @XmlAttribute(name = "onclick")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onclick;

    @XmlAttribute(name = "onmousedown")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmousedown;

    @XmlAttribute(name = "onmouseup")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmouseup;

    @XmlAttribute(name = "onmouseover")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmouseover;

    @XmlAttribute(name = "onmousemove")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmousemove;

    @XmlAttribute(name = "onmouseout")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmouseout;

    @XmlAttribute(name = "onload")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onload;

    @XmlAttribute(name = "externalResourcesRequired")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String externalResourcesRequired;

    @XmlAttribute(name = "transform")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String transform;

    @XmlElements({
        @XmlElement(name = "desc", type = Desc.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = Title.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = Metadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = Animate.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = Set.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateMotion", type = AnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = AnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = AnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<Object> elements;

    public AbstractSvgShape() {
    }

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
     * Gets the value of the requiredFeatures property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRequiredFeatures() {
        return requiredFeatures;
    }

    /**
     * Sets the value of the requiredFeatures property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRequiredFeatures(String value) {
        this.requiredFeatures = value;
    }

    /**
     * Gets the value of the requiredExtensions property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRequiredExtensions() {
        return requiredExtensions;
    }

    /**
     * Sets the value of the requiredExtensions property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRequiredExtensions(String value) {
        this.requiredExtensions = value;
    }

    /**
     * Gets the value of the systemLanguage property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSystemLanguage() {
        return systemLanguage;
    }

    /**
     * Sets the value of the systemLanguage property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSystemLanguage(String value) {
        this.systemLanguage = value;
    }

    /**
     * Gets the value of the style property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStyle(String value) {
        this.style = value;
    }

    /**
     * Gets the value of the clazz property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the enableBackground property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEnableBackground() {
        return enableBackground;
    }

    /**
     * Sets the value of the enableBackground property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEnableBackground(String value) {
        this.enableBackground = value;
    }

    /**
     * Gets the value of the clip property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClip() {
        return clip;
    }

    /**
     * Sets the value of the clip property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClip(String value) {
        this.clip = value;
    }

    /**
     * Gets the value of the overflow property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOverflow() {
        return overflow;
    }

    /**
     * Sets the value of the overflow property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOverflow(String value) {
        this.overflow = value;
    }

    /**
     * Gets the value of the writingMode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWritingMode() {
        return writingMode;
    }

    /**
     * Sets the value of the writingMode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWritingMode(String value) {
        this.writingMode = value;
    }

    /**
     * Gets the value of the alignmentBaseline property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAlignmentBaseline() {
        return alignmentBaseline;
    }

    /**
     * Sets the value of the alignmentBaseline property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAlignmentBaseline(String value) {
        this.alignmentBaseline = value;
    }

    /**
     * Gets the value of the baselineShift property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBaselineShift() {
        return baselineShift;
    }

    /**
     * Sets the value of the baselineShift property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBaselineShift(String value) {
        this.baselineShift = value;
    }

    /**
     * Gets the value of the direction property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Sets the value of the direction property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDirection(String value) {
        this.direction = value;
    }

    /**
     * Gets the value of the dominantBaseline property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDominantBaseline() {
        return dominantBaseline;
    }

    /**
     * Sets the value of the dominantBaseline property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDominantBaseline(String value) {
        this.dominantBaseline = value;
    }

    /**
     * Gets the value of the glyphOrientationHorizontal property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGlyphOrientationHorizontal() {
        return glyphOrientationHorizontal;
    }

    /**
     * Sets the value of the glyphOrientationHorizontal property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGlyphOrientationHorizontal(String value) {
        this.glyphOrientationHorizontal = value;
    }

    /**
     * Gets the value of the glyphOrientationVertical property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGlyphOrientationVertical() {
        return glyphOrientationVertical;
    }

    /**
     * Sets the value of the glyphOrientationVertical property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGlyphOrientationVertical(String value) {
        this.glyphOrientationVertical = value;
    }

    /**
     * Gets the value of the kerning property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKerning() {
        return kerning;
    }

    /**
     * Sets the value of the kerning property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKerning(String value) {
        this.kerning = value;
    }

    /**
     * Gets the value of the letterSpacing property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLetterSpacing() {
        return letterSpacing;
    }

    /**
     * Sets the value of the letterSpacing property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLetterSpacing(String value) {
        this.letterSpacing = value;
    }

    /**
     * Gets the value of the textAnchor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTextAnchor() {
        return textAnchor;
    }

    /**
     * Sets the value of the textAnchor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTextAnchor(String value) {
        this.textAnchor = value;
    }

    /**
     * Gets the value of the textDecoration property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTextDecoration() {
        return textDecoration;
    }

    /**
     * Sets the value of the textDecoration property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTextDecoration(String value) {
        this.textDecoration = value;
    }

    /**
     * Gets the value of the unicodeBidi property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnicodeBidi() {
        return unicodeBidi;
    }

    /**
     * Sets the value of the unicodeBidi property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnicodeBidi(String value) {
        this.unicodeBidi = value;
    }

    /**
     * Gets the value of the wordSpacing property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWordSpacing() {
        return wordSpacing;
    }

    /**
     * Sets the value of the wordSpacing property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWordSpacing(String value) {
        this.wordSpacing = value;
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
     * Gets the value of the fontSizeAdjust property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFontSizeAdjust() {
        return fontSizeAdjust;
    }

    /**
     * Sets the value of the fontSizeAdjust property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFontSizeAdjust(String value) {
        this.fontSizeAdjust = value;
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
     * Gets the value of the fill property.
     *
     * @return
     *     possible object is
     *     {@link Paint }
     *
     */
    public Paint getFill() {
        return fill;
    }

    /**
     * Sets the value of the fill property.
     *
     * @param value
     *     allowed object is
     *     {@link Paint }
     *
     */
    public void setFill(Paint value) {
        this.fill = value;
    }

    /**
     * Gets the value of the fillRule property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFillRule() {
        return fillRule;
    }

    /**
     * Sets the value of the fillRule property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFillRule(String value) {
        this.fillRule = value;
    }

    /**
     * Gets the value of the stroke property.
     *
     * @return
     *     possible object is
     *     {@link Paint }
     *
     */
    public Paint getStroke() {
        return stroke;
    }

    /**
     * Sets the value of the stroke property.
     *
     * @param value
     *     allowed object is
     *     {@link Paint }
     *
     */
    public void setStroke(Paint value) {
        this.stroke = value;
    }

    /**
     * Gets the value of the strokeDasharray property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrokeDasharray() {
        return strokeDasharray;
    }

    /**
     * Sets the value of the strokeDasharray property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrokeDasharray(String value) {
        this.strokeDasharray = value;
    }

    /**
     * Gets the value of the strokeDashoffset property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrokeDashoffset() {
        return strokeDashoffset;
    }

    /**
     * Sets the value of the strokeDashoffset property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrokeDashoffset(String value) {
        this.strokeDashoffset = value;
    }

    /**
     * Gets the value of the strokeLinecap property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrokeLinecap() {
        return strokeLinecap;
    }

    /**
     * Sets the value of the strokeLinecap property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrokeLinecap(String value) {
        this.strokeLinecap = value;
    }

    /**
     * Gets the value of the strokeLinejoin property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrokeLinejoin() {
        return strokeLinejoin;
    }

    /**
     * Sets the value of the strokeLinejoin property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrokeLinejoin(String value) {
        this.strokeLinejoin = value;
    }

    /**
     * Gets the value of the strokeMiterlimit property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrokeMiterlimit() {
        return strokeMiterlimit;
    }

    /**
     * Sets the value of the strokeMiterlimit property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrokeMiterlimit(String value) {
        this.strokeMiterlimit = value;
    }

    /**
     * Gets the value of the strokeWidth property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * Sets the value of the strokeWidth property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrokeWidth(String value) {
        this.strokeWidth = value;
    }

    /**
     * Gets the value of the color property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColor(String value) {
        this.color = value;
    }

    /**
     * Gets the value of the colorInterpolation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColorInterpolation() {
        return colorInterpolation;
    }

    /**
     * Sets the value of the colorInterpolation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColorInterpolation(String value) {
        this.colorInterpolation = value;
    }

    /**
     * Gets the value of the colorRendering property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColorRendering() {
        return colorRendering;
    }

    /**
     * Sets the value of the colorRendering property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColorRendering(String value) {
        this.colorRendering = value;
    }

    /**
     * Gets the value of the opacity property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOpacity() {
        return opacity;
    }

    /**
     * Sets the value of the opacity property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOpacity(String value) {
        this.opacity = value;
    }

    /**
     * Gets the value of the fillOpacity property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFillOpacity() {
        return fillOpacity;
    }

    /**
     * Sets the value of the fillOpacity property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFillOpacity(String value) {
        this.fillOpacity = value;
    }

    /**
     * Gets the value of the strokeOpacity property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrokeOpacity() {
        return strokeOpacity;
    }

    /**
     * Sets the value of the strokeOpacity property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrokeOpacity(String value) {
        this.strokeOpacity = value;
    }

    /**
     * Gets the value of the display property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Sets the value of the display property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDisplay(String value) {
        this.display = value;
    }

    /**
     * Gets the value of the imageRendering property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getImageRendering() {
        return imageRendering;
    }

    /**
     * Sets the value of the imageRendering property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setImageRendering(String value) {
        this.imageRendering = value;
    }

    /**
     * Gets the value of the pointerEvents property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPointerEvents() {
        return pointerEvents;
    }

    /**
     * Sets the value of the pointerEvents property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPointerEvents(String value) {
        this.pointerEvents = value;
    }

    /**
     * Gets the value of the shapeRendering property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getShapeRendering() {
        return shapeRendering;
    }

    /**
     * Sets the value of the shapeRendering property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setShapeRendering(String value) {
        this.shapeRendering = value;
    }

    /**
     * Gets the value of the textRendering property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTextRendering() {
        return textRendering;
    }

    /**
     * Sets the value of the textRendering property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTextRendering(String value) {
        this.textRendering = value;
    }

    /**
     * Gets the value of the visibility property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets the value of the visibility property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVisibility(String value) {
        this.visibility = value;
    }

    /**
     * Gets the value of the markerStart property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMarkerStart() {
        return markerStart;
    }

    /**
     * Sets the value of the markerStart property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMarkerStart(String value) {
        this.markerStart = value;
    }

    /**
     * Gets the value of the markerMid property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMarkerMid() {
        return markerMid;
    }

    /**
     * Sets the value of the markerMid property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMarkerMid(String value) {
        this.markerMid = value;
    }

    /**
     * Gets the value of the markerEnd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMarkerEnd() {
        return markerEnd;
    }

    /**
     * Sets the value of the markerEnd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMarkerEnd(String value) {
        this.markerEnd = value;
    }

    /**
     * Gets the value of the colorProfile property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColorProfile() {
        return colorProfile;
    }

    /**
     * Sets the value of the colorProfile property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColorProfile(String value) {
        this.colorProfile = value;
    }

    /**
     * Gets the value of the stopColor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStopColor() {
        return stopColor;
    }

    /**
     * Sets the value of the stopColor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStopColor(String value) {
        this.stopColor = value;
    }

    /**
     * Gets the value of the stopOpacity property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStopOpacity() {
        return stopOpacity;
    }

    /**
     * Sets the value of the stopOpacity property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStopOpacity(String value) {
        this.stopOpacity = value;
    }

    /**
     * Gets the value of the clipPath property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClipPath() {
        return clipPath;
    }

    /**
     * Sets the value of the clipPath property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClipPath(String value) {
        this.clipPath = value;
    }

    /**
     * Gets the value of the clipRule property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClipRule() {
        return clipRule;
    }

    /**
     * Sets the value of the clipRule property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClipRule(String value) {
        this.clipRule = value;
    }

    /**
     * Gets the value of the mask property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMask() {
        return mask;
    }

    /**
     * Sets the value of the mask property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMask(String value) {
        this.mask = value;
    }

    /**
     * Gets the value of the filter property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFilter(String value) {
        this.filter = value;
    }

    /**
     * Gets the value of the colorInterpolationFilters property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColorInterpolationFilters() {
        return colorInterpolationFilters;
    }

    /**
     * Sets the value of the colorInterpolationFilters property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColorInterpolationFilters(String value) {
        this.colorInterpolationFilters = value;
    }

    /**
     * Gets the value of the cursor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCursor() {
        return cursor;
    }

    /**
     * Sets the value of the cursor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCursor(String value) {
        this.cursor = value;
    }

    /**
     * Gets the value of the floodColor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFloodColor() {
        return floodColor;
    }

    /**
     * Sets the value of the floodColor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFloodColor(String value) {
        this.floodColor = value;
    }

    /**
     * Gets the value of the floodOpacity property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFloodOpacity() {
        return floodOpacity;
    }

    /**
     * Sets the value of the floodOpacity property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFloodOpacity(String value) {
        this.floodOpacity = value;
    }

    /**
     * Gets the value of the lightingColor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLightingColor() {
        return lightingColor;
    }

    /**
     * Sets the value of the lightingColor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLightingColor(String value) {
        this.lightingColor = value;
    }

    /**
     * Gets the value of the onfocusin property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnfocusin() {
        return onfocusin;
    }

    /**
     * Sets the value of the onfocusin property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnfocusin(String value) {
        this.onfocusin = value;
    }

    /**
     * Gets the value of the onfocusout property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnfocusout() {
        return onfocusout;
    }

    /**
     * Sets the value of the onfocusout property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnfocusout(String value) {
        this.onfocusout = value;
    }

    /**
     * Gets the value of the onactivate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnactivate() {
        return onactivate;
    }

    /**
     * Sets the value of the onactivate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnactivate(String value) {
        this.onactivate = value;
    }

    /**
     * Gets the value of the onclick property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnclick() {
        return onclick;
    }

    /**
     * Sets the value of the onclick property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnclick(String value) {
        this.onclick = value;
    }

    /**
     * Gets the value of the onmousedown property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnmousedown() {
        return onmousedown;
    }

    /**
     * Sets the value of the onmousedown property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnmousedown(String value) {
        this.onmousedown = value;
    }

    /**
     * Gets the value of the onmouseup property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnmouseup() {
        return onmouseup;
    }

    /**
     * Sets the value of the onmouseup property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnmouseup(String value) {
        this.onmouseup = value;
    }

    /**
     * Gets the value of the onmouseover property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnmouseover() {
        return onmouseover;
    }

    /**
     * Sets the value of the onmouseover property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnmouseover(String value) {
        this.onmouseover = value;
    }

    /**
     * Gets the value of the onmousemove property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnmousemove() {
        return onmousemove;
    }

    /**
     * Sets the value of the onmousemove property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnmousemove(String value) {
        this.onmousemove = value;
    }

    /**
     * Gets the value of the onmouseout property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnmouseout() {
        return onmouseout;
    }

    /**
     * Sets the value of the onmouseout property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnmouseout(String value) {
        this.onmouseout = value;
    }

    /**
     * Gets the value of the onload property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnload() {
        return onload;
    }

    /**
     * Sets the value of the onload property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnload(String value) {
        this.onload = value;
    }

    /**
     * Gets the value of the externalResourcesRequired property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExternalResourcesRequired() {
        return externalResourcesRequired;
    }

    /**
     * Sets the value of the externalResourcesRequired property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExternalResourcesRequired(String value) {
        this.externalResourcesRequired = value;
    }

    /**
     * Gets the value of the transform property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTransform() {
        return transform;
    }

    /**
     * Sets the value of the transform property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransform(String value) {
        this.transform = value;
    }

    /**
     * Gets the value of the descOrTitleOrMetadataOrAnimateOrSetOrAnimateMotionOrAnimateColorOrAnimateTransform property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the descOrTitleOrMetadataOrAnimateOrSetOrAnimateMotionOrAnimateColorOrAnimateTransform property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescOrTitleOrMetadataOrAnimateOrSetOrAnimateMotionOrAnimateColorOrAnimateTransform().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Desc }
     * {@link Title }
     * {@link Metadata }
     * {@link Animate }
     * {@link Set }
     * {@link AnimateMotion }
     * {@link AnimateColor }
     * {@link AnimateTransform }
     *
     *
     */
    public List<Object> getElements() {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        return this.elements;
    }

    @Override
    public String toString() {
        ToStringHelper builder = MoreObjects.toStringHelper(this).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    protected void toStringDetail(ToStringHelper builder) {
        builder.add("id", id);
        builder.add("xmlBase", xmlBase);
        builder.add("xmlLang", xmlLang);
        builder.add("xmlSpace", xmlSpace);
        builder.add("requiredFeatures", requiredFeatures);
        builder.add("requiredExtensions", requiredExtensions);
        builder.add("systemLanguage", systemLanguage);
        builder.add("style", style);
        builder.add("clazz", clazz);
        builder.add("enableBackground", enableBackground);
        builder.add("clip", clip);
        builder.add("overflow", overflow);
        builder.add("writingMode", writingMode);
        builder.add("alignmentBaseline", alignmentBaseline);
        builder.add("baselineShift", baselineShift);
        builder.add("direction", direction);
        builder.add("dominantBaseline", dominantBaseline);
        builder.add("glyphOrientationHorizontal", glyphOrientationHorizontal);
        builder.add("glyphOrientationVertical", glyphOrientationVertical);
        builder.add("kerning", kerning);
        builder.add("letterSpacing", letterSpacing);
        builder.add("textAnchor", textAnchor);
        builder.add("textDecoration", textDecoration);
        builder.add("unicodeBidi", unicodeBidi);
        builder.add("wordSpacing", wordSpacing);
        builder.add("fontFamily", fontFamily);
        builder.add("fontSize", fontSize);
        builder.add("fontSizeAdjust", fontSizeAdjust);
        builder.add("fontStretch", fontStretch);
        builder.add("fontStyle", fontStyle);
        builder.add("fontVariant", fontVariant);
        builder.add("fontWeight", fontWeight);
        builder.add("fill", fill);
        builder.add("fillRule", fillRule);
        builder.add("stroke", stroke);
        builder.add("strokeDasharray", strokeDasharray);
        builder.add("strokeDashoffset", strokeDashoffset);
        builder.add("strokeLinecap", strokeLinecap);
        builder.add("strokeLinejoin", strokeLinejoin);
        builder.add("strokeMiterlimit", strokeMiterlimit);
        builder.add("strokeWidth", strokeWidth);
        builder.add("color", color);
        builder.add("colorInterpolation", colorInterpolation);
        builder.add("colorRendering", colorRendering);
        builder.add("opacity", opacity);
        builder.add("fillOpacity", fillOpacity);
        builder.add("strokeOpacity", strokeOpacity);
        builder.add("display", display);
        builder.add("imageRendering", imageRendering);
        builder.add("pointerEvents", pointerEvents);
        builder.add("shapeRendering", shapeRendering);
        builder.add("textRendering", textRendering);
        builder.add("visibility", visibility);
        builder.add("markerStart", markerStart);
        builder.add("markerMid", markerMid);
        builder.add("markerEnd", markerEnd);
        builder.add("colorProfile", colorProfile);
        builder.add("stopColor", stopColor);
        builder.add("stopOpacity", stopOpacity);
        builder.add("clipPath", clipPath);
        builder.add("clipRule", clipRule);
        builder.add("mask", mask);
        builder.add("filter", filter);
        builder.add("colorInterpolationFilters", colorInterpolationFilters);
        builder.add("cursor", cursor);
        builder.add("floodColor", floodColor);
        builder.add("floodOpacity", floodOpacity);
        builder.add("lightingColor", lightingColor);
        builder.add("onfocusin", onfocusin);
        builder.add("onfocusout", onfocusout);
        builder.add("onactivate", onactivate);
        builder.add("onclick", onclick);
        builder.add("onmousedown", onmousedown);
        builder.add("onmouseup", onmouseup);
        builder.add("onmouseover", onmouseover);
        builder.add("onmousemove", onmousemove);
        builder.add("onmouseout", onmouseout);
        builder.add("onload", onload);
        builder.add("externalResourcesRequired", externalResourcesRequired);
        builder.add("transform", transform);
        if (elements != null) {
            builder.add("elements", elements.subList(0, Math.min(elements.size(), maxLen)));
        }
    }


}