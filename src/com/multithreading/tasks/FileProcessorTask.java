
package com.multithreading.tasks;

import com.multithreading.manager.ProcessingStatistics;
import com.multithreading.monitor.ProgressTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class FileProcessorTask implements Callable<Map<String, Object>> {

    private final Path filePath;
    private final AtomicInteger processedCount;
    private final AtomicInteger failedCount;
    private final ProgressTracker progressTracker;
    private final ProcessingStatistics processingStatistics;

    public FileProcessorTask(
            Path filePath,
            AtomicInteger processedCount,
            AtomicInteger failedCount,
            ProgressTracker progressTracker,
            ProcessingStatistics processingStatistics) {

        this.filePath = filePath;
        this.processedCount = processedCount;
        this.failedCount = failedCount;
        this.progressTracker = progressTracker;
        this.processingStatistics = processingStatistics;
    }

    @Override
    public Map<String, Object> call() throws InterruptedException {

        Map<String, Object> result = new LinkedHashMap<>();
        long startTime = System.nanoTime();

        result.put("filename", filePath.getFileName().toString());
        result.put("thread", Thread.currentThread().getName());

        try {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException(
                        "File processing was interrupted.");
            }

            long fileSize = Files.size(filePath);
            long lineCount = 0;
            long wordCount = 0;

            try (BufferedReader reader = Files.newBufferedReader(
                    filePath, StandardCharsets.UTF_8)) {

                String line;

                while ((line = reader.readLine()) != null) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException(
                                "File processing was interrupted.");
                    }

                    lineCount++;

                    String trimmedLine = line.trim();

                    if (!trimmedLine.isEmpty()) {
                        wordCount += trimmedLine.split("\\s+").length;
                    }
                }
            }

            result.put("size_bytes", fileSize);
            result.put("line_count", lineCount);
            result.put("word_count", wordCount);
            result.put("status", "SUCCESS");

            processedCount.incrementAndGet();
            progressTracker.recordSuccess();

        } catch (InterruptedException e) {
            failedCount.incrementAndGet();
            progressTracker.recordFailure();
            processingStatistics.recordFailure();

            result.put("status", "INTERRUPTED");
            result.put("error", e.getMessage());

            throw e;

        } catch (IOException | SecurityException e) {
            failedCount.incrementAndGet();
            progressTracker.recordFailure();
            processingStatistics.recordFailure();

            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        long elapsedNanos = System.nanoTime() - startTime;

        result.put("processing_time_ms", elapsedNanos / 1_000_000.0);

        if ("SUCCESS".equals(result.get("status"))) {
            processingStatistics.recordSuccess(
                    ((Number) result.get("size_bytes")).longValue(),
                    elapsedNanos);
        }

        return result;
    }
}
