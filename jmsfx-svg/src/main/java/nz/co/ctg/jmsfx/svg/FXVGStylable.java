/*
 * Copyright (c) 2010 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */

package nz.co.ctg.jmsfx.svg;

import java.util.List;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public interface FXVGStylable {
    default CSSStyleDeclaration getStyleDeclaration() {
        return null;
    }

    default CSSValue getPresentationAttribute(String name) {
        return null;
    }

    String getClassName();

    void setClassName(String className);

    String getStyle();

    void setStyle(String style);

    void setCursor(String value);

    String getCursor();

    void setColorInterpolationFilters(String value);

    String getColorInterpolationFilters();

    void setFilter(String value);

    String getFilter();

    void setMask(String value);

    String getMask();

    void setClipRule(String value);

    String getClipRule();

    void setClipPath(String value);

    String getClipPath();

    void setStopOpacity(String value);

    String getStopOpacity();

    void setStopColor(String value);

    String getStopColor();

    void setColorProfile(String value);

    String getColorProfile();

    void setMarkerEnd(String value);

    String getMarkerEnd();

    void setMarkerMid(String value);

    String getMarkerMid();

    void setMarkerStart(String value);

    String getMarkerStart();

    void setVisibility(String value);

    String getVisibility();

    void setTextRendering(String value);

    String getTextRendering();

    void setShapeRendering(String value);

    String getShapeRendering();

    void setPointerEvents(String value);

    String getPointerEvents();

    void setImageRendering(String value);

    String getImageRendering();

    void setDisplay(String value);

    String getDisplay();

    void setStrokeOpacity(String value);

    String getStrokeOpacity();

    void setFillOpacity(String value);

    String getFillOpacity();

    void setOpacity(String value);

    String getOpacity();

    void setColorRendering(String value);

    String getColorRendering();

    void setColorInterpolation(String value);

    String getColorInterpolation();

    void setColor(String value);

    String getColor();

    void setStrokeWidth(double value);

    double getStrokeWidth();

    void setStrokeMiterLimit(double value);

    double getStrokeMiterLimit();

    void setStrokeLineJoin(StrokeLineJoin value);

    StrokeLineJoin getStrokeLineJoin();

    void setStrokeLineCap(StrokeLineCap value);

    StrokeLineCap getStrokeLineCap();

    void setStrokeDashOffset(double value);

    double getStrokeDashOffset();

    void setStrokeDashArray(List<Double> value);

    List<Double> getStrokeDashArray();

    void setStroke(Paint value);

    Paint getStroke();

    void setFillRule(String value);

    String getFillRule();

    void setFill(Paint value);

    Paint getFill();

    void setFontWeight(String value);

    String getFontWeight();

    void setFontVariant(String value);

    String getFontVariant();

    void setFontStyle(String value);

    String getFontStyle();

    void setFontStretch(String value);

    String getFontStretch();

    void setFontSizeAdjust(String value);

    String getFontSizeAdjust();

    void setFontSize(String value);

    String getFontSize();

    void setFontFamily(String value);

    String getFontFamily();

    void setWordSpacing(String value);

    String getWordSpacing();

    void setUnicodeBidi(String value);

    String getUnicodeBidi();

    void setTextDecoration(String value);

    String getTextDecoration();

    void setTextAnchor(String value);

    String getTextAnchor();

    void setLetterSpacing(String value);

    String getLetterSpacing();

    void setKerning(String value);

    String getKerning();

    void setGlyphOrientationVertical(String value);

    String getGlyphOrientationVertical();

    void setGlyphOrientationHorizontal(String value);

    String getGlyphOrientationHorizontal();

    void setDominantBaseline(String value);

    String getDominantBaseline();

    void setDirection(String value);

    String getDirection();

    void setBaselineShift(String value);

    String getBaselineShift();

    void setAlignmentBaseline(String value);

    String getAlignmentBaseline();

    void setWritingMode(String value);

    String getWritingMode();

    void setOverflow(String value);

    String getOverflow();

    void setClip(String value);

    String getClip();

    void setEnableBackground(String value);

    String getEnableBackground();

    void setLightingColor(String value);

    String getLightingColor();

    void setFloodOpacity(String value);

    String getFloodOpacity();

    void setFloodColor(String value);

    String getFloodColor();
}
