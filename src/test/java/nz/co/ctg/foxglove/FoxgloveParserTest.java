package nz.co.ctg.foxglove;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nz.co.ctg.foxglove.element.SvgAnchor;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.paint.SvgPattern;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPolyline;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Transform;

public class FoxgloveParserTest {

    private FoxgloveParser parser;

    @BeforeClass
    public static void initJFX() {
        Platform.startup(() -> {});
    }

    @Before
    public void setup() throws Exception {
        parser = new FoxgloveParser();
    }

    @Test
    public void testParse() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/test.svg"));
        assertThat(svg, notNullValue());
        assertThat(svg.isExternalResourcesRequired(), is(true));
        assertThat(svg.getViewBox().getWidth().pixels(), is(612.0));
        assertThat(svg.getViewBox().getHeight().pixels(), is(792.0));
        printElement(svg);
        svg.getContent().forEach(this::printElement);
        assertThat(svg.getMetadata().isPresent(), is(true));
    }

    @Test
    public void testParseTransform() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/test.svg"));
        SvgGroup group = (SvgGroup) svg.getContent().get(6);
        List<Transform> transformList = group.getTransformList();
        assertThat(transformList, hasSize(1));
    }

    @Test
    public void testParseLine() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/test.svg"));
        SvgGroup group = (SvgGroup) svg.getContent().get(4);
        SvgLine line = (SvgLine) group.getContent().get(2);
        printElement(line);
        Line fxLine = line.createGraphic();
        assertThat(fxLine.getStartX(), closeTo(202.5, 0.05));
        assertThat(fxLine.getStartY(), closeTo(345.7, 0.05));
        assertThat(fxLine.getEndX(), closeTo(407.5, 0.05));
        assertThat(fxLine.getEndY(), closeTo(345.7, 0.05));
        assertThat(fxLine.getStroke(), is(Color.BLUE));
        assertThat(fxLine.getStrokeWidth(), is(2.0));
    }

    @Test
    public void testParseRectangle() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/test.svg"));
        SvgGroup group = (SvgGroup) svg.getContent().get(4);
        SvgRectangle rect = (SvgRectangle) group.getContent().get(5);
        printElement(rect);
        Rectangle fxRect = rect.createGraphic();
        assertThat(fxRect.getX(), closeTo(441.47, 0.05));
        assertThat(fxRect.getY(), closeTo(248.11, 0.05));
        assertThat(fxRect.getWidth(), closeTo(168.23, 0.05));
        assertThat(fxRect.getHeight(), closeTo(97.59, 0.05));
        assertThat(fxRect.getStroke(), is(Color.BLUE));
        assertThat(fxRect.getStrokeWidth(), is(1.99172));
    }

    @Test
    public void testParsePolys() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/27110306.svg"));
        assertThat(svg, notNullValue());
        SvgGroup group = (SvgGroup) svg.getContent().get(1);
        group = (SvgGroup) group.getContent().get(0);
        SvgPolyline polyline = (SvgPolyline) group.getContent().get(3);
        printElement(polyline);
        Polyline fxPolyline = polyline.createGraphic();
        assertThat(fxPolyline.getPoints(), hasSize(6));
    }

    @Test
    public void testParseEnums() throws Exception {
        SvgGraphic svg = parser.parse(SvgGraphic.class.getResourceAsStream("/131.svg"));
        assertThat(svg, notNullValue());
        SvgGroup group = (SvgGroup) svg.getContent().get(0);
        printElement(group);
        group = (SvgGroup) group.getContent().get(0);
        printElement(group);
        SvgLine line = (SvgLine) group.getContent().get(0);
        printElement(line);
        Line fxLine = line.createGraphic();
        assertThat(fxLine.getStrokeLineCap(), is(StrokeLineCap.ROUND));
        assertThat(fxLine.getStrokeLineJoin(), is(StrokeLineJoin.ROUND));
        assertThat(fxLine.getStroke(), is(Color.BLACK));
        assertThat(fxLine.getStrokeWidth(), is(5.0));
        assertThat(fxLine.getStrokeMiterLimit(), is(10.0));
    }

    @Test
    public void testWrite() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.setRequiredFeatures("foo bar");
        svg.setRequiredExtensions("bar qux");
        svg.setSystemLanguage("qux");
        svg.setExternalResourcesRequired(true);
        svg.setOnFocusIn("foo");
        svg.setOnFocusOut("bar");
        SvgGroup group = new SvgGroup();
        group.setExternalResourcesRequired(true);
        group.setId("g1");
        SvgGroup subGroup = new SvgGroup();
        subGroup.setId("g2");
        subGroup.setTransform("rotate(1.23456)");
        SvgAnchor anchor = new SvgAnchor();
        anchor.setId("a1");
        anchor.setXlinkHref("foobar");
        subGroup.getContent().add(anchor);
        SvgPattern pattern = new SvgPattern();
        pattern.setXlinkHref("barqux");
        subGroup.getContent().add(pattern);
        group.getContent().add(subGroup);
        svg.getContent().add(group);
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
