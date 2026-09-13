package nz.co.ctg.foxglove.filter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.clip.SvgClipPath;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.ImageInput;
import javafx.scene.paint.Color;

/**
 * Exercises #26/#76's acceptance criteria: {@code filter="url(#id)"} resolves and applies, a lone
 * {@code feGaussianBlur} renders with the correct blur radius, {@code filterUnits}/{@code primitiveUnits} resolve, and
 * a chain of directly-mappable primitives builds the equivalent JavaFX effect chain.
 * <p>
 * Since #77 there is a second path behind this one, so the interesting assertion for anything the chain cannot express
 * is no longer "no effect at all" but "not <i>this</i> path" - the filter is handed to
 * {@link SvgFilterRasterPipeline}, which {@link SvgFilterRasterPipelineTest} covers on its own terms. What stays an
 * outright degrade is narrower: a filter with no primitives, and a genuine authoring error such as an {@code in} that
 * names a {@code result} no primitive ever declared.
 * <p>
 * Every test renders on the JavaFX Application Thread. That matters for the degrade assertions specifically: off it,
 * the raster path cannot snapshot and so cannot engage at all, which would leave those tests passing while proving
 * nothing about which path ran.
 */
public class SvgFilterRenderingTest {

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

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

        Node node = onFxThread(() -> {
            SvgGraphic svg = new SvgGraphic();
            svg.getContent().add(clipPath);
            svg.getContent().add(filter);
            return rect.createGraphic(RenderContext.root(svg.getElementIndex(), 0, 0));
        });

        assertThat(node.getClip(), notNullValue());
        // the clip-path clip itself now has a further clip (the filter region) - both narrow the visible area
        assertThat(node.getClip().getClip(), notNullValue());
    }

    @Test
    public void testAPrimitiveWithNoEffectEquivalentFallsBackToTheRasterPipeline() throws Exception {
        SvgFilter filter = filterOf(new FeOffset());
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), instanceOf(ImageInput.class));
    }

    @Test
    public void testTwoChainedGaussianBlursBuildANestedEffect() throws Exception {
        // the second blur's blank `in` resolves to the first blur's result, per spec - a valid 2-step chain now
        // that stage 2 (#76) generalises the single-primitive fast path into a chain builder
        SvgFilter filter = filterOf(blur("5"), blur("5"));
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        GaussianBlur outer = (GaussianBlur) render(rect, filter).getEffect();
        assertThat(outer, notNullValue());
        assertThat(outer.getInput(), instanceOf(GaussianBlur.class));
    }

    @Test
    public void testAChainContainingAnExcludedPrimitiveFallsBackWholesaleRatherThanPartially() throws Exception {
        // feOffset has no JavaFX effect equivalent at all (verified against the OpenJFX javadoc - see
        // SvgFilterRenderer's own class javadoc), so it aborts the whole chain rather than just that one step: the
        // feGaussianBlur before it is re-evaluated by the raster pipeline too, not left as a half-built effect
        SvgFilter filter = filterOf(blur("5"), new FeOffset());
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), instanceOf(ImageInput.class));
    }

    @Test
    public void testAChainReferencingAnUnresolvableNamedResultDegradesToNoEffect() throws Exception {
        FeGaussianBlur second = blur("5");
        second.setIn("neverDeclared");
        SvgFilter filter = filterOf(blur("5"), second);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), is(nullValue()));
    }

    @Test
    public void testAnInTheChainCannotResolveFallsBackToTheRasterPipeline() throws Exception {
        // SourceAlpha is not a plain node and not an earlier Effect, so there is nothing to hand a GaussianBlur as
        // its input - the raster pipeline derives it from the rasterised source instead
        FeGaussianBlur blur = blur("5");
        blur.setIn("SourceAlpha");
        SvgFilter filter = filterOf(blur);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), instanceOf(ImageInput.class));
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
        Node node = onFxThread(() -> rect.createGraphic(RenderContext.root(new SvgGraphic().getElementIndex(), 0, 0)));
        assertThat(node.getEffect(), is(nullValue()));
        assertThat(node.getClip(), is(nullValue()));
    }

    @Test
    public void testNullElementIndexIsANoOp() throws Exception {
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#anything)");
        Node node = onFxThread(() -> rect.createGraphic(RenderContext.root(null, 0, 0)));
        assertThat(node.getEffect(), is(nullValue()));
    }

    // --- stage 2 (#76): chains --------------------------------------------

    @Test
    public void testALoneFeFloodRendersAColorInput() throws Exception {
        FeFlood flood = new FeFlood();
        flood.setFloodColor("red");
        flood.setFloodOpacity("0.5");
        SvgFilter filter = filterOf(flood);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        ColorInput input = (ColorInput) render(rect, filter).getEffect();
        assertThat(input, notNullValue());
        assertThat(input.getPaint(), is(Color.RED.deriveColor(0, 1, 1, 0.5)));
    }

    @Test
    public void testGaussianBlurThenColorMatrixSaturateChains() throws Exception {
        FeColorMatrix saturate = new FeColorMatrix();
        saturate.setType("saturate");
        saturate.setValues("0.5");
        SvgFilter filter = filterOf(blur("5"), saturate);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        ColorAdjust adjust = (ColorAdjust) render(rect, filter).getEffect();
        assertThat(adjust, notNullValue());
        assertThat(adjust.getSaturation(), closeTo(-0.5, 1e-9)); // 0.5 - 1
        assertThat(adjust.getInput(), instanceOf(GaussianBlur.class));
    }

    @Test
    public void testColorMatrixHueRotateConvertsDegreesToJavaFxHueRange() throws Exception {
        FeColorMatrix hueRotate = new FeColorMatrix();
        hueRotate.setType("hueRotate");
        hueRotate.setValues("90");
        SvgFilter filter = filterOf(hueRotate);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        ColorAdjust adjust = (ColorAdjust) render(rect, filter).getEffect();
        assertThat(adjust.getHue(), closeTo(0.5, 1e-9)); // 90 / 180
    }

    @Test
    public void testColorMatrixTypeMatrixFallsBackToTheRasterPipeline() throws Exception {
        // ColorAdjust can only approximate saturate/hueRotate; an arbitrary 5x4 matrix needs the real arithmetic
        FeColorMatrix matrix = new FeColorMatrix();
        matrix.setValues("1 0 0 0 0 0 1 0 0 0 0 0 1 0 0 0 0 0 1 0");
        SvgFilter filter = filterOf(matrix);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), instanceOf(ImageInput.class));
    }

    @Test
    public void testFeFloodThenFeBlendBuildsTheMappedBlendMode() throws Exception {
        FeFlood flood = new FeFlood();
        flood.setFloodColor("blue");
        FeBlend blend = new FeBlend();
        blend.setIn2("SourceGraphic");
        blend.setMode("multiply");
        SvgFilter filter = filterOf(flood, blend);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        Blend built = (Blend) render(rect, filter).getEffect();
        assertThat(built, notNullValue());
        assertThat(built.getMode(), is(BlendMode.MULTIPLY));
        assertThat(built.getBottomInput(), instanceOf(ColorInput.class)); // flood's own result
        assertThat(built.getTopInput(), is(nullValue())); // SourceGraphic - the plain node itself
    }

    @Test
    public void testFeGaussianBlurThenFeMergeCombinesSourceGraphicWithTheNamedBlurResult() throws Exception {
        FeGaussianBlur blur = blur("5");
        blur.setResult("blurred");
        FeMerge merge = new FeMerge();
        FeMergeNode sourceNode = new FeMergeNode();
        sourceNode.setIn("SourceGraphic");
        FeMergeNode blurredNode = new FeMergeNode();
        blurredNode.setIn("blurred");
        merge.getFeMergeNode().add(sourceNode);
        merge.getFeMergeNode().add(blurredNode);
        SvgFilter filter = filterOf(blur, merge);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        Blend built = (Blend) render(rect, filter).getEffect();
        assertThat(built, notNullValue());
        assertThat(built.getBottomInput(), is(nullValue())); // SourceGraphic
        assertThat(built.getTopInput(), instanceOf(GaussianBlur.class));
    }

    @Test
    public void testFeMergeWithFewerThanTwoNodesFallsBackToTheRasterPipeline() throws Exception {
        // a one-node feMerge is degenerate for a Blend, which needs two inputs; compositing a single layer over an
        // empty canvas is perfectly well-defined for the raster pipeline
        FeMerge merge = new FeMerge();
        FeMergeNode onlyNode = new FeMergeNode();
        onlyNode.setIn("SourceGraphic");
        merge.getFeMergeNode().add(onlyNode);
        SvgFilter filter = filterOf(merge);
        filter.setId("f");
        SvgRectangle rect = rect(0, 0, 100, 100, "url(#f)");

        assertThat(render(rect, filter).getEffect(), instanceOf(ImageInput.class));
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
        return onFxThread(() -> {
            SvgGraphic svg = new SvgGraphic();
            svg.getContent().add(filter);
            RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
            return rect.createGraphic(context);
        });
    }

}
