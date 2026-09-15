package nz.co.ctg.foxglove.element;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgPolygon;
import nz.co.ctg.foxglove.shape.SvgPolyline;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.ViewBox;

/**
 * Exercises #21's acceptance criteria: {@code marker-end} renders an arrowhead on a {@code <line>} and a {@code <polyline>}, correctly oriented; {@code marker-mid} renders at
 * interior vertices only; {@code orient="auto"} bisects correctly at a corner; {@code markerUnits="strokeWidth"} scales with stroke width.
 * <p>
 * Also #67's acceptance criteria: markers on a {@code <path>} render at the right vertices, including after multiple subpaths and a {@code Z} closepath, with {@code orient="auto"}
 * bisecting correctly at every vertex.
 * <p>
 * Also #176's acceptance criteria: {@code refX}/{@code refY} are mapped through the marker's own {@code viewBox} before being used to position content, rather than applied
 * directly in a coordinate system they were never expressed in; a marker with no {@code viewBox} is unaffected; {@code orient="auto-start-reverse"} reverses only the
 * {@code marker-start} instance.
 * <p>
 * Also #183's acceptance criteria: a {@code <polygon>}'s implicit closing edge counts as an additional vertex coinciding with the first point (SVG2 11.6.1), so {@code marker-end}
 * renders there rather than at the last explicit point, and a 2-point {@code <polygon>} gains exactly one interior vertex for {@code marker-mid}.
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
        marker.getContent()
            .add(shape);
        return marker;
    }

    @Test
    public void testMarkerEndOnALineRendersOrientedAtTheEndpoint() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("arrow"));

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(line);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        assertThat(wrapper.getChildren(), hasSize(2));
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Translate vertex = (Translate) markerInstance.getTransforms()
            .get(0);
        assertThat(vertex.getX(), closeTo(10, 1e-9));
        assertThat(vertex.getY(), closeTo(0, 1e-9));
        Rotate rotate = (Rotate) markerInstance.getTransforms()
            .get(1);
        assertThat(rotate.getAngle(), closeTo(0, 1e-6));
    }

    @Test
    public void testMarkerStartAndEndOnAPolylineRenderOnlyAtTheEnds() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("dot"));

        SvgPolyline polyline = new SvgPolyline();
        polyline.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(20, 0)));
        polyline.setMarkerStart("url(#dot)");
        polyline.setMarkerEnd("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(polyline);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        // the shape plus exactly 2 markers (start and end) - none at the interior vertex
        assertThat(wrapper.getChildren(), hasSize(3));
    }

    @Test
    public void testMarkerMidRendersOnlyAtInteriorVertices() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("dot"));

        SvgPolyline polyline = new SvgPolyline();
        polyline.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(10, 10), new Point2D(20, 10)));
        polyline.setMarkerMid("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(polyline);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        // the shape plus 2 mid markers, at the 2 interior vertices out of 4 points
        assertThat(wrapper.getChildren(), hasSize(3));
    }

    @Test
    public void testOrientAutoBisectsAtACorner() throws Exception {
        SvgMarker dot = markerWithContent("dot");
        dot.setOrient("auto");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(dot);

        SvgPolyline polyline = new SvgPolyline();
        polyline.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(10, 10)));
        polyline.setMarkerMid("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(polyline);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Rotate rotate = (Rotate) markerInstance.getTransforms()
            .get(1);
        // incoming along +x (0 deg), outgoing along +y (90 deg) - bisected halfway, at 45 deg
        assertThat(rotate.getAngle(), closeTo(45, 1e-6));
    }

    @Test
    public void testMarkerOnAPolygonUsesTheClosingEdgeForWraparoundBisection() throws Exception {
        SvgMarker dot = markerWithContent("dot");
        dot.setOrient("auto");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(dot);

        // a right triangle: (0,0) -> (10,0) -> (0,10) -> implicit close back to (0,0)
        SvgPolygon polygon = new SvgPolygon();
        polygon.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(0, 10)));
        polygon.setMarkerStart("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(polygon);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Rotate rotate = (Rotate) markerInstance.getTransforms()
            .get(1);
        // incoming via the closing edge (0,10)->(0,0): -90 deg; outgoing (0,0)->(10,0): 0 deg; bisected: -45 deg
        assertThat(rotate.getAngle(), closeTo(-45, 1e-6));
    }

    /**
     * #183, per SVG2 11.6.1: every shape except {@code <polyline>}/{@code <path>} closes implicitly, so the last vertex coincides with the first - {@code marker-end} renders
     * there, not at the last point actually listed in {@code points}. Root cause of {@code painting-marker-properties-01-f}'s broken/discontinuous rendering: its own comment
     * already assumed {@code <polygon>} got this treatment (see the closed-{@code <path>}-subpath branch just above in {@code applyMarkers}), but the code didn't.
     */
    @Test
    public void testMarkerEndOnAPolygonRendersAtTheClosingVertexCoincidingWithTheFirstPoint() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("arrow"));

        SvgPolygon polygon = new SvgPolygon();
        polygon.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(0, 10)));
        polygon.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(polygon);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        assertThat(wrapper.getChildren(), hasSize(2));
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Translate vertex = (Translate) markerInstance.getTransforms()
            .get(0);
        assertThat(vertex.getX(), closeTo(0, 1e-9));
        assertThat(vertex.getY(), closeTo(0, 1e-9));
    }

    /**
     * #183: the exact shape of {@code painting-marker-properties-01-f}'s own "mid" polygon (a 2-point {@code <polygon>}) - before this fix it had zero interior vertices (first
     * point was start, second was end, nothing between), which is why that test's marker-mid column rendered with a gap. Counting the implicit closing edge turns the second point
     * into the sole interior vertex.
     */
    @Test
    public void testMarkerMidOnATwoPointPolygonRendersOneMarkerAtTheSecondPoint() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("dot"));

        SvgPolygon polygon = new SvgPolygon();
        polygon.setPoints(List.of(new Point2D(300, 150), new Point2D(350, 150)));
        polygon.setMarkerMid("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(polygon);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        assertThat(wrapper.getChildren(), hasSize(2));
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Translate vertex = (Translate) markerInstance.getTransforms()
            .get(0);
        assertThat(vertex.getX(), closeTo(350, 1e-9));
        assertThat(vertex.getY(), closeTo(150, 1e-9));
    }

    @Test
    public void testMarkerEndOnAPathRendersAtItsRealFinalVertex() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("arrow"));

        SvgPath path = new SvgPath();
        path.setD("M0,0 L10,0 L10,10");
        path.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(path);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        assertThat(wrapper.getChildren(), hasSize(2));
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Translate vertex = (Translate) markerInstance.getTransforms()
            .get(0);
        assertThat(vertex.getX(), closeTo(10, 1e-9));
        assertThat(vertex.getY(), closeTo(10, 1e-9));
    }

    @Test
    public void testMarkerOnAPathCurveRendersOnlyAtItsRealEndpointNotEveryFlattenedSample() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("dot"));

        // one straight segment, then one cubic curve - a naive dense-sample approach would produce dozens of
        // markers along the curve; the real path data has exactly 2 vertices
        SvgPath path = new SvgPath();
        path.setD("M0,0 L10,0 C10,10 20,10 20,0");
        path.setMarkerMid("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(path);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        // the shape plus exactly 1 mid marker, at the single interior vertex (10,0) between the line and the curve
        assertThat(wrapper.getChildren(), hasSize(2));
    }

    @Test
    public void testMarkerStartAndEndOnAMultiSubpathPathApplyOnlyToTheOverallFirstAndLastVertex() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("dot"));

        // two subpaths (a fresh M mid-data, no Z) - the boundary vertex (100,100) must be marker-mid, not a
        // second marker-start/marker-end
        SvgPath path = new SvgPath();
        path.setD("M0,0 L10,0 M100,100 L200,200");
        path.setMarkerStart("url(#dot)");
        path.setMarkerMid("url(#dot)");
        path.setMarkerEnd("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(path);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        // the shape plus 4 markers: one per vertex (start, mid, mid, end) across both subpaths
        assertThat(wrapper.getChildren(), hasSize(5));
    }

    @Test
    public void testMarkerOnAClosedPathSubpathUsesTheClosingEdgeForBisection() throws Exception {
        SvgMarker dot = markerWithContent("dot");
        dot.setOrient("auto");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(dot);

        // a right triangle path, closed with Z - same geometry as the <polygon> wraparound test
        SvgPath path = new SvgPath();
        path.setD("M0,0 L10,0 L0,10 Z");
        path.setMarkerStart("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(path);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Rotate rotate = (Rotate) markerInstance.getTransforms()
            .get(1);
        // incoming via the closing edge (0,10)->(0,0): -90 deg; outgoing (0,0)->(10,0): 0 deg; bisected: -45 deg
        assertThat(rotate.getAngle(), closeTo(-45, 1e-6));
    }

    @Test
    public void testOrientAutoBisectsAtAPathCorner() throws Exception {
        SvgMarker dot = markerWithContent("dot");
        dot.setOrient("auto");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(dot);

        SvgPath path = new SvgPath();
        path.setD("M0,0 L10,0 L10,10");
        path.setMarkerMid("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(path);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Rotate rotate = (Rotate) markerInstance.getTransforms()
            .get(1);
        // incoming along +x (0 deg), outgoing along +y (90 deg) - bisected halfway, at 45 deg
        assertThat(rotate.getAngle(), closeTo(45, 1e-6));
    }

    /**
     * The exact shape of #176: a marker's {@code refX}/{@code refY} are given in its own {@code viewBox} coordinate system, but the translate that aligns them with the vertex is
     * applied in the post-{@code viewBox} viewport space ({@code markerWidth}/{@code markerHeight}) - so the reference point has to be mapped through the same viewBox-to-viewport
     * transform {@code fitted}'s own content already goes through, not used as a raw offset in the wrong coordinate system. Mirrors {@code painting-marker-01-f}'s actual values
     * exactly (viewBox 0 0 10 10, markerWidth/Height 2, refX/refY 5, stroke-width 8): the viewBox's own center (5,5) should map to (1,1) in viewport space, not stay (5,5).
     */
    @Test
    public void testRefXRefYAreMappedThroughTheMarkersOwnViewBox() throws Exception {
        SvgMarker marker = markerWithContent("arrow");
        marker.setViewBox(new ViewBox(px(0), px(0), px(10), px(10)));
        marker.setMarkerWidth("2");
        marker.setMarkerHeight("2");
        marker.setRefX("5");
        marker.setRefY("5");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(marker);

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setStrokeWidth(8.0);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(line);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        // [Translate(vertex), Rotate, Scale(strokeWidth), Translate(-refPoint)]
        Translate refTranslate = (Translate) markerInstance.getTransforms()
            .get(3);
        assertThat(refTranslate.getX(), closeTo(-1, 1e-9));
        assertThat(refTranslate.getY(), closeTo(-1, 1e-9));
    }

    /** The regression guard for #176's fix: a marker with no {@code viewBox} has no viewport transform to map through, so {@code refX}/{@code refY} are used exactly as given. */
    @Test
    public void testRefXRefYWithNoViewBoxAreUsedDirectly() throws Exception {
        SvgMarker marker = markerWithContent("arrow");
        marker.setRefX("3");
        marker.setRefY("4");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(marker);

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(line);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        // [Translate(vertex), Rotate, Scale(strokeWidth), Translate(-refX,-refY)]
        Translate refTranslate = (Translate) markerInstance.getTransforms()
            .get(3);
        assertThat(refTranslate.getX(), closeTo(-3, 1e-9));
        assertThat(refTranslate.getY(), closeTo(-4, 1e-9));
    }

    /**
     * #176: {@code auto-start-reverse} behaves exactly like {@code auto} except the {@code marker-start} instance is additionally rotated 180 degrees - {@code marker-end} on the
     * same path, sharing the same {@code orient}, is unaffected.
     */
    @Test
    public void testOrientAutoStartReverseRotatesOnlyTheStartMarker180Degrees() throws Exception {
        SvgMarker dot = markerWithContent("dot");
        dot.setOrient("auto-start-reverse");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(dot);

        SvgPolyline polyline = new SvgPolyline();
        polyline.setPoints(List.of(new Point2D(0, 0), new Point2D(10, 0), new Point2D(10, 10)));
        polyline.setMarkerStart("url(#dot)");
        polyline.setMarkerEnd("url(#dot)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(polyline);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        assertThat(wrapper.getChildren(), hasSize(3));

        Group startInstance = (Group) wrapper.getChildren()
            .get(1);
        Rotate startRotate = (Rotate) startInstance.getTransforms()
            .get(1);
        // outgoing tangent (0,0)->(10,0) is 0 deg; auto-start-reverse adds 180 for the start instance only
        assertThat(startRotate.getAngle(), closeTo(180, 1e-6));

        Group endInstance = (Group) wrapper.getChildren()
            .get(2);
        Rotate endRotate = (Rotate) endInstance.getTransforms()
            .get(1);
        // incoming tangent (10,0)->(10,10) is 90 deg - unaffected, since only marker-start reverses
        assertThat(endRotate.getAngle(), closeTo(90, 1e-6));
    }

    @Test
    public void testMarkerUnitsStrokeWidthScalesByTheShapesStrokeWidth() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("arrow"));

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setStrokeWidth(3.0);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(line);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Scale scale = (Scale) markerInstance.getTransforms()
            .get(2);
        assertThat(scale.getX(), closeTo(3.0, 1e-9));
        assertThat(scale.getY(), closeTo(3.0, 1e-9));
    }

    @Test
    public void testMarkerUnitsUserSpaceOnUseDoesNotScale() throws Exception {
        SvgMarker arrow = markerWithContent("arrow");
        arrow.setMarkerUnits("userSpaceOnUse");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(arrow);

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setStrokeWidth(3.0);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(line);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        // no Scale transform: just [Translate(vertex), Rotate, Translate(-refX,-refY)]
        assertThat(markerInstance.getTransforms(), hasSize(3));
    }

    @Test
    public void testShapesOwnTransformAppliesToItsMarkersToo() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("arrow"));

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setMarkerEnd("url(#arrow)");
        line.setTransform("translate(100 200)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(line);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        // the line's own transform is relocated onto the wrapper, so it applies to the marker sibling too
        assertThat(wrapper.getTransforms(), hasSize(1));
        Translate relocated = (Translate) wrapper.getTransforms()
            .get(0);
        assertThat(relocated.getX(), closeTo(100, 1e-9));
        assertThat(relocated.getY(), closeTo(200, 1e-9));
        assertThat(wrapper.getChildren()
            .get(0)
            .getTransforms(), is(empty()));
    }

    @Test
    public void testUnresolvableMarkerRendersTheShapeAloneWithoutThrowing() throws Exception {
        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setMarkerEnd("url(#missing)");

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(line);

        Group wrapper = (Group) render(root).getChildren()
            .get(0);
        assertThat(wrapper.getChildren(), hasSize(1));
    }

    @Test
    public void testMarkerContentDoesNotInheritStyleFromTheReferencingShape() throws Exception {
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(markerWithContent("arrow"));

        SvgLine line = new SvgLine();
        line.setEndX(10);
        line.setMarkerEnd("url(#arrow)");

        SvgGroup styledAncestor = new SvgGroup();
        styledAncestor.setFill(Color.RED);
        styledAncestor.getContent()
            .add(line);

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(styledAncestor);

        Group renderedAncestor = (Group) render(root).getChildren()
            .get(0);
        Group wrapper = (Group) renderedAncestor.getChildren()
            .get(0);
        Group markerInstance = (Group) wrapper.getChildren()
            .get(1);
        Group fitted = (Group) markerInstance.getChildren()
            .get(0);
        Rectangle renderedMarkerShape = (Rectangle) fitted.getChildren()
            .get(0);
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

        SvgMarker marker = (SvgMarker) ((SvgDefinitions) svg.getContent()
            .get(0)).getContent()
            .get(0);
        assertThat(marker.getRefX(), is("5"));
        assertThat(marker.getRefY(), is("5"));
        assertThat(marker.getMarkerWidth(), is("8"));
        assertThat(marker.getMarkerHeight(), is("8"));
        assertThat(marker.getMarkerUnits(), is("userSpaceOnUse"));
        assertThat(marker.getOrient(), is("auto"));

        // and it actually renders, end to end, through the real parsed document
        Group wrapper = (Group) svg.createGroup()
            .getChildren()
            .get(0);
        assertThat(wrapper.getChildren(), hasSize(2));
    }

    // --- the no-markers fast path (#121) ------------------------------------

    /**
     * Working out a shape's vertices is not free - for a {@code <path>} it means parsing and flattening the whole {@code d} attribute - and this runs for every child of every
     * container, while markers are rare. The marker attributes are therefore read first, and nothing else happens when none is set.
     * <p>
     * Asserted through a {@code <path>} that counts reads of its own {@code d}, since that is the single thing the expensive branch needs and the only observable evidence that it
     * was entered at all. Ordering is easy to undo by accident in a later edit, and the cost of getting it wrong is invisible.
     */
    @Test
    public void testAPathWithNoMarkersIsNeverParsed() throws Exception {
        CountingPath path = new CountingPath();
        path.setD("M0,0 C10,0 20,10 20,20 C30,30 40,40 50,50 Z");

        Node rendered = render(groupOf(path));

        assertThat("a path with no markers should not have its d parsed at all", path.reads, is(0));
        assertThat(rendered, is(notNullValue()));
    }

    /** The counterpart: declaring a marker does take the expensive path, so the fast path is not simply always on. */
    @Test
    public void testAPathWithAMarkerIsStillParsed() throws Exception {
        CountingPath path = new CountingPath();
        path.setD("M0,0 L10,0 L20,10");
        path.setMarkerEnd("url(#m)");

        SvgMarker marker = new SvgMarker();
        marker.setId("m");
        marker.getContent()
            .add(new SvgRectangle());
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent()
            .add(marker);

        SvgGroup root = new SvgGroup();
        root.getContent()
            .add(defs);
        root.getContent()
            .add(path);
        render(root);

        assertThat("a path that declares a marker must still be parsed", path.reads > 0, is(true));
    }

    /** Counts how many times its path data is read - see {@link #testAPathWithNoMarkersIsNeverParsed}. */
    private static final class CountingPath extends SvgPath {

        private int reads;

        @Override
        public String getD() {
            reads++;
            return super.getD();
        }
    }

    private static SvgGroup groupOf(nz.co.ctg.foxglove.ISvgElement child) {
        SvgGroup group = new SvgGroup();
        group.getContent()
            .add(child);
        return group;
    }

    private static Group render(SvgGroup root) {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(root);
        return (Group) svg.createGroup()
            .getChildren()
            .get(0);
    }

}
