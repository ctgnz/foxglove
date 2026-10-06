package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.description.DublinCore;
import nz.co.ctg.foxglove.description.SvgMetadata;

/**
 * Exercises #169: read-only Dublin Core accessors over {@code <metadata>}'s RDF block, permissive about its structure. The Inkscape cases are the reason it is permissive - every
 * SVG in jmsfx that carries metadata was written by Inkscape, which uses a typed {@code cc:Work} node rather than {@code rdf:Description}, gives {@code dc:type} as an
 * {@code rdf:resource}, leaves {@code dc:title} empty, and wraps {@code dc:creator} in a {@code cc:Agent} with its own {@code dc:title}.
 */
public class DublinCoreTest {

    private static final String NAMESPACES = """
                    xmlns="http://www.w3.org/2000/svg" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                         xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:cc="http://creativecommons.org/ns#"
                    """;

    @Test
    public void testReadsATextbookDescription() throws Exception {
        DublinCore dc = dublinCore("""
                        <rdf:RDF>
                          <rdf:Description>
                            <dc:title>Test Document</dc:title>
                            <dc:creator>Someone</dc:creator>
                            <dc:date>2026-10-07</dc:date>
                          </rdf:Description>
                        </rdf:RDF>
                        """);
        assertThat(dc.getTitle(), is(Optional.of("Test Document")));
        assertThat(dc.getCreator(), is(Optional.of("Someone")));
        assertThat(dc.getDate(), is(Optional.of("2026-10-07")));
        assertThat(dc.getRdf()
            .getLocalName(), is("RDF"));
        assertThat(dc.getRights(), is(Optional.empty()));
    }

    @Test
    public void testReadsInkscapesTypedNode() throws Exception {
        DublinCore dc = dublinCore("""
                        <rdf:RDF>
                          <cc:Work rdf:about="">
                            <dc:format>image/svg+xml</dc:format>
                            <dc:type rdf:resource="http://purl.org/dc/dcmitype/StillImage" />
                            <dc:title></dc:title>
                          </cc:Work>
                        </rdf:RDF>
                        """);
        assertThat(dc.getFormat(), is(Optional.of("image/svg+xml")));
        // A value given as rdf:resource rather than as text.
        assertThat(dc.getType(), is(Optional.of("http://purl.org/dc/dcmitype/StillImage")));
        // Inkscape's empty title is absent, not an empty string.
        assertThat(dc.getTitle(), is(Optional.empty()));
        assertThat(dc.getAll("title"), is(List.of()));
    }

    /** The agent's own dc:title is the creator's name - not a second title for the document. */
    @Test
    public void testReadsInkscapesCreatorAgentWithoutTakingItsTitle() throws Exception {
        DublinCore dc = dublinCore("""
                        <rdf:RDF>
                          <cc:Work rdf:about="">
                            <dc:title>Armour</dc:title>
                            <dc:creator>
                              <cc:Agent>
                                <dc:title>CTG Games</dc:title>
                              </cc:Agent>
                            </dc:creator>
                          </cc:Work>
                        </rdf:RDF>
                        """);
        assertThat(dc.getCreator(), is(Optional.of("CTG Games")));
        assertThat(dc.getAll("title"), is(List.of("Armour")));
    }

    @Test
    public void testReturnsEveryValueOfARepeatedProperty() throws Exception {
        DublinCore dc = dublinCore("""
                        <rdf:RDF>
                          <rdf:Description>
                            <dc:subject xml:lang="en">symbology</dc:subject>
                            <dc:subject xml:lang="fr">symbologie</dc:subject>
                            <dc:subject>APP-6</dc:subject>
                          </rdf:Description>
                        </rdf:RDF>
                        """);
        assertThat(dc.getAll("subject"), is(List.of("symbology", "symbologie", "APP-6")));
        assertThat(dc.getSubject(), is(Optional.of("symbology")));
        List<DublinCore.Value> values = dc.getValues("subject");
        assertThat(values.get(0)
            .getLanguage(), is(Optional.of("en")));
        assertThat(values.get(1)
            .getLanguage(), is(Optional.of("fr")));
        assertThat(values.get(2)
            .getLanguage(), is(Optional.empty()));
    }

    @Test
    public void testReadsEveryNodeUnderTheRdfBlockInDocumentOrder() throws Exception {
        DublinCore dc = dublinCore("""
                        <rdf:RDF>
                          <cc:Work rdf:about="">
                            <dc:contributor>First</dc:contributor>
                          </cc:Work>
                          <rdf:Description rdf:about="#second">
                            <dc:contributor>Second</dc:contributor>
                          </rdf:Description>
                        </rdf:RDF>
                        """);
        assertThat(dc.getAll("contributor"), is(List.of("First", "Second")));
    }

    @Test
    public void testMetadataWithoutAnRdfBlockHasNoDublinCore() throws Exception {
        SvgMetadata metadata = metadata("Just some text");
        assertThat(metadata.getRdf(), is(Optional.empty()));
        assertThat(metadata.getDublinCore(), is(Optional.empty()));
    }

    @Test
    public void testSurvivesWritingAndReparsing() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svg = parser.parse(stream(document("""
                        <rdf:RDF>
                          <cc:Work rdf:about="">
                            <dc:title>Round Trip</dc:title>
                            <dc:type rdf:resource="http://purl.org/dc/dcmitype/StillImage" />
                          </cc:Work>
                        </rdf:RDF>
                        """)));
        SvgGraphic reparsed = parser.parse(stream(parser.write(svg, Boolean.TRUE)));
        DublinCore dc = reparsed.getMetadata()
            .flatMap(SvgMetadata::getDublinCore)
            .orElseThrow(() -> new AssertionError("Dublin Core lost on round trip"));
        assertThat(dc.getTitle(), is(Optional.of("Round Trip")));
        assertThat(dc.getType(), is(Optional.of("http://purl.org/dc/dcmitype/StillImage")));
    }

    /** foxglove's own test document, written by Inkscape. */
    @Test
    public void testReadsARealInkscapeDocument() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/test.svg")) {
            DublinCore dc = new FoxgloveParser().parse(in)
                .getMetadata()
                .flatMap(SvgMetadata::getDublinCore)
                .orElseThrow(() -> new AssertionError("no Dublin Core in test.svg"));
            assertThat(dc.getFormat(), is(Optional.of("image/svg+xml")));
            assertThat(dc.getType(), is(Optional.of("http://purl.org/dc/dcmitype/StillImage")));
            assertThat(dc.getTitle(), is(Optional.empty()));
        }
    }

    private static DublinCore dublinCore(String content) throws Exception {
        return metadata(content).getDublinCore()
            .orElseThrow(() -> new AssertionError("no Dublin Core parsed"));
    }

    private static SvgMetadata metadata(String content) throws Exception {
        return new FoxgloveParser().parse(stream(document(content)))
            .getMetadata()
            .orElseThrow(() -> new AssertionError("no metadata parsed"));
    }

    private static String document(String content) {
        return "<svg " + NAMESPACES + "><metadata>" + content + "</metadata><rect width=\"10\" height=\"10\"/></svg>";
    }

    private static InputStream stream(String document) {
        return new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8));
    }

}
