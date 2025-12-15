# Lab 3.6: The Spring Consumer (@KafkaListener)

In Lab 3.2, you wrote a `while(true)` loop to poll for data. This is error-prone and hard to scale. In Spring Boot, we "invert control." Instead of asking for data, we simply tell Spring: "Here is a method. When a message comes in, shove it in here."

In this lab, you will create a Spring Service that listens to the `stock-prices` topic. You will verify that Spring automatically handles the polling, threading, and deserialization for you.

**This exercise should take approximately 15 minutes to complete.**

-----

## **Step 1: Open the Skeleton**

1.  Navigate to `lab-03-kafka-basics/src/main/java/com/course/kafka/SpringConsumer.java`.
2.  Open the file. It is currently empty.

## **Step 2: The Imports & Annotations**

We need to turn this plain class into a Spring Bean so the framework can manage it.

  * **`@Service`:** Tells Spring "Load this class on startup."
  * **`@KafkaListener`:** The magic annotation that binds a method to a Kafka topic.

**Action:** Replace the entire content of the file with this structure:

```java
package com.course.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SpringConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(SpringConsumer.class);

    // TODO: Add the Listener Method Here
}
```

## **Step 3: The Listener Logic**

Now we define the handler. Notice we don't return anything, and we don't loop. We just handle **one** message.

  * **`topics`:** Which queue to listen to.
  * **`groupId`:** Which consumer team we belong to.

**Action:** Paste this method inside the class (replace `// TODO`):

```java
    @KafkaListener(topics = "stock-prices", groupId = "spring-group")
    public void consume(String message) {
        LOG.info("✅ Consumed via Spring: {}", message);
    }
```

## **Step 4: Run and Verify**

This is the cool part. We don't need to run a separate "Consumer App." Since `SpringConsumer` is part of the same application package as `SpringProducerApp`, running the main app will start **both**.

1.  Navigate to `SpringProducerApp.java`.
2.  Click **Run**.
3.  **Watch the Console:**
      * You will see the Producer send the message: `Sent via Spring...`
      * Immediately after, you will see the Consumer wake up and process it: `✅ Consumed via Spring: 2500.00`

**What just happened?**

1.  Spring started up.
2.  It spun up a background thread for your `@KafkaListener`.
3.  It ran the `CommandLineRunner` (Producer).
4.  The Producer put a message on the topic.
5.  The Listener immediately picked it up.