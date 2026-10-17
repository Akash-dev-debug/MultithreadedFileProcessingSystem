
package com.multithreading.patterns;

import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConcurrentFileQueue {

    private final BlockingQueue<Path> queue;

    public ConcurrentFileQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Queue capacity must be greater than zero.");
        }

        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    public void addFile(Path filePath) throws InterruptedException {
        if (filePath == null) {
            throw new IllegalArgumentException(
                    "File path cannot be null.");
        }

        queue.put(filePath);
    }

    public Path takeFile() throws InterruptedException {
        return queue.take();
    }

    public Path pollFile(long timeout, TimeUnit unit)
            throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public int remainingCapacity() {
        return queue.remainingCapacity();
    }
}
