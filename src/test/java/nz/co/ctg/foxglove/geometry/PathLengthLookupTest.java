package nz.co.ctg.foxglove.geometry;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.geometry.Point2D;

public class PathLengthLookupTest {

    @Test
    public void testTotalLengthOfAStraightLine() throws Exception {
        PathLengthLookup lookup = PathLengthLookup.of(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(10, 10)));
        assertThat(lookup.getTotalLength(), closeTo(20.0, 1e-9));
    }

    @Test
    public void testPointAtInterpolatesWithinASegment() throws Exception {
        PathLengthLookup lookup = PathLengthLookup.of(List.of(new Point2D(0, 0), new Point2D(10, 0)));
        Point2D point = lookup.pointAt(2.5);
        assertThat(point.getX(), closeTo(2.5, 1e-9));
        assertThat(point.getY(), closeTo(0, 1e-9));
    }

    @Test
    public void testPointAtCrossesIntoTheNextSegment() throws Exception {
        PathLengthLookup lookup = PathLengthLookup.of(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(10, 10)));
        Point2D point = lookup.pointAt(15.0);
        assertThat(point.getX(), closeTo(10, 1e-9));
        assertThat(point.getY(), closeTo(5, 1e-9));
    }

    @Test
    public void testLengthClampsToTheTotal() throws Exception {
        PathLengthLookup lookup = PathLengthLookup.of(List.of(new Point2D(0, 0), new Point2D(10, 0)));
        assertThat(lookup.pointAt(-5.0), is(new Point2D(0, 0)));
        assertThat(lookup.pointAt(1000.0), is(new Point2D(10, 0)));
    }

    @Test
    public void testAngleAtMatchesTheSegmentDirection() throws Exception {
        PathLengthLookup lookup = PathLengthLookup.of(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(10, 10)));
        assertThat(lookup.angleAt(5.0), closeTo(0.0, 1e-9));
        assertThat(lookup.angleAt(15.0), closeTo(90.0, 1e-9));
    }

    @Test
    public void testDegenerateSinglePointHasZeroLength() throws Exception {
        PathLengthLookup lookup = PathLengthLookup.of(List.of(new Point2D(5, 5)));
        assertThat(lookup.getTotalLength(), is(0.0));
        assertThat(lookup.pointAt(0.0), is(new Point2D(5, 5)));
    }

    @Test
    public void testEmptyOrNullPointsIsTreatedAsTheOrigin() throws Exception {
        assertThat(PathLengthLookup.of(List.of()).getTotalLength(), is(0.0));
        assertThat(PathLengthLookup.of(null).pointAt(0.0), is(Point2D.ZERO));
    }

}
