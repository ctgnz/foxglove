package nz.co.ctg.foxglove.animate;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * Exercises #30's orchestration - {@link SvgAnimationController} resolving each animation element's target/node, calling {@link ISvgAnimationElement#buildAnimation}
 * polymorphically, and wrapping the result in that element's own {@link SvgAnimationTiming} - using test-local stub animation elements, since no concrete element type overrides
 * {@code buildAnimation} yet (#31-#34 will). This is a faithful test of exactly what #30 itself delivers.
 */
public class SvgAnimationControllerTest {

    @BeforeAll
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    /**
     * A stub whose {@code buildAnimation} always succeeds, with a short, inspectable {@link PauseTransition} kept accessible via {@link #built} - when this element's own
     * {@code begin} is unset (the common case in most of these tests), {@link SvgAnimationController} adds no wrapping transition around it at all, so the exact same instance ends
     * up being the one the controller actually calls {@code play()}/{@code pause()}/{@code stop()} on, letting a test check its status directly rather than needing the controller
     * to expose its internal list.
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
        target.getContent()
            .add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        registry.put(target, new Rectangle());
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));

        assertThat(controller.size(), is(1));
    }

    @Test
    public void testNoAnimationElementsInTheDocumentIsAHarmlessNoOp() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(new SvgRectangle());
        Map<ISvgElement, Node> registry = new IdentityHashMap<>();

        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));
        assertThat(controller.size(), is(0));
    }

    @Test
    public void testTargetWithNoRegisteredNodeIsSkipped() throws Exception {
        StubAnimation stub = new StubAnimation();
        SvgRectangle target = new SvgRectangle();
        target.getContent()
            .add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

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
        target.getContent()
            .add(plain);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

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
        target.getContent()
            .add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

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
        target.getContent()
            .add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        registry.put(target, new Rectangle());
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));

        // one 1s delay + 2 cycles of 50ms each = 1.1s - if the delay were (wrongly) repeated too, this would be 2.1s
        assertThat(controller.getTotalDuration(), is(Duration.millis(1100)));
    }

    @Test
    public void testAccumulateSumWithFiniteRepeatCountIsNotDoubleWrapped() throws Exception {
        // #32's accumulate="sum" builds one continuous Timeline already spanning every repeat - repeatCount must
        // not be applied again on top of that, which would replay the whole already-unrolled sequence again
        SvgAnimateAttribute animate = new SvgAnimateAttribute();
        animate.setAttributeName("x");
        animate.setDuration("1s");
        animate.setValues("0;10");
        animate.setAccumulate("sum");
        animate.setRepeatCount("3");
        animate.setFill("freeze"); // isolate accumulate+repeatCount interaction from #149's own fill=remove revert

        SvgRectangle target = new SvgRectangle();
        target.getContent()
            .add(animate);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        registry.put(target, new Rectangle());
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));

        // 3 accumulated 1s cycles = 3s total - if repeatCount were (wrongly) re-applied, this would be 9s
        assertThat(controller.getTotalDuration(), is(Duration.seconds(3)));
    }

    /**
     * The controller-level half of #149's {@code fill="remove"} support: {@link SvgValueAnimationBuilder} returns a {@code SequentialTransition} whose first child (a
     * {@code Timeline} playing with {@code cycleCount=3} itself) already spans every repeat, and this must be played with the controller's own {@code cycleCount} left at 1, not
     * re-wrapped in the controller's generic {@code repeatCount} on top of that. A one-cycle {@code Timeline} wrongly wrapped in {@code repeatCount=3} <i>again</i> at the
     * controller level would report roughly double - the class of bug a purely {@link SvgValueAnimationBuilder}-level test cannot see at all, since building the raw
     * {@code Animation} never touches the controller's own {@code cycleCount} to begin with.
     */
    @Test
    public void testFillRemoveWithFiniteRepeatCountIsNotDoubleWrapped() throws Exception {
        SvgAnimateAttribute animate = new SvgAnimateAttribute();
        animate.setAttributeName("x");
        animate.setDuration("1s");
        animate.setValues("0;10");
        animate.setRepeatCount("3");
        animate.setFill("remove");

        SvgRectangle target = new SvgRectangle();
        target.getContent()
            .add(animate);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        registry.put(target, new Rectangle());
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));

        // 3 cycles of 1s (core, cycled internally) + a 1ms revert (Duration.millis(1), not .ZERO - #152) = 3001ms
        // total - if the controller (wrongly) re-applied repeatCount=3 on top of that, this would come out around
        // 9s instead
        assertThat(controller.getTotalDuration(), is(Duration.millis(3001)));
    }

    /**
     * <b>The inconvenient case, and #151's own regression coverage.</b> A JavaFX {@code Animation} that has never been {@code play()}ed does not apply anything to its target
     * property when {@code jumpTo()} is called - confirmed empirically, not merely assumed. {@link SvgAnimationController#seek} was a complete no-op for a caller who seeks before
     * ever calling {@code play()} - exactly how the #112 conformance harness uses it, and how a scrub-bar UI would too. The constructor now warms up every built animation with a
     * discreet {@code play()}/{@code pause()} so seeking works regardless of call order; this asserts that specifically, never touching {@link SvgAnimationController#play()} at
     * all.
     */
    @Test
    public void testSeekAppliesTheCorrectValueEvenWhenPlayWasNeverCalled() throws Exception {
        SvgAnimateAttribute animate = new SvgAnimateAttribute();
        animate.setAttributeName("x");
        animate.setDuration("10s");
        animate.setFrom("0");
        animate.setTo("100");

        SvgRectangle target = new SvgRectangle();
        target.getContent()
            .add(animate);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        Rectangle node = new Rectangle();
        registry.put(target, node);
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));

        onFxThread(() -> {
            controller.seek(Duration.seconds(5));
            return null;
        });
        assertThat("halfway through a 0->100 animation, seeked to directly with no prior play()",
            onFxThread(node::getX), closeTo(50.0, 1e-6));
    }

    /**
     * <b>#152's own regression coverage.</b> {@code fill="remove"}'s revert lives in its own {@code Timeline}, played after {@code core} via {@code SequentialTransition} - but a
     * {@code Timeline} whose only {@code KeyFrame} sits at {@code Duration.ZERO} has zero temporal footprint inside a {@code SequentialTransition}: it never counts toward
     * {@code getTotalDuration()}, and seeking past the end never applies it, even though real uninterrupted playback does apply it correctly on entry. This seeks (never calling
     * {@code play()} to actual completion) well past the animation's 1s active duration - exactly how #112's conformance harness and any scrub-bar UI consume this controller - and
     * fails against a reverted {@code Duration.ZERO} KeyFrame, passing only once the revert is given a real (if tiny) span.
     */
    @Test
    public void testFillRemoveRevertsWhenSeekedPastTheEnd() throws Exception {
        SvgAnimateAttribute animate = new SvgAnimateAttribute();
        animate.setAttributeName("x");
        animate.setDuration("1s");
        animate.setFrom("0");
        animate.setTo("100");
        animate.setFill("remove");

        SvgRectangle target = new SvgRectangle();
        target.getContent()
            .add(animate);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        Rectangle node = new Rectangle();
        node.setX(7);
        registry.put(target, node);
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));

        onFxThread(() -> {
            controller.seek(Duration.seconds(5)); // well past the 1s active duration
            return null;
        });
        assertThat("seeked past the end, fill=remove must have reverted to the pre-animation value",
            onFxThread(node::getX), closeTo(7.0, 1e-6));
    }

    /** As above, for {@link SvgAnimateTransformBuilder}'s own identical {@code fill="remove"} revert (#152). */
    @Test
    public void testFillRemoveOnAnimateTransformRevertsWhenSeekedPastTheEnd() throws Exception {
        SvgAnimateTransform animate = new SvgAnimateTransform();
        animate.setType("rotate");
        animate.setDuration("1s");
        animate.setFrom("0");
        animate.setTo("90");
        animate.setFill("remove");

        SvgRectangle target = new SvgRectangle();
        target.getContent()
            .add(animate);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        Rectangle node = new Rectangle();
        registry.put(target, node);
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));

        onFxThread(() -> {
            controller.seek(Duration.seconds(5)); // well past the 1s active duration
            return null;
        });
        double angle = onFxThread(() -> ((javafx.scene.transform.Rotate) node.getTransforms()
            .get(0)).getAngle());
        assertThat("seeked past the end, fill=remove must have reverted the rotation to its identity (angle 0)",
            angle, closeTo(0.0, 1e-6));
    }

    /**
     * <b>#86, the composition case that already works.</b> Two {@code <animate>} elements on the same attribute with disjoint active windows - {@code [0s, 3s)} then
     * {@code [3s, 6s)}, exactly the shape every real same-attribute case in the W3C suite takes (see {@code animate-elem-32-t}) - correctly hand off from one to the other rather
     * than clobbering, which is an emergent property of this class's own document-ordered {@link SvgAnimationController#build}/{@link SvgAnimationController#seek}, not a
     * deliberate composition feature; see this class's own javadoc for why. Sampled across both windows and past the end, where {@code fill="freeze"} on the second (and therefore
     * document-order-last, therefore seek-order-last) element must win.
     */
    @Test
    public void testSequentialAnimationsOnTheSameAttributeHandOffCorrectly() throws Exception {
        SvgAnimateAttribute first = new SvgAnimateAttribute();
        first.setAttributeName("width");
        first.setFrom("0");
        first.setTo("25");
        first.setBegin("0s");
        first.setDuration("3s");
        first.setFill("freeze");

        SvgAnimateAttribute second = new SvgAnimateAttribute();
        second.setAttributeName("width");
        second.setFrom("25");
        second.setTo("0");
        second.setBegin("3s");
        second.setDuration("3s");
        second.setFill("freeze");

        SvgRectangle target = new SvgRectangle();
        target.getContent()
            .add(first);
        target.getContent()
            .add(second);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        Rectangle node = new Rectangle();
        registry.put(target, node);
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));

        double[] times = {
            0, 1.5, 3, 4.5, 6, 7
        };
        double[] expected = {
            0, 12.5, 25, 12.5, 0, 0
        };
        for (int i = 0; i < times.length; i++) {
            double time = times[i];
            double width = onFxThread(() -> {
                controller.seek(Duration.seconds(time));
                return node.getWidth();
            });
            assertThat("at t=" + time + "s", width, closeTo(expected[i], 1e-6));
        }
    }

    /**
     * <b>#86, the composition case that is deliberately not implemented.</b> Two {@code <animate>} elements with {@code additive="sum"} and genuinely <i>overlapping</i> active
     * windows (both {@code [0s, 4s)}) should, under SMIL's real model, sum their two contributions at every instant - here the later one in document order simply overwrites the
     * property, discarding the earlier one's contribution entirely. This test pins that known, documented gap (see this class's own javadoc) rather than the SMIL-correct sum, so a
     * future change to this behaviour is a deliberate decision, not an accidental regression this suite fails to notice either way.
     */
    @Test
    public void testOverlappingAdditiveSumOnTheSameAttributeIsNotComposed() throws Exception {
        SvgAnimateAttribute first = new SvgAnimateAttribute();
        first.setAttributeName("width");
        first.setFrom("0");
        first.setTo("10");
        first.setBegin("0s");
        first.setDuration("4s");
        first.setFill("freeze");
        first.setAdditive("sum");

        SvgAnimateAttribute second = new SvgAnimateAttribute();
        second.setAttributeName("width");
        second.setFrom("0");
        second.setTo("100");
        second.setBegin("0s");
        second.setDuration("4s");
        second.setFill("freeze");
        second.setAdditive("sum");

        SvgRectangle target = new SvgRectangle();
        target.getContent()
            .add(first);
        target.getContent()
            .add(second);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

        Map<ISvgElement, Node> registry = new IdentityHashMap<>();
        Rectangle node = new Rectangle();
        registry.put(target, node);
        SvgAnimationController controller = new SvgAnimationController(svg.getElementIndex(), registry,
                                                                       RenderContext.root(svg.getElementIndex(), 0, 0));

        onFxThread(() -> {
            controller.seek(Duration.seconds(2));
            return null;
        });
        assertThat("the SMIL-correct composed value would be 55 (2.5*2 + 25*2) - only the document-order-last "
                   + "element's own value survives",
            onFxThread(node::getWidth), closeTo(50.0, 1e-6));
    }

    @Test
    public void testPlayPauseStopAndSeekFanOutToEveryBuiltAnimation() throws Exception {
        StubAnimation stub = new StubAnimation();
        SvgRectangle target = new SvgRectangle();
        target.getContent()
            .add(stub);
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);

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
