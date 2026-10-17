# Testing Report
## Multithreaded File Processing System

## 1. Purpose

This report documents the functional tests performed during development of the Multithreaded File Processing System. It records observed results and identifies additional tests needed to improve confidence in the application's reliability.

## 2. Test Environment

| Item | Details |
|---|---|
| Operating system | Microsoft Windows 10 |
| Programming language | Java |
| Java version | JDK 26 |
| Development environment | Visual Studio Code |
| Test dataset | Three sample files |
| File formats | TXT, CSV, LOG |

## 3. Test Dataset

The application was tested using these files:

| File | Format | Purpose |
|---|---|---|
| `sample1.txt` | Text | Test line and word counting |
| `sample2.csv` | CSV | Test processing of comma-separated data |
| `sample3.log` | Log | Test processing of log entries |

The combined size of the three sample files was reported as 337 bytes.

## 4. Tests Performed

### Test 1: Java Compilation

**Objective:** Verify that the Java source files compile.

**Procedure:**

```cmd
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
```

**Observed result:** Compilation completed without errors.

**Status:** PASS

### Test 2: Thread-Pool File Processing

**Objective:** Verify that the application processes multiple files using a configurable thread pool.

**Procedure:**

1. Start the application.
2. Select option 1.
3. Enter the `test_files` directory.
4. Set the worker-thread count to 3.
5. Observe the processing results.

**Observed result:**

- Three files were discovered.
- Three files were processed successfully.
- Zero file-processing failures were reported.
- The output showed multiple worker-thread names.
- The final report displayed progress, success rate, bytes processed, average processing time, elapsed time, and throughput.

**Status:** PASS

### Test 3: Producer-Consumer Processing

**Objective:** Verify file processing through the producer-consumer workflow.

**Procedure:**

1. Start the application.
2. Select option 2.
3. Enter the `test_files` directory.
4. Configure three consumer workers.
5. Observe the queue-processing results.

**Observed result:**

- Three files were discovered.
- All three files reported `SUCCESS`.
- Zero file-processing failures were reported.
- The summary reported 337 total bytes processed.
- Individual results included line counts, word counts, and worker-thread names.

**Status:** PASS

**Observation:** Duplicate 100% progress messages were visible in the producer-consumer output. This is a display issue that should be corrected and retested.

### Test 4: Thread Monitoring

**Objective:** Verify that the application displays JVM thread and memory information.

**Procedure:** Select option 3 from the application menu.

**Observed result:** The application displayed available processors, live threads, peak thread count, total threads started, thread states, used memory, and maximum JVM memory.

**Status:** PASS

### Test 5: Invalid Menu Input

**Objective:** Verify that non-numeric menu input is rejected.

**Procedure:** Enter `abc` at the menu prompt.

**Observed result:** The application displayed an invalid-choice message and returned to the menu.

**Status:** PASS

### Test 6: Invalid Numeric Input

**Objective:** Verify that invalid numeric values are handled.

**Procedure:** Select directory processing, enter a directory path, and enter a non-numeric value such as `s` when prompted for the thread-pool size.

**Observed