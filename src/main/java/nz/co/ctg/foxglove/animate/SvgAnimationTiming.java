package nz.co.ctg.foxglove.animate;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javafx.animation.Animation;
import javafx.util.Duration;

/**
 * A parsed, typed view of one animation element's timing - the SMIL clock-value grammar and the handful of
 * timing attributes {@link ISvgAnimationElement} declares as raw strings.
 * <p>
 * Scoped to a first increment, per the issue's own suggestion: {@code begin}/{@code end} support a plain clock-value
 * offset only. Syncbase (<code>other.end+2s</code>), event, repeat and accessKey begin-values, and
 * semicolon-separated lists, all fail to parse as a plain offset and come back {@link Optional#empty()} - the same
 * documented "unsupported, degrades rather than throws" treatment used throughout this renderer, not a crash and not
 * silently wrong. Full SMIL begin-value support is its own, much larger, piece of work.
 * <p>
 * {@code min}/{@code max}/{@code restart} are intentionally not represented here at all yet - parsed by
 * {@link ISvgAnimationElement} already, but not enforced by anything in this first increment (documented gap).
 */
public record SvgAnimationTiming(Optional<Duration> begin, Duration duration, Optional<Duration> end, int repeatCount,
    Optional<Duration> repeatDuration, FillBehavior fill) {

    public enum FillBehavior {
        FREEZE,
        REMOVE
    }

    private static final String[] METRIC_SUFFIXES = {"ms", "min", "h", "s"};

    public static SvgAnimationTiming parse(ISvgAnimationElement element) {
        Optional<Duration> begin = parseClockValue(element.getBegin());
        Duration duration = parseDur(element.getDuration()).orElse(Duration.ZERO);
        Optional<Duration> end = parseClockValue(element.getEnd());
        int repeatCount = parseRepeatCount(element.getRepeatCount());
        Optional<Duration> repeatDuration = parseDur(element.getRepeatDuration());
        FillBehavior fill = "freeze".equalsIgnoreCase(StringUtils.trimToEmpty(element.getFill())) ? FillBehavior.FREEZE : FillBehavior.REMOVE;
        return new SvgAnimationTiming(begin, duration, end, repeatCount, repeatDuration, fill);
    }

    /**
     * {@code dur} (and {@code repeatDur}, which shares the same grammar plus {@code indefinite}): a plain clock
     * value, or the {@code indefinite} keyword mapped to {@link Duration#INDEFINITE} - {@code media} (an intrinsic
     * media duration) is not resolvable by anything in this renderer and comes back empty, the same as any other
     * unsupported value.
     */
    private static Optional<Duration> parseDur(String raw) {
        String value = StringUtils.trimToNull(raw);
        if (value == null) {
            return Optional.empty();
        }
        if ("indefinite".equals(value)) {
            return Optional.of(Duration.INDEFINITE);
        }
        if ("media".equals(value)) {
            return Optional.empty();
        }
        return parseClockValue(value);
    }

    /**
     * A bare SMIL clock value only - {@code Full-clock-value} ({@code "02:30:03"}, hh:mm:ss), {@code
     * Partial-clock-value} ({@code "12:30"}, mm:ss), or {@code Timecount-value} (a number with an optional
     * {@code h}/{@code min}/{@code s}/{@code ms} suffix, defaulting to seconds - {@code "5s"}, {@code "500ms"},
     * {@code "2"}). Also used for {@code begin}/{@code end}, whose only supported form is one of these.
     */
    static Optional<Duration> parseClockValue(String raw) {
        String value = StringUtils.trimToNull(raw);
        if (value == null) {
            return Optional.empty();
        }
        if (value.contains(":")) {
            return parseColonForm(value);
        }
        return parseTimecount(value);
    }

    private static Optional<Duration> parseColonForm(String value) {
        String[] parts = value.split(":");
        try {
            if (parts.length == 3) {
                double hours = Double.parseDouble(parts[0]);
                double minutes = Double.parseDouble(parts[1]);
                double seconds = Double.parseDouble(parts[2]);
                return Optional.of(Duration.seconds(hours * 3600 + minutes * 60 + seconds));
            }
            if (parts.length == 2) {
                double minutes = Double.parseDouble(parts[0]);
                double seconds = Double.parseDouble(parts[1]);
                return Optional.of(Duration.seconds(minutes * 60 + seconds));
            }
        } catch (NumberFormatException e) {
            // falls through to empty below
        }
        return Optional.empty();
    }

    private static Optional<Duration> parseTimecount(String value) {
        // longest/most specific suffix first - "500ms" ends with both "s" and "ms", and only the latter is right
        for (String metric : METRIC_SUFFIXES) {
            if (value.endsWith(metric)) {
                return parseTimecountNumber(value.substring(0, value.length() - metric.length()), metric);
            }
        }
        return parseTimecountNumber(value, "s");
    }

    private static Optional<Duration> parseTimecountNumber(String numberPart, String metric) {
        if (!NumberUtils.isParsable(numberPart)) {
            return Optional.empty();
        }
        double number = NumberUtils.toDouble(numberPart);
        double seconds = switch (metric) {
            case "ms" -> number / 1000.0;
            case "min" -> number * 60.0;
            case "h" -> number * 3600.0;
            default -> number;
        };
        return Optional.of(Duration.seconds(seconds));
    }

    /**
     * A bare number (SMIL allows a fraction; rounded to the nearest whole cycle - {@link Animation#setCycleCount}
     * has no fractional notion of a cycle) or {@code indefinite} mapped to {@link Animation#INDEFINITE}. Absent or
     * unparseable defaults to {@code 1}, the SMIL initial value.
     */
    private static int parseRepeatCount(String raw) {
        String value = StringUtils.trimToNull(raw);
        if (value == null) {
            return 1;
        }
        if ("indefinite".equals(value)) {
            return Animation.INDEFINITE;
        }
        if (NumberUtils.isParsable(value)) {
            double count = NumberUtils.toDouble(value);
            return count > 0 ? (int) Math.round(count) : 1;
        }
        return 1;
    }

}
