package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * A font's {@code <hkern>} pairs: how much closer together two particular glyphs sit than their own advances would put them (#136).
 * <p>
 * Each side of a pair is matched by <b>the union of its {@code u} list and its {@code g} list</b> - {@code u1}/ {@code u2} name characters (or ranges of them),
 * {@code g1}/{@code g2} name glyphs. The suite's own {@code fonts-kern-01-t} settles this reading: its {@code fontC} declares {@code <hkern u1="1" u2="2" g1="gl_3" g2="gl_4">} and
 * expects the one rule to kern both "12" and "34", while leaving "23" alone. A rule matches a pair only when the <i>left</i> glyph is in the left set <i>and</i> the right glyph is
 * in the right set; matching if either side does would kern "23" too.
 * <p>
 * <b>Glyph names are the path that matters in practice</b>, not characters: all 406 pairs in {@code SVGFreeSans.svg} - the font every one of the 525 W3C suite documents labels
 * itself in - use {@code g1}/{@code g2}, and none use {@code u1}/{@code u2}.
 * <p>
 * Rules are indexed by their literal left-hand entries so a lookup touches one or two of them rather than all 406; only rules whose left side carries a <i>range</i> have to be
 * scanned every time, and real fonts have very few. This runs once per adjacent glyph pair in every document, so the difference is not academic.
 */
final class SvgFontKerning {

    /** A font with no kerning at all - every lookup is zero, and no table is allocated for it. */
    static final SvgFontKerning NONE = new SvgFontKerning(List.of());

    private final List<Rule> rules;
    private final Map<String, List<Rule>> byLeftLiteral = new HashMap<>();
    private final List<Rule> leftRanged = new ArrayList<>();

    private SvgFontKerning(List<Rule> rules) {
        this.rules = rules;
        for (Rule rule : rules) {
            for (String literal : rule.left.literals()) {
                byLeftLiteral.computeIfAbsent(literal, key -> new ArrayList<>())
                    .add(rule);
            }
            if (!rule.left.ranges.isEmpty()) {
                leftRanged.add(rule);
            }
        }
    }

    static SvgFontKerning of(List<SvgHorizontalKerning> pairs) {
        List<Rule> rules = new ArrayList<>();
        for (SvgHorizontalKerning pair : pairs) {
            Side left = Side.of(pair.getUnicodeChars1(), pair.getGlyphs1());
            Side right = Side.of(pair.getUnicodeChars2(), pair.getGlyphs2());
            // a side naming nothing can never match, so the rule is inert - drop it rather than carry it
            if (left.isEmpty() || right.isEmpty()) {
                continue;
            }
            String kern = StringUtils.trimToEmpty(pair.getKern());
            if (!NumberUtils.isParsable(kern)) {
                continue;
            }
            rules.add(new Rule(rules.size(), left, right, NumberUtils.toDouble(kern)));
        }
        return rules.isEmpty() ? NONE : new SvgFontKerning(rules);
    }

    /**
     * How much closer {@code right} sits to {@code left}, in font units, or zero when no pair matches. Either glyph name may be null for a character the font has no glyph for.
     * <p>
     * Where more than one rule matches, the first in document order wins. The W3C suite gives no evidence either way - no font in it declares two rules matching the same pair - so
     * this is a documented choice, not a derived one.
     */
    double kern(String leftChar, String leftName, String rightChar, String rightName) {
        if (rules.isEmpty()) {
            return 0;
        }
        Rule best = firstMatch(null, byLeftLiteral.get(leftChar), leftChar, leftName, rightChar, rightName);
        if (leftName != null) {
            best = firstMatch(best, byLeftLiteral.get(leftName), leftChar, leftName, rightChar, rightName);
        }
        best = firstMatch(best, leftRanged, leftChar, leftName, rightChar, rightName);
        return best == null ? 0 : best.kern;
    }

    /**
     * The earliest-declared rule among {@code candidates} that matches, or {@code best} if none beats it. The index only narrows which rules are worth testing - every candidate is
     * still checked against both sides in full, so a rule reached through one of its literal entries is never assumed to match on the strength of that alone.
     */
    private static Rule firstMatch(Rule best, List<Rule> candidates, String leftChar, String leftName,
                                   String rightChar, String rightName) {
        if (candidates == null) {
            return best;
        }
        for (Rule rule : candidates) {
            if ((best == null || rule.order < best.order) && rule.matches(leftChar, leftName, rightChar, rightName)) {
                best = rule;
            }
        }
        return best;
    }

    private record Rule(int order, Side left, Side right, double kern) {

        boolean matches(String leftChar, String leftName, String rightChar, String rightName) {
            return left.matches(leftChar, leftName) && right.matches(rightChar, rightName);
        }

    }

    /** One side of a pair: the characters, glyph names and character ranges that side will match. */
    private record Side(Set<String> characters, Set<String> names, List<int[]> ranges) {

        static Side of(String unicode, String glyphNames) {
            Set<String> characters = new HashSet<>();
            List<int[]> ranges = new ArrayList<>();
            for (String token : split(unicode)) {
                if (StringUtils.startsWithIgnoreCase(token, "U+")) {
                    int[] range = parseRange(token.substring(2));
                    if (range != null) {
                        ranges.add(range);
                    }
                } else {
                    characters.add(token);
                }
            }
            return new Side(characters, new HashSet<>(split(glyphNames)), ranges);
        }

        boolean isEmpty() {
            return characters.isEmpty() && names.isEmpty() && ranges.isEmpty();
        }

        /** The entries a lookup can be indexed by - everything except the ranges, which have to be scanned. */
        Set<String> literals() {
            Set<String> literals = new HashSet<>(characters);
            literals.addAll(names);
            return literals;
        }

        boolean matches(String character, String name) {
            if (character != null && characters.contains(character)) {
                return true;
            }
            if (name != null && names.contains(name)) {
                return true;
            }
            if (ranges.isEmpty() || StringUtils.isEmpty(character)) {
                return false;
            }
            int codePoint = character.codePointAt(0);
            for (int[] range : ranges) {
                if (codePoint >= range[0] && codePoint <= range[1]) {
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * A comma-separated list, with the single deliberate exception that an attribute which is nothing but a comma is that character itself rather than two empty entries - the
     * specification gives commas both jobs.
     */
    private static List<String> split(String value) {
        String text = StringUtils.trimToEmpty(value);
        if (text.isEmpty()) {
            return List.of();
        }
        if (",".equals(text)) {
            return List.of(",");
        }
        List<String> tokens = new ArrayList<>();
        for (String token : text.split(",")) {
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    /**
     * The inclusive code point range a {@code U+...} token covers: {@code 0031-0034} a span, {@code 003?} every value the wildcards can take ({@code 0030} to {@code 003F}), a bare
     * {@code 0041} just itself.
     */
    private static int[] parseRange(String body) {
        if (body.contains("-")) {
            int codePoint = hex(StringUtils.substringBefore(body, "-"));
            int end = hex(StringUtils.substringAfter(body, "-"));
            return codePoint < 0 || end < 0 ? null : new int[] {
                codePoint, end
            };
        }
        if (body.indexOf('?') >= 0) {
            int codePoint = hex(body.replace('?', '0'));
            int end = hex(body.replace('?', 'F'));
            return codePoint < 0 || end < 0 ? null : new int[] {
                codePoint, end
            };
        }
        int codePoint = hex(body);
        return codePoint < 0 ? null : new int[] {
            codePoint, codePoint
        };
    }

    private static int hex(String value) {
        try {
            return Integer.parseInt(value.trim(), 16);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
