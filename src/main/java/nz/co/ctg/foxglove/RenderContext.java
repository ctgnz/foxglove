package nz.co.ctg.foxglove;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.element.SvgAnchor;

import static com.google.common.base.MoreObjects.toStringHelper;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Bounds;

/**
 * The state threaded through rendering that a bare parent element cannot carry on its own: the style resolved down
 * the ancestor chain (see {@link SvgInheritedStyle}), the document's {@link SvgElementIndex}, the nearest enclosing
 * viewport, and - where established - the object bounding box that {@code objectBoundingBox}-mode coordinates
 * resolve against.
 * <p>
 * Implements {@link ISvgStylable} by delegating to the wrapped {@link SvgInheritedStyle}, so it can stand in
 * anywhere a resolved parent style was previously expected.
 */
public final class RenderContext implements ISvgStylable {

    public enum Axis {
        HORIZONTAL,
        VERTICAL,
        /** Per SVG 1.1 7.10: {@code sqrt((width^2 + height^2) / 2)}, used by properties with no single axis. */
        DIAGONAL
    }

    public enum UnitsMode {
        USER_SPACE_ON_USE,
        OBJECT_BOUNDING_BOX
    }

    private static final String USER_SPACE_ON_USE = "userSpaceOnUse";
    private static final String OBJECT_BOUNDING_BOX = "objectBoundingBox";

    /**
     * Parses one of the {@code *Units} attributes ({@code gradientUnits}, {@code patternUnits},
     * {@code patternContentUnits}, {@code clipPathUnits}, {@code maskUnits}, {@code maskContentUnits},
     * {@code filterUnits}, {@code primitiveUnits}), falling back to {@code defaultMode} when {@code raw} is blank or
     * unrecognised. {@code markerUnits} is not one of these - its values are {@code strokeWidth}/
     * {@code userSpaceOnUse} rather than a {@code userSpaceOnUse}/{@code objectBoundingBox} choice.
     */
    public static UnitsMode parseUnits(String raw, UnitsMode defaultMode) {
        if (USER_SPACE_ON_USE.equals(raw)) {
            return UnitsMode.USER_SPACE_ON_USE;
        }
        if (OBJECT_BOUNDING_BOX.equals(raw)) {
            return UnitsMode.OBJECT_BOUNDING_BOX;
        }
        return defaultMode;
    }

    /**
     * The context at the top of the document: no inherited style, the document's element index, and the given
     * initial viewport.
     */
    public static RenderContext root(SvgElementIndex elementIndex, double viewportWidth, double viewportHeight) {
        return new RenderContext(SvgInheritedStyle.root(), elementIndex, viewportWidth, viewportHeight, null, null, Locale.getDefault(), null,
            null);
    }

    private final SvgInheritedStyle style;
    private final SvgElementIndex elementIndex;
    private final double viewportWidth;
    private final double viewportHeight;
    private final Bounds objectBoundingBox;
    private final URI baseUri;
    private final Locale locale;
    private final Consumer<SvgAnchor> anchorActivationHandler;
    private final ForeignObjectHandler foreignObjectHandler;

    private RenderContext(SvgInheritedStyle style, SvgElementIndex elementIndex, double viewportWidth, double viewportHeight,
        Bounds objectBoundingBox, URI baseUri, Locale locale, Consumer<SvgAnchor> anchorActivationHandler,
        ForeignObjectHandler foreignObjectHandler) {
        this.style = style;
        this.elementIndex = elementIndex;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.objectBoundingBox = objectBoundingBox;
        this.baseUri = baseUri;
        this.locale = locale;
        this.anchorActivationHandler = anchorActivationHandler;
        this.foreignObjectHandler = foreignObjectHandler;
    }

    /**
     * The context a container hands to one of its children: the container's own style resolved one level further,
     * same viewport, index, object bounding box, base URI, locale and handlers.
     */
    public RenderContext resolveChild(ISvgAttributes element) {
        return new RenderContext(SvgInheritedStyle.resolve(style, element), elementIndex, viewportWidth, viewportHeight, objectBoundingBox,
            baseUri, locale, anchorActivationHandler, foreignObjectHandler);
    }

    /**
     * The context inside a newly established viewport - a nested {@code <svg>} - with the same style, index, base
     * URI, locale and handlers, the new viewport size, and no object bounding box (a new viewport is not itself
     * bound to a shape).
     */
    public RenderContext withViewport(double width, double height) {
        return new RenderContext(style, elementIndex, width, height, null, baseUri, locale, anchorActivationHandler, foreignObjectHandler);
    }

    /**
     * The context while resolving {@code objectBoundingBox}-mode coordinates against {@code bbox}. Not yet
     * populated by anything in this codebase - carried ahead of the paint server and clip/mask/marker work that
     * will call it.
     */
    public RenderContext withObjectBoundingBox(Bounds bbox) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, bbox, baseUri, locale, anchorActivationHandler,
            foreignObjectHandler);
    }

    /**
     * The context with the document's base URI established - what a relative {@code xlink:href}, such as on
     * {@code <image>}, resolves against. Set once at the root from wherever the document was parsed from (see
     * {@link FoxgloveParser#parseFile}); absent when parsed from a bare stream with no known source.
     */
    public RenderContext withBaseUri(URI baseUri) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
            foreignObjectHandler);
    }

    /**
     * The context with the locale {@code systemLanguage} (see {@link ISvgConditionalFeatures}) evaluates against
     * established - defaults to {@link Locale#getDefault()} at {@link #root}, overridable by a caller that wants
     * to render the same document for a specific language.
     */
    public RenderContext withLocale(Locale locale) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
            foreignObjectHandler);
    }

    /**
     * The context with a callback established for {@code <a>} activation - invoked with the {@link SvgAnchor} when
     * its rendered content is clicked. Absent by default: this library does not own a browser, so "following a
     * link" is entirely up to the embedding application. A caller wanting this (or {@link #withForeignObjectHandler})
     * builds a context directly via {@link #root} rather than through {@code SvgGraphic.createGroup()} - there is no
     * dedicated overload per optional capability, since that stops scaling once there is more than one.
     */
    public RenderContext withAnchorActivationHandler(Consumer<SvgAnchor> anchorActivationHandler) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
            foreignObjectHandler);
    }

    /**
     * The context with a handler established for {@code <foreignObject>} content (see {@link ForeignObjectHandler}).
     * Absent by default - a {@code <foreignObject>} then renders as an empty, correctly positioned group.
     */
    public RenderContext withForeignObjectHandler(ForeignObjectHandler foreignObjectHandler) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
            foreignObjectHandler);
    }

    public double getViewportWidth() {
        return viewportWidth;
    }

    public double getViewportHeight() {
        return viewportHeight;
    }

    public SvgElementIndex getElementIndex() {
        return elementIndex;
    }

    public Optional<Bounds> getObjectBoundingBox() {
        return Optional.ofNullable(objectBoundingBox);
    }

    public Optional<URI> getBaseUri() {
        return Optional.ofNullable(baseUri);
    }

    public Locale getLocale() {
        return locale;
    }

    public Optional<Consumer<SvgAnchor>> getAnchorActivationHandler() {
        return Optional.ofNullable(anchorActivationHandler);
    }

    public Optional<ForeignObjectHandler> getForeignObjectHandler() {
        return Optional.ofNullable(foreignObjectHandler);
    }

    /**
     * Resolves a length to user units: a percentage resolves against the reference length for {@code axis} (the
     * current viewport's width, height, or, for {@link Axis#DIAGONAL}, its diagonal per SVG 1.1 7.10); anything else
     * resolves to its own pixel value regardless of axis. A null size resolves to zero.
     */
    public double resolveLength(Size size, Axis axis) {
        if (size == null) {
            return 0;
        }
        if (size.getUnits() == SizeUnits.PERCENT) {
            return size.getValue() / 100.0 * referenceLength(axis);
        }
        return size.pixels();
    }

    /**
     * Resolves a coordinate under {@code objectBoundingBox} units, where a bare number and a percentage both mean a
     * fraction of the bounding box (0.5 and 50% are the same fraction) - unlike {@link #resolveLength}, there is no
     * reference length to multiply by here, since the caller multiplies the returned fraction by the bounding box
     * dimension itself. A null size resolves to zero.
     */
    public static double resolveFraction(Size size) {
        if (size == null) {
            return 0;
        }
        return size.getUnits() == SizeUnits.PERCENT ? size.getValue() / 100.0 : size.getValue();
    }

    private double referenceLength(Axis axis) {
        switch (axis) {
            case HORIZONTAL:
                return viewportWidth;
            case VERTICAL:
                return viewportHeight;
            case DIAGONAL:
            default:
                return Math.sqrt((viewportWidth * viewportWidth + viewportHeight * viewportHeight) / 2.0);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String property) {
        return (T) style.get(property);
    }

    @Override
    public void set(String property, Object value) {
        style.set(property, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return style.getProperties();
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper("renderContext");
        builder.add("viewportWidth", viewportWidth);
        builder.add("viewportHeight", viewportHeight);
        builder.add("style", style);
        if (objectBoundingBox != null) {
            builder.add("objectBoundingBox", objectBoundingBox);
        }
        return builder.toString();
    }

}
