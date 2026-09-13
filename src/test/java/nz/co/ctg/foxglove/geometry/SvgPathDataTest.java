package nz.co.ctg.foxglove.geometry;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.geometry.Point2D;

public class SvgPathDataTest {

    @Test
    public void testMovetoAloneProducesOnePoint() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M10,20");
        assertThat(points, hasSize(1));
        assertThat(points.get(0), is(new Point2D(10, 20)));
    }

    @Test
    public void testLinetoAbsolute() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M0,0 L10,0 L10,10");
        assertThat(points, hasSize(3));
        assertThat(points.get(1), is(new Point2D(10, 0)));
        assertThat(points.get(2), is(new Point2D(10, 10)));
    }

    @Test
    public void testLinetoRelativeAccumulates() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M0,0 l10,0 l0,10");
        assertThat(points.get(1), is(new Point2D(10, 0)));
        assertThat(points.get(2), is(new Point2D(10, 10)));
    }

    @Test
    public void testImplicitRepeatOfLineto() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M0,0 L10,0 20,0 30,0");
        assertThat(points, hasSize(4));
        assertThat(points.get(3), is(new Point2D(30, 0)));
    }

    @Test
    public void testHorizontalAndVerticalLineto() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M0,0 H10 V10");
        assertThat(points.get(1), is(new Point2D(10, 0)));
        assertThat(points.get(2), is(new Point2D(10, 10)));
    }

    @Test
    public void testSecondMovetoStopsParsing() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M0,0 L10,0 M100,100 L200,200");
        assertThat(points, hasSize(2));
        assertThat(points.get(1), is(new Point2D(10, 0)));
    }

    @Test
    public void testClosepathReturnsToStartAndStopsParsing() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M0,0 L10,0 L10,10 Z L999,999");
        assertThat(points, hasSize(4));
        assertThat(points.get(3), is(new Point2D(0, 0)));
    }

    @Test
    public void testCubicCurveEndsAtItsFinalPoint() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M0,0 C0,10 10,10 10,0");
        Point2D last = points.get(points.size() - 1);
        assertThat(last.getX(), closeTo(10, 1e-9));
        assertThat(last.getY(), closeTo(0, 1e-9));
        // the curve bulges below the chord, so its midpoint-ish sample should have y > 0
        assertThat(points.get(points.size() / 2).getY() > 0, is(true));
    }

    @Test
    public void testQuadraticCurveEndsAtItsFinalPoint() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M0,0 Q5,10 10,0");
        Point2D last = points.get(points.size() - 1);
        assertThat(last.getX(), closeTo(10, 1e-9));
        assertThat(last.getY(), closeTo(0, 1e-9));
    }

    @Test
    public void testQuarterCircleArcEndsAtItsFinalPoint() throws Exception {
        // a quarter circle of radius 10 from (10,0) to (0,10), centred at the origin
        List<Point2D> points = SvgPathData.flatten("M10,0 A10,10 0 0 1 0,10");
        Point2D last = points.get(points.size() - 1);
        assertThat(last.getX(), closeTo(0, 1e-6));
        assertThat(last.getY(), closeTo(10, 1e-6));
        // every sampled point should be ~10 units from the origin (on the circle)
        for (Point2D point : points) {
            assertThat(point.distance(Point2D.ZERO), closeTo(10, 1e-6));
        }
    }

    @Test
    public void testSmoothCubicReflectsThePreviousControlPoint() throws Exception {
        List<Point2D> points = SvgPathData.flatten("M0,0 C0,10 5,10 10,0 S20,-10 20,0");
        Point2D last = points.get(points.size() - 1);
        assertThat(last.getX(), closeTo(20, 1e-9));
        assertThat(last.getY(), closeTo(0, 1e-9));
    }

    @Test
    public void testBlankPathProducesNoPoints() throws Exception {
        assertThat(SvgPathData.flatten(""), hasSize(0));
        assertThat(SvgPathData.flatten(null), hasSize(0));
    }

    // --- subpaths() (#67) ---------------------------------------------------

    @Test
    public void testSubpathsWalksPastAFurtherMoveto() throws Exception {
        List<SvgPathData.Subpath> subpaths = SvgPathData.subpaths("M0,0 L10,0 M100,100 L200,200");
        assertThat(subpaths, hasSize(2));
        assertThat(subpaths.get(0).vertices(), hasSize(2));
        assertThat(subpaths.get(0).closed(), is(false));
        assertThat(subpaths.get(1).vertices(), hasSize(2));
        assertThat(subpaths.get(1).vertices().get(0), is(new Point2D(100, 100)));
        assertThat(subpaths.get(1).closed(), is(false));
    }

    @Test
    public void testSubpathsWalksPastAClosepath() throws Exception {
        List<SvgPathData.Subpath> subpaths = SvgPathData.subpaths("M0,0 L10,0 L10,10 Z M50,50 L60,60");
        assertThat(subpaths, hasSize(2));
        List<Point2D> first = subpaths.get(0).vertices();
        assertThat(first, hasSize(4));
        assertThat(first.get(3), is(new Point2D(0, 0)));
        assertThat(subpaths.get(0).closed(), is(true));
        assertThat(subpaths.get(1).vertices(), hasSize(2));
        assertThat(subpaths.get(1).closed(), is(false));
    }

    @Test
    public void testSubpathsOfASingleOpenPathMatchesFlatten() throws Exception {
        // no curves, so flatten()'s dense sampling and subpaths()'s real-vertices-only output should coincide
        String d = "M0,0 L10,0 L10,10";
        assertThat(SvgPathData.subpaths(d), hasSize(1));
        assertThat(SvgPathData.subpaths(d).get(0).vertices(), is(SvgPathData.flatten(d)));
    }

    @Test
    public void testSubpathsOnACurveKeepsOnlyTheRealEndpoint() throws Exception {
        // flatten() densely samples the curve; subpaths() keeps only its actual endpoint
        List<Point2D> vertices = SvgPathData.subpaths("M0,0 C0,10 10,10 10,0").get(0).vertices();
        assertThat(vertices, hasSize(2));
        assertThat(vertices.get(1).getX(), closeTo(10, 1e-9));
        assertThat(vertices.get(1).getY(), closeTo(0, 1e-9));
    }

    @Test
    public void testSubpathsOfABlankPathIsEmpty() throws Exception {
        assertThat(SvgPathData.subpaths(""), hasSize(0));
        assertThat(SvgPathData.subpaths(null), hasSize(0));
    }

    // --- toJavaFxPath ------------------------------------------------------

    @Test
    public void testToJavaFxPathOnALineProducesAMoveToAndALineTo() throws Exception {
        javafx.scene.shape.Path path = SvgPathData.toJavaFxPath("M0,0 L10,0");
        assertThat(path.getElements(), hasSize(2));
        assertThat(path.getElements().get(0), org.hamcrest.CoreMatchers.instanceOf(javafx.scene.shape.MoveTo.class));
        assertThat(path.getElements().get(1), org.hamcrest.CoreMatchers.instanceOf(javafx.scene.shape.LineTo.class));
    }

    @Test
    public void testToJavaFxPathDenselySamplesACurveRatherThanAChordingToItsEndpoint() throws Exception {
        // unlike subpaths() (which keeps only the real endpoint), this needs real intermediate samples so
        // PathTransition actually follows the curve rather than a single straight chord across it
        javafx.scene.shape.Path path = SvgPathData.toJavaFxPath("M0,0 C0,10 10,10 10,0");
        assertThat(path.getElements().size() > 2, is(true));
    }

    @Test
    public void testToJavaFxPathWalksEverySubpathUnlikeFlatten() throws Exception {
        javafx.scene.shape.Path path = SvgPathData.toJavaFxPath("M0,0 L10,0 M20,20 L30,20");
        long moveTos = path.getElements().stream().filter(javafx.scene.shape.MoveTo.class::isInstance).count();
        assertThat(moveTos, is(2L));
    }

    @Test
    public void testToJavaFxPathOfABlankPathIsEmpty() throws Exception {
        assertThat(SvgPathData.toJavaFxPath("").getElements(), hasSize(0));
        assertThat(SvgPathData.toJavaFxPath(null).getElements(), hasSize(0));
    }

    // --- elliptical arc flags (#115) ----------------------------------------

    /**
     * SVG's grammar defines an arc flag as a single character, {@code flag ::= "0" | "1"}, not as a number - so a
     * separator between the two flags is optional and {@code 10} means {@code large-arc-flag=1 sweep-flag=0}.
     * Reading it as the number ten instead shifts every later argument along by one, which is how a path ending
     * {@code 25,25z} used to walk its final coordinate onto the {@code z} and throw.
     */
    @Test
    public void testArcFlagsNeedNoSeparatorBetweenThem() throws Exception {
        assertArcEquivalent("M120,120 h25 a25,25 0 10 -25,25z", "M120,120 h25 a25,25 0 1,0 -25,25 z");
    }

    /** Nor between the second flag and the coordinate pair that follows it. */
    @Test
    public void testArcFlagsNeedNoSeparatorBeforeTheCoordinatePair() throws Exception {
        assertArcEquivalent("M120,200 h25 a25,25 0 1 1-25,-25 z", "M120,200 h25 a25,25 0 1,1 -25,-25 z");
    }

    /** Both at once: {@code 1125,25} is flag 1, flag 1, then the pair 25,25. */
    @Test
    public void testArcFlagsAndCoordinatesCanAllRunTogether() throws Exception {
        assertArcEquivalent("M200,120 h-25 a25,25 0 1125,25 z", "M200,120 h-25 a25,25 0 1,1 25,25 z");
    }

    /**
     * Asserts the two forms flatten identically <i>and</i> that they actually produced an arc - without the second
     * check, two paths that both degraded to the same truncated prefix would satisfy the first one happily.
     */
    private static void assertArcEquivalent(String compact, String canonical) {
        List<Point2D> expected = SvgPathData.flatten(canonical);
        assertThat("the canonical form should itself flatten a real arc", expected.size() > 10, is(true));
        assertThat(SvgPathData.flatten(compact), is(expected));
    }

    @Test
    public void testAnArcFlagOutOfRangePutsThePathInError() throws Exception {
        // 6 is not a flag, so the path renders up to that point - the preceding M and h, and nothing of the arc
        assertThat(SvgPathData.flatten("M280,120 h25 a25,25 0 6 0 -25,25 z"),
            is(SvgPathData.flatten("M280,120 h25")));
    }

    @Test
    public void testANegativeArcFlagPutsThePathInError() throws Exception {
        assertThat(SvgPathData.flatten("M360,120 h-25 a25,25 0 1 -1 25,25 z"),
            is(SvgPathData.flatten("M360,120 h-25")));
    }

    /**
     * The separator <i>before</i> the first flag is required, unlike the ones between and after - so the arc's
     * {@code ry} and rotation greedily swallow the digits the flags needed, and the path is in error.
     */
    @Test
    public void testAMissingSeparatorBeforeTheFlagsPutsThePathInError() throws Exception {
        assertThat(SvgPathData.flatten("M200,200 h-25 a25,2501 025,-25 z"),
            is(SvgPathData.flatten("M200,200 h-25")));
    }

    // --- malformed data degrades rather than throwing (#115) ----------------

    /**
     * The crash this issue was filed for. A path whose arguments have been shifted along lands a command letter in
     * a coordinate slot; that used to reach {@code Double.parseDouble} and throw {@code NumberFormatException} out
     * of the renderer, taking the whole document with it.
     */
    @Test
    public void testACommandLetterInACoordinateSlotDoesNotThrow() throws Exception {
        assertThat(SvgPathData.flatten("M10,10 L20,20 L30,z"), is(SvgPathData.flatten("M10,10 L20,20")));
    }

    /** A path that simply runs out of arguments mid-command keeps what came before it. */
    @Test
    public void testATruncatedPathKeepsWhatCameBefore() throws Exception {
        assertThat(SvgPathData.flatten("M10,10 L20,20 L30"), is(SvgPathData.flatten("M10,10 L20,20")));
    }

    @Test
    public void testSubpathsDegradesRatherThanThrowing() throws Exception {
        List<SvgPathData.Subpath> subpaths = SvgPathData.subpaths("M0,0 L10,0 Z M20,20 L30,z");
        // the first subpath completed before the error and is kept; the broken one is not
        assertThat(subpaths, hasSize(1));
        assertThat(subpaths.get(0).closed(), is(true));
    }

    @Test
    public void testToJavaFxPathDegradesRatherThanThrowing() throws Exception {
        javafx.scene.shape.Path path = SvgPathData.toJavaFxPath("M10,10 L20,20 L30,z");
        assertThat(path.getElements(), hasSize(SvgPathData.toJavaFxPath("M10,10 L20,20").getElements().size()));
    }

}
