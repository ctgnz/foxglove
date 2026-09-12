package nz.co.ctg.foxglove.style;

import java.util.List;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.SvgStyle;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class CssStylesheetTest {

    private static SvgStyle style(String css) {
        SvgStyle style = new SvgStyle();
        style.setValue(css);
        return style;
    }

    @Test
    public void testNoStyleElementsYieldsAnEmptyStylesheet() throws Exception {
        assertThat(CssStylesheet.of(List.of()).matchingDeclarations(new SvgRectangle()), is(empty()));
    }

    @Test
    public void testASimpleRuleMatchesByType() throws Exception {
        CssStylesheet sheet = CssStylesheet.of(List.of(style("rect { fill: red; }")));
        assertThat(sheet.matchingDeclarations(new SvgRectangle()), is(List.of(new CssDeclaration("fill", "red", false))));
    }

    @Test
    public void testANonMatchingRuleContributesNothing() throws Exception {
        CssStylesheet sheet = CssStylesheet.of(List.of(style("g { fill: red; }")));
        assertThat(sheet.matchingDeclarations(new SvgRectangle()), is(empty()));
    }

    @Test
    public void testCommaGroupedSelectorsShareOneDeclarationBlock() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setClassName("warning");
        CssStylesheet sheet = CssStylesheet.of(List.of(style("g, .warning { stroke: blue; }")));
        assertThat(sheet.matchingDeclarations(rect), is(List.of(new CssDeclaration("stroke", "blue", false))));
    }

    @Test
    public void testCommentsAreStripped() throws Exception {
        CssStylesheet sheet = CssStylesheet.of(List.of(style("/* a comment { with braces } */ rect { fill: red; }")));
        assertThat(sheet.matchingDeclarations(new SvgRectangle()), is(List.of(new CssDeclaration("fill", "red", false))));
    }

    @Test
    public void testMultipleStyleElementsAreConcatenated() throws Exception {
        CssStylesheet sheet = CssStylesheet.of(List.of(style("rect { fill: red; }"), style("rect { stroke: blue; }")));
        assertThat(sheet.matchingDeclarations(new SvgRectangle()),
            is(List.of(new CssDeclaration("fill", "red", false), new CssDeclaration("stroke", "blue", false))));
    }

    @Test
    public void testATypeOtherThanTextCssIsIgnored() throws Exception {
        SvgStyle other = style("rect { fill: red; }");
        other.setType("text/plain");
        assertThat(CssStylesheet.of(List.of(other)).matchingDeclarations(new SvgRectangle()), is(empty()));
    }

    @Test
    public void testBlankTypeIsTreatedAsTextCss() throws Exception {
        SvgStyle blank = style("rect { fill: red; }");
        assertThat(CssStylesheet.of(List.of(blank)).matchingDeclarations(new SvgRectangle()), is(List.of(new CssDeclaration("fill", "red", false))));
    }

    @Test
    public void testDeclarationsAreOrderedByAscendingSpecificityThenDocumentOrder() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setId("title");
        rect.setClassName("warning");
        // declared highest specificity first; the result must still come back low-to-high so a later apply wins
        CssStylesheet sheet = CssStylesheet.of(List.of(style("#title { fill: red; } .warning { fill: green; } rect { fill: blue; }")));
        assertThat(sheet.matchingDeclarations(rect), is(List.of(
            new CssDeclaration("fill", "blue", false),
            new CssDeclaration("fill", "green", false),
            new CssDeclaration("fill", "red", false))));
    }

    @Test
    public void testEqualSpecificityBreaksTiesByDocumentOrder() throws Exception {
        CssStylesheet sheet = CssStylesheet.of(List.of(style("rect { fill: red; } rect { fill: blue; }")));
        assertThat(sheet.matchingDeclarations(new SvgRectangle()),
            is(List.of(new CssDeclaration("fill", "red", false), new CssDeclaration("fill", "blue", false))));
    }

}
