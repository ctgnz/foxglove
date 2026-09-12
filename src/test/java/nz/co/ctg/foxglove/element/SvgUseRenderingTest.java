package nz.co.ctg.foxglove.element;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.ViewBox;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;

/**
 * Exercises #19's acceptance criteria: {@code <use>} of a shape, a group, a {@code <symbol>} and a nested
 * {@code <svg>} each render correctly, the referenced content inherits from the {@code <use>} site rather than from
 * where it was declared, {@code <defs>} content is reachable only by reference, and an ancestor-referencing
 * {@code <use>} is rejected without hanging. Also covers #95: a reference cycle reachable only through
 * {@code xlink:href} chains between otherwise-unrelated elements (not each other's static-containment ancestor,
 * so the ancestor check alone misses it) must be rejected the same way, without a {@code StackOverflowError} - and
 * two independent siblings legitimately reusing the same target must not be mistaken for one.
 */
public class SvgUseRenderingTest {

    private static Size px(double value) {
        return new Size(value, SizeUnits.PX);
    }

    @Test
    public void testUseOfAShapeAppliesXYAndTransform() throws Exception {
        SvgRectangle target = rect("r");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#r");
        use.setX(px(10));
        use.setY(px(20));
        use.setTransform("scale(2)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(use);

        Group renderedRoot = render(root);
        assertThat(renderedRoot.getChildren(), hasSize(1));
        Group renderedUse = (Group) renderedRoot.getChildren().get(0);
        // per spec, translate(x,y) is appended to the end of <use>'s own transform list, so it applies to the
        // content first (inner) and <use>'s own transform - here scale(2) - wraps around that result (outer):
        // (0,0) -> translate(10,20) -> (10,20) -> scale(2) -> (20,40)
        Point2D origin = renderedUse.localToParent(0, 0);
        assertThat(origin.getX(), closeTo(20, 1e-9));
        assertThat(origin.getY(), closeTo(40, 1e-9));

        Rectangle renderedShape = (Rectangle) renderedUse.getChildren().get(0);
        assertThat(renderedShape.getId(), is("r"));
    }

    /**
     * A regression case for a bug caught by visual inspection: rotating around a pivot that names the same point
     * {@code x}/{@code y} translates content to should be a no-op, since the content ends up centred exactly on
     * that pivot - {@code translate(x,y)} has to apply before the rotation, not after, or the content flies off to
     * wherever rotating the origin around a distant pivot happens to land.
     */
    @Test
    public void testRotateAroundTheUsesOwnPivotIsANoOp() throws Exception {
        SvgRectangle target = rect("dot");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#dot");
        use.setX(px(130));
        use.setY(px(40));
        use.setTransform("rotate(45 130 40)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(use);

        Group renderedUse = (Group) render(root).getChildren().get(0);
        Point2D origin = renderedUse.localToParent(0, 0);
        assertThat(origin.getX(), closeTo(130, 1e-9));
        assertThat(origin.getY(), closeTo(40, 1e-9));
    }

    @Test
    public void testUseOfAGroupRendersItsChildren() throws Exception {
        SvgGroup target = new SvgGroup();
        target.setId("g1");
        target.getContent().add(rect("a"));
        target.getContent().add(rect("b"));
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#g1");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(use);

        Group renderedUse = (Group) render(root).getChildren().get(0);
        Group renderedTarget = (Group) renderedUse.getChildren().get(0);
        assertThat(renderedTarget.getChildren(), hasSize(2));
    }

    @Test
    public void testUseOfASymbolScalesByViewBoxAndUseSize() throws Exception {
        SvgSymbol symbol = new SvgSymbol();
        symbol.setId("sym");
        symbol.setViewBox(viewBox(0, 0, 100, 100));
        symbol.getContent().add(new SvgRectangle());
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(symbol);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#sym");
        use.setWidth(px(50));
        use.setHeight(px(50));

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(use);

        Group renderedUse = (Group) render(root).getChildren().get(0);
        Group renderedSymbol = (Group) renderedUse.getChildren().get(0);
        Affine transform = (Affine) renderedSymbol.getTransforms().get(0);
        assertThat(transform.getMxx(), closeTo(0.5, 1e-9));
        assertThat(transform.getMyy(), closeTo(0.5, 1e-9));
    }

    @Test
    public void testUseOfASymbolWithoutSizeDefaultsToTheCurrentViewport() throws Exception {
        SvgSymbol symbol = new SvgSymbol();
        symbol.setId("sym");
        symbol.setViewBox(viewBox(0, 0, 100, 100));
        symbol.setPreserveAspectRatio("none");
        symbol.getContent().add(new SvgRectangle());
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(symbol);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#sym");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(use);

        // render() wraps root in an SvgGraphic with no width/height of its own, so the UA default 300x150 viewport
        // applies - <use> declares no width/height of its own, so it should default to exactly that viewport.
        Group renderedUse = (Group) render(root).getChildren().get(0);
        Group renderedSymbol = (Group) renderedUse.getChildren().get(0);
        Affine transform = (Affine) renderedSymbol.getTransforms().get(0);
        assertThat(transform.getMxx(), closeTo(3.0, 1e-9));
        assertThat(transform.getMyy(), closeTo(1.5, 1e-9));
    }

    @Test
    public void testUseOfANestedSvgOverridesItsWidthAndHeight() throws Exception {
        SvgGraphic target = new SvgGraphic();
        target.setId("nested");
        target.setWidth(px(100));
        target.setHeight(px(100));
        SvgRectangle child = new SvgRectangle();
        child.setWidth(new Size(50, SizeUnits.PERCENT));
        target.getContent().add(child);
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#nested");
        use.setWidth(px(400));
        use.setHeight(px(400));

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(use);

        Group renderedUse = (Group) render(root).getChildren().get(0);
        Group renderedNested = (Group) renderedUse.getChildren().get(0);
        Rectangle renderedChild = (Rectangle) renderedNested.getChildren().get(0);
        // 50% of <use>'s overridden 400 width, not the target's own declared 100
        assertThat(renderedChild.getWidth(), closeTo(200, 1e-9));
    }

    @Test
    public void testReferencedContentInheritsFromTheUseSiteNotItsDeclaration() throws Exception {
        SvgRectangle target = rect("shape");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#shape");

        SvgGroup styledAncestor = new SvgGroup();
        styledAncestor.setFill(Color.RED);
        styledAncestor.getContent().add(use);

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(styledAncestor);

        Group renderedRoot = render(root);
        assertThat(renderedRoot.getChildren(), hasSize(1));
        Group renderedAncestor = (Group) renderedRoot.getChildren().get(0);
        Group renderedUse = (Group) renderedAncestor.getChildren().get(0);
        Rectangle renderedShape = (Rectangle) renderedUse.getChildren().get(0);
        assertThat(renderedShape.getFill(), is(Color.RED));
    }

    @Test
    public void testDefinitionsContentIsReachableOnlyByReference() throws Exception {
        SvgRectangle target = rect("shape");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#shape");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(use);

        Group renderedRoot = render(root);
        // defs itself contributes no rendered node - only the <use> does
        assertThat(renderedRoot.getChildren(), hasSize(1));
        Group renderedUse = (Group) renderedRoot.getChildren().get(0);
        assertThat(((Rectangle) renderedUse.getChildren().get(0)).getId(), is("shape"));
    }

    @Test
    public void testUseReferencingItsOwnAncestorRendersEmptyWithoutHanging() throws Exception {
        SvgGroup outer = new SvgGroup();
        outer.setId("outer");
        SvgUse use = new SvgUse();
        use.setXlinkHref("#outer");
        outer.getContent().add(use);

        Group renderedOuter = render(outer);
        Group renderedUse = (Group) renderedOuter.getChildren().get(0);
        assertThat(renderedUse.getChildren(), is(empty()));
    }

    @Test
    public void testSelfReferencingUseRendersEmptyWithoutHanging() throws Exception {
        SvgUse use = new SvgUse();
        use.setId("self");
        use.setXlinkHref("#self");
        SvgGroup root = new SvgGroup();
        root.getContent().add(use);

        Group renderedUse = (Group) render(root).getChildren().get(0);
        assertThat(renderedUse.getChildren(), is(empty()));
    }

    /**
     * A regression case for #95: two sibling {@code <use>} elements referencing each other - neither is the
     * other's static-containment ancestor, so {@code SvgElementIndex#isSelfOrAncestor} alone does not catch this;
     * without a chain-tracking guard this recurses (A -> B -> A -> B -> ...) until the stack overflows. {@code
     * useA} itself resolves fine (its target, {@code useB}, is not yet active) and renders a group containing
     * {@code useB}'s own result - it is {@code useB}'s own attempt to resolve back to {@code useA} (already active)
     * that gets rejected, one level down, rather than {@code useA} itself rendering nothing at all.
     */
    @Test
    public void testMutualSiblingReferenceCycleRendersEmptyWithoutHanging() throws Exception {
        SvgUse useA = new SvgUse();
        useA.setId("useA");
        useA.setXlinkHref("#useB");
        SvgUse useB = new SvgUse();
        useB.setId("useB");
        useB.setXlinkHref("#useA");

        SvgGroup root = new SvgGroup();
        root.getContent().add(useA);
        root.getContent().add(useB);

        Group renderedRoot = render(root);
        Group renderedA = (Group) renderedRoot.getChildren().get(0);
        assertThat(renderedA.getChildren(), hasSize(1));
        Group renderedAsB = (Group) renderedA.getChildren().get(0);
        assertThat(renderedAsB.getChildren(), is(empty()));
    }

    /**
     * A longer indirect cycle (A -> B -> C -> A) - the same class of bug as the mutual two-element case, just one
     * hop further, confirming the guard tracks the whole active chain (not only the immediately-previous element):
     * A and B each resolve fine, nesting three groups deep, with only C's own attempt to resolve back to A (already
     * active on this chain) rejected at the innermost level.
     */
    @Test
    public void testLongerIndirectReferenceCycleRendersEmptyWithoutHanging() throws Exception {
        SvgUse useA = new SvgUse();
        useA.setId("chainA");
        useA.setXlinkHref("#chainB");
        SvgUse useB = new SvgUse();
        useB.setId("chainB");
        useB.setXlinkHref("#chainC");
        SvgUse useC = new SvgUse();
        useC.setId("chainC");
        useC.setXlinkHref("#chainA");

        SvgGroup root = new SvgGroup();
        root.getContent().add(useA);
        root.getContent().add(useB);
        root.getContent().add(useC);

        Group renderedA = (Group) render(root).getChildren().get(0);
        Group renderedAsB = (Group) renderedA.getChildren().get(0);
        Group renderedAsC = (Group) renderedAsB.getChildren().get(0);
        assertThat(renderedAsC.getChildren(), is(empty()));
    }

    /**
     * The guard must not mistake two independent siblings legitimately reusing the same target for a cycle - a
     * common, perfectly valid idiom (e.g. reusing one {@code <symbol>} several times).
     */
    @Test
    public void testTwoSiblingUsesOfTheSameTargetIsNotMistakenForACycle() throws Exception {
        SvgRectangle target = rect("shared");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(target);

        SvgUse useA = new SvgUse();
        useA.setXlinkHref("#shared");
        SvgUse useB = new SvgUse();
        useB.setXlinkHref("#shared");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(useA);
        root.getContent().add(useB);

        Group renderedRoot = render(root);
        assertThat(((Group) renderedRoot.getChildren().get(0)).getChildren(), hasSize(1));
        assertThat(((Group) renderedRoot.getChildren().get(1)).getChildren(), hasSize(1));
    }

    @Test
    public void testUnresolvableHrefRendersEmptyWithoutThrowing() throws Exception {
        SvgUse use = new SvgUse();
        use.setXlinkHref("#missing");
        SvgGroup root = new SvgGroup();
        root.getContent().add(use);

        Group renderedUse = (Group) render(root).getChildren().get(0);
        assertThat(renderedUse.getChildren(), is(empty()));
    }

    private static SvgRectangle rect(String id) {
        SvgRectangle rect = new SvgRectangle();
        rect.setId(id);
        return rect;
    }

    private static ViewBox viewBox(double minX, double minY, double width, double height) {
        return new ViewBox(px(minX), px(minY), px(width), px(height));
    }

    private static Group render(SvgGroup root) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(root);
        return (Group) svg.createGroup().getChildren().get(0);
    }

}
