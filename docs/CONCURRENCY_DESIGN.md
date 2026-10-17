# Concurrency Design
## Multithreaded File Processing System

## 1. Overview

This document describes the concurrency design used in the Multithreaded File Processing System. The application uses Java concurrency utilities to process files concurrently, coordinate producer and consumer workers, track progress, and maintain shared processing statistics.

The design aims to make concurrent execution organized, thread-safe, and easier to monitor.

## 2. Thread-Pool Architecture

The application uses `ExecutorService` to manage worker threads.

Instead of manually creating a new thread for every file, tasks are executed through a thread pool. This approach allows the application to control the number of concurrent workers and reuse threads across tasks.

### Workflow

1. Discover the files to process.
2. Create an executor service.
3. Submit processing tasks.
4. Allow worker threads to process files concurrently.
5. collect the results and update statistics.
6. Shut down the executor when processing is finished.

### Benefits

- Limits the number of concurrent worker threads.
- Reduces the overhead of repeatedly creating threads.
- Separates task definition from task execution.
- Makes worker management easier.

## 3. Producer-Consumer Pattern

The producer-consumer implementation separates file discovery from file processing.

### Producer

The producer discovers supported files and places their paths into a bounded concurrent queue.

### Queue

The queue temporarily stores file paths waiting to be processed. Its capacity limits how many queued items can be held at one time.

A blocking queue allows producers and consumers to coordinate when queue operations cannot proceed immediately.

### Consumers

Consumer workers retrieve file paths from the queue and process the files. Multiple consumers can work concurrently on different files.

### Stop Signals

After the producer has queued the files, stop signals are added so that consumer workers can terminate when their work is complete.

### Workflow

```text
File Discovery
      |
      v
Producer
      |
      v
Bounded Concurrent Queue
      |
      +----------+----------+
      |          |          |
      v          v          v
  Consumer 1  Consumer 2  Consumer 3
      |          |          |
      v          v          v
  Process File Process File Process File
      |          |          |
      +----------+----------+
                 |
                 v
         Processing Results
```

The number of consumers is configurable within the limits enforced by the application.

## 4. Shared Data and Thread Safety

Multiple worker threads may update shared statistics while processing files. Without suitable coordination, concurrent updates could produce inconsistent results.

The application uses Java concurrency utilities to protect shared state.

### Atomic Variables

`AtomicInteger` and `AtomicLong` provide atomic operations for counters and numerical totals.

Examples include:

- Successful file count
- Failed file count
- Completed file count
- Total bytes processed
- Total processing time

Atomic operations prevent individual counter updates from being lost due to simultaneous access.

### Concurrent Collections

Concurrent collections support safe access to shared data by multiple threads.

The application's concurrent queue transfers work to consumers, while its result registry stores results produced by processing tasks.

### Synchronization

The progress tracker synchronizes operations that update related progress counters and display progress. This helps keep progress reporting consistent when workers finish close together.

### Important Distinction

Thread-safe counters and collections do not automatically make every operation in an application thread-safe. Any shared resource that can be modified by multiple workers must be evaluated separately.

The current processing workflow reads files and records results. If future versions allow multiple workers to modify the same file, additional coordination or file-locking rules may be required.

## 5. File-Processing Tasks

The `FileProcessorTask` class implements `Callable` and returns a result containing file-processing information.

Each task:

1. Checks whether processing has been interrupted.
2. Reads file metadata.
3. Reads the file content using a buffered reader and UTF-8 encoding.
4. Counts lines and words.
5. Records the file size and processing time.
6. Updates progress and aggregate statistics.
7. Returns the processing result or handles a processing failure.

The use of buffered reading avoids requiring the entire file content to be loaded into memory at once.

## 6. Exception Handling and Interruption

Concurrent applications must