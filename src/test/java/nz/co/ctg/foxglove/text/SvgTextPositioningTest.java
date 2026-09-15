package nz.co.ctg.foxglove.text;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.element.SvgGroup;

/**
 * Exercises #28's acceptance criteria: text-anchor, list-valued x/y/dx/dy positioning glyphs individually, per-glyph rotate, text-decoration, and the baseline-attribute
 * approximation.
 */
public class SvgTextPositioningTest {

    @Test
    public void testASingleXValueOnlyPositionsTheFirstRunNotEveryRunFromThatOwner() throws Exception {
        // <text x="10">Hello <tspan>world</tspan> and more</text> - "Hello " and " and more" are two separate
        // runs both owned directly by SvgText, but x="10" is a single value and must only place the very first
        // character; the second run has to continue from the flowing cursor, not jump back to x=10 too.
        SvgText text = new SvgText();
        text.setX(List.of(10.0));
        text.getContent()
            .add("Hello ");
        SvgTextSpan span = new SvgTextSpan();
        span.getContent()
            .add("world");
        text.getContent()
            .add(span);
        text.getContent()
            .add(" and more");

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(3));
        double helloX = ((Text) children.get(0)).getX();
        double helloWidth = ((Text) children.get(0)).getLayoutBounds()
            .getWidth();
        double worldX = ((Text) children.get(1)).getX();
        double worldWidth = ((Text) children.get(1)).getLayoutBounds()
            .getWidth();
        double andMoreX = ((Text) children.get(2)).getX();

        assertThat(helloX, is(10.0));
        assertThat(worldX, closeTo(helloX + helloWidth, 1e-6));
        assertThat(andMoreX, closeTo(worldX + worldWidth, 1e-6));
    }

    @Test
    public void testSingleXYStaysOnTheSingleNodeFastPath() throws Exception {
        SvgText text = new SvgText();
        text.setX(List.of(10.0));
        text.setY(List.of(20.0));
        text.getContent()
            .add("Hello");

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
        text.getContent()
            .add("abc");

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
        text.getContent()
            .add("abc");

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
        start.getContent()
            .add("Hello");
        Text startNode = (Text) render(start);

        SvgText middle = new SvgText();
        middle.setX(List.of(50.0));
        middle.setTextAnchor("middle");
        middle.getContent()
            .add("Hello");
        Node middleNode = render(middle);

        double width = startNode.getLayoutBounds()
            .getWidth();
        assertThat(middleNode.getTranslateX(), closeTo(-width / 2.0, 1e-6));
    }

    @Test
    public void testTextAnchorEndAlignsTheLineToItsEnd() throws Exception {
        SvgText start = new SvgText();
        start.setX(List.of(50.0));
        start.getContent()
            .add("Hello");
        Text startNode = (Text) render(start);

        SvgText end = new SvgText();
        end.setX(List.of(50.0));
        end.setTextAnchor("end");
        end.getContent()
            .add("Hello");
        Node endNode = render(end);

        double width = startNode.getLayoutBounds()
            .getWidth();
        assertThat(endNode.getTranslateX(), closeTo(-width, 1e-6));
    }

    // --- vertical writing-mode (#139) ----------------------------------------

    /**
     * {@code writing-mode="tb"} stacks glyphs top-to-bottom instead of laying them left-to-right: the cursor advances {@code y} by one em per character (the specification's own
     * default for {@code vert-adv-y}) rather than {@code x} by the glyph's own width, and each glyph is centred horizontally on the column - the usual convention for vertical
     * layout, and this renderer's approximation of {@code vert-origin-x}'s own default.
     */
    @Test
    public void testVerticalWritingModeStacksGlyphsTopToBottomOneEmApart() throws Exception {
        SvgText text = new SvgText();
        text.setWritingMode("tb");
        text.getContent()
            .add("AB");

        Group rendered = (Group) render(text);
        List<Node> children = rendered.getChildren();
        assertThat(children, hasSize(2));
        Text a = (Text) children.get(0);
        Text b = (Text) children.get(1);

        assertThat("the first glyph starts at the flow's origin", a.getY(), closeTo(0.0, 1e-6));
        assertThat("one em (the default font size) further down, not sideways - the whole point of #139",
            b.getY(), closeTo(16.0, 1e-6));
        assertThat("no horizontal drift between characters", b.getX(), closeTo(a.getX(), 1e-6));
        assertThat("centred on the column, not flush against it - applied as a translate, not x itself",
            a.getTranslateX(), closeTo(-a.getLayoutBounds()
                .getWidth() / 2.0, 1e-6));
    }

    /** As {@link #testTextAnchorMiddleCentresTheLine}, but along the flow axis {@code writing-mode="tb"} swaps to y. */
    @Test
    public void testTextAnchorMiddleCentresVerticalTextAlongY() throws Exception {
        SvgText start = new SvgText();
        start.setWritingMode("tb");
        start.getContent()
            .add("AB");
        Group startNode = (Group) render(start);
        double height = ((Text) startNode.getChildren()
            .get(1)).getY() + 16.0; // 2 ems, one per glyph

        SvgText middle = new SvgText();
        middle.setWritingMode("tb");
        middle.setTextAnchor("middle");
        middle.getContent()
            .add("AB");
        Node middleNode = render(middle);

        assertThat(middleNode.getTranslateX(), closeTo(0.0, 1e-6));
        assertThat(middleNode.getTranslateY(), closeTo(-height / 2.0, 1e-6));
    }

    /** As {@link #testTextAnchorEndAlignsTheLineToItsEnd}, but along the flow axis {@code writing-mode="tb"} swaps to y. */
    @Test
    public void testTextAnchorEndAlignsVerticalTextToItsEndAlongY() throws Exception {
        SvgText start = new SvgText();
        start.setWritingMode("tb");
        start.getContent()
            .add("AB");
        Group startNode = (Group) render(start);
        double height = ((Text) startNode.getChildren()
            .get(1)).getY() + 16.0;

        SvgText end = new SvgText();
        end.setWritingMode("tb");
        end.setTextAnchor("end");
        end.getContent()
            .add("AB");
        Node endNode = render(end);

        assertThat(endNode.getTranslateX(), closeTo(0.0, 1e-6));
        assertThat(endNode.getTranslateY(), closeTo(-height, 1e-6));
    }

    /**
     * An SVG font's own vertical metrics are never read (#139's own documented scope limit) - a run resolving to one falls through to the ordinary horizontal path regardless of
     * {@code writing-mode}, rather than silently mispositioning glyphs using metrics nothing actually declared. Parse-driven, per this codebase's own standing rule for anything
     * touching an SVG font binding (#105/#119/#136): an in-memory fixture through typed setters would prove nothing about whether {@code writing-mode} and an inline {@code <font>}
     * actually interact the way this asserts, only that the Java objects can be constructed.
     */
    @Test
    public void testVerticalWritingModeIsIgnoredWhenTheRunUsesAnSvgFont() throws Exception {
        String document = """
                        <svg xmlns="http://www.w3.org/2000/svg" width="50" height="50">
                          <defs>
                            <font horiz-adv-x="500">
                              <font-face font-family="VerticalTestFont" units-per-em="1000" ascent="800" descent="-200"/>
                              <glyph unicode="A" horiz-adv-x="500" d="M0,0 L10,0 L10,10 Z"/>
                            </font>
                          </defs>
                          <text id="subject" font-family="VerticalTestFont" writing-mode="tb">A</text>
                        </svg>
                        """;
        SvgGraphic svg = new FoxgloveParser().parse(new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8)));
        Node rendered = svg.createGroup()
            .getChildren()
            .get(0);

        // a single glyph from an SVG font is a bare outline node, not split/centred the way vertical plain text is -
        // confirming this took the ordinary horizontal path rather than throwing or silently mispositioning it
        assertThat(rendered, instanceOf(javafx.scene.shape.Path.class));
    }

    @Test
    public void testTextDecorationUnderlineRenders() throws Exception {
        SvgText text = new SvgText();
        text.setTextDecoration("underline");
        text.getContent()
            .add("Hello");

        Text rendered = (Text) render(text);
        assertThat(rendered.isUnderline(), is(true));
        assertThat(rendered.isStrikethrough(), is(false));
    }

    @Test
    public void testTextDecorationLineThroughRenders() throws Exception {
        SvgText text = new SvgText();
        text.setTextDecoration("line-through");
        text.getContent()
            .add("Hello");

        Text rendered = (Text) render(text);
        assertThat(rendered.isStrikethrough(), is(true));
        assertThat(rendered.isUnderline(), is(false));
    }

    @Test
    public void testBaselineShiftSuperMovesTextUp() throws Exception {
        SvgText plain = new SvgText();
        plain.setY(List.of(100.0));
        plain.getContent()
            .add("Hello");
        Text plainNode = (Text) render(plain);

        SvgText shifted = new SvgText();
        shifted.setY(List.of(100.0));
        shifted.setBaselineShift("super");
        shifted.getContent()
            .add("Hello");
        Text shiftedNode = (Text) render(shifted);

        assertThat(shiftedNode.getY(), closeTo(plainNode.getY() - 0.30 * shiftedNode.getFont()
            .getSize(), 1e-6));
    }

    @Test
    public void testAlignmentBaselineMiddleShiftsText() throws Exception {
        SvgText plain = new SvgText();
        plain.setY(List.of(100.0));
        plain.getContent()
            .add("Hello");
        Text plainNode = (Text) render(plain);

        SvgText shifted = new SvgText();
        shifted.setY(List.of(100.0));
        shifted.setAlignmentBaseline("middle");
        shifted.getContent()
            .add("Hello");
        Text shiftedNode = (Text) render(shifted);

        assertThat(shiftedNode.getY(), closeTo(plainNode.getY() - 0.30 * shiftedNode.getFont()
            .getSize(), 1e-6));
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
        group.getContent()
            .add(text);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(group);
        return ((Group) svg.createGroup()
            .getChildren()
            .get(0)).getChildren()
            .get(0);
    }

}
