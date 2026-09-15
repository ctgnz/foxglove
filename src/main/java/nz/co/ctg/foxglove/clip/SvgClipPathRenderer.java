package nz.co.ctg.foxglove.clip;

import java.util.Set;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;

import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgContainer;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.RenderContext.UnitsMode;
import nz.co.ctg.foxglove.SvgInheritedStyle;

/**
 * Builds the {@link Node} that {@link nz.co.ctg.foxglove.ISvgGraphicsAttributes#applyClip} hands to {@link Node#setClip(Node)}.
 * <p>
 * A plain {@link Group} of the rendered children is enough to union them - verified empirically (a throwaway test, since removed) that a {@code Group} used as a clip covers the
 * union of its children's own coverage, not just the first child or their intersection. This also sidesteps needing every child to be a single {@link javafx.scene.shape.Shape} (as
 * {@code Shape.union} would), since a {@code <clipPath>} may contain a {@code <use>}, which renders to a {@code Group}.
 */
public final class SvgClipPathRenderer {

    /**
     * Renders {@code clipPath}'s content as a clip node, in the coordinate space {@code targetBounds} was measured in (the referencing node's own pre-transform local space - see
     * {@code applyClip}).
     *
     * @param targetBounds
     *            the bounding box of the element the clip is being applied to, used both to resolve {@code clipPathUnits="objectBoundingBox"} and, degenerate (non-positive), to
     *            short circuit to an empty clip rather than build content that would only be discarded
     */
    public static Node render(SvgClipPath clipPath, RenderContext context, Bounds targetBounds) {
        return render(clipPath, context, targetBounds, Sets.newIdentityHashSet());
    }

    private static Node render(SvgClipPath clipPath, RenderContext context, Bounds targetBounds, Set<SvgClipPath> visited) {
        if (!visited.add(clipPath)) {
            // a cyclic clip-path chain: degrade to "nothing visible" at the repeat rather than recurse forever
            return new Group();
        }

        UnitsMode unitsMode = RenderContext.parseUnits(clipPath.getClipPathUnits(), UnitsMode.USER_SPACE_ON_USE);
        if (unitsMode == UnitsMode.OBJECT_BOUNDING_BOX && (targetBounds.getWidth() <= 0 || targetBounds.getHeight() <= 0)) {
            return new Group();
        }

        // A fresh, non-inheriting context - the clip path's own contents do not inherit style from the element that
        // references it (the same reasoning already established for <marker>) - but reuses the current viewport,
        // locale and base URI, since those describe the document being rendered rather than the referencing element.
        RenderContext contentContext = RenderContext.root(context.getElementIndex(), context.getViewportWidth(), context.getViewportHeight())
            .withLocale(context.getLocale())
            .withBaseUri(context.getBaseUri()
                .orElse(null))
            .resolveChild(clipPath);

        Group content = new Group();
        for (ISvgElement child : clipPath.getContent()) {
            if (child instanceof FxGraphic<?> graphic && ISvgContainer.isRendered(child, contentContext.getLocale())) {
                Node built = graphic.createGraphic(contentContext);
                if (built != null) {
                    applyClipRule(child, contentContext, built);
                    content.getChildren()
                        .add(built);
                }
            }
        }

        if (unitsMode == UnitsMode.OBJECT_BOUNDING_BOX) {
            // Content coordinates are fractions of the target's bounding box: scaling (and offsetting) the whole
            // subtree is equivalent to, and far simpler than, teaching every shape class a bbox-relative coordinate
            // mode - the same technique already established for SvgPattern's patternContentUnits.
            content.getTransforms()
                .add(new Translate(targetBounds.getMinX(), targetBounds.getMinY()));
            content.getTransforms()
                .add(new Scale(targetBounds.getWidth(), targetBounds.getHeight()));
        }

        // A <clipPath> may itself carry a clip-path, which intersects rather than unions - Node.setClip() nests
        // freely, so giving this content its own clip is exactly set intersection, with no CAG library needed.
        context.getElementIndex()
            .resolve(clipPath.getClipPath(), SvgClipPath.class)
            .ifPresent(nested -> content.setClip(render(nested, context, targetBounds, visited)));

        return content;
    }

    /**
     * Overrides the {@code fill-rule} the child's own normal rendering pipeline already set with {@code clip-rule} instead - the two attributes play the same role (which points a
     * shape's fill contributes across a self -intersecting or {@code fill-rule="evenodd"}-style path), but a shape does not otherwise know when it is being built as clip content
     * rather than painted directly.
     * <p>
     * Applied recursively so a {@code <use>}'s rendered {@link Group} is covered too - which does mean one resolved value is reapplied across that whole subtree, so a deeply
     * nested override (a {@code <use>} referencing content that declares its own, different {@code clip-rule}) is not preserved. Accepted as a known limitation; full fidelity here
     * is materially more machinery for a rare case.
     */
    private static void applyClipRule(ISvgElement child, RenderContext context, Node node) {
        if (!(child instanceof ISvgStylable stylable)) {
            return;
        }
        String clipRule = SvgInheritedStyle.resolve(context, stylable)
            .getClipRule();
        FillRule fillRule = "evenodd".equalsIgnoreCase(StringUtils.trimToEmpty(clipRule)) ? FillRule.EVEN_ODD : FillRule.NON_ZERO;
        applyFillRule(fillRule, node);
    }

    private static void applyFillRule(FillRule fillRule, Node node) {
        if (node instanceof SVGPath svgPath) {
            svgPath.setFillRule(fillRule);
        } else if (node instanceof Path path) {
            path.setFillRule(fillRule);
        } else if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                applyFillRule(fillRule, child);
            }
        }
    }

    private SvgClipPathRenderer() {
    }

}
