package nz.co.ctg.foxglove;

import org.junit.Test;

import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.ViewBox;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;

/**
 * Exercises the two gaps #13 closes end to end: {@code viewBox}/{@code preserveAspectRatio} (root and nested,
 * mirroring the {@code meet}/{@code slice}/alignment cases in {@code transform/preserve-aspect-ratio.svg} and the
 * {@code transform/stretch-to-fit.svg} scenario) and percentage lengths resolving against the enclosing viewport.
 * <p>
 * Built in memory, following {@link ISvgContainerTest}'s convention, rather than parsed from those fixtures: both
 * declare the full SVG 1.1 DTD, whose {@code %SVG.Presentation.attrib;} parameter entity exceeds the JDK's default
 * {@code jdk.xml.maxParameterEntitySizeLimit} - a pre-existing environmental limit unrelated to this issue.
 */
public class ViewportRenderingTest {

    private static ViewBox viewBox(double minX, double minY, double width, double height) {
        return new ViewBox(px(minX), px(minY), px(width), px(height));
    }

    private static Size px(double value) {
        return new Size(value, SizeUnits.PX);
    }

    private static Size percent(double value) {
        return new Size(value, SizeUnits.PERCENT);
    }

    private static Affine transformOf(Group group) {
        assertThat(group.getTransforms(), is(not(empty())));
        return (Affine) group.getTransforms().get(0);
    }

    // --- root viewBox / preserveAspectRatio (#3) ----------------------------

    @Test
    public void testRootViewBoxNoneStretchesIndependently() throws Exception {
        // mirrors transform/stretch-to-fit.svg: 300x200 viewport, viewBox 0 0 1500 1000, preserveAspectRatio="none"
        SvgGraphic svg = new SvgGraphic();
        svg.setWidth(px(300));
        svg.setHeight(px(200));
        svg.setViewBox(viewBox(0, 0, 1500, 1000));
        svg.setPreserveAspectRatio("none");

        Affine transform = transformOf(svg.createGroup());
        assertThat(transform.getMxx(), closeTo(0.2, 1e-9));
        assertThat(transform.getMyy(), closeTo(0.2, 1e-9));
        assertThat(transform.getTx(), closeTo(0, 1e-9));
        assertThat(transform.getTy(), closeTo(0, 1e-9));
    }

    @Test
    public void testRootWithNoWidthFallsBackToViewBox() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.setViewBox(viewBox(0, 0, 200, 100));

        // no width/height of its own: the viewport is the viewBox itself, so the transform is the identity
        Affine transform = transformOf(svg.createGroup());
        assertThat(transform.getMxx(), closeTo(1, 1e-9));
        assertThat(transform.getMyy(), closeTo(1, 1e-9));
        assertThat(transform.getTx(), closeTo(0, 1e-9));
        assertThat(transform.getTy(), closeTo(0, 1e-9));
    }

    // --- nested <svg> meet/slice/alignment, mirroring transform/preserve-aspect-ratio.svg -------------------------

    private Group nestedSvg(String preserveAspectRatio, double viewportWidth, double viewportHeight) {
        SvgGraphic root = new SvgGraphic();
        SvgGraphic nested = new SvgGraphic();
        nested.setWidth(px(viewportWidth));
        nested.setHeight(px(viewportHeight));
        nested.setViewBox(viewBox(0, 0, 30, 40));
        nested.setPreserveAspectRatio(preserveAspectRatio);
        root.getContent().add(nested);

        return (Group) root.createGroup().getChildren().get(0);
    }

    @Test
    public void testMeetXMinYMinPicksTheSmallerScale() throws Exception {
        Affine transform = transformOf(nestedSvg("xMinYMin meet", 50, 30));
        assertThat(transform.getMxx(), closeTo(0.75, 1e-9));
        assertThat(transform.getMyy(), closeTo(0.75, 1e-9));
        assertThat(transform.getTx(), closeTo(0, 1e-9));
        assertThat(transform.getTy(), closeTo(0, 1e-9));
    }

    @Test
    public void testMeetXMidYMidCentersTheLeftoverSpace() throws Exception {
        Affine transform = transformOf(nestedSvg("xMidYMid meet", 50, 30));
        assertThat(transform.getMxx(), closeTo(0.75, 1e-9));
        assertThat(transform.getTx(), closeTo(13.75, 1e-9));
        assertThat(transform.getTy(), closeTo(0, 1e-9));
    }

    @Test
    public void testMeetXMaxYMaxPushesTheLeftoverSpaceToTheFarEdge() throws Exception {
        Affine transform = transformOf(nestedSvg("xMaxYMax meet", 50, 30));
        assertThat(transform.getMxx(), closeTo(0.75, 1e-9));
        assertThat(transform.getTx(), closeTo(27.5, 1e-9));
        assertThat(transform.getTy(), closeTo(0, 1e-9));
    }

    @Test
    public void testSliceXMinYMinPicksTheLargerScale() throws Exception {
        Affine transform = transformOf(nestedSvg("xMinYMin slice", 50, 30));
        assertThat(transform.getMxx(), closeTo(50.0 / 30, 1e-9));
        assertThat(transform.getMyy(), closeTo(50.0 / 30, 1e-9));
    }

    // --- percentage lengths resolve against the enclosing viewport ---------

    @Test
    public void testNestedSvgSizePercentResolvesAgainstParentViewport() throws Exception {
        SvgGraphic root = new SvgGraphic();
        root.setWidth(px(400));
        root.setHeight(px(200));
        SvgGraphic nested = new SvgGraphic();
        nested.setWidth(percent(50));
        nested.setHeight(percent(50));
        root.getContent().add(nested);
        // a child rectangle sized as a percentage of *its own* viewport - correct only if nested's 50%/50% above
        // resolved to a 200x100 viewport rather than being silently dropped to zero
        SvgRectangle rect = new SvgRectangle();
        rect.setX(percent(50));
        rect.setWidth(percent(50));
        nested.getContent().add(rect);

        Group renderedNested = (Group) root.createGroup().getChildren().get(0);
        Rectangle fxRect = (Rectangle) renderedNested.getChildren().get(0);
        assertThat(fxRect.getX(), closeTo(100, 1e-9));
        assertThat(fxRect.getWidth(), closeTo(100, 1e-9));
    }

    @Test
    public void testRectanglePercentHeightResolvesAgainstViewportHeight() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.setWidth(px(400));
        svg.setHeight(px(200));
        SvgRectangle rect = new SvgRectangle();
        rect.setY(percent(25));
        rect.setHeight(percent(50));
        svg.getContent().add(rect);

        Rectangle fxRect = (Rectangle) svg.createGroup().getChildren().get(0);
        assertThat(fxRect.getY(), closeTo(50, 1e-9));
        assertThat(fxRect.getHeight(), closeTo(100, 1e-9));
    }

    @Test
    public void testNestedSvgXYResolveAgainstTheParentViewportNotItsOwn() throws Exception {
        SvgGraphic root = new SvgGraphic();
        root.setWidth(px(400));
        root.setHeight(px(200));
        SvgGraphic nested = new SvgGraphic();
        // 50% of the parent's 400x200 is (200,100) - 50% of nested's own declared 100x100 would wrongly be (50,50)
        nested.setX(percent(50));
        nested.setY(percent(50));
        nested.setWidth(px(100));
        nested.setHeight(px(100));
        root.getContent().add(nested);

        Group renderedNested = (Group) root.createGroup().getChildren().get(0);
        assertThat(renderedNested.getTranslateX(), closeTo(200, 1e-9));
        assertThat(renderedNested.getTranslateY(), closeTo(100, 1e-9));
    }

    @Test
    public void testPercentInsideAViewBoxResolvesAgainstTheViewBoxNotThePixelSize() throws Exception {
        SvgGraphic svg = new SvgGraphic();
        // a 400x200 pixel viewport, but content lives in a 0..100 x 0..100 user-unit coordinate system
        svg.setWidth(px(400));
        svg.setHeight(px(200));
        svg.setViewBox(viewBox(0, 0, 100, 100));
        svg.setPreserveAspectRatio("none");
        SvgRectangle rect = new SvgRectangle();
        rect.setWidth(percent(50));
        rect.setHeight(percent(50));
        svg.getContent().add(rect);

        Rectangle fxRect = (Rectangle) svg.createGroup().getChildren().get(0);
        // 50% of the viewBox's own 100x100, not of the 400x200 pixel viewport (which would give 200x100)
        assertThat(fxRect.getWidth(), closeTo(50, 1e-9));
        assertThat(fxRect.getHeight(), closeTo(50, 1e-9));
    }

}
