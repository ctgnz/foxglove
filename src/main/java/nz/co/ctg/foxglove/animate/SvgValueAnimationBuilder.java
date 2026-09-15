package nz.co.ctg.foxglove.animate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import nz.co.ctg.foxglove.RenderContext;

/**
 * Builds the {@link Animation} for {@code <animate>}/{@code <animateColor>} - identical logic for both, since {@link ISvgValueAnimationElement} exposes the same attributes either
 * way and the only real difference (a numeric vs colour-typed value) is already handled per-property by {@link SvgAttributeRegistry}'s own parser. Colour values flow through the
 * exact same {@code KeyFrame}/{@code Interpolator} machinery as numbers - JavaFX's own {@link Interpolator#LINEAR} interpolates {@link Color} directly (verified empirically, not
 * merely assumed).
 * <p>
 * {@code by}/{@code additive}/{@code accumulate} only make sense arithmetically on a numeric binding (SVG doesn't define arithmetic on colours either) - on a colour-typed binding
 * they are silently ignored (this renderer's usual "unsupported, skip the feature rather than the whole animation" treatment), except {@code by} used alone with no
 * {@code from}/{@code to}/{@code values}, which cannot produce any value list at all for a colour and so the whole animation resolves to {@link Optional#empty()}.
 */
public final class SvgValueAnimationBuilder {

    public static Optional<Animation> build(ISvgValueAnimationElement element, Node target, RenderContext context) {
        Optional<SvgAttributeBinding<?>> resolved = SvgAttributeRegistry.resolve(target, element.getAttributeName());
        if (resolved.isEmpty()) {
            return Optional.empty();
        }
        SvgAttributeBinding<Object> binding = castBinding(resolved.get());
        Object currentValue = binding.property()
            .getValue();
        boolean numeric = currentValue instanceof Number;

        Optional<List<Object>> valuesOpt = resolveValues(element, binding, currentValue, numeric);
        if (valuesOpt.isEmpty() || valuesOpt.get()
            .size() < 2) {
            return Optional.empty();
        }
        List<Object> values = valuesOpt.get();

        List<Double> keyTimes = resolveKeyTimes(element, values);
        List<Interpolator> interpolators = resolveInterpolators(element, values.size() - 1);

        boolean additive = numeric && "sum".equalsIgnoreCase(StringUtils.trimToEmpty(element.getAdditive()));
        double baseValue = additive ? ((Number) currentValue).doubleValue() : 0.0;

        SvgAnimationTiming timing = SvgAnimationTiming.parse(element);
        Duration simpleDuration = timing.duration();
        boolean finiteRepeat = timing.repeatCount() != Animation.INDEFINITE && timing.repeatCount() > 0;

        boolean accumulate = numeric && "sum".equalsIgnoreCase(StringUtils.trimToEmpty(element.getAccumulate()));
        // fill="remove" never applies to something that never ends - repeatCount="indefinite" plays forever, so
        // there is no "after dur" for the property to revert at (#149).
        boolean removeOnFinish = timing.fill() == SvgAnimationTiming.FillBehavior.REMOVE && finiteRepeat;

        Timeline core;
        if (accumulate && finiteRepeat) {
            // accumulate="sum" shifts each cycle by the previous cycle's own delta - something JavaFX's own
            // cycleCount replay cannot express (it always restarts a Timeline from its own t=0 unchanged), so this
            // must be unrolled into one continuous, non-repeating Timeline spanning every cycle up front.
            core = new Timeline(buildRepeatedFrames(binding.property(), values, keyTimes, interpolators, baseValue,
                simpleDuration, timing.repeatCount()).toArray(new KeyFrame[0]));
        } else {
            // Without accumulate, every repeat plays the identical value list, restarting from its own first value
            // each time - exactly what JavaFX's native cycleCount already does for free, so nothing here needs
            // unrolling; repeatCount is applied directly to this single-cycle Timeline.
            core = new Timeline(buildKeyFrames(binding.property(), values, keyTimes, interpolators, baseValue, simpleDuration, 0)
                .toArray(new KeyFrame[0]));
            if (removeOnFinish) {
                core.setCycleCount(timing.repeatCount());
            }
        }
        if (!removeOnFinish) {
            return Optional.of(core);
        }
        // The revert lives in its own Timeline, played once, strictly after core finishes (SequentialTransition) -
        // rather than as one more KeyFrame appended inside core itself. That sidesteps two separate problems: inside
        // the accumulate branch, core's own final KeyFrame already sits at the exact same instant a same-Timeline
        // revert frame would want, an unverified same-instant tie-break between two KeyFrames on one property;
        // inside the non-accumulate branch, core now plays with cycleCount>1 itself, so a revert frame baked into
        // it would repeat at the end of *every* cycle rather than only the very last one.
        // Duration.millis(1), not Duration.ZERO (#152): a Timeline whose only KeyFrame sits at Duration.ZERO has zero
        // temporal footprint inside a SequentialTransition - it never appears in getTotalDuration() and seeking past
        // the end never applies it, even though real uninterrupted playback does apply it correctly on entry. The
        // 1ms offset gives it a real span, so both playback and seeking revert consistently.
        Timeline revert = new Timeline(
                                       new KeyFrame(Duration.millis(1), new KeyValue(binding.property(), currentValue, Interpolator.DISCRETE)));
        return Optional.of(new SequentialTransition(core, revert));
    }

    @SuppressWarnings("unchecked")
    private static SvgAttributeBinding<Object> castBinding(SvgAttributeBinding<?> binding) {
        return (SvgAttributeBinding<Object>) binding;
    }

    // --- value list resolution -------------------------------------------------

    private static Optional<List<Object>> resolveValues(ISvgValueAnimationElement element, SvgAttributeBinding<Object> binding,
                                                        Object currentValue, boolean numeric) {
        String valuesAttr = StringUtils.trimToNull(element.getValues());
        if (valuesAttr != null) {
            return parseAll(binding, splitSemicolon(valuesAttr));
        }
        String from = StringUtils.trimToNull(element.getFrom());
        String to = StringUtils.trimToNull(element.getTo());
        String by = StringUtils.trimToNull(element.getBy());
        if (from != null && to != null) {
            return parseAll(binding, List.of(from, to));
        }
        if (from != null && by != null && numeric) {
            Optional<Object> fromVal = binding.parser()
                .apply(from);
            Optional<Double> byVal = parseNumeric(binding, by);
            if (fromVal.isEmpty() || byVal.isEmpty()) {
                return Optional.empty();
            }
            double start = ((Number) fromVal.get()).doubleValue();
            return Optional.of(List.of(fromVal.get(), (Object) (start + byVal.get())));
        }
        if (to != null) {
            Optional<Object> toVal = binding.parser()
                .apply(to);
            if (toVal.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(List.of(currentValue, toVal.get()));
        }
        if (by != null && numeric) {
            Optional<Double> byVal = parseNumeric(binding, by);
            if (byVal.isEmpty()) {
                return Optional.empty();
            }
            double start = ((Number) currentValue).doubleValue();
            return Optional.of(List.of(currentValue, (Object) (start + byVal.get())));
        }
        return Optional.empty();
    }

    private static List<String> splitSemicolon(String raw) {
        List<String> result = new ArrayList<>();
        for (String part : raw.split(";")) {
            result.add(part.trim());
        }
        return result;
    }

    private static Optional<List<Object>> parseAll(SvgAttributeBinding<Object> binding, List<String> raw) {
        List<Object> result = new ArrayList<>();
        for (String value : raw) {
            Optional<Object> parsed = binding.parser()
                .apply(value);
            if (parsed.isEmpty()) {
                return Optional.empty();
            }
            result.add(parsed.get());
        }
        return Optional.of(result);
    }

    private static Optional<Double> parseNumeric(SvgAttributeBinding<Object> binding, String raw) {
        return binding.parser()
            .apply(raw)
            .map(v -> ((Number) v).doubleValue());
    }

    // --- keyTimes ----------------------------------------------------------

    private static List<Double> resolveKeyTimes(ISvgValueAnimationElement element, List<Object> values) {
        boolean paced = "paced".equalsIgnoreCase(StringUtils.trimToEmpty(element.getCalcMode()));
        if (paced) {
            return pacedKeyTimes(values);
        }
        String keyTimesAttr = StringUtils.trimToNull(element.getKeyTimes());
        if (keyTimesAttr != null) {
            Optional<List<Double>> explicit = parseKeyTimes(keyTimesAttr, values.size());
            if (explicit.isPresent()) {
                return explicit.get();
            }
        }
        return evenlySpaced(values.size());
    }

    private static Optional<List<Double>> parseKeyTimes(String raw, int expectedSize) {
        String[] parts = raw.split(";");
        if (parts.length != expectedSize) {
            return Optional.empty();
        }
        List<Double> result = new ArrayList<>();
        double previous = -1;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (!NumberUtils.isParsable(part)) {
                return Optional.empty();
            }
            double value = NumberUtils.toDouble(part);
            if (value < previous || (i == 0 && value != 0)) {
                return Optional.empty();
            }
            result.add(value);
            previous = value;
        }
        return Optional.of(result);
    }

    private static List<Double> evenlySpaced(int count) {
        if (count <= 1) {
            return List.of(0.0);
        }
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add((double) i / (count - 1));
        }
        return result;
    }

    private static List<Double> pacedKeyTimes(List<Object> values) {
        int size = values.size();
        double[] distances = new double[size - 1];
        double total = 0;
        for (int i = 0; i < size - 1; i++) {
            distances[i] = distance(values.get(i), values.get(i + 1));
            total += distances[i];
        }
        if (total <= 0) {
            return evenlySpaced(size);
        }
        List<Double> result = new ArrayList<>();
        result.add(0.0);
        double cumulative = 0;
        for (double d : distances) {
            cumulative += d;
            result.add(cumulative / total);
        }
        return result;
    }

    /**
     * Numeric distance is a plain difference; colour distance is Euclidean over RGB - SVG doesn't mandate a specific colour metric for {@code calcMode="paced"}, so this is a
     * documented, reasonable choice.
     */
    private static double distance(Object a, Object b) {
        if (a instanceof Number numberA && b instanceof Number numberB) {
            return Math.abs(numberA.doubleValue() - numberB.doubleValue());
        }
        if (a instanceof Color colorA && b instanceof Color colorB) {
            double dr = colorA.getRed() - colorB.getRed();
            double dg = colorA.getGreen() - colorB.getGreen();
            double db = colorA.getBlue() - colorB.getBlue();
            return Math.sqrt(dr * dr + dg * dg + db * db);
        }
        return 0;
    }

    // --- interpolators -----------------------------------------------------

    private static List<Interpolator> resolveInterpolators(ISvgValueAnimationElement element, int intervalCount) {
        String calcMode = StringUtils.trimToEmpty(element.getCalcMode());
        if ("discrete".equalsIgnoreCase(calcMode)) {
            return Collections.nCopies(intervalCount, Interpolator.DISCRETE);
        }
        if ("spline".equalsIgnoreCase(calcMode)) {
            return resolveSplineInterpolators(element, intervalCount);
        }
        return Collections.nCopies(intervalCount, Interpolator.LINEAR);
    }

    private static List<Interpolator> resolveSplineInterpolators(ISvgValueAnimationElement element, int intervalCount) {
        String keySplines = StringUtils.trimToNull(element.getKeySplines());
        String[] groups = keySplines == null ? new String[0] : keySplines.split(";");
        List<Interpolator> result = new ArrayList<>();
        for (int i = 0; i < intervalCount; i++) {
            Interpolator interpolator = Interpolator.LINEAR;
            if (i < groups.length) {
                interpolator = parseSpline(groups[i]).orElse(Interpolator.LINEAR);
            }
            result.add(interpolator);
        }
        return result;
    }

    private static Optional<Interpolator> parseSpline(String raw) {
        String[] parts = raw.trim()
            .split("[,\\s]+");
        if (parts.length != 4) {
            return Optional.empty();
        }
        try {
            double x1 = Double.parseDouble(parts[0]);
            double y1 = Double.parseDouble(parts[1]);
            double x2 = Double.parseDouble(parts[2]);
            double y2 = Double.parseDouble(parts[3]);
            return Optional.of(Interpolator.SPLINE(x1, y1, x2, y2));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    // --- KeyFrame construction -----------------------------------------------

    private static List<KeyFrame> buildKeyFrames(WritableValue<Object> property, List<Object> values, List<Double> keyTimes,
                                                 List<Interpolator> interpolators, double valueShift, Duration simpleDuration, int startIndex) {
        List<KeyFrame> frames = new ArrayList<>();
        for (int i = startIndex; i < values.size(); i++) {
            Object written = write(values.get(i), valueShift);
            Duration time = simpleDuration.multiply(keyTimes.get(i));
            if (i == 0) {
                frames.add(new KeyFrame(time, new KeyValue(property, written)));
            } else {
                frames.add(new KeyFrame(time, new KeyValue(property, written, interpolators.get(i - 1))));
            }
        }
        return frames;
    }

    private static Object write(Object value, double shift) {
        return shift == 0.0 ? value : ((Number) value).doubleValue() + shift;
    }

    /**
     * {@code accumulate="sum"} with a finite {@code repeatCount}: unrolls every repeat into one continuous sequence of {@code KeyFrame}s spanning the entire repeated duration,
     * each cycle shifted by the previous cycle's own delta - something JavaFX's own {@code cycleCount} replay cannot express, since it always restarts a {@code Timeline} from its
     * own start unchanged. The {@code Timeline} built from the result is played with {@code cycleCount} left at {@code 1} - {@link SvgAnimationController#withTiming} recognises
     * this case (by element type/attributes, not by inspecting {@code cycleCount}) and skips its own generic {@code repeatCount} wrapping accordingly. Consecutive cycles
     * deliberately share a single {@code KeyFrame} at the boundary (cycle {@code n}'s last value equals cycle {@code n+1}'s first value by construction) rather than emitting a
     * duplicate at the same time.
     * <p>
     * Deliberately not reused for plain {@code repeatCount} replay with no {@code accumulate} (#149): that case has no shift between cycles at all, each one independently
     * replaying the same value list from its own start, which is exactly what JavaFX's native cycling already does - unrolling it here would instead skip every cycle's own first
     * value (the boundary-sharing behaviour above, only valid when cycles truly are continuous), collapsing a repeated ramp into a single ramp followed by a flat hold.
     */
    private static List<KeyFrame> buildRepeatedFrames(WritableValue<Object> property, List<Object> values, List<Double> keyTimes,
                                                      List<Interpolator> interpolators, double baseValue, Duration simpleDuration, int cycles) {
        double firstValue = ((Number) values.get(0)).doubleValue();
        double lastValue = ((Number) values.get(values.size() - 1)).doubleValue();
        double perCycleDelta = lastValue - firstValue;

        List<KeyFrame> frames = new ArrayList<>();
        for (int cycle = 0; cycle < cycles; cycle++) {
            double valueShift = baseValue + cycle * perCycleDelta;
            Duration timeShift = simpleDuration.multiply(cycle);
            int startIndex = cycle == 0 ? 0 : 1;
            for (KeyFrame frame : buildKeyFrames(property, values, keyTimes, interpolators, valueShift, simpleDuration, startIndex)) {
                frames.add(new KeyFrame(timeShift.add(frame.getTime()), frame.getValues()
                    .toArray(new KeyValue[0])));
            }
        }
        return frames;
    }

    private SvgValueAnimationBuilder() {
    }

}
