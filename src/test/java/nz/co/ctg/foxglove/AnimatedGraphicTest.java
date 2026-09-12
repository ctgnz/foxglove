package nz.co.ctg.foxglove;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.animate.AbstractSvgAnimationElement;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Smoke-tests #30's new, additive entry point: {@code createAnimatedGraphic} builds the same node
 * {@code createGraphic} alone would (the node registry is a pure side effect of rendering, not a different render),
 * and the registry it populates along the way actually resolves back to the real rendered shape - end to end,
 * through the real rendering pipeline rather than a manually-built registry like {@code SvgAnimationControllerTest}.
 */
public class AnimatedGraphicTest {

    @BeforeAll
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    /** A stub whose {@code buildAnimation} always succeeds - see {@code SvgAnimationControllerTest} for the same idea. */
    private static class StubAnimation extends AbstractSvgAnimationElement {
        @Override
        public Optional<Animation> buildAnimation(Node target, RenderContext context) {
            return Optional.of(new PauseTransition(Duration.millis(1)));
        }
    }

    @Test
    public void testCreateAnimatedGraphicReturnsAWorkingNode() throws Exception {
        SvgRectangle rect = new SvgRectangle(0, 0, 10, 10);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(rect);

        AnimatedGraphic result = onFxThread(() -> svg.createAnimatedGraphic(RenderContext.root(svg.getElementIndex(), 0, 0)));
        assertThat(result.node(), notNullValue());
        assertThat(result.animations(), notNullValue());
        assertThat(((Group) result.node()).getChildren().size(), is(1));
    }

    @Test
    public void testNodeRegistryResolvesTheRealRenderedTarget() throws Exception {
        // the stub's own parent (the rect) is its target, per SMIL's default rule - if the registry produced by the
        // real rendering pipeline didn't actually contain the rendered Rectangle, this would come back as 0
        StubAnimation stub = new StubAnimation();
        SvgRectangle rect = new SvgRectangle(0, 0, 10, 10);
        rect.getContent().add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(rect);

        AnimatedGraphic result = onFxThread(() -> svg.createAnimatedGraphic(RenderContext.root(svg.getElementIndex(), 0, 0)));
        assertThat(result.animations().size(), is(1));
    }

}
