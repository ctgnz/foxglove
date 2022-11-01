package nz.co.ctg.foxglove.animate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.attributes.SvgConditionalFeaturesAttributes;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;
import nz.co.ctg.foxglove.description.ISvgDescriptiveElement;
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
@XmlType(name = "set", propOrder = {
    "animation", "conditionalFeatures", "linkable", "externalResources", "contents"
})
@XmlRootElement(name = "set")
public class SvgSetAttribute extends AbstractSvgElement implements ISvgAnimationElement {

    @XmlAttribute(name = "attributeName", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String attributeName;

    @XmlAttribute(name = "attributeType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String attributeType;

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgAnimationAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@onbegin", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onend", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onrepeat", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@onload", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@begin", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@dur", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@end", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@min", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@max", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@restart", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@repeatCount", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@repeatDur", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@fill", transformerClass = SvgAnimationAttributes.class),
        @XmlWriteTransformer(xmlPath = "@to", transformerClass = SvgAnimationAttributes.class),
    })
    private final SvgAnimationAttributes animation = new SvgAnimationAttributes();

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

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgDescriptiveElement> contents;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String value) {
        this.attributeName = value;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String value) {
        this.attributeType = value;
    }

    @Override
    public SvgAnimationAttributes getAnimationAttributes() {
        return animation;
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

    public List<ISvgDescriptiveElement> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return this.contents;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("attributeName", attributeName);
        builder.add("attributeType", attributeType);
        super.toStringDetail(builder);
        animation.toStringDetail(builder);
        conditionalFeatures.toStringDetail(builder);
        linkable.toStringDetail(builder);
        externalResources.toStringDetail(builder);
    }

}
