package nz.co.ctg.foxglove;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Exercises #27 against a document that has been through the parser, rather than one assembled in memory, following
 * {@link StyleElementParseTest}'s convention.
 */
public class MixedTextRenderingParseTest {

    private Group rendered;

    @BeforeEach
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/mixed-text-rendering.svg"));
        assertThat(svg, notNullValue());
        rendered = svg.createGroup();
    }

    @Test
    public void testPlainTextStillRendersAsABareTextNode() throws Exception {
        assertThat(node("plain"), instanceOf(Text.class));
        assertThat(((Text) node("plain")).getText(), is("Hello world"));
    }

    @Test
    public void testMixedTextRendersRunsWithTheirOwnStyling() throws Exception {
        Node mixed = node("mixed");
        assertThat(mixed, instanceOf(Group.class));
        Group group = (Group) mixed;

        Text hello = (Text) group.getChildren().get(0);
        Text world = (Text) group.getChildren().get(1);
        Text and = (Text) group.getChildren().get(2);
        Text ref = (Text) group.getChildren().get(3);

        assertThat(hello.getText(), is("Hello "));
        assertThat(hello.getFill(), is(Color.BLUE));

        assertThat(world.getText(), is("world"));
        assertThat(world.getFill(), is(Color.RED));

        assertThat(and.getText(), is(" and "));
        assertThat(and.getFill(), is(Color.BLUE));

        assertThat(ref.getText(), is("Hello world"));
        assertThat(ref.getFill(), is(Color.BLUE));
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
