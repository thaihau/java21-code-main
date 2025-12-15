# Lab 4.1: The Provider Microservice (REST API)

Your journey into microservices begins by establishing the bedrock of our business: the **Inventory Service**. Imagine this service is the highly organized manager of a warehouse, holding the master list of all available stock. Before any customer order can be fulfilled, the **Order Service** must contact this manager to check if the product is on the shelf.

In this lab, you will build this critical manager. You will use **Spring Data JPA** to seamlessly connect to the `inventory_db` and then build the **REST API** endpoints (`GET` and `POST`) that allow other services to safely query and update the stock levels. This service is our "Source of Truth," and by the end of this exercise, it will be the first functional node in our distributed system. 

**This exercise should take approximately 40 minutes to complete.**

## **Step 1: The Product Entity (Database Schema)**

We need a Java class to represent a row in our database table. We will use **JPA** to map this class to Postgres.

1.  Open `src/main/java/com/richardlearning/inventory/Product.java`.
2.  Add the necessary imports for JPA and Lombok.

| Why this? | Annotation/Import |
| :--- | :--- |
| **JPA** | `jakarta.persistence.*` (Database Mapping) |
| **Lombok** | `lombok.*` (Auto-generate Getters/Setters/Constructors) |

```java
package com.richardlearning.inventory;

import jakarta.persistence.*;
import lombok.*;

// ... rest of the file ...
```

3.  Locate the class declaration and add the required annotations for database mapping and boilerplate code.

* **`@Entity`**
Marks this Java class as an object that is stored in the database.
* **`@Table(name = "products")`**
Tells the database to save these objects specifically in the "products" table.
* **`@Getter`**
Automatically generates the code needed to read your variables (e.g., `getPrice()`).
* **`@Setter`**
Automatically generates the code needed to update your variables (e.g., `setPrice()`).
* **`@NoArgsConstructor`**
Creates an empty constructor, which is strictly required for JPA/Hibernate to work.
* **`@AllArgsConstructor`**
Creates a full constructor so you can build a new object in a single line of code.

<!-- end list -->

```java
// Locate: public class Product {
// Replace with this:
@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Product {
```

4.  Inside the class, define the fields, including the primary key.

* **`@Id`**
Designates this field as the primary key.
* **`@GeneratedValue(strategy = GenerationType.IDENTITY)`**
Configures the database to automatically generate and assign 

<!-- end list -->

```java
    // TODO 3: Add ID field with generation strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 4: Add fields for 'sku' (String) and 'quantity' (Integer)
    private String sku;
    private Integer quantity;
```

## **Step 2: The Inventory Repository (Data Access)**

We use **Spring Data JPA** to interact with the database without writing manual SQL.

1.  Open `InventoryRepository.java`.
2.  Add the `JpaRepository` import.
    
<!-- end list -->

```java
package com.richardlearning.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // We need this for safe lookups

// ... rest of the file ...
```

3.  Locate the interface declaration and extend `JpaRepository`.

    This is a standard and powerful line of code used in Spring Data JPA.


    * **`extends JpaRepository`**
    This is the core magic! By extending,  **automatically inherits over 15 methods** for performing common database operations (like `save()`, `findAll()`, `findById()`, `delete()`, etc.). 
    * **`<Product, Long>`**
    These are the two required generics (placeholders) that tell Spring Data JPA:
    * **`Product`**: This is the Entity this repository is designed to manage.
    * **`Long`**: This is the data type of the **Primary Key** 

<!-- end list -->

```java
// Locate: public interface InventoryRepository {
// Replace with this:
public interface InventoryRepository extends JpaRepository<Product, Long> {
```

4.  Add the "Magic Method" to find products by their unique Stock Keeping Unit (SKU).

    When a method returns `Optional<Product>`, the result is never null. It is always an Optional object, which is either:
        
    **Present** : It contains a valid Product inside.
    
    **Empty**: It contains nothing, clearly indicating the product was not found.


<!-- end list -->

```java
    // TODO 3: Define method to find by SKU
    // Spring translates this method signature into: SELECT * FROM products WHERE sku = ?
    Optional<Product> findBySku(String sku);
```

## **Step 3: The Inventory Request DTO (API Input & Validation)**

We define a Data Transfer Object (DTO) to control the input data format and enforce basic validation rules before the data even touches our business logic.

1.  Open `InventoryRequest.java`.
2.  Add the necessary **Bean Validation** imports.

| Why this? | Annotation/Import |
| :--- | :--- |
| **Validation** | `jakarta.validation.constraints.*` |

```java
package com.richardlearning.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// ... rest of the file ...
```

3.  Locate the `record` declaration and define the fields with validation annotations.

<!-- end list -->

```java
// Locate: public record InventoryRequest(
// Replace with this:
public record InventoryRequest(
    @NotNull(message = "SKU is required") String sku,
    @Min(value = 0, message = "Quantity cannot be negative") Integer quantity
) {}
```

## **Step 4: The Inventory Controller (The REST API)**

This is the public-facing API that exposes our service functionality to other services.

1.  Open `InventoryController.java`.
2.  Add the Spring Web and utility imports.

<!-- end list -->

```java
package com.richardlearning.inventory;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;

// ... rest of the file ...
```

3.  Add the necessary annotations to make this a REST controller and inject the repository.

<!-- end list -->

```java
// Locate: public class InventoryController {
// Replace with this:
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    // TODO 3: Inject Repository
    private final InventoryRepository repository;
```

4.  Implement the **GET** endpoint to check stock.

<!-- end list -->

```java
    // TODO 4: Create GET endpoint to check stock
    @GetMapping("/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("sku") String sku){
        // Find product by SKU and check if quantity is greater than 0
        return repository.findBySku(sku)
                .map(product -> product.getQuantity() > 0)
                .orElse(false); // If product not found, it's not in stock
    }
```

5.  Implement the **POST** endpoint to add stock.

<!-- end list -->

```java
    // TODO 5: Create POST endpoint to add stock
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addStock(@RequestBody @Valid InventoryRequest request) {
        // 1. Check if product already exists
        Product product = repository.findBySku(request.sku())
                // 2. If not found, create a new one with 0 quantity
                .orElse(new Product(null, request.sku(), 0));

        // 3. Update quantity and save back to DB
        product.setQuantity(product.getQuantity() + request.quantity());
        repository.save(product);
    }
```

## **Step 5: Run and Test**

1.  **Run the Inventory Service:**

      * In your terminal (from the inventory-service folder), run:
        ```bash
        mvn spring-boot:run
        ```
      * Wait for the log: `Tomcat started on port 8081`.

2.  **Test 1: Add Stock (POST)**

      * open another terminal and use this `cURL` command to add 150 units of a product.
        ```bash
        curl -X POST http://localhost:8081/api/inventory \
             -H "Content-Type: application/json" \
             -d '{"sku": "samsung_s25", "quantity": 150}'
        ```

3.  **Test 2: Check Stock (GET)**

      * Check the product we just added:

        ```bash
        curl http://localhost:8081/api/inventory/samsung_s25
        ```

        **Expected Output:** `true`

      * Check a product that was not added:

        ```bash
        curl http://localhost:8081/api/inventory/fake_item
        ```

        **Expected Output:** `false`

If the tests succeed, you have a functional microservice\! **Stop the application** (Ctrl+C).

-----

