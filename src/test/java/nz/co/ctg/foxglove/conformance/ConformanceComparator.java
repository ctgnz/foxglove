package nz.co.ctg.foxglove.conformance;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * A fuzzy, tolerant image comparison for #44 - an exact pixel match against a reference produced by a different
 * renderer is not achievable (antialiasing, font rasterisation and gamma all differ), so this counts how many
 * pixels differ by more than a per-channel threshold and passes if that stays under a documented ratio, rather than
 * demanding pixel-perfect equality.
 */
public final class ConformanceComparator {

    /** Per-channel (0-255) absolute difference beyond which a pixel counts as "different". */
    private static final int PIXEL_CHANNEL_TOLERANCE = 32;

    /** {@link #diff}'s palette: what differs, what matches and carries ink, what matches and is empty, what was excluded. */
    private static final int DIFFERING = 0xFFD32F2F;
    private static final int MATCHING_CONTENT = 0xFFD8D8D8;
    private static final int MATCHING_EMPTY = 0xFFFFFFFF;
    private static final int EXCLUDED = 0xFFEFF3FA;

    /**
     * @param differingRatio     differing pixels over <i>all</i> compared pixels - what {@link #PIXEL_CHANNEL_TOLERANCE}
     *                           and the pass threshold are defined against, and deliberately unchanged since #44
     * @param contentSimilarity  matching pixels over those carrying ink in <i>either</i> image, {@code 0..1} - what
     *                           #110's report shows; see {@link #compare} for why the two are not interchangeable
     * @param contentPixels      how many pixels carried ink in either image, i.e. how much {@code contentSimilarity}
     *                           is actually measuring
     */
    public record Result(boolean passed, double differingRatio, int comparedPixels, double contentSimilarity,
        int contentPixels) {
    }

    /**
     * Compares {@code actual} against {@code reference}, both {@code width}x{@code height}, only over rows
     * {@code [0, cropFromY)} - rows at or beyond {@code cropFromY} are excluded entirely (used to skip the
     * font-dependent "revision" legend furniture every W3C test carries, which this renderer can't reproduce
     * faithfully regardless of what is actually under test - see {@link W3cSvgConformanceCheck}). Pass
     * {@code cropFromY == height} to compare the whole image.
     * <p>
     * Two ratios come back, and conflating them would be a mistake worth spelling out. {@code differingRatio} is
     * over every compared pixel; it is the pass/fail contract and must not change. But most of a W3C test canvas is
     * empty, so that figure flatters heavily - a document this renderer draws <b>nothing at all</b> for still scores
     * around 93% "similar" purely because both images agree about the blank background. Reporting that as progress
     * would be close to dishonest.
     * <p>
     * {@code contentSimilarity} therefore measures only the pixels that carry ink in one image or the other: draw
     * nothing and it is 0, draw the right thing and it approaches 1. It is the figure #110's report shows. A useful
     * side effect is that a test passing only because its ink is a tiny fraction of the canvas shows up as a high
     * pass with a low similarity, rather than hiding.
     */
    public static Result compare(WritableImage actual, WritableImage reference, int width, int height, int cropFromY,
        double maxDifferingRatio) {
        PixelReader actualReader = actual.getPixelReader();
        PixelReader referenceReader = reference.getPixelReader();
        int limitY = Math.max(0, Math.min(cropFromY, height));

        int compared = 0;
        int differing = 0;
        int content = 0;
        int differingContent = 0;
        for (int y = 0; y < limitY; y++) {
            for (int x = 0; x < width; x++) {
                int actualArgb = actualReader.getArgb(x, y);
                int referenceArgb = referenceReader.getArgb(x, y);
                boolean pixelDiffers = differs(actualArgb, referenceArgb);
                compared++;
                if (pixelDiffers) {
                    differing++;
                }
                if (hasContent(actualArgb) || hasContent(referenceArgb)) {
                    content++;
                    if (pixelDiffers) {
                        differingContent++;
                    }
                }
            }
        }
        double ratio = compared == 0 ? 0.0 : (double) differing / compared;
        // two blank images agree perfectly; saying "0% similar" of nothing-versus-nothing would be nonsense
        double similarity = content == 0 ? 1.0 : 1.0 - (double) differingContent / content;
        return new Result(ratio <= maxDifferingRatio, ratio, compared, similarity, content);
    }

    /**
     * Renders where {@code actual} and {@code reference} disagree, for #110's per-test pages - deliberately here,
     * beside {@link #compare}, so both share the single definition of {@link #differs} below. Two copies of that
     * rule drifting apart would make the report quietly disagree with the pass/fail it is illustrating.
     * <p>
     * Differing pixels are opaque red; matching pixels that carry content in either image are faint grey, so the
     * shape being compared stays readable rather than the diff being red marks floating in a void; matching
     * transparent pixels are white. Rows at or beyond {@code cropFromY} are tinted, which makes the "revision"
     * legend cropping visible - otherwise it is invisible methodology that silently changes what is being measured.
     */
    public static WritableImage diff(WritableImage actual, WritableImage reference, int width, int height, int cropFromY) {
        PixelReader actualReader = actual.getPixelReader();
        PixelReader referenceReader = reference.getPixelReader();
        WritableImage result = new WritableImage(width, height);
        PixelWriter writer = result.getPixelWriter();
        int limitY = Math.max(0, Math.min(cropFromY, height));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y >= limitY) {
                    writer.setArgb(x, y, EXCLUDED);
                    continue;
                }
                int actualArgb = actualReader.getArgb(x, y);
                int referenceArgb = referenceReader.getArgb(x, y);
                if (differs(actualArgb, referenceArgb)) {
                    writer.setArgb(x, y, DIFFERING);
                } else if (hasContent(actualArgb) || hasContent(referenceArgb)) {
                    writer.setArgb(x, y, MATCHING_CONTENT);
                } else {
                    writer.setArgb(x, y, MATCHING_EMPTY);
                }
            }
        }
        return result;
    }

    /** Opaque enough to be worth drawing - anything nearly transparent reads as empty canvas. */
    private static boolean hasContent(int argb) {
        return ((argb >>> 24) & 0xFF) > 16;
    }

    private static boolean differs(int argb1, int argb2) {
        return channelDiffers(argb1, argb2, 24) || channelDiffers(argb1, argb2, 16)
            || channelDiffers(argb1, argb2, 8) || channelDiffers(argb1, argb2, 0);
    }

    private static boolean channelDiffers(int argb1, int argb2, int shift) {
        int c1 = (argb1 >>> shift) & 0xFF;
        int c2 = (argb2 >>> shift) & 0xFF;
        return Math.abs(c1 - c2) > PIXEL_CHANNEL_TOLERANCE;
    }

    private ConformanceComparator() {
    }

}
