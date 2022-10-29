package nz.co.ctg.foxglove;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.parser.FoxgloveParser;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPolyline;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import jakarta.xml.bind.JAXBException;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Transform;

public class FoxgloveParserTest {

    private FoxgloveParser parser;

    @Before
    public void setup() throws JAXBException {
        parser = new FoxgloveParser();
    }

    @Test
    public void testParse() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/test.svg"));
        assertThat(svg, notNullValue());
        printElement(svg);
        svg.getContent().forEach(this::printElement);
    }

    @Test
    public void testParseTransform() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/test.svg"));
        SvgGroup group = (SvgGroup) svg.getContent().get(4);
        List<Transform> transformList = group.getTransformList();
        assertThat(transformList, hasSize(1));
    }

    @Test
    public void testParseLine() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/test.svg"));
        SvgGroup group = (SvgGroup) svg.getContent().get(2);
        SvgLine line = (SvgLine) group.getContent().get(1);
        printElement(line);
        Line fxLine = line.createShape();
        assertThat(fxLine.getStartX(), closeTo(202.5, 0.05));
        assertThat(fxLine.getStartY(), closeTo(345.7, 0.05));
        assertThat(fxLine.getEndX(), closeTo(407.5, 0.05));
        assertThat(fxLine.getEndY(), closeTo(345.7, 0.05));
        assertThat(fxLine.getStroke(), is(Color.BLUE));
        assertThat(fxLine.getStrokeWidth(), is(2.0));
    }

    @Test
    public void testParsePolys() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/27110306.svg"));
        assertThat(svg, notNullValue());
        SvgGroup group = (SvgGroup) svg.getContent().get(1);
        group = (SvgGroup) group.getContent().get(0);
        SvgPolyline polyline = (SvgPolyline) group.getContent().get(3);
        printElement(polyline);
        Polyline fxPolyline = polyline.createShape();
        assertThat(fxPolyline.getPoints(), hasSize(6));
    }

    @Test
    public void testParseEnums() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/131.svg"));
        assertThat(svg, notNullValue());
        SvgGroup group = (SvgGroup) svg.getContent().get(0);
        group = (SvgGroup) group.getContent().get(0);
        SvgLine line = (SvgLine) group.getContent().get(0);
        printElement(line);
        Line fxLine = line.createShape();
        assertThat(fxLine.getStrokeLineCap(), is(StrokeLineCap.ROUND));
        assertThat(fxLine.getStrokeLineJoin(), is(StrokeLineJoin.ROUND));
        assertThat(fxLine.getStroke(), is(Color.BLACK));
        assertThat(fxLine.getStrokeWidth(), is(5.0));
        assertThat(fxLine.getStrokeMiterLimit(), is(10.0));
    }

    @Test
    public void testWrite() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(new SvgGroup());
        System.out.println(parser.write(svg));
    }

    private void printElement(Object el) {
        System.out.println(el);
        if (el instanceof SvgGroup) {
            SvgGroup group = (SvgGroup) el;
            group.getContent().forEach(this::printElement);
        }
    }

}
