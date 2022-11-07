package nz.co.ctg.foxglove.adapter;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.scene.text.Font;

public class SizeAdapterTest {

    private SizeAdapter candidate;

    @Before
    public void setUp() throws Exception {
        candidate = new SizeAdapter();
    }

    @Test
    public void testUnmarshalNoUnitsAsPixels() throws Exception {
        assertThat(candidate.unmarshal("120").pixels(), is(120.0));
        assertThat(candidate.unmarshal("12.345").pixels(), is(12.345));
    }

    @Test
    public void testUnmarshalPixels() throws Exception {
        assertThat(candidate.unmarshal("120px").pixels(), is(120.0));
        assertThat(candidate.unmarshal("12.345px").pixels(), is(12.345));
    }

    @Test
    public void testUnmarshalPercent() throws Exception {
        assertThat(candidate.unmarshal("120%").pixels(), is(1.2));
        assertThat(candidate.unmarshal("123.45%").pixels(), is(1.2345));
    }

    @Test
    public void testUnmarshalCentimetres() throws Exception {
        assertThat(candidate.unmarshal("12cm").pixels(), closeTo(453.54, 0.01));
        assertThat(candidate.unmarshal("12.345cm").pixels(), closeTo(466.58, 0.01));
    }

    @Test
    public void testUnmarshalMillimetres() throws Exception {
        assertThat(candidate.unmarshal("120mm").pixels(), closeTo(453.54, 0.01));
        assertThat(candidate.unmarshal("12.345mm").pixels(), closeTo(46.66, 0.01));
    }

    @Test
    public void testUnmarshalInches() throws Exception {
        assertThat(candidate.unmarshal("12in").pixels(), is(1152.0));
        assertThat(candidate.unmarshal("12.345in").pixels(), closeTo(1185.12, 0.01));
    }

    @Test
    public void testUnmarshalEms() throws Exception {
        assertThat(candidate.unmarshal("12em").pixels(Font.font("SansSerif")), is(144.0));
        assertThat(candidate.unmarshal("1.2em").pixels(Font.font("SansSerif")), is(14.4));
    }

    @Test
    public void testUnmarshalExs() throws Exception {
        assertThat(candidate.unmarshal("12ex").pixels(Font.font("SansSerif")), is(72.0));
        assertThat(candidate.unmarshal("1.2ex").pixels(Font.font("SansSerif")), is(7.2));
    }

    @Test
    public void testUnmarshalPoints() throws Exception {
        assertThat(candidate.unmarshal("120pt").pixels(), is(160.0));
        assertThat(candidate.unmarshal("12.345pt").pixels(), closeTo(16.46, 0.01));
    }

    @Test
    public void testUnmarshalPicas() throws Exception {
        assertThat(candidate.unmarshal("12pc").pixels(), is(192.0));
        assertThat(candidate.unmarshal("12.345pc").pixels(), is(197.52));
    }

    @Test
    public void testMarshalNoUnitsAsPixels() throws Exception {
        assertThat(candidate.marshal(new Size(120, null)), is("120.0px"));
        assertThat(candidate.marshal(new Size(12.345, null)), is("12.345px"));
    }

    @Test
    public void testMarshalPixels() throws Exception {
        assertThat(candidate.marshal(new Size(120, SizeUnits.PX)), is("120.0px"));
        assertThat(candidate.marshal(new Size(12.345, SizeUnits.PX)), is("12.345px"));
    }

    @Test
    public void testMarshalPercent() throws Exception {
        assertThat(candidate.marshal(new Size(120, SizeUnits.PERCENT)), is("120.0%"));
        assertThat(candidate.marshal(new Size(12.345, SizeUnits.PERCENT)), is("12.345%"));
    }

    @Test
    public void testMarshalCentimetres() throws Exception {
        assertThat(candidate.marshal(new Size(12, SizeUnits.CM)), is("12.0cm"));
        assertThat(candidate.marshal(new Size(12.345, SizeUnits.CM)), is("12.345cm"));
    }

    @Test
    public void testMarshalMillimetres() throws Exception {
        assertThat(candidate.marshal(new Size(120, SizeUnits.MM)), is("120.0mm"));
        assertThat(candidate.marshal(new Size(12.345, SizeUnits.MM)), is("12.345mm"));
    }

    @Test
    public void testMarshalInches() throws Exception {
        assertThat(candidate.marshal(new Size(12, SizeUnits.IN)), is("12.0in"));
        assertThat(candidate.marshal(new Size(12.345, SizeUnits.IN)), is("12.345in"));
    }

    @Test
    public void testMarshalEms() throws Exception {
        assertThat(candidate.marshal(new Size(120, SizeUnits.EM)), is("120.0em"));
        assertThat(candidate.marshal(new Size(12.345, SizeUnits.EM)), is("12.345em"));
    }

    @Test
    public void testMarshalExs() throws Exception {
        assertThat(candidate.marshal(new Size(120, SizeUnits.EX)), is("120.0ex"));
        assertThat(candidate.marshal(new Size(12.345, SizeUnits.EX)), is("12.345ex"));
    }

    @Test
    public void testMarshalPoints() throws Exception {
        assertThat(candidate.marshal(new Size(12, SizeUnits.PT)), is("12.0pt"));
        assertThat(candidate.marshal(new Size(12.345, SizeUnits.PT)), is("12.345pt"));
    }

    @Test
    public void testMarshalPicas() throws Exception {
        assertThat(candidate.marshal(new Size(120, SizeUnits.PC)), is("120.0pc"));
        assertThat(candidate.marshal(new Size(12.345, SizeUnits.PC)), is("12.345pc"));
    }

}
