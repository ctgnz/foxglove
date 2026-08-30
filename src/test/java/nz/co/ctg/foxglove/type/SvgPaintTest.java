package nz.co.ctg.foxglove.type;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.scene.paint.Color;

public class SvgPaintTest {

    @Test
    public void testParsesAColour() throws Exception {
        SvgPaint paint = SvgPaint.parse("red");
        assertThat(paint.isColor(), is(true));
        assertThat(paint.getPaint(), is(Color.RED));
    }

    @Test
    public void testParsesAHexColour() throws Exception {
        assertThat(SvgPaint.parse("#ff0000").getPaint(), is(Color.web("#ff0000")));
    }

    /**
     * An explicit none is not the same as an unspecified value: none paints nothing, while absence inherits.
     */
    @Test
    public void testParsesNone() throws Exception {
        assertThat(SvgPaint.parse("none").isNone(), is(true));
        assertThat(SvgPaint.parse("NONE").isNone(), is(true));
        assertThat(SvgPaint.parse("none"), is(SvgPaint.none()));
    }

    @Test
    public void testParsesCurrentColor() throws Exception {
        assertThat(SvgPaint.parse("currentColor").isCurrentColor(), is(true));
        assertThat(SvgPaint.parse("currentcolor").isCurrentColor(), is(true));
    }

    @Test
    public void testParsesAReference() throws Exception {
        SvgPaint paint = SvgPaint.parse("url(#grad)");
        assertThat(paint.isReference(), is(true));
        assertThat(paint.getReference(), is("url(#grad)"));
        assertThat(paint.getFallback(), is(nullValue()));
    }

    @Test
    public void testParsesAReferenceWithAColourFallback() throws Exception {
        SvgPaint paint = SvgPaint.parse("url(#grad) red");
        assertThat(paint.isReference(), is(true));
        assertThat(paint.getReference(), is("url(#grad)"));
        assertThat(paint.getFallback(), is(SvgPaint.of(Color.RED)));
    }

    @Test
    public void testParsesAReferenceWithANoneFallback() throws Exception {
        assertThat(SvgPaint.parse("url(#grad) none").getFallback(), is(SvgPaint.none()));
    }

    @Test
    public void testReferencePreservesIdCase() throws Exception {
        assertThat(SvgPaint.parse("url(#Grad1)").getReference(), is("url(#Grad1)"));
    }

    /**
     * A blank or unrecognised value leaves the property unspecified, so it inherits rather than painting something
     * arbitrary.
     */
    @Test
    public void testUnusableValuesAreUnspecified() throws Exception {
        assertThat(SvgPaint.parse(null), is(nullValue()));
        assertThat(SvgPaint.parse(""), is(nullValue()));
        assertThat(SvgPaint.parse("   "), is(nullValue()));
        assertThat(SvgPaint.parse("not-a-colour"), is(nullValue()));
        assertThat(SvgPaint.parse("url(#unclosed"), is(nullValue()));
    }

    @Test
    public void testEqualityAndToString() throws Exception {
        assertThat(SvgPaint.parse("red"), is(SvgPaint.of(Color.RED)));
        assertThat(SvgPaint.none().toString(), is("none"));
        assertThat(SvgPaint.currentColor().toString(), is("currentColor"));
        assertThat(SvgPaint.parse("url(#g) red").toString(), is("url(#g) 0xff0000ff"));
    }

}
