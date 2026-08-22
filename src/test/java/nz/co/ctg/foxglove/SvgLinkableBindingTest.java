package nz.co.ctg.foxglove;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.element.SvgImage;
import nz.co.ctg.foxglove.element.SvgUse;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.text.SvgText;
import nz.co.ctg.foxglove.text.SvgTextPath;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * The xlink attributes are bound through the OXM virtual accessors, so the {@code java-attribute} in the binding has
 * to match the property key the {@link ISvgLinkable} getters read, and the {@code name} has to be the local name
 * rather than the prefixed one. Both were wrong, which left every xlink attribute silently unbound.
 */
public class SvgLinkableBindingTest {

    private FoxgloveParser parser;
    private SvgGraphic svg;

    @Before
    public void setUp() throws Exception {
        parser = new FoxgloveParser();
        svg = parser.parse(SvgGraphic.class.getResourceAsStream("/xlink-attributes.svg"));
        assertThat(svg, notNullValue());
    }

    @Test
    public void testHrefBindsOnAStructuralElement() throws Exception {
        assertThat(use().getXlinkHref(), is("#box"));
    }

    @Test
    public void testEveryXlinkAttributeBinds() throws Exception {
        SvgUse use = use();
        assertThat(use.getXlinkHref(), is("#box"));
        assertThat(use.getXlinkType(), is("simple"));
        assertThat(use.getXlinkRole(), is("http://example.com/role"));
        assertThat(use.getXlinkArcrole(), is("http://example.com/arcrole"));
        assertThat(use.getXlinkTitle(), is("a reused box"));
        assertThat(use.getXlinkShow(), is("embed"));
        assertThat(use.getXlinkActuate(), is("onLoad"));
    }

    /**
     * The bindings are split across one file per package, so a gradient, a text element and a structural element
     * between them prove the paint, text and element binding files.
     */
    @Test
    public void testHrefBindsOnAGradient() throws Exception {
        SvgDefinitions defs = find(svg.getContent(), SvgDefinitions.class, null);
        assertThat(find(defs.getContent(), SvgLinearGradient.class, "derived").getXlinkHref(), is("#base"));
        assertThat(find(defs.getContent(), SvgLinearGradient.class, "base").getXlinkHref(), is((String) null));
    }

    @Test
    public void testHrefBindsOnATextElement() throws Exception {
        SvgText text = find(svg.getContent(), SvgText.class, "label");
        SvgTextPath textPath = text.getContent().stream()
            .filter(SvgTextPath.class::isInstance)
            .map(SvgTextPath.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError("no textPath parsed"));
        assertThat(textPath.getXlinkHref(), is("#curve"));
    }

    @Test
    public void testHrefBindsOnAnExternalReference() throws Exception {
        assertThat(find(svg.getContent(), SvgImage.class, "pic").getXlinkHref(), is("pic.png"));
    }

    /**
     * The marshaller reads the same property key, so a document that parses correctly must also write correctly.
     */
    @Test
    public void testHrefSurvivesARoundTrip() throws Exception {
        String written = parser.write(svg, Boolean.TRUE);
        assertThat(written, containsString("href=\"#box\""));
        assertThat(written, containsString("href=\"#base\""));
    }

    @Test
    public void testProgrammaticallySetHrefIsWritten() throws Exception {
        SvgGraphic graphic = new SvgGraphic();
        SvgUse use = new SvgUse();
        use.setId("u1");
        use.setXlinkHref("#target");
        graphic.getContent().add(use);

        assertThat(parser.write(graphic, Boolean.TRUE), containsString("href=\"#target\""));
    }

    private SvgUse use() {
        return find(svg.getContent(), SvgUse.class, "user");
    }

    private static <T> T find(List<ISvgElement> content, Class<T> type, String id) {
        return content.stream()
            .filter(type::isInstance)
            .map(type::cast)
            .filter(element -> id == null || id.equals(((ISvgElement) element).getId()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("no " + type.getSimpleName() + " with id " + id));
    }

}
