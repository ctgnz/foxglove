package nz.co.ctg.foxglove.text;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlValueExtension;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.ISvgValueElement;
import nz.co.ctg.foxglove.attributes.SvgConditionalFeaturesAttributes;
import nz.co.ctg.foxglove.attributes.SvgEventListenerAttributes;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.attributes.SvgGraphicsStyleAttributes;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;
import nz.co.ctg.foxglove.attributes.SvgPresentationStyleAttributes;
import nz.co.ctg.foxglove.attributes.SvgTextStyleAttributes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "presentation", "graphics", "text", "conditionalFeatures", "linkable", "externalResources", "eventListener", "value"
})
@XmlRootElement(name = "textPath")
public class SvgTextPath extends AbstractSvgElement implements ISvgTextContentElement, ISvgLinkable, ISvgValueElement {

    @XmlAttribute(name = "startOffset")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String startOffset;

    @XmlAttribute(name = "textLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String textLength;

    @XmlAttribute(name = "lengthAdjust")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String lengthAdjust;

    @XmlAttribute(name = "method")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String method;

    @XmlAttribute(name = "spacing")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String spacing;

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgConditionalFeaturesAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@requiredFeatures", transformerClass = SvgConditionalFeaturesAttributes.class),
        @XmlWriteTransformer(xmlPath = "@requiredExtensions", transformerClass = SvgConditionalFeaturesAttributes.class),
        @XmlWriteTransformer(xmlPath = "@systemLanguage", transformerClass = SvgConditionalFeaturesAttributes.class)
    })
    private final SvgConditionalFeaturesAttributes conditionalFeatures = new SvgConditionalFeaturesAttributes();

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgLinkableAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@xmlns:xlink", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "@xlink:type", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "@xlink:href", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "@xlink:role", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "@xlink:arcrole", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "@xlink:title", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "@xlink:show", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "@xlink:actuate", transformerClass = SvgLinkableAttributes.class)
    })
    private final SvgLinkableAttributes linkable = new SvgLinkableAttributes();

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgExternalResourcesAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@externalResourcesRequired", transformerClass = SvgExternalResourcesAttributes.class)
    })
    private final SvgExternalResourcesAttributes externalResources = new SvgExternalResourcesAttributes();

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgEventListenerAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@onfocusin", transformerClass = SvgEventListenerAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onfocusout", transformerClass = SvgEventListenerAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onactivate", transformerClass = SvgEventListenerAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onclick", transformerClass = SvgEventListenerAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onmousedown", transformerClass = SvgEventListenerAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onmouseup", transformerClass = SvgEventListenerAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onmouseover", transformerClass = SvgEventListenerAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onmousemove", transformerClass = SvgEventListenerAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onmouseout", transformerClass = SvgEventListenerAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onload", transformerClass = SvgEventListenerAttributes.class)
    })
    private final SvgEventListenerAttributes eventListener = new SvgEventListenerAttributes();

    @XmlValueExtension
    private String value;

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

    public String getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(String value) {
        this.startOffset = value;
    }

    public String getTextLength() {
        return textLength;
    }

    public void setTextLength(String value) {
        this.textLength = value;
    }

    public String getLengthAdjust() {
        return lengthAdjust;
    }

    public void setLengthAdjust(String value) {
        this.lengthAdjust = value;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String value) {
        this.method = value;
    }

    public String getSpacing() {
        return spacing;
    }

    public void setSpacing(String value) {
        this.spacing = value;
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
    public SvgLinkableAttributes getLinkableAttributes() {
        return linkable;
    }

    @Override
    public SvgExternalResourcesAttributes getExternalResourcesAttributes() {
        return externalResources;
    }

    @Override
    public SvgEventListenerAttributes getEventListenerAttributes() {
        return eventListener;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("horizOriginX", startOffset);
        builder.add("textLength", textLength);
        builder.add("lengthAdjust", lengthAdjust);
        builder.add("method", method);
        builder.add("spacing", spacing);
        super.toStringDetail(builder);
        builder.add("style", style);
        builder.add("className", className);
        presentation.toStringDetail(builder);
        graphics.toStringDetail(builder);
        text.toStringDetail(builder);
        conditionalFeatures.toStringDetail(builder);
        linkable.toStringDetail(builder);
        externalResources.toStringDetail(builder);
        eventListener.toStringDetail(builder);
        builder.add("value", value);
    }

}
