package nz.co.ctg.foxglove.text;

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.SvgGraphic;

/**
 * Exercises #136: {@code <hkern>} pairs tighten the gap between two particular glyphs.
 * <p>
 * The fixtures are taken from the W3C suite's own {@code fonts-kern-01-t}, whose fonts {@code fontA}-{@code fontG} enumerate every form the matching rules take. That test cannot
 * itself pass until inline {@code <font>} elements are supported (#137) - it declares its fonts inline - but its semantics transfer verbatim to an external font, which is what
 * these do.
 * <p>
 * Parse-driven, per #105/#119: nothing had ever read {@code glyph-name} or any {@code <hkern>} attribute before this, so whether they bind at all is part of what is under test,
 * and an in-memory fixture going through typed setters would prove nothing about it.
 */
public class SvgFontKerningTest {

    @TempDir
    Path documents;

    private static final double UNITS_PER_EM = 1000;
    private static final double FONT_SIZE = 20;
    private static final double SCALE = FONT_SIZE / UNITS_PER_EM;

    /**
     * The four glyphs of the oracle's fonts, identical in every one of them; only the kerning rule differs. The {@code <missing-glyph>} is this fixture's own addition, so that a
     * character deliberately outside a range (a '5') still draws something to measure rather than silently producing one node instead of two.
     */
    private static final String GLYPHS = """
                          <missing-glyph horiz-adv-x="500" d="M0 0H500V500H0Z"/>
                          <glyph unicode="1" glyph-name="gl_1" horiz-adv-x="250" d="M0 0H250V250H0Z"/>
                          <glyph unicode="2" glyph-name="gl_2" horiz-adv-x="1500" d="M0 0H500V500H0Z"/>
                          <glyph unicode="3" glyph-name="gl_3" horiz-adv-x="750" d="M0 0H750V750H0Z"/>
                          <glyph unicode="4" glyph-name="gl_4" horiz-adv-x="1000" d="M0 0H1000V1000H0Z"/>
                    """;

    /** fontA - plain characters on both sides. */
    @Test
    public void testAPairMatchedByCharacter() throws Exception {
        assertKerned("<hkern u1=\"1\" u2=\"2\" k=\"1000\"/>", "12", 250, 1000);
    }

    /** fontB - glyph names, which is what all 406 pairs in the suite's own SVGFreeSans.svg actually use. */
    @Test
    public void testAPairMatchedByGlyphName() throws Exception {
        assertKerned("<hkern g1=\"gl_1\" g2=\"gl_2\" k=\"2000\"/>", "12", 250, 2000);
    }

    /** fontD - u1/u2 as comma-separated lists of characters. */
    @Test
    public void testCharacterListsMatchEveryEntry() throws Exception {
        String rule = "<hkern u1=\"1,3\" u2=\"2,4\" k=\"1500\"/>";
        assertKerned(rule, "12", 250, 1500);
        assertKerned(rule, "34", 750, 1500);
    }

    /** fontE - g1/g2 as comma-separated lists of names. */
    @Test
    public void testGlyphNameListsMatchEveryEntry() throws Exception {
        String rule = "<hkern g1=\"gl_1,gl_3\" g2=\"gl_2,gl_4\" k=\"1500\"/>";
        assertKerned(rule, "12", 250, 1500);
        assertKerned(rule, "34", 750, 1500);
    }

    /** fontG - a character on one side and a glyph name on the other. */
    @Test
    public void testOneSideMatchedByCharacterAndTheOtherByName() throws Exception {
        assertKerned("<hkern u1=\"1\" g2=\"gl_2\" k=\"1000\"/>", "12", 250, 1000);
    }

    /** fontF - an explicit range, and a wildcard range where {@code ?} spans every hex digit. */
    @Test
    public void testUnicodeRangesMatch() throws Exception {
        assertKerned("<hkern u1=\"U+003?\" u2=\"U+0031-0034\" k=\"1500\"/>", "12", 250, 1500);
    }

    /**
     * The boundary of an explicit range. {@code U+0031-0034} covers '1' to '4', so a '4' on the right kerns and a '5' does not - a range parsed as "anything beginning U+003" would
     * pass the positive case and fail this.
     */
    @Test
    public void testARangeDoesNotMatchBeyondItsEnd() throws Exception {
        String rule = "<hkern u1=\"U+0031\" u2=\"U+0031-0034\" k=\"1500\"/>";
        assertKerned(rule, "14", 250, 1500);
        assertKerned(rule, "15", 250, 0);
    }

    /**
     * <b>The inconvenient case.</b> fontC declares one rule carrying both {@code u1}/{@code u2} and {@code g1}/ {@code g2}, and the oracle expects it to kern "12" and "34" - the
     * union of the two sets on each side.
     * <p>
     * The pair that actually pins the semantics down is <b>"13"</b>: the left glyph is in the left set and the right glyph is in neither, so requiring both sides leaves it alone
     * while matching on either side would close it up. "23", which the oracle's own description reaches for, turns out not to discriminate - it matches neither side, so it stays
     * unkerned either way. Both are asserted, but only the first is load-bearing.
     */
    @Test
    public void testBothSidesMustMatchTheSameRule() throws Exception {
        String rule = "<hkern u1=\"1\" u2=\"2\" g1=\"gl_3\" g2=\"gl_4\" k=\"1500\"/>";
        assertKerned(rule, "12", 250, 1500);
        assertKerned(rule, "34", 750, 1500);
        assertKerned(rule, "13", 250, 0);
        assertKerned(rule, "23", 1500, 0);
    }

    /** A pair is directional: the rule for "12" must not also close up "21". */
    @Test
    public void testKerningIsDirectional() throws Exception {
        assertKerned("<hkern u1=\"1\" u2=\"2\" k=\"1000\"/>", "21", 1500, 0);
    }

    /** A font declaring no pairs at all must lay out exactly as it did before #136. */
    @Test
    public void testAFontWithNoKerningIsUnchanged() throws Exception {
        assertKerned("", "12", 250, 0);
    }

    /** Kerning shrinks the advance total, so {@code text-anchor} has less to shift by. */
    @Test
    public void testTextAnchorCentresOnTheKernedTotal() throws Exception {
        Node kerned = render("<hkern u1=\"1\" u2=\"2\" k=\"1000\"/>",
            "<text x='100' y='100' text-anchor='middle' font-family='TestFont' font-size='20'>12</text>");

        // advances 250 + 1500 less 1000 of kerning = 750 units, half of which is the shift
        assertThat(kerned.getTranslateX(), closeTo(-0.5 * 750 * SCALE, 0.01));
    }

    /**
     * An explicit per-glyph {@code x} positions absolutely, so there is no carried gap for kerning to tighten - applying it anyway would drag the glyph off the position the
     * document asked for.
     */
    @Test
    public void testAnExplicitXIsNotAdjustedByKerning() throws Exception {
        List<Node> glyphs = glyphs("<hkern u1=\"1\" u2=\"2\" k=\"1000\"/>",
            "<text x='0 60' y='100' font-family='TestFont' font-size='20'>12</text>");

        assertThat(glyphs, hasSize(2));
        assertThat(glyphs.get(1)
            .getBoundsInParent()
            .getMinX(), closeTo(60, 0.01));
    }

    // --- helpers -------------------------------------------------------------

    /**
     * Renders {@code text} under {@code rule} and checks where the second glyph lands: the first glyph's own advance, less the kerning expected. Both are given in font units, so
     * each case reads as the numbers the font declares.
     */
    private void assertKerned(String rule, String text, double firstAdvance, double expectedKern) throws Exception {
        List<Node> glyphs = glyphs(rule,
            "<text x='0' y='100' font-family='TestFont' font-size='20'>" + text + "</text>");

        assertThat(glyphs, hasSize(2));
        assertThat(glyphs.get(1)
            .getBoundsInParent()
            .getMinX(),
            closeTo((firstAdvance - expectedKern) * SCALE, 0.01));
    }

    private List<Node> glyphs(String rule, String body) throws Exception {
        Node rendered = render(rule, body);
        return rendered instanceof Group group ? List.copyOf(group.getChildren()) : List.of(rendered);
    }

    /**
     * Each distinct rule gets its own font file, because {@link SvgFontResolver} caches by resolved URI for the life of the JVM - two rules written to one path within a test would
     * silently both see whichever loaded first.
     */
    private Node render(String rule, String body) throws Exception {
        String fontName = "testfont-" + Integer.toHexString(rule.hashCode()) + ".svg";
        String font = """
                        <svg xmlns="http://www.w3.org/2000/svg">
                          <defs>
                            <font id="test" horiz-adv-x="1000">
                              <font-face font-family="TestFont" units-per-em="1000" ascent="800" descent="-200"/>
                        %s
                        %s
                            </font>
                          </defs>
                        </svg>
                        """.formatted(GLYPHS, rule);
        Files.writeString(documents.resolve(fontName), font, StandardCharsets.UTF_8);

        String document = """
                        <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="200" height="200">
                          <defs>
                            <font-face font-family="TestFont">
                              <font-face-src><font-face-uri xlink:href="%s#test"/></font-face-src>
                            </font-face>
                          </defs>
                          %s
                        </svg>
                        """.formatted(fontName, body.replace("<text ", "<text id='subject' "));
        Path file = documents.resolve("document-" + Integer.toHexString(rule.hashCode()) + ".svg");
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
