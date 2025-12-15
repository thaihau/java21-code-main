# Lab 2.3: Restricted Hierarchies (Sealed Classes)

## Topic and Timing

  * **Topic:** Sealed Interfaces, `permits` clauses, and Switch Exhaustiveness.
  * **Estimated Duration:** 20 Minutes
      
## Scenario

We are building a **High-Security Authentication System**.
We need to guarantee that **only** `Password` and `FaceID` are valid authentication methods.

**The Problem (Open Interface):**
In standard Java, interfaces are "Open." A developer (or a malicious library) can create a class `HackerLogin implements Authentication` and pass it to your logic. The compiler allows this.

**The Solution (Sealed Interface):**
We use `sealed interface ... permits A, B`.
This creates a **Compile-Time Contract**. The compiler physically prevents any other class from implementing the interface. It also allows us to write safer `switch` statements without a `default` case.

## Setup and Files

  * **Module:** `lab-02-modern-java`
  * **Package:** `com.course.lab02.sealed`
  * **Files:**
      * `LegacyAuth.java` (The Vulnerability - Read Only)
      * `SealedAuth.java` (The Fix - You will edit this)
      * `AuthService.java` (The Logic - You will implement this)
      * `AuthTest.java` (The Validator)

## Steps

### Step 1: Analyze the Vulnerability

1.  Open `src/main/java/com/course/lab02/sealed/LegacyAuth.java`.
2.  Observe the `HackerLogin` class inside the file.
3.  **Concept:** Because `LegacyAuth` is a standard interface, nothing stops this rogue class from existing.

### Step 2: Lock Down the Hierarchy

1.  Open `src/main/java/com/course/lab02/sealed/SealedAuth.java`.
2.  Locate the `TODO: Refactor` section.
3.  **Action:** Change the interface definition to be `sealed` and strictly permit only the two allowed records:
    ```java
    public sealed interface SealedAuth permits SealedAuth.Password, SealedAuth.FaceID {
        // ... records are already here ...
    }
    ```

### Step 3: Verify the Lock (The Experiment)

1.  Still in `SealedAuth.java`.
2.  Locate the `TODO: Paste Experiment Here` section.
3.  **Action:** Paste the following rogue class:
    ```java
    // final class HackerLogin implements SealedAuth {}
    ```
4.  **Action:** Uncomment the line.
5.  **Observe:** The compiler immediately shows a red error: *"Class 'HackerLogin' is not allowed in the sealed hierarchy"*.
6.  **Action:** Re-comment or delete the line to fix the error.

### Step 4: Implement Exhaustive Logic

1.  Open `src/main/java/com/course/lab02/sealed/AuthService.java`.

2.  **Analyze `checkLegacy` (The Warning):**

      * Look at `case LegacyAuth.HackerLogin h`. This line **compiles successfully** because `LegacyAuth` is an **Open Interface**. The compiler sees `HackerLogin` as a perfectly valid sibling to `Password`.
      * **The Problem:** This proves your security logic is "leaky." A rogue class defined elsewhere can enter your system and be processed.
      * Look at the `default` case. It is **mandatory** here. The compiler says: *"I can't trust you. There might be a 4th or 5th implementation I don't know about."*

3.  **Action:** Implement the `checkSealed` method using the Sealed Interface.

      * Note that you do **not** need a `default` case. The compiler knows `HackerLogin` is impossible.

    <!-- end list -->

    ```java
    return switch (auth) {
        case SealedAuth.Password p -> "Verified Password: " + p.hash();
        case SealedAuth.FaceID f   -> "Verified FaceID: " + f.data().length + " bytes";
    };
    ```

### Step 5: Validate (Runtime Exhaustiveness)

1.  Open `src/test/java/com/course/lab02/sealed/AuthTest.java`.
2.  **Concept:** We are testing that our `switch` expression works correctly at runtime **without a `default` case**. This proves the compiler's guarantee holds true—no "unknown" types can crash our logic.
3.  **Action:** Add the test logic:
    ```java
    // 1. Setup a valid Sealed type (Password)
    SealedAuth.Password p = new SealedAuth.Password("secret123");
    AuthService service = new AuthService();

    // 2. Execute the switch
    String result = service.checkSealed(p);

    // 3. Assert Success
    // This proves the runtime found the correct "case" automatically.
    assertEquals("Verified Password: secret123", result);
    ```
4.  Run the test.
      * **Success:** Green checkmark.
      * **Note:** If you had forgotten a case (e.g., `FaceID`), the code wouldn't even compile. If you added a new permitted class later, this test would still pass, but the `AuthService` would refuse to compile until you handled the new case.

## References

### Concepts

  * **Sealed Interface:** Limits inheritance to a specific set of classes.
  * **Permits:** The keyword listing the allowed subclasses.
  * **Exhaustiveness:** The compiler's ability to calculate if a `switch` covers every possible subtype.

### Rules

  * **Subclass Requirement:** Permitted subclasses must be `final`, `sealed`, or `non-sealed`. Records are implicitly `final`, making them ideal for this.