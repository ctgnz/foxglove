package nz.co.ctg.foxglove.conformance;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Per-test overrides of {@link ConformanceComparator}'s default differing-pixel-ratio tolerance, for the handful of
 * tests that are legitimately borderline under the default - read from a small, checked-in classpath resource
 * (unlike the fetched suite itself), populated only with entries a real baseline run actually needed, not guessed
 * up front.
 */
public final class ConformanceTolerances {

    public static final double DEFAULT_MAX_DIFFERING_RATIO = 0.02;

    private static final Properties OVERRIDES = load();

    public static double forTest(String testName) {
        String override = OVERRIDES.getProperty(testName);
        return override != null ? Double.parseDouble(override) : DEFAULT_MAX_DIFFERING_RATIO;
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = ConformanceTolerances.class.getResourceAsStream("/conformance/tolerance-overrides.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return properties;
    }

    private ConformanceTolerances() {
    }

}
