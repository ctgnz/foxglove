package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.ISvgPresentationAttributes;
import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.ISvgTextAttributes;
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
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "animations"
})
@XmlRootElement(name = "feComposite")
public class FeComposite extends AbstractSvgElement implements ISvgStylable, ISvgPresentationAttributes, ISvgGraphicsAttributes, ISvgTextAttributes, ISvgFilterPrimitive {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in;

    @XmlAttribute(name = "in2", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in2;

    @XmlAttribute(name = "operator")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String operator;

    @XmlAttribute(name = "k1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String k1;

    @XmlAttribute(name = "k2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String k2;

    @XmlAttribute(name = "k3")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String k3;

    @XmlAttribute(name = "k4")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String k4;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgAnimationElement> animations;

    @XmlAttribute(name = "style")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String style;

    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String className;

    public String getIn() {
        return in;
    }

    public void setIn(String value) {
        this.in = value;
    }

    public String getIn2() {
        return in2;
    }

    public void setIn2(String value) {
        this.in2 = value;
    }

    public String getOperator() {
        if (operator == null) {
            return "over";
        } else {
            return operator;
        }
    }

    public void setOperator(String value) {
        this.operator = value;
    }

    public String getK1() {
        return k1;
    }

    public void setK1(String value) {
        this.k1 = value;
    }

    public String getK2() {
        return k2;
    }

    public void setK2(String value) {
        this.k2 = value;
    }

    public String getK3() {
        return k3;
    }

    public void setK3(String value) {
        this.k3 = value;
    }

    public String getK4() {
        return k4;
    }

    public void setK4(String value) {
        this.k4 = value;
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

    public List<ISvgAnimationElement> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<>();
        }
        return this.animations;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("in", in);
        builder.add("in2", in2);
        builder.add("operator", operator);
        builder.add("k1", k1);
        builder.add("k2", k2);
        builder.add("k3", k3);
        builder.add("k4", k4);
        super.toStringDetail(builder);
        builder.add("style", style);
        builder.add("className", className);
        ISvgPresentationAttributes.super.toStringDetail(builder);
        ISvgGraphicsAttributes.super.toStringDetail(builder);
        ISvgTextAttributes.super.toStringDetail(builder);
        ISvgFilterPrimitive.super.toStringDetail(builder);
    }

}
