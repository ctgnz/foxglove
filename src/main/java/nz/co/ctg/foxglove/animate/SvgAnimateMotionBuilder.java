package nz.co.ctg.foxglove.animate;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgElementIndex;
import nz.co.ctg.foxglove.geometry.SvgPathData;
import nz.co.ctg.foxglove.shape.SvgPath;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import javafx.util.Duration;

/**
 * Builds the {@link Animation} for {@code <animateMotion>} via {@code javafx.animation.PathTransition} - close to
 * purpose-built for this, since it already moves a node along a {@link Path} and can orient it to the tangent,
 * matching {@code rotate="auto"}.
 * <p>
 * The motion path comes from one of two sources, in precedence order: a child {@code <mpath>} referencing a {@code
 * <path>} element via {@code xlink:href}, or the inline {@code path} attribute. The third source SMIL allows - a
 * point list via {@code from}/{@code by}/{@code to}/{@code values} - is a documented, deliberate gap: it needs its
 * own value-list resolution (distinct from both #32's scalar/paint values and #34's fixed-arity transform vectors)
 * for a form this issue's own acceptance criteria doesn't test. {@code keyPoints} (progress-along-path per key time)
 * is a further documented gap for the same reason - {@code PathTransition} has no direct support for it and would
 * need a manual {@code Timeline} instead. {@code origin} is defined but has no effect in SVG 1.1, so it is not read
 * at all - a spec no-op, not a gap.
 */
public final class SvgAnimateMotionBuilder {

    public static Optional<Animation> build(SvgAnimateMotion element, Node target, RenderContext context) {
        Optional<Path> pathOpt = resolvePath(element, context);
        if (pathOpt.isEmpty()) {
            return Optional.empty();
        }
        Path path = pathOpt.get();
        if (path.getElements().isEmpty()) {
            return Optional.empty();
        }

        SvgAnimationTiming timing = SvgAnimationTiming.parse(element);
        Duration duration = timing.duration();
        if (duration.isIndefinite() || duration.lessThanOrEqualTo(Duration.ZERO)) {
            return Optional.empty();
        }

        PathTransition transition = new PathTransition(duration, path, target);
        RotateMode rotateMode = resolveRotateMode(element);
        transition.setOrientation(rotateMode.orientation());
        rotateMode.fixedAngle().ifPresent(target::setRotate);

        return Optional.of(transition);
    }

    // --- motion path resolution --------------------------------------------

    private static Optional<Path> resolvePath(SvgAnimateMotion element, RenderContext context) {
        Optional<Path> viaMpath = resolveViaMpath(element, context);
        if (viaMpath.isPresent()) {
            return viaMpath;
        }
        String inline = StringUtils.trimToNull(element.getPath());
        return inline == null ? Optional.empty() : Optional.of(SvgPathData.toJavaFxPath(inline));
    }

    private static Optional<Path> resolveViaMpath(SvgAnimateMotion element, RenderContext context) {
        SvgElementIndex index = context == null ? null : context.getElementIndex();
        if (index == null) {
            return Optional.empty();
        }
        return element.getContents().stream()
            .filter(SvgMotionPath.class::isInstance)
            .map(SvgMotionPath.class::cast)
            .findFirst()
            .flatMap(mpath -> index.resolve(mpath.getXlinkHref(), SvgPath.class))
            .map(SvgPath::getD)
            .map(SvgPathData::toJavaFxPath);
    }

    // --- rotate --------------------------------------------------------------

    private record RotateMode(PathTransition.OrientationType orientation, Optional<Double> fixedAngle) {
    }

    /**
     * {@code auto} maps directly onto {@code ORTHOGONAL_TO_TANGENT}. A plain number is a constant tilt applied once,
     * up front - not part of the continuous animation at all, so it is written directly rather than routed through
     * {@code PathTransition}. {@code auto-reverse} (tangent + 180°) is a documented gap: {@code PathTransition}
     * computes its own orientation internally every frame, with no way to layer a further offset on top of it without
     * per-frame interception - it degrades to no rotation, the same as an absent/unparseable {@code rotate}.
     */
    private static RotateMode resolveRotateMode(SvgAnimateMotion element) {
        String raw = StringUtils.trimToNull(element.getRotate());
        if (raw == null) {
            return new RotateMode(PathTransition.OrientationType.NONE, Optional.empty());
        }
        if ("auto".equalsIgnoreCase(raw)) {
            return new RotateMode(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT, Optional.empty());
        }
        if (NumberUtils.isParsable(raw)) {
            return new RotateMode(PathTransition.OrientationType.NONE, Optional.of(NumberUtils.toDouble(raw)));
        }
        return new RotateMode(PathTransition.OrientationType.NONE, Optional.empty());
    }

    private SvgAnimateMotionBuilder() {
    }

}
