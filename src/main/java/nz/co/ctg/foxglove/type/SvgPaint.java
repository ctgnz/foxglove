package nz.co.ctg.foxglove.type;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.paint.Paint;

/**
 * The value of a {@code fill} or {@code stroke}, which is not always a paint that can be constructed on the spot.
 * <p>
 * {@code url(#grad)} names an element that may not have been parsed yet and whose paint depends on the shape it ends
 * up on, and {@code currentColor} stands for whatever {@code color} resolves to at the point of use. Neither can be
 * turned into a {@link Paint} while parsing, and {@code javafx.scene.paint.Paint} cannot be extended to carry them -
 * its only constructor is package private. So the parsed value is held here and resolved at render time.
 * <p>
 * A null {@code SvgPaint} means the property was not specified, which is what allows it to be inherited. That is
 * distinct from {@link #none()}, which is an explicit instruction not to paint.
 */
public final class SvgPaint {

    private enum Kind {
        NONE, COLOR, REFERENCE, CURRENT_COLOR
    }

    private static final String NONE_KEYWORD = "none";
    private static final String CURRENT_COLOR_KEYWORD = "currentColor";
    private static final String URL_PREFIX = "url(";

    private static final SvgPaint NONE = new SvgPaint(Kind.NONE, null, null, null);
    private static final SvgPaint CURRENT_COLOR = new SvgPaint(Kind.CURRENT_COLOR, null, null, null);

    /**
     * An explicit {@code none}: the shape is not painted. Distinct from an unspecified value, which inherits.
     */
    public static SvgPaint none() {
        return NONE;
    }

    /**
     * The {@code currentColor} keyword, resolved against the inherited {@code color} at render time.
     */
    public static SvgPaint currentColor() {
        return CURRENT_COLOR;
    }

    public static SvgPaint of(Paint paint) {
        return paint == null ? null : new SvgPaint(Kind.COLOR, paint, null, null);
    }

    /**
     * A reference to a paint server, with the fallback to use when it cannot be resolved.
     */
    public static SvgPaint reference(String reference, SvgPaint fallback) {
        return new SvgPaint(Kind.REFERENCE, null, Objects.requireNonNull(reference), fallback);
    }

    /**
     * Parses a {@code fill} or {@code stroke} value.
     * <p>
     * Accepts a colour, {@code none}, {@code currentColor}, and a {@code url(#id)} reference which may be followed by
     * a fallback for when the reference does not resolve - {@code fill="url(#grad) red"}.
     *
     * @return the parsed value, or null if the value is blank or not recognised, both of which leave the property
     *         unspecified so that it inherits
     */
    public static SvgPaint parse(String value) {
        String text = StringUtils.trimToEmpty(value);
        if (text.isEmpty()) {
            return null;
        }
        if (NONE_KEYWORD.equalsIgnoreCase(text)) {
            return none();
        }
        if (CURRENT_COLOR_KEYWORD.equalsIgnoreCase(text)) {
            return currentColor();
        }
        if (StringUtils.startsWithIgnoreCase(text, URL_PREFIX)) {
            int close = text.indexOf(')');
            if (close < 0) {
                return null;
            }
            String reference = text.substring(0, close + 1);
            SvgPaint fallback = parse(text.substring(close + 1));
            return reference(reference, fallback);
        }
        return of(parseColour(text));
    }

    private static Paint parseColour(String text) {
        try {
            return Paint.valueOf(text);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private final Kind kind;
    private final Paint paint;
    private final String reference;
    private final SvgPaint fallback;

    private SvgPaint(Kind kind, Paint paint, String reference, SvgPaint fallback) {
        this.kind = kind;
        this.paint = paint;
        this.reference = reference;
        this.fallback = fallback;
    }

    public boolean isNone() {
        return kind == Kind.NONE;
    }

    public boolean isColor() {
        return kind == Kind.COLOR;
    }

    public boolean isReference() {
        return kind == Kind.REFERENCE;
    }

    public boolean isCurrentColor() {
        return kind == Kind.CURRENT_COLOR;
    }

    /**
     * The paint this value resolves to directly, or null when it names a reference or a keyword that has to be
     * resolved against the document or the inherited style.
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * The reference as written, including the {@code url(...)} wrapper, ready for
     * {@code SvgElementIndex.resolve}.
     */
    public String getReference() {
        return reference;
    }

    /**
     * What to paint with when the reference cannot be resolved, or null if none was given - in which case the
     * specification says to treat the paint as {@code none}.
     */
    public SvgPaint getFallback() {
        return fallback;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SvgPaint that)) {
            return false;
        }
        return kind == that.kind
            && Objects.equals(paint, that.paint)
            && Objects.equals(reference, that.reference)
            && Objects.equals(fallback, that.fallback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, paint, reference, fallback);
    }

    @Override
    public String toString() {
        return switch (kind) {
            case NONE -> NONE_KEYWORD;
            case CURRENT_COLOR -> CURRENT_COLOR_KEYWORD;
            case COLOR -> String.valueOf(paint);
            case REFERENCE -> fallback == null ? reference : reference + " " + fallback;
        };
    }

}
