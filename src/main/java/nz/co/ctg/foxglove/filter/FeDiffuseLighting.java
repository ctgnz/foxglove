package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

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
@XmlRootElement(name = "feDiffuseLighting")
public class FeDiffuseLighting extends AbstractSvgStylable implements ISvgFilterPrimitive {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in;

    @XmlAttribute(name = "surfaceScale")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String surfaceScale;

    @XmlAttribute(name = "diffuseConstant")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String diffuseConstant;

    @XmlAttribute(name = "kernelUnitLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String kernelUnitLength;

    @XmlPath(".")
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

    public String getDiffuseConstant() {
        return diffuseConstant;
    }

    public void setDiffuseConstant(String value) {
        this.diffuseConstant = value;
    }

    public String getKernelUnitLength() {
        return kernelUnitLength;
    }

    public void setKernelUnitLength(String value) {
        this.kernelUnitLength = value;
    }

    @Override
    public SvgFilterAttributes getFilterAttributes() {
        return filter;
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
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("in", in);
        builder.add("surfaceScale", surfaceScale);
        builder.add("diffuseConstant", diffuseConstant);
        builder.add("kernelUnitLength", kernelUnitLength);
        super.toStringDetail(builder);
        filter.toStringDetail(builder);
    }

}
