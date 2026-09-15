package nz.co.ctg.foxglove.shape;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Rectangle;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgBounded;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.RenderContext;
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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rect", propOrder = {
    "content"
})
@XmlRootElement(name = "rect")
public final class SvgRectangle extends AbstractSvgShape<Rectangle> implements ISvgBounded {

    /**
     * Boxed, not a primitive {@code double} - {@code null} (attribute absent) must be distinguishable from an explicit {@code 0}, since SVG's own defaulting rule ("if only one of
     * {@code rx}/{@code ry} is specified, the other defaults to the same value") depends on knowing which case this is, not just what the numeric value is.
     */
    @XmlAttribute(name = "rx")
    private Double radiusX;

    @XmlAttribute(name = "ry")
    private Double radiusY;

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

    public SvgRectangle(double x, double y, double width, double height) {
        setPixelsX(x);
        setPixelsY(y);
        setPixelsWidth(width);
        setPixelsHeight(height);
    }

    @Override
    protected Rectangle createShape(RenderContext context) {
        double width = resolveWidth(context);
        double height = resolveHeight(context);
        Rectangle rect = new Rectangle(resolveX(context), resolveY(context), width, height);

        // SVG's own rx/ry resolution (shapes.html#RectElement): whichever of rx/ry is omitted defaults to the
        // other's value (not 0); either specified value exceeding half its own dimension is clamped to that half.
        double effectiveRx = radiusX != null ? radiusX : (radiusY != null ? radiusY : 0);
        double effectiveRy = radiusY != null ? radiusY : (radiusX != null ? radiusX : 0);
        effectiveRx = Math.min(effectiveRx, width / 2);
        effectiveRy = Math.min(effectiveRy, height / 2);

        // JavaFX's arcWidth/arcHeight are the full width/height of the corner-rounding ellipse (a diameter,
        // matching AWT's RoundRectangle2D convention) - SVG's rx/ry are radii, so this doubles rather than passing
        // them straight through.
        rect.setArcWidth(2 * effectiveRx);
        rect.setArcHeight(2 * effectiveRy);
        return rect;
    }

    public Double getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(Double value) {
        this.radiusX = value;
    }

    public Double getRadiusY() {
        return radiusY;
    }

    public void setRadiusY(Double value) {
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
        ISvgBounded.super.toStringDetail(builder);
        builder.add("rx", radiusX);
        builder.add("ry", radiusY);
        super.toStringDetail(builder);
    }

}
