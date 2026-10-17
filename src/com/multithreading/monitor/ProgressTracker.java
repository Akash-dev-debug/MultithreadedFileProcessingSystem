
package com.multithreading.monitor;

import java.util.concurrent.atomic.AtomicInteger;

public class ProgressTracker {

    private final int totalFiles;
    private final AtomicInteger completedFiles = new AtomicInteger();
    private final AtomicInteger successfulFiles = new AtomicInteger();
    private final AtomicInteger failedFiles = new AtomicInteger();

    public ProgressTracker(int totalFiles) {
        if (totalFiles < 0) {
            throw new IllegalArgumentException(
                    "Total files cannot be negative.");
        }

        this.totalFiles = totalFiles;
    }

    public synchronized void recordSuccess() {
        successfulFiles.incrementAndGet();
        completedFiles.incrementAndGet();
        displayProgress();
    }

    public synchronized void recordFailure() {
        failedFiles.incrementAndGet();
        completedFiles.incrementAndGet();
        displayProgress();
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getCompletedFiles() {
        return completedFiles.get();
    }

    public int getSuccessfulFiles() {
        return successfulFiles.get();
    }

    public int getFailedFiles() {
        return failedFiles.get();
    }

    public double getProgressPercentage() {
        if (totalFiles == 0) {
            return 100.0;
        }

        return completedFiles.get() * 100.0 / totalFiles;
    }

    public synchronized void displayProgress() {
        System.out.printf(
                "Progress: %d/%d files (%.1f%%) | Success: %d | Failed: %d%n",
                getCompletedFiles(),
                totalFiles,
                getProgressPercentage(),
                getSuccessfulFiles(),
                getFailedFiles());
    }
}
