package nz.co.ctg.foxglove.element;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import nz.co.ctg.foxglove.ForeignObjectHandler;
import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * Exercises #23's {@code <foreignObject>} acceptance criterion: either delegates to a supplied handler or degrades
 * cleanly, and does not silently swallow content without a way to know.
 */
public class SvgForeignObjectRenderingTest {

    private static Size px(double value) {
        return new Size(value, SizeUnits.PX);
    }

    @Test
    public void testHandlerReturnedNodeIsEmbeddedAndPositioned() throws Exception {
        SvgForeignObject foreignObject = new SvgForeignObject();
        foreignObject.setX(px(10));
        foreignObject.setY(px(20));
        foreignObject.setWidth(px(100));
        foreignObject.setHeight(px(50));

        Group rendered = (Group) render(foreignObject, (fo, w, h) -> {
            Rectangle placeholder = new Rectangle(w, h);
            placeholder.setId("placeholder");
            return placeholder;
        });

        assertThat(rendered.getChildren(), notNullValue());
        Rectangle placeholder = (Rectangle) rendered.getChildren().get(0);
        assertThat(placeholder.getWidth(), closeTo(100, 1e-9));
        assertThat(placeholder.getHeight(), closeTo(50, 1e-9));
        Point2D origin = rendered.localToParent(0, 0);
        assertThat(origin.getX(), closeTo(10, 1e-9));
        assertThat(origin.getY(), closeTo(20, 1e-9));
    }

    @Test
    public void testNoHandlerRendersAnEmptyGroupWithoutThrowing() throws Exception {
        SvgForeignObject foreignObject = new SvgForeignObject();
        foreignObject.setWidth(px(10));
        foreignObject.setHeight(px(10));

        Group rendered = (Group) render(foreignObject, null);
        assertThat(rendered.getChildren(), is(empty()));
    }

    @Test
    public void testHandlerReturningNullDegradesTheSameWay() throws Exception {
        SvgForeignObject foreignObject = new SvgForeignObject();
        foreignObject.setWidth(px(10));
        foreignObject.setHeight(px(10));

        Group rendered = (Group) render(foreignObject, (fo, w, h) -> null);
        assertThat(rendered.getChildren(), is(empty()));
    }

    @Test
    public void testRawContentRemainsInspectableRegardlessOfHandler() throws Exception {
        // built in memory here (no real XML content), but proves getRawContent() is a real accessor a caller can
        // always consult - see testRawContentCapturesRealNestedXhtml for what it actually captures from real XML
        SvgForeignObject foreignObject = new SvgForeignObject();
        assertThat(foreignObject.getRawContent(), nullValue());
    }

    @Test
    public void testRawContentCapturesRealNestedXhtml() throws Exception {
        String xml = "<svg xmlns=\"http://www.w3.org/2000/svg\">"
            + "<foreignObject x=\"5\" y=\"6\" width=\"100\" height=\"50\">"
            + "<div xmlns=\"http://www.w3.org/1999/xhtml\">Hello <b>world</b></div>"
            + "</foreignObject>"
            + "</svg>";
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        SvgForeignObject foreignObject = (SvgForeignObject) svg.getContent().get(0);
        assertThat(foreignObject.getPixelsX(), closeTo(5, 1e-9));
        assertThat(foreignObject.getPixelsY(), closeTo(6, 1e-9));
        assertThat(foreignObject.getPixelsWidth(), closeTo(100, 1e-9));
        assertThat(foreignObject.getPixelsHeight(), closeTo(50, 1e-9));
        assertThat(foreignObject.getRawContent(), notNullValue());
        assertThat(foreignObject.getRawContent().getTagName(), is("div"));
        assertThat(foreignObject.getRawContent().getTextContent(), is("Hello world"));
    }

    private static Node render(SvgForeignObject foreignObject, ForeignObjectHandler handler) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(foreignObject);
        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        if (handler != null) {
            context = context.withForeignObjectHandler(handler);
        }
        return svg.createGraphic(context).getChildren().get(0);
    }

}
