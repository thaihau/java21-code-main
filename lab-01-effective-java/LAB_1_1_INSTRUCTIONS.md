#  Lab 1.1: The Type-Safety Bridge

**Topic:** Advanced Generics, Variance (PECS), and API Design.
<br>**Time:** 30 Minutes

-----

##  Scenario

The Platform Team has identified a critical stability issue in our legacy data processing pipeline. The existing `LegacyMover` utility uses raw `List` (Object) types to move data between systems.

Last night, production crashed with a `ClassCastException` because an `Integer` was accidentally added to a list of `Strings`, and the system didn't catch it until runtime.

**Your Mission:**
You have been tasked to build a replacement utility called `SecureMover`.

1.  It must be **Type-Safe** (Compile-time checks, no runtime surprises).
2.  It must be **Flexible** (Allow moving `List<Integer>` into `List<Number>`).

-----

##  Setup & Files

Open the following files in `lab-01-effective-java`:

1.  **The Problem:** `src/main/java/.../mover/LegacyMover.java` (Do not edit)
2.  **The Task:** `src/main/java/.../mover/SecureMover.java` (You will edit this)
3.  **The Test:** `src/test/java/.../mover/SecureMoverTest.java` (Run this to verify)

-----

##  Steps

### Step 1: Witness the Crash (Visual Demo)

We need to confirm why the "Old Way" is dangerous by seeing the crash with our own eyes.

1.  Open `SecureMoverTest.java`.
2.  Run the test method `visualCrashDemo()`.
3.  **Observation:** The test **FAILS** (Red X ❌).
4.  **Check the Output:** Look at the "Debug Console".
      * You will see: `java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String`
5.  **Action:** Comment out the `@Test` annotation on `visualCrashDemo` so it stops failing, then proceed to Step 2.

### Step 2: Try Basic Generics (Snippet A)

Let's try to fix it using standard Generics.

1.  Open `SecureMover.java`.
2.  Copy **Snippet A** and paste it into the class, replacing the existing method.
 
    **Snippet A**: Basic Generics (Too Rigid)

    ```java
    public <T> void moveData(List<T> source, List<T> destination) {
        // This compiles, but it's too strict.
        // You can't move Integers into Numbers.
        destination.addAll(source);
    }
    ```
3.  Open `SecureMoverTest.java` and **Uncomment** the code inside `testSecureMoverPreventsErrors()`.
4.  **Observation:** You will see a **Red Compilation Error** in the test file on this line:
    ```java
    mover.moveData(integers, numbers);
    ```
5.  **Why:** In Java, `List<Integer>` is **NOT** a subtype of `List<Number>`. This is called **Invariance**.

### Step 3: Implement PECS (Snippet B)

We need to tell the compiler that it's safe to move "Integers" into a "Number" list. We use the **PECS** principle.

1.  Replace your code in `SecureMover.java` with **Snippet B**.
    
    Snippet B: Variance / PECS (Correct)
    ```java
    public <T> void moveData(List<? extends T> source, List<? super T> destination) {
        // ? extends T : source can be T or any subclass (e.g., Integer)
        // ? super T   : destination can be T or any superclass (e.g., Number)
        destination.addAll(source);
    }
    ```
2.  Check `SecureMoverTest.java`.
3.  **Observation:** The Red compilation error disappears.
4.  Run `testSecureMoverPreventsErrors()`.
5.  **Result:** ✅ **Green Checkmark.**

-----

##  Concept De-Brief: The PECS Principle

**PECS** stands for **"Producer Extends, Consumer Super"**. It is a mnemonic coined by Joshua Bloch (Effective Java) to help developers choose the correct wildcard (`?`) when designing flexible APIs.

### 1. The Producer (`? extends T`)
* **Role:** The **Source**. It provides data *to* your method.
* **Why:** You want to read `T` items out of it. It doesn't matter if the list is actually `List<Integer>` or `List<Double>`, as long as the items are a subtype of `Number`.
* **Limitation:** You are **Read-Only**. You cannot add items to this list (because you don't know the specific subtype at runtime).

### 2. The Consumer (`? super T`)
* **Role:** The **Destination**. It accepts data *from* your method.
* **Why:** You want to put `T` items into it. It doesn't matter if the list is `List<Number>` or `List<Object>`, as long as it is a supertype capable of holding your data.
* **Limitation:** You are **Write-Only** (mostly). If you read from this list, you only get `Object` types back, losing specific type information.

### 🎯 The "Cheat Sheet" for API Design

| If your parameter... | Use Wildcard | Example |
| :--- | :--- | :--- |
| **Gives** you data (Source) | `? extends T` | `List<? extends Number>` |
| **Takes** your data (Dest) | `? super T` | `List<? super Integer>` |
| Does **Both** (Read & Write) | **No Wildcard** | `List<T>` |

> **In this Lab:**
> * `source` was the **Producer** (we read from it), so we used `? extends`.
> * `destination` was the **Consumer** (we wrote to it), so we used `? super`.