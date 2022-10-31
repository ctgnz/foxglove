package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

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
@XmlRootElement(name = "feDisplacementMap")
public class FeDisplacementMap extends AbstractSvgStylable implements ISvgFilterPrimitive {

    @XmlAttribute(name = "in")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in;

    @XmlAttribute(name = "in2", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String in2;

    @XmlAttribute(name = "scale")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String scale;

    @XmlAttribute(name = "xChannelSelector")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String xChannelSelector;

    @XmlAttribute(name = "yChannelSelector")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String yChannelSelector;

    @XmlPath(".")
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

    public String getIn2() {
        return in2;
    }

    public void setIn2(String value) {
        this.in2 = value;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String value) {
        this.scale = value;
    }

    public String getXChannelSelector() {
        if (xChannelSelector == null) {
            return "A";
        } else {
            return xChannelSelector;
        }
    }

    public void setXChannelSelector(String value) {
        this.xChannelSelector = value;
    }

    public String getYChannelSelector() {
        if (yChannelSelector == null) {
            return "A";
        } else {
            return yChannelSelector;
        }
    }

    public void setYChannelSelector(String value) {
        this.yChannelSelector = value;
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
        builder.add("in2", in2);
        builder.add("scale", scale);
        builder.add("xChannelSelector", xChannelSelector);
        builder.add("yChannelSelector", yChannelSelector);
        super.toStringDetail(builder);
        filter.toStringDetail(builder);
    }

}
