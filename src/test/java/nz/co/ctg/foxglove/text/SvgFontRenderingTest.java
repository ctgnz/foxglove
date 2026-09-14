package nz.co.ctg.foxglove.text;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.SvgGraphic;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * Exercises #61: a {@code <font-face>} pointing at an external SVG font, its glyph outlines rendered as paths, and -
 * the half that matters for real documents rather than for the conformance numbers - advances taken from the font's
 * own metrics rather than measured off what was drawn.
 * <p>
 * Parse-driven throughout, both because {@code font-face-uri} resolution needs a real document base URI and because
 * #105 and #119 were both binding bugs that an in-memory test, going through typed setters, could not have seen.
 */
public class SvgFontRenderingTest {

    @TempDir
    Path documents;

    /**
     * A font whose {@code A} and {@code B} have <b>identical outlines</b> but {@code horiz-adv-x} differing by 4x.
     * That is the whole point of the fixture: nothing measured from a rendered glyph could tell these two apart, so
     * an advance that differs proves it came from the font's metrics. The space, conversely, has an advance and no
     * outline at all - the case measuring bounds would collapse to zero.
     */
    private static final String FONT = """
        <svg xmlns="http://www.w3.org/2000/svg">
          <defs>
            <font id="test" horiz-adv-x="1000">
              <font-face font-family="TestFont" units-per-em="1000" ascent="800" descent="-200"/>
              <missing-glyph horiz-adv-x="500" d="M0 0H500V500H0Z"/>
              <glyph unicode="A" horiz-adv-x="250" d="M0 0H500V500H0Z"/>
              <glyph unicode="B" horiz-adv-x="1000" d="M0 0H500V500H0Z"/>
              <glyph unicode=" " horiz-adv-x="300"/>
            </font>
          </defs>
        </svg>
        """;

    /** {@code fontSize / unitsPerEm} for every document here: 20px text in a 1000-unit em. */
    private static final double SCALE = 20.0 / 1000;

    @Test
    public void testGlyphsRenderAsOutlinePathsRatherThanText() throws Exception {
        List<Node> glyphs = glyphs("<text x='0' y='100' font-family='TestFont' font-size='20'>AB</text>");

        assertThat(glyphs, hasSize(2));
        assertThat(glyphs.get(0), instanceOf(javafx.scene.shape.Path.class));
        assertThat(glyphs.get(1), instanceOf(javafx.scene.shape.Path.class));
    }

    /**
     * The measurement half of #61. Both glyphs draw the same 500-unit square, so their rendered widths are equal -
     * only {@code horiz-adv-x} separates them, putting the second glyph at 5px in one ordering and 20px in the other.
     * No JavaFX-measured layout could produce that difference.
     */
    @Test
    public void testAdvancesComeFromTheFontNotFromTheRenderedOutline() throws Exception {
        List<Node> narrowThenWide = glyphs("<text x='0' y='100' font-family='TestFont' font-size='20'>AB</text>");
        List<Node> wideThenNarrow = glyphs("<text x='0' y='100' font-family='TestFont' font-size='20'>BA</text>");

        assertThat(minX(narrowThenWide.get(0)), closeTo(0, 0.01));
        assertThat(minX(narrowThenWide.get(1)), closeTo(250 * SCALE, 0.01));

        assertThat(minX(wideThenNarrow.get(0)), closeTo(0, 0.01));
        assertThat(minX(wideThenNarrow.get(1)), closeTo(1000 * SCALE, 0.01));
    }

    /**
     * Glyph outlines are y-up where JavaFX is y-down, so the vertical scale has to be negative. A square spanning
     * y 0..500 in font units must therefore render <i>above</i> its baseline. Asserted on both edges rather than on
     * the height, which a glyph rendered upside down about its baseline would match exactly.
     */
    @Test
    public void testGlyphOutlinesAreFlippedOntoTheBaseline() throws Exception {
        Bounds bounds = glyphs("<text x='0' y='100' font-family='TestFont' font-size='20'>A</text>")
            .get(0).getBoundsInParent();

        assertThat("the glyph sits on its baseline", bounds.getMaxY(), closeTo(100, 0.01));
        assertThat("and extends upward from it", bounds.getMinY(), closeTo(100 - 500 * SCALE, 0.01));
    }

    /** A space has a real advance and nothing to draw - it must move the cursor without adding a node. */
    @Test
    public void testASpaceAdvancesWithoutRenderingAnything() throws Exception {
        List<Node> glyphs = glyphs("<text x='0' y='100' font-family='TestFont' font-size='20'>A B</text>");

        assertThat("two drawable glyphs, not three", glyphs, hasSize(2));
        // A advances 250 units and the space 300, so B starts 550 units - 11px - along
        assertThat(minX(glyphs.get(1)), closeTo((250 + 300) * SCALE, 0.01));
    }

    @Test
    public void testAnUnknownCharacterFallsBackToMissingGlyph() throws Exception {
        List<Node> glyphs = glyphs("<text x='0' y='100' font-family='TestFont' font-size='20'>ZB</text>");

        assertThat(glyphs, hasSize(2));
        // the missing glyph's own 500-unit advance, not the font's 1000-unit default
        assertThat(minX(glyphs.get(1)), closeTo(500 * SCALE, 0.01));
    }

    /**
     * {@code text-anchor} shifts by the <b>advance</b> total, which here is deliberately not the ink width: A and B
     * advance 250 + 1000 units (25px) while their outlines together span only 750 units (15px). A layout anchored on
     * what was drawn would shift by 7.5px instead of 12.5px.
     */
    @Test
    public void testTextAnchorCentresOnTheAdvanceTotalNotTheInkWidth() throws Exception {
        Node middle = renderNode("<text x='100' y='100' text-anchor='middle' font-family='TestFont' font-size='20'>AB</text>");
        Node end = renderNode("<text x='100' y='100' text-anchor='end' font-family='TestFont' font-size='20'>AB</text>");

        assertThat(middle.getTranslateX(), closeTo(-0.5 * (250 + 1000) * SCALE, 0.01));
        assertThat(end.getTranslateX(), closeTo(-(250 + 1000) * SCALE, 0.01));
    }

    /** A family naming no SVG font must leave text rendering exactly as it was, through the JavaFX text system. */
    @Test
    public void testAFamilyThatNamesNoSvgFontStillRendersAsText() throws Exception {
        Node rendered = renderNode("<text x='0' y='100' font-family='Helvetica' font-size='20'>AB</text>");

        assertThat(rendered, instanceOf(Text.class));
    }

    /** {@code font-family} is a list, so the first name that resolves to an SVG font wins. */
    @Test
    public void testTheFirstResolvableFamilyInTheListWins() throws Exception {
        List<Node> glyphs = glyphs("<text x='0' y='100' font-family='Nonexistent, TestFont, sans-serif' font-size='20'>AB</text>");

        assertThat(glyphs.get(0), instanceOf(javafx.scene.shape.Path.class));
    }

    /**
     * One 37KB font document is shared by all 525 suite documents, so it is loaded once per URI and cached. Proven by
     * <b>deleting the font</b> between renders: a second render that still draws outlines cannot have re-read it.
     */
    @Test
    public void testTheExternalFontIsLoadedOnceAcrossRepeatedResolutions() throws Exception {
        Path document = write("<text x='0' y='100' font-family='TestFont' font-size='20'>AB</text>");
        assertThat(glyphsOf(render(document)).get(0), instanceOf(javafx.scene.shape.Path.class));

        Files.delete(documents.resolve("testfont.svg"));

        assertThat("the font came from the cache, not from a second parse",
            glyphsOf(render(document)).get(0), instanceOf(javafx.scene.shape.Path.class));
    }

    // --- helpers -------------------------------------------------------------

    private static double minX(Node glyph) {
        return glyph.getBoundsInParent().getMinX();
    }

    private List<Node> glyphs(String body) throws Exception {
        return glyphsOf(renderNode(body));
    }

    /** A run of one glyph collapses to the glyph itself rather than a single-child {@code Group}. */
    private static List<Node> glyphsOf(Node rendered) {
        return rendered instanceof Group group ? List.copyOf(group.getChildren()) : List.of(rendered);
    }

    private Node renderNode(String body) throws Exception {
        return render(write(body));
    }

    /**
     * Writes the font and a document referencing it side by side, so {@code font-face-uri} resolves the way it does
     * in a real document - relative to the referencing file's own location.
     */
    private Path write(String body) throws Exception {
        Files.writeString(documents.resolve("testfont.svg"), FONT, StandardCharsets.UTF_8);

        String document = """
            <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="200" height="200">
              <defs>
                <font-face font-family="TestFont">
                  <font-face-src><font-face-uri xlink:href="testfont.svg#test"/></font-face-src>
                </font-face>
              </defs>
              %s
            </svg>
            """.formatted(body.replace("<text ", "<text id='subject' "));
        Path file = documents.resolve("document.svg");
        Files.writeString(file, document, StandardCharsets.UTF_8);
        return file;
    }

    /** The node the {@code <text>} rendered to, found by id rather than by position among the root's children. */
    private static Node render(Path file) throws Exception {
        SvgGraphic svg;
        try (InputStream in = Files.newInputStream(file)) {
            svg = new FoxgloveParser().parse(in);
        }
        svg.setBaseUri(file.toUri());
        Node subject = svg.createGroup().lookup("#subject");
        if (subject == null) {
            throw new AssertionError("the <text> rendered no node at all");
        }
        return subject;
    }

}
