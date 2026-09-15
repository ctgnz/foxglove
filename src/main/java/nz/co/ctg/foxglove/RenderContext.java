package nz.co.ctg.foxglove;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.net.URI;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.element.SvgAnchor;

/**
 * The state threaded through rendering that a bare parent element cannot carry on its own: the style resolved down the ancestor chain (see {@link SvgInheritedStyle}), the
 * document's {@link SvgElementIndex}, the nearest enclosing viewport, and - where established - the object bounding box that {@code objectBoundingBox}-mode coordinates resolve
 * against.
 * <p>
 * Implements {@link ISvgStylable} by delegating to the wrapped {@link SvgInheritedStyle}, so it can stand in anywhere a resolved parent style was previously expected.
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
     * Parses one of the {@code *Units} attributes ({@code gradientUnits}, {@code patternUnits}, {@code patternContentUnits}, {@code clipPathUnits}, {@code maskUnits},
     * {@code maskContentUnits}, {@code filterUnits}, {@code primitiveUnits}), falling back to {@code defaultMode} when {@code raw} is blank or unrecognised. {@code markerUnits} is
     * not one of these - its values are {@code strokeWidth}/ {@code userSpaceOnUse} rather than a {@code userSpaceOnUse}/{@code objectBoundingBox} choice.
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
     * The context at the top of the document: no inherited style, the document's element index, and the given initial viewport. This is the only context that reports
     * {@link #isDocumentRoot()}.
     */
    public static RenderContext root(SvgElementIndex elementIndex, double viewportWidth, double viewportHeight) {
        return new RenderContext(SvgInheritedStyle.root(), elementIndex, viewportWidth, viewportHeight, null, null, Locale.getDefault(), null,
                                 null, null, Collections.emptySet(), true);
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
    private final Map<ISvgElement, Node> nodeRegistry;
    private final Set<ISvgElement> activeUseTargets;
    private final boolean documentRoot;

    private RenderContext(SvgInheritedStyle style, SvgElementIndex elementIndex, double viewportWidth, double viewportHeight,
                          Bounds objectBoundingBox, URI baseUri, Locale locale, Consumer<SvgAnchor> anchorActivationHandler,
                          ForeignObjectHandler foreignObjectHandler, Map<ISvgElement, Node> nodeRegistry, Set<ISvgElement> activeUseTargets,
                          boolean documentRoot) {
        this.style = style;
        this.elementIndex = elementIndex;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.objectBoundingBox = objectBoundingBox;
        this.baseUri = baseUri;
        this.locale = locale;
        this.anchorActivationHandler = anchorActivationHandler;
        this.foreignObjectHandler = foreignObjectHandler;
        this.nodeRegistry = nodeRegistry;
        this.activeUseTargets = activeUseTargets;
        this.documentRoot = documentRoot;
    }

    /**
     * Whether this context is the one the document's outermost element renders against - true only for {@link #root}, cleared the moment anything descends through
     * {@link #resolveChild}.
     * <p>
     * Exists because a handful of attributes mean something different, or nothing at all, on the outermost {@code <svg>}: its {@code x}/{@code y} are ignored entirely (SVG 1.1
     * 5.1.2), since there is no parent coordinate system to be positioned within, whereas on a nested {@code <svg>} they position it in its parent.
     */
    public boolean isDocumentRoot() {
        return documentRoot;
    }

    /**
     * The context a container hands to one of its children: the container's own style resolved one level further, same viewport, index, object bounding box, base URI, locale and
     * handlers.
     * <p>
     * This is also the single point where {@link #isDocumentRoot()} is cleared, and the reason that flag can be trusted: every element rendered as somebody's child arrives through
     * here, so nothing below the top of the document can claim to be the root, however the context was otherwise derived on the way down.
     */
    public RenderContext resolveChild(ISvgAttributes element) {
        return new RenderContext(SvgInheritedStyle.resolve(style, element), elementIndex, viewportWidth, viewportHeight, objectBoundingBox,
                                 baseUri, locale, anchorActivationHandler, foreignObjectHandler, nodeRegistry, activeUseTargets, false);
    }

    /**
     * The context inside a newly established viewport - a nested {@code <svg>} - with the same style, index, base URI, locale and handlers, the new viewport size, and no object
     * bounding box (a new viewport is not itself bound to a shape).
     */
    public RenderContext withViewport(double width, double height) {
        return new RenderContext(style, elementIndex, width, height, null, baseUri, locale, anchorActivationHandler, foreignObjectHandler,
                                 nodeRegistry, activeUseTargets, documentRoot);
    }

    /**
     * The context with a different {@link SvgElementIndex} established - what {@code <use>} of a target resolved from another document (see
     * {@link SvgElementIndex#resolveWithOwner}) hands to that target's own {@code createGraphic}/{@code appendContent}, so any further reference inside the target's own content
     * resolves against the document it actually belongs to rather than the {@code <use>} site's document. A no-op for the ordinary same-document case, where the resolved owner is
     * this context's own index already.
     */
    public RenderContext withElementIndex(SvgElementIndex elementIndex) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
                                 foreignObjectHandler, nodeRegistry, activeUseTargets, documentRoot);
    }

    /**
     * The context while resolving {@code objectBoundingBox}-mode coordinates against {@code bbox}. Not yet populated by anything in this codebase - carried ahead of the paint
     * server and clip/mask/marker work that will call it.
     */
    public RenderContext withObjectBoundingBox(Bounds bbox) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, bbox, baseUri, locale, anchorActivationHandler,
                                 foreignObjectHandler, nodeRegistry, activeUseTargets, documentRoot);
    }

    /**
     * The context with the document's base URI established - what a relative {@code xlink:href}, such as on {@code <image>}, resolves against. Set once at the root from wherever
     * the document was parsed from (see {@link FoxgloveParser#parseFile}); absent when parsed from a bare stream with no known source.
     */
    public RenderContext withBaseUri(URI baseUri) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
                                 foreignObjectHandler, nodeRegistry, activeUseTargets, documentRoot);
    }

    /**
     * The context with the locale {@code systemLanguage} (see {@link ISvgConditionalFeatures}) evaluates against established - defaults to {@link Locale#getDefault()} at
     * {@link #root}, overridable by a caller that wants to render the same document for a specific language.
     */
    public RenderContext withLocale(Locale locale) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
                                 foreignObjectHandler, nodeRegistry, activeUseTargets, documentRoot);
    }

    /**
     * The context with a callback established for {@code <a>} activation - invoked with the {@link SvgAnchor} when its rendered content is clicked. Absent by default: this library
     * does not own a browser, so "following a link" is entirely up to the embedding application. A caller wanting this (or {@link #withForeignObjectHandler}) builds a context
     * directly via {@link #root} rather than through {@code SvgGraphic.createGroup()} - there is no dedicated overload per optional capability, since that stops scaling once there
     * is more than one.
     */
    public RenderContext withAnchorActivationHandler(Consumer<SvgAnchor> anchorActivationHandler) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
                                 foreignObjectHandler, nodeRegistry, activeUseTargets, documentRoot);
    }

    /**
     * The context with a handler established for {@code <foreignObject>} content (see {@link ForeignObjectHandler}). Absent by default - a {@code <foreignObject>} then renders as
     * an empty, correctly positioned group.
     */
    public RenderContext withForeignObjectHandler(ForeignObjectHandler foreignObjectHandler) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
                                 foreignObjectHandler, nodeRegistry, activeUseTargets, documentRoot);
    }

    /**
     * The context with a registry established to record each element's own built {@link Node} as rendering proceeds (see
     * {@link nz.co.ctg.foxglove.ISvgGraphicsAttributes#registerNode}) - what {@link SvgGraphic#createAnimatedGraphic} uses to resolve an animation's target element back to the
     * concrete node it needs to animate. Absent by default, the same as the other optional capabilities above - a plain {@link SvgGraphic#createGroup()}/{@link #root} caller pays
     * nothing for this.
     */
    public RenderContext withNodeRegistry(Map<ISvgElement, Node> nodeRegistry) {
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale, anchorActivationHandler,
                                 foreignObjectHandler, nodeRegistry, activeUseTargets, documentRoot);
    }

    /**
     * The context with {@code target} added to the set of elements currently being expanded as a {@code <use>} reference somewhere up this call chain (see
     * {@code SvgUse#createGraphic} for the two points it calls this - once for the {@code <use>} element itself, once for whatever it resolves to) - empty by default, at
     * {@link #root}. Unlike every other {@code withXxx} method here, the added element accumulates rather than replacing what was there: each sibling branch of the render tree
     * gets its own independent copy (this context is otherwise immutable), so one sibling reusing the same target as another is never mistaken for a cycle - only actually
     * revisiting something already active on THIS branch's own chain is.
     */
    public RenderContext withActiveUseTarget(ISvgElement target) {
        Set<ISvgElement> updated = Collections.newSetFromMap(new IdentityHashMap<>());
        updated.addAll(activeUseTargets);
        updated.add(target);
        return new RenderContext(style, elementIndex, viewportWidth, viewportHeight, objectBoundingBox, baseUri, locale,
                                 anchorActivationHandler, foreignObjectHandler, nodeRegistry, Collections.unmodifiableSet(updated), documentRoot);
    }

    /**
     * Whether {@code target} is already being expanded somewhere up this call chain - see {@link #withActiveUseTarget}. A {@code <use>} resolving to a target this returns
     * {@code true} for is a reference cycle (SVG 1.1 5.6: "If the referenced element... is an ancestor of the 'use' element... in the DOM" generalises, in practice, to any cycle
     * reachable purely through reference chains, not just static containment - see {@link SvgElementIndex#isSelfOrAncestor} for the complementary static-containment check this
     * doesn't replace) and must not be rendered, rather than expanded forever.
     */
    public boolean isActiveUseTarget(ISvgElement target) {
        return activeUseTargets.contains(target);
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

    public Optional<Map<ISvgElement, Node>> getNodeRegistry() {
        return Optional.ofNullable(nodeRegistry);
    }

    /**
     * Resolves a length to user units: a percentage resolves against the reference length for {@code axis} (the current viewport's width, height, or, for {@link Axis#DIAGONAL},
     * its diagonal per SVG 1.1 7.10); anything else resolves to its own pixel value regardless of axis. A null size resolves to zero.
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
     * Resolves a coordinate under {@code objectBoundingBox} units, where a bare number and a percentage both mean a fraction of the bounding box (0.5 and 50% are the same
     * fraction) - unlike {@link #resolveLength}, there is no reference length to multiply by here, since the caller multiplies the returned fraction by the bounding box dimension
     * itself. A null size resolves to zero.
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
