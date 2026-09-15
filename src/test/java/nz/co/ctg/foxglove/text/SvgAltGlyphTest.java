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
 * Exercises #138: {@code <altGlyph>} draws the glyphs it names in place of its own characters.
 * <p>
 * Modelled on the suite's own three tests. The font is deliberately shaped like {@code text-altglyph-01-b}'s - {@code units-per-em="8"} with glyph outlines in the 0..8 range, and
 * referenced from text set in an ordinary family - because that is what makes the metric question real: a substituted glyph is scaled by <i>its own</i> font, not by the text
 * around it, and at 40px those two readings differ by a factor of over a hundred.
 * <p>
 * Parse-driven, per #105/#119 - {@code <altGlyphDef>} binds a mixed list of {@code <glyphRef>} and {@code <altGlyphItem>} children, which is precisely the kind of binding an
 * in-memory fixture would not exercise.
 */
public class SvgAltGlyphTest {

    @TempDir
    Path documents;

    /** 40px text in an 8-unit em: every font unit is 5 pixels. */
    private static final double SCALE = 40.0 / 8;

    /**
     * The two glyphs differ in <b>both</b> outline and advance, deliberately: an assertion on either alone would otherwise be unable to tell which of them was drawn.
     */
    private static final String FONT = """
                      <defs>
                        <font id="Font1" horiz-adv-x="5">
                          <font-face font-family="HappySad" units-per-em="8" ascent="8" descent="2"/>
                          <glyph id="A1" horiz-adv-x="4" d="M0,0 L2,8 L4,0Z"/>
                          <glyph id="A2" horiz-adv-x="6" d="M0,0 L3,8 L6,0Z"/>
                        </font>
                      </defs>
                    """;

    /** The flat form: an {@code <altGlyphDef>} whose {@code <glyphRef>} children are taken together. */
    @Test
    public void testAGlyphRefListSubstitutesItsGlyphs() throws Exception {
        List<Node> glyphs = glyphs(FONT + """
                          <defs>
                            <altGlyphDef id="def">
                              <glyphRef xlink:href="#A1"/>
                            </altGlyphDef>
                          </defs>
                        """, "<altGlyph xlink:href='#def'>A</altGlyph>");

        assertThat(glyphs, hasSize(1));
        assertThat(glyphs.get(0), instanceOf(javafx.scene.shape.Path.class));
    }

    /**
     * The substituted glyph is scaled by <b>its own font's</b> {@code units-per-em}, not the text's. The outline spans 8 units vertically, so at 40px it is exactly 40px tall -
     * against roughly 8px if the surrounding text's metrics were used by mistake.
     */
    @Test
    public void testASubstitutedGlyphIsScaledByItsOwnFont() throws Exception {
        Node glyph = glyphs(FONT + """
                          <defs>
                            <altGlyphDef id="def">
                              <glyphRef xlink:href="#A1"/>
                            </altGlyphDef>
                          </defs>
                        """, "<altGlyph xlink:href='#def'>A</altGlyph>").get(0);

        assertThat(glyph.getBoundsInParent()
            .getHeight(), closeTo(8 * SCALE, 0.01));
        assertThat("and sits on the baseline", glyph.getBoundsInParent()
            .getMaxY(), closeTo(100, 0.01));
    }

    /** Two {@code <glyphRef>}s replace two characters, each advancing by its own {@code horiz-adv-x}. */
    @Test
    public void testSeveralGlyphRefsAdvanceByTheirOwnMetrics() throws Exception {
        List<Node> glyphs = glyphs(FONT + """
                          <defs>
                            <altGlyphDef id="def">
                              <glyphRef xlink:href="#A2"/>
                              <glyphRef xlink:href="#A1"/>
                            </altGlyphDef>
                          </defs>
                        """, "<altGlyph xlink:href='#def'>AB</altGlyph>");

        assertThat(glyphs, hasSize(2));
        assertThat(glyphs.get(1)
            .getBoundsInParent()
            .getMinX(), closeTo(6 * SCALE, 0.01));
    }

    /** {@code <altGlyph>} may name a {@code <glyph>} outright, without going through a definition. */
    @Test
    public void testAReferenceStraightToAGlyph() throws Exception {
        List<Node> glyphs = glyphs(FONT, "<altGlyph xlink:href='#A1'>A</altGlyph>");

        assertThat(glyphs, hasSize(1));
        assertThat(glyphs.get(0), instanceOf(javafx.scene.shape.Path.class));
    }

    /**
     * The alternatives form: the first {@code <altGlyphItem>} whose every {@code <glyphRef>} resolves wins. The suite's {@code text-altglyph-02-b} salts its items with
     * {@code #bad-link} to test exactly this, so the first item here is unresolvable and the second must be chosen.
     */
    @Test
    public void testTheFirstFullyResolvableItemWins() throws Exception {
        List<Node> glyphs = glyphs(FONT + """
                          <defs>
                            <altGlyphDef id="def">
                              <altGlyphItem><glyphRef xlink:href="#bad-link"/></altGlyphItem>
                              <altGlyphItem><glyphRef xlink:href="#A2"/></altGlyphItem>
                            </altGlyphDef>
                          </defs>
                        """, "<altGlyph xlink:href='#def'>A</altGlyph>");

        assertThat(glyphs, hasSize(1));
        // A2's outline is 6 units wide against A1's 4, so this says which glyph was actually drawn
        assertThat(glyphs.get(0)
            .getBoundsInParent()
            .getWidth(), closeTo(6 * SCALE, 0.01));
    }

    /**
     * An item is all-or-nothing: one broken reference among several disqualifies the whole item, rather than substituting the glyphs that did resolve and quietly dropping a
     * character.
     */
    @Test
    public void testAnItemWithOneBrokenReferenceIsSkippedEntirely() throws Exception {
        List<Node> glyphs = glyphs(FONT + """
                          <defs>
                            <altGlyphDef id="def">
                              <altGlyphItem>
                                <glyphRef xlink:href="#A1"/>
                                <glyphRef xlink:href="#bad-link"/>
                              </altGlyphItem>
                              <altGlyphItem>
                                <glyphRef xlink:href="#A2"/>
                                <glyphRef xlink:href="#A2"/>
                              </altGlyphItem>
                            </altGlyphDef>
                          </defs>
                        """, "<altGlyph xlink:href='#def'>AB</altGlyph>");

        assertThat(glyphs, hasSize(2));
        assertThat("the second item's glyphs, both 6 units wide",
            glyphs.get(1)
                .getBoundsInParent()
                .getMinX(),
            closeTo(6 * SCALE, 0.01));
    }

    /**
     * <b>The inconvenient case.</b> When nothing resolves, the element renders its own characters as ordinary text - the reason it carries them. An implementation that substituted
     * whatever it managed to find, or that rendered nothing at all, would pass every test above and lose the text here.
     */
    @Test
    public void testAnUnresolvableReferenceFallsBackToTheCharacters() throws Exception {
        Node rendered = render(FONT, "<altGlyph xlink:href='#missing'>AB</altGlyph>");

        assertThat(rendered, instanceOf(Text.class));
        assertThat(((Text) rendered).getText(), org.hamcrest.CoreMatchers.is("AB"));
    }

    /** A definition offering only alternatives, none satisfiable, substitutes nothing and falls back too. */
    @Test
    public void testADefinitionWithNoSatisfiableItemFallsBack() throws Exception {
        Node rendered = render(FONT + """
                          <defs>
                            <altGlyphDef id="def">
                              <altGlyphItem><glyphRef xlink:href="#bad-link"/></altGlyphItem>
                            </altGlyphDef>
                          </defs>
                        """, "<altGlyph xlink:href='#def'>AB</altGlyph>");

        assertThat(rendered, instanceOf(Text.class));
        assertThat(((Text) rendered).getText(), org.hamcrest.CoreMatchers.is("AB"));
    }

    // --- helpers -------------------------------------------------------------

    private List<Node> glyphs(String defs, String body) throws Exception {
        Node rendered = render(defs, body);
        return rendered instanceof Group group ? List.copyOf(group.getChildren()) : List.of(rendered);
    }

    private Node render(String defs, String body) throws Exception {
        String document = """
                        <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="200" height="200">
                        %s
                          <text id="subject" font-family="Arial" font-size="40" x="0" y="100">%s</text>
                        </svg>
                        """.formatted(defs, body);
        Path file = documents.resolve("document-" + Integer.toHexString((defs + body).hashCode()) + ".svg");
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
