package nz.co.ctg.foxglove.animate;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * {@code dur} and {@code repeatDur} never parsed on any animation element - {@code foxglove-animate.xml} declared
 * {@code java-attribute="duration"}/{@code "repeatDuration"} (matching the getter method names), but
 * {@link ISvgAnimationElement#getDuration()}/{@link ISvgAnimationElement#getRepeatDuration()} read from the map
 * keys {@code "dur"}/{@code "repeatDur"} instead - a real value parsed under the wrong key, confirmed empirically
 * via the raw property map before this fix (`{duration=3s, ...}`, `getDuration()` returning {@code null} regardless).
 * Found while researching #33/#10; unrelated to either, so fixed and tested separately.
 */
public class AnimationTimingBindingTest {

    @Test
    public void testDurAndRepeatDurParseOnAnimate() throws Exception {
        SvgAnimateAttribute anim = parseFirstAnimation("<animate attributeName=\"fill\" dur=\"3s\" repeatDur=\"10s\"/>", SvgAnimateAttribute.class);
        assertThat(anim.getDuration(), is("3s"));
        assertThat(anim.getRepeatDuration(), is("10s"));
    }

    @Test
    public void testDurAndRepeatDurParseOnAnimateColor() throws Exception {
        SvgAnimateColor anim = parseFirstAnimation("<animateColor attributeName=\"fill\" dur=\"3s\" repeatDur=\"10s\"/>", SvgAnimateColor.class);
        assertThat(anim.getDuration(), is("3s"));
        assertThat(anim.getRepeatDuration(), is("10s"));
    }

    @Test
    public void testDurAndRepeatDurParseOnAnimateTransform() throws Exception {
        SvgAnimateTransform anim = parseFirstAnimation("<animateTransform attributeName=\"transform\" type=\"rotate\" dur=\"3s\" repeatDur=\"10s\"/>",
            SvgAnimateTransform.class);
        assertThat(anim.getDuration(), is("3s"));
        assertThat(anim.getRepeatDuration(), is("10s"));
    }

    @Test
    public void testDurAndRepeatDurParseOnAnimateMotion() throws Exception {
        SvgAnimateMotion anim = parseFirstAnimation("<animateMotion dur=\"3s\" repeatDur=\"10s\"/>", SvgAnimateMotion.class);
        assertThat(anim.getDuration(), is("3s"));
        assertThat(anim.getRepeatDuration(), is("10s"));
    }

    private static <T> T parseFirstAnimation(String elementXml, Class<T> type) throws Exception {
        String xml = "<svg xmlns=\"http://www.w3.org/2000/svg\"><rect x=\"0\" y=\"0\" width=\"10\" height=\"10\">" + elementXml
            + "</rect></svg>";
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        SvgRectangle rect = (SvgRectangle) svg.getContent().get(0);
        return type.cast(rect.getContent().get(0));
    }

}
