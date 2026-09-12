package nz.co.ctg.foxglove.filter;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.RenderContext.UnitsMode;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.shape.Rectangle;

/**
 * Applies {@code filter="url(#id)"} to an already-built {@code node}, in place - stage 1 of #26: filter plumbing
 * (the filter region, {@code filterUnits}/{@code primitiveUnits}) plus a single-primitive fast path covering a lone
 * {@code feGaussianBlur}. A filter with any other shape (empty, more than one primitive, an unsupported primitive,
 * or an unsupported {@code in}) degrades to no effect - {@code node} renders unfiltered, never throwing and never
 * silently wrong. Full filter-graph support (named results, chains, the pixel-level primitives with no JavaFX
 * equivalent) is out of scope here - see the issue's own suggested follow-up stages.
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

    public static void apply(RenderContext context, Node node, SvgFilter filter) {
        Bounds targetBounds = node.getBoundsInLocal();
        applySinglePrimitiveFastPath(node, filter, targetBounds);
        applyFilterRegionClip(context, node, filter, targetBounds);
    }

    /**
     * Sets a {@link GaussianBlur} when {@code filter} contains exactly one primitive, it is a {@link FeGaussianBlur},
     * and its {@code in} is blank or exactly {@code "SourceGraphic"} - any other shape leaves {@code node}'s effect
     * untouched (the documented degrade).
     */
    private static void applySinglePrimitiveFastPath(Node node, SvgFilter filter, Bounds targetBounds) {
        List<ISvgFilterPrimitive> primitives = filter.getContent().stream()
            .filter(ISvgFilterPrimitive.class::isInstance)
            .map(ISvgFilterPrimitive.class::cast)
            .toList();
        if (primitives.size() != 1 || !(primitives.get(0) instanceof FeGaussianBlur blur)) {
            return;
        }
        String in = blur.getIn();
        if (StringUtils.isNotBlank(in) && !"SourceGraphic".equals(in)) {
            return;
        }

        double stdDeviation = firstNumber(blur.getStdDeviation());
        if (RenderContext.parseUnits(filter.getPrimitiveUnits(), UnitsMode.USER_SPACE_ON_USE) == UnitsMode.OBJECT_BOUNDING_BOX) {
            stdDeviation *= bboxDiagonal(targetBounds);
        }
        double radius = Math.clamp(STD_DEVIATION_TO_RADIUS * stdDeviation, 0.0, 63.0);
        node.setEffect(new GaussianBlur(radius));
    }

    /**
     * The first number in a {@code <number-optional-number>} value such as {@code stdDeviation} - JavaFX's
     * {@link GaussianBlur} has one isotropic radius, so a second (anisotropic) number is not representable and is
     * ignored, a documented limitation.
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
    private static double bboxDiagonal(Bounds bounds) {
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
    private static void applyFilterRegionClip(RenderContext context, Node node, SvgFilter filter, Bounds targetBounds) {
        Bounds region = resolveFilterRegion(filter, context, targetBounds);
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
