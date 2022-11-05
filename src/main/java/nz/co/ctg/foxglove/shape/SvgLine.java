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
import javafx.scene.shape.Line;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "line", propOrder = {
    "content"
})
@XmlRootElement(name = "line")
public class SvgLine extends AbstractSvgStylable implements ISvgShape<Line>, ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgTransformable {

    @XmlAttribute(name = "x1")
    private double startX;

    @XmlAttribute(name = "y1")
    private double startY;

    @XmlAttribute(name = "x2")
    private double endX;

    @XmlAttribute(name = "y2")
    private double endY;

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
    public Line createShape() {
        parseStyle();
        Line line = new Line(startX, startY, endX, endY);
        applyGraphicsProperties(line);
        applyTransforms(line);
        return line;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setEndX(double value) {
        this.endX = value;
    }

    public void setEndY(double value) {
        this.endY = value;
    }

    public void setStartX(double value) {
        this.startX = value;
    }

    public void setStartY(double value) {
        this.startY = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("x1", startX);
        builder.add("y1", startY);
        builder.add("x2", endX);
        builder.add("y2", endY);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgTransformable.super.toStringDetail(builder);
    }

}
