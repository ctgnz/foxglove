package nz.co.ctg.foxglove.shape;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects.ToStringHelper;

import javafx.scene.shape.Rectangle;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rect")
@XmlRootElement(name = "rect")
public class SvgRectangle extends AbstractSvgShape implements ISvgShape<Rectangle> {

    private @XmlAttribute(name = "x") double x;
    private @XmlAttribute(name = "y") double y;
    private @XmlAttribute(name = "width", required = true) double width;
    private @XmlAttribute(name = "height", required = true) double height;
    private @XmlAttribute(name = "rx") double radiusX;
    private @XmlAttribute(name = "ry") double radiusY;

    public SvgRectangle() {
    }

    @Override
    public Rectangle createShape() {
        parseStyle();
        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setArcWidth(radiusX);
        rect.setArcHeight(radiusY);
        setColors(rect);
        setStrokeProperties(rect);
        setTransforms(rect);
        return rect;
    }

    /**
     * Gets the value of the x property.
     *
     * @return
     *     possible object is
     *     {@link double }
     *
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     *
     * @param value
     *     allowed object is
     *     {@link double }
     *
     */
    public void setX(double value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     *
     * @return
     *     possible object is
     *     {@link double }
     *
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     *
     * @param value
     *     allowed object is
     *     {@link double }
     *
     */
    public void setY(double value) {
        this.y = value;
    }

    /**
     * Gets the value of the width property.
     *
     * @return
     *     possible object is
     *     {@link double }
     *
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     *
     * @param value
     *     allowed object is
     *     {@link double }
     *
     */
    public void setWidth(double value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     *
     * @return
     *     possible object is
     *     {@link double }
     *
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     *
     * @param value
     *     allowed object is
     *     {@link double }
     *
     */
    public void setHeight(double value) {
        this.height = value;
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
        builder.add("x", x);
        builder.add("y", y);
        builder.add("width", width);
        builder.add("height", height);
        builder.add("rx", radiusX);
        builder.add("ry", radiusY);
        super.toStringDetail(builder);
    }

}
