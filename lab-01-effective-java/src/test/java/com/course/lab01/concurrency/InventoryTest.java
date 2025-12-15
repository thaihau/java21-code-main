package com.course.lab01.concurrency;

import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventoryTest {

    @Test
    void testUnsafeInventory_HasRaceCondition() throws InterruptedException {
        // 1. Setup
        UnsafeInventory inventory = new UnsafeInventory();
        int numberOfThreads = 1000;
        ExecutorService service = Executors.newFixedThreadPool(10);

        // 2. Action: 1000 threads try to increment at the same time
        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> inventory.increment());
        }

        // 3. Wait for all to finish
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);

        // 4. Verify
        // EXPECTATION: 1000
        // REALITY: Probably 980-999 (Due to lost updates)
        assertEquals(1000, inventory.getStockLevel(), 
            "Race Condition Detected! The stock count should be exactly 1000.");
    }
    
    /* * TODO: Uncomment this later for the SafeInventory test
     */
    // @Test
    // void testSafeInventory_IsThreadSafe() throws InterruptedException {
    //     SafeInventory inventory = new SafeInventory();
    //     int numberOfThreads = 1000;
    //     ExecutorService service = Executors.newFixedThreadPool(10);

    //     for (int i = 0; i < numberOfThreads; i++) {
    //         service.submit(() -> inventory.increment());
    //     }

    //     service.shutdown();
    //     service.awaitTermination(5, TimeUnit.SECONDS);

    //     assertEquals(1000, inventory.getStockLevel(), "Atomic implementation should be perfectly thread-safe.");
    // }
}