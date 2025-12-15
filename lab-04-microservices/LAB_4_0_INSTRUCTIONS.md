# Lab 4.0: Microservices Architecture Setup

**Objective:** Understand the "Shared Nothing" architecture of our distributed system before we write the logic.

In Module 4, we are moving from a single monolithic application to a **Distributed System**. We have pre-configured two empty services that mimic a real-world production environment.

-----

### **1. The Architecture**

We are building two independent services that will communicate over the network. To ensure true isolation, they do not share memory, ports, or databases.

  * **Inventory Service (The Provider):** Owns product availability.
  * **Order Service (The Consumer):** Processes user requests and consumes inventory.

-----

### **2. Configuration Walkthrough**

We have established the following baseline configuration. Open the `application.properties` files to verify these settings.

#### **A. Inventory Service (Port 8081)**

  * **Role:** The "Source of Truth" for stock levels.
  * **Database:** Connects strictly to `inventory_db`. It cannot see Order data.
  * **Port:** Runs on `8081` to avoid conflict with the Order Service.

<!-- end list -->

```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://microservice-db:5432/inventory_db
```

#### **B. Order Service (Port 8082)**

  * **Role:** The public-facing API that takes customer orders.
  * **Database:** Connects strictly to `order_db`.
  * **Virtual Threads:** We have enabled Java 21 Virtual Threads (`Loom`). This allows the service to handle thousands of concurrent requests without blocking standard OS threads, significantly improving throughput for I/O operations (like calling other services).

<!-- end list -->

```properties
server.port=8082
spring.datasource.url=jdbc:postgresql://microservice-db:5432/order_db
spring.threads.virtual.enabled=true  # <--- High Performance Mode
```

-----

### **3. Key Principles**

1.  **Database Per Service:**

      * **Rule:** Service A must never access Service B's database directly.
      * **Why?** If Inventory changes its table schema, Order Service shouldn't break. This is **Loose Coupling**.

2.  **Port Separation:**

      * **Rule:** Each service needs a unique entry point.
      * **Why?** In a real cluster (Kubernetes), these would be on different IPs. Locally, we simulate this with different ports (`8081`, `8082`).

3.  **Virtual Threads (Project Loom):**

      * **Rule:** Use lightweight threads for blocking tasks.
      * **Why?** When Order Service calls Inventory Service, it has to wait. Virtual threads make this "wait" almost free for the CPU.

-----

**Next Step:**
Now that the infrastructure is ready, let's build the **Inventory API** in **Lab 4.1**.