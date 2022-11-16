package nz.co.ctg.foxglove.adapter;

import org.junit.Test;

import nz.co.ctg.foxglove.type.ViewBox;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

public class ViewBoxAdapterTest {

    private ViewBoxAdapter candidate = new ViewBoxAdapter();

    @Test
    public void testUnmarshalNoUnitsAsPixels() throws Exception {
        ViewBox viewBox = candidate.unmarshal("0 0 800 600");
        assertThat(viewBox.getMinX().pixels(), is(0.0));
        assertThat(viewBox.getMinY().pixels(), is(0.0));
        assertThat(viewBox.getWidth().pixels(), is(800.0));
        assertThat(viewBox.getHeight().pixels(), is(600.0));
    }

    @Test
    public void testUnmarshalPixels() throws Exception {
        ViewBox viewBox = candidate.unmarshal("0px 0px 800px 600px");
        assertThat(viewBox.getMinX().pixels(), is(0.0));
        assertThat(viewBox.getMinY().pixels(), is(0.0));
        assertThat(viewBox.getWidth().pixels(), is(800.0));
        assertThat(viewBox.getHeight().pixels(), is(600.0));
    }

    @Test
    public void testUnmarshalPixelsCommaSeparator() throws Exception {
        ViewBox viewBox = candidate.unmarshal("0px, 0px, 800px, 600px");
        assertThat(viewBox.getMinX().pixels(), is(0.0));
        assertThat(viewBox.getMinY().pixels(), is(0.0));
        assertThat(viewBox.getWidth().pixels(), is(800.0));
        assertThat(viewBox.getHeight().pixels(), is(600.0));
    }

    @Test
    public void testUnmarshalMillimetres() throws Exception {
        ViewBox viewBox = candidate.unmarshal("0mm 0mm 800mm 600mm");
        assertThat(viewBox.getMinX().pixels(), is(0.0));
        assertThat(viewBox.getMinY().pixels(), is(0.0));
        assertThat(viewBox.getWidth().pixels(), closeTo(3023.6, 0.05));
        assertThat(viewBox.getHeight().pixels(), closeTo(2267.7, 0.05));
    }

}
