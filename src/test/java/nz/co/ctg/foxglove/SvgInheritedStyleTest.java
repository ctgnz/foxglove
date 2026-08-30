package nz.co.ctg.foxglove;

import org.junit.Test;

import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.SvgRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapWithSize.anEmptyMap;

import javafx.scene.paint.Color;

public class SvgInheritedStyleTest {

    @Test
    public void testRootHasNothingSpecified() throws Exception {
        assertThat(SvgInheritedStyle.root().getProperties(), is(anEmptyMap()));
        assertThat(SvgInheritedStyle.root().getFill(), is(nullValue()));
    }

    @Test
    public void testCarriesInheritableValuesFromTheElement() throws Exception {
        SvgGroup group = new SvgGroup();
        group.setFill(Color.RED);
        group.setStrokeWidth(3.0);

        SvgInheritedStyle style = SvgInheritedStyle.resolve(null, group);
        assertThat(style.getFill(), is(Color.RED));
        assertThat(style.getStrokeWidth(), is(3.0));
    }

    @Test
    public void testCarriesInheritableValuesFromTheParent() throws Exception {
        SvgGroup outer = new SvgGroup();
        outer.setFill(Color.RED);

        SvgInheritedStyle style = SvgInheritedStyle.resolve(SvgInheritedStyle.resolve(null, outer), new SvgGroup());
        assertThat(style.getFill(), is(Color.RED));
    }

    @Test
    public void testElementOverridesTheParent() throws Exception {
        SvgGroup outer = new SvgGroup();
        outer.setFill(Color.RED);
        SvgGroup inner = new SvgGroup();
        inner.setFill(Color.BLUE);

        SvgInheritedStyle style = SvgInheritedStyle.resolve(SvgInheritedStyle.resolve(null, outer), inner);
        assertThat(style.getFill(), is(Color.BLUE));
    }

    /**
     * Accumulating down the chain is what lets a grandparent's value reach a grandchild, since an element is only
     * ever handed its immediate parent.
     */
    @Test
    public void testAccumulatesAcrossSeveralLevels() throws Exception {
        SvgGroup a = new SvgGroup();
        a.setFill(Color.RED);
        SvgGroup b = new SvgGroup();
        b.setStrokeWidth(2.0);
        SvgGroup c = new SvgGroup();
        c.setStroke(Color.GREEN);

        SvgInheritedStyle style = SvgInheritedStyle.resolve(
            SvgInheritedStyle.resolve(SvgInheritedStyle.resolve(null, a), b), c);
        assertThat(style.getFill(), is(Color.RED));
        assertThat(style.getStrokeWidth(), is(2.0));
        assertThat(style.getStroke(), is(Color.GREEN));
    }

    /**
     * {@code opacity} applies to the element that declares it, so it must not travel down to descendants - otherwise
     * a half transparent group would make each child half transparent again.
     */
    @Test
    public void testDoesNotCarryNonInheritedProperties() throws Exception {
        SvgGroup group = new SvgGroup();
        group.setOpacity("0.5");
        group.setDisplay("inline");
        group.setClipPath("url(#c)");
        group.setFilter("url(#f)");
        group.setMask("url(#m)");

        SvgInheritedStyle style = SvgInheritedStyle.resolve(null, group);
        assertThat(style.getOpacity(), is(nullValue()));
        assertThat(style.getDisplay(), is(nullValue()));
        assertThat(style.getClipPath(), is(nullValue()));
        assertThat(style.getFilter(), is(nullValue()));
        assertThat(style.getMask(), is(nullValue()));
    }

    @Test
    public void testCarriesTextProperties() throws Exception {
        SvgGroup group = new SvgGroup();
        group.setFontFamily("Serif");
        group.setFontSize("20");
        group.setTextAnchor("middle");

        SvgInheritedStyle style = SvgInheritedStyle.resolve(null, group);
        assertThat(style.getFontFamily(), is("Serif"));
        assertThat(style.getFontSize(), is("20"));
        assertThat(style.getTextAnchor(), is("middle"));
    }

    @Test
    public void testNullParentIsTreatedAsRoot() throws Exception {
        assertThat(SvgInheritedStyle.resolve(null, new SvgRectangle()).getProperties(), is(anEmptyMap()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testResolvedStyleIsImmutable() throws Exception {
        SvgInheritedStyle.root().set(ISvgGraphicsAttributes.GRAPHX_FILL, Color.RED);
    }

}
