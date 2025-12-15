package com.course.lab02.modern;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    void testLegacyVsModern() {
        // 1. Legacy Setup (Reference)
        LegacyTransaction legacy = new LegacyTransaction("TX-100", 50.00, "DEBIT");
        assertEquals("TX-100", legacy.getId()); 

        /*
         * TODO: Test your Record below.
         * Instantiate ModernTransaction, check .id(), check .equals(), and print .toJSON().
         */
         
         // <--- PASTE/TYPE CODE HERE
    }
}