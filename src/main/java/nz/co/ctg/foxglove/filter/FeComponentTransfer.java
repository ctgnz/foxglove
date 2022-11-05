package nz.co.ctg.foxglove.filter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;

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
public class FeComponentTransfer extends AbstractSvgStylable implements ISvgFilterPrimitive {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in;

    @XmlElement(name = "feFuncR")
    private FeFunctionRed feFuncR;

    @XmlElement(name = "feFuncG")
    private FeFunctionGreen feFuncG;

    @XmlElement(name = "feFuncB")
    private FeFunctionBlue feFuncB;

    @XmlElement(name = "feFuncA")
    private FeFunctionAlpha feFuncA;

    public String getIn() {
        return in;
    }

    public void setIn(String value) {
        this.in = value;
    }

    public FeFunctionRed getFeFuncR() {
        return feFuncR;
    }

    public void setFeFuncR(FeFunctionRed value) {
        this.feFuncR = value;
    }

    public FeFunctionGreen getFeFuncG() {
        return feFuncG;
    }

    public void setFeFuncG(FeFunctionGreen value) {
        this.feFuncG = value;
    }

    public FeFunctionBlue getFeFuncB() {
        return feFuncB;
    }

    public void setFeFuncB(FeFunctionBlue value) {
        this.feFuncB = value;
    }

    public FeFunctionAlpha getFeFuncA() {
        return feFuncA;
    }

    public void setFeFuncA(FeFunctionAlpha value) {
        this.feFuncA = value;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("in", in);
        super.toStringDetail(builder);
        ISvgFilterPrimitive.super.toStringDetail(builder);
        builder.add("feFuncR", feFuncR);
        builder.add("feFuncG", feFuncG);
        builder.add("feFuncB", feFuncB);
        builder.add("feFuncA", feFuncA);
    }

}
