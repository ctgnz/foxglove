package nz.co.ctg.foxglove.paint;

import java.util.List;
import java.util.logging.Logger;

import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.adapter.SvgTransformListAdapter;

/**
 * A gradient's {@code gradientTransform}, and the question of whether JavaFX can express the result (#52).
 * <p>
 * JavaFX's {@code LinearGradient}/{@code RadialGradient} carry no transform of their own, so the only way to honour one is to fold it into the coordinates the gradient is built
 * from. That works exactly for some transforms and not at all for others, and the difference is not "simple versus complicated" - it depends on what the transform does to the
 * <i>shape</i> of the gradient:
 * <ul>
 * <li><b>Linear.</b> Colour depends on the projection onto the axis, so baking the endpoints is correct precisely when the transform keeps the iso-lines perpendicular to the
 * transformed axis - see {@link #preservesLinearAxis}. That admits translate, rotation and uniform scale, and also a non-uniform scale <i>aligned with the gradient's own axis</i>,
 * which a blanket similarity test would reject for no reason.
 * <li><b>Radial.</b> A circle has to stay a circle, so nothing short of a similarity will do - see {@link #isSimilarity}. Any other transform turns it into an ellipse, which
 * JavaFX cannot represent.
 * </ul>
 * Everything is computed in the gradient's own coordinate system, which is the space the specification defines the transform to act in. That is why
 * {@code gradientUnits="objectBoundingBox"} needs no special handling here: JavaFX applies the same proportional mapping to the result afterwards.
 */
final class GradientTransform {

    /**
     * Reports a transform this renderer cannot express, at {@code WARNING} because it is actionable - the gradient still paints, but not where the document asked, and an author
     * can fix it by simplifying the transform. Follows the level split {@code SvgFilterRenderer} established.
     */
    private static final Logger LOG = Logger.getLogger(GradientTransform.class.getName());

    /** How far from perpendicular, or from equal length, still counts - these are parsed decimals, not exact reals. */
    private static final double TOLERANCE = 1e-9;

    private static final SvgTransformListAdapter PARSER = new SvgTransformListAdapter();

    private final Transform matrix;

    private GradientTransform(Transform matrix) {
        this.matrix = matrix;
    }

    /**
     * The transform declared by {@code transformText}, or {@code null} when there is none, it does not parse, or it is degenerate (a zero scale collapses the gradient to nothing,
     * and inverting it is meaningless). A null return means "build the gradient exactly as before", which is what every caller wants for all three cases.
     */
    static GradientTransform parse(String transformText) {
        if (StringUtils.isBlank(transformText)) {
            return null;
        }
        List<Transform> transforms = PARSER.parse(transformText);
        if (transforms == null || transforms.isEmpty()) {
            return null;
        }
        Transform combined = transforms.get(0);
        for (int i = 1; i < transforms.size(); i++) {
            combined = combined.createConcatenation(transforms.get(i));
        }
        if (Math.abs(combined.determinant()) < TOLERANCE) {
            return null;
        }
        return new GradientTransform(combined);
    }

    Point2D apply(double x, double y) {
        return matrix.transform(x, y);
    }

    /**
     * Whether baking this transform into a linear gradient's endpoints gives the right picture: with {@code u} the axis direction and {@code v} perpendicular to it, whether
     * {@code M·v} is still perpendicular to {@code M·u}.
     * <p>
     * Deliberately asked about a <i>specific</i> axis rather than about the transform alone. A transform that scales only along the gradient's own direction leaves the iso-lines
     * exactly where they were, and rejecting it because it is not a similarity would refuse a case that renders perfectly.
     */
    boolean preservesLinearAxis(double x1, double y1, double x2, double y2) {
        double ux = x2 - x1;
        double uy = y2 - y1;
        if (Math.hypot(ux, uy) < TOLERANCE) {
            return true;
        }
        Point2D mappedAxis = deltaOf(ux, uy);
        Point2D mappedPerpendicular = deltaOf(-uy, ux);
        return Math.abs(mappedAxis.dotProduct(mappedPerpendicular)) < TOLERANCE * mappedAxis.magnitude() * mappedPerpendicular.magnitude()
                                                                      + TOLERANCE;
    }

    /**
     * Whether this is a similarity - a rotation and a uniform scale, possibly with a reflection and a translation - which is what a radial gradient needs if its circle is to stay
     * a circle. Tested on the matrix's two column vectors: equal in length, and perpendicular.
     */
    boolean isSimilarity() {
        Point2D columnX = deltaOf(1, 0);
        Point2D columnY = deltaOf(0, 1);
        double lengthX = columnX.magnitude();
        double lengthY = columnY.magnitude();
        return Math.abs(lengthX - lengthY) < TOLERANCE * Math.max(lengthX, lengthY) + TOLERANCE
               && Math.abs(columnX.dotProduct(columnY)) < TOLERANCE * lengthX * lengthY + TOLERANCE;
    }

    /** The factor a similarity scales lengths by - what a radial gradient's radius has to be multiplied by. */
    double uniformScale() {
        return deltaOf(1, 0).magnitude();
    }

    /**
     * A direction transformed rather than a position - the translation left out, since it moves points and not vectors. What a radial gradient's focus offset needs: rotating it by
     * the transform's own angle instead would be wrong for a reflection, which passes {@link #isSimilarity} quite legitimately.
     */
    Point2D applyToDirection(double dx, double dy) {
        return deltaOf(dx, dy);
    }

    /**
     * A direction transformed rather than a point: the translation has to be subtracted out, since it moves a position but says nothing about what happens to a vector.
     */
    private Point2D deltaOf(double dx, double dy) {
        return matrix.deltaTransform(dx, dy);
    }

    /** Called instead of baking, when nothing above can represent what was asked for. */
    static void reportUnrepresentable(String gradientId, String transformText) {
        LOG.warning(() -> "gradientTransform \"" + StringUtils.trimToEmpty(transformText) + "\" on gradient '"
                          + StringUtils.defaultIfBlank(gradientId, "(no id)") + "' cannot be represented by a JavaFX gradient - it changes the shape "
                          + "of the gradient, not just its position. Rendering untransformed.");
    }

}
