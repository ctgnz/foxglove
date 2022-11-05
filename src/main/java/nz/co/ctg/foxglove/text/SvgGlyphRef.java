package nz.co.ctg.foxglove.text;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgLinkable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "linkable"
})
@XmlRootElement(name = "glyphRef")
public class SvgGlyphRef extends AbstractSvgStylable implements ISvgGlyphItem, ISvgLinkable {

    @XmlAttribute(name = "x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String x;

    @XmlAttribute(name = "y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String y;

    @XmlAttribute(name = "dx")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String dx;

    @XmlAttribute(name = "dy")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String dy;

    @XmlAttribute(name = "glyphRef")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String glyphRef;

    @XmlAttribute(name = "format")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String format;

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

    public String getDx() {
        return dx;
    }

    public void setDx(String value) {
        this.dx = value;
    }

    public String getDy() {
        return dy;
    }

    public void setDy(String value) {
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
    public void toStringDetail(ToStringHelper builder) {
        builder.add("x", x);
        builder.add("y", y);
        builder.add("dx", dx);
        builder.add("dy", dy);
        builder.add("glyphRef", glyphRef);
        builder.add("format", format);
        super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
    }

}
