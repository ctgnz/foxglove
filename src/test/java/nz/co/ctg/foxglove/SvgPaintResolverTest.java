package nz.co.ctg.foxglove;

import org.junit.Test;

import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.paint.SvgRadialGradient;
import nz.co.ctg.foxglove.paint.SvgStop;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.SvgPaint;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Shape;

/**
 * A {@code url(#grad)} reference could not survive parsing before: the paint adapter threw on it and the value was
 * dropped, so the fill silently became nothing at all.
 */
public class SvgPaintResolverTest {

    // --- references --------------------------------------------------------

    @Test
    public void testFillResolvesALinearGradient() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setFill(SvgPaint.parse("url(#grad)"));

        Paint fill = render(rect, linearGradient("grad")).getFill();
        assertThat(fill, is(instanceOf(LinearGradient.class)));
        assertThat(((LinearGradient) fill).getStops(), hasSize(2));
    }

    @Test
    public void testStrokeResolvesAGradient() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStroke(SvgPaint.parse("url(#grad)"));

        assertThat(render(rect, linearGradient("grad")).getStroke(), is(instanceOf(LinearGradient.class)));
    }

    /**
     * The style attribute takes a different route into the property map, so it needs proving separately.
     */
    @Test
    public void testReferenceWorksFromAStyleAttribute() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStyle("fill:url(#grad)");

        assertThat(render(rect, linearGradient("grad")).getFill(), is(instanceOf(LinearGradient.class)));
    }

    @Test
    public void testReferenceIsInherited() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        SvgGroup group = new SvgGroup();
        group.setFill(SvgPaint.parse("url(#grad)"));
        group.getContent().add(rect);

        assertThat(renderInGroup(group, linearGradient("grad")).getFill(), is(instanceOf(LinearGradient.class)));
    }

    // --- fallbacks ---------------------------------------------------------

    /**
     * The specification says an unresolvable reference uses the colour written after it, and paints nothing when
     * there is not one - notably not black, which is what dropping the value used to produce.
     */
    @Test
    public void testUnresolvableReferenceUsesItsFallback() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setFill(SvgPaint.parse("url(#missing) red"));

        assertThat(render(rect, linearGradient("grad")).getFill(), is(Color.RED));
    }

    @Test
    public void testUnresolvableReferenceWithoutAFallbackPaintsNothing() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setFill(SvgPaint.parse("url(#missing)"));

        assertThat(render(rect, linearGradient("grad")).getFill(), is(nullValue()));
    }

    @Test
    public void testReferenceToSomethingThatIsNotAPaintServerFallsBack() throws Exception {
        SvgRectangle target = new SvgRectangle();
        target.setId("notagradient");
        SvgRectangle rect = new SvgRectangle();
        rect.setFill(SvgPaint.parse("url(#notagradient) red"));

        assertThat(render(rect, target).getFill(), is(Color.RED));
    }

    // --- currentColor ------------------------------------------------------

    @Test
    public void testCurrentColorResolvesToTheInheritedColor() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setFill(SvgPaint.currentColor());
        SvgGroup group = new SvgGroup();
        group.setColor("blue");
        group.getContent().add(rect);

        assertThat(renderInGroup(group).getFill(), is(Color.BLUE));
    }

    @Test
    public void testCurrentColorFallsBackToBlack() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setFill(SvgPaint.currentColor());

        assertThat(render(rect).getFill(), is(Color.BLACK));
    }

    // --- gradient construction ---------------------------------------------

    @Test
    public void testSpreadMethodMapsToCycleMethod() throws Exception {
        assertThat(cycleMethodFor(null), is(CycleMethod.NO_CYCLE));
        assertThat(cycleMethodFor("pad"), is(CycleMethod.NO_CYCLE));
        assertThat(cycleMethodFor("reflect"), is(CycleMethod.REFLECT));
        assertThat(cycleMethodFor("repeat"), is(CycleMethod.REPEAT));
    }

    @Test
    public void testGradientUnitsSelectProportionalCoordinates() throws Exception {
        SvgLinearGradient objectBoundingBox = linearGradient("a");
        SvgLinearGradient userSpace = linearGradient("b");
        userSpace.setGradientUnits("userSpaceOnUse");

        assertThat(((LinearGradient) objectBoundingBox.createPaint()).isProportional(), is(true));
        assertThat(((LinearGradient) userSpace.createPaint()).isProportional(), is(false));
    }

    @Test
    public void testLinearGradientCoordinatesDefaultToLeftToRight() throws Exception {
        LinearGradient gradient = (LinearGradient) linearGradient("g").createPaint();
        assertThat(gradient.getStartX(), is(0.0));
        assertThat(gradient.getStartY(), is(0.0));
        assertThat(gradient.getEndX(), is(1.0));
        assertThat(gradient.getEndY(), is(0.0));
    }

    @Test
    public void testLinearGradientAcceptsPercentageCoordinates() throws Exception {
        SvgLinearGradient svg = linearGradient("g");
        svg.setX1("25%");
        svg.setX2("75%");

        LinearGradient gradient = (LinearGradient) svg.createPaint();
        assertThat(gradient.getStartX(), closeTo(0.25, 1e-9));
        assertThat(gradient.getEndX(), closeTo(0.75, 1e-9));
    }

    @Test
    public void testStopColourAndOpacityBecomeTheStopColour() throws Exception {
        SvgLinearGradient svg = new SvgLinearGradient();
        svg.setId("g");
        svg.getContent().add(stop("0", "red", "0.5"));
        svg.getContent().add(stop("1", "blue", null));

        LinearGradient gradient = (LinearGradient) svg.createPaint();
        assertThat(gradient.getStops().get(0).getColor().getRed(), is(1.0));
        assertThat(gradient.getStops().get(0).getColor().getOpacity(), closeTo(0.5, 1e-9));
        assertThat(gradient.getStops().get(1).getColor(), is(Color.BLUE));
    }

    /**
     * Offsets are clamped to the unit interval, and one smaller than the stop before takes the earlier value.
     */
    @Test
    public void testStopOffsetsAreClampedAndNonDecreasing() throws Exception {
        SvgLinearGradient svg = new SvgLinearGradient();
        svg.setId("g");
        svg.getContent().add(stop("-1", "red", null));
        svg.getContent().add(stop("60%", "green", null));
        svg.getContent().add(stop("0.2", "blue", null));
        svg.getContent().add(stop("5", "white", null));

        LinearGradient gradient = (LinearGradient) svg.createPaint();
        assertThat(gradient.getStops().get(0).getOffset(), is(0.0));
        assertThat(gradient.getStops().get(1).getOffset(), closeTo(0.6, 1e-9));
        assertThat(gradient.getStops().get(2).getOffset(), closeTo(0.6, 1e-9));
        assertThat(gradient.getStops().get(3).getOffset(), is(1.0));
    }

    @Test
    public void testGradientWithNoStopsPaintsNothing() throws Exception {
        SvgLinearGradient svg = new SvgLinearGradient();
        svg.setId("g");
        assertThat(svg.createPaint(), is(nullValue()));
    }

    @Test
    public void testGradientWithOneStopPaintsThatColourFlat() throws Exception {
        SvgLinearGradient svg = new SvgLinearGradient();
        svg.setId("g");
        svg.getContent().add(stop("0", "red", null));
        assertThat(svg.createPaint(), is(Color.RED));
    }

    // --- radial ------------------------------------------------------------

    @Test
    public void testRadialGradientDefaults() throws Exception {
        RadialGradient gradient = (RadialGradient) radialGradient("g").createPaint();
        assertThat(gradient.getCenterX(), is(0.5));
        assertThat(gradient.getCenterY(), is(0.5));
        assertThat(gradient.getRadius(), is(0.5));
        assertThat(gradient.getFocusDistance(), is(0.0));
    }

    /**
     * SVG places the focal point in cartesian coordinates while JavaFX takes an angle and a distance as a fraction of
     * the radius, so a focus directly right of the centre at half the radius is angle 0, distance 0.5.
     */
    @Test
    public void testRadialFocalPointIsConvertedToPolar() throws Exception {
        SvgRadialGradient svg = radialGradient("g");
        svg.setCx("0.5");
        svg.setCy("0.5");
        svg.setR("0.4");
        svg.setFx("0.7");
        svg.setFy("0.5");

        RadialGradient gradient = (RadialGradient) svg.createPaint();
        assertThat(gradient.getFocusDistance(), closeTo(0.5, 1e-9));
        assertThat(gradient.getFocusAngle(), closeTo(0.0, 1e-9));
    }

    @Test
    public void testRadialFocalPointAboveTheCentre() throws Exception {
        SvgRadialGradient svg = radialGradient("g");
        svg.setCx("0.5");
        svg.setCy("0.5");
        svg.setR("0.5");
        svg.setFx("0.5");
        svg.setFy("0.25");

        RadialGradient gradient = (RadialGradient) svg.createPaint();
        assertThat(gradient.getFocusDistance(), closeTo(0.5, 1e-9));
        assertThat(gradient.getFocusAngle(), closeTo(-90.0, 1e-9));
    }

    @Test
    public void testRadialGradientWithZeroRadiusPaintsTheLastStop() throws Exception {
        SvgRadialGradient svg = radialGradient("g");
        svg.setR("0");
        assertThat(svg.createPaint(), is(Color.BLUE));
    }

    // --- helpers -----------------------------------------------------------

    private static CycleMethod cycleMethodFor(String spreadMethod) {
        SvgLinearGradient svg = linearGradient("g");
        svg.setSpreadMethod(spreadMethod);
        return ((LinearGradient) svg.createPaint()).getCycleMethod();
    }

    private static SvgStop stop(String offset, String color, String opacity) {
        SvgStop stop = new SvgStop();
        stop.setOffset(offset);
        stop.setStopColor(color);
        if (opacity != null) {
            stop.setStopOpacity(opacity);
        }
        return stop;
    }

    private static SvgLinearGradient linearGradient(String id) {
        SvgLinearGradient gradient = new SvgLinearGradient();
        gradient.setId(id);
        gradient.getContent().add(stop("0", "red", null));
        gradient.getContent().add(stop("1", "blue", null));
        return gradient;
    }

    private static SvgRadialGradient radialGradient(String id) {
        SvgRadialGradient gradient = new SvgRadialGradient();
        gradient.setId(id);
        gradient.getContent().add(stop("0", "red", null));
        gradient.getContent().add(stop("1", "blue", null));
        return gradient;
    }

    private static Shape render(SvgRectangle rect, ISvgElement... definitions) {
        SvgGroup group = new SvgGroup();
        group.getContent().add(rect);
        return renderInGroup(group, definitions);
    }

    private static Shape renderInGroup(SvgGroup group, ISvgElement... definitions) {
        SvgGraphic svg = new SvgGraphic();
        if (definitions.length > 0) {
            SvgDefinitions defs = new SvgDefinitions();
            for (ISvgElement definition : definitions) {
                defs.getContent().add(definition);
            }
            svg.getContent().add(defs);
        }
        svg.getContent().add(group);
        return (Shape) ((Group) svg.createGroup().getChildren().get(0)).getChildren().get(0);
    }

}
