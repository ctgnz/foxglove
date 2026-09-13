package nz.co.ctg.foxglove.filter;

import org.apache.commons.lang3.StringUtils;

/**
 * The colour space a filter primitive operates in - what {@code color-interpolation-filters} selects (#108).
 * <p>
 * This matters more than it might sound. SVG's default is <b>linearRGB</b>, not sRGB, and every primitive that
 * interpolates or combines values - {@code feGaussianBlur}, {@code feComposite}, {@code feBlend}, {@code feMerge},
 * {@code feColorMatrix}, {@code feComponentTransfer} - gives visibly different answers in the two. Blending in sRGB
 * is blending gamma-encoded numbers as though they were light, which comes out systematically too dark in the
 * midtones. Working in sRGB was #77's largest documented inaccuracy.
 * <p>
 * Note that the {@code sRGB} keyword names the space colours arrive in, so its conversion into itself is the
 * identity; the interesting direction is {@link #LINEAR_RGB}'s.
 */
enum FilterColorSpace {

    /**
     * Colour channels as stored and displayed - gamma-encoded. Converting <i>into</i> sRGB applies the encoding
     * curve to a linear value.
     */
    SRGB {
        @Override
        float convert(float linearValue) {
            if (linearValue <= 0.0031308f) {
                return linearValue * 12.92f;
            }
            return (float) (1.055 * Math.pow(linearValue, 1 / 2.4) - 0.055);
        }
    },

    /**
     * Light-linear colour channels, SVG's default working space for filters. Converting <i>into</i> linearRGB
     * removes the sRGB encoding curve.
     */
    LINEAR_RGB {
        @Override
        float convert(float srgbValue) {
            if (srgbValue <= 0.04045f) {
                return srgbValue / 12.92f;
            }
            return (float) Math.pow((srgbValue + 0.055) / 1.055, 2.4);
        }
    };

    /**
     * One colour channel converted <i>into</i> this space from the other one. The specification's own piecewise
     * transfer function, deliberately not the 2.2-power approximation it is often confused with - the linear segment
     * near black is what keeps dark values from collapsing.
     */
    abstract float convert(float value);

    /**
     * The space named by a {@code color-interpolation-filters} value, or {@code fallback} when it is absent, blank,
     * {@code auto} (which the specification leaves to the implementation) or unrecognised.
     */
    static FilterColorSpace parse(String value, FilterColorSpace fallback) {
        String text = StringUtils.trimToEmpty(value);
        if ("sRGB".equalsIgnoreCase(text)) {
            return SRGB;
        }
        if ("linearRGB".equalsIgnoreCase(text)) {
            return LINEAR_RGB;
        }
        return fallback;
    }

}
