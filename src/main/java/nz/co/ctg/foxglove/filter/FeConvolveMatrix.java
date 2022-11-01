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
    "filter", "animations"
})
@XmlRootElement(name = "feConvolveMatrix")
public class FeConvolveMatrix extends AbstractSvgStylable implements ISvgFilterPrimitive {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in;

    @XmlAttribute(name = "order")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String order;

    @XmlAttribute(name = "kernelMatrix", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String kernelMatrix;

    @XmlAttribute(name = "divisor")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String divisor;

    @XmlAttribute(name = "bias")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String bias;

    @XmlAttribute(name = "targetX")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String targetX;

    @XmlAttribute(name = "targetY")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String targetY;

    @XmlAttribute(name = "edgeMode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String edgeMode;

    @XmlAttribute(name = "kernelUnitLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String kernelUnitLength;

    @XmlAttribute(name = "preserveAlpha")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String preserveAlpha;

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
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgAnimationElement> animations;

    public String getIn() {
        return in;
    }

    public void setIn(String value) {
        this.in = value;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String value) {
        this.order = value;
    }

    public String getKernelMatrix() {
        return kernelMatrix;
    }

    public void setKernelMatrix(String value) {
        this.kernelMatrix = value;
    }

    public String getDivisor() {
        return divisor;
    }

    public void setDivisor(String value) {
        this.divisor = value;
    }

    public String getBias() {
        return bias;
    }

    public void setBias(String value) {
        this.bias = value;
    }

    public String getTargetX() {
        return targetX;
    }

    public void setTargetX(String value) {
        this.targetX = value;
    }

    public String getTargetY() {
        return targetY;
    }

    public void setTargetY(String value) {
        this.targetY = value;
    }

    public String getEdgeMode() {
        if (edgeMode == null) {
            return "duplicate";
        } else {
            return edgeMode;
        }
    }

    public void setEdgeMode(String value) {
        this.edgeMode = value;
    }

    public String getKernelUnitLength() {
        return kernelUnitLength;
    }

    public void setKernelUnitLength(String value) {
        this.kernelUnitLength = value;
    }

    public String getPreserveAlpha() {
        return preserveAlpha;
    }

    public void setPreserveAlpha(String value) {
        this.preserveAlpha = value;
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
        builder.add("order", order);
        builder.add("kernelMatrix", kernelMatrix);
        builder.add("divisor", divisor);
        builder.add("bias", bias);
        builder.add("targetX", targetX);
        builder.add("targetY", targetY);
        builder.add("edgeMode", edgeMode);
        builder.add("kernelUnitLength", kernelUnitLength);
        builder.add("preserveAlpha", preserveAlpha);
        super.toStringDetail(builder);
        filter.toStringDetail(builder);
    }

}
