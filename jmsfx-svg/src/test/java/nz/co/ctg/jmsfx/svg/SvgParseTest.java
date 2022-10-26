package nz.co.ctg.jmsfx.svg;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import nz.co.ctg.jmsfx.svg.document.FXVGGroup;
import nz.co.ctg.jmsfx.svg.shape.FXVGLine;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class SvgParseTest {

    private FXVGParser parser;

    @BeforeClass
    public static void prep() {
        System.setProperty("javax.xml.accessExternalDTD", "all");
    }

    @Before
    public void setup() throws JAXBException {
        parser = new FXVGParser();
    }

    @Test
    public void testParse() throws Exception {
        FXVGSvgElement svg = parser.parse(FXVGSvgElement.class.getResourceAsStream("/test.svg"));
        assertThat(svg, notNullValue());
        svg.getContent().forEach(el -> {
            printElement(el);
        });
        FXVGGroup group = (FXVGGroup) svg.getContent().get(2);
        FXVGLine line = (FXVGLine) group.getContent().get(1);
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
    public void testParseEnums() throws Exception {
        FXVGSvgElement svg = parser.parse(FXVGSvgElement.class.getResourceAsStream("/131.svg"));
        assertThat(svg, notNullValue());
        svg.getContent().forEach(el -> {
            printElement(el);
        });
        FXVGGroup group = (FXVGGroup) svg.getContent().get(0);
        group = (FXVGGroup) group.getContent().get(0);
        FXVGLine line = (FXVGLine) group.getContent().get(0);
        printElement(line);
        Line fxLine = line.createShape();
        assertThat(fxLine.getStrokeLineCap(), is(StrokeLineCap.ROUND));
        assertThat(fxLine.getStrokeLineJoin(), is(StrokeLineJoin.ROUND));
        assertThat(fxLine.getStroke(), is(Color.BLACK));
        assertThat(fxLine.getStrokeWidth(), is(5.0));
        assertThat(fxLine.getStrokeMiterLimit(), is(10.0));
    }

    private void printElement(Object el) {
        System.out.println(el);
        if (el instanceof FXVGGroup) {
            ((FXVGGroup) el).getContent().forEach(this::printElement);
        }
    }

    @Test @Ignore
    public void testWrite() throws Exception {
        JAXBContext context = JAXBContext.newInstance(FXVGSvgElement.class);
        Marshaller m = context.createMarshaller();
        StringWriter out = new StringWriter();
        FXVGSvgElement fXVGSvgElement = new FXVGSvgElement();
        fXVGSvgElement.getContent().add(new FXVGGroup());
        m.marshal(fXVGSvgElement, out);
        System.out.println(out);
    }

}
