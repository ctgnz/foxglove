package nz.co.ctg.foxglove.animate;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.animate.SvgAnimationTiming.FillBehavior;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.animation.Animation;
import javafx.util.Duration;

/**
 * Exercises #30's SMIL clock-value parsing and the handful of timing attributes it feeds into
 * {@link SvgAnimationTiming} - the bulk of the genuinely novel logic in the animation infrastructure, and the
 * easiest part to get subtly wrong (the colon-separated forms especially).
 */
public class SvgAnimationTimingTest {

    // --- dur: Timecount-value ------------------------------------------------

    @Test
    public void testDurBareNumberDefaultsToSeconds() {
        assertThat(timingWithDur("5").duration(), is(Duration.seconds(5)));
    }

    @Test
    public void testDurSecondsSuffix() {
        assertThat(timingWithDur("5s").duration(), is(Duration.seconds(5)));
    }

    @Test
    public void testDurMillisecondsSuffix() {
        // "ms" and "s" share a trailing "s" - this is the one most likely to be mis-parsed
        assertThat(timingWithDur("500ms").duration(), is(Duration.millis(500)));
    }

    @Test
    public void testDurMinutesSuffix() {
        assertThat(timingWithDur("2min").duration(), is(Duration.minutes(2)));
    }

    @Test
    public void testDurHoursSuffix() {
        assertThat(timingWithDur("1h").duration(), is(Duration.hours(1)));
    }

    @Test
    public void testDurFractionalSeconds() {
        assertThat(timingWithDur("1.5s").duration().toMillis(), closeTo(1500, 1e-6));
    }

    // --- dur: colon forms ------------------------------------------------------

    @Test
    public void testDurPartialClockValueMinutesSeconds() {
        assertThat(timingWithDur("02:30").duration(), is(Duration.seconds(150)));
    }

    @Test
    public void testDurFullClockValueHoursMinutesSeconds() {
        assertThat(timingWithDur("01:02:03").duration(), is(Duration.seconds(3600 + 120 + 3)));
    }

    @Test
    public void testDurFullClockValueWithFraction() {
        assertThat(timingWithDur("00:00:01.5").duration().toMillis(), closeTo(1500, 1e-6));
    }

    // --- dur: keywords -----------------------------------------------------

    @Test
    public void testDurIndefinite() {
        assertThat(timingWithDur("indefinite").duration(), is(Duration.INDEFINITE));
    }

    @Test
    public void testDurMediaIsUnsupportedAndDefaultsToZero() {
        assertThat(timingWithDur("media").duration(), is(Duration.ZERO));
    }

    @Test
    public void testDurAbsentDefaultsToZero() {
        assertThat(timingWithDur(null).duration(), is(Duration.ZERO));
    }

    // --- begin/end: plain offsets only --------------------------------------

    @Test
    public void testBeginPlainOffsetParses() {
        SvgAnimationTiming timing = timing(a -> a.setBegin("2s"));
        assertThat(timing.begin().orElseThrow(), is(Duration.seconds(2)));
    }

    @Test
    public void testBeginNegativeOffsetParses() {
        SvgAnimationTiming timing = timing(a -> a.setBegin("-2s"));
        assertThat(timing.begin().orElseThrow(), is(Duration.seconds(-2)));
    }

    @Test
    public void testEndPlainOffsetParses() {
        SvgAnimationTiming timing = timing(a -> a.setEnd("5s"));
        assertThat(timing.end().orElseThrow(), is(Duration.seconds(5)));
    }

    @Test
    public void testBeginSyncbaseValueIsUnsupportedAndParsesEmpty() {
        SvgAnimationTiming timing = timing(a -> a.setBegin("other.end+2s"));
        assertThat(timing.begin().isEmpty(), is(true));
    }

    @Test
    public void testBeginEventValueIsUnsupportedAndParsesEmpty() {
        SvgAnimationTiming timing = timing(a -> a.setBegin("click"));
        assertThat(timing.begin().isEmpty(), is(true));
    }

    @Test
    public void testBeginAbsentParsesEmpty() {
        assertThat(timing(a -> { }).begin().isEmpty(), is(true));
    }

    // --- repeatCount ---------------------------------------------------------

    @Test
    public void testRepeatCountNumber() {
        assertThat(timing(a -> a.setRepeatCount("3")).repeatCount(), is(3));
    }

    @Test
    public void testRepeatCountIndefinite() {
        assertThat(timing(a -> a.setRepeatCount("indefinite")).repeatCount(), is(Animation.INDEFINITE));
    }

    @Test
    public void testRepeatCountAbsentDefaultsToOne() {
        assertThat(timing(a -> { }).repeatCount(), is(1));
    }

    // --- repeatDur -------------------------------------------------------------

    @Test
    public void testRepeatDurationParses() {
        SvgAnimationTiming timing = timing(a -> a.setRepeatDuration("10s"));
        assertThat(timing.repeatDuration().orElseThrow(), is(Duration.seconds(10)));
    }

    @Test
    public void testRepeatDurationAbsentParsesEmpty() {
        assertThat(timing(a -> { }).repeatDuration().isEmpty(), is(true));
    }

    // --- fill ------------------------------------------------------------------

    @Test
    public void testFillFreeze() {
        assertThat(timing(a -> a.setFill("freeze")).fill(), is(FillBehavior.FREEZE));
    }

    @Test
    public void testFillRemove() {
        assertThat(timing(a -> a.setFill("remove")).fill(), is(FillBehavior.REMOVE));
    }

    @Test
    public void testFillAbsentDefaultsToRemove() {
        assertThat(timing(a -> { }).fill(), is(FillBehavior.REMOVE));
    }

    // --- helpers -----------------------------------------------------------

    private static SvgAnimationTiming timingWithDur(String dur) {
        return timing(a -> a.setDuration(dur));
    }

    private static SvgAnimationTiming timing(java.util.function.Consumer<SvgAnimateAttribute> configure) {
        SvgAnimateAttribute element = new SvgAnimateAttribute();
        configure.accept(element);
        return SvgAnimationTiming.parse(element);
    }

}
