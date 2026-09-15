package nz.co.ctg.foxglove.cucumber;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * The JUnit Platform Suite entrypoint Surefire discovers to run every {@code .feature} file under {@code
 * src/test/resources/features} (via {@link SelectClasspathResource}) against the step definitions in {@code
 * nz.co.ctg.foxglove.cucumber} (the "glue" package, via {@link ConfigurationParameter}).
 * <p>
 * Discovery logs a non-critical warning suggesting {@code @SelectPackage("features")} instead - tried that first, but it discovers zero tests ({@code NoTestsDiscoveredException})
 * against this project's actual classpath layout, so {@link SelectClasspathResource} stays despite the warning; it is the one that actually finds the feature file.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "nz.co.ctg.foxglove.cucumber")
public class RunCucumberTest {
}
