package nz.co.ctg.foxglove;

import org.junit.Test;

import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Inheritance never worked: {@code getFill()} substituted black for an absent value, so the parent was never
 * consulted, and only the immediate parent was passed anyway. Several parsed properties were also never applied.
 */
public class ISvgGraphicsAttributesTest {

    // --- inheritance -------------------------------------------------------

    @Test
    public void testShapeInheritsFillFromItsGroup() throws Exception {
        SvgGroup group = new SvgGroup();
        group.setFill(Color.RED);
        group.getContent().add(new SvgRectangle());

        assertThat(firstShape(render(group)).getFill(), is(Color.RED));
    }

    @Test
    public void testInheritanceReachesThroughSeveralLevels() throws Exception {
        SvgGroup inner = new SvgGroup();
        inner.getContent().add(new SvgRectangle());
        SvgGroup middle = new SvgGroup();
        middle.getContent().add(inner);
        SvgGroup outer = new SvgGroup();
        outer.setFill(Color.RED);
        outer.getContent().add(middle);

        assertThat(firstShape(render(outer)).getFill(), is(Color.RED));
    }

    @Test
    public void testNearestAncestorWins() throws Exception {
        SvgGroup inner = new SvgGroup();
        inner.setFill(Color.BLUE);
        inner.getContent().add(new SvgRectangle());
        SvgGroup outer = new SvgGroup();
        outer.setFill(Color.RED);
        outer.getContent().add(inner);

        assertThat(firstShape(render(outer)).getFill(), is(Color.BLUE));
    }

    @Test
    public void testElementOverridesTheInheritedValue() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setFill(Color.GREEN);
        SvgGroup group = new SvgGroup();
        group.setFill(Color.RED);
        group.getContent().add(rect);

        assertThat(firstShape(render(group)).getFill(), is(Color.GREEN));
    }

    @Test
    public void testShapeInheritsStrokePropertiesFromItsGroup() throws Exception {
        SvgGroup group = new SvgGroup();
        group.setStroke(Color.BLUE);
        group.setStrokeWidth(4.0);
        group.setStrokeLineCap(StrokeLineCap.ROUND);
        group.getContent().add(new SvgRectangle());

        Shape shape = firstShape(render(group));
        assertThat(shape.getStroke(), is(Color.BLUE));
        assertThat(shape.getStrokeWidth(), is(4.0));
        assertThat(shape.getStrokeLineCap(), is(StrokeLineCap.ROUND));
    }

    // --- initial values ----------------------------------------------------

    @Test
    public void testUnspecifiedFillIsBlack() throws Exception {
        assertThat(firstShape(render(groupOf(new SvgRectangle()))).getFill(), is(Color.BLACK));
    }

    @Test
    public void testUnspecifiedStrokeIsNone() throws Exception {
        assertThat(firstShape(render(groupOf(new SvgRectangle()))).getStroke(), is(nullValue()));
    }

    /**
     * SVG starts a stroke one unit wide, butt capped, mitred with a limit of four. JavaFX would otherwise default to
     * a square cap and a limit of ten, and the previous code set the width and the limit to zero.
     */
    @Test
    public void testStrokeInitialValuesFollowTheSpecification() throws Exception {
        Shape shape = firstShape(render(groupOf(new SvgRectangle())));
        assertThat(shape.getStrokeWidth(), is(1.0));
        assertThat(shape.getStrokeMiterLimit(), is(4.0));
        assertThat(shape.getStrokeDashOffset(), is(0.0));
        assertThat(shape.getStrokeLineCap(), is(StrokeLineCap.BUTT));
        assertThat(shape.getStrokeLineJoin(), is(StrokeLineJoin.MITER));
    }

    // --- opacity -----------------------------------------------------------

    @Test
    public void testOpacityIsAppliedToTheNode() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setOpacity("0.25");

        assertThat(firstShape(render(groupOf(rect))).getOpacity(), is(0.25));
    }

    @Test
    public void testOpacityAcceptsAPercentage() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setOpacity("40%");

        assertThat(firstShape(render(groupOf(rect))).getOpacity(), closeTo(0.4, 1e-9));
    }

    /**
     * Group opacity composites the subtree once; it must not be handed to each child as well, which would apply it
     * twice.
     */
    @Test
    public void testOpacityIsNotInherited() throws Exception {
        SvgGroup group = new SvgGroup();
        group.setOpacity("0.5");
        group.getContent().add(new SvgRectangle());

        Group rendered = (Group) render(group).getChildren().get(0);
        assertThat(rendered.getOpacity(), is(0.5));
        assertThat(rendered.getChildren().get(0).getOpacity(), is(1.0));
    }

    @Test
    public void testFillOpacityIsFoldedIntoTheFill() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setFill(Color.RED);
        rect.setFillOpacity("0.5");

        Color fill = (Color) firstShape(render(groupOf(rect))).getFill();
        assertThat(fill.getRed(), is(1.0));
        assertThat(fill.getOpacity(), closeTo(0.5, 1e-9));
    }

    @Test
    public void testStrokeOpacityIsFoldedIntoTheStroke() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStroke(Color.BLUE);
        rect.setStrokeOpacity("0.25");

        Color stroke = (Color) firstShape(render(groupOf(rect))).getStroke();
        assertThat(stroke.getBlue(), is(1.0));
        assertThat(stroke.getOpacity(), closeTo(0.25, 1e-9));
    }

    @Test
    public void testFillOpacityIsInherited() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setFill(Color.RED);
        SvgGroup group = new SvgGroup();
        group.setFillOpacity("0.5");
        group.getContent().add(rect);

        assertThat(((Color) firstShape(render(group)).getFill()).getOpacity(), closeTo(0.5, 1e-9));
    }

    // --- visibility --------------------------------------------------------

    /**
     * Unlike display, a hidden element keeps its place in the scene graph and is simply not painted.
     */
    @Test
    public void testVisibilityHiddenHidesTheShapeWithoutRemovingIt() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setVisibility("hidden");

        Group rendered = render(groupOf(rect));
        assertThat(((Group) rendered.getChildren().get(0)).getChildren().size(), is(1));
        assertThat(firstShape(rendered).isVisible(), is(false));
    }

    @Test
    public void testVisibilityIsInherited() throws Exception {
        SvgGroup group = new SvgGroup();
        group.setVisibility("hidden");
        group.getContent().add(new SvgRectangle());

        assertThat(firstShape(render(group)).isVisible(), is(false));
    }

    /**
     * A descendant may become visible again inside a hidden ancestor, which is why visibility travels down as an
     * inherited property rather than by hiding the group node - an invisible JavaFX parent would hide the child
     * unconditionally and make the override impossible.
     */
    @Test
    public void testVisibleChildOverridesHiddenAncestor() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setVisibility("visible");
        SvgGroup group = new SvgGroup();
        group.setVisibility("hidden");
        group.getContent().add(rect);

        Group rendered = (Group) render(group).getChildren().get(0);
        assertThat(rendered.isVisible(), is(true));
        assertThat(rendered.getChildren().get(0).isVisible(), is(true));
    }

    // --- fill rule ---------------------------------------------------------

    @Test
    public void testFillRuleIsAppliedToAPath() throws Exception {
        SvgPath path = new SvgPath();
        path.setD("M0 0 L10 0 L10 10 Z");
        path.setFillRule(FillRule.EVEN_ODD);

        assertThat(((SVGPath) firstShape(render(groupOf(path)))).getFillRule(), is(FillRule.EVEN_ODD));
    }

    @Test
    public void testFillRuleIsInherited() throws Exception {
        SvgPath path = new SvgPath();
        path.setD("M0 0 L10 0 L10 10 Z");
        SvgGroup group = new SvgGroup();
        group.setFillRule(FillRule.EVEN_ODD);
        group.getContent().add(path);

        assertThat(((SVGPath) firstShape(render(group))).getFillRule(), is(FillRule.EVEN_ODD));
    }

    // --- opacity parsing ---------------------------------------------------

    @Test
    public void testParseOpacity() throws Exception {
        assertThat(ISvgGraphicsAttributes.parseOpacity("0.5"), is(0.5));
        assertThat(ISvgGraphicsAttributes.parseOpacity(" 1 "), is(1.0));
        assertThat(ISvgGraphicsAttributes.parseOpacity("50%"), closeTo(0.5, 1e-9));
        assertThat(ISvgGraphicsAttributes.parseOpacity("1.5"), is(1.0));
        assertThat(ISvgGraphicsAttributes.parseOpacity("-2"), is(0.0));
        assertThat(ISvgGraphicsAttributes.parseOpacity(null), is(nullValue()));
        assertThat(ISvgGraphicsAttributes.parseOpacity(""), is(nullValue()));
        assertThat(ISvgGraphicsAttributes.parseOpacity("opaque"), is(nullValue()));
    }

    // --- helpers -----------------------------------------------------------

    private static SvgGroup groupOf(ISvgElement child) {
        SvgGroup group = new SvgGroup();
        group.getContent().add(child);
        return group;
    }

    private static Group render(SvgGroup group) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(group);
        return svg.createGroup();
    }

    private static Shape firstShape(Group rendered) {
        Node node = rendered.getChildren().get(0);
        while (node instanceof Group group) {
            node = group.getChildren().get(0);
        }
        return (Shape) node;
    }

}
