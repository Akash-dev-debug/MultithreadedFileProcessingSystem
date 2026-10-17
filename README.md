Multithreaded File Processing System

A Java-based file processing application that demonstrates multithreading, concurrent programming, producer-consumer architecture, thread-pool management, synchronization, and performance monitoring.

Project Overview

The Multithreaded File Processing System processes multiple files concurrently using Java's concurrency utilities. It supports file discovery, content analysis, progress tracking, error handling, and performance benchmarking.

The application is designed to demonstrate how multithreading can improve the efficiency of file-processing workloads while maintaining thread safety and managing shared resources.

Features

Concurrent File Processing: Processes multiple files using a configurable thread pool.

Producer-Consumer Pattern: Uses a bounded concurrent queue to distribute files to worker threads.

Thread-Safe Operations: Uses atomic counters and concurrent data structures to manage shared information safely.

File Analysis: Calculates file size, line count, word count, processing time, and processing status.

Progress Tracking: Displays completed files, successful files, failed files, and progress percentage.

Exception Handling: Handles file-access errors and thread interruptions.

Thread Monitoring: Displays JVM thread information, thread states, and memory usage.

Performance Benchmarking: Compares processing performance using different thread counts.

Benchmark Results Export: Saves benchmark measurements in a CSV file.

Input Validation: Handles invalid menu choices and invalid numeric input.

Technologies Used

Java

Java Concurrency API

ExecutorService and thread pools

BlockingQueue and concurrent collections

AtomicInteger and AtomicLong

Java NIO file operations

CSV file handling

Project Structure

MultithreadedFileProcessingSystem/
├── src/
│   └── com/
│       └── multithreading/
│           ├── Main.java
│           ├── tasks/
│           │   └── FileProcessorTask.java
│           ├── manager/
│           │   ├── FileProcessingManager.java
│           │   ├── ProcessingStatistics.java
│           │   ├── FileProcessingExceptionHandler.java
│           │   ├── ThreadPoolFactory.java
│           │   └── FileResultRegistry.java
│           ├── patterns/
│           │   ├── ConcurrentFileQueue.java
│           │   └── FileProducerConsumer.java
│           ├── monitor/
│           │   ├── ThreadMonitor.java
│           │   └── ProgressTracker.java
│           └── benchmark/
│               └── PerformanceBenchmark.java
├── test_files/
│   ├── sample1.txt
│   ├── sample2.csv
│   └── sample3.log
├── benchmark_results/
│   └── benchmark_results.csv
├── docs/
├── screenshots/
├── out/
├── sources.txt
└── README.md

Requirements

Java Development Kit (JDK) 17 or later

Command Prompt or terminal

Visual Studio Code or another Java-compatible IDE

The project has been compiled and tested using Java 26.

How to Run

1. Open the project directory

Open Command Prompt and navigate to the project folder:

cd /d C:\Users\dell\Desktop\MultithreadedFileProcessingSystem

Change the path if your project is stored elsewhere.

2. Compile the Java source files

dir /s /b src\*.java > sources.txt
javac -d out @sources.txt

3. Run the application

java -cp out com.multithreading.Main

Application Menu

The application provides the following options:

Process Directory (Thread Pool): Finds supported files in a directory and processes them concurrently.

Run Producer-Consumer Example: Demonstrates file processing using a bounded queue and multiple consumer workers.

Thread Monitoring: Displays information about JVM threads and memory usage.

Performance Benchmark: Compares processing performance using different thread counts.

Exit: Closes the application.

Supported File Types

The application discovers the following file types:

.txt

.csv

.log

File discovery and processing depend on the selected operation and the directory provided.

Processing Results

For each processed file, the application can report:

File name

File size in bytes

Number of lines

Number of words

Processing status

Worker thread name

Processing time

The application also displays aggregate statistics, including successful and failed files, total bytes processed, average processing time, elapsed time, and throughput.

Producer-Consumer Design

The producer-consumer implementation uses a bounded concurrent queue to transfer files from the producer to worker threads.

The producer adds discovered files to the queue. Consumer workers retrieve files and process them concurrently. Stop signals allow the workers to finish after the queued files have been handled.

This design demonstrates task distribution, concurrent execution, and controlled coordination between producers and consumers.

Performance Benchmarking

The benchmark compares execution times for different thread counts and exports results to:

benchmark_results/benchmark_results.csv

The CSV contains these columns:

Column

Description

files_tested

Number of files processed

available_processors

Number of processors reported by the JVM

thread_count

Number of worker threads used

elapsed_ms

Measured execution time in milliseconds

speedup

Performance relative to the one-thread baseline

files_per_second

Measured processing throughput

Note: Benchmark results depend on file sizes, system load, storage performance, and JVM warm-up. Very small test files are not sufficient to establish reliable performance improvements. Use a larger dataset and repeat measurements for a more meaningful comparison.

Testing Performed

The following checks have been performed during development:

Java source compilation

Processing the sample text, CSV, and log files

Producer-consumer processing with multiple workers

Thread monitoring output

Performance benchmark execution and CSV export

Invalid menu input handling

Rejection of a zero value for a positive-integer input

These checks demonstrate the core functionality. Additional testing with empty directories, unreadable files, larger datasets, and interruption scenarios is recommended.

Learning Outcomes

This project demonstrates practical application of:

Java multithreading and concurrency

Executor services and thread pools

Producer-consumer architecture

Thread-safe shared state

Concurrent collections and atomic variables

File I/O and resource management

Exception and interruption handling

Progress monitoring and performance measurement

Future Improvements

Add automated unit and integration tests.

Add configurable queue capacity and worker counts.

Support additional file formats.

Generate more detailed benchmark reports.

Add automated testing for file-access failures and thread interruption.

Add a graphical interface for monitoring processing progress.

Conclusion

The Multithreaded File Processing System provides a practical demonstration of concurrent file processing in Java. It combines thread-pool management, producer-consumer coordination, progress tracking, error handling, and performance  measurement in a single application.