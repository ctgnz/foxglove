package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.adapter.DoubleListAdapter;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "text", propOrder = {
    "text", "content"
})
@XmlRootElement(name = "text")
public class SvgText extends AbstractSvgTextContentElement implements ISvgTextPositioningElement, ISvgTransformable, FxGraphic<Node>, ISvgGlyphPositioned {

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

    @XmlMixed
    @XmlElementRefs({
        @XmlElementRef(name = "tspan", type = SvgTextSpan.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "tref", type = SvgTextReference.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "textPath", type = SvgTextPath.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "altGlyph", type = SvgAltGlyph.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "animateMotion", type = SvgAnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<Object> content;

    @Override
    public Node createGraphic(RenderContext context) {
        return TextGlyphLayout.layout(this, context);
    }

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

    @Override
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return content;
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
        builder.add("value", getValue());
    }

}
