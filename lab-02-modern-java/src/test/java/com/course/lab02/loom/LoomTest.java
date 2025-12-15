package com.course.lab02.loom;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoomTest {

    private final LoomSimulation sim = new LoomSimulation();
    private final int TASK_COUNT = 1_000; 

    @Test
    void testPlatformBottleneck() {
        long duration = sim.runPlatform(TASK_COUNT);
        
        System.out.println("Platform Threads Time: " + duration + "ms");
        assertTrue(duration >= 5000, "Platform threads should be slow due to pooling.");
    }

    @Test
    void testVirtualSpeed() {
        /*
         * TODO: Test Virtual Thread performance
         */
         // <--- PASTE/TYPE CODE HERE
    }
}