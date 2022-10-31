package nz.co.ctg.foxglove;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.parser.DoubleListAdapter;
import nz.co.ctg.foxglove.parser.StrokeLineCapAdapter;
import nz.co.ctg.foxglove.parser.StrokeLineJoinAdapter;
import nz.co.ctg.foxglove.parser.SvgPaintAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String value) {
        this.style = value;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String value) {
        this.className = value;
    }

    @Override
    public String getEnableBackground() {
        return enableBackground;
    }

    @Override
    public void setEnableBackground(String value) {
        this.enableBackground = value;
    }

    @Override
    public String getClip() {
        return clip;
    }

    @Override
    public void setClip(String value) {
        this.clip = value;
    }

    @Override
    public String getOverflow() {
        return overflow;
    }

    @Override
    public void setOverflow(String value) {
        this.overflow = value;
    }

    @Override
    public String getWritingMode() {
        return writingMode;
    }

    @Override
    public void setWritingMode(String value) {
        this.writingMode = value;
    }

    @Override
    public String getAlignmentBaseline() {
        return alignmentBaseline;
    }

    @Override
    public void setAlignmentBaseline(String value) {
        this.alignmentBaseline = value;
    }

    @Override
    public String getBaselineShift() {
        return baselineShift;
    }

    @Override
    public void setBaselineShift(String value) {
        this.baselineShift = value;
    }

    @Override
    public String getDirection() {
        return direction;
    }

    @Override
    public void setDirection(String value) {
        this.direction = value;
    }

    @Override
    public String getDominantBaseline() {
        return dominantBaseline;
    }

    @Override
    public void setDominantBaseline(String value) {
        this.dominantBaseline = value;
    }

    @Override
    public String getGlyphOrientationHorizontal() {
        return glyphOrientationHorizontal;
    }

    @Override
    public void setGlyphOrientationHorizontal(String value) {
        this.glyphOrientationHorizontal = value;
    }

    @Override
    public String getGlyphOrientationVertical() {
        return glyphOrientationVertical;
    }

    @Override
    public void setGlyphOrientationVertical(String value) {
        this.glyphOrientationVertical = value;
    }

    @Override
    public String getKerning() {
        return kerning;
    }

    @Override
    public void setKerning(String value) {
        this.kerning = value;
    }

    @Override
    public String getLetterSpacing() {
        return letterSpacing;
    }

    @Override
    public void setLetterSpacing(String value) {
        this.letterSpacing = value;
    }

    @Override
    public String getTextAnchor() {
        return textAnchor;
    }

    @Override
    public void setTextAnchor(String value) {
        this.textAnchor = value;
    }

    @Override
    public String getTextDecoration() {
        return textDecoration;
    }

    @Override
    public void setTextDecoration(String value) {
        this.textDecoration = value;
    }

    @Override
    public String getUnicodeBidi() {
        return unicodeBidi;
    }

    @Override
    public void setUnicodeBidi(String value) {
        this.unicodeBidi = value;
    }

    @Override
    public String getWordSpacing() {
        return wordSpacing;
    }

    @Override
    public void setWordSpacing(String value) {
        this.wordSpacing = value;
    }

    @Override
    public String getFontFamily() {
        return fontFamily;
    }

    @Override
    public void setFontFamily(String value) {
        this.fontFamily = value;
    }

    @Override
    public String getFontSize() {
        return fontSize;
    }

    @Override
    public void setFontSize(String value) {
        this.fontSize = value;
    }

    @Override
    public String getFontSizeAdjust() {
        return fontSizeAdjust;
    }

    @Override
    public void setFontSizeAdjust(String value) {
        this.fontSizeAdjust = value;
    }

    @Override
    public String getFontStretch() {
        return fontStretch;
    }

    @Override
    public void setFontStretch(String value) {
        this.fontStretch = value;
    }

    @Override
    public String getFontStyle() {
        return fontStyle;
    }

    @Override
    public void setFontStyle(String value) {
        this.fontStyle = value;
    }

    @Override
    public String getFontVariant() {
        return fontVariant;
    }

    @Override
    public void setFontVariant(String value) {
        this.fontVariant = value;
    }

    @Override
    public String getFontWeight() {
        return fontWeight;
    }

    @Override
    public void setFontWeight(String value) {
        this.fontWeight = value;
    }

    @Override
    public Paint getFill() {
        return fill;
    }

    @Override
    public void setFill(Paint value) {
        this.fill = value;
    }

    @Override
    public String getFillRule() {
        return fillRule;
    }

    @Override
    public void setFillRule(String value) {
        this.fillRule = value;
    }

    @Override
    public Paint getStroke() {
        return stroke;
    }

    @Override
    public void setStroke(Paint value) {
        this.stroke = value;
    }

    @Override
    public List<Double> getStrokeDashArray() {
        return strokeDashArray;
    }

    @Override
    public void setStrokeDashArray(List<Double> value) {
        this.strokeDashArray = value;
    }

    @Override
    public double getStrokeDashOffset() {
        return strokeDashOffset;
    }

    @Override
    public void setStrokeDashOffset(double value) {
        this.strokeDashOffset = value;
    }

    @Override
    public StrokeLineCap getStrokeLineCap() {
        return strokeLineCap;
    }

    @Override
    public void setStrokeLineCap(StrokeLineCap value) {
        this.strokeLineCap = value;
    }

    @Override
    public StrokeLineJoin getStrokeLineJoin() {
        return strokeLineJoin;
    }

    @Override
    public void setStrokeLineJoin(StrokeLineJoin value) {
        this.strokeLineJoin = value;
    }

    @Override
    public double getStrokeMiterLimit() {
        return strokeMiterLimit;
    }

    @Override
    public void setStrokeMiterLimit(double value) {
        this.strokeMiterLimit = value;
    }

    @Override
    public double getStrokeWidth() {
        return strokeWidth;
    }

    @Override
    public void setStrokeWidth(double value) {
        this.strokeWidth = value;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void setColor(String value) {
        this.color = value;
    }

    @Override
    public String getColorInterpolation() {
        return colorInterpolation;
    }

    @Override
    public void setColorInterpolation(String value) {
        this.colorInterpolation = value;
    }

    @Override
    public String getColorRendering() {
        return colorRendering;
    }

    @Override
    public void setColorRendering(String value) {
        this.colorRendering = value;
    }

    @Override
    public String getOpacity() {
        return opacity;
    }

    @Override
    public void setOpacity(String value) {
        this.opacity = value;
    }

    @Override
    public String getFillOpacity() {
        return fillOpacity;
    }

    @Override
    public void setFillOpacity(String value) {
        this.fillOpacity = value;
    }

    @Override
    public String getStrokeOpacity() {
        return strokeOpacity;
    }

    @Override
    public void setStrokeOpacity(String value) {
        this.strokeOpacity = value;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(String value) {
        this.display = value;
    }

    @Override
    public String getImageRendering() {
        return imageRendering;
    }

    @Override
    public void setImageRendering(String value) {
        this.imageRendering = value;
    }

    @Override
    public String getPointerEvents() {
        return pointerEvents;
    }

    @Override
    public void setPointerEvents(String value) {
        this.pointerEvents = value;
    }

    @Override
    public String getShapeRendering() {
        return shapeRendering;
    }

    @Override
    public void setShapeRendering(String value) {
        this.shapeRendering = value;
    }

    @Override
    public String getTextRendering() {
        return textRendering;
    }

    @Override
    public void setTextRendering(String value) {
        this.textRendering = value;
    }

    @Override
    public String getVisibility() {
        return visibility;
    }

    @Override
    public void setVisibility(String value) {
        this.visibility = value;
    }

    @Override
    public String getMarkerStart() {
        return markerStart;
    }

    @Override
    public void setMarkerStart(String value) {
        this.markerStart = value;
    }

    @Override
    public String getMarkerMid() {
        return markerMid;
    }

    @Override
    public void setMarkerMid(String value) {
        this.markerMid = value;
    }

    @Override
    public String getMarkerEnd() {
        return markerEnd;
    }

    @Override
    public void setMarkerEnd(String value) {
        this.markerEnd = value;
    }

    @Override
    public String getColorProfile() {
        return colorProfile;
    }

    @Override
    public void setColorProfile(String value) {
        this.colorProfile = value;
    }

    @Override
    public String getStopColor() {
        return stopColor;
    }

    @Override
    public void setStopColor(String value) {
        this.stopColor = value;
    }

    @Override
    public String getStopOpacity() {
        return stopOpacity;
    }

    @Override
    public void setStopOpacity(String value) {
        this.stopOpacity = value;
    }

    @Override
    public String getClipPath() {
        return clipPath;
    }

    @Override
    public void setClipPath(String value) {
        this.clipPath = value;
    }

    @Override
    public String getClipRule() {
        return clipRule;
    }

    @Override
    public void setClipRule(String value) {
        this.clipRule = value;
    }

    @Override
    public String getMask() {
        return mask;
    }

    @Override
    public void setMask(String value) {
        this.mask = value;
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public void setFilter(String value) {
        this.filter = value;
    }

    @Override
    public String getColorInterpolationFilters() {
        return colorInterpolationFilters;
    }

    @Override
    public void setColorInterpolationFilters(String value) {
        this.colorInterpolationFilters = value;
    }

    @Override
    public String getCursor() {
        return cursor;
    }

    @Override
    public void setCursor(String value) {
        this.cursor = value;
    }

    @Override
    public String getFloodColor() {
        return floodColor;
    }

    @Override
    public void setFloodColor(String value) {
        this.floodColor = value;
    }

    @Override
    public String getFloodOpacity() {
        return floodOpacity;
    }

    @Override
    public void setFloodOpacity(String value) {
        this.floodOpacity = value;
    }

    @Override
    public String getLightingColor() {
        return lightingColor;
    }

    @Override
    public void setLightingColor(String value) {
        this.lightingColor = value;
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

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        builder.add("style", style);
        builder.add("clazz", className);
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
        builder.add("strokeDasharray", strokeDashArray);
        builder.add("strokeDashoffset", strokeDashOffset);
        builder.add("strokeLinecap", strokeLineCap);
        builder.add("strokeLinejoin", strokeLineJoin);
        builder.add("strokeMiterlimit", strokeMiterLimit);
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
    }

}