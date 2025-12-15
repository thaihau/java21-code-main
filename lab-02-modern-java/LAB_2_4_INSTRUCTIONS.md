# Lab 2.4: Virtual Threads (Project Loom)

## Topic and Timing

  * **Topic:** High-Throughput Concurrency with `Thread.ofVirtual()`.
  * **Estimated Duration:** 20 Minutes
    

## Scenario

We are building a **High-Performance Web Server** simulation.
The server needs to handle 1,000 concurrent requests. Each request simulates a database call that "blocks" (sleeps) for 1 second.

**The Problem (Platform Threads):**
Traditional OS threads are expensive (heavy memory footprint). You cannot create 1,000 of them easily. You are forced to use a **Thread Pool** (e.g., limit 100).

  * **Result:** Requests queue up. 1,000 tasks / 100 threads = 10 cycles. Total time = \~10 seconds.

**The Solution (Virtual Threads):**
Virtual Threads are lightweight (managed by the JVM, not the OS). We can create a new thread for **every single task**.

  * **Result:** All 1,000 tasks run in parallel. Total time = \~1 second.

## Setup and Files

  * **Module:** `lab-02-modern-java`
  * **Package:** `com.course.lab02.loom`
  * **Files:**
      * `BlockingTask.java` (The Workload - Read Only)
      * `LoomSimulation.java` (The Engine - You will build this)
      * `LoomTest.java` (The Validator)

## Steps

### Step 1: Analyze the Blocking Task
1.  Open `src/main/java/com/course/lab02/loom/BlockingTask.java`.
2.  **Observe:** It calls `Thread.sleep(Duration.ofSeconds(1))`.
    * **Note:** This is the modern, readable way to pause execution (introduced in Java 19), replacing the old `Thread.sleep(millis)`.
    * In the old world, this call wastes resources by holding an OS thread.
    * In the new world, Virtual Threads detect this "blocking" call and automatically unmount.

### Step 2: Implement the Virtual Thread Executor

1.  Open `src/main/java/com/course/lab02/loom/LoomSimulation.java`.
2.  Locate the `runVirtual` method.
3.  **Action:** Implement the logic using the new Java 21 Executor.
    ```java
    var start = Instant.now();

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        IntStream.range(0, taskCount).forEach(i -> 
            executor.submit(new BlockingTask())
        );
    } 

    return Duration.between(start, Instant.now()).toMillis();
    ```
4.  **Code Explanation:**
      * **`var`:** This is Local Variable Type Inference (Java 10+). The compiler figures out that `start` is an `Instant` and `executor` is an `ExecutorService`, so you don't have to type the class names explicitly.
      * **`Executors.newVirtualThreadPerTaskExecutor()`:** This is the key method. Unlike a thread pool (which reuses a fixed set of threads), this executor creates a brand new, lightweight virtual thread for *every single task* you submit.
      * **Implicit Wait (`try` block):** Because we are using the `try-with-resources` syntax, the code will automatically pause at the closing brace `}` until **all** submitted virtual threads have finished. This is part of the new "Structured Concurrency" philosophy.

### Step 3: Validate the Performance Gap

1.  Open `src/test/java/com/course/lab02/loom/LoomTest.java`.
2.  Locate the `testVirtualSpeed` method.
3.  **Action:** Add the test logic to verify the speedup.
    ```java
    // 1. Run with Virtual Threads (1,000 tasks)
    long duration = sim.runVirtual(TASK_COUNT);

    System.out.println("Virtual Threads Time: " + duration + "ms");

    // 2. Assert Speed
    // It should finish in ~1 second (plus slight overhead), definitely under 2 seconds.
    assertTrue(duration < 2000, "Virtual threads should run in parallel, finishing in ~1 sec");
    ```
4.  Run the tests.
      * **Expectation:** `testPlatformBottleneck` takes \~10s (slower). `testVirtualSpeed` takes \~1s (faster).

## Important Considerations

### 1\. Do Not Pool Virtual Threads

**Never** use a Thread Pool for virtual threads.

  * **Old Habit:** `Executors.newFixedThreadPool(100)` (To save expensive threads).
  * **New Habit:** `Executors.newVirtualThreadPerTaskExecutor()` (Create, run, destroy).
  * **Why:** Virtual threads are cheap. Pooling them adds overhead without benefit.

### 2\. The "Pinning" Problem

Avoid using `synchronized` blocks around long blocking operations when using Virtual Threads.

  * **Issue:** A virtual thread "pins" the underlying OS carrier thread to the CPU while inside a `synchronized` block, preventing it from unmounting.
  * **Fix:** Use `ReentrantLock` instead of `synchronized` if you need to lock during blocking I/O.




