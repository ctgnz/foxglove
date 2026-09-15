package nz.co.ctg.foxglove.animate;

/**
 * An animation element that declares {@code accumulate} - both {@link ISvgValueAnimationElement} (via {@code
 * <animate>}/{@code <animateColor>}, #32) and {@code SvgAnimateTransform} (#34) do, and both can self-unroll {@code accumulate="sum"} with a finite {@code repeatCount} into one
 * continuous {@code Timeline} rather than leaving it to JavaFX's own {@code cycleCount} (which has no way to shift values between replays).
 * {@link SvgAnimationController#withTiming} checks for this interface - not any single concrete animation type - to decide whether to skip its own generic {@code repeatCount}
 * wrapping.
 */
public interface ISvgAccumulatableAnimationElement extends ISvgAnimationElement {

    String getAccumulate();

}
