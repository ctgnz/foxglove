package nz.co.ctg.foxglove.filter;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * One intermediate image in a filter's primitive graph (#77) - a fixed-size pixel buffer the pixel-level primitives read and write directly, since they have no
 * {@code javafx.scene.effect} equivalent to delegate to.
 * <p>
 * Channels are stored <b>premultiplied</b>, in {@code R,G,B,A} order, as floats in {@code [0, 1]}. Premultiplied is the form the specification's own Porter-Duff
 * ({@code feComposite}) and {@code feBlend} formulas are written in, so those primitives need no conversion at all; the two that are specified on non-premultiplied values
 * ({@code feColorMatrix}, {@code feComponentTransfer}) unpremultiply, apply, and repremultiply around their own math (see {@link #unpremultiply}/{@link #premultiply}).
 */
final class FilterRaster {

    private final int width;
    private final int height;
    private final float[] data;

    /**
     * Which colour space {@link #data}'s colour channels are currently in - see {@link #toColorSpace} and {@code color-interpolation-filters} (#108). Carried on the buffer rather
     * than tracked alongside it because intermediate results outlive the primitive that produced them: a named {@code result} can be consumed much later by a primitive operating
     * in the other space, and only the buffer itself knows what it holds.
     */
    private FilterColorSpace colorSpace = FilterColorSpace.SRGB;

    FilterRaster(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new float[Math.max(0, width * height * 4)];
    }

    FilterColorSpace getColorSpace() {
        return colorSpace;
    }

    void setColorSpace(FilterColorSpace colorSpace) {
        this.colorSpace = colorSpace;
    }

    /**
     * This buffer converted into {@code target}, or itself when already there.
     * <p>
     * Colour channels only: alpha is a coverage fraction, not a colour, and converting it would be meaningless. The buffer is premultiplied and the transfer function is
     * non-linear, so each pixel has to be unpremultiplied, converted and repremultiplied - applying the curve to a premultiplied value would fold the alpha into the gamma and
     * darken every partially covered pixel.
     */
    FilterRaster toColorSpace(FilterColorSpace target) {
        if (colorSpace == target) {
            return this;
        }
        FilterRaster result = newLike();
        result.colorSpace = target;
        float[] rgba = new float[4];
        for (int i = 0; i < data.length; i += 4) {
            unpremultiply(data, i, rgba);
            for (int c = 0; c < 3; c++) {
                rgba[c] = target.convert(rgba[c]);
            }
            premultiply(result.data, i, rgba);
        }
        return result;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    /** Index of pixel {@code (x, y)}'s red channel; green, blue and alpha follow at {@code +1}, {@code +2}, {@code +3}. */
    int index(int x, int y) {
        return (y * width + x) * 4;
    }

    float[] getData() {
        return data;
    }

    FilterRaster newLike() {
        return new FilterRaster(width, height);
    }

    /**
     * Reads {@code image} into a new buffer, premultiplying as it goes - {@link PixelReader#getArgb} returns non-premultiplied sRGB bytes.
     */
    static FilterRaster fromImage(Image image, int width, int height) {
        FilterRaster raster = new FilterRaster(width, height);
        PixelReader reader = image.getPixelReader();
        if (reader == null) {
            return raster;
        }
        int imageWidth = (int) Math.round(image.getWidth());
        int imageHeight = (int) Math.round(image.getHeight());
        float[] data = raster.data;
        for (int y = 0; y < Math.min(height, imageHeight); y++) {
            for (int x = 0; x < Math.min(width, imageWidth); x++) {
                int argb = reader.getArgb(x, y);
                float alpha = ((argb >>> 24) & 0xFF) / 255f;
                int i = raster.index(x, y);
                data[i] = ((argb >>> 16) & 0xFF) / 255f * alpha;
                data[i + 1] = ((argb >>> 8) & 0xFF) / 255f * alpha;
                data[i + 2] = (argb & 0xFF) / 255f * alpha;
                data[i + 3] = alpha;
            }
        }
        return raster;
    }

    /** Writes this buffer back out, undoing the premultiplication {@link PixelWriter#setArgb} does not expect. */
    WritableImage toImage() {
        WritableImage image = new WritableImage(Math.max(1, width), Math.max(1, height));
        PixelWriter writer = image.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = index(x, y);
                float alpha = clamp(data[i + 3]);
                int a = Math.round(alpha * 255);
                int r = toByte(data[i], alpha);
                int g = toByte(data[i + 1], alpha);
                int b = toByte(data[i + 2], alpha);
                writer.setArgb(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return image;
    }

    private static int toByte(float premultiplied, float alpha) {
        if (alpha <= 0) {
            return 0;
        }
        return Math.round(clamp(premultiplied / alpha) * 255);
    }

    /** The non-premultiplied colour channels of pixel {@code i}, written into {@code out} as {@code R,G,B,A}. */
    static void unpremultiply(float[] data, int i, float[] out) {
        float alpha = data[i + 3];
        if (alpha <= 0) {
            out[0] = 0;
            out[1] = 0;
            out[2] = 0;
            out[3] = 0;
            return;
        }
        out[0] = data[i] / alpha;
        out[1] = data[i + 1] / alpha;
        out[2] = data[i + 2] / alpha;
        out[3] = alpha;
    }

    /** The inverse of {@link #unpremultiply}: writes non-premultiplied {@code rgba} back into {@code data} at {@code i}. */
    static void premultiply(float[] data, int i, float[] rgba) {
        float alpha = clamp(rgba[3]);
        data[i] = clamp(rgba[0]) * alpha;
        data[i + 1] = clamp(rgba[1]) * alpha;
        data[i + 2] = clamp(rgba[2]) * alpha;
        data[i + 3] = alpha;
    }

    static float clamp(float value) {
        if (value < 0) {
            return 0;
        }
        return value > 1 ? 1 : value;
    }

}
