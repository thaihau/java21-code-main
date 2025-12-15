# Lab 3.8: Exactly-Once (Transactions & Idempotency)

This is the Capstone Lab for Module 3. We will implement the "Holy Grail" of messaging: **Exactly-Once Delivery**.

You will build a simulated Payment System that:

1.  **Producer:** Uses `@Transactional` to send "Debit" and "Credit" messages as a single atomic unit. If one fails, both are rolled back.
2.  **Consumer:** Uses a "Read Committed" isolation level and a De-duplication check to ensure we never process the same payment twice.

**This exercise should take approximately 25 minutes to complete.**

## **Step 1: Configure Transactions**

Transactions require specific settings in your configuration file.

1.  Open `src/main/resources/application.yml`.
2.  Ensure the following lines are present


```yml
# Required for @Transactional to work
      transaction-id-prefix: tx-

# This tells the consumer: "Ignore messages from rolled-back transactions"
      isolation-level: read_committed
```

## **Step 2: The Atomic Producer**

We will implement a method that sends two related messages. We will verify that if the method crashes halfway, **neither** message is visible.

1.  Open `src/main/java/com/course/kafka/PaymentService.java`.
2.  **Uncomment** the imports, annotation (`@Service`), and the `template` field.
3.  **Paste** this method logic (replace `TODO 4`):

<!-- end list -->

```java
    @Transactional
    public void sendPayment(String transactionId, boolean fail) {
        LOG.info("💰 Starting Transaction: {}", transactionId);

        // 1. Debit Alice (This happens first)
        template.send("payments", transactionId + "-debit", "Debit:Alice:100");

        if (fail) {
            throw new RuntimeException("🔥 Database Crash! Rolling back transaction...");
        }

        // 2. Credit Bob (This happens only if no crash)
        template.send("payments", transactionId + "-credit", "Credit:Bob:100");
        
        LOG.info("✅ Transaction Committed");
    }
```

## **Step 3: The Idempotent Consumer**

Now we implement the consumer. It keeps a memory cache of processed IDs. If it sees an ID again, it stops.

1.  Open `src/main/java/com/course/kafka/PaymentListener.java`.
2.  **Uncomment** the imports and `@Service`.
3.  **Paste** this method logic (replace `TODO 3`):

<!-- end list -->

```java
    @KafkaListener(topics = "payments", groupId = "payment-group")
    public void consume(@Payload String message, 
                        @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        // IDEMPOTENCY CHECK
        if (processedIds.contains(key)) {
            LOG.warn("🛑 Skipping Duplicate Transaction: {}", key);
            return;
        }

        LOG.info("💳 Processing Payment: ID={} | Val={}", key, message);
        
        // Mark as processed so we never do it again
        processedIds.add(key);
    }
```

## **Step 4: The Runner**

Finally, we run the test case. We will attempt one successful transaction and one failed transaction.

1.  Open `src/main/java/com/course/kafka/PaymentRunner.java`.
2.  **Uncomment** the imports, `@Component`, `implements CommandLineRunner`, and the `paymentService` field.
3.  **Paste** this logic inside the class (replace `TODO 4`):

<!-- end list -->

```java
    @Override
    public void run(String... args) throws Exception {
        // Test 1: Successful Transaction
        paymentService.sendPayment("TX-101", false);

        // Test 2: Failed Transaction (Rollback)
        // We expect an error, but CRUCIALLY, the Consumer should NOT see "Debit:Alice"
        try {
           paymentService.sendPayment("TX-102", true);
        } catch (Exception e) {
           System.out.println("❌ " + e.getMessage());
        }
    }
```
---
## **Step 5: Clean up**
Here is your clean-up plan. We are going to **"Mute"** the Stock/Retry labs so they don't interfere, and **"Unmute"** the new Payment/Transaction lab.

Follow these 2 simple changes.

1. Disable the Stock Producer (Driver)Go to `SpringProducerApp.java`.
We need to keep the `main` method (so the app starts), but we must stop it from sending the "TESLA" message.

    **Action:** Comment out the code inside the `run` method.

    ```java
    // SpringProducerApp.java

    @Override
    public void run(String... args) throws Exception {
        /* <--- BLOCK COMMENT START
        System.out.println("=== Starting Spring Boot Producer ===");
        
        String topic = "stock-prices";
        String key = "TESLA";
        String value = "FAIL";

        var result = template.send(topic, key, value).get();

        System.out.println("✅ Sent via Spring: " + result.getRecordMetadata());
        */ // <--- BLOCK COMMENT END
    }

    ```

2. Disable the Stock Consumers (Passengers)We don't want the old consumers logging "Poison Pill" or "Stock Price" messages if something accidentally gets sent.

    **Action:** Open `SpringConsumer.java` and comment out the `@Service` annotation.

    ```java
    // SpringConsumer.java
    // @Service  <--- Comment this out. Spring will now ignore this class.
    public class SpringConsumer { ... }

    ```

    **Action:** Open `SpringRetryConsumer.java` and comment out the `@Service` annotation.

    ```java
    // SpringRetryConsumer.java
    // @Service  <--- Comment this out too.
    public class SpringRetryConsumer { ... }

    ```
----

## **Step 6: Run and Verify**

1.  Run the `SpringProducerApp.java` application.
2.  **Check the logs:**
      * **TX-101:** You will see "Processing Payment" twice (Debit Alice, Credit Bob).
      * **TX-102:** You will see the exception "🔥 Database Crash".
      * **Crucial Observation:** You will **NOT** see "Processing Payment... Debit:Alice" for TX-102. Even though the code executed that line, the transaction rolled back, so the Consumer (Read Committed) ignored it.