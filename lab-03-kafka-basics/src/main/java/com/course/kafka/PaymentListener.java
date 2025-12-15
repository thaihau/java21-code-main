package com.course.kafka;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO 1: Uncomment Spring Imports
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;

// TODO 2: Uncomment Annotation
// @Service
public class PaymentListener {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentListener.class);
    
    private Set<String> processedIds = new HashSet<>();

    // TODO 3: Add Idempotent Listener Method
    
}