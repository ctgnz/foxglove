package nz.co.ctg.foxglove.paint;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.SvgPaint;
import nz.co.ctg.foxglove.type.ViewBox;

/**
 * Resolving a pattern rasterises via {@code Node.snapshot(...)}, which - per the JavaFX javadoc - throws {@link IllegalStateException} off the JavaFX Application Thread.
 * {@link JavaFxTestSupport} starts the toolkit (shared with other test classes, since it can only be started once per JVM) and hands each {@code createPaint} call to that thread.
 */
public class SvgPatternTest {

    private static final double DELTA = 1e-6;

    @BeforeAll
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    private static Size px(double value) {
        return new Size(value, SizeUnits.PX);
    }

    private static Size percent(double value) {
        return new Size(value, SizeUnits.PERCENT);
    }

    private static SvgRectangle rect(double x, double y, double width, double height, String fill) {
        SvgRectangle rect = new SvgRectangle();
        rect.setX(px(x));
        rect.setY(px(y));
        rect.setWidth(px(width));
        rect.setHeight(px(height));
        rect.setFill(SvgPaint.parse(fill));
        return rect;
    }

    @Test
    public void testObjectBoundingBoxIsTheDefaultPatternUnits() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setWidth(percent(50));
        pattern.setHeight(percent(50));

        RenderContext context = RenderContext.root(null, 0, 0)
            .withObjectBoundingBox(new BoundingBox(0, 0, 100, 50));
        ImagePattern paint = onFxThread(() -> (ImagePattern) pattern.createPaint(context));

        assertThat(paint.getX(), is(0.0));
        assertThat(paint.getY(), is(0.0));
        assertThat(paint.getWidth(), is(50.0));
        assertThat(paint.getHeight(), is(25.0));
        assertThat(paint.isProportional(), is(false));
    }

    @Test
    public void testObjectBoundingBoxAnchorIncludesTheBoundingBoxOrigin() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setX(percent(10));
        pattern.setY(percent(20));
        pattern.setWidth(percent(50));
        pattern.setHeight(percent(50));

        RenderContext context = RenderContext.root(null, 0, 0)
            .withObjectBoundingBox(new BoundingBox(10, 20, 100, 50));
        ImagePattern paint = onFxThread(() -> (ImagePattern) pattern.createPaint(context));

        assertThat(paint.getX(), closeTo(20.0, DELTA)); // 10 + 0.1*100
        assertThat(paint.getY(), closeTo(30.0, DELTA)); // 20 + 0.2*50
    }

    @Test
    public void testUserSpaceOnUseResolvesAbsoluteLengthsAgainstTheViewport() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setPatternUnits("userSpaceOnUse");
        pattern.setX(px(10));
        pattern.setY(px(20));
        pattern.setWidth(px(30));
        pattern.setHeight(px(40));

        RenderContext context = RenderContext.root(null, 200, 100);
        ImagePattern paint = onFxThread(() -> (ImagePattern) pattern.createPaint(context));

        assertThat(paint.getX(), is(10.0));
        assertThat(paint.getY(), is(20.0));
        assertThat(paint.getWidth(), is(30.0));
        assertThat(paint.getHeight(), is(40.0));
    }

    @Test
    public void testObjectBoundingBoxWithNoBoundsIsDegenerate() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setWidth(percent(50));
        pattern.setHeight(percent(50));

        RenderContext context = RenderContext.root(null, 0, 0);
        assertThat(onFxThread(() -> pattern.createPaint(context)), is(nullValue()));
    }

    @Test
    public void testNonPositiveSizeIsDegenerate() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setPatternUnits("userSpaceOnUse");
        pattern.setWidth(px(0));
        pattern.setHeight(px(30));

        RenderContext context = RenderContext.root(null, 200, 100);
        assertThat(onFxThread(() -> pattern.createPaint(context)), is(nullValue()));
    }

    @Test
    public void testRepeatedResolutionAtTheSameTileSizeReusesTheRasterisedImage() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setPatternUnits("userSpaceOnUse");
        pattern.setWidth(px(20));
        pattern.setHeight(px(20));
        pattern.getContent()
            .add(rect(0, 0, 20, 20, "red"));

        RenderContext context = RenderContext.root(null, 200, 100);
        Image first = onFxThread(() -> ((ImagePattern) pattern.createPaint(context)).getImage());
        Image second = onFxThread(() -> ((ImagePattern) pattern.createPaint(context)).getImage());

        assertThat(second, is(sameInstance(first)));
    }

    @Test
    public void testAViewBoxScalesTheContentToFillTheTile() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setPatternUnits("userSpaceOnUse");
        pattern.setWidth(px(50));
        pattern.setHeight(px(50));
        pattern.setViewBox(new ViewBox(px(0), px(0), px(10), px(10)));
        pattern.getContent()
            .add(rect(0, 0, 10, 10, "red"));

        RenderContext context = RenderContext.root(null, 0, 0);
        // a corner well outside the un-scaled 10x10 content, but still inside the 50x50 tile, is red only if the
        // viewBox transform stretched the content to fill the tile
        Color color = onFxThread(() -> colorAt((ImagePattern) pattern.createPaint(context), 45, 45));
        assertThat(color, is(Color.RED));
    }

    @Test
    public void testPatternContentUnitsObjectBoundingBoxScalesContentByTheBoundingBox() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setPatternUnits("userSpaceOnUse");
        pattern.setPatternContentUnits("objectBoundingBox");
        pattern.setX(px(0));
        pattern.setY(px(0));
        pattern.setWidth(px(50));
        pattern.setHeight(px(50));
        // a one-unit-square rect becomes bbox-sized (50x50) once the content-units scale is applied
        pattern.getContent()
            .add(rect(0, 0, 1, 1, "red"));

        Bounds bbox = new BoundingBox(0, 0, 50, 50);
        RenderContext context = RenderContext.root(null, 0, 0)
            .withObjectBoundingBox(bbox);
        Color color = onFxThread(() -> colorAt((ImagePattern) pattern.createPaint(context), 25, 25));
        assertThat(color, is(Color.RED));
    }

    @Test
    public void testPatternContentUnitsDefaultsToUserSpaceOnUse() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setPatternUnits("userSpaceOnUse");
        pattern.setWidth(px(50));
        pattern.setHeight(px(50));
        // no patternContentUnits set: a one-unit-square rect stays a one-unit-square, not bbox-sized
        pattern.getContent()
            .add(rect(0, 0, 1, 1, "red"));

        Bounds bbox = new BoundingBox(0, 0, 50, 50);
        RenderContext context = RenderContext.root(null, 0, 0)
            .withObjectBoundingBox(bbox);
        Color color = onFxThread(() -> colorAt((ImagePattern) pattern.createPaint(context), 25, 25));
        assertThat(color, is(Color.TRANSPARENT));
    }

    /**
     * A single shape somewhere in the tile survived the bug this guards against: the rasterised image came out the declared tile size, but {@code Node.snapshot} had actually
     * captured a smaller, shifted region of the content and stretched it to fill that image - invisible with one shape covering most of the frame, but a tile with several
     * distinctly coloured regions across its full extent comes out uniformly wrong instead of showing all of them in their right places.
     */
    @Test
    public void testMultipleContentElementsAcrossTheFullTileRasteriseInTheRightPlaces() throws Exception {
        SvgPattern pattern = new SvgPattern();
        pattern.setPatternUnits("userSpaceOnUse");
        pattern.setWidth(px(10));
        pattern.setHeight(px(10));
        pattern.getContent()
            .add(rect(0, 0, 10, 10, "white"));
        pattern.getContent()
            .add(rect(0, 0, 5, 5, "black"));
        pattern.getContent()
            .add(rect(5, 5, 5, 5, "black"));

        RenderContext context = RenderContext.root(null, 0, 0);
        ImagePattern paint = onFxThread(() -> (ImagePattern) pattern.createPaint(context));

        // the output image is the declared tile at exactly SvgPattern.rasterScale resolution - not a smaller,
        // shifted capture stretched to fill an image of that size
        assertThat(paint.getImage()
            .getWidth(), is(10.0 * SvgPattern.rasterScale));
        assertThat(paint.getImage()
            .getHeight(), is(10.0 * SvgPattern.rasterScale));

        assertThat(colorAt(paint, 2, 2), is(Color.BLACK)); // top-left quadrant
        assertThat(colorAt(paint, 7, 2), is(Color.WHITE)); // top-right quadrant
        assertThat(colorAt(paint, 2, 7), is(Color.WHITE)); // bottom-left quadrant
        assertThat(colorAt(paint, 7, 7), is(Color.BLACK)); // bottom-right quadrant
    }

    // --- xlink:href inheritance (#18) ---------------------------------------

    @Test
    public void testContentInheritsFromTheReferencedPatternWhenThisOneHasNone() throws Exception {
        SvgPattern base = new SvgPattern();
        base.setId("base");
        base.getContent()
            .add(rect(0, 0, 10, 10, "red"));

        SvgPattern own = new SvgPattern();
        own.setId("own");
        own.setXlinkHref("#base");
        own.setPatternUnits("userSpaceOnUse");
        own.setWidth(px(10));
        own.setHeight(px(10));
        // no content of its own - it comes from "base" instead

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(base);
        svg.getContent()
            .add(own);

        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        Color color = onFxThread(() -> colorAt((ImagePattern) own.createPaint(context), 5, 5));
        assertThat(color, is(Color.RED));
    }

    @Test
    public void testAttributesInheritFromTheReferencedPatternWhenThisOneDoesNotSpecifyThem() throws Exception {
        SvgPattern base = new SvgPattern();
        base.setId("base");
        base.setPatternUnits("userSpaceOnUse");
        base.setWidth(px(20));
        base.setHeight(px(20));

        SvgPattern own = new SvgPattern();
        own.setId("own");
        own.setXlinkHref("#base");
        own.getContent()
            .add(rect(0, 0, 20, 20, "red")); // its own content, sized to the inherited tile

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(base);
        svg.getContent()
            .add(own);

        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        ImagePattern paint = onFxThread(() -> (ImagePattern) own.createPaint(context));
        assertThat(paint.getWidth(), is(20.0));
        assertThat(paint.getHeight(), is(20.0));
        assertThat(colorAt(paint, 10, 10), is(Color.RED));
    }

    /**
     * Neither pattern in the cycle declares a width or height, so this must terminate as a degenerate (zero-size) tile rather than hang or overflow the stack.
     */
    @Test
    public void testACycleResolvesWithoutHangingOrOverflowing() throws Exception {
        SvgPattern a = new SvgPattern();
        a.setId("a");
        a.setPatternUnits("userSpaceOnUse");
        a.setXlinkHref("#b");
        SvgPattern b = new SvgPattern();
        b.setId("b");
        b.setXlinkHref("#a");

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(a);
        svg.getContent()
            .add(b);

        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        assertThat(onFxThread(() -> a.createPaint(context)), is(nullValue()));
    }

    /**
     * Independent of whatever the image's actual dimensions turn out to be - unlike the tests above, which measure the image and scale into it, exactly the blind spot that let the
     * bug this test method's sibling guards against slip past every other test in this class.
     */
    private static Color colorAt(ImagePattern paint, double tileX, double tileY) {
        PixelReader reader = paint.getImage()
            .getPixelReader();
        return reader.getColor((int) Math.round(tileX * SvgPattern.rasterScale), (int) Math.round(tileY * SvgPattern.rasterScale));
    }

}
