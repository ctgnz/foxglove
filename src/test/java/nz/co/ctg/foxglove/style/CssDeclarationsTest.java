package nz.co.ctg.foxglove.style;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CssDeclarationsTest {

    @Test
    public void testBlankOrNullYieldsNoDeclarations() throws Exception {
        assertThat(CssDeclarations.parse(null), is(empty()));
        assertThat(CssDeclarations.parse("   "), is(empty()));
    }

    @Test
    public void testSplitsOnSemicolonThenFirstColon() throws Exception {
        List<CssDeclaration> declarations = CssDeclarations.parse("fill:red;stroke:blue");
        assertThat(declarations, contains(new CssDeclaration("fill", "red", false), new CssDeclaration("stroke", "blue", false)));
    }

    @Test
    public void testTakesOnlyTheFirstColonSoAValueMayContainMore() throws Exception {
        List<CssDeclaration> declarations = CssDeclarations.parse("font-family:Courier:New");
        assertThat(declarations, contains(new CssDeclaration("font-family", "Courier:New", false)));
    }

    @Test
    public void testToleratesWhitespaceAndLowercasesThePropertyOnly() throws Exception {
        List<CssDeclaration> declarations = CssDeclarations.parse("  FILL : Red  ");
        assertThat(declarations, contains(new CssDeclaration("fill", "Red", false)));
    }

    @Test
    public void testImportantIsDetectedAndStripped() throws Exception {
        List<CssDeclaration> declarations = CssDeclarations.parse("fill: red !important");
        assertThat(declarations, contains(new CssDeclaration("fill", "red", true)));
    }

    @Test
    public void testImportantIsCaseInsensitive() throws Exception {
        List<CssDeclaration> declarations = CssDeclarations.parse("fill: red !IMPORTANT");
        assertThat(declarations, contains(new CssDeclaration("fill", "red", true)));
    }

    @Test
    public void testDeclarationWithNoColonIsIgnored() throws Exception {
        assertThat(CssDeclarations.parse("fill"), is(empty()));
    }

    @Test
    public void testDeclarationWithNoValueIsIgnored() throws Exception {
        assertThat(CssDeclarations.parse("fill:"), is(empty()));
    }

    @Test
    public void testEmptyDeclarationsBetweenSemicolonsAreIgnored() throws Exception {
        List<CssDeclaration> declarations = CssDeclarations.parse("fill:red;;stroke:blue; ;");
        assertThat(declarations, contains(new CssDeclaration("fill", "red", false), new CssDeclaration("stroke", "blue", false)));
    }

}
