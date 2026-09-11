package nz.co.ctg.foxglove.element;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPolygon;
import nz.co.ctg.foxglove.shape.SvgPolyline;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Exercises #21's acceptance criteria: {@code marker-end} renders an arrowhead on a {@code <line>} and a
 * {@code <polyline>}, correctly oriented; {@code marker-mid} renders at interior vertices only;
 * {@code orient="auto"} bisects correctly at a corner; {@code markerUnits="strokeWidth"} scales with stroke width.
 */
public class SvgMarkerRenderingTest {

    private static Size px(double value) {
        return new Size(value, SizeUnits.PX);
    }

    private static SvgMarker markerWithContent(String id) {
        SvgMarker marker = new SvgMarker();
        marker.setId(id);
        SvgRectangle shape = new SvgRectangle();
        shape.setWidth(px(1));
        shape.setHeight(px(1));
        marker.getContent().add(shape);
        return marker;
    }

    @Test
    public void testMarkerEndOnALineRendersOrientedAtTheEndpoint() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(markerWithContent("arrow"));

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(line);

        Group wrapper = (Group) render(root).getChildren().get(0);
        assertThat(wrapper.getChildren(), hasSize(2));
        Group markerInstance = (Group) wrapper.getChildren().get(1);
        Translate vertex = (Translate) markerInstance.getTransforms().get(0);
        assertThat(vertex.getX(), closeTo(10, 1e-9));
        assertThat(vertex.getY(), closeTo(0, 1e-9));
        Rotate rotate = (Rotate) markerInstance.getTransforms().get(1);
        assertThat(rotate.getAngle(), closeTo(0, 1e-6));
    }

    @Test
    public void testMarkerStartAndEndOnAPolylineRenderOnlyAtTheEnds() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(markerWithContent("dot"));

        SvgPolyline polyline = new SvgPolyline();
        polyline.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(20, 0)));
        polyline.setMarkerStart("url(#dot)");
        polyline.setMarkerEnd("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(polyline);

        Group wrapper = (Group) render(root).getChildren().get(0);
        // the shape plus exactly 2 markers (start and end) - none at the interior vertex
        assertThat(wrapper.getChildren(), hasSize(3));
    }

    @Test
    public void testMarkerMidRendersOnlyAtInteriorVertices() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(markerWithContent("dot"));

        SvgPolyline polyline = new SvgPolyline();
        polyline.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(10, 10), new Point2D(20, 10)));
        polyline.setMarkerMid("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(polyline);

        Group wrapper = (Group) render(root).getChildren().get(0);
        // the shape plus 2 mid markers, at the 2 interior vertices out of 4 points
        assertThat(wrapper.getChildren(), hasSize(3));
    }

    @Test
    public void testOrientAutoBisectsAtACorner() throws Exception {
        SvgMarker dot = markerWithContent("dot");
        dot.setOrient("auto");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(dot);

        SvgPolyline polyline = new SvgPolyline();
        polyline.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(10, 10)));
        polyline.setMarkerMid("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(polyline);

        Group wrapper = (Group) render(root).getChildren().get(0);
        Group markerInstance = (Group) wrapper.getChildren().get(1);
        Rotate rotate = (Rotate) markerInstance.getTransforms().get(1);
        // incoming along +x (0 deg), outgoing along +y (90 deg) - bisected halfway, at 45 deg
        assertThat(rotate.getAngle(), closeTo(45, 1e-6));
    }

    @Test
    public void testMarkerOnAPolygonUsesTheClosingEdgeForWraparoundBisection() throws Exception {
        SvgMarker dot = markerWithContent("dot");
        dot.setOrient("auto");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(dot);

        // a right triangle: (0,0) -> (10,0) -> (0,10) -> implicit close back to (0,0)
        SvgPolygon polygon = new SvgPolygon();
        polygon.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(0, 10)));
        polygon.setMarkerStart("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(polygon);

        Group wrapper = (Group) render(root).getChildren().get(0);
        Group markerInstance = (Group) wrapper.getChildren().get(1);
        Rotate rotate = (Rotate) markerInstance.getTransforms().get(1);
        // incoming via the closing edge (0,10)->(0,0): -90 deg; outgoing (0,0)->(10,0): 0 deg; bisected: -45 deg
        assertThat(rotate.getAngle(), closeTo(-45, 1e-6));
    }

    @Test
    public void testMarkerUnitsStrokeWidthScalesByTheShapesStrokeWidth() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(markerWithContent("arrow"));

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setStrokeWidth(3.0);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(line);

        Group wrapper = (Group) render(root).getChildren().get(0);
        Group markerInstance = (Group) wrapper.getChildren().get(1);
        Scale scale = (Scale) markerInstance.getTransforms().get(2);
        assertThat(scale.getX(), closeTo(3.0, 1e-9));
        assertThat(scale.getY(), closeTo(3.0, 1e-9));
    }

    @Test
    public void testMarkerUnitsUserSpaceOnUseDoesNotScale() throws Exception {
        SvgMarker arrow = markerWithContent("arrow");
        arrow.setMarkerUnits("userSpaceOnUse");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(arrow);

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setStrokeWidth(3.0);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(line);

        Group wrapper = (Group) render(root).getChildren().get(0);
        Group markerInstance = (Group) wrapper.getChildren().get(1);
        // no Scale transform: just [Translate(vertex), Rotate, Translate(-refX,-refY)]
        assertThat(markerInstance.getTransforms(), hasSize(3));
    }

    @Test
    public void testShapesOwnTransformAppliesToItsMarkersToo() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(markerWithContent("arrow"));

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setMarkerEnd("url(#arrow)");
        line.setTransform("translate(100 200)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(line);

        Group wrapper = (Group) render(root).getChildren().get(0);
        // the line's own transform is relocated onto the wrapper, so it applies to the marker sibling too
        assertThat(wrapper.getTransforms(), hasSize(1));
        Translate relocated = (Translate) wrapper.getTransforms().get(0);
        assertThat(relocated.getX(), closeTo(100, 1e-9));
        assertThat(relocated.getY(), closeTo(200, 1e-9));
        assertThat(((Node) wrapper.getChildren().get(0)).getTransforms(), is(empty()));
    }

    @Test
    public void testUnresolvableMarkerRendersTheShapeAloneWithoutThrowing() throws Exception {
        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setMarkerEnd("url(#missing)");

        SvgGroup root = new SvgGroup();
        root.getContent().add(line);

        Group wrapper = (Group) render(root).getChildren().get(0);
        assertThat(wrapper.getChildren(), hasSize(1));
    }

    @Test
    public void testMarkerContentDoesNotInheritStyleFromTheReferencingShape() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(markerWithContent("arrow"));

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup styledAncestor = new SvgGroup();
        styledAncestor.setFill(Color.RED);
        styledAncestor.getContent().add(line);

        SvgGroup root = new SvgGroup();
        root.getContent().add(defs);
        root.getContent().add(styledAncestor);

        Group renderedAncestor = (Group) render(root).getChildren().get(0);
        Group wrapper = (Group) renderedAncestor.getChildren().get(0);
        Group markerInstance = (Group) wrapper.getChildren().get(1);
        Group fitted = (Group) markerInstance.getChildren().get(0);
        Rectangle renderedMarkerShape = (Rectangle) fitted.getChildren().get(0);
        // the SVG initial fill value (black), not the ancestor's red
        assertThat(renderedMarkerShape.getFill(), is(Color.BLACK));
    }

    @Test
    public void testMarkerAttributesBindFromRealXml() throws Exception {
        String xml = "<svg xmlns=\"http://www.w3.org/2000/svg\">"
            + "<defs><marker id=\"arrow\" refX=\"5\" refY=\"5\" markerWidth=\"8\" markerHeight=\"8\""
            + " markerUnits=\"userSpaceOnUse\" orient=\"auto\"><rect width=\"2\" height=\"2\"/></marker></defs>"
            + "<line x1=\"0\" y1=\"0\" x2=\"10\" y2=\"0\" marker-end=\"url(#arrow)\"/>"
            + "</svg>";
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        SvgMarker marker = (SvgMarker) ((SvgDefinitions) svg.getContent().get(0)).getContent().get(0);
        assertThat(marker.getRefX(), is("5"));
        assertThat(marker.getRefY(), is("5"));
        assertThat(marker.getMarkerWidth(), is("8"));
        assertThat(marker.getMarkerHeight(), is("8"));
        assertThat(marker.getMarkerUnits(), is("userSpaceOnUse"));
        assertThat(marker.getOrient(), is("auto"));

        // and it actually renders, end to end, through the real parsed document
        Group wrapper = (Group) svg.createGroup().getChildren().get(0);
        assertThat(wrapper.getChildren(), hasSize(2));
    }

    private static Group render(SvgGroup root) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(root);
        return (Group) svg.createGroup().getChildren().get(0);
    }

}
