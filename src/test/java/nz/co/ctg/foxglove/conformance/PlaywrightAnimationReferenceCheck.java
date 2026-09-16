package nz.co.ctg.foxglove.conformance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.image.WritableImage;
import javafx.util.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nz.co.ctg.foxglove.JavaFxTestSupport;

/**
 * The single assertion #112's whole design rests on: that a browser engine's SMIL clock can be seeked, and that what it then renders reflects the moment asked for. Everything else
 * in the animation check is comparison plumbing around this.
 * <p>
 * The document animates a square's {@code x} from 0 to 80 over ten seconds, so its leading edge is at exactly {@code 8t} - a value chosen so that a seek being ignored, rounded, or
 * applied at the wrong scale all give visibly different answers rather than a near miss.
 * <p>
 * Named to avoid Surefire's default {@code **&#47;*Test.java}-style discovery patterns, like every other conformance-profile-only class (see {@link W3cSvgReferenceGenerator}'s own
 * javadoc) - {@link PlaywrightAnimationReference#open} launches a real headless Chromium process, which needs the browser binary the {@code conformance} profile installs, not
 * something the default {@code mvn test}/{@code mvn verify} should depend on. Run explicitly with {@code mvn -Pconformance test -Dtest=PlaywrightAnimationReferenceCheck}.
 */
public class PlaywrightAnimationReferenceCheck {

    private static final String DOC = """
                    <svg xmlns="http://www.w3.org/2000/svg" width="100" height="20" viewBox="0 0 100 20">
                      <rect y="0" width="20" height="20" fill="black">
                        <animate attributeName="x" from="0" to="80" dur="10s" fill="freeze"/>
                      </rect>
                    </svg>
                    """;

    @TempDir
    Path documents;

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @Test
    public void testSeekingMovesWhatTheEngineRenders() throws Exception {
        Path document = documents.resolve("animated.svg");
        Files.writeString(document, DOC, StandardCharsets.UTF_8);

        try (PlaywrightAnimationReference reference = PlaywrightAnimationReference.open(100, 20)) {
            assertThat(reference.load(document), is(true));

            for (double seconds : new double[] {
                0, 2.5, 5, 10
            }) {
                reference.seek(Duration.seconds(seconds));
                assertThat("at t=" + seconds + "s the square's leading edge should be at x=" + (8 * seconds),
                    leadingEdgeOf(reference.snapshot()), is((int) Math.round(8 * seconds)));
            }
        }
    }

    /** Seeking backwards has to work too, since the check samples in order but documents are reused. */
    @Test
    public void testSeekingBackwardsAlsoWorks() throws Exception {
        Path document = documents.resolve("animated.svg");
        Files.writeString(document, DOC, StandardCharsets.UTF_8);

        try (PlaywrightAnimationReference reference = PlaywrightAnimationReference.open(100, 20)) {
            reference.load(document);
            reference.seek(Duration.seconds(10));
            reference.seek(Duration.seconds(2.5));

            assertThat(leadingEdgeOf(reference.snapshot()), is(20));
        }
    }

    /**
     * #208: confirmed directly, not assumed carried over from #153's original WebView finding - an isolated probe first tried without any rewrite stayed at its base colour
     * forever, at every seeked moment including well past {@code fill="freeze"}'s own endpoint, exactly like #153 originally reported for WebView. Chromium's reason is different
     * (SVG2 deprecated {@code <animateColor>}, folding it into plain {@code <animate>}, and Chromium dropped native support for the standalone element some years back) but the
     * practical effect on this reference is identical, so {@link PlaywrightAnimationReference#load} carries the same rewrite-to-{@code <animate>} workaround forward rather than
     * dropping it.
     */
    @Test
    public void testAnimateColorIsRewrittenSoTheEngineActuallyAnimatesIt() throws Exception {
        String doc = """
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20">
                          <rect width="20" height="20" fill="#000000">
                            <animateColor attributeName="fill" from="#000000" to="#00ff00" dur="4s" fill="freeze"/>
                          </rect>
                        </svg>
                        """;
        Path document = documents.resolve("animate-color.svg");
        Files.writeString(document, doc, StandardCharsets.UTF_8);

        try (PlaywrightAnimationReference reference = PlaywrightAnimationReference.open(20, 20)) {
            assertThat(reference.load(document), is(true));

            reference.seek(Duration.seconds(4));
            assertThat("seeked to fill=\"freeze\"'s own endpoint, the rect should have reached full green",
                reference.snapshot()
                    .getPixelReader()
                    .getColor(10, 10)
                    .getGreen(),
                is(1.0));
        }
    }

    /** A document the engine cannot make sense of is a recordable result, not an exception. */
    @Test
    public void testAnUnloadableDocumentReportsFailureRatherThanThrowing() throws Exception {
        Path document = documents.resolve("not-really.svg");
        Files.writeString(document, "this is not markup at all", StandardCharsets.UTF_8);

        try (PlaywrightAnimationReference reference = PlaywrightAnimationReference.open(100, 20)) {
            // it may or may not "load" - what matters is that asking does not blow up
            reference.load(document);
        }
    }

    private static int leadingEdgeOf(WritableImage image) {
        for (int x = 0; x < (int) image.getWidth(); x++) {
            if (image.getPixelReader()
                .getColor(x, 10)
                .getOpacity() > 0.5
                && image.getPixelReader()
                    .getColor(x, 10)
                    .getRed() < 0.5) {
                return x;
            }
        }
        return -1;
    }

}
