package nz.co.ctg.foxglove.element;

import org.junit.Test;

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
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;

/**
 * Exercises #19's acceptance criteria: {@code <use>} of a shape, a group, a {@code <symbol>} and a nested
 * {@code <svg>} each render correctly, the referenced content inherits from the {@code <use>} site rather than from
 * where it was declared, {@code <defs>} content is reachable only by reference, and an ancestor-referencing
 * {@code <use>} is rejected without hanging.
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
        assertThat(renderedUse.getTranslateX(), closeTo(10, 1e-9));
        assertThat(renderedUse.getTranslateY(), closeTo(20, 1e-9));
        assertThat(renderedUse.getTransforms(), hasSize(1));
        Scale scale = (Scale) renderedUse.getTransforms().get(0);
        assertThat(scale.getX(), closeTo(2, 1e-9));
        assertThat(scale.getY(), closeTo(2, 1e-9));

        Rectangle renderedShape = (Rectangle) renderedUse.getChildren().get(0);
        assertThat(renderedShape.getId(), is("r"));
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
