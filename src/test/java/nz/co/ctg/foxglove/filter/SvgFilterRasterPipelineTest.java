package nz.co.ctg.foxglove.filter;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.scene.Node;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * Exercises #77's acceptance criteria: the pixel-level primitives that have no {@code javafx.scene.effect} equivalent render end to end, and an arbitrary (non-chain) graph
 * resolves {@code result}/{@code in}/{@code in2} correctly.
 * <p>
 * Every test renders on the JavaFX Application Thread, since the raster pipeline snapshots the node - the same constraint {@code SvgMaskRenderingTest} already works under.
 * Assertions read the pipeline's own output buffer straight off the {@link ImageInput} it sets as the node's effect, rather than snapshotting the filtered node a second time: that
 * is what the pipeline actually computed, with no second rasterisation to blur the comparison.
 * <p>
 * Each filter declares an explicit {@code userSpaceOnUse} region matching the target exactly, so an image pixel and a user-space coordinate are the same thing and the assertions
 * can name real positions.
 */
public class SvgFilterRasterPipelineTest {

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    // --- the headline idiom --------------------------------------------------

    /**
     * A drop shadow - {@code feGaussianBlur} of {@code SourceAlpha}, offset, with the source merged back over the top. Every one of those three steps is outside what the effect
     * chain can express ({@code feOffset} has no {@code Effect} equivalent at all), so before #77 this rendered completely unfiltered.
     */
    @Test
    public void testDropShadowRendersBlurredOffsetAlphaBehindTheSource() throws Exception {
        FeGaussianBlur blur = new FeGaussianBlur();
        blur.setIn("SourceAlpha");
        blur.setStdDeviation("2");
        blur.setResult("blurred");

        FeOffset offset = new FeOffset();
        offset.setIn("blurred");
        offset.setDx("10");
        offset.setDy("10");
        offset.setResult("shadow");

        FeMerge merge = new FeMerge();
        merge.getFeMergeNode()
            .add(mergeNode("shadow"));
        merge.getFeMergeNode()
            .add(mergeNode("SourceGraphic"));

        Image result = filtered(redRect(), filterOf(blur, offset, merge));

        // the source rectangle (0,0)-(50,50) is still red and opaque on top
        assertColor(result, 25, 25, Color.RED);
        // down-right of it, the offset shadow is dark and at least partly opaque where the source is not
        Color shadow = colorAt(result, 55, 55);
        assertThat(shadow.getOpacity() > 0.3, is(true));
        assertThat(shadow.getRed() < 0.2, is(true));
        // well clear of both, nothing at all
        assertThat(colorAt(result, 95, 95).getOpacity(), closeTo(0.0, 0.02));
    }

    // --- pixel-level primitives ---------------------------------------------

    @Test
    public void testFeComponentTransferLinearRemapsAChannel() throws Exception {
        // red (1,0,0) with green forced to 1 via a linear function of slope 0, intercept 1 -> yellow
        FeFunctionGreen green = new FeFunctionGreen();
        green.setType("linear");
        green.setSlope("0");
        green.setIntercept("1");
        FeComponentTransfer transfer = new FeComponentTransfer();
        transfer.setFeFuncG(green);

        Image result = filtered(redRect(), filterOf(transfer));
        assertColor(result, 25, 25, Color.YELLOW);
    }

    @Test
    public void testFeComponentTransferTableInterpolatesBetweenItsValues() throws Exception {
        // a two-entry table [0, 1] on red is the identity at the endpoints; on the blue channel of pure red
        // (blue = 0) a reversed table [1, 0] maps it to 1
        FeFunctionBlue blue = new FeFunctionBlue();
        blue.setType("table");
        blue.setTableValues("1 0");
        FeComponentTransfer transfer = new FeComponentTransfer();
        transfer.setFeFuncB(blue);

        Image result = filtered(redRect(), filterOf(transfer));
        assertColor(result, 25, 25, Color.MAGENTA);
    }

    @Test
    public void testFeComponentTransferDiscretePicksTheRightStep() throws Exception {
        FeFunctionGreen green = new FeFunctionGreen();
        green.setType("discrete");
        green.setTableValues("1 1");
        FeComponentTransfer transfer = new FeComponentTransfer();
        transfer.setFeFuncG(green);

        Image result = filtered(redRect(), filterOf(transfer));
        assertColor(result, 25, 25, Color.YELLOW);
    }

    @Test
    public void testFeColorMatrixFullMatrixFormSwapsChannels() throws Exception {
        // the effect-chain path can only approximate saturate/hueRotate through ColorAdjust - an explicit matrix
        // (here, routing red into the green output) has no expression there at all
        FeColorMatrix matrix = new FeColorMatrix();
        matrix.setType("matrix");
        matrix.setValues("0 0 0 0 0  1 0 0 0 0  0 0 0 0 0  0 0 0 1 0");

        Image result = filtered(redRect(), filterOf(matrix));
        assertColor(result, 25, 25, Color.LIME);
    }

    @Test
    public void testFeColorMatrixLuminanceToAlphaZeroesColourAndSetsAlphaFromLuminance() throws Exception {
        FeColorMatrix matrix = new FeColorMatrix();
        matrix.setType("luminanceToAlpha");

        Image result = filtered(redRect(), filterOf(matrix));
        Color color = colorAt(result, 25, 25);
        assertThat(color.getOpacity(), closeTo(0.2125, 0.01)); // red's luminance coefficient
        assertThat(color.getRed(), closeTo(0.0, 0.01));
    }

    @Test
    public void testFeCompositeArithmeticCombinesTwoInputs() throws Exception {
        FeFlood flood = new FeFlood();
        flood.setFloodColor("lime");
        flood.setResult("green");

        // k2=1, k3=1: plain addition of the two premultiplied inputs - red + green = yellow
        FeComposite composite = new FeComposite();
        composite.setIn("SourceGraphic");
        composite.setIn2("green");
        composite.setOperator("arithmetic");
        composite.setK2("1");
        composite.setK3("1");

        Image result = filtered(redRect(), filterOf(flood, composite));
        assertColor(result, 25, 25, Color.YELLOW);
    }

    @Test
    public void testFeCompositeInClipsTheFloodToTheSourceAlpha() throws Exception {
        FeFlood flood = new FeFlood();
        flood.setFloodColor("blue");
        flood.setResult("flooded");

        FeComposite composite = new FeComposite();
        composite.setIn("flooded");
        composite.setIn2("SourceGraphic");
        composite.setOperator("in");

        Image result = filtered(redRect(), filterOf(flood, composite));
        // blue where the source rectangle is...
        assertColor(result, 25, 25, Color.BLUE);
        // ...and nothing where it is not, despite the flood covering the whole region
        assertThat(colorAt(result, 80, 80).getOpacity(), closeTo(0.0, 0.02));
    }

    @Test
    public void testFeOffsetShiftsTheSource() throws Exception {
        FeOffset offset = new FeOffset();
        offset.setDx("20");
        offset.setDy("0");

        Image result = filtered(redRect(), filterOf(offset));
        assertColor(result, 45, 25, Color.RED); // was (25,25)
        assertThat(colorAt(result, 5, 25).getOpacity(), closeTo(0.0, 0.02)); // vacated
    }

    @Test
    public void testFeBlendMultiplyDarkensWhereInputsOverlap() throws Exception {
        FeFlood flood = new FeFlood();
        flood.setFloodColor("lime");
        flood.setResult("green");

        FeBlend blend = new FeBlend();
        blend.setIn("SourceGraphic");
        blend.setIn2("green");
        blend.setMode("multiply");

        Image result = filtered(redRect(), filterOf(flood, forceRaster(), blend));
        // red x green has no channel in common, so the overlap goes black (fully opaque over the flood)
        Color overlap = colorAt(result, 25, 25);
        assertThat(overlap.getRed(), closeTo(0.0, 0.02));
        assertThat(overlap.getGreen(), closeTo(0.0, 0.02));
        assertThat(overlap.getOpacity(), closeTo(1.0, 0.02));
    }

    /**
     * A lone {@code feGaussianBlur} is exactly what the effect chain handles best, so this blurs {@code SourceAlpha} - colour discarded, coverage kept - which the chain cannot
     * resolve, to get the raster implementation of the spec's own three-box-pass approximation under test.
     */
    @Test
    public void testFeGaussianBlurSpreadsAlphaBeyondTheSourceEdge() throws Exception {
        FeGaussianBlur blur = new FeGaussianBlur();
        blur.setIn("SourceAlpha");
        blur.setStdDeviation("3");

        Image result = filtered(redRect(), filterOf(blur));
        // just outside the rectangle's own right edge (x=50) the blur has carried some alpha
        double justOutside = colorAt(result, 52, 25).getOpacity();
        assertThat(justOutside > 0.05, is(true));
        assertThat(justOutside < 0.95, is(true));
        // and the centre is still essentially solid
        assertThat(colorAt(result, 25, 25).getOpacity() > 0.9, is(true));
    }

    // --- arbitrary graphs ----------------------------------------------------

    /**
     * The other half of #77's acceptance criteria: two branches off {@code SourceGraphic} converging in a later primitive, with the first branch's {@code result} referenced well
     * after the primitive that follows it. The effect chain can only ever thread one previous result through, so this shape aborts there.
     */
    @Test
    public void testBranchingGraphResolvesResultsReferencedOutOfOrder() throws Exception {
        FeFlood flood = new FeFlood();
        flood.setFloodColor("lime");
        flood.setResult("branchA");

        FeOffset offset = new FeOffset();
        offset.setIn("SourceGraphic");
        offset.setDx("0");
        offset.setDy("0");
        offset.setResult("branchB");

        // converges the two branches - branchA was produced two primitives ago, not immediately before
        FeComposite composite = new FeComposite();
        composite.setIn("branchB");
        composite.setIn2("branchA");
        composite.setOperator("in");

        Image result = filtered(redRect(), filterOf(flood, offset, composite));
        assertColor(result, 25, 25, Color.RED);
    }

    @Test
    public void testSourceAlphaDiscardsColourButKeepsCoverage() throws Exception {
        FeOffset offset = new FeOffset();
        offset.setIn("SourceAlpha");
        offset.setDx("0");
        offset.setDy("0");

        Image result = filtered(redRect(), filterOf(offset));
        Color color = colorAt(result, 25, 25);
        assertThat(color.getOpacity(), closeTo(1.0, 0.02));
        assertThat(color.getRed(), closeTo(0.0, 0.02));
    }

    // --- degrades ------------------------------------------------------------

    @Test
    public void testAPrimitiveNeitherPathSupportsStillDegradesToNoEffect() throws Exception {
        SvgFilter filter = filterOf(new FeTurbulence());
        Node node = onFxThread(() -> render(redRect(), filter));
        assertThat(node.getEffect(), is(nullValue()));
    }

    @Test
    public void testAnUnresolvableNamedResultStillDegradesToNoEffect() throws Exception {
        FeOffset offset = new FeOffset();
        offset.setIn("neverDeclared");
        SvgFilter filter = filterOf(offset);

        Node node = onFxThread(() -> render(redRect(), filter));
        assertThat(node.getEffect(), is(nullValue()));
    }

    /**
     * The raster path needs {@code Node.snapshot}, which requires the JavaFX Application Thread - the same constraint masking (#25) carries. Off it, the filter degrades to
     * unfiltered rather than throwing.
     */
    @Test
    public void testOffTheFxThreadDegradesToNoEffectRatherThanThrowing() throws Exception {
        FeOffset offset = new FeOffset();
        offset.setDx("10");
        SvgFilter filter = filterOf(offset);

        Node node = render(redRect(), filter); // deliberately not on the FX thread
        assertThat(node.getEffect(), is(nullValue()));
    }

    // --- the element's own opacity (#129) ------------------------------------

    /**
     * SVG applies {@code opacity} to the filter's <i>result</i>, not its input, so {@code SourceGraphic} is the element before it. Leaving the node's opacity on while snapshotting
     * applied it twice - once baked into the source raster, and again when JavaFX painted the {@code ImageInput} built from it.
     * <p>
     * Asserted on the pipeline's own output buffer, which is the filter result <i>before</i> JavaFX applies node opacity on top: a correct {@code SourceGraphic} is fully opaque
     * there. Note an opaque element could not catch this at all, and neither could sampling the final rendering without accounting for the one legitimate application.
     */
    @Test
    public void testTheElementsOwnOpacityIsNotBakedIntoSourceGraphic() throws Exception {
        SvgRectangle rect = redRect();
        rect.setOpacity("0.5");

        Image result = filtered(rect, filterOf(passThrough("SourceGraphic")));

        assertThat("SourceGraphic should be the element before its own opacity",
            colorAt(result, 25, 25).getOpacity(), closeTo(1.0, 0.02));
    }

    /** And the node keeps its opacity afterwards, so the one legitimate application still happens. */
    @Test
    public void testTheElementKeepsItsOpacityAfterFiltering() throws Exception {
        SvgRectangle rect = redRect();
        rect.setOpacity("0.5");

        Node node = onFxThread(() -> render(rect, filterOf(passThrough("SourceGraphic"))));

        assertThat(node.getOpacity(), closeTo(0.5, 1e-9));
    }

    // --- colour-interpolation space (#108) -----------------------------------

    /**
     * SVG's default filter working space is linearRGB, not sRGB, and getting that wrong makes every interpolated value systematically too dark.
     * <p>
     * Note what it takes to see the difference at all: {@code 0} and {@code 1} are fixed points of the sRGB transfer function, so a test built from saturated primaries - as every
     * other test in this class is, quite reasonably, since they make the geometry legible - passes identically in either space. The difference only shows in the midtones, so this
     * halves white and looks at where it lands: {@code 0.5} in linear light is {@code 0.735} once encoded back to sRGB, against {@code 0.5} if the maths had been done in sRGB
     * throughout. Nothing subtle about a 60-level gap; it is invisible only if you never test a midtone.
     */
    @Test
    public void testPrimitivesEvaluateInLinearRgbByDefault() throws Exception {
        Image result = filtered(whiteRect(), filterOf(halveEveryChannel()));

        assertThat(colorAt(result, 25, 25).getRed(), closeTo(0.7354, 0.005));
    }

    /** {@code color-interpolation-filters="sRGB"} on the {@code <filter>} opts the whole graph out. */
    @Test
    public void testTheFilterCanOptIntoSrgb() throws Exception {
        SvgFilter filter = filterOf(halveEveryChannel());
        filter.setColorInterpolationFilters("sRGB");

        assertThat(colorAt(filtered(whiteRect(), filter), 25, 25).getRed(), closeTo(0.5, 0.005));
    }

    /** And an individual primitive can opt out on its own, overriding the filter around it. */
    @Test
    public void testAPrimitiveCanOptIntoSrgbOnItsOwn() throws Exception {
        FeComponentTransfer transfer = halveEveryChannel();
        transfer.setColorInterpolationFilters("sRGB");
        SvgFilter filter = filterOf(transfer);
        filter.setColorInterpolationFilters("linearRGB");

        assertThat(colorAt(filtered(whiteRect(), filter), 25, 25).getRed(), closeTo(0.5, 0.005));
    }

    /**
     * {@code flood-color} is authored in sRGB whatever space the primitive works in, so a flood that passes through untouched has to come back out exactly as authored - the
     * conversion in and the conversion out must cancel. A midtone grey, since a primary would survive either way.
     */
    @Test
    public void testAFloodColourSurvivesTheRoundTripThroughLinearRgb() throws Exception {
        FeFlood flood = new FeFlood();
        flood.setFloodColor("#808080");
        flood.setResult("flooded");

        // feFlood alone is effect-expressible; the no-op feOffset reading it both forces the raster path and
        // leaves the flood as the graph's result
        Image result = filtered(whiteRect(), filterOf(flood, passThrough("flooded")));
        assertThat(colorAt(result, 25, 25).getRed(), closeTo(0x80 / 255.0, 0.01));
    }

    /** Halves every colour channel in whatever space the primitive is working in. */
    private static FeComponentTransfer halveEveryChannel() {
        FeComponentTransfer transfer = new FeComponentTransfer();
        FeFunctionRed red = new FeFunctionRed();
        red.setType("linear");
        red.setSlope("0.5");
        transfer.setFeFuncR(red);
        FeFunctionGreen green = new FeFunctionGreen();
        green.setType("linear");
        green.setSlope("0.5");
        transfer.setFeFuncG(green);
        FeFunctionBlue blue = new FeFunctionBlue();
        blue.setType("linear");
        blue.setSlope("0.5");
        transfer.setFeFuncB(blue);
        return transfer;
    }

    /** An {@code feOffset} of zero reading a named result - a way to end a graph on a chosen input. */
    private static FeOffset passThrough(String in) {
        FeOffset offset = new FeOffset();
        offset.setIn(in);
        offset.setDx("0");
        offset.setDy("0");
        return offset;
    }

    // --- helpers -------------------------------------------------------------

    /**
     * A deliberate no-op {@code feOffset} ({@code dx}/{@code dy} of zero), for a filter whose other primitives the effect chain <i>can</i> express: {@code feOffset} has no
     * {@code Effect} equivalent at all (#76), so including one aborts the chain and hands the filter to the raster pipeline without changing any pixel.
     */
    private static FeOffset forceRaster() {
        FeOffset offset = new FeOffset();
        offset.setIn("SourceGraphic");
        offset.setDx("0");
        offset.setDy("0");
        offset.setResult("unchanged");
        return offset;
    }

    private static FeMergeNode mergeNode(String in) {
        FeMergeNode node = new FeMergeNode();
        node.setIn(in);
        return node;
    }

    private static SvgRectangle redRect() {
        return rectFilled(Color.RED);
    }

    /** White, for the colour-space tests: its channels are 1.0, which halving moves into the revealing midtones. */
    private static SvgRectangle whiteRect() {
        return rectFilled(Color.WHITE);
    }

    private static SvgRectangle rectFilled(Color fill) {
        SvgRectangle rect = new SvgRectangle(0, 0, 50, 50);
        rect.setFill(fill);
        rect.setFilter("url(#f)");
        return rect;
    }

    /** A filter whose region is exactly the 100x100 user-space square, so image pixels and user coordinates match. */
    private static SvgFilter filterOf(ISvgElement... primitives) {
        SvgFilter filter = new SvgFilter();
        filter.setId("f");
        filter.setFilterUnits("userSpaceOnUse");
        filter.setX(new Size(0, SizeUnits.PX));
        filter.setY(new Size(0, SizeUnits.PX));
        filter.setWidth(new Size(100, SizeUnits.PX));
        filter.setHeight(new Size(100, SizeUnits.PX));
        for (ISvgElement primitive : primitives) {
            filter.getContent()
                .add(primitive);
        }
        return filter;
    }

    private static Node render(SvgRectangle rect, SvgFilter filter) throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(filter);
        return rect.createGraphic(RenderContext.root(svg.getElementIndex(), 0, 0));
    }

    private static Image filtered(SvgRectangle rect, SvgFilter filter) throws Exception {
        Node node = onFxThread(() -> render(rect, filter));
        assertThat("expected the raster pipeline to have applied an ImageInput", node.getEffect(), notNullValue());
        return ((ImageInput) node.getEffect()).getSource();
    }

    private static Color colorAt(Image image, int x, int y) {
        return image.getPixelReader()
            .getColor(x, y);
    }

    private static void assertColor(Image image, int x, int y, Color expected) {
        Color actual = colorAt(image, x, y);
        assertThat("red at (" + x + "," + y + ")", actual.getRed(), closeTo(expected.getRed(), 0.02));
        assertThat("green at (" + x + "," + y + ")", actual.getGreen(), closeTo(expected.getGreen(), 0.02));
        assertThat("blue at (" + x + "," + y + ")", actual.getBlue(), closeTo(expected.getBlue(), 0.02));
        assertThat("alpha at (" + x + "," + y + ")", actual.getOpacity(), closeTo(expected.getOpacity(), 0.02));
    }

}
