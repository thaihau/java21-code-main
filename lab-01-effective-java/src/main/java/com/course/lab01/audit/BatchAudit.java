package com.course.lab01.audit;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchAudit {

    /*
     * TODO: Activity 2 - The Stream Implementation
     * * Scenario: We have 500 invoices. We need to apply Tax and Log the result.
     * * CONCEPT 1: "Pollution" (The Bad Way)
     * Mixing side effects (logging) inside the transformation (.map) makes
     * the function impure. It is harder to test and parallelize.
     * * CONCEPT 2: "Purity" (The Good Way)
     * We keep .map() pure (Math only).
     * We use .peek() for the side effects (Logging).
     * We use our SmartLogging (Supplier) to keep it fast.
     */

    public static List<Double> processPolluted(List<Double> prices) {
        return prices.stream()
            .map(price -> {
                Double taxed = price * 1.20;
                // BAD: Side Effect (IO) inside a Transformation
                System.out.println("Polluted Log: " + taxed); 
                return taxed;
            })
            .collect(Collectors.toList());
    }

    // public static List<Double> processClean(List<Double> prices) {
    //     // Uncomment the pipeline below
        
    //     return prices.stream()
    //          // 1. Pure Transformation (Math Only)
    //          .map(price -> price * 1.20)
        
    //          // 2. Isolated Side Effect (The "Window")
    //          // We use our Lazy Logger. It only builds the string if enabled.
    //          .peek(price -> SmartLogging.log(() -> "Clean Log: " + price))
        
    //          .collect(Collectors.toList());
        
    //     // return List.of(); // Placeholder until uncommented
    // }
}