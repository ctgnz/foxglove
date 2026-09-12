package nz.co.ctg.foxglove.shape;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.RenderContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.shape.Rectangle;

/**
 * Exercises #93's fix to {@code rx}/{@code ry} resolution: JavaFX's {@code arcWidth}/{@code arcHeight} are a
 * diameter (matching AWT's {@code RoundRectangle2D} convention), not the radius SVG's own {@code rx}/{@code ry}
 * attributes are - and SVG defaults whichever of the two is omitted to the other's value, clamping either to half
 * its own dimension, rather than treating an omitted attribute as {@code 0}.
 */
public class SvgRectangleRenderingTest {

    @Test
    public void testNeitherRxNorRyGivenHasNoRounding() {
        Rectangle rect = render(r -> { });
        assertThat(rect.getArcWidth(), closeTo(0.0, 1e-9));
        assertThat(rect.getArcHeight(), closeTo(0.0, 1e-9));
    }

    @Test
    public void testOnlyRxGivenDefaultsRyToTheSameValue() {
        Rectangle rect = render(r -> r.setRadiusX(10.0));
        assertThat(rect.getArcWidth(), closeTo(20.0, 1e-9));
        assertThat(rect.getArcHeight(), closeTo(20.0, 1e-9));
    }

    @Test
    public void testOnlyRyGivenDefaultsRxToTheSameValue() {
        Rectangle rect = render(r -> r.setRadiusY(15.0));
        assertThat(rect.getArcWidth(), closeTo(30.0, 1e-9));
        assertThat(rect.getArcHeight(), closeTo(30.0, 1e-9));
    }

    @Test
    public void testBothGivenAreUsedIndependently() {
        Rectangle rect = render(r -> {
            r.setRadiusX(5.0);
            r.setRadiusY(8.0);
        });
        assertThat(rect.getArcWidth(), closeTo(10.0, 1e-9));
        assertThat(rect.getArcHeight(), closeTo(16.0, 1e-9));
    }

    @Test
    public void testOversizedRxIsClampedToHalfTheWidth() {
        // width=50 -> half-width=25; rx=30 exceeds it
        Rectangle rect = render(r -> r.setRadiusX(30.0));
        assertThat(rect.getArcWidth(), closeTo(50.0, 1e-9));
    }

    @Test
    public void testOversizedRyIsClampedToHalfTheHeight() {
        // height=80 -> half-height=40; ry=50 exceeds it
        Rectangle rect = render(r -> {
            r.setRadiusX(30.0);
            r.setRadiusY(50.0);
        });
        assertThat(rect.getArcHeight(), closeTo(80.0, 1e-9));
    }

    private static Rectangle render(java.util.function.Consumer<SvgRectangle> configure) {
        SvgRectangle rect = new SvgRectangle(0, 0, 50, 80);
        configure.accept(rect);
        return rect.createGraphic(RenderContext.root(null, 0, 0));
    }

}
