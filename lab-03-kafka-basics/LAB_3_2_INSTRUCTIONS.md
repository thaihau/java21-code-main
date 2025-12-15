# Lab 3.2: The Reliable Producer (Acks & Idempotency)

In Lab 3.1, we built a "Fast" producer. It fired messages and didn't worry too much about whether the broker actually saved them to disk. In the real world (especially for financial systems), speed isn't enough; we need guarantees.

In Lab 3.2, you will harden your producer using two powerful settings:

1.  **`acks=all`:** Forces the leader broker to wait for all replicas to acknowledge the save before telling you "Success."
2.  **`enable.idempotence=true`:** Adds a sequence number to every message. If the network fails and you retry, the broker detects the duplicate and discards it.

**This exercise should take approximately 20 minutes to complete.**


-----

## **Step 1: Open the Skeleton**

1.  Navigate to `lab-03-kafka-basics/src/main/java/com/course/kafka/ReliableProducerApp.java`.
2.  Open the file.

## **Step 2: The Imports**

We need the standard producer tools.

**Action:** Paste this snippet at the top of the file (replace `// TODO: Paste Imports Here`):

```java
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
```

## **Step 3: The "Safety" Configuration**

Here is where the magic happens. We will set the `ACKS_CONFIG` to `all` (durability) and enable `IDEMPOTENCE` (consistency).

**Action:** Paste this inside the `main()` method (replace `// TODO: Configure the Reliable Producer`):

```java
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // === RELIABILITY SETTINGS ===
        
        // 1. Durability: Wait for ALL replicas to save the data
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        
        // 2. Consistency: Ensure exactly-once semantics (no duplicates)
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        
        // 3. Resilience: Retry forever until the timeout
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
```

## **Step 4: The Sending Loop**

The sending logic remains the same. The reliability is hidden in the configuration, not the code.

**Action:** Paste this below the configuration (replace `// TODO: Send Data`):

```java
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            for (int i = 0; i < 10; i++) {
                String key = "id-" + i;
                String value = "Critical-Data-" + i;

                ProducerRecord<String, String> record = new ProducerRecord<>("reliable-topic", key, value);

                producer.send(record, (meta, e) -> {
                    if (e != null) {
                        LOG.error("Send Failed", e);
                    } else {
                        LOG.info("Persisted: Offset={} | Acks=ALL", meta.offset());
                    }
                });
                
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
```

## **Step 5: Run and Analyze**

1.  Click **Run** on `ReliableProducerApp.java`.
2.  **Analyze the Output:**
      * You will see the `Persisted` logs.
      * **Key Takeaway:** To your Java code, nothing changed. But under the hood, Kafka is now performing a much heavier "handshake" for every message to ensure zero data loss.