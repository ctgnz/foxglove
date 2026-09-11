package nz.co.ctg.foxglove;

import org.junit.Before;
import org.junit.Test;

import nz.co.ctg.foxglove.element.SvgImage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

/**
 * {@code SvgImage}'s binding was missing the {@code x}/{@code y}/{@code width}/{@code height}
 * ({@link nz.co.ctg.foxglove.ISvgBounded}) mappings entirely, along with the {@code style}/{@code class}/
 * presentation/graphics/text attribute boilerplate every other structural element declares - the same class of bug
 * fixed for {@code <use>} in #19 (see {@link SvgBoundedBindingTest}), just more of it. Parsing a document was
 * silently leaving every one of these {@code null} regardless of what the XML said, including {@code display},
 * which controls whether the element renders at all.
 */
public class SvgImageBindingTest {

    private SvgImage image;

    @Before
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/xlink-attributes.svg"));
        assertThat(svg, notNullValue());
        image = svg.getContent().stream()
            .filter(SvgImage.class::isInstance)
            .map(SvgImage.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError("no <image> parsed"));
    }

    @Test
    public void testXYWidthHeightBindFromXml() throws Exception {
        assertThat(image.getPixelsX(), closeTo(5, 1e-9));
        assertThat(image.getPixelsY(), closeTo(6, 1e-9));
        assertThat(image.getPixelsWidth(), closeTo(10, 1e-9));
        assertThat(image.getPixelsHeight(), closeTo(10, 1e-9));
    }

    @Test
    public void testDisplayBindsFromXml() throws Exception {
        assertThat(image.getDisplay(), is("none"));
        assertThat(image.isVisible(), is(false));
    }

}
