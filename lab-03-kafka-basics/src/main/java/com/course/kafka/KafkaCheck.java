package com.course.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
// import org.slf4j.Logger;         <-- Remove/Comment these
// import org.slf4j.LoggerFactory;  <-- Remove/Comment these
import java.util.Properties;

public class KafkaCheck {
    // private static final Logger LOG = LoggerFactory.getLogger(KafkaCheck.class); <-- Remove

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");

        try (AdminClient client = AdminClient.create(props)) {
            String clusterId = client.describeCluster().clusterId().get();
            // USE SYSTEM.OUT instead of LOG.info
            System.out.println("✅ KAFKA IS ALIVE! Cluster ID: " + clusterId);
        } catch (Exception e) {
            System.err.println("❌ KAFKA CONNECTION FAILED.");
            e.printStackTrace();
        }
    }
}