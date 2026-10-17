
package com.multithreading.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadMonitor {

    private final ThreadMXBean threadBean;

    public ThreadMonitor() {
        this.threadBean = ManagementFactory.getThreadMXBean();
    }

    public void displayThreadReport() {
        System.out.println("\n=== THREAD MONITORING REPORT ===");

        System.out.println("Available processors: "
                + Runtime.getRuntime().availableProcessors());

        System.out.println("Live JVM threads: "
                + threadBean.getThreadCount());

        System.out.println("Peak JVM threads: "
                + threadBean.getPeakThreadCount());

        System.out.println("Total started threads: "
                + threadBean.getTotalStartedThreadCount());

        System.out.println("\n=== THREAD STATES ===");

        ThreadInfo[] threadInfos =
                threadBean.dumpAllThreads(false, false);

        int runnable = 0;
        int waiting = 0;
        int timedWaiting = 0;
        int blocked = 0;
        int terminated = 0;

        for (ThreadInfo info : threadInfos) {
            switch (info.getThreadState()) {
                case RUNNABLE:
                    runnable++;
                    break;

                case WAITING:
                    waiting++;
                    break;

                case TIMED_WAITING:
                    timedWaiting++;
                    break;

                case BLOCKED:
                    blocked++;
                    break;

                case TERMINATED:
                    terminated++;
                    break;

                default:
                    break;
            }
        }

        System.out.println("RUNNABLE: " + runnable);
        System.out.println("WAITING: " + waiting);
        System.out.println("TIMED_WAITING: " + timedWaiting);
        System.out.println("BLOCKED: " + blocked);
        System.out.println("TERMINATED: " + terminated);

        Runtime runtime = Runtime.getRuntime();

        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maximumMemory = runtime.maxMemory();

        System.out.printf("Used memory: %.2f MB%n",
                usedMemory / (1024.0 * 1024));

        System.out.printf("Maximum JVM memory: %.2f MB%n",
                maximumMemory / (1024.0 * 1024));
    }
}
