package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.geometry.Point2D;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.SvgElementIndex;
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
public class SvgLinearGradient extends AbstractSvgStylable implements ISvgGradientElement, ISvgExternalResources {

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

    /**
     * The initial values run the gradient left to right across the target: x1 and y1 at the origin, x2 at the far edge and y2 level with the start.
     * <p>
     * Any of the four coordinates this element does not specify, and its stops if it declares none, are taken from its {@code xlink:href} chain (#18) - stopping at the first
     * element that is not itself a {@code linearGradient}, since geometry is not one of the attributes a cross-type reference inherits.
     */
    @Override
    public Paint createPaint(SvgElementIndex index) {
        List<Stop> stops = getEffectiveGradientStops(index);
        if (stops.size() < 2) {
            return getDegeneratePaint(stops);
        }
        double x1 = ISvgGradientElement.coordinate(effective(index, SvgLinearGradient::getX1), 0.0);
        double y1 = ISvgGradientElement.coordinate(effective(index, SvgLinearGradient::getY1), 0.0);
        double x2 = ISvgGradientElement.coordinate(effective(index, SvgLinearGradient::getX2), 1.0);
        double y2 = ISvgGradientElement.coordinate(effective(index, SvgLinearGradient::getY2), 0.0);

        String transformText = getEffectiveGradientTransform(index);
        GradientTransform transform = GradientTransform.parse(transformText);
        if (transform != null) {
            if (transform.preservesLinearAxis(x1, y1, x2, y2)) {
                // the axis carries the whole gradient: transforming its endpoints is the entire transformation
                Point2D start = transform.apply(x1, y1);
                Point2D end = transform.apply(x2, y2);
                x1 = start.getX();
                y1 = start.getY();
                x2 = end.getX();
                y2 = end.getY();
            } else {
                GradientTransform.reportUnrepresentable(getId(), transformText);
            }
        }
        return new LinearGradient(x1, y1, x2, y2, isEffectivelyProportional(index), getEffectiveCycleMethod(index), stops);
    }

    /**
     * The {@code xlink:href} chain starting at this element, bound to {@code SvgLinearGradient} rather than the shared {@link ISvgGradientElement} interface - unlike stops and the
     * common attributes, geometry is not inherited across a change of gradient type, and {@link SvgElementIndex#resolveChain} already stops a chain the moment the referenced
     * element is not an instance of the bound type.
     */
    private List<SvgLinearGradient> hrefChain(SvgElementIndex index) {
        return index == null ? List.of(this) : index.resolveChain(this, SvgLinearGradient::getXlinkHref, SvgLinearGradient.class);
    }

    /**
     * The first non-null value of a linear-gradient-specific attribute, walking the (same-type-only) {@code
     * xlink:href} chain.
     */
    private <T> T effective(SvgElementIndex index, Function<SvgLinearGradient, T> getter) {
        for (SvgLinearGradient current : hrefChain(index)) {
            T value = getter.apply(current);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

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
        ISvgGradientElement.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
    }

}
