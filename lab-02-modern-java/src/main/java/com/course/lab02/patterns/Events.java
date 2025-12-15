package com.course.lab02.patterns;

public interface Events {
    record LoginEvent(String username, boolean success) {}
    record PaymentEvent(String id, double amount) {}
    record ErrorEvent(int code, String message) {}
}