package nz.co.ctg.foxglove.text;

import java.util.List;

import org.junit.Test;

import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.element.SvgGroup;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

/**
 * Exercises #28's acceptance criteria: text-anchor, list-valued x/y/dx/dy positioning glyphs individually,
 * per-glyph rotate, text-decoration, and the baseline-attribute approximation.
 */
public class SvgTextPositioningTest {

    @Test
    public void testSingleXYStaysOnTheSingleNodeFastPath() throws Exception {
        SvgText text = new SvgText();
        text.setX(List.of(10.0));
        text.setY(List.of(20.0));
        text.getContent().add("Hello");

        Node rendered = render(text);
        assertThat(rendered, instanceOf(Text.class));
        assertThat(((Text) rendered).getX(), is(10.0));
        assertThat(((Text) rendered).getY(), is(20.0));
    }

    @Test
    public void testListValuedXPositionsGlyphsIndividually() throws Exception {
        SvgText text = new SvgText();
        text.setX(List.of(10.0, 20.0, 30.0));
        text.setY(List.of(100.0));
        text.getContent().add("abc");

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(3));
        assertThat(((Text) children.get(0)).getText(), is("a"));
        assertThat(((Text) children.get(0)).getX(), is(10.0));
        assertThat(((Text) children.get(1)).getText(), is("b"));
        assertThat(((Text) children.get(1)).getX(), is(20.0));
        assertThat(((Text) children.get(2)).getText(), is("c"));
        assertThat(((Text) children.get(2)).getX(), is(30.0));
        for (Node child : children) {
            assertThat(((Text) child).getY(), is(100.0));
        }
    }

    @Test
    public void testRotateAppliesToEachGlyphIndividuallyRepeatingTheLastValue() throws Exception {
        SvgText text = new SvgText();
        text.setRotate(List.of(10.0, 20.0));
        text.getContent().add("abc");

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(3));
        assertThat(rotationOf(children.get(0)), is(10.0));
        assertThat(rotationOf(children.get(1)), is(20.0));
        assertThat(rotationOf(children.get(2)), is(20.0));
    }

    @Test
    public void testTextAnchorMiddleCentresTheLine() throws Exception {
        SvgText start = new SvgText();
        start.setX(List.of(50.0));
        start.getContent().add("Hello");
        Text startNode = (Text) render(start);

        SvgText middle = new SvgText();
        middle.setX(List.of(50.0));
        middle.setTextAnchor("middle");
        middle.getContent().add("Hello");
        Node middleNode = render(middle);

        double width = startNode.getLayoutBounds().getWidth();
        assertThat(middleNode.getTranslateX(), closeTo(-width / 2.0, 1e-6));
    }

    @Test
    public void testTextAnchorEndAlignsTheLineToItsEnd() throws Exception {
        SvgText start = new SvgText();
        start.setX(List.of(50.0));
        start.getContent().add("Hello");
        Text startNode = (Text) render(start);

        SvgText end = new SvgText();
        end.setX(List.of(50.0));
        end.setTextAnchor("end");
        end.getContent().add("Hello");
        Node endNode = render(end);

        double width = startNode.getLayoutBounds().getWidth();
        assertThat(endNode.getTranslateX(), closeTo(-width, 1e-6));
    }

    @Test
    public void testTextDecorationUnderlineRenders() throws Exception {
        SvgText text = new SvgText();
        text.setTextDecoration("underline");
        text.getContent().add("Hello");

        Text rendered = (Text) render(text);
        assertThat(rendered.isUnderline(), is(true));
        assertThat(rendered.isStrikethrough(), is(false));
    }

    @Test
    public void testTextDecorationLineThroughRenders() throws Exception {
        SvgText text = new SvgText();
        text.setTextDecoration("line-through");
        text.getContent().add("Hello");

        Text rendered = (Text) render(text);
        assertThat(rendered.isStrikethrough(), is(true));
        assertThat(rendered.isUnderline(), is(false));
    }

    @Test
    public void testBaselineShiftSuperMovesTextUp() throws Exception {
        SvgText plain = new SvgText();
        plain.setY(List.of(100.0));
        plain.getContent().add("Hello");
        Text plainNode = (Text) render(plain);

        SvgText shifted = new SvgText();
        shifted.setY(List.of(100.0));
        shifted.setBaselineShift("super");
        shifted.getContent().add("Hello");
        Text shiftedNode = (Text) render(shifted);

        assertThat(shiftedNode.getY(), closeTo(plainNode.getY() - 0.30 * shiftedNode.getFont().getSize(), 1e-6));
    }

    @Test
    public void testAlignmentBaselineMiddleShiftsText() throws Exception {
        SvgText plain = new SvgText();
        plain.setY(List.of(100.0));
        plain.getContent().add("Hello");
        Text plainNode = (Text) render(plain);

        SvgText shifted = new SvgText();
        shifted.setY(List.of(100.0));
        shifted.setAlignmentBaseline("middle");
        shifted.getContent().add("Hello");
        Text shiftedNode = (Text) render(shifted);

        assertThat(shiftedNode.getY(), closeTo(plainNode.getY() - 0.30 * shiftedNode.getFont().getSize(), 1e-6));
    }

    private static double rotationOf(Node node) {
        for (var transform : node.getTransforms()) {
            if (transform instanceof Rotate rotate) {
                return rotate.getAngle();
            }
        }
        throw new AssertionError("no Rotate transform on " + node);
    }

    private static Node render(SvgText text) {
        SvgGroup group = new SvgGroup();
        group.getContent().add(text);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(group);
        return ((Group) svg.createGroup().getChildren().get(0)).getChildren().get(0);
    }

}
