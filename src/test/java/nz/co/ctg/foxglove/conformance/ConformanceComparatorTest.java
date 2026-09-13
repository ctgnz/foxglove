package nz.co.ctg.foxglove.conformance;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.JavaFxTestSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Covers the diff image #110's per-test pages show. The important property is not any particular colour but that
 * the diff agrees with {@link ConformanceComparator#compare} about what "differs" means - a diff that disagreed
 * with the verdict it illustrates would be worse than none at all.
 */
public class ConformanceComparatorTest {

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    private static final Color DIFFERING = Color.web("#D32F2F");
    private static final Color MATCHING_CONTENT = Color.web("#D8D8D8");
    private static final Color MATCHING_EMPTY = Color.WHITE;
    private static final Color EXCLUDED = Color.web("#EFF3FA");

    /**
     * The reason the report measures ink rather than the whole canvas. Most of a W3C test image is empty, so a
     * document this renderer draws nothing at all for scores ~93% on the raw differing-pixel ratio purely by
     * agreeing about the background - `struct-frag-04-t` really does. Publishing that as a progress figure would
     * read as far better news than it is.
     */
    @Test
    public void testRenderingNothingScoresZeroInkMatchedEvenThoughMostPixelsAgree() throws Exception {
        // a 10x1 strip: one pixel of ink in the reference, nine of shared blank background
        WritableImage blank = image(10, 1, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT,
            Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
        WritableImage reference = image(10, 1, Color.BLACK, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT,
            Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

        ConformanceComparator.Result result = ConformanceComparator.compare(blank, reference, 10, 1, 1, 1.0);

        assertThat("nine of ten pixels agree, which is exactly the flattery being avoided",
            result.differingRatio(), closeTo(0.1, 1e-9));
        assertThat("but none of the ink was reproduced", result.contentSimilarity(), closeTo(0.0, 1e-9));
        assertThat(result.contentPixels(), is(1));
    }

    @Test
    public void testTwoBlankImagesAgreePerfectlyRatherThanScoringZero() throws Exception {
        WritableImage blank = image(2, 1, Color.TRANSPARENT, Color.TRANSPARENT);

        // nothing-versus-nothing has no ink to disagree about; calling that 0% similar would be nonsense
        assertThat(ConformanceComparator.compare(blank, blank, 2, 1, 1, 1.0).contentSimilarity(), closeTo(1.0, 1e-9));
    }

    @Test
    public void testDiffMarksDifferingPixelsAndKeepsMatchingContentVisible() throws Exception {
        WritableImage actual = image(3, 1, Color.BLACK, Color.BLACK, Color.TRANSPARENT);
        WritableImage reference = image(3, 1, Color.BLACK, Color.WHITE, Color.TRANSPARENT);

        WritableImage diff = ConformanceComparator.diff(actual, reference, 3, 1, 1);

        assertThat("identical ink stays readable as grey", colorAt(diff, 0, 0), is(MATCHING_CONTENT));
        assertThat("black against white differs", colorAt(diff, 1, 0), is(DIFFERING));
        assertThat("matching empty canvas is blank", colorAt(diff, 2, 0), is(MATCHING_EMPTY));
    }

    @Test
    public void testDiffTintsTheRowsExcludedFromComparison() throws Exception {
        // rows at or beyond cropFromY are not compared at all - the "revision" legend every W3C test carries
        WritableImage actual = image(1, 2, Color.BLACK, Color.BLACK);
        WritableImage reference = image(1, 2, Color.BLACK, Color.WHITE);

        WritableImage diff = ConformanceComparator.diff(actual, reference, 1, 2, 1);

        assertThat(colorAt(diff, 0, 0), is(MATCHING_CONTENT));
        assertThat("the excluded row is tinted, not marked as differing", colorAt(diff, 0, 1), is(EXCLUDED));
    }

    @Test
    public void testDiffCountsTheSameDifferencesAsCompareDoes() throws Exception {
        WritableImage actual = image(4, 1, Color.BLACK, Color.BLACK, Color.WHITE, Color.TRANSPARENT);
        WritableImage reference = image(4, 1, Color.BLACK, Color.WHITE, Color.WHITE, Color.TRANSPARENT);

        ConformanceComparator.Result result = ConformanceComparator.compare(actual, reference, 4, 1, 1, 1.0);
        WritableImage diff = ConformanceComparator.diff(actual, reference, 4, 1, 1);

        int marked = 0;
        for (int x = 0; x < 4; x++) {
            if (colorAt(diff, x, 0).equals(DIFFERING)) {
                marked++;
            }
        }
        assertThat((double) marked / result.comparedPixels(), closeTo(result.differingRatio(), 1e-9));
    }

    @Test
    public void testAChannelDifferenceWithinToleranceIsNotMarked() throws Exception {
        // the comparison is deliberately fuzzy: antialiasing and gamma differ between renderers, so a small
        // per-channel difference must not read as a defect in either the verdict or the picture of it
        WritableImage actual = image(1, 1, Color.rgb(100, 100, 100));
        WritableImage reference = image(1, 1, Color.rgb(120, 120, 120));

        assertThat(colorAt(ConformanceComparator.diff(actual, reference, 1, 1, 1), 0, 0), is(MATCHING_CONTENT));
    }

    private static WritableImage image(int width, int height, Color... pixels) {
        WritableImage image = new WritableImage(width, height);
        for (int i = 0; i < pixels.length; i++) {
            image.getPixelWriter().setColor(i % width, i / width, pixels[i]);
        }
        return image;
    }

    private static Color colorAt(WritableImage image, int x, int y) {
        return image.getPixelReader().getColor(x, y);
    }

}
