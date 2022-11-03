package nz.co.ctg.foxglove.attributes;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgStylable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgTextStyleAttributes implements AttributeTransformer, FieldTransformer {
    private static final String ATTR_FONT_FAMILY = "font-family";
    private static final String ATTR_FONT_SIZE = "font-size";
    private static final String ATTR_FONT_SIZE_ADJUST = "font-size-adjust";
    private static final String ATTR_FONT_STRETCH = "font-stretch";
    private static final String ATTR_FONT_STYLE = "font-style";
    private static final String ATTR_FONT_VARIANT = "font-variant";
    private static final String ATTR_FONT_WEIGHT = "font-weight";
    private static final String ATTR_ALIGNMENT_BASELINE = "alignment-baseline";
    private static final String ATTR_BASELINE_SHIFT = "baseline-shift";
    private static final String ATTR_DIRECTION = "direction";
    private static final String ATTR_DOMINANT_BASELINE = "dominant-baseline";
    private static final String ATTR_GLYPH_ORIENTATION_HORIZONTAL = "glyph-orientation-horizontal";
    private static final String ATTR_GLYPH_ORIENTATION_VERTICAL = "glyph-orientation-vertical";
    private static final String ATTR_KERNING = "kerning";
    private static final String ATTR_LETTER_SPACING = "letter-spacing";
    private static final String ATTR_TEXT_ANCHOR = "text-anchor";
    private static final String ATTR_TEXT_DECORATION = "text-decoration";
    private static final String ATTR_UNICODE_BIDI = "unicode-bidi";
    private static final String ATTR_WORD_SPACING = "word-spacing";
    private static final String ATTR_WRITING_MODE = "writing-mode";

    private static final long serialVersionUID = 1L;

    private AbstractTransformationMapping mapping;

    @XmlAttribute(name = ATTR_WRITING_MODE)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String writingMode;

    @XmlAttribute(name = ATTR_ALIGNMENT_BASELINE)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String alignmentBaseline;

    @XmlAttribute(name = ATTR_BASELINE_SHIFT)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String baselineShift;

    @XmlAttribute(name = ATTR_DIRECTION)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String direction;

    @XmlAttribute(name = ATTR_DOMINANT_BASELINE)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String dominantBaseline;

    @XmlAttribute(name = ATTR_GLYPH_ORIENTATION_HORIZONTAL)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String glyphOrientationHorizontal;

    @XmlAttribute(name = ATTR_GLYPH_ORIENTATION_VERTICAL)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String glyphOrientationVertical;

    @XmlAttribute(name = ATTR_KERNING)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String kerning;

    @XmlAttribute(name = ATTR_LETTER_SPACING)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String letterSpacing;

    @XmlAttribute(name = ATTR_TEXT_ANCHOR)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String textAnchor;

    @XmlAttribute(name = ATTR_TEXT_DECORATION)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String textDecoration;

    @XmlAttribute(name = ATTR_UNICODE_BIDI)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String unicodeBidi;

    @XmlAttribute(name = ATTR_WORD_SPACING)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String wordSpacing;

    @XmlAttribute(name = ATTR_FONT_FAMILY)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fontFamily;

    @XmlAttribute(name = ATTR_FONT_SIZE)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fontSize;

    @XmlAttribute(name = ATTR_FONT_SIZE_ADJUST)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fontSizeAdjust;

    @XmlAttribute(name = ATTR_FONT_STRETCH)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String fontStretch;

    @XmlAttribute(name = ATTR_FONT_STYLE)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String fontStyle;

    @XmlAttribute(name = ATTR_FONT_VARIANT)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String fontVariant;

    @XmlAttribute(name = ATTR_FONT_WEIGHT)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String fontWeight;

    public SvgTextStyleAttributes() {
    }

    public String getWritingMode() {
        return writingMode;
    }

    public void setWritingMode(String writingMode) {
        this.writingMode = writingMode;
    }

    public String getAlignmentBaseline() {
        return alignmentBaseline;
    }

    public void setAlignmentBaseline(String alignmentBaseline) {
        this.alignmentBaseline = alignmentBaseline;
    }

    public String getBaselineShift() {
        return baselineShift;
    }

    public void setBaselineShift(String baselineShift) {
        this.baselineShift = baselineShift;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDominantBaseline() {
        return dominantBaseline;
    }

    public void setDominantBaseline(String dominantBaseline) {
        this.dominantBaseline = dominantBaseline;
    }

    public String getGlyphOrientationHorizontal() {
        return glyphOrientationHorizontal;
    }

    public void setGlyphOrientationHorizontal(String glyphOrientationHorizontal) {
        this.glyphOrientationHorizontal = glyphOrientationHorizontal;
    }

    public String getGlyphOrientationVertical() {
        return glyphOrientationVertical;
    }

    public void setGlyphOrientationVertical(String glyphOrientationVertical) {
        this.glyphOrientationVertical = glyphOrientationVertical;
    }

    public String getKerning() {
        return kerning;
    }

    public void setKerning(String kerning) {
        this.kerning = kerning;
    }

    public String getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(String letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    public String getTextAnchor() {
        return textAnchor;
    }

    public void setTextAnchor(String textAnchor) {
        this.textAnchor = textAnchor;
    }

    public String getAttrTextDecoration() {
        return textDecoration;
    }

    public void setAttrTextDecoration(String textDecoration) {
        this.textDecoration = textDecoration;
    }

    public String getUnicodeBidi() {
        return unicodeBidi;
    }

    public void setUnicodeBidi(String unicodeBidi) {
        this.unicodeBidi = unicodeBidi;
    }

    public String getWordSpacing() {
        return wordSpacing;
    }

    public void setWordSpacing(String wordSpacing) {
        this.wordSpacing = wordSpacing;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontSizeAdjust() {
        return fontSizeAdjust;
    }

    public void setFontSizeAdjust(String fontSizeAdjust) {
        this.fontSizeAdjust = fontSizeAdjust;
    }

    public String getFontStretch() {
        return fontStretch;
    }

    public void setFontStretch(String fontStretch) {
        this.fontStretch = fontStretch;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontVariant() {
        return fontVariant;
    }

    public void setFontVariant(String fontVariant) {
        this.fontVariant = fontVariant;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public void applyTextProperties(Text svgText) {
        Double size = StringUtils.isNotBlank(fontSize) ? Double.valueOf(StringUtils.strip(fontSize, "px")) : Font.getDefault().getSize();
        Font font = new Font(fontFamily, size);
        svgText.setFont(font);
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgStylable stylable = (ISvgStylable) object;
        SvgTextStyleAttributes attributes = stylable.getTextAttributes();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FONT_FAMILY)).findFirst().ifPresent(fld -> {
                attributes.setFontFamily((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FONT_SIZE)).findFirst().ifPresent(fld -> {
                attributes.setFontSize((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FONT_SIZE_ADJUST)).findFirst().ifPresent(fld -> {
                attributes.setFontSizeAdjust((String) dataRecord.get(fld));
            });
        }
        return attributes;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgStylable stylable = (ISvgStylable) instance;
        SvgTextStyleAttributes attributes = stylable.getTextAttributes();
        String attributeName = StringUtils.defaultIfBlank(fieldName, "@");
        switch (StringUtils.remove(attributeName, '@')) {
            case ATTR_FONT_FAMILY:
                return attributes.getFontFamily();
            case ATTR_FONT_SIZE:
                return attributes.getFontSize();
            case ATTR_FONT_SIZE_ADJUST:
                return attributes.getFontSizeAdjust();
            default:
                return null;
        }
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add("writingMode", writingMode);
        builder.add("alignmentBaseline", alignmentBaseline);
        builder.add("baselineShift", baselineShift);
        builder.add(ATTR_DIRECTION, direction);
        builder.add("dominantBaseline", dominantBaseline);
        builder.add("glyphOrientationHorizontal", glyphOrientationHorizontal);
        builder.add("glyphOrientationVertical", glyphOrientationVertical);
        builder.add(ATTR_KERNING, kerning);
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
    }

    public void parseStyle(String style) {
        if (StringUtils.isNotBlank(style)) {
            Arrays.stream(StringUtils.split(style, ';')).forEach(stylePair -> {
                String[] values = StringUtils.split(stylePair, ':');
                String name = values[0].toLowerCase();
                String value = values[1].toLowerCase();
                switch (name) {
                    case ATTR_FONT_FAMILY:
                        setFontFamily(value);
                        break;
                    case ATTR_FONT_SIZE:
                        setFontSize(value);
                        break;
                    case ATTR_FONT_STYLE:
                        setFontStyle(value);
                        break;
                    case ATTR_FONT_WEIGHT:
                        setFontWeight(value);
                        break;
                }
            });
        }
    }

}
