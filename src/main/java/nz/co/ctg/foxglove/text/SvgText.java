package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgTransformable;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.scene.text.Text;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "text", propOrder = {
    "text", "content"
})
@XmlRootElement(name = "text")
public class SvgText extends AbstractSvgStylable implements ISvgTextPositioningElement, ISvgTransformable {

    @XmlAttribute(name = "x")
    private double x;

    @XmlAttribute(name = "y")
    private double y;

    @XmlAttribute(name = "dx")
    private double dx;

    @XmlAttribute(name = "dy")
    private double dy;

    @XmlAttribute(name = "rotate")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String rotate;

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
        @XmlElementRef(name = "altGlyph", type = SvgAltGlyph.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<Object> content;

    public Text createGraphic() {
        parseStyle();
        Text svgText = createShape();
        applyGraphicsProperties(svgText);
        applyTextProperties(svgText);
        applyTransforms(svgText);
        return svgText;
    }

    protected Text createShape() {
        return new Text(x, y, getTextValue());
    }

    public double getX() {
        return x;
    }

    public void setX(double value) {
        this.x = value;
    }

    public double getY() {
        return y;
    }

    public void setY(double value) {
        this.y = value;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double value) {
        this.dx = value;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double value) {
        this.dy = value;
    }

    public String getRotate() {
        return rotate;
    }

    public void setRotate(String value) {
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

    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return content;
    }

    @XmlTransient
    public List<ISvgTextContentElement> getTextContent() {
        return streamTextContent().collect(toList());
    }

    @XmlTransient
    public String getTextValue() {
        if (getContent().stream().allMatch(String.class::isInstance)) {
            return getContent().stream().map(String.class::cast).collect(joining(" "));
        }
        return streamTextContent().findFirst().map(ISvgTextContentElement::getValue).orElse(null);
    }

    @Override
    @XmlTransient
    public String getValue() {
        return getTextValue();
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
        builder.add("value", getTextValue());
    }

    private Stream<ISvgTextContentElement> streamTextContent() {
        return getContent().stream()
            .filter(ISvgTextContentElement.class::isInstance)
            .map(ISvgTextContentElement.class::cast);
    }

}
