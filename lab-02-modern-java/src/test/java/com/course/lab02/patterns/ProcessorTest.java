package com.course.lab02.patterns;

import com.course.lab02.patterns.Events.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessorTest {

    @Test
    void testSecurityCheck() {
        // 1. Setup: Failed Login
        LoginEvent hackAttempt = new LoginEvent("hacker", false);
        
        ModernProcessor modern = new ModernProcessor();

        // 2. Assert: Should return ALARM (Part 1 of Lab)
        assertEquals("ALARM", modern.process(hackAttempt), 
            "Failed logins should trigger an ALARM immediately.");
    }

    @Test
    void testMainLogic() {
        // 1. Setup: Valid Events
        LoginEvent success = new LoginEvent("Alice", true);
        PaymentEvent pay = new PaymentEvent("TX-999", 50.00);

        ModernProcessor modern = new ModernProcessor();
        LegacyProcessor legacy = new LegacyProcessor();

        // 2. Assert: Logic matches Legacy behavior (Part 2 of Lab)
        assertEquals(legacy.process(success), modern.process(success));
        assertEquals(legacy.process(pay), modern.process(pay));
    }
}