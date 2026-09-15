package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import nz.co.ctg.foxglove.description.SvgMetadata;

/**
 * Exercises #2: {@code <metadata>} previously bound as {@code @XmlValue}, which only ever captures character data - a document embedding RDF/Dublin Core (the schemas the issue
 * itself names) parsed to nothing but the whitespace around it, silently discarding the whole subtree. {@code @XmlAnyElement}/{@code @XmlMixed} together now capture every child -
 * an element from any namespace as a real DOM {@link Element}, character data as a {@link String} - in document order.
 * <p>
 * Deliberately raw DOM access only, per the user's own scope decision: a typed RDF/Dublin Core accessor API is a separate follow-up, once there is a concrete consumer to design
 * one around.
 */
public class SvgMetadataBindingTest {

    private static final String RDF_DOCUMENT = """
                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                         xmlns:dc="http://purl.org/dc/elements/1.1/">
                      <metadata>
                        <rdf:RDF>
                          <rdf:Description>
                            <dc:title>Test Document</dc:title>
                            <dc:creator>Someone</dc:creator>
                          </rdf:Description>
                        </rdf:RDF>
                      </metadata>
                      <rect width="10" height="10"/>
                    </svg>
                    """;

    @Test
    public void testMetadataCapturesArbitraryNamespacedContentAsRealDomElements() throws Exception {
        SvgGraphic svg = parse(RDF_DOCUMENT);
        SvgMetadata metadata = svg.getMetadata()
            .orElseThrow(() -> new AssertionError("no metadata parsed"));

        Element rdf = firstElementOf(metadata.getContent());
        assertThat(rdf.getLocalName(), is("RDF"));
        assertThat(rdf.getNamespaceURI(), is("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));

        Element description = firstChildElement(rdf);
        assertThat(description.getLocalName(), is("Description"));
        Element title = firstChildElement(description);
        assertThat(title.getLocalName(), is("title"));
        assertThat(title.getNamespaceURI(), is("http://purl.org/dc/elements/1.1/"));
        assertThat(title.getTextContent(), is("Test Document"));
    }

    /**
     * The user's own explicit acceptance criterion for #2: a document carrying RDF/Dublin Core metadata must survive being written back out, not just parsed - the whole point of
     * capturing it rather than discarding it. A structural check (the RDF content is present, correctly nested and correctly qualified in the marshalled output), not a
     * byte-for-byte comparison against the original bytes - XML re-serialization routinely reformats whitespace and may choose different (but equivalent) namespace prefixes, which
     * this test does not depend on.
     */
    @Test
    public void testMetadataRoundTripsThroughWriteAndReparse() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(new ByteArrayInputStream(RDF_DOCUMENT.getBytes(StandardCharsets.UTF_8)));

        String written = parser.write(svg, Boolean.TRUE);
        assertThat(written, containsString("RDF"));
        assertThat(written, containsString("Description"));
        assertThat(written, containsString("Test Document"));
        assertThat(written, containsString("Someone"));

        SvgGraphic reparsed = parser.parse(new ByteArrayInputStream(written.getBytes(StandardCharsets.UTF_8)));
        SvgMetadata metadata = reparsed.getMetadata()
            .orElseThrow(() -> new AssertionError("metadata lost on round trip"));
        Element rdf = firstElementOf(metadata.getContent());
        assertThat(rdf.getLocalName(), is("RDF"));
        Element description = firstChildElement(rdf);
        Element title = firstChildElement(description);
        assertThat(title.getLocalName(), is("title"));
        assertThat(title.getTextContent(), is("Test Document"));
    }

    /** The common, empty case must still parse and write cleanly - no content is not an error. */
    @Test
    public void testEmptyMetadataDoesNotThrowOnParseOrWrite() throws Exception {
        String document = """
                        <svg xmlns="http://www.w3.org/2000/svg">
                          <metadata/>
                          <rect width="10" height="10"/>
                        </svg>
                        """;
        SvgGraphic svg = parse(document);
        SvgMetadata metadata = svg.getMetadata()
            .orElseThrow(() -> new AssertionError("no metadata parsed"));
        assertThat(metadata.getContent(), notNullValue());

        String written = new FoxgloveParser().write(svg, Boolean.TRUE);
        assertThat(written, containsString("metadata"));
    }

    private static SvgGraphic parse(String document) throws Exception {
        return new FoxgloveParser().parse(new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8)));
    }

    private static Element firstElementOf(List<Object> content) {
        return content.stream()
            .filter(Element.class::isInstance)
            .map(Element.class::cast)
            .findFirst()
            .orElseThrow(() -> new AssertionError("no element in metadata content: " + content));
    }

    private static Element firstChildElement(Element parent) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element element) {
                return element;
            }
        }
        throw new AssertionError("no child element of " + parent.getTagName());
    }

}
