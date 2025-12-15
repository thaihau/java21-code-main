package com.course.lab01.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class SalesManager {

    private AtomicInteger inventory;

    public SalesManager(int initialStock) {
        this.inventory = new AtomicInteger(initialStock);
    }

    /*
     * TODO: Implement Unsafe Logic
     * Use a standard 'if' check (if > 0) then decrement.
     */
    public boolean sellItemUnsafe() {
        // <--- PASTE/TYPE CODE HERE
        
        return false; 
    }

    /*
     * TODO: Implement Safe Logic (CAS Loop)
     * Use 'compareAndSet' inside the loop.
     */
    public boolean sellItemSafe() {
        while (true) {
            int currentStock = inventory.get();

            if (currentStock <= 0) {
                return false; 
            }

            // <--- PASTE/TYPE CODE HERE
            
        }
    }

    public int getStock() {
        return inventory.get();
    }
}