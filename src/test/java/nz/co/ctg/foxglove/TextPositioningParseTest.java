package nz.co.ctg.foxglove;

import static nz.co.ctg.foxglove.JavaFxTestSupport.advanceOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exercises #28 against a document that has been through the parser, following {@link MixedTextRenderingParseTest}'s convention.
 */
public class TextPositioningParseTest {

    private Group rendered;

    @BeforeEach
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/text-positioning.svg"));
        assertThat(svg, notNullValue());
        rendered = svg.createGroup();
    }

    @Test
    public void testListedXPositionsEachGlyph() throws Exception {
        Group group = (Group) node("listed");
        assertThat(((Text) group.getChildren()
            .get(0)).getX(), is(10.0));
        assertThat(((Text) group.getChildren()
            .get(1)).getX(), is(20.0));
        assertThat(((Text) group.getChildren()
            .get(2)).getX(), is(30.0));
    }

    @Test
    public void testRotateRepeatsItsLastValue() throws Exception {
        Group group = (Group) node("rotated");
        assertThat(rotationOf(group.getChildren()
            .get(0)), is(15.0));
        assertThat(rotationOf(group.getChildren()
            .get(1)), is(30.0));
        assertThat(rotationOf(group.getChildren()
            .get(2)), is(30.0));
    }

    @Test
    public void testTextAnchorMiddleShiftsTheWholeLine() throws Exception {
        Node anchored = node("anchored");
        assertThat(anchored, instanceOf(Text.class));
        assertThat(anchored.getTranslateX(), closeTo(-advanceOf((Text) anchored) / 2.0, 1e-6));
    }

    @Test
    public void testTextDecorationUnderlineRenders() throws Exception {
        Node decorated = node("decorated");
        assertThat(decorated, instanceOf(Text.class));
        assertThat(((Text) decorated).isUnderline(), is(true));
    }

    private static double rotationOf(Node node) {
        for (var transform : node.getTransforms()) {
            if (transform instanceof Rotate rotate) {
                return rotate.getAngle();
            }
        }
        throw new AssertionError("no Rotate transform on " + node);
    }

    private Node node(String id) {
        for (Node candidate : rendered.getChildren()) {
            if (id.equals(candidate.getId())) {
                return candidate;
            }
        }
        throw new AssertionError("no rendered node with id " + id);
    }

}
