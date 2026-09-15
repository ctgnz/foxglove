package nz.co.ctg.foxglove.clip;

import java.util.List;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import org.apache.commons.lang3.ObjectUtils;

import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgContainer;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.RenderContext.Axis;
import nz.co.ctg.foxglove.RenderContext.UnitsMode;

/**
 * Substitutes {@code target} with a masked {@link ImageView}, or returns it unchanged when {@code mask} disables rendering entirely.
 * <p>
 * JavaFX has no per-pixel mask analogue to {@code Node.setClip()} - masking here rasterises both {@code target} and {@code mask}'s content to the same pixel rectangle (the
 * resolved mask region) and multiplies {@code target}'s own alpha by the mask's linearRGB luminance, the default SVG masking computes, per pixel. This necessarily flattens the
 * masked subtree to a static raster - nothing inside a mask, or the masked content itself, stays interactive or animatable after this - and always uses linearRGB (this does not
 * read {@code <mask>}'s {@code color-interpolation}, which could select sRGB instead).
 */
public final class SvgMaskRenderer {

    /**
     * Supersampling factor applied when rasterising, so a mask still looks crisp when scaled up - at a proportional memory cost. Adjustable at runtime, the same precedent as
     * {@link nz.co.ctg.foxglove.paint.SvgPattern#rasterScale}.
     */
    public static double rasterScale = 2.0;

    private static final double LUMINANCE_R = 0.2126;
    private static final double LUMINANCE_G = 0.7152;
    private static final double LUMINANCE_B = 0.0722;

    public static Node apply(Node target, SvgMask mask, RenderContext context) {
        Bounds targetBounds = target.getBoundsInLocal();
        Bounds maskRegion = resolveMaskRegion(mask, context, targetBounds);
        if (maskRegion.getWidth() <= 0 || maskRegion.getHeight() <= 0) {
            // per spec, a zero (or negative) mask region disables rendering of the referencing element entirely
            return new Group();
        }

        Group maskContent = buildMaskContent(mask, context, targetBounds);

        // Saved and zeroed, not just target.getTransforms(): SvgGraphic positions a nested <svg> via the
        // translateX/Y node properties instead (there is no competing `transform` attribute on <svg> to be inner or
        // outer relative to, so its "always outermost" behaviour is exactly what is wanted there) - either mechanism
        // would otherwise offset the rasterisation away from the pre-transform local frame `maskRegion` is in.
        List<Transform> ownTransforms = List.copyOf(target.getTransforms());
        double ownTranslateX = target.getTranslateX();
        double ownTranslateY = target.getTranslateY();
        target.getTransforms()
            .clear();
        target.setTranslateX(0);
        target.setTranslateY(0);
        // target is discarded in favour of the composited replacement below, so it is never restored - its saved
        // transforms are applied to the replacement instead, in resolveMaskRegion's own coordinate frame
        WritableImage targetImage = rasterize(target, maskRegion);
        WritableImage maskImage = rasterize(maskContent, maskRegion);

        WritableImage composite = compositeByLuminance(targetImage, maskImage);

        Group replacement = new Group(new ImageView(composite));
        replacement.getTransforms()
            .addAll(ownTransforms);
        replacement.getTransforms()
            .add(new Translate(maskRegion.getMinX(), maskRegion.getMinY()));
        // the composite image is rasterised at rasterScale pixels per unit (for supersampling quality) - an
        // ImageView otherwise renders it at its natural pixel size, so this scales it back down to the mask
        // region's own physical size before the translate above positions it. Innermost (applied first): unscale
        // the raw image pixels back to true local units before the translate moves that unit-sized result into place.
        replacement.getTransforms()
            .add(new Scale(1 / rasterScale, 1 / rasterScale));
        replacement.setTranslateX(ownTranslateX);
        replacement.setTranslateY(ownTranslateY);
        return replacement;
    }

    /**
     * The mask region ({@code x}/{@code y}/{@code width}/{@code height}, per {@code maskUnits} - default {@code objectBoundingBox}, unlike {@code clipPathUnits}), in the same
     * pre-transform local frame {@link nz.co.ctg.foxglove.ISvgGraphicsAttributes#applyClip} already resolves {@code objectBoundingBox} against. The SVG-specified defaults
     * ({@code x}/{@code y} = -10%, {@code width}/{@code height} = 120%) apply whenever {@code mask} does not declare its own.
     */
    private static Bounds resolveMaskRegion(SvgMask mask, RenderContext context, Bounds targetBounds) {
        Size x = ObjectUtils.defaultIfNull(mask.getX(), new Size(-10, SizeUnits.PERCENT));
        Size y = ObjectUtils.defaultIfNull(mask.getY(), new Size(-10, SizeUnits.PERCENT));
        Size width = ObjectUtils.defaultIfNull(mask.getWidth(), new Size(120, SizeUnits.PERCENT));
        Size height = ObjectUtils.defaultIfNull(mask.getHeight(), new Size(120, SizeUnits.PERCENT));

        UnitsMode unitsMode = RenderContext.parseUnits(mask.getMaskUnits(), UnitsMode.OBJECT_BOUNDING_BOX);
        if (unitsMode == UnitsMode.OBJECT_BOUNDING_BOX) {
            double regionX = targetBounds.getMinX() + RenderContext.resolveFraction(x) * targetBounds.getWidth();
            double regionY = targetBounds.getMinY() + RenderContext.resolveFraction(y) * targetBounds.getHeight();
            double regionWidth = RenderContext.resolveFraction(width) * targetBounds.getWidth();
            double regionHeight = RenderContext.resolveFraction(height) * targetBounds.getHeight();
            return new BoundingBox(regionX, regionY, regionWidth, regionHeight);
        }
        return new BoundingBox(context.resolveLength(x, Axis.HORIZONTAL), context.resolveLength(y, Axis.VERTICAL),
                               context.resolveLength(width, Axis.HORIZONTAL), context.resolveLength(height, Axis.VERTICAL));
    }

    /**
     * Renders {@code mask}'s content in a fresh, non-inheriting context - the same reasoning already established for {@code <clipPath>} content in {@link SvgClipPathRenderer} -
     * scaling by {@code targetBounds} when {@code maskContentUnits="objectBoundingBox"} (default {@code userSpaceOnUse}, unlike {@code clipPathUnits}).
     */
    private static Group buildMaskContent(SvgMask mask, RenderContext context, Bounds targetBounds) {
        RenderContext contentContext = RenderContext.root(context.getElementIndex(), context.getViewportWidth(), context.getViewportHeight())
            .withLocale(context.getLocale())
            .withBaseUri(context.getBaseUri()
                .orElse(null))
            .resolveChild(mask);

        Group content = new Group();
        for (ISvgElement child : mask.getContent()) {
            if (child instanceof FxGraphic<?> graphic && ISvgContainer.isRendered(child, contentContext.getLocale())) {
                Node built = graphic.createGraphic(contentContext);
                if (built != null) {
                    content.getChildren()
                        .add(built);
                }
            }
        }

        if (RenderContext.parseUnits(mask.getMaskContentUnits(), UnitsMode.USER_SPACE_ON_USE) == UnitsMode.OBJECT_BOUNDING_BOX
            && targetBounds.getWidth() > 0 && targetBounds.getHeight() > 0) {
            content.getTransforms()
                .add(new Translate(targetBounds.getMinX(), targetBounds.getMinY()));
            content.getTransforms()
                .add(new Scale(targetBounds.getWidth(), targetBounds.getHeight()));
        }
        return content;
    }

    /**
     * Snapshots {@code node} at {@code region}, supersampled by {@link #rasterScale}. Wraps in a throwaway {@link Group}/{@link Scene} first, matching
     * {@code SvgPattern.rasterize}'s established precedent - an explicit {@code viewport} is read in the node's own post-transform space, so whatever {@code node}'s own transforms
     * already are, they must map its content into the same frame {@code region} is expressed in (true of {@code
     * target} once its own transforms have been cleared by the caller, and true of mask content by construction - see {@link #buildMaskContent}). The supersampling scale is
     * inserted as the outermost transform (index 0), so it scales that already-correctly-framed result rather than the raw pre-{@code objectBoundingBox}-scale content underneath
     * it.
     */
    private static WritableImage rasterize(Node node, Bounds region) {
        new Scene(new Group(node));
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setViewport(new Rectangle2D(region.getMinX() * rasterScale, region.getMinY() * rasterScale, region.getWidth() * rasterScale,
                                           region.getHeight() * rasterScale));
        Scale supersample = new Scale(rasterScale, rasterScale);
        node.getTransforms()
            .add(0, supersample);
        try {
            return node.snapshot(params, null);
        } finally {
            node.getTransforms()
                .remove(supersample);
        }
    }

    private static WritableImage compositeByLuminance(WritableImage targetImage, WritableImage maskImage) {
        int width = (int) targetImage.getWidth();
        int height = (int) targetImage.getHeight();
        WritableImage composite = new WritableImage(width, height);
        PixelReader targetReader = targetImage.getPixelReader();
        PixelReader maskReader = maskImage.getPixelReader();
        PixelWriter writer = composite.getPixelWriter();

        for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
                Color targetColor = targetReader.getColor(px, py);
                Color maskColor = maskReader.getColor(px, py);
                double luminance = linear(maskColor.getRed()) * LUMINANCE_R + linear(maskColor.getGreen()) * LUMINANCE_G
                                   + linear(maskColor.getBlue()) * LUMINANCE_B;
                double maskValue = luminance * maskColor.getOpacity();
                writer.setColor(px, py,
                    new Color(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue(), targetColor.getOpacity() * maskValue));
            }
        }
        return composite;
    }

    /**
     * The inverse sRGB electro-optical transfer function, converting one sRGB channel (0-1) to linearRGB - what the SVG masking specification's default luminance-to-alpha computes
     * against, not the sRGB values directly.
     */
    private static double linear(double srgb) {
        return srgb <= 0.04045 ? srgb / 12.92 : Math.pow((srgb + 0.055) / 1.055, 2.4);
    }

    private SvgMaskRenderer() {
    }

}
