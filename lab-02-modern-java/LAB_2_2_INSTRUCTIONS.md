# Lab 2.2: Expressive Logic

## Topic and Timing
* **Topic:** Pattern Matching (`instanceof` & `switch`) and Pattern Variables.
* **Estimated Duration:** 20 Minutes
   

## Scenario
We are building an **Event Processor** that handles generic `Object` messages.
We have a new strict security requirement: **"Any failed login attempt must trigger an immediate ALARM."**

**The Problem (Legacy Code):**
* **Nesting:** To check a specific property of a subclass, you have to Check (`instanceof`), then Cast (`(Type)`), then Check again (`if`).
* **Clutter:** The main logic is a long chain of `if-else` statements.

**The Solution (Modern Java):**
1.  **Pattern Variables:** We can check type and cast in one step: `event instanceof LoginEvent login`.
2.  **Scope Extension:** We can use that variable immediately in the same boolean expression.
3.  **Switch Expressions:** We can replace the entire `if-else` chain with a clean, readable structure.

## Setup and Files
* **Module:** `lab-02-modern-java`
* **Package:** `com.course.lab02.patterns`
* **Files:**
    * `Events.java` (The Data Records - Read Only)
    * `LegacyProcessor.java` (The Problem - Read Only)
    * `ModernProcessor.java` (The Solution - You will build this)
    * `ProcessorTest.java` (The Validator)

## Steps

### Step 1: Analyze the Legacy Pain
1.  Open `LegacyProcessor.java`.
2.  Look at the **Security Check** (Lines 10-15).
    * **Observe:** It takes 4-5 lines of code just to check `!login.success()`.
    * **Critique:** The manual cast `(LoginEvent) event` is noise.

### Step 2: Implement the Security Check (Part 1)
1.  Open `ModernProcessor.java`.
2.  Locate the `TODO: Part 1` section.
3.  **Action:** Implement the "Alarm" rule using a single line of logic.
    ```java
    // if (event is LoginEvent AND success is false) -> return "ALARM"
    if (event instanceof LoginEvent login && !login.success()) {
        return "ALARM";
    }
    ```
4.  **Concept Check:**
    * Notice we defined the variable `login` inside the `instanceof` check.
    * Notice we used `login` *immediately* after the `&&`. This is called **Flow Scoping**. Java knows that if the first part is true, `login` is safe to use in the second part.

### Step 3: Implement the Main Logic (Part 2)
1.  Still in `ModernProcessor.java`.
2.  Locate the `TODO: Part 2` section.
3.  **Action:** Replace the `if-else` chain with a Switch Expression:
    ```java
    return switch (event) {
        case LoginEvent login -> "User " + login.username() + " logged in: " + login.success();
        case PaymentEvent pay -> "Paid " + pay.amount() + " for ID: " + pay.id();
        case ErrorEvent err   -> "Error " + err.code() + ": " + err.message();
        default -> "Unknown Event";
    };
    ```

### Step 4: Validate
1.  Open `ProcessorTest.java`.
2.  Run the tests.
    * `testSecurityCheck()`: Verifies your Part 1 logic (Returns "ALARM").
    * `testMainLogic()`: Verifies your Part 2 logic (Matches Legacy output).
3.  **Success:** Both tests should pass (Green).

## References

### Syntax

**1. Instanceof Pattern:**
```java
// OLD
if (obj instanceof String) { String s = (String) obj; ... }

// NEW
if (obj instanceof String s) { ... }
```

**2. Guarded Pattern (Using logical AND):**

```Java
// Valid because 's' is only accessed if the left side is true
if (obj instanceof String s && s.length() > 5) { ... }
```
**3. Switch Pattern:**

```Java

switch (obj) {
    case String s -> ...
    case Integer i -> ...
}
```