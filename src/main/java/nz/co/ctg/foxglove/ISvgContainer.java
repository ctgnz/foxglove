package nz.co.ctg.foxglove;

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
     * @param inherited the style in force outside this container, or null at the root of the document
     */
    default void appendContent(Group target, ISvgStylable inherited) {
        SvgInheritedStyle style = SvgInheritedStyle.resolve(inherited, this);
        for (ISvgElement child : getContent()) {
            if (child instanceof FxGraphic<?> graphic && isRendered(child)) {
                Node node = graphic.createGraphic(style);
                if (node != null) {
                    target.getChildren().add(node);
                }
            }
        }
    }

    /**
     * Whether a child takes part in rendering at all.
     * <p>
     * An element with {@code display="none"} is absent from the scene graph rather than merely hidden, which is what
     * separates {@code display} from {@code visibility} - the latter still occupies its place and contributes to
     * bounds. Because this is a decision about whether to include a child, it belongs to the container: an element's
     * own {@code createGraphic} builds the node it was asked for and does not consult {@code display}.
     */
    static boolean isRendered(ISvgElement child) {
        return !(child instanceof ISvgStylable stylable) || stylable.isVisible();
    }

}
