package com.richardlearning.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.openfeign.EnableFeignClients; // Lab 4.2

// TODO: Uncomment EnableFeignClients in Lab 4.2
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}