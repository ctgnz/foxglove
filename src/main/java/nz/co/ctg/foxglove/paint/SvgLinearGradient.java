package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

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
    "content"
})
@XmlRootElement(name = "linearGradient")
public class SvgLinearGradient extends AbstractSvgStylable implements ISvgGradientElement, ISvgExternalResources, ISvgLinkable {

    @XmlAttribute(name = "x1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String x1;

    @XmlAttribute(name = "y1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String y1;

    @XmlAttribute(name = "x2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String x2;

    @XmlAttribute(name = "y2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String y2;

    @XmlAttribute(name = "gradientUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String gradientUnits;

    @XmlAttribute(name = "gradientTransform")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String gradientTransform;

    @XmlAttribute(name = "spreadMethod")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String spreadMethod;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "stop", type = SvgStop.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    public String getX1() {
        return x1;
    }

    public void setX1(String value) {
        this.x1 = value;
    }

    public String getY1() {
        return y1;
    }

    public void setY1(String value) {
        this.y1 = value;
    }

    public String getX2() {
        return x2;
    }

    public void setX2(String value) {
        this.x2 = value;
    }

    public String getY2() {
        return y2;
    }

    public void setY2(String value) {
        this.y2 = value;
    }

    public String getGradientUnits() {
        return gradientUnits;
    }

    public void setGradientUnits(String value) {
        this.gradientUnits = value;
    }

    public String getGradientTransform() {
        return gradientTransform;
    }

    public void setGradientTransform(String value) {
        this.gradientTransform = value;
    }

    public String getSpreadMethod() {
        return spreadMethod;
    }

    public void setSpreadMethod(String value) {
        this.spreadMethod = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("x1", x1);
        builder.add("y1", y1);
        builder.add("x2", x2);
        builder.add("y2", y2);
        builder.add("gradientUnits", gradientUnits);
        builder.add("gradientTransform", gradientTransform);
        builder.add("spreadMethod", spreadMethod);
        super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
    }

}
