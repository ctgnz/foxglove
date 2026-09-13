package nz.co.ctg.foxglove.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.RenderContext.UnitsMode;

import javafx.application.Platform;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Applies {@code filter="url(#id)"} to an already-built {@code node}, in place - the filter region
 * ({@code filterUnits}/{@code primitiveUnits}) plus a chain-of-primitives fast path (stages 1 and 2 of #26/#76).
 * <p>
 * A {@code <filter>}'s primitives form a directed graph via named {@code result}/{@code in}/{@code in2}, but a
 * JavaFX {@link Effect} chains through a single {@code input} (two, for {@link Blend}) - so only primitives that
 * genuinely map onto a real {@code Effect} with such a slot are supported: {@link FeGaussianBlur} → {@link
 * GaussianBlur}, {@link FeFlood} → {@link ColorInput}, {@link FeColorMatrix}'s {@code saturate}/{@code hueRotate}
 * shorthand → {@link ColorAdjust}, {@link FeBlend} → {@link Blend}, {@link FeMerge}/{@link FeMergeNode} → a fold of
 * {@link Blend}. Deliberately excluded, not just deferred: {@code feOffset} has no {@code Effect} equivalent at all
 * (verified against the OpenJFX javadoc - every effect's input is another {@code Effect}, and there is no way to
 * hand a translated raw node into one), and {@code feDiffuseLighting}/{@code feSpecularLighting}/{@code feImage}
 * need machinery (light sources, image loading) beyond what a chain needs.
 * <p>
 * A filter whose primitives don't fit this shape at all (an excluded primitive, a graph that isn't resolvable
 * through blank/{@code SourceGraphic}/an earlier {@code result} - branches, {@code SourceAlpha}) no longer degrades
 * straight to no effect: it falls back to {@link SvgFilterRasterPipeline}, which evaluates the primitive graph as
 * real pixels (#77). The effect chain is tried first because it is strictly better where it applies - the result
 * stays vector, so it scales crisply and costs no rasterisation - with the raster pipeline picking up everything it
 * cannot express. Only when that fails too does {@code node} render unfiltered.
 * <p>
 * <b>This path is an sRGB-only fast path</b> (#126). JavaFX effects operate in sRGB and expose no way to ask
 * otherwise - verified by blurring a hard black/white edge, whose midpoint comes back at {@code 0.52} rather than
 * the {@code 0.735} a linear-light blur would give - while the raster pipeline honours
 * {@code color-interpolation-filters} and works in linearRGB by default, per the specification (#108). Rather than
 * let the same document render differently depending on which path happened to take it, the chain is used only when
 * every primitive resolves to sRGB. Since linearRGB is the default, that means only when a document asks for sRGB
 * outright.
 * <p>
 * The trade-off was measured rather than assumed, and is recorded on #126: the chain was carrying 22 of 176 filter
 * applications in the W3C suite, and rasterising those was uniformly more accurate (the {@code filters} chapter
 * moved from 29.4% to 31.9% ink matched). What it costs is resolution independence - on a hard edge magnified
 * eightfold the chain stays perfectly crisp where the raster smears over about eight pixels - which is why the
 * chain is kept for the documents that do opt in rather than deleted outright.
 * <p>
 * One wart survives, now narrowed to those documents: {@code feColorMatrix}'s {@code saturate}/{@code hueRotate}
 * keep the approximate {@link ColorAdjust} treatment below, while the raster pipeline computes them exactly.
 * <p>
 * Unlike {@code mask} (#25), this mutates {@code node} in place ({@code setEffect}/{@code setClip}) rather than
 * replacing it, so it needs none of masking's consumer-side indirection - {@code AbstractSvgShape}'s narrower
 * {@code S extends Shape} return type is not in the way here.
 */
public final class SvgFilterRenderer {

    /**
     * The conversion JavaFX itself uses internally (verified against the OpenJFX source, since the public
     * {@link GaussianBlur#radiusProperty()} javadoc states no formula): {@code sigma = radius / 3}, i.e.
     * {@code radius = 3 * stdDeviation}.
     */
    private static final double STD_DEVIATION_TO_RADIUS = 3.0;

    /**
     * The only logging in this library. A filter that cannot be rendered at all produces nothing visible, which is
     * otherwise indistinguishable from one that rendered correctly but subtly - so the two reasons that can happen
     * are reported rather than swallowed, at levels that reflect how actionable each is:
     * <ul>
     * <li>{@code FINE} - the filter genuinely uses something unsupported (see {@link SvgFilterRasterPipeline}'s own
     * list of gaps). Expected, and far too common in real documents to warrant anything louder.
     * <li>{@code WARNING} - the filter could have rendered, but rasterising was not possible, almost always because
     * the scene graph is being built off the JavaFX Application Thread, which {@code Node.snapshot} requires. That
     * is an environmental problem the calling application can actually act on.
     * </ul>
     * Uses {@code java.util.logging} deliberately: no new dependency, part of the JDK, what JavaFX itself uses, and
     * routable to SLF4J/Log4j by a consuming application that wants that.
     */
    private static final Logger LOG = Logger.getLogger(SvgFilterRenderer.class.getName());

    public static void apply(RenderContext context, Node node, SvgFilter filter) {
        Bounds targetBounds = node.getBoundsInLocal();
        Bounds region = resolveFilterRegion(filter, context, targetBounds);
        applyChain(node, filter, targetBounds, context, region);
        applyFilterRegionClip(node, region);
    }

    /**
     * Builds a JavaFX effect chain by walking {@code filter}'s primitives in document order, tracking the
     * previous primitive's built {@link Effect} (what a blank {@code in} on any primitive after the first resolves
     * to, per spec) and a name→{@code Effect} map for anything that declared its own {@code result}. Aborts to no
     * effect at all - rather than a partially-built one - the moment anything doesn't fit.
     */
    private static void applyChain(Node node, SvgFilter filter, Bounds targetBounds, RenderContext context, Bounds region) {
        List<ISvgFilterPrimitive> primitives = filter.getContent().stream()
            .filter(ISvgFilterPrimitive.class::isInstance)
            .map(ISvgFilterPrimitive.class::cast)
            .toList();
        if (primitives.isEmpty()) {
            return;
        }
        if (!allPrimitivesUseSrgb(primitives, filter)) {
            applyRasterPipeline(context, node, filter, primitives, targetBounds, region);
            return;
        }
        try {
            Map<String, Effect> namedResults = new HashMap<>();
            Effect current = null;
            boolean first = true;
            for (ISvgFilterPrimitive primitive : primitives) {
                current = buildEffect(primitive, current, first, namedResults, filter, targetBounds, context);
                first = false;
                String result = primitive.getResult();
                if (StringUtils.isNotBlank(result)) {
                    namedResults.put(result, current);
                }
            }
            node.setEffect(current);
        } catch (UnsupportedFilterException e) {
            applyRasterPipeline(context, node, filter, primitives, targetBounds, region);
        }
    }

    /**
     * Whether every primitive works in sRGB, which is what this path requires: JavaFX effects operate in sRGB and
     * expose no way to ask otherwise (#126). SVG's default is linearRGB, so in practice this is true only when a
     * document says {@code color-interpolation-filters="sRGB"} outright.
     * <p>
     * Every primitive has to agree, not just the filter: the property is per-primitive, and an effect chain has no
     * way to represent a graph that changes colour space partway through.
     */
    private static boolean allPrimitivesUseSrgb(List<ISvgFilterPrimitive> primitives, SvgFilter filter) {
        return primitives.stream().allMatch(primitive -> FilterColorSpace.of(primitive, filter) == FilterColorSpace.SRGB);
    }

    /**
     * The fallback for everything the effect chain above cannot express - see {@link SvgFilterRasterPipeline}. Its
     * own failure to render is the last word: {@code node} then stays unfiltered, reported per {@link #LOG}.
     */
    private static void applyRasterPipeline(RenderContext context, Node node, SvgFilter filter, List<ISvgFilterPrimitive> primitives,
        Bounds targetBounds, Bounds region) {
        if (SvgFilterRasterPipeline.apply(context, node, filter, primitives, targetBounds, region)) {
            return;
        }
        if (Platform.isFxApplicationThread()) {
            LOG.fine(() -> "Filter '" + filter.getId() + "' uses features this renderer does not support; rendering unfiltered.");
        } else {
            LOG.warning(() -> "Filter '" + filter.getId() + "' needs rasterising, which requires the JavaFX Application Thread - "
                + "build the scene graph there (see Platform.runLater) for it to render. Rendering unfiltered.");
        }
    }

    private static Effect buildEffect(ISvgFilterPrimitive primitive, Effect previousResult, boolean first, Map<String, Effect> namedResults,
        SvgFilter filter, Bounds targetBounds, RenderContext context) {
        if (primitive instanceof FeGaussianBlur blur) {
            return buildGaussianBlur(blur, previousResult, first, namedResults, filter, targetBounds);
        }
        if (primitive instanceof FeFlood flood) {
            return buildColorInput(flood, filter, targetBounds, context);
        }
        if (primitive instanceof FeColorMatrix matrix) {
            return buildColorAdjust(matrix, previousResult, first, namedResults);
        }
        if (primitive instanceof FeBlend blend) {
            return buildBlend(blend, previousResult, first, namedResults);
        }
        if (primitive instanceof FeMerge merge) {
            return buildMerge(merge, previousResult, first, namedResults);
        }
        throw new UnsupportedFilterException();
    }

    /**
     * Resolves a primitive's {@code in} (or a {@code feMergeNode}'s): blank resolves to {@code SourceGraphic} only
     * for the very first primitive in the filter, otherwise to the previous primitive's own result, per spec - both
     * mean "the plain node itself" here, represented as {@code null} (every {@link Effect} used here already treats
     * a {@code null} input that way). An explicit {@code SourceGraphic} always means the plain node, regardless of
     * position. Anything else must name an earlier {@code result}; a reference to nothing this renderer tracked
     * (a genuine {@code SourceAlpha}/{@code BackgroundImage}, or a name from outside this supported subset) aborts
     * the whole filter.
     */
    private static Effect resolveInput(String in, Effect previousResult, boolean first, Map<String, Effect> namedResults) {
        String ref = StringUtils.trimToEmpty(in);
        if (ref.isEmpty()) {
            return first ? null : previousResult;
        }
        if ("SourceGraphic".equals(ref)) {
            return null;
        }
        if (namedResults.containsKey(ref)) {
            return namedResults.get(ref);
        }
        throw new UnsupportedFilterException();
    }

    private static GaussianBlur buildGaussianBlur(FeGaussianBlur blur, Effect previousResult, boolean first, Map<String, Effect> namedResults,
        SvgFilter filter, Bounds targetBounds) {
        Effect in = resolveInput(blur.getIn(), previousResult, first, namedResults);
        double stdDeviation = firstNumber(blur.getStdDeviation());
        if (RenderContext.parseUnits(filter.getPrimitiveUnits(), UnitsMode.USER_SPACE_ON_USE) == UnitsMode.OBJECT_BOUNDING_BOX) {
            stdDeviation *= bboxDiagonal(targetBounds);
        }
        double radius = Math.clamp(STD_DEVIATION_TO_RADIUS * stdDeviation, 0.0, 63.0);
        GaussianBlur gaussianBlur = new GaussianBlur(radius);
        gaussianBlur.setInput(in);
        return gaussianBlur;
    }

    /**
     * A leaf - {@code feFlood} has no {@code in} of its own. Always sized to the filter region rather than the
     * primitive's own (optional) subregion: {@code ISvgFilterPrimitive}'s x/y/width/height are plain strings with
     * their own unit-mode rules, and a document setting them on a bare {@code feFlood} specifically is rare enough
     * that this is a deliberate, documented simplification rather than the first thing worth the extra parsing.
     */
    private static ColorInput buildColorInput(FeFlood flood, SvgFilter filter, Bounds targetBounds, RenderContext context) {
        Bounds region = resolveFilterRegion(filter, context, targetBounds);
        Color color = parseFloodColor(flood.getFloodColor());
        Double opacity = ISvgGraphicsAttributes.parseOpacity(flood.getFloodOpacity());
        if (opacity != null) {
            color = color.deriveColor(0, 1, 1, opacity);
        }
        return new ColorInput(region.getMinX(), region.getMinY(), region.getWidth(), region.getHeight(), color);
    }

    static Color parseFloodColor(String value) {
        if (StringUtils.isBlank(value)) {
            return Color.BLACK;
        }
        try {
            return Color.web(value.trim());
        } catch (RuntimeException e) {
            return Color.BLACK;
        }
    }

    /**
     * Only the {@code saturate}/{@code hueRotate} shorthand forms - {@code matrix}/{@code luminanceToAlpha} need an
     * arbitrary matrix {@link ColorAdjust} has no way to express, and abort the filter like any other unsupported
     * primitive. Neither shorthand's scale matches {@link ColorAdjust}'s {@code [-1, 1]} range exactly (SVG's
     * {@code saturate} is {@code [0, 1]} with identity {@code 1}; {@code hueRotate} is degrees) - documented
     * approximations, not exact fidelity.
     */
    private static ColorAdjust buildColorAdjust(FeColorMatrix matrix, Effect previousResult, boolean first, Map<String, Effect> namedResults) {
        String type = matrix.getType();
        if (!"saturate".equals(type) && !"hueRotate".equals(type)) {
            throw new UnsupportedFilterException();
        }
        Effect in = resolveInput(matrix.getIn(), previousResult, first, namedResults);
        double value = firstNumber(matrix.getValues());
        ColorAdjust adjust = new ColorAdjust();
        if ("saturate".equals(type)) {
            adjust.setSaturation(Math.clamp(value - 1, -1.0, 1.0));
        } else {
            adjust.setHue(Math.clamp(value / 180.0, -1.0, 1.0));
        }
        adjust.setInput(in);
        return adjust;
    }

    /**
     * Per SVG 1.1, {@code in} is image A and {@code in2} is image B, and every {@code feBlend} formula composites A
     * over B - {@code normal} is {@code cr = (1 - qa) * cb + ca}, plain source-over with A as the source. So
     * {@code in} is the <b>top</b> layer, which is what {@link Blend#topInputProperty()} means too.
     * <p>
     * These were the wrong way round until #107, so an asymmetric mode rendered with its operands swapped whenever
     * the two inputs overlapped with partial coverage. {@link SvgFilterRasterPipeline#blend} always had it right,
     * which meant the same document rendered differently depending on which of the two paths happened to take it.
     */
    private static Blend buildBlend(FeBlend blend, Effect previousResult, boolean first, Map<String, Effect> namedResults) {
        Effect top = resolveInput(blend.getIn(), previousResult, first, namedResults);
        Effect bottom = resolveInput(blend.getIn2(), previousResult, first, namedResults);
        Blend result = new Blend(mapBlendMode(blend.getMode()));
        result.setBottomInput(bottom);
        result.setTopInput(top);
        return result;
    }

    private static BlendMode mapBlendMode(String mode) {
        return switch (mode) {
            case "multiply" -> BlendMode.MULTIPLY;
            case "screen" -> BlendMode.SCREEN;
            case "darken" -> BlendMode.DARKEN;
            case "lighten" -> BlendMode.LIGHTEN;
            default -> BlendMode.SRC_OVER;
        };
    }

    /**
     * A left-to-right fold of {@link Blend}, each {@code feMergeNode} layered on top of the accumulated result so
     * far - matching the specification's own compositing order. Every node's {@code in} resolves against the same
     * {@code first}/{@code previousResult} the {@code <feMerge>} element itself would have, per spec (a blank
     * {@code in} means the result of the primitive before the whole {@code <feMerge>}, not the previous merge
     * node). Fewer than two nodes is degenerate and aborts, same as any other unsupported shape.
     */
    private static Effect buildMerge(FeMerge merge, Effect previousResult, boolean first, Map<String, Effect> namedResults) {
        List<FeMergeNode> nodes = merge.getFeMergeNode();
        if (nodes.size() < 2) {
            throw new UnsupportedFilterException();
        }
        Effect accumulated = resolveInput(nodes.get(0).getIn(), previousResult, first, namedResults);
        for (int i = 1; i < nodes.size(); i++) {
            Effect top = resolveInput(nodes.get(i).getIn(), previousResult, first, namedResults);
            Blend blend = new Blend(BlendMode.SRC_OVER);
            blend.setBottomInput(accumulated);
            blend.setTopInput(top);
            accumulated = blend;
        }
        return accumulated;
    }

    /**
     * The first number in a {@code <number-optional-number>} value such as {@code stdDeviation}, or a bare
     * {@code values} number such as {@code feColorMatrix}'s shorthand forms use - {@link GaussianBlur} has one
     * isotropic radius, so a second (anisotropic) {@code stdDeviation} number is not representable and is ignored,
     * a documented limitation.
     */
    private static double firstNumber(String value) {
        String text = StringUtils.trimToEmpty(value);
        String first = text.split("[\\s,]+", 2)[0];
        return NumberUtils.isParsable(first) ? NumberUtils.toDouble(first, 0.0) : 0.0;
    }

    /**
     * Per SVG 1.1 7.10, the same formula {@link RenderContext#resolveLength} uses for a viewport-relative
     * {@code Axis.DIAGONAL} length, but keyed to the target's own bounding box rather than the viewport - what
     * {@code primitiveUnits="objectBoundingBox"} resolves a fraction against.
     */
    static double bboxDiagonal(Bounds bounds) {
        return Math.sqrt((bounds.getWidth() * bounds.getWidth() + bounds.getHeight() * bounds.getHeight()) / 2.0);
    }

    /**
     * The filter region ({@code x}/{@code y}/{@code width}/{@code height}, {@code filterUnits} - default
     * {@code objectBoundingBox}, same -10%/-10%/120%/120% defaults as {@code mask}'s region), applied as a clip.
     * Nests with any clip {@code node} already carries (from {@code clip-path}, #24) via
     * {@code node.getClip().setClip(...)} - {@code Node.setClip()} nesting is a plain set intersection, the same
     * technique #24 used for a nested {@code clip-path} - rather than overwriting it. Bounding a blur's spread to the
     * filter region is genuinely spec-correct, not just plumbing for its own sake: it is why the default region is
     * 120%, not the shape's own exact bounds.
     */
    private static void applyFilterRegionClip(Node node, Bounds region) {
        if (region.getWidth() <= 0 || region.getHeight() <= 0) {
            return;
        }
        Rectangle regionRect = new Rectangle(region.getMinX(), region.getMinY(), region.getWidth(), region.getHeight());
        Node existingClip = node.getClip();
        if (existingClip != null) {
            existingClip.setClip(regionRect);
        } else {
            node.setClip(regionRect);
        }
    }

    private static Bounds resolveFilterRegion(SvgFilter filter, RenderContext context, Bounds targetBounds) {
        Size x = ObjectUtils.defaultIfNull(filter.getX(), new Size(-10, SizeUnits.PERCENT));
        Size y = ObjectUtils.defaultIfNull(filter.getY(), new Size(-10, SizeUnits.PERCENT));
        Size width = ObjectUtils.defaultIfNull(filter.getWidth(), new Size(120, SizeUnits.PERCENT));
        Size height = ObjectUtils.defaultIfNull(filter.getHeight(), new Size(120, SizeUnits.PERCENT));

        UnitsMode unitsMode = RenderContext.parseUnits(filter.getFilterUnits(), UnitsMode.OBJECT_BOUNDING_BOX);
        if (unitsMode == UnitsMode.OBJECT_BOUNDING_BOX) {
            double regionX = targetBounds.getMinX() + RenderContext.resolveFraction(x) * targetBounds.getWidth();
            double regionY = targetBounds.getMinY() + RenderContext.resolveFraction(y) * targetBounds.getHeight();
            double regionWidth = RenderContext.resolveFraction(width) * targetBounds.getWidth();
            double regionHeight = RenderContext.resolveFraction(height) * targetBounds.getHeight();
            return new BoundingBox(regionX, regionY, regionWidth, regionHeight);
        }
        return new BoundingBox(context.resolveLength(x, RenderContext.Axis.HORIZONTAL), context.resolveLength(y, RenderContext.Axis.VERTICAL),
            context.resolveLength(width, RenderContext.Axis.HORIZONTAL), context.resolveLength(height, RenderContext.Axis.VERTICAL));
    }

    private SvgFilterRenderer() {
    }

}
