package nz.co.ctg.foxglove.animate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgElementIndex;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Plays, pauses, stops and seeks every animation built for a rendered document - returned alongside the built
 * {@link Node} by {@code SvgGraphic.createAnimatedGraphic}, since rendering itself has no other way to hand back
 * something a caller can control playback through.
 * <p>
 * Built by resolving every {@link ISvgAnimationElement} in the document to its target node (via {@link
 * SvgAnimationTargets} and the node registry {@code createAnimatedGraphic} populates), asking the element itself to
 * build its own {@link Animation} (see {@link ISvgAnimationElement#buildAnimation}), and wrapping whatever comes
 * back in that element's own {@link SvgAnimationTiming}. An element with no resolvable target, no built node for
 * that target, or nothing from {@code buildAnimation} (every element, until #31-#34 override it) is skipped -
 * exactly this renderer's usual "unsupported degrades rather than throws" treatment, not a partial or broken
 * controller.
 * <p>
 * <b>#86: what happens when two or more elements animate the same attribute on the same node.</b> Each one still
 * gets its own independent {@link Animation}, writing directly to the same shared JavaFX property - there is no
 * shared per-(target, attribute) evaluator combining them. Investigated directly rather than assumed, with both a
 * synthetic controller-level seek probe and the real W3C suite (which composition case actually reaches this at all):
 * <ul>
 * <li><b>Sequential, non-overlapping elements already compose correctly</b> - e.g. one {@code <animate>} spanning
 * {@code [0s, 3s)} followed by a second spanning {@code [3s, 6s)} on the same attribute, the shape every real
 * same-attribute case in the suite actually takes (see {@code animate-elem-32-t}). This is an emergent property of
 * this class's own {@link #build} and {@link #seek}, not a deliberate composition feature: an element still before
 * its own {@code begin} is a {@link PauseTransition} that never touches the property at all, and {@link #build}
 * preserves document order, so {@link #seek}'s {@code forEach} always {@code jumpTo}s whichever element is later in
 * the document - and therefore later to become active - last, letting it correctly overwrite an earlier, by-then-
 * irrelevant element's frozen value. Confirmed both with a direct seek probe and against the live conformance
 * dashboard's rendering of {@code animate-elem-32-t}, which matches the reference throughout.
 * <li><b>Genuinely overlapping elements with {@code additive="sum"} are not composed.</b> SMIL's real model would sum
 * every currently-active contribution on top of the base value; here, whichever element is later in document order
 * simply overwrites the property each time it is touched, silently discarding what any other simultaneously-active
 * element already wrote - confirmed directly: two overlapping {@code additive="sum"} animations produce only the
 * later one's own value, not their sum. Deliberately not implemented: it needs a genuinely different design (a
 * shared per-(target, attribute) evaluator, materially larger than this class), and an exhaustive search of the
 * whole W3C SVG 1.1 test suite found no document that actually exercises simultaneous {@code additive="sum"} on one
 * scalar attribute - every real {@code additive="sum"} case in the suite is either a single element repeating
 * itself (already handled - see {@link ISvgAccumulatableAnimationElement}) or multiple {@code <animateTransform>}/
 * {@code <animateMotion>} elements on one node's {@code transform}, which is a list JavaFX itself already composes
 * by matrix-multiplying every entry in list order - not this same "one property, one writer" problem at all.
 * </ul>
 */
public final class SvgAnimationController {

    private final List<Animation> animations;

    public SvgAnimationController(SvgElementIndex index, Map<ISvgElement, Node> nodeRegistry, RenderContext context) {
        this.animations = build(index, nodeRegistry, context);
        // Warm-up (#151): a JavaFX Animation that has never been play()ed does not apply anything to its target
        // property when jumpTo() is called - confirmed empirically, not merely assumed - so seek() alone, called
        // before any play(), was a silent no-op for every animation in the document. Playing and immediately
        // pausing each one here, before the caller ever sees this controller, makes every later seek()/play() work
        // correctly regardless of call order. This happens off-screen (construction, before any node is typically
        // shown) and produces no visible flash: JavaFX only paints at a pulse, and no pulse occurs between the
        // synchronous play() and pause() calls below.
        this.animations.forEach(animation -> {
            animation.play();
            animation.pause();
        });
    }

    private static List<Animation> build(SvgElementIndex index, Map<ISvgElement, Node> nodeRegistry, RenderContext context) {
        List<Animation> result = new ArrayList<>();
        for (ISvgAnimationElement element : index.getElementsOfType(ISvgAnimationElement.class)) {
            SvgAnimationTargets.resolve(element, index)
                .map(nodeRegistry::get)
                .flatMap(target -> element.buildAnimation(target, context))
                .map(animation -> withTiming(element, animation, SvgAnimationTiming.parse(element)))
                .ifPresent(result::add);
        }
        return result;
    }

    /**
     * {@code repeatCount} applies to {@code animation} itself, not the leading {@code begin} pause - repeating
     * "pause then play" as a unit would re-insert the delay before every cycle, which is not what SMIL's
     * {@code repeatCount} means. {@code fill="freeze"} is a JavaFX {@code Animation}'s own default end-of-run
     * behaviour (the last interpolated value holds); {@code fill="remove"} needs an explicit reset of whatever
     * property was actually animated, which only whichever future code supplies the real {@code KeyValue} can do -
     * nothing to reset yet in this issue, since {@link ISvgAnimationElement#buildAnimation} has no concrete
     * override anywhere yet.
     * <p>
     * Two independent element/attribute combinations (see {@link SvgValueAnimationBuilder} for {@code <animate>}/
     * {@code <animateColor>}, {@code SvgAnimateTransformBuilder} for {@code <animateTransform>}) are genuine
     * exceptions to generic {@code repeatCount} wrapping, because JavaFX's {@code cycleCount} can only replay a
     * {@code Timeline} from its own start every time, with no way to vary what happens between cycles - so each
     * builder manually unrolls every repeat into one continuous {@code Timeline} spanning the *entire* repeated
     * duration and plays it exactly once ({@code cycleCount} left at its correct value, {@code 1}) whenever either
     * applies: {@code accumulate="sum"} with a finite {@code repeatCount} ({@link ISvgAccumulatableAnimationElement}
     * - each cycle's values shift by the previous cycle's own delta, which plain replay can't express), or
     * {@code fill="remove"} with a finite {@code repeatCount} (#149 - a revert appended once must not repeat at the
     * end of every cycle). Re-applying {@code repeatCount} here on top of either would replay the already-unrolled
     * sequence {@code repeatCount} times over - a real playback bug, not a harmless no-op - so both cases are
     * recognised by element type/attributes (the only reliable signal; {@code cycleCount} itself is legitimately
     * {@code 1} either way) and skipped rather than inferred from {@code animation}'s own state. {@code
     * SvgSetAttribute} implements neither interface: its own per-cycle revert is already correct for its constant
     * value (see its own javadoc), so it is deliberately not matched here.
     */
    private static Animation withTiming(ISvgAnimationElement element, Animation animation, SvgAnimationTiming timing) {
        if (!repeatCountAlreadyHandled(element, timing)) {
            animation.setCycleCount(timing.repeatCount());
        }
        Duration begin = timing.begin().orElse(Duration.ZERO);
        if (begin.greaterThan(Duration.ZERO)) {
            return new SequentialTransition(new PauseTransition(begin), animation);
        }
        return animation;
    }

    private static boolean repeatCountAlreadyHandled(ISvgAnimationElement element, SvgAnimationTiming timing) {
        boolean finiteRepeat = timing.repeatCount() != Animation.INDEFINITE && timing.repeatCount() > 0;
        if (!finiteRepeat) {
            return false;
        }
        boolean accumulateSum = element instanceof ISvgAccumulatableAnimationElement value
            && "sum".equalsIgnoreCase(StringUtils.trimToEmpty(value.getAccumulate()));
        boolean removeOnFinish = (element instanceof ISvgValueAnimationElement || element instanceof SvgAnimateTransform)
            && timing.fill() == SvgAnimationTiming.FillBehavior.REMOVE;
        return accumulateSum || removeOnFinish;
    }

    public void play() {
        animations.forEach(Animation::play);
    }

    public void pause() {
        animations.forEach(Animation::pause);
    }

    public void stop() {
        animations.forEach(Animation::stop);
    }

    public void seek(Duration time) {
        animations.forEach(animation -> animation.jumpTo(time));
    }

    /**
     * The longest total duration across every wrapped animation ({@code Duration.INDEFINITE} if any of them repeat
     * indefinitely), or {@link Duration#ZERO} for a document with nothing to animate.
     */
    public Duration getTotalDuration() {
        return animations.stream().map(Animation::getTotalDuration).max(Comparator.naturalOrder()).orElse(Duration.ZERO);
    }

    /**
     * The number of animations actually built - mostly useful for tests, since an element with no resolvable
     * target/node/{@code buildAnimation} result is silently skipped rather than represented here at all.
     */
    public int size() {
        return animations.size();
    }

}
