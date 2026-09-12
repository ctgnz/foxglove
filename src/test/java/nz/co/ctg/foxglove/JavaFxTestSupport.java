package nz.co.ctg.foxglove;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;

/**
 * Starts the JavaFX toolkit for tests that need it - resolving a pattern-backed paint, via {@code Node.snapshot(...)},
 * requires the JavaFX Application Thread and throws {@link IllegalStateException} otherwise.
 * <p>
 * {@code Platform.startup(Runnable)} can only be called once per JVM (a second call throws
 * {@code IllegalStateException: Toolkit already initialized}), so every test class that needs the toolkit calls
 * {@link #ensureStarted()} from its own {@code @BeforeAll} rather than calling {@code Platform.startup} directly.
 * It also returns before the toolkit has necessarily finished initialising - the runnable only marks when it is
 * ready - so this waits on a latch rather than returning immediately, which avoided a real, observed flake: a
 * different test class's unrelated classpath resource reads intermittently failed when it started running while
 * the toolkit was still settling in the background.
 */
public final class JavaFxTestSupport {

    private static final AtomicBoolean STARTING = new AtomicBoolean();
    private static final CountDownLatch READY = new CountDownLatch(1);

    public static void ensureStarted() throws InterruptedException {
        if (STARTING.compareAndSet(false, true)) {
            Platform.startup(READY::countDown);
        }
        READY.await(5, TimeUnit.SECONDS);
    }

    /**
     * Runs {@code action} on the JavaFX Application Thread and waits for its result - {@code Platform.startup}'s own
     * callback runs there once at startup, not every subsequent call, so anything needing the thread has to be
     * handed to it explicitly via {@code Platform.runLater}.
     */
    public static <T> T onFxThread(Callable<T> action) throws Exception {
        CompletableFuture<T> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                result.complete(action.call());
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        });
        return result.get(5, TimeUnit.SECONDS);
    }

    private JavaFxTestSupport() {
    }

}
