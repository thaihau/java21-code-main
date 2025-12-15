package com.richardlearning.mcp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// TODO: Add necessary MCP imports here during the lab

import java.time.Duration;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class McpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("🚀 Starting MCP Client Lab...");

            // STEP 1: Configure the Server Parameters (npx command)
            // TODO: Create ServerParameters to run the filesystem server

            // STEP 2: Initialize the Transport
            // TODO: Create StdioClientTransport with the server parameters and default JSON mapper

            // STEP 3: Build and Connect the Client
            // TODO: Initialize a synchronous McpClient and call .initialize()

            System.out.println("✅ Connected to MCP Server!");

            // STEP 4: List Available Tools
            // TODO: Retrieve and print the list of available tools from the server

            // STEP 5: Execute a Tool (read_text_file)
            System.out.println("\n📖  Reading 'pom.xml'...");
            try {
                // TODO: Create a CallToolRequest for 'read_text_file' targeting 'pom.xml'

                // TODO: Execute the tool using the client and capture the result

                // TODO: Parse and print the TextContent from the result
                
            } catch (Exception e) {
                System.err.println("❌ Failed: " + e.getMessage());
            } finally {
                // TODO: Close the client connection
            }
        };
    }
}