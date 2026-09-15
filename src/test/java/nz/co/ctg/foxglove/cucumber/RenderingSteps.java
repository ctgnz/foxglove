package nz.co.ctg.foxglove.cucumber;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

import nz.co.ctg.foxglove.FoxgloveParser;
import nz.co.ctg.foxglove.ISvgContent;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.type.SvgPaint;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for the document-level scenarios #43 adds - the level nothing in this suite exercised before: parse a real document, render it, and assert against the actual
 * JavaFX node that comes out, rather than an intermediate object (e.g. {@code SvgInheritedStyle}) or a node built in isolation without its document context.
 * <p>
 * Cucumber constructs a fresh instance of this class per scenario, so plain instance fields are safe scenario state - no separate "world" object is needed.
 */
public class RenderingSteps {

    private SvgGraphic svg;
    private final Map<ISvgElement, Node> nodeRegistry = new IdentityHashMap<>();

    @Given("the document:")
    public void theDocument(String document) throws Exception {
        svg = new FoxgloveParser().parse(new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Renders via the plain {@link RenderContext#withNodeRegistry} mechanism directly (the same one {@code
     * SvgGraphic.createAnimatedGraphic} uses internally) rather than {@code createAnimatedGraphic} itself - nothing here needs to actually play an animation, only to look up which
     * {@link Node} a given source element built into, so a locally-held registry map is simpler than routing through an {@code SvgAnimationController} this step never uses.
     */
    @When("it is rendered")
    public void itIsRendered() {
        RenderContext context = RenderContext.root(svg.getElementIndex(), 0, 0)
            .withNodeRegistry(nodeRegistry);
        svg.createGraphic(context);
    }

    @Then("the node at {string} has fill {string}")
    public void theNodeAtHasFill(String selector, String colorName) {
        Node node = nodeRegistry.get(resolve(selector));
        assertThat(node, is(instanceOf(Shape.class)));
        assertThat(((Shape) node).getFill(), is(SvgPaint.parse(colorName)
            .getPaint()));
    }

    /**
     * A {@code svg > g > rect}-style path, walked against the *source* {@link ISvgElement} tree (not the built {@link Node} tree, which has no notion of SVG tag names) - each
     * segment matched by a small element-type → tag lookup, extended only as further scenarios need more tags.
     */
    private ISvgElement resolve(String selector) {
        String[] segments = selector.split(">");
        ISvgElement current = svg;
        for (int i = 1; i < segments.length; i++) {
            String tag = segments[i].trim();
            ISvgElement parent = current;
            current = ((ISvgContent) parent).getContent()
                .stream()
                .filter(child -> tag.equals(tagOf(child)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No child tagged '" + tag + "' under " + parent));
        }
        return current;
    }

    private static String tagOf(ISvgElement element) {
        if (element instanceof SvgGraphic) {
            return "svg";
        }
        if (element instanceof SvgGroup) {
            return "g";
        }
        if (element instanceof SvgRectangle) {
            return "rect";
        }
        throw new IllegalArgumentException("No known tag for " + element.getClass());
    }

}
