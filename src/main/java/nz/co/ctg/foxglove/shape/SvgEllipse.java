package nz.co.ctg.foxglove.shape;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects.ToStringHelper;

import javafx.scene.shape.Ellipse;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ellipse")
@XmlRootElement(name = "ellipse")
public class SvgEllipse extends AbstractSvgShape implements ISvgShape<Ellipse> {

    @XmlAttribute(name = "cx")
    protected double centreX;

    @XmlAttribute(name = "cy")
    protected double centreY;

    @XmlAttribute(name = "rx", required = true)
    protected double radiusX;

    @XmlAttribute(name = "ry", required = true)
    protected double radiusY;

    @Override
    public Ellipse createShape() {
        parseStyle();
        Ellipse ellipse = new Ellipse(centreY, centreX, radiusX, radiusY);
        setColors(ellipse);
        setStrokeProperties(ellipse);
        setTransforms(ellipse);
        return ellipse;
    }

    /**
     * Gets the value of the cx property.
     *
     * @return
     *     possible object is
     *     {@link double }
     *
     */
    public double getCentreX() {
        return centreX;
    }

    /**
     * Gets the value of the cy property.
     *
     * @return
     *     possible object is
     *     {@link double }
     *
     */
    public double getCentreY() {
        return centreY;
    }

    /**
     * Gets the value of the rx property.
     *
     * @return
     *     possible object is
     *     {@link double }
     *
     */
    public double getRadiusX() {
        return radiusX;
    }

    /**
     * Gets the value of the ry property.
     *
     * @return
     *     possible object is
     *     {@link double }
     *
     */
    public double getRadiusY() {
        return radiusY;
    }

    /**
     * Sets the value of the cx property.
     *
     * @param value
     *     allowed object is
     *     {@link double }
     *
     */
    public void setCentreX(double value) {
        this.centreX = value;
    }

    /**
     * Sets the value of the cy property.
     *
     * @param value
     *     allowed object is
     *     {@link double }
     *
     */
    public void setCentreY(double value) {
        this.centreY = value;
    }

    /**
     * Sets the value of the rx property.
     *
     * @param value
     *     allowed object is
     *     {@link double }
     *
     */
    public void setRadiusX(double value) {
        this.radiusX = value;
    }

    /**
     * Sets the value of the ry property.
     *
     * @param value
     *     allowed object is
     *     {@link double }
     *
     */
    public void setRadiusY(double value) {
        this.radiusY = value;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("cx", centreX);
        builder.add("cy", centreY);
        builder.add("rx", radiusX);
        builder.add("ry", radiusY);
        super.toStringDetail(builder);
    }

}
