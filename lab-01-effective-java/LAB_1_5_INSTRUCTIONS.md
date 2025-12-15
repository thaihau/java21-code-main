# Lab 1.5: The Concurrent Inventory

## Topic and Timing
* **Topic:** Shared Mutable State, Race Conditions, `AtomicInteger`, and "Check-Then-Act" Logic.
* **Estimated Duration:** 25 Minutes


## Scenario
You are building the Inventory Service for a high-traffic e-commerce site.
We are running a "Flash Sale" where thousands of users click "Buy" simultaneously.

**The Problem:**
1.  **Lost Updates:** Simple math (`++`) is not atomic. Steps get lost when threads collide.
2.  **Logical Gaps:** Even if variables are safe, your *logic* might have gaps where threads can sneak in (Check-Then-Act).

## Setup and Files
* **Module:** `lab-01-effective-java`
* **Package:** `com.course.lab01.concurrency`
* **Files:**
    * `UnsafeInventory.java` & `SafeInventory.java` (Activity 1)
    * `InventoryTest.java` (Validator 1)
    * `SalesManager.java` (Activity 2)
    * `SalesTest.java` (Validator 2)

---

## Activity 1: The Basic Counter

### Step 1: Create the "Lost Update" Bug
1.  Open `UnsafeInventory.java`.
2.  Locate the `increment()` method.
3.  **Instruction:** Type the following naive code:
    ```java
    stockLevel++;
    ```
4.  **Why this fails:** `++` is actually 3 steps (Read -> Modify -> Write). If two threads read "10" at the same time, they both write "11". One update disappears.

### Step 2: Witness the Failure
1.  Open `InventoryTest.java`.
2.  Run `testUnsafeInventory_HasRaceCondition()`.
3.  **Observe:**
    * The test expects **1000**.
    * The actual result is likely **980 - 999**.
    * **Conclusion:** We lost data. The system is non-deterministic.

### Step 3: Fix with Atomic Variables
1.  Open `SafeInventory.java`.
2.  Locate `increment()`.
3.  **Instruction:** Use the Atomic method to perform the operation in one hardware step:
    ```java
    stockLevel.incrementAndGet();
    ```
4.  **Verify:**
    * Return to `InventoryTest.java`.
    * Uncomment and run `testSafeInventory_IsThreadSafe()`.
    * It passes (Green Check).

---

## Activity 2: The "Overselling" Logic (Check-Then-Act)

### Step 4: Create the Logical Race Condition
1.  Open `SalesManager.java`.
2.  Locate `sellItemUnsafe()`.
3.  **Instruction:** Implement a standard "Check then Decrement" logic:
    ```java
    if (inventory.get() > 0) {
        // Simulate a tiny thread pause to force the error
        try { Thread.sleep(10); } catch (Exception e) {}
        
        inventory.decrementAndGet();
        return true;
    }
    return false;
    ```
4.  **Why this fails:** Thread A checks `stock > 0` (True). Thread A pauses. Thread B sells the last item. Thread A resumes and decrements anyway. Stock goes to **-1**.

### Step 5: Witness the Oversell
1.  Open `SalesTest.java`.
2.  Run `testOverselling_Unsafe()`.
3.  **Observe:**
    * Console Output: `Unsafe Stock Remaining: -2` (or similar).
    * **Conclusion:** We sold items we didn't have.

### Step 6: Fix with a CAS Loop
1.  Return to `SalesManager.java`.
2.  Locate `sellItemSafe()`.
3.  **Instruction:** Implement a "Compare-And-Swap" loop to close the gap:
    ```java
    // Inside the while(true) loop:
    
    // 2. Atomic Attempt
    // Try to update ONLY IF the value is still 'currentStock'
    if (inventory.compareAndSet(currentStock, currentStock - 1)) {
        return true; // Success: We won the race
    }
    
    // If false, the loop repeats and tries again with the new value!
    ```
4.  **Verify:**
    * Return to `SalesTest.java`.
    * Uncomment and run `testOverselling_Safe()`.
    * It passes. Stock stops exactly at **0**.

## References

### Concepts
* **Race Condition:** Output depends on the timing of other events.
* **Atomicity:** An operation that is indivisible.
* **CAS (Compare-And-Swap):** An optimistic technique. "Update X to Y only if X is currently Z."

### Classes
* **`AtomicInteger`**: Thread-safe integer using hardware CAS.
* **`compareAndSet(expect, update)`**: The method that enables lock-free thread safety.