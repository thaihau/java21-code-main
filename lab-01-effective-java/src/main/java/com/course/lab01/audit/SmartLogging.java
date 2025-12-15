package com.course.lab01.audit;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SmartLogging {

    public static boolean LOGGING_ENABLED = false;

    /*
     * TODO: PART 1 - The "Pass-Through" Auditor (Consumer)
     * Uncomment the method below.
     * * Pattern: The "Peek" Pattern.
     * 1. We take a generic value (T).
     * 2. We run a 'Consumer' (the side effect, like printing) on it.
     * 3. We return the ORIGINAL value exactly as is.
     * * Why? This allows us to inject logging into a chain without breaking it.
     */
    
    // public static <T> T peek(T value, Consumer<T> auditor) {
    //     auditor.accept(value); // Execute the side effect
    //     return value;          // Return original to keep the pipeline moving
    // }


    /*
     * TODO: PART 2 - The "Lazy" Logger (Supplier)
     * Uncomment the method below.
     * * Pattern: "Lazy Evaluation"
     * * OLD WAY: log(String msg) -> Java calculates 'msg' BEFORE calling.
     * * NEW WAY: log(Supplier<String> msgGen) -> Java passes the 'function' object.
     * * Benefit: The code inside the Supplier (.get()) ONLY runs if we actually need it.
     */

    // public static void log(Supplier<String> messageGenerator) {
    //     if (LOGGING_ENABLED) {
    //         // The Expensive Code runs HERE, and ONLY here.
    //         System.out.println("LOG: " + messageGenerator.get());
    //     }
    // }

    /*
     * TODO: PART 3 - The Optimized Usage
     * Uncomment the calculation method below.
     */

    // public static Double calculateWithLazyLog(Double price) {
    //     // 1. Pass a LAMBDA (The Recipe), not a String (The Meal).
    //     // Java sees "() -> ..." and wraps it in an object. It does NOT run the code yet.
    //     log(() -> LegacyLogging.generateExpensiveAuditTrail(price));

    //     return price * 1.20;
    // }
}