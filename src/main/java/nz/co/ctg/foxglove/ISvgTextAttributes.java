package nz.co.ctg.foxglove;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public interface ISvgTextAttributes extends ISvgAttributes {
    String TEXT_WRITING_MODE = "writing-mode";
    String TEXT_ALIGNMENT_BASELINE = "alignment-baseline";
    String TEXT_BASELINE_SHIFT = "baseline-shift";
    String TEXT_DIRECTION = "direction";
    String TEXT_DOMINANT_BASELINE = "dominant-baseline";
    String TEXT_GLYPH_ORIENTATION_HORIZONTAL = "glyph-orientation-horizontal";
    String TEXT_GLYPH_ORIENTATION_VERTICAL = "glyph-orientation-vertical";
    String TEXT_KERNING = "kerning";
    String TEXT_LETTER_SPACING = "letter-spacing";
    String TEXT_ANCHOR = "text-anchor";
    String TEXT_DECORATION = "text-decoration";
    String TEXT_UNICODE_BIDI = "unicode-bidi";
    String TEXT_WORD_SPACING = "word-spacing";
    String TEXT_FONT_FAMILY = "font-family";
    String TEXT_FONT_SIZE = "font-size";
    String TEXT_FONT_SIZE_ADJUST = "font-size-adjust";
    String TEXT_FONT_STRETCH = "font-stretch";
    String TEXT_FONT_STYLE = "font-style";
    String TEXT_FONT_VARIANT = "font-variant";
    String TEXT_FONT_WEIGHT = "font-weight";

    default String getWritingMode() {
        return get(TEXT_WRITING_MODE);
    }

    default void setWritingMode(String value) {
        set(TEXT_WRITING_MODE, value);
    }

    default String getAlignmentBaseline() {
        return get(TEXT_ALIGNMENT_BASELINE);
    }

    default void setAlignmentBaseline(String value) {
        set(TEXT_ALIGNMENT_BASELINE, value);
    }

    default String getBaselineShift() {
        return get(TEXT_BASELINE_SHIFT);
    }

    default void setBaselineShift(String value) {
        set(TEXT_BASELINE_SHIFT, value);
    }

    default String getDirection() {
        return get(TEXT_DIRECTION);
    }

    default void setDirection(String value) {
        set(TEXT_DIRECTION, value);
    }

    default String getDominantBaseline() {
        return get(TEXT_DOMINANT_BASELINE);
    }

    default void setDominantBaseline(String value) {
        set(TEXT_DOMINANT_BASELINE, value);
    }

    default String getGlyphOrientationHorizontal() {
        return get(TEXT_GLYPH_ORIENTATION_HORIZONTAL);
    }

    default void setGlyphOrientationHorizontal(String value) {
        set(TEXT_GLYPH_ORIENTATION_HORIZONTAL, value);
    }

    default String getGlyphOrientationVertical() {
        return get(TEXT_GLYPH_ORIENTATION_VERTICAL);
    }

    default void setGlyphOrientationVertical(String value) {
        set(TEXT_GLYPH_ORIENTATION_VERTICAL, value);
    }

    default String getKerning() {
        return get(TEXT_KERNING);
    }

    default void setKerning(String value) {
        set(TEXT_KERNING, value);
    }

    default String getLetterSpacing() {
        return get(TEXT_LETTER_SPACING);
    }

    default void setLetterSpacing(String value) {
        set(TEXT_LETTER_SPACING, value);
    }

    default String getTextAnchor() {
        return get(TEXT_ANCHOR);
    }

    default void setTextAnchor(String value) {
        set(TEXT_ANCHOR, value);
    }

    default String getTextDecoration() {
        return get(TEXT_DECORATION);
    }

    default void setTextDecoration(String value) {
        set(TEXT_DECORATION, value);
    }

    default String getUnicodeBidi() {
        return get(TEXT_UNICODE_BIDI);
    }

    default void setUnicodeBidi(String value) {
        set(TEXT_UNICODE_BIDI, value);
    }

    default String getWordSpacing() {
        return get(TEXT_WORD_SPACING);
    }

    default void setWordSpacing(String value) {
        set(TEXT_WORD_SPACING, value);
    }

    default String getFontFamily() {
        return get(TEXT_FONT_FAMILY);
    }

    default void setFontFamily(String value) {
        set(TEXT_FONT_FAMILY, value);
    }

    default String getFontSize() {
        return get(TEXT_FONT_SIZE);
    }

    default void setFontSize(String value) {
        set(TEXT_FONT_SIZE, value);
    }

    default String getFontSizeAdjust() {
        return get(TEXT_FONT_SIZE_ADJUST);
    }

    default void setFontSizeAdjust(String value) {
        set(TEXT_FONT_SIZE_ADJUST, value);
    }

    default String getFontStretch() {
        return get(TEXT_FONT_STRETCH);
    }

    default void setFontStretch(String value) {
        set(TEXT_FONT_STRETCH, value);
    }

    default String getFontStyle() {
        return get(TEXT_FONT_STYLE);
    }

    default void setFontStyle(String value) {
        set(TEXT_FONT_STYLE, value);
    }

    default String getFontVariant() {
        return get(TEXT_FONT_VARIANT);
    }

    default void setFontVariant(String value) {
        set(TEXT_FONT_VARIANT, value);
    }

    default String getFontWeight() {
        return get(TEXT_FONT_WEIGHT);
    }

    default void setFontWeight(String value) {
        set(TEXT_FONT_WEIGHT, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(TEXT_WRITING_MODE, getWritingMode());
        builder.add(TEXT_ALIGNMENT_BASELINE, getAlignmentBaseline());
        builder.add(TEXT_BASELINE_SHIFT, getBaselineShift());
        builder.add(TEXT_DIRECTION, getDirection());
        builder.add(TEXT_DOMINANT_BASELINE, getDominantBaseline());
        builder.add(TEXT_GLYPH_ORIENTATION_HORIZONTAL, getGlyphOrientationHorizontal());
        builder.add(TEXT_GLYPH_ORIENTATION_VERTICAL, getGlyphOrientationVertical());
        builder.add(TEXT_KERNING, getKerning());
        builder.add(TEXT_LETTER_SPACING, getLetterSpacing());
        builder.add(TEXT_ANCHOR, getTextAnchor());
        builder.add(TEXT_DECORATION, getTextDecoration());
        builder.add(TEXT_UNICODE_BIDI, getUnicodeBidi());
        builder.add(TEXT_WORD_SPACING, getWordSpacing());
        builder.add(TEXT_FONT_FAMILY, getFontFamily());
        builder.add(TEXT_FONT_SIZE, getFontSize());
        builder.add(TEXT_FONT_SIZE_ADJUST, getFontSizeAdjust());
        builder.add(TEXT_FONT_STRETCH, getFontStretch());
        builder.add(TEXT_FONT_STYLE, getFontStyle());
        builder.add(TEXT_FONT_VARIANT, getFontVariant());
        builder.add(TEXT_FONT_WEIGHT, getFontWeight());
    }

    default void applyTextProperties(Text svgText) {
        Double size = StringUtils.isNotBlank(getFontSize()) ? Double.valueOf(StringUtils.strip(getFontSize(), "px")) : Font.getDefault().getSize();
        Font font = new Font(getFontFamily(), size);
        svgText.setFont(font);
    }

    default void parseTextStyle(String style) {
        if (StringUtils.isNotBlank(style)) {
            Arrays.stream(StringUtils.split(style, ';')).forEach(stylePair -> {
                String[] values = StringUtils.split(stylePair, ':');
                String name = values[0].toLowerCase().trim();
                String value = values[1].toLowerCase().trim();
                switch (name) {
                    case TEXT_FONT_FAMILY:
                        setFontFamily(value);
                        break;
                    case TEXT_FONT_SIZE:
                        setFontSize(value);
                        break;
                    case TEXT_FONT_STYLE:
                        setFontStyle(value);
                        break;
                    case TEXT_FONT_WEIGHT:
                        setFontWeight(value);
                        break;
                }
            });
        }
    }
}
