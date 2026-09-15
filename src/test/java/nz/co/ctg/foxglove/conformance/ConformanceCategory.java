package nz.co.ctg.foxglove.conformance;

/**
 * #201: the three fundamentally different kinds of test in the W3C SVG 1.1 suite, each needing its own reporting rather than one blended pass-rate - {@link W3cSvgConformanceCheck}
 * only ever produces a meaningful percentage for {@link #STATIC}, the other two are surfaced honestly rather than silently dropped or double-counted against it.
 */
public enum ConformanceCategory {

        /** Ordinary static rendering - the real, primary target foxglove is working toward 100% on. */
        STATIC(null),

        /**
         * {@code animate-*} SMIL animation (excluding the scripting-driven {@code animate-dom-*} subset - see {@link #of}) - also a real, pursued goal (foxglove intends full
         * declarative-animation spec compliance), just not yet measurable this way: this harness renders one static frame, and nothing in a test document records which moment in
         * time its reference image was captured at, so a correct implementation and a broken one are equally likely to mismatch. See #208 for the still-open "how do we actually
         * measure this" problem - not this enum's job to solve.
         */
        ANIMATION("""
                        <strong>These results are not meaningful.</strong> This check renders a single static frame, and nothing \
                        in a test document records which moment in time its reference image was captured at - so a correct SMIL \
                        implementation and a broken one are equally likely to mismatch. Animation behaviour is covered by \
                        dedicated unit tests instead; see \
                        <a href="https://github.com/ctgnz/foxglove/issues/208">ctgnz/foxglove#208</a> for a real measurement \
                        methodology, still to be worked out."""),

        /**
         * Scripting/DOM-interaction tests ({@code interact-*}/{@code script-*}/{@code svgdom-*}, plus the wider {@code -dom-} naming pattern scattered across
         * otherwise-static-looking chapters - see {@link #of}) - permanently, architecturally out of scope for foxglove itself. Foxglove renders SVG into JavaFX {@code Node}s;
         * user interaction and behaviour are the <i>consuming</i> application's responsibility, not something foxglove provides via an internal scripting/event harness. Expected
         * to sit at (or near) 0% forever, by design, not a gap to close - still rendered, scored and shown here so that stays an honest, visible fact rather than a silently
         * dropped category.
         */
        INTERACTION("""
                        <strong>These results are not meaningful, by design.</strong> This test's pass criteria require scripted \
                        or user-driven interaction (a click, a DOM call) that foxglove itself never supplies - foxglove renders \
                        SVG into JavaFX Nodes, and interaction is the consuming application's responsibility, not this library's. \
                        This category is expected to remain near 0% permanently; see \
                        <a href="https://github.com/ctgnz/foxglove/issues/201">ctgnz/foxglove#201</a>.""");

    private final String noteHtml;

    ConformanceCategory(String noteHtml) {
        this.noteHtml = noteHtml;
    }

    /**
     * Ready-to-embed HTML (a trusted, hand-authored constant - never escaped by the caller) explaining why this category's results aren't a meaningful pass/fail signal, for
     * {@link ConformanceReport} to wrap in its own {@code <div class="note">} and show alongside them - {@code null} for {@link #STATIC}, where the ordinary pass/fail verdict
     * already says everything that needs saying.
     */
    public String noteHtml() {
        return noteHtml;
    }

    /**
     * Classifies {@code testName} per the suite's own {@code <chapter>-<rest>} naming convention, verified against the real 525-test suite (not assumed from chapter names alone):
     * {@code interact-*}/{@code script-*}/{@code svgdom-*} at the chapter level, or {@code -dom-} anywhere in the name (which also catches scripting-flavoured tests scattered
     * across otherwise-static chapters - {@code struct-dom-*}, {@code types-dom-*}, {@code text-dom-*}, {@code coords-dom-*}, {@code animate-dom-*}, {@code paths-dom-*}) are
     * {@link #INTERACTION}; the remaining {@code animate-*} tests are {@link #ANIMATION}; everything else is {@link #STATIC}. 76 + 75 + 374 = 525.
     */
    public static ConformanceCategory of(String testName) {
        String chapter = chapterOf(testName);
        if ("interact".equals(chapter) || "script".equals(chapter) || "svgdom".equals(chapter) || testName.contains("-dom-")) {
            return INTERACTION;
        }
        if ("animate".equals(chapter)) {
            return ANIMATION;
        }
        return STATIC;
    }

    private static String chapterOf(String testName) {
        int dash = testName.indexOf('-');
        return dash < 0 ? testName : testName.substring(0, dash);
    }

}
