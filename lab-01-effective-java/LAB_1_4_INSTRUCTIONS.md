# Lab 1.4: The Lazy Auditor

* **Topic:** Managing Side Effects (`Consumer`), Debugging (`peek`), and Optimization (`Supplier` / Lazy Evaluation).
* **Estimated Duration:** 20 Minutes

## Scenario
The Compliance Team has requested an **Audit Trail** for the pricing engine.
A developer implemented a logging system in `LegacyLogging.java`, but there is a major performance bug. The system is running slowly even when logging is **disabled**.

**The Problem (Eager Evaluation):**
Java evaluates method arguments *before* passing them to a method.
Even if `LOGGING_ENABLED = false`, Java executes the expensive string concatenation and data lookup (`generateExpensiveAuditTrail`) *before* checking the flag. This creates an unintended **Side Effect** (Latency).

**The Solution (Lazy Evaluation):**
You will refactor the auditing logic using **Functional Interfaces**:
1.  **`Consumer<T>`:** To create a "transparent" side-effect tool for debugging (`peek`).
2.  **`Supplier<T>`:** To wrap the expensive logic in a lambda, ensuring it only runs when absolutely necessary.

## Setup and Files
* **Module:** `lab-01-effective-java`
* **Package:** `com.course.lab01.audit`
* **Files:**
    * `LegacyLogging.java` (The Problem - Read Only)
    * `SmartLogging.java` (The Solution - Methods commented out)
    * `AuditTest.java` (The Validator - Tests commented out)

## Steps

### Step 1: Analyze the Latency Side Effect
1.  Open `src/main/java/com/course/lab01/audit/LegacyLogging.java` and locate the method `calculateWithEagerLog`.
2.  Look closely at the line: `log("Audit Trail ID: " + generateExpensiveAuditTrail(price));`.
    * In standard Java, does `generateExpensiveAuditTrail(price)` run *before* or *after* we enter the `log` method?
    * <details>
        <summary>Hint</summary>
        Even though the log method checks if logging is enabled, that check happens too late.
    </details>
3.  Open `src/test/java/com/course/lab01/audit/AuditTest.java` and uncomment the test `testLegacy_IsSlow()`.
4.  Run the test.
    * **Observe:** The test runs for ~500ms and fails with a `TimeoutException`.
    * **Conclusion:** We are paying a performance penalty for logs we aren't even using.

### Step 2: Examine the Functional Tools
1.  Open `src/main/java/com/course/lab01/audit/SmartLogging.java`.
2.  Locate **Part 1 (The `peek` method)**.
    * This uses `Consumer<T>`, which returns `void`. Note that it executes `auditor.accept(value)` but returns the original `value` so the chain doesn't break.
    * **Uncomment Part 1.**
3.  Locate **Part 2 (The `log` method)**.
    * This accepts a `Supplier<String>` instead of a `String`.
    * **Think:** Look at where `.get()` is called. Under what exact condition does the code inside the Supplier run?
    * **Uncomment Part 2.**

### Step 3: Implement Lazy Evaluation
1.  Still in `SmartLogging.java`, look at **Part 3 (`calculateWithLazyLog`)**.
2.  Analyze the syntax:
    ```java
    log(() -> LegacyLogging.generateExpensiveAuditTrail(price));
    ```
    * We are passing a lambda `() -> ...` instead of a result. This wraps the expensive call in a `Supplier`.
    * **Concept Check:** This is like handing the logger a recipe instead of a cooked meal. If the logger isn't hungry, it never cooks the meal.
3.  **Uncomment Part 3.**

### Step 4: Verify the Performance Fix
1.  Return to `AuditTest.java` and uncomment the test `testSmart_IsFast()`.
2.  Run the test.
    * **Observe:** The test passes instantly (Green Check).
    * **Conclusion:** Because logging was disabled, the `Supplier.get()` was never called, and the 500ms delay was completely skipped.
<!--
### Step 5:  The Stream Pipeline (Real World)
Now we apply these concepts to a batch of 1,000 items.

1.  Open `src/main/java/com/course/lab01/audit/BatchAudit.java`.
2.  **Analyze `processPolluted()`:**
    * Notice how `System.out.println` is mixed inside `.map()`.
    * **Question:** Why is this bad?
    * *Answer:* The `.map()` function is no longer "Side-Effect-Free." We cannot easily test the math without printing to the console.
3.  **Implement `processClean()`:**
    * **Uncomment** the clean pipeline.
    * Observe the separation: `.map()` handles the math, `.peek()` handles the logging.
    * Observe the usage of `SmartLogging.log(...)` inside the peek. This ensures that even in a loop of 1,000 items, we pay **zero** performance cost for logging when it is disabled.
4.  **Verify:**
    * Open `src/test/java/com/course/lab01/audit/BatchTest.java`.
    * Run `testCleanPipeline_IsFastAndCorrect()`.
    * It passes instantly, proving our Lazy architecture scales perfectly to Streams.
-->
## References

### Interfaces
* **`Consumer<T>`**: Takes an input, returns nothing (`void`). Used for **Side Effects** (Logging, Saving, Printing).
* **`Supplier<T>`**: Takes no input, returns a result. Used for **Lazy Evaluation** (Deferring execution until needed).

### Concepts
* **Eager Evaluation:** (Standard Java) Arguments are calculated before the method runs.
* **Lazy Evaluation:** (Functional) Logic is wrapped in a function and only calculated when explicitly invoked.