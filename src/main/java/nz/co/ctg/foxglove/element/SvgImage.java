package nz.co.ctg.foxglove.element;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javafx.geometry.BoundingBox;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgBounded;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.SvgInheritedStyle;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.type.PreserveAspectRatio;
import nz.co.ctg.foxglove.type.ViewBox;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "image")
public class SvgImage extends AbstractSvgStylable implements ISvgStructuralElement, ISvgBounded, ISvgEventListener, ISvgConditionalFeatures, ISvgLinkable, ISvgExternalResources, ISvgTransformable, FxGraphic<Node> {

    @XmlAttribute(name = "preserveAspectRatio")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String preserveAspectRatio;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateMotion", type = SvgAnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    public String getPreserveAspectRatio() {
        if (preserveAspectRatio == null) {
            return "xMidYMid meet";
        } else {
            return preserveAspectRatio;
        }
    }

    public void setPreserveAspectRatio(String value) {
        this.preserveAspectRatio = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    /**
     * Renders the referenced raster image, fitted into this element's {@code x}/{@code y}/{@code width}/ {@code height} viewport per {@code preserveAspectRatio}, or an empty
     * {@link Group} - never {@code null} - when the reference is missing, malformed, unresolvable, or the viewport has no positive area.
     * <p>
     * A {@code data:} URI, a reference relative to a known document base URI (see {@link nz.co.ctg.foxglove.FoxgloveParser#parseFile}) to a raster format, and - #178 - one to
     * another SVG document (rasterised at that document's own intrinsic size, then fitted into this element's viewport the same as any other image) are all supported. Any other
     * absolute reference - a network URL or a bare {@code file:} URI given directly in the document - is out of scope for now and also fails cleanly, keeping the trust boundary to
     * only the document's own embedded data and whatever the caller chose to parse from disk.
     * <p>
     * Per the specification, {@code translate(x,y)} is appended to the end of this element's own {@code transform} list rather than applied separately - both go into the JavaFX
     * {@code transforms} list, in that order, rather than using the {@code translateX}/{@code translateY} node properties, which are always outermost in JavaFX regardless of call
     * order. See the identical fix for {@code <use>} (#19).
     */
    @Override
    public Node createGraphic(RenderContext context) {
        applyStyle(context);
        double width = resolveWidth(context);
        double height = resolveHeight(context);
        Group group = new Group();
        group.setId(getId());
        if (width <= 0 || height <= 0) {
            return group;
        }

        Image image = resolveImage(context);
        if (image == null || image.isError()) {
            return group;
        }

        Group fitted = new Group(new ImageView(image));
        ViewBox intrinsic = new ViewBox(new BoundingBox(0, 0, image.getWidth(), image.getHeight()));
        Transform fitTransform = intrinsic.createTransform(width, height, PreserveAspectRatio.parse(getPreserveAspectRatio()));
        if (fitTransform != null) {
            fitted.getTransforms()
                .add(fitTransform);
        }
        group.getChildren()
            .add(fitted);
        // the clip must live here rather than on `fitted`: Node.setClip() applies in this node's own pre-transform
        // local space, which - since `fitted`'s own scale transform already ran to produce group's child - is
        // exactly the fitted image's post-scale coordinates, not the image's raw pixels
        group.setClip(new Rectangle(width, height));
        // #193: unlike every other graphics element, this class never called applyFilter at all - filter="..." was
        // silently ignored. This plays the role of applyClip's own required ordering (see that method's javadoc):
        // the slice-fit clip just above is the "existing clip" a filter region needs to already be there to
        // intersect correctly with.
        applyFilter(context, group);

        applyNodeProperties(context, group);
        String visibility = SvgInheritedStyle.resolve(context, this)
            .getVisibility();
        if ("hidden".equalsIgnoreCase(visibility) || "collapse".equalsIgnoreCase(visibility)) {
            group.setVisible(false);
        }
        group.getTransforms()
            .addAll(getTransformList());
        group.getTransforms()
            .add(new Translate(resolveX(context), resolveY(context)));
        // clip-path is not applied here: group already has a clip of its own (the slice-fit rectangle above), and
        // a second setClip() call would overwrite it - see #24's PR description for why this is out of scope for now.
        return group;
    }

    private Image resolveImage(RenderContext context) {
        String href = getXlinkHref();
        if (StringUtils.isBlank(href)) {
            return null;
        }
        try {
            if (href.startsWith("data:")) {
                // A tool that line-wraps its base64 output (Inkscape, at ~76 characters per line, is common) embeds
                // newlines the payload itself never contains meaningfully - every browser strips all ASCII
                // whitespace from a data: URL before decoding it (WHATWG Fetch's data: URL processing), but
                // JavaFX's own Image does not: it throws IllegalArgumentException on an embedded newline rather
                // than tolerating it, which this method's own catch below then silently turns into an empty image -
                // #181, found via a real Inkscape-exported document that rendered blank in foxglove but fine in a
                // WebView reference.
                return new Image(StringUtils.deleteWhitespace(href));
            }
            URI uri = new URI(href);
            if (uri.isAbsolute()) {
                return null;
            }
            return context.getBaseUri()
                .map(base -> base.resolve(uri)
                    .normalize())
                .filter(resolved -> !context.isActiveImageSource(resolved))
                .map(resolved -> loadImage(resolved, context))
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Loads the already-resolved, already-trust-checked {@code resolved} location - a bitmap via JavaFX's own {@link Image} decoder, unchanged from before #178, or - when the path
     * looks like an SVG document - by rendering it and rasterising the result (see {@link #rasterizeSvg}). Detected by file extension rather than by attempting a bitmap decode
     * first and falling back: the W3C suite and every realistic document name an SVG source with a {@code .svg} extension, and a single deterministic attempt avoids doing the
     * (much more common) raster case's decode twice.
     */
    private static Image loadImage(URI resolved, RenderContext context) {
        String path = resolved.getPath();
        if (path != null && path.toLowerCase(Locale.ROOT)
            .endsWith(".svg")) {
            return rasterizeSvg(resolved, context);
        }
        return new Image(resolved.toString(), false);
    }

    /**
     * Parses and renders the SVG document at {@code resolved} at its own intrinsic size (see {@link SvgGraphic#getIntrinsicSize()}) - not this {@code <image>} element's own
     * {@code width}/{@code height} - then snapshots it, the same technique {@code SvgMaskRenderer.rasterize}/{@code SvgPattern.rasterize} already use elsewhere in this codebase.
     * Rendering at the referenced document's own size, rather than this element's box, keeps the result equivalent to a raster image of that same intrinsic size: {@code
     * createGraphic}'s own existing {@code ViewBox}/{@code preserveAspectRatio} fit logic then scales it into this element's viewport exactly as it already does for a bitmap,
     * rather than this element's own fit being bypassed by forcing the referenced document to fill the box outright.
     * <p>
     * {@code resolved} is added to {@code context}'s own {@link RenderContext#getActiveImageSources()} and transplanted onto the fresh root context the referenced document is
     * rendered from (see {@link RenderContext#withActiveImageSources}) - {@code resolved}'s own <i>caller</i>, this element, already refused to reach this method at all when
     * {@code resolved} was already in that set (see {@link #resolveImage}); this propagates the set one level further down so a cycle reachable through this document in turn -
     * either back to {@code resolved} itself or to another location already in progress further up the chain - is still caught (#192) rather than recursing until the stack
     * overflows.
     * <p>
     * Returns {@code null} - degrading to an empty group, the same as an unresolvable or errored raster reference - when the document fails to load or parse
     * ({@link FoxgloveParser#parseFile(URI)} degrades to an empty, base-URI-less {@link SvgGraphic} rather than throwing; a {@code null} base URI here is exactly that failure,
     * since a document that genuinely parsed - even an empty one - always has its base URI set), when it resolves to a zero-area intrinsic size, or when snapshotting itself is not
     * possible (most usually because the caller is not on the JavaFX Application Thread, which {@code Node.snapshot} requires).
     */
    private static Image rasterizeSvg(URI resolved, RenderContext context) {
        SvgGraphic externalGraphic = FoxgloveParser.shared()
            .parseFile(resolved);
        if (externalGraphic.getBaseUri() == null) {
            return null;
        }
        Dimension2D intrinsic = externalGraphic.getIntrinsicSize();
        if (intrinsic.getWidth() <= 0 || intrinsic.getHeight() <= 0) {
            return null;
        }
        try {
            Set<URI> activeImageSources = new HashSet<>(context.getActiveImageSources());
            activeImageSources.add(resolved);
            RenderContext externalContext = RenderContext.root(externalGraphic.getElementIndex(), 0, 0)
                .withBaseUri(externalGraphic.getBaseUri())
                .withActiveImageSources(activeImageSources);
            Node rendered = externalGraphic.createGraphic(externalContext);
            new Scene(new Group(rendered));
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            params.setViewport(new Rectangle2D(0, 0, intrinsic.getWidth(), intrinsic.getHeight()));
            return rendered.snapshot(params, null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        ISvgBounded.super.toStringDetail(builder);
        builder.add("preserveAspectRatio", preserveAspectRatio);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgTransformable.super.toStringDetail(builder);
    }

}
