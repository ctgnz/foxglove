package nz.co.ctg.foxglove;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.element.SvgUse;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * Exercises the element index against a document that has actually been through the parser, rather than one assembled
 * in memory, so that the walk is proven against the object graph JAXB really produces.
 */
public class SvgElementIndexParseTest {

    private SvgElementIndex index;
    private SvgGraphic svg;

    @Before
    public void setUp() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        svg = parser.parse(SvgGraphic.class.getResourceAsStream("/references.svg"));
        assertThat(svg, notNullValue());
        index = svg.getElementIndex();
    }

    @Test
    public void testIndexesEveryIdInTheDocument() throws Exception {
        assertThat(index.getElementsById().keySet(),
            contains("early", "base", "stop1", "stop2", "derived", "box", "layer", "dot"));
    }

    @Test
    public void testResolvesElementsByType() throws Exception {
        assertThat(index.resolve("#base", SvgLinearGradient.class).isPresent(), is(true));
        assertThat(index.resolve("#box", SvgRectangle.class).isPresent(), is(true));
        assertThat(index.resolve("#layer", SvgGroup.class).isPresent(), is(true));
        assertThat(index.resolve("#dot", SvgCircle.class).isPresent(), is(true));
        assertThat(index.resolve("#early", SvgUse.class).isPresent(), is(true));
    }

    @Test
    public void testResolvesForwardReferenceFromUse() throws Exception {
        SvgUse use = index.resolve("#early", SvgUse.class).get();
        assertThat(index.resolve(use.getXlinkHref()).get(), is(sameInstance(index.resolve("#box").get())));
    }

    @Test
    public void testResolvesPaintReferenceFromAttribute() throws Exception {
        assertThat(index.resolve("url(#derived)", SvgLinearGradient.class).isPresent(), is(true));
    }

    @Test
    public void testFollowsGradientHrefChain() throws Exception {
        SvgLinearGradient derived = index.resolve("#derived", SvgLinearGradient.class).get();
        List<SvgLinearGradient> chain = index.resolveChain(derived, SvgLinearGradient::getXlinkHref, SvgLinearGradient.class);
        assertThat(chain.stream().map(ISvgElement::getId).collect(toList()), contains("derived", "base"));
    }

    @Test
    public void testTracksAncestryThroughDefsAndGroups() throws Exception {
        ISvgElement dot = index.resolve("#dot").get();
        ISvgElement layer = index.resolve("#layer").get();
        assertThat(index.getParent(dot).get(), is(sameInstance(layer)));
        assertThat(index.getParent(layer).get(), is(sameInstance((ISvgElement) svg)));
        assertThat(index.isSelfOrAncestor(layer, dot), is(true));
        assertThat(index.isSelfOrAncestor(index.resolve("#box").get(), dot), is(false));
    }

    @Test
    public void testDocumentHasNoDuplicateIds() throws Exception {
        assertThat(index.getDuplicateIds(), is(empty()));
    }

}
