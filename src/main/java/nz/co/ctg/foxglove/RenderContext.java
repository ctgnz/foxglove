package nz.co.ctg.foxglove;

import java.util.Map;
import java.util.Optional;

import com.google.common.base.MoreObjects.ToStringHelper;

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
        return new RenderContext(SvgInheritedStyle.root(), elementIndex, viewportWidth, viewportHeight, null);
    }

    private final SvgInheritedStyle style;
    private final SvgElementIndex elementIndex;
    private final double viewportWidth;
    private final double viewportHeight;
    private final Bounds objectBoundingBox;

    private RenderContext(SvgInheritedStyle style, SvgElementIndex elementIndex, double viewportWidth, double viewportHeight,
        Bounds objectBoundingBox) {
        this.style = style;
        this.elementIndex = elementIndex;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.objectBoundingBox = objectBoundingBox;
    }

    /**
     * The context a container hands to one of its children: the container's own style resolved one level further,
     * same viewport, index and object bounding box.
     */
    public RenderContext resolveChild(ISvgAttributes element) {
        return new RenderContext(SvgInheritedStyle.resolve(style, element), elementIndex, viewportWidth, viewportHeight, objectBoundingBox);
    }

    /**
     * The context inside a newly established viewport - a nested {@code <svg>} - with the same style and index, the
     * new viewport size, and no object bounding box (a new viewport is not itself bound to a shape).
     */
    public RenderContext withViewport(double width, double height) {
        return new RenderContext(style, elementIndex, width, height, null);
    }

    /**
     * The context while resolving {@code objectBoundingBox}-mode coordinates against {@code bbox}. Not yet
     * populated by anything in this codebase - carried ahead of the paint server and clip/mask/marker work that
     * will call it.
     */
    public RenderContext withObjectBoundingBox(Bounds bbox) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, bbox);
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
