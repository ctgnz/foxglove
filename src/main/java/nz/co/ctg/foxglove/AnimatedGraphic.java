package nz.co.ctg.foxglove;

import nz.co.ctg.foxglove.animate.SvgAnimationController;

import javafx.scene.Node;

/**
 * The result of {@link SvgGraphic#createAnimatedGraphic} - the same built {@link Node} {@code createGraphic}/{@code
 * createGroup} alone would return, paired with an {@link SvgAnimationController} to play, pause, stop and seek
 * whatever animations the document declared. A new, additive entry point (#30): {@code createGroup()}/{@code
 * createGraphic(RenderContext)} are unchanged, so every existing caller keeps compiling without this.
 */
public record AnimatedGraphic(Node node, SvgAnimationController animations) {
}
