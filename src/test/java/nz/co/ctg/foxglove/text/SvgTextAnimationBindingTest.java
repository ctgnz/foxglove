package nz.co.ctg.foxglove.text;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.number.OrderingComparison.greaterThan;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.AnimatedGraphic;
import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.animate.ISvgAnimationElement;

/**
 * #215: {@code SvgText}/{@code SvgTextSpan}/{@code SvgTextPath} never declared the SMIL animation elements as valid mixed content, so JAXB silently dropped them during parsing -
 * confirmed directly against the real W3C {@code animate-elem-24-t.svg}, whose only animation elements are nested inside its {@code <text>}.
 */
public class SvgTextAnimationBindingTest {

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @Test
    public void testAnimationElementsNestedDirectlyInTextAreParsedAndAnimated() throws Exception {
        SvgGraphic svg = parse("<text x=\"0\" y=\"20\">Moving"
                               + "<animateMotion dur=\"4s\" path=\"M0,0 L10,0\"/>"
                               + "<animateTransform attributeName=\"transform\" type=\"rotate\" dur=\"4s\" from=\"0\" to=\"360\"/>"
                               + "</text>");

        assertThat(svg.getElementIndex()
            .getElementsOfType(ISvgAnimationElement.class), is(not(empty())));
        assertThat(animate(svg).animations()
            .size(), is(greaterThan(0)));
    }

    /**
     * A {@code tspan} isn't itself independently registered in the node registry (only the root {@code <text>} is - a {@code tspan} is just one styled run within it, see
     * {@code TextRunBuilder}), so an animation implicitly targeting its {@code tspan} parent still can't be built - a separate, deeper gap than #215 diagnosed, not attempted here.
     * What #215's fix does guarantee is that an animation element is no longer silently dropped by JAXB merely for being nested inside a {@code tspan} - confirmed here via an
     * explicit {@code xlink:href} target, which resolves independently of the implicit-parent path.
     */
    @Test
    public void testAnAnimationNestedInATspanIsParsedAndAnimatesItsHrefTarget() throws Exception {
        SvgGraphic svg = parse("<rect id=\"target\" x=\"0\" y=\"0\" width=\"10\" height=\"10\"/>"
                               + "<text x=\"0\" y=\"20\"><tspan>Moving"
                               + "<animateTransform xlink:href=\"#target\" attributeName=\"transform\" type=\"rotate\" dur=\"4s\" from=\"0\" to=\"360\"/>"
                               + "</tspan></text>");

        assertThat(animate(svg).animations()
            .size(), is(greaterThan(0)));
    }

    /**
     * Same reasoning as the {@code tspan} case above, for {@code textPath}.
     */
    @Test
    public void testAnAnimationNestedInATextPathIsParsedAndAnimatesItsHrefTarget() throws Exception {
        SvgGraphic svg = parse("<defs><path id=\"p\" d=\"M0,0 L100,0\"/></defs>"
                               + "<rect id=\"target\" x=\"0\" y=\"0\" width=\"10\" height=\"10\"/>"
                               + "<text><textPath xlink:href=\"#p\">Moving"
                               + "<animateTransform xlink:href=\"#target\" attributeName=\"transform\" type=\"rotate\" dur=\"4s\" from=\"0\" to=\"360\"/>"
                               + "</textPath></text>");

        assertThat(animate(svg).animations()
            .size(), is(greaterThan(0)));
    }

    private static AnimatedGraphic animate(SvgGraphic svg) throws Exception {
        return onFxThread(() -> svg.createAnimatedGraphic(RenderContext.root(svg.getElementIndex(), 100, 100)));
    }

    private static SvgGraphic parse(String bodyXml) throws Exception {
        String xml = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" + bodyXml + "</svg>";
        FoxgloveParser parser = new FoxgloveParser();
        return parser.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

}
