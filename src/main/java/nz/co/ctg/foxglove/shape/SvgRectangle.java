package nz.co.ctg.foxglove.shape;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgTransformable;
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
import javafx.scene.shape.Rectangle;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rect", propOrder = {
    "content"
})
@XmlRootElement(name = "rect")
public class SvgRectangle extends AbstractSvgStylable implements ISvgShape<Rectangle>, ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgTransformable {

    @XmlAttribute(name = "x")
    private double x;

    @XmlAttribute(name = "y")
    private double y;

    @XmlAttribute(name = "width", required = true)
    private double width;

    @XmlAttribute(name = "height", required = true)
    private double height;

    @XmlAttribute(name = "rx")
    private double radiusX;

    @XmlAttribute(name = "ry")
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

    public SvgRectangle() {
    }

    @Override
    public Rectangle createShape() {
        parseStyle();
        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setArcWidth(radiusX);
        rect.setArcHeight(radiusY);
        applyGraphicsProperties(rect);
        applyTransforms(rect);
        return rect;
    }

    public double getX() {
        return x;
    }

    public void setX(double value) {
        this.x = value;
    }

    public double getY() {
        return y;
    }

    public void setY(double value) {
        this.y = value;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double value) {
        this.width = value;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double value) {
        this.height = value;
    }

    public double getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(double value) {
        this.radiusX = value;
    }

    public double getRadiusY() {
        return radiusY;
    }

    public void setRadiusY(double value) {
        this.radiusY = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("x", x);
        builder.add("y", y);
        builder.add("width", width);
        builder.add("height", height);
        builder.add("rx", radiusX);
        builder.add("ry", radiusY);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgTransformable.super.toStringDetail(builder);
    }

}
