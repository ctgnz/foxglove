package nz.co.ctg.foxglove;

import java.util.Set;

/**
 * The SVG 1.1 feature strings this renderer claims to support, for {@code requiredFeatures} evaluation ({@link ISvgConditionalFeatures}). Declared in one place so it stays honest:
 * only features actually rendered somewhere in this codebase belong here, and it should shrink or grow as other issues land.
 * <p>
 * Deliberately excludes {@code Clip}/{@code BasicClip}/{@code Mask}/{@code Filter}/{@code BasicFilter} ({@code SvgClipPath}/{@code SvgMask}/{@code SvgFilter} don't implement
 * {@link FxGraphic} - not rendered), {@code Hyperlinking} ({@code SvgAnchor} doesn't either - tracked in #23), and {@code Cursor}/{@code View}/
 * {@code Animation}/{@code Font}/{@code BasicFont}/{@code Script}/{@code Extensibility} (none implemented).
 */
public final class SvgFeatures {

    private static final String PREFIX = "http://www.w3.org/TR/SVG11/feature#";

    public static final Set<String> SUPPORTED = Set.of(
        PREFIX + "BasicStructure",
        PREFIX + "ConditionalProcessing",
        PREFIX + "Image",
        PREFIX + "Style",
        PREFIX + "ViewportAttribute",
        PREFIX + "Shape",
        PREFIX + "Text",
        PREFIX + "BasicText",
        PREFIX + "PaintAttribute",
        PREFIX + "BasicPaintAttribute",
        PREFIX + "OpacityAttribute",
        PREFIX + "GraphicsAttribute",
        PREFIX + "BasicGraphicsAttribute",
        PREFIX + "Marker",
        PREFIX + "Gradient",
        PREFIX + "Pattern");

    private SvgFeatures() {
    }

}
