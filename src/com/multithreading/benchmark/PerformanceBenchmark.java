
package com.multithreading.benchmark;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PerformanceBenchmark {

    private static final Path RESULTS_DIRECTORY =
            Path.of("benchmark_results");

    private static final Path RESULTS_FILE =
            RESULTS_DIRECTORY.resolve("benchmark_results.csv");

    public void runBenchmark(List<Path> files)
            throws InterruptedException, ExecutionException {

        if (files == null || files.isEmpty()) {
            System.out.println("No files available for benchmarking.");
            return;
        }

        int processors = Runtime.getRuntime().availableProcessors();

        // Use unique thread counts to avoid duplicate benchmark runs.
        Set<Integer> threadCountSet = new LinkedHashSet<>();
        threadCountSet.add(1);
        threadCountSet.add(Math.max(2, Math.min(4, processors)));
        threadCountSet.add(Math.max(2, processors));
        threadCountSet.add(Math.max(4, processors * 2));

        List<Integer> threadCounts = new ArrayList<>(threadCountSet);
        List<BenchmarkResult> results = new ArrayList<>();

        long singleThreadTime = -1;

        System.out.println("\n=== PERFORMANCE BENCHMARK ===");
        System.out.println("Files: " + files.size());
        System.out.println("Available processors: " + processors);
        System.out.println();

        System.out.printf(
                Locale.ROOT,
                "%-10s %-15s %-15s %-20s%n",
                "Threads", "Time (ms)", "Speedup", "Files/second");

        for (int threadCount : threadCounts) {
            long elapsedMillis = measure(files, threadCount);

            if (threadCount == 1) {
                singleThreadTime = elapsedMillis;
            }

            double speedup = elapsedMillis > 0 && singleThreadTime > 0
                    ? (double) singleThreadTime / elapsedMillis
                    : 0.0;

            double throughput = elapsedMillis > 0
                    ? files.size() * 1000.0 / elapsedMillis
                    : 0.0;

            BenchmarkResult result = new BenchmarkResult(
                    threadCount,
                    elapsedMillis,
                    speedup,
                    throughput);

            results.add(result);

            System.out.printf(
                    Locale.ROOT,
                    "%-10d %-15d %-15.2fx %-20.2f%n",
                    threadCount,
                    elapsedMillis,
                    speedup,
                    throughput);
        }

        try {
            saveResults(files.size(), processors, results);

            System.out.println("\nBenchmark results saved to:");
            System.out.println(RESULTS_FILE.toAbsolutePath());

        } catch (IOException e) {
            System.err.println(
                    "Could not save benchmark results: " + e.getMessage());
        }

        System.out.println(
                "\nBenchmark complete. Results depend on your files and hardware.");
    }

    private long measure(List<Path> files, int threadCount)
            throws InterruptedException, ExecutionException {

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        long start = System.nanoTime();

        try {
            List<Future<Long>> futures = new ArrayList<>();

            for (Path file : files) {
                futures.add(executor.submit(() -> countWords(file)));
            }

            for (Future<Long> future : futures) {
                future.get();
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

        return TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - start);
    }

    private long countWords(Path file) throws IOException {
        long count = 0;

        try (var reader = Files.newBufferedReader(
                file, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                if (!trimmed.isEmpty()) {
                    count += trimmed.split("\\s+").length;
                }
            }
        }

        return count;
    }

    private void saveResults(
            int fileCount,
            int processors,
            List<BenchmarkResult> results) throws IOException {

        Files.createDirectories(RESULTS_DIRECTORY);

        try (BufferedWriter writer = Files.newBufferedWriter(
                RESULTS_FILE,
                StandardCharsets.UTF_8)) {

            writer.write(
                    "files_tested,available_processors,thread_count,"
                    + "elapsed_ms,speedup,files_per_second");
            writer.newLine();

            for (BenchmarkResult result : results) {
                writer.write(String.format(
                        Locale.ROOT,
                        "%d,%d,%d,%d,%.4f,%.4f",
                        fileCount,
                        processors,
                        result.threadCount(),
                        result.elapsedMillis(),
                        result.speedup(),
                        result.throughput()));

                writer.newLine();
            }
        }
    }

    private record BenchmarkResult(
            int threadCount,
            long elapsedMillis,
            double speedup,
            double throughput) {
    }
}
