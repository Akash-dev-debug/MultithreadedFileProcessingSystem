
package com.multithreading.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileResultRegistry {

    private final ConcurrentLinkedQueue<Map<String, Object>> results =
            new ConcurrentLinkedQueue<>();

    public void addResult(Map<String, Object> result) {
        if (result == null) {
            throw new IllegalArgumentException(
                    "Result cannot be null.");
        }

        results.add(Collections.unmodifiableMap(result));
    }

    public List<Map<String, Object>> getResults() {
        return Collections.unmodifiableList(
                new ArrayList<>(results));
    }

    public int getResultCount() {
        return results.size();
    }

    public long getSuccessfulCount() {
        return results.stream()
                .filter(result ->
                        "SUCCESS".equals(result.get("status")))
                .count();
    }

    public long getFailedCount() {
        return results.stream()
                .filter(result ->
                        !"SUCCESS".equals(result.get("status")))
                .count();
    }
}
