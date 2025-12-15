package com.course.lab02.loom;

import java.time.Duration;

public class BlockingTask implements Runnable {
    
    @Override
    public void run() {
        try {
            // Simulate a blocking IO operation (e.g., DB call) taking 1 second.
            // In a real app, this blocks the OS thread, making it unusable for others.
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
            // Restore interrupt status
            Thread.currentThread().interrupt();
        }
    }
}