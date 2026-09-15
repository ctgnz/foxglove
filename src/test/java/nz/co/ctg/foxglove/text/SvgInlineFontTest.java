package nz.co.ctg.foxglove.text;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.SvgGraphic;

/**
 * Exercises #137: a {@code <font>} declared in the document that uses it, rather than fetched through {@code <font-face-uri>} from somewhere else (#61).
 * <p>
 * The shape is taken from the W3C suite, where 15 of the 17 {@code fonts}-chapter documents declare their font this way: the {@code <font-face>} sits <b>inside</b> the
 * {@code <font>} and carries no {@code <font-face-src>}, there being nothing to point at.
 * <p>
 * Parse-driven, per #105/#119 - and especially here, since what is under test is largely which element contains which.
 */
public class SvgInlineFontTest {

    @TempDir
    Path documents;

    private static final double SCALE = 20.0 / 1000;

    /** A font carrying its own glyphs, declared exactly as the suite's own documents declare theirs. */
    private static final String INLINE_FONT = """
                      <defs>
                        <font id="inline" horiz-adv-x="1000">
                          <font-face font-family="InlineFont" units-per-em="1000" ascent="800" descent="-200"/>
                          <missing-glyph horiz-adv-x="500" d="M0 0H500V500H0Z"/>
                          <glyph unicode="A" glyph-name="gl_A" horiz-adv-x="250" d="M0 0H500V500H0Z"/>
                          <glyph unicode="B" glyph-name="gl_B" horiz-adv-x="1000" d="M0 0H500V500H0Z"/>
                          <hkern g1="gl_A" g2="gl_B" k="100"/>
                        </font>
                      </defs>
                    """;

    @Test
    public void testAFontDeclaredInTheDocumentResolvesByFamily() throws Exception {
        List<Node> glyphs = glyphs(INLINE_FONT,
            "<text x='0' y='100' font-family='InlineFont' font-size='20'>AB</text>");

        assertThat(glyphs, hasSize(2));
        assertThat(glyphs.get(0), instanceOf(javafx.scene.shape.Path.class));
    }

    /** The inline font's own metrics apply, not JavaFX's - 250 units of advance, less 100 of kerning. */
    @Test
    public void testAnInlineFontSuppliesAdvancesAndKerning() throws Exception {
        List<Node> glyphs = glyphs(INLINE_FONT,
            "<text x='0' y='100' font-family='InlineFont' font-size='20'>AB</text>");

        assertThat(glyphs.get(1)
            .getBoundsInParent()
            .getMinX(), closeTo((250 - 100) * SCALE, 0.01));
    }

    /** A family naming no font at all still falls through to the JavaFX text system. */
    @Test
    public void testAnUnmatchedFamilyStillFallsThrough() throws Exception {
        Node rendered = render(INLINE_FONT,
            "<text x='0' y='100' font-family='Helvetica' font-size='20'>AB</text>");

        assertThat(rendered, instanceOf(Text.class));
    }

    /**
     * A font declaring nothing but a {@code <missing-glyph>} is still a usable font - every character renders as its box. The suite's {@code MissingInAction} exists precisely to
     * test this, and judging a font by its glyph count would quietly render real text where the reference shows boxes.
     */
    @Test
    public void testAFontWithOnlyAMissingGlyphIsStillUsed() throws Exception {
        String font = """
                          <defs>
                            <font id="missy" horiz-adv-x="1000">
                              <font-face font-family="MissingInAction" units-per-em="1000"/>
                              <missing-glyph horiz-adv-x="600" d="M0 0H500V500H0Z"/>
                            </font>
                          </defs>
                        """;
        List<Node> glyphs = glyphs(font,
            "<text x='0' y='100' font-family='MissingInAction' font-size='20'>AB</text>");

        assertThat("both characters render as the missing glyph", glyphs, hasSize(2));
        assertThat(glyphs.get(0), instanceOf(javafx.scene.shape.Path.class));
        assertThat(glyphs.get(1)
            .getBoundsInParent()
            .getMinX(), closeTo(600 * SCALE, 0.01));
    }

    /** Where two fonts claim one family, the first in document order wins - what {@code fonts-desc-01-t} expects. */
    @Test
    public void testTheFirstFontDeclaringAFamilyWins() throws Exception {
        String font = """
                          <defs>
                            <font id="first" horiz-adv-x="1000">
                              <font-face font-family="Twice" units-per-em="1000"/>
                              <glyph unicode="A" horiz-adv-x="250" d="M0 0H500V500H0Z"/>
                              <glyph unicode="B" horiz-adv-x="250" d="M0 0H500V500H0Z"/>
                            </font>
                            <font id="second" horiz-adv-x="1000">
                              <font-face font-family="Twice" units-per-em="1000"/>
                              <glyph unicode="A" horiz-adv-x="900" d="M0 0H500V500H0Z"/>
                              <glyph unicode="B" horiz-adv-x="900" d="M0 0H500V500H0Z"/>
                            </font>
                          </defs>
                        """;
        List<Node> glyphs = glyphs(font, "<text x='0' y='100' font-family='Twice' font-size='20'>AB</text>");

        assertThat(glyphs.get(1)
            .getBoundsInParent()
            .getMinX(), closeTo(250 * SCALE, 0.01));
    }

    /** A bare {@code #id} reference names a {@code <font>} in this same document. */
    @Test
    public void testAFragmentOnlyReferenceResolvesWithinTheDocument() throws Exception {
        String font = INLINE_FONT + """
                          <defs>
                            <font-face font-family="ByReference">
                              <font-face-src><font-face-uri xlink:href="#inline"/></font-face-src>
                            </font-face>
                          </defs>
                        """;
        List<Node> glyphs = glyphs(font,
            "<text x='0' y='100' font-family='ByReference' font-size='20'>AB</text>");

        assertThat(glyphs, hasSize(2));
        assertThat(glyphs.get(1)
            .getBoundsInParent()
            .getMinX(), closeTo((250 - 100) * SCALE, 0.01));
    }

    /**
     * <b>The inconvenient case.</b> Two documents declaring different fonts under the <i>same</i> family must not see each other's glyphs. A cache keyed by family name - or by
     * anything but the element itself - would serve the first document's font to the second, and every single-document test here would still pass.
     */
    @Test
    public void testTwoDocumentsSharingAFamilyNameKeepTheirOwnGlyphs() throws Exception {
        String wide = """
                          <defs>
                            <font id="shared" horiz-adv-x="1000">
                              <font-face font-family="Shared" units-per-em="1000"/>
                              <glyph unicode="A" horiz-adv-x="900" d="M0 0H500V500H0Z"/>
                              <glyph unicode="B" horiz-adv-x="900" d="M0 0H500V500H0Z"/>
                            </font>
                          </defs>
                        """;
        String narrow = wide.replace("900", "100");
        String text = "<text x='0' y='100' font-family='Shared' font-size='20'>AB</text>";

        assertThat(glyphs(wide, text).get(1)
            .getBoundsInParent()
            .getMinX(), closeTo(900 * SCALE, 0.01));
        assertThat(glyphs(narrow, text).get(1)
            .getBoundsInParent()
            .getMinX(), closeTo(100 * SCALE, 0.01));
    }

    // --- helpers -------------------------------------------------------------

    private List<Node> glyphs(String font, String body) throws Exception {
        Node rendered = render(font, body);
        return rendered instanceof Group group ? List.copyOf(group.getChildren()) : List.of(rendered);
    }

    private Node render(String font, String body) throws Exception {
        String document = """
                        <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="200" height="200">
                        %s
                          %s
                        </svg>
                        """.formatted(font, body.replace("<text ", "<text id='subject' "));
        Path file = documents.resolve("document-" + Integer.toHexString(font.hashCode()) + ".svg");
        Files.writeString(file, document, StandardCharsets.UTF_8);

        SvgGraphic svg;
        try (InputStream in = Files.newInputStream(file)) {
            svg = new FoxgloveParser().parse(in);
        }
        svg.setBaseUri(file.toUri());
        Node subject = svg.createGroup()
            .lookup("#subject");
        if (subject == null) {
            throw new AssertionError("the <text> rendered no node at all");
        }
        return subject;
    }

}
