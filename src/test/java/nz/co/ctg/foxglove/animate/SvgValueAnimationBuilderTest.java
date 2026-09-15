package nz.co.ctg.foxglove.animate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.junit.jupiter.api.Test;

/**
 * Exercises #32's single-animation correctness for {@code <animate>}/{@code <animateColor>} - built directly against real {@link Rectangle} nodes and inspecting the returned
 * {@link Timeline}, independent of {@link SvgAnimationController} (which #30 already covers). {@code context} is never read by the builder itself, so {@code null} is passed
 * throughout, matching the "these two element types are otherwise identical" premise this issue is built on.
 */
public class SvgValueAnimationBuilderTest {

    // --- from/to/by/values resolution ----------------------------------------

    @Test
    public void testFromAndToProduceATwoPointAnimation() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setFrom("5");
            a.setTo("15");
        }, new Rectangle(0, 0, 10, 10));

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(frames.size(), is(2));
        assertThat(doubleValue(frames.get(0)), closeTo(5.0, 1e-9));
        assertThat(doubleValue(frames.get(1)), closeTo(15.0, 1e-9));
    }

    @Test
    public void testFromAndByProduceATwoPointAnimation() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setFrom("5");
            a.setBy("3");
        }, new Rectangle(0, 0, 10, 10));

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(doubleValue(frames.get(0)), closeTo(5.0, 1e-9));
        assertThat(doubleValue(frames.get(1)), closeTo(8.0, 1e-9));
    }

    /**
     * #99: {@code rx}/{@code ry} are the one geometry attribute where {@link SvgAttributeRegistry} doesn't write the parsed value straight through - it doubles it first, since
     * {@code arcWidth}/{@code arcHeight} are a diameter and SVG's {@code rx}/{@code ry} are radii (the same distinction #93 already fixed for the static resolution). End-to-end
     * through the real builder, not just {@link SvgAttributeRegistryTest}'s own lower-level unit test - this is the shape the issue itself asked for.
     */
    @Test
    public void testAnimatingRxOnARectangleDoublesTheParsedValue() {
        Timeline timeline = build(a -> {
            a.setAttributeName("rx");
            a.setFrom("5");
            a.setTo("15");
        }, new Rectangle(0, 0, 100, 100));

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat("rx=5 is a radius; arcWidth is the full diameter", doubleValue(frames.get(0)), closeTo(10.0, 1e-9));
        assertThat("rx=15 is a radius; arcWidth is the full diameter", doubleValue(frames.get(1)), closeTo(30.0, 1e-9));
    }

    @Test
    public void testToAloneStartsFromTheNodesCurrentValue() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        rect.setX(7);
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setTo("20");
        }, rect);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(doubleValue(frames.get(0)), closeTo(7.0, 1e-9));
        assertThat(doubleValue(frames.get(1)), closeTo(20.0, 1e-9));
    }

    @Test
    public void testByAloneStartsFromTheNodesCurrentValue() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        rect.setX(7);
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setBy("4");
        }, rect);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(doubleValue(frames.get(0)), closeTo(7.0, 1e-9));
        assertThat(doubleValue(frames.get(1)), closeTo(11.0, 1e-9));
    }

    @Test
    public void testValuesListWithMatchingKeyTimesProducesKeyFramesAtTheRightTimes() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setDuration("10s");
            a.setValues("0;5;20");
            a.setKeyTimes("0;0.25;1");
        }, new Rectangle(0, 0, 10, 10));

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(frames.size(), is(3));
        assertThat(frames.get(0)
            .getTime()
            .toSeconds(), closeTo(0.0, 1e-9));
        assertThat(frames.get(1)
            .getTime()
            .toSeconds(), closeTo(2.5, 1e-9));
        assertThat(frames.get(2)
            .getTime()
            .toSeconds(), closeTo(10.0, 1e-9));
    }

    // --- calcMode --------------------------------------------------------------

    @Test
    public void testCalcModeDiscreteUsesTheDiscreteInterpolator() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setFrom("0");
            a.setTo("10");
            a.setCalcMode("discrete");
        }, new Rectangle(0, 0, 10, 10));

        assertThat(interpolator(timeline.getKeyFrames()
            .get(1)), is(Interpolator.DISCRETE));
    }

    @Test
    public void testCalcModeLinearOrUnrecognisedUsesTheLinearInterpolator() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setFrom("0");
            a.setTo("10");
            a.setCalcMode("bogus");
        }, new Rectangle(0, 0, 10, 10));

        assertThat(interpolator(timeline.getKeyFrames()
            .get(1)), is(Interpolator.LINEAR));
    }

    @Test
    public void testCalcModeSplineUsesASplineInterpolatorPerInterval() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setValues("0;5;10");
            a.setCalcMode("spline");
            a.setKeySplines("0.1,0.9,0.9,0.1;0.5,0.5,0.5,0.5");
        }, new Rectangle(0, 0, 10, 10));

        List<KeyFrame> frames = timeline.getKeyFrames();
        Interpolator first = interpolator(frames.get(1));
        Interpolator second = interpolator(frames.get(2));
        assertThat(first, is(not(Interpolator.LINEAR)));
        assertThat(first, is(not(Interpolator.DISCRETE)));
        assertThat(second, is(not(Interpolator.LINEAR)));
        assertThat(second, is(not(Interpolator.DISCRETE)));
    }

    @Test
    public void testCalcModeSplineFallsBackToLinearForAnIntervalWithNoMatchingSpline() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setValues("0;5;10");
            a.setCalcMode("spline");
            a.setKeySplines("0.1,0.9,0.9,0.1");
        }, new Rectangle(0, 0, 10, 10));

        assertThat(interpolator(timeline.getKeyFrames()
            .get(2)), is(Interpolator.LINEAR));
    }

    @Test
    public void testCalcModePacedComputesKeyTimesProportionalToValueDistance() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setDuration("100s");
            a.setValues("0;1;100");
            a.setCalcMode("paced");
        }, new Rectangle(0, 0, 10, 10));

        List<KeyFrame> frames = timeline.getKeyFrames();
        // distances 1 and 99 out of a total of 100 - the middle keyTime lands at 1/100, not evenly at 1/2
        assertThat(frames.get(0)
            .getTime()
            .toSeconds(), closeTo(0.0, 1e-9));
        assertThat(frames.get(1)
            .getTime()
            .toSeconds(), closeTo(1.0, 1e-9));
        assertThat(frames.get(2)
            .getTime()
            .toSeconds(), closeTo(100.0, 1e-9));
    }

    // --- additive/accumulate -----------------------------------------------

    @Test
    public void testAdditiveSumOffsetsByTheNodesPreAnimationValue() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        rect.setX(100);
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setValues("0;10");
            a.setAdditive("sum");
        }, rect);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(doubleValue(frames.get(0)), closeTo(100.0, 1e-9));
        assertThat(doubleValue(frames.get(1)), closeTo(110.0, 1e-9));
    }

    @Test
    public void testAccumulateSumWithFiniteRepeatCountUnrollsIntoAShiftedMultiCycleTimeline() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setDuration("1s");
            a.setValues("0;10");
            a.setAccumulate("sum");
            a.setRepeatCount("3");
        }, new Rectangle(0, 0, 10, 10));

        // cycle boundaries coincide in time and value, so only 4 distinct KeyFrames, not 6
        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(frames.size(), is(4));
        assertThat(timeline.getCycleCount(), is(1));
        assertThat(frames.get(0)
            .getTime()
            .toSeconds(), closeTo(0.0, 1e-9));
        assertThat(doubleValue(frames.get(0)), closeTo(0.0, 1e-9));
        assertThat(frames.get(1)
            .getTime()
            .toSeconds(), closeTo(1.0, 1e-9));
        assertThat(doubleValue(frames.get(1)), closeTo(10.0, 1e-9));
        assertThat(frames.get(2)
            .getTime()
            .toSeconds(), closeTo(2.0, 1e-9));
        assertThat(doubleValue(frames.get(2)), closeTo(20.0, 1e-9));
        assertThat(frames.get(3)
            .getTime()
            .toSeconds(), closeTo(3.0, 1e-9));
        assertThat(doubleValue(frames.get(3)), closeTo(30.0, 1e-9));
    }

    @Test
    public void testAccumulateSumWithIndefiniteRepeatCountFallsBackToAPlainSingleCycleResult() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setDuration("1s");
            a.setValues("0;10");
            a.setAccumulate("sum");
            a.setRepeatCount("indefinite");
        }, new Rectangle(0, 0, 10, 10));

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(frames.size(), is(2));
        assertThat(doubleValue(frames.get(0)), closeTo(0.0, 1e-9));
        assertThat(doubleValue(frames.get(1)), closeTo(10.0, 1e-9));
    }

    // --- fill="remove" (#149) -------------------------------------------------
    //
    // fill="remove" returns a SequentialTransition(core, revert), never a bare Timeline, so these tests build
    // directly against SvgValueAnimationBuilder.build(...) rather than through the Timeline-casting build() helper.

    @Test
    public void testFillRemoveRevertsToThePreAnimationValueAfterDur() {
        Rectangle target = new Rectangle(0, 0, 10, 10);
        target.setX(42);
        SequentialTransition result = buildSequential(a -> {
            a.setAttributeName("x");
            a.setDuration("1s");
            a.setFrom("5");
            a.setTo("15");
            a.setFill("remove");
        }, target);

        List<Animation> children = result.getChildren();
        assertThat(children, hasSize(2));
        Timeline core = (Timeline) children.get(0);
        Timeline revert = (Timeline) children.get(1);
        assertThat("core is the untouched, unreverted animation", core.getKeyFrames()
            .size(), is(2));
        assertThat(doubleValue(core.getKeyFrames()
            .get(1)), closeTo(15.0, 1e-9));
        assertThat("the revert plays after core, back to the pre-animation value",
            doubleValue(revert.getKeyFrames()
                .get(0)),
            closeTo(42.0, 1e-9));
        assertThat("the revert is a hard jump, not an interpolated glide back",
            interpolator(revert.getKeyFrames()
                .get(0)),
            is(Interpolator.DISCRETE));
    }

    /** {@code fill="freeze"} (this file's own fixture default) must be entirely unaffected - a regression check. */
    @Test
    public void testFillFreezeAddsNoRevertFrame() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setFrom("5");
            a.setTo("15");
        }, new Rectangle(0, 0, 10, 10));

        assertThat(timeline.getKeyFrames()
            .size(), is(2));
    }

    /**
     * <b>The inconvenient case.</b> {@code fill="remove"} with {@code repeatCount="3"} must revert only once, after the third cycle - not at the end of every cycle. Also proves
     * each cycle independently replays the whole value list (0 -> 10, 0 -> 10, 0 -> 10) via JavaFX's own {@code cycleCount}, rather than being wrongly unrolled into a single ramp
     * followed by a flat hold - the exact regression a mutation test caught during development: a first implementation reused the {@code accumulate="sum"} unrolling machinery for
     * this case too, which silently dropped every cycle's own first value (valid only when cycles truly are continuous) and produced a flat hold from t=1s to t=3s instead of two
     * more repeats of the ramp.
     */
    @Test
    public void testFillRemoveWithFiniteRepeatCountRevertsOnlyOnceAfterEveryCycleReplays() {
        Rectangle target = new Rectangle(0, 0, 10, 10);
        target.setX(7);
        SequentialTransition result = buildSequential(a -> {
            a.setAttributeName("x");
            a.setDuration("1s");
            a.setValues("0;10");
            a.setRepeatCount("3");
            a.setFill("remove");
        }, target);

        List<Animation> children = result.getChildren();
        Timeline core = (Timeline) children.get(0);
        Timeline revert = (Timeline) children.get(1);

        assertThat("each cycle independently replays the same 2-frame value list",
            core.getKeyFrames()
                .size(),
            is(2));
        assertThat(doubleValue(core.getKeyFrames()
            .get(0)), closeTo(0.0, 1e-9));
        assertThat(doubleValue(core.getKeyFrames()
            .get(1)), closeTo(10.0, 1e-9));
        assertThat("JavaFX's own cycling replays core, not a manual unroll", core.getCycleCount(), is(3));
        assertThat(doubleValue(revert.getKeyFrames()
            .get(0)), closeTo(7.0, 1e-9));
    }

    /** {@code accumulate="sum"} and {@code fill="remove"} are independent triggers and must compose. */
    @Test
    public void testAccumulateSumWithFillRemoveStillRevertsAtTheEnd() {
        Rectangle target = new Rectangle(0, 0, 10, 10);
        target.setX(99);
        SequentialTransition result = buildSequential(a -> {
            a.setAttributeName("x");
            a.setDuration("1s");
            a.setValues("0;10");
            a.setAccumulate("sum");
            a.setRepeatCount("3");
            a.setFill("remove");
        }, target);

        List<Animation> children = result.getChildren();
        Timeline core = (Timeline) children.get(0);
        Timeline revert = (Timeline) children.get(1);

        assertThat("accumulate still needs its own unroll - cycleCount stays 1 on core, unlike the plain case",
            core.getCycleCount(), is(1));
        List<KeyFrame> coreFrames = core.getKeyFrames();
        // the accumulated end value (0 + 3*10 = 30) is core's own last frame, not reset early by the revert
        assertThat(doubleValue(coreFrames.get(coreFrames.size() - 1)), closeTo(30.0, 1e-9));
        assertThat(doubleValue(revert.getKeyFrames()
            .get(0)), closeTo(99.0, 1e-9));
    }

    /** {@code fill="remove"} with {@code repeatCount="indefinite"} never ends, so nothing is ever built to revert. */
    @Test
    public void testFillRemoveWithIndefiniteRepeatCountNeverAppendsARevertFrame() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setDuration("1s");
            a.setFrom("5");
            a.setTo("15");
            a.setRepeatCount("indefinite");
            a.setFill("remove");
        }, new Rectangle(0, 0, 10, 10));

        assertThat(timeline.getKeyFrames()
            .size(), is(2));
    }

    // --- colour bindings -----------------------------------------------------

    @Test
    public void testAdditiveAccumulateAndByAloneOnAColourBindingDegradeRatherThanThrow() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        rect.setFill(Color.BLACK);

        // additive/accumulate are simply ignored on a colour binding - the base from/to animation still builds
        Timeline timeline = build(a -> {
            a.setAttributeName("fill");
            a.setFrom("black");
            a.setTo("white");
            a.setAdditive("sum");
            a.setAccumulate("sum");
            a.setRepeatCount("3");
        }, rect);
        assertThat(timeline.getKeyFrames()
            .size(), is(2));

        // "by" alone can't produce any colour value list at all - the whole animation is unsupported
        assertThat(SvgValueAnimationBuilder.build(animate(a -> {
            a.setAttributeName("fill");
            a.setBy("10");
        }), rect, null)
            .isEmpty(), is(true));
    }

    @Test
    public void testAnimateColorProducesTheSameResultAsAnimateGivenTheSameAttributes() {
        Rectangle rectForAnimate = new Rectangle(0, 0, 10, 10);
        Rectangle rectForAnimateColor = new Rectangle(0, 0, 10, 10);

        SvgAnimateAttribute animate = animate(a -> {
            a.setAttributeName("fill");
            a.setFrom("black");
            a.setTo("white");
        });
        SvgAnimateColor animateColor = new SvgAnimateColor();
        animateColor.setAttributeName("fill");
        animateColor.setFrom("black");
        animateColor.setTo("white");
        animateColor.setFill("freeze"); // match animate's fixture default (see the animate() helper) for parity

        Timeline first = (Timeline) SvgValueAnimationBuilder.build(animate, rectForAnimate, null)
            .orElseThrow();
        Timeline second = (Timeline) SvgValueAnimationBuilder.build(animateColor, rectForAnimateColor, null)
            .orElseThrow();

        assertThat(first.getKeyFrames()
            .size(),
            is(second.getKeyFrames()
                .size()));
        assertThat(colorValue(first.getKeyFrames()
            .get(0)), is(
                colorValue(second.getKeyFrames()
                    .get(0))));
        assertThat(colorValue(first.getKeyFrames()
            .get(1)), is(
                colorValue(second.getKeyFrames()
                    .get(1))));
    }

    // --- unsupported/failure cases -------------------------------------------

    @Test
    public void testUnmappableAttributeNameIsUnsupported() {
        Optional<Animation> result = SvgValueAnimationBuilder.build(animate(a -> {
            a.setAttributeName("not-a-real-attribute");
            a.setFrom("0");
            a.setTo("10");
        }), new Rectangle(), null);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void testAValueThatFailsToParseIsUnsupported() {
        // "x" (a length) tolerates garbage input via SizeAdapter's own fallback, so this needs a binding whose
        // parser can genuinely fail - "fill" does, for anything that isn't a recognised colour
        Optional<Animation> result = SvgValueAnimationBuilder.build(animate(a -> {
            a.setAttributeName("fill");
            a.setFrom("not-a-colour");
            a.setTo("white");
        }), new Rectangle(), null);
        assertThat(result.isEmpty(), is(true));
    }

    // --- helpers -----------------------------------------------------------

    /**
     * Defaults {@code fill="freeze"} - the SVG default is {@code "remove"} (#149), which would add a revert {@code KeyFrame} to every one of these fixtures and break their
     * frame-count assertions for a reason unrelated to whatever each test actually exercises. Tests specifically about {@code fill="remove"} override it via their own
     * {@code configure} lambda, which runs after this and so wins.
     */
    private static SvgAnimateAttribute animate(Consumer<SvgAnimateAttribute> configure) {
        SvgAnimateAttribute element = new SvgAnimateAttribute();
        element.setFill("freeze");
        configure.accept(element);
        return element;
    }

    private static Timeline build(Consumer<SvgAnimateAttribute> configure, Rectangle target) {
        return (Timeline) SvgValueAnimationBuilder.build(animate(configure), target, null)
            .orElseThrow();
    }

    /** For {@code fill="remove"} tests, whose result is a {@link SequentialTransition}, not a bare {@link Timeline}. */
    private static SequentialTransition buildSequential(Consumer<SvgAnimateAttribute> configure, Rectangle target) {
        return (SequentialTransition) SvgValueAnimationBuilder.build(animate(configure), target, null)
            .orElseThrow();
    }

    private static double doubleValue(KeyFrame frame) {
        return ((Number) frame.getValues()
            .iterator()
            .next()
            .getEndValue()).doubleValue();
    }

    private static Color colorValue(KeyFrame frame) {
        return (Color) frame.getValues()
            .iterator()
            .next()
            .getEndValue();
    }

    private static Interpolator interpolator(KeyFrame frame) {
        return frame.getValues()
            .iterator()
            .next()
            .getInterpolator();
    }

}
