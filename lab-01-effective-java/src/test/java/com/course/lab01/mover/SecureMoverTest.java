package com.course.lab01.mover;

import com.course.lab01.mover.SecureMover;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class SecureMoverTest {

    /**
     * INSTRUCTOR DEMO: Run this to see the app crash!
     * This test is DESIGNED TO FAIL (Red X).
     */
    @Test
    void visualCrashDemo() {
        System.out.println("--- 1. Setting up Legacy Data ---");
        LegacyMover legacy = new LegacyMover();
        
        // A list that claims to hold Strings
        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        
        // The "Hack" to insert a Poison Pill
        List rawList = names; 
        rawList.add(9999); // <--- The Integer that shouldn't be here
        
        System.out.println("--- 2. Moving Data (LegacyMover accepts anything) ---");
        List<String> destination = new ArrayList<>();
        legacy.moveData(names, destination);

        System.out.println("--- 3. Reading Data ---");
        System.out.println("Item 0: " + destination.get(0)); // Works fine
        System.out.println("Item 1: " + destination.get(1)); // Works fine
        
        System.out.println(">>> ATTEMPTING TO READ THE POISON PILL (Item 2) <<<");
        
        // 💥 BOOM: The next line causes the Red X and Stack Trace
        String crash = destination.get(2); 
        
        // We will never reach this line
        System.out.println("Success? " + crash);
    }

    @Test
    void testSecureMoverPreventsErrors() {
        SecureMover mover = new SecureMover();
        
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);

        List<Number> numbers = new ArrayList<>();

        // --- INSTRUCTIONS ---
        // UNCOMMENT the line below.
        // If you used Snippet A (Basic Generics), this will show a RED compilation error.
        // If you used Snippet B (PECS), this will compile and pass.
        
        // mover.moveData(integers, numbers); 

        // assertThat(numbers).contains(1, 2);
    }
}