# Lab 4.2: The Consumer Microservice (Order Service)

**Scenario:** We are now building the front end of our e-commerce platform—the **Order Service**. Its job is to process a customer's request to buy a product. Because Inventory is the owner of stock data, the Order Service **must** synchronously communicate with the Inventory Service (our API from Lab 4.1) before confirming the sale.

**Objective:** Implement inter-service communication using **OpenFeign** and enable **Java 21 Virtual Threads** to ensure high-performance I/O for our synchronous calls.

**Concepts:** OpenFeign, `@FeignClient`, Synchronous Communication, Virtual Threads (Project Loom).

## **Preparation: Ensure Dependencies are Ready**

Before coding, let's ensure all new dependencies (especially OpenFeign) are downloaded.

1.  Run the following command from the **root** folder (`advanced-java-course/`):
    ```bash
    mvn clean install -DskipTests
    ```
    *(This compiles everything and downloads all dependencies into your local Maven cache.)*

## **Step 1: The Order Entity and Repository**

We'll quickly create the data structure for an order, similar to Lab 4.1.

### **1.1 The Order Entity**

Open `src/main/java/com/richardlearning/order/Order.java`.

1.  **Add Imports:** Add the necessary imports for JPA and Lombok.

    | Why this? | Annotation/Import |
    | :--- | :--- |
    | **JPA** | `jakarta.persistence.*` (Database Mapping) |
    | **Lombok** | `lombok.*` (Less Boilerplate Code) |

    ```java
    package com.richardlearning.order;

    import jakarta.persistence.*;
    import lombok.*;
    // ... rest of the file ...
    ```

2.  **Add Annotations:** Add the required annotations for database mapping and Lombok constructors.

    ```java
    // Locate: public class Order {
    // Replace with this:
    @Entity
    @Table(name = "orders")
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public class Order {
    ```

3.  **Define Primary Key:** Add the ID field.

    ```java
        // TODO 3: Add ID field with generation strategy
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    ```

4.  **Define Data Fields:** Define the fields to track the order details.

    ```java
        // TODO 4: Add fields for 'sku' (String), 'quantity' (Integer), and 'status' (String)
        private String sku;
        private Integer quantity;
        private String status; // E.g., "PLACED", "INVENTORY_FAILURE"
    }
    ```

### **1.2 The Order Repository**

Open `src/main/java/com/richardlearning/order/OrderRepository.java` and make it a JPA repository.

1.  **Add Imports:**

    ```java
    package com.richardlearning.order;

    import org.springframework.data.jpa.repository.JpaRepository;

    // ... rest of the file ...
    ```

2.  **Extend JpaRepository:**

    ```java
    // Locate: public interface OrderRepository {
    // Replace with this:
    public interface OrderRepository extends JpaRepository<Order, Long> {
    }
    ```

## **Step 2: The Order Request DTO (API Input)**

We need a simple DTO to receive the customer's request and ensure the input is valid.

Open `src/main/java/com/richardlearning/order/OrderRequest.java`.

1.  **Add Validation Imports:**

    | Why this? | Annotation/Import |
    | :--- | :--- |
    | **Validation** | `jakarta.validation.constraints.*` (To ensure SKU and Quantity exist) |

    ```java
    package com.richardlearning.order;

    import jakarta.validation.constraints.Min;
    import jakarta.validation.constraints.NotNull;
    // ... rest of the file ...
    ```

2.  **Define Fields with Validation:** Define the `record` fields and their validation rules.

    ```java
    // Locate: public record OrderRequest(
    // Replace with this:
    public record OrderRequest(
        @NotNull String sku,
        @Min(1) Integer quantity // Must order at least 1 item
    ) {}
    ```

## **Step 3: The Inventory Client (The Communication Bridge)**

This is the most critical new component. **OpenFeign** allows us to create a **Declarative REST Client**—we define the interface, and Spring generates the actual HTTP request code.

Open `src/main/java/com/richardlearning/order/InventoryClient.java`.

1.  **Add Imports:**

    | Why this? | Annotation/Import |
    | :--- | :--- |
    | **OpenFeign** | `org.springframework.cloud.openfeign.FeignClient` |
    | **Spring Web** | `org.springframework.web.bind.annotation.*` (To map the endpoint) |

    ```java
    package com.richardlearning.order;

    import org.springframework.cloud.openfeign.FeignClient;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    // ... rest of the file ...
    ```

2.  **Add Feign Client Annotation:** This annotation tells Spring **where** to find the Inventory Service.

    ```java
    // Locate: public interface InventoryClient {
    // Replace with this:
    @FeignClient(name = "inventory-service", url = "http://localhost:8081")
    public interface InventoryClient {
    ```

      * **Note:** The `url` points exactly to the Inventory Service we built on port 8081.

3.  **Define the Method Signature:** Add the method that corresponds to the `GET` endpoint in the Inventory Service.

    ```java
        // TODO 3: Define a method to call the Inventory stock check endpoint
        // This method signature exactly matches the InventoryController's GET method from Lab 4.1
        @GetMapping("/api/inventory/{sku}")
        boolean isInStock(@PathVariable("sku") String sku);

    }
    ```

      * **Why an interface method?** OpenFeign reads this line and automatically builds the necessary HTTP request code (URL, headers, network logic) at runtime. When you call `client.isInStock("...")`, you are actually executing an HTTP GET request.

## **Step 4: The Order Controller (The Consumer Logic)**

We implement the API endpoint that executes the core microservice logic: **Check Inventory, then Save Order.**

Open `src/main/java/com/richardlearning/order/OrderController.java`.

1.  **Add Imports and Annotations:**

    ```java
    package com.richardlearning.order;

    import org.springframework.web.bind.annotation.*;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import jakarta.validation.Valid;

    // Locate: public class OrderController {
    // Replace with this:
    @RestController
    @RequestMapping("/api/orders")
    @RequiredArgsConstructor
    public class OrderController {
    ```

2.  **Inject Dependencies:** Inject the repository and the new Feign client.

    ```java
        // TODO 3: Inject Repository and InventoryClient
        private final OrderRepository orderRepository;
        private final InventoryClient inventoryClient; // Our new Feign Client
    ```

3.  **Create the POST Endpoint:** Define the `placeOrder` endpoint signature.

    ```java
        // TODO 4: Create POST endpoint to place an order
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public String placeOrder(@RequestBody @Valid OrderRequest request) {
    ```

4.  **Implement Logic (Call Inventory):** Implement the core communication logic.

    ```java
        // 1. Call Inventory Service (SYNCHRONOUS HTTP CALL - This is a blocking step)
        boolean inStock = inventoryClient.isInStock(request.sku());
        // 

        // 2. Create Order Entity
        Order order = new Order();
        order.setSku(request.sku());
        order.setQuantity(request.quantity());
    ```

5.  **Determine Status and Save:** Set the status based on the inventory response and save the record.

    ```java
        if (inStock) {
            order.setStatus("PLACED");
            // NOTE: We skip deducting stock for simplicity; the real world uses events (Lab 4.4)
        } else {
            order.setStatus("INVENTORY_FAILURE");
        }

        orderRepository.save(order);
        return "Order Placed! Status: " + order.getStatus();
    }
    ```

## **Step 5: Final Activation**

### **5.1 Enable Feign Clients**

We must tell the Spring Boot application to scan and activate the `@FeignClient` we just created.

Open `src/main/java/com/richardlearning/order/OrderApplication.java` and add the `EnableFeignClients` annotation:

```java
package com.richardlearning.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // New Import

@EnableFeignClients // New Annotation: Activates all Feign Interfaces
@SpringBootApplication
public class OrderApplication {
    // ... rest of the file ...
}
```

### **5.2 Verify Virtual Threads**

Check `order-service/src/main/resources/application.properties`:

```properties
spring.threads.virtual.enabled=true
```

  * **Performance Note:** Because our `placeOrder` method *blocks* while waiting for the Inventory Service to respond, the Virtual Threads (`Project Loom`) feature is critical here. It prevents a large number of waiting orders from consuming all the server's precious OS threads, ensuring high concurrency.

## **Step 6: Run and Test**

To test communication, you must run **both** microservices at the same time.

1.  **Start Inventory Service (Terminal 1):**

    ```bash
    mvn spring-boot:run
    ```

2.  **Start Order Service (Terminal 2):**

    ```bash
    mvn spring-boot:run 
    ```

3.  **Test 1: Fulfillable Order (SKU is in stock)**
    *(Assuming you added `samsung_s25` in Lab 4.1. If not, POST stock to port 8081 first.)*

    ```bash
    curl -X POST http://localhost:8082/api/orders \
         -H "Content-Type: application/json" \
         -d '{"sku": "samsung_s25", "quantity": 2}'
    ```

    **Expected Output:** `Order Placed! Status: PLACED`

4.  **Test 2: Unfulfillable Order (SKU is NOT in stock)**

    ```bash
    curl -X POST http://localhost:8082/api/orders \
         -H "Content-Type: application/json" \
         -d '{"sku": "nokia_3310", "quantity": 1}'
    ```

    **Expected Output:** `Order Placed! Status: INVENTORY_FAILURE`

If both services successfully communicate and return the correct status, you are ready for the next level: **Resilience**\! Stop both applications.