package nz.co.ctg.foxglove;

import org.junit.Before;
import org.junit.Test;

import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class AbstractSvgStylableTest {

    private SvgRectangle candidate;

    @Before
    public void setUp() throws Exception {
        candidate = new SvgRectangle();
    }

    @Test
    public void testParseStyleAppliesEachDeclaration() throws Exception {
        candidate.setStyle("fill:red;stroke:blue;stroke-width:2.5;stroke-miterlimit:8;stroke-dashoffset:3");
        candidate.parseStyle();
        assertThat(candidate.getFill(), is(Color.RED));
        assertThat(candidate.getStroke(), is(Color.BLUE));
        assertThat(candidate.getStrokeWidth(), is(2.5));
        assertThat(candidate.getStrokeMiterLimit(), is(8.0));
        assertThat(candidate.getStrokeDashOffset(), is(3.0));
    }

    @Test
    public void testParseStyleToleratesWhitespace() throws Exception {
        candidate.setStyle("  fill : red ; stroke : blue  ");
        candidate.parseStyle();
        assertThat(candidate.getFill(), is(Color.RED));
        assertThat(candidate.getStroke(), is(Color.BLUE));
    }

    @Test
    public void testParseStyleAppliesDashArray() throws Exception {
        candidate.setStyle("stroke-dasharray:4 2 6");
        candidate.parseStyle();
        assertThat(candidate.getStrokeDashArray(), contains(4.0, 2.0, 6.0));
    }

    /**
     * Property names are case insensitive, so an upper case name must still match.
     */
    @Test
    public void testPropertyNamesAreCaseInsensitive() throws Exception {
        candidate.setStyle("FILL:red;Stroke-Width:2.5");
        candidate.parseStyle();
        assertThat(candidate.getFill(), is(Color.RED));
        assertThat(candidate.getStrokeWidth(), is(2.5));
    }

    /**
     * Values must reach the parsers as authored. Font family is the one property held as a raw string, so it is
     * the only place the preserved case is directly observable - but every value travels the same code path, which
     * is what matters for case sensitive values such as {@code url(#Grad1)}.
     */
    @Test
    public void testValueCaseIsPreserved() throws Exception {
        candidate.setStyle("font-family:Helvetica Neue");
        candidate.parseStyle();
        assertThat(candidate.getFontFamily(), is("Helvetica Neue"));
    }

    @Test
    public void testReferenceValueCaseIsPreserved() throws Exception {
        candidate.setStyle("font-family:url(#Grad1)");
        candidate.parseStyle();
        assertThat(candidate.getFontFamily(), is("url(#Grad1)"));
    }

    /**
     * Keyword values are case insensitive, which each parser is responsible for now that the caller no longer
     * lowercases everything on its behalf.
     */
    @Test
    public void testKeywordValuesAreCaseInsensitive() throws Exception {
        candidate.setStyle("fill:NONE;stroke:Blue;stroke-linecap:ROUND;stroke-linejoin:Bevel;font-weight:BOLD;font-style:Italic");
        candidate.parseStyle();
        assertThat(candidate.getFill(), is(Color.TRANSPARENT));
        assertThat(candidate.getStroke(), is(Color.BLUE));
        assertThat(candidate.getStrokeLineCap(), is(StrokeLineCap.ROUND));
        assertThat(candidate.getStrokeLineJoin(), is(StrokeLineJoin.BEVEL));
        assertThat(candidate.getFontWeight(), is(FontWeight.BOLD));
        assertThat(candidate.getFontStyle(), is(FontPosture.ITALIC));
    }

    /**
     * Only the first colon separates the name from the value, so a value containing further colons survives intact.
     */
    @Test
    public void testValueMayContainColons() throws Exception {
        candidate.setStyle("font-family:Courier:New");
        candidate.parseStyle();
        assertThat(candidate.getFontFamily(), is("Courier:New"));
    }

    @Test
    public void testDeclarationWithNoColonIsIgnored() throws Exception {
        candidate.setStyle("fill");
        candidate.parseStyle();
        assertThat(candidate.get(ISvgGraphicsAttributes.GRAPHX_FILL), is(nullValue()));
    }

    @Test
    public void testDeclarationWithNoValueIsIgnored() throws Exception {
        candidate.setStyle("stroke:");
        candidate.parseStyle();
        assertThat(candidate.getStroke(), is(nullValue()));
    }

    @Test
    public void testDeclarationWithNoNameIsIgnored() throws Exception {
        candidate.setStyle(":red");
        candidate.parseStyle();
        assertThat(candidate.get(ISvgGraphicsAttributes.GRAPHX_FILL), is(nullValue()));
    }

    /**
     * A malformed declaration must not prevent the valid ones around it from being applied, and above all must not
     * throw out of {@code createGraphic}.
     */
    @Test
    public void testMalformedDeclarationsDoNotPreventOthersApplying() throws Exception {
        candidate.setStyle("fill:red;;stroke;stroke-width:2.5; ;");
        candidate.parseStyle();
        assertThat(candidate.getFill(), is(Color.RED));
        assertThat(candidate.getStroke(), is(nullValue()));
        assertThat(candidate.getStrokeWidth(), is(2.5));
    }

    @Test
    public void testBlankStyleIsIgnored() throws Exception {
        candidate.setStyle("   ");
        candidate.parseStyle();
        assertThat(candidate.get(ISvgGraphicsAttributes.GRAPHX_FILL), is(nullValue()));
    }

    @Test
    public void testMissingStyleIsIgnored() throws Exception {
        candidate.parseStyle();
        assertThat(candidate.get(ISvgGraphicsAttributes.GRAPHX_FILL), is(nullValue()));
    }

}
