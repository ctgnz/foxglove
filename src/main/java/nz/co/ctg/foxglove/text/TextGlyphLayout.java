package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgTextAttributes;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgInheritedStyle;
import nz.co.ctg.foxglove.geometry.PathLengthLookup;
import nz.co.ctg.foxglove.geometry.SvgPathData;
import nz.co.ctg.foxglove.shape.SvgPath;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * Positions the runs {@link TextRunBuilder} (#27) produces onto one baseline, applying #28's positioning
 * attributes: list-valued {@code x}/{@code y}/{@code dx}/{@code dy} and {@code rotate} (each indexing into the
 * characters of the run's own owning element - see {@link ISvgGlyphPositioned}), {@code text-anchor}, and an
 * em-based approximation of {@code baseline-shift}/{@code alignment-baseline}/{@code dominant-baseline} (JavaFX's
 * {@code Text} exposes no font ascent/descent metrics, so this is not a precise implementation of the CSS baseline
 * algorithm - it covers the common superscript/subscript/centring cases).
 * <p>
 * A run whose owner declares none of these lists stays a single {@code Text} node, exactly as in #27; only a run
 * that actually uses per-character positioning is split into one node per Unicode code point. {@code textLength}/
 * {@code lengthAdjust} and {@code letter-spacing}/{@code word-spacing}/{@code kerning} are out of scope - the
 * former is absent from #28's acceptance criteria, the latter would need their own per-glyph advance model.
 * <p>
 * A run under a {@code <textPath>} (#29) is laid out differently: always split per code point regardless of its
 * own {@code x}/{@code y}/{@code dx}/{@code dy}/{@code rotate} (not applied while on a path - out of scope), each
 * glyph placed and rotated to match the referenced {@code <path>}'s tangent at an accumulating arc length starting
 * from {@code startOffset}, via {@link nz.co.ctg.foxglove.geometry.PathLengthLookup}. The ordinary linear cursor is
 * synced to the last glyph's position afterward (an approximation - it cannot itself follow the curve) so text
 * after a {@code </textPath>} continues from roughly there rather than jumping back to wherever the linear flow
 * was before the path started.
 */
final class TextGlyphLayout {

    private TextGlyphLayout() {
    }

    /**
     * One laid-out character (or whole run): the node to draw, and how far it advances the cursor.
     * <p>
     * The advance is carried rather than measured back off the node, because the two glyph sources disagree about
     * where it comes from. A {@code Text} knows its own rendered width; an SVG-font glyph's advance is declared by
     * the font as {@code horiz-adv-x} and is <b>not</b> its outline's width - a space has a real advance and no
     * outline at all. Reading bounds back would work for one source and silently mis-space the other.
     */
    private record Glyph(Node node, double advance) {
    }

    static Node layout(SvgText root, RenderContext context) {
        List<TextRunBuilder.Run> runs = TextRunBuilder.build(root, context);
        List<Node> nodes = new ArrayList<>();
        // The <text>'s own x/y seed the cursor, rather than being left to arrive when the <text> happens to own the
        // first run (#142). It only owns one when it has character data of its own, so a <text> whose content opens
        // with a child element - <text x="0" y="100"><tspan>A</tspan></text>, a thoroughly ordinary shape - would
        // otherwise start that child at the origin and lose the position entirely.
        double cursorX = firstPosition(root, ISvgGlyphPositioned::getX);
        double cursorY = firstPosition(root, ISvgGlyphPositioned::getY);
        double totalAdvance = 0;
        double anchorStartX = 0;
        boolean anchorStarted = false;
        SvgTextPath currentPath = null;
        PathLengthLookup currentPathLookup = null;
        double pathCursor = 0;
        // How many of an owner's own x/y/dx/dy/rotate list entries have already been consumed - an element can
        // contribute more than one run (text directly inside it, both before and after a nested child), and the
        // list indexes that element's *own* characters as a whole, not each run independently.
        Map<AbstractSvgStylable, Integer> ownerIndex = new IdentityHashMap<>();

        for (TextRunBuilder.Run run : runs) {
            if (run.enclosingPath() != null) {
                if (run.enclosingPath() != currentPath) {
                    currentPath = run.enclosingPath();
                    currentPathLookup = resolvePathLookup(currentPath, context);
                    pathCursor = currentPathLookup == null ? 0 : resolveStartOffset(currentPath, currentPathLookup);
                }
                if (currentPathLookup != null) {
                    SvgFontGlyphs pathFont = svgFontFor(run, context);
                    double pathFontSize = fontSizeFor(run, context);
                    List<String> pathPieces = codePoints(run.text());
                    for (int k = 0; k < pathPieces.size(); k++) {
                        String piece = pathPieces.get(k);
                        if (k > 0 && pathFont != null) {
                            pathCursor -= pathFont.kerningBetween(pathPieces.get(k - 1), piece, pathFontSize);
                        }
                        Point2D point = currentPathLookup.pointAt(pathCursor);
                        double angle = currentPathLookup.angleAt(pathCursor);
                        Glyph glyph = glyphOf(piece, run, pathFont, pathFontSize, point.getX(), point.getY(),
                            angle, point.getX(), point.getY());
                        if (glyph.node() != null) {
                            nodes.add(glyph.node());
                        }

                        pathCursor += glyph.advance();
                        // Text after </textPath> cannot follow the curve, but leaving the linear cursor where it
                        // was before the path would make it overlap the path's own text instead - continuing in a
                        // straight line from the last glyph is only an approximation, but a far less broken one.
                        cursorX = point.getX() + glyph.advance();
                        cursorY = point.getY();
                    }
                }
                continue;
            }
            List<Double> xs = positions(run.owner(), ISvgGlyphPositioned::getX);
            List<Double> ys = positions(run.owner(), ISvgGlyphPositioned::getY);
            List<Double> dxs = positions(run.owner(), ISvgGlyphPositioned::getDx);
            List<Double> dys = positions(run.owner(), ISvgGlyphPositioned::getDy);
            List<Double> rotates = positions(run.owner(), ISvgGlyphPositioned::getRotate);
            // A single x/y/dx/dy value positions the run's start exactly like the pre-#28 scalar attributes did -
            // splitting into glyphs only matters once there is more than one position to assign, or for `rotate`,
            // which always rotates each character individually rather than the run as one rigid block.
            boolean perGlyph = xs.size() > 1 || ys.size() > 1 || dxs.size() > 1 || dys.size() > 1 || !rotates.isEmpty();
            SvgFontGlyphs runFont = svgFontFor(run, context);
            double runFontSize = fontSizeFor(run, context);
            // an SVG font draws one Path per character, so a run using one is always split even when no positioning
            // list asks for it - there is no single node that could carry the whole string
            List<String> pieces = perGlyph || runFont != null ? codePoints(run.text()) : List.of(run.text());
            int baseIndex = ownerIndex.getOrDefault(run.owner(), 0);

            for (int k = 0; k < pieces.size(); k++) {
                int i = baseIndex + k;
                Double explicitX = i < xs.size() ? xs.get(i) : null;
                Double explicitY = i < ys.size() ? ys.get(i) : null;
                double dx = i < dxs.size() ? dxs.get(i) : 0.0;
                double dy = i < dys.size() ? dys.get(i) : 0.0;
                Double rotate = rotates.isEmpty() ? null : rotates.get(Math.min(i, rotates.size() - 1));

                // Kerning tightens the gap left by the previous glyph's advance, so it applies only where the cursor
                // is actually carrying that gap - an explicit x positions the glyph absolutely and is left alone.
                double kern = k > 0 && runFont != null && explicitX == null
                    ? runFont.kerningBetween(pieces.get(k - 1), pieces.get(k), runFontSize)
                    : 0;
                double flowX = (explicitX != null ? explicitX : cursorX - kern) + dx;
                double flowY = (explicitY != null ? explicitY : cursorY) + dy;
                double displayY = flowY + baselineOffset(run, runFontSize);

                Glyph glyph = glyphOf(pieces.get(k), run, runFont, runFontSize, flowX, displayY,
                    rotate, flowX, displayY);
                if (glyph.node() != null) {
                    nodes.add(glyph.node());
                }
                if (!anchorStarted) {
                    anchorStartX = flowX;
                    anchorStarted = true;
                }
                totalAdvance = Math.max(totalAdvance, flowX + glyph.advance() - anchorStartX);

                cursorX = flowX + glyph.advance();
                cursorY = flowY;
            }
            ownerIndex.put(run.owner(), baseIndex + pieces.size());
        }

        Node result = nodes.size() == 1 ? nodes.get(0) : groupOf(nodes);
        applyTextAnchor(root, context, totalAdvance, result);
        result.setId(root.getId());
        root.applyTransforms(result);
        return result;
    }

    /**
     * The referenced {@code <path>}'s geometry, flattened and indexed for arc length (#29) - null if the reference
     * does not resolve to a {@code <path>}, or that path has no usable data, in which case the {@code <textPath>}
     * simply renders no text rather than guessing a position.
     */
    private static PathLengthLookup resolvePathLookup(SvgTextPath path, RenderContext context) {
        if (context.getElementIndex() == null) {
            return null;
        }
        return context.getElementIndex().resolve(path.getXlinkHref(), SvgPath.class)
            .map(referenced -> SvgPathData.flatten(referenced.getD()))
            .filter(points -> !points.isEmpty())
            .map(PathLengthLookup::of)
            .orElse(null);
    }

    /**
     * {@code startOffset} as an arc length: a bare number is user units along the path, a percentage is a fraction
     * of the path's total length - the same number-or-percentage idiom used for gradient offsets (see
     * {@code ISvgGradientElement.parseNumberOrPercentage}).
     */
    private static double resolveStartOffset(SvgTextPath path, PathLengthLookup lookup) {
        String raw = StringUtils.trimToEmpty(path.getStartOffset());
        if (raw.isEmpty()) {
            return 0;
        }
        boolean percentage = raw.endsWith("%");
        Double value = parseDouble(percentage ? raw.substring(0, raw.length() - 1) : raw);
        if (value == null) {
            return 0;
        }
        return percentage ? (value / 100.0) * lookup.getTotalLength() : value;
    }

    private static List<Double> positions(AbstractSvgStylable owner, Function<ISvgGlyphPositioned, List<Double>> getter) {
        return owner instanceof ISvgGlyphPositioned positioned ? getter.apply(positioned) : List.of();
    }

    /**
     * The first entry of one of {@code root}'s own positioning lists, or 0 where it declares none - the origin the
     * flow starts from (#142). Only the first matters here: any further entries are consumed per-glyph by the run
     * that owns them, through {@link #positions}.
     */
    private static double firstPosition(SvgText root, Function<ISvgGlyphPositioned, List<Double>> getter) {
        List<Double> values = positions(root, getter);
        return values.isEmpty() ? 0 : values.get(0);
    }

    private static List<String> codePoints(String text) {
        return text.codePoints().mapToObj(Character::toString).toList();
    }

    private static Group groupOf(List<Node> nodes) {
        Group group = new Group();
        group.getChildren().addAll(nodes);
        return group;
    }

    /**
     * One character as a node plus its advance, from whichever source the run's font resolves to.
     * <p>
     * The SVG-font branch builds its transforms as a list rather than using {@code translateX}/{@code translateY},
     * deliberately: JavaFX applies those node properties <i>outside</i> everything in the transforms list, so a
     * {@code Rotate} added there would pivot in the glyph's own untranslated space rather than about the baseline
     * point. Ordering them explicitly - rotate, then translate, then scale, outermost first - keeps the pivot where
     * the caller meant it. (The same trap #19 hit composing {@code <use>}'s x/y with its own transform.)
     */
    private static Glyph glyphOf(String piece, TextRunBuilder.Run run, SvgFontGlyphs font, double fontSize,
        double x, double y, Double rotation, double pivotX, double pivotY) {
        if (font == null) {
            Text node = new Text(piece);
            run.owner().applyGraphicsProperties(run.ownerContext(), node);
            run.owner().applyTextProperties(run.ownerContext(), node);
            node.setX(x);
            node.setY(y);
            if (rotation != null) {
                node.getTransforms().add(new Rotate(rotation, pivotX, pivotY));
            }
            return new Glyph(node, node.getLayoutBounds().getWidth());
        }

        Node outline = font.glyphFor(piece, fontSize);
        double advance = font.advanceFor(piece, fontSize);
        if (outline == null) {
            // a space: a real advance, nothing to draw
            return new Glyph(null, advance);
        }
        run.owner().applyGraphicsProperties(run.ownerContext(), (javafx.scene.shape.Shape) outline);
        List<Transform> transforms = new ArrayList<>();
        if (rotation != null) {
            transforms.add(new Rotate(rotation, pivotX, pivotY));
        }
        transforms.add(new Translate(x, y));
        transforms.addAll(outline.getTransforms());
        outline.getTransforms().setAll(transforms);
        return new Glyph(outline, advance);
    }

    /** The SVG font this run's {@code font-family} names, or null to render through the JavaFX text system. */
    private static SvgFontGlyphs svgFontFor(TextRunBuilder.Run run, RenderContext context) {
        return SvgFontResolver.resolve(SvgInheritedStyle.resolve(run.ownerContext(), run.owner()).getFontFamily(), context);
    }

    private static double fontSizeFor(TextRunBuilder.Run run, RenderContext context) {
        return ISvgTextAttributes.resolveFontSize(SvgInheritedStyle.resolve(run.ownerContext(), run.owner())).pixels();
    }

    /**
     * Shifts the whole result horizontally so it is centred ({@code middle}) or ends ({@code end}) at the flow's
     * start position, rather than beginning there ({@code start}, the initial value) - applied as a
     * {@code translateX} on the finished node/group rather than by rewriting each glyph's own {@code x} and any
     * {@code Rotate} pivot, which would otherwise need recomputing too.
     */
    private static void applyTextAnchor(SvgText root, RenderContext context, double totalWidth, Node result) {
        if (totalWidth <= 0) {
            return;
        }
        String anchor = StringUtils.trimToEmpty(SvgInheritedStyle.resolve(context, root).getTextAnchor());
        double fraction = "middle".equalsIgnoreCase(anchor) ? 0.5 : "end".equalsIgnoreCase(anchor) ? 1.0 : 0.0;
        if (fraction == 0.0) {
            return;
        }
        result.setTranslateX(result.getTranslateX() - fraction * totalWidth);
    }

    /**
     * An em-based approximation of {@code baseline-shift}/{@code alignment-baseline}/{@code dominant-baseline},
     * confirmed with the user as the intended scope: JavaFX's {@code Text} has no font ascent/descent metrics to
     * implement the CSS baseline-alignment algorithm precisely.
     */
    private static double baselineOffset(TextRunBuilder.Run run, double fontSize) {
        // baseline-shift/alignment-baseline/dominant-baseline are not inherited - like text-decoration, each
        // applies only to the element that declares it, so they are read from the owner directly rather than
        // through the resolved (inherited-only) style.
        AbstractSvgStylable style = run.owner();
        double offset = 0;
        String shift = StringUtils.trimToEmpty(style.getBaselineShift());
        if ("super".equalsIgnoreCase(shift)) {
            offset -= 0.30 * fontSize;
        } else if ("sub".equalsIgnoreCase(shift)) {
            offset += 0.20 * fontSize;
        } else if (shift.endsWith("%")) {
            Double percentage = parseDouble(shift.substring(0, shift.length() - 1));
            if (percentage != null) {
                offset -= (percentage / 100.0) * fontSize;
            }
        } else if (!shift.isEmpty() && !"baseline".equalsIgnoreCase(shift)) {
            Double pixels = parseDouble(shift);
            if (pixels != null) {
                offset -= pixels;
            }
        }
        String baseline = StringUtils.trimToEmpty(style.getAlignmentBaseline());
        if (baseline.isEmpty() || "auto".equalsIgnoreCase(baseline)) {
            baseline = StringUtils.trimToEmpty(style.getDominantBaseline());
        }
        if ("middle".equalsIgnoreCase(baseline) || "central".equalsIgnoreCase(baseline)) {
            offset -= 0.30 * fontSize;
        } else if ("hanging".equalsIgnoreCase(baseline) || "text-before-edge".equalsIgnoreCase(baseline)) {
            offset += 0.80 * fontSize;
        } else if ("text-after-edge".equalsIgnoreCase(baseline)) {
            offset -= 0.20 * fontSize;
        }
        return offset;
    }

    private static Double parseDouble(String value) {
        try {
            return Double.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
