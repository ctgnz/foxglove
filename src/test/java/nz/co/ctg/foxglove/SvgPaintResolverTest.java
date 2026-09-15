package nz.co.ctg.foxglove;

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

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.paint.SvgRadialGradient;
import nz.co.ctg.foxglove.paint.SvgStop;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.SvgPaint;

/**
 * A {@code url(#grad)} reference could not survive parsing before: the paint adapter threw on it and the value was dropped, so the fill silently became nothing at all.
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
        group.getContent()
            .add(rect);

        assertThat(renderInGroup(group, linearGradient("grad")).getFill(), is(instanceOf(LinearGradient.class)));
    }

    // --- fallbacks ---------------------------------------------------------

    /**
     * The specification says an unresolvable reference uses the colour written after it, and paints nothing when there is not one - notably not black, which is what dropping the
     * value used to produce.
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
        group.getContent()
            .add(rect);

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

        assertThat(((LinearGradient) objectBoundingBox.createPaint(null)).isProportional(), is(true));
        assertThat(((LinearGradient) userSpace.createPaint(null)).isProportional(), is(false));
    }

    @Test
    public void testLinearGradientCoordinatesDefaultToLeftToRight() throws Exception {
        LinearGradient gradient = (LinearGradient) linearGradient("g").createPaint(null);
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

        LinearGradient gradient = (LinearGradient) svg.createPaint(null);
        assertThat(gradient.getStartX(), closeTo(0.25, 1e-9));
        assertThat(gradient.getEndX(), closeTo(0.75, 1e-9));
    }

    @Test
    public void testStopColourAndOpacityBecomeTheStopColour() throws Exception {
        SvgLinearGradient svg = new SvgLinearGradient();
        svg.setId("g");
        svg.getContent()
            .add(stop("0", "red", "0.5"));
        svg.getContent()
            .add(stop("1", "blue", null));

        LinearGradient gradient = (LinearGradient) svg.createPaint(null);
        assertThat(gradient.getStops()
            .get(0)
            .getColor()
            .getRed(), is(1.0));
        assertThat(gradient.getStops()
            .get(0)
            .getColor()
            .getOpacity(), closeTo(0.5, 1e-9));
        assertThat(gradient.getStops()
            .get(1)
            .getColor(), is(Color.BLUE));
    }

    /**
     * Offsets are clamped to the unit interval, and one smaller than the stop before takes the earlier value.
     */
    @Test
    public void testStopOffsetsAreClampedAndNonDecreasing() throws Exception {
        SvgLinearGradient svg = new SvgLinearGradient();
        svg.setId("g");
        svg.getContent()
            .add(stop("-1", "red", null));
        svg.getContent()
            .add(stop("60%", "green", null));
        svg.getContent()
            .add(stop("0.2", "blue", null));
        svg.getContent()
            .add(stop("5", "white", null));

        LinearGradient gradient = (LinearGradient) svg.createPaint(null);
        assertThat(gradient.getStops()
            .get(0)
            .getOffset(), is(0.0));
        assertThat(gradient.getStops()
            .get(1)
            .getOffset(), closeTo(0.6, 1e-9));
        assertThat(gradient.getStops()
            .get(2)
            .getOffset(), closeTo(0.6, 1e-9));
        assertThat(gradient.getStops()
            .get(3)
            .getOffset(), is(1.0));
    }

    @Test
    public void testGradientWithNoStopsPaintsNothing() throws Exception {
        SvgLinearGradient svg = new SvgLinearGradient();
        svg.setId("g");
        assertThat(svg.createPaint(null), is(nullValue()));
    }

    @Test
    public void testGradientWithOneStopPaintsThatColourFlat() throws Exception {
        SvgLinearGradient svg = new SvgLinearGradient();
        svg.setId("g");
        svg.getContent()
            .add(stop("0", "red", null));
        assertThat(svg.createPaint(null), is(Color.RED));
    }

    // --- radial ------------------------------------------------------------

    @Test
    public void testRadialGradientDefaults() throws Exception {
        RadialGradient gradient = (RadialGradient) radialGradient("g").createPaint(null);
        assertThat(gradient.getCenterX(), is(0.5));
        assertThat(gradient.getCenterY(), is(0.5));
        assertThat(gradient.getRadius(), is(0.5));
        assertThat(gradient.getFocusDistance(), is(0.0));
    }

    /**
     * SVG places the focal point in cartesian coordinates while JavaFX takes an angle and a distance as a fraction of the radius, so a focus directly right of the centre at half
     * the radius is angle 0, distance 0.5.
     */
    @Test
    public void testRadialFocalPointIsConvertedToPolar() throws Exception {
        SvgRadialGradient svg = radialGradient("g");
        svg.setCx("0.5");
        svg.setCy("0.5");
        svg.setR("0.4");
        svg.setFx("0.7");
        svg.setFy("0.5");

        RadialGradient gradient = (RadialGradient) svg.createPaint(null);
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

        RadialGradient gradient = (RadialGradient) svg.createPaint(null);
        assertThat(gradient.getFocusDistance(), closeTo(0.5, 1e-9));
        assertThat(gradient.getFocusAngle(), closeTo(-90.0, 1e-9));
    }

    @Test
    public void testRadialGradientWithZeroRadiusPaintsTheLastStop() throws Exception {
        SvgRadialGradient svg = radialGradient("g");
        svg.setR("0");
        assertThat(svg.createPaint(null), is(Color.BLUE));
    }

    // --- xlink:href inheritance (#18) ---------------------------------------

    @Test
    public void testAttributeInheritanceAcrossAChainOfThree() throws Exception {
        SvgLinearGradient base = linearGradient("base");
        base.setX1("0.1");
        SvgLinearGradient middle = new SvgLinearGradient();
        middle.setId("middle");
        middle.setXlinkHref("#base");
        middle.setX2("0.9");
        SvgLinearGradient top = new SvgLinearGradient();
        top.setId("top");
        top.setXlinkHref("#middle");

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(base);
        svg.getContent()
            .add(middle);
        svg.getContent()
            .add(top);

        LinearGradient gradient = (LinearGradient) top.createPaint(svg.getElementIndex());
        assertThat(gradient.getStops(), hasSize(2)); // base's stops, through middle
        assertThat(gradient.getStartX(), closeTo(0.1, 1e-9)); // base's x1
        assertThat(gradient.getEndX(), closeTo(0.9, 1e-9)); // middle's x2
    }

    @Test
    public void testStopsInheritOnlyWhenTheReferencingGradientDeclaresNone() throws Exception {
        SvgLinearGradient base = linearGradient("base"); // red, blue
        SvgLinearGradient own = new SvgLinearGradient();
        own.setId("own");
        own.setXlinkHref("#base");
        own.getContent()
            .add(stop("0", "green", null));
        own.getContent()
            .add(stop("1", "yellow", null));

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(base);
        svg.getContent()
            .add(own);

        LinearGradient gradient = (LinearGradient) own.createPaint(svg.getElementIndex());
        assertThat(gradient.getStops()
            .get(0)
            .getColor(), is(Color.GREEN));
    }

    /**
     * A linear gradient may legally reference a radial one, and vice versa - only the attributes common to both (here, {@code spreadMethod} and {@code gradientUnits}) and the
     * stops transfer; geometry specific to one type has nothing type-compatible to come from, so it falls back to its own initial value.
     */
    @Test
    public void testCrossTypeReferenceInheritsStopsAndCommonAttributesButNotGeometry() throws Exception {
        SvgRadialGradient base = radialGradient("base"); // red, blue
        base.setSpreadMethod("reflect");
        base.setGradientUnits("userSpaceOnUse");
        SvgLinearGradient linear = new SvgLinearGradient();
        linear.setId("linear");
        linear.setXlinkHref("#base");

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(base);
        svg.getContent()
            .add(linear);

        LinearGradient gradient = (LinearGradient) linear.createPaint(svg.getElementIndex());
        assertThat(gradient.getStops(), hasSize(2));
        assertThat(gradient.getCycleMethod(), is(CycleMethod.REFLECT));
        assertThat(gradient.isProportional(), is(false));
        // the initial values, not anything derived from the radial gradient's cx/cy/r
        assertThat(gradient.getStartX(), is(0.0));
        assertThat(gradient.getEndX(), is(1.0));
    }

    /**
     * Neither gradient in the cycle ever declares stops, so this must terminate with "nothing to paint" rather than hang or overflow the stack - exercised through
     * {@code createPaint} itself, the path a real document takes, rather than {@code SvgElementIndex.resolveChain} directly (already covered in {@code SvgElementIndexTest}).
     */
    @Test
    public void testACycleResolvesWithoutHangingOrOverflowing() throws Exception {
        SvgLinearGradient a = new SvgLinearGradient();
        a.setId("a");
        a.setXlinkHref("#b");
        SvgLinearGradient b = new SvgLinearGradient();
        b.setId("b");
        b.setXlinkHref("#a");

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(a);
        svg.getContent()
            .add(b);

        assertThat(a.createPaint(svg.getElementIndex()), is(nullValue()));
    }

    // --- gradientTransform (#52) -------------------------------------------

    @Test
    public void testATranslateMovesTheGradientAxis() throws Exception {
        SvgLinearGradient gradient = linearGradient("g");
        gradient.setGradientTransform("translate(10 20)");

        LinearGradient paint = (LinearGradient) gradient.createPaint(null);
        assertThat(paint.getStartX(), closeTo(10, 1e-9)); // default axis runs (0,0) to (1,0)
        assertThat(paint.getStartY(), closeTo(20, 1e-9));
        assertThat(paint.getEndX(), closeTo(11, 1e-9));
        assertThat(paint.getEndY(), closeTo(20, 1e-9));
    }

    @Test
    public void testAUniformScaleScalesTheGradientAxis() throws Exception {
        SvgLinearGradient gradient = linearGradient("g");
        gradient.setGradientTransform("scale(2)");

        LinearGradient paint = (LinearGradient) gradient.createPaint(null);
        assertThat(paint.getEndX(), closeTo(2, 1e-9));
        assertThat(paint.getEndY(), closeTo(0, 1e-9));
    }

    @Test
    public void testARotationTurnsTheGradientAxis() throws Exception {
        SvgLinearGradient gradient = linearGradient("g");
        gradient.setGradientTransform("rotate(90)");

        LinearGradient paint = (LinearGradient) gradient.createPaint(null);
        // the default axis (0,0)->(1,0) turns to point down the y axis
        assertThat(paint.getEndX(), closeTo(0, 1e-9));
        assertThat(paint.getEndY(), closeTo(1, 1e-9));
    }

    /**
     * The case the sharper criterion buys, and the one a "must be a similarity" test would wrongly reject: scaling only along the gradient's own axis stretches the axis but leaves
     * the iso-lines exactly where they were, so JavaFX can represent the result precisely.
     */
    @Test
    public void testANonUniformScaleAlignedWithTheAxisIsStillApplied() throws Exception {
        SvgLinearGradient gradient = linearGradient("g");
        gradient.setGradientTransform("scale(3 1)"); // the default axis runs along x, so this stretches only it

        LinearGradient paint = (LinearGradient) gradient.createPaint(null);
        assertThat(paint.getEndX(), closeTo(3, 1e-9));
        assertThat(paint.getEndY(), closeTo(0, 1e-9));
    }

    /** A skew tilts the iso-lines off perpendicular, which no JavaFX gradient can express. */
    @Test
    public void testASkewOnALinearGradientLeavesThePaintUntransformed() throws Exception {
        SvgLinearGradient gradient = linearGradient("g");
        gradient.setGradientTransform("skewX(45)");

        LinearGradient paint = (LinearGradient) gradient.createPaint(null);
        assertThat(paint.getStartX(), closeTo(0, 1e-9));
        assertThat(paint.getEndX(), closeTo(1, 1e-9));
        assertThat(paint.getEndY(), closeTo(0, 1e-9));
    }

    /** Across the axis rather than along it, the same scale does tilt the iso-lines - so it is refused. */
    @Test
    public void testANonUniformScaleAcrossTheAxisIsRefused() throws Exception {
        SvgLinearGradient gradient = linearGradient("g");
        gradient.setX2("0");
        gradient.setY2("1"); // axis now runs down y, so scale(3 1) is no longer aligned with it
        gradient.setGradientTransform("scale(3 1)");

        LinearGradient paint = (LinearGradient) gradient.createPaint(null);
        assertThat(paint.getEndX(), closeTo(0, 1e-9));
        assertThat(paint.getEndY(), closeTo(1, 1e-9));
    }

    @Test
    public void testASimilarityMovesARadialGradientsCentreAndRadius() throws Exception {
        SvgRadialGradient gradient = radialGradient("g");
        gradient.setGradientTransform("translate(1 2) scale(2)");

        RadialGradient paint = (RadialGradient) gradient.createPaint(null);
        // defaults are centre (0.5,0.5) radius 0.5
        assertThat(paint.getCenterX(), closeTo(1 + 2 * 0.5, 1e-9));
        assertThat(paint.getCenterY(), closeTo(2 + 2 * 0.5, 1e-9));
        assertThat(paint.getRadius(), closeTo(1.0, 1e-9));
    }

    /**
     * A similarity scales the focus's distance from the centre by exactly the factor it scales the radius by, so the ratio JavaFX stores is unchanged; only the angle moves. Worth
     * pinning, because transforming the focus point separately and re-deriving the ratio would be the obvious thing to write and would double-count the scale.
     */
    @Test
    public void testARotationMovesARadialGradientsFocusAngleButNotItsDistance() throws Exception {
        SvgRadialGradient gradient = radialGradient("g");
        gradient.setFx("0.75"); // a quarter-radius off centre, along +x, so the angle starts at 0
        gradient.setGradientTransform("rotate(90)");

        RadialGradient paint = (RadialGradient) gradient.createPaint(null);
        assertThat(paint.getFocusDistance(), closeTo(0.5, 1e-9));
        assertThat(paint.getFocusAngle(), closeTo(90, 1e-9));
    }

    /**
     * A reflection is a perfectly good similarity - a circle stays a circle - so it is applied, and the focus has to follow it. The first version of this transformed the focus by
     * adding the transform's own rotation angle, which gets a reflection 180 degrees wrong; the focus offset is transformed as a direction instead. Deliberately puts the focus
     * along +y, since a focus along +x survives {@code scale(1 -1)} either way and proves nothing.
     */
    @Test
    public void testAReflectionMovesARadialGradientsFocusToTheOtherSide() throws Exception {
        SvgRadialGradient gradient = radialGradient("g");
        gradient.setFy("0.75"); // a quarter-radius below centre, so the angle starts at +90
        gradient.setGradientTransform("scale(1 -1)");

        RadialGradient paint = (RadialGradient) gradient.createPaint(null);
        assertThat(paint.getFocusDistance(), closeTo(0.5, 1e-9));
        assertThat(paint.getFocusAngle(), closeTo(-90, 1e-9));
    }

    /** A circle has to stay a circle, so anything short of a similarity is refused for a radial gradient. */
    @Test
    public void testANonUniformScaleOnARadialGradientLeavesThePaintUntransformed() throws Exception {
        SvgRadialGradient gradient = radialGradient("g");
        gradient.setGradientTransform("scale(2 1)");

        RadialGradient paint = (RadialGradient) gradient.createPaint(null);
        assertThat(paint.getCenterX(), closeTo(0.5, 1e-9));
        assertThat(paint.getRadius(), closeTo(0.5, 1e-9));
    }

    /** A degenerate transform would collapse the gradient entirely; it is refused rather than applied. */
    @Test
    public void testASingularTransformLeavesThePaintUntransformed() throws Exception {
        SvgLinearGradient gradient = linearGradient("g");
        gradient.setGradientTransform("scale(0)");

        LinearGradient paint = (LinearGradient) gradient.createPaint(null);
        assertThat(paint.getEndX(), closeTo(1, 1e-9));
    }

    /**
     * {@code gradientTransform} is inherited through {@code xlink:href} like {@code gradientUnits} and {@code spreadMethod}. It deliberately was not before #52, on the grounds
     * that an unused value had nothing to inherit for.
     */
    @Test
    public void testGradientTransformIsInheritedThroughXlinkHref() throws Exception {
        SvgLinearGradient base = new SvgLinearGradient();
        base.setId("base");
        base.setGradientTransform("translate(10 20)");
        base.getContent()
            .add(stop("0", "red", null));
        base.getContent()
            .add(stop("1", "blue", null));

        SvgLinearGradient derived = new SvgLinearGradient();
        derived.setId("derived");
        derived.setXlinkHref("#base");

        SvgRectangle rect = new SvgRectangle();
        rect.setFill(SvgPaint.parse("url(#derived)"));
        LinearGradient paint = (LinearGradient) render(rect, base, derived).getFill();

        assertThat(paint.getStartX(), closeTo(10, 1e-9));
        assertThat(paint.getStartY(), closeTo(20, 1e-9));
    }

    // --- helpers -----------------------------------------------------------

    private static CycleMethod cycleMethodFor(String spreadMethod) {
        SvgLinearGradient svg = linearGradient("g");
        svg.setSpreadMethod(spreadMethod);
        return ((LinearGradient) svg.createPaint(null)).getCycleMethod();
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
        gradient.getContent()
            .add(stop("0", "red", null));
        gradient.getContent()
            .add(stop("1", "blue", null));
        return gradient;
    }

    private static SvgRadialGradient radialGradient(String id) {
        SvgRadialGradient gradient = new SvgRadialGradient();
        gradient.setId(id);
        gradient.getContent()
            .add(stop("0", "red", null));
        gradient.getContent()
            .add(stop("1", "blue", null));
        return gradient;
    }

    private static Shape render(SvgRectangle rect, ISvgElement... definitions) {
        SvgGroup group = new SvgGroup();
        group.getContent()
            .add(rect);
        return renderInGroup(group, definitions);
    }

    private static Shape renderInGroup(SvgGroup group, ISvgElement... definitions) {
        SvgGraphic svg = new SvgGraphic();
        if (definitions.length > 0) {
            SvgDefinitions defs = new SvgDefinitions();
            for (ISvgElement definition : definitions) {
                defs.getContent()
                    .add(definition);
            }
            svg.getContent()
                .add(defs);
        }
        svg.getContent()
            .add(group);
        return (Shape) ((Group) svg.createGroup()
            .getChildren()
            .get(0)).getChildren()
            .get(0);
    }

}
