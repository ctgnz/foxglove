package nz.co.ctg.foxglove.text;

import java.util.List;

import org.junit.Test;

import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.element.SvgGroup;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Exercises #27's acceptance criteria: mixed text content renders every run in order, a run's own styling differs
 * from its parent's, nested {@code <tspan>} recurses, {@code xml:space} handling behaves both ways, and
 * {@code <tref>} inlines the referenced text styled at the {@code tref} site.
 */
public class SvgTextRenderingTest {

    @Test
    public void testMixedContentRendersBothRunsInOrder() throws Exception {
        SvgText text = new SvgText();
        text.getContent().add("Hello ");
        SvgTextSpan span = new SvgTextSpan();
        span.getContent().add("world");
        text.getContent().add(span);

        Node rendered = render(text);
        assertThat(rendered, instanceOf(Group.class));
        List<Node> children = ((Group) rendered).getChildren();
        assertThat(children, hasSize(2));
        assertThat(((Text) children.get(0)).getText(), is("Hello "));
        assertThat(((Text) children.get(1)).getText(), is("world"));
    }

    @Test
    public void testTspanFillOverridesTheParent() throws Exception {
        SvgText text = new SvgText();
        text.setFill(Color.BLUE);
        text.getContent().add("Hello ");
        SvgTextSpan span = new SvgTextSpan();
        span.setFill(Color.RED);
        span.getContent().add("world");
        text.getContent().add(span);

        Group rendered = (Group) render(text);
        assertThat(((Text) rendered.getChildren().get(0)).getFill(), is(Color.BLUE));
        assertThat(((Text) rendered.getChildren().get(1)).getFill(), is(Color.RED));
    }

    @Test
    public void testNestedTspanRecurses() throws Exception {
        SvgText text = new SvgText();
        text.getContent().add("a");
        SvgTextSpan outer = new SvgTextSpan();
        outer.getContent().add("b");
        SvgTextSpan inner = new SvgTextSpan();
        inner.getContent().add("c");
        outer.getContent().add(inner);
        outer.getContent().add("d");
        text.getContent().add(outer);

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(4));
        assertThat(((Text) children.get(0)).getText(), is("a"));
        assertThat(((Text) children.get(1)).getText(), is("b"));
        assertThat(((Text) children.get(2)).getText(), is("c"));
        assertThat(((Text) children.get(3)).getText(), is("d"));
    }

    @Test
    public void testDefaultWhitespaceCollapsesAndTrims() throws Exception {
        SvgText text = new SvgText();
        text.getContent().add("  Hello   ");
        SvgTextSpan span = new SvgTextSpan();
        span.getContent().add(" world  ");
        text.getContent().add(span);

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(2));
        assertThat(((Text) children.get(0)).getText(), is("Hello "));
        assertThat(((Text) children.get(1)).getText(), is("world"));
    }

    @Test
    public void testPreserveKeepsWhitespaceVerbatim() throws Exception {
        SvgText text = new SvgText();
        text.setXmlSpace("preserve");
        text.getContent().add("  Hello   ");
        SvgTextSpan span = new SvgTextSpan();
        span.getContent().add(" world  ");
        text.getContent().add(span);

        Node rendered = render(text);
        assertThat(rendered, instanceOf(Group.class));
        List<Node> children = ((Group) rendered).getChildren();
        assertThat(((Text) children.get(0)).getText(), is("  Hello   "));
        assertThat(((Text) children.get(1)).getText(), is(" world  "));
    }

    @Test
    public void testTrefInlinesTheReferencedTextStyledAtTheTrefSite() throws Exception {
        SvgText source = new SvgText();
        source.setId("label");
        source.setFill(Color.BLUE);
        source.getContent().add("source text");

        SvgText text = new SvgText();
        text.setFill(Color.GREEN);
        SvgTextReference reference = new SvgTextReference();
        reference.setXlinkHref("#label");
        text.getContent().add(reference);

        SvgGroup group = new SvgGroup();
        group.getContent().add(source);
        group.getContent().add(text);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(group);

        Group rendered = svg.createGroup();
        Node textNode = ((Group) rendered.getChildren().get(0)).getChildren().get(1);

        assertThat(textNode, instanceOf(Text.class));
        Text run = (Text) textNode;
        assertThat(run.getText(), is("source text"));
        assertThat(run.getFill(), is(Color.GREEN));
    }

    private static Node render(SvgText text) {
        SvgGroup group = new SvgGroup();
        group.getContent().add(text);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(group);
        return ((Group) svg.createGroup().getChildren().get(0)).getChildren().get(0);
    }

}
