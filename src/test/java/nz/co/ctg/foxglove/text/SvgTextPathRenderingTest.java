package nz.co.ctg.foxglove.text;

import java.util.List;

import org.junit.Test;

import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.SvgPath;

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
 * Exercises #29's acceptance criteria: text renders along a referenced path with correct per-glyph rotation,
 * honouring {@code startOffset}. A quarter circle of radius 1000 centred at the origin, from (1000,0) to
 * (0,1000), is used throughout - its tangent angle is exactly {@code startOffset fraction * 90 + 90} degrees,
 * letting rotation be checked exactly via {@code startOffset} rather than depending on font metrics to land a
 * glyph at a particular arc length.
 */
public class SvgTextPathRenderingTest {

    private static final String QUARTER_CIRCLE = "M1000,0 A1000,1000 0 0 1 0,1000";

    @Test
    public void testGlyphsFollowAStraightPathInIncreasingOrder() throws Exception {
        Group rendered = (Group) renderWithPath("M0,0 L1000,0", "abc", null);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(3));
        assertThat(((Text) children.get(0)).getText(), is("a"));
        assertThat(((Text) children.get(0)).getY(), closeTo(0.0, 1e-6));
        double previousX = -1;
        for (Node child : children) {
            double x = ((Text) child).getX();
            assertThat(x > previousX, is(true));
            previousX = x;
            assertThat(rotationOf(child), closeTo(0.0, 1e-6));
        }
    }

    @Test
    public void testStartOffsetShiftsTheGlyphAlongThePath() throws Exception {
        Text unshifted = (Text) renderWithPath("M0,0 L1000,0", "a", null);
        Text shifted = (Text) renderWithPath("M0,0 L1000,0", "a", "50");

        assertThat(shifted.getX(), closeTo(unshifted.getX() + 50.0, 1e-6));
    }

    @Test
    public void testPercentageStartOffsetIsAFractionOfTheTotalLength() throws Exception {
        Text unshifted = (Text) renderWithPath("M0,0 L1000,0", "a", null);
        Text shifted = (Text) renderWithPath("M0,0 L1000,0", "a", "10%");

        assertThat(shifted.getX(), closeTo(unshifted.getX() + 100.0, 1e-6));
    }

    @Test
    public void testRotationAtTheStartOfTheArcMatchesItsTangent() throws Exception {
        Text glyph = (Text) renderWithPath(QUARTER_CIRCLE, "a", "0%");
        assertThat(rotationOf(glyph), closeTo(90.0, 2.0));
    }

    @Test
    public void testRotationAtTheMidpointOfTheArcMatchesItsTangent() throws Exception {
        Text glyph = (Text) renderWithPath(QUARTER_CIRCLE, "a", "50%");
        assertThat(rotationOf(glyph), closeTo(135.0, 2.0));
    }

    @Test
    public void testRotationAtTheEndOfTheArcMatchesItsTangent() throws Exception {
        Text glyph = (Text) renderWithPath(QUARTER_CIRCLE, "a", "100%");
        assertThat(rotationOf(glyph), closeTo(180.0, 2.0));
    }

    @Test
    public void testUnresolvableHrefRendersNothingWithoutThrowing() throws Exception {
        SvgText text = new SvgText();
        SvgTextPath textPath = new SvgTextPath();
        textPath.setXlinkHref("#missing");
        textPath.getContent().add("Hello");
        text.getContent().add(textPath);

        Node rendered = render(text, null);
        assertThat(rendered, instanceOf(Group.class));
        assertThat(((Group) rendered).getChildren(), hasSize(0));
    }

    private static double rotationOf(Node node) {
        for (var transform : node.getTransforms()) {
            if (transform instanceof Rotate rotate) {
                return rotate.getAngle();
            }
        }
        throw new AssertionError("no Rotate transform on " + node);
    }

    private static Node renderWithPath(String d, String text, String startOffset) {
        SvgText svgText = new SvgText();
        SvgTextPath textPath = new SvgTextPath();
        textPath.setXlinkHref("#p");
        textPath.setStartOffset(startOffset);
        textPath.getContent().add(text);
        svgText.getContent().add(textPath);

        SvgPath path = new SvgPath();
        path.setId("p");
        path.setD(d);

        return render(svgText, path);
    }

    private static Node render(SvgText text, SvgPath path) {
        SvgGroup group = new SvgGroup();
        if (path != null) {
            group.getContent().add(path);
        }
        group.getContent().add(text);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(group);
        return ((Group) svg.createGroup().getChildren().get(0)).getChildren().get(path != null ? 1 : 0);
    }

}
