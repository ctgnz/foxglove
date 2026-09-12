package nz.co.ctg.foxglove.animate;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.shape.SvgPath;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.scene.shape.Rectangle;

/**
 * Exercises #88's single-animation correctness for {@code <animateMotion>}: motion path resolution (inline
 * {@code path} attribute vs. a child {@code <mpath>} reference), and {@code rotate}. Built directly against a real
 * {@link Rectangle} target, inspecting the returned {@link PathTransition}.
 */
public class SvgAnimateMotionBuilderTest {

    @Test
    public void testInlinePathAttributeMovesTheTargetAlongThePath() {
        SvgAnimateMotion element = new SvgAnimateMotion();
        element.setDuration("2s");
        element.setPath("M0,0 L100,0");

        PathTransition transition = build(element, new Rectangle());
        assertThat(transition.getDuration(), is(javafx.util.Duration.seconds(2)));
        assertThat(pathElements(transition).size() > 0, is(true));
    }

    @Test
    public void testMpathReferenceResolvesThePathElementsAttribute() {
        SvgPath referenced = new SvgPath();
        referenced.setId("motionPath");
        referenced.setD("M0,0 L50,50");

        SvgMotionPath mpath = new SvgMotionPath();
        mpath.setXlinkHref("#motionPath");

        SvgAnimateMotion element = new SvgAnimateMotion();
        element.setDuration("1s");
        element.getContents().add(mpath);

        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(referenced);

        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        Animation result = element.buildAnimation(new Rectangle(), context).orElseThrow();
        PathTransition transition = (PathTransition) result;
        assertThat(pathElements(transition).size() > 0, is(true));
    }

    @Test
    public void testMpathTakesPrecedenceOverTheInlinePathAttribute() {
        SvgPath referenced = new SvgPath();
        referenced.setId("motionPath");
        referenced.setD("M0,0 L1,1");

        SvgMotionPath mpath = new SvgMotionPath();
        mpath.setXlinkHref("#motionPath");

        SvgAnimateMotion element = new SvgAnimateMotion();
        element.setDuration("1s");
        element.setPath("M0,0 L999,999");
        element.getContents().add(mpath);

        SvgGraphic svg = new SvgGraphic();
        svg.getContent().add(referenced);

        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0);
        PathTransition transition = (PathTransition) element.buildAnimation(new Rectangle(), context).orElseThrow();
        javafx.scene.shape.LineTo lineTo = (javafx.scene.shape.LineTo) pathElements(transition).get(1);
        assertThat(lineTo.getX(), closeTo(1.0, 1e-9));
    }

    @Test
    public void testRotateAutoOrientsToTheTangent() {
        SvgAnimateMotion element = new SvgAnimateMotion();
        element.setDuration("1s");
        element.setPath("M0,0 L100,0");
        element.setRotate("auto");

        PathTransition transition = build(element, new Rectangle());
        assertThat(transition.getOrientation(), is(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT));
    }

    @Test
    public void testRotateAbsentUsesNoOrientation() {
        SvgAnimateMotion element = new SvgAnimateMotion();
        element.setDuration("1s");
        element.setPath("M0,0 L100,0");

        PathTransition transition = build(element, new Rectangle());
        assertThat(transition.getOrientation(), is(PathTransition.OrientationType.NONE));
    }

    @Test
    public void testFixedAngleRotateIsAppliedOnceAsAConstantTilt() {
        Rectangle target = new Rectangle();
        SvgAnimateMotion element = new SvgAnimateMotion();
        element.setDuration("1s");
        element.setPath("M0,0 L100,0");
        element.setRotate("30");

        PathTransition transition = build(element, target);
        assertThat(transition.getOrientation(), is(PathTransition.OrientationType.NONE));
        assertThat(target.getRotate(), closeTo(30.0, 1e-9));
    }

    @Test
    public void testNoPathSourceIsUnsupported() {
        SvgAnimateMotion element = new SvgAnimateMotion();
        element.setDuration("1s");
        assertThat(element.buildAnimation(new Rectangle(), null).isEmpty(), is(true));
    }

    @Test
    public void testIndefiniteDurationIsUnsupported() {
        SvgAnimateMotion element = new SvgAnimateMotion();
        element.setPath("M0,0 L100,0");
        element.setDuration("indefinite");
        assertThat(element.buildAnimation(new Rectangle(), null).isEmpty(), is(true));
    }

    @Test
    public void testResultIsAPathTransition() {
        SvgAnimateMotion element = new SvgAnimateMotion();
        element.setDuration("1s");
        element.setPath("M0,0 L100,0");
        Animation result = element.buildAnimation(new Rectangle(), null).orElseThrow();
        assertThat(result, is(instanceOf(PathTransition.class)));
    }

    private static PathTransition build(SvgAnimateMotion element, Rectangle target) {
        return (PathTransition) element.buildAnimation(target, null).orElseThrow();
    }

    private static java.util.List<javafx.scene.shape.PathElement> pathElements(PathTransition transition) {
        return ((javafx.scene.shape.Path) transition.getPath()).getElements();
    }

}
