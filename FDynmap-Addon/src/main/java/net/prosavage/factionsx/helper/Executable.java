package net.prosavage.factionsx.helper;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class Executable {
    /**
     * {@link Executor} instance of the executor to submit async from.
     */
    @NotNull
    private final Executor executor;

    /**
     * Constructor.
     * Initialize our executor field with corresponding threads.
     *
     * @param threads {@link Integer} amount of threads to be initial in the thread pool.
     */
    public Executable(final int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    /**
     * Execute code asynchronously by a runnable.
     *
     * @param runnable {@link Runnable} code in this runnable will be ran on another thread.
     */
    public void callAsync(final Runnable runnable) {
        executor.execute(runnable);
    }
}