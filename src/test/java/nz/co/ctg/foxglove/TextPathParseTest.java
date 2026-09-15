package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import org.junit.jupiter.api.Test;

/**
 * Exercises #29 against a document that has been through the parser, following {@link MixedTextRenderingParseTest}'s convention.
 */
public class TextPathParseTest {

    @Test
    public void testTextPathRendersGlyphsAlongTheReferencedPath() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/text-path.svg"));
        assertThat(svg, notNullValue());
        Group rendered = svg.createGroup();

        Node onPath = node(rendered, "on-path");
        assertThat(onPath, instanceOf(Group.class));
        Group glyphs = (Group) onPath;
        assertThat(glyphs.getChildren(), hasSize(5));
        for (Node glyph : glyphs.getChildren()) {
            assertThat(glyph, instanceOf(Text.class));
            boolean hasRotation = glyph.getTransforms()
                .stream()
                .anyMatch(Rotate.class::isInstance);
            assertThat(hasRotation, is(true));
        }
    }

    private static Node node(Group rendered, String id) {
        for (Node candidate : rendered.getChildren()) {
            if (id.equals(candidate.getId())) {
                return candidate;
            }
        }
        throw new AssertionError("no rendered node with id " + id);
    }

}
