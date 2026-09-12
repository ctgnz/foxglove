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
 */
public final class SvgAnimationController {

    private final List<Animation> animations;

    public SvgAnimationController(SvgElementIndex index, Map<ISvgElement, Node> nodeRegistry, RenderContext context) {
        this.animations = build(index, nodeRegistry, context);
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
     * {@code accumulate="sum"} with a finite {@code repeatCount} (see {@link SvgValueAnimationBuilder}) is a genuine
     * exception to generic {@code repeatCount} wrapping: JavaFX's {@code cycleCount} can only replay a {@code
     * Timeline} from its own start every time, with no way to shift values between cycles, so that builder manually
     * unrolls every repeat into one continuous {@code Timeline} spanning the *entire* repeated duration and plays it
     * exactly once ({@code cycleCount} left at its correct value, {@code 1}). Re-applying {@code repeatCount} here on
     * top of that would replay the already-fully-unrolled sequence {@code repeatCount} times over - a real playback
     * bug, not a harmless no-op - so this case is recognised by element type/attributes (the only reliable signal;
     * {@code cycleCount} itself is legitimately {@code 1} either way) and skipped rather than inferred from
     * {@code animation}'s own state.
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
        return element instanceof ISvgValueAnimationElement value
            && "sum".equalsIgnoreCase(StringUtils.trimToEmpty(value.getAccumulate()))
            && timing.repeatCount() != Animation.INDEFINITE
            && timing.repeatCount() > 0;
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
