package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.animate.ISvgAnimationElement;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;

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
    "filter", "lightSources", "animations"
})
@XmlRootElement(name = "feSpecularLighting")
public class FeSpecularLighting extends AbstractSvgStylable implements ISvgFilterPrimitive {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in;

    @XmlAttribute(name = "surfaceScale")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String surfaceScale;

    @XmlAttribute(name = "specularConstant")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String specularConstant;

    @XmlAttribute(name = "specularExponent")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String specularExponent;

    @XmlAttribute(name = "kernelUnitLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String kernelUnitLength;

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgFilterAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@x", transformerClass = SvgFilterAttributes.class),
        @XmlWriteTransformer(xmlPath = "@y", transformerClass = SvgFilterAttributes.class),
        @XmlWriteTransformer(xmlPath = "@width", transformerClass = SvgFilterAttributes.class),
        @XmlWriteTransformer(xmlPath = "@height", transformerClass = SvgFilterAttributes.class),
        @XmlWriteTransformer(xmlPath = "@result", transformerClass = SvgFilterAttributes.class)
    })
    private final SvgFilterAttributes filter = new SvgFilterAttributes();

    @XmlElements({
        @XmlElement(name = "feDistantLight", required = true, type = FeDistantLight.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "fePointLight", required = true, type = FePointLight.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "feSpotLight", required = true, type = FeSpotLight.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgFilterLightSource> lightSources;
    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgAnimationElement> animations;

    public String getIn() {
        return in;
    }

    public void setIn(String value) {
        this.in = value;
    }

    public String getSurfaceScale() {
        return surfaceScale;
    }

    public void setSurfaceScale(String value) {
        this.surfaceScale = value;
    }

    public String getSpecularConstant() {
        return specularConstant;
    }

    public void setSpecularConstant(String value) {
        this.specularConstant = value;
    }

    public String getSpecularExponent() {
        return specularExponent;
    }

    public void setSpecularExponent(String value) {
        this.specularExponent = value;
    }

    public String getKernelUnitLength() {
        return kernelUnitLength;
    }

    public void setKernelUnitLength(String value) {
        this.kernelUnitLength = value;
    }

    public List<ISvgFilterLightSource> getLightSources() {
        if (lightSources == null) {
            lightSources = new ArrayList<>();
        }
        return this.lightSources;
    }

    public List<ISvgAnimationElement> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<>();
        }
        return this.animations;
    }

    @Override
    public SvgFilterAttributes getFilterAttributes() {
        return filter;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("in", in);
        builder.add("surfaceScale", surfaceScale);
        builder.add("specularConstant", specularConstant);
        builder.add("specularExponent", specularExponent);
        builder.add("kernelUnitLength", kernelUnitLength);
        super.toStringDetail(builder);
        filter.toStringDetail(builder);
    }

}
