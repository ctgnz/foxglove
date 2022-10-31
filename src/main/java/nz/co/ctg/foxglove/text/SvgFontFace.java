package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;
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


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "font-face")
public class SvgFontFace extends AbstractSvgElement {

    @XmlAttribute(name = "font-family")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fontFamily;

    @XmlAttribute(name = "font-style")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fontStyle;

    @XmlAttribute(name = "font-variant")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fontVariant;

    @XmlAttribute(name = "font-weight")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fontWeight;

    @XmlAttribute(name = "font-stretch")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fontStretch;

    @XmlAttribute(name = "font-size")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fontSize;

    @XmlAttribute(name = "unicode-range")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String unicodeRange;

    @XmlAttribute(name = "units-per-em")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String unitsPerEm;

    @XmlAttribute(name = "panose-1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String panose1;

    @XmlAttribute(name = "stemv")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String stemWidthVertical;

    @XmlAttribute(name = "stemh")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String stemWidthHorizontal;

    @XmlAttribute(name = "slope")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String slope;

    @XmlAttribute(name = "cap-height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String capHeight;

    @XmlAttribute(name = "x-height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xHeight;

    @XmlAttribute(name = "accent-height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String accentHeight;

    @XmlAttribute(name = "ascent")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String ascent;

    @XmlAttribute(name = "descent")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String descent;

    @XmlAttribute(name = "widths")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String widths;

    @XmlAttribute(name = "bbox")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String boundingBox;

    @XmlAttribute(name = "ideographic")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String ideographic;

    @XmlAttribute(name = "alphabetic")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String alphabetic;

    @XmlAttribute(name = "mathematical")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String mathematical;

    @XmlAttribute(name = "hanging")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String hanging;

    @XmlAttribute(name = "v-ideographic")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String vIdeographic;

    @XmlAttribute(name = "v-alphabetic")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String vAlphabetic;

    @XmlAttribute(name = "v-mathematical")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String vMathematical;

    @XmlAttribute(name = "v-hanging")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String vHanging;

    @XmlAttribute(name = "underline-position")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String underlinePosition;

    @XmlAttribute(name = "underline-thickness")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String underlineThickness;

    @XmlAttribute(name = "strikethrough-position")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String strikethroughPosition;

    @XmlAttribute(name = "strikethrough-thickness")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String strikethroughThickness;

    @XmlAttribute(name = "overline-position")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String overlinePosition;

    @XmlAttribute(name = "overline-thickness")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String overlineThickness;

    @XmlElements({
        @XmlElement(name = "font-face-src", required = true, type = SvgFontFaceSrc.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "desc", required = true, type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", required = true, type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", required = true, type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String value) {
        this.fontFamily = value;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String value) {
        this.fontStyle = value;
    }

    public String getFontVariant() {
        return fontVariant;
    }

    public void setFontVariant(String value) {
        this.fontVariant = value;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String value) {
        this.fontWeight = value;
    }

    public String getFontStretch() {
        return fontStretch;
    }

    public void setFontStretch(String value) {
        this.fontStretch = value;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String value) {
        this.fontSize = value;
    }

    public String getUnicodeRange() {
        return unicodeRange;
    }

    public void setUnicodeRange(String value) {
        this.unicodeRange = value;
    }

    public String getUnitsPerEm() {
        return unitsPerEm;
    }

    public void setUnitsPerEm(String value) {
        this.unitsPerEm = value;
    }

    public String getPanose1() {
        return panose1;
    }

    public void setPanose1(String value) {
        this.panose1 = value;
    }

    public String getStemWidthVertical() {
        return stemWidthVertical;
    }

    public void setStemWidthVertical(String value) {
        this.stemWidthVertical = value;
    }

    public String getStemWidthHorizontal() {
        return stemWidthHorizontal;
    }

    public void setStemWidthHorizontal(String value) {
        this.stemWidthHorizontal = value;
    }

    public String getSlope() {
        return slope;
    }

    public void setSlope(String value) {
        this.slope = value;
    }

    public String getCapHeight() {
        return capHeight;
    }

    public void setCapHeight(String value) {
        this.capHeight = value;
    }

    public String getXHeight() {
        return xHeight;
    }

    public void setXHeight(String value) {
        this.xHeight = value;
    }

    public String getAccentHeight() {
        return accentHeight;
    }

    public void setAccentHeight(String value) {
        this.accentHeight = value;
    }

    public String getAscent() {
        return ascent;
    }

    public void setAscent(String value) {
        this.ascent = value;
    }

    public String getDescent() {
        return descent;
    }

    public void setDescent(String value) {
        this.descent = value;
    }

    public String getWidths() {
        return widths;
    }

    public void setWidths(String value) {
        this.widths = value;
    }

    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String value) {
        this.boundingBox = value;
    }

    public String getIdeographic() {
        return ideographic;
    }

    public void setIdeographic(String value) {
        this.ideographic = value;
    }

    public String getAlphabetic() {
        return alphabetic;
    }

    public void setAlphabetic(String value) {
        this.alphabetic = value;
    }

    public String getMathematical() {
        return mathematical;
    }

    public void setMathematical(String value) {
        this.mathematical = value;
    }

    public String getHanging() {
        return hanging;
    }

    public void setHanging(String value) {
        this.hanging = value;
    }

    public String getVIdeographic() {
        return vIdeographic;
    }

    public void setVIdeographic(String value) {
        this.vIdeographic = value;
    }

    public String getVAlphabetic() {
        return vAlphabetic;
    }

    public void setVAlphabetic(String value) {
        this.vAlphabetic = value;
    }

    public String getVMathematical() {
        return vMathematical;
    }

    public void setVMathematical(String value) {
        this.vMathematical = value;
    }

    public String getVHanging() {
        return vHanging;
    }

    public void setVHanging(String value) {
        this.vHanging = value;
    }

    public String getUnderlinePosition() {
        return underlinePosition;
    }

    public void setUnderlinePosition(String value) {
        this.underlinePosition = value;
    }

    public String getUnderlineThickness() {
        return underlineThickness;
    }

    public void setUnderlineThickness(String value) {
        this.underlineThickness = value;
    }

    public String getStrikethroughPosition() {
        return strikethroughPosition;
    }

    public void setStrikethroughPosition(String value) {
        this.strikethroughPosition = value;
    }

    public String getStrikethroughThickness() {
        return strikethroughThickness;
    }

    public void setStrikethroughThickness(String value) {
        this.strikethroughThickness = value;
    }

    public String getOverlinePosition() {
        return overlinePosition;
    }

    public void setOverlinePosition(String value) {
        this.overlinePosition = value;
    }

    public String getOverlineThickness() {
        return overlineThickness;
    }

    public void setOverlineThickness(String value) {
        this.overlineThickness = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("fontFamily", fontFamily);
        builder.add("fontStyle", fontStyle);
        builder.add("fontVariant", fontVariant);
        builder.add("fontWeight", fontWeight);
        builder.add("fontStretch", fontStretch);
        builder.add("fontSize", fontSize);
        builder.add("unicodeRange", unicodeRange);
        builder.add("unitsPerEm", unitsPerEm);
        builder.add("panose1", panose1);
        builder.add("stemWidthVertical", stemWidthVertical);
        builder.add("stemWidthHorizontal", stemWidthHorizontal);
        builder.add("slope", slope);
        builder.add("capHeight", capHeight);
        builder.add("xHeight", xHeight);
        builder.add("accentHeight", accentHeight);
        builder.add("ascent", ascent);
        builder.add("descent", descent);
        builder.add("widths", widths);
        builder.add("boundingBox", boundingBox);
        builder.add("ideographic", ideographic);
        builder.add("alphabetic", alphabetic);
        builder.add("mathematical", mathematical);
        builder.add("hanging", hanging);
        builder.add("vIdeographic", vIdeographic);
        builder.add("vAlphabetic", vAlphabetic);
        builder.add("vMathematical", vMathematical);
        builder.add("vHanging", vHanging);
        builder.add("underlinePosition", underlinePosition);
        builder.add("underlineThickness", underlineThickness);
        builder.add("strikethroughPosition", strikethroughPosition);
        builder.add("strikethroughThickness", strikethroughThickness);
        builder.add("overlinePosition", overlinePosition);
        builder.add("overlineThickness", overlineThickness);
        super.toStringDetail(builder);
    }

}
