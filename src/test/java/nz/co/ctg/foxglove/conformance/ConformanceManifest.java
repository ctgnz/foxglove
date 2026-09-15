package nz.co.ctg.foxglove.conformance;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * The checked-in baseline for #44 - one {@code PASS}/{@code FAIL} line per W3C SVG test name (small, our own data, unlike the fetched test suite itself), so a run's actual results
 * have something to be measured against: "400 tests fail" says nothing about whether that is progress or a regression, but "3 tests regressed from the baseline" does.
 * <p>
 * Reads/writes {@code src/test/resources/conformance/manifest.properties} directly by file path rather than the classpath, since {@code record} mode needs to overwrite the real
 * source file - this assumes the JVM's working directory is the Maven module root, true for any {@code mvn} invocation (including the {@code conformance} profile this class only
 * ever runs under).
 */
public final class ConformanceManifest {

    private static final Path PATH = Path.of("src/test/resources/conformance/manifest.properties");

    public record Diff(List<String> regressions, List<String> newlyPassing) {

        public boolean hasRegressions() {
            return !regressions.isEmpty();
        }
    }

    public static Map<String, Boolean> load() {
        return load(PATH);
    }

    /**
     * As {@link #load()}, from a named file - {@link W3cSvgAnimationCheck} keeps its own baseline (#112), and the diff logic below is worth sharing rather than copying alongside
     * it.
     */
    public static Map<String, Boolean> load(Path path) {
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Map<String, Boolean> result = new TreeMap<>();
        for (String name : properties.stringPropertyNames()) {
            result.put(name, "PASS".equals(properties.getProperty(name)));
        }
        return result;
    }

    /**
     * Overwrites the manifest with {@code actual} directly - the deliberate, explicit way to accept a reviewed change in baseline ({@code -Dconformance.mode=record}), never done
     * implicitly by a plain verification run.
     */
    public static void record(Map<String, Boolean> actual) {
        record(PATH, actual, "# W3C SVG 1.1 conformance baseline (#44) - one PASS/FAIL line per test name.\n"
                             + "# Regenerate deliberately with -Dconformance.mode=record after reviewing what changed.\n");
    }

    /** As {@link #record(Map)}, to a named file and under its own {@code header} - see {@link #load(Path)}. */
    public static void record(Path path, Map<String, Boolean> actual, String header) {
        StringBuilder content = new StringBuilder(header);
        for (Map.Entry<String, Boolean> entry : new TreeMap<>(actual).entrySet()) {
            content.append(entry.getKey())
                .append('=')
                .append(entry.getValue() ? "PASS" : "FAIL")
                .append('\n');
        }
        try {
            Files.writeString(path, content.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * A test present in {@code actual} but absent from {@code baseline} (the suite grew, or the manifest is stale) is skipped rather than treated as a regression or an addition -
     * {@code record} mode is how it gets a real baseline entry.
     */
    public static Diff diff(Map<String, Boolean> baseline, Map<String, Boolean> actual) {
        List<String> regressions = new ArrayList<>();
        List<String> newlyPassing = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : actual.entrySet()) {
            Boolean expectedPass = baseline.get(entry.getKey());
            if (expectedPass == null) {
                continue;
            }
            if (expectedPass && !entry.getValue()) {
                regressions.add(entry.getKey());
            } else if (!expectedPass && entry.getValue()) {
                newlyPassing.add(entry.getKey());
            }
        }
        Collections.sort(regressions);
        Collections.sort(newlyPassing);
        return new Diff(regressions, newlyPassing);
    }

    private ConformanceManifest() {
    }

}
