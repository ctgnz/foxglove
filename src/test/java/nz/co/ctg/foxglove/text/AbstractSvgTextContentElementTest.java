package nz.co.ctg.foxglove.text;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * Exercises the mixed-content flattening shared by {@link SvgText}, {@link SvgTextSpan} and {@link SvgTextPath}
 * (#1), via {@link SvgTextSpan} as the simplest concrete subclass.
 */
public class AbstractSvgTextContentElementTest {

    @Test
    public void testValueOfEmptyContentIsEmptyString() throws Exception {
        SvgTextSpan span = new SvgTextSpan();
        assertThat(span.getValue(), is(""));
        assertThat(span.getTextContent(), hasSize(0));
    }

    @Test
    public void testValueOfPureTextJoinsTheRuns() throws Exception {
        SvgTextSpan span = new SvgTextSpan();
        span.getContent().add("Hello ");
        span.getContent().add("world");
        assertThat(span.getValue(), is("Hello  world"));
    }

    @Test
    public void testValueOfMixedContentFallsBackToTheFirstChildElement() throws Exception {
        SvgTextSpan child = new SvgTextSpan();
        child.getContent().add("inner");

        SvgTextSpan span = new SvgTextSpan();
        span.getContent().add("Hello ");
        span.getContent().add(child);
        span.getContent().add(" tail");

        assertThat(span.getValue(), is("inner"));
        assertThat(span.getTextContent(), hasSize(1));
        assertThat(span.getTextContent().get(0), is(child));
    }

}
