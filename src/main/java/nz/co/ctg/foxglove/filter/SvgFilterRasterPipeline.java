package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.RenderContext.UnitsMode;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

/**
 * Stage 3 of the filter work (#77): evaluates a {@code <filter>}'s primitive graph as real pixels, for everything
 * {@link SvgFilterRenderer}'s JavaFX-effect chain cannot express - the pixel-level primitives that have no
 * {@code javafx.scene.effect} equivalent, and arbitrary (non-chain) graphs where a named {@code result} is branched,
 * converged, or referenced out of order.
 * <p>
 * The result is applied <b>in place</b>, via {@link ImageInput} as the node's own effect. Masking (#25) had to move
 * to the consumer side because it replaces its node and {@code AbstractSvgShape}'s narrower {@code S extends Shape}
 * return type cannot carry an {@code ImageView}; this needs none of that - verified empirically before the design
 * was settled on: a node with an {@code ImageInput} effect renders that image rather than its own geometry, the
 * image's {@code x}/{@code y} are in the node's own local space, the image is <i>not</i> clipped to the node's own
 * geometry (so a blur may spread beyond the shape), and an existing {@code setClip} still applies on top (so the
 * filter-region clip composes correctly).
 * <p>
 * Buffers are rasterised at one pixel per user unit and never supersampled - {@link ImageInput} has no scale of its
 * own, unlike the {@code ImageView} masking wraps in a scaled {@link Group}. That is spec-aligned in principle,
 * since filters are defined on a pixel grid, though {@code filterRes} is not honoured.
 * <p>
 * Known gaps, each degrading the whole filter to unfiltered rather than rendering something wrong: the primitives
 * not listed in {@link #evaluate} ({@code feTurbulence}, {@code feConvolveMatrix}, {@code feMorphology},
 * {@code feDisplacementMap}, {@code feTile}, {@code feImage}, the lighting primitives), per-primitive subregions
 * ({@code x}/{@code y}/{@code width}/{@code height} on an individual {@code fe*}), and {@code in="BackgroundImage"}.
 * Separately, and unlike those: this works in <b>sRGB</b> where the specification's default working space for
 * filters is linearRGB, so colours from the interpolating primitives differ from a fully conformant renderer's.
 */
final class SvgFilterRasterPipeline {

    /**
     * An upper bound on either dimension of the rasterised filter region, so a pathological region (a huge shape, or
     * a {@code filterUnits="userSpaceOnUse"} region declared far larger than anything visible) degrades rather than
     * trying to allocate an unbounded buffer.
     */
    private static final int MAX_DIMENSION = 4096;

    private final SvgFilter filter;
    private final RenderContext context;
    private final Bounds targetBounds;
    private final Bounds region;
    private final int width;
    private final int height;

    private final Map<String, FilterRaster> namedResults = new HashMap<>();
    private FilterRaster sourceGraphic;
    private FilterRaster sourceAlpha;
    private FilterRaster previous;
    private boolean first = true;

    private SvgFilterRasterPipeline(SvgFilter filter, RenderContext context, Bounds targetBounds, Bounds region, int width, int height) {
        this.filter = filter;
        this.context = context;
        this.targetBounds = targetBounds;
        this.region = region;
        this.width = width;
        this.height = height;
    }

    /**
     * Renders {@code filter}'s primitives against {@code node} and sets the result as its effect, returning whether
     * it could. {@code false} means the caller should leave {@code node} unfiltered - either the filter uses
     * something still unsupported, or rasterising was not possible at all (see {@link #rasterizeSource}).
     */
    static boolean apply(RenderContext context, Node node, SvgFilter filter, List<ISvgFilterPrimitive> primitives, Bounds targetBounds,
        Bounds region) {
        int width = (int) Math.round(region.getWidth());
        int height = (int) Math.round(region.getHeight());
        if (width <= 0 || height <= 0 || width > MAX_DIMENSION || height > MAX_DIMENSION) {
            return false;
        }

        SvgFilterRasterPipeline pipeline = new SvgFilterRasterPipeline(filter, context, targetBounds, region, width, height);
        FilterRaster result = pipeline.run(node, primitives);
        if (result == null) {
            return false;
        }
        WritableImage image = result.toImage();
        node.setEffect(new ImageInput(image, region.getMinX(), region.getMinY()));
        return true;
    }

    private FilterRaster run(Node node, List<ISvgFilterPrimitive> primitives) {
        sourceGraphic = rasterizeSource(node);
        if (sourceGraphic == null) {
            return null;
        }
        try {
            FilterRaster result = null;
            for (ISvgFilterPrimitive primitive : primitives) {
                result = evaluate(primitive);
                previous = result;
                first = false;
                String name = StringUtils.trimToEmpty(primitive.getResult());
                if (!name.isEmpty()) {
                    namedResults.put(name, result);
                }
            }
            return result;
        } catch (UnsupportedFilterException e) {
            return null;
        }
    }

    /**
     * Snapshots {@code node} over the filter region, the same technique {@code SvgMaskRenderer.rasterize} and
     * {@code SvgPattern} already use - the node's own transforms are cleared first, since an explicit snapshot
     * viewport is read in post-transform space while the region is in the pre-transform local space
     * {@code getBoundsInLocal} gave it. Unlike masking, which discards its node, everything is put back afterwards:
     * this node goes on to be rendered for real.
     * <p>
     * Returns {@code null} when snapshotting is not possible at all - most usually because the caller is not on the
     * JavaFX Application Thread, which {@code Node.snapshot} requires. That is the same constraint masking already
     * carries, and callers degrade to an unfiltered node rather than propagating it.
     */
    private FilterRaster rasterizeSource(Node node) {
        List<Transform> ownTransforms = List.copyOf(node.getTransforms());
        double translateX = node.getTranslateX();
        double translateY = node.getTranslateY();
        Group holder = new Group();
        try {
            node.getTransforms().clear();
            node.setTranslateX(0);
            node.setTranslateY(0);

            holder.getChildren().add(node);
            new Scene(holder);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            params.setViewport(new Rectangle2D(region.getMinX(), region.getMinY(), region.getWidth(), region.getHeight()));
            WritableImage image = node.snapshot(params, null);
            return FilterRaster.fromImage(image, width, height);
        } catch (Throwable e) {
            return null;
        } finally {
            // detach from the throwaway scene, so the node is parentless again for whoever actually renders it
            holder.getChildren().remove(node);
            node.getTransforms().setAll(ownTransforms);
            node.setTranslateX(translateX);
            node.setTranslateY(translateY);
        }
    }

    private FilterRaster evaluate(ISvgFilterPrimitive primitive) {
        if (primitive instanceof FeFlood flood) {
            return flood(flood);
        }
        if (primitive instanceof FeOffset offset) {
            return offset(offset);
        }
        if (primitive instanceof FeMerge merge) {
            return merge(merge);
        }
        if (primitive instanceof FeBlend blend) {
            return blend(blend);
        }
        if (primitive instanceof FeComposite composite) {
            return composite(composite);
        }
        if (primitive instanceof FeColorMatrix matrix) {
            return colorMatrix(matrix);
        }
        if (primitive instanceof FeComponentTransfer transfer) {
            return componentTransfer(transfer);
        }
        if (primitive instanceof FeGaussianBlur blur) {
            return gaussianBlur(blur);
        }
        throw new UnsupportedFilterException();
    }

    /**
     * Resolves a primitive's {@code in}: blank means {@code SourceGraphic} for the very first primitive and the
     * previous primitive's result thereafter, per spec. Unlike the effect-chain path, {@code SourceAlpha} and any
     * earlier named {@code result} both resolve here - retaining every result, rather than only the previous one, is
     * exactly what lets a branching or out-of-order graph work. {@code BackgroundImage} and friends still abort.
     */
    private FilterRaster resolveInput(String in) {
        String ref = StringUtils.trimToEmpty(in);
        if (ref.isEmpty()) {
            return first ? sourceGraphic : previous;
        }
        if ("SourceGraphic".equals(ref)) {
            return sourceGraphic;
        }
        if ("SourceAlpha".equals(ref)) {
            return sourceAlpha();
        }
        FilterRaster named = namedResults.get(ref);
        if (named == null) {
            throw new UnsupportedFilterException();
        }
        return named;
    }

    /** {@code SourceGraphic}'s alpha channel alone, colour zeroed - built once, on first use. */
    private FilterRaster sourceAlpha() {
        if (sourceAlpha == null) {
            sourceAlpha = sourceGraphic.newLike();
            float[] source = sourceGraphic.getData();
            float[] target = sourceAlpha.getData();
            for (int i = 3; i < source.length; i += 4) {
                target[i] = source[i];
            }
        }
        return sourceAlpha;
    }

    // --- primitives ----------------------------------------------------------

    private FilterRaster flood(FeFlood flood) {
        Color color = SvgFilterRenderer.parseFloodColor(flood.getFloodColor());
        Double opacity = ISvgGraphicsAttributes.parseOpacity(flood.getFloodOpacity());
        float alpha = (float) (color.getOpacity() * (opacity != null ? opacity : 1.0));
        FilterRaster result = new FilterRaster(width, height);
        float[] data = result.getData();
        float red = (float) color.getRed() * alpha;
        float green = (float) color.getGreen() * alpha;
        float blue = (float) color.getBlue() * alpha;
        for (int i = 0; i < data.length; i += 4) {
            data[i] = red;
            data[i + 1] = green;
            data[i + 2] = blue;
            data[i + 3] = alpha;
        }
        return result;
    }

    private FilterRaster offset(FeOffset offset) {
        FilterRaster in = resolveInput(offset.getIn());
        double dx = number(offset.getDx(), 0);
        double dy = number(offset.getDy(), 0);
        if (primitiveUnits() == UnitsMode.OBJECT_BOUNDING_BOX) {
            dx *= targetBounds.getWidth();
            dy *= targetBounds.getHeight();
        }
        int shiftX = (int) Math.round(dx);
        int shiftY = (int) Math.round(dy);

        FilterRaster result = in.newLike();
        float[] source = in.getData();
        float[] target = result.getData();
        for (int y = 0; y < height; y++) {
            int sourceY = y - shiftY;
            if (sourceY < 0 || sourceY >= height) {
                continue;
            }
            for (int x = 0; x < width; x++) {
                int sourceX = x - shiftX;
                if (sourceX < 0 || sourceX >= width) {
                    continue;
                }
                System.arraycopy(source, in.index(sourceX, sourceY), target, result.index(x, y), 4);
            }
        }
        return result;
    }

    /** Each {@code feMergeNode} composited src-over the accumulated result, bottom-most first, per spec. */
    private FilterRaster merge(FeMerge merge) {
        FilterRaster result = new FilterRaster(width, height);
        for (FeMergeNode node : merge.getFeMergeNode()) {
            result = sourceOver(resolveInput(node.getIn()), result);
        }
        return result;
    }

    /** {@code top} over {@code bottom}, both premultiplied. */
    private FilterRaster sourceOver(FilterRaster top, FilterRaster bottom) {
        FilterRaster result = new FilterRaster(width, height);
        float[] a = top.getData();
        float[] b = bottom.getData();
        float[] out = result.getData();
        for (int i = 0; i < out.length; i += 4) {
            float alphaA = a[i + 3];
            for (int c = 0; c < 4; c++) {
                out[i + c] = a[i + c] + b[i + c] * (1 - alphaA);
            }
        }
        return result;
    }

    /**
     * Per SVG 1.1, {@code in} is image A (the top layer) and {@code in2} is image B; the result alpha is
     * {@code qr = 1 - (1-qa)*(1-qb)} for every mode.
     */
    private FilterRaster blend(FeBlend blend) {
        FilterRaster top = resolveInput(blend.getIn());
        FilterRaster bottom = resolveInput(blend.getIn2());
        String mode = StringUtils.trimToEmpty(blend.getMode());

        FilterRaster result = new FilterRaster(width, height);
        float[] a = top.getData();
        float[] b = bottom.getData();
        float[] out = result.getData();
        for (int i = 0; i < out.length; i += 4) {
            float qa = a[i + 3];
            float qb = b[i + 3];
            for (int c = 0; c < 3; c++) {
                float ca = a[i + c];
                float cb = b[i + c];
                out[i + c] = switch (mode) {
                    case "multiply" -> (1 - qa) * cb + (1 - qb) * ca + ca * cb;
                    case "screen" -> cb + ca - ca * cb;
                    case "darken" -> Math.min((1 - qa) * cb + ca, (1 - qb) * ca + cb);
                    case "lighten" -> Math.max((1 - qa) * cb + ca, (1 - qb) * ca + cb);
                    default -> (1 - qa) * cb + ca;
                };
            }
            out[i + 3] = 1 - (1 - qa) * (1 - qb);
        }
        return result;
    }

    /** Porter-Duff, plus SVG's own {@code arithmetic} operator - all defined directly on premultiplied values. */
    private FilterRaster composite(FeComposite composite) {
        FilterRaster inA = resolveInput(composite.getIn());
        FilterRaster inB = resolveInput(composite.getIn2());
        String operator = StringUtils.trimToEmpty(composite.getOperator());

        if ("arithmetic".equals(operator)) {
            return arithmetic(inA, inB, composite);
        }

        FilterRaster result = new FilterRaster(width, height);
        float[] a = inA.getData();
        float[] b = inB.getData();
        float[] out = result.getData();
        for (int i = 0; i < out.length; i += 4) {
            float qa = a[i + 3];
            float qb = b[i + 3];
            for (int c = 0; c < 4; c++) {
                float ca = a[i + c];
                float cb = b[i + c];
                out[i + c] = switch (operator) {
                    case "in" -> ca * qb;
                    case "out" -> ca * (1 - qb);
                    case "atop" -> ca * qb + cb * (1 - qa);
                    case "xor" -> ca * (1 - qb) + cb * (1 - qa);
                    default -> ca + cb * (1 - qa);
                };
            }
        }
        return result;
    }

    /** {@code result = k1*i1*i2 + k2*i1 + k3*i2 + k4}, per channel, on premultiplied values. */
    private FilterRaster arithmetic(FilterRaster inA, FilterRaster inB, FeComposite composite) {
        float k1 = (float) number(composite.getK1(), 0);
        float k2 = (float) number(composite.getK2(), 0);
        float k3 = (float) number(composite.getK3(), 0);
        float k4 = (float) number(composite.getK4(), 0);

        FilterRaster result = new FilterRaster(width, height);
        float[] a = inA.getData();
        float[] b = inB.getData();
        float[] out = result.getData();
        for (int i = 0; i < out.length; i++) {
            out[i] = FilterRaster.clamp(k1 * a[i] * b[i] + k2 * a[i] + k3 * b[i] + k4);
        }
        return result;
    }

    /**
     * All four types, on non-premultiplied values as the specification defines them - including the full 5x4
     * {@code matrix} and {@code luminanceToAlpha} forms the effect-chain path has no way to express (it can only
     * approximate {@code saturate}/{@code hueRotate} through {@code ColorAdjust}).
     */
    private FilterRaster colorMatrix(FeColorMatrix matrix) {
        FilterRaster in = resolveInput(matrix.getIn());
        double[] m = colorMatrixValues(matrix);

        FilterRaster result = in.newLike();
        float[] source = in.getData();
        float[] out = result.getData();
        float[] rgba = new float[4];
        float[] mapped = new float[4];
        for (int i = 0; i < out.length; i += 4) {
            FilterRaster.unpremultiply(source, i, rgba);
            for (int row = 0; row < 4; row++) {
                int base = row * 5;
                mapped[row] = (float) (m[base] * rgba[0] + m[base + 1] * rgba[1] + m[base + 2] * rgba[2] + m[base + 3] * rgba[3]
                    + m[base + 4]);
            }
            FilterRaster.premultiply(out, i, mapped);
        }
        return result;
    }

    private double[] colorMatrixValues(FeColorMatrix matrix) {
        String type = StringUtils.trimToEmpty(matrix.getType());
        return switch (type) {
            case "saturate" -> saturateMatrix(number(matrix.getValues(), 1));
            case "hueRotate" -> hueRotateMatrix(number(matrix.getValues(), 0));
            case "luminanceToAlpha" -> new double[] {
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0.2125, 0.7154, 0.0721, 0, 0 };
            case "matrix", "" -> explicitMatrix(matrix.getValues());
            default -> throw new UnsupportedFilterException();
        };
    }

    private double[] explicitMatrix(String values) {
        double[] numbers = numberList(values);
        if (numbers.length != 20) {
            // the identity matrix, per spec, when values is absent; a wrong-length list is a malformed document
            if (numbers.length == 0) {
                return new double[] {
                    1, 0, 0, 0, 0,
                    0, 1, 0, 0, 0,
                    0, 0, 1, 0, 0,
                    0, 0, 0, 1, 0 };
            }
            throw new UnsupportedFilterException();
        }
        return numbers;
    }

    private static double[] saturateMatrix(double s) {
        return new double[] {
            0.213 + 0.787 * s, 0.715 - 0.715 * s, 0.072 - 0.072 * s, 0, 0,
            0.213 - 0.213 * s, 0.715 + 0.285 * s, 0.072 - 0.072 * s, 0, 0,
            0.213 - 0.213 * s, 0.715 - 0.715 * s, 0.072 + 0.928 * s, 0, 0,
            0, 0, 0, 1, 0 };
    }

    private static double[] hueRotateMatrix(double degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new double[] {
            0.213 + cos * 0.787 - sin * 0.213, 0.715 - cos * 0.715 - sin * 0.715, 0.072 - cos * 0.072 + sin * 0.928, 0, 0,
            0.213 - cos * 0.213 + sin * 0.143, 0.715 + cos * 0.285 + sin * 0.140, 0.072 - cos * 0.072 - sin * 0.283, 0, 0,
            0.213 - cos * 0.213 - sin * 0.787, 0.715 - cos * 0.715 + sin * 0.715, 0.072 + cos * 0.928 + sin * 0.072, 0, 0,
            0, 0, 0, 1, 0 };
    }

    /** Each channel independently remapped by its own {@code feFunc*}, on non-premultiplied values. */
    private FilterRaster componentTransfer(FeComponentTransfer transfer) {
        FilterRaster in = resolveInput(transfer.getIn());
        ISvgFilterFunction[] functions = {
            transfer.getFeFuncR(), transfer.getFeFuncG(), transfer.getFeFuncB(), transfer.getFeFuncA() };

        FilterRaster result = in.newLike();
        float[] source = in.getData();
        float[] out = result.getData();
        float[] rgba = new float[4];
        float[] mapped = new float[4];
        for (int i = 0; i < out.length; i += 4) {
            FilterRaster.unpremultiply(source, i, rgba);
            for (int c = 0; c < 4; c++) {
                mapped[c] = functions[c] == null ? rgba[c] : (float) transferFunction(functions[c], FilterRaster.clamp(rgba[c]));
            }
            FilterRaster.premultiply(out, i, mapped);
        }
        return result;
    }

    private double transferFunction(ISvgFilterFunction function, double value) {
        String type = StringUtils.trimToEmpty(function.getType());
        double[] table = numberList(function.getTableValues());
        switch (type) {
            case "table":
                if (table.length == 0) {
                    return value;
                }
                if (table.length == 1) {
                    return table[0];
                }
                int n = table.length - 1;
                int k = Math.min((int) (value * n), n - 1);
                return table[k] + (value - (double) k / n) * n * (table[k + 1] - table[k]);
            case "discrete":
                if (table.length == 0) {
                    return value;
                }
                int slot = Math.min((int) (value * table.length), table.length - 1);
                return table[slot];
            case "linear":
                return number(function.getSlope(), 1) * value + number(function.getIntercept(), 0);
            case "gamma":
                return number(function.getAmplitude(), 1) * Math.pow(value, number(function.getExponent(), 1))
                    + number(function.getOffset(), 0);
            default:
                return value;
        }
    }

    /**
     * The specification's own prescribed approximation: three successive box blurs with
     * {@code d = floor(s * 3 * sqrt(2*PI) / 4 + 0.5)}, separable into a horizontal and a vertical pass. An even
     * {@code d} cannot be centred on a pixel, so - again per spec - the first two passes are offset half a pixel in
     * opposite directions and the third widened by one.
     */
    private FilterRaster gaussianBlur(FeGaussianBlur blur) {
        FilterRaster in = resolveInput(blur.getIn());
        double[] deviations = numberList(blur.getStdDeviation());
        double deviationX = deviations.length > 0 ? deviations[0] : 0;
        double deviationY = deviations.length > 1 ? deviations[1] : deviationX;
        if (primitiveUnits() == UnitsMode.OBJECT_BOUNDING_BOX) {
            double diagonal = SvgFilterRenderer.bboxDiagonal(targetBounds);
            deviationX *= diagonal;
            deviationY *= diagonal;
        }
        if (deviationX < 0 || deviationY < 0) {
            throw new UnsupportedFilterException();
        }

        FilterRaster result = in;
        result = blurAxis(result, boxSize(deviationX), true);
        result = blurAxis(result, boxSize(deviationY), false);
        return result == in ? copyOf(in) : result;
    }

    private static int boxSize(double deviation) {
        return (int) Math.floor(deviation * 3 * Math.sqrt(2 * Math.PI) / 4 + 0.5);
    }

    private FilterRaster blurAxis(FilterRaster in, int d, boolean horizontal) {
        if (d <= 0) {
            return in;
        }
        FilterRaster result = in;
        if (d % 2 == 1) {
            int radius = (d - 1) / 2;
            for (int pass = 0; pass < 3; pass++) {
                result = boxBlur(result, radius, radius, horizontal);
            }
        } else {
            result = boxBlur(result, d / 2, d / 2 - 1, horizontal);
            result = boxBlur(result, d / 2 - 1, d / 2, horizontal);
            result = boxBlur(result, d / 2, d / 2, horizontal);
        }
        return result;
    }

    /**
     * One box-blur pass along a single axis, averaging over {@code [-left, +right]} with a sliding window, so cost is
     * independent of the blur radius. Samples outside the buffer count as fully transparent, per spec.
     */
    private FilterRaster boxBlur(FilterRaster in, int left, int right, boolean horizontal) {
        FilterRaster result = in.newLike();
        float[] source = in.getData();
        float[] out = result.getData();
        int lineCount = horizontal ? height : width;
        int lineLength = horizontal ? width : height;
        int window = left + right + 1;

        float[] sums = new float[4];
        for (int line = 0; line < lineCount; line++) {
            java.util.Arrays.fill(sums, 0);
            for (int i = -left; i <= right; i++) {
                accumulate(sums, source, in, line, i, lineLength, horizontal, 1);
            }
            for (int position = 0; position < lineLength; position++) {
                int target = horizontal ? result.index(position, line) : result.index(line, position);
                for (int c = 0; c < 4; c++) {
                    out[target + c] = sums[c] / window;
                }
                accumulate(sums, source, in, line, position - left, lineLength, horizontal, -1);
                accumulate(sums, source, in, line, position + right + 1, lineLength, horizontal, 1);
            }
        }
        return result;
    }

    private void accumulate(float[] sums, float[] source, FilterRaster in, int line, int position, int lineLength, boolean horizontal,
        int sign) {
        if (position < 0 || position >= lineLength) {
            return;
        }
        int index = horizontal ? in.index(position, line) : in.index(line, position);
        for (int c = 0; c < 4; c++) {
            sums[c] += sign * source[index + c];
        }
    }

    private FilterRaster copyOf(FilterRaster in) {
        FilterRaster result = in.newLike();
        System.arraycopy(in.getData(), 0, result.getData(), 0, in.getData().length);
        return result;
    }

    // --- value parsing -------------------------------------------------------

    private UnitsMode primitiveUnits() {
        return RenderContext.parseUnits(filter.getPrimitiveUnits(), UnitsMode.USER_SPACE_ON_USE);
    }

    private static double number(String value, double fallback) {
        String text = StringUtils.trimToEmpty(value);
        return NumberUtils.isParsable(text) ? NumberUtils.toDouble(text) : fallback;
    }

    private static double[] numberList(String value) {
        String text = StringUtils.trimToEmpty(value);
        if (text.isEmpty()) {
            return new double[0];
        }
        List<Double> numbers = new ArrayList<>();
        for (String token : text.split("[\\s,]+")) {
            if (NumberUtils.isParsable(token)) {
                numbers.add(NumberUtils.toDouble(token));
            }
        }
        double[] result = new double[numbers.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = numbers.get(i);
        }
        return result;
    }

}
