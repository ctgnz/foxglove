package nz.co.ctg.foxglove;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.type.ViewBox;

import static java.util.stream.Collectors.toList;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.transform.Transform;

import static nz.co.ctg.foxglove.RenderContext.Axis;

@XmlRootElement(name = "svg", namespace = "http://www.w3.org/2000/svg")
public class SvgGraphic extends AbstractSvgStylable
    implements ISvgStylable, ISvgBounded, ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgFitToViewBox, ISvgDescribable, ISvgContainer,
    FxGraphic<Group> {

    /**
     * The CSS/SVG UA fallback intrinsic size when neither {@code width}/{@code height} nor {@code viewBox} give one -
     * relevant only at the document root, since a nested {@code <svg>} always resolves against its parent viewport.
     */
    private static final double DEFAULT_WIDTH = 300;
    private static final double DEFAULT_HEIGHT = 150;

    private String onUnload;
    private String onAbort;
    private String onError;
    private String onResize;
    private String onScroll;
    private String onZoom;
    private String zoomAndPan;
    private String version;
    private String baseProfile;
    private String contentScriptType;
    private String contentStyleType;
    private List<ISvgElement> content;

    @XmlTransient
    private SvgElementIndex elementIndex;

    public void addContent(ISvgElement element) {
        if (element != null) {
            content.add(element);
        }
    }

    /**
     * The index used to resolve {@code url(#id)} and {@code xlink:href="#id"} references within this document, built
     * on first use and cached thereafter. Call {@link #rebuildElementIndex()} after modifying the document, as the
     * index is a snapshot rather than a live view.
     */
    @XmlTransient
    public SvgElementIndex getElementIndex() {
        if (elementIndex == null) {
            elementIndex = SvgElementIndex.of(this);
        }
        return elementIndex;
    }

    /**
     * Rebuilds the element index to pick up changes made to the document since it was last built.
     */
    public SvgElementIndex rebuildElementIndex() {
        elementIndex = SvgElementIndex.of(this);
        return elementIndex;
    }

    /**
     * Renders this element as the root of the document, establishing the initial viewport from its own
     * {@code width}/{@code height} (falling back to its {@code viewBox}, then to the standard 300x150 default).
     */
    public Group createGroup() {
        return createGraphic(RenderContext.root(getElementIndex(), 0, 0));
    }

    /**
     * Renders this element - root or nested - establishing the viewport its content and descendants resolve
     * percentages and {@code viewBox} against.
     */
    @Override
    public Group createGraphic(RenderContext parentContext) {
        parseStyle();
        Group group = new Group();
        group.setId(StringUtils.defaultIfBlank(getId(), "svg"));

        // x/y position this element within its parent, so - unlike width/height/viewBox below - they resolve
        // against the parent's viewport rather than the one this element is about to establish.
        group.setTranslateX(parentContext.resolveLength(getX(), Axis.HORIZONTAL));
        group.setTranslateY(parentContext.resolveLength(getY(), Axis.VERTICAL));

        double width = resolveIntrinsicLength(getWidth(), parentContext, Axis.HORIZONTAL, DEFAULT_WIDTH);
        double height = resolveIntrinsicLength(getHeight(), parentContext, Axis.VERTICAL, DEFAULT_HEIGHT);

        // Content resolves percentages against the viewBox's own width/height, not the pixel viewport, once a
        // viewBox has switched descendants into its coordinate system - the pixel size only matters for computing
        // the viewBox-to-viewport transform itself.
        double childViewportWidth = width;
        double childViewportHeight = height;
        ViewBox viewBox = getViewBox();
        if (viewBox != null) {
            Transform viewBoxTransform = createViewportTransform(width, height);
            if (viewBoxTransform != null) {
                group.getTransforms().add(viewBoxTransform);
            }
            if (viewBox.getWidth() != null) {
                childViewportWidth = viewBox.getWidth().pixels();
            }
            if (viewBox.getHeight() != null) {
                childViewportHeight = viewBox.getHeight().pixels();
            }
        }

        appendContent(group, parentContext.withViewport(childViewportWidth, childViewportHeight));
        return group;
    }

    /**
     * Resolves {@code width}/{@code height} against the parent viewport when set to a usable length, falling back
     * to the {@code viewBox} dimension along the same axis, then to {@code fallbackDefault} - the standard UA
     * behaviour when a viewport-establishing element gives no intrinsic size of its own.
     */
    private double resolveIntrinsicLength(Size size, RenderContext parentContext, Axis axis, double fallbackDefault) {
        if (size != null) {
            double resolved = parentContext.resolveLength(size, axis);
            if (resolved > 0) {
                return resolved;
            }
        }
        ViewBox viewBox = getViewBox();
        if (viewBox != null) {
            Size viewBoxLength = axis == Axis.HORIZONTAL ? viewBox.getWidth() : viewBox.getHeight();
            if (viewBoxLength != null && viewBoxLength.pixels() > 0) {
                return viewBoxLength.pixels();
            }
        }
        return fallbackDefault;
    }

    public SvgGroup getBaseGroup() {
        if (content == null || content.isEmpty()) {
            return null;
        }
        return content.stream().filter(SvgGroup.class::isInstance).map(SvgGroup.class::cast).filter(SvgGroup::isVisible).findFirst().orElse(null);
    }

    public String getBaseProfile() {
        return baseProfile;
    }

    @Override
    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    public String getContentScriptType() {
        if (contentScriptType == null) {
            return "application/ecmascript";
        } else {
            return contentScriptType;
        }
    }

    public String getContentStyleType() {
        if (contentStyleType == null) {
            return "text/css";
        } else {
            return contentStyleType;
        }
    }

    public String getOnAbort() {
        return onAbort;
    }

    public String getOnError() {
        return onError;
    }

    public String getOnResize() {
        return onResize;
    }

    public String getOnScroll() {
        return onScroll;
    }

    public String getOnUnload() {
        return onUnload;
    }

    public String getOnZoom() {
        return onZoom;
    }

    public String getVersion() {
        if (version == null) {
            return "1.1";
        } else {
            return version;
        }
    }

    @XmlTransient
    public List<? extends ISvgElement> getVisibleContent() {
        return content.stream()
            .filter(AbstractSvgStylable.class::isInstance)
            .map(AbstractSvgStylable.class::cast)
            .filter(AbstractSvgStylable::isVisible)
            .collect(toList());
    }

    public String getZoomAndPan() {
        if (zoomAndPan == null) {
            return "magnify";
        } else {
            return zoomAndPan;
        }
    }

    public void setBaseProfile(String value) {
        this.baseProfile = value;
    }

    public void setBounds(Bounds bounds) {
        setX(new Size(bounds.getMinX(), SizeUnits.PX));
        setY(new Size(bounds.getMinY(), SizeUnits.PX));
        setWidth(new Size(bounds.getWidth(), SizeUnits.PX));
        setHeight(new Size(bounds.getHeight(), SizeUnits.PX));
    }

    public void setContentScriptType(String value) {
        this.contentScriptType = value;
    }

    public void setContentStyleType(String value) {
        this.contentStyleType = value;
    }

    public void setOnAbort(String value) {
        this.onAbort = value;
    }

    public void setOnError(String value) {
        this.onError = value;
    }

    public void setOnResize(String value) {
        this.onResize = value;
    }

    public void setOnScroll(String value) {
        this.onScroll = value;
    }

    public void setOnUnload(String value) {
        this.onUnload = value;
    }

    public void setOnZoom(String value) {
        this.onZoom = value;
    }

    public void setTitle(String title) {
        SvgTitle svgTitle = new SvgTitle();
        svgTitle.setValue(title);
        getContent().add(svgTitle);
    }

    public void setVersion(String value) {
        this.version = value;
    }

    public void setZoomAndPan(String value) {
        this.zoomAndPan = value;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        ISvgBounded.super.toStringDetail(builder);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgFitToViewBox.super.toStringDetail(builder);
    }

}
