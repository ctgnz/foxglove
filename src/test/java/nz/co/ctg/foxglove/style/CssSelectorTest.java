package nz.co.ctg.foxglove.style;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.SvgRectangle;

public class CssSelectorTest {

    @Test
    public void testTypeSelectorMatchesItsElementName() throws Exception {
        assertThat(CssSelector.parse("rect")
            .matches(new SvgRectangle()), is(true));
        assertThat(CssSelector.parse("rect")
            .matches(new SvgGroup()), is(false));
    }

    @Test
    public void testUniversalSelectorMatchesAnyElement() throws Exception {
        assertThat(CssSelector.parse("*")
            .matches(new SvgRectangle()), is(true));
        assertThat(CssSelector.parse("*")
            .matches(new SvgGroup()), is(true));
    }

    @Test
    public void testClassSelectorRequiresTheClassAmongPossiblySeveral() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setClassName("warning outline");
        assertThat(CssSelector.parse(".warning")
            .matches(rect), is(true));
        assertThat(CssSelector.parse(".outline")
            .matches(rect), is(true));
        assertThat(CssSelector.parse(".missing")
            .matches(rect), is(false));
    }

    @Test
    public void testClassSelectorDoesNotMatchAPrefixOfAnotherClass() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setClassName("warnings");
        assertThat(CssSelector.parse(".warning")
            .matches(rect), is(false));
    }

    @Test
    public void testIdSelectorRequiresAnExactId() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setId("title");
        assertThat(CssSelector.parse("#title")
            .matches(rect), is(true));
        assertThat(CssSelector.parse("#other")
            .matches(rect), is(false));
    }

    @Test
    public void testCompoundSelectorRequiresEveryPart() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setId("title");
        rect.setClassName("warning");
        assertThat(CssSelector.parse("rect.warning#title")
            .matches(rect), is(true));
        assertThat(CssSelector.parse("g.warning#title")
            .matches(rect), is(false));
        assertThat(CssSelector.parse("rect.other#title")
            .matches(rect), is(false));
    }

    @Test
    public void testNoSelectorMatchesAnElementWithNoClassOrId() throws Exception {
        assertThat(CssSelector.parse(".warning")
            .matches(new SvgRectangle()), is(false));
        assertThat(CssSelector.parse("#title")
            .matches(new SvgRectangle()), is(false));
    }

    @Test
    public void testSpecificityCountsIdThenClassThenType() throws Exception {
        assertThat(CssSelector.parse("*")
            .specificity(), is(0));
        assertThat(CssSelector.parse("rect")
            .specificity(), is(1));
        assertThat(CssSelector.parse(".warning")
            .specificity(), is(10));
        assertThat(CssSelector.parse("#title")
            .specificity(), is(100));
        assertThat(CssSelector.parse("rect.warning.outline#title")
            .specificity(), is(121));
    }

}
