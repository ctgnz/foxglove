package nz.co.ctg.foxglove.type;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.scene.transform.Affine;

public class ViewBoxTest {

    private static final double DELTA = 1e-9;

    private static Size px(double value) {
        return new Size(value, SizeUnits.PX);
    }

    private static ViewBox viewBox(double minX, double minY, double width, double height) {
        return new ViewBox(px(minX), px(minY), px(width), px(height));
    }

    @Test
    public void testNonPositiveViewBoxHasNoTransform() throws Exception {
        assertThat(viewBox(0, 0, 0, 40).createTransform(50, 30, PreserveAspectRatio.parse(null)), is(nullValue()));
    }

    @Test
    public void testNoneScalesEachAxisIndependently() throws Exception {
        // mirrors transform/stretch-to-fit.svg: 300x200 viewport, viewBox 0 0 1500 1000, preserveAspectRatio="none"
        Affine transform = (Affine) viewBox(0, 0, 1500, 1000).createTransform(300, 200, PreserveAspectRatio.parse("none"));
        assertThat(transform.getMxx(), closeTo(0.2, DELTA));
        assertThat(transform.getMyy(), closeTo(0.2, DELTA));
        assertThat(transform.getTx(), closeTo(0, DELTA));
        assertThat(transform.getTy(), closeTo(0, DELTA));
    }

    @Test
    public void testMeetPicksTheSmallerScale() throws Exception {
        Affine transform = (Affine) viewBox(0, 0, 30, 40).createTransform(50, 30, PreserveAspectRatio.parse("xMinYMin meet"));
        assertThat(transform.getMxx(), closeTo(0.75, DELTA));
        assertThat(transform.getMyy(), closeTo(0.75, DELTA));
    }

    @Test
    public void testSlicePicksTheLargerScale() throws Exception {
        Affine transform = (Affine) viewBox(0, 0, 30, 40).createTransform(50, 30, PreserveAspectRatio.parse("xMinYMin slice"));
        assertThat(transform.getMxx(), closeTo(50.0 / 30, DELTA));
        assertThat(transform.getMyy(), closeTo(50.0 / 30, DELTA));
    }

    @Test
    public void testXMidYMidCentersTheLeftoverSpace() throws Exception {
        Affine transform = (Affine) viewBox(0, 0, 30, 40).createTransform(50, 30, PreserveAspectRatio.parse("xMidYMid meet"));
        // scale is min(50/30, 30/40) = 0.75; leftover horizontal space is 50 - 30*0.75 = 27.5, halved
        assertThat(transform.getTx(), closeTo(13.75, DELTA));
        assertThat(transform.getTy(), closeTo(0, DELTA));
    }

    @Test
    public void testXMaxYMaxPushesTheLeftoverSpaceToTheFarEdge() throws Exception {
        Affine transform = (Affine) viewBox(0, 0, 30, 40).createTransform(50, 30, PreserveAspectRatio.parse("xMaxYMax meet"));
        assertThat(transform.getTx(), closeTo(27.5, DELTA));
        assertThat(transform.getTy(), closeTo(0, DELTA));
    }

    @Test
    public void testNonZeroMinIsFoldedIntoTheTranslation() throws Exception {
        Affine transform = (Affine) viewBox(10, 20, 100, 100).createTransform(100, 100, PreserveAspectRatio.parse("xMinYMin meet"));
        assertThat(transform.getMxx(), closeTo(1, DELTA));
        assertThat(transform.getTx(), closeTo(-10, DELTA));
        assertThat(transform.getTy(), closeTo(-20, DELTA));
    }

}
