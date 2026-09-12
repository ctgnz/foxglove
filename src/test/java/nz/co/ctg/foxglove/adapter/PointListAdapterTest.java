package nz.co.ctg.foxglove.adapter;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import javafx.geometry.Point2D;

/**
 * Exercises #94's fix: {@code points} is a flat "list of numbers" (SVG's own grammar term), not a list of
 * comma-joined "x,y" pairs - every case here is a real value pulled from the W3C conformance suite (#44) that
 * crashed or silently dropped points under the previous whitespace-then-single-comma-split implementation.
 */
public class PointListAdapterTest {

    private final PointListAdapter adapter = new PointListAdapter();

    @Test
    public void testSpaceSeparatedPairsWithNoCommasAtAll() throws Exception {
        // animate-elem-37-t / painting-control-03-f
        List<Point2D> points = adapter.unmarshal("-30 20 -30 -20 30 20 30 -20");
        assertThat(points, hasSize(4));
        assertThat(points.get(0), is(new Point2D(-30, 20)));
        assertThat(points.get(3), is(new Point2D(30, -20)));
    }

    @Test
    public void testCommaWithATrailingSpaceBeforeTheNextNumber() throws Exception {
        // filters-displace-02-f - the previous implementation's whitespace-split left a dangling "280," token
        List<Point2D> points = adapter.unmarshal("280, 40, 280,120");
        assertThat(points, hasSize(2));
        assertThat(points.get(0), is(new Point2D(280, 40)));
        assertThat(points.get(1), is(new Point2D(280, 120)));
    }

    @Test
    public void testEveryNumberCommaJoinedWithNoWhitespaceAtAll() throws Exception {
        // shapes-grammar-01-f - the previous implementation treated this as ONE token and silently kept only the
        // first pair
        List<Point2D> points = adapter.unmarshal("179,-185,218,-203,228,-245,202,-279,159,-280,131,-247,139,-205");
        assertThat(points, hasSize(7));
        assertThat(points.get(0), is(new Point2D(179, -185)));
        assertThat(points.get(6), is(new Point2D(139, -205)));
    }

    @Test
    public void testANegativeSignAloneSeparatesTwoNumbersWithNoOtherDelimiter() throws Exception {
        // shapes-grammar-01-f's second (deliberately-tricky) variant - "179-185" is the two numbers 179 and -185
        List<Point2D> points = adapter.unmarshal("179-185,218-203");
        assertThat(points, hasSize(2));
        assertThat(points.get(0), is(new Point2D(179, -185)));
        assertThat(points.get(1), is(new Point2D(218, -203)));
    }

    @Test
    public void testAnOddTrailingNumberWithNoPairPartnerIsDropped() throws Exception {
        // shapes-polygon-03-t
        List<Point2D> points = adapter.unmarshal("80,200 80,300 150,250 80,200 250");
        assertThat(points, hasSize(4));
        assertThat(points.get(3), is(new Point2D(80, 200)));
    }

    @Test
    public void testMixedCommaAndSpaceSeparatedPairsInOneList() throws Exception {
        // struct-use-01-t
        List<Point2D> points = adapter.unmarshal("0,0 20,0 20,20 0,20 0 0");
        assertThat(points, hasSize(5));
        assertThat(points.get(4), is(new Point2D(0, 0)));
    }

    @Test
    public void testBlankOrNullValueIsAnEmptyListNotNull() throws Exception {
        assertThat(adapter.unmarshal(""), hasSize(0));
        assertThat(adapter.unmarshal(null), hasSize(0));
        assertThat(adapter.unmarshal("   "), hasSize(0));
    }

}
