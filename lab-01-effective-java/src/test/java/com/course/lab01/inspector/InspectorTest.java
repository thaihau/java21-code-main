package com.course.lab01.inspector;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InspectorTest {

    @Test
    void testLegacyInspectorCorruptsData() {
        LegacyInspector legacy = new LegacyInspector();
        
        System.out.println("--- 1. Setup Integer List ---");
        List<Integer> numbers = new ArrayList<>();
        numbers.add(100);
        numbers.add(200);

        System.out.println("--- 2. Call Legacy Inspector ---");
        // This looks safe, but LegacyInspector adds a String to our Integer list!
        List rawRef = numbers;
        legacy.inspectAndLog(rawRef);

        System.out.println("--- 3. Read Data Back ---");
        System.out.println("Item 0: " + numbers.get(0)); // 100
        System.out.println("Item 1: " + numbers.get(1)); // 200
        
        System.out.println(">>> ATTEMPTING TO READ THE CORRUPTED ITEM (Item 2) <<<");
        
        // 💥 BOOM: The Stack Trace will point exactly here.
        // It tries to cast "INSPECTED..." (String) to Integer.
        Integer crash = numbers.get(2);
        
        System.out.println("Success? " + crash);
    }

    @Test
    void testSmartInspectorEnforcesReadOnly() {
        System.out.println("--- 1. Setup Integer List ---");
        SmartInspector smart = new SmartInspector();
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);

        System.out.println("--- 2. Call Smart Inspector ---");
        // UNCOMMENT the line below once you have implemented SmartInspector
        // smart.inspect(numbers); 

        System.out.println("--- 3. Verify Data Integrity ---");
        // If SmartInspector used List<?>, the list size should still be 2.
        // If it used List<Object> and added something, this would be 3 (and incorrect).
        if (numbers.size() == 2) {
            System.out.println("✅ SUCCESS: Data was NOT modified.");
        } else {
            System.out.println("❌ FAILURE: Data was modified! Inspector added something.");
        }
    }
}