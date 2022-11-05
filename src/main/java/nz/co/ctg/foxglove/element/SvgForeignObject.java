package nz.co.ctg.foxglove.element;

import org.eclipse.persistence.oxm.annotations.XmlValueExtension;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.ISvgValueElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "foreignObject")
public class SvgForeignObject extends AbstractSvgStylable implements ISvgStructuralElement, ISvgEventListener, ISvgConditionalFeatures, ISvgExternalResources, ISvgTransformable, ISvgValueElement {

    @XmlAttribute(name = "x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String x;

    @XmlAttribute(name = "y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String y;

    @XmlAttribute(name = "width", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String width;

    @XmlAttribute(name = "height", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String height;

    @XmlValueExtension
    private String value;

    public String getX() {
        return x;
    }

    public void setX(String value) {
        this.x = value;
    }

    public String getY() {
        return y;
    }

    public void setY(String value) {
        this.y = value;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String value) {
        this.width = value;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String value) {
        this.height = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("x", x);
        builder.add("y", y);
        builder.add("width", width);
        builder.add("height", height);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgTransformable.super.toStringDetail(builder);
    }

}
