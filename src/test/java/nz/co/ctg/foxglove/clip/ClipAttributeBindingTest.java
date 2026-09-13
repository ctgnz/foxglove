package nz.co.ctg.foxglove.clip;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.SvgGraphic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Regression cover for #119: seven presentation attributes on {@code <clipPath>} and {@code <mask>} were bound as
 * plain strings, against getters returning {@code SvgPaint}, {@code List<Double>}, {@code Double} and
 * {@code StrokeLineJoin}. Reading any of them threw {@code ClassCastException}.
 * <p>
 * That a {@code <clipPath>} never strokes anything of its own is no protection: these properties are <b>inherited</b>
 * (see {@code SvgInheritedStyle}), so the wrongly-typed value travels down to the element's children and the
 * unchecked cast fails as soon as a child shape has its graphics properties applied.
 * <p>
 * Necessarily driven through {@link FoxgloveParser}. Building the same objects in memory goes through the typed
 * setters and stores the right type regardless of what the binding file says, so an in-memory test passes whether
 * the binding is right or wrong - which is exactly how this survived unnoticed, the same trap as #105.
 */
public class ClipAttributeBindingTest {

    @Test
    public void testClipPathPresentationAttributesBindToTheirDeclaredTypes() throws Exception {
        SvgClipPath clipPath = parse("""
            <clipPath id="c" fill="red" stroke="blue" stroke-dasharray="5,2" stroke-dashoffset="3"
                stroke-linejoin="round" stroke-miterlimit="6" stroke-width="2">
              <rect width="50" height="50"/>
            </clipPath>
            """, SvgClipPath.class);

        assertAttributesAreTyped(clipPath);
    }

    @Test
    public void testMaskPresentationAttributesBindToTheirDeclaredTypes() throws Exception {
        SvgMask mask = parse("""
            <mask id="c" fill="red" stroke="blue" stroke-dasharray="5,2" stroke-dashoffset="3"
                stroke-linejoin="round" stroke-miterlimit="6" stroke-width="2">
              <rect width="50" height="50"/>
            </mask>
            """, SvgMask.class);

        assertAttributesAreTyped(mask);
    }

    /**
     * The crash as originally reported: a child shape inheriting the mistyped value from its {@code <clipPath>}
     * parent. Rendering is what triggers it, since that is when graphics properties are applied.
     */
    @Test
    public void testAChildInheritingThoseAttributesRendersRatherThanThrowing() throws Exception {
        SvgGraphic svg = parseDocument("""
            <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
              <defs>
                <clipPath id="c" fill="red" stroke="blue" stroke-dasharray="5,2" stroke-dashoffset="3"
                    stroke-linejoin="round" stroke-miterlimit="6" stroke-width="2">
                  <rect width="50" height="50"/>
                </clipPath>
              </defs>
              <rect width="80" height="80" clip-path="url(#c)"/>
            </svg>
            """);

        assertThat(svg.createGroup(), notNullValue());
    }

    private static void assertAttributesAreTyped(ISvgGraphicsAttributes element) {
        assertThat(element.getFill().getPaint(), is(Color.RED));
        assertThat(element.getStroke().getPaint(), is(Color.BLUE));
        assertThat(element.getStrokeDashArray(), is(List.of(5.0, 2.0)));
        assertThat(element.getStrokeDashOffset(), is(3.0));
        assertThat(element.getStrokeLineJoin(), is(StrokeLineJoin.ROUND));
        assertThat(element.getStrokeMiterLimit(), is(6.0));
        assertThat(element.getStrokeWidth(), is(2.0));
    }

    private static <T> T parse(String fragment, Class<T> type) throws Exception {
        SvgGraphic svg = parseDocument("""
            <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
              <defs>
            %s
              </defs>
            </svg>
            """.formatted(fragment));
        return svg.getElementIndex().resolve("#c")
            .filter(type::isInstance)
            .map(type::cast)
            .orElseThrow(() -> new AssertionError("no " + type.getSimpleName() + " with id \"c\" in the parsed document"));
    }

    private static SvgGraphic parseDocument(String document) throws Exception {
        return new FoxgloveParser().parse(new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8)));
    }

}
