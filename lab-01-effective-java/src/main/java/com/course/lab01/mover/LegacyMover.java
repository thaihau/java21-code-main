package com.course.lab01.mover;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class LegacyMover {
    
    /**
     * LEGACY CODE: This uses raw Lists (no Generics).
     * It allows dangerous operations that compile fine but crash at runtime.
     */
    public void moveData(List source, List destination) {
        // This looks innocent, but what if 'source' has Integers 
        // and 'destination' expects Strings?
        for (Object item : source) {
            destination.add(item); 
        }
    }
}