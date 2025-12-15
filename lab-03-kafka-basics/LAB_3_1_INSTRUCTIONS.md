Here is the updated introduction for Lab 3.1.

# Lab 3.1: The Partition-Aware Producer

How does Kafka handle millions of events per second without crashing? The secret lies in how it physically splits data into parallel buckets. In Lab 3.1, you will uncover this mechanism by building a "Naked" Java Producer for a **Stock Trading Platform**, using the low-level `kafka-clients` library to manipulate the cluster directly. Your goal is to simulate a stream of trades and visually verify the physics of **Data Locality**. Through the logs, you will witness how the Broker hashes your message **Keys** to route traffic into specific **Partitions**, and how **Offsets** act as the immutable sequence numbers that ensure your data is not just sent, but safely persisted in order.

**This exercise should take approximately 30 minutes to complete.**

-----

## **Step 1: Open the Skeleton**

1.  Open `lab-03-kafka-basics/src/main/java/com/course/kafka/ProducerApp.java`.
2.  **Observe:** It is currently empty except for the `main` method.

## **Step 2: The Imports**

We need to import the Kafka Client library.

  * **The Concept:** Notice we import `clients.producer.*` (the client API) and `common.serialization.*` (shared utilities). Kafka only deals in bytes, so we need tools to convert our Strings.

**Action:** Paste this snippet at the top of the file (replace `// TODO: Paste Imports Here`):

```java
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;
```

## **Step 3: The Handshake (Configuration)**

We must tell the client where the Broker is and how to "speak" to it.

  * **The Concept:**
      * `BOOTSTRAP_SERVERS`: The initial connection point (`kafka:9092`).
      * `SERIALIZER`: Defines how to turn the Key and Value into byte arrays.

**Action:** Paste this inside `main()` (replace `// TODO: Configure the Producer`):

```java
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
```

## **Step 4: The Structure (The Loop)**

We will simulate a stream of stock prices.

  * **The Concept:** We deliberately send the same stock symbol (`AAPL`) multiple times. We want to prove that Kafka groups them together.

**Action:** Paste this below the configuration (replace `// TODO: Send Data`):

```java
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            // We send different keys to see them land on different Partitions
            String[] tickers = {
                "AAPL", "GOOGL", "MSFT", "TSLA", "AMZN", "NFLX", 
                "META", "NVDA", "ORCL", "IBM", "INTC", "AMD", 
                "UBER", "ABNB", "PYPL", "AAPL", "GOOGL", "MSFT"
            };

            String[] prices = {
                "150", "2800", "300", "750", "3300", "600", 
                "320", "220", "90", "140", "50", "110", 
                "45", "170", "270", "155", "2810", "305"
            };

            for (int i = 0; i < tickers.length; i++) {
                String key = tickers[i];
                String value = prices[i];
                
                // TODO: Create Record & Send
                
                // Small sleep to keep the logs readable
                Thread.sleep(500); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
```

## **Step 5: The Payload (Async Send)**

Now we construct the atomic unit of Kafka: The Record.

  * **The Concept:** `ProducerRecord` consists of `Topic` + `Key` + `Value`.
      * **The Key** is crucial. Kafka hashes the key ("AAPL") to assign it a consistent Partition ID.
  * **Async Callback:** Sending is asynchronous. We pass a lambda function `(meta, e) -> ...` that runs *later* when the Broker acknowledges receipt.

**Action:** Paste this **inside the `for` loop** (replace `// TODO: Create Record & Send`):

```java
                ProducerRecord<String, String> record = new ProducerRecord<>("stock-prices", key, value);

                // Send and wait for the "Ack" (Metadata) to print the proof
                producer.send(record, (meta, e) -> {
                    if (e != null) {
                        LOG.error("❌ Error sending", e);
                    } else {
                        LOG.info("✅ Sent: Key={} | Partition={} | Offset={}", 
                            key, meta.partition(), meta.offset());
                    }
                });
```

-----
## **Step 6: Update Kafka Partition**

1. Run the following command to clear any default partition and create 3 partition
    ```bash
    # 1. DELETE the old 1-partition topic (to clear out all existing data)
    docker exec kafka-broker /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --delete --topic stock-prices

    # 2. CREATE the new topic with 3 Partitions (3 Lanes)
    docker exec kafka-broker /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --topic stock-prices --partitions 3 --replication-factor 1
    ```
    ----
## **Step 7: Run and Analyze**

1.  Click **Run** on `ProducerApp.java`.
2.  **Analyze the Output:**

**Look for the Pattern:**

  * Does `Key=AAPL` always have the **same Partition ID** (e.g., Partition 0)?
  * Does `Key=MSFT` go to a **different Partition**?
  * Do the **Offsets** increase (0 -\> 1) for the same key?

**What this proves:**

  * **Scalability:** Kafka splits the workload. `AAPL` and `MSFT` live in different buckets.
  * **Ordering:** Within `Partition 0`, the offset ensures `AAPL` trades remain in order (Trade 1 before Trade 2).


  Here are the condensed context paragraphs for Steps 2, 3, and 4 (approx. 50% shorter).



## **Step 3: The Handshake (Configuration)**
We configure the client with two essentials: **Bootstrapping** and **Serialization**. First, we point to `kafka:9092`, which acts as the entry door to the entire cluster. Second, we set the Serializers to `StringSerializer`. This teaches the client exactly how to convert our Keys and Values into the byte format that the Broker expects for storage.

## **Step 4: The Structure (The Loop)**
To demonstrate scalability, we need a stream of data, not just one message. We will loop through an array of stock prices, deliberately sending the same symbol (`AAPL`) multiple times. This allows us to verify **Data Locality**: visual proof that Kafka consistently routes the exact same Key to the exact same Partition (lane) every time to preserve order.