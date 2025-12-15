package com.course.lab02.loom;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class LoomSimulation {

    // BASELINE: Traditional threads (Max 100).
    public long runPlatform(int taskCount) {
        var start = Instant.now();

        try (var executor = Executors.newFixedThreadPool(100)) {
            IntStream.range(0, taskCount).forEach(i -> 
                executor.submit(new BlockingTask())
            );
        } 

        return Duration.between(start, Instant.now()).toMillis();
    }

    /*
     * TODO: Implement runVirtual using Executors.newVirtualThreadPerTaskExecutor()
     */
    public long runVirtual(int taskCount) {
        // <--- PASTE/TYPE CODE HERE
        return 0;
    }
}