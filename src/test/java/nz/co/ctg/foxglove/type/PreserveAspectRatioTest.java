package nz.co.ctg.foxglove.type;

import org.junit.Test;

import nz.co.ctg.foxglove.type.PreserveAspectRatio.Align;
import nz.co.ctg.foxglove.type.PreserveAspectRatio.MeetOrSlice;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PreserveAspectRatioTest {

    @Test
    public void testBlankFallsBackToTheDefault() throws Exception {
        assertDefault(PreserveAspectRatio.parse(null));
        assertDefault(PreserveAspectRatio.parse(""));
        assertDefault(PreserveAspectRatio.parse("   "));
    }

    @Test
    public void testUnrecognisedAlignFallsBackToTheDefault() throws Exception {
        assertDefault(PreserveAspectRatio.parse("xSomethingYWeird meet"));
    }

    @Test
    public void testNoneDisablesUniformScaling() throws Exception {
        PreserveAspectRatio value = PreserveAspectRatio.parse("none");
        assertThat(value.getAlign(), is(Align.NONE));
        assertThat(value.getMeetOrSlice(), is(MeetOrSlice.MEET));
    }

    @Test
    public void testAlignWithoutAMeetOrSliceTokenDefaultsToMeet() throws Exception {
        PreserveAspectRatio value = PreserveAspectRatio.parse("xMaxYMin");
        assertThat(value.getAlign(), is(Align.X_MAX_Y_MIN));
        assertThat(value.getMeetOrSlice(), is(MeetOrSlice.MEET));
    }

    @Test
    public void testSlice() throws Exception {
        PreserveAspectRatio value = PreserveAspectRatio.parse("xMidYMax slice");
        assertThat(value.getAlign(), is(Align.X_MID_Y_MAX));
        assertThat(value.getMeetOrSlice(), is(MeetOrSlice.SLICE));
    }

    @Test
    public void testALeadingDeferTokenIsIgnored() throws Exception {
        PreserveAspectRatio value = PreserveAspectRatio.parse("defer xMinYMax slice");
        assertThat(value.getAlign(), is(Align.X_MIN_Y_MAX));
        assertThat(value.getMeetOrSlice(), is(MeetOrSlice.SLICE));
    }

    @Test
    public void testEveryAlignKeywordParses() throws Exception {
        assertThat(PreserveAspectRatio.parse("xMinYMin").getAlign(), is(Align.X_MIN_Y_MIN));
        assertThat(PreserveAspectRatio.parse("xMidYMin").getAlign(), is(Align.X_MID_Y_MIN));
        assertThat(PreserveAspectRatio.parse("xMaxYMin").getAlign(), is(Align.X_MAX_Y_MIN));
        assertThat(PreserveAspectRatio.parse("xMinYMid").getAlign(), is(Align.X_MIN_Y_MID));
        assertThat(PreserveAspectRatio.parse("xMidYMid").getAlign(), is(Align.X_MID_Y_MID));
        assertThat(PreserveAspectRatio.parse("xMaxYMid").getAlign(), is(Align.X_MAX_Y_MID));
        assertThat(PreserveAspectRatio.parse("xMinYMax").getAlign(), is(Align.X_MIN_Y_MAX));
        assertThat(PreserveAspectRatio.parse("xMidYMax").getAlign(), is(Align.X_MID_Y_MAX));
        assertThat(PreserveAspectRatio.parse("xMaxYMax").getAlign(), is(Align.X_MAX_Y_MAX));
    }

    private void assertDefault(PreserveAspectRatio value) {
        assertThat(value.getAlign(), is(Align.X_MID_Y_MID));
        assertThat(value.getMeetOrSlice(), is(MeetOrSlice.MEET));
    }

}
