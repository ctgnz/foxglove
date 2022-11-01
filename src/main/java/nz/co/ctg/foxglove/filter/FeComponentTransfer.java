package nz.co.ctg.foxglove.filter;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

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
    "filter", "feFuncR", "feFuncG", "feFuncB", "feFuncA"
})
@XmlRootElement(name = "feComponentTransfer")
public class FeComponentTransfer extends AbstractSvgStylable implements ISvgFilterPrimitive {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in;

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

    @XmlElement(name = "feFuncR")
    private FilterEffectFunctionRed feFuncR;

    @XmlElement(name = "feFuncG")
    private FilterEffectFunctionGreen feFuncG;

    @XmlElement(name = "feFuncB")
    private FilterEffectFunctionBlue feFuncB;

    @XmlElement(name = "feFuncA")
    private FilterEffectFunctionAlpha feFuncA;

    public String getIn() {
        return in;
    }

    public void setIn(String value) {
        this.in = value;
    }

    @Override
    public SvgFilterAttributes getFilterAttributes() {
        return filter;
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
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("in", in);
        super.toStringDetail(builder);
        filter.toStringDetail(builder);
        builder.add("feFuncR", feFuncR);
        builder.add("feFuncG", feFuncG);
        builder.add("feFuncB", feFuncB);
        builder.add("feFuncA", feFuncA);
    }

}
