package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgBounded;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgContainer;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgFitToViewBox;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.RenderContext.Axis;
import nz.co.ctg.foxglove.RenderContext.UnitsMode;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.SvgStyle;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.clip.SvgClipPath;
import nz.co.ctg.foxglove.clip.SvgMask;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.element.SvgAnchor;
import nz.co.ctg.foxglove.element.SvgCursor;
import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.element.SvgForeignObject;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.element.SvgImage;
import nz.co.ctg.foxglove.element.SvgMarker;
import nz.co.ctg.foxglove.element.SvgScript;
import nz.co.ctg.foxglove.element.SvgSwitch;
import nz.co.ctg.foxglove.element.SvgSymbol;
import nz.co.ctg.foxglove.element.SvgUse;
import nz.co.ctg.foxglove.element.SvgView;
import nz.co.ctg.foxglove.filter.SvgFilter;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgEllipse;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgPolygon;
import nz.co.ctg.foxglove.shape.SvgPolyline;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.text.SvgAltGlyphDef;
import nz.co.ctg.foxglove.text.SvgFont;
import nz.co.ctg.foxglove.text.SvgFontFace;
import nz.co.ctg.foxglove.text.SvgText;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "pattern")
public class SvgPattern extends AbstractSvgStylable
    implements ISvgBounded, ISvgConditionalFeatures, ISvgLinkable, ISvgExternalResources, ISvgFitToViewBox, ISvgContainer {

    /**
     * Supersampling factor applied when rasterising a tile, so a pattern fill still looks crisp when the shape it
     * fills is scaled up - at a proportional memory cost. Adjustable at runtime; there is no configuration system in
     * this codebase to hang it off instead.
     */
    public static double rasterScale = 2.0;

    @XmlAttribute(name = "patternUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String patternUnits;

    @XmlAttribute(name = "patternContentUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String patternContentUnits;

    @XmlAttribute(name = "patternTransform")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String patternTransform;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateMotion", type = SvgAnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "svg", type = SvgGraphic.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "g", type = SvgGroup.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "defs", type = SvgDefinitions.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "symbol", type = SvgSymbol.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "use", type = SvgUse.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "switch", type = SvgSwitch.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "image", type = SvgImage.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "style", type = SvgStyle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "path", type = SvgPath.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "rect", type = SvgRectangle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "circle", type = SvgCircle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "line", type = SvgLine.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "ellipse", type = SvgEllipse.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "polyline", type = SvgPolyline.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "polygon", type = SvgPolygon.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "text", type = SvgText.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "altGlyphDef", type = SvgAltGlyphDef.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "marker", type = SvgMarker.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "color-profile", type = SvgColorProfile.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "linearGradient", type = SvgLinearGradient.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "radialGradient", type = SvgRadialGradient.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "pattern", type = SvgPattern.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "clipPath", type = SvgClipPath.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "mask", type = SvgMask.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "filter", type = SvgFilter.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "cursor", type = SvgCursor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "a", type = SvgAnchor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "view", type = SvgView.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "script", type = SvgScript.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "font", type = SvgFont.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "font-face", type = SvgFontFace.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "foreignObject", type = SvgForeignObject.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    public String getPatternUnits() {
        return patternUnits;
    }

    public void setPatternUnits(String value) {
        this.patternUnits = value;
    }

    public String getPatternContentUnits() {
        return patternContentUnits;
    }

    public void setPatternContentUnits(String value) {
        this.patternContentUnits = value;
    }

    /**
     * Parsed, but not applied - like {@code gradientTransform} ({@link ISvgGradientElement#getGradientTransform()}),
     * a JavaFX {@link ImagePattern} has nowhere to put a transform, only an anchor rectangle. Baking a rotation or a
     * skew into the rasterised tile itself is possible but is its own piece of follow-on work.
     */
    public String getPatternTransform() {
        return patternTransform;
    }

    public void setPatternTransform(String value) {
        this.patternTransform = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    /**
     * Builds the tiling paint for this pattern, or null when it resolves to no usable tile.
     * <p>
     * Rasterises the pattern's content via {@code Node.snapshot(...)}, which requires the JavaFX Application
     * Thread and throws {@link IllegalStateException} otherwise - the one place in this renderer with that
     * requirement. A repeated call at the same resolved tile size reuses the cached image rather than rendering
     * again.
     *
     * @param context the rendering context, carrying the current viewport for {@code userSpaceOnUse} lengths and,
     *        for the default {@code objectBoundingBox} mode, the referencing shape's own bounding box
     */
    public Paint createPaint(RenderContext context) {
        Bounds bbox = context.getObjectBoundingBox().orElse(null);
        UnitsMode unitsMode = RenderContext.parseUnits(getPatternUnits(), UnitsMode.OBJECT_BOUNDING_BOX);

        double tileX;
        double tileY;
        double tileWidth;
        double tileHeight;
        if (unitsMode == UnitsMode.OBJECT_BOUNDING_BOX) {
            if (bbox == null) {
                return null;
            }
            tileX = bbox.getMinX() + RenderContext.resolveFraction(getX()) * bbox.getWidth();
            tileY = bbox.getMinY() + RenderContext.resolveFraction(getY()) * bbox.getHeight();
            tileWidth = RenderContext.resolveFraction(getWidth()) * bbox.getWidth();
            tileHeight = RenderContext.resolveFraction(getHeight()) * bbox.getHeight();
        } else {
            tileX = context.resolveLength(getX(), Axis.HORIZONTAL);
            tileY = context.resolveLength(getY(), Axis.VERTICAL);
            tileWidth = context.resolveLength(getWidth(), Axis.HORIZONTAL);
            tileHeight = context.resolveLength(getHeight(), Axis.VERTICAL);
        }
        if (tileWidth <= 0 || tileHeight <= 0) {
            return null;
        }

        Image image = PatternTileCache.getOrRasterize(this, tileWidth, tileHeight, () -> rasterize(context, tileWidth, tileHeight, bbox));
        return new ImagePattern(image, tileX, tileY, tileWidth, tileHeight, false);
    }

    private Image rasterize(RenderContext context, double tileWidth, double tileHeight, Bounds bbox) {
        Group tileContent = new Group();
        appendContent(tileContent, context.withViewport(tileWidth, tileHeight));

        Transform viewBoxTransform = createViewportTransform(tileWidth, tileHeight);
        if (viewBoxTransform != null) {
            tileContent.getTransforms().add(viewBoxTransform);
        } else if (bbox != null && RenderContext.parseUnits(getPatternContentUnits(), UnitsMode.USER_SPACE_ON_USE) == UnitsMode.OBJECT_BOUNDING_BOX) {
            // Content coordinates are fractions of the bounding box: scaling the whole subtree by its dimensions is
            // equivalent to, and far simpler than, teaching every shape class a bounding-box-relative coordinate mode.
            tileContent.getTransforms().add(new Scale(bbox.getWidth(), bbox.getHeight()));
        }

        Group root = new Group(tileContent);
        new Scene(root);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setTransform(new Scale(rasterScale, rasterScale));
        // Content need not fill the whole declared tile (a pattern can be smaller than its own width/height, the
        // rest left transparent) - without an explicit viewport, snapshot sizes the image to the content's own
        // bounds instead of the tile, so a sparse tile would rasterise far smaller than it declares.
        params.setViewport(new Rectangle2D(0, 0, tileWidth, tileHeight));
        return root.snapshot(params, null);
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        ISvgBounded.super.toStringDetail(builder);
        builder.add("patternUnits", patternUnits);
        builder.add("patternContentUnits", patternContentUnits);
        builder.add("patternTransform", patternTransform);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgFitToViewBox.super.toStringDetail(builder);
    }

}
