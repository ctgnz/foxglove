package nz.co.ctg.foxglove;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.RenderContext.Axis;
import nz.co.ctg.foxglove.RenderContext.UnitsMode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;

public class RenderContextTest {

    private static final double DELTA = 1e-9;

    private RenderContext viewport(double width, double height) {
        return RenderContext.root(null, width, height);
    }

    @Test
    public void testNullSizeResolvesToZero() throws Exception {
        assertThat(viewport(200, 100).resolveLength(null, Axis.HORIZONTAL), is(0.0));
    }

    @Test
    public void testAbsoluteLengthIgnoresTheViewport() throws Exception {
        assertThat(viewport(200, 100).resolveLength(new Size(42, SizeUnits.PX), Axis.HORIZONTAL), closeTo(42, DELTA));
    }

    @Test
    public void testPercentResolvesAgainstViewportWidth() throws Exception {
        assertThat(viewport(200, 100).resolveLength(new Size(50, SizeUnits.PERCENT), Axis.HORIZONTAL), closeTo(100, DELTA));
    }

    @Test
    public void testPercentResolvesAgainstViewportHeight() throws Exception {
        assertThat(viewport(200, 100).resolveLength(new Size(50, SizeUnits.PERCENT), Axis.VERTICAL), closeTo(50, DELTA));
    }

    @Test
    public void testPercentResolvesAgainstTheViewportDiagonalPerSvg17Point10() throws Exception {
        // sqrt((w^2 + h^2) / 2), per SVG 1.1 7.10
        double expectedReference = Math.sqrt((300.0 * 300 + 400.0 * 400) / 2.0);
        assertThat(viewport(300, 400).resolveLength(new Size(100, SizeUnits.PERCENT), Axis.DIAGONAL), closeTo(expectedReference, DELTA));
    }

    @Test
    public void testWithViewportReplacesTheReferenceLengths() throws Exception {
        RenderContext nested = viewport(200, 100).withViewport(1000, 1000);
        assertThat(nested.resolveLength(new Size(10, SizeUnits.PERCENT), Axis.HORIZONTAL), closeTo(100, DELTA));
        assertThat(nested.getViewportWidth(), is(1000.0));
        assertThat(nested.getViewportHeight(), is(1000.0));
    }

    @Test
    public void testObjectBoundingBoxIsAbsentUntilEstablished() throws Exception {
        assertThat(viewport(200, 100).getObjectBoundingBox().isPresent(), is(false));
    }

    @Test
    public void testWithObjectBoundingBoxCarriesItForward() throws Exception {
        javafx.geometry.Bounds bbox = new javafx.geometry.BoundingBox(1, 2, 3, 4);
        RenderContext withBbox = viewport(200, 100).withObjectBoundingBox(bbox);
        assertThat(withBbox.getObjectBoundingBox().get(), is(bbox));
    }

    @Test
    public void testParseUnitsRecognisesUserSpaceOnUse() throws Exception {
        assertThat(RenderContext.parseUnits("userSpaceOnUse", UnitsMode.OBJECT_BOUNDING_BOX), is(UnitsMode.USER_SPACE_ON_USE));
    }

    @Test
    public void testParseUnitsRecognisesObjectBoundingBox() throws Exception {
        assertThat(RenderContext.parseUnits("objectBoundingBox", UnitsMode.USER_SPACE_ON_USE), is(UnitsMode.OBJECT_BOUNDING_BOX));
    }

    @Test
    public void testParseUnitsFallsBackToTheDefaultOnBlankOrUnrecognisedInput() throws Exception {
        assertThat(RenderContext.parseUnits(null, UnitsMode.OBJECT_BOUNDING_BOX), is(UnitsMode.OBJECT_BOUNDING_BOX));
        assertThat(RenderContext.parseUnits("nonsense", UnitsMode.USER_SPACE_ON_USE), is(UnitsMode.USER_SPACE_ON_USE));
    }

    @Test
    public void testResolveChildKeepsTheViewportAndAddsTheElementsStyle() throws Exception {
        SvgGraphic element = new SvgGraphic();
        element.setColor("red");
        RenderContext child = viewport(200, 100).resolveChild(element);
        assertThat(child.getViewportWidth(), is(200.0));
        assertThat(child.getColor(), is("red"));
    }

    @Test
    public void testRootHasNoInheritedProperties() throws Exception {
        assertThat(viewport(1, 1).getFill(), is(nullValue()));
    }

    // --- resolveFraction (objectBoundingBox coordinates) --------------------

    @Test
    public void testResolveFractionOfNullIsZero() throws Exception {
        assertThat(RenderContext.resolveFraction(null), is(0.0));
    }

    @Test
    public void testResolveFractionOfABareNumberIsTheNumberItself() throws Exception {
        assertThat(RenderContext.resolveFraction(new Size(0.25, SizeUnits.PX)), closeTo(0.25, DELTA));
    }

    @Test
    public void testResolveFractionOfAPercentDividesByOneHundred() throws Exception {
        assertThat(RenderContext.resolveFraction(new Size(25, SizeUnits.PERCENT)), closeTo(0.25, DELTA));
    }

}
