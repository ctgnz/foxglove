package nz.co.ctg.foxglove.element;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * Exercises #23's {@code <a>} acceptance criterion: content renders and invokes a supplied activation callback on click.
 */
public class SvgAnchorRenderingTest {

    @Test
    public void testContentRendersWithoutAHandler() throws Exception {
        SvgAnchor anchor = new SvgAnchor();
        anchor.setXlinkHref("#target");
        anchor.getContent()
            .add(rect("child"));

        Group rendered = render(anchor, null);
        assertThat(rendered.getChildren(), hasSize(1));
        assertThat(rendered.getCursor(), nullValue());
    }

    @Test
    public void testClickInvokesTheActivationHandlerWithThisAnchor() throws Exception {
        SvgAnchor anchor = new SvgAnchor();
        anchor.setXlinkHref("#target");
        anchor.getContent()
            .add(rect("child"));

        AtomicReference<SvgAnchor> activated = new AtomicReference<>();
        Group rendered = render(anchor, activated::set);

        click(rendered);
        assertThat(activated.get(), is(anchor));
    }

    @Test
    public void testATitledChildIsStillClickableDespiteInstallTooltipsMouseTransparency() throws Exception {
        // a shape WITH a title would otherwise get a Tooltip, not mouseTransparent(true) - but a titleless one
        // (the common case, exercised by the other tests here) is exactly what installTooltip makes unpickable
        SvgAnchor anchor = new SvgAnchor();
        anchor.setXlinkHref("#target");
        SvgRectangle child = rect("child");
        anchor.getContent()
            .add(child);

        AtomicReference<SvgAnchor> activated = new AtomicReference<>();
        Group rendered = render(anchor, activated::set);

        Node childNode = rendered.getChildren()
            .get(0);
        assertThat(childNode.isMouseTransparent(), is(false));
        click(childNode);
        assertThat(activated.get(), is(anchor));
    }

    private static SvgRectangle rect(String id) {
        SvgRectangle rect = new SvgRectangle();
        rect.setId(id);
        return rect;
    }

    private static void click(Node node) {
        node.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                                      false, false, false, false, true, false, false, false, false, false, null));
    }

    private static Group render(SvgAnchor anchor, java.util.function.Consumer<SvgAnchor> handler) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(anchor);
        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        if (handler != null) {
            context = context.withAnchorActivationHandler(handler);
        }
        return (Group) svg.createGraphic(context)
            .getChildren()
            .get(0);
    }

}
