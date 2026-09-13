package nz.co.ctg.foxglove.filter;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.ISvgPresentationAttributes;

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
     * The space {@code primitive} operates in: its own {@code color-interpolation-filters}, else the
     * {@code <filter>}'s, else SVG's default of linearRGB.
     * <p>
     * Lives here rather than on either renderer because <b>both</b> paths have to agree about it - the raster
     * pipeline to know what to convert, and {@link SvgFilterRenderer} to know whether it may use an effect chain at
     * all (#126). Two copies of this rule drifting apart is exactly the failure #107 was.
     * <p>
     * {@link ISvgFilterPrimitive} carries only the {@code in}/{@code result}/subregion attributes common to every
     * primitive, not the presentation properties - but every concrete {@code fe*} class extends
     * {@code AbstractSvgStylable} and so does have them, and the binding files declare the attribute on all of them.
     */
    static FilterColorSpace of(ISvgFilterPrimitive primitive, SvgFilter filter) {
        String declared = primitive instanceof ISvgPresentationAttributes attrs ? attrs.getColorInterpolationFilters() : null;
        return parse(declared, of(filter));
    }

    /**
     * The space a {@code <filter>} establishes for primitives that declare none of their own.
     * <p>
     * {@code color-interpolation-filters} is a properly inherited property, so strictly this should also consult the
     * {@code <filter>} element's ancestors. Resolving primitive → filter → default covers how it is actually written
     * in practice and is a documented simplification, not an oversight - a {@code <filter>} normally sits in
     * {@code <defs>}, whose ancestors are not the referencing element's and carry nothing meaningful.
     */
    static FilterColorSpace of(SvgFilter filter) {
        return parse(filter.getColorInterpolationFilters(), LINEAR_RGB);
    }

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
