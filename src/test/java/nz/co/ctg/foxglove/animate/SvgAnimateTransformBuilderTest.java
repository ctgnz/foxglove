package nz.co.ctg.foxglove.animate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * Exercises #34's single-animation correctness for {@code <animateTransform>} - built directly against a real {@link
 * Rectangle}, inspecting both the returned {@link Timeline} and the {@link Transform} appended to the node's own
 * transform list (since, unlike {@code <animate>}, the animated target here is a brand new {@link Transform} this
 * builder creates and appends, not an existing property {@link SvgAttributeRegistry} already knows about).
 */
public class SvgAnimateTransformBuilderTest {

    // --- per-type value resolution + property mapping -------------------------

    @Test
    public void testTranslateWithOneNumberDefaultsTyToZero() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("translate");
            a.setFrom("0");
            a.setTo("10");
        }, target);

        Translate translate = (Translate) onlyTransform(target);
        assertThat(timeline.getKeyFrames().size(), is(2));
        assertThat(translate.getY(), closeTo(0.0, 1e-9));
    }

    @Test
    public void testScaleWithOneNumberDefaultsSyToSx() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("scale");
            a.setValues("1;2");
        }, target);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(numberAt(frames.get(1), 0), closeTo(2.0, 1e-9));
        assertThat(numberAt(frames.get(1), 1), closeTo(2.0, 1e-9));
    }

    @Test
    public void testRotateWithOneNumberDefaultsPivotToOrigin() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("rotate");
            a.setFrom("0");
            a.setTo("90");
        }, target);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(numberAt(frames.get(1), 0), closeTo(90.0, 1e-9));
        assertThat(numberAt(frames.get(1), 1), closeTo(0.0, 1e-9));
        assertThat(numberAt(frames.get(1), 2), closeTo(0.0, 1e-9));
    }

    @Test
    public void testRotateWithCentrePointForm() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("rotate");
            a.setFrom("0 50 50");
            a.setTo("360 50 50");
        }, target);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(numberAt(frames.get(1), 0), closeTo(360.0, 1e-9));
        assertThat(numberAt(frames.get(1), 1), closeTo(50.0, 1e-9));
        assertThat(numberAt(frames.get(1), 2), closeTo(50.0, 1e-9));
    }

    @Test
    public void testSkewXConvertsAngleToAShearFactor() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("skewX");
            a.setFrom("0");
            a.setTo("45");
        }, target);

        // tan(45deg) == 1.0, not the raw angle 45 - the Shear property is a factor, not degrees
        assertThat(numberAt(timeline.getKeyFrames().get(1), 0), closeTo(1.0, 1e-6));
        assertThat(onlyTransform(target), is(org.hamcrest.CoreMatchers.instanceOf(Shear.class)));
    }

    @Test
    public void testSkewYConvertsAngleToAShearFactor() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("skewY");
            a.setFrom("0");
            a.setTo("45");
        }, target);

        assertThat(numberAt(timeline.getKeyFrames().get(1), 0), closeTo(1.0, 1e-6));
    }

    @Test
    public void testTypeAbsentDefaultsToTranslate() {
        Rectangle target = new Rectangle();
        build(a -> {
            a.setFrom("0");
            a.setTo("10");
        }, target);
        assertThat(onlyTransform(target), is(org.hamcrest.CoreMatchers.instanceOf(Translate.class)));
    }

    // --- to-alone/by-alone use the type's identity, not a live property read ------

    @Test
    public void testToAloneOnScaleStartsFromIdentityOne() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("scale");
            a.setTo("3");
        }, target);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(numberAt(frames.get(0), 0), closeTo(1.0, 1e-9));
        assertThat(numberAt(frames.get(1), 0), closeTo(3.0, 1e-9));
    }

    @Test
    public void testByAloneOnRotateStartsFromIdentityZero() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("rotate");
            a.setBy("90");
        }, target);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(numberAt(frames.get(0), 0), closeTo(0.0, 1e-9));
        assertThat(numberAt(frames.get(1), 0), closeTo(90.0, 1e-9));
    }

    @Test
    public void testFromAndByCompose() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("rotate");
            a.setFrom("10");
            a.setBy("80");
        }, target);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(numberAt(frames.get(0), 0), closeTo(10.0, 1e-9));
        assertThat(numberAt(frames.get(1), 0), closeTo(90.0, 1e-9));
    }

    // --- calcMode --------------------------------------------------------------

    @Test
    public void testCalcModeDiscreteUsesTheDiscreteInterpolator() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("rotate");
            a.setFrom("0");
            a.setTo("90");
            a.setCalcMode("discrete");
        }, target);

        KeyFrame frame = timeline.getKeyFrames().get(1);
        assertThat(new ArrayList<>(frame.getValues()).get(0).getInterpolator(), is(Interpolator.DISCRETE));
    }

    @Test
    public void testCalcModePacedComputesKeyTimesProportionalToValueDistance() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("rotate");
            a.setDuration("100s");
            a.setValues("0;1;100");
            a.setCalcMode("paced");
        }, target);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(frames.get(1).getTime().toSeconds(), closeTo(1.0, 1e-9));
        assertThat(frames.get(2).getTime().toSeconds(), closeTo(100.0, 1e-9));
    }

    // --- additive/accumulate -----------------------------------------------

    @Test
    public void testAdditiveSumAndReplaceBothJustAppendToTheTransformsList() {
        Rectangle target = new Rectangle();
        target.getTransforms().add(new Scale(2, 2));

        build(a -> {
            a.setType("rotate");
            a.setFrom("0");
            a.setTo("90");
            a.setAdditive("sum");
        }, target);

        // the static Scale is untouched, and the new Rotate is simply appended alongside it
        assertThat(target.getTransforms().size(), is(2));
        assertThat(target.getTransforms().get(0), is(org.hamcrest.CoreMatchers.instanceOf(Scale.class)));
        assertThat(target.getTransforms().get(1), is(org.hamcrest.CoreMatchers.instanceOf(Rotate.class)));
    }

    @Test
    public void testAccumulateSumWithFiniteRepeatCountUnrollsIntoAShiftedMultiCycleTimeline() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("rotate");
            a.setDuration("1s");
            a.setValues("0;90");
            a.setAccumulate("sum");
            a.setRepeatCount("4");
        }, target);

        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(frames.size(), is(5));
        assertThat(timeline.getCycleCount(), is(1));
        assertThat(numberAt(frames.get(4), 0), closeTo(360.0, 1e-9));
        assertThat(frames.get(4).getTime(), is(javafx.util.Duration.seconds(4)));
    }

    @Test
    public void testAccumulateSumWithIndefiniteRepeatCountFallsBackToAPlainSingleCycleResult() {
        Rectangle target = new Rectangle();
        Timeline timeline = build(a -> {
            a.setType("rotate");
            a.setDuration("1s");
            a.setValues("0;90");
            a.setAccumulate("sum");
            a.setRepeatCount("indefinite");
        }, target);

        assertThat(timeline.getKeyFrames().size(), is(2));
    }

    // --- unsupported/failure cases -------------------------------------------

    @Test
    public void testUnrecognisedTypeIsUnsupported() {
        Optional<Animation> result = SvgAnimateTransformBuilder.build(animate(a -> {
            a.setType("bogus");
            a.setFrom("0");
            a.setTo("10");
        }), new Rectangle());
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void testWrongArityForTypeIsUnsupported() {
        // rotate takes 1 or 3 numbers, not 2
        Optional<Animation> result = SvgAnimateTransformBuilder.build(animate(a -> {
            a.setType("rotate");
            a.setFrom("0 0");
            a.setTo("90 90");
        }), new Rectangle());
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void testAValueThatFailsToParseIsUnsupported() {
        Optional<Animation> result = SvgAnimateTransformBuilder.build(animate(a -> {
            a.setType("rotate");
            a.setFrom("not-a-number");
            a.setTo("90");
        }), new Rectangle());
        assertThat(result.isEmpty(), is(true));
    }

    // --- helpers -----------------------------------------------------------

    private static SvgAnimateTransform animate(Consumer<SvgAnimateTransform> configure) {
        SvgAnimateTransform element = new SvgAnimateTransform();
        configure.accept(element);
        return element;
    }

    private static Timeline build(Consumer<SvgAnimateTransform> configure, Rectangle target) {
        return (Timeline) SvgAnimateTransformBuilder.build(animate(configure), target).orElseThrow();
    }

    private static Transform onlyTransform(Rectangle target) {
        assertThat(target.getTransforms().size(), is(1));
        return target.getTransforms().get(0);
    }

    private static double numberAt(KeyFrame frame, int index) {
        return ((Number) new ArrayList<>(frame.getValues()).get(index).getEndValue()).doubleValue();
    }

}
