package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.Group;
import javafx.scene.text.Text;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.text.SvgText;

/**
 * The font properties are inherited, so they have to be resolved against the ancestors rather than read from the text element alone.
 */
public class ISvgTextAttributesTest {

    @Test
    public void testTextInheritsFontPropertiesFromItsGroup() throws Exception {
        SvgText text = new SvgText();
        text.getContent()
            .add("Hello");
        SvgGroup group = new SvgGroup();
        group.setFontFamily("Serif");
        group.setFontSize("24");
        group.getContent()
            .add(text);

        Text rendered = renderText(group);
        assertThat(rendered.getFont()
            .getSize(), closeTo(24.0, 1e-9));
        assertThat(rendered.getFont()
            .getFamily(), is("Serif"));
    }

    @Test
    public void testTextOverridesTheInheritedFontSize() throws Exception {
        SvgText text = new SvgText();
        text.getContent()
            .add("Hello");
        text.setFontSize("10");
        SvgGroup group = new SvgGroup();
        group.setFontSize("24");
        group.getContent()
            .add(text);

        assertThat(renderText(group).getFont()
            .getSize(), closeTo(10.0, 1e-9));
    }

    @Test
    public void testUnstyledTextGetsTheInitialFontSizeRatherThanZero() throws Exception {
        SvgText text = new SvgText();
        text.getContent()
            .add("Hello");
        SvgGroup group = new SvgGroup();
        group.getContent()
            .add(text);

        Text rendered = renderText(group);
        assertThat(rendered.getFont()
            .getSize(), closeTo(ISvgTextAttributes.INITIAL_FONT_SIZE.pixels(), 1e-9));
        assertThat(rendered.getBoundsInLocal()
            .getWidth() > 0, is(true));
    }

    @Test
    public void testTextInheritsFillFromItsGroup() throws Exception {
        SvgText text = new SvgText();
        text.getContent()
            .add("Hello");
        SvgGroup group = new SvgGroup();
        group.setFill(javafx.scene.paint.Color.RED);
        group.getContent()
            .add(text);

        assertThat(renderText(group).getFill(), is(javafx.scene.paint.Color.RED));
    }

    private static Text renderText(SvgGroup group) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(group);
        return (Text) ((Group) svg.createGroup()
            .getChildren()
            .get(0)).getChildren()
            .get(0);
    }

}
