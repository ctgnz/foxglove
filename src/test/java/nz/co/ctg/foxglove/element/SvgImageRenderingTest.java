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
 * {@code preserveAspectRatio} including {@code slice} positions and clips correctly, and a missing or malformed reference never throws out of {@code createGraphic}. Also covers
 * #178: a relative reference to another SVG document (no fragment) renders that document's own content, rasterised at its own intrinsic size and fitted into this element's
 * viewport exactly like a bitmap, and a reference to an SVG document that fails to load degrades the same way a broken raster reference already does.
 * <p>
 * {@code javafx.scene.image.Image} construction requires the JavaFX Application Thread - see {@link JavaFxTestSupport} and {@code PatternParseTest}, which has the same requirement
 * for a different reason.
 */
public class SvgImageRenderingTest {

    // a real 1x1 transparent PNG
    private static final String DOT_PNG = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4nGMAAQAABQAB0NcObQAAAABJRU5ErkJggg==";

    // a real 4x2 solid-red PNG, for exercising non-square meet/slice fitting
    private static final String WIDE_PNG = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAACCAIAAADwyuo0AAAAEElEQVR42mP4z8AARwzIHABvqgf5aN2vpwAAAABJRU5ErkJggg==";

    // #181: the same payload as DOT_PNG, line-wrapped the way Inkscape (and other tools) export base64 data -
    // JavaFX's own Image throws on the embedded newline rather than tolerating it the way a browser does
    private static final String DOT_PNG_LINE_WRAPPED = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAACklEQVR4\nnGMAAQAABQAB0NcObQAAAABJRU5ErkJggg==";

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

    /**
     * #181: a line-wrapped base64 payload (the common real-world form - see {@link #DOT_PNG_LINE_WRAPPED}) must decode and render the same as the unwrapped payload does, not
     * silently degrade to an empty image the way {@code SvgImage.resolveImage}'s blanket exception handling previously turned JavaFX's own {@code IllegalArgumentException} on the
     * embedded newline into.
     */
    @Test
    public void testDataUriWithLineWrappedBase64PayloadStillRenders() throws Exception {
        SvgImage image = new SvgImage();
        image.setXlinkHref(DOT_PNG_LINE_WRAPPED);
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

    /**
     * #178: {@code xlink:href} naming another SVG document (no fragment - the whole file as the image source, per {@code struct-image-05-b}) renders that document's own content,
     * rasterised at its own intrinsic size - here 4x2, declared on {@code image-svg-source.svg}'s own root, the same dimensions {@link #WIDE_PNG} uses for the equivalent raster
     * fit tests below.
     */
    @Test
    public void testRelativeHrefToAnSvgDocumentRendersItsContentParsedFromAFile() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parseFile("/image-relative-svg-source.svg");
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
        assertThat(imageView.getImage()
            .getHeight(), closeTo(2, 1e-9));
    }

    /**
     * The referenced document is rasterised at its own intrinsic size, not stretched to fill this element's box outright - {@code <image>}'s own {@code preserveAspectRatio}
     * (default {@code xMidYMid meet}) then fits that intrinsic-sized result into the declared 10x10 viewport exactly like a raster image, the identical 2.5x scale
     * {@link #testMeetFitsWithinBoundsPreservingAspectRatio} computes for the same 4x2-into-10x10 case via {@link #WIDE_PNG}.
     */
    @Test
    public void testSvgSourceFitsWithinBoundsPreservingAspectRatio() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parseFile("/image-relative-svg-source.svg");

        Group rendered = (Group) onFxThread(svg::createGroup).getChildren()
            .get(0);
        Affine transform = fitTransformOf(rendered);
        assertThat(transform.getMxx(), closeTo(2.5, 1e-9));
        assertThat(transform.getMyy(), closeTo(2.5, 1e-9));
    }

    @Test
    public void testUnresolvableExternalSvgDocumentRendersEmptyWithoutThrowing() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parseFile("/image-missing-svg-source.svg");
        assertThat(svg.getBaseUri(), notNullValue());

        Group rendered = (Group) onFxThread(svg::createGroup).getChildren()
            .get(0);
        assertThat(rendered.getChildren(), is(empty()));
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
