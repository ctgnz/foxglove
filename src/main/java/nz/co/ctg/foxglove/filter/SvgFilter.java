package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
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
    "linkable", "externalResources", "content"
})
@XmlRootElement(name = "filter")
public class SvgFilter extends AbstractSvgStylable implements ISvgExternalResources, ISvgLinkable {

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

    @XmlAttribute(name = "filterRes")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String filterRes;

    @XmlAttribute(name = "filterUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String filterUnits;

    @XmlAttribute(name = "primitiveUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String primitiveUnits;

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgLinkableAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "xmlns:@xlink", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "xlink:@type", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "xlink:@href", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "xlink:@role", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "xlink:@arcrole", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "xlink:@title", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "xlink:@show", transformerClass = SvgLinkableAttributes.class),
        @XmlWriteTransformer(xmlPath = "xlink:@actuate", transformerClass = SvgLinkableAttributes.class)
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
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feBlend", type = FeBlend.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feColorMatrix", type = FeColorMatrix.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feComponentTransfer", type = FeComponentTransfer.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feComposite", type = FeComposite.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feConvolveMatrix", type = FeConvolveMatrix.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feDiffuseLighting", type = FeDiffuseLighting.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feDisplacementMap", type = FeDisplacementMap.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feFlood", type = FeFlood.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feGaussianBlur", type = FeGaussianBlur.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feImage", type = FeImage.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feMerge", type = FeMerge.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feMorphology", type = FeMorphology.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feOffset", type = FeOffset.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feSpecularLighting", type = FeSpecularLighting.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feTile", type = FeTile.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feTurbulence", type = FeTurbulence.class, namespace = "http://www.w3.org/2000/svg")
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

    public String getFilterRes() {
        return filterRes;
    }

    public void setFilterRes(String value) {
        this.filterRes = value;
    }

    public String getFilterUnits() {
        return filterUnits;
    }

    public void setFilterUnits(String value) {
        this.filterUnits = value;
    }

    public String getPrimitiveUnits() {
        return primitiveUnits;
    }

    public void setPrimitiveUnits(String value) {
        this.primitiveUnits = value;
    }

    @Override
    public SvgLinkableAttributes getLinkableAttributes() {
        return linkable;
    }

    @Override
    public SvgExternalResourcesAttributes getExternalResourcesAttributes() {
        return externalResources;
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
        builder.add("filterRes", filterRes);
        builder.add("filterUnits", filterUnits);
        builder.add("primitiveUnits", primitiveUnits);
        super.toStringDetail(builder);
        linkable.toStringDetail(builder);
        externalResources.toStringDetail(builder);
    }
}
