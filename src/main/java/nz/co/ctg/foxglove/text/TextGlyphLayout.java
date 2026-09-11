package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgInheritedStyle;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

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
 */
final class TextGlyphLayout {

    private TextGlyphLayout() {
    }

    static Node layout(SvgText root, RenderContext context) {
        List<TextRunBuilder.Run> runs = TextRunBuilder.build(root, context);
        List<Text> nodes = new ArrayList<>();
        double cursorX = 0;
        double cursorY = 0;
        for (TextRunBuilder.Run run : runs) {
            List<Double> xs = positions(run.owner(), ISvgGlyphPositioned::getX);
            List<Double> ys = positions(run.owner(), ISvgGlyphPositioned::getY);
            List<Double> dxs = positions(run.owner(), ISvgGlyphPositioned::getDx);
            List<Double> dys = positions(run.owner(), ISvgGlyphPositioned::getDy);
            List<Double> rotates = positions(run.owner(), ISvgGlyphPositioned::getRotate);
            // A single x/y/dx/dy value positions the run's start exactly like the pre-#28 scalar attributes did -
            // splitting into glyphs only matters once there is more than one position to assign, or for `rotate`,
            // which always rotates each character individually rather than the run as one rigid block.
            boolean perGlyph = xs.size() > 1 || ys.size() > 1 || dxs.size() > 1 || dys.size() > 1 || !rotates.isEmpty();
            List<String> pieces = perGlyph ? codePoints(run.text()) : List.of(run.text());

            for (int i = 0; i < pieces.size(); i++) {
                Text node = new Text(pieces.get(i));
                run.owner().applyGraphicsProperties(run.ownerContext(), node);
                run.owner().applyTextProperties(run.ownerContext(), node);

                Double explicitX = i < xs.size() ? xs.get(i) : null;
                Double explicitY = i < ys.size() ? ys.get(i) : null;
                double dx = i < dxs.size() ? dxs.get(i) : 0.0;
                double dy = i < dys.size() ? dys.get(i) : 0.0;
                Double rotate = rotates.isEmpty() ? null : rotates.get(Math.min(i, rotates.size() - 1));

                double flowX = (explicitX != null ? explicitX : cursorX) + dx;
                double flowY = (explicitY != null ? explicitY : cursorY) + dy;
                double displayY = flowY + baselineOffset(run, node.getFont().getSize());

                node.setX(flowX);
                node.setY(displayY);
                if (rotate != null) {
                    node.getTransforms().add(new Rotate(rotate, flowX, displayY));
                }
                nodes.add(node);

                cursorX = flowX + node.getLayoutBounds().getWidth();
                cursorY = flowY;
            }
        }

        Node result = nodes.size() == 1 ? nodes.get(0) : groupOf(nodes);
        applyTextAnchor(root, context, nodes, result);
        result.setId(root.getId());
        root.applyTransforms(result);
        return result;
    }

    private static List<Double> positions(AbstractSvgStylable owner, Function<ISvgGlyphPositioned, List<Double>> getter) {
        return owner instanceof ISvgGlyphPositioned positioned ? getter.apply(positioned) : List.of();
    }

    private static List<String> codePoints(String text) {
        return text.codePoints().mapToObj(Character::toString).toList();
    }

    private static Group groupOf(List<Text> nodes) {
        Group group = new Group();
        group.getChildren().addAll(nodes);
        return group;
    }

    /**
     * Shifts the whole result horizontally so it is centred ({@code middle}) or ends ({@code end}) at the flow's
     * start position, rather than beginning there ({@code start}, the initial value) - applied as a
     * {@code translateX} on the finished node/group rather than by rewriting each glyph's own {@code x} and any
     * {@code Rotate} pivot, which would otherwise need recomputing too.
     */
    private static void applyTextAnchor(SvgText root, RenderContext context, List<Text> nodes, Node result) {
        if (nodes.isEmpty()) {
            return;
        }
        String anchor = StringUtils.trimToEmpty(SvgInheritedStyle.resolve(context, root).getTextAnchor());
        double fraction = "middle".equalsIgnoreCase(anchor) ? 0.5 : "end".equalsIgnoreCase(anchor) ? 1.0 : 0.0;
        if (fraction == 0.0) {
            return;
        }
        Text first = nodes.get(0);
        Text last = nodes.get(nodes.size() - 1);
        double totalWidth = (last.getX() + last.getLayoutBounds().getWidth()) - first.getX();
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
