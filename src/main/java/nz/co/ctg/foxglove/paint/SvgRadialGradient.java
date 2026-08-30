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

import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

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
@XmlRootElement(name = "radialGradient")
public class SvgRadialGradient extends AbstractSvgStylable implements ISvgGradientElement, ISvgExternalResources, ISvgLinkable {

    @XmlAttribute(name = "cx")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String cx;

    @XmlAttribute(name = "cy")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String cy;

    @XmlAttribute(name = "r")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String r;

    @XmlAttribute(name = "fx")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fx;

    @XmlAttribute(name = "fy")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fy;

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

    /**
     * The initial values centre the gradient on the target and give it a radius half its width, with the focal point
     * at the centre.
     * <p>
     * SVG places the focal point in cartesian coordinates, while JavaFX takes an angle and a distance as a fraction
     * of the radius, so the offset from the centre is converted to polar form. A radius of zero paints the last stop
     * flat, per the specification.
     */
    @Override
    public Paint createPaint() {
        List<Stop> stops = getGradientStops();
        if (stops.size() < 2) {
            return getDegeneratePaint(stops);
        }
        double centreX = ISvgGradientElement.coordinate(cx, 0.5);
        double centreY = ISvgGradientElement.coordinate(cy, 0.5);
        double radius = ISvgGradientElement.coordinate(r, 0.5);
        if (radius <= 0.0) {
            return stops.get(stops.size() - 1).getColor();
        }
        double focusX = ISvgGradientElement.coordinate(fx, centreX);
        double focusY = ISvgGradientElement.coordinate(fy, centreY);
        double offsetX = focusX - centreX;
        double offsetY = focusY - centreY;
        double focusDistance = Math.clamp(Math.hypot(offsetX, offsetY) / radius, 0.0, 1.0);
        double focusAngle = Math.toDegrees(Math.atan2(offsetY, offsetX));
        return new RadialGradient(focusAngle, focusDistance, centreX, centreY, radius,
            isProportional(), getCycleMethod(), stops);
    }

    public String getCx() {
        return cx;
    }

    public void setCx(String value) {
        this.cx = value;
    }

    public String getCy() {
        return cy;
    }

    public void setCy(String value) {
        this.cy = value;
    }

    public String getR() {
        return r;
    }

    public void setR(String value) {
        this.r = value;
    }

    public String getFx() {
        return fx;
    }

    public void setFx(String value) {
        this.fx = value;
    }

    public String getFy() {
        return fy;
    }

    public void setFy(String value) {
        this.fy = value;
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
        builder.add("cx", cx);
        builder.add("cy", cy);
        builder.add("r", r);
        builder.add("fx", fx);
        builder.add("fy", fy);
        builder.add("gradientUnits", gradientUnits);
        builder.add("gradientTransform", gradientTransform);
        builder.add("spreadMethod", spreadMethod);
        super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
    }

}
