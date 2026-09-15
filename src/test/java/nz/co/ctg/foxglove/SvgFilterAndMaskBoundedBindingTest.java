package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.clip.SvgMask;
import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.filter.SvgFilter;

/**
 * {@code x}/{@code y}/{@code width}/{@code height} on {@code <filter>} and {@code <mask>} are bound through the OXM virtual accessors, the same mechanism
 * {@link SvgBoundedBindingTest} covers for {@code <use>} - and were missing from the binding entirely (no {@code <java-type>} entry for either class at all), which left both
 * silently defaulting regardless of what a document declared, even though {@link ISvgBounded} parsed them fine from a document built in memory. Found while researching #26; a
 * real, already-shipped bug for {@code <mask>} (#25).
 */
public class SvgFilterAndMaskBoundedBindingTest {

    @Test
    public void testFilterXYWidthHeightBindFromXml() throws Exception {
        SvgFilter filter = parseFilter("<filter id=\"f\" x=\"1\" y=\"2\" width=\"3\" height=\"4\"/>");
        assertThat(filter.getPixelsX(), closeTo(1, 1e-9));
        assertThat(filter.getPixelsY(), closeTo(2, 1e-9));
        assertThat(filter.getPixelsWidth(), closeTo(3, 1e-9));
        assertThat(filter.getPixelsHeight(), closeTo(4, 1e-9));
    }

    @Test
    public void testMaskXYWidthHeightBindFromXml() throws Exception {
        SvgMask mask = parseMask("<mask id=\"m\" x=\"1\" y=\"2\" width=\"3\" height=\"4\"/>");
        assertThat(mask.getPixelsX(), closeTo(1, 1e-9));
        assertThat(mask.getPixelsY(), closeTo(2, 1e-9));
        assertThat(mask.getPixelsWidth(), closeTo(3, 1e-9));
        assertThat(mask.getPixelsHeight(), closeTo(4, 1e-9));
    }

    private static SvgFilter parseFilter(String filterXml) throws Exception {
        SvgDefinitions defs = parseDefs(filterXml);
        return defs.getContent()
            .stream()
            .filter(SvgFilter.class::isInstance)
            .map(SvgFilter.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError("no <filter> parsed"));
    }

    private static SvgMask parseMask(String maskXml) throws Exception {
        SvgDefinitions defs = parseDefs(maskXml);
        return defs.getContent()
            .stream()
            .filter(SvgMask.class::isInstance)
            .map(SvgMask.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError("no <mask> parsed"));
    }

    private static SvgDefinitions parseDefs(String innerXml) throws Exception {
        String xml = "<svg xmlns=\"http://www.w3.org/2000/svg\"><defs>" + innerXml + "</defs></svg>";
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        assertThat(svg, notNullValue());
        return (SvgDefinitions) svg.getContent()
            .get(0);
    }

}
