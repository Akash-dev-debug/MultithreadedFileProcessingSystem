# Project Documentation
## Multithreaded File Processing System

## 1. Introduction

The Multithreaded File Processing System is a Java application developed to demonstrate multithreading and concurrency concepts. It processes multiple files concurrently using worker threads, manages shared data safely, tracks processing progress, and measures performance.

The application supports text files, CSV files, and log files. It provides both thread-pool processing and a producer-consumer implementation.

## 2. Objectives

The main objectives of this project are:

- To implement concurrent file processing using Java threads.
- To use thread pools for efficient task management.
- To implement the producer-consumer design pattern.
- To demonstrate thread-safe data management.
- To monitor processing progress and thread activity.
- To handle processing errors and thread interruptions.
- To measure processing performance using benchmarks.

## 3. Technologies Used

- **Programming language:** Java
- **File handling:** Java NIO and buffered readers
- **Concurrency:** ExecutorService, Callable, atomic variables, and concurrent collections
- **Data structures:** Bounded blocking queue and concurrent result registry
- **Performance reporting:** CSV output
- **Development environment:** Visual Studio Code and JDK

## 4. Main Features

### 4.1 Thread-Pool File Processing

The application uses a configurable thread pool to process multiple files concurrently. Each file is assigned to a worker thread, allowing multiple processing tasks to run without creating a separate thread manually for every file.

### 4.2 Producer-Consumer Pattern

The producer discovers files and places them into a bounded queue. Consumer workers retrieve files from the queue and process them.

Stop signals are used to indicate when consumers should finish their work.

### 4.3 File Content Analysis

For each successfully processed file, the application calculates:

- File size in bytes
- Number of lines
- Number of words
- Processing time
- Worker thread name
- Processing status

### 4.4 Progress Tracking

The progress tracker maintains counts of completed, successful, and failed files. It displays the completion percentage as files finish processing.

### 4.5 Thread Monitoring

The thread monitoring feature reports JVM thread information, including available processors, live threads, peak thread count, thread states, and memory usage.

### 4.6 Performance Benchmarking

The benchmark feature compares processing times using different worker-thread counts. Results are exported to a CSV file for further analysis.

### 4.7 Error Handling

The application includes validation for menu choices and numeric inputs. File-processing and interruption-handling logic is also implemented to manage failures during execution.

## 5. System Architecture

The application is divided into packages based on responsibility.

| Package | Responsibility |
|---|---|
| `com.multithreading` | Application entry point and menu |
| `com.multithreading.tasks` | Individual file-processing tasks |
| `com.multithreading.manager` | Processing coordination, statistics, result registry, thread-pool creation, and exception handling |
| `com.multithreading.patterns` | Concurrent queue and producer-consumer implementation |
| `com.multithreading.monitor` | Progress tracking and thread monitoring |
| `com.multithreading.benchmark` | Performance benchmarking |

## 6. Important Classes

### Main.java

Starts the application and displays the menu. It allows users to choose directory processing, producer-consumer processing, thread monitoring, performance benchmarking, or exit.

### FileProcessorTask.java

Implements the file-processing task. It reads a file, calculates its size, line count, and word count, records processing time, and updates statistics.

### FileProcessingManager.java

Coordinates directory processing. It discovers supported files, creates the required processing components, manages execution, and displays the final report.

### ThreadPoolFactory.java

Creates the executor service used to manage worker threads. It also configures thread naming and uncaught-exception handling.

### ConcurrentFileQueue.java

Provides a bounded queue for transferring file paths between the producer and consumer workers.

### FileProducerConsumer.java

Implements the producer-consumer workflow. It queues files, starts consumer workers, and collects processing results.

### ProcessingStatistics.java

Maintains aggregate statistics, including successful files, failed files, bytes processed, and total processing time.

### FileResultRegistry.java

Stores processing results so that they can be collected and displayed after tasks complete.

### ProgressTracker.java

Tracks completed, successful, and failed files and calculates the progress percentage.

### ThreadMonitor.java

Collects and displays JVM thread information and memory usage.

### PerformanceBenchmark.java

Measures file-processing performance using different thread counts and exports benchmark results.

## 7. Processing Workflow

The general processing workflow is:

1. The user selects a processing option.
2. The application validates the input directory.
3. Supported files are discovered.
4. A thread pool and the required processing components are created.
5. Files are submitted for processing or placed in the concurrent queue.
6. Worker threads read and analyze files.
7. Processing results and statistics are updated.
8. Progress is displayed as files finish.
9. The application displays a summary and shuts down its executor service.

## 8. Concurrency and Thread Safety

The project uses Java concurrency utilities to coordinate work across multiple threads.

- **ExecutorService:** Manages worker-thread execution.
- **AtomicInteger and AtomicLong:** Support atomic updates to shared counters and totals.
- **Concurrent queue:** Transfers work between producers and consumers.
- **Concurrent result registry:** Allows results to be collected safely across concurrent tasks.
- **Synchronization:** Protects progress-tracking operations that update and display related counters.
- **Interruption handling:** Allows processing code to respond to interruption requests.

These mechanisms help reduce race conditions when multiple workers update shared state.

## 9. Supported File Types

The application discovers these file extensions:

- `.txt`
- `.csv`
- `.log`

The sample test dataset contains:

- `sample1.txt`
- `sample2.csv`
- `sample3.log`

## 10. Application Menu

The application provides five options:

1. Process Directory (Thread Pool)
2. Run Producer-Consumer Example
3. Thread Monitoring
4. Performance Benchmark
5. Exit

## 11. Benchmark Results

Benchmark measurements are saved to:

`benchmark_results/benchmark_results.csv`

The CSV contains the number of files tested, available processors, thread count, elapsed time, speedup, and throughput.

Benchmark results depend on file sizes, system load, JVM warm-up, and storage performance. The three-file sample dataset is useful for functional demonstrations but is too small for reliable performance conclusions. Larger datasets and repeated runs are recommended for meaningful comparisons.

## 12. Testing and Verification

During development, the following checks were performed:

- Compilation of Java source files
- Successful processing of the three sample files
- Producer-consumer processing using multiple workers
- Thread monitoring output
- Benchmark execution and CSV generation
- Invalid menu input handling
- Rejection of invalid numeric input, including a zero worker count

Further verification should include empty directories, unreadable files, actual file-processing failures, interruption scenarios, and larger datasets. These scenarios should be tested before claiming complete coverage of error-handling requirements.

## 13. Limitations

- Performance measurements based on three small files are not representative of large workloads.
- Additional automated tests would improve confidence in failure handling and concurrency behavior.
- The application currently supports a limited set of file extensions.
- A graphical interface is not included.

## 14. Future Enhancements

Possible improvements include:

- Automated unit and integration tests
- More detailed error reports
- Configurable queue capacity
- Support for additional file types
- More comprehensive benchmark reports
- A graphical progress-monitoring interface

## 15. Conclusion

The Multithreaded File Processing System demonstrates important Java concurrency concepts through practical file-processing tasks. It combines thread-pool execution, producer-consumer coordination, thread-safe statistics, progress tracking, monitoring, and benchmarking in one application.

The project provides a foundation for further experimentation with concurrent programming, performance analysis, and reliable resource management.
