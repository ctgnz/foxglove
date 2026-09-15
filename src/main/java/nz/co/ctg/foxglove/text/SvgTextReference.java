package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.adapter.DoubleListAdapter;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
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
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "tref")
public class SvgTextReference extends AbstractSvgStylable implements ISvgTextPositioningElement, ISvgConditionalFeatures, ISvgLinkable, ISvgExternalResources, ISvgEventListener, ISvgGlyphPositioned {

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

    @XmlAttribute(name = "rotate")
    @XmlJavaTypeAdapter(DoubleListAdapter.class)
    private List<Double> rotate;

    @XmlAttribute(name = "textLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String textLength;

    @XmlAttribute(name = "lengthAdjust")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String lengthAdjust;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

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

    @Override
    public List<Double> getRotate() {
        return rotate == null ? List.of() : rotate;
    }

    public void setRotate(List<Double> value) {
        this.rotate = value;
    }

    public String getTextLength() {
        return textLength;
    }

    public void setTextLength(String value) {
        this.textLength = value;
    }

    public String getLengthAdjust() {
        return lengthAdjust;
    }

    public void setLengthAdjust(String value) {
        this.lengthAdjust = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("x", x);
        builder.add("y", y);
        builder.add("dx", dx);
        builder.add("dy", dy);
        builder.add("rotate", rotate);
        builder.add("textLength", textLength);
        builder.add("lengthAdjust", lengthAdjust);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
    }

}
