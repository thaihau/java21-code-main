# Lab 3.5: The Spring Upgrade

We have spent the last hour doing things the "Hard Way" (Manual Props, Manual Serializers, Manual Loops). This was necessary to understand the engine. Now, we upgrade to the "Easy Way."

In this lab, you will convert your plain Java project into a modern **Spring Boot Application**. You will see how 50 lines of boilerplate code vanish, replaced by Spring's auto-configuration and the powerful `KafkaTemplate`.

**This exercise should take approximately 20 minutes to complete.**

-----

## **Step 1: The Dependency Swap**

Spring Boot manages dependencies differently. We don't need to manually list `kafka-clients` or `slf4j` anymore. Spring provides a "Starter" that bundles everything together.

1.  Open `lab-03-kafka-basics/pom.xml`.
2.  **Delete** the entire existing `<dependencies>` block (approx lines 15-27).
3.  **Paste** this new block in its place:

<!-- end list -->

```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
</dependencies>
```

4.  **Important:** Right-click inside the `pom.xml` editor and select **"Reload Projects"** (or click the sync icon) to download the new JARs.

## **Step 2: The Configuration Upgrade**

In the old code, we hardcoded `Properties` in Java. In Spring, we externalize this to a file.

1. Navigate to `src/main/resources`.
2. Rename  `application.yml.bak` to `application.yml`
3. Rename  `simplelogger.properties` to `simplelogger.properties.bak`
4. Run the clean command again to flush out the old settings on `lab-03-kafka-basics`:
    ```bash
        mvn clean compile
    ```
---

## **Step 3: Upgrading to Spring Boot**

In this exercise, you will convert a plain Java class into a fully managed Spring Boot application. Open `src/main/java/com/course/kafka/SpringProducerApp.java` and follow the steps below to complete the TODOs.

**1: Add Required Imports**

Locate `//TODO 1` and paste the following imports to bring in the Spring Boot and Kafka libraries:

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

```

**Step 2: Configure the Application Class**

Locate `//TODO 2`. You need to do two things here:

1. Add the `@SpringBootApplication` annotation.
2. Update the class definition to implement the `CommandLineRunner` interface (this allows us to run code on startup).

**Replace:**

```java
//TODO2: add necessary Spring Boot annotations
public class SpringProducerApp {

```

**With:**

```java
@SpringBootApplication
public class SpringProducerApp implements CommandLineRunner {

```

**Step 3: Inject the Kafka Template**

Locate `//TODO 3`. Instead of manually creating properties and producers, we will let Spring inject a pre-configured `KafkaTemplate`.

**Paste the following:**

```java
    // Magic: Spring creates the connection and injects it here
    @Autowired
    private KafkaTemplate<String, String> template;

```

**Step 4: Add the Main Method**

Locate `//TODO 4`. Spring Boot applications need a standard entry point. Add the `main` method directly after the `template` declaration:

```java
    public static void main(String[] args) {
        SpringApplication.run(SpringProducerApp.class, args);
    }

```

**Step 5: Send the Message**

Locate `//TODO 5`. This is where we implement the `run` method from the `CommandLineRunner` interface. Notice how much simpler the sending logic is compared to the plain Java driver.

**Paste the following method:**

```java
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Starting Spring Boot Producer ===");
        
        // Notice: No Properties, No Serializers, No Future.get()
        // Just one line to send.
        
        String topic = "stock-prices";
        String key = "TESLA";
        String value = "2500.00";

        // We use the simpler .send() method
        var result = template.send(topic, key, value).get();

        System.out.println(" Sent via Spring: " + result.getRecordMetadata());
    }

```


## **Step 4: Run and Verify**

1.  Click **Run** on `SpringProducerApp.java`.
2.  **Observation:** You will see the Spring Boot banner (ASCII art) start up, followed by the log:
    ` Sent via Spring: stock-prices-0@...`
3.  **Success:** You have successfully migrated your stack to Spring Boot\!