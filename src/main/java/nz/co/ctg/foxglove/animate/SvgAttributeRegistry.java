package nz.co.ctg.foxglove.animate;

import java.util.Optional;
import java.util.function.Function;

import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.adapter.SizeAdapter;
import nz.co.ctg.foxglove.type.SvgPaint;

import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Resolves a SMIL {@code attributeName} against an already-built JavaFX node to an {@link SvgAttributeBinding} -
 * the target {@link WritableValue} plus the parser for whatever value type it expects. Keyed on the *built node's*
 * own runtime type, not the SVG element type that produced it: geometry mapping only ever depends on which concrete
 * JavaFX shape was built, and this avoids any coupling back to the {@code nz.co.ctg.foxglove.shape} classes at all.
 * <p>
 * Deliberately narrower than every animatable SVG attribute - each of the following is a genuinely different-shaped
 * problem, not a harder version of the same one, and is left unmapped (this resolves to {@link Optional#empty()},
 * the same "skip, don't throw" treatment used throughout this renderer's other unsupported cases):
 * <ul>
 * <li>{@code points} on {@code <polygon>}/{@code <polyline>} - {@code Polygon.getPoints()} is an
 * {@code ObservableList<Double>}, not a single {@code WritableValue}.
 * <li>{@code d} on {@code <path>} - {@code SVGPath.contentProperty()} is a raw {@code String}; interpolating it
 * means interpolating path segments, not text.
 * <li>{@code transform} - handled by {@code <animateTransform>} rather than {@code <animate>}, and maps to the
 * node's transform list rather than a single property.
 * <li>A non-{@code Shape} inherited property, such as {@code fill} animated on a {@code <g>} and expected to
 * propagate to descendants that don't set their own - needs its own mechanism, not a single {@code WritableValue}.
 * <li>{@code attributeType} ({@code CSS} vs {@code XML}) is not disambiguated - a name resolves the same way
 * regardless of which one a document declares.
 * <li>Presentation attributes with no continuous interpolation ({@code visibility}, {@code display}) are not
 * mapped at all; discrete stepping through arbitrary values ({@code calcMode="discrete"}) is separate, later work.
 * </ul>
 */
public final class SvgAttributeRegistry {

    public static Optional<SvgAttributeBinding<?>> resolve(Node node, String attributeName) {
        if (attributeName == null) {
            return Optional.empty();
        }
        Optional<SvgAttributeBinding<?>> geometry = resolveGeometry(node, attributeName);
        if (geometry.isPresent()) {
            return geometry;
        }
        return node instanceof Shape shape ? resolveShapeProperty(shape, attributeName) : Optional.empty();
    }

    /**
     * Mirrors each shape's own {@code createShape} - including {@code rx}/{@code ry} mapping directly onto
     * {@code Rectangle}'s {@code arcWidth}/{@code arcHeight} with no doubling, matching what
     * {@code SvgRectangle.createShape} already does today. Not this issue's place to second-guess that.
     */
    private static Optional<SvgAttributeBinding<?>> resolveGeometry(Node node, String attributeName) {
        if (node instanceof Rectangle rectangle) {
            return switch (attributeName) {
                case "x" -> Optional.of(lengthBinding(rectangle.xProperty()));
                case "y" -> Optional.of(lengthBinding(rectangle.yProperty()));
                case "width" -> Optional.of(lengthBinding(rectangle.widthProperty()));
                case "height" -> Optional.of(lengthBinding(rectangle.heightProperty()));
                case "rx" -> Optional.of(lengthBinding(rectangle.arcWidthProperty()));
                case "ry" -> Optional.of(lengthBinding(rectangle.arcHeightProperty()));
                default -> Optional.empty();
            };
        }
        if (node instanceof Circle circle) {
            return switch (attributeName) {
                case "cx" -> Optional.of(lengthBinding(circle.centerXProperty()));
                case "cy" -> Optional.of(lengthBinding(circle.centerYProperty()));
                case "r" -> Optional.of(lengthBinding(circle.radiusProperty()));
                default -> Optional.empty();
            };
        }
        if (node instanceof Ellipse ellipse) {
            return switch (attributeName) {
                case "cx" -> Optional.of(lengthBinding(ellipse.centerXProperty()));
                case "cy" -> Optional.of(lengthBinding(ellipse.centerYProperty()));
                case "rx" -> Optional.of(lengthBinding(ellipse.radiusXProperty()));
                case "ry" -> Optional.of(lengthBinding(ellipse.radiusYProperty()));
                default -> Optional.empty();
            };
        }
        if (node instanceof Line line) {
            return switch (attributeName) {
                case "x1" -> Optional.of(lengthBinding(line.startXProperty()));
                case "y1" -> Optional.of(lengthBinding(line.startYProperty()));
                case "x2" -> Optional.of(lengthBinding(line.endXProperty()));
                case "y2" -> Optional.of(lengthBinding(line.endYProperty()));
                default -> Optional.empty();
            };
        }
        // Polygon/Polyline ("points") and SVGPath ("d") are not single WritableValues - see the class javadoc.
        return Optional.empty();
    }

    private static Optional<SvgAttributeBinding<?>> resolveShapeProperty(Shape shape, String attributeName) {
        return switch (attributeName) {
            case "fill" -> Optional.of(new SvgAttributeBinding<>(shape.fillProperty(), SvgAttributeRegistry::parseColor));
            case "stroke" -> Optional.of(new SvgAttributeBinding<>(shape.strokeProperty(), SvgAttributeRegistry::parseColor));
            case "opacity" -> Optional.of(lengthBinding(shape.opacityProperty(), ISvgGraphicsAttributes::parseOpacity));
            case "stroke-width" -> Optional.of(lengthBinding(shape.strokeWidthProperty()));
            default -> Optional.empty();
        };
    }

    /**
     * A plain color only - {@code url(#...)} and {@code currentColor} values are out of scope for an animated
     * value here even though {@code fill}/{@code stroke} themselves are mappable properties (see the class
     * javadoc's "value can fail independently of the property" note).
     */
    private static Optional<Paint> parseColor(String raw) {
        SvgPaint paint = SvgPaint.parse(raw);
        return paint != null && paint.isColor() ? Optional.ofNullable(paint.getPaint()) : Optional.empty();
    }

    private static SvgAttributeBinding<Number> lengthBinding(WritableValue<Number> property) {
        return lengthBinding(property, raw -> SizeAdapter.parse(raw).pixels());
    }

    private static SvgAttributeBinding<Number> lengthBinding(WritableValue<Number> property, Function<String, Double> parse) {
        return new SvgAttributeBinding<>(property, raw -> {
            try {
                return Optional.ofNullable(parse.apply(raw));
            } catch (RuntimeException e) {
                return Optional.empty();
            }
        });
    }

    private SvgAttributeRegistry() {
    }

}
