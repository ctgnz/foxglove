package nz.co.ctg.foxglove.animate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.SvgElementIndex;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * Exercises #30's target resolution: {@code xlink:href} when present and resolvable, otherwise the animation element's own parent in the parsed tree - SMIL's default target rule.
 */
public class SvgAnimationTargetsTest {

    @Test
    public void testXlinkHrefResolvesToTheReferencedElement() throws Exception {
        SvgRectangle target = new SvgRectangle();
        target.setId("target");
        SvgAnimateAttribute animation = new SvgAnimateAttribute();
        animation.setXlinkHref("#target");

        SvgGroup unrelatedParent = new SvgGroup();
        unrelatedParent.getContent()
            .add(animation);

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(target);
        svg.getContent()
            .add(unrelatedParent);
        SvgElementIndex index = svg.getElementIndex();

        assertThat(SvgAnimationTargets.resolve(animation, index)
            .orElseThrow(), is(sameInstance((ISvgElement) target)));
    }

    @Test
    public void testAbsentHrefFallsBackToTheParent() throws Exception {
        SvgRectangle parent = new SvgRectangle();
        SvgAnimateAttribute animation = new SvgAnimateAttribute();
        parent.getContent()
            .add(animation);

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(parent);
        SvgElementIndex index = svg.getElementIndex();

        assertThat(SvgAnimationTargets.resolve(animation, index)
            .orElseThrow(), is(sameInstance((ISvgElement) parent)));
    }

    @Test
    public void testUnresolvableHrefFallsBackToTheParent() throws Exception {
        SvgRectangle parent = new SvgRectangle();
        SvgAnimateAttribute animation = new SvgAnimateAttribute();
        animation.setXlinkHref("#missing");
        parent.getContent()
            .add(animation);

        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(parent);
        SvgElementIndex index = svg.getElementIndex();

        assertThat(SvgAnimationTargets.resolve(animation, index)
            .orElseThrow(), is(sameInstance((ISvgElement) parent)));
    }

    @Test
    public void testAnimationDirectlyUnderTheDocumentRootTargetsTheRootItself() throws Exception {
        // an animation element can never itself be the document root, but its parent - the fallback target - can be
        SvgAnimateAttribute animation = new SvgAnimateAttribute();
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(animation);
        SvgElementIndex index = svg.getElementIndex();

        assertThat(SvgAnimationTargets.resolve(animation, index)
            .orElseThrow(), is(sameInstance((ISvgElement) svg)));
    }

}
