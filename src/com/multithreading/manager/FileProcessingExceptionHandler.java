
package com.multithreading.manager;

import java.nio.file.Path;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class FileProcessingExceptionHandler
        implements Thread.UncaughtExceptionHandler {

    private final AtomicInteger uncaughtExceptionCount =
            new AtomicInteger();

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        uncaughtExceptionCount.incrementAndGet();

        System.err.printf(
                "Uncaught exception in thread '%s': %s%n",
                thread.getName(),
                exception.getMessage());
    }

    public int getUncaughtExceptionCount() {
        return uncaughtExceptionCount.get();
    }

    public static void reportFileFailure(Path file, Exception exception) {
        System.err.printf(
                "Failed to process '%s': %s%n",
                file.getFileName(),
                exception.getMessage());
    }

    public static void reportExecutorFailure(
            ThreadPoolExecutor executor, Exception exception) {

        System.err.printf(
                "Executor failure (active threads: %d): %s%n",
                executor.getActiveCount(),
                exception.getMessage());
    }
}
