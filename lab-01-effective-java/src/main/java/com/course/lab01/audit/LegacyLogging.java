package com.course.lab01.audit;

/**
 * The "Legacy" Logging Approach.
 * * PROBLEM: "Eager Evaluation"
 * Java evaluates method arguments *before* passing them to the method.
 * * In the code below, 'generateExpensiveAuditTrail()' runs every single time,
 * even if logging is disabled. This makes the system slow (simulated 500ms latency)
 * for absolutely no benefit.
 */
public class LegacyLogging {

    // A flag to simulate whether we want logs (currently OFF)
    public static boolean LOGGING_ENABLED = false;

    public static Double calculateWithEagerLog(Double price) {
        
        /* * THE TRAP:
         * We are calling log(...). 
         * To pass the argument, Java MUST execute 'generateExpensiveAuditTrail(price)' first.
         * It does this BEFORE it enters the 'log' method to check the boolean flag.
         */
        log("Audit Trail ID: " + generateExpensiveAuditTrail(price));

        // The actual business logic (Tax)
        return price * 1.20;
    }

    private static void log(String message) {
        // We check the flag here... but it's too late! 
        // The expensive string generation has already happened.
        if (LOGGING_ENABLED) {
            System.out.println(message);
        }
    }

    // A helper that simulates a slow operation (e.g., DB lookup or JSON serialization)
    public static String generateExpensiveAuditTrail(Double price) {
        try {
            // Simulate 500ms latency
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "TX-" + System.currentTimeMillis() + "-" + price;
    }
}