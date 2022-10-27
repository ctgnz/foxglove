package nz.co.ctg.foxglove;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.parser.DoubleListAdapter;
import nz.co.ctg.foxglove.parser.SvgPaintAdapter;
import nz.co.ctg.foxglove.parser.StrokeLineCapAdapter;
import nz.co.ctg.foxglove.parser.StrokeLineJoinAdapter;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractSvgStylable extends AbstractSvgElement implements ISvgStylable {

    @XmlAttribute(name = "style")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String style;

    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String className;

    @XmlAttribute(name = "flood-color")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String floodColor;

    @XmlAttribute(name = "flood-opacity")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String floodOpacity;

    @XmlAttribute(name = "lighting-color")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String lightingColor;

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
    @XmlJavaTypeAdapter(SvgPaintAdapter.class)
    protected Paint fill;

    @XmlAttribute(name = "fill-rule")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fillRule;

    @XmlAttribute(name = "stroke")
    @XmlJavaTypeAdapter(SvgPaintAdapter.class)
    protected Paint stroke;

    @XmlAttribute(name = "stroke-dasharray")
    @XmlJavaTypeAdapter(DoubleListAdapter.class)
    protected List<Double> strokeDashArray;

    @XmlAttribute(name = "stroke-dashoffset")
    protected double strokeDashOffset;

    @XmlAttribute(name = "stroke-linecap")
    @XmlJavaTypeAdapter(StrokeLineCapAdapter.class)
    protected StrokeLineCap strokeLineCap;

    @XmlAttribute(name = "stroke-linejoin")
    @XmlJavaTypeAdapter(StrokeLineJoinAdapter.class)
    protected StrokeLineJoin strokeLineJoin;

    @XmlAttribute(name = "stroke-miterlimit")
    protected double strokeMiterLimit;

    @XmlAttribute(name = "stroke-width")
    protected double strokeWidth;

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

    public AbstractSvgStylable() {
    }

    /**
     * Gets the value of the style property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
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
    @Override
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
    @Override
    public String getClassName() {
        return className;
    }

    /**
     * Sets the value of the clazz property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setClassName(String value) {
        this.className = value;
    }

    /**
     * Gets the value of the enableBackground property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public List<Double> getStrokeDashArray() {
        return strokeDashArray;
    }

    /**
     * Sets the value of the strokeDasharray property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setStrokeDashArray(List<Double> value) {
        this.strokeDashArray = value;
    }

    /**
     * Gets the value of the strokeDashoffset property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public double getStrokeDashOffset() {
        return strokeDashOffset;
    }

    /**
     * Sets the value of the strokeDashoffset property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setStrokeDashOffset(double value) {
        this.strokeDashOffset = value;
    }

    /**
     * Gets the value of the strokeLinecap property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public StrokeLineCap getStrokeLineCap() {
        return strokeLineCap;
    }

    /**
     * Sets the value of the strokeLinecap property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setStrokeLineCap(StrokeLineCap value) {
        this.strokeLineCap = value;
    }

    /**
     * Gets the value of the strokeLinejoin property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public StrokeLineJoin getStrokeLineJoin() {
        return strokeLineJoin;
    }

    /**
     * Sets the value of the strokeLinejoin property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setStrokeLineJoin(StrokeLineJoin value) {
        this.strokeLineJoin = value;
    }

    /**
     * Gets the value of the strokeMiterlimit property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public double getStrokeMiterLimit() {
        return strokeMiterLimit;
    }

    /**
     * Sets the value of the strokeMiterlimit property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setStrokeMiterLimit(double value) {
        this.strokeMiterLimit = value;
    }

    /**
     * Gets the value of the strokeWidth property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public double getStrokeWidth() {
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
    @Override
    public void setStrokeWidth(double value) {
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void setLightingColor(String value) {
        this.lightingColor = value;
    }

    @Override
    protected void buildToString(ToStringHelper builder) {
        super.buildToString(builder);
        builder.add("style", style);
        builder.add("className", className);
    }

    protected void setColors(Shape shape) {
        shape.setFill(fill);
        shape.setStroke(stroke);
    }

    protected void setStrokeProperties(Shape shape) {
        shape.setStrokeWidth(strokeWidth);
        shape.setStrokeMiterLimit(strokeMiterLimit);
        shape.setStrokeLineCap(strokeLineCap);
        shape.setStrokeLineJoin(strokeLineJoin);
        shape.setStrokeDashOffset(strokeDashOffset);
        if (strokeDashArray != null) {
            shape.getStrokeDashArray().addAll(strokeDashArray);
        }
    }

    protected void parseStyle() {
        if (StringUtils.isNotBlank(style)) {
            SvgPaintAdapter paintAdapter = new SvgPaintAdapter();
            StrokeLineCapAdapter strokeLineCapAdapter = new StrokeLineCapAdapter();
            StrokeLineJoinAdapter strokeLineJoinAdapter = new StrokeLineJoinAdapter();
            DoubleListAdapter doubleListAdapter = new DoubleListAdapter();
            Arrays.stream(StringUtils.split(style, ';')).forEach(stylePair -> {
                try {
                    String[] values = StringUtils.split(stylePair, ':');
                    String name = values[0].toLowerCase();
                    String value = values[1].toLowerCase();
                    switch (name) {
                        case "fill":
                            setFill(paintAdapter.unmarshal(value));
                            break;
                        case "fill-opacity":
                            break;
                        case "stroke":
                            setStroke(paintAdapter.unmarshal(value));
                            break;
                        case "stroke-opacity":
                            break;
                        case "stroke-width":
                            setStrokeWidth(Double.valueOf(value));
                            break;
                        case "stroke-linecap":
                            setStrokeLineCap(strokeLineCapAdapter.unmarshal(value));
                            break;
                        case "stroke-linejoin":
                            setStrokeLineJoin(strokeLineJoinAdapter.unmarshal(value));
                            break;
                        case "stroke-miterlimit":
                            setStrokeMiterLimit(Double.valueOf(value));
                            break;
                        case "stroke-dashoffset":
                            setStrokeDashOffset(Double.valueOf(value));
                            break;
                        case "stroke-dasharray":
                            setStrokeDashArray(doubleListAdapter.unmarshal(value));
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

}