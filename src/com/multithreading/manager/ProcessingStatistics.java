
package com.multithreading.manager;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProcessingStatistics {

    private final AtomicInteger successfulFiles = new AtomicInteger();
    private final AtomicInteger failedFiles = new AtomicInteger();
    private final AtomicLong totalBytes = new AtomicLong();
    private final AtomicLong totalProcessingTimeNanos = new AtomicLong();

    public void recordSuccess(long fileSizeBytes, long processingTimeNanos) {
        if (fileSizeBytes < 0 || processingTimeNanos < 0) {
            throw new IllegalArgumentException(
                    "File size and processing time cannot be negative.");
        }

        totalBytes.addAndGet(fileSizeBytes);
        totalProcessingTimeNanos.addAndGet(processingTimeNanos);
        successfulFiles.incrementAndGet();
    }

    public void recordFailure() {
        failedFiles.incrementAndGet();
    }

    public int getSuccessfulFiles() {
        return successfulFiles.get();
    }

    public int getFailedFiles() {
        return failedFiles.get();
    }

    public long getTotalBytes() {
        return totalBytes.get();
    }

    public long getTotalProcessingTimeNanos() {
        return totalProcessingTimeNanos.get();
    }

    public double getAverageProcessingTimeMillis() {
        int count = successfulFiles.get();

        if (count == 0) {
            return 0.0;
        }

        return totalProcessingTimeNanos.get()
                / 1_000_000.0 / count;
    }
}
