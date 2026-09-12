package nz.co.ctg.foxglove.animate;

import java.util.Optional;
import java.util.function.Function;

import javafx.beans.value.WritableValue;

/**
 * A resolved animation target: the concrete {@link WritableValue} on an already-built node, paired with the parser
 * for whatever value type that property expects.
 * <p>
 * The parser is deliberately separate from - and can fail independently of - property resolution: an attribute can
 * be a perfectly valid, mappable property (e.g. {@code fill}) while a *specific* value string still doesn't parse
 * (e.g. {@code url(#grad)}, out of scope for an animated value here even though the property itself resolves fine).
 * {@link Optional#empty()} from {@link #parser()} signals that distinct case - the same "skip, don't throw"
 * treatment {@link SvgAttributeRegistry#resolve} itself uses when the property mapping doesn't exist at all.
 */
public record SvgAttributeBinding<T>(WritableValue<T> property, Function<String, Optional<T>> parser) {
}
