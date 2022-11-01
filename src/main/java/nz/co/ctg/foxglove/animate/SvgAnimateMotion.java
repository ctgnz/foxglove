package nz.co.ctg.foxglove.animate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.attributes.SvgConditionalFeaturesAttributes;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;
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
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "animation", "conditionalFeatures", "linkable", "externalResources", "contents"
})
@XmlRootElement(name = "animateMotion")
public class SvgAnimateMotion extends AbstractSvgElement implements ISvgAnimationElement {

    @XmlAttribute(name = "additive")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String additive;

    @XmlAttribute(name = "accumulate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String accumulate;

    @XmlAttribute(name = "calcMode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String calcMode;

    @XmlAttribute(name = "values")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String values;

    @XmlAttribute(name = "keyTimes")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String keyTimes;

    @XmlAttribute(name = "keySplines")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String keySplines;

    @XmlAttribute(name = "from")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String from;

    @XmlAttribute(name = "by")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String by;

    @XmlAttribute(name = "path")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String path;

    @XmlAttribute(name = "keyPoints")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String keyPoints;

    @XmlAttribute(name = "rotate")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String rotate;

    @XmlAttribute(name = "origin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String origin;

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
        @XmlElement(name = "mpath", required = true, type = SvgMotionPath.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "desc", required = true, type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", required = true, type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", required = true, type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> contents;

    public String getAdditive() {
        if (additive == null) {
            return "replace";
        } else {
            return additive;
        }
    }

    public void setAdditive(String value) {
        this.additive = value;
    }

    public String getAccumulate() {
        if (accumulate == null) {
            return "none";
        } else {
            return accumulate;
        }
    }

    public void setAccumulate(String value) {
        this.accumulate = value;
    }

    public String getCalcMode() {
        if (calcMode == null) {
            return "paced";
        } else {
            return calcMode;
        }
    }

    public void setCalcMode(String value) {
        this.calcMode = value;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String value) {
        this.values = value;
    }

    public String getKeyTimes() {
        return keyTimes;
    }

    public void setKeyTimes(String value) {
        this.keyTimes = value;
    }

    public String getKeySplines() {
        return keySplines;
    }

    public void setKeySplines(String value) {
        this.keySplines = value;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String value) {
        this.from = value;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String value) {
        this.by = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String value) {
        this.path = value;
    }

    public String getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(String value) {
        this.keyPoints = value;
    }

    public String getRotate() {
        return rotate;
    }

    public void setRotate(String value) {
        this.rotate = value;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String value) {
        this.origin = value;
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

    public List<ISvgElement> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return this.contents;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("additive", additive);
        builder.add("accumulate", accumulate);
        builder.add("calcMode", calcMode);
        builder.add("values", values);
        builder.add("keyTimes", keyTimes);
        builder.add("keySplines", keySplines);
        builder.add("from", from);
        builder.add("by", by);
        builder.add("path", path);
        builder.add("keyPoints", keyPoints);
        builder.add("rotate", rotate);
        builder.add("origin", origin);
        super.toStringDetail(builder);
        animation.toStringDetail(builder);
        conditionalFeatures.toStringDetail(builder);
        linkable.toStringDetail(builder);
        externalResources.toStringDetail(builder);
    }

}
