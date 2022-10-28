package nz.co.ctg.foxglove.shape;

import java.util.List;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.parser.PointListAdapter;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "polygon")
@XmlRootElement(name = "polygon")
public class SvgPolygon extends AbstractSvgShape implements ISvgShape<Polygon> {

    @XmlAttribute(name = "points", required = true)
    @XmlJavaTypeAdapter(PointListAdapter.class)
    protected List<Point2D> points;

    @Override
    public Polygon createShape() {
        parseStyle();
        Polygon polygon = new Polygon(getPointList());
        setColors(polygon);
        setStrokeProperties(polygon);
        setTransforms(polygon);
        return polygon;
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
