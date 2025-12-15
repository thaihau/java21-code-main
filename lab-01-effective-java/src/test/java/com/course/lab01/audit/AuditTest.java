package com.course.lab01.audit;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public class AuditTest {

    /*
     * TODO: Step 1 - Uncomment the Legacy Test (The Failure)
     * * Scenario:
     * Logic is clean, but the logger is "Eager".
     * Even with logging DISABLED, the expensive audit trail generation runs.
     * * Expectation:
     * This test will FAIL with a TimeoutException because 500ms > 100ms.
     */

    // @Test
    // void testLegacy_IsSlow() {
    //     // Ensure logging is OFF
    //     LegacyLogging.LOGGING_ENABLED = false;

    //     // We assert that this MUST finish within 100ms.
    //     // LegacyLogging sleeps for 500ms, so this will crash hard.
    //     assertTimeout(Duration.ofMillis(100), () -> {
    //         LegacyLogging.calculateWithEagerLog(100.00);
    //     }, "Legacy method took too long! It's calculating strings unnecessarily.");
    // }


    /*
     * TODO: Step 4 - Uncomment the Smart Test (The Success)
     * * Scenario:
     * We use a Supplier (Lambda) to wrap the expensive code.
     * The 'log' method sees logging is DISABLED, so it never calls .get().
     * * Expectation:
     * This test will PASS instantly (~0-1ms).
     */

    // @Test
    // void testSmart_IsFast() {
    //     // Ensure logging is OFF
    //     SmartLogging.LOGGING_ENABLED = false;

    //     // Assert strict 100ms timeout
    //     assertTimeout(Duration.ofMillis(100), () -> {
    //         Double result = SmartLogging.calculateWithLazyLog(100.00);
            
    //         // Verify logic still works
    //         assertEquals(120.00, result, 0.001);
    //     });
    // }
}