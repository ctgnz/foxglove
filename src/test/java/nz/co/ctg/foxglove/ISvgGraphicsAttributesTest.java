package nz.co.ctg.foxglove;

import java.util.List;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.clip.SvgClipPath;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
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
    public void testShapeInheritsCursorFromItsGroup() throws Exception {
        SvgGroup group = new SvgGroup();
        group.setCursor("pointer");
        group.getContent().add(new SvgRectangle());

        Shape shape = firstShape(render(group));
        assertThat(shape.getCursor(), is(javafx.scene.Cursor.HAND));
        // a titleless shape otherwise gets mouseTransparent(true) from installTooltip, which would make it
        // unhoverable - so its own cursor could never actually show
        assertThat(shape.isMouseTransparent(), is(false));
    }

    /**
     * The exact scenario a bug report caught: a bare titleless shape with its own {@code cursor}, no wrapping
     * {@code <a>} to reset {@code mouseTransparent} the way {@code SvgAnchor} does for its own content.
     */
    @Test
    public void testStandaloneShapeWithItsOwnCursorIsStillHoverable() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setCursor("pointer");

        Shape shape = firstShape(render(groupOf(rect)));
        assertThat(shape.getCursor(), is(javafx.scene.Cursor.HAND));
        assertThat(shape.isMouseTransparent(), is(false));
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

    // --- stroke-dasharray (#114) -------------------------------------------

    /**
     * JavaFX rejects a dash array whose entries are all zero, throwing {@code IllegalArgumentException} from inside
     * rendering rather than from the setter - so this used to escape {@code createGraphic} into whatever application
     * was rendering the document. SVG 1.1 says the opposite: a sum of zero is "rendered as if a value of none were
     * specified", i.e. an ordinary solid stroke.
     */
    @Test
    public void testAnAllZeroDashArrayRendersSolidRatherThanThrowing() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStrokeDashArray(List.of(0.0));

        assertThat(firstShape(render(groupOf(rect))).getStrokeDashArray(), is(empty()));
    }

    @Test
    public void testAMultiEntryAllZeroDashArrayAlsoRendersSolid() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStrokeDashArray(List.of(0.0, 0.0, 0.0));

        assertThat(firstShape(render(groupOf(rect))).getStrokeDashArray(), is(empty()));
    }

    /** JavaFX throws {@code "negative dash length"} here; SVG puts the declaration in error, which means solid. */
    @Test
    public void testADashArrayContainingANegativeRendersSolidRatherThanThrowing() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStrokeDashArray(List.of(5.0, -2.0));

        assertThat(firstShape(render(groupOf(rect))).getStrokeDashArray(), is(empty()));
    }

    /** The whole point is to reject only what JavaFX cannot take - a real dash pattern must still get through. */
    @Test
    public void testAValidDashArrayIsStillApplied() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStrokeDashArray(List.of(5.0, 2.0));

        assertThat(firstShape(render(groupOf(rect))).getStrokeDashArray(), is(List.of(5.0, 2.0)));
    }

    /**
     * A zero entry is only fatal when every entry is zero - {@code "5 0"} sums above zero, is a legal (if degenerate)
     * pattern, and JavaFX accepts it.
     */
    @Test
    public void testAZeroEntryAlongsideANonZeroOneIsStillApplied() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStrokeDashArray(List.of(5.0, 0.0));

        assertThat(firstShape(render(groupOf(rect))).getStrokeDashArray(), is(List.of(5.0, 0.0)));
    }

    /**
     * SVG's "repeat an odd list to yield an even number of values" rule is left to JavaFX, which cycles an
     * odd-length array so dash and gap roles swap on each pass - pixel-for-pixel identical to the doubled list.
     * This pins that the list is passed through as-authored rather than silently rewritten.
     */
    @Test
    public void testAnOddLengthDashArrayIsPassedThroughUnchanged() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStrokeDashArray(List.of(5.0, 2.0, 5.0));

        assertThat(firstShape(render(groupOf(rect))).getStrokeDashArray(), is(List.of(5.0, 2.0, 5.0)));
    }

    /** {@code stroke-dasharray="none"} parses to an empty list, which must mean solid, not an empty dash pattern. */
    @Test
    public void testDashArrayNoneRendersSolid() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setStrokeDashArray(List.of());

        assertThat(firstShape(render(groupOf(rect))).getStrokeDashArray(), is(empty()));
    }

    /**
     * The end-to-end proof, and the reason the assertions above are not sufficient on their own: JavaFX accepts a
     * bad dash array quite happily at the setter and only throws once something paints it. So a document like this
     * would not fail in {@code createGraphic} at all - it would blow up later, on the JavaFX Application Thread,
     * mid-render, a long way from the value that caused it. This rasterises to prove that no longer happens.
     */
    @Test
    public void testAnAllZeroDashArrayActuallyRasterises() throws Exception {
        JavaFxTestSupport.ensureStarted();
        SvgRectangle rect = new SvgRectangle();
        rect.setWidth(new Size(40, SizeUnits.PX));
        rect.setHeight(new Size(20, SizeUnits.PX));
        rect.setStroke(Color.BLACK);
        rect.setStrokeDashArray(List.of(0.0));

        WritableImage image = JavaFxTestSupport.onFxThread(() -> {
            Group rendered = render(groupOf(rect));
            new Scene(rendered);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.WHITE);
            return rendered.snapshot(params, new WritableImage(50, 30));
        });

        // asserting that something was painted, rather than on any particular pixel: the claim under test is that
        // the render completes at all, and pinning an exact colour would only make this hostage to edge antialiasing
        int painted = 0;
        for (int y = 0; y < 30; y++) {
            for (int x = 0; x < 50; x++) {
                if (!image.getPixelReader().getColor(x, y).equals(Color.WHITE)) {
                    painted++;
                }
            }
        }
        assertThat("the shape should have rendered rather than thrown", painted > 0, is(true));
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

    // --- clip-path -----------------------------------------------------------

    @Test
    public void testAbsentClipPathLeavesTheNodeUnclipped() throws Exception {
        assertThat(firstShape(render(groupOf(new SvgRectangle()))).getClip(), is(nullValue()));
    }

    /**
     * {@code clip-path} is not inherited (confirmed absent from {@code SvgInheritedStyle}'s inherited set) - a
     * group's own clip must not cascade onto a child that declares none of its own, unlike {@code fill}/
     * {@code cursor} above.
     */
    @Test
    public void testClipPathIsNotInheritedByAChildThatDeclaresNone() throws Exception {
        SvgClipPath clipPath = new SvgClipPath();
        clipPath.setId("clip");
        clipPath.getContent().add(new SvgCircle());

        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(clipPath);
        SvgGroup group = new SvgGroup();
        group.setClipPath("url(#clip)");
        SvgRectangle rect = new SvgRectangle();
        group.getContent().add(rect);
        svg.getContent().add(group);

        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        Group renderedGroup = group.createGraphic(context);
        assertThat(renderedGroup.getClip(), is(notNullValue()));
        assertThat(firstShape(renderedGroup).getClip(), is(nullValue()));
    }

    // --- mask ----------------------------------------------------------------

    /**
     * {@code applyMask} is a no-op (returns the exact same node) when {@code mask} is absent - it must never build a
     * replacement {@link Node} it doesn't need.
     */
    @Test
    public void testAbsentMaskReturnsTheSameNode() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        SvgGraphic svg = new SvgGraphic();
        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);

        Node node = rect.createGraphic(context);
        assertThat(rect.applyMask(context, node), is(sameInstance(node)));
    }

    // --- filter ----------------------------------------------------------------

    /**
     * {@code applyFilter} is a no-op when {@code filter} is absent - unlike {@code applyClip}/{@code applyMask},
     * there is no separate property to assert against directly, so this just confirms nothing throws and no effect
     * is set.
     */
    @Test
    public void testAbsentFilterLeavesTheNodeUnaffected() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        SvgGraphic svg = new SvgGraphic();
        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);

        Node node = rect.createGraphic(context);
        rect.applyFilter(context, node);
        assertThat(node.getEffect(), is(nullValue()));
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
