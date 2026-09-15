package nz.co.ctg.foxglove.element;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgPresentationAttributes;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgElementIndex;
import nz.co.ctg.foxglove.geometry.SvgPathData;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgPolygon;
import nz.co.ctg.foxglove.shape.SvgPolyline;

/**
 * Assembles {@code marker-start}/{@code marker-mid}/{@code marker-end} onto a shape that supports them ({@code <line>}, {@code <polyline>}, {@code <polygon>}, {@code <path>}).
 * <p>
 * Applied at the {@link nz.co.ctg.foxglove.ISvgContainer#appendContent} consumer level rather than by changing {@link nz.co.ctg.foxglove.shape.AbstractSvgShape}'s return type:
 * every production caller already treats a built child as a plain {@code Node}, so wrapping it here - only when markers are actually present - needs no change to any shape's own
 * {@code createGraphic} signature.
 */
public final class SvgMarkerRenderer {

    private enum MarkerRole {
            START,
            MID,
            END
    }

    private record MarkerPlacement(Point2D point, double autoAngle, MarkerRole role) {
    }

    /**
     * Wraps {@code node} with its markers when {@code child} is a markable shape with at least one of {@code marker-start}/{@code marker-mid}/{@code marker-end} set, or returns
     * {@code node} unchanged otherwise - which covers every shape that doesn't use markers, the overwhelming majority. That majority is established first and costs nothing beyond
     * three attribute reads; see the comment at the top of the method body.
     * <p>
     * {@code node}'s own {@code transforms} (from its {@code transform} attribute) are relocated onto the returned wrapper, so the shape and its markers share exactly the same
     * outer transform - verified empirically that this relocation is a visual no-op for the shape itself, since a JavaFX {@link Group}'s transform and the same transform placed
     * directly on its only child produce an identical result.
     */
    public static Node applyMarkers(Node node, ISvgElement child, RenderContext context) {
        // Established before computing anything, deliberately (#121). Every child of every container comes through
        // here, markers are rare, and working out a shape's vertices is not free - for a <path> it means parsing and
        // flattening the whole `d` attribute. Doing that first and discarding it was pure waste on nearly every
        // element, and it needlessly widened the blast radius of anything going wrong in path parsing: it is how
        // #115's NumberFormatException reached a document that declares no markers at all.
        if (!(child instanceof ISvgPresentationAttributes attrs)) {
            return node;
        }
        String startHref = attrs.getMarkerStart();
        String midHref = attrs.getMarkerMid();
        String endHref = attrs.getMarkerEnd();
        if (StringUtils.isBlank(startHref) && StringUtils.isBlank(midHref) && StringUtils.isBlank(endHref)) {
            return node;
        }

        List<Point2D> vertices;
        List<Double> angles;
        if (child instanceof SvgLine line) {
            vertices = List.of(new Point2D(line.getStartX(), line.getStartY()), new Point2D(line.getEndX(), line.getEndY()));
            angles = autoAnglesFor(vertices, false);
        } else if (child instanceof SvgPolyline polyline) {
            vertices = polyline.getPoints();
            angles = vertices == null ? List.of() : autoAnglesFor(vertices, false);
        } else if (child instanceof SvgPolygon polygon) {
            vertices = polygon.getPoints();
            angles = vertices == null ? List.of() : autoAnglesFor(vertices, true);
        } else if (child instanceof SvgPath path) {
            // marker-start/marker-end apply only to the very first/last vertex of the whole path, not per subpath -
            // so angles are computed per subpath (respecting each one's own open/closed tangent rules) but roles
            // are assigned once below, over every subpath's vertices concatenated together
            vertices = new ArrayList<>();
            angles = new ArrayList<>();
            for (SvgPathData.Subpath subpath : SvgPathData.subpaths(path.getD())) {
                List<Point2D> subVertices = subpath.vertices();
                if (subpath.closed() && subVertices.size() > 1) {
                    // Z appends a closing point that duplicates the subpath's own start (matching flatten()'s
                    // existing convention) - bisecting against that duplicate directly would make vertex 0's
                    // "incoming" a zero-length vector pointing from itself to itself. Bisect over the distinct
                    // corners only (the same representation <polygon>'s implicit closing edge already uses), then
                    // re-append the duplicate closing point with vertex 0's own angle, since it's the same location
                    List<Point2D> distinct = subVertices.subList(0, subVertices.size() - 1);
                    List<Double> distinctAngles = autoAnglesFor(distinct, true);
                    vertices.addAll(distinct);
                    vertices.add(subVertices.get(subVertices.size() - 1));
                    angles.addAll(distinctAngles);
                    angles.add(distinctAngles.get(0));
                } else {
                    vertices.addAll(subVertices);
                    angles.addAll(autoAnglesFor(subVertices, false));
                }
            }
        } else {
            return node;
        }

        if (vertices == null || vertices.size() < 2) {
            return node;
        }

        SvgElementIndex index = context.getElementIndex();
        double strokeWidth = node instanceof Shape shape ? shape.getStrokeWidth() : 1.0;

        Group wrapper = new Group(node);
        wrapper.getTransforms()
            .addAll(node.getTransforms());
        node.getTransforms()
            .clear();

        for (MarkerPlacement placement : assignRoles(vertices, angles)) {
            String href = switch (placement.role()) {
                case START -> startHref;
                case MID -> midHref;
                case END -> endHref;
            };
            index.resolve(href, SvgMarker.class)
                .map(marker -> buildMarkerInstance(marker, placement, strokeWidth, index))
                .ifPresent(markerNode -> {
                    if (markerNode != null) {
                        wrapper.getChildren()
                            .add(markerNode);
                    }
                });
        }
        return wrapper;
    }

    /**
     * The bisected auto-orientation angle at every vertex of one subpath, computed unconditionally - a marker only consults it when its own {@code orient} is {@code auto}.
     * <p>
     * When {@code closed} (a {@code <polygon>}'s implicit closing edge back to the first point, or a {@code <path>} subpath ending in {@code Z}), the first vertex's incoming
     * direction and the last vertex's outgoing direction wrap around through that closing edge, per the specification's closed-path bisection rule - not merely approximated as
     * open-path start/end.
     */
    private static List<Double> autoAnglesFor(List<Point2D> vertices, boolean closed) {
        int n = vertices.size();
        List<Double> angles = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Point2D point = vertices.get(i);
            // plain if/else, not a nested ternary: mixing angleOf's primitive double with a null branch in a
            // ternary forces the whole expression's static type to double, which then throws unboxing null
            Double inAngle = null;
            if (i > 0) {
                inAngle = angleOf(vertices.get(i - 1), point);
            } else if (closed) {
                inAngle = angleOf(vertices.get(n - 1), point);
            }
            Double outAngle = null;
            if (i < n - 1) {
                outAngle = angleOf(point, vertices.get(i + 1));
            } else if (closed) {
                outAngle = angleOf(point, vertices.get(0));
            }
            angles.add(bisect(inAngle, outAngle));
        }
        return angles;
    }

    /**
     * One placement per vertex, in order: the first is {@link MarkerRole#START}, the last {@link MarkerRole#END}, everything between {@link MarkerRole#MID} - so a 2-point line
     * never gets a {@code MID} placement, and a multi-subpath {@code <path>} gets exactly one start and one end overall, with every subpath-boundary vertex in between (including a
     * fresh {@code M} in the middle of the data) getting {@code MID} - per the specification, {@code marker-start}/{@code marker-end} apply to the first/last vertex of the whole
     * path, not per subpath.
     */
    private static List<MarkerPlacement> assignRoles(List<Point2D> vertices, List<Double> angles) {
        int n = vertices.size();
        List<MarkerPlacement> placements = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            MarkerRole role = i == 0 ? MarkerRole.START : (i == n - 1 ? MarkerRole.END : MarkerRole.MID);
            placements.add(new MarkerPlacement(vertices.get(i), angles.get(i), role));
        }
        return placements;
    }

    private static double angleOf(Point2D from, Point2D to) {
        return Math.toDegrees(Math.atan2(to.getY() - from.getY(), to.getX() - from.getX()));
    }

    /**
     * Bisects two tangent directions (in degrees) by summing their unit vectors and taking the angle of the result - the standard technique, correct except for the degenerate case
     * of an exact 180-degree reversal (the sum is then near zero), where this falls back to the outgoing angle alone.
     */
    private static double bisect(Double inAngle, Double outAngle) {
        if (inAngle == null && outAngle == null) {
            // a subpath consisting of a single bare moveto with nothing drawn has no tangent at all
            return 0.0;
        }
        if (inAngle == null) {
            return outAngle;
        }
        if (outAngle == null) {
            return inAngle;
        }
        double inRad = Math.toRadians(inAngle);
        double outRad = Math.toRadians(outAngle);
        double x = Math.cos(inRad) + Math.cos(outRad);
        double y = Math.sin(inRad) + Math.sin(outRad);
        if (Math.hypot(x, y) < 1e-9) {
            return outAngle;
        }
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Builds one instance of {@code marker}, or {@code null} when its viewport has no positive area or its content renders to nothing.
     * <p>
     * Renders in a fresh, non-inheriting context ({@link RenderContext#root}) rather than one resolved from the referencing shape - per the specification, markers do not inherit
     * style from the element that references them. Reuses {@link nz.co.ctg.foxglove.ISvgFitToViewBox#createViewportTransform}, already proven for {@code <symbol>}/{@code <image>},
     * for the marker's own {@code viewBox}. The clip and the positioning transforms (vertex translate, orientation, {@code markerUnits} scale, {@code refX}/{@code refY} shift)
     * live on the outer {@code instance} group, while the viewBox-fit transform lives on its child {@code fitted} - {@code Node.setClip()} applies in a node's own pre-transform
     * local space, so the two must be kept apart the same way {@code <image>}'s {@code slice} clipping does.
     */
    private static Node buildMarkerInstance(SvgMarker marker, MarkerPlacement placement, double strokeWidth, SvgElementIndex index) {
        double refX = parseCoordinate(marker.getRefX(), 0);
        double refY = parseCoordinate(marker.getRefY(), 0);
        double markerWidth = parseCoordinate(marker.getMarkerWidth(), 3);
        double markerHeight = parseCoordinate(marker.getMarkerHeight(), 3);
        if (markerWidth <= 0 || markerHeight <= 0) {
            return null;
        }
        boolean scaleByStrokeWidth = !"userSpaceOnUse".equals(marker.getMarkerUnits());
        String rawOrient = marker.getOrient();
        double angle;
        if ("auto".equals(rawOrient)) {
            angle = placement.autoAngle();
        } else if ("auto-start-reverse".equals(rawOrient)) {
            // Per spec, behaves exactly like `auto` except the marker-start instance is additionally reversed -
            // mid/end instances on the same path are unaffected, so this has to consult the placement's own role
            // rather than being a single flag computed once per marker like the plain `auto` case above.
            angle = placement.role() == MarkerRole.START ? placement.autoAngle() + 180 : placement.autoAngle();
        } else {
            angle = parseCoordinate(rawOrient, 0);
        }

        RenderContext markerContext = RenderContext.root(index, markerWidth, markerHeight);
        Group fitted = new Group();
        Transform viewBoxTransform = marker.createViewportTransform(markerWidth, markerHeight);
        if (viewBoxTransform != null) {
            fitted.getTransforms()
                .add(viewBoxTransform);
        }
        marker.applyStyle(markerContext);
        marker.appendContent(fitted, markerContext);
        if (fitted.getChildren()
            .isEmpty()) {
            return null;
        }

        // refX/refY are given in the marker's own viewBox coordinate system (SVG 1.1 11.6.2), but the translate
        // below positions content in the post-viewBox viewport space (markerWidth x markerHeight) that
        // viewBoxTransform already maps `fitted`'s own content into - so the reference point has to go through that
        // same transform to land in the same space as the content it is meant to align with. Absent a viewBox, the
        // viewport space *is* the coordinate system refX/refY are given in, so they are used as-is.
        Point2D refPoint = viewBoxTransform != null ? viewBoxTransform.transform(refX, refY) : new Point2D(refX, refY);

        Group instance = new Group(fitted);
        instance.setClip(new Rectangle(markerWidth, markerHeight));
        instance.getTransforms()
            .add(new Translate(placement.point()
                .getX(), placement.point()
                    .getY()));
        instance.getTransforms()
            .add(new Rotate(angle));
        if (scaleByStrokeWidth) {
            instance.getTransforms()
                .add(new Scale(strokeWidth, strokeWidth));
        }
        instance.getTransforms()
            .add(new Translate(-refPoint.getX(), -refPoint.getY()));
        return instance;
    }

    private static double parseCoordinate(String value, double defaultValue) {
        if (StringUtils.isBlank(value) || !NumberUtils.isParsable(value.trim())) {
            return defaultValue;
        }
        return NumberUtils.toDouble(value.trim(), defaultValue);
    }

    private SvgMarkerRenderer() {
    }

}
