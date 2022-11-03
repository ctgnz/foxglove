package nz.co.ctg.foxglove.filter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.ISvgPresentationAttributes;
import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.ISvgTextAttributes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "feFuncR", "feFuncG", "feFuncB", "feFuncA"
})
@XmlRootElement(name = "feComponentTransfer")
public class FeComponentTransfer extends AbstractSvgElement implements ISvgStylable, ISvgPresentationAttributes, ISvgGraphicsAttributes, ISvgTextAttributes, ISvgFilterPrimitive {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in;

    @XmlElement(name = "feFuncR")
    private FilterEffectFunctionRed feFuncR;

    @XmlElement(name = "feFuncG")
    private FilterEffectFunctionGreen feFuncG;

    @XmlElement(name = "feFuncB")
    private FilterEffectFunctionBlue feFuncB;

    @XmlElement(name = "feFuncA")
    private FilterEffectFunctionAlpha feFuncA;

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

    public FilterEffectFunctionRed getFeFuncR() {
        return feFuncR;
    }

    public void setFeFuncR(FilterEffectFunctionRed value) {
        this.feFuncR = value;
    }

    public FilterEffectFunctionGreen getFeFuncG() {
        return feFuncG;
    }

    public void setFeFuncG(FilterEffectFunctionGreen value) {
        this.feFuncG = value;
    }

    public FilterEffectFunctionBlue getFeFuncB() {
        return feFuncB;
    }

    public void setFeFuncB(FilterEffectFunctionBlue value) {
        this.feFuncB = value;
    }

    public FilterEffectFunctionAlpha getFeFuncA() {
        return feFuncA;
    }

    public void setFeFuncA(FilterEffectFunctionAlpha value) {
        this.feFuncA = value;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("in", in);
        super.toStringDetail(builder);
        builder.add("style", style);
        builder.add("className", className);
        ISvgPresentationAttributes.super.toStringDetail(builder);
        ISvgGraphicsAttributes.super.toStringDetail(builder);
        ISvgTextAttributes.super.toStringDetail(builder);
        ISvgFilterPrimitive.super.toStringDetail(builder);
        builder.add("feFuncR", feFuncR);
        builder.add("feFuncG", feFuncG);
        builder.add("feFuncB", feFuncB);
        builder.add("feFuncA", feFuncA);
    }

}
