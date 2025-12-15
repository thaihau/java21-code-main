# Lab 2.1: Modern Data Modeling

## Topic and Timing
* **Topic:** Records (Concise Data Carriers) and Text Blocks (Multi-line Strings).
* **Estimated Duration:** 20 Minutes
   

## Scenario
We are building a **Financial Transaction** system.
We need a simple data object to hold: `ID`, `Amount`, and `Type`. We also need to generate a JSON report for auditing.

**The Problem (Legacy Class):**
To make a proper immutable data object in older Java, you need:
1.  Private final fields.
2.  A constructor.
3.  Getters for every field.
4.  `equals()`, `hashCode()`, and `toString()` implementations.
5.  Messy string concatenation for JSON (escaping quotes `\"` and newlines `\n`).

**The Solution (Record + Text Blocks):**
* **Records:** A one-line declaration that auto-generates all the boilerplate.
* **Text Blocks:** A clean way to write multi-line JSON strings using `"""`.

## Setup and Files
* **Module:** `lab-02-modern-java`
* **Package:** `com.course.lab02.modern`
* **Files:**
    * `LegacyTransaction.java` (The Problem - Read Only)
    * `ModernTransaction.java` (The Solution - You will build this)
    * `TransactionTest.java` (The Validator)

## Steps

### Step 1: Feel the Legacy Pain
1.  Open `src/main/java/com/course/lab02/modern/LegacyTransaction.java`.
2.  Scroll through the file.
    * **Observe:** It takes **~50 lines of code** just to hold 3 variables.
    * **Analyze:** Look at `equals()` and `hashCode()`. If you add a new field later, you must remember to update these methods manually. (This is a common source of bugs).
    * **Analyze:** Look at `toJSON()`. Notice how ugly the `\n` and `\"` escape characters are.

### Step 2: Create the Record
1.  Open `src/main/java/com/course/lab02/modern/ModernTransaction.java`.
2.  **Action:** Delete the entire `public class ModernTransaction { ... }` definition.
3.  **Action:** Type the following "Cheat Code" to replace it:
    ```java
    public record ModernTransaction(String id, double amount, String type) {
    }
    ```
4.  **Concept Check:** That single line just generated the Constructor, Getters, `equals`, `hashCode`, and `toString` for you.
    * Note: The getters are named `id()`, `amount()`, and `type()` (No "get" prefix).

### Step 3: Implement Text Blocks
1.  Inside the curly braces `{}` of your new Record, add the `toJSON` method using a Text Block:
    ```java
    public String toJSON() {
        return """
               {
                 "id": "%s",
                 "amount": %.2f,
                 "type": "%s"
               }
               """.formatted(id, amount, type);
    }
    ```
2.  **Observe:**
    * We use triple quotes `"""` to start and end the block.
    * We don't need to escape the inner quotes (`"id"`).
    * We use the new `.formatted()` method (Java 15+) to inject the data directly.

### Step 4: Validate and Compare
1.  Open `src/test/java/com/course/lab02/modern/TransactionTest.java`.
2.  Locate the `TODO` block.
3.  **Action:** Type the code to verify your new Record:
    ```java
    // 1. Create Instance
    ModernTransaction modern = new ModernTransaction("TX-100", 50.00, "DEBIT");
    
    // 2. Assert Accessor (Notice: .id() not .getId())
    assertEquals("TX-100", modern.id());
    
    // 3. Assert Equality (Records compare Data, not Memory Address)
    ModernTransaction copy = new ModernTransaction("TX-100", 50.00, "DEBIT");
    assertEquals(modern, copy);
    
    // 4. Check JSON
    System.out.println(modern.toJSON());
    ```
4.  Run the test.
    * **Success:** It should pass. You have replaced 50 lines of code with 5, and the output is cleaner.

## References

### Concepts
* **Record:** A restricted class type that models immutable data. It is implicitly `final` and cannot extend other classes.
* **Component Accessors:** Records do not use `getVariable()`. They use `variable()`.
* **Text Block (`"""`):** Preserves the formatting of the source string. The indentation is determined by the position of the closing `"""`.