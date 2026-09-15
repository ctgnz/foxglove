package nz.co.ctg.foxglove.conformance;

/**
 * One test's outcome (#110) - what the report needs to say more than "failed".
 * <p>
 * {@link #similarity} is the substantive addition over the bare pass/fail the harness used to return. Roughly a fifth of the suite currently sits within 2-5% of the threshold and
 * another fifth within 5-10%, so a renderer improvement can move dozens of tests measurably closer without moving the pass count at all - invisible when the only thing recorded is
 * which side of the line a test landed on. The baseline manifest deliberately stays a pass/fail contract ({@link ConformanceManifest}): similarity is for humans reading the
 * report, and gating the build on it would turn antialiasing noise into build failures.
 *
 * @param similarity
 *            how much of the <i>ink</i> matched, {@code 0..1} - see {@link ConformanceComparator#compare}. Deliberately not the raw differing-pixel ratio the pass threshold uses:
 *            most of a test canvas is blank, so by that measure a document this renderer draws nothing at all for still scores about 93%, which would make the report read as far
 *            better news than it is
 * @param contentPixels
 *            how many pixels carried ink in either image, i.e. how much {@code similarity} measured
 * @param passCriteria
 *            the test's own {@code <d:passCriteria>} prose - what it is actually checking, which a pixel ratio never says
 * @param failureReason
 *            the exception that aborted this test, or {@code null} if it completed; the harness catches {@code Throwable} per test so one renderer crash can't take down the run,
 *            and that detail belongs on the test's own page rather than only in console output
 */
public record ConformanceResult(String name, boolean passed, double similarity, int contentPixels, double tolerance,
                                String passCriteria, String failureReason) {

    /** The chapter this test belongs to - the suite names every file {@code <chapter>-<rest>}. */
    public String chapter() {
        int dash = name.indexOf('-');
        return dash < 0 ? name : name.substring(0, dash);
    }

    /** {@code similarity} as a percentage, for display. */
    public double similarityPercent() {
        return similarity * 100.0;
    }

    /**
     * Whether a static frame can meaningfully be compared against this test's reference at all. The whole {@code animate} chapter cannot: the harness renders one unanimated frame,
     * and nothing in a test document records which moment in time its reference image was captured at, so a correct SMIL implementation and a broken one are equally likely to
     * mismatch. Reporting those as ordinary failures overstates how much is actually broken - see #112 for the comparison that would work instead.
     */
    public boolean staticallyComparable() {
        return !"animate".equals(chapter());
    }

}
