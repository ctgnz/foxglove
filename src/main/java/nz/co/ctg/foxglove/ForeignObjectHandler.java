package nz.co.ctg.foxglove;

import javafx.scene.Node;

import nz.co.ctg.foxglove.element.SvgForeignObject;

/**
 * Supplied by the embedding application to render a {@code <foreignObject>}'s content - almost always XHTML, which this library has no HTML engine to render itself (pulling in
 * {@code javafx.scene.web.WebView} would be a heavy, currently-absent dependency for what may be a small part of a document). {@link #render} receives the raw
 * {@link SvgForeignObject#getRawContent() foreign content} and the element's resolved width/height, and returns the {@link Node} to embed, or {@code null} to render nothing for
 * this element.
 */
@FunctionalInterface
public interface ForeignObjectHandler {

    Node render(SvgForeignObject foreignObject, double width, double height);

}
