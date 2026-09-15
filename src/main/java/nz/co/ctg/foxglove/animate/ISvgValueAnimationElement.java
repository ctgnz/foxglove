package nz.co.ctg.foxglove.animate;

/**
 * The shared shape of {@code <animate>} and {@code <animateColor>} - both declare an identical set of value-animation attributes (only the parser applied to
 * {@code from}/{@code to}/{@code by}/{@code values} differs, and that difference lives entirely in {@link SvgAttributeRegistry}'s per-property binding, not in either element
 * class). {@code to} itself is already declared by {@link ISvgAnimationElement}. Purely additive - both concrete classes already declare every one of these getters with a matching
 * signature.
 */
public interface ISvgValueAnimationElement extends ISvgAccumulatableAnimationElement {

    String getAttributeName();

    String getCalcMode();

    String getValues();

    String getKeyTimes();

    String getKeySplines();

    String getFrom();

    String getBy();

    String getAdditive();

}
