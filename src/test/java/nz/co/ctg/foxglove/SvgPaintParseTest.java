package nz.co.ctg.foxglove;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Shape;

/**
 * Exercises paint references against a document that has been through the parser, rather than one assembled in
 * memory, so the adapter and the OXM bindings are proven too.
 */
public class SvgPaintParseTest {

    private Group rendered;

    @Before
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/gradients.svg"));
        assertThat(svg, notNullValue());
        rendered = svg.createGroup();
    }

    @Test
    public void testLinearGradientFromAPresentationAttribute() throws Exception {
        LinearGradient gradient = (LinearGradient) shape("viaAttribute").getFill();
        assertThat(gradient.getStops(), hasSize(3));
        assertThat(gradient.getCycleMethod(), is(CycleMethod.REFLECT));
        assertThat(gradient.isProportional(), is(true));
        assertThat(gradient.getStops().get(0).getColor(), is(Color.RED));
        assertThat(gradient.getStops().get(1).getColor().getOpacity(), closeTo(0.5, 1e-9));
    }

    @Test
    public void testRadialGradientFromAStyleAttribute() throws Exception {
        RadialGradient gradient = (RadialGradient) shape("viaStyle").getFill();
        assertThat(gradient.getStops(), hasSize(2));
        assertThat(gradient.getCenterX(), closeTo(0.5, 1e-9));
        assertThat(gradient.getFocusDistance(), closeTo(0.5, 1e-9));
    }

    @Test
    public void testUnresolvableReferenceUsesItsFallback() throws Exception {
        assertThat(shape("withFallback").getFill(), is(Color.RED));
    }

    @Test
    public void testUnresolvableReferenceWithoutAFallbackPaintsNothing() throws Exception {
        assertThat(shape("noFallback").getFill(), is(nullValue()));
    }

    @Test
    public void testCurrentColorResolvesAgainstTheRootColor() throws Exception {
        assertThat(shape("currentColour").getFill(), is(Color.GREEN));
    }

    @Test
    public void testExplicitNonePaintsNothing() throws Exception {
        assertThat(shape("explicitNone").getFill(), is(nullValue()));
    }

    @Test
    public void testGradientOnAStroke() throws Exception {
        assertThat(shape("strokedWithGradient").getStroke(), is(instanceOf(LinearGradient.class)));
    }

    private Shape shape(String id) {
        for (Node node : rendered.getChildren()) {
            if (id.equals(node.getId())) {
                return (Shape) node;
            }
        }
        throw new AssertionError("no rendered shape with id " + id);
    }

}
