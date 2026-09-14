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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

/**
 * Exercises #145: a stroked SVG-font glyph renders at the {@code stroke-width} the document declared, not that
 * width multiplied by the glyph's own outline scale.
 * <p>
 * The outline scale is {@code fontSize / unitsPerEm}, applied to the {@code Path} as a {@code Scale} transform (#61)
 * - and JavaFX applies a node's own transforms to its stroke as well as its geometry. Deliberately tested at a scale
 * far from 1 (8-unit em, 40px font - scale 5), since a fixture where the two happen to coincide would pass whether
 * or not the fix is present at all.
 */
public class SvgFontStrokeScaleTest {

    @TempDir
    Path documents;

    /** Deliberately not 1000 - a small, round-number em where the scale bug is impossible to miss if reintroduced. */
    private static final String FONT = """
          <defs>
            <font id="test" horiz-adv-x="8">
              <font-face font-family="TestFont" units-per-em="8"/>
              <glyph unicode="A" horiz-adv-x="8" d="M0,0 L4,8 L8,0"/>
            </font>
          </defs>
        """;

    /** At {@code units-per-em="8"}, {@code font-size="40"} the outline scale is 5. */
    private static final double SCALE = 40.0 / 8;

    /**
     * JavaFX applies a node's own transforms to its stroke as well as its geometry, so the property value has to be
     * pre-divided by the glyph's scale - {@link javafx.scene.shape.Shape#getStrokeWidth()} itself never reflects a
     * transform, only the pixels JavaFX goes on to paint do. So what proves the fix is not the raw property equalling
     * the declared width, but the property <b>times the known scale</b> recovering it - which is what the transform
     * does at paint time, and exactly cancels what the unfixed defect multiplied in.
     */
    @Test
    public void testStrokeWidthIsNotMultipliedByTheGlyphScale() throws Exception {
        Shape glyph = renderGlyph(2);
        assertThat(glyph.getStrokeWidth() * SCALE, closeTo(2, 0.01));
    }

    /**
     * <b>The inconvenient case.</b> At scale 1 (em equal to font size), dividing by the scale and not dividing at all
     * produce the identical property value - a fixture at scale 1 cannot tell a present fix from an absent one, or
     * from one that divides by the wrong quantity entirely. {@link #testStrokeWidthIsNotMultipliedByTheGlyphScale}'s
     * scale of 5 is what actually exercises this; this case only confirms scale 1 is not itself broken by the fix.
     */
    @Test
    public void testStrokeWidthIsUnaffectedWhenTheScaleIsOne() throws Exception {
        String font = FONT.replace("units-per-em=\"8\"", "units-per-em=\"40\"");
        Shape glyph = renderGlyph(font, 2, 40);
        assertThat(glyph.getStrokeWidth(), closeTo(2, 0.01));
    }

    /** {@code stroke-dashoffset} is a length too, and suffers the identical multiplication if left uncorrected. */
    @Test
    public void testStrokeDashOffsetIsNotMultipliedByTheGlyphScale() throws Exception {
        Node rendered = render(FONT, "<text x='0' y='100' font-family='TestFont' font-size='40' "
            + "stroke='black' stroke-width='2' stroke-dashoffset='3'>A</text>");
        Shape glyph = firstGlyph(rendered);
        assertThat(glyph.getStrokeDashOffset() * SCALE, closeTo(3, 0.01));
    }

    /** As is {@code stroke-dasharray}. */
    @Test
    public void testStrokeDashArrayIsNotMultipliedByTheGlyphScale() throws Exception {
        Node rendered = render(FONT, "<text x='0' y='100' font-family='TestFont' font-size='40' "
            + "stroke='black' stroke-width='2' stroke-dasharray='3,1'>A</text>");
        Shape glyph = firstGlyph(rendered);
        assertThat(glyph.getStrokeDashArray().get(0) * SCALE, closeTo(3, 0.01));
        assertThat(glyph.getStrokeDashArray().get(1) * SCALE, closeTo(1, 0.01));
    }

    /** A font-only document (no stroke at all) is unaffected - fill is never touched by this fix. */
    @Test
    public void testAnUnstrokedGlyphIsUnaffected() throws Exception {
        Node rendered = render(FONT, "<text x='0' y='100' font-family='TestFont' font-size='40'>A</text>");
        Shape glyph = firstGlyph(rendered);
        // JavaFX's own default (1), descaled the same way - on screen this paints as 1/5px, i.e. hairline-thin,
        // which is correct: nothing in the document asked for a visible stroke at all.
        assertThat(glyph.getStrokeWidth() * SCALE, closeTo(1, 0.01));
    }

    // --- helpers -------------------------------------------------------------

    private Shape renderGlyph(double strokeWidth) throws Exception {
        return renderGlyph(FONT, strokeWidth, 40);
    }

    private Shape renderGlyph(String font, double strokeWidth, double fontSize) throws Exception {
        Node rendered = render(font, ("<text x='0' y='100' font-family='TestFont' font-size='" + fontSize + "' "
            + "stroke='black' stroke-width='" + strokeWidth + "'>A</text>"));
        return firstGlyph(rendered);
    }

    private static Shape firstGlyph(Node rendered) {
        return (Shape) (rendered instanceof Group group ? group.getChildren().get(0) : rendered);
    }

    private Node render(String font, String body) throws Exception {
        String document = """
            <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="200" height="200">
            %s
              %s
            </svg>
            """.formatted(font, body.replace("<text ", "<text id='real-subject' "));
        Path file = documents.resolve("document-" + Integer.toHexString((font + body).hashCode()) + ".svg");
        Files.writeString(file, document, StandardCharsets.UTF_8);

        SvgGraphic svg;
        try (InputStream in = Files.newInputStream(file)) {
            svg = new FoxgloveParser().parse(in);
        }
        svg.setBaseUri(file.toUri());
        Node subject = svg.createGroup().lookup("#real-subject");
        if (subject == null) {
            throw new AssertionError("the <text> rendered no node at all");
        }
        return subject;
    }

}
