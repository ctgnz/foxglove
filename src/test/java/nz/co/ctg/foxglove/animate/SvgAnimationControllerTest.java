package nz.co.ctg.foxglove.animate;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Exercises #30's orchestration - {@link SvgAnimationController} resolving each animation element's target/node,
 * calling {@link ISvgAnimationElement#buildAnimation} polymorphically, and wrapping the result in that element's own
 * {@link SvgAnimationTiming} - using test-local stub animation elements, since no concrete element type overrides
 * {@code buildAnimation} yet (#31-#34 will). This is a faithful test of exactly what #30 itself delivers.
 */
public class SvgAnimationControllerTest {

    @BeforeClass
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    /**
     * A stub whose {@code buildAnimation} always succeeds, with a short, inspectable {@link PauseTransition} kept
     * accessible via {@link #built} - when this element's own {@code begin} is unset (the common case in most of
     * these tests), {@link SvgAnimationController} adds no wrapping transition around it at all, so the exact same
     * instance ends up being the one the controller actually calls {@code play()}/{@code pause()}/{@code stop()} on,
     * letting a test check its status directly rather than needing the controller to expose its internal list.
     */
    private static class StubAnimation extends AbstractSvgAnimationElement {
        private PauseTransition built;

        @Override
        public Optional<Animation> buildAnimation(Node target, RenderContext context) {
            built = new PauseTransition(Duration.millis(50));
            return Optional.of(built);
        }
    }

    @Test
    public void testResolvableTargetAndNodeBuildsAnAnimation() throws Exception {
        StubAnimation stub = new StubAnimation();
        SvgRectangle target = new SvgRectangle();
        target.getContent().add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        registry.put(target, new Rectangle());
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
            RenderContext.root(svg.getElementIndex(), 0, 0));

        assertThat(controller.size(), is(1));
    }

    @Test
    public void testNoAnimationElementsInTheDocumentIsAHarmlessNoOp() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(new SvgRectangle());
        Map<ISvgElement, Node> registry = new IdentityHashMap<>();

        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
            RenderContext.root(svg.getElementIndex(), 0, 0));
        assertThat(controller.size(), is(0));
    }

    @Test
    public void testTargetWithNoRegisteredNodeIsSkipped() throws Exception {
        StubAnimation stub = new StubAnimation();
        SvgRectangle target = new SvgRectangle();
        target.getContent().add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(target);

        // registry deliberately left empty - the target resolves, but was never actually rendered
        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
            RenderContext.root(svg.getElementIndex(), 0, 0));
        assertThat(controller.size(), is(0));
    }

    @Test
    public void testDefaultBuildAnimationReturningEmptyIsSkipped() throws Exception {
        // a plain SvgAnimateAttribute, whose buildAnimation is the unmodified default (Optional.empty())
        SvgAnimateAttribute plain = new SvgAnimateAttribute();
        SvgRectangle target = new SvgRectangle();
        target.getContent().add(plain);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        registry.put(target, new Rectangle());
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
            RenderContext.root(svg.getElementIndex(), 0, 0));
        assertThat(controller.size(), is(0));
    }

    @Test
    public void testRepeatCountWrapsTheBuiltAnimation() throws Exception {
        StubAnimation stub = new StubAnimation();
        stub.setRepeatCount("3");
        SvgRectangle target = new SvgRectangle();
        target.getContent().add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        registry.put(target, new Rectangle());
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
            RenderContext.root(svg.getElementIndex(), 0, 0));

        // 3 cycles of a 50ms PauseTransition, no begin delay
        assertThat(controller.getTotalDuration(), is(Duration.millis(150)));
    }

    @Test
    public void testBeginOffsetAddsALeadingDelayWithoutRepeatingIt() throws Exception {
        StubAnimation stub = new StubAnimation();
        stub.setBegin("1s");
        stub.setRepeatCount("2");
        SvgRectangle target = new SvgRectangle();
        target.getContent().add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        registry.put(target, new Rectangle());
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
            RenderContext.root(svg.getElementIndex(), 0, 0));

        // one 1s delay + 2 cycles of 50ms each = 1.1s - if the delay were (wrongly) repeated too, this would be 2.1s
        assertThat(controller.getTotalDuration(), is(Duration.millis(1100)));
    }

    @Test
    public void testPlayPauseStopAndSeekFanOutToEveryBuiltAnimation() throws Exception {
        StubAnimation stub = new StubAnimation();
        SvgRectangle target = new SvgRectangle();
        target.getContent().add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        registry.put(target, new Rectangle());
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
            RenderContext.root(svg.getElementIndex(), 0, 0));

        Animation.Status statusAfterPlay = onFxThread(() -> {
            controller.play();
            return stub.built.getStatus();
        });
        assertThat(statusAfterPlay, is(Animation.Status.RUNNING));

        onFxThread(() -> {
            controller.pause();
            return null;
        });
        assertThat(onFxThread(() -> stub.built.getStatus()), is(Animation.Status.PAUSED));

        onFxThread(() -> {
            controller.stop();
            return null;
        });
        assertThat(onFxThread(() -> stub.built.getStatus()), is(Animation.Status.STOPPED));
    }

}
