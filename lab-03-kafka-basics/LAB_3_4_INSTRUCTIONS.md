# Lab 3.4: Manual Offsets (The "At-Least-Once" Bug)


By default, Kafka Consumers operate on "Auto-Pilot." They automatically commit their offsets every few seconds, assuming that if no error occurred, the messages were successfully processed. But what happens if your application crashes *after* doing the work (e.g., charging a credit card) but *before* the offset is committed?

In Lab 3.4, you will take control of the "Stick Shift." You will disable Auto-Commit and deliberately introduce a scenario where the consumer processes data but fails to "save its game." This visually demonstrates **At-Least-Once Delivery**: the guarantee that Kafka will never lose your message, but might deliver it twice if you aren't careful.

**This exercise should take approximately 20 minutes to complete.**

-----

## **Step 1: Enable the Base Code**

1.  Navigate to `lab-03-kafka-basics/src/main/java/com/course/kafka/DeliverySemanticsApp.java`.
2.  Open the file. You will see a fully written Consumer application that is currently commented out.
3.  **Action:** Highlight the code block inside `main()` and uncomment it (Select the lines and press `Ctrl + /` or `Cmd + /`).
4.  **Sanity Check:** Run the app. It should connect to the broker and sit idle (or consume existing messages if any represent). Stop the app.

## **Step 2: Disable Auto-Pilot**

To control the delivery semantics, we must first tell Kafka to stop auto-saving our position.

**Action:** Paste this line into the configuration block (replace `// TODO 2: Disable Auto-Commit`):

```java
        // Snippet A: Disable Auto-Commit
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
```

## **Step 3: The "Groundhog Day" Bug**

Now that Auto-Commit is off, we have created a dangerous situation. We are reading messages, but we are never telling the broker "I am done."

1.  **Run the Producer:** Go to `ProducerApp.java` and click **Run** to send a fresh batch of stock prices.
2.  **Run the Consumer:** Go to `DeliverySemanticsApp.java` and click **Run**.
      * *Observation:* You will see the logs: `Processing: Key=AAPL...`.
3.  **The Crash (Simulated):** Stop the Consumer manually (Red Square button).
4.  **The Restart:** Click **Run** on `DeliverySemanticsApp.java` again.
      * *Observation:* **It reads the exact same messages again\!**
      * *Why:* Because we processed them, but never committed the offset. Kafka thinks we failed, so it sends them again. This is **At-Least-Once** behavior.

## **Step 4: The Fix (Manual Commit)**

To fix this, we must explicitly tell Kafka when we are safe. We will use `commitSync()` to force a "Save Game" after every batch of messages.

**Action:** Paste this line **after** the `for` loop closes (replace `// TODO 3: Manual Commit`):

```java
                // Snippet B: Commit the Offset
                consumer.commitSync();
                LOG.info("✅ Offsets Committed");
```

## **Step 5: Verify the Fix**

1.  **Run the Producer:** Send one more batch of data.
2.  **Run the Consumer:** It processes the new batch and prints `✅ Offsets Committed`.
3.  **Restart the Consumer:** Stop it and run it again.
      * *Observation:* It remains silent. It remembers where it left off. You have successfully implemented **Manual Offset Management**.