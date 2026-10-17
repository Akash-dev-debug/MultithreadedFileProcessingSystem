
package com.multithreading;

import com.multithreading.benchmark.PerformanceBenchmark;
import com.multithreading.manager.FileProcessingManager;
import com.multithreading.manager.FileResultRegistry;
import com.multithreading.manager.ProcessingStatistics;
import com.multithreading.monitor.ProgressTracker;
import com.multithreading.monitor.ThreadMonitor;
import com.multithreading.patterns.ConcurrentFileQueue;
import com.multithreading.patterns.FileProducerConsumer;
import java.util.concurrent.Executors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("   MULTITHREADED FILE PROCESSING SYSTEM");
        System.out.println("   Multithreading & Concurrency");
        System.out.println("==============================================");

        boolean running = true;

        while (running) {
            displayMenu();
            System.out.print("Enter your choice: ");

            String choice = SCANNER.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> processDirectory();
                    case "2" -> runProducerConsumer();
                    case "3" -> new ThreadMonitor().displayThreadReport();
                    case "4" -> runBenchmark();
                    case "5" -> {
                        running = false;
                        System.out.println("Application closed.");
                    }
                    default -> System.out.println(
                            "Invalid choice. Enter a number from 1 to 5.");
                }
            } catch (IOException | InterruptedException
                    | ExecutionException e) {

                System.err.println("Operation failed: " + e.getMessage());

                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid input: " + e.getMessage());
            }
        }

        SCANNER.close();
    }

    private static void displayMenu() {
        System.out.println("\n=== FILE PROCESSING MENU ===");
        System.out.println("1. Process Directory (Thread Pool)");
        System.out.println("2. Run Producer-Consumer Example");
        System.out.println("3. Thread Monitoring");
        System.out.println("4. Performance Benchmark");
        System.out.println("5. Exit");
    }

    private static void processDirectory()
            throws IOException, InterruptedException, ExecutionException {

        System.out.print("Enter directory path: ");
        String directory = SCANNER.nextLine().trim();

        int threadCount = readPositiveInteger("Enter thread pool size: ");

        FileProcessingManager manager =
                new FileProcessingManager(threadCount);

        manager.processDirectory(directory);
    }

    private static void runProducerConsumer()
            throws IOException, InterruptedException, ExecutionException {

        System.out.print("Enter directory path: ");
        String directoryPath = SCANNER.nextLine().trim();

        Path directory = Path.of(directoryPath);

        if (!Files.isDirectory(directory)) {
            throw new IOException(
                    "Directory does not exist: " + directoryPath);
        }

        List<Path> files = discoverFiles(directory);

        if (files.isEmpty()) {
            System.out.println("No supported files found (.txt, .csv, .log).");
            return;
        }

        int defaultWorkers = Math.min(
                Runtime.getRuntime().availableProcessors(),
                files.size());

        System.out.println("\n=== PRODUCER-CONSUMER DEMONSTRATION ===");
        System.out.println("Files discovered by producer: " + files.size());
        System.out.println("The producer adds files to a bounded queue.");
        System.out.println("Consumer workers remove and process queued files.");

        int workerCount = readPositiveInteger(
                "Enter number of consumer workers (recommended "
                        + defaultWorkers + "): ");

        workerCount = Math.min(workerCount, files.size());

        AtomicInteger processedCount = new AtomicInteger();
        AtomicInteger failedCount = new AtomicInteger();

        ProgressTracker progressTracker = new ProgressTracker(files.size());
        ProcessingStatistics statistics = new ProcessingStatistics();
        FileResultRegistry resultRegistry = new FileResultRegistry();

        int queueCapacity = Math.max(1, Math.min(files.size(), workerCount * 2));

        ExecutorService executor =
                Executors.newFixedThreadPool(workerCount);

        long startTime = System.nanoTime();

        try {
            ConcurrentFileQueue queue =
                    new ConcurrentFileQueue(queueCapacity);

            FileProducerConsumer producerConsumer =
                    new FileProducerConsumer(
                            queue,
                            executor,
                            processedCount,
                            failedCount,
                            workerCount,
                            resultRegistry,
                            progressTracker,
                            statistics);

            producerConsumer.processFiles(files);

            System.out.println("\n=== PRODUCER-CONSUMER RESULTS ===");

            for (var result : resultRegistry.getResults()) {
                System.out.printf(
                        Locale.ROOT,
                        "%s | Status: %s | Lines: %s | Words: %s | Worker: %s%n",
                        result.get("filename"),
                        result.get("status"),
                        result.getOrDefault("line_count", 0),
                        result.getOrDefault("word_count", 0),
                        result.get("thread"));
            }

            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - startTime);

            System.out.println("\n=== SUMMARY ===");
            System.out.println("Total files: " + files.size());
            System.out.println(
                    "Successful files: " + progressTracker.getSuccessfulFiles());
            System.out.println(
                    "Failed files: " + progressTracker.getFailedFiles());
            System.out.println(
                    "Total bytes processed: " + statistics.getTotalBytes());
            System.out.printf(
                    Locale.ROOT,
                    "Average successful-file processing time: %.3f ms%n",
                    statistics.getAverageProcessingTimeMillis());
            System.out.println("Elapsed time: " + elapsedMillis + " ms");

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
    }

    private static void runBenchmark()
            throws IOException, InterruptedException, ExecutionException {

        System.out.print("Enter directory path: ");
        String directoryPath = SCANNER.nextLine().trim();

        List<Path> files = discoverFiles(Path.of(directoryPath));

        new PerformanceBenchmark().runBenchmark(files);
    }

    private static int readPositiveInteger(String prompt) {
        System.out.print(prompt);

        String input = SCANNER.nextLine().trim();

        try {
            int value = Integer.parseInt(input);

            if (value <= 0) {
                throw new IllegalArgumentException(
                        "Enter a number greater than zero.");
            }

            return value;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Please enter a valid whole number.");
        }
    }

    private static List<Path> discoverFiles(Path directory)
            throws IOException {

        if (!Files.isDirectory(directory)) {
            throw new IOException(
                    "Directory does not exist: " + directory);
        }

        List<Path> files = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName()
                                .toString()
                                .toLowerCase(Locale.ROOT);

                        return name.endsWith(".txt")
                                || name.endsWith(".csv")
                                || name.endsWith(".log");
                    })
                    .forEach(files::add);
        }

        return files;
    }
}
