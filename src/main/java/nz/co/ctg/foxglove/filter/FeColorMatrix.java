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
@XmlRootElement(name = "feColorMatrix")
public class FeColorMatrix extends AbstractSvgStylable implements ISvgFilterPrimitive {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in;

    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String type;

    @XmlAttribute(name = "values")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String values;

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

    public String getType() {
        if (type == null) {
            return "matrix";
        } else {
            return type;
        }
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String value) {
        this.values = value;
    }

    @Override
    public SvgFilterAttributes getFilterAttributes() {
        return filter;
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
        builder.add("type", type);
        builder.add("values", values);
        super.toStringDetail(builder);
        filter.toStringDetail(builder);
    }

}
