package nz.co.ctg.foxglove.element;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgBounded;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.RenderContext;
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
import javafx.geometry.BoundingBox;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "image")
public class SvgImage extends AbstractSvgStylable
    implements ISvgStructuralElement, ISvgBounded, ISvgEventListener, ISvgConditionalFeatures, ISvgLinkable, ISvgExternalResources, ISvgTransformable,
    FxGraphic<Node> {

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
     * Renders the referenced raster image, fitted into this element's {@code x}/{@code y}/{@code width}/
     * {@code height} viewport per {@code preserveAspectRatio}, or an empty {@link Group} - never {@code null} -
     * when the reference is missing, malformed, unresolvable, or the viewport has no positive area.
     * <p>
     * Only {@code data:} URIs and references relative to a known document base URI (see
     * {@link nz.co.ctg.foxglove.FoxgloveParser#parseFile}) are supported. Any other absolute reference - a network
     * URL or a bare {@code file:} URI given directly in the document - is out of scope for now and also fails
     * cleanly, keeping the trust boundary to only the document's own embedded data and whatever the caller chose
     * to parse from disk.
     * <p>
     * Per the specification, {@code translate(x,y)} is appended to the end of this element's own {@code transform}
     * list rather than applied separately - both go into the JavaFX {@code transforms} list, in that order, rather
     * than using the {@code translateX}/{@code translateY} node properties, which are always outermost in JavaFX
     * regardless of call order. See the identical fix for {@code <use>} (#19).
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
            fitted.getTransforms().add(fitTransform);
        }
        group.getChildren().add(fitted);
        // the clip must live here rather than on `fitted`: Node.setClip() applies in this node's own pre-transform
        // local space, which - since `fitted`'s own scale transform already ran to produce group's child - is
        // exactly the fitted image's post-scale coordinates, not the image's raw pixels
        group.setClip(new Rectangle(width, height));

        applyNodeProperties(context, group);
        String visibility = SvgInheritedStyle.resolve(context, this).getVisibility();
        if ("hidden".equalsIgnoreCase(visibility) || "collapse".equalsIgnoreCase(visibility)) {
            group.setVisible(false);
        }
        group.getTransforms().addAll(getTransformList());
        group.getTransforms().add(new Translate(resolveX(context), resolveY(context)));
        return group;
    }

    private Image resolveImage(RenderContext context) {
        String href = getXlinkHref();
        if (StringUtils.isBlank(href)) {
            return null;
        }
        try {
            if (href.startsWith("data:")) {
                return new Image(href);
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
