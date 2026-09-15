package nz.co.ctg.foxglove.element;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.Locale;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * Exercises #22's acceptance criteria: {@code <switch>} renders only the first direct child whose conditional processing attributes all pass, a standalone conditional element is
 * suppressed by an ordinary container the same way {@code display:none} already is, and neither ever throws.
 */
public class SvgSwitchRenderingTest {

    private static SvgRectangle rect(String id) {
        SvgRectangle rect = new SvgRectangle();
        rect.setId(id);
        return rect;
    }

    @Test
    public void testSwitchRendersOnlyTheFirstPassingChild() throws Exception {
        SvgRectangle failing = rect("failing");
        failing.setRequiredFeatures("http://www.w3.org/TR/SVG11/feature#Animation");
        SvgRectangle passing = rect("passing");
        SvgRectangle later = rect("later");

        SvgSwitch svgSwitch = new SvgSwitch();
        svgSwitch.getContent()
            .add(failing);
        svgSwitch.getContent()
            .add(passing);
        svgSwitch.getContent()
            .add(later);

        Group rendered = render(svgSwitch);
        assertThat(rendered.getChildren(), hasSize(1));
        assertThat(rendered.getChildren()
            .get(0)
            .getId(), is("passing"));
    }

    @Test
    public void testSwitchSkipsAChildHiddenByDisplayNone() throws Exception {
        SvgRectangle hidden = rect("hidden");
        hidden.setDisplay("none");
        SvgRectangle visible = rect("visible");

        SvgSwitch svgSwitch = new SvgSwitch();
        svgSwitch.getContent()
            .add(hidden);
        svgSwitch.getContent()
            .add(visible);

        Group rendered = render(svgSwitch);
        assertThat(rendered.getChildren(), hasSize(1));
        assertThat(rendered.getChildren()
            .get(0)
            .getId(), is("visible"));
    }

    @Test
    public void testSwitchWithNoPassingChildRendersEmptyWithoutThrowing() throws Exception {
        SvgRectangle failing = rect("failing");
        failing.setRequiredFeatures("http://www.w3.org/TR/SVG11/feature#Animation");

        SvgSwitch svgSwitch = new SvgSwitch();
        svgSwitch.getContent()
            .add(failing);

        assertThat(render(svgSwitch).getChildren(), is(empty()));
    }

    @Test
    public void testSwitchWithNoChildrenRendersEmptyWithoutThrowing() throws Exception {
        assertThat(render(new SvgSwitch()).getChildren(), is(empty()));
    }

    @Test
    public void testStandaloneElementWithFailingConditionIsSuppressedByItsContainer() throws Exception {
        SvgRectangle failing = rect("failing");
        failing.setSystemLanguage("fr");
        SvgRectangle passing = rect("passing");

        SvgGroup group = new SvgGroup();
        group.getContent()
            .add(failing);
        group.getContent()
            .add(passing);

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(group);
        Group rendered = svg.createGroup(Locale.forLanguageTag("en-NZ"));
        Group renderedGroup = (Group) rendered.getChildren()
            .get(0);
        assertThat(renderedGroup.getChildren(), hasSize(1));
        assertThat(((Rectangle) renderedGroup.getChildren()
            .get(0)).getId(), is("passing"));
    }

    private static Group render(SvgSwitch svgSwitch) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(svgSwitch);
        return (Group) svg.createGroup()
            .getChildren()
            .get(0);
    }

}
