package com.course.lab01.pricing;

/**
 * The "Legacy" implementation.
 * PROBLEM: The order of operations is hardcoded inside the method.
 * To support VIPs (Discount BEFORE Tax), we would have to copy-paste this method
 * or add messy "if/else" flags.
 */
public class LegacyPricing {

    public static Double calculate(Double price) {
        // Hardcoded Rule 1: Apply 20% Tax
        Double afterTax = price * 1.20;

        // Hardcoded Rule 2: Apply VIP $10 Discount
        Double finalPrice = afterTax - 10.00;

        return finalPrice;
    }
}