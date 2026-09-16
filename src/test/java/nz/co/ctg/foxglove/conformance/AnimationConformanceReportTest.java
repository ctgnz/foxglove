package nz.co.ctg.foxglove.conformance;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Covers #220's own real per-test animation report. Needs neither the JavaFX toolkit nor a live browser reference - the generator works from a plain results/notes map, the same
 * way {@link ConformanceReportTest} covers {@link ConformanceReport} - so it runs in the default build.
 */
public class AnimationConformanceReportTest {

    @TempDir
    Path outputDir;

    @Test
    public void testListsEveryTestWithItsRealResultAndNote() throws Exception {
        Map<String, Boolean> results = new LinkedHashMap<>();
        results.put("animate-elem-22-b", true);
        results.put("animate-elem-24-t", false);
        Map<String, String> notes = Map.of("animate-elem-22-b", "worst differing ratio 0.0125 across 3 moments, 4 animation(s) built",
            "animate-elem-24-t", "worst differing ratio 0.0991 across 4 moments, 3 animation(s) built");

        AnimationConformanceReport.write(results, notes, outputDir);

        String index = read("index.html");
        assertThat(index, containsString("1 / 2 tests passing"));
        assertThat(index, containsString("animate-elem-22-b"));
        assertThat(index, containsString("animate-elem-24-t"));
        assertThat(index, containsString("worst differing ratio 0.0125 across 3 moments, 4 animation(s) built"));
        assertThat(index, containsString("worst differing ratio 0.0991 across 4 moments, 3 animation(s) built"));
        assertThat(index, containsString("<span class=\"pass\">pass</span>"));
        assertThat(index, containsString("<span class=\"fail\">fail</span>"));
    }

    /** A test with no note (should never happen in practice, but a missing key must not throw) renders an empty cell rather than "null". */
    @Test
    public void testAMissingNoteRendersBlankRatherThanThrowing() throws Exception {
        AnimationConformanceReport.write(Map.of("animate-elem-22-b", true), Map.of(), outputDir);

        assertThat(read("index.html"), not(containsString("null")));
    }

    private String read(String relativePath) throws Exception {
        return Files.readString(outputDir.resolve(relativePath));
    }

}
