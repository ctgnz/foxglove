package nz.co.ctg.foxglove.conformance;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javafx.util.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nz.co.ctg.foxglove.AnimatedGraphic;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * Covers how {@link W3cSvgAnimationCheck} chooses the moments to compare - the part of #112 most likely to be silently wrong, since a bad sampling window produces
 * plausible-looking numbers rather than an error.
 */
public class AnimationSamplingTest {

    @TempDir
    Path work;

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @Test
    public void testAFiniteAnimationIsSampledAcrossItsOwnDuration() throws Exception {
        List<Duration> times = sampleTimesFor(animation("4s", null));

        assertThat(times, hasSize(5));
        assertThat(times.get(0)
            .toSeconds(), closeTo(0, 1e-9));
        assertThat(times.get(2)
            .toSeconds(), closeTo(2, 1e-9));
        assertThat("the end is sampled deliberately - fill=\"freeze\" lives there",
            times.get(4)
                .toSeconds(),
            closeTo(4, 1e-9));
    }

    /**
     * An animation that repeats forever has no meaningful "100% of duration", so it is sampled across a fixed window instead. Without this the check would ask for a seek to an
     * infinite time.
     */
    @Test
    public void testAnIndefiniteAnimationFallsBackToAFixedWindow() throws Exception {
        List<Duration> times = sampleTimesFor(animation("1s", "indefinite"));

        assertThat(times, hasSize(5));
        assertThat(times.get(4)
            .isIndefinite(), is(false));
        assertThat(times.get(4)
            .toSeconds(), closeTo(4, 1e-9));
    }

    /**
     * A document this renderer builds no animations for is sampled across <b>real time anyway</b>, not five times over at {@code t=0}.
     * <p>
     * This is the subtle one. Deriving the window from our own duration looks reasonable until you notice that a zero duration means "we animate nothing", and comparing a
     * stationary rendering at {@code t=0} against a reference also still at its start makes such a document score <i>well</i> - for exactly the reason it should score badly. The
     * first version of this check did that, and a document with no animations built came back looking as good as any other.
     */
    @Test
    public void testADocumentWeAnimateNothingForIsStillSampledAcrossTime() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(new SvgRectangle(0, 0, 10, 10));
        AnimatedGraphic animated = onFxThread(() -> svg.createAnimatedGraphic(RenderContext.root(svg.getElementIndex(), 100, 100)));

        List<Duration> times = W3cSvgAnimationCheck.sampleTimes(animated);

        assertThat(animated.animations()
            .size(), is(0));
        assertThat(times, hasSize(5));
        assertThat("sampling only t=0 would hide the very failure this check exists to find",
            times.get(4)
                .toSeconds(),
            closeTo(4, 1e-9));
    }

    /**
     * #208: {@code animate-elem-24-t}'s own real shape - one animation group, {@code begin="3s" dur="6s"} - confirmed against the real W3C test (its own {@code passCriteria}
     * literally names 3s/6s/9s as the moments its pale-blue guides exist at, not assumed from the timing attributes alone).
     */
    @Test
    public void testDerivedSampleTimesUseAnAnimationsOwnBeginMidAndEnd() throws Exception {
        SvgRectangle rect = new SvgRectangle(0, 0, 10, 10);
        rect.getContent()
            .add(valueAnimation("3s", "6s"));
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(rect);
        AnimatedGraphic animated = onFxThread(() -> svg.createAnimatedGraphic(RenderContext.root(svg.getElementIndex(), 100, 100)));

        List<Duration> times = W3cSvgAnimationCheck.deriveSampleTimes(svg, animated);

        assertThat(secondsOf(times), is(List.of(0.0, 3.0, 6.0, 9.0)));
    }

    /**
     * #208: two independently-timed animations ({@code [1s,5s]} and {@code [4s,7s]}, the real shape {@code animate-elem-26-t} takes) sample at every one of their own boundaries
     * unioned together, not 5 arbitrary fractions of whatever the longest one adds up to.
     */
    @Test
    public void testDerivedSampleTimesUnionSeveralIndependentlyTimedAnimations() throws Exception {
        SvgRectangle rect = new SvgRectangle(0, 0, 10, 10);
        rect.getContent()
            .add(valueAnimation("1s", "4s"));
        rect.getContent()
            .add(valueAnimation("4s", "3s"));
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(rect);
        AnimatedGraphic animated = onFxThread(() -> svg.createAnimatedGraphic(RenderContext.root(svg.getElementIndex(), 100, 100)));

        List<Duration> times = W3cSvgAnimationCheck.deriveSampleTimes(svg, animated);

        assertThat(secondsOf(times), is(List.of(0.0, 1.0, 3.0, 4.0, 5.0, 5.5, 7.0)));
    }

    /**
     * Nothing derivable (no animation elements at all) falls back to the standard fixed-fraction schedule, exactly as
     * {@link #testADocumentWeAnimateNothingForIsStillSampledAcrossTime} already covers for {@link W3cSvgAnimationCheck#sampleTimes} directly.
     */
    @Test
    public void testDerivedSampleTimesFallsBackWhenNothingIsDerivable() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(new SvgRectangle(0, 0, 10, 10));
        AnimatedGraphic animated = onFxThread(() -> svg.createAnimatedGraphic(RenderContext.root(svg.getElementIndex(), 100, 100)));

        List<Duration> times = W3cSvgAnimationCheck.deriveSampleTimes(svg, animated);

        assertThat(times, hasSize(5));
    }

    private static List<Double> secondsOf(List<Duration> times) {
        return times.stream()
            .map(Duration::toSeconds)
            .toList();
    }

    private static SvgAnimateAttribute valueAnimation(String begin, String duration) {
        SvgAnimateAttribute animate = new SvgAnimateAttribute();
        animate.setAttributeName("width");
        animate.setFrom("10");
        animate.setTo("50");
        animate.setBegin(begin);
        animate.setDuration(duration);
        animate.setFill("freeze");
        return animate;
    }

    @Test
    public void testTheManifestRoundTripsAtANonDefaultPath() throws Exception {
        Path manifest = work.resolve("animation-manifest.properties");
        Files.writeString(manifest, "");
        Map<String, Boolean> results = Map.of("animate-elem-01-t", true, "animate-elem-02-t", false);

        ConformanceManifest.record(manifest, results, "# test\n");
        Map<String, Boolean> loaded = ConformanceManifest.load(manifest);

        assertThat(loaded.get("animate-elem-01-t"), is(true));
        assertThat(loaded.get("animate-elem-02-t"), is(false));
        assertThat(ConformanceManifest.diff(loaded, results)
            .hasRegressions(), is(false));
    }

    private static List<Duration> sampleTimesFor(SvgGraphic svg) throws Exception {
        AnimatedGraphic animated = onFxThread(() -> svg.createAnimatedGraphic(RenderContext.root(svg.getElementIndex(), 100, 100)));
        return W3cSvgAnimationCheck.sampleTimes(animated);
    }

    /**
     * A rectangle whose width animates, so the controller has something real to report a duration for. {@code fill="freeze"} keeps the built duration an exact, round number of
     * seconds - this test is about the sampling window, not fill semantics, and the SVG default {@code fill="remove"} would otherwise add its own 1ms revert (#152) on top,
     * shifting every assertion below by a fraction of a millisecond for a reason unrelated to what this test actually checks.
     */
    private static SvgGraphic animation(String duration, String repeatCount) {
        SvgRectangle rect = new SvgRectangle(0, 0, 10, 10);
        rect.setId("target");
        SvgAnimateAttribute animate = new SvgAnimateAttribute();
        animate.setAttributeName("width");
        animate.setFrom("10");
        animate.setTo("50");
        animate.setDuration(duration);
        animate.setFill("freeze");
        if (repeatCount != null) {
            animate.setRepeatCount(repeatCount);
        }
        rect.getContent()
            .add(animate);

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(rect);
        return svg;
    }

}
