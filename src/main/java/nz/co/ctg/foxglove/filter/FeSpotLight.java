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
@XmlRootElement(name = "feSpotLight")
public class FeSpotLight extends AbstractSvgElement implements ISvgFilterLightSource {

    @XmlAttribute(name = "x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String x;

    @XmlAttribute(name = "y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String y;

    @XmlAttribute(name = "z")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String z;

    @XmlAttribute(name = "pointsAtX")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String pointsAtX;

    @XmlAttribute(name = "pointsAtY")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String pointsAtY;

    @XmlAttribute(name = "pointsAtZ")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String pointsAtZ;

    @XmlAttribute(name = "specularExponent")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String specularExponent;

    @XmlAttribute(name = "limitingConeAngle")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String limitingConeAngle;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgAnimationElement> animations;

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

    public String getZ() {
        return z;
    }

    public void setZ(String value) {
        this.z = value;
    }

    public String getPointsAtX() {
        return pointsAtX;
    }

    public void setPointsAtX(String value) {
        this.pointsAtX = value;
    }

    public String getPointsAtY() {
        return pointsAtY;
    }

    public void setPointsAtY(String value) {
        this.pointsAtY = value;
    }

    public String getPointsAtZ() {
        return pointsAtZ;
    }

    public void setPointsAtZ(String value) {
        this.pointsAtZ = value;
    }

    public String getSpecularExponent() {
        return specularExponent;
    }

    public void setSpecularExponent(String value) {
        this.specularExponent = value;
    }

    public String getLimitingConeAngle() {
        return limitingConeAngle;
    }

    public void setLimitingConeAngle(String value) {
        this.limitingConeAngle = value;
    }

    @Override
    public List<ISvgAnimationElement> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<>();
        }
        return this.animations;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("x", x);
        builder.add("y", y);
        builder.add("z", z);
        builder.add("pointsAtX", pointsAtX);
        builder.add("pointsAtY", pointsAtY);
        builder.add("pointsAtZ", pointsAtZ);
        builder.add("specularExponent", specularExponent);
        builder.add("limitingConeAngle", limitingConeAngle);
        super.toStringDetail(builder);
    }

}
