package nz.co.ctg.foxglove.shape;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
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
import javafx.scene.shape.Ellipse;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ellipse", propOrder = {
    "content"
})
@XmlRootElement(name = "ellipse")
public class SvgEllipse extends AbstractSvgShape<Ellipse> {

    @XmlAttribute(name = "cx")
    private double centreX;

    @XmlAttribute(name = "cy")
    private double centreY;

    @XmlAttribute(name = "rx", required = true)
    private double radiusX;

    @XmlAttribute(name = "ry", required = true)
    private double radiusY;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"), @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateMotion", type = SvgAnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    @Override
    protected Ellipse createShape() {
        return new Ellipse(centreX, centreY, radiusX, radiusY);
    }

    public double getCentreX() {
        return centreX;
    }

    public double getCentreY() {
        return centreY;
    }

    public double getRadiusX() {
        return radiusX;
    }

    public double getRadiusY() {
        return radiusY;
    }

    public void setCentreX(double value) {
        this.centreX = value;
    }

    public void setCentreY(double value) {
        this.centreY = value;
    }

    public void setRadiusX(double value) {
        this.radiusX = value;
    }

    public void setRadiusY(double value) {
        this.radiusY = value;
    }

    @Override
    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("cx", centreX);
        builder.add("cy", centreY);
        builder.add("rx", radiusX);
        builder.add("ry", radiusY);
        super.toStringDetail(builder);
    }

}
