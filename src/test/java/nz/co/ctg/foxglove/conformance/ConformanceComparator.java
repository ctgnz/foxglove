package nz.co.ctg.foxglove.conformance;

import javafx.scene.image.PixelReader;
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

    public record Result(boolean passed, double differingRatio, int comparedPixels) {
    }

    /**
     * Compares {@code actual} against {@code reference}, both {@code width}x{@code height}, only over rows
     * {@code [0, cropFromY)} - rows at or beyond {@code cropFromY} are excluded entirely (used to skip the
     * font-dependent "revision" legend furniture every W3C test carries, which this renderer can't reproduce
     * faithfully regardless of what is actually under test - see {@link W3cSvgConformanceCheck}). Pass
     * {@code cropFromY == height} to compare the whole image.
     */
    public static Result compare(WritableImage actual, WritableImage reference, int width, int height, int cropFromY,
        double maxDifferingRatio) {
        PixelReader actualReader = actual.getPixelReader();
        PixelReader referenceReader = reference.getPixelReader();
        int limitY = Math.max(0, Math.min(cropFromY, height));

        int compared = 0;
        int differing = 0;
        for (int y = 0; y < limitY; y++) {
            for (int x = 0; x < width; x++) {
                compared++;
                if (differs(actualReader.getArgb(x, y), referenceReader.getArgb(x, y))) {
                    differing++;
                }
            }
        }
        double ratio = compared == 0 ? 0.0 : (double) differing / compared;
        return new Result(ratio <= maxDifferingRatio, ratio, compared);
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
