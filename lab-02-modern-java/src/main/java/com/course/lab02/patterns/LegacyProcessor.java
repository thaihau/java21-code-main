package com.course.lab02.patterns;

import com.course.lab02.patterns.Events.*;

public class LegacyProcessor {

    public String process(Object event) {
        
        // 1. SECURITY CHECK (The "Clunky" Way)
        // We want to return "ALARM" if it is a failed login.
        if (event instanceof LoginEvent) {
            LoginEvent login = (LoginEvent) event; // Manual Cast
            if (!login.success()) {                // Nested Logic
                return "ALARM";
            }
        }

        // 2. MAIN LOGIC (The "Verbose" Way)
        if (event instanceof LoginEvent) {
            LoginEvent login = (LoginEvent) event;
            return "User " + login.username() + " logged in: " + login.success();
        } 
        else if (event instanceof PaymentEvent) {
            PaymentEvent pay = (PaymentEvent) event;
            return "Paid " + pay.amount() + " for ID: " + pay.id();
        } 
        else if (event instanceof ErrorEvent) {
            ErrorEvent err = (ErrorEvent) event;
            return "Error " + err.code() + ": " + err.message();
        } 
        else {
            return "Unknown Event";
        }
    }
}