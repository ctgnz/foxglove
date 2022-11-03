package nz.co.ctg.foxglove.clip;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.attributes.SvgConditionalFeaturesAttributes;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.attributes.SvgGraphicsStyleAttributes;
import nz.co.ctg.foxglove.attributes.SvgPresentationStyleAttributes;
import nz.co.ctg.foxglove.attributes.SvgTextStyleAttributes;
import nz.co.ctg.foxglove.attributes.SvgTransformAttribute;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.element.SvgUse;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgEllipse;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgPolygon;
import nz.co.ctg.foxglove.shape.SvgPolyline;
import nz.co.ctg.foxglove.shape.SvgRectangle;
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


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "presentation", "graphics", "text", "conditionalFeatures", "externalResources", "transform", "content"
})
@XmlRootElement(name = "clipPath")
public class SvgClipPath extends AbstractSvgElement implements ISvgClipElement, ISvgTransformable {

    @XmlAttribute(name = "clipPathUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String clipPathUnits;

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgConditionalFeaturesAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@requiredFeatures", transformerClass = SvgConditionalFeaturesAttributes.class),
        @XmlWriteTransformer(xmlPath = "@requiredExtensions", transformerClass = SvgConditionalFeaturesAttributes.class),
        @XmlWriteTransformer(xmlPath = "@systemLanguage", transformerClass = SvgConditionalFeaturesAttributes.class)
    })
    private final SvgConditionalFeaturesAttributes conditionalFeatures = new SvgConditionalFeaturesAttributes();

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgExternalResourcesAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@externalResourcesRequired", transformerClass = SvgExternalResourcesAttributes.class)
    })
    private final SvgExternalResourcesAttributes externalResources = new SvgExternalResourcesAttributes();

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgTransformAttribute.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@transform", transformerClass = SvgTransformAttribute.class)
    })
    private final SvgTransformAttribute transform = new SvgTransformAttribute();

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateMotion", type = SvgAnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "use", type = SvgUse.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "path", type = SvgPath.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "rect", type = SvgRectangle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "circle", type = SvgCircle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "line", type = SvgLine.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "ellipse", type = SvgEllipse.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "polyline", type = SvgPolyline.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "polygon", type = SvgPolygon.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "text", type = SvgText.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgPresentationStyleAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@flood-color", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@flood-opacity", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@lighting-color", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@enable-background", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@clip", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@overflow", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@marker-start", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@marker-mid", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@marker-end", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@clip-path", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@clip-rule", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@mask", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@filter", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color-interpolation-filters", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@cursor", transformerClass = SvgPresentationStyleAttributes.class)
    })
    protected final SvgPresentationStyleAttributes presentation = new SvgPresentationStyleAttributes();

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgGraphicsStyleAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@fill", transformerClass = SvgGraphicsStyleAttributes.class), @XmlWriteTransformer(xmlPath = "@fill-rule", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-dasharray", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-dashoffset", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-linecap", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-linejoin", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-miterlimit", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-width", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color-interpolation", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color-rendering", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@opacity", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@fill-opacity", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-opacity", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@display", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@image-rendering", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@pointer-events", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@shape-rendering", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@text-rendering", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@visibility", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color-profile", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stop-color", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stop-opacity", transformerClass = SvgGraphicsStyleAttributes.class)
    })
    protected final SvgGraphicsStyleAttributes graphics = new SvgGraphicsStyleAttributes();

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgTextStyleAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@font-family", transformerClass = SvgTextStyleAttributes.class), @XmlWriteTransformer(xmlPath = "@font-size", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-size-adjust", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-stretch", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-style", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-variant", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-weight", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@alignment-baseline", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@baseline-shift", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@direction", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@dominant-baseline", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@glyph-orientation-horizontal", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@glyph-orientation-vertical", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@kerning", transformerClass = SvgTextStyleAttributes.class), @XmlWriteTransformer(xmlPath = "@letter-spacing", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@text-anchor", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@text-decoration", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@unicode-bidi", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@word-spacing", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@writing-mode", transformerClass = SvgTextStyleAttributes.class)
    })
    protected final SvgTextStyleAttributes text = new SvgTextStyleAttributes();

    @XmlAttribute(name = "style")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String style;

    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String className;

    public String getClipPathUnits() {
        return clipPathUnits;
    }

    public void setClipPathUnits(String value) {
        this.clipPathUnits = value;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String value) {
        this.style = value;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String value) {
        this.className = value;
    }

    @Override
    public SvgPresentationStyleAttributes getPresentationAttributes() {
        return presentation;
    }

    @Override
    public SvgGraphicsStyleAttributes getGraphicsAttributes() {
        return graphics;
    }

    @Override
    public SvgTextStyleAttributes getTextAttributes() {
        return text;
    }

    @Override
    public SvgConditionalFeaturesAttributes getConditionalFeaturesAttributes() {
        return conditionalFeatures;
    }

    @Override
    public SvgExternalResourcesAttributes getExternalResourcesAttributes() {
        return externalResources;
    }

    @Override
    public SvgTransformAttribute getTransformAttribute() {
        return transform;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("clipPathUnits", clipPathUnits);
        super.toStringDetail(builder);
        builder.add("style", style);
        builder.add("className", className);
        presentation.toStringDetail(builder);
        graphics.toStringDetail(builder);
        text.toStringDetail(builder);
        conditionalFeatures.toStringDetail(builder);
        externalResources.toStringDetail(builder);
        transform.toStringDetail(builder);
    }

}
