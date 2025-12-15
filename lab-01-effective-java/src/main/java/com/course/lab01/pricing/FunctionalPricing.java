package com.course.lab01.pricing;

import java.util.function.Function;

public class FunctionalPricing {

    /*
     * TODO: PART 1 - Define the Components
     * We call these "Function Variables" (or First-Class Functions).
     * * Why "Component"? 
     * These are standalone units of logic. They don't know about each other.
     */
    
    // public static final Function<Double, Double> taxRule = 
    //     price -> price * 1.20;

    // public static final Function<Double, Double> discountRule = 
    //     price -> price - 10.00;


    /*
     * TODO: PART 2 - Define the Composer
     * This method is a "dumb runner". It acts as the execution engine.
     * It does not know (or care) if the pipeline is Tax->Discount or Discount->Tax.
     * * pipeline.apply(initialPrice) is the "Go" button that triggers the chain.
     */

    // public static Double calculate(Double initialPrice, Function<Double, Double> pipeline) {
    //     return pipeline.apply(initialPrice);
    // }
}