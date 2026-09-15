package nz.co.ctg.foxglove.text;

import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlValueExtension;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.ISvgValueElement;
import nz.co.ctg.foxglove.adapter.DoubleListAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "altGlyph")
public class SvgAltGlyph extends AbstractSvgStylable implements ISvgTextPositioningElement, ISvgConditionalFeatures, ISvgLinkable, ISvgExternalResources, ISvgEventListener, ISvgValueElement, ISvgGlyphPositioned {

    @XmlAttribute(name = "x")
    @XmlJavaTypeAdapter(DoubleListAdapter.class)
    private List<Double> x;

    @XmlAttribute(name = "y")
    @XmlJavaTypeAdapter(DoubleListAdapter.class)
    private List<Double> y;

    @XmlAttribute(name = "dx")
    @XmlJavaTypeAdapter(DoubleListAdapter.class)
    private List<Double> dx;

    @XmlAttribute(name = "dy")
    @XmlJavaTypeAdapter(DoubleListAdapter.class)
    private List<Double> dy;

    @XmlAttribute(name = "glyphRef")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String glyphRef;

    @XmlAttribute(name = "format")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String format;

    @XmlAttribute(name = "rotate")
    @XmlJavaTypeAdapter(DoubleListAdapter.class)
    private List<Double> rotate;

    @XmlValue
    @XmlValueExtension
    private String value;

    @Override
    public List<Double> getX() {
        return x == null ? List.of() : x;
    }

    public void setX(List<Double> value) {
        this.x = value;
    }

    @Override
    public List<Double> getY() {
        return y == null ? List.of() : y;
    }

    public void setY(List<Double> value) {
        this.y = value;
    }

    @Override
    public List<Double> getDx() {
        return dx == null ? List.of() : dx;
    }

    public void setDx(List<Double> value) {
        this.dx = value;
    }

    @Override
    public List<Double> getDy() {
        return dy == null ? List.of() : dy;
    }

    public void setDy(List<Double> value) {
        this.dy = value;
    }

    public String getGlyphRef() {
        return glyphRef;
    }

    public void setGlyphRef(String value) {
        this.glyphRef = value;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String value) {
        this.format = value;
    }

    @Override
    public List<Double> getRotate() {
        return rotate == null ? List.of() : rotate;
    }

    public void setRotate(List<Double> value) {
        this.rotate = value;
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
        builder.add("dx", dx);
        builder.add("dy", dy);
        builder.add("glyphRef", glyphRef);
        builder.add("format", format);
        builder.add("rotate", rotate);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
    }

}
