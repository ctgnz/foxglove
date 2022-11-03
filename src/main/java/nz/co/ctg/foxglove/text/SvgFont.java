package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.ISvgPresentationAttributes;
import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.ISvgTextAttributes;
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
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "font")
public class SvgFont extends AbstractSvgElement implements ISvgStylable, ISvgPresentationAttributes, ISvgGraphicsAttributes, ISvgTextAttributes, ISvgExternalResources {

    @XmlAttribute(name = "horiz-origin-x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String horizOriginX;

    @XmlAttribute(name = "horiz-origin-y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String horizOriginY;

    @XmlAttribute(name = "horiz-adv-x", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String horizAdvX;

    @XmlAttribute(name = "vert-origin-x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String vertOriginX;

    @XmlAttribute(name = "vert-origin-y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String vertOriginY;

    @XmlAttribute(name = "vert-adv-y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String vertAdvY;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "font-face", type = SvgFontFace.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "missing-glyph", type = SvgMissingGlyph.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "glyph", type = SvgGlyph.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "hkern", type = SvgHorizontalKerning.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "vkern", type = SvgVerticalKerning.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    @XmlAttribute(name = "style")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String style;

    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String className;

    public String getHorizOriginX() {
        return horizOriginX;
    }

    public void setHorizOriginX(String value) {
        this.horizOriginX = value;
    }

    public String getHorizOriginY() {
        return horizOriginY;
    }

    public void setHorizOriginY(String value) {
        this.horizOriginY = value;
    }

    public String getHorizAdvX() {
        return horizAdvX;
    }

    public void setHorizAdvX(String value) {
        this.horizAdvX = value;
    }

    public String getVertOriginX() {
        return vertOriginX;
    }

    public void setVertOriginX(String value) {
        this.vertOriginX = value;
    }

    public String getVertOriginY() {
        return vertOriginY;
    }

    public void setVertOriginY(String value) {
        this.vertOriginY = value;
    }

    public String getVertAdvY() {
        return vertAdvY;
    }

    public void setVertAdvY(String value) {
        this.vertAdvY = value;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String value) {
        this.style = value;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String value) {
        this.className = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("horizOriginX", horizOriginX);
        builder.add("horizOriginY", horizOriginY);
        builder.add("horizAdvX", horizAdvX);
        builder.add("vertOriginX", vertOriginX);
        builder.add("vertOriginY", vertOriginY);
        builder.add("vertAdvY", vertAdvY);
        super.toStringDetail(builder);
        builder.add("style", style);
        builder.add("className", className);
        ISvgPresentationAttributes.super.toStringDetail(builder);
        ISvgGraphicsAttributes.super.toStringDetail(builder);
        ISvgTextAttributes.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
    }

}
