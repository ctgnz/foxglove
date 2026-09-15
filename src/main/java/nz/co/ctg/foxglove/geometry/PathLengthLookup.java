package nz.co.ctg.foxglove.geometry;

import java.util.List;

import javafx.geometry.Point2D;

/**
 * A lookup from arc length to position and tangent along a flattened polyline (see {@link SvgPathData}), for laying text along a {@code <textPath>} (#29). Generically useful for
 * any future path-following need (such as marker {@code orient="auto"}) - not specific to text.
 */
public final class PathLengthLookup {

    private final List<Point2D> points;
    private final double[] cumulativeLength;

    private PathLengthLookup(List<Point2D> points, double[] cumulativeLength) {
        this.points = points;
        this.cumulativeLength = cumulativeLength;
    }

    /**
     * Builds a lookup over the given polyline. Fewer than two points is treated as a single degenerate point at the origin (or the one point given), so callers get a zero-length
     * path rather than an error.
     */
    public static PathLengthLookup of(List<Point2D> points) {
        if (points == null || points.size() < 2) {
            Point2D point = points == null || points.isEmpty() ? Point2D.ZERO : points.get(0);
            return new PathLengthLookup(List.of(point, point), new double[] {
                0, 0
            });
        }
        double[] cumulativeLength = new double[points.size()];
        for (int i = 1; i < points.size(); i++) {
            cumulativeLength[i] = cumulativeLength[i - 1] + points.get(i - 1)
                .distance(points.get(i));
        }
        return new PathLengthLookup(points, cumulativeLength);
    }

    public double getTotalLength() {
        return cumulativeLength[cumulativeLength.length - 1];
    }

    /**
     * The point at the given arc length, clamped to the path's extent. Interpolates linearly within whichever flattened segment straddles that length.
     */
    public Point2D pointAt(double length) {
        int index = segmentIndexAt(length);
        Point2D start = points.get(index);
        Point2D end = points.get(index + 1);
        double t = segmentFraction(length, index);
        return new Point2D(start.getX() + (end.getX() - start.getX()) * t, start.getY() + (end.getY() - start.getY()) * t);
    }

    /**
     * The tangent direction, in degrees, of the segment straddling the given arc length - constant across the whole segment, since the path has already been flattened into
     * straight pieces.
     */
    public double angleAt(double length) {
        int index = segmentIndexAt(length);
        Point2D start = points.get(index);
        Point2D end = points.get(index + 1);
        return Math.toDegrees(Math.atan2(end.getY() - start.getY(), end.getX() - start.getX()));
    }

    private double segmentFraction(double length, int index) {
        double segmentLength = cumulativeLength[index + 1] - cumulativeLength[index];
        if (segmentLength <= 0) {
            return 0;
        }
        double clamped = Math.clamp(length, 0, getTotalLength());
        return (clamped - cumulativeLength[index]) / segmentLength;
    }

    private int segmentIndexAt(double length) {
        double clamped = Math.clamp(length, 0, getTotalLength());
        for (int i = 0; i < cumulativeLength.length - 1; i++) {
            if (clamped <= cumulativeLength[i + 1]) {
                return i;
            }
        }
        return cumulativeLength.length - 2;
    }

}
