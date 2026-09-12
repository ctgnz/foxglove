package nz.co.ctg.foxglove.animate;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * {@code SvgSetAttribute} used to extend {@code AbstractSvgElement} directly instead of
 * {@code AbstractSvgAnimationElement}, so it never gained {@code to}, the timing attributes, {@code xlink:href} or
 * the conditional processing attributes - a silent data-loss bug on the parsing side, independent of whether
 * {@code <set>} is actually rendered yet (#33, part of #10).
 */
public class SvgSetAttributeTest {

    @Test
    public void testExtendsAbstractSvgAnimationElement() {
        // a regression test for the inheritance itself - #33 called out exactly this as worth pinning down so it
        // cannot silently regress, since every sibling animation element already extends this class correctly
        assertThat(new SvgSetAttribute(), instanceOf(AbstractSvgAnimationElement.class));
        assertThat(new SvgSetAttribute(), instanceOf(ISvgAnimationElement.class));
        assertThat(new SvgSetAttribute(), instanceOf(ISvgConditionalFeatures.class));
        assertThat(new SvgSetAttribute(), instanceOf(ISvgLinkable.class));
        assertThat(new SvgSetAttribute(), instanceOf(ISvgExternalResources.class));
    }

    @Test
    public void testToAndTimingAttributesRoundTripThroughParseAndWrite() throws Exception {
        String xml = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
            + "<rect x=\"0\" y=\"0\" width=\"10\" height=\"10\">"
            + "<set attributeName=\"fill\" to=\"red\" begin=\"2s\" dur=\"3s\" fill=\"freeze\" xlink:href=\"#other\"/>"
            + "</rect></svg>";
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        SvgRectangle rect = (SvgRectangle) svg.getContent().get(0);
        SvgSetAttribute set = (SvgSetAttribute) rect.getContent().get(0);

        assertThat(set.getAttributeName(), is("fill"));
        assertThat(set.getTo(), is("red"));
        assertThat(set.getBegin(), is("2s"));
        assertThat(set.getDuration(), is("3s"));
        assertThat(set.getFill(), is("freeze"));
        assertThat(set.getXlinkHref(), is("#other"));

        String written = parser.write(svg, Boolean.FALSE);
        SvgGraphic roundTripped = parser.parse(new ByteArrayInputStream(written.getBytes(StandardCharsets.UTF_8)));
        SvgRectangle roundTrippedRect = (SvgRectangle) roundTripped.getContent().get(0);
        SvgSetAttribute roundTrippedSet = (SvgSetAttribute) roundTrippedRect.getContent().get(0);

        assertThat(roundTrippedSet.getTo(), is("red"));
        assertThat(roundTrippedSet.getBegin(), is("2s"));
        assertThat(roundTrippedSet.getDuration(), is("3s"));
        assertThat(roundTrippedSet.getFill(), is("freeze"));
        assertThat(roundTrippedSet.getXlinkHref(), is("#other"));
    }

}
