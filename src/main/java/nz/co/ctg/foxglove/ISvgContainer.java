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
     */
    default void appendContent(Group target) {
        for (ISvgElement child : getContent()) {
            if (child instanceof FxGraphic<?> graphic && isRendered(child)) {
                Node node = graphic.createGraphic(this);
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
