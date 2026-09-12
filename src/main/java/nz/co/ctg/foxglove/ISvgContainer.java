package nz.co.ctg.foxglove;

import java.util.Locale;

import nz.co.ctg.foxglove.element.SvgMarkerRenderer;

import javafx.scene.Group;
import javafx.scene.Node;

/**
 * An element that renders its content as child nodes.
 * <p>
 * A child takes part in rendering if and only if it implements {@link FxGraphic}. Elements that exist only to be
 * referenced or described - {@code <defs>}, {@code <title>}, {@code <desc>}, {@code <metadata>} and the animation
 * elements - are excluded structurally by not implementing it, rather than by being absent from a list of the types
 * a container knows how to draw. Adding a new renderable element therefore needs nothing here.
 */
public interface ISvgContainer extends ISvgContent, ISvgStylable {

    /**
     * Builds a graphic for each renderable child and appends it to the given node, in document order.
     * <p>
     * Children are handed this container's own style resolved against what it inherited, rather than the container
     * element itself. SVG inheritance walks the whole ancestor chain, but an element is only ever passed its
     * immediate parent, so the accumulated style has to descend with the traversal for a grandparent's fill to
     * reach a grandchild.
     *
     * @param inherited the rendering context in force outside this container
     */
    default void appendContent(Group target, RenderContext inherited) {
        RenderContext context = inherited.resolveChild(this);
        for (ISvgElement child : getContent()) {
            if (child instanceof FxGraphic<?> graphic && isRendered(child, context.getLocale())) {
                Node node = graphic.createGraphic(context);
                if (node != null) {
                    node = SvgMarkerRenderer.applyMarkers(node, child, context);
                    node = applyChildMask(child, node, context);
                    target.getChildren().add(node);
                }
            }
        }
    }

    /**
     * Applies {@code child}'s own {@code mask}, if any, to its already-built {@code node} - the consumer side,
     * mirroring where markers are applied just above, rather than inside each element's own {@code createGraphic}.
     * <p>
     * This is where masking has to live for a shape: {@link nz.co.ctg.foxglove.shape.AbstractSvgShape} declares
     * {@code createGraphic} to return {@code S extends Shape}, not a plain {@link Node}, so a shape cannot return an
     * {@link javafx.scene.image.ImageView}-based replacement from inside its own method the way a container (which
     * already returns {@code Group}/{@code Node}) could. Applying masking uniformly here instead - for every child,
     * container or shape alike - avoids a child masking itself once internally and then being masked again by its
     * own consumer, since a container never masks its own return value from within its own {@code createGraphic}.
     * <p>
     * One consequence: the document root (built via {@code SvgGraphic.createGroup()}, never itself "a child" of
     * anyone's {@code appendContent}) has no consumer to apply this on its behalf - a {@code mask} declared directly
     * on the root {@code <svg>} element is not applied. Accepted as a known, narrow gap; masking a whole document
     * against something defined within itself is a rare case in practice.
     */
    private static Node applyChildMask(ISvgElement child, Node node, RenderContext context) {
        return child instanceof ISvgGraphicsAttributes attrs ? attrs.applyMask(context, node) : node;
    }

    /**
     * Whether a child takes part in rendering at all.
     * <p>
     * An element with {@code display="none"} is absent from the scene graph rather than merely hidden, which is what
     * separates {@code display} from {@code visibility} - the latter still occupies its place and contributes to
     * bounds. Because this is a decision about whether to include a child, it belongs to the container: an element's
     * own {@code createGraphic} builds the node it was asked for and does not consult {@code display}.
     * <p>
     * A failing conditional processing attribute ({@code requiredFeatures}/{@code requiredExtensions}/
     * {@code systemLanguage}, see {@link ISvgConditionalFeatures}) suppresses a child the same way - which is also
     * exactly the test {@code <switch>} uses to pick its first passing child.
     */
    static boolean isRendered(ISvgElement child, Locale locale) {
        if (child instanceof ISvgStylable stylable && !stylable.isVisible()) {
            return false;
        }
        return !(child instanceof ISvgConditionalFeatures cond) || cond.isConditionSatisfied(locale);
    }

}
