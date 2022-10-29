package nz.co.ctg.foxglove.shape;

import java.util.List;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.parser.PointListAdapter;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polyline;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "polyline")
@XmlRootElement(name = "polyline")
public class SvgPolyline extends AbstractSvgShape implements ISvgShape<Polyline> {

    @XmlAttribute(name = "points", required = true)
    @XmlJavaTypeAdapter(PointListAdapter.class)
    protected List<Point2D> points;

    @Override
    public Polyline createShape() {
        parseStyle();
        Polyline polyline = new Polyline(getPointList());
        setColors(polyline);
        setStrokeProperties(polyline);
        setTransforms(polyline);
        return polyline;
    }

    /**
     * Gets the value of the points property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public List<Point2D> getPoints() {
        return points;
    }

    /**
     * Sets the value of the points property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPoints(List<Point2D> value) {
        this.points = value;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        builder.add("points", points);
    }

    private double[] getPointList() {
        return points.stream().flatMap(pt -> Stream.of(pt.getX(), pt.getY())).mapToDouble(Double::doubleValue).toArray();
    }

}
