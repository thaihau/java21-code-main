# Lab 3.3: The Elastic Consumer

Now that we have a Producer firing data into the cluster, we need someone to listen. In Lab 3.3, you will build a "Naked" Java Consumer that acts as the **Stock Analytics Engine**. Unlike the Producer which just "fires and forgets," the Consumer is a long-running process that must actively "poll" the broker for new data. You will configure this consumer to join a specific **Consumer Group**, unlocking Kafka's most powerful feature: the ability to automatically share the workload with other instances and "Rebalance" seamlessly if a member crashes.

**This exercise should take approximately 30 minutes to complete.**

-----

## **Step 1: Open the Skeleton**

1.  Navigate to `lab-03-kafka-basics/src/main/java/com/course/kafka/ConsumerApp.java`.
2.  Open the file. You will see the basic class structure ready for your code.

## **Step 2: The Imports**

Just like the Producer, the Consumer needs specific tools. Notice the symmetry: where the Producer used a `Serializer` (Java -\> Bytes), the Consumer requires a `Deserializer` (Bytes -\> Java) to translate the raw network data back into readable Strings.

**Action:** Paste this snippet at the top of the file (replace `// TODO: Paste Imports Here`):

```java
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
```

## **Step 3: The Handshake (Configuration)**

We need to configure three critical settings:

1.  **Connection:** `BOOTSTRAP_SERVERS` (Same as before).
2.  **Team Name:** `GROUP_ID_CONFIG`. This is the ID of your "Team". All consumers sharing this ID will divide the topic's partitions between them.
3.  **Starting Point:** `AUTO_OFFSET_RESET_CONFIG`. If this is the first time the team has ever seen this topic, where should we start reading? `earliest` means "Start from the beginning of time."

**Action:** Paste this inside the `main()` method (replace `// TODO: Configure the Consumer`):

```java
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
        // Critical: The Group ID determines the "Team"
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "stock-analytics-group");
        // Critical: If we have no history, start from the beginning
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
```

## **Step 4: The Polling Loop (The "Pull" Model)**

Kafka is a **PULL** system. The broker does not push data to you; you must ask for it. We do this by running an infinite `while(true)` loop that calls `poll()`.

  * `subscribe()`: Tells Kafka which topics we are interested in.
  * `poll(Duration)`: Asks the broker "Do you have data?" and waits up to 1 second for an answer.

**Action:** Paste this below the configuration (replace `// TODO: Subscribe & Poll`):

```java
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // 1. Subscribe to the topic
            consumer.subscribe(List.of("stock-prices"));

            // 2. The "Event Loop"
            while (true) {
                // Ask for data (wait up to 1 second)
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    LOG.info("Consumed: Key={} | Value={} | Partition={} | Offset={}", 
                        record.key(), record.value(), record.partition(), record.offset());
                }
            }
        }
```

-----

## **Step 5: Run and Verify**

This requires a specific sequence because the Consumer is a listener.

1.  **Start the Listener:** Click **Run** on `ConsumerApp.java`.
      * *Observation:* It will start and then... do nothing. It is just waiting.
2.  **Trigger the Data:** Switch back to `ProducerApp.java` and click **Run**.
3.  **Watch the Consumer:**
      * Switch back to the `ConsumerApp` terminal.
      * *Result:* You should see the stock prices (`AAPL`, `GOOGL`, etc.) appear instantly.

## **Step 6: The "Elastic" Test (Load Balancing)**

Now we will prove that Kafka scales. We will run **two** consumers side-by-side.

1.  **Keep Consumer 1 Running:** (Do not stop the previous one).
2.  **Open a Split Terminal:**
      * In the VS Code Terminal panel, click the **"Split Terminal"** icon (It looks like a box split in half `|\`, or right-click the terminal list and select "Split").
3.  **Run Consumer 2 Manually:**
      * In the new right-hand terminal, type:
        ```bash
        /usr/bin/env /opt/java/openjdk/bin/java -cp "target/classes:target/dependency/*" com.course.kafka.ConsumerApp
        ```
      * *(Or simply right-click `ConsumerApp.java` and choose "Run Java" again—VS Code handles parallel runs).*
4.  **The Trigger:** Go to `ProducerApp.java` and click **Run** again.
5.  **The Proof:**
      * Watch both terminals.
      * You will see **some** messages appear on the Left Terminal (e.g., `AAPL`).
      * You will see **other** messages appear on the Right Terminal (e.g., `GOOGL`).
      * Kafka has automatically split the partitions between your two instances\!