//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2021.03.25 at 03:40:09 PM NZDT
//


package nz.co.ctg.foxglove.shape;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects.ToStringHelper;

import javafx.scene.shape.Circle;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "circle")
@XmlRootElement(name = "circle")
public class SvgCircle extends AbstractSvgShape implements ISvgShape<Circle> {

    @XmlAttribute(name = "cx")
    protected double centreX;

    @XmlAttribute(name = "cy")
    protected double centreY;

    @XmlAttribute(name = "r", required = true)
    protected double radius;

    @Override
    public Circle createShape() {
        parseStyle();
        Circle circle = new Circle(centreX, centreY, radius);
        setColors(circle);
        setStrokeProperties(circle);
        setTransforms(circle);
        return circle;
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
     * Gets the value of the r property.
     *
     * @return
     *     possible object is
     *     {@link double }
     *
     */
    public double getRadius() {
        return radius;
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
     * Sets the value of the r property.
     *
     * @param value
     *     allowed object is
     *     {@link double }
     *
     */
    public void setRadius(double value) {
        this.radius = value;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("cx", centreX);
        builder.add("cy", centreY);
        builder.add("r", radius);
        super.toStringDetail(builder);
    }

}