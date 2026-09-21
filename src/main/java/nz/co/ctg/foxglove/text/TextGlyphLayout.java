package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgTextAttributes;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgInheritedStyle;
import nz.co.ctg.foxglove.geometry.PathLengthLookup;
import nz.co.ctg.foxglove.geometry.SvgPathData;
import nz.co.ctg.foxglove.shape.SvgPath;

/**
 * Positions the runs {@link TextRunBuilder} (#27) produces onto one baseline, applying #28's positioning attributes: list-valued {@code x}/{@code y}/{@code dx}/{@code dy} and
 * {@code rotate} (see {@link ISvgGlyphPositioned}), {@code text-anchor}, and an em-based approximation of {@code baseline-shift}/
 * {@code alignment-baseline}/{@code dominant-baseline} (JavaFX's {@code Text} exposes no font ascent/descent metrics, so this is not a precise implementation of the CSS baseline
 * algorithm - it covers the common superscript/subscript/centring cases).
 * <p>
 * A list addresses the characters of its element's <b>whole subtree, including descendants</b> (#143), not just those the element owns directly -
 * {@code <text x="10 20 30"><tspan>AB</tspan>C</text>} places A/B/C at 10/20/30 even though {@code <tspan>} has no list of its own. Each run is resolved against its full ancestor
 * chain, innermost first: the nearest element with a list that still reaches this character wins, and a list that is present but already exhausted at this index falls through to
 * the next element out rather than stopping there. Bookkeeping (how many characters of an element's own list have been consumed) advances for every element in a run's chain, not
 * just its immediate owner - otherwise an ancestor's own later run would re-offer indices its descendants' runs already used.
 * <p>
 * A run whose whole chain declares none of these lists stays a single {@code Text} node, exactly as in #27; only a run that actually uses per-character positioning is split into
 * one node per Unicode code point. {@code
 * textLength}/{@code lengthAdjust} and {@code letter-spacing}/{@code word-spacing}/{@code kerning} are out of scope - the former is absent from #28's acceptance criteria, the
 * latter would need their own per-glyph advance model.
 * <p>
 * A run under a {@code <textPath>} (#29) is laid out differently: always split per code point regardless of its own {@code x}/{@code y}/{@code dx}/{@code dy}/{@code rotate} (not
 * applied while on a path - out of scope), each glyph placed and rotated to match the referenced {@code <path>}'s tangent at an accumulating arc length starting from
 * {@code startOffset}, via {@link nz.co.ctg.foxglove.geometry.PathLengthLookup}. The ordinary linear cursor is synced to the last glyph's position afterward (an approximation - it
 * cannot itself follow the curve) so text after a {@code </textPath>} continues from roughly there rather than jumping back to wherever the linear flow was before the path
 * started.
 * <p>
 * {@code writing-mode="tb"}/{@code "tb-rl"} (#139) swaps which axis the cursor advances along: each character still places at {@code (x, y)} the same way, but the <i>next</i>
 * character's position comes from stepping {@code y} forward by one em (the specification's own default for {@code vert-adv-y}, since no suite document declares one) rather than
 * {@code x} forward by the glyph's own width, and is centred horizontally on the column rather than starting flush against it (the usual convention for vertical CJK layout, and
 * this renderer's approximation of {@code vert-origin-x}'s own default of half the glyph's advance). Deliberately narrower than #139's own full scope: no suite document uses an
 * SVG font, {@code glyph-orientation-vertical} other than {@code 0} (upright, unrotated glyphs - the only value applied), or a positioning list together with vertical text, so
 * none of those are handled - a run resolving to an SVG font falls through to this class's ordinary horizontal handling regardless of {@code writing-mode}, which is honest about
 * what is actually supported rather than silently mispositioning glyphs a font's own vertical metrics were never read for.
 */
final class TextGlyphLayout {

    private TextGlyphLayout() {
    }

    /**
     * One laid-out character (or whole run): the node to draw, and how far it advances the cursor.
     * <p>
     * The advance is carried rather than measured back off the node, because neither source reports it as bounds. An SVG-font glyph's advance is declared by the font as
     * {@code horiz-adv-x} and is <b>not</b> its outline's width; and since #230 a {@code Text} reports the ink it draws rather than its LOGICAL line box, which is where the
     * advance used to be readable. A space makes the point for both: it advances the cursor and draws nothing at all.
     */
    private record Glyph(Node node, double advance) {
    }

    static Node layout(SvgText root, RenderContext context) {
        List<TextRunBuilder.Run> runs = TextRunBuilder.build(root, context);
        List<Node> nodes = new ArrayList<>();
        double cursorX = 0;
        double cursorY = 0;
        double totalAdvance = 0;
        // The flow axis's own start coordinate - x normally, y under writing-mode="tb"/"tb-rl" (#139); only one
        // axis is ever active for a given <text> element in practice, so one variable serves both.
        double anchorFlowStart = 0;
        boolean anchorStarted = false;
        SvgTextPath currentPath = null;
        PathLengthLookup currentPathLookup = null;
        double pathCursor = 0;
        // How many of each element's own x/y/dx/dy/rotate list entries have already been consumed - keyed by every
        // positioning element in a run's ancestor chain, not just its immediate owner, since a list addresses a
        // whole subtree's characters (#143): <text x="30"><tspan>A</tspan>B</text> must not let "B" re-consume
        // x[0] just because <tspan>'s "A" was never counted against <text>'s own index. This also subsumes #142's
        // former special-cased cursor seed - the root is always the outermost element of every chain, so a leading
        // child with nothing of its own falls through to it via the same mechanism, with no separate seed needed.
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
                    // A run inside a <textPath> isn't itself positioned by x/y/dx/dy/rotate (out of scope, per the
                    // class doc), but its characters still occupy indices in an enclosing element's subtree - an
                    // ancestor's list must not be re-offered characters a <textPath> already consumed (#143).
                    bumpAncestors(ownerIndex, run.ancestors(), pathPieces.size());
                }
                continue;
            }
            // Innermost first: a run's own owner gets to decide before any ancestor is even consulted (#143). The
            // chain is root-to-owner, so this is simply that order reversed.
            List<AbstractSvgStylable> innermostFirst = new ArrayList<>(run.ancestors());
            Collections.reverse(innermostFirst);
            // A single x/y/dx/dy value positions the run's start exactly like the pre-#28 scalar attributes did -
            // splitting into glyphs only matters once there is more than one position to assign anywhere in the
            // chain, or for `rotate`, which always rotates each character individually. Checked across every
            // ancestor, not just the owner: a <tspan> with no list of its own inside a multi-valued <text> still
            // has to split, or the inherited per-character values it falls through to (#143) would never land.
            boolean perGlyph = run.ancestors()
                .stream()
                .anyMatch(TextGlyphLayout::hasMultiValuedPositioning);
            SvgFontGlyphs runFont = svgFontFor(run, context);
            double runFontSize = fontSizeFor(run, context);
            // an <altGlyph> draws the glyphs it names instead of its own characters (#138); an unresolved reference
            // leaves the run to render those characters exactly as it did before
            List<SvgAltGlyphs.Substitute> substitutes = run.owner() instanceof SvgAltGlyph altGlyph
                ? SvgAltGlyphs.resolve(altGlyph, context)
                : null;
            // #139: vertical layout is only handled for plain (non-SVG-font) text - see this class's own javadoc for
            // why an SVG font falls through to the ordinary horizontal path regardless of writing-mode.
            boolean vertical = runFont == null && substitutes == null && writingModeVertical(run, context);
            // an SVG font draws one Path per character, so a run using one is always split even when no positioning
            // list asks for it - there is no single node that could carry the whole string; vertical text is always
            // split too, since JavaFX's Text has no notion of laying its own characters out top-to-bottom.
            List<String> pieces = perGlyph || vertical || runFont != null || substitutes != null ? codePoints(run.text())
                : List.of(run.text());
            // a substitution replaces the run's characters wholesale, so the glyphs it names drive the loop instead
            int count = substitutes != null ? substitutes.size() : pieces.size();

            for (int k = 0; k < count; k++) {
                Double explicitX = resolvePositionedValue(innermostFirst, ownerIndex, k, ISvgGlyphPositioned::getX, false);
                Double explicitY = resolvePositionedValue(innermostFirst, ownerIndex, k, ISvgGlyphPositioned::getY, false);
                Double explicitDx = resolvePositionedValue(innermostFirst, ownerIndex, k, ISvgGlyphPositioned::getDx, false);
                Double explicitDy = resolvePositionedValue(innermostFirst, ownerIndex, k, ISvgGlyphPositioned::getDy, false);
                double dx = explicitDx == null ? 0.0 : explicitDx;
                double dy = explicitDy == null ? 0.0 : explicitDy;
                Double rotate = resolvePositionedValue(innermostFirst, ownerIndex, k, ISvgGlyphPositioned::getRotate, true);

                // Kerning tightens the gap left by the previous glyph's advance, so it applies only where the cursor
                // is actually carrying that gap - an explicit x positions the glyph absolutely and is left alone.
                // Substituted glyphs are named individually rather than spelt, so no character pair applies.
                // Vertical text never kerns (#139) - kerning is a horizontal-advance concept, and no suite document
                // declares vertical kerning data for this class to read even if it did.
                double kern = k > 0 && runFont != null && explicitX == null && substitutes == null && !vertical
                    ? runFont.kerningBetween(pieces.get(k - 1), pieces.get(k), runFontSize)
                    : 0;
                double flowX = (explicitX != null ? explicitX : cursorX - kern) + dx;
                double flowY = (explicitY != null ? explicitY : cursorY) + dy;
                double displayY = flowY + baselineOffset(run, runFontSize);

                Glyph glyph = substitutes != null
                    ? substituteGlyph(substitutes.get(k), run, runFontSize, flowX, displayY, rotate)
                    : glyphOf(pieces.get(k), run, runFont, runFontSize, flowX, displayY, rotate, flowX, displayY);
                if (glyph.node() != null) {
                    if (vertical) {
                        // Centred on the column (an approximation of vert-origin-x's own default, half the glyph's
                        // advance) rather than starting flush against it, the usual convention for vertical layout.
                        // glyphOf already measured this width as the glyph's own advance for the plain-Text branch
                        // vertical text is restricted to (see this class's own javadoc), so no extra measurement.
                        glyph.node()
                            .setTranslateX(glyph.node()
                                .getTranslateX() - glyph.advance() / 2);
                    }
                    nodes.add(glyph.node());
                }
                if (!anchorStarted) {
                    anchorFlowStart = vertical ? flowY : flowX;
                    anchorStarted = true;
                }
                // One em, not glyph.advance() (the glyph's own width - the wrong axis here): the specification's
                // own default for vert-adv-y, used unconditionally since no suite document declares a real one.
                double advance = vertical ? runFontSize : glyph.advance();
                totalAdvance = Math.max(totalAdvance, (vertical ? flowY : flowX) + advance - anchorFlowStart);

                if (vertical) {
                    cursorX = flowX;
                    cursorY = flowY + advance;
                } else {
                    cursorX = flowX + advance;
                    cursorY = flowY;
                }
            }
            bumpAncestors(ownerIndex, run.ancestors(), count);
        }

        Node result = nodes.size() == 1 ? nodes.get(0) : groupOf(nodes);
        applyTextAnchor(root, context, totalAdvance, result);
        result.setId(root.getId());
        root.applyTransforms(result);
        return result;
    }

    /**
     * The referenced {@code <path>}'s geometry, flattened and indexed for arc length (#29) - null if the reference does not resolve to a {@code <path>}, or that path has no usable
     * data, in which case the {@code <textPath>} simply renders no text rather than guessing a position.
     */
    private static PathLengthLookup resolvePathLookup(SvgTextPath path, RenderContext context) {
        if (context.getElementIndex() == null) {
            return null;
        }
        return context.getElementIndex()
            .resolve(path.getXlinkHref(), SvgPath.class)
            .map(referenced -> SvgPathData.flatten(referenced.getD()))
            .filter(points -> !points.isEmpty())
            .map(PathLengthLookup::of)
            .orElse(null);
    }

    /**
     * {@code startOffset} as an arc length: a bare number is user units along the path, a percentage is a fraction of the path's total length - the same number-or-percentage idiom
     * used for gradient offsets (see {@code ISvgGradientElement.parseNumberOrPercentage}).
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

    /** Whether {@code element} has any list that would force per-glyph splitting (#143) - see {@code perGlyph}. */
    private static boolean hasMultiValuedPositioning(AbstractSvgStylable element) {
        return positions(element, ISvgGlyphPositioned::getX).size() > 1
               || positions(element, ISvgGlyphPositioned::getY).size() > 1
               || positions(element, ISvgGlyphPositioned::getDx).size() > 1
               || positions(element, ISvgGlyphPositioned::getDy).size() > 1
               || !positions(element, ISvgGlyphPositioned::getRotate).isEmpty();
    }

    /**
     * The value one positioning list contributes to the {@code k}-th character of a run, searched from the run's own owner outward through its ancestors, or null where nothing in
     * the chain has one (#143).
     * <p>
     * An element's list, once found non-empty, does not automatically win outright: if it does not yet reach index {@code k} within <i>that element's own</i> running count, the
     * search keeps going outward rather than stopping - {@code <tspan x="1">AB</tspan>} inside {@code <text x="100 200 300">} must let "B" (beyond the tspan's single entry) fall
     * through to the {@code <text>}'s own list, even though the {@code <tspan>} does have a list of its own. {@code rotate} is the one exception ({@code holdLast}): its existing
     * behaviour never runs out once present (the last value repeats), so the first ancestor with any entries at all wins outright.
     * <p>
     * {@code ownerIndex} is read, not written, here - safe because a run's own bookkeeping update happens only after every character of it has been resolved, so the map does not
     * change mid-run.
     */
    private static Double resolvePositionedValue(List<AbstractSvgStylable> innermostFirst,
                                                 Map<AbstractSvgStylable, Integer> ownerIndex, int k, Function<ISvgGlyphPositioned, List<Double>> getter, boolean holdLast) {
        for (AbstractSvgStylable element : innermostFirst) {
            List<Double> values = positions(element, getter);
            if (values.isEmpty()) {
                continue;
            }
            int index = ownerIndex.getOrDefault(element, 0) + k;
            if (holdLast) {
                return values.get(Math.min(index, values.size() - 1));
            }
            if (index < values.size()) {
                return values.get(index);
            }
            // this element does have a list, but it doesn't reach this character - keep searching outward
        }
        return null;
    }

    /**
     * Records that {@code count} more characters have flowed through every element in {@code ancestors}' subtree (#143) - not just the run's immediate owner, so that when an
     * ancestor later owns a run of its own directly, it correctly resumes from where its whole subtree left off rather than only from what it itself has produced.
     */
    private static void bumpAncestors(Map<AbstractSvgStylable, Integer> ownerIndex, List<AbstractSvgStylable> ancestors, int count) {
        for (AbstractSvgStylable ancestor : ancestors) {
            ownerIndex.merge(ancestor, count, Integer::sum);
        }
    }

    private static List<String> codePoints(String text) {
        return text.codePoints()
            .mapToObj(Character::toString)
            .toList();
    }

    private static Group groupOf(List<Node> nodes) {
        Group group = new Group();
        group.getChildren()
            .addAll(nodes);
        return group;
    }

    /**
     * One character as a node plus its advance, from whichever source the run's font resolves to.
     * <p>
     * The SVG-font branch builds its transforms as a list rather than using {@code translateX}/{@code translateY}, deliberately: JavaFX applies those node properties
     * <i>outside</i> everything in the transforms list, so a {@code Rotate} added there would pivot in the glyph's own untranslated space rather than about the baseline point.
     * Ordering them explicitly - rotate, then translate, then scale, outermost first - keeps the pivot where the caller meant it. (The same trap #19 hit composing {@code <use>}'s
     * x/y with its own transform.)
     */
    private static Glyph glyphOf(String piece, TextRunBuilder.Run run, SvgFontGlyphs font, double fontSize,
                                 double x, double y, Double rotation, double pivotX, double pivotY) {
        if (font == null) {
            Text node = new Text(piece);
            run.owner()
                .applyGraphicsProperties(run.ownerContext(), node);
            run.owner()
                .applyTextProperties(run.ownerContext(), node);
            node.setX(x);
            node.setY(y);
            if (rotation != null) {
                node.getTransforms()
                    .add(new Rotate(rotation, pivotX, pivotY));
            }
            // The advance has to be read while the node still reports its LOGICAL line box, because that is
            // what carries the font's advance width - a space has one and no ink at all. Only then switch to
            // VISUAL, so what the node reports as its bounds is what it actually draws.
            double advance = node.getLayoutBounds()
                .getWidth();
            node.setBoundsType(TextBoundsType.VISUAL);
            return new Glyph(node, advance);
        }

        Node outline = font.glyphFor(piece, fontSize);
        double advance = font.advanceFor(piece, fontSize);
        if (outline == null) {
            // a space: a real advance, nothing to draw
            return new Glyph(null, advance);
        }
        run.owner()
            .applyGraphicsProperties(run.ownerContext(), (javafx.scene.shape.Shape) outline);
        font.descaleStroke((javafx.scene.shape.Shape) outline, fontSize);
        List<Transform> transforms = new ArrayList<>();
        if (rotation != null) {
            transforms.add(new Rotate(rotation, pivotX, pivotY));
        }
        transforms.add(new Translate(x, y));
        transforms.addAll(outline.getTransforms());
        outline.getTransforms()
            .setAll(transforms);
        return new Glyph(outline, advance);
    }

    /**
     * One glyph an {@code <altGlyph>} named, drawn in place of a character it would otherwise have spelt (#138).
     * <p>
     * Scale and advance come from the font that <i>owns the glyph</i>, carried on the substitute, not from the run's own {@code font-family}: the two are routinely different
     * fonts, and {@code text-altglyph-01-b} makes the point by substituting glyphs whose em is 8 units into text set in Arial.
     */
    private static Glyph substituteGlyph(SvgAltGlyphs.Substitute substitute, TextRunBuilder.Run run, double fontSize,
                                         double x, double y, Double rotation) {
        SvgFontGlyphs font = substitute.font();
        Node outline = font.glyphNodeOf(substitute.glyph(), fontSize);
        double advance = font.advanceOf(substitute.glyph(), fontSize);
        if (outline == null) {
            return new Glyph(null, advance);
        }
        run.owner()
            .applyGraphicsProperties(run.ownerContext(), (javafx.scene.shape.Shape) outline);
        // #145: the substituted glyph's own outline scale, not the run's font, is what the paint transform carries.
        font.descaleStroke((javafx.scene.shape.Shape) outline, fontSize);
        List<Transform> transforms = new ArrayList<>();
        if (rotation != null) {
            transforms.add(new Rotate(rotation, x, y));
        }
        transforms.add(new Translate(x, y));
        transforms.addAll(outline.getTransforms());
        outline.getTransforms()
            .setAll(transforms);
        return new Glyph(outline, advance);
    }

    /** The SVG font this run's {@code font-family} names, or null to render through the JavaFX text system. */
    private static SvgFontGlyphs svgFontFor(TextRunBuilder.Run run, RenderContext context) {
        return SvgFontResolver.resolve(SvgInheritedStyle.resolve(run.ownerContext(), run.owner())
            .getFontFamily(), context);
    }

    private static double fontSizeFor(TextRunBuilder.Run run, RenderContext context) {
        return ISvgTextAttributes.resolveFontSize(SvgInheritedStyle.resolve(run.ownerContext(), run.owner()))
            .pixels();
    }

    /**
     * Shifts the whole result along the flow axis so it is centred ({@code middle}) or ends ({@code end}) at the flow's start position, rather than beginning there ({@code start},
     * the initial value) - applied as a single {@code translateX}/{@code translateY} on the finished node/group rather than by rewriting each glyph's own position and any
     * {@code Rotate} pivot, which would otherwise need recomputing too. The flow axis is {@code y} under {@code writing-mode="tb"}/{@code "tb-rl"} (#139), {@code x} otherwise.
     */
    private static void applyTextAnchor(SvgText root, RenderContext context, double totalAdvance, Node result) {
        if (totalAdvance <= 0) {
            return;
        }
        SvgInheritedStyle style = SvgInheritedStyle.resolve(context, root);
        String anchor = StringUtils.trimToEmpty(style.getTextAnchor());
        double fraction = "middle".equalsIgnoreCase(anchor) ? 0.5 : "end".equalsIgnoreCase(anchor) ? 1.0 : 0.0;
        if (fraction == 0.0) {
            return;
        }
        if (isVerticalWritingMode(style.getWritingMode())) {
            result.setTranslateY(result.getTranslateY() - fraction * totalAdvance);
        } else {
            result.setTranslateX(result.getTranslateX() - fraction * totalAdvance);
        }
    }

    /**
     * Whether {@code run}'s fully-resolved (inherited) {@code writing-mode} is one of the two vertical values - {@code "tb"}/{@code "tb-rl"}, top-to-bottom - rather than a
     * horizontal one (#139). Resolved the same way {@link #svgFontFor}/{@link #fontSizeFor} resolve their own inherited properties.
     */
    private static boolean writingModeVertical(TextRunBuilder.Run run, RenderContext context) {
        return isVerticalWritingMode(SvgInheritedStyle.resolve(run.ownerContext(), run.owner())
            .getWritingMode());
    }

    private static boolean isVerticalWritingMode(String writingMode) {
        String mode = StringUtils.trimToEmpty(writingMode);
        return "tb".equalsIgnoreCase(mode) || "tb-rl".equalsIgnoreCase(mode);
    }

    /**
     * An em-based approximation of {@code baseline-shift}/{@code alignment-baseline}/{@code dominant-baseline}, confirmed with the user as the intended scope: JavaFX's
     * {@code Text} has no font ascent/descent metrics to implement the CSS baseline-alignment algorithm precisely.
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
