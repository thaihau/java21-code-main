package com.richardlearning.order;

// TODO 1: Add Resilience4j, Spring Service, and Lombok imports

// TODO 2: Add Service and RequiredArgsConstructor annotations
public class OrderService {

    // TODO 3: Inject Repository and InventoryClient

    // TODO 4: Define the 'placeOrder' method (Move logic from Controller to here)
    // Add @CircuitBreaker annotation here later

    // TODO 5: Define the 'placeOrderFallback' method
    // This method is called when the Circuit Breaker is OPEN or Inventory is down

}