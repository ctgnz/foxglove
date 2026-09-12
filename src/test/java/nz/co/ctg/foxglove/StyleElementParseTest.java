package nz.co.ctg.foxglove;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * Exercises a {@code <style>} block against a document that has been through the parser, rather than one assembled
 * in memory, so the acceptance criterion for #15 - "a {@code <style>} block with element, {@code .class} and
 * {@code #id} selectors affects rendering" - is proven end to end, the same way {@link SvgPaintParseTest} does for
 * paint references.
 */
public class StyleElementParseTest {

    private Group rendered;

    @BeforeEach
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/style-element.svg"));
        assertThat(svg, notNullValue());
        rendered = svg.createGroup();
    }

    @Test
    public void testATypeSelectorRuleApplies() throws Exception {
        assertThat(shape("plain").getFill(), is(Color.LIGHTGRAY));
    }

    @Test
    public void testAClassSelectorRuleBeatsTheTypeSelectorRule() throws Exception {
        assertThat(shape("classy").getFill(), is(Color.ORANGE));
    }

    @Test
    public void testAnIdSelectorRuleBeatsTheClassSelectorRule() throws Exception {
        assertThat(shape("target").getFill(), is(Color.RED));
    }

    @Test
    public void testTheInlineStyleAttributeBeatsEveryStylesheetRule() throws Exception {
        assertThat(shape("inline").getFill(), is(Color.BLUE));
    }

    private Shape shape(String id) {
        for (Node node : rendered.getChildren()) {
            if (id.equals(node.getId())) {
                return (Shape) node;
            }
        }
        throw new AssertionError("no rendered shape with id " + id);
    }

}
