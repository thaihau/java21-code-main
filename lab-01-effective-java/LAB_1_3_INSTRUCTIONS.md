# Lab 1.3: The Functional Pricing Pipeline

* **Topic:** Functional Interfaces (Focus on `Function<T,R>`), Functional Composition (`.andThen`), and Refactoring Strategies.
* **Estimated Duration:** 20 Minutes
    
## Scenario
You are refactoring a "Legacy Monolith" pricing engine. Currently, the `LegacyPricing` class enforces a rigid order of operations: **Tax (20%)** is always applied first, followed by a **Flat Discount ($10.00)**.

**The New Requirement:**
A new "VIP" program requires the **Discount** to be applied *before* the **Tax**.
In the legacy code, this would require rewriting the method or adding complex `if/else` flags (increasing Cyclomatic Complexity).

**The Solution:**
You will refactor the logic into granular **Function Variables (Components)**. You will then use **Functional Composition** to dynamically reorder the "Pipeline" (Tax -> Discount vs. Discount -> Tax) without changing the core logic.

## Setup and Files
* **Module:** `lab-01-effective-java`
* **Package:** `com.course.lab01.pricing`
* **Files:**
    * `LegacyPricing.java` (The Problem - Read Only)
    * `FunctionalPricing.java` (The Solution - Components commented out)
    * `PricingTest.java` (The Validator - Tests commented out)

## Steps

### Step 1: Verify Legacy Rigidity
1.  Open `src/test/java/com/course/lab01/pricing/PricingTest.java`.
2.  Run the test `testLegacy_IsRigid()`.
3.  **Observe:** The test passes, confirming the hardcoded math: $100.00 + 20\% - \$10.00 = \$110.00$.
4.  **Analyze:** Note that `LegacyPricing.calculate()` has the order of operations hardcoded. There is no way to swap them without modifying the source code.

### Step 2: Define the Functional Components
1.  Open `src/main/java/com/course/lab01/pricing/FunctionalPricing.java`.
2.  Locate **Part 1** (The Components).
3.  **Uncomment** the definitions for `taxRule` and `discountRule`.
4.  **Concept Check:**
    * These are **Function Variables** (First-Class Functions).
    * They are `static final` because they are stateless and immutable.
    * We call them **Components** because they are isolated units of logic that don't know about each other.

### Step 3: Enable the Composer
1.  Still in `FunctionalPricing.java`, locate **Part 2** (The Composer).
2.  **Uncomment** the `calculate` method.
3.  **Concept Check:**
    * This method acts as a "Dumb Runner". It accepts a starting price and a `pipeline`.
    * It executes `pipeline.apply(initialPrice)`. It does not know the order of operations; it just runs whatever chain you hand it.

### Step 4: Build the Pipelines
1.  Return to `PricingTest.java`.
2.  **Uncomment** `testFunctional_StandardOrder()`.
    * Observe how `taxRule.andThen(discountRule)` creates a pipeline that matches the legacy behavior.
    * Run the test to confirm it matches ($110.00).
3.  **Uncomment** `testFunctional_VipOrder()`.
    * Observe the composition: `discountRule.andThen(taxRule)`.
    * We have reordered the logic to support the VIP requirement ($100 - \$10 + 20\% = \$108.00).
    * Run the test to confirm the math.

## References

### Interface: `java.util.function.Function<T, R>`
* **`R apply(T t)`**: The abstract method (the "Go" button) that executes the logic.
* **`andThen(Function after)`**: A default method that chains two functions together. It runs the current function first, and uses its output as the input for the `after` function.

### Key Terminology
* **Function Variable:** A variable (object) that holds executable logic.
* **Composition:** The act of chaining small, isolated functions together to build complex workflows.
* **Pipeline:** The resulting chain of functions.