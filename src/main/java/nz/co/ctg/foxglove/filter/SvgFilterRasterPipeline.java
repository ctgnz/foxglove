package nz.co.ctg.foxglove.filter;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.ISvgPresentationAttributes;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.RenderContext.Axis;
import nz.co.ctg.foxglove.RenderContext.UnitsMode;
import nz.co.ctg.foxglove.adapter.SizeAdapter;
import nz.co.ctg.foxglove.type.PreserveAspectRatio;
import nz.co.ctg.foxglove.type.ViewBox;

/**
 * Stage 3 of the filter work (#77): evaluates a {@code <filter>}'s primitive graph as real pixels, for everything {@link SvgFilterRenderer}'s JavaFX-effect chain cannot express -
 * the pixel-level primitives that have no {@code javafx.scene.effect} equivalent, and arbitrary (non-chain) graphs where a named {@code result} is branched, converged, or
 * referenced out of order.
 * <p>
 * The result is applied <b>in place</b>, via {@link ImageInput} as the node's own effect. Masking (#25) had to move to the consumer side because it replaces its node and
 * {@code AbstractSvgShape}'s narrower {@code S extends Shape} return type cannot carry an {@code ImageView}; this needs none of that - verified empirically before the design was
 * settled on: a node with an {@code ImageInput} effect renders that image rather than its own geometry, the image's {@code x}/{@code y} are in the node's own local space, the
 * image is <i>not</i> clipped to the node's own geometry (so a blur may spread beyond the shape), and an existing {@code setClip} still applies on top (so the filter-region clip
 * composes correctly).
 * <p>
 * Buffers are rasterised at one pixel per user unit and never supersampled - {@link ImageInput} has no scale of its own, unlike the {@code ImageView} masking wraps in a scaled
 * {@link Group}. That is spec-aligned in principle, since filters are defined on a pixel grid, though {@code filterRes} is not honoured.
 * <p>
 * Known gaps, each degrading the whole filter to unfiltered rather than rendering something wrong: {@code in="BackgroundImage"} and friends, and the lighting primitives
 * ({@link #diffuseLighting}/{@link #specularLighting}) parsing {@code kernelUnitLength} but not honouring it - like {@code filterRes}, doing so would mean resampling the input to
 * a different pixel grid and back, and no cited test exercises it. Every primitive's own {@code x}/{@code y}/{@code width}/{@code height} filter primitive subregion (see
 * {@link #resolveSubregion}, added for #174) now really does clip its result (#188) - defaulting, when entirely unset, to the whole filter region per SVG 1.1 15.7.5's own special
 * case, not the ordinary per-axis percentage resolution a partially-set subregion still uses.
 * <p>
 * Primitives evaluate in <b>linearRGB</b> by default, per the specification, honouring {@code color-interpolation-filters} per primitive - see {@link FilterColorSpace}. Working in
 * sRGB instead was #77's largest documented inaccuracy, fixed in #108: it left structure and geometry right but every interpolated value systematically too dark.
 */
final class SvgFilterRasterPipeline {

    /**
     * An upper bound on either dimension of the rasterised filter region, so a pathological region (a huge shape, or a {@code filterUnits="userSpaceOnUse"} region declared far
     * larger than anything visible) degrades rather than trying to allocate an unbounded buffer.
     */
    private static final int MAX_DIMENSION = 4096;

    private final SvgFilter filter;
    private final RenderContext context;
    private final Bounds targetBounds;
    private final Bounds region;
    private final int width;
    private final int height;

    private final Map<String, FilterRaster> namedResults = new HashMap<>();
    private final Map<String, Bounds> namedSubregions = new HashMap<>();
    private FilterRaster sourceGraphic;
    private FilterRaster sourceAlpha;
    private FilterRaster previous;
    private Bounds previousSubregion;
    private boolean first = true;

    /** The space the primitive currently being evaluated works in - see {@link #run} and {@link #resolveInput}. */
    private FilterColorSpace colorSpace = FilterColorSpace.LINEAR_RGB;

    private SvgFilterRasterPipeline(SvgFilter filter, RenderContext context, Bounds targetBounds, Bounds region, int width, int height) {
        this.filter = filter;
        this.context = context;
        this.targetBounds = targetBounds;
        this.region = region;
        this.width = width;
        this.height = height;
    }

    /**
     * Renders {@code filter}'s primitives against {@code node} and sets the result as its effect, returning whether it could. {@code false} means the caller should leave
     * {@code node} unfiltered - either the filter uses something still unsupported, or rasterising was not possible at all (see {@link #rasterizeSource}).
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
                // each primitive declares the space it works in, so this is per-primitive rather than set once
                // for the filter - resolveInput converts whatever it is handed into it
                colorSpace = FilterColorSpace.of(primitive, filter);
                result = evaluate(primitive);
                result.setColorSpace(colorSpace);
                // every primitive's x/y/width/height is a hard clip on its own result (SVG 1.1 15.7.5), defaulting
                // to the whole filter region (see resolveSubregion) - a no-op for the overwhelming majority of
                // primitives, which never set their own subregion, and already redundant-but-harmless for feImage,
                // which clips itself to the identical Bounds via its own dedicated path
                Bounds subregion = resolveSubregion(primitive);
                clipToSubregion(result, subregion);
                previous = result;
                previousSubregion = subregion;
                first = false;
                String name = StringUtils.trimToEmpty(primitive.getResult());
                if (!name.isEmpty()) {
                    namedResults.put(name, result);
                    namedSubregions.put(name, subregion);
                }
            }
            // whatever space the last primitive worked in, what gets displayed is sRGB
            return result == null ? null : result.toColorSpace(FilterColorSpace.SRGB);
        } catch (UnsupportedFilterException e) {
            return null;
        }
    }

    /**
     * Snapshots {@code node} over the filter region, the same technique {@code SvgMaskRenderer.rasterize} and {@code SvgPattern} already use. Two of the node's own properties are
     * taken off first, for different reasons, and both are put back afterwards - unlike masking, which discards its node, this one goes on to be rendered for real:
     * <ul>
     * <li>its <b>transforms</b> (and {@code translateX}/{@code translateY}), because an explicit snapshot viewport is read in post-transform space while the region is in the
     * pre-transform local space {@code getBoundsInLocal} gave it;
     * <li>its <b>opacity</b>, because {@code SourceGraphic} is the element before its own opacity - SVG applies that to the filter's result, not its input. Leaving it on applied
     * it twice (#129): once baked into this snapshot, and again when JavaFX paints the {@link ImageInput} built from it.
     * </ul>
     * A {@code clip} from {@code clip-path} is likewise still on the node and likewise re-applied afterwards, but clipping twice with the same clip is idempotent, so it needs no
     * equivalent treatment.
     * <p>
     * Returns {@code null} when snapshotting is not possible at all - most usually because the caller is not on the JavaFX Application Thread, which {@code Node.snapshot}
     * requires. That is the same constraint masking already carries, and callers degrade to an unfiltered node rather than propagating it.
     */
    private FilterRaster rasterizeSource(Node node) {
        List<Transform> ownTransforms = List.copyOf(node.getTransforms());
        double translateX = node.getTranslateX();
        double translateY = node.getTranslateY();
        double opacity = node.getOpacity();
        Group holder = new Group();
        try {
            node.getTransforms()
                .clear();
            node.setTranslateX(0);
            node.setTranslateY(0);
            // SourceGraphic is the element before its own opacity, which SVG applies to the filter's result rather
            // than its input. Leaving it on double-applies it (#129): once baked into this snapshot, and again when
            // JavaFX paints the ImageInput built from it, since node opacity still applies to the effect's output
            node.setOpacity(1.0);

            holder.getChildren()
                .add(node);
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
            holder.getChildren()
                .remove(node);
            node.getTransforms()
                .setAll(ownTransforms);
            node.setTranslateX(translateX);
            node.setTranslateY(translateY);
            node.setOpacity(opacity);
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
        if (primitive instanceof FeTurbulence turbulence) {
            return turbulence(turbulence);
        }
        if (primitive instanceof FeImage image) {
            return image(image);
        }
        if (primitive instanceof FeConvolveMatrix matrix) {
            return convolveMatrix(matrix);
        }
        if (primitive instanceof FeMorphology morphology) {
            return morphology(morphology);
        }
        if (primitive instanceof FeDisplacementMap displacementMap) {
            return displacementMap(displacementMap);
        }
        if (primitive instanceof FeDiffuseLighting diffuse) {
            return diffuseLighting(diffuse);
        }
        if (primitive instanceof FeSpecularLighting specular) {
            return specularLighting(specular);
        }
        if (primitive instanceof FeTile tile) {
            return tile(tile);
        }
        throw new UnsupportedFilterException();
    }

    /**
     * Resolves a primitive's {@code in}, <b>in the space that primitive works in</b>: blank means {@code SourceGraphic} for the very first primitive and the previous primitive's
     * result thereafter, per spec. Unlike the effect-chain path, {@code SourceAlpha} and any earlier named {@code result} both resolve here - retaining every result, rather than
     * only the previous one, is exactly what lets a branching or out-of-order graph work. {@code BackgroundImage} and friends still abort.
     * <p>
     * Converting here, rather than once for the whole filter, is what makes a per-primitive {@code color-interpolation-filters} work at all (#108): {@code SourceGraphic} arrives
     * in sRGB, a named result arrives in whatever space the primitive that produced it declared, and each consumer gets it in its own.
     */
    private FilterRaster resolveInput(String in) {
        return resolve(in).toColorSpace(colorSpace);
    }

    private FilterRaster resolve(String in) {
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

    /** As {@link #resolve}, but returns the referenced primitive's own resolved subregion rather than its pixel data - see {@link #tile}, the one consumer that needs it. */
    private Bounds resolveInputSubregion(String in) {
        String ref = StringUtils.trimToEmpty(in);
        if (ref.isEmpty()) {
            return first ? region : previousSubregion;
        }
        if ("SourceGraphic".equals(ref) || "SourceAlpha".equals(ref)) {
            return region;
        }
        Bounds subregion = namedSubregions.get(ref);
        if (subregion == null) {
            throw new UnsupportedFilterException();
        }
        return subregion;
    }

    /**
     * Zeroes {@code raster}'s premultiplied pixel data outside {@code subregion}, in place - the "hard clip... on the filter primitive result" every primitive's own
     * {@code x}/{@code y}/{@code width}/{@code height} is per SVG 1.1 15.7.5. {@code subregion} is in absolute user-space coordinates, converted to buffer-local pixel indices the
     * same way {@link #resolveSubregion}'s own callers already do.
     */
    private void clipToSubregion(FilterRaster raster, Bounds subregion) {
        int x0 = (int) Math.round(subregion.getMinX() - region.getMinX());
        int y0 = (int) Math.round(subregion.getMinY() - region.getMinY());
        int x1 = (int) Math.round(subregion.getMaxX() - region.getMinX());
        int y1 = (int) Math.round(subregion.getMaxY() - region.getMinY());
        float[] data = raster.getData();
        for (int y = 0; y < height; y++) {
            boolean insideY = y >= y0 && y < y1;
            for (int x = 0; x < width; x++) {
                if (insideY && x >= x0 && x < x1) {
                    continue;
                }
                int idx = raster.index(x, y);
                data[idx] = 0;
                data[idx + 1] = 0;
                data[idx + 2] = 0;
                data[idx + 3] = 0;
            }
        }
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
        // flood-color is authored in sRGB whatever space this primitive works in, so it converts on the way in -
        // unlike every other input, which arrives as a buffer resolveInput can convert wholesale
        float red = colorSpace.convert((float) color.getRed()) * alpha;
        float green = colorSpace.convert((float) color.getGreen()) * alpha;
        float blue = colorSpace.convert((float) color.getBlue()) * alpha;
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
     * Per SVG 1.1, {@code in} is image A (the top layer) and {@code in2} is image B; the result alpha is {@code qr = 1 - (1-qa)*(1-qb)} for every mode.
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
     * All four types, on non-premultiplied values as the specification defines them - including the full 5x4 {@code matrix} and {@code luminanceToAlpha} forms the effect-chain
     * path has no way to express (it can only approximate {@code saturate}/{@code hueRotate} through {@code ColorAdjust}).
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
                0.2125, 0.7154, 0.0721, 0, 0
                };
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
                    0, 0, 0, 1, 0
                };
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
            0, 0, 0, 1, 0
        };
    }

    private static double[] hueRotateMatrix(double degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new double[] {
            0.213 + cos * 0.787 - sin * 0.213, 0.715 - cos * 0.715 - sin * 0.715, 0.072 - cos * 0.072 + sin * 0.928, 0, 0,
            0.213 - cos * 0.213 + sin * 0.143, 0.715 + cos * 0.285 + sin * 0.140, 0.072 - cos * 0.072 - sin * 0.283, 0, 0,
            0.213 - cos * 0.213 - sin * 0.787, 0.715 - cos * 0.715 + sin * 0.715, 0.072 + cos * 0.928 + sin * 0.072, 0, 0,
            0, 0, 0, 1, 0
        };
    }

    /** Each channel independently remapped by its own {@code feFunc*}, on non-premultiplied values. */
    private FilterRaster componentTransfer(FeComponentTransfer transfer) {
        FilterRaster in = resolveInput(transfer.getIn());
        ISvgFilterFunction[] functions = {
            transfer.getFeFuncR(), transfer.getFeFuncG(), transfer.getFeFuncB(), transfer.getFeFuncA()
        };

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
     * The specification's own prescribed approximation: three successive box blurs with {@code d = floor(s * 3 * sqrt(2*PI) / 4 + 0.5)}, separable into a horizontal and a vertical
     * pass. An even {@code d} cannot be centred on a pixel, so - again per spec - the first two passes are offset half a pixel in opposite directions and the third widened by one.
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
     * One box-blur pass along a single axis, averaging over {@code [-left, +right]} with a sliding window, so cost is independent of the blur radius. Samples outside the buffer
     * count as fully transparent, per spec.
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

    /**
     * Generates {@code feTurbulence}'s output directly, pixel by pixel - unlike every other primitive, this one has no input image at all, only a procedural function of each
     * output pixel's own user-space coordinate (see {@link PerlinTurbulence}, which implements the spec's algorithm exactly). Colour and alpha are all four generated
     * independently, per SVG 1.1 15.24 - alpha is noise too, not derived from RGB - then mapped from {@code turbFunctionResult} to a {@code [0, 1]} channel value per the
     * specification's own formula for each {@code type}, and premultiplied like any other primitive's output.
     */
    private FilterRaster turbulence(FeTurbulence turbulence) {
        double[] freq = numberList(turbulence.getBaseFrequency());
        double baseFreqX = freq.length > 0 ? freq[0] : 0;
        double baseFreqY = freq.length > 1 ? freq[1] : baseFreqX;
        if (baseFreqX < 0 || baseFreqY < 0) {
            throw new UnsupportedFilterException();
        }
        int numOctaves = (int) number(turbulence.getNumOctaves(), 1);
        boolean fractalSum = "fractalNoise".equals(turbulence.getType());
        boolean stitching = "stitch".equals(turbulence.getStitchTiles());

        PerlinTurbulence.StitchInfo stitchInfo = null;
        if (stitching) {
            double tileWidth = region.getWidth();
            double tileHeight = region.getHeight();
            if (baseFreqX != 0) {
                double loFreq = Math.floor(tileWidth * baseFreqX) / tileWidth;
                double hiFreq = Math.ceil(tileWidth * baseFreqX) / tileWidth;
                baseFreqX = baseFreqX / loFreq < hiFreq / baseFreqX ? loFreq : hiFreq;
            }
            if (baseFreqY != 0) {
                double loFreq = Math.floor(tileHeight * baseFreqY) / tileHeight;
                double hiFreq = Math.ceil(tileHeight * baseFreqY) / tileHeight;
                baseFreqY = baseFreqY / loFreq < hiFreq / baseFreqY ? loFreq : hiFreq;
            }
            int stitchWidth = (int) (tileWidth * baseFreqX + 0.5);
            int stitchWrapX = (int) (region.getMinX() * baseFreqX + PerlinTurbulence.PERLIN_N + stitchWidth);
            int stitchHeight = (int) (tileHeight * baseFreqY + 0.5);
            int stitchWrapY = (int) (region.getMinY() * baseFreqY + PerlinTurbulence.PERLIN_N + stitchHeight);
            stitchInfo = new PerlinTurbulence.StitchInfo(stitchWidth, stitchWrapX, stitchHeight, stitchWrapY);
        }

        PerlinTurbulence generator = new PerlinTurbulence(number(turbulence.getSeed(), 0));
        FilterRaster result = new FilterRaster(width, height);
        float[] out = result.getData();
        float[] rgba = new float[4];
        for (int py = 0; py < height; py++) {
            double y = region.getMinY() + py;
            for (int px = 0; px < width; px++) {
                double x = region.getMinX() + px;
                for (int c = 0; c < 4; c++) {
                    double turb = generator.turbulence(c, x, y, baseFreqX, baseFreqY, numOctaves, fractalSum, stitchInfo);
                    rgba[c] = (float) (fractalSum ? (turb + 1) / 2 : turb);
                }
                FilterRaster.premultiply(out, result.index(px, py), rgba);
            }
        }
        return result;
    }

    // --- feImage (#174) --------------------------------------------------------

    /**
     * {@code feImage} has no input - it renders a referenced raster image or, per spec, a same-document element, fitted into its own primitive subregion (see
     * {@link #resolveSubregion}) rather than filling the whole filter region the way every other primitive does.
     * <p>
     * A same-document {@code #id} reference is rendered and fitted with a plain translate and clip only - not the spec's full "as if it were a stand-alone document, using
     * x/y/width/height in place of a viewBox" treatment, which would additionally scale the referenced content to fill the subregion. No W3C test exercises this case (every
     * failing {@code filters-image-*} test references an external raster file), so this is a documented, honest simplification rather than a silent one - revisit if a real
     * document needs the full treatment.
     */
    private FilterRaster image(FeImage image) {
        Bounds subregion = resolveSubregion(image);
        if (subregion.getWidth() <= 0 || subregion.getHeight() <= 0) {
            return new FilterRaster(width, height);
        }
        String href = StringUtils.trimToEmpty(image.getXlinkHref());
        if (href.isEmpty()) {
            return new FilterRaster(width, height);
        }

        Optional<ISvgElement> element = context.getElementIndex()
            .resolve(href);
        Node content = element.isPresent() ? renderReferencedElement(element.get(), subregion)
            : loadFittedImageView(href, subregion, image.getPreserveAspectRatio());
        if (content == null) {
            return new FilterRaster(width, height);
        }
        return rasterizeIntoRegion(content, subregion);
    }

    /** The same-document-element case - see {@link #image}'s note on why this is a plain translate/clip rather than the spec's full viewBox-style treatment. */
    private Node renderReferencedElement(ISvgElement element, Bounds subregion) {
        if (!(element instanceof FxGraphic<?> graphic)) {
            return null;
        }
        Node rendered = graphic.createGraphic(context);
        if (rendered == null) {
            return null;
        }
        Group clipped = new Group(rendered);
        clipped.setClip(new Rectangle(subregion.getWidth(), subregion.getHeight()));
        return clipped;
    }

    /**
     * The external-raster-file case: resolved relative to the document's own base URI - never an absolute reference given directly, the same trust boundary
     * {@link nz.co.ctg.foxglove.element.SvgImage} and {@link nz.co.ctg.foxglove.SvgElementIndex}'s external-document support already apply - then fitted into {@code subregion} via
     * the identical {@link ViewBox}/{@link PreserveAspectRatio} machinery {@code SvgImage.createGraphic} uses for {@code <image>}.
     */
    private Node loadFittedImageView(String href, Bounds subregion, String preserveAspectRatio) {
        Image sourceImage = loadImage(href);
        if (sourceImage == null || sourceImage.isError()) {
            return null;
        }
        ImageView view = new ImageView(sourceImage);
        Group fitted = new Group(view);
        ViewBox intrinsic = new ViewBox(new BoundingBox(0, 0, sourceImage.getWidth(), sourceImage.getHeight()));
        Transform fit = intrinsic.createTransform(subregion.getWidth(), subregion.getHeight(), PreserveAspectRatio.parse(preserveAspectRatio));
        if (fit != null) {
            fitted.getTransforms()
                .add(fit);
        }
        fitted.setClip(new Rectangle(subregion.getWidth(), subregion.getHeight()));
        return fitted;
    }

    private Image loadImage(String href) {
        try {
            if (href.startsWith("data:")) {
                return new Image(StringUtils.deleteWhitespace(href));
            }
            URI uri = new URI(href);
            if (uri.isAbsolute()) {
                return null;
            }
            return context.getBaseUri()
                .map(base -> base.resolve(uri))
                .map(resolved -> new Image(resolved.toString(), false))
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Snapshots {@code content} - already fitted/clipped to {@code subregion}'s own size, in {@code subregion}'s own local space - positioned at its true absolute location, the
     * same technique {@link #rasterizeSource} already uses: the snapshot viewport is the filter region's own absolute bounds, so content placed at its real absolute position lines
     * up with the pixel buffer automatically.
     */
    private FilterRaster rasterizeIntoRegion(Node content, Bounds subregion) {
        // Inserted at index 0 (outermost - the first transform in the list applies last), not appended: `content`
        // may already carry its own fit-scale transform (see loadFittedImageView), which must apply in the
        // subregion's own local space before this translate carries the result out to its absolute position -
        // appending here would nest this translate inside that scale instead, scaling the offset itself.
        content.getTransforms()
            .add(0, new Translate(subregion.getMinX(), subregion.getMinY()));
        Group holder = new Group(content);
        try {
            new Scene(holder);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            params.setViewport(new Rectangle2D(region.getMinX(), region.getMinY(), region.getWidth(), region.getHeight()));
            WritableImage snapshot = content.snapshot(params, null);
            return FilterRaster.fromImage(snapshot, width, height);
        } catch (Throwable e) {
            return new FilterRaster(width, height);
        }
    }

    /**
     * A primitive's own filter primitive subregion ({@code x}/{@code y}/{@code width}/{@code height} on the {@code fe*} element itself) - the same shape as
     * {@link SvgFilterRenderer#resolveFilterRegion}'s filter-region resolution one level down: relative to {@code primitiveUnits} rather than {@code filterUnits}, defaulting each
     * unset axis to {@code 0%}/{@code 0%}/{@code 100%}/{@code 100%} per spec (a primitive with no input, like {@code feImage}, has no input-image bounds to default against
     * either). {@link ISvgFilterPrimitive}'s own {@code x}/{@code y}/{@code width}/{@code height} are plain {@code String}s, unlike {@code SvgFilter}'s typed {@code Size} -
     * unneeded until now, since no other primitive reads its own subregion - so parsed here via the same {@link SizeAdapter} {@code SvgFilter}'s own binding already uses.
     */
    private Bounds resolveSubregion(ISvgFilterPrimitive primitive) {
        if (StringUtils.isBlank(primitive.getX()) && StringUtils.isBlank(primitive.getY()) && StringUtils.isBlank(primitive.getWidth())
            && StringUtils.isBlank(primitive.getHeight())) {
            // SVG 1.1 15.7.5: with no subregion attribute given at all, the default 0%/0%/100%/100% is "a special
            // case" relative to the *filter region* itself - not primitiveUnits' own ordinary percentage-resolution
            // space (the ambient viewport under userSpaceOnUse, or the target bbox under objectBoundingBox), which
            // is what a *partially* set subregion's still-unset axes keep resolving against below (matching the
            // spec's own primitiveUnits="objectBoundingBox" example: feFlood x="25%" resolves against the target
            // bbox, not the filter region).
            return region;
        }
        Size x = parseSubregionSize(primitive.getX(), 0);
        Size y = parseSubregionSize(primitive.getY(), 0);
        Size subregionWidth = parseSubregionSize(primitive.getWidth(), 100);
        Size subregionHeight = parseSubregionSize(primitive.getHeight(), 100);

        if (primitiveUnits() == UnitsMode.OBJECT_BOUNDING_BOX) {
            double rx = targetBounds.getMinX() + RenderContext.resolveFraction(x) * targetBounds.getWidth();
            double ry = targetBounds.getMinY() + RenderContext.resolveFraction(y) * targetBounds.getHeight();
            double rw = RenderContext.resolveFraction(subregionWidth) * targetBounds.getWidth();
            double rh = RenderContext.resolveFraction(subregionHeight) * targetBounds.getHeight();
            return new BoundingBox(rx, ry, rw, rh);
        }
        return new BoundingBox(context.resolveLength(x, Axis.HORIZONTAL), context.resolveLength(y, Axis.VERTICAL),
                               context.resolveLength(subregionWidth, Axis.HORIZONTAL), context.resolveLength(subregionHeight, Axis.VERTICAL));
    }

    private static Size parseSubregionSize(String raw, double defaultPercent) {
        return StringUtils.isBlank(raw) ? new Size(defaultPercent, SizeUnits.PERCENT) : SizeAdapter.parse(raw);
    }

    // --- feConvolveMatrix, feMorphology, feDisplacementMap (#172) --------------

    /**
     * SVG 1.1 15.13's own formula, transcribed directly: {@code COLOR[x,y] = (sum over the kernel of SOURCE[x-targetX+j, y-targetY+i] * kernelMatrix[orderX-j-1, orderY-i-1]) /
     * divisor + bias}. Cross-checked index-for-index against the specification's own worked example (a 5x5 image, a {@code 1 2 3 / 4 5 6 / 7 8 9} kernel, default
     * {@code targetX}/{@code targetY}) before trusting it. {@code preserveAlpha=false} (the default) applies the identical formula to all four premultiplied channels, alpha
     * included - no unpremultiply step needed, since {@link FilterRaster}'s own storage is already premultiplied; {@code preserveAlpha=true} convolves colour only, leaving each
     * pixel's own original alpha untouched (spec: {@code ALPHA[x,y] = SOURCE[x,y]}), unpremultiplying/re-premultiplying around just that.
     */
    private FilterRaster convolveMatrix(FeConvolveMatrix matrix) {
        FilterRaster in = resolveInput(matrix.getIn());
        double[] orderXY = numberList(matrix.getOrder());
        int orderX = orderXY.length > 0 ? (int) orderXY[0] : 3;
        int orderY = orderXY.length > 1 ? (int) orderXY[1] : orderX;
        double[] kernel = numberList(matrix.getKernelMatrix());
        if (orderX <= 0 || orderY <= 0 || kernel.length != orderX * orderY) {
            throw new UnsupportedFilterException();
        }
        double kernelSum = 0;
        for (double k : kernel) {
            kernelSum += k;
        }
        double divisor = StringUtils.isNotBlank(matrix.getDivisor()) ? number(matrix.getDivisor(), 1) : (kernelSum == 0 ? 1 : kernelSum);
        if (divisor == 0) {
            throw new UnsupportedFilterException();
        }
        double bias = number(matrix.getBias(), 0);
        int targetX = StringUtils.isNotBlank(matrix.getTargetX()) ? (int) number(matrix.getTargetX(), 0) : orderX / 2;
        int targetY = StringUtils.isNotBlank(matrix.getTargetY()) ? (int) number(matrix.getTargetY(), 0) : orderY / 2;
        if (targetX < 0 || targetX >= orderX || targetY < 0 || targetY >= orderY) {
            throw new UnsupportedFilterException();
        }
        String edgeMode = matrix.getEdgeMode();
        boolean preserveAlpha = "true".equals(matrix.getPreserveAlpha());

        float[] source = in.getData();
        float[] work = source;
        if (preserveAlpha) {
            work = new float[source.length];
            float[] rgba = new float[4];
            for (int i = 0; i < source.length; i += 4) {
                FilterRaster.unpremultiply(source, i, rgba);
                System.arraycopy(rgba, 0, work, i, 4);
            }
        }

        FilterRaster result = in.newLike();
        float[] out = result.getData();
        float[] sample = new float[4];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = 0;
                double g = 0;
                double b = 0;
                double a = 0;
                for (int i = 0; i < orderY; i++) {
                    for (int j = 0; j < orderX; j++) {
                        samplePixel(work, x - targetX + j, y - targetY + i, edgeMode, sample);
                        double k = kernel[(orderX - j - 1) + (orderY - i - 1) * orderX];
                        r += sample[0] * k;
                        g += sample[1] * k;
                        b += sample[2] * k;
                        a += sample[3] * k;
                    }
                }
                int idx = result.index(x, y);
                if (preserveAlpha) {
                    float[] rgba = {
                        (float) (r / divisor + bias), (float) (g / divisor + bias), (float) (b / divisor + bias), work[in.index(x, y) + 3]
                    };
                    FilterRaster.premultiply(out, idx, rgba);
                } else {
                    out[idx] = FilterRaster.clamp((float) (r / divisor + bias));
                    out[idx + 1] = FilterRaster.clamp((float) (g / divisor + bias));
                    out[idx + 2] = FilterRaster.clamp((float) (b / divisor + bias));
                    out[idx + 3] = FilterRaster.clamp((float) (a / divisor + bias));
                }
            }
        }
        return result;
    }

    /**
     * One pixel of {@code data} (a {@code width x height} buffer, {@code R,G,B,A} order), extending past the edge per {@code edgeMode} - {@code duplicate} clamps to the nearest
     * real pixel, {@code wrap} takes the opposite edge, {@code none} (and any other/unrecognised value - not spec'd, but the safest degrade) is transparent black. Written into
     * {@code out} rather than returned, so the hot loop in {@link #convolveMatrix} does not allocate a new array per kernel tap.
     */
    private void samplePixel(float[] data, int x, int y, String edgeMode, float[] out) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            if ("wrap".equals(edgeMode)) {
                x = Math.floorMod(x, width);
                y = Math.floorMod(y, height);
            } else if ("duplicate".equals(edgeMode)) {
                x = Math.max(0, Math.min(width - 1, x));
                y = Math.max(0, Math.min(height - 1, y));
            } else {
                out[0] = 0;
                out[1] = 0;
                out[2] = 0;
                out[3] = 0;
                return;
            }
        }
        int index = (y * width + x) * 4;
        System.arraycopy(data, index, out, 0, 4);
    }

    /**
     * SVG 1.1 15.20: per-channel {@code min} ({@code erode}, the default operator) or {@code max} ({@code dilate}) of premultiplied {@code R,G,B,A} over a
     * {@code (2*radiusX+1) x (2*radiusY+1)} window - operating on premultiplied values means colour can never exceed alpha in the result, exactly as the spec notes. A radius of
     * {@code 0} on either axis is explicitly transparent black per spec (not identity - "disables the effect... i.e., the result is a transparent black image"). Samples outside
     * the buffer count as {@code (0,0,0,0)} - the same "infinite transparent black extension" {@link #boxBlur} already relies on for {@code feGaussianBlur}.
     */
    private FilterRaster morphology(FeMorphology morphology) {
        FilterRaster in = resolveInput(morphology.getIn());
        double[] radii = numberList(morphology.getRadius());
        double radiusX = radii.length > 0 ? radii[0] : 0;
        double radiusY = radii.length > 1 ? radii[1] : radiusX;
        if (primitiveUnits() == UnitsMode.OBJECT_BOUNDING_BOX) {
            double diagonal = SvgFilterRenderer.bboxDiagonal(targetBounds);
            radiusX *= diagonal;
            radiusY *= diagonal;
        }
        if (radiusX < 0 || radiusY < 0) {
            throw new UnsupportedFilterException();
        }
        int rx = (int) Math.round(radiusX);
        int ry = (int) Math.round(radiusY);

        FilterRaster result = in.newLike();
        if (rx == 0 && ry == 0) {
            return result;
        }
        boolean dilate = "dilate".equals(morphology.getOperator());
        float[] source = in.getData();
        float[] out = result.getData();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float r = dilate ? 0f : 1f;
                float g = dilate ? 0f : 1f;
                float b = dilate ? 0f : 1f;
                float a = dilate ? 0f : 1f;
                for (int dy = -ry; dy <= ry; dy++) {
                    int sy = y + dy;
                    for (int dx = -rx; dx <= rx; dx++) {
                        int sx = x + dx;
                        float pr = 0;
                        float pg = 0;
                        float pb = 0;
                        float pa = 0;
                        if (sx >= 0 && sx < width && sy >= 0 && sy < height) {
                            int idx = in.index(sx, sy);
                            pr = source[idx];
                            pg = source[idx + 1];
                            pb = source[idx + 2];
                            pa = source[idx + 3];
                        }
                        if (dilate) {
                            r = Math.max(r, pr);
                            g = Math.max(g, pg);
                            b = Math.max(b, pb);
                            a = Math.max(a, pa);
                        } else {
                            r = Math.min(r, pr);
                            g = Math.min(g, pg);
                            b = Math.min(b, pb);
                            a = Math.min(a, pa);
                        }
                    }
                }
                int idx = result.index(x, y);
                out[idx] = r;
                out[idx + 1] = g;
                out[idx + 2] = b;
                out[idx + 3] = a;
            }
        }
        return result;
    }

    /**
     * SVG 1.1 15.15: {@code P'(x,y) = P(x + scale*(XC(x,y)-0.5), y + scale*(YC(x,y)-0.5))} - {@code in} stays premultiplied and is sampled as-is, but {@code in2}'s selected
     * channel is read <b>unpremultiplied</b> (spec: "calculations using in2 are performed using non-premultiplied color values"). Nearest-neighbour sampling - the spec only
     * recommends bilinear for "high quality viewers", it does not require it, and every other pixel lookup in this pipeline (e.g. {@code feOffset}) already works on the same
     * integer grid.
     */
    private FilterRaster displacementMap(FeDisplacementMap displacementMap) {
        FilterRaster in = resolveInput(displacementMap.getIn());
        FilterRaster in2 = resolveInput(displacementMap.getIn2());
        double scale = number(displacementMap.getScale(), 0);
        if (primitiveUnits() == UnitsMode.OBJECT_BOUNDING_BOX) {
            scale *= SvgFilterRenderer.bboxDiagonal(targetBounds);
        }
        int xChannel = channelIndex(displacementMap.getXChannelSelector());
        int yChannel = channelIndex(displacementMap.getYChannelSelector());

        float[] source = in.getData();
        float[] map = in2.getData();
        FilterRaster result = in.newLike();
        float[] out = result.getData();
        float[] rgba = new float[4];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                FilterRaster.unpremultiply(map, in2.index(x, y), rgba);
                int sx = (int) Math.round(x + scale * (rgba[xChannel] - 0.5));
                int sy = (int) Math.round(y + scale * (rgba[yChannel] - 0.5));
                int outIdx = result.index(x, y);
                if (sx < 0 || sx >= width || sy < 0 || sy >= height) {
                    out[outIdx] = 0;
                    out[outIdx + 1] = 0;
                    out[outIdx + 2] = 0;
                    out[outIdx + 3] = 0;
                } else {
                    System.arraycopy(source, in.index(sx, sy), out, outIdx, 4);
                }
            }
        }
        return result;
    }

    /**
     * {@code "R"}/{@code "G"}/{@code "B"} select that channel's index; {@code "A"} and anything else (including unset, which the getters already default to {@code "A"}) select
     * alpha.
     */
    private static int channelIndex(String selector) {
        return switch (StringUtils.trimToEmpty(selector)) {
            case "R" -> 0;
            case "G" -> 1;
            case "B" -> 2;
            default -> 3;
        };
    }

    // --- feTile (#188) -----------------------------------------------------------

    /**
     * SVG 1.1 15.23: tiles {@code in}'s own <b>declared</b> subregion (not the bounding box of its actual ink) across the whole buffer, with wraparound sampling. Does not need to
     * know or clip to its own subregion at all - the generic per-primitive output clip {@link #run} already applies (via {@link #resolveSubregion}, defaulting to the whole filter
     * region per {@code feTile}'s own spec-cited special case) restricts this result to it immediately afterward, so this method only has to answer "what to repeat", not "how
     * far".
     */
    private FilterRaster tile(FeTile tile) {
        FilterRaster in = resolveInput(tile.getIn());
        Bounds sourceSubregion = resolveInputSubregion(tile.getIn());
        int tx0 = (int) Math.round(sourceSubregion.getMinX() - region.getMinX());
        int ty0 = (int) Math.round(sourceSubregion.getMinY() - region.getMinY());
        int tw = (int) Math.round(sourceSubregion.getWidth());
        int th = (int) Math.round(sourceSubregion.getHeight());

        FilterRaster result = in.newLike();
        if (tw <= 0 || th <= 0) {
            return result;
        }
        float[] source = in.getData();
        float[] out = result.getData();
        for (int y = 0; y < height; y++) {
            int sy = ty0 + Math.floorMod(y - ty0, th);
            if (sy < 0 || sy >= height) {
                continue;
            }
            for (int x = 0; x < width; x++) {
                int sx = tx0 + Math.floorMod(x - tx0, tw);
                if (sx < 0 || sx >= width) {
                    continue;
                }
                System.arraycopy(source, in.index(sx, sy), out, result.index(x, y), 4);
            }
        }
        return result;
    }

    // --- feDiffuseLighting, feSpecularLighting (#173) ---------------------------

    /**
     * SVG 1.1 15.14: {@code Dr,g,b = diffuseConstant * (N.L) * Lr,g,b}, {@code Da = 1.0} always - the result is written via {@link FilterRaster#premultiply}, which for a constant
     * alpha of {@code 1} degrades to a plain clamp-and-copy, but reusing it keeps this in step with every other primitive's own non-premultiplied-formula handling (see
     * {@link #colorMatrix}, {@link #componentTransfer}).
     */
    private FilterRaster diffuseLighting(FeDiffuseLighting node) {
        FilterRaster in = resolveInput(node.getIn());
        double surfaceScale = number(node.getSurfaceScale(), 1);
        double diffuseConstant = number(node.getDiffuseConstant(), 1);
        ISvgFilterLightSource light = firstLightSource(node.getLightSources());
        float[] baseColor = lightingColorComponents(node);

        FilterRaster result = new FilterRaster(width, height);
        float[] out = result.getData();
        float[] rgba = new float[4];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] n = surfaceNormal(in, x, y, surfaceScale);
                double z = surfaceScale * alphaAt(in, x, y);
                double[] l = lightVector(light, x, y, z);
                float[] lightColor = lightColorAt(light, l, baseColor);
                double nDotL = n[0] * l[0] + n[1] * l[1] + n[2] * l[2];
                rgba[0] = (float) (diffuseConstant * nDotL * lightColor[0]);
                rgba[1] = (float) (diffuseConstant * nDotL * lightColor[1]);
                rgba[2] = (float) (diffuseConstant * nDotL * lightColor[2]);
                rgba[3] = 1f;
                FilterRaster.premultiply(out, result.index(x, y), rgba);
            }
        }
        return result;
    }

    /**
     * SVG 1.1 15.22: {@code Sr,g,b = specularConstant * pow(N.H, specularExponent) * Lr,g,b}, {@code Sa = max(Sr,Sg,Sb)}, {@code H = normalize(L + E)} with the constant eye vector
     * {@code E = (0,0,1)}. {@code N.H} is clamped to {@code >= 0} before {@code pow} - not spec text verbatim, but necessary: a negative base with a non-integer
     * {@code specularExponent} is {@code NaN} in {@link Math#pow}, and physically a negative {@code N.H} means no specular reflection reaches the eye at all, i.e. zero.
     * <p>
     * Written <b>directly</b> into the buffer (clamped per channel), not via {@link FilterRaster#premultiply}: {@code (Sr,Sg,Sb,Sa)} is already a valid premultiplied tuple by
     * construction, since colour can never exceed {@code Sa}, its own max - running it through {@code premultiply} would multiply it by its own alpha a second time.
     */
    private FilterRaster specularLighting(FeSpecularLighting node) {
        FilterRaster in = resolveInput(node.getIn());
        double surfaceScale = number(node.getSurfaceScale(), 1);
        double specularConstant = number(node.getSpecularConstant(), 1);
        double specularExponent = number(node.getSpecularExponent(), 1);
        ISvgFilterLightSource light = firstLightSource(node.getLightSources());
        float[] baseColor = lightingColorComponents(node);

        FilterRaster result = new FilterRaster(width, height);
        float[] out = result.getData();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] n = surfaceNormal(in, x, y, surfaceScale);
                double z = surfaceScale * alphaAt(in, x, y);
                double[] l = lightVector(light, x, y, z);
                float[] lightColor = lightColorAt(light, l, baseColor);
                double[] h = normalize(l[0], l[1], l[2] + 1.0);
                double nDotH = Math.max(0, n[0] * h[0] + n[1] * h[1] + n[2] * h[2]);
                double factor = specularConstant * Math.pow(nDotH, specularExponent);
                double sr = factor * lightColor[0];
                double sg = factor * lightColor[1];
                double sb = factor * lightColor[2];
                double sa = Math.max(sr, Math.max(sg, sb));
                int idx = result.index(x, y);
                out[idx] = FilterRaster.clamp((float) sr);
                out[idx + 1] = FilterRaster.clamp((float) sg);
                out[idx + 2] = FilterRaster.clamp((float) sb);
                out[idx + 3] = FilterRaster.clamp((float) sa);
            }
        }
        return result;
    }

    private static ISvgFilterLightSource firstLightSource(List<ISvgFilterLightSource> lights) {
        if (lights.isEmpty()) {
            // spec: "exactly one light source element" - none present is a malformed document
            throw new UnsupportedFilterException();
        }
        return lights.get(0);
    }

    /**
     * The surface normal at pixel {@code (x,y)}, from {@code in}'s own alpha channel as the bump map - SVG 1.1 15.14's 3x3 Sobel gradient, using one of 9 distinct boundary-case
     * kernels (see {@link #SOBEL}) rather than clamping the sample positions to the buffer edge, which would be a materially different (and wrong) result.
     */
    private double[] surfaceNormal(FilterRaster in, int x, int y, double surfaceScale) {
        SobelKernel kernel = sobelKernel(x, y);
        double sumX = 0;
        double sumY = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                double alpha = alphaAt(in, x + col - 1, y + row - 1);
                sumX += kernel.kx()[row][col] * alpha;
                sumY += kernel.ky()[row][col] * alpha;
            }
        }
        double nx = -surfaceScale * kernel.factorX() * sumX;
        double ny = -surfaceScale * kernel.factorY() * sumY;
        return normalize(nx, ny, 1.0);
    }

    /**
     * {@code in}'s own alpha channel at {@code (x,y)}, or {@code 0} outside the buffer - safe here because every {@link #SOBEL} boundary kernel only ever weights in-bounds taps.
     */
    private double alphaAt(FilterRaster in, int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return 0;
        }
        return in.getData()[in.index(x, y) + 3];
    }

    private record SobelKernel(int[][] kx, int[][] ky, double factorX, double factorY) {
    }

    /**
     * The 9 boundary-case Sobel kernels SVG 1.1 15.14 defines, transcribed exactly (not derived) - indexed {@code [xEdge][yEdge]} where each axis is {@code 0} (at the low edge),
     * {@code 1} (interior) or {@code 2} (at the high edge). Each {@code kx}/{@code ky} is read {@code [row][col]}, row 0 = {@code y-dy}, col 0 = {@code x-dx}, matching the
     * specification's own {@code Kx(col,row)} convention. Interior's own {@code factorX}/{@code factorY} are both {@code 1/4}; a row/column kernel is {@code 1/3} along its own
     * free axis and {@code 1/2} across it; a corner is {@code 2/3} on both - not simplifiable to "zero out the missing tap", since the two-tap kernels these fall back to reweight
     * the taps they do have rather than leaving a hole (verified by hand against the spec's own tables before trusting it).
     */
    private static final SobelKernel[][] SOBEL = {
        {
            // top/left corner (x low, y low)
            new SobelKernel(new int[][] {
                {
                    0, 0, 0
                }, {
                    0, -2, 2
                }, {
                    0, -1, 1
                }
            }, new int[][] {
                {
                    0, 0, 0
                }, {
                    0, -2, -1
                }, {
                    0, 2, 1
                }
            }, 2.0 / 3, 2.0 / 3),
            // left column (x low, y interior)
            new SobelKernel(new int[][] {
                {
                    0, -1, 1
                }, {
                    0, -2, 2
                }, {
                    0, -1, 1
                }
            }, new int[][] {
                {
                    0, -2, -1
                }, {
                    0, 0, 0
                }, {
                    0, 2, 1
                }
            }, 1.0 / 2, 1.0 / 3),
            // bottom/left corner (x low, y high)
            new SobelKernel(new int[][] {
                {
                    0, -1, 1
                }, {
                    0, -2, 2
                }, {
                    0, 0, 0
                }
            }, new int[][] {
                {
                    0, -2, -1
                }, {
                    0, 2, 1
                }, {
                    0, 0, 0
                }
            }, 2.0 / 3, 2.0 / 3)
        }, {
            // top row (x interior, y low)
            new SobelKernel(new int[][] {
                {
                    0, 0, 0
                }, {
                    -2, 0, 2
                }, {
                    -1, 0, 1
                }
            }, new int[][] {
                {
                    0, 0, 0
                }, {
                    -1, -2, -1
                }, {
                    1, 2, 1
                }
            }, 1.0 / 3, 1.0 / 2),
            // interior (x interior, y interior)
            new SobelKernel(new int[][] {
                {
                    -1, 0, 1
                }, {
                    -2, 0, 2
                }, {
                    -1, 0, 1
                }
            }, new int[][] {
                {
                    -1, -2, -1
                }, {
                    0, 0, 0
                }, {
                    1, 2, 1
                }
            }, 1.0 / 4, 1.0 / 4),
            // bottom row (x interior, y high)
            new SobelKernel(new int[][] {
                {
                    -1, 0, 1
                }, {
                    -2, 0, 2
                }, {
                    0, 0, 0
                }
            }, new int[][] {
                {
                    -1, -2, -1
                }, {
                    1, 2, 1
                }, {
                    0, 0, 0
                }
            }, 1.0 / 3, 1.0 / 2)
        }, {
            // top/right corner (x high, y low)
            new SobelKernel(new int[][] {
                {
                    0, 0, 0
                }, {
                    -2, 2, 0
                }, {
                    -1, 1, 0
                }
            }, new int[][] {
                {
                    0, 0, 0
                }, {
                    -1, -2, 0
                }, {
                    1, 2, 0
                }
            }, 2.0 / 3, 2.0 / 3),
            // right column (x high, y interior)
            new SobelKernel(new int[][] {
                {
                    -1, 1, 0
                }, {
                    -2, 2, 0
                }, {
                    -1, 1, 0
                }
            }, new int[][] {
                {
                    -1, -2, 0
                }, {
                    0, 0, 0
                }, {
                    1, 2, 0
                }
            }, 1.0 / 2, 1.0 / 3),
            // bottom/right corner (x high, y high)
            new SobelKernel(new int[][] {
                {
                    -1, 1, 0
                }, {
                    -2, 2, 0
                }, {
                    0, 0, 0
                }
            }, new int[][] {
                {
                    -1, -2, 0
                }, {
                    1, 2, 0
                }, {
                    0, 0, 0
                }
            }, 2.0 / 3, 2.0 / 3)
        }
    };

    private SobelKernel sobelKernel(int x, int y) {
        int xEdge = x == 0 ? 0 : (x == width - 1 ? 2 : 1);
        int yEdge = y == 0 ? 0 : (y == height - 1 ? 2 : 1);
        return SOBEL[xEdge][yEdge];
    }

    /**
     * {@code L}, the unit vector from the surface to the light, at pixel {@code (x,y)} whose own {@code Z(x,y)} is {@code z} - constant for {@link FeDistantLight}, a function of
     * position for {@link FePointLight}/{@link FeSpotLight} per SVG 1.1 15.14.
     */
    private double[] lightVector(ISvgFilterLightSource light, int x, int y, double z) {
        if (light instanceof FeDistantLight distant) {
            double azimuth = Math.toRadians(number(distant.getAzimuth(), 0));
            double elevation = Math.toRadians(number(distant.getElevation(), 0));
            return new double[] {
                Math.cos(azimuth) * Math.cos(elevation), Math.sin(azimuth) * Math.cos(elevation), Math.sin(elevation)
            };
        }
        double[] position = light instanceof FePointLight point ? lightPosition(point.getX(), point.getY(), point.getZ())
            : lightPosition(((FeSpotLight) light).getX(), ((FeSpotLight) light).getY(), ((FeSpotLight) light).getZ());
        double lx = position[0] - (region.getMinX() + x);
        double ly = position[1] - (region.getMinY() + y);
        double lz = position[2] - z;
        return normalize(lx, ly, lz);
    }

    /**
     * The light colour {@code Lr,Lg,Lb} at a pixel whose light vector is {@code l} - {@code baseColor} unchanged for {@link FeDistantLight}/{@link FePointLight}, or
     * {@link FeSpotLight}'s own position-dependent falloff (SVG 1.1 15.14): zero outside the light's cone (including behind it, {@code L.S > 0}) or outside an explicitly specified
     * {@code limitingConeAngle} - unset means no cone restriction at all, which is why this checks {@code isNotBlank} rather than defaulting the angle itself.
     */
    private float[] lightColorAt(ISvgFilterLightSource light, double[] l, float[] baseColor) {
        if (!(light instanceof FeSpotLight spot)) {
            return baseColor;
        }
        double[] lightPos = lightPosition(spot.getX(), spot.getY(), spot.getZ());
        double[] pointsAt = lightPosition(spot.getPointsAtX(), spot.getPointsAtY(), spot.getPointsAtZ());
        double[] s = normalize(pointsAt[0] - lightPos[0], pointsAt[1] - lightPos[1], pointsAt[2] - lightPos[2]);
        double dot = l[0] * s[0] + l[1] * s[1] + l[2] * s[2];
        double negDot = -dot;
        if (dot > 0) {
            return new float[4];
        }
        String coneAttr = spot.getLimitingConeAngle();
        if (StringUtils.isNotBlank(coneAttr) && negDot < Math.cos(Math.toRadians(number(coneAttr, 0)))) {
            return new float[4];
        }
        double exponent = number(spot.getSpecularExponent(), 1);
        double factor = Math.pow(negDot, exponent);
        return new float[] {
            (float) (baseColor[0] * factor), (float) (baseColor[1] * factor), (float) (baseColor[2] * factor)
        };
    }

    /**
     * A light-source position or aim point ({@code x}/{@code y}/{@code z} on {@link FePointLight}/{@link FeSpotLight}, or the latter's {@code pointsAtX/Y/Z}), in the coordinate
     * system {@code primitiveUnits} establishes per spec - verified by hand against {@code filters-light-03-f}'s own worked numbers (an 80x80 {@code objectBoundingBox} case)
     * before trusting it: {@code x}/{@code y} scale and translate like {@link #resolveSubregion}'s own fractions, {@code z} only scales, by {@link SvgFilterRenderer#bboxDiagonal}
     * - "one unit along Z equals one unit in X and Y" per spec, exactly what that helper already computes for
     * {@code feGaussianBlur}/{@code feMorphology}/{@code feDisplacementMap}.
     */
    private double[] lightPosition(String xAttr, String yAttr, String zAttr) {
        double x = number(xAttr, 0);
        double y = number(yAttr, 0);
        double z = number(zAttr, 0);
        if (primitiveUnits() == UnitsMode.OBJECT_BOUNDING_BOX) {
            double diagonal = SvgFilterRenderer.bboxDiagonal(targetBounds);
            x = targetBounds.getMinX() + x * targetBounds.getWidth();
            y = targetBounds.getMinY() + y * targetBounds.getHeight();
            z *= diagonal;
        }
        return new double[] {
            x, y, z
        };
    }

    private static double[] normalize(double x, double y, double z) {
        double norm = Math.sqrt(x * x + y * y + z * z);
        if (norm == 0) {
            return new double[3];
        }
        return new double[] {
            x / norm, y / norm, z / norm
        };
    }

    /**
     * {@code lighting-color}'s components in this primitive's own working colour space - like {@code flood-color} (see {@link #flood}), authored in sRGB and converted on the way
     * in, but with a different initial value ({@code white}, not {@code flood-color}'s {@code black}) and {@code currentColor} support (see {@link #currentColor}), which
     * {@code flood-color} does not need to handle for any cited test.
     */
    private float[] lightingColorComponents(ISvgFilterPrimitive primitive) {
        Color color = lightingColor(primitive);
        return new float[] {
            colorSpace.convert((float) color.getRed()), colorSpace.convert((float) color.getGreen()), colorSpace.convert((float) color.getBlue())
        };
    }

    private Color lightingColor(ISvgFilterPrimitive primitive) {
        String raw = StringUtils.trimToEmpty(((ISvgPresentationAttributes) primitive).getLightingColor());
        if ("currentColor".equals(raw)) {
            return currentColor((ISvgElement) primitive);
        }
        if (raw.isEmpty()) {
            return Color.WHITE;
        }
        try {
            return Color.web(raw);
        } catch (RuntimeException e) {
            return Color.WHITE;
        }
    }

    /**
     * {@code color}'s inherited value at {@code element}, walked up the document tree via {@link nz.co.ctg.foxglove.SvgElementIndex#getParent} - unlike
     * {@link nz.co.ctg.foxglove.SvgPaintResolver#resolve}, which takes an already-resolved style, {@code element} here is a filter primitive with no computed style of its own to
     * hand in, so this walks its ancestors directly. Works because {@link nz.co.ctg.foxglove.SvgElementIndex} indexes the whole document tree generically, {@code <filter>} and its
     * {@code fe*} children included, not only the render tree.
     */
    private Color currentColor(ISvgElement element) {
        for (ISvgElement current = element; current != null; current = context.getElementIndex()
            .getParent(current)
            .orElse(null)) {
            if (current instanceof ISvgGraphicsAttributes attrs) {
                String color = StringUtils.trimToEmpty(attrs.getColor());
                if (!color.isEmpty()) {
                    try {
                        return Color.web(color);
                    } catch (RuntimeException e) {
                        return Color.BLACK;
                    }
                }
            }
        }
        return Color.BLACK;
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
