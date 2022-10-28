package nz.co.ctg.foxglove.shape;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.MoreObjects.ToStringHelper;

import javafx.scene.shape.SVGPath;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "path")
@XmlRootElement(name = "path")
public class SvgPath extends AbstractSvgShape implements ISvgShape<SVGPath> {

    @XmlAttribute(name = "d", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String pathData;

    @XmlAttribute(name = "pathLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String pathLength;

    @Override
    public SVGPath createShape() {
        parseStyle();
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(pathData);
        setColors(svgPath);
        setStrokeProperties(svgPath);
        setTransforms(svgPath);
        return svgPath;
    }

    /**
     * Gets the value of the d property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getD() {
        return pathData;
    }

    /**
     * Gets the value of the pathLength property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPathLength() {
        return pathLength;
    }

    /**
     * Sets the value of the d property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setD(String value) {
        this.pathData = value;
    }

    /**
     * Sets the value of the pathLength property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPathLength(String value) {
        this.pathLength = value;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("pathLength", pathLength);
        super.toStringDetail(builder);
        builder.add("d", pathData);
    }

}
