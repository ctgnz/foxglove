package nz.co.ctg.foxglove.style;

import org.junit.Before;
import org.junit.Test;

import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.SvgPaint;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.FontWeight;

public class SvgPropertyTableTest {

    private SvgRectangle target;

    @Before
    public void setUp() throws Exception {
        target = new SvgRectangle();
    }

    @Test
    public void testTypedConversionsProduceTheExpectedObjects() throws Exception {
        SvgPropertyTable.apply(target, ISvgGraphicsAttributes.GRAPHX_FILL, "red");
        SvgPropertyTable.apply(target, ISvgGraphicsAttributes.GRAPHX_FILL_RULE, "evenodd");
        SvgPropertyTable.apply(target, ISvgGraphicsAttributes.GRAPHX_STROKE_WIDTH, "2.5");
        SvgPropertyTable.apply(target, ISvgGraphicsAttributes.GRAPHX_STROKE_LINECAP, "round");
        SvgPropertyTable.apply(target, "font-weight", "bold");

        assertThat(target.getFill(), is(SvgPaint.of(Color.RED)));
        assertThat(target.getFillRule(), is(FillRule.EVEN_ODD));
        assertThat(target.getStrokeWidth(), is(2.5));
        assertThat(target.getStrokeLineCap(), is(StrokeLineCap.ROUND));
        assertThat(target.getFontWeight(), is(FontWeight.BOLD));
    }

    @Test
    public void testFillRuleDefaultsToNonZero() throws Exception {
        SvgPropertyTable.apply(target, ISvgGraphicsAttributes.GRAPHX_FILL_RULE, "garbage");
        assertThat(target.getFillRule(), is(FillRule.NON_ZERO));
    }

    @Test
    public void testAnUnrecognisedNumericValueIsDropped() throws Exception {
        SvgPropertyTable.apply(target, ISvgGraphicsAttributes.GRAPHX_STROKE_WIDTH, "not-a-number");
        assertThat(target.getStrokeWidth(), is(nullValue()));
    }

    @Test
    public void testAPropertyWithNoConversionIsStoredAsATrimmedString() throws Exception {
        SvgPropertyTable.apply(target, ISvgGraphicsAttributes.GRAPHX_OPACITY, "  0.5  ");
        assertThat(target.getOpacity(), is("0.5"));
    }

}
