package nz.co.ctg.foxglove.animate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import org.junit.jupiter.api.Test;

/**
 * Exercises #34's single-animation correctness for {@code <animateTransform>} - built directly against a real {@link Rectangle}, inspecting both the returned {@link Timeline} and
 * the {@link Transform} appended to the node's own transform list (since, unlike {@code <animate>}, the animated target here is a brand new {@link Transform} this builder creates
 * and appends, not an existing property {@link SvgAttributeRegistry} already knows about).
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
        assertThat(timeline.getKeyFrames()
            .size(), is(2));
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
        assertThat(numberAt(timeline.getKeyFrames()
            .get(1), 0), closeTo(1.0, 1e-6));
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

        assertThat(numberAt(timeline.getKeyFrames()
            .get(1), 0), closeTo(1.0, 1e-6));
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

        KeyFrame frame = timeline.getKeyFrames()
            .get(1);
        assertThat(new ArrayList<>(frame.getValues()).get(0)
            .getInterpolator(), is(Interpolator.DISCRETE));
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
        assertThat(frames.get(1)
            .getTime()
            .toSeconds(), closeTo(1.0, 1e-9));
        assertThat(frames.get(2)
            .getTime()
            .toSeconds(), closeTo(100.0, 1e-9));
    }

    // --- additive/accumulate -----------------------------------------------

    @Test
    public void testAdditiveSumAndReplaceBothJustAppendToTheTransformsList() {
        Rectangle target = new Rectangle();
        target.getTransforms()
            .add(new Scale(2, 2));

        build(a -> {
            a.setType("rotate");
            a.setFrom("0");
            a.setTo("90");
            a.setAdditive("sum");
        }, target);

        // the static Scale is untouched, and the new Rotate is simply appended alongside it
        assertThat(target.getTransforms()
            .size(), is(2));
        assertThat(target.getTransforms()
            .get(0), is(org.hamcrest.CoreMatchers.instanceOf(Scale.class)));
        assertThat(target.getTransforms()
            .get(1), is(org.hamcrest.CoreMatchers.instanceOf(Rotate.class)));
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
        assertThat(frames.get(4)
            .getTime(), is(javafx.util.Duration.seconds(4)));
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

        assertThat(timeline.getKeyFrames()
            .size(), is(2));
    }

    // --- fill="remove" (#149) -------------------------------------------------

    @Test
    public void testFillRemoveRevertsToIdentityAfterDur() {
        Rectangle target = new Rectangle();
        SequentialTransition result = buildSequential(a -> {
            a.setType("rotate");
            a.setDuration("1s");
            a.setFrom("0");
            a.setTo("90");
            a.setFill("remove");
        }, target);

        List<Animation> children = result.getChildren();
        assertThat(children, hasSize(2));
        Timeline core = (Timeline) children.get(0);
        Timeline revert = (Timeline) children.get(1);
        assertThat("core is the untouched, unreverted animation", numberAt(core.getKeyFrames()
            .get(1), 0), closeTo(90.0, 1e-9));
        assertThat("reverts to the rotate identity (angle 0)", numberAt(revert.getKeyFrames()
            .get(0), 0), closeTo(0.0, 1e-9));
        assertThat(revert.getKeyFrames()
            .get(0)
            .getValues()
            .iterator()
            .next()
            .getInterpolator(), is(Interpolator.DISCRETE));
    }

    /**
     * The identity {@code <animateTransform>} reverts to is not always all-zero - {@code scale}'s identity is {@code (1,1)}, and this must come out the same way
     * {@code kind.identity()} defines it everywhere else, not a uniform zero-fill.
     */
    @Test
    public void testFillRemoveOnScaleRevertsToScaleOneNotZero() {
        SequentialTransition result = buildSequential(a -> {
            a.setType("scale");
            a.setDuration("1s");
            a.setFrom("1");
            a.setTo("3");
            a.setFill("remove");
        }, new Rectangle());

        Timeline revert = (Timeline) result.getChildren()
            .get(1);
        List<KeyValue> values = new ArrayList<>(revert.getKeyFrames()
            .get(0)
            .getValues());
        assertThat(((Number) values.get(0)
            .getEndValue()).doubleValue(), closeTo(1.0, 1e-9));
        assertThat(((Number) values.get(1)
            .getEndValue()).doubleValue(), closeTo(1.0, 1e-9));
    }

    /**
     * {@code skewX}/{@code skewY} store a shear <b>factor</b> ({@code tan} of the angle), not the angle itself. The revert must target the same space - {@code tan(0)=0} - not the
     * raw angle domain; this would only coincide with a plain angle-domain revert at exactly 0, so this is the one case that actually tells the two apart.
     */
    @Test
    public void testFillRemoveOnSkewXRevertsTheShearFactorNotTheAngle() {
        SequentialTransition result = buildSequential(a -> {
            a.setType("skewX");
            a.setDuration("1s");
            a.setFrom("0");
            a.setTo("45");
            a.setFill("remove");
        }, new Rectangle());

        Timeline core = (Timeline) result.getChildren()
            .get(0);
        Timeline revert = (Timeline) result.getChildren()
            .get(1);
        assertThat("core stores the shear factor, tan(45deg)=1, not the raw angle 45",
            numberAt(core.getKeyFrames()
                .get(1), 0),
            closeTo(1.0, 1e-9));
        assertThat("the revert is tan(0)=0", numberAt(revert.getKeyFrames()
            .get(0), 0), closeTo(0.0, 1e-9));
    }

    /**
     * <b>The inconvenient case.</b> {@code fill="remove"} with {@code repeatCount="3"} must revert only once, after every cycle independently replays the same value list via
     * JavaFX's own {@code cycleCount} - not unrolled the way {@code accumulate="sum"} is, which would (as a first implementation of #149 discovered via a failing mutation test)
     * silently skip every cycle's own first value and collapse the repeats into a flat hold.
     */
    @Test
    public void testFillRemoveWithFiniteRepeatCountRevertsOnlyOnceAfterEveryCycleReplays() {
        SequentialTransition result = buildSequential(a -> {
            a.setType("rotate");
            a.setDuration("1s");
            a.setValues("0;90");
            a.setRepeatCount("3");
            a.setFill("remove");
        }, new Rectangle());

        Timeline core = (Timeline) result.getChildren()
            .get(0);
        Timeline revert = (Timeline) result.getChildren()
            .get(1);
        assertThat("each cycle independently replays the same 2-frame value list", core.getKeyFrames()
            .size(), is(2));
        assertThat(numberAt(core.getKeyFrames()
            .get(1), 0), closeTo(90.0, 1e-9));
        assertThat("JavaFX's own cycling replays core, not a manual unroll", core.getCycleCount(), is(3));
        assertThat(numberAt(revert.getKeyFrames()
            .get(0), 0), closeTo(0.0, 1e-9));
    }

    /** {@code accumulate="sum"} and {@code fill="remove"} are independent triggers and must compose. */
    @Test
    public void testAccumulateSumWithFillRemoveStillRevertsAtTheEnd() {
        SequentialTransition result = buildSequential(a -> {
            a.setType("rotate");
            a.setDuration("1s");
            a.setValues("0;90");
            a.setAccumulate("sum");
            a.setRepeatCount("4");
            a.setFill("remove");
        }, new Rectangle());

        Timeline core = (Timeline) result.getChildren()
            .get(0);
        Timeline revert = (Timeline) result.getChildren()
            .get(1);
        assertThat("accumulate still needs its own unroll - cycleCount stays 1 on core", core.getCycleCount(), is(1));
        List<KeyFrame> coreFrames = core.getKeyFrames();
        // the accumulated end value (4 cycles of 90deg = 360deg) is core's own last frame, not reset early
        assertThat(numberAt(coreFrames.get(coreFrames.size() - 1), 0), closeTo(360.0, 1e-9));
        assertThat(numberAt(revert.getKeyFrames()
            .get(0), 0), closeTo(0.0, 1e-9));
    }

    /** {@code fill="remove"} with {@code repeatCount="indefinite"} never ends, so nothing is ever built to revert. */
    @Test
    public void testFillRemoveWithIndefiniteRepeatCountNeverAppendsARevertFrame() {
        Timeline timeline = build(a -> {
            a.setType("rotate");
            a.setDuration("1s");
            a.setFrom("0");
            a.setTo("90");
            a.setRepeatCount("indefinite");
            a.setFill("remove");
        }, new Rectangle());

        assertThat(timeline.getKeyFrames()
            .size(), is(2));
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

    /**
     * Defaults {@code fill="freeze"} - the SVG default is {@code "remove"} (#149), which would add a revert {@code KeyFrame} to every one of these fixtures and break their
     * frame-count assertions for a reason unrelated to whatever each test actually exercises. Tests specifically about {@code fill="remove"} override it via their own
     * {@code configure} lambda, which runs after this and so wins.
     */
    private static SvgAnimateTransform animate(Consumer<SvgAnimateTransform> configure) {
        SvgAnimateTransform element = new SvgAnimateTransform();
        element.setFill("freeze");
        configure.accept(element);
        return element;
    }

    private static Timeline build(Consumer<SvgAnimateTransform> configure, Rectangle target) {
        return (Timeline) SvgAnimateTransformBuilder.build(animate(configure), target)
            .orElseThrow();
    }

    /** For {@code fill="remove"} tests, whose result is a {@link SequentialTransition}, not a bare {@link Timeline}. */
    private static SequentialTransition buildSequential(Consumer<SvgAnimateTransform> configure, Rectangle target) {
        return (SequentialTransition) SvgAnimateTransformBuilder.build(animate(configure), target)
            .orElseThrow();
    }

    private static Transform onlyTransform(Rectangle target) {
        assertThat(target.getTransforms()
            .size(), is(1));
        return target.getTransforms()
            .get(0);
    }

    private static double numberAt(KeyFrame frame, int index) {
        return ((Number) new ArrayList<>(frame.getValues()).get(index)
            .getEndValue()).doubleValue();
    }

}
