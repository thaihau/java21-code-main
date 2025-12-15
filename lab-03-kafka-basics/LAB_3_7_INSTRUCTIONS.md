# Lab 3.7: Safe Reprocessing (Spring Retry)

In a distributed system, things fail. A database might blink, a network might stutter, or a message might be malformed. The worst thing a consumer can do is crash the entire application because of one bad message.

Spring Kafka provides a safety net called the **DefaultErrorHandler**. By default, if your listener throws an exception, Spring will **not** commit the offset. Instead, it will pause, wait, and try to process the message again (up to 10 times). This "Backoff" gives your system a chance to self-heal.

In this lab, you will intentionally poison a message to verify that Spring retries it safely before giving up.

**This exercise should take approximately 15 minutes to complete.**

-----

## **Step 1: Enable the Consumer**

1.  Navigate to `lab-03-kafka-basics/src/main/java/com/course/kafka/SpringRetryConsumer.java`.
2.  Open the file. You will see the code is commented out to prevent it from running prematurely.
3.  **Action:** Uncomment the **Imports** (TODO 1) and the **`@Service` annotation** (TODO 2).

*Your code should look like this:*

```java
package com.course.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO 1: Uncomment Imports
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// TODO 2: Uncomment Annotation
@Service
public class SpringRetryConsumer {
    // ...
```

## **Step 2: Enable the Poison Pill**

Now, uncomment the listener logic. Notice the check: if the message contains the word "FAIL", we throw a `RuntimeException`. This simulates a processing crash (like a database connection failure).

**Action:** Uncomment the method block (TODO 3 & 4).

```java
    // TODO 3: Uncomment The Listener Method
    @KafkaListener(topics = "stock-prices", groupId = "retry-group")
    public void consume(String message) {
        LOG.info("📥 Consuming: {}", message);

        // TODO 4: Add Poison Pill Logic
        if (message.contains("FAIL")) {
            throw new RuntimeException("🔥 Poison Pill Detected!");
        }
    }
```

## **Step 3: Trigger the Crash**

To test this, we need to send a message that triggers the `if` block.

1.  Open `SpringProducerApp.java`.
2.  Find the `value` variable inside the `run()` method.
3.  **Action:** Change the value to `"FAIL"`.

<!-- end list -->

```java
        String topic = "stock-prices";
        String key = "TESLA";
        String value = "FAIL"; // <--- Change this from "2500.00"
```

## **Step 4: Run and Verify Safety**

1.  Click **Run** on `SpringProducerApp.java`.
2.  **Watch the Console closely.**
      * *Observation:* You will see the log `📥 Consuming: FAIL` appear **10 times** in rapid succession.
      * *Conclusion:* Spring caught the exception, paused, and retried 9 more times.
      * *Final Result:* After the 10th failure, Spring prints a huge `ERROR` stack trace and says "Giving up." The message is skipped, and the app stays alive.

**You have successfully implemented Safe Reprocessing\!**

