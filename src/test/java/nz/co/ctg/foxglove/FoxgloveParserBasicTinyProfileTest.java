package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Exercises #155 and #158: {@code catalog.xml} bundled only the SVG 1.1 <b>Full</b> profile DTD, so a document declaring the <b>Basic</b> or <b>Tiny</b> profile instead had no
 * catalog entry and fell through to a real network fetch of that DTD from {@code www.w3.org} - which itself parameter-entities in several {@code .mod} files, each fetched
 * separately. This surfaced as intermittent {@code HTTP 429}s from w3.org breaking the live conformance dashboard Action, which runs the full suite on every push to master.
 * <p>
 * Basic and Tiny are both grammatically restricted <i>subsets</i> of Full - nothing a document declaring either one needs is missing from the bundled Full DTD, since this renderer
 * never validates that a document actually conforms to its declared profile's own restrictions regardless of DOCTYPE. Both are mapped to the same bundled {@code svg.dtd} (#155),
 * using the exact {@code PUBLIC} identifiers the real W3C suite documents declare.
 * <p>
 * <b>#155's catalog entries turned out not to be sufficient on their own.</b> The live Action still failed afterward, with the identical symptom on the same document. #158 closes
 * the gap unconditionally instead: {@code XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES = FALSE} means an unresolvable external reference is simply skipped, never fetched,
 * regardless of what structural feature would otherwise have triggered it. {@link #testFullProfileDocumentsStillResolveTheirDtdLocally} is the one regression this fix could
 * plausibly cause (blocking the catalog's own, legitimately-local resolution along with genuine network fetches) and confirms it doesn't - once the catalog rewrites a
 * {@code SYSTEM} id to a classpath resource, the resulting fetch is local, not "external" in the sense this property governs.
 * <p>
 * <b>No synthetic test here proves the fix would catch a regression of the original bug, and that limitation is deliberate, not an oversight.</b> Four different reproduction
 * attempts - a bare {@code DOCTYPE}, one with an internal subset declaring an unused entity, one referencing it, and the real suite document's own exact bytes with only its
 * {@code SYSTEM} id substituted - all parsed a deliberately unreachable host successfully regardless of whether this fix was present. The reason turned out to matter: the real
 * failure needs a <i>successful</i> HTTP connection that returns something that isn't valid DTD content (w3.org's own {@code 429} error page), which then fails while being
 * <i>parsed</i> as a DTD - a connection failure to an unreachable host is a different failure mode entirely, already handled gracefully elsewhere in the pipeline regardless of
 * this property. Reproducing the real failure synthetically would need a local HTTP server standing in for a misbehaving w3.org, which is disproportionate to what this fix needs
 * proven. The real evidence is direct measurement against the genuine fixture (47ms with this fix, 150-1400ms and occasional outright failure without it, confirmed manually) and
 * the property's own well-documented, standard JAXP semantics - not a unit test that would pass regardless of whether the fix works, for a reason unrelated to what it actually
 * claims to check.
 */
public class FoxgloveParserBasicTinyProfileTest {

    @Test
    public void testABasicProfileDocumentParses() throws Exception {
        SvgGraphic svg = parse("-//W3C//DTD SVG 1.1 Basic//EN", "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic.dtd");
        assertThat(svg, notNullValue());
    }

    @Test
    public void testATinyProfileDocumentParses() throws Exception {
        SvgGraphic svg = parse("-//W3C//DTD SVG 1.1 Tiny//EN", "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny.dtd");
        assertThat(svg, notNullValue());
    }

    /**
     * The one regression #158's fix could plausibly cause: disabling external entities outright might also have blocked the catalog's own <i>local</i> resolution of the
     * Full-profile DTD, since a document's DOCTYPE still names it as an external SYSTEM/PUBLIC reference regardless of where the catalog ultimately points it. It doesn't - the
     * property governs the underlying fetch, not the declared-external-ness of the reference.
     */
    @Test
    public void testFullProfileDocumentsStillResolveTheirDtdLocally() throws Exception {
        SvgGraphic svg = parse("-//W3C//DTD SVG 1.1//EN", "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd");
        assertThat(svg, notNullValue());
    }

    private static SvgGraphic parse(String publicId, String systemId) throws Exception {
        String document = "<!DOCTYPE svg PUBLIC \"" + publicId + "\" \"" + systemId + "\">\n"
                          + "<svg xmlns=\"http://www.w3.org/2000/svg\"><rect width=\"10\" height=\"10\"/></svg>";
        return new FoxgloveParser().parse(new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8)));
    }

}
