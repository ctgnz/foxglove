package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.RenderContext;

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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "text", propOrder = {
    "text", "content"
})
@XmlRootElement(name = "text")
public class SvgText extends AbstractSvgTextContentElement implements ISvgTextPositioningElement, ISvgTransformable, FxGraphic<Node> {

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

    @Override
    public Node createGraphic(RenderContext context) {
        List<TextRunBuilder.Run> runs = TextRunBuilder.build(this, context);
        List<Text> nodes = new ArrayList<>();
        for (TextRunBuilder.Run run : runs) {
            Text node = new Text(run.text());
            run.owner().applyGraphicsProperties(run.ownerContext(), node);
            run.owner().applyTextProperties(run.ownerContext(), node);
            nodes.add(node);
        }
        positionRuns(nodes);
        Node result = nodes.size() == 1 ? nodes.get(0) : groupOf(nodes);
        result.setId(getId());
        applyTransforms(result);
        return result;
    }

    /**
     * Lays out each run left to right along one baseline, starting at this element's own {@code x}/{@code y} - the
     * absolute repositioning a nested run's own {@code x}/{@code y}/{@code dx}/{@code dy} would cause is #28's
     * concern, not this one.
     */
    private void positionRuns(List<Text> nodes) {
        double cursorX = x;
        for (Text node : nodes) {
            node.setX(cursorX);
            node.setY(y);
            cursorX += node.getLayoutBounds().getWidth();
        }
    }

    private static Group groupOf(List<Text> nodes) {
        Group group = new Group();
        group.getChildren().addAll(nodes);
        return group;
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
