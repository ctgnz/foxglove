package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.element.SvgCursor;
import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * {@code javafx.scene.image.Image} construction requires the JavaFX Application Thread - see {@link JavaFxTestSupport} and {@code SvgImageRenderingTest}, which has the same
 * requirement for the same reason.
 */
public class SvgCursorResolverTest {

    @BeforeAll
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @Test
    public void testBlankOrAbsentValueResolvesToNull() throws Exception {
        assertThat(SvgCursorResolver.resolve(null, null), nullValue());
        assertThat(SvgCursorResolver.resolve("", null), nullValue());
        assertThat(SvgCursorResolver.resolve("   ", null), nullValue());
    }

    @Test
    public void testStandardKeywordsMapToTheirCursorConstants() throws Exception {
        assertThat(SvgCursorResolver.resolve("pointer", null), is(Cursor.HAND));
        assertThat(SvgCursorResolver.resolve("crosshair", null), is(Cursor.CROSSHAIR));
        assertThat(SvgCursorResolver.resolve("move", null), is(Cursor.MOVE));
        assertThat(SvgCursorResolver.resolve("text", null), is(Cursor.TEXT));
        assertThat(SvgCursorResolver.resolve("wait", null), is(Cursor.WAIT));
        assertThat(SvgCursorResolver.resolve("n-resize", null), is(Cursor.N_RESIZE));
        assertThat(SvgCursorResolver.resolve("sw-resize", null), is(Cursor.SW_RESIZE));
    }

    @Test
    public void testUnrecognizedKeywordFallsBackToDefault() throws Exception {
        assertThat(SvgCursorResolver.resolve("auto", null), is(Cursor.DEFAULT));
        assertThat(SvgCursorResolver.resolve("default", null), is(Cursor.DEFAULT));
        assertThat(SvgCursorResolver.resolve("something-unknown", null), is(Cursor.DEFAULT));
    }

    @Test
    public void testUrlResolvingToADataUriCursorBuildsAnImageCursor() throws Exception {
        SvgCursor cursor = new SvgCursor();
        cursor.setId("custom");
        cursor.setX("2");
        cursor.setY("1");
        // a real 4x2 PNG - wide enough that a (2,1) hotspot is within bounds, unlike a 1x1 image (ImageCursor
        // silently clamps an out-of-bounds hotspot to (0,0), which a 1x1 image can never actually accommodate)
        cursor.setXlinkHref(
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAACCAIAAADwyuo0AAAAEElEQVR42mP4z8AARwzIHABvqgf5aN2vpwAAAABJRU5ErkJggg==");
        SvgRectangle placeholder = new SvgRectangle();
        placeholder.setId("holder");
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(cursor);
        svg.getContent()
            .add(placeholder);

        Cursor resolved = SvgCursorResolver.resolve("url(#custom), pointer", svg.getElementIndex());
        assertThat(resolved, is(instanceOf(ImageCursor.class)));
        ImageCursor imageCursor = (ImageCursor) resolved;
        assertThat(imageCursor.getHotspotX(), is(2.0));
        assertThat(imageCursor.getHotspotY(), is(1.0));
    }

    @Test
    public void testUnresolvableUrlFallsThroughToTheNextToken() throws Exception {
        SvgRectangle placeholder = new SvgRectangle();
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(placeholder);

        Cursor resolved = SvgCursorResolver.resolve("url(#missing), pointer", svg.getElementIndex());
        assertThat(resolved, is(Cursor.HAND));
    }

}
