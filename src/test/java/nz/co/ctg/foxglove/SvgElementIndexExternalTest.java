package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.SvgElementIndex.ResolvedElement;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * Exercises #175's external-document resolution - {@code external-reference.svg} references {@code
 * external-reference-target.svg} by a bare {@code xlink:href="external-reference-target.svg#id"}, parsed via {@link FoxgloveParser#parseFile(String)} so the referencing document's
 * own base URI is established the same way {@link SvgImageRenderingTest} already proves for {@code <image>}.
 */
public class SvgElementIndexExternalTest {

    private SvgElementIndex index;

    @BeforeEach
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parseFile("/external-reference.svg");
        assertThat(svg, notNullValue());
        assertThat(svg.getBaseUri(), notNullValue());
        index = svg.getElementIndex();
    }

    @Test
    public void testResolvesUseTargetFromExternalDocument() throws Exception {
        assertThat(index.resolve("external-reference-target.svg#shape", SvgRectangle.class)
            .isPresent(), is(true));
    }

    @Test
    public void testResolvesTrefTargetFromExternalDocument() throws Exception {
        assertThat(index.resolve("external-reference-target.svg#label")
            .isPresent(), is(true));
    }

    /**
     * The resolved owner is the *external* document's own index, not this one - what {@link nz.co.ctg.foxglove.element.SvgUse} needs to correctly resolve a further reference
     * inside the target's own content (here, the target rect's own {@code fill="url(#grad)"}, same-document relative to the external file).
     */
    @Test
    public void testResolveWithOwnerReturnsTheExternalDocumentsOwnIndex() throws Exception {
        ResolvedElement resolved = index.resolveWithOwner("external-reference-target.svg#shape")
            .orElseThrow();
        assertThat(resolved.index(), is(not(sameInstance(index))));
        assertThat(resolved.index()
            .resolve("#grad", SvgLinearGradient.class)
            .isPresent(), is(true));
    }

    @Test
    public void testMissingExternalDocumentResolvesEmptyWithoutThrowing() throws Exception {
        assertThat(index.resolve("does-not-exist.svg#nope")
            .isPresent(), is(false));
    }

    @Test
    public void testDanglingFragmentInExternalDocumentResolvesEmpty() throws Exception {
        assertThat(index.resolve("external-reference-target.svg#doesNotExist")
            .isPresent(), is(false));
    }

    /**
     * An absolute reference given directly in the document - a network URL or a bare {@code file:} URI - is never followed, even though a base URI is available: only a location
     * reachable by resolving relative to it is trusted, matching {@link nz.co.ctg.foxglove.element.SvgImage#resolveImage}'s existing restriction.
     */
    @Test
    public void testAbsoluteReferenceIsRefused() throws Exception {
        assertThat(index.resolve("http://example.com/other.svg#shape")
            .isPresent(), is(false));
        assertThat(index.resolve("file:///etc/other.svg#shape")
            .isPresent(), is(false));
    }

    @Test
    public void testResolvedTargetTypeMismatchStillResolvesEmpty() throws Exception {
        assertThat(index.resolve("external-reference-target.svg#shape", SvgLinearGradient.class)
            .isPresent(), is(false));
        assertThat(index.resolve("external-reference-target.svg#shape")
            .get(), instanceOf(SvgRectangle.class));
    }

}
