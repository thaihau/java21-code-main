package com.course.lab01.inspector;

import java.util.List;

public class LegacyInspector {

    /**
     * LEGACY CODE: Accepts List<Object>.
     * This is dangerous because it allows the method to modify the list
     * and insert types that don't belong there.
     */
    public void inspectAndLog(List<Object> data) {
        System.out.println("LOG: Inspecting " + data.size() + " elements.");
        
        // 🚨 CRITICAL BUG:
        // Because the list is treated as List<Object>, the compiler allows this.
        // We are accidentally corrupting the caller's data by adding a String!
        data.add("INSPECTED_BY_LEGACY_SYSTEM");
    }
}