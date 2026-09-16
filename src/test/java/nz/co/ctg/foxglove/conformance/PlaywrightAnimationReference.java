package nz.co.ctg.foxglove.conformance;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * A browser engine rendering the same document, seekable to a chosen moment - the reference #112 compares against.
 * <p>
 * It exists because the W3C suite's own reference PNGs cannot serve as one for animation: each was captured at some moment in the animation that nothing in the document records,
 * so a correct implementation and a broken one are equally likely to mismatch. A live engine has no such problem, because both sides can be told which moment to be at.
 * <p>
 * #208: headless Chromium via Playwright, the same engine #196 already established as this project's reference browser for the static suite - replacing an earlier WebView
 * (JavaFX's own bundled legacy WebKit fork) based version. A headless browser process needs no shown window to render, unlike WebView's own off-screen-snapshot limitation
 * (confirmed back then: a plain off-screen {@code Scene} always came back blank) - though this does not, on its own, remove this check's need for a real display in CI, since
 * foxglove's own rendering, the side actually under test, still goes through JavaFX {@code Node.snapshot()} regardless of which engine supplies the reference.
 * <p>
 * <b>{@code <animateColor>} still needs the same rewrite-to-{@code <animate>} hack #153 gave WebView, confirmed directly rather than assumed fixed by switching engines.</b> An
 * isolated probe with nothing but one {@code <animateColor>}, tried with no rewrite first, stayed at its un-animated base colour forever - the identical symptom #153 originally
 * found in WebView. The underlying cause is different (WebView's engine is a legacy WebKit fork that predates SVG2 folding {@code <animateColor>} into plain {@code <animate>};
 * Chromium's is that it dropped native support for the standalone element some years after that same SVG2 deprecation), but the practical effect on this reference is identical, so
 * {@link #withAnimateColorRewrittenToAnimate} carries the same workaround forward rather than dropping it on the assumption a newer engine would not need it.
 * <p>
 * One browser/context/page serves the entire run: creating one per document would be wasteful, and each document is loaded once and then seeked repeatedly rather than reloaded per
 * sample - the same shape the WebView-based version used.
 */
public final class PlaywrightAnimationReference implements AutoCloseable {

    /**
     * How long to let Chromium repaint after a seek before capturing. There is no "repainted" signal to await - the seek returns as soon as the clock moves, not when the frame is
     * on screen - so this is empirical, carried over unchanged from the WebView-based version's own measured value.
     */
    private static final long REPAINT_MILLIS = 150;

    private final Playwright playwright;
    private final Browser browser;
    private final BrowserContext context;
    private final Page page;
    private final int width;
    private final int height;

    private PlaywrightAnimationReference(Playwright playwright, Browser browser, BrowserContext context, Page page, int width, int height) {
        this.playwright = playwright;
        this.browser = browser;
        this.context = context;
        this.page = page;
        this.width = width;
        this.height = height;
    }

    /** Launches the shared browser. */
    public static PlaywrightAnimationReference open(int width, int height) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium()
            .launch();
        BrowserContext context = browser.newContext(new Browser.NewContextOptions().setViewportSize(width, height));
        Page page = context.newPage();
        return new PlaywrightAnimationReference(playwright, browser, context, page, width, height);
    }

    /**
     * Loads {@code document} and pauses its animation clock, so nothing moves until {@link #seek} says so. Returns whether it loaded at all - a document the engine rejects (or
     * whose root isn't an {@code <svg>}, so {@code pauseAnimations()} itself throws) is a result worth recording, not an exception.
     */
    public boolean load(Path document) {
        Path toLoad;
        try {
            toLoad = withAnimateColorRewrittenToAnimate(document);
        } catch (IOException e) {
            return false;
        }
        try {
            page.navigate(toLoad.toUri()
                .toString());
        } catch (RuntimeException e) {
            return false;
        } finally {
            if (toLoad != document) {
                try {
                    Files.deleteIfExists(toLoad);
                } catch (IOException e) {
                    // a leaked temp file in the OS temp directory is not worth failing the run over
                }
            }
        }
        return script("document.documentElement.pauseAnimations(); true;") != null;
    }

    /**
     * Rewriting the tag name only for what this reference engine is fed - never touching what is actually under test, still parsed and rendered by this project's own
     * {@code FoxgloveParser} unmodified - turns a reference that can never show the right answer for the suite's {@code <animateColor>} documents back into one that can.
     */
    private static Path withAnimateColorRewrittenToAnimate(Path document) throws IOException {
        String content = Files.readString(document);
        if (!content.contains("animateColor")) {
            return document;
        }
        String rewritten = content.replace("animateColor", "animate");
        Path temp = Files.createTempFile(document.getParent(), "reference-", ".svg");
        Files.writeString(temp, rewritten);
        return temp;
    }

    /** Moves the loaded document's SMIL clock to {@code time} and waits for the engine to repaint. */
    public void seek(Duration time) {
        script("document.documentElement.setCurrentTime(" + time.toSeconds() + "); true;");
        page.waitForTimeout(REPAINT_MILLIS);
    }

    /** What the engine is showing right now, at the size this reference was opened at. */
    public WritableImage snapshot() {
        Image image = new Image(new ByteArrayInputStream(page.screenshot()));
        return new WritableImage(image.getPixelReader(), width, height);
    }

    /**
     * Runs {@code javascript} against the loaded document, returning null if it throws - a document without an SVG root, or one the engine could not parse, should be recorded as a
     * failure rather than abort the whole run.
     */
    private Object script(String javascript) {
        try {
            return page.evaluate(javascript);
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public void close() {
        context.close();
        browser.close();
        playwright.close();
    }

}
