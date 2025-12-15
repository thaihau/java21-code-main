package com.course.lab01.concurrency;

import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SalesTest {

    @Test
    void testOverselling_Unsafe() throws InterruptedException {
        // Setup: 5 Items, 10 Buyers
        SalesManager store = new SalesManager(5);
        ExecutorService service = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            service.submit(() -> store.sellItemUnsafe());
        }

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);

        // EXPECTATION: Stock should stop at 0.
        // REALITY: Stock goes below 0 (Oversold!)
        System.out.println("Unsafe Stock Remaining: " + store.getStock());
        
        // Assert that we successfully broke the system
        assertTrue(store.getStock() < 0, "Race condition failed to trigger! Stock should be negative.");
    }

    /*
     * TODO: Uncomment for Step 4
     */
    // @Test
    // void testOverselling_Safe() throws InterruptedException {
    //     SalesManager store = new SalesManager(5);
    //     ExecutorService service = Executors.newFixedThreadPool(10);

    //     for (int i = 0; i < 10; i++) {
    //         service.submit(() -> store.sellItemSafe());
    //     }

    //     service.shutdown();
    //     service.awaitTermination(5, TimeUnit.SECONDS);

    //     System.out.println("Safe Stock Remaining: " + store.getStock());
    //     assertEquals(0, store.getStock(), "Stock should stop exactly at 0.");
    // }
}