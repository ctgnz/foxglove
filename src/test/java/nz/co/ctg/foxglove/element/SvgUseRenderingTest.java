package nz.co.ctg.foxglove.element;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.ViewBox;

/**
 * Exercises #19's acceptance criteria: {@code <use>} of a shape, a group, a {@code <symbol>} and a nested {@code <svg>} each render correctly, the referenced content inherits from
 * the {@code <use>} site rather than from where it was declared, {@code <defs>} content is reachable only by reference, and an ancestor-referencing {@code <use>} is rejected
 * without hanging. Also covers #95: a reference cycle reachable only through {@code xlink:href} chains between otherwise-unrelated elements (not each other's static-containment
 * ancestor, so the ancestor check alone misses it) must be rejected the same way, without a {@code StackOverflowError} - and two independent siblings legitimately reusing the same
 * target must not be mistaken for one. Also covers #101: {@code <use>} of an {@code <image>} target, which needs the JavaFX Application Thread to construct (see
 * {@link JavaFxTestSupport}) unlike every other target type above. Also covers #175: {@code <use>} of a target resolved from another document, and that a cross-document reference
 * cycle terminates the same way an in-document one already does.
 */
public class SvgUseRenderingTest {

    @BeforeAll
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    private static Size px(double value) {
        return new Size(value, SizeUnits.PX);
    }

    @Test
    public void testUseOfAShapeAppliesXYAndTransform() throws Exception {
        SvgRectangle target = rect("r");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#r");
        use.setX(px(10));
        use.setY(px(20));
        use.setTransform("scale(2)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(use);

        Group renderedRoot = render(root);
        assertThat(renderedRoot.getChildren(), hasSize(1));
        Group renderedUse = (Group) renderedRoot.getChildren()
            .get(0);
        // per spec, translate(x,y) is appended to the end of <use>'s own transform list, so it applies to the
        // content first (inner) and <use>'s own transform - here scale(2) - wraps around that result (outer):
        // (0,0) -> translate(10,20) -> (10,20) -> scale(2) -> (20,40)
        Point2D origin = renderedUse.localToParent(0, 0);
        assertThat(origin.getX(), closeTo(20, 1e-9));
        assertThat(origin.getY(), closeTo(40, 1e-9));

        Rectangle renderedShape = (Rectangle) renderedUse.getChildren()
            .get(0);
        assertThat(renderedShape.getId(), is("r"));
    }

    /**
     * A regression case for a bug caught by visual inspection: rotating around a pivot that names the same point {@code x}/{@code y} translates content to should be a no-op, since
     * the content ends up centred exactly on that pivot - {@code translate(x,y)} has to apply before the rotation, not after, or the content flies off to wherever rotating the
     * origin around a distant pivot happens to land.
     */
    @Test
    public void testRotateAroundTheUsesOwnPivotIsANoOp() throws Exception {
        SvgRectangle target = rect("dot");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#dot");
        use.setX(px(130));
        use.setY(px(40));
        use.setTransform("rotate(45 130 40)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(use);

        Group renderedUse = (Group) render(root).getChildren()
            .get(0);
        Point2D origin = renderedUse.localToParent(0, 0);
        assertThat(origin.getX(), closeTo(130, 1e-9));
        assertThat(origin.getY(), closeTo(40, 1e-9));
    }

    /**
     * #101: {@code <use>} of an {@code <image>} target - found via the W3C conformance harness's own {@code struct-use-01-t}, which showed a blank row where the reference PNG had
     * an embedded raster swatch. Reproducing that exact document directly (not just this minimal in-memory case) found the underlying rendering already correct -
     * {@link SvgUse#buildReferenced}'s generic {@code FxGraphic<?>} dispatch already calls {@link SvgImage#createGraphic} like any other target type, and
     * {@link nz.co.ctg.foxglove.RenderContext}'s base URI already threads through {@code <use>}'s own {@code resolveChild}/{@code withActiveUseTarget} calls unchanged, the same as
     * every other {@code withXxx} on that class - there was nothing left to fix. No test exercised this specific target-type/reference combination before, so nothing had ever
     * pinned the fix in place; this does. See {@link #testUseOfAnImageWithARelativeHrefParsedFromAFile} for the base-URI-threading half of this, which a {@code data:} URI alone
     * (this test) cannot exercise.
     */
    @Test
    public void testUseOfAnImageRendersTheReferencedRasterImage() throws Exception {
        // a real 1x1 transparent PNG, the same fixture SvgImageRenderingTest uses
        String dotPng = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4nGMAAQAABQAB0NcObQAAAABJRU5ErkJggg==";
        SvgImage target = new SvgImage();
        target.setId("pic");
        target.setXlinkHref(dotPng);
        target.setWidth(px(10));
        target.setHeight(px(10));
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#pic");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(use);

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(root);
        Node rendered = onFxThread(svg::createGroup);

        ImageView imageView = findImageView(rendered);
        assertThat("the referenced <image> actually decoded, not an error placeholder", imageView.getImage(), notNullValue());
        assertThat(imageView.getImage()
            .isError(), is(false));
    }

    /**
     * The base-URI-threading half of #101 - a relative {@code xlink:href} on the referenced {@code <image>} must still resolve against the document's own base URI (see
     * {@link FoxgloveParser#parseFile}) when reached through a {@code <use>} indirection, exactly the structure the real {@code struct-use-01-t} conformance document uses.
     */
    @Test
    public void testUseOfAnImageWithARelativeHrefParsedFromAFile() throws Exception {
        SvgGraphic svg = new FoxgloveParser().parseFile("/image-relative-via-use.svg");
        assertThat(svg.getBaseUri(), notNullValue());

        Node rendered = onFxThread(svg::createGroup);
        ImageView imageView = findImageView(rendered);
        assertThat(imageView.getImage(), notNullValue());
        assertThat(imageView.getImage()
            .isError(), is(false));
        assertThat(imageView.getImage()
            .getWidth(), closeTo(4, 1e-9));
    }

    private static ImageView findImageView(Node node) {
        if (node instanceof ImageView imageView) {
            return imageView;
        }
        if (node instanceof Group group) {
            for (Node child : group.getChildren()) {
                ImageView found = findImageView(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    @Test
    public void testUseOfAGroupRendersItsChildren() throws Exception {
        SvgGroup target = new SvgGroup();
        target.setId("g1");
        target.getContent()
            .add(rect("a"));
        target.getContent()
            .add(rect("b"));
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#g1");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(use);

        Group renderedUse = (Group) render(root).getChildren()
            .get(0);
        Group renderedTarget = (Group) renderedUse.getChildren()
            .get(0);
        assertThat(renderedTarget.getChildren(), hasSize(2));
    }

    @Test
    public void testUseOfASymbolScalesByViewBoxAndUseSize() throws Exception {
        SvgSymbol symbol = new SvgSymbol();
        symbol.setId("sym");
        symbol.setViewBox(viewBox(0, 0, 100, 100));
        symbol.getContent()
            .add(new SvgRectangle());
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(symbol);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#sym");
        use.setWidth(px(50));
        use.setHeight(px(50));

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(use);

        Group renderedUse = (Group) render(root).getChildren()
            .get(0);
        Group renderedSymbol = (Group) renderedUse.getChildren()
            .get(0);
        Affine transform = (Affine) renderedSymbol.getTransforms()
            .get(0);
        assertThat(transform.getMxx(), closeTo(0.5, 1e-9));
        assertThat(transform.getMyy(), closeTo(0.5, 1e-9));
    }

    @Test
    public void testUseOfASymbolWithoutSizeDefaultsToTheCurrentViewport() throws Exception {
        SvgSymbol symbol = new SvgSymbol();
        symbol.setId("sym");
        symbol.setViewBox(viewBox(0, 0, 100, 100));
        symbol.setPreserveAspectRatio("none");
        symbol.getContent()
            .add(new SvgRectangle());
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(symbol);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#sym");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(use);

        // render() wraps root in an SvgGraphic with no width/height of its own, so the UA default 300x150 viewport
        // applies - <use> declares no width/height of its own, so it should default to exactly that viewport.
        Group renderedUse = (Group) render(root).getChildren()
            .get(0);
        Group renderedSymbol = (Group) renderedUse.getChildren()
            .get(0);
        Affine transform = (Affine) renderedSymbol.getTransforms()
            .get(0);
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
        target.getContent()
            .add(child);
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#nested");
        use.setWidth(px(400));
        use.setHeight(px(400));

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(use);

        Group renderedUse = (Group) render(root).getChildren()
            .get(0);
        Group renderedNested = (Group) renderedUse.getChildren()
            .get(0);
        Rectangle renderedChild = (Rectangle) renderedNested.getChildren()
            .get(0);
        // 50% of <use>'s overridden 400 width, not the target's own declared 100
        assertThat(renderedChild.getWidth(), closeTo(200, 1e-9));
    }

    /**
     * An {@code <svg>} reached through {@code <use>} is a nested viewport, not the document root, so its own {@code x}/{@code y} still position it - the rule #113 added applies to
     * being outermost, not to {@code <svg>} elements in general, and this is the path most likely to be broken by confusing the two.
     */
    @Test
    public void testUseOfANestedSvgStillAppliesTheTargetsOwnXAndY() throws Exception {
        SvgGraphic target = new SvgGraphic();
        target.setId("nested");
        target.setX(px(25));
        target.setY(px(35));
        target.setWidth(px(100));
        target.setHeight(px(100));
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#nested");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(use);

        Group renderedNested = (Group) ((Group) render(root).getChildren()
            .get(0)).getChildren()
            .get(0);
        assertThat(renderedNested.getTranslateX(), closeTo(25, 1e-9));
        assertThat(renderedNested.getTranslateY(), closeTo(35, 1e-9));
    }

    @Test
    public void testReferencedContentInheritsFromTheUseSiteNotItsDeclaration() throws Exception {
        SvgRectangle target = rect("shape");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#shape");

        SvgGroup styledAncestor = new SvgGroup();
        styledAncestor.setFill(Color.RED);
        styledAncestor.getContent()
            .add(use);

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(styledAncestor);

        Group renderedRoot = render(root);
        assertThat(renderedRoot.getChildren(), hasSize(1));
        Group renderedAncestor = (Group) renderedRoot.getChildren()
            .get(0);
        Group renderedUse = (Group) renderedAncestor.getChildren()
            .get(0);
        Rectangle renderedShape = (Rectangle) renderedUse.getChildren()
            .get(0);
        assertThat(renderedShape.getFill(), is(Color.RED));
    }

    @Test
    public void testDefinitionsContentIsReachableOnlyByReference() throws Exception {
        SvgRectangle target = rect("shape");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        SvgUse use = new SvgUse();
        use.setXlinkHref("#shape");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(use);

        Group renderedRoot = render(root);
        // defs itself contributes no rendered node - only the <use> does
        assertThat(renderedRoot.getChildren(), hasSize(1));
        Group renderedUse = (Group) renderedRoot.getChildren()
            .get(0);
        assertThat(((Rectangle) renderedUse.getChildren()
            .get(0)).getId(), is("shape"));
    }

    @Test
    public void testUseReferencingItsOwnAncestorRendersEmptyWithoutHanging() throws Exception {
        SvgGroup outer = new SvgGroup();
        outer.setId("outer");
        SvgUse use = new SvgUse();
        use.setXlinkHref("#outer");
        outer.getContent()
            .add(use);

        Group renderedOuter = render(outer);
        Group renderedUse = (Group) renderedOuter.getChildren()
            .get(0);
        assertThat(renderedUse.getChildren(), is(empty()));
    }

    @Test
    public void testSelfReferencingUseRendersEmptyWithoutHanging() throws Exception {
        SvgUse use = new SvgUse();
        use.setId("self");
        use.setXlinkHref("#self");
        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(use);

        Group renderedUse = (Group) render(root).getChildren()
            .get(0);
        assertThat(renderedUse.getChildren(), is(empty()));
    }

    /**
     * A regression case for #95: two sibling {@code <use>} elements referencing each other - neither is the other's static-containment ancestor, so
     * {@code SvgElementIndex#isSelfOrAncestor} alone does not catch this; without a chain-tracking guard this recurses (A -> B -> A -> B -> ...) until the stack overflows. {@code
     * useA} itself resolves fine (its target, {@code useB}, is not yet active) and renders a group containing {@code useB}'s own result - it is {@code useB}'s own attempt to
     * resolve back to {@code useA} (already active) that gets rejected, one level down, rather than {@code useA} itself rendering nothing at all.
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
        root.getContent()
            .add(useA);
        root.getContent()
            .add(useB);

        Group renderedRoot = render(root);
        Group renderedA = (Group) renderedRoot.getChildren()
            .get(0);
        assertThat(renderedA.getChildren(), hasSize(1));
        Group renderedAsB = (Group) renderedA.getChildren()
            .get(0);
        assertThat(renderedAsB.getChildren(), is(empty()));
    }

    /**
     * A longer indirect cycle (A -> B -> C -> A) - the same class of bug as the mutual two-element case, just one hop further, confirming the guard tracks the whole active chain
     * (not only the immediately-previous element): A and B each resolve fine, nesting three groups deep, with only C's own attempt to resolve back to A (already active on this
     * chain) rejected at the innermost level.
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
        root.getContent()
            .add(useA);
        root.getContent()
            .add(useB);
        root.getContent()
            .add(useC);

        Group renderedA = (Group) render(root).getChildren()
            .get(0);
        Group renderedAsB = (Group) renderedA.getChildren()
            .get(0);
        Group renderedAsC = (Group) renderedAsB.getChildren()
            .get(0);
        assertThat(renderedAsC.getChildren(), is(empty()));
    }

    /**
     * The guard must not mistake two independent siblings legitimately reusing the same target for a cycle - a common, perfectly valid idiom (e.g. reusing one {@code <symbol>}
     * several times).
     */
    @Test
    public void testTwoSiblingUsesOfTheSameTargetIsNotMistakenForACycle() throws Exception {
        SvgRectangle target = rect("shared");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(target);

        SvgUse useA = new SvgUse();
        useA.setXlinkHref("#shared");
        SvgUse useB = new SvgUse();
        useB.setXlinkHref("#shared");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(useA);
        root.getContent()
            .add(useB);

        Group renderedRoot = render(root);
        assertThat(((Group) renderedRoot.getChildren()
            .get(0)).getChildren(), hasSize(1));
        assertThat(((Group) renderedRoot.getChildren()
            .get(1)).getChildren(), hasSize(1));
    }

    /**
     * #175: {@code <use>} of a target resolved from another document - the referenced {@code <rect>}'s own {@code fill="url(#grad)"} is same-document relative to *that* file, not
     * this test's referencing document, which declares no {@code #grad} at all. Rendering it as {@link Color#BLACK} (the unresolved-paint default, see {@code
     * SvgPaintResolver.INITIAL_COLOR}) rather than the actual {@link LinearGradient} would mean the target's content rendered against the wrong document's index - the specific
     * regression {@link nz.co.ctg.foxglove.RenderContext#withElementIndex} exists to prevent.
     */
    @Test
    public void testUseOfAnExternalTargetResolvesTheTargetsOwnReferencesAgainstItsOwnDocument() throws Exception {
        SvgGraphic svg = new FoxgloveParser().parseFile("/external-reference.svg");
        assertThat(svg.getBaseUri(), notNullValue());

        Node rendered = onFxThread(svg::createGroup);
        Rectangle shape = findRectangle(rendered);
        assertThat("the target's own gradient reference resolved against its own document", shape.getFill(), instanceOf(LinearGradient.class));
    }

    /**
     * #175: a cross-document {@code <use>} cycle (A references B, B references back into A) must terminate the same way an in-document cycle already does (see
     * {@link #testMutualSiblingReferenceCycleRendersEmptyWithoutHanging}), not hang or overflow the stack.
     */
    @Test
    public void testCrossDocumentUseCycleTerminatesWithoutHanging() throws Exception {
        SvgGraphic svg = new FoxgloveParser().parseFile("/cycle-a.svg");
        assertThat(svg.getBaseUri(), notNullValue());

        Node rendered = onFxThread(svg::createGroup);
        assertThat(rendered, notNullValue());
    }

    private static Rectangle findRectangle(Node node) {
        if (node instanceof Rectangle rectangle) {
            return rectangle;
        }
        if (node instanceof Group group) {
            for (Node child : group.getChildren()) {
                Rectangle found = findRectangle(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    @Test
    public void testUnresolvableHrefRendersEmptyWithoutThrowing() throws Exception {
        SvgUse use = new SvgUse();
        use.setXlinkHref("#missing");
        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(use);

        Group renderedUse = (Group) render(root).getChildren()
            .get(0);
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
        svg.getContent()
            .add(root);
        return (Group) svg.createGroup()
            .getChildren()
            .get(0);
    }

}
