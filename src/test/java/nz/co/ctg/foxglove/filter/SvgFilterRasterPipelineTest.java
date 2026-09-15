package nz.co.ctg.foxglove.filter;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.net.URI;
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
import nz.co.ctg.foxglove.element.SvgDefinitions;
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

    // --- feTurbulence (#171) --------------------------------------------------

    /**
     * The full pipeline - parse, dispatch, per-pixel generation, premultiply, and back out through {@code toImage} - against the same independently-computed reference value
     * {@link PerlinTurbulenceTest} checks at the algorithm level, this time for a real pixel of a real rendered image. {@code color-interpolation-filters="sRGB"} opts out of the
     * default linearRGB working space (see {@code testTheFilterCanOptIntoSrgb}), so the raw computed channel values are exactly what the final image holds, with no gamma curve to
     * also replicate in the expected value.
     * <p>
     * Pixel {@code (15, 63)} was chosen deliberately: at {@code baseFrequency=0.1} it is off any exact lattice point (15*0.1=1.5, 63*0.1=6.3 - see {@link PerlinTurbulenceTest}'s
     * own note on why lattice-aligned points can't distinguish a correct implementation from a broken one), and its alpha ({@code ~0.63}) is high enough that
     * {@code WritableImage}'s own internal premultiplied-byte storage doesn't round a low-alpha pixel's colour away to nothing on the way through {@code setArgb} - a real,
     * pre-existing precision limit of that round trip this test found by first picking a low-alpha pixel and watching every colour channel come back as flatly wrong.
     */
    @Test
    public void testFeTurbulenceMatchesTheIndependentReferenceComputationEndToEnd() throws Exception {
        FeTurbulence turbulence = new FeTurbulence();
        turbulence.setBaseFrequency("0.1");
        turbulence.setNumOctaves("1");

        SvgFilter filter = filterOf(turbulence);
        filter.setColorInterpolationFilters("sRGB");

        Image result = filtered(redRect(), filter);
        Color actual = colorAt(result, 15, 63);
        assertThat(actual.getRed(), closeTo(0.09415459488917087, 0.02));
        assertThat(actual.getGreen(), closeTo(0.10826127394222423, 0.02));
        assertThat(actual.getBlue(), closeTo(0.25592618128882694, 0.02));
        assertThat(actual.getOpacity(), closeTo(0.6289476393677605, 0.01));
    }

    /**
     * #171's acceptance criteria: a buffer, not {@code UnsupportedFilterException} degrading the whole filter to unfiltered. {@code (15, 63)} and {@code (23, 77)} are both
     * off-lattice at {@code baseFrequency=0.1} (see the note above) - two lattice-aligned points would both read exactly {@code 0} regardless of whether the generator works at
     * all, which is what an earlier version of this test accidentally did with {@code (10, 10)}/{@code (60, 60)} (both exact multiples of the lattice spacing).
     */
    @Test
    public void testFeTurbulenceProducesNonUniformNoiseInsteadOfDegradingToUnfiltered() throws Exception {
        FeTurbulence turbulence = new FeTurbulence();
        turbulence.setBaseFrequency("0.1");
        turbulence.setNumOctaves("1");

        Image result = filtered(redRect(), filterOf(turbulence));
        Color a = colorAt(result, 15, 63);
        Color b = colorAt(result, 23, 77);
        boolean differs = Math.abs(a.getRed() - b.getRed()) > 0.02 || Math.abs(a.getGreen() - b.getGreen()) > 0.02
                          || Math.abs(a.getBlue() - b.getBlue()) > 0.02;
        assertThat(differs, is(true));
    }

    /**
     * SVG 1.1 15.24: alpha is itself generated by the noise function, not derived from RGB or fixed at 1. The sampling grid starts at {@code (1, 1)} rather than {@code (0, 0)}:
     * with {@code baseFrequency=0.2} and a step of {@code 5}, starting at {@code 0} would sample only {@code x*0.2}/{@code y*0.2} values that are exact integers - every single
     * sampled point landing exactly on a lattice point, always reading exactly {@code 0} regardless of whether the generator works at all (an earlier version of this test did
     * exactly that, and "found" every one of 400 samples fully transparent).
     */
    @Test
    public void testFeTurbulenceAlphaIsAlsoNoiseNotFixedOpaque() throws Exception {
        FeTurbulence turbulence = new FeTurbulence();
        turbulence.setBaseFrequency("0.2");
        turbulence.setNumOctaves("1");

        Image result = filtered(redRect(), filterOf(turbulence));
        boolean sawPartialAlpha = false;
        for (int x = 1; x < 100 && !sawPartialAlpha; x += 5) {
            for (int y = 1; y < 100 && !sawPartialAlpha; y += 5) {
                double alpha = colorAt(result, x, y).getOpacity();
                if (alpha > 0.05 && alpha < 0.95) {
                    sawPartialAlpha = true;
                }
            }
        }
        assertThat(sawPartialAlpha, is(true));
    }

    /** {@code fractalNoise} maps {@code turbFunctionResult} through {@code (result + 1) / 2} rather than {@code turbulence}'s plain {@code result} - a different colour mapping. */
    @Test
    public void testFractalNoiseUsesItsOwnColourMappingFormula() throws Exception {
        FeTurbulence turbulence = new FeTurbulence();
        turbulence.setType("fractalNoise");
        turbulence.setBaseFrequency("0.1");
        turbulence.setNumOctaves("2");

        SvgFilter filter = filterOf(turbulence);
        filter.setColorInterpolationFilters("sRGB");

        Image result = filtered(redRect(), filter);
        Color actual = colorAt(result, 13, 7);
        // channel A (index 3), 2 octaves, fractalNoise: independently computed raw result -0.2754662588430778,
        // mapped via (result + 1) / 2
        assertThat(actual.getOpacity(), closeTo(0.3622668705784611, 0.002));
    }

    // --- feImage (#174) --------------------------------------------------------

    /**
     * {@code wide.png} (the same 4x2 solid-red fixture {@link #WIDE_PNG} uses, this time as a real file) fitted exactly into an explicit subregion whose aspect ratio already
     * matches the image's own (2:1) - no letterboxing/cropping to reason about, isolating subregion positioning itself from {@code preserveAspectRatio} fitting.
     */
    @Test
    public void testFeImageOfARelativeRasterReferenceRendersAtItsExplicitSubregion() throws Exception {
        FeImage image = new FeImage();
        image.setXlinkHref("wide.png");
        image.setX("10");
        image.setY("10");
        image.setWidth("20");
        image.setHeight("10");

        Image result = filteredWithBaseUri(redRect(), filterOf(image), wideBaseUri());
        assertColor(result, 20, 15, Color.RED);
        assertThat(colorAt(result, 90, 90).getOpacity(), closeTo(0.0, 0.02));
    }

    /** No {@code x}/{@code y}/{@code width}/{@code height} on {@code feImage} defaults its subregion to the whole filter region, per spec - not to a zero-size box. */
    @Test
    public void testFeImageWithNoSubregionFillsTheWholeFilterRegion() throws Exception {
        FeImage image = new FeImage();
        image.setXlinkHref("wide.png");

        Image result = filteredWithBaseUri(redRect(), filterOf(image), wideBaseUri());
        // default preserveAspectRatio (xMidYMid meet) fits the 4x2 image into the 100x100 region at scale 25,
        // centred vertically (y 25..75) - sampled within that band, at both horizontal extremes
        assertColor(result, 5, 50, Color.RED);
        assertColor(result, 95, 50, Color.RED);
    }

    /** {@code xMidYMid meet} (the default) letterboxes rather than cropping - some of the subregion is left empty. */
    @Test
    public void testFeImagePreserveAspectRatioMeetLetterboxesWithinTheSubregion() throws Exception {
        FeImage image = new FeImage();
        image.setXlinkHref("wide.png");
        image.setX("45");
        image.setY("45");
        image.setWidth("10");
        image.setHeight("10");
        image.setPreserveAspectRatio("xMidYMid meet");

        Image result = filteredWithBaseUri(redRect(), filterOf(image), wideBaseUri());
        // 4x2 into 10x10, meet: scaled to 10x5, centred vertically - the top of the subregion is left transparent
        assertThat(colorAt(result, 50, 46).getOpacity(), closeTo(0.0, 0.05));
        assertColor(result, 50, 50, Color.RED);
    }

    /** {@code xMidYMid slice} crops rather than letterboxing - the whole subregion is covered, right up to its edges. */
    @Test
    public void testFeImagePreserveAspectRatioSliceFillsTheWholeSubregion() throws Exception {
        FeImage image = new FeImage();
        image.setXlinkHref("wide.png");
        image.setX("45");
        image.setY("45");
        image.setWidth("10");
        image.setHeight("10");
        image.setPreserveAspectRatio("xMidYMid slice");

        Image result = filteredWithBaseUri(redRect(), filterOf(image), wideBaseUri());
        assertColor(result, 50, 46, Color.RED);
        assertColor(result, 50, 50, Color.RED);
    }

    /** {@code primitiveUnits="objectBoundingBox"}: a fractional subregion resolves against the target's own bounding box, not the viewport. */
    @Test
    public void testFeImageSubregionUnderObjectBoundingBoxResolvesAgainstTheTargetsBounds() throws Exception {
        FeImage image = new FeImage();
        image.setXlinkHref("wide.png");
        image.setX("0");
        image.setY("0");
        image.setWidth("1");
        image.setHeight("1");

        SvgFilter filter = filterOf(image);
        filter.setPrimitiveUnits("objectBoundingBox");

        Image result = filteredWithBaseUri(redRect(), filter, wideBaseUri());
        // redRect() is 50x50 at (0,0) - width/height=1 (100%) of that bbox, letterboxed (meet) within it
        assertColor(result, 25, 25, Color.RED);
        // outside the 50x50 bbox but still inside the 100x100 filter region - not covered by this primitive at all
        assertThat(colorAt(result, 80, 80).getOpacity(), closeTo(0.0, 0.02));
    }

    /**
     * The acceptance criterion no conformance test actually exercises: a same-document {@code #id} reference renders that element's own content - fitted with a plain
     * translate/clip here rather than the spec's full viewBox-style treatment (see {@code SvgFilterRasterPipeline.image}'s own note on why), so the target is deliberately sized to
     * exactly match the subregion to keep the test meaningful either way.
     */
    @Test
    public void testFeImageOfASameDocumentElementRendersItsContent() throws Exception {
        SvgRectangle target = new SvgRectangle(0, 0, 20, 20);
        target.setId("box");
        target.setFill(Color.LIME);
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        FeImage image = new FeImage();
        image.setXlinkHref("#box");
        image.setX("10");
        image.setY("10");
        image.setWidth("20");
        image.setHeight("20");

        Image result = filteredWithExtraContent(redRect(), filterOf(image), defs);
        assertColor(result, 20, 20, Color.LIME);
    }

    @Test
    public void testFeImageWithAnUnresolvableReferenceDegradesToEmptyWithoutThrowing() throws Exception {
        FeImage image = new FeImage();
        image.setXlinkHref("does-not-exist.png");

        Image result = filteredWithBaseUri(redRect(), filterOf(image), wideBaseUri());
        assertThat(colorAt(result, 50, 50).getOpacity(), closeTo(0.0, 0.02));
    }

    // --- feConvolveMatrix, feMorphology, feDisplacementMap (#172) --------------

    /** {@code filters-conv-01-f}'s own box-blur kernel, exactly - {@code order="3 3"}, all taps {@code 0.1111}. */
    private static FeConvolveMatrix boxBlurKernel() {
        FeConvolveMatrix matrix = new FeConvolveMatrix();
        matrix.setOrder("3 3");
        matrix.setKernelMatrix("0.1111 0.1111 0.1111 0.1111 0.1111 0.1111 0.1111 0.1111 0.1111");
        return matrix;
    }

    /**
     * The kernel-index derivation was cross-checked, term by term, against SVG 1.1 15.13's own worked example (a 5x5 image, a {@code 1 2 3 / 4 5 6 / 7 8 9} kernel, default
     * {@code targetX}/{@code targetY}) before trusting it - see {@code SvgFilterRasterPipeline.convolveMatrix}'s own javadoc. This test instead checks the box-blur kernel
     * end-to-end: at the last fully-red column of {@link #redRect()} (x=49), 6 of the 3x3 kernel's 9 taps land inside the rect (red, premultiplied alpha 1) and 3 land just outside
     * it (transparent, {@code edgeMode="none"}) - a hand-computable {@code 6/9} opacity falloff, not just "doesn't throw".
     */
    @Test
    public void testFeConvolveMatrixBoxBlurAveragesAcrossAnEdge() throws Exception {
        FeConvolveMatrix matrix = boxBlurKernel();
        matrix.setEdgeMode("none");

        Image result = filtered(redRect(), filterOf(matrix));
        Color edge = colorAt(result, 49, 25);
        assertThat(edge.getRed(), closeTo(1.0, 0.02));
        assertThat(edge.getOpacity(), closeTo(6.0 / 9.0, 0.02));
        // well inside the rect, all 9 taps are red - unaffected
        assertColor(result, 25, 25, Color.RED);
    }

    /**
     * {@code preserveAlpha="true"}: the same edge pixel's colour is still blurred (6/9 red, 3/9 transparent-black contributing to the colour average), but its own alpha is left
     * exactly as {@code SourceGraphic} had it - fully opaque, unlike the {@code 6/9} opacity the non-{@code preserveAlpha} case above computes for the identical kernel and pixel.
     */
    @Test
    public void testFeConvolveMatrixPreserveAlphaLeavesTheOriginalAlphaUntouched() throws Exception {
        FeConvolveMatrix matrix = boxBlurKernel();
        matrix.setEdgeMode("none");
        matrix.setPreserveAlpha("true");

        Image result = filtered(redRect(), filterOf(matrix));
        assertThat(colorAt(result, 49, 25).getOpacity(), closeTo(1.0, 0.02));
    }

    /**
     * {@code edgeMode="wrap"} against a rect that fills the <i>entire</i> filter region (not just {@link #redRect()}'s 50x50 corner), so a buffer-edge pixel's kernel taps that
     * fall outside the buffer wrap around to the opposite (also fully red) edge instead of reading transparent - the same pixel would show the {@code 6/9} falloff
     * {@link #testFeConvolveMatrixBoxBlurAveragesAcrossAnEdge} computes for {@code edgeMode="none"}.
     */
    @Test
    public void testFeConvolveMatrixEdgeModeWrapPullsFromTheOppositeEdge() throws Exception {
        FeConvolveMatrix matrix = boxBlurKernel();
        matrix.setEdgeMode("wrap");

        Image result = filtered(fullRegionRedRect(), filterOf(matrix));
        assertColor(result, 0, 50, Color.RED);
    }

    @Test
    public void testFeConvolveMatrixWithAMismatchedKernelLengthDegradesToUnfiltered() throws Exception {
        FeConvolveMatrix matrix = new FeConvolveMatrix();
        matrix.setOrder("3 3");
        matrix.setKernelMatrix("1 1 1"); // needs 9 values for a 3x3 order, not 3

        Node node = onFxThread(() -> render(redRect(), filterOf(matrix)));
        assertThat(node.getEffect(), is(nullValue()));
    }

    @Test
    public void testFeMorphologyDilateSpreadsColourIntoTransparentNeighbours() throws Exception {
        FeMorphology morphology = new FeMorphology();
        morphology.setOperator("dilate");
        morphology.setRadius("2");

        Image result = filtered(redRect(), filterOf(morphology));
        // 1px outside the rect's own right edge (x=50) - the radius-2 window reaches back to the rect's last red
        // column (x=49), so this previously-transparent pixel is now covered by the dilation
        assertColor(result, 51, 25, Color.RED);
    }

    @Test
    public void testFeMorphologyErodeShrinksAwayFromTheEdge() throws Exception {
        FeMorphology morphology = new FeMorphology();
        morphology.setOperator("erode");
        morphology.setRadius("2");

        Image result = filtered(redRect(), filterOf(morphology));
        // 2px in from the rect's own right edge (x=50) - the erosion window reaches the transparent outside, so
        // this pixel is eroded away to transparent even though it was originally opaque red
        assertThat(colorAt(result, 48, 25).getOpacity(), closeTo(0.0, 0.02));
        // 3px in - just out of the radius-2 window's reach - still fully red, unaffected
        assertColor(result, 47, 25, Color.RED);
    }

    /** SVG 1.1 15.20's own explicit special case: {@code radius="0"} is transparent black, not the identity/no-op a reader might assume. */
    @Test
    public void testFeMorphologyZeroRadiusIsExplicitlyTransparentNotIdentity() throws Exception {
        FeMorphology morphology = new FeMorphology();
        morphology.setOperator("dilate");
        morphology.setRadius("0");

        Image result = filtered(redRect(), filterOf(morphology));
        assertThat(colorAt(result, 25, 25).getOpacity(), closeTo(0.0, 0.02));
    }

    /**
     * A flood-filled {@code in2} gives a spatially <i>constant</i> displacement, isolating the displacement formula itself from any spatial variation in the map - {@code
     * rgb(255,128,0)} gives {@code R=1.0} (a full, hand-computable x-shift) and {@code G} within rounding of {@code 0.5} (a near-zero y-shift, so only x moves).
     */
    @Test
    public void testFeDisplacementMapShiftsTheSourceByTheSelectedChannels() throws Exception {
        FeFlood flood = new FeFlood();
        flood.setFloodColor("rgb(255,128,0)");
        flood.setResult("map");

        FeDisplacementMap displace = new FeDisplacementMap();
        displace.setIn("SourceGraphic");
        displace.setIn2("map");
        displace.setScale("20");
        displace.setXChannelSelector("R");
        displace.setYChannelSelector("G");

        Image result = filtered(redRect(), filterOf(flood, displace));
        // output(x,y) = input(x + scale*(XC-0.5), y) = input(x+20*0.5, y) = input(x+10, y) - redRect() (originally
        // red for x in [0,50)) appears shifted left by 10: still red at x=35 (35+10=45, inside), transparent at
        // x=45 (45+10=55, outside)
        assertColor(result, 35, 25, Color.RED);
        assertThat(colorAt(result, 45, 25).getOpacity(), closeTo(0.0, 0.02));
    }

    /** SVG 1.1 15.15's own explicit special case: {@code scale="0"} "has no effect on the source image". */
    @Test
    public void testFeDisplacementMapZeroScaleLeavesTheSourceUnchanged() throws Exception {
        FeFlood flood = new FeFlood();
        flood.setFloodColor("white");
        flood.setResult("map");

        FeDisplacementMap displace = new FeDisplacementMap();
        displace.setIn("SourceGraphic");
        displace.setIn2("map");
        displace.setScale("0");

        Image result = filtered(redRect(), filterOf(flood, displace));
        assertColor(result, 25, 25, Color.RED);
        assertThat(colorAt(result, 75, 75).getOpacity(), closeTo(0.0, 0.02));
    }

    // --- feDiffuseLighting, feSpecularLighting (#173) --------------------------

    /**
     * A vertical {@code feDistantLight} ({@code elevation="90"}) makes {@code L=(0,0,1)}, so {@code N.L} reduces to {@code N}'s own {@code z} component - a closed-form check of
     * the surface normal alone, independent of the light-vector maths exercised separately below. {@link #insetRedRect()}'s 1px transparent border reaches every one of the 9 Sobel
     * boundary kernels with a genuine, non-degenerate alpha gradient - unlike a rect flush with the buffer edge (as in every other test in this file), whose neighbourhood is flat
     * everywhere and could not tell a correct boundary kernel from a wrong one.
     */
    @Test
    public void testFeDiffuseLightingSurfaceNormalUsesTheCorrectSobelKernelAtEdgesAndCorners() throws Exception {
        FeDistantLight light = new FeDistantLight();
        light.setAzimuth("0");
        light.setElevation("90");
        FeDiffuseLighting diffuse = new FeDiffuseLighting();
        diffuse.setSurfaceScale("0.5");
        diffuse.setDiffuseConstant("1");
        diffuse.getLightSources()
            .add(light);
        SvgFilter filter = filterOf(diffuse);
        filter.setColorInterpolationFilters("sRGB");

        Image result = filtered(insetRedRect(), filter);

        // deep interior: alpha is 1 throughout the 3x3 neighbourhood, so N=(0,0,1) exactly and N.L=1 - also exercises
        // lighting-color's own default (white), which no test in this section sets explicitly
        assertColor(result, 50, 50, Color.WHITE);
        // left edge, away from either corner: a pure horizontal gradient (uniform along y) - the "left column" kernel
        // (factorX=1/2) gives Nx=-2*surfaceScale=-1, Ny=0
        double edgeNz = 1.0 / Math.sqrt(1 * 1 + 1);
        assertColor(result, 0, 50, new Color(edgeNz, edgeNz, edgeNz, 1));
        // top/left corner: a genuine 2D gradient - the "top/left corner" kernel (factorX=factorY=2/3) gives
        // Nx=Ny=-(2/3)*surfaceScale=-1/3, deliberately different from what the "left column" kernel above would give
        // if it were wrongly selected here too (factorX 1/2 vs 2/3)
        double cornerN = 1.0 / 3;
        double cornerNz = 1.0 / Math.sqrt(cornerN * cornerN + cornerN * cornerN + 1);
        assertColor(result, 0, 0, new Color(cornerNz, cornerNz, cornerNz, 1));
    }

    /**
     * {@code L} genuinely depends on pixel position for {@code fePointLight} - two pixels under a flat surface (so {@code N=(0,0,1)} everywhere, isolating the light-vector maths
     * from the surface-normal maths already covered above) get different brightness purely from their different angle to the same light.
     */
    @Test
    public void testFePointLightVectorDependsOnPixelPosition() throws Exception {
        FePointLight light = new FePointLight();
        light.setX("50");
        light.setY("50");
        light.setZ("50");
        FeDiffuseLighting diffuse = new FeDiffuseLighting();
        diffuse.setSurfaceScale("1");
        diffuse.setDiffuseConstant("1");
        diffuse.getLightSources()
            .add(light);
        SvgFilter filter = filterOf(diffuse);
        filter.setColorInterpolationFilters("sRGB");

        Image result = filtered(fullRegionRedRect(), filter);

        // directly under the light: L=(0,0,1)=N, full brightness
        assertColor(result, 50, 50, Color.WHITE);
        // off to the side: Z(x,y)=surfaceScale*1=1, so L=normalize((50-30,50-50,50-1))=normalize(20,0,49), and since
        // N=(0,0,1), N.L is just L's own z component
        double lz = 49.0 / Math.sqrt(20 * 20 + 49 * 49);
        assertColor(result, 30, 50, new Color(lz, lz, lz, 1));
    }

    /**
     * {@code feSpotLight} zeroes the light colour outside its {@code limitingConeAngle} - but {@code feDiffuseLighting}'s own {@code Da=1.0} is unconditional, so a pixel with no
     * light at all is opaque black, not transparent (contrast {@code feSpecularLighting}'s own zero case below, where the whole pixel really does vanish).
     */
    @Test
    public void testFeSpotLightZeroesLightOutsideTheLimitingConeAngle() throws Exception {
        FeSpotLight light = new FeSpotLight();
        light.setX("50");
        light.setY("50");
        light.setZ("50");
        light.setPointsAtX("50");
        light.setPointsAtY("50");
        light.setPointsAtZ("0");
        light.setLimitingConeAngle("10");
        FeDiffuseLighting diffuse = new FeDiffuseLighting();
        diffuse.setSurfaceScale("1");
        diffuse.setDiffuseConstant("1");
        diffuse.getLightSources()
            .add(light);
        SvgFilter filter = filterOf(diffuse);
        filter.setColorInterpolationFilters("sRGB");

        Image result = filtered(fullRegionRedRect(), filter);

        // straight down the cone's own axis: fully lit
        assertColor(result, 50, 50, Color.WHITE);
        // far off-axis, well outside a 10 degree cone: no light, but still opaque per Da=1.0
        assertColor(result, 90, 50, Color.BLACK);
    }

    /**
     * SVG 1.1 15.22's own stated rationale: {@code Sa=max(Sr,Sg,Sb)}, not {@code 1.0} - a specular highlight adds colour <i>and</i> coverage together, so a partial highlight is
     * partly transparent, and (the degenerate case here, a light aimed straight through the surface so {@code N.H=0}) zero specular is <i>fully</i> transparent rather than opaque
     * black the way a zeroed {@code feDiffuseLighting} pixel is (see the {@code feSpotLight} test above).
     */
    @Test
    public void testFeSpecularLightingAlphaIsTheMaxChannelNotOne() throws Exception {
        FeDistantLight overhead = new FeDistantLight();
        overhead.setAzimuth("0");
        overhead.setElevation("90");
        FeSpecularLighting specular = new FeSpecularLighting();
        specular.setSurfaceScale("1");
        specular.setSpecularConstant("0.5");
        specular.setSpecularExponent("2");
        specular.setLightingColor("red");
        specular.getLightSources()
            .add(overhead);
        SvgFilter brightFilter = filterOf(specular);
        brightFilter.setColorInterpolationFilters("sRGB");

        // N=(0,0,1), L=(0,0,1), H=normalize(L+E)=(0,0,1), N.H=1, lighting-color=red (Lr=1,Lg=Lb=0):
        // Sr=specularConstant*1*1=0.5, Sg=Sb=0, Sa=max(Sr,Sg,Sb)=0.5 - stored directly (not via FilterRaster.premultiply,
        // which would instead store Sr*Sa=0.25), so PixelReader's own unpremultiply-on-readback (Sr/Sa=1.0) reports
        // this as pure, fully-saturated red at 50% opacity, not a half-strength red at 50% opacity
        Image bright = filtered(fullRegionRedRect(), brightFilter);
        assertColor(bright, 50, 50, new Color(1.0, 0.0, 0.0, 0.5));

        FeDistantLight throughSurface = new FeDistantLight();
        throughSurface.setAzimuth("0");
        throughSurface.setElevation("-90");
        FeSpecularLighting zeroSpecular = new FeSpecularLighting();
        zeroSpecular.setSurfaceScale("1");
        zeroSpecular.setSpecularConstant("1");
        zeroSpecular.setSpecularExponent("2");
        zeroSpecular.getLightSources()
            .add(throughSurface);
        SvgFilter zeroFilter = filterOf(zeroSpecular);
        zeroFilter.setColorInterpolationFilters("sRGB");

        // L=(0,0,-1), H=normalize(L+E)=normalize(0,0,0)=(0,0,0): N.H=0, so the whole result is zero, not just colour
        Image zero = filtered(fullRegionRedRect(), zeroFilter);
        assertColor(zero, 50, 50, new Color(0, 0, 0, 0));
    }

    /**
     * {@code lighting-color}'s own initial value is white (unlike {@code flood-color}'s black - see {@link #testAFloodColourSurvivesTheRoundTripThroughLinearRgb}) - every other
     * test in this section relies on that default implicitly by never setting {@code lighting-color} at all. This one sets {@code currentColor} instead, resolved through the
     * {@code <svg>} root's own {@code color} - the filter primitive's own document-tree ancestry, not the filtered target's, is what {@code currentColor} means here.
     */
    @Test
    public void testLightingColorCurrentColorResolvesThroughTheDocumentTree() throws Exception {
        FeDistantLight light = new FeDistantLight();
        light.setAzimuth("0");
        light.setElevation("90");
        FeDiffuseLighting diffuse = new FeDiffuseLighting();
        diffuse.setSurfaceScale("1");
        diffuse.setDiffuseConstant("1");
        diffuse.setLightingColor("currentColor");
        diffuse.getLightSources()
            .add(light);
        SvgFilter filter = filterOf(diffuse);
        filter.setColorInterpolationFilters("sRGB");

        Image result = filteredWithRootColor(fullRegionRedRect(), filter, "lime");

        assertColor(result, 50, 50, Color.LIME);
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
        // feConvolveMatrix (#172) - feTurbulence itself is supported now (#171)
        SvgFilter filter = filterOf(new FeConvolveMatrix());
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

    /** Fills the whole 100x100 filter region {@link #filterOf} declares, rather than {@link #redRect()}'s 50x50 corner - for a test that needs the buffer's own edges covered. */
    private static SvgRectangle fullRegionRedRect() {
        SvgRectangle rect = new SvgRectangle(0, 0, 100, 100);
        rect.setFill(Color.RED);
        rect.setFilter("url(#f)");
        return rect;
    }

    /** White, for the colour-space tests: its channels are 1.0, which halving moves into the revealing midtones. */
    private static SvgRectangle whiteRect() {
        return rectFilled(Color.WHITE);
    }

    /**
     * Opaque everywhere except a 1px transparent border on every edge of the 100x100 buffer {@link #filterOf} declares - unlike {@link #fullRegionRedRect()}, whose alpha is flat
     * even at the buffer's own edges, this reaches every one of the 9 Sobel boundary kernels a lighting primitive's surface normal uses with a genuine, non-degenerate gradient.
     */
    private static SvgRectangle insetRedRect() {
        SvgRectangle rect = new SvgRectangle(1, 1, 98, 98);
        rect.setFill(Color.RED);
        rect.setFilter("url(#f)");
        return rect;
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

    /**
     * As {@link #render}, but with {@code color} set on the {@code <svg>} root itself - for a {@code lighting-color="currentColor"} test, which resolves against the filter
     * primitive's own ancestry in the document tree (the {@code <filter>}'s parent chain up to the root here), not the filtered target's.
     */
    private static Node renderWithRootColor(SvgRectangle rect, SvgFilter filter, String rootColor) {
        SvgGraphic svg = new SvgGraphic();
        svg.setColor(rootColor);
        svg.getContent()
            .add(filter);
        return rect.createGraphic(RenderContext.root(svg.getElementIndex(), 0, 0));
    }

    private static Image filteredWithRootColor(SvgRectangle rect, SvgFilter filter, String rootColor) throws Exception {
        Node node = onFxThread(() -> renderWithRootColor(rect, filter, rootColor));
        assertThat("expected the raster pipeline to have applied an ImageInput", node.getEffect(), notNullValue());
        return ((ImageInput) node.getEffect()).getSource();
    }

    /**
     * {@code wide.png}'s own classpath location, as a base URI a relative {@code xlink:href="wide.png"} resolves cleanly against - the file itself, per {@link URI#resolve}'s own
     * "replace the last path segment" semantics.
     */
    private static URI wideBaseUri() throws Exception {
        return SvgFilterRasterPipelineTest.class.getResource("/wide.png")
            .toURI();
    }

    /**
     * As {@link #render}, but with a base URI established for {@code feImage}'s own relative {@code xlink:href} resolution, extra document content (for a same-document reference)
     * available to resolve against, and - unlike every other test in this file - a real 100x100 viewport: {@code feImage}'s own default subregion is {@code 0%}/{@code 0%}/
     * {@code 100%}/{@code 100%} of the <i>current viewport</i> under {@code primitiveUnits="userSpaceOnUse"} (the default), which a {@code 0}-sized viewport (every other test's
     * setup, never needing a viewport-relative percentage) would resolve to nothing.
     */
    private static Node renderWithContext(SvgRectangle rect, SvgFilter filter, URI baseUri, ISvgElement... extraContent) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(filter);
        for (ISvgElement extra : extraContent) {
            svg.getContent()
                .add(extra);
        }
        RenderContext context = RenderContext.root(svg.getElementIndex(), 100, 100);
        if (baseUri != null) {
            context = context.withBaseUri(baseUri);
        }
        return rect.createGraphic(context);
    }

    private static Image filteredWithBaseUri(SvgRectangle rect, SvgFilter filter, URI baseUri) throws Exception {
        Node node = onFxThread(() -> renderWithContext(rect, filter, baseUri));
        assertThat("expected the raster pipeline to have applied an ImageInput", node.getEffect(), notNullValue());
        return ((ImageInput) node.getEffect()).getSource();
    }

    private static Image filteredWithExtraContent(SvgRectangle rect, SvgFilter filter, ISvgElement... extraContent) throws Exception {
        Node node = onFxThread(() -> renderWithContext(rect, filter, null, extraContent));
        assertThat("expected the raster pipeline to have applied an ImageInput", node.getEffect(), notNullValue());
        return ((ImageInput) node.getEffect()).getSource();
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
