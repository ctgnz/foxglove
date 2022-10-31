package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.animate.ISvgAnimationElement;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "animations"
})
public class CompositeFilterEffectFunction {

    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String type;

    @XmlAttribute(name = "tableValues")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String tableValues;

    @XmlAttribute(name = "slope")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String slope;

    @XmlAttribute(name = "intercept")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String intercept;

    @XmlAttribute(name = "amplitude")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String amplitude;

    @XmlAttribute(name = "exponent")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String exponent;

    @XmlAttribute(name = "offset")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String offset;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgAnimationElement> animations;

    public CompositeFilterEffectFunction() {
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getTableValues() {
        return tableValues;
    }

    public void setTableValues(String value) {
        this.tableValues = value;
    }

    public String getSlope() {
        return slope;
    }

    public void setSlope(String value) {
        this.slope = value;
    }

    public String getIntercept() {
        return intercept;
    }

    public void setIntercept(String value) {
        this.intercept = value;
    }

    public String getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(String value) {
        this.amplitude = value;
    }

    public String getExponent() {
        return exponent;
    }

    public void setExponent(String value) {
        this.exponent = value;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String value) {
        this.offset = value;
    }

    public List<ISvgAnimationElement> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<>();
        }
        return this.animations;
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add("type", type);
        builder.add("tableValues", tableValues);
        builder.add("slope", slope);
        builder.add("intercept", intercept);
        builder.add("amplitude", amplitude);
        builder.add("exponent", exponent);
        builder.add("offset", offset);
    }
}