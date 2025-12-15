# Lab 4.4 Part 2: The Consumer (Inventory Service)

**Scenario:** The Inventory Service is now a passive listener. It sits and waits for "Order Placed" events to arrive on the Kafka topic. When an event arrives, it wakes up, checks if it has seen this order before (Idempotency), and if not, updates the stock levels.

**Objective:** Implement a Kafka Consumer that is **Idempotent** (safe against duplicate messages).

**Concepts:** Kafka Consumer, Idempotency, At-Least-Once Delivery.

-----

## **Step 1: The Event Record**

We need a matching Java record to deserialize the JSON data coming from the Order Service.

Open `src/main/java/com/richardlearning/inventory/OrderPlacedEvent.java`.

1.  **Define the Record:**
    *(This must match the Producer's structure exactly)*
    ```java
    package com.richardlearning.inventory;

    public record OrderPlacedEvent(
        Long orderId,
        String sku,
        Integer quantity
    ) {}
    ```

## **Step 2: Idempotency Infrastructure**

Kafka guarantees "At-Least-Once" delivery. This means, on rare occasions, you might receive the same message twice. If we blindly deduct stock every time, our inventory will be wrong. We need a "memory" of processed IDs.

### **2.1 The Processed Event Entity**

Open `src/main/java/com/richardlearning/inventory/ProcessedEvent.java`.

1.  **Add Imports and Annotations:**
    ```java
    package com.richardlearning.inventory;

    import jakarta.persistence.*;
    import lombok.*;

    @Entity
    @Table(name = "processed_events")
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public class ProcessedEvent {

        // TODO 3: Add ID (Primary Key)
        // We manually set this ID to match the Order ID
        @Id
        private Long id; 
    }
    ```

### **2.2 The Repository**

Open `src/main/java/com/richardlearning/inventory/ProcessedEventRepository.java`.

1.  **Implement the Interface:**
    ```java
    package com.richardlearning.inventory;

    import org.springframework.data.jpa.repository.JpaRepository;

    public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {
    }
    ```

## **Step 3: The Kafka Consumer**

This is the brain of the operation. It listens to the topic and executes the business logic.

Open `src/main/java/com/richardlearning/inventory/InventoryEventListener.java`.

1.  **Add Imports:**

    ```java
    package com.richardlearning.inventory;

    import org.springframework.kafka.annotation.KafkaListener;
    import org.springframework.stereotype.Component;
    import org.springframework.transaction.annotation.Transactional;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import java.util.Optional;
    ```

2.  **Annotate and Inject:**

    ```java
    @Component
    @RequiredArgsConstructor
    @Slf4j
    public class InventoryEventListener {

        private final InventoryRepository inventoryRepository;
        private final ProcessedEventRepository processedEventRepository;
        private final ObjectMapper objectMapper;
    ```

3.  **Implement the Listener:**

    ```java
       @KafkaListener(topics = "orders", groupId = "inventory-group")
        @Transactional
        public void handleOrderPlaced(String message) {
            try {
                // 1. Parse the Message
                OrderPlacedEvent event = objectMapper.readValue(message, OrderPlacedEvent.class);
                log.info("Received Event for Order ID: {}", event.orderId());

                // 2. Idempotency Check (Have we processed this Order ID before?)
                if (processedEventRepository.existsById(event.orderId())) {
                    log.warn("Duplicate Event detected for Order ID: {}. Skipping.", event.orderId());
                    return;
                }

                // 3. Update Inventory (Using your 'Product' entity)
                Optional<Product> productOpt = inventoryRepository.findBySku(event.sku());
                
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    
                    // Deduct the quantity
                    product.setQuantity(product.getQuantity() - event.quantity());
                    
                    // Save the updated Product back to DB
                    inventoryRepository.save(product);
                    log.info("Stock updated for SKU: {}", event.sku());
                } else {
                    log.error("SKU not found: {}", event.sku());
                }

                // 4. Mark as Processed (Idempotency)
                ProcessedEvent processed = new ProcessedEvent(event.orderId());
                processedEventRepository.save(processed);

            } catch (Exception e) {
                log.error("Error processing message", e);
                throw new RuntimeException(e);
            }
        }
    }
    ```

## **Step 4: Configuration Check**

Ensure your `inventory-service` knows where Kafka is.

Check `src/main/resources/application.properties` in **inventory-service**:

```properties
# It should basically look like this:
spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.consumer.group-id=inventory-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

*Note: We use `StringDeserializer` because we are manually parsing JSON with `ObjectMapper` in the listener.*

-----

## **Step 5: The Grand Finale Test**

Now we test the fully decoupled, event-driven system.

1.  **Start Infrastructure:** Ensure Docker (Postgres/Kafka) is running.

2.  **Start Services:**

      * Terminal 1 (inventory services): `mvn spring-boot:run`
      * Terminal 2 (order services): `mvn spring-boot:run`

3.  **Check Stock (Before):**
    You should have stock for `samsung_s25`. (Check via logs or API if you kept the GET endpoint).

4.  **Place Order:**

    ```bash
    curl -X POST http://localhost:8082/api/orders \
         -H "Content-Type: application/json" \
         -d '{"sku": "samsung_s25", "quantity": 1}'
    ```

      * **Response:** `Order Received. ID: 1` (Instant response\!)

5.  **Watch the Logs:**

      * **Order Service:** Should say `Found 1 unprocessed events... Sent event ID: 1`.
      * **Inventory Service:** Should say `Received Event for Order ID: 1 ... Stock updated`.

6.  **Verify Idempotency (The "Duplicate" Test):**

      * Restart the Inventory Service (simulating a crash/restart).
      * Because the message was committed to Kafka, it shouldn't re-process.
      * *Hard Mode:* If you manually send the same JSON message to the Kafka topic using a CLI tool, the logs should say: `Duplicate Event detected... Skipping.`

**Congratulations\!** You have built a robust, microservices-based e-commerce platform with:

  * Domain-Driven Design (Entities/Repositories)
  * REST APIs
  * OpenFeign (Synchronous Comms)
  * Circuit Breakers (Resilience)
  * Kafka & Outbox Pattern (Event-Driven Consistency)
