package nz.co.ctg.foxglove.animate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

/**
 * Builds the {@link Animation} for {@code <animateTransform>} - a genuinely different shape of problem from {@link
 * SvgValueAnimationBuilder} (#32), not a harder version of the same one: the value is a fixed-arity vector of
 * numbers whose meaning is {@code type}-dependent (a single angle for {@code rotate}, an "angle cx cy" triple, one
 * or two numbers for {@code translate}/{@code scale}), and the target is a brand new {@link Transform} appended to
 * the node's own transform list, not an existing scalar property {@link SvgAttributeRegistry} already knows about.
 * <p>
 * {@code additive="sum"} and the default {@code additive="replace"} are deliberately treated identically here: both
 * simply append the animated {@link Transform} to {@code target.getTransforms()}, composing with whatever static
 * {@code transform} attribute and any other active {@code <animateTransform>} already contributed - JavaFX's
 * transform list already composes multiple entries for free, which is exactly what {@code additive="sum"} needs.
 * True {@code additive="replace"} semantics (temporarily removing the static transform while this animation is
 * active) is a documented, deliberate simplification - not implemented, since it needs imperative list mutation via
 * {@code KeyFrame} {@code onFinished} callbacks for a case this element's own acceptance criteria doesn't test, and
 * composing is the safer default visually (an object keeps its base position/orientation and gains motion, rather
 * than snapping to an unrelated absolute transform).
 */
public final class SvgAnimateTransformBuilder {

    public static Optional<Animation> build(SvgAnimateTransform element, Node target) {
        Kind kind = Kind.forType(StringUtils.trimToEmpty(element.getType()));
        if (kind == null) {
            return Optional.empty();
        }

        Optional<List<double[]>> valuesOpt = resolveValues(element, kind);
        if (valuesOpt.isEmpty() || valuesOpt.get().size() < 2) {
            return Optional.empty();
        }
        List<double[]> values = valuesOpt.get();

        List<Double> keyTimes = resolveKeyTimes(element, values);
        List<Interpolator> interpolators = resolveInterpolators(element, values.size() - 1);

        SvgAnimationTiming timing = SvgAnimationTiming.parse(element);
        Duration simpleDuration = timing.duration();
        boolean finiteRepeat = timing.repeatCount() != Animation.INDEFINITE && timing.repeatCount() > 0;

        Transform transform = kind.create();
        List<WritableValue<Number>> properties = kind.properties(transform);

        boolean accumulate = "sum".equalsIgnoreCase(StringUtils.trimToEmpty(element.getAccumulate()));
        // fill="remove" never applies to something that never ends (#149) - same reasoning as SvgValueAnimationBuilder.
        boolean removeOnFinish = timing.fill() == SvgAnimationTiming.FillBehavior.REMOVE && finiteRepeat;

        Timeline core;
        if (accumulate && finiteRepeat) {
            core = buildAccumulatedTimeline(kind, properties, values, keyTimes, interpolators, simpleDuration, timing.repeatCount());
        } else {
            // Without accumulate, every repeat plays the identical value list, restarting from its own first value
            // each time - exactly what JavaFX's native cycleCount already does for free (same reasoning as
            // SvgValueAnimationBuilder's own non-accumulate branch).
            core = new Timeline(buildKeyFrames(kind, properties, values, keyTimes, interpolators, simpleDuration, null, 0)
                .toArray(new KeyFrame[0]));
            if (removeOnFinish) {
                core.setCycleCount(timing.repeatCount());
            }
        }

        target.getTransforms().add(transform);
        if (!removeOnFinish) {
            return Optional.of(core);
        }
        // Reverting to this type's identity - through toPropertyValues, so skewX/skewY correctly revert their shear
        // *factor* to tan(0)=0, not the raw angle 0 - makes the transform contribute nothing, without needing to
        // remove it from target.getTransforms() at all. Same SequentialTransition rationale as
        // SvgValueAnimationBuilder: a separate, once-played Timeline avoids both the same-instant-KeyFrame concern
        // (accumulate branch) and the repeat-every-cycle concern (plain branch).
        double[] identity = kind.toPropertyValues(kind.identity());
        KeyValue[] revertValues = new KeyValue[properties.size()];
        for (int p = 0; p < properties.size(); p++) {
            revertValues[p] = new KeyValue(properties.get(p), identity[p], Interpolator.DISCRETE);
        }
        Timeline revert = new Timeline(new KeyFrame(Duration.ZERO, revertValues));
        return Optional.of(new SequentialTransition(core, revert));
    }

    // --- value list resolution -------------------------------------------------

    private static Optional<List<double[]>> resolveValues(SvgAnimateTransform element, Kind kind) {
        String valuesAttr = StringUtils.trimToNull(element.getValues());
        if (valuesAttr != null) {
            return parseAll(kind, splitSemicolon(valuesAttr));
        }
        String from = StringUtils.trimToNull(element.getFrom());
        String to = StringUtils.trimToNull(element.getTo());
        String by = StringUtils.trimToNull(element.getBy());
        if (from != null && to != null) {
            return parseAll(kind, List.of(from, to));
        }
        if (from != null && by != null) {
            Optional<double[]> fromVal = parseComponents(from, kind);
            Optional<double[]> byVal = parseComponents(by, kind);
            if (fromVal.isEmpty() || byVal.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(List.of(fromVal.get(), add(fromVal.get(), byVal.get())));
        }
        if (to != null) {
            Optional<double[]> toVal = parseComponents(to, kind);
            if (toVal.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(List.of(kind.identity(), toVal.get()));
        }
        if (by != null) {
            Optional<double[]> byVal = parseComponents(by, kind);
            if (byVal.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(List.of(kind.identity(), add(kind.identity(), byVal.get())));
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

    private static Optional<List<double[]>> parseAll(Kind kind, List<String> raw) {
        List<double[]> result = new ArrayList<>();
        for (String value : raw) {
            Optional<double[]> parsed = parseComponents(value, kind);
            if (parsed.isEmpty()) {
                return Optional.empty();
            }
            result.add(parsed.get());
        }
        return Optional.of(result);
    }

    private static Optional<double[]> parseComponents(String raw, Kind kind) {
        String[] tokens = raw.trim().split("[,\\s]+");
        double[] numbers = new double[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            if (!NumberUtils.isParsable(tokens[i])) {
                return Optional.empty();
            }
            numbers[i] = NumberUtils.toDouble(tokens[i]);
        }
        return kind.expand(numbers);
    }

    private static double[] add(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    // --- keyTimes (identical structure to SvgValueAnimationBuilder, over double[] instead of Object) -------------

    private static List<Double> resolveKeyTimes(SvgAnimateTransform element, List<double[]> values) {
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

    private static List<Double> pacedKeyTimes(List<double[]> values) {
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

    private static double distance(double[] a, double[] b) {
        double sumSquares = 0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sumSquares += d * d;
        }
        return Math.sqrt(sumSquares);
    }

    // --- interpolators (identical to SvgValueAnimationBuilder) ----------------

    private static List<Interpolator> resolveInterpolators(SvgAnimateTransform element, int intervalCount) {
        String calcMode = StringUtils.trimToEmpty(element.getCalcMode());
        if ("discrete".equalsIgnoreCase(calcMode)) {
            return Collections.nCopies(intervalCount, Interpolator.DISCRETE);
        }
        if ("spline".equalsIgnoreCase(calcMode)) {
            return resolveSplineInterpolators(element, intervalCount);
        }
        return Collections.nCopies(intervalCount, Interpolator.LINEAR);
    }

    private static List<Interpolator> resolveSplineInterpolators(SvgAnimateTransform element, int intervalCount) {
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
        String[] parts = raw.trim().split("[,\\s]+");
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

    private static List<KeyFrame> buildKeyFrames(Kind kind, List<WritableValue<Number>> properties, List<double[]> values,
        List<Double> keyTimes, List<Interpolator> interpolators, Duration simpleDuration, double[] valueShift, int startIndex) {
        List<KeyFrame> frames = new ArrayList<>();
        for (int i = startIndex; i < values.size(); i++) {
            double[] shifted = valueShift == null ? values.get(i) : add(values.get(i), valueShift);
            double[] written = kind.toPropertyValues(shifted);
            Duration time = simpleDuration.multiply(keyTimes.get(i));
            KeyValue[] keyValues = new KeyValue[properties.size()];
            for (int p = 0; p < properties.size(); p++) {
                keyValues[p] = i == 0
                    ? new KeyValue(properties.get(p), written[p])
                    : new KeyValue(properties.get(p), written[p], interpolators.get(i - 1));
            }
            frames.add(new KeyFrame(time, keyValues));
        }
        return frames;
    }

    /**
     * {@code accumulate="sum"} with a finite {@code repeatCount}: unrolls every repeat into one continuous {@code
     * Timeline} spanning the entire repeated duration - the same technique as {@link SvgValueAnimationBuilder}, over
     * a vector shift instead of a scalar one. {@link SvgAnimationController#withTiming} recognises this case via
     * {@link ISvgAccumulatableAnimationElement} and skips its own generic {@code repeatCount} wrapping accordingly.
     */
    private static Timeline buildAccumulatedTimeline(Kind kind, List<WritableValue<Number>> properties, List<double[]> values,
        List<Double> keyTimes, List<Interpolator> interpolators, Duration simpleDuration, int cycles) {
        double[] first = values.get(0);
        double[] last = values.get(values.size() - 1);
        double[] perCycleDelta = new double[first.length];
        for (int i = 0; i < first.length; i++) {
            perCycleDelta[i] = last[i] - first[i];
        }

        List<KeyFrame> frames = new ArrayList<>();
        for (int cycle = 0; cycle < cycles; cycle++) {
            double[] valueShift = new double[perCycleDelta.length];
            for (int i = 0; i < valueShift.length; i++) {
                valueShift[i] = cycle * perCycleDelta[i];
            }
            Duration timeShift = simpleDuration.multiply(cycle);
            int startIndex = cycle == 0 ? 0 : 1;
            for (KeyFrame frame : buildKeyFrames(kind, properties, values, keyTimes, interpolators, simpleDuration, valueShift,
                startIndex)) {
                frames.add(new KeyFrame(timeShift.add(frame.getTime()), frame.getValues().toArray(new KeyValue[0])));
            }
        }
        return new Timeline(frames.toArray(new KeyFrame[0]));
    }

    // --- per-type shape --------------------------------------------------------

    private enum Kind {
        TRANSLATE(new double[] {0, 0}) {
            @Override
            Optional<double[]> expand(double[] raw) {
                if (raw.length == 1) {
                    return Optional.of(new double[] {raw[0], 0});
                }
                return raw.length == 2 ? Optional.of(raw) : Optional.empty();
            }

            @Override
            Transform create() {
                return new Translate();
            }

            @Override
            List<WritableValue<Number>> properties(Transform transform) {
                Translate translate = (Translate) transform;
                return List.of(translate.xProperty(), translate.yProperty());
            }
        },
        SCALE(new double[] {1, 1}) {
            @Override
            Optional<double[]> expand(double[] raw) {
                if (raw.length == 1) {
                    return Optional.of(new double[] {raw[0], raw[0]});
                }
                return raw.length == 2 ? Optional.of(raw) : Optional.empty();
            }

            @Override
            Transform create() {
                return new Scale();
            }

            @Override
            List<WritableValue<Number>> properties(Transform transform) {
                Scale scale = (Scale) transform;
                return List.of(scale.xProperty(), scale.yProperty());
            }
        },
        ROTATE(new double[] {0, 0, 0}) {
            @Override
            Optional<double[]> expand(double[] raw) {
                if (raw.length == 1) {
                    return Optional.of(new double[] {raw[0], 0, 0});
                }
                return raw.length == 3 ? Optional.of(raw) : Optional.empty();
            }

            @Override
            Transform create() {
                return new Rotate();
            }

            @Override
            List<WritableValue<Number>> properties(Transform transform) {
                Rotate rotate = (Rotate) transform;
                return List.of(rotate.angleProperty(), rotate.pivotXProperty(), rotate.pivotYProperty());
            }
        },
        SKEWX(new double[] {0}) {
            @Override
            Optional<double[]> expand(double[] raw) {
                return raw.length == 1 ? Optional.of(raw) : Optional.empty();
            }

            @Override
            Transform create() {
                return new Shear();
            }

            @Override
            List<WritableValue<Number>> properties(Transform transform) {
                return List.of(((Shear) transform).xProperty());
            }

            @Override
            double[] toPropertyValues(double[] parsed) {
                return new double[] {Math.tan(Math.toRadians(parsed[0]))};
            }
        },
        SKEWY(new double[] {0}) {
            @Override
            Optional<double[]> expand(double[] raw) {
                return raw.length == 1 ? Optional.of(raw) : Optional.empty();
            }

            @Override
            Transform create() {
                return new Shear();
            }

            @Override
            List<WritableValue<Number>> properties(Transform transform) {
                return List.of(((Shear) transform).yProperty());
            }

            @Override
            double[] toPropertyValues(double[] parsed) {
                return new double[] {Math.tan(Math.toRadians(parsed[0]))};
            }
        };

        private final double[] identity;

        Kind(double[] identity) {
            this.identity = identity;
        }

        double[] identity() {
            return identity;
        }

        /**
         * Validates {@code raw}'s length against this type's allowed forms and fills in any omitted trailing
         * component with its type-specific default ({@code translate ty}/{@code rotate cx,cy} default to 0,
         * {@code scale sy} defaults to {@code sx} - SVG's own per-type defaulting rules, not a uniform "pad with
         * zero"). {@link Optional#empty()} for any other length - an invalid value, degrading the whole animation.
         */
        abstract Optional<double[]> expand(double[] raw);

        abstract Transform create();

        /** In the same order {@link #expand} produces components. */
        abstract List<WritableValue<Number>> properties(Transform transform);

        /**
         * Converts a resolved value (still in the SVG-visible unit for this type - degrees for an angle) into
         * whatever the actual JavaFX property expects. Identity for every type except {@code skewX}/{@code skewY},
         * where {@link Shear}'s property is a shear *factor* ({@code tan} of the angle), not the angle itself -
         * applied only at the point of writing each {@code KeyValue}, so {@code keyTimes}/{@code paced}/{@code
         * accumulate} math above all stays in the more meaningful angle domain.
         */
        double[] toPropertyValues(double[] parsed) {
            return parsed;
        }

        static Kind forType(String type) {
            return switch (type) {
                case "translate" -> TRANSLATE;
                case "scale" -> SCALE;
                case "rotate" -> ROTATE;
                case "skewX" -> SKEWX;
                case "skewY" -> SKEWY;
                default -> null;
            };
        }
    }

    private SvgAnimateTransformBuilder() {
    }

}
