#  Lab 1.2: The Tamper-Proof Inspector

**Topic:** Unbounded Wildcards (`?`), Type Safety, and API Security.
<br>
**Time:** 20 Minutes

-----

##  Scenario

You are maintaining a **Security Audit System**. This system has a utility called `DataInspector` that accepts lists of data from various modules (Payroll, Inventory, etc.) to log their size and hash codes.

**The Problem:** 
<br> 
The current implementation uses `List<Object>`. A recent update by a junior developer introduced a bug: the inspector doesn't just *look* at the data—it modifies it by "stamping" the list with an audit string.

This is causing production crashes. When the Payroll module passes a `List<Integer>`, the Inspector adds a `String` to it. When Payroll tries to read its numbers back, the application explodes.

**Your Mission:**
<br>
Refactor the Inspector to use **Unbounded Wildcards (`?`)**.

1.  Create a "Read-Only" contract.
2.  Force the compiler to block *any* code that tries to modify the list.

-----

##  Setup & Files


Open the following files in `lab-01-effective-java`:

1.  **The Problem:** `src/main/java/.../inspector/LegacyInspector.java` (Do not edit)
2.  **The Task:** `src/main/java/.../SmartInspector.java` (You will edit this)
3.  **The Test:** `src/test/java/.../InspectorTest.java` (Run this to verify)

-----

##  Steps

### Step 1: Witness the Crash (Visual Demo)
We need to see how the Legacy system corrupts data.
1.  Open `InspectorTest.java`.
2.  Run the test method `testLegacyInspectorCorruptsData()`.
3.  **Observation:** The test **FAILS** (Red X ❌) with a `ClassCastException`.
4.  **Lesson:** `List<Object>` allowed the Legacy Inspector to insert a String into our Integer list.

### Step 2: The "Naive" Fix (List Object)
Let's try to fix it using `List<Object>` in our new class.
1.  Open `SmartInspector.java`.
2.  Paste **Snippet A** (below).
3.  Uncomment the code in `InspectorTest.java` inside `testSmartInspectorEnforcesReadOnly()`.
4.  **Observation:** You see a **Red Compilation Error** in `InspectorTest.java` on the line `smart.inspect(numbers)`.
5.  **The Failure:** `List<Object>` is too strict. It refuses to accept `List<Integer>`. We cannot even use this solution!

### Step 3: The "Unbounded" Fix (List ?)
Now, let's use the Wildcard to fix the compilation error and lock down security.
1.  In `SmartInspector.java`, change `List<Object>` to `List<?>` (**Snippet B**).
2.  **Observation 1:** Look at `InspectorTest.java`. The red error is **GONE**. The Wildcard successfully accepts `List<Integer>`.
3.  **Observation 2:** Look at `SmartInspector.java`. A **NEW Red Error** has appeared on the line `data.add("AUDITED")`.
4.  **The Success:** This is exactly what we want! The compiler now forbids us from modifying the list.
5.  **Action:** Delete the `data.add(...)` line to fix the error.
6.  Run `testSmartInspectorEnforcesReadOnly()`.
7.  **Result:** ✅ **Green Checkmark.**


-----

##  Code Snippets

###  Snippet A: The Open Door (`List<Object>`)

*Use this for Step 2. It compiles, but it's dangerous because it allows writes.*

```java
public void inspect(List<Object> data) {
    System.out.println("Inspecting " + data.size() + " elements.");
    
    // DANGER: We can accidentally corrupt the caller's data
    data.add("AUDITED"); 
}
```

###  Snippet B: The Read-Only Lock (`List<?>`)

*Use this for Step 3. The compiler will now prevent any writes.*

```java
public void inspect(List<?> data) {
    System.out.println("Inspecting " + data.size() + " elements.");
    
    // SAFE: The compiler throws an error if we try to add()
    // data.add("AUDITED"); // <--- This line will turn Red
}
```

-----

###  Key Takeaway for Leads

**Unbounded Wildcards (`?`) are not just "I don't know the type."**
They are a security feature.

  * Use `List<?>` when your method only needs to read properties (like `.size()`, `.toString()`) and you want to **guarantee** to the caller that you will not modify their list.
  * It turns the parameter into a **Read-Only View**.