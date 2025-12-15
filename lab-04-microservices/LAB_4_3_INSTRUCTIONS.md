# Lab 4.3: Resilience & Observability

**Scenario:** In a distributed system, failure is not an option—it’s a guarantee. The **Inventory Service** might crash, the network might lag, or a database might lock up. If the Inventory Service goes down, we don't want the **Order Service** to crash with a generic `500 Internal Server Error`. Instead, we want it to degrade gracefully—perhaps accepting the order but marking it as "PENDING" to be checked later.

**Objective:**

1.  **Refactor** our logic into a `Service` layer (Architecture Best Practice).
2.  Protect the `placeOrder` call with a **Circuit Breaker**.
3.  Implement a **Fallback** mechanism.
4.  Visualize the failure using **Spring Boot Actuator**.

**Concepts:** Resilience4j, Circuit Breaker Pattern, Fallbacks, Actuator.

-----

## **Step 1: The Service Layer (Refactoring)**

To use Spring's Circuit Breaker annotations correctly, we must move our business logic out of the Controller and into a Service class. This allows Spring to wrap the class in a "Proxy" that handles the error interception.

Open `src/main/java/com/richardlearning/order/OrderService.java`.

1.  **Add Imports:**
    We need Resilience4j annotations and our standard Spring components.

    | Why this? | Annotation/Import |
    | :--- | :--- |
    | **Resilience** | `io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker` |
    | **Service** | `org.springframework.stereotype.Service` |

    ```java
    package com.richardlearning.order;

    import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
    import org.springframework.stereotype.Service;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    // ... rest of the file ...
    ```

2.  **Annotate the Class:**
    Make it a Spring Bean and add logging support.

    ```java
    // Locate: public class OrderService {
    // Replace with this:
    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class OrderService {

        // TODO 3: Inject Repository and InventoryClient
        private final OrderRepository orderRepository;
        private final InventoryClient inventoryClient;
    ```

3.  **Move the Logic & Add Circuit Breaker:**
    We copy the logic from the Controller, but we decorate it.

    ```java
        // TODO 4: Define the 'placeOrder' method
        @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
        public String placeOrder(OrderRequest request) {
            
            // 1. Call Inventory (The Dangerous Operation)
            boolean inStock = inventoryClient.isInStock(request.sku());

            // 2. Standard Logic
            Order order = new Order();
            order.setSku(request.sku());
            order.setQuantity(request.quantity());
            order.setStatus(inStock ? "PLACED" : "INVENTORY_FAILURE");
            
            orderRepository.save(order);
            return "Order Placed! Status: " + order.getStatus();
        }
    ```

      * **`name = "inventory"`**: Maps to configuration we will write in `application.properties`.
      * **`fallbackMethod`**: If this method throws an error (e.g., Connection Refused), Spring calls `placeOrderFallback` instead.

4.  **Implement the Fallback:**
    This method **must** have the exact same arguments as the original, plus a `Throwable`.

    ```java
        // TODO 5: Define the 'placeOrderFallback' method
        public String placeOrderFallback(OrderRequest request, Throwable t) {
            log.error("Inventory Service is down! Fallback triggered. Reason: {}", t.getMessage());

            // Strategy: Accept the order anyway, but mark it PENDING
            Order order = new Order();
            order.setSku(request.sku());
            order.setQuantity(request.quantity());
            order.setStatus("PENDING_INVENTORY_CHECK");

            orderRepository.save(order);
            return "Order Received (Pending Inventory Check). Status: " + order.getStatus();
        }
    }
    ```

-----

## **Step 2: Update the Controller**

Now that the logic is in the Service, the Controller becomes a simple "traffic cop."

Open `src/main/java/com/richardlearning/order/OrderController.java`.

1.  **Replace Dependencies:**
    Remove `OrderRepository` and `InventoryClient`. Inject `OrderService` instead.

    ```java
    public class OrderController {

        // DELETE the old fields
        // private final OrderRepository orderRepository;
        // private final InventoryClient inventoryClient;

        // ADD the new field
        private final OrderService orderService;
    ```

2.  **Update Endpoint:**
    Delegate the call to the service.

    ```java
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody @Valid OrderRequest request) {
        // Simple delegation
        return orderService.placeOrder(request);
    }
    ```

-----

## **Step 3: Configuration (Actuator & Resilience)**

We need to tell Resilience4j *when* to open the circuit (e.g., "if 50% of requests fail") and tell Actuator to show us the status.

Open `src/main/resources/application.properties` in the **order-service**.

1.  **Add Actuator Config:**

    ```properties
    # --- Actuator Endpoints ---
    management.endpoints.web.exposure.include=health,info
    management.endpoint.health.show-details=always
    management.health.circuitbreakers.enabled=true
    ```

2.  **Add Resilience4j Config:**

    ```properties
    # CRITICAL: Show status in /actuator/health
    resilience4j.circuitbreaker.instances.inventory.register-health-indicator=true

    # Sensitivity Settings
    resilience4j.circuitbreaker.instances.inventory.sliding-window-size=5
    resilience4j.circuitbreaker.instances.inventory.minimum-number-of-calls=3
    resilience4j.circuitbreaker.instances.inventory.permitted-number-of-calls-in-half-open-state=3
    resilience4j.circuitbreaker.instances.inventory.automatic-transition-from-open-to-half-open-enabled=true
    resilience4j.circuitbreaker.instances.inventory.wait-duration-in-open-state=5s
    resilience4j.circuitbreaker.instances.inventory.failure-rate-threshold=50
    ```

-----

## **Step 4: The Chaos Test**

Let's verify that our system is robust.

1.  **Start Everything:** Run both `inventory-service` and `order-service`.

2.  **Happy Path:**
    Send a valid request.

    ```bash
    curl -X POST http://localhost:8082/api/orders \
         -H "Content-Type: application/json" \
         -d '{"sku": "samsung_s25", "quantity": 1}'
    ```

      * **Output:** `Order Placed! Status: PLACED`

3.  **Kill Inventory Service:**
    Go to the terminal running `inventory-service` and press `Ctrl+C`. **It is now dead.**

4.  **Trigger Fallback:**
    Send the **same** request again.

    ```bash
    curl -X POST http://localhost:8082/api/orders \
         -H "Content-Type: application/json" \
         -d '{"sku": "samsung_s25", "quantity": 1}'
    ```

      * **Output:** `Order Received (Pending Inventory Check). Status: PENDING_INVENTORY_CHECK`
      * **Observation:** The client did **not** get an error. The Order Service survived\!

5.  **Check Actuator (The Health Dashboard):**
    Open your browser or use curl:
    `http://localhost:8082/actuator/health`

    Look for the `circuitBreakers` section in the JSON. You should see:

    ```json
    "details": {
        "circuitBreakers": {
            "inventory": {
                "status": "OPEN",
                 ...
            }
        }
    }
    ```

    The Circuit is **OPEN**, meaning it has stopped trying to call the dead service to save resources.

-----

