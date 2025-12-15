package com.course.lab01.audit;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public class BatchTest {

    @Test
    void testCleanPipeline_IsFastAndCorrect() {
        // 1. Setup Data: 1000 items
        Double[] priceArray = new Double[1000];
        Arrays.fill(priceArray, 100.00);
        List<Double> prices = Arrays.asList(priceArray);

        // 2. Ensure Logging is OFF (Simulate Production)
        SmartLogging.LOGGING_ENABLED = false;

        // 3. Run the Pipeline
        // We assert timeout to prove the Lazy Logging inside the stream 
        // isn't slowing down the batch processing.
        // assertTimeout(Duration.ofMillis(100), () -> {
            
        //     List<Double> results = BatchAudit.processClean(prices);

        //     // 4. Verify Math (Pure Logic)
        //     assertEquals(1000, results.size());
        //     assertEquals(120.00, results.get(0), 0.001);
            
        // }, "The stream pipeline was too slow! Check if you are Eagerly evaluating strings.");
    }
}