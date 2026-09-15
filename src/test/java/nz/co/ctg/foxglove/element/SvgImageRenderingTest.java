package nz.co.ctg.foxglove.element;

import static nz.co.ctg.foxglove.JavaFxTestSupport.onFxThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Affine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.JavaFxTestSupport;
import nz.co.ctg.foxglove.SvgGraphic;

/**
 * Exercises #20's acceptance criteria: a {@code data:} URI image renders, a file-relative reference renders when the base URI is known and fails cleanly when it is not,
 * {@code preserveAspectRatio} including {@code slice} positions and clips correctly, and a missing or malformed reference never throws out of {@code createGraphic}.
 * <p>
 * {@code javafx.scene.image.Image} construction requires the JavaFX Application Thread - see {@link JavaFxTestSupport} and {@code PatternParseTest}, which has the same requirement
 * for a different reason.
 */
public class SvgImageRenderingTest {

    // a real 1x1 transparent PNG
    private static final String DOT_PNG = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4nGMAAQAABQAB0NcObQAAAABJRU5ErkJggg==";

    // a real 4x2 solid-red PNG, for exercising non-square meet/slice fitting
    private static final String WIDE_PNG = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAACCAIAAADwyuo0AAAAEElEQVR42mP4z8AARwzIHABvqgf5aN2vpwAAAABJRU5ErkJggg==";

    @BeforeAll
    public static void initJFX() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    private static Size px(double value) {
        return new Size(value, SizeUnits.PX);
    }

    @Test
    public void testDataUriImageRenders() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref(DOT_PNG);
        image.setWidth(px(10));
        image.setHeight(px(10));

        Group rendered = render(image);
        ImageView imageView = findImageView(rendered);
        assertThat(imageView.getImage(), notNullValue());
        assertThat(imageView.getImage()
            .isError(), is(false));
    }

    @Test
    public void testMeetFitsWithinBoundsPreservingAspectRatio() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref(WIDE_PNG);
        image.setWidth(px(10));
        image.setHeight(px(10));
        // default preserveAspectRatio: xMidYMid meet

        Affine transform = fitTransformOf(render(image));
        // 4x2 image into a 10x10 box: meet picks min(10/4, 10/2) = 2.5, centering the shorter axis
        assertThat(transform.getMxx(), closeTo(2.5, 1e-9));
        assertThat(transform.getMyy(), closeTo(2.5, 1e-9));
        assertThat(transform.getTx(), closeTo(0, 1e-9));
        assertThat(transform.getTy(), closeTo(2.5, 1e-9));
    }

    @Test
    public void testSliceFillsAndClipsToTheDeclaredSize() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref(WIDE_PNG);
        image.setWidth(px(10));
        image.setHeight(px(10));
        image.setPreserveAspectRatio("xMidYMid slice");

        Group rendered = render(image);
        Affine transform = fitTransformOf(rendered);
        // slice picks max(10/4, 10/2) = 5, overflowing the 10-wide box - the clip below is what keeps it to 10x10
        assertThat(transform.getMxx(), closeTo(5, 1e-9));
        assertThat(transform.getMyy(), closeTo(5, 1e-9));

        Bounds bounds = rendered.getBoundsInLocal();
        assertThat(bounds.getWidth(), closeTo(10, 1e-9));
        assertThat(bounds.getHeight(), closeTo(10, 1e-9));
    }

    @Test
    public void testXYAndTransformCompose() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref(DOT_PNG);
        image.setWidth(px(10));
        image.setHeight(px(10));
        image.setX(px(10));
        image.setY(px(20));
        image.setTransform("scale(2)");

        Group rendered = render(image);
        // per spec (and the identical <use> fix): translate(x,y) applies first (inner), this element's own
        // transform - scale(2) - wraps around that result (outer): (0,0) -> translate(10,20) -> scale(2) -> (20,40)
        Point2D origin = rendered.localToParent(0, 0);
        assertThat(origin.getX(), closeTo(20, 1e-9));
        assertThat(origin.getY(), closeTo(40, 1e-9));
    }

    @Test
    public void testMissingHrefRendersEmptyWithoutThrowing() throws Exception {
        SvgImage image = new SvgImage();
        image.setWidth(px(10));
        image.setHeight(px(10));

        assertThat(render(image).getChildren(), is(empty()));
    }

    @Test
    public void testMalformedHrefRendersEmptyWithoutThrowing() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref("has space.png");
        image.setWidth(px(10));
        image.setHeight(px(10));

        assertThat(render(image).getChildren(), is(empty()));
    }

    @Test
    public void testUndecodableDataUriRendersEmptyWithoutThrowing() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref("data:image/png;base64,!!!notbase64!!!");
        image.setWidth(px(10));
        image.setHeight(px(10));

        assertThat(render(image).getChildren(), is(empty()));
    }

    @Test
    public void testZeroWidthRendersEmptyWithoutThrowing() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref(DOT_PNG);
        image.setHeight(px(10));
        // no width set - resolves to 0

        assertThat(render(image).getChildren(), is(empty()));
    }

    @Test
    public void testRelativeHrefWithNoBaseUriRendersEmptyWithoutThrowing() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref("wide.png");
        image.setWidth(px(10));
        image.setHeight(px(10));

        assertThat(render(image).getChildren(), is(empty()));
    }

    @Test
    public void testRelativeHrefRendersWhenParsedFromAFile() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parseFile("/image-relative.svg");
        assertThat(svg, notNullValue());
        assertThat(svg.getBaseUri(), notNullValue());

        Group rendered = onFxThread(svg::createGroup);
        Group imageGroup = (Group) rendered.getChildren()
            .get(0);
        ImageView imageView = findImageView(imageGroup);
        assertThat(imageView.getImage(), notNullValue());
        assertThat(imageView.getImage()
            .isError(), is(false));
        assertThat(imageView.getImage()
            .getWidth(), closeTo(4, 1e-9));
    }

    @Test
    public void testOpacityApplies() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref(DOT_PNG);
        image.setWidth(px(10));
        image.setHeight(px(10));
        image.setOpacity("0.5");

        assertThat(render(image).getOpacity(), closeTo(0.5, 1e-9));
    }

    @Test
    public void testVisibilityHiddenHidesTheImage() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref(DOT_PNG);
        image.setWidth(px(10));
        image.setHeight(px(10));
        image.setVisibility("hidden");

        assertThat(render(image).isVisible(), is(false));
    }

    private static Affine fitTransformOf(Group rendered) {
        Group fitted = (Group) rendered.getChildren()
            .get(0);
        assertThat(fitted.getTransforms(), is(not(empty())));
        return (Affine) fitted.getTransforms()
            .get(0);
    }

    private static ImageView findImageView(Group rendered) {
        Node node = rendered;
        while (node instanceof Group group) {
            node = group.getChildren()
                .get(0);
        }
        return (ImageView) node;
    }

    private static Group render(SvgImage image) throws Exception {
        SvgGraphic svg = new SvgGraphic();
        svg.getContent()
            .add(image);
        return (Group) onFxThread(svg::createGroup).getChildren()
            .get(0);
    }

}
