package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.animate.ISvgAnimationElement;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
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
    "animations"
})
@XmlRootElement(name = "feDistantLight")
public class FeDistantLight extends AbstractSvgElement implements ISvgFilterLightSource {

    @XmlAttribute(name = "azimuth")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String azimuth;

    @XmlAttribute(name = "elevation")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String elevation;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgAnimationElement> animations;

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String value) {
        this.azimuth = value;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String value) {
        this.elevation = value;
    }

    @Override
    public List<ISvgAnimationElement> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<>();
        }
        return this.animations;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("", azimuth);
        super.toStringDetail(builder);
    }

}
