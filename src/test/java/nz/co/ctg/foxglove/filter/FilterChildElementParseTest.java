package nz.co.ctg.foxglove.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.SvgGraphic;

/**
 * Regression cover for #105: {@code <feMergeNode>} and {@code <feFuncR>}/{@code G}/{@code B}/{@code A} were silently dropped by the parser, so {@code feMerge} and
 * {@code feComponentTransfer} were no-ops on any real document.
 * <p>
 * The cause is easy to reintroduce and impossible to see by reading the class alone: this package's {@code elementFormDefault} is {@code UNQUALIFIED}, so a child-element binding
 * that does not name the SVG namespace explicitly matches an element in <i>no</i> namespace instead - which nothing in a real document ever is. Only a test that goes through the
 * parser catches it; one that assembles the same objects in memory passes either way, which is exactly why this went unnoticed through #26 and #76.
 */
public class FilterChildElementParseTest {

    @Test
    public void testFeMergeNodeChildrenBindWhenParsed() throws Exception {
        SvgFilter filter = filterOf("""
                        <filter id="f">
                          <feGaussianBlur stdDeviation="3" result="blurred"/>
                          <feMerge>
                            <feMergeNode in="blurred"/>
                            <feMergeNode in="SourceGraphic"/>
                          </feMerge>
                        </filter>
                        """);

        FeMerge merge = (FeMerge) filter.getContent()
            .get(1);
        assertThat(merge.getFeMergeNode(), hasSize(2));
        assertThat(merge.getFeMergeNode()
            .get(0)
            .getIn(), is("blurred"));
        assertThat(merge.getFeMergeNode()
            .get(1)
            .getIn(), is("SourceGraphic"));
    }

    @Test
    public void testFeFuncChildrenBindWhenParsed() throws Exception {
        SvgFilter filter = filterOf("""
                        <filter id="f">
                          <feComponentTransfer>
                            <feFuncR type="linear" slope="0.5" intercept="0.25"/>
                            <feFuncG type="table" tableValues="0 1"/>
                            <feFuncB type="discrete" tableValues="0 0.5 1"/>
                            <feFuncA type="gamma" amplitude="2" exponent="3"/>
                          </feComponentTransfer>
                        </filter>
                        """);

        FeComponentTransfer transfer = (FeComponentTransfer) filter.getContent()
            .get(0);
        assertThat(transfer.getFeFuncR(), notNullValue());
        assertThat(transfer.getFeFuncR()
            .getType(), is("linear"));
        assertThat(transfer.getFeFuncR()
            .getSlope(), is("0.5"));
        assertThat(transfer.getFeFuncG(), notNullValue());
        assertThat(transfer.getFeFuncG()
            .getTableValues(), is("0 1"));
        assertThat(transfer.getFeFuncB(), notNullValue());
        assertThat(transfer.getFeFuncB()
            .getType(), is("discrete"));
        assertThat(transfer.getFeFuncA(), notNullValue());
        assertThat(transfer.getFeFuncA()
            .getExponent(), is("3"));
    }

    /** Parses a self-contained document wrapping {@code filterMarkup} and returns its one {@code <filter>}. */
    private static SvgFilter filterOf(String filterMarkup) throws Exception {
        String document = """
                        <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                          <defs>
                        %s
                          </defs>
                        </svg>
                        """.formatted(filterMarkup);
        SvgGraphic svg = new FoxgloveParser().parse(new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8)));
        return svg.getElementIndex()
            .resolve("#f")
            .filter(SvgFilter.class::isInstance)
            .map(SvgFilter.class::cast)
            .orElseThrow(() -> new AssertionError("no <filter id=\"f\"> in the parsed document"));
    }

}
