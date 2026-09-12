package nz.co.ctg.foxglove.animate;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Exercises #32's single-animation correctness for {@code <animate>}/{@code <animateColor>} - built directly against
 * real {@link Rectangle} nodes and inspecting the returned {@link Timeline}, independent of {@link
 * SvgAnimationController} (which #30 already covers). {@code context} is never read by the builder itself, so
 * {@code null} is passed throughout, matching the "these two element types are otherwise identical" premise this
 * issue is built on.
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
        assertThat(frames.get(0).getTime().toSeconds(), closeTo(0.0, 1e-9));
        assertThat(frames.get(1).getTime().toSeconds(), closeTo(2.5, 1e-9));
        assertThat(frames.get(2).getTime().toSeconds(), closeTo(10.0, 1e-9));
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

        assertThat(interpolator(timeline.getKeyFrames().get(1)), is(Interpolator.DISCRETE));
    }

    @Test
    public void testCalcModeLinearOrUnrecognisedUsesTheLinearInterpolator() {
        Timeline timeline = build(a -> {
            a.setAttributeName("x");
            a.setFrom("0");
            a.setTo("10");
            a.setCalcMode("bogus");
        }, new Rectangle(0, 0, 10, 10));

        assertThat(interpolator(timeline.getKeyFrames().get(1)), is(Interpolator.LINEAR));
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

        assertThat(interpolator(timeline.getKeyFrames().get(2)), is(Interpolator.LINEAR));
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
        assertThat(frames.get(0).getTime().toSeconds(), closeTo(0.0, 1e-9));
        assertThat(frames.get(1).getTime().toSeconds(), closeTo(1.0, 1e-9));
        assertThat(frames.get(2).getTime().toSeconds(), closeTo(100.0, 1e-9));
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
        assertThat(frames.get(0).getTime().toSeconds(), closeTo(0.0, 1e-9));
        assertThat(doubleValue(frames.get(0)), closeTo(0.0, 1e-9));
        assertThat(frames.get(1).getTime().toSeconds(), closeTo(1.0, 1e-9));
        assertThat(doubleValue(frames.get(1)), closeTo(10.0, 1e-9));
        assertThat(frames.get(2).getTime().toSeconds(), closeTo(2.0, 1e-9));
        assertThat(doubleValue(frames.get(2)), closeTo(20.0, 1e-9));
        assertThat(frames.get(3).getTime().toSeconds(), closeTo(3.0, 1e-9));
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
        assertThat(timeline.getKeyFrames().size(), is(2));

        // "by" alone can't produce any colour value list at all - the whole animation is unsupported
        assertThat(SvgValueAnimationBuilder.build(animate(a -> {
            a.setAttributeName("fill");
            a.setBy("10");
        }), rect, null).isEmpty(), is(true));
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

        Timeline first = (Timeline) SvgValueAnimationBuilder.build(animate, rectForAnimate, null).orElseThrow();
        Timeline second = (Timeline) SvgValueAnimationBuilder.build(animateColor, rectForAnimateColor, null).orElseThrow();

        assertThat(first.getKeyFrames().size(), is(second.getKeyFrames().size()));
        assertThat(colorValue(first.getKeyFrames().get(0)), is(colorValue(second.getKeyFrames().get(0))));
        assertThat(colorValue(first.getKeyFrames().get(1)), is(colorValue(second.getKeyFrames().get(1))));
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

    private static SvgAnimateAttribute animate(Consumer<SvgAnimateAttribute> configure) {
        SvgAnimateAttribute element = new SvgAnimateAttribute();
        configure.accept(element);
        return element;
    }

    private static Timeline build(Consumer<SvgAnimateAttribute> configure, Rectangle target) {
        return (Timeline) SvgValueAnimationBuilder.build(animate(configure), target, null).orElseThrow();
    }

    private static double doubleValue(KeyFrame frame) {
        return ((Number) frame.getValues().iterator().next().getEndValue()).doubleValue();
    }

    private static Color colorValue(KeyFrame frame) {
        return (Color) frame.getValues().iterator().next().getEndValue();
    }

    private static Interpolator interpolator(KeyFrame frame) {
        return frame.getValues().iterator().next().getInterpolator();
    }

}
