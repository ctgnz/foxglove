package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Exercises #96: the real SVG 1.1 DTD's own {@code %SVG.Presentation.attrib} parameter entity is ~15,041 characters - one character over the JDK's own
 * {@code jdk.xml.maxParameterEntitySizeLimit} default of 15,000 (a security hardening limit against XML entity-expansion attacks) - so a document declaring (or referencing) the
 * real DTD failed to parse at all.
 * <p>
 * Reproduced here with a self-contained internal-subset fixture (an oversized parameter entity declared inline, never even referenced) rather than a document that names the real
 * external DTD - deterministic and independent of whether the external DTD happens to be resolved locally (via {@code catalog.xml}) or fetched over the network for a given
 * {@code PUBLIC}/{@code SYSTEM} identifier pair, which turned out not to be consistent across every profile variant the W3C conformance suite (#44) uses.
 */
public class FoxgloveParserDtdEntityLimitTest {

    @Test
    public void testDocumentWithAnOversizedParameterEntityStillParses() throws Exception {
        String oversized = "x".repeat(15_001);
        String document = "<!DOCTYPE svg [\n"
                          + "  <!ENTITY % Big \"" + oversized + "\">\n"
                          + "]>\n"
                          + "<svg xmlns=\"http://www.w3.org/2000/svg\"><rect width=\"10\" height=\"10\"/></svg>";

        SvgGraphic svg = new FoxgloveParser().parse(new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8)));
        assertThat(svg, notNullValue());
    }

}
