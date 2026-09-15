package nz.co.ctg.foxglove.animate;

import java.util.Optional;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.SvgElementIndex;

/**
 * Resolves an animation element's target: {@code xlink:href} if present and resolvable, otherwise the element's own parent in the parsed tree - SMIL's default target rule, and the
 * reason {@link SvgElementIndex#getParent} (already public for {@code <use>}'s own cycle guard) is enough here without any new parent-tracking of its own.
 */
public final class SvgAnimationTargets {

    public static Optional<ISvgElement> resolve(ISvgAnimationElement element, SvgElementIndex index) {
        if (element instanceof ISvgLinkable linkable) {
            Optional<ISvgElement> viaHref = index.resolve(linkable.getXlinkHref());
            if (viaHref.isPresent()) {
                return viaHref;
            }
        }
        if (element instanceof ISvgElement svgElement) {
            return index.getParent(svgElement);
        }
        return Optional.empty();
    }

    private SvgAnimationTargets() {
    }

}
