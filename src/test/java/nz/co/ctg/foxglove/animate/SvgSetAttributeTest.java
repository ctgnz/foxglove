package nz.co.ctg.foxglove.animate;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * {@code SvgSetAttribute} used to extend {@code AbstractSvgElement} directly instead of {@code AbstractSvgAnimationElement}, so it never gained {@code to}, the timing attributes,
 * {@code xlink:href} or the conditional processing attributes - a silent data-loss bug on the parsing side, independent of whether {@code <set>} is actually rendered yet (#33,
 * part of #10).
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
        SvgRectangle rect = (SvgRectangle) svg.getContent()
            .get(0);
        SvgSetAttribute set = (SvgSetAttribute) rect.getContent()
            .get(0);

        assertThat(set.getAttributeName(), is("fill"));
        assertThat(set.getTo(), is("red"));
        assertThat(set.getBegin(), is("2s"));
        assertThat(set.getDuration(), is("3s"));
        assertThat(set.getFill(), is("freeze"));
        assertThat(set.getXlinkHref(), is("#other"));

        String written = parser.write(svg, Boolean.FALSE);
        SvgGraphic roundTripped = parser.parse(new ByteArrayInputStream(written.getBytes(StandardCharsets.UTF_8)));
        SvgRectangle roundTrippedRect = (SvgRectangle) roundTripped.getContent()
            .get(0);
        SvgSetAttribute roundTrippedSet = (SvgSetAttribute) roundTrippedRect.getContent()
            .get(0);

        assertThat(roundTrippedSet.getTo(), is("red"));
        assertThat(roundTrippedSet.getBegin(), is("2s"));
        assertThat(roundTrippedSet.getDuration(), is("3s"));
        assertThat(roundTrippedSet.getFill(), is("freeze"));
        assertThat(roundTrippedSet.getXlinkHref(), is("#other"));
    }

    // --- rendering -----------------------------------------------------------

    @Test
    public void testAppliesToValueImmediatelyAndFreezeHoldsIt() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        rect.setX(5);
        SvgSetAttribute set = set(s -> {
            s.setAttributeName("x");
            s.setTo("20");
            s.setFill("freeze");
        });

        Timeline timeline = build(set, rect);
        List<KeyFrame> frames = timeline.getKeyFrames();
        // freeze needs only the "set" KeyFrame - a JavaFX Animation already holds its last value once finished
        assertThat(frames.size(), is(1));
        assertThat(frames.get(0)
            .getTime(), is(javafx.util.Duration.ZERO));
        assertThat(doubleValue(frames.get(0)), closeTo(20.0, 1e-9));
    }

    @Test
    public void testFillRemoveRevertsToThePreAnimationValueAtTheEndOfDur() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        rect.setX(5);
        SvgSetAttribute set = set(s -> {
            s.setAttributeName("x");
            s.setTo("20");
            s.setDuration("3s");
            s.setFill("remove");
        });

        Timeline timeline = build(set, rect);
        List<KeyFrame> frames = timeline.getKeyFrames();
        assertThat(frames.size(), is(2));
        assertThat(frames.get(0)
            .getTime(), is(javafx.util.Duration.ZERO));
        assertThat(doubleValue(frames.get(0)), closeTo(20.0, 1e-9));
        assertThat(frames.get(1)
            .getTime(), is(javafx.util.Duration.seconds(3)));
        assertThat(doubleValue(frames.get(1)), closeTo(5.0, 1e-9));
    }

    @Test
    public void testFillDefaultsToRemove() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        rect.setX(5);
        SvgSetAttribute set = set(s -> {
            s.setAttributeName("x");
            s.setTo("20");
            s.setDuration("1s");
        });

        assertThat(build(set, rect).getKeyFrames()
            .size(), is(2));
    }

    @Test
    public void testMissingToIsUnsupported() {
        SvgSetAttribute set = set(s -> s.setAttributeName("x"));
        assertThat(set.buildAnimation(new Rectangle(), null)
            .isEmpty(), is(true));
    }

    @Test
    public void testUnmappableAttributeNameIsUnsupported() {
        SvgSetAttribute set = set(s -> {
            s.setAttributeName("not-a-real-attribute");
            s.setTo("20");
        });
        assertThat(set.buildAnimation(new Rectangle(), null)
            .isEmpty(), is(true));
    }

    @Test
    public void testAValueThatFailsToParseIsUnsupported() {
        SvgSetAttribute set = set(s -> {
            s.setAttributeName("fill");
            s.setTo("not-a-colour");
        });
        assertThat(set.buildAnimation(new Rectangle(), null)
            .isEmpty(), is(true));
    }

    // --- helpers -----------------------------------------------------------

    private static SvgSetAttribute set(java.util.function.Consumer<SvgSetAttribute> configure) {
        SvgSetAttribute element = new SvgSetAttribute();
        configure.accept(element);
        return element;
    }

    private static Timeline build(SvgSetAttribute set, Rectangle target) {
        Optional<Animation> result = set.buildAnimation(target, null);
        return (Timeline) result.orElseThrow();
    }

    private static double doubleValue(KeyFrame frame) {
        return ((Number) frame.getValues()
            .iterator()
            .next()
            .getEndValue()).doubleValue();
    }

}
