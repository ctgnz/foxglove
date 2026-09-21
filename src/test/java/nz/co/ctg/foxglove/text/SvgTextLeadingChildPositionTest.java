package nz.co.ctg.foxglove.text;

import static nz.co.ctg.foxglove.JavaFxTestSupport.advanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgPath;

/**
 * Covers #142: a {@code <text>}'s own {@code x}/{@code y} apply even when its content opens with a child element.
 * <p>
 * The position used to arrive only as a side effect of the {@code <text>} owning the first run, which happens just when it has character data of its own.
 * {@code <text x="0" y="100">Hello <tspan>world</tspan></text>} therefore worked while {@code <text x="0" y="100"><tspan>Hello</tspan></text>} silently rendered at the origin -
 * and wrapping a whole {@code <text>} in one {@code <tspan>} is an everyday authoring shape. #143 later generalised the mechanism this relies on to whole subtrees rather than a
 * single seeded value; see {@link SvgTextPositionInheritanceTest} for that broader coverage.
 */
public class SvgTextLeadingChildPositionTest {

    @Test
    public void testALeadingTspanInheritsTheTextPosition() {
        SvgTextSpan span = new SvgTextSpan();
        span.getContent()
            .add("A");

        Text rendered = (Text) render(text(30, 100, span));
        assertThat(rendered.getX(), closeTo(30, 1e-6));
        assertThat(rendered.getY(), closeTo(100, 1e-6));
    }

    /** A {@code <tspan>} carrying its own position still overrides the inherited one. */
    @Test
    public void testALeadingTspanWithItsOwnPositionStillWins() {
        SvgTextSpan span = new SvgTextSpan();
        span.setX(List.of(70.0));
        span.setY(List.of(20.0));
        span.getContent()
            .add("A");

        Text rendered = (Text) render(text(30, 100, span));
        assertThat(rendered.getX(), closeTo(70, 1e-6));
        assertThat(rendered.getY(), closeTo(20, 1e-6));
    }

    /** The long-standing case - leading character data - must be unchanged. */
    @Test
    public void testLeadingCharacterDataIsUnaffected() {
        SvgText text = new SvgText();
        text.setX(List.of(30.0));
        text.setY(List.of(100.0));
        text.getContent()
            .add("A");

        Text rendered = (Text) render(text);
        assertThat(rendered.getX(), closeTo(30, 1e-6));
        assertThat(rendered.getY(), closeTo(100, 1e-6));
    }

    /**
     * A leading child and the text's own following characters share one baseline, and the second flows after the first rather than landing on top of it.
     * <p>
     * That second part was #143: the {@code <text>} consumed {@code x[0]} a second time for its own first character, since nothing recorded that the leading {@code <tspan>} had
     * already used it. Fixed there; this asserts the resolution rather than merely the part that worked all along.
     */
    @Test
    public void testTextFollowingALeadingTspanContinuesFromIt() {
        SvgTextSpan span = new SvgTextSpan();
        span.getContent()
            .add("A");
        SvgText text = text(30, 100, span);
        text.getContent()
            .add("B");

        Group rendered = (Group) render(text);
        Text a = (Text) rendered.getChildren()
            .get(0);
        Text b = (Text) rendered.getChildren()
            .get(1);
        assertThat(b.getY(), closeTo(100, 1e-6));
        assertThat(b.getX(), closeTo(a.getX() + advanceOf(a), 1e-6));
    }

    /** A {@code <text>} declaring no position at all still starts at the origin. */
    @Test
    public void testATextWithNoPositionStartsAtTheOrigin() {
        SvgTextSpan span = new SvgTextSpan();
        span.getContent()
            .add("A");
        SvgText text = new SvgText();
        text.getContent()
            .add(span);

        Text rendered = (Text) render(text);
        assertThat(rendered.getX(), closeTo(0, 1e-6));
        assertThat(rendered.getY(), closeTo(0, 1e-6));
    }

    /** The same inheritance reaches a {@code <textPath>}, which is also a leading child. */
    @Test
    public void testALeadingTextPathStillResolves() {
        SvgTextPath textPath = new SvgTextPath();
        textPath.setXlinkHref("#p");
        textPath.getContent()
            .add("A");

        SvgPath path = new SvgPath();
        path.setId("p");
        path.setD("M0,50 L1000,50");

        SvgText text = text(30, 100, textPath);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(path);
        svg.getContent()
            .add(text);

        // a textPath takes its position from the path, not from the text - the point is that it still renders
        Text rendered = (Text) svg.createGroup()
            .getChildren()
            .get(1);
        assertThat(rendered.getY(), closeTo(50, 1e-6));
    }

    private static SvgText text(double x, double y, nz.co.ctg.foxglove.AbstractSvgStylable child) {
        SvgText text = new SvgText();
        text.setX(List.of(x));
        text.setY(List.of(y));
        text.getContent()
            .add(child);
        return text;
    }

    private static Node render(SvgText text) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(text);
        return svg.createGroup()
            .getChildren()
            .get(0);
    }

}
