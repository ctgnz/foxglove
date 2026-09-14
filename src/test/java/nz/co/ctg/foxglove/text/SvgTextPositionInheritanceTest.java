package nz.co.ctg.foxglove.text;

import java.util.List;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.SvgPath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

/**
 * Exercises #143: {@code x}/{@code y}/{@code dx}/{@code dy}/{@code rotate} address the characters of a
 * text-positioning element's <b>whole subtree</b>, not just those it owns directly.
 * <p>
 * Built around cases that discriminate the actual fix from simpler-but-wrong readings of it - in particular, "the
 * first ancestor with a non-empty list wins" is not the same rule as "the nearest ancestor whose list still reaches
 * this character wins," and only the second one is correct (see {@link #testAnElementsOwnListFallsThroughOnceExhausted}).
 */
public class SvgTextPositionInheritanceTest {

    /** The issue's own first example: a leading child must not cause its own following sibling to double back. */
    @Test
    public void testATrailingCharacterAfterAPositionedChildDoesNotDoubleBack() throws Exception {
        SvgTextSpan span = new SvgTextSpan();
        span.getContent().add("A");
        SvgText text = new SvgText();
        text.setX(List.of(30.0));
        text.setY(List.of(100.0));
        text.getContent().add(span);
        text.getContent().add("B");

        Group rendered = (Group) render(text);
        Text a = (Text) rendered.getChildren().get(0);
        Text b = (Text) rendered.getChildren().get(1);

        assertThat(a.getX(), closeTo(30, 1e-6));
        assertThat(b.getX(), closeTo(a.getX() + a.getLayoutBounds().getWidth(), 1e-6));
        assertThat(b.getY(), closeTo(100, 1e-6));
    }

    /** The issue's own second example: an ancestor's list reaches straight through a child with none of its own. */
    @Test
    public void testAChildWithNoListOfItsOwnInheritsThePositionsItsCharactersFallInto() throws Exception {
        SvgTextSpan span = new SvgTextSpan();
        span.getContent().add("AB");
        SvgText text = new SvgText();
        text.setX(List.of(10.0, 20.0, 30.0));
        text.getContent().add(span);
        text.getContent().add("C");

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(3));
        assertThat(((Text) children.get(0)).getX(), closeTo(10, 1e-6));
        assertThat(((Text) children.get(1)).getX(), closeTo(20, 1e-6));
        assertThat(((Text) children.get(2)).getX(), closeTo(30, 1e-6));
    }

    /**
     * <b>The inconvenient case.</b> {@code <tspan x="1">AB</tspan>} covers only its own first character; "B" must
     * fall through to the enclosing {@code <text>}'s list rather than being left unpositioned just because the
     * {@code <tspan>} happens to have a list of its own. "C" then proves both A and B were counted against
     * {@code <text>}'s own running index - an implementation that stopped at the first non-empty list (rather than
     * only at the first one that actually reaches the character) would fail exactly this case while still passing
     * the two simpler ones above.
     */
    @Test
    public void testAnElementsOwnListFallsThroughOnceExhausted() throws Exception {
        SvgTextSpan span = new SvgTextSpan();
        span.setX(List.of(1.0));
        span.getContent().add("AB");
        SvgText text = new SvgText();
        text.setX(List.of(100.0, 200.0, 300.0));
        text.getContent().add(span);
        text.getContent().add("C");

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(3));
        assertThat("tspan's own value wins for its first character",
            ((Text) children.get(0)).getX(), closeTo(1, 1e-6));
        assertThat("tspan's list is exhausted here, so text's x[1] applies",
            ((Text) children.get(1)).getX(), closeTo(200, 1e-6));
        assertThat("both A and B were counted against text's own index",
            ((Text) children.get(2)).getX(), closeTo(300, 1e-6));
    }

    /** The mechanism is not hard-coded to two levels. */
    @Test
    public void testThreeLevelsOfNestingStillIndexCorrectly() throws Exception {
        SvgTextSpan inner = new SvgTextSpan();
        inner.getContent().add("AB");
        SvgTextSpan outer = new SvgTextSpan();
        outer.getContent().add(inner);
        SvgText text = new SvgText();
        text.setX(List.of(5.0, 6.0, 7.0));
        text.getContent().add(outer);
        text.getContent().add("C");

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(3));
        assertThat(((Text) children.get(0)).getX(), closeTo(5, 1e-6));
        assertThat(((Text) children.get(1)).getX(), closeTo(6, 1e-6));
        assertThat(((Text) children.get(2)).getX(), closeTo(7, 1e-6));
    }

    /** {@code rotate} inherited from an ancestor also forces the per-glyph split, and never runs out once found. */
    @Test
    public void testRotateInheritedFromAnAncestorAppliesPerGlyph() throws Exception {
        SvgTextSpan span = new SvgTextSpan();
        span.getContent().add("AB");
        SvgText text = new SvgText();
        text.setRotate(List.of(15.0, 25.0));
        text.getContent().add(span);

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(2));
        assertThat(rotationOf(children.get(0)), closeTo(15, 1e-6));
        assertThat(rotationOf(children.get(1)), closeTo(25, 1e-6));
    }

    /**
     * An element's own explicit position always wins over an ancestor's, even where the ancestor's list would
     * otherwise reach that index too - precedence, not merely fallback.
     */
    @Test
    public void testAChildsOwnPositionOverridesTheAncestorsAtThatIndex() throws Exception {
        SvgTextSpan span = new SvgTextSpan();
        span.setX(List.of(999.0));
        span.getContent().add("A");
        SvgText text = new SvgText();
        text.setX(List.of(10.0, 20.0));
        text.getContent().add(span);
        text.getContent().add("B");

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(((Text) children.get(0)).getX(), closeTo(999, 1e-6));
        assertThat("A still counted against text's index despite using its own value",
            ((Text) children.get(1)).getX(), closeTo(20, 1e-6));
    }

    /**
     * Characters laid out along a {@code <textPath>} aren't themselves positioned by these lists (out of scope,
     * #29), but they still occupy indices in an enclosing element's subtree - a following plain run must resume
     * from beyond them, not re-offered the same list entries.
     */
    @Test
    public void testCharactersOnATextPathStillAdvanceAnEnclosingAncestorsIndex() throws Exception {
        SvgTextPath textPath = new SvgTextPath();
        textPath.setXlinkHref("#p");
        textPath.getContent().add("AB");
        SvgText text = new SvgText();
        text.setX(List.of(10.0, 20.0, 30.0));
        text.getContent().add(textPath);
        text.getContent().add("C");

        SvgPath path = new SvgPath();
        path.setId("p");
        path.setD("M0,0 L1000,0");

        SvgGroup group = new SvgGroup();
        group.getContent().add(path);
        group.getContent().add(text);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(group);
        Group rendered = (Group) ((Group) svg.createGroup().getChildren().get(0)).getChildren().get(1);

        // "AB" on the path (2 glyphs) + "C" (1 run) = 3 children; C must land on text.x[2]=30, not x[0] again
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(3));
        assertThat(((Text) children.get(2)).getX(), closeTo(30, 1e-6));
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
