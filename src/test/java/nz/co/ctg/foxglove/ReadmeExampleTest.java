package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.layout.BorderPane;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * #36: the README's own "Basic Usage" snippet, kept here so it can never silently drift out of compiling again - the original example called a {@code createGraphic()} overload
 * that didn't exist, passed a {@code String} where {@code Files.newInputStream} needs a {@code Path}, and didn't account for {@link FoxgloveParser#parse} declaring
 * {@code throws Exception}. If this test ever needs to change, the README's own snippet needs the same change alongside it.
 */
public class ReadmeExampleTest {

    @BeforeAll
    public static void initJfx() throws Exception {
        JavaFxTestSupport.ensureStarted();
    }

    @Test
    public void testTheReadmesBasicUsageSnippetCompilesAndRenders() throws Exception {
        Path myfile = Path.of(SvgGraphic.class.getResource("/test.svg")
            .toURI());

        // --- README "Basic Usage" snippet begins here ---
        FoxgloveParser parser = new FoxgloveParser();
        BorderPane parent = new BorderPane();
        try {
            SvgGraphic graphic = parser.parse(Files.newInputStream(myfile));
            parent.setCenter(graphic.createGroup());
        } catch (Exception e) {
            // handle a malformed or unreadable document
        }
        // --- README "Basic Usage" snippet ends here ---

        assertThat(parent.getCenter(), notNullValue());
    }

}
