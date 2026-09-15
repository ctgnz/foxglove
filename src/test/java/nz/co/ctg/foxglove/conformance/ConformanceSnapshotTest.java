package nz.co.ctg.foxglove.conformance;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;

/**
 * Regression cover for #111: the conformance harness captured the wrong region for any document whose ink extends outside the viewport origin.
 * <p>
 * {@code Node.snapshot} with no explicit viewport renders from the node's own {@code boundsInLocal} origin rather than from {@code (0, 0)}, so the whole rendering was displaced by
 * however far the content's own bounds sat from the origin - and mismatched the reference for a reason with nothing to do with rendering quality.
 * <p>
 * Deliberately self-contained rather than driven off the fetched W3C suite, so it runs in the default build and pins the behaviour directly - the suite-level evidence (three tests
 * moving to PASS in the baseline manifest) demonstrates the effect but would not say <i>why</i>, nor fail usefully if this regressed.
 */
public class ConformanceSnapshotTest {

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    /**
     * The vertical line sits on {@code x=0} with a 6-wide stroke, so its ink reaches {@code x=-3} - exactly the overhang that used to shift everything. The red square is the
     * probe: it is at user {@code (10,10)-(14,14)}, and only lands there in the image if the snapshot is anchored to the viewport rather than to the content.
     */
    @Test
    public void testSnapshotIsAnchoredToTheViewportNotTheContentBounds() throws Exception {
        WritableImage image = render("""
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" width="20" height="20">
                          <line x1="0" y1="0" x2="0" y2="20" stroke="black" stroke-width="6"/>
                          <rect x="10" y="10" width="4" height="4" fill="red"/>
                        </svg>
                        """, 20, 20);

        assertThat("the probe square should be where the document puts it", colorAt(image, 12, 12), is(Color.RED));
        // and the overhang is clipped away rather than shifting the picture: the far side stays empty
        assertThat(colorAt(image, 18, 12).getOpacity(), is(0.0));
    }

    /**
     * Overhang is only the most visible symptom. Content that merely <i>starts</i> somewhere other than the origin was displaced just as badly, because the content bounds are the
     * origin the snapshot used - here the lone square would have been dragged all the way to the top-left corner. Most suite documents escaped the worst of this only by accident:
     * their {@code <rect id="test-frame" x="1" y="1">} stroke puts the content bounds at about {@code (0.5, 0.5)}, so the error stayed sub-pixel for everything except the
     * documents that overhang.
     */
    @Test
    public void testContentIsPlacedWhereTheDocumentPutsItNotAtItsOwnBoundsOrigin() throws Exception {
        WritableImage image = render("""
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" width="20" height="20">
                          <rect x="10" y="10" width="4" height="4" fill="red"/>
                        </svg>
                        """, 20, 20);

        assertThat(colorAt(image, 12, 12), is(Color.RED));
        assertThat(colorAt(image, 2, 2).getOpacity(), is(0.0));
    }

    private static WritableImage render(String document, int width, int height) throws Exception {
        SvgGraphic svg = new FoxgloveParser().parse(new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8)));
        return onFxThread(() -> {
            Node built = svg.createGraphic(RenderContext.root(svg.getElementIndex(), width, height));
            return W3cSvgConformanceCheck.snapshot(built, width, height);
        });
    }

    private static Color colorAt(WritableImage image, int x, int y) {
        return image.getPixelReader()
            .getColor(x, y);
    }

}
