package nz.co.ctg.foxglove;

import org.junit.Test;

import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Render dispatch used to be a hard coded {@code instanceof} chain duplicated in {@code SvgGraphic} and
 * {@code SvgGroup}, which had already drifted: the root skipped invisible groups and shapes but always drew text,
 * while a group drew everything and merely hid itself. These cover the single dispatch that replaced it.
 */
public class ISvgContainerTest {

    @Test
    public void testRendersShapeChildren() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(rect("r"));
        svg.getContent().add(new SvgCircle());

        Group rendered = svg.createGroup();
        assertThat(rendered.getChildren(), hasSize(2));
        assertThat(rendered.getChildren().get(0), is(instanceOf(Rectangle.class)));
        assertThat(rendered.getChildren().get(1), is(instanceOf(Circle.class)));
    }

    @Test
    public void testRendersNestedGroups() throws Exception {
        SvgGroup inner = new SvgGroup();
        inner.getContent().add(rect("deep"));
        SvgGroup outer = new SvgGroup();
        outer.getContent().add(inner);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(outer);

        Group renderedOuter = (Group) svg.createGroup().getChildren().get(0);
        Group renderedInner = (Group) renderedOuter.getChildren().get(0);
        assertThat(renderedInner.getChildren(), hasSize(1));
        assertThat(renderedInner.getChildren().get(0).getId(), is("deep"));
    }

    @Test
    public void testPreservesDocumentOrder() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(rect("first"));
        svg.getContent().add(rect("second"));
        svg.getContent().add(rect("third"));

        Group rendered = svg.createGroup();
        assertThat(rendered.getChildren().get(0).getId(), is("first"));
        assertThat(rendered.getChildren().get(1).getId(), is("second"));
        assertThat(rendered.getChildren().get(2).getId(), is("third"));
    }

    /**
     * Elements that exist to be referenced or to describe are excluded by not implementing {@link FxGraphic}, rather
     * than by being missing from a list of types the container knows how to draw.
     */
    @Test
    public void testSkipsNonRenderableChildren() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(new SvgTitle());
        svg.getContent().add(new SvgDescription());
        svg.getContent().add(new SvgMetadata());
        svg.getContent().add(new SvgAnimateAttribute());
        svg.getContent().add(new SvgLinearGradient());

        assertThat(svg.createGroup().getChildren(), is(empty()));
    }

    @Test
    public void testDefinitionsContentIsNotRendered() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(rect("defined"));
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(defs);

        assertThat(svg.createGroup().getChildren(), is(empty()));
    }

    /**
     * Any element implementing {@link FxGraphic} is rendered, with no registration anywhere.
     */
    @Test
    public void testAnyFxGraphicIsRendered() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(new CustomGraphic());

        Group rendered = svg.createGroup();
        assertThat(rendered.getChildren(), hasSize(1));
        assertThat(rendered.getChildren().get(0).getId(), is("custom"));
    }

    // --- display -----------------------------------------------------------

    @Test
    public void testDisplayNoneShapeIsAbsentAtRoot() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(hiddenRect());
        svg.getContent().add(rect("shown"));

        Group rendered = svg.createGroup();
        assertThat(rendered.getChildren(), hasSize(1));
        assertThat(rendered.getChildren().get(0).getId(), is("shown"));
    }

    @Test
    public void testDisplayNoneShapeIsAbsentInsideAGroup() throws Exception {
        SvgGroup group = new SvgGroup();
        group.getContent().add(hiddenRect());
        group.getContent().add(rect("shown"));
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(group);

        Group rendered = (Group) svg.createGroup().getChildren().get(0);
        assertThat(rendered.getChildren(), hasSize(1));
        assertThat(rendered.getChildren().get(0).getId(), is("shown"));
    }

    /**
     * The acceptance criterion for the rework: the two dispatch sites disagreed about this, so the same content has
     * to produce the same outcome whether it sits at the root or inside a group.
     */
    @Test
    public void testDisplayNoneBehavesTheSameAtRootAndNested() throws Exception {
        SvgGraphic atRoot = new SvgGraphic();
        atRoot.getContent().add(hiddenRect());
        atRoot.getContent().add(rect("shown"));

        SvgGroup group = new SvgGroup();
        group.getContent().add(hiddenRect());
        group.getContent().add(rect("shown"));
        SvgGraphic nested = new SvgGraphic();
        nested.getContent().add(group);

        Group rootChildren = atRoot.createGroup();
        Group nestedChildren = (Group) nested.createGroup().getChildren().get(0);
        assertThat(rootChildren.getChildren().size(), is(nestedChildren.getChildren().size()));
        assertThat(rootChildren.getChildren().get(0).getId(), is(nestedChildren.getChildren().get(0).getId()));
    }

    /**
     * {@code display="none"} removes the element from the scene graph along with everything inside it, rather than
     * adding a hidden node.
     */
    @Test
    public void testDisplayNoneGroupIsAbsentWithItsChildren() throws Exception {
        SvgGroup hidden = new SvgGroup();
        hidden.setDisplay("none");
        hidden.getContent().add(rect("buried"));
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(hidden);
        svg.getContent().add(rect("shown"));

        Group rendered = svg.createGroup();
        assertThat(rendered.getChildren(), hasSize(1));
        assertThat(rendered.getChildren().get(0).getId(), is("shown"));
    }

    private static SvgRectangle rect(String id) {
        SvgRectangle rect = new SvgRectangle();
        rect.setId(id);
        return rect;
    }

    private static SvgRectangle hiddenRect() {
        SvgRectangle rect = rect("hidden");
        rect.setDisplay("none");
        return rect;
    }

    private static class CustomGraphic extends AbstractSvgStylable implements FxGraphic<Rectangle> {

        @Override
        public Rectangle createGraphic(ISvgStylable parent) {
            Rectangle rectangle = new Rectangle(1, 1);
            rectangle.setId("custom");
            return rectangle;
        }

    }

}
