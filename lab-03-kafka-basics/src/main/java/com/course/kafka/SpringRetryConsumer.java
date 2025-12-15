package com.course.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO 1: Uncomment Imports
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;

// TODO 2: Uncomment Annotation
// @Service
public class SpringRetryConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(SpringRetryConsumer.class);

    // TODO 3: Uncomment The Listener Method
    /*
    @KafkaListener(topics = "stock-prices", groupId = "retry-group")
    public void consume(String message) {
        LOG.info("📥 Consuming: {}", message);

        // TODO 4: Add Poison Pill Logic
        if (message.contains("FAIL")) {
            throw new RuntimeException("🔥 Poison Pill Detected!");
        }
    }
    */
}