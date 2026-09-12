package nz.co.ctg.foxglove.clip;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.SvgPaint;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Exercises #24's acceptance criteria for {@code clip-path}: renders the real node tree and snapshots it, checking
 * specific pixel colours for geometric coverage - not a pixel-perfect comparison against a reference renderer, which
 * clipping's hard, renderer-specific edge anti-aliasing makes unreasonable (per the issue itself), just a
 * self-consistent check of which regions are and are not visible. Follows {@code SvgPatternTest}'s established style.
 */
public class SvgClipPathRenderingTest {

    @BeforeAll
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @Test
    public void testClipPathClipsAShape() throws Exception {
        SvgClipPath clipPath = clipPathOf(circle(50, 50, 30));
        SvgRectangle rect = filledRect(0, 0, 100, 100, "red");
        rect.setClipPath("url(#clip)");
        clipPath.setId("clip");

        Color corner = colorAt(rect, clipPath, 5, 5);
        Color centre = colorAt(rect, clipPath, 50, 50);
        assertThat(corner, is(Color.BLUE)); // outside the circle - clipped away, background shows through
        assertThat(centre, is(Color.RED)); // inside the circle - visible
    }

    @Test
    public void testClipPathClipsAGroup() throws Exception {
        SvgClipPath clipPath = clipPathOf(circle(50, 50, 30));
        clipPath.setId("clip");

        SvgGroup group = new SvgGroup();
        group.getContent().add(filledRect(0, 0, 100, 100, "red"));
        group.setClipPath("url(#clip)");

        Color corner = colorAt(group, clipPath, 5, 5);
        Color centre = colorAt(group, clipPath, 50, 50);
        assertThat(corner, is(Color.BLUE));
        assertThat(centre, is(Color.RED));
    }

    @Test
    public void testMultipleShapesInOneClipPathUnion() throws Exception {
        SvgClipPath clipPath = clipPathOf(new SvgRectangle(0, 0, 20, 20), new SvgRectangle(80, 80, 20, 20));
        clipPath.setId("clip");
        SvgRectangle rect = filledRect(0, 0, 100, 100, "red");
        rect.setClipPath("url(#clip)");

        assertThat(colorAt(rect, clipPath, 5, 5), is(Color.RED)); // inside the first clip rect
        assertThat(colorAt(rect, clipPath, 90, 90), is(Color.RED)); // inside the second clip rect
        assertThat(colorAt(rect, clipPath, 50, 50), is(Color.BLUE)); // outside both - not unioned in
    }

    @Test
    public void testObjectBoundingBoxScalesTheClipToTheTarget() throws Exception {
        // a unit-square clip rect under objectBoundingBox covers the whole target regardless of its actual size
        SvgClipPath clipPath = clipPathOf(new SvgRectangle(0, 0, 1, 1));
        clipPath.setId("clip");
        clipPath.setClipPathUnits("objectBoundingBox");
        SvgRectangle rect = filledRect(0, 0, 200, 80, "red");
        rect.setClipPath("url(#clip)");

        assertThat(colorAt(rect, clipPath, 199, 79), is(Color.RED));
        assertThat(colorAt(rect, clipPath, 5, 5), is(Color.RED));
    }

    @Test
    public void testUserSpaceOnUseTreatsCoordinatesAsLiteralPixels() throws Exception {
        // the default: the same 0,0 to 1,1 rect used above is now a literal 1x1 pixel clip, not the whole target
        SvgClipPath clipPath = clipPathOf(new SvgRectangle(0, 0, 1, 1));
        clipPath.setId("clip");
        SvgRectangle rect = filledRect(0, 0, 200, 80, "red");
        rect.setClipPath("url(#clip)");

        assertThat(colorAt(rect, clipPath, 50, 50), is(Color.BLUE));
    }

    @Test
    public void testNestedClipPathIntersectsRatherThanUnions() throws Exception {
        SvgClipPath inner = clipPathOf(new SvgRectangle(40, 0, 60, 100));
        inner.setId("inner");
        SvgClipPath outer = clipPathOf(new SvgRectangle(0, 0, 60, 100));
        outer.setId("outer");
        outer.setClipPath("url(#inner)");

        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(inner);
        defs.getContent().add(outer);
        SvgRectangle rect = filledRect(0, 0, 100, 100, "red");
        rect.setClipPath("url(#outer)");

        assertThat(colorAt(rect, defs, 50, 50), is(Color.RED)); // inside both 0-60 and 40-100 - the overlap
        assertThat(colorAt(rect, defs, 10, 50), is(Color.BLUE)); // inside outer only, not inner
        assertThat(colorAt(rect, defs, 90, 50), is(Color.BLUE)); // inside inner only, not outer
    }

    @Test
    public void testClipRuleEvenOddHollowsOutASelfOverlappingPath() throws Exception {
        String d = "M0,0 L100,0 L100,100 L0,100 Z M20,20 L80,20 L80,80 L20,80 Z";

        SvgClipPath nonzero = clipPathOf(path(d));
        nonzero.setId("nonzero");
        SvgClipPath evenodd = clipPathOf(path(d));
        evenodd.setId("evenodd");
        ((SvgPath) evenodd.getContent().get(0)).setClipRule("evenodd");

        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(nonzero);
        defs.getContent().add(evenodd);

        SvgRectangle nonzeroTarget = filledRect(0, 0, 100, 100, "red");
        nonzeroTarget.setClipPath("url(#nonzero)");
        SvgRectangle evenoddTarget = filledRect(0, 0, 100, 100, "red");
        evenoddTarget.setClipPath("url(#evenodd)");

        // the ring between the two squares is filled either way
        assertThat(colorAt(nonzeroTarget, defs, 10, 10), is(Color.RED));
        assertThat(colorAt(evenoddTarget, defs, 10, 10), is(Color.RED));
        // the centre (inside both nested squares) is filled under nonzero, hollowed out under evenodd
        assertThat(colorAt(nonzeroTarget, defs, 50, 50), is(Color.RED));
        assertThat(colorAt(evenoddTarget, defs, 50, 50), is(Color.BLUE));
    }

    @Test
    public void testACyclicClipPathReferenceResolvesWithoutHangingOrOverflowing() throws Exception {
        SvgClipPath a = new SvgClipPath();
        a.setId("a");
        a.setClipPath("url(#b)");
        SvgClipPath b = new SvgClipPath();
        b.setId("b");
        b.setClipPath("url(#a)");

        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(a);
        defs.getContent().add(b);
        SvgRectangle rect = filledRect(0, 0, 100, 100, "red");
        rect.setClipPath("url(#a)");

        // resolves to fully clipped away (empty at the repeat) rather than hanging - either way, no crash
        assertThat(colorAt(rect, defs, 50, 50), is(Color.BLUE));
    }

    @Test
    public void testAnUnresolvableClipPathReferenceLeavesTheNodeUnclipped() throws Exception {
        SvgRectangle rect = filledRect(0, 0, 100, 100, "red");
        rect.setClipPath("url(#missing)");

        SvgGraphic svg = new SvgGraphic();
        Node node = onFxThread(() -> rect.createGraphic(RenderContext.root(svg.getElementIndex(), 0, 0)));
        assertThat(node.getClip(), is(nullValue()));
    }

    // --- helpers -------------------------------------------------------------

    private static SvgClipPath clipPathOf(nz.co.ctg.foxglove.ISvgElement... children) {
        SvgClipPath clipPath = new SvgClipPath();
        for (nz.co.ctg.foxglove.ISvgElement child : children) {
            clipPath.getContent().add(child);
        }
        return clipPath;
    }

    private static SvgCircle circle(double cx, double cy, double r) {
        SvgCircle circle = new SvgCircle();
        circle.setCentreX(cx);
        circle.setCentreY(cy);
        circle.setRadius(r);
        return circle;
    }

    private static SvgPath path(String d) {
        SvgPath path = new SvgPath();
        path.setD(d);
        return path;
    }

    private static SvgRectangle filledRect(double x, double y, double width, double height, String fill) {
        SvgRectangle rect = new SvgRectangle(x, y, width, height);
        rect.setFill(SvgPaint.parse(fill));
        return rect;
    }

    /**
     * Builds an index over {@code definitionsHolder} (a {@code <clipPath>} or {@code <defs>} the target's reference
     * needs to resolve), renders {@code target} against it, and returns the colour at the given point in the
     * rendered snapshot.
     * <p>
     * The snapshot viewport is fixed at a generous 300x300 - a clip shrinks a node's own
     * {@code getBoundsInLocal()}, and {@code snapshot()} would otherwise auto-size the output image to those
     * (possibly tiny, or even empty) bounds instead of the full area a test point might fall in.
     */
    private static Color colorAt(nz.co.ctg.foxglove.FxGraphic<? extends Node> target, nz.co.ctg.foxglove.ISvgElement definitionsHolder, double x,
        double y) throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(definitionsHolder);

        return onFxThread(() -> {
            RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
            Node node = target.createGraphic(context);
            Group root = new Group(node);
            new Scene(root);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.BLUE);
            params.setViewport(new javafx.geometry.Rectangle2D(0, 0, 300, 300));
            WritableImage image = root.snapshot(params, null);
            return image.getPixelReader().getColor((int) x, (int) y);
        });
    }

}
