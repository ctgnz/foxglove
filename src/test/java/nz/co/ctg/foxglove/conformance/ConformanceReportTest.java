package nz.co.ctg.foxglove.conformance;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Covers #110's drill-down site. Needs neither the JavaFX toolkit nor the fetched W3C suite - the generator works from a results map alone - so it runs in the default build,
 * unlike {@link W3cSvgConformanceCheck} itself.
 */
public class ConformanceReportTest {

    @TempDir
    Path outputDir;

    @Test
    public void testIndexLinksToEveryChapter() throws Exception {
        write(result("filters-offset-01-b", true, 0.985), result("paths-data-01-t", false, 0.60));

        String index = read("index.html");
        assertThat(index, containsString("href=\"filters/index.html\""));
        assertThat(index, containsString("href=\"paths/index.html\""));
        assertThat(index, containsString("1 / 2 tests passing"));
    }

    @Test
    public void testEveryLevelReportsInkMatchedNotJustPassCount() throws Exception {
        write(result("paths-data-01-t", false, 0.60), result("paths-data-02-t", false, 0.80));

        // the pass count is 0 at both levels; the ink figure is what shows these are most of the way there
        assertThat(read("index.html"), containsString("70.0%"));
        assertThat(read("paths/index.html"), containsString("Ink matched 70.0%"));
    }

    @Test
    public void testChapterPageLinksToItsTestsMostSimilarFirst() throws Exception {
        write(result("paths-data-01-t", false, 0.20), result("paths-data-02-t", false, 0.90));

        String chapter = read("paths/index.html");
        assertThat(chapter, containsString("href=\"paths-data-01-t.html\""));
        assertThat(chapter, containsString("href=\"paths-data-02-t.html\""));
        // near-misses belong at the top, where they read as a worklist
        assertThat("the more similar test should be listed first",
            chapter.indexOf("paths-data-02-t.html") < chapter.indexOf("paths-data-01-t.html"), is(true));
        assertThat(chapter, containsString("href=\"../index.html\""));
    }

    @Test
    public void testTestPageShowsAllThreePanelsWithTheReferenceHotlinked() throws Exception {
        write(result("filters-offset-01-b", true, 0.985));

        String page = read("filters/filters-offset-01-b.html");
        // the W3C reference is linked, never republished - see ConformanceReport's class javadoc
        assertThat(page, containsString("https://www.w3.org/Graphics/SVG/Test/20110816/png/filters-offset-01-b.png"));
        // our own render and the diff are local files, written by the harness alongside the page
        assertThat(page, containsString("src=\"filters-offset-01-b.png\""));
        assertThat(page, containsString("src=\"filters-offset-01-b-diff.png\""));
        assertThat(page, containsString("98.5% of the ink matches"));
        assertThat(page, containsString("https://www.w3.org/Graphics/SVG/Test/20110816/svg/filters-offset-01-b.svg"));
    }

    @Test
    public void testTestPageShowsTheTestsOwnPassCriteria() throws Exception {
        Map<String, ConformanceResult> results = new LinkedHashMap<>();
        results.put("shapes-rect-01-t", new ConformanceResult("shapes-rect-01-t", false, 0.9, 1000, 0.02,
                                                              "The rendered picture should match the reference image.", null));
        ConformanceReport.write(results, outputDir);

        assertThat(read("shapes/shapes-rect-01-t.html"),
            containsString("The rendered picture should match the reference image."));
    }

    @Test
    public void testAnimateResultsAreMarkedUnmeasurableRatherThanSimplyFailing() throws Exception {
        write(result("animate-elem-01-t", false, 0.5), result("shapes-rect-01-t", false, 0.5));

        assertThat(read("animate/animate-elem-01-t.html"), containsString("not meaningful"));
        assertThat(read("animate/index.html"), containsString("not meaningful"));
        // and the caveat must not leak onto chapters it doesn't apply to
        assertThat(read("shapes/shapes-rect-01-t.html"), not(containsString("not meaningful")));
    }

    /**
     * #201: interaction-category tests (here, {@code struct-dom-01-b} - the {@code -dom-} naming pattern, not the {@code struct} chapter's own static tests) are scattered across
     * many raw chapters rather than one, so they get a single shared listing page (not a same-directory chapter index - {@code struct} has no static tests in this input at all, so
     * {@code struct/index.html} is never written) and their own page links back to that shared listing, not a (nonexistent) same-directory index.
     */
    @Test
    public void testInteractionResultsAreMarkedUnmeasurableAndListedOnOneSharedPage() throws Exception {
        write(result("struct-dom-01-b", false, 0.1), result("shapes-rect-01-t", true, 0.99));

        String testPage = read("struct/struct-dom-01-b.html");
        assertThat(testPage, containsString("not meaningful"));
        assertThat(testPage, containsString("href=\"../interaction/index.html\">interaction</a>"));
        assertThat(read("interaction/index.html"), containsString("href=\"../struct/struct-dom-01-b.html\""));
        // struct has no static tests here at all, so its own chapter index must not exist
        assertThat(Files.exists(outputDir.resolve("struct")
            .resolve("index.html")), is(false));
        // and the caveat must not leak onto categories it doesn't apply to
        assertThat(read("shapes/shapes-rect-01-t.html"), not(containsString("not meaningful")));
    }

    @Test
    public void testIndexShowsSeparateAnimationAndInteractionSections() throws Exception {
        write(result("shapes-rect-01-t", true, 0.99), result("animate-elem-01-t", false, 0.5),
            result("interact-order-01-b", false, 0.1));

        String index = read("index.html");
        assertThat(index, containsString("href=\"animate/index.html\""));
        assertThat(index, containsString("href=\"interaction/index.html\""));
        // the primary headline is static-only, unaffected by the other two categories being present
        assertThat(index, containsString("1 / 1 tests passing"));
    }

    @Test
    public void testATestThatThrewSaysSoOnItsOwnPage() throws Exception {
        Map<String, ConformanceResult> results = new LinkedHashMap<>();
        results.put("painting-stroke-06-t", new ConformanceResult("painting-stroke-06-t", false, 0, 0, 0.02, "",
                                                                  "java.lang.IllegalArgumentException: dash lengths all zero"));
        ConformanceReport.write(results, outputDir);

        String page = read("painting/painting-stroke-06-t.html");
        assertThat(page, containsString("did not finish rendering"));
        assertThat(page, containsString("dash lengths all zero"));
        // it never got as far as producing images, so those panels would be broken links; the reference still stands
        assertThat(page, not(containsString("src=\"painting-stroke-06-t.png\"")));
        assertThat(page, not(containsString("src=\"painting-stroke-06-t-diff.png\"")));
        assertThat(page, containsString("https://www.w3.org/Graphics/SVG/Test/20110816/png/painting-stroke-06-t.png"));
    }

    @Test
    public void testPassCriteriaAreEscapedRatherThanInjectedAsMarkup() throws Exception {
        Map<String, ConformanceResult> results = new LinkedHashMap<>();
        results.put("struct-frag-01-t", new ConformanceResult("struct-frag-01-t", false, 0.5, 100, 0.02,
                                                              "Compare <svg> against \"the reference\" & check.", null));
        ConformanceReport.write(results, outputDir);

        String page = read("struct/struct-frag-01-t.html");
        assertThat(page, containsString("&lt;svg&gt;"));
        assertThat(page, containsString("&amp;"));
        assertThat(page, not(containsString("Compare <svg>")));
    }

    // --- helpers -------------------------------------------------------------

    private void write(ConformanceResult... results) {
        Map<String, ConformanceResult> map = new LinkedHashMap<>();
        for (ConformanceResult result : results) {
            map.put(result.name(), result);
        }
        ConformanceReport.write(map, outputDir);
    }

    private static ConformanceResult result(String name, boolean passed, double similarity) {
        return new ConformanceResult(name, passed, similarity, 144480, 0.02, "", null);
    }

    private String read(String relativePath) throws Exception {
        return Files.readString(outputDir.resolve(relativePath));
    }

}
