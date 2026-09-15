package nz.co.ctg.foxglove;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;

/**
 * Starts the JavaFX toolkit for tests that need it - resolving a pattern-backed paint, via {@code Node.snapshot(...)}, requires the JavaFX Application Thread and throws
 * {@link IllegalStateException} otherwise.
 * <p>
 * {@code Platform.startup(Runnable)} can only be called once per JVM (a second call throws {@code IllegalStateException: Toolkit already initialized}), so every test class that
 * needs the toolkit calls {@link #ensureStarted()} from its own {@code @BeforeAll} rather than calling {@code Platform.startup} directly. It also returns before the toolkit has
 * necessarily finished initialising - the runnable only marks when it is ready - so this waits on a latch rather than returning immediately, which avoided a real, observed flake:
 * a different test class's unrelated classpath resource reads intermittently failed when it started running while the toolkit was still settling in the background.
 */
public final class JavaFxTestSupport {

    private static final AtomicBoolean STARTING = new AtomicBoolean();
    private static final CountDownLatch READY = new CountDownLatch(1);

    public static void ensureStarted() throws InterruptedException {
        if (STARTING.compareAndSet(false, true)) {
            Platform.startup(READY::countDown);
            // Without this, closing the last shown Stage terminates the toolkit for the whole JVM - and then every
            // later Platform.runLater is simply never run, so every subsequent test times out rather than failing
            // with anything that points at the cause. Nothing needed it until #112 started showing a Stage (a
            // WebView only paints into a snapshot when its Stage is showing), and it cost a genuinely baffling
            // cascade of timeouts to find.
            Platform.setImplicitExit(false);
        }
        READY.await(5, TimeUnit.SECONDS);
    }

    /**
     * Runs {@code action} on the JavaFX Application Thread and waits for its result - {@code Platform.startup}'s own callback runs there once at startup, not every subsequent
     * call, so anything needing the thread has to be handed to it explicitly via {@code Platform.runLater}.
     */
    public static <T> T onFxThread(Callable<T> action) throws Exception {
        return onFxThread(action, 5, TimeUnit.SECONDS);
    }

    /**
     * As {@link #onFxThread(Callable)}, but with an explicit timeout - for an action that's doing meaningfully more work than a single test's own render/snapshot (#44's
     * conformance check runs its entire ~525-document loop in one call, to avoid 525 separate {@code Platform.runLater} round-trips).
     */
    public static <T> T onFxThread(Callable<T> action, long timeout, TimeUnit unit) throws Exception {
        CompletableFuture<T> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                result.complete(action.call());
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        });
        return result.get(timeout, unit);
    }

    private JavaFxTestSupport() {
    }

}
