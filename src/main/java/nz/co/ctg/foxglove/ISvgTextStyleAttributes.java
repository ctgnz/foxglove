package nz.co.ctg.foxglove;

import nz.co.ctg.foxglove.attributes.SvgTextStyleAttributes;

public interface ISvgTextStyleAttributes {

    default String getFontFamily() {
        return getTextAttributes().getFontFamily();
    }

    default void setFontFamily(String value) {
        getTextAttributes().setFontFamily(value);
    }

    default String getFontSize() {
        return getTextAttributes().getFontSize();
    }

    default void setFontSize(String value) {
        getTextAttributes().setFontSize(value);
    }

    default String getFontSizeAdjust() {
        return getTextAttributes().getFontSizeAdjust();
    }

    default void setFontSizeAdjust(String value) {
        getTextAttributes().setFontSizeAdjust(value);
    }

    default String getFontStretch() {
        return getTextAttributes().getFontStretch();
    }

    default void setFontStretch(String value) {
        getTextAttributes().setFontStretch(value);
    }

    default String getFontStyle() {
        return getTextAttributes().getFontStyle();
    }

    default void setFontStyle(String value) {
        getTextAttributes().setFontStyle(value);
    }

    default String getFontVariant() {
        return getTextAttributes().getFontVariant();
    }

    default void setFontVariant(String value) {
        getTextAttributes().setFontVariant(value);
    }

    default String getFontWeight() {
        return getTextAttributes().getFontWeight();
    }

    default void setFontWeight(String value) {
        getTextAttributes().setFontWeight(value);
    }

    default String getAlignmentBaseline() {
        return getTextAttributes().getAlignmentBaseline();
    }

    default void setAlignmentBaseline(String value) {
        getTextAttributes().setAlignmentBaseline(value);
    }

    default String getBaselineShift() {
        return getTextAttributes().getBaselineShift();
    }

    default void setBaselineShift(String value) {
        getTextAttributes().setBaselineShift(value);
    }

    default String getDirection() {
        return getTextAttributes().getDirection();
    }

    default void setDirection(String value) {
        getTextAttributes().setDirection(value);
    }

    default String getDominantBaseline() {
        return getTextAttributes().getDominantBaseline();
    }

    default void setDominantBaseline(String value) {
        getTextAttributes().setDominantBaseline(value);
    }

    default String getGlyphOrientationHorizontal() {
        return getTextAttributes().getGlyphOrientationHorizontal();
    }

    default void setGlyphOrientationHorizontal(String value) {
        getTextAttributes().setGlyphOrientationHorizontal(value);
    }

    default String getGlyphOrientationVertical() {
        return getTextAttributes().getGlyphOrientationVertical();
    }

    default void setGlyphOrientationVertical(String value) {
        getTextAttributes().setGlyphOrientationVertical(value);
    }

    default String getKerning() {
        return getTextAttributes().getKerning();
    }

    default void setKerning(String value) {
        getTextAttributes().setKerning(value);
    }

    default String getLetterSpacing() {
        return getTextAttributes().getLetterSpacing();
    }

    default void setLetterSpacing(String value) {
        getTextAttributes().setLetterSpacing(value);
    }

    default String getTextAnchor() {
        return getTextAttributes().getTextAnchor();
    }

    default void setTextAnchor(String value) {
        getTextAttributes().setTextAnchor(value);
    }

    default String getTextDecoration() {
        return getTextAttributes().getAttrTextDecoration();
    }

    default void setTextDecoration(String value) {
        getTextAttributes().setAttrTextDecoration(value);
    }

    default String getUnicodeBidi() {
        return getTextAttributes().getUnicodeBidi();
    }

    default void setUnicodeBidi(String value) {
        getTextAttributes().setUnicodeBidi(value);
    }

    default String getWordSpacing() {
        return getTextAttributes().getWordSpacing();
    }

    default void setWordSpacing(String value) {
        getTextAttributes().setWordSpacing(value);
    }

    default String getWritingMode() {
        return getTextAttributes().getWritingMode();
    }

    default void setWritingMode(String value) {
        getTextAttributes().setWritingMode(value);
    }

    SvgTextStyleAttributes getTextAttributes();

}