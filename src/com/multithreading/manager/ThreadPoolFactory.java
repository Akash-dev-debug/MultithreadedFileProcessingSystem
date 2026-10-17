
package com.multithreading.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadPoolFactory {

    private ThreadPoolFactory() {
        // Utility class; instances are not required.
    }

    public static ExecutorService createFixedThreadPool(int poolSize) {
        if (poolSize <= 0) {
            throw new IllegalArgumentException(
                    "Pool size must be greater than zero.");
        }

        AtomicInteger threadNumber = new AtomicInteger(1);
        FileProcessingExceptionHandler exceptionHandler =
                new FileProcessingExceptionHandler();

        ThreadFactory threadFactory = task -> {
            Thread thread = new Thread(
                    task,
                    "file-worker-" + threadNumber.getAndIncrement());

            thread.setUncaughtExceptionHandler(exceptionHandler);
            return thread;
        };

        return Executors.newFixedThreadPool(poolSize, threadFactory);
    }
}
