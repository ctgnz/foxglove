package nz.co.ctg.foxglove.element;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.attributes.SvgConditionalFeaturesAttributes;
import nz.co.ctg.foxglove.attributes.SvgEventListenerAttributes;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;
import nz.co.ctg.foxglove.attributes.SvgTransformAttribute;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

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
    "conditionalFeatures", "linkable", "externalResources", "eventListener", "transform", "content"
})
@XmlRootElement(name = "use")
public class SvgUse extends AbstractSvgStylable implements ISvgStructuralElement, ISvgEventListener, ISvgConditionalFeatures, ISvgTransformable, ISvgLinkable {

    @XmlAttribute(name = "x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String x;

    @XmlAttribute(name = "y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String y;

    @XmlAttribute(name = "width")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String width;

    @XmlAttribute(name = "height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String height;

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
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    public String getX() {
        return x;
    }

    public void setX(String value) {
        this.x = value;
    }

    public String getY() {
        return y;
    }

    public void setY(String value) {
        this.y = value;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String value) {
        this.width = value;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String value) {
        this.height = value;
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
    public SvgTransformAttribute getTransformAttribute() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("x", x);
        builder.add("y", y);
        builder.add("width", width);
        builder.add("height", height);
        super.toStringDetail(builder);
        conditionalFeatures.toStringDetail(builder);
        linkable.toStringDetail(builder);
        externalResources.toStringDetail(builder);
        eventListener.toStringDetail(builder);
        transform.toStringDetail(builder);
    }

}
