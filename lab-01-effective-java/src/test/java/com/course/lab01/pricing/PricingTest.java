package com.course.lab01.pricing;

import org.junit.jupiter.api.Test;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PricingTest {

    @Test
    void testLegacy_IsRigid() {
        // Input: $100.00
        // Legacy: Tax (100 * 1.2 = 120) -> Discount (120 - 10 = 110)
        Double result = LegacyPricing.calculate(100.00);
        
        assertEquals(110.00, result, 0.001, "Legacy should be strictly Tax then Discount");
    }

    /*
     * TODO: Step 3 - Uncomment the Functional Tests
     * These tests verify we can dynamically compose the pipeline.
     */

    // @Test
    // void testFunctional_StandardOrder() {
    //     // 1. Compose the Pipeline (The "Blueprints")
    //     // We use .andThen() to connect the components.
    //     // Logic: Run Tax Component -> Then run Discount Component
    //     Function<Double, Double> standardPipeline = 
    //         FunctionalPricing.taxRule.andThen(FunctionalPricing.discountRule);

    //     // 2. Execute (The "Runner")
    //     // We pass the Blueprint into the generic calculator.
    //     Double result = FunctionalPricing.calculate(100.00, standardPipeline);

    //     // 3. Verify
    //     // 100 -> 120 -> 110
    //     assertEquals(110.00, result, 0.001);
    // }

    // @Test
    // void testFunctional_VipOrder() {
    //     // 1. Compose the VIP Pipeline (Reordered Blueprints)
    //     // Notice we didn't change the code in FunctionalPricing.java!
    //     // We just changed the order of connection here.
    //     Function<Double, Double> vipPipeline = 
    //         FunctionalPricing.discountRule.andThen(FunctionalPricing.taxRule);

    //     Double result = FunctionalPricing.calculate(100.00, vipPipeline);

    //     // 3. Verify
    //     // Math:
    //     // 1. Discount: 100 - 10 = 90
    //     // 2. Tax: 90 * 1.2 = 108
    //     assertEquals(108.00, result, 0.001);
    // }
}