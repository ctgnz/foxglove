package nz.co.ctg.foxglove;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.element.SvgUse;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.text.SvgText;
import nz.co.ctg.foxglove.text.SvgTextSpan;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class SvgElementIndexTest {

    private SvgGraphic svg;

    @Before
    public void setUp() throws Exception {
        svg = new SvgGraphic();
    }

    // --- reference parsing -------------------------------------------------

    @Test
    public void testParseFragmentReference() throws Exception {
        assertThat(SvgElementIndex.parseReference("#grad").get(), is("grad"));
    }

    @Test
    public void testParseUrlReference() throws Exception {
        assertThat(SvgElementIndex.parseReference("url(#grad)").get(), is("grad"));
        assertThat(SvgElementIndex.parseReference("url( #grad )").get(), is("grad"));
        assertThat(SvgElementIndex.parseReference("  url(#grad)  ").get(), is("grad"));
    }

    @Test
    public void testParseQuotedUrlReference() throws Exception {
        assertThat(SvgElementIndex.parseReference("url('#grad')").get(), is("grad"));
        assertThat(SvgElementIndex.parseReference("url(\"#grad\")").get(), is("grad"));
    }

    @Test
    public void testParseUrlKeywordIsCaseInsensitive() throws Exception {
        assertThat(SvgElementIndex.parseReference("URL(#grad)").get(), is("grad"));
    }

    /**
     * Ids are case sensitive even though the {@code url} keyword around them is not.
     */
    @Test
    public void testParseReferencePreservesIdCase() throws Exception {
        assertThat(SvgElementIndex.parseReference("url(#Grad1)").get(), is("Grad1"));
    }

    /**
     * A paint may name a fallback colour after the reference, which is not part of the id.
     */
    @Test
    public void testParseReferenceIgnoresPaintFallback() throws Exception {
        assertThat(SvgElementIndex.parseReference("url(#grad) red").get(), is("grad"));
    }

    @Test
    public void testParseExternalReferenceIsRejected() throws Exception {
        assertThat(SvgElementIndex.parseReference("other.svg#grad"), is(Optional.empty()));
        assertThat(SvgElementIndex.parseReference("url(other.svg#grad)"), is(Optional.empty()));
        assertThat(SvgElementIndex.parseReference("http://example.com/a.svg#grad"), is(Optional.empty()));
    }

    @Test
    public void testParseMalformedReferenceIsRejected() throws Exception {
        assertThat(SvgElementIndex.parseReference(null), is(Optional.empty()));
        assertThat(SvgElementIndex.parseReference(""), is(Optional.empty()));
        assertThat(SvgElementIndex.parseReference("   "), is(Optional.empty()));
        assertThat(SvgElementIndex.parseReference("grad"), is(Optional.empty()));
        assertThat(SvgElementIndex.parseReference("#"), is(Optional.empty()));
        assertThat(SvgElementIndex.parseReference("url(#grad"), is(Optional.empty()));
        assertThat(SvgElementIndex.parseReference("url()"), is(Optional.empty()));
    }

    // --- indexing ----------------------------------------------------------

    @Test
    public void testResolvesElementInDefs() throws Exception {
        SvgLinearGradient gradient = gradient("grad");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(gradient);
        svg.getContent().add(defs);

        assertThat(svg.getElementIndex().resolve("url(#grad)").get(), is(sameInstance((ISvgElement) gradient)));
        assertThat(svg.getElementIndex().resolve("#grad").get(), is(sameInstance((ISvgElement) gradient)));
    }

    @Test
    public void testResolvesElementNestedInGroups() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setId("inner");
        SvgGroup inner = new SvgGroup();
        inner.getContent().add(rect);
        SvgGroup outer = new SvgGroup();
        outer.getContent().add(inner);
        svg.getContent().add(outer);

        assertThat(svg.getElementIndex().resolve("#inner").get(), is(sameInstance((ISvgElement) rect)));
    }

    @Test
    public void testResolvesElementInNestedSvg() throws Exception {
        SvgCircle circle = new SvgCircle();
        circle.setId("dot");
        SvgGraphic nested = new SvgGraphic();
        nested.getContent().add(circle);
        svg.getContent().add(nested);

        assertThat(svg.getElementIndex().resolve("#dot").get(), is(sameInstance((ISvgElement) circle)));
    }

    /**
     * {@code SvgText} holds mixed character data and elements in a {@code List<Object>}, which the walk has to handle
     * alongside the uniformly typed content lists everywhere else.
     */
    @Test
    public void testResolvesElementInMixedTextContent() throws Exception {
        SvgTextSpan span = new SvgTextSpan();
        span.setId("span1");
        SvgText text = new SvgText();
        text.getContent().add("Hello ");
        text.getContent().add(span);
        svg.getContent().add(text);

        assertThat(svg.getElementIndex().resolve("#span1").get(), is(sameInstance((ISvgElement) span)));
    }

    @Test
    public void testIndexesTheRootElement() throws Exception {
        svg.setId("root");
        assertThat(svg.getElementIndex().resolve("#root").get(), is(sameInstance((ISvgElement) svg)));
    }

    @Test
    public void testElementsWithoutAnIdAreNotIndexed() throws Exception {
        svg.getContent().add(new SvgRectangle());
        assertThat(svg.getElementIndex().size(), is(0));
    }

    @Test
    public void testUnknownReferenceResolvesToEmpty() throws Exception {
        svg.getContent().add(gradient("grad"));
        assertThat(svg.getElementIndex().resolve("#nosuch"), is(Optional.empty()));
    }

    /**
     * A reference may name an element declared later in the document, so the whole tree is walked before any lookup
     * is served.
     */
    @Test
    public void testForwardReferenceResolves() throws Exception {
        SvgUse use = new SvgUse();
        use.setId("user");
        use.setXlinkHref("#later");
        svg.getContent().add(use);

        SvgRectangle target = new SvgRectangle();
        target.setId("later");
        SvgDefinitions defs = new SvgDefinitions();
        defs.getContent().add(target);
        svg.getContent().add(defs);

        assertThat(svg.getElementIndex().resolve(use.getXlinkHref()).get(), is(sameInstance((ISvgElement) target)));
    }

    // --- typed lookup ------------------------------------------------------

    @Test
    public void testTypedResolveReturnsMatchingElement() throws Exception {
        SvgLinearGradient gradient = gradient("grad");
        svg.getContent().add(gradient);

        assertThat(svg.getElementIndex().resolve("url(#grad)", SvgLinearGradient.class).get(), is(sameInstance(gradient)));
    }

    @Test
    public void testTypedResolveRejectsWrongType() throws Exception {
        svg.getContent().add(gradient("grad"));

        assertThat(svg.getElementIndex().resolve("url(#grad)", SvgRectangle.class), is(Optional.empty()));
    }

    // --- duplicate ids -----------------------------------------------------

    /**
     * A duplicate id makes the document invalid and the specification leaves the outcome undefined; the first element
     * in document order wins, and the id is reported.
     */
    @Test
    public void testDuplicateIdKeepsTheFirstElement() throws Exception {
        SvgRectangle first = new SvgRectangle();
        first.setId("dup");
        SvgRectangle second = new SvgRectangle();
        second.setId("dup");
        svg.getContent().add(first);
        svg.getContent().add(second);

        SvgElementIndex index = svg.getElementIndex();
        assertThat(index.resolve("#dup").get(), is(sameInstance((ISvgElement) first)));
        assertThat(index.getDuplicateIds(), contains("dup"));
    }

    @Test
    public void testWellFormedDocumentReportsNoDuplicates() throws Exception {
        svg.getContent().add(gradient("a"));
        svg.getContent().add(gradient("b"));

        assertThat(svg.getElementIndex().getDuplicateIds(), is(empty()));
    }

    // --- reference chains and cycles ---------------------------------------

    @Test
    public void testResolveChainFollowsReferences() throws Exception {
        SvgLinearGradient base = gradient("base");
        SvgLinearGradient middle = gradient("middle");
        middle.setXlinkHref("#base");
        SvgLinearGradient top = gradient("top");
        top.setXlinkHref("#middle");
        svg.getContent().add(base);
        svg.getContent().add(middle);
        svg.getContent().add(top);

        List<SvgLinearGradient> chain = svg.getElementIndex()
            .resolveChain(top, SvgLinearGradient::getXlinkHref, SvgLinearGradient.class);
        assertThat(chain, contains(top, middle, base));
    }

    @Test
    public void testResolveChainStopsAtAnUnreferencedElement() throws Exception {
        SvgLinearGradient only = gradient("only");
        svg.getContent().add(only);

        assertThat(svg.getElementIndex().resolveChain(only, SvgLinearGradient::getXlinkHref, SvgLinearGradient.class),
            contains(only));
    }

    /**
     * A reference cycle must terminate rather than recurse until the stack overflows.
     */
    @Test
    public void testResolveChainTerminatesOnCycle() throws Exception {
        SvgLinearGradient a = gradient("a");
        SvgLinearGradient b = gradient("b");
        a.setXlinkHref("#b");
        b.setXlinkHref("#a");
        svg.getContent().add(a);
        svg.getContent().add(b);

        List<SvgLinearGradient> chain = svg.getElementIndex()
            .resolveChain(a, SvgLinearGradient::getXlinkHref, SvgLinearGradient.class);
        assertThat(chain, contains(a, b));
    }

    @Test
    public void testResolveChainTerminatesOnSelfReference() throws Exception {
        SvgLinearGradient self = gradient("self");
        self.setXlinkHref("#self");
        svg.getContent().add(self);

        assertThat(svg.getElementIndex().resolveChain(self, SvgLinearGradient::getXlinkHref, SvgLinearGradient.class),
            hasSize(1));
    }

    // --- ancestry ----------------------------------------------------------

    @Test
    public void testGetParent() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setId("r");
        SvgGroup group = new SvgGroup();
        group.setId("g");
        group.getContent().add(rect);
        svg.getContent().add(group);

        SvgElementIndex index = svg.getElementIndex();
        assertThat(index.getParent(rect).get(), is(sameInstance((ISvgElement) group)));
        assertThat(index.getParent(group).get(), is(sameInstance((ISvgElement) svg)));
        assertThat(index.getParent(svg), is(Optional.empty()));
    }

    /**
     * A {@code <use>} referencing one of its own ancestors would expand forever, so the referrer needs to be able to
     * detect it before following the reference.
     */
    @Test
    public void testDetectsUseReferencingAnAncestor() throws Exception {
        SvgUse use = new SvgUse();
        use.setXlinkHref("#outer");
        SvgGroup outer = new SvgGroup();
        outer.setId("outer");
        outer.getContent().add(use);
        svg.getContent().add(outer);

        SvgElementIndex index = svg.getElementIndex();
        ISvgElement target = index.resolve(use.getXlinkHref()).get();
        assertThat(index.isSelfOrAncestor(target, use), is(true));
    }

    @Test
    public void testUnrelatedElementIsNotAnAncestor() throws Exception {
        SvgUse use = new SvgUse();
        use.setXlinkHref("#sibling");
        SvgRectangle sibling = new SvgRectangle();
        sibling.setId("sibling");
        svg.getContent().add(use);
        svg.getContent().add(sibling);

        SvgElementIndex index = svg.getElementIndex();
        assertThat(index.isSelfOrAncestor(index.resolve(use.getXlinkHref()).get(), use), is(false));
    }

    // --- caching -----------------------------------------------------------

    @Test
    public void testIndexIsCachedUntilRebuilt() throws Exception {
        svg.getContent().add(gradient("first"));
        SvgElementIndex original = svg.getElementIndex();
        assertThat(svg.getElementIndex(), is(sameInstance(original)));

        svg.getContent().add(gradient("second"));
        assertThat(original.resolve("#second"), is(Optional.empty()));

        SvgElementIndex rebuilt = svg.rebuildElementIndex();
        assertThat(rebuilt.resolve("#second").isPresent(), is(true));
        assertThat(svg.getElementIndex(), is(sameInstance(rebuilt)));
    }

    private static SvgLinearGradient gradient(String id) {
        SvgLinearGradient gradient = new SvgLinearGradient();
        gradient.setId(id);
        return gradient;
    }

}
