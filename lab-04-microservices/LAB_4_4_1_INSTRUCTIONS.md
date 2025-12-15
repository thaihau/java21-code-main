# Lab 4.4 Part 1: The Producer (Order Service)

**Scenario:** We are cutting the cord. The **Order Service** will no longer wait for the **Inventory Service**. If the Inventory system is down for maintenance, we should still be able to accept orders.

To do this safely, we use the **Transactional Outbox Pattern**. When an order comes in, we save two things to the database in the **same transaction**:

1.  The `Order` record.
2.  An `Outbox` event record (a "To-Do" note for Kafka).

A background process (The Poller) then reads the "To-Do" notes and pushes them to Kafka. This guarantees we never lose a message.

**Objective:** Implement the Outbox Pattern and a Kafka Producer in the Order Service.

**Concepts:** Transactional Outbox, Kafka Producer, Spring Scheduling.

-----

## **Step 1: The Event Record**

First, we define the data payload we want to send to Kafka.

Open `src/main/java/com/richardlearning/order/OrderPlacedEvent.java`.

1.  **Define the Record:**
    ```java
    package com.richardlearning.order;

    // TODO 1: Create a record to hold order details
    public record OrderPlacedEvent(
        Long orderId,
        String sku,
        Integer quantity
    ) {}
    ```

## **Step 2: The Outbox Entity & Repository**

We need a table to store events before they go to Kafka.

### **2.1 The Outbox Entity**

Open `src/main/java/com/richardlearning/order/Outbox.java`.

1.  **Add Imports and Annotations:**

    ```java
    package com.richardlearning.order;

    import jakarta.persistence.*;
    import lombok.*;

    // Locate: public class Outbox {
    // Replace with this:
    @Entity
    @Table(name = "outbox")
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder // Helps us create objects easily
    public class Outbox {
    ```

2.  **Define Fields:**

    ```java
        // TODO 3: Add ID, 'topic', 'payload', and 'processed' fields
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String topic;    // Where to send it (e.g., "orders")
        
        // We store the JSON payload as a simple String here
        @Column(columnDefinition = "TEXT") 
        private String payload; 

        private boolean processed; // True = Sent to Kafka
    }
    ```

### **2.2 The Outbox Repository**

Open `src/main/java/com/richardlearning/order/OutboxRepository.java`.

1.  **Implement the Interface:**
    ```java
    package com.richardlearning.order;

    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.List;

    // Locate: public interface OutboxRepository {
    // Replace with this:
    public interface OutboxRepository extends JpaRepository<Outbox, Long> {

        // TODO 3: Define a method to find all records where 'processed' is false
        List<Outbox> findByProcessedFalse();
    }
    ```

## **Step 3: Update OrderService (The "Write" Side)**

We need to modify our service to stop calling Feign and start saving to the Outbox.

Open `src/main/java/com/richardlearning/order/OrderService.java`.

1.  **Update Dependencies:**
    Remove `InventoryClient`. Add `OutboxRepository` and `ObjectMapper` (for JSON conversion).

    ```java
    // ... imports ...
    import com.fasterxml.jackson.databind.ObjectMapper; // New Import
    import jakarta.transaction.Transactional; // Critical for atomicity!

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class OrderService {

        private final OrderRepository orderRepository;
        private final OutboxRepository outboxRepository; // New
        private final ObjectMapper objectMapper;         // New
        // REMOVED: private final InventoryClient inventoryClient; 
    ```

2.  **Update `placeOrder`:**
    Rewrite the method to use the Outbox pattern.

    ```java
    // TODO 4: Define the 'placeOrder' method
    @Transactional // IMPORTANT: Saves Order AND Outbox in one go
    public String placeOrder(OrderRequest request) {
        
        // 1. Save the Order (Status: PENDING)
        Order order = new Order();
        order.setSku(request.sku());
        order.setQuantity(request.quantity());
        order.setStatus("PENDING"); // We don't know the result yet
        orderRepository.save(order);

        // 2. Create the Event Payload
        OrderPlacedEvent event = new OrderPlacedEvent(
            order.getId(), 
            request.sku(), 
            request.quantity()
        );

        // 3. Save to Outbox (Serialize to JSON)
        try {
            String jsonPayload = objectMapper.writeValueAsString(event);
            
            Outbox outbox = Outbox.builder()
                .topic("orders")
                .payload(jsonPayload)
                .processed(false)
                .build();
            
            outboxRepository.save(outbox);
            
        } catch (Exception e) {
            throw new RuntimeException("Error serializing event", e);
        }

        return "Order Received. ID: " + order.getId();
    }
    ```

      * *Note: We can remove the Fallback method and `@CircuitBreaker` annotation since we aren't making network calls here anymore.*

## **Step 4: The Poller (The "Send" Side)**

Now we create the background task that actually talks to Kafka.

Open `src/main/java/com/richardlearning/order/OrderPoller.java`.

1.  **Add Imports:**

    ```java
    package com.richardlearning.order;

    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.stereotype.Component;
    import org.springframework.kafka.core.KafkaTemplate;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import java.util.List;
    ```

2.  **Annotate and Inject:**

    ```java
    // Locate: public class OrderPoller {
    // Replace with this:
    @Component
    @RequiredArgsConstructor
    @Slf4j
    public class OrderPoller {

        // TODO 3: Inject dependencies
        private final OutboxRepository outboxRepository;
        private final KafkaTemplate<String, String> kafkaTemplate;
    ```

3.  **Implement the Scheduled Task:**

    ```java
        // TODO 4: Create a @Scheduled method to process the Outbox
        @Scheduled(fixedRate = 5000) // Run every 5 seconds
        public void processOutbox() {
            List<Outbox> unprocessedEvents = outboxRepository.findByProcessedFalse();

            if (unprocessedEvents.isEmpty()) return;

            log.info("Found {} unprocessed events. Sending to Kafka...", unprocessedEvents.size());

            for (Outbox event : unprocessedEvents) {
                try {
                    // Send to Kafka: topic, key (id), value (payload)
                    kafkaTemplate.send(event.getTopic(), String.valueOf(event.getId()), event.getPayload());

                    // Mark as processed
                    event.setProcessed(true);
                    outboxRepository.save(event);
                    
                    log.info("Sent event ID: {}", event.getId());

                } catch (Exception e) {
                    log.error("Failed to send event ID: {}", event.getId(), e);
                }
            }
        }
    }
    ```

## **Step 5: Enable Scheduling**

Finally, turn on the "heartbeat" of the application.

Open `src/main/java/com/richardlearning/order/OrderApplication.java`.

```java
import org.springframework.scheduling.annotation.EnableScheduling; // Import

@EnableScheduling // Add this annotation
// @EnableFeignClients (You can comment this out, we aren't using it anymore)
@SpringBootApplication
public class OrderApplication {
    // ...
}
```

Lets move on to Lab 4.4.2
-----
