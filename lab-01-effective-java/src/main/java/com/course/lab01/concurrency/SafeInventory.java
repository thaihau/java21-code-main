package com.course.lab01.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class SafeInventory {

    // Shared State: We use an Atomic class which is Thread-Safe by default.
    private AtomicInteger stockLevel = new AtomicInteger(0);

    /**
     * TODO: Implement thread-safe increment.
     * Instruction: Type 'stockLevel.incrementAndGet();' inside.
     */
    public void increment() {
        // <--- PASTE CODE HERE
        
    }

    public int getStockLevel() {
        // .get() guarantees we see the latest value from memory
        return stockLevel.get();
    }
}