package nz.co.ctg.foxglove;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.element.SvgUse;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

/**
 * {@code x}/{@code y}/{@code width}/{@code height} on {@code <use>} are bound through the OXM virtual accessors, the
 * same mechanism {@link SvgLinkableBindingTest} covers for {@code xlink:href} - and were missing from the binding
 * entirely, which left every {@code <use>} silently un-positioned even though {@link nz.co.ctg.foxglove.ISvgBounded}
 * parsed them fine from a document built in memory.
 */
public class SvgBoundedBindingTest {

    private SvgUse use;

    @BeforeEach
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/xlink-attributes.svg"));
        assertThat(svg, notNullValue());
        use = svg.getContent().stream()
            .filter(SvgUse.class::isInstance)
            .map(SvgUse.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError("no <use> parsed"));
    }

    @Test
    public void testXYWidthHeightBindFromXml() throws Exception {
        assertThat(use.getPixelsX(), closeTo(1, 1e-9));
        assertThat(use.getPixelsY(), closeTo(2, 1e-9));
        assertThat(use.getPixelsWidth(), closeTo(3, 1e-9));
        assertThat(use.getPixelsHeight(), closeTo(4, 1e-9));
    }

}
