package nz.co.ctg.foxglove.clip;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.paint.SvgStop;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.SvgPaint;

/**
 * Exercises #25's acceptance criteria for {@code mask}: an opaque-rect mask clips, a gradient mask gives a soft edge, both {@code maskUnits}/{@code maskContentUnits} resolve, and
 * a degenerate or missing mask degrades visibly (fully clipped, or unchanged) rather than rendering silently wrong. Follows {@code SvgClipPathRenderingTest}'s
 * snapshot-and-sample-pixels style, sampling alpha rather than colour since masking's effect is on coverage.
 */
public class SvgMaskRenderingTest {

    @BeforeAll
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @Test
    public void testOpaqueRectMaskClipsCorrectly() throws Exception {
        SvgMask mask = maskOf(rect(0, 0, 50, 100, "white"));
        mask.setId("mask");
        SvgRectangle target = filledRect(0, 0, 100, 100, "red");
        target.setMask("url(#mask)");

        assertThat(alphaAt(target, mask, 25, 50), closeTo(1.0, 1e-6));
        assertThat(alphaAt(target, mask, 75, 50), closeTo(0.0, 1e-6));
    }

    @Test
    public void testGradientMaskGivesASoftEdge() throws Exception {
        SvgLinearGradient gradient = new SvgLinearGradient();
        gradient.setId("grad");
        SvgStop white = new SvgStop();
        white.setOffset("0");
        white.setStopColor("white");
        SvgStop black = new SvgStop();
        black.setOffset("1");
        black.setStopColor("black");
        gradient.getContent()
            .add(white);
        gradient.getContent()
            .add(black);

        SvgMask mask = maskOf(rect(0, 0, 100, 100, null));
        mask.setId("mask");
        ((SvgRectangle) mask.getContent()
            .get(0)).setFill(SvgPaint.parse("url(#grad)"));

        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(gradient);
        defs.getContent()
            .add(mask);

        SvgRectangle target = filledRect(0, 0, 100, 100, "red");
        target.setMask("url(#mask)");

        double near = alphaAt(target, defs, 10, 50);
        double middle = alphaAt(target, defs, 50, 50);
        double far = alphaAt(target, defs, 90, 50);
        assertThat(near, greaterThan(middle));
        assertThat(middle, greaterThan(far));
    }

    @Test
    public void testMaskUnitsObjectBoundingBoxIsTheDefault() throws Exception {
        // the default -10%/-10%/120%/120% region, relative to the target's own bounding box, covers it entirely
        SvgMask mask = maskOf(rect(0, 0, 1, 1, "white"));
        mask.setId("mask");
        mask.setMaskContentUnits("objectBoundingBox");
        SvgRectangle target = filledRect(20, 30, 100, 50, "red");
        target.setMask("url(#mask)");

        assertThat(alphaAt(target, mask, 21, 31), closeTo(1.0, 1e-6));
        assertThat(alphaAt(target, mask, 119, 79), closeTo(1.0, 1e-6));
    }

    @Test
    public void testMaskUnitsUserSpaceOnUseTreatsCoordinatesAsLiteralPixels() throws Exception {
        SvgMask mask = maskOf(rect(0, 0, 10, 10, "white"));
        mask.setId("mask");
        mask.setMaskUnits("userSpaceOnUse");
        SvgRectangle target = filledRect(0, 0, 100, 100, "red");
        target.setMask("url(#mask)");

        // the mask region itself defaults to -10%/120% of the VIEWPORT under userSpaceOnUse (irrelevant here, since
        // the actual mask CONTENT - a 10x10 white rect at the origin - is what's being exercised): far outside that
        // small rect is unmasked-away, near it is not
        assertThat(alphaAt(target, mask, 5, 5), closeTo(1.0, 1e-6));
        assertThat(alphaAt(target, mask, 50, 50), closeTo(0.0, 1e-6));
    }

    @Test
    public void testMaskContentUnitsObjectBoundingBoxScalesContentByTheTargetBoundingBox() throws Exception {
        // a unit-square white rect, scaled by the target's own 100x100 bounding box, covers it entirely
        SvgMask mask = maskOf(rect(0, 0, 1, 1, "white"));
        mask.setId("mask");
        mask.setMaskContentUnits("objectBoundingBox");
        SvgRectangle target = filledRect(0, 0, 100, 100, "red");
        target.setMask("url(#mask)");

        assertThat(alphaAt(target, mask, 5, 5), closeTo(1.0, 1e-6));
        assertThat(alphaAt(target, mask, 95, 95), closeTo(1.0, 1e-6));
    }

    @Test
    public void testMaskContentUnitsDefaultsToUserSpaceOnUse() throws Exception {
        // the same unit-square rect, WITHOUT maskContentUnits, stays a literal 1x1 pixel - not scaled to the target
        SvgMask mask = maskOf(rect(0, 0, 1, 1, "white"));
        mask.setId("mask");
        SvgRectangle target = filledRect(0, 0, 100, 100, "red");
        target.setMask("url(#mask)");

        assertThat(alphaAt(target, mask, 50, 50), closeTo(0.0, 1e-6));
    }

    @Test
    public void testZeroWidthMaskDisablesRenderingEntirely() throws Exception {
        SvgMask mask = maskOf(rect(0, 0, 100, 100, "white"));
        mask.setId("mask");
        mask.setWidth(new Size(0, SizeUnits.PX));
        SvgRectangle target = filledRect(0, 0, 100, 100, "red");
        target.setMask("url(#mask)");

        assertThat(alphaAt(target, mask, 50, 50), closeTo(0.0, 1e-6));
    }

    @Test
    public void testUnresolvableMaskReferenceReturnsTheOriginalNodeUnchanged() throws Exception {
        SvgRectangle target = filledRect(0, 0, 10, 10, "red");
        target.setMask("url(#missing)");

        SvgGraphic svg = new SvgGraphic();
        Node original = onFxThread(() -> target.createGraphic(RenderContext.root(svg.getElementIndex(), 0, 0)));
        Node result = onFxThread(() -> target.applyMask(RenderContext.root(svg.getElementIndex(), 0, 0), original));
        assertThat(result, is(sameInstance(original)));
    }

    @Test
    public void testNullElementIndexIsANoOpNotAnException() throws Exception {
        SvgRectangle target = filledRect(0, 0, 10, 10, "red");
        target.setMask("url(#anything)");

        Node original = onFxThread(() -> target.createGraphic(RenderContext.root(null, 0, 0)));
        Node result = onFxThread(() -> target.applyMask(RenderContext.root(null, 0, 0), original));
        assertThat(result, is(sameInstance(original)));
    }

    // --- helpers ---------------------------------------------------------

    private static SvgMask maskOf(ISvgElement... children) {
        SvgMask mask = new SvgMask();
        for (ISvgElement child : children) {
            mask.getContent()
                .add(child);
        }
        return mask;
    }

    private static SvgRectangle rect(double x, double y, double width, double height, String fill) {
        SvgRectangle rect = new SvgRectangle(x, y, width, height);
        if (fill != null) {
            rect.setFill(SvgPaint.parse(fill));
        }
        return rect;
    }

    private static SvgRectangle filledRect(double x, double y, double width, double height, String fill) {
        return rect(x, y, width, height, fill);
    }

    /**
     * Renders {@code target} as the child of a plain {@code <g>} (so masking - applied at the {@code ISvgContainer.appendContent} consumer level, not inside {@code target}'s own
     * {@code createGraphic} - actually takes effect), against an index built over {@code definitionsHolder}, and samples the alpha channel at the given point in a fixed, generous
     * snapshot viewport.
     */
    private static double alphaAt(ISvgElement target, ISvgElement definitionsHolder, double x, double y) throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(definitionsHolder);
        SvgGroup group = new SvgGroup();
        group.getContent()
            .add(target);
        svg.getContent()
            .add(group);

        return onFxThread(() -> {
            Group rendered = svg.createGroup();
            Group root = new Group(rendered);
            new Scene(root);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            params.setViewport(new Rectangle2D(0, 0, 300, 300));
            WritableImage image = root.snapshot(params, null);
            return image.getPixelReader()
                .getColor((int) x, (int) y)
                .getOpacity();
        });
    }

}
