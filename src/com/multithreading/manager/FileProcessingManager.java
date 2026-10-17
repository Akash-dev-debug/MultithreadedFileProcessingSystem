
package com.multithreading.manager;

import com.multithreading.monitor.ProgressTracker;
import com.multithreading.patterns.ConcurrentFileQueue;
import com.multithreading.patterns.FileProducerConsumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FileProcessingManager {

    private final int threadPoolSize;
    private final AtomicInteger processedCount = new AtomicInteger();
    private final AtomicInteger failedCount = new AtomicInteger();

    public FileProcessingManager(int threadPoolSize) {
        if (threadPoolSize <= 0) {
            throw new IllegalArgumentException(
                    "Thread pool size must be greater than zero.");
        }

        this.threadPoolSize = threadPoolSize;
    }

    public void processDirectory(String directoryPath)
            throws IOException, InterruptedException, ExecutionException {

        Path directory = Path.of(directoryPath);

        if (!Files.isDirectory(directory)) {
            throw new IOException(
                    "Directory does not exist: " + directoryPath);
        }

        List<Path> files = discoverFiles(directory);

        System.out.println("\n=== FILE PROCESSING ===");
        System.out.println("Directory: " + directory.toAbsolutePath());
        System.out.println("Files found: " + files.size());
        System.out.println("Worker threads: " + threadPoolSize);

        if (files.isEmpty()) {
            System.out.println("No supported files found.");
            return;
        }

        processedCount.set(0);
        failedCount.set(0);

        ProgressTracker progressTracker =
                new ProgressTracker(files.size());

        ProcessingStatistics statistics = new ProcessingStatistics();

        long startTime = System.nanoTime();

        ExecutorService executor =
                ThreadPoolFactory.createFixedThreadPool(threadPoolSize);

        FileResultRegistry resultRegistry = new FileResultRegistry();

        try {
            ConcurrentFileQueue queue =
                    new ConcurrentFileQueue(
                            Math.max(10, threadPoolSize * 2));

            FileProducerConsumer producerConsumer =
                    new FileProducerConsumer(
                            queue,
                            executor,
                            processedCount,
                            failedCount,
                            threadPoolSize,
                            resultRegistry,
                            progressTracker,
                            statistics);

            producerConsumer.processFiles(files);

            System.out.println("\n=== INDIVIDUAL FILE RESULTS ===");

            for (Map<String, Object> result : resultRegistry.getResults()) {
                System.out.printf(
                        Locale.ROOT,
                        "File: %s | Status: %s | Lines: %s | Words: %s "
                                + "| Size: %s bytes | Time: %s ms | Thread: %s%n",
                        result.get("filename"),
                        result.get("status"),
                        result.getOrDefault("line_count", 0),
                        result.getOrDefault("word_count", 0),
                        result.getOrDefault("size_bytes", 0),
                        result.getOrDefault("processing_time_ms", 0),
                        result.get("thread"));
            }

        } finally {
            executor.shutdown();

            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                throw e;
            }
        }

        long elapsedNanos = System.nanoTime() - startTime;
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);

        printReport(files.size(), elapsedMillis, progressTracker, statistics);
    }

    private List<Path> discoverFiles(Path directory) throws IOException {
        List<Path> files = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                    .filter(this::isSupportedFile)
                    .forEach(files::add);
        }

        return files;
    }

    private boolean isSupportedFile(Path path) {
        String name = path.getFileName()
                .toString()
                .toLowerCase(Locale.ROOT);

        return name.endsWith(".txt")
                || name.endsWith(".csv")
                || name.endsWith(".log");
    }

    private void printReport(
            int totalFiles,
            long elapsedMillis,
            ProgressTracker progressTracker,
            ProcessingStatistics statistics) {

        int successful = progressTracker.getSuccessfulFiles();
        int failed = progressTracker.getFailedFiles();

        System.out.println("\n=== FILE PROCESSING REPORT ===");
        System.out.println("Total files: " + totalFiles);
        System.out.println("Successful files: " + successful);
        System.out.println("Failed files: " + failed);

        progressTracker.displayProgress();

        double successRate = totalFiles == 0
                ? 0.0
                : successful * 100.0 / totalFiles;

        System.out.printf(
                Locale.ROOT,
                "Success rate: %.2f%%%n",
                successRate);

        System.out.println(
                "Total bytes processed: " + statistics.getTotalBytes());

        System.out.printf(
                Locale.ROOT,
                "Average successful-file processing time: %.3f ms%n",
                statistics.getAverageProcessingTimeMillis());

        System.out.println("Elapsed time: " + elapsedMillis + " ms");

        if (elapsedMillis > 0) {
            double throughput = totalFiles * 1000.0 / elapsedMillis;

            System.out.printf(
                    Locale.ROOT,
                    "Throughput: %.2f files/second%n",
                    throughput);
        }
    }
}
