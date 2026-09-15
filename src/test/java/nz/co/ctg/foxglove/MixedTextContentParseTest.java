package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.text.SvgText;
import nz.co.ctg.foxglove.text.SvgTextPath;
import nz.co.ctg.foxglove.text.SvgTextReference;
import nz.co.ctg.foxglove.text.SvgTextSpan;

/**
 * Exercises mixed text content against a document that has been through the parser, rather than one assembled in memory, following {@link StyleElementParseTest}'s convention. This
 * is the core regression #1 fixes: before it, a {@code <tspan>}, {@code <tref>} or {@code <textPath>} nested inside a {@code <tspan>} or {@code <textPath>} was silently dropped,
 * since those elements only supported a single flat string via {@code @XmlValue}.
 */
public class MixedTextContentParseTest {

    private SvgElementIndex index;

    @BeforeEach
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/mixed-text-content.svg"));
        assertThat(svg, notNullValue());
        index = SvgElementIndex.of(svg);
    }

    @Test
    public void testPlainTextHasNoDataLoss() throws Exception {
        assertThat(resolve("plain-text", SvgText.class).getValue(), is("Hello world"));
    }

    @Test
    public void testTextPreservesInterleavedChildElements() throws Exception {
        List<Object> content = resolve("mixed-text", SvgText.class).getContent();
        assertThat(content.size(), is(7));
        assertThat(content.get(0), is("Hello "));
        assertThat(content.get(1), instanceOf(SvgTextSpan.class));
        assertThat(content.get(2), is(" and "));
        assertThat(content.get(3), instanceOf(SvgTextReference.class));
        assertThat(content.get(4), is(" and "));
        assertThat(content.get(5), instanceOf(SvgTextPath.class));
        assertThat(content.get(6), is(" end"));
    }

    @Test
    public void testTspanPreservesItsOwnNestedTspan() throws Exception {
        List<Object> content = resolve("bold-span", SvgTextSpan.class).getContent();
        assertThat(content.size(), is(3));
        assertThat(content.get(0), is("bold "));
        assertThat(content.get(1), instanceOf(SvgTextSpan.class));
        assertThat(content.get(2), is(" tail"));
        assertThat(((SvgTextSpan) content.get(1)).getId(), is("inner-span"));
    }

    @Test
    public void testDeeplyNestedTspanKeepsItsOwnText() throws Exception {
        assertThat(resolve("inner-span", SvgTextSpan.class).getValue(), is("inner"));
    }

    @Test
    public void testTextPathPreservesItsOwnNestedTspan() throws Exception {
        List<Object> content = resolve("path-span", SvgTextPath.class).getContent();
        assertThat(content.size(), is(2));
        assertThat(content.get(0), is("path text "));
        assertThat(content.get(1), instanceOf(SvgTextSpan.class));
        assertThat(((SvgTextSpan) content.get(1)).getId(), is("path-nested-span"));
    }

    private <T extends ISvgElement> T resolve(String id, Class<T> type) {
        Optional<T> resolved = index.resolve("#" + id, type);
        assertThat("no element with id " + id, resolved.isPresent(), is(true));
        return resolved.get();
    }

}
