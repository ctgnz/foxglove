package nz.co.ctg.foxglove;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.paint.SvgPattern;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

/**
 * Exercises pattern references against a document that has been through the parser, rather than one assembled in
 * memory, following {@link SvgPaintParseTest}'s convention for gradients. Resolving a pattern rasterises via
 * {@code Node.snapshot(...)}, which requires the JavaFX Application Thread - see {@link JavaFxTestSupport}.
 */
public class PatternParseTest {

    private Group rendered;

    @BeforeAll
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @BeforeEach
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/patterns.svg"));
        assertThat(svg, notNullValue());
        rendered = onFxThread(svg::createGroup);
    }

    @Test
    public void testUserSpaceOnUseTilingFillsWithAPattern() throws Exception {
        ImagePattern paint = (ImagePattern) shape("checkerFill").getFill();
        assertThat(paint, is(instanceOf(ImagePattern.class)));
        // the checkerboard, not four quadrants of a single flattened colour - see SvgPatternTest's regression test
        // for how easily a tile like this comes out uniformly wrong instead
        assertThat(colorAt(paint, 2, 2), is(Color.BLACK));
        assertThat(colorAt(paint, 7, 2), is(Color.WHITE));
        assertThat(colorAt(paint, 2, 7), is(Color.WHITE));
        assertThat(colorAt(paint, 7, 7), is(Color.BLACK));
    }

    @Test
    public void testObjectBoundingBoxPatternResolvesAgainstEachTargetsOwnBounds() throws Exception {
        ImagePattern small = (ImagePattern) shape("dotSmall").getFill();
        ImagePattern wide = (ImagePattern) shape("dotWide").getFill();
        // dotSmall is 40x40, dotWide is 100x40: a 0.25 fraction of each gives a different absolute tile size
        assertThat(small.getWidth(), is(10.0));
        assertThat(wide.getWidth(), is(25.0));
    }

    @Test
    public void testPatternContentUnitsObjectBoundingBoxFillsWithAPattern() throws Exception {
        ImagePattern paint = (ImagePattern) shape("stripesFill").getFill();
        assertThat(paint, is(instanceOf(ImagePattern.class)));
        // the stripe (0.1 of the 80-wide bounding box = 8, half the 16-wide tile), not the whole tile solid purple
        assertThat(colorAt(paint, 2, 25), is(Color.PURPLE));
        assertThat(colorAt(paint, 12, 25), is(Color.TRANSPARENT));
    }

    @Test
    public void testPatternOnAStroke() throws Exception {
        Paint stroke = shape("strokedWithPattern").getStroke();
        assertThat(stroke, is(instanceOf(ImagePattern.class)));
    }

    private Shape shape(String id) {
        for (Node node : rendered.getChildren()) {
            if (id.equals(node.getId())) {
                return (Shape) node;
            }
        }
        throw new AssertionError("no rendered shape with id " + id);
    }

    private static Color colorAt(ImagePattern paint, double tileX, double tileY) {
        PixelReader reader = paint.getImage().getPixelReader();
        return reader.getColor((int) Math.round(tileX * SvgPattern.rasterScale), (int) Math.round(tileY * SvgPattern.rasterScale));
    }

}
