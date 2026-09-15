package nz.co.ctg.foxglove;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.paint.ISvgGradientElement;
import nz.co.ctg.foxglove.paint.SvgPattern;
import nz.co.ctg.foxglove.type.SvgPaint;

/**
 * Turns a parsed {@link SvgPaint} into something JavaFX can paint with.
 * <p>
 * This is deferred to render time because neither of the interesting cases can be settled while parsing: a {@code url(#id)} reference needs the whole document to have been read,
 * and {@code currentColor} needs the {@code color} in force where the paint is used.
 * <p>
 * A reference to a {@code <pattern>} rasterises via {@link SvgPattern#createPaint}, which calls {@code Node.snapshot(...)} and so requires the JavaFX Application Thread - the only
 * path through here that does. A colour, a gradient reference and {@code currentColor} all remain callable off-thread.
 */
public final class SvgPaintResolver {

    /**
     * The initial value of {@code color}, used when {@code currentColor} is asked for and nothing has set one.
     */
    private static final Color INITIAL_COLOR = Color.BLACK;

    /**
     * Resolves a paint value.
     *
     * @param value
     *            the parsed value, or null if the property was not specified
     * @param style
     *            the style in force, which supplies {@code color}
     * @param context
     *            the rendering context, carrying the document's element index (used to resolve a {@code url(#id)} reference) and, for a pattern reference, the referencing shape's
     *            own bounding box
     * @return the paint to use, or null for no paint - which covers an explicit {@code none}, an unspecified value, and a reference that resolves to a gradient or pattern with
     *         nothing to paint
     */
    public static Paint resolve(SvgPaint value, ISvgStylable style, RenderContext context) {
        if (value == null || value.isNone()) {
            return null;
        }
        if (value.isColor()) {
            return value.getPaint();
        }
        if (value.isCurrentColor()) {
            return currentColor(style);
        }
        SvgElementIndex index = context.getElementIndex();
        if (index != null) {
            ISvgGradientElement gradient = index.resolve(value.getReference(), ISvgGradientElement.class)
                .orElse(null);
            if (gradient != null) {
                return gradient.createPaint(index);
            }
            SvgPattern pattern = index.resolve(value.getReference(), SvgPattern.class)
                .orElse(null);
            if (pattern != null) {
                return pattern.createPaint(context);
            }
        }
        // The specification says an unresolvable reference falls back to the colour given after it, and to none when
        // there is not one - notably not to black, which is what silently dropping the value used to produce.
        return resolve(value.getFallback(), style, context);
    }

    /**
     * {@code color} is inherited, so the value comes from the resolved style rather than from the element that wrote {@code currentColor}.
     */
    private static Paint currentColor(ISvgStylable style) {
        String color = style == null ? null : style.getColor();
        if (StringUtils.isBlank(color)) {
            return INITIAL_COLOR;
        }
        try {
            return Color.web(color.trim());
        } catch (RuntimeException e) {
            return INITIAL_COLOR;
        }
    }

    private SvgPaintResolver() {
    }

}
