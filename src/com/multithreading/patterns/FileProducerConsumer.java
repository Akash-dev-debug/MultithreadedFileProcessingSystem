
package com.multithreading.patterns;

import com.multithreading.manager.FileResultRegistry;
import com.multithreading.manager.ProcessingStatistics;
import com.multithreading.monitor.ProgressTracker;
import com.multithreading.tasks.FileProcessorTask;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class FileProducerConsumer {

    private static final Path STOP_SIGNAL =
            Path.of("__FILE_PROCESSING_STOP_SIGNAL__");

    private final ConcurrentFileQueue fileQueue;
    private final ExecutorService executor;
    private final AtomicInteger processedCount;
    private final AtomicInteger failedCount;
    private final int workerCount;
    private final FileResultRegistry resultRegistry;
    private final ProgressTracker progressTracker;
    private final ProcessingStatistics processingStatistics;

    public FileProducerConsumer(
            ConcurrentFileQueue fileQueue,
            ExecutorService executor,
            AtomicInteger processedCount,
            AtomicInteger failedCount,
            int workerCount,
            FileResultRegistry resultRegistry,
            ProgressTracker progressTracker,
            ProcessingStatistics processingStatistics) {

        if (workerCount <= 0) {
            throw new IllegalArgumentException(
                    "Worker count must be greater than zero.");
        }

        this.fileQueue = fileQueue;
        this.executor = executor;
        this.processedCount = processedCount;
        this.failedCount = failedCount;
        this.workerCount = workerCount;
        this.resultRegistry = resultRegistry;
        this.progressTracker = progressTracker;
        this.processingStatistics = processingStatistics;
    }

    public void processFiles(List<Path> files)
            throws InterruptedException, ExecutionException {

        List<Future<?>> consumers = new ArrayList<>();

        try {
            for (int i = 0; i < workerCount; i++) {
                consumers.add(executor.submit(this::consumeFiles));
            }

            for (Path file : files) {
                fileQueue.addFile(file);
            }

            for (int i = 0; i < workerCount; i++) {
                fileQueue.addFile(STOP_SIGNAL);
            }

            for (Future<?> consumer : consumers) {
                consumer.get();
            }

        } catch (InterruptedException e) {
            for (Future<?> consumer : consumers) {
                consumer.cancel(true);
            }

            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private void consumeFiles() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Path file = fileQueue.takeFile();

                if (STOP_SIGNAL.equals(file)) {
                    return;
                }

                FileProcessorTask task = new FileProcessorTask(
                        file,
                        processedCount,
                        failedCount,
                        progressTracker,
                        processingStatistics);

                try {
                    Map<String, Object> result = task.call();
                    resultRegistry.addResult(result);

                    System.out.printf(
                            "[%s] %s - %s%n",
                            result.get("thread"),
                            result.get("filename"),
                            result.get("status"));

                   
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getWorkerCount() {
        return workerCount;
    }
}
