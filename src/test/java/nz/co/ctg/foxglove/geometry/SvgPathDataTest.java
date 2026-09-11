package nz.co.ctg.foxglove.geometry;

import java.util.List;

import org.junit.Test;

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

}
