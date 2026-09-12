package nz.co.ctg.foxglove.filter;

import org.junit.Test;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.clip.SvgClipPath;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;

/**
 * Exercises #26 stage 1's acceptance criteria: {@code filter="url(#id)"} resolves and applies, a lone
 * {@code feGaussianBlur} renders with the correct blur radius, {@code filterUnits}/{@code primitiveUnits} resolve,
 * and an unsupported filter degrades to no effect rather than throwing or rendering nothing.
 */
public class SvgFilterRenderingTest {

    @Test
    public void testFilterResolvesAndAppliesAGaussianBlur() throws Exception {
        SvgFilter filter = filterOf(blur("5"));
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        Node node = render(rect, filter);
        GaussianBlur effect = (GaussianBlur) node.getEffect();
        assertThat(effect, notNullValue());
        assertThat(effect.getRadius(), closeTo(15.0, 1e-9)); // 3 * 5
    }

    @Test
    public void testPrimitiveUnitsObjectBoundingBoxScalesStdDeviationByTheBoundingBoxDiagonal() throws Exception {
        SvgFilter filter = filterOf(blur("0.1"));
        filter.setId("f");
        filter.setPrimitiveUnits("objectBoundingBox");
        // a 30-40-50 right triangle's worth of width/height gives a diagonal (per the 7.10 formula) of 50/sqrt(2)*sqrt(2)... use simple square instead
        SvgRectangle rect = rect(0, 0, 30, 40, "url(#f)");

        Node node = render(rect, filter);
        GaussianBlur effect = (GaussianBlur) node.getEffect();
        // bbox diagonal = sqrt((30^2 + 40^2)/2) = sqrt(1250) ≈ 35.355; stdDeviation = 0.1 * that; radius = 3 * that
        double expectedDiagonal = Math.sqrt((30.0 * 30.0 + 40.0 * 40.0) / 2.0);
        assertThat(effect.getRadius(), closeTo(3 * 0.1 * expectedDiagonal, 1e-9));
    }

    @Test
    public void testPrimitiveUnitsDefaultsToUserSpaceOnUse() throws Exception {
        SvgFilter filter = filterOf(blur("5"));
        filter.setId("f");
        // no primitiveUnits set: stdDeviation stays a literal 5 regardless of the target's size
        SvgRectangle rect = rect(0, 0, 1000, 1000, "url(#f)");

        Node node = render(rect, filter);
        GaussianBlur effect = (GaussianBlur) node.getEffect();
        assertThat(effect.getRadius(), closeTo(15.0, 1e-9));
    }

    @Test
    public void testFilterRegionAppliesAsAClip() throws Exception {
        SvgFilter filter = filterOf(blur("1"));
        filter.setId("f");
        filter.setFilterUnits("userSpaceOnUse");
        filter.setX(px(0));
        filter.setY(px(0));
        filter.setWidth(px(50));
        filter.setHeight(px(100));
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        Node node = render(rect, filter);
        assertThat(node.getClip(), notNullValue());
        Bounds clipBounds = node.getClip().getBoundsInLocal();
        assertThat(clipBounds.getWidth(), closeTo(50, 1e-9));
        assertThat(clipBounds.getHeight(), closeTo(100, 1e-9));
    }

    @Test
    public void testFilterRegionNestsWithAnExistingClipPathClip() throws Exception {
        SvgClipPath clipPath = new SvgClipPath();
        clipPath.setId("clip");
        clipPath.getContent().add(new SvgCircle());
        ((SvgCircle) clipPath.getContent().get(0)).setRadius(40);
        ((SvgCircle) clipPath.getContent().get(0)).setCentreX(50);
        ((SvgCircle) clipPath.getContent().get(0)).setCentreY(50);

        SvgFilter filter = filterOf(blur("1"));
        filter.setId("f");
        filter.setFilterUnits("userSpaceOnUse");
        filter.setX(px(0));
        filter.setY(px(0));
        filter.setWidth(px(50));
        filter.setHeight(px(100));

        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");
        rect.setClipPath("url(#clip)");

        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(clipPath);
        svg.getContent().add(filter);
        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        Node node = rect.createGraphic(context);

        assertThat(node.getClip(), notNullValue());
        // the clip-path clip itself now has a further clip (the filter region) - both narrow the visible area
        assertThat(node.getClip().getClip(), notNullValue());
    }

    @Test
    public void testUnsupportedPrimitiveDegradesToNoEffect() throws Exception {
        SvgFilter filter = filterOf(new FeOffset());
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), is(nullValue()));
    }

    @Test
    public void testMoreThanOnePrimitiveDegradesToNoEffect() throws Exception {
        SvgFilter filter = filterOf(blur("5"), blur("5"));
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), is(nullValue()));
    }

    @Test
    public void testUnsupportedInDegradesToNoEffect() throws Exception {
        FeGaussianBlur blur = blur("5");
        blur.setIn("SourceAlpha");
        SvgFilter filter = filterOf(blur);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), is(nullValue()));
    }

    @Test
    public void testEmptyFilterDegradesToNoEffect() throws Exception {
        SvgFilter filter = new SvgFilter();
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), is(nullValue()));
    }

    @Test
    public void testUnresolvableFilterReferenceIsANoOp() throws Exception {
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#missing)");
        SvgGraphic svg = new SvgGraphic();
        Node node = rect.createGraphic(RenderContext.root(svg.getElementIndex(), 0, 0));
        assertThat(node.getEffect(), is(nullValue()));
        assertThat(node.getClip(), is(nullValue()));
    }

    @Test
    public void testNullElementIndexIsANoOp() throws Exception {
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#anything)");
        Node node = rect.createGraphic(RenderContext.root(null, 0, 0));
        assertThat(node.getEffect(), is(nullValue()));
    }

    // --- helpers ---------------------------------------------------------

    private static Size px(double value) {
        return new Size(value, SizeUnits.PX);
    }

    private static FeGaussianBlur blur(String stdDeviation) {
        FeGaussianBlur blur = new FeGaussianBlur();
        blur.setStdDeviation(stdDeviation);
        return blur;
    }

    private static SvgFilter filterOf(ISvgElement... primitives) {
        SvgFilter filter = new SvgFilter();
        for (ISvgElement primitive : primitives) {
            filter.getContent().add(primitive);
        }
        return filter;
    }

    private static SvgRectangle rect(double x, double y, double width, double height, String filterRef) {
        SvgRectangle rect = new SvgRectangle(x, y, width, height);
        rect.setFilter(filterRef);
        return rect;
    }

    private static Node render(SvgRectangle rect, SvgFilter filter) throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(filter);
        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        return rect.createGraphic(context);
    }

}
