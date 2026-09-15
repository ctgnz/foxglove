package nz.co.ctg.foxglove.animate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.Optional;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import org.junit.jupiter.api.Test;

/**
 * Exercises #31's acceptance criteria - entirely independent of any animation actually running, per the issue's own acceptance criterion: geometry attributes on the four
 * numeric-property shapes resolve to the right property, paint/colour attributes resolve and parse, and an unmappable attribute name is skipped (returns {@link Optional#empty()})
 * rather than throwing.
 */
public class SvgAttributeRegistryTest {

    // --- Rectangle -----------------------------------------------------------

    @Test
    public void testRectangleGeometryResolvesToTheRightProperty() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        assertResolvesAndWrites(rect, "x", rect::getX);
        assertResolvesAndWrites(rect, "y", rect::getY);
        assertResolvesAndWrites(rect, "width", rect::getWidth);
        assertResolvesAndWrites(rect, "height", rect::getHeight);
        // rx/ry are NOT 1:1 like the above - see testRectangleRxRyDoubleTheParsedValue, #99.
    }

    /**
     * #99: the same radius-vs-diameter doubling #93 already fixed for {@code SvgRectangle.createShape}'s static resolution also has to happen here, in the
     * {@code <animate>}/{@code <set>} binding - otherwise an animated {@code rx}/{@code ry} lands on {@code arcWidth}/{@code arcHeight} at half the intended size. Deliberately its
     * own test, not folded into {@link #testRectangleGeometryResolvesToTheRightProperty}'s generic {@code assertResolvesAndWrites} helper, which assumes a parsed value writes back
     * unchanged - true for every other geometry attribute, but not this one.
     */
    @Test
    public void testRectangleRxRyDoubleTheParsedValue() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        SvgAttributeBinding<Number> rx = resolveTyped(rect, "rx");
        rx.property()
            .setValue(rx.parser()
                .apply("15")
                .orElseThrow());
        assertThat("rx=15 is a radius; arcWidth is the full diameter", rect.getArcWidth(), closeTo(30.0, 1e-9));

        SvgAttributeBinding<Number> ry = resolveTyped(rect, "ry");
        ry.property()
            .setValue(ry.parser()
                .apply("8")
                .orElseThrow());
        assertThat("ry=8 is a radius; arcHeight is the full diameter", rect.getArcHeight(), closeTo(16.0, 1e-9));
    }

    @Test
    public void testRectangleParsesPixelAndPercentValues() {
        SvgAttributeBinding<?> binding = resolve(new Rectangle(), "width");
        assertThat(binding.parser()
            .apply("42"), is(Optional.of(42.0)));
        assertThat(binding.parser()
            .apply("42px"), is(Optional.of(42.0)));
    }

    // --- Circle ----------------------------------------------------------------

    @Test
    public void testCircleGeometryResolvesToTheRightProperty() {
        Circle circle = new Circle(5, 5, 5);
        assertResolvesAndWrites(circle, "cx", circle::getCenterX);
        assertResolvesAndWrites(circle, "cy", circle::getCenterY);
        assertResolvesAndWrites(circle, "r", circle::getRadius);
    }

    // --- Ellipse -----------------------------------------------------------

    @Test
    public void testEllipseGeometryResolvesToTheRightProperty() {
        Ellipse ellipse = new Ellipse(5, 5, 5, 3);
        assertResolvesAndWrites(ellipse, "cx", ellipse::getCenterX);
        assertResolvesAndWrites(ellipse, "cy", ellipse::getCenterY);
        assertResolvesAndWrites(ellipse, "rx", ellipse::getRadiusX);
        assertResolvesAndWrites(ellipse, "ry", ellipse::getRadiusY);
    }

    // --- Line --------------------------------------------------------------

    @Test
    public void testLineGeometryResolvesToTheRightProperty() {
        Line line = new Line(0, 0, 10, 10);
        assertResolvesAndWrites(line, "x1", line::getStartX);
        assertResolvesAndWrites(line, "y1", line::getStartY);
        assertResolvesAndWrites(line, "x2", line::getEndX);
        assertResolvesAndWrites(line, "y2", line::getEndY);
    }

    // --- fill/stroke ---------------------------------------------------------

    @Test
    public void testFillResolvesAndParsesAPlainColor() {
        Rectangle rect = new Rectangle();
        SvgAttributeBinding<Paint> binding = resolveTyped(rect, "fill");
        Paint parsed = binding.parser()
            .apply("red")
            .orElseThrow();
        binding.property()
            .setValue(parsed);
        assertThat(rect.getFill(), is(Color.RED));
    }

    @Test
    public void testStrokeResolvesAndParsesAPlainColor() {
        Rectangle rect = new Rectangle();
        SvgAttributeBinding<Paint> binding = resolveTyped(rect, "stroke");
        Paint parsed = binding.parser()
            .apply("blue")
            .orElseThrow();
        binding.property()
            .setValue(parsed);
        assertThat(rect.getStroke(), is(Color.BLUE));
    }

    @Test
    public void testFillUrlReferenceValueParsesToEmptyButThePropertyStillResolves() {
        SvgAttributeBinding<?> binding = resolve(new Rectangle(), "fill");
        assertThat(binding, is(notNullValue()));
        assertThat(binding.parser()
            .apply("url(#grad)")
            .isEmpty(), is(true));
    }

    // --- opacity / stroke-width ----------------------------------------------

    @Test
    public void testOpacityResolvesAndParsesAPercentage() {
        Rectangle rect = new Rectangle();
        SvgAttributeBinding<Number> binding = resolveTyped(rect, "opacity");
        double parsed = binding.parser()
            .apply("50%")
            .orElseThrow()
            .doubleValue();
        binding.property()
            .setValue(parsed);
        assertThat(rect.getOpacity(), closeTo(0.5, 1e-9));
    }

    @Test
    public void testStrokeWidthResolvesAndParses() {
        Rectangle rect = new Rectangle();
        SvgAttributeBinding<Number> binding = resolveTyped(rect, "stroke-width");
        binding.property()
            .setValue(binding.parser()
                .apply("3")
                .orElseThrow());
        assertThat(rect.getStrokeWidth(), closeTo(3.0, 1e-9));
    }

    // --- unmappable ----------------------------------------------------------

    @Test
    public void testGeometryAttributeOfTheWrongShapeTypeIsUnmappable() {
        assertThat(SvgAttributeRegistry.resolve(new Rectangle(), "cx")
            .isEmpty(), is(true));
    }

    @Test
    public void testPointsOnAPolygonIsUnmappable() {
        assertThat(SvgAttributeRegistry.resolve(new Polygon(), "points")
            .isEmpty(), is(true));
    }

    @Test
    public void testAnyAttributeOnANonShapeNodeIsUnmappable() {
        assertThat(SvgAttributeRegistry.resolve(new Group(), "opacity")
            .isEmpty(), is(true));
    }

    @Test
    public void testUnknownAttributeNameIsUnmappable() {
        assertThat(SvgAttributeRegistry.resolve(new Rectangle(), "not-a-real-attribute")
            .isEmpty(), is(true));
    }

    // --- helpers -----------------------------------------------------------

    private static SvgAttributeBinding<?> resolve(javafx.scene.Node node, String attributeName) {
        return SvgAttributeRegistry.resolve(node, attributeName)
            .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private static <T> SvgAttributeBinding<T> resolveTyped(javafx.scene.Node node, String attributeName) {
        return (SvgAttributeBinding<T>) resolve(node, attributeName);
    }

    private static void assertResolvesAndWrites(javafx.scene.Node node, String attributeName, java.util.function.DoubleSupplier readBack) {
        SvgAttributeBinding<Number> binding = resolveTyped(node, attributeName);
        double value = binding.parser()
            .apply("77")
            .orElseThrow()
            .doubleValue();
        binding.property()
            .setValue(value);
        assertThat(readBack.getAsDouble(), closeTo(77.0, 1e-9));
    }

}
