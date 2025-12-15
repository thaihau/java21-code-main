package com.richardlearning.mcp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper; 
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class McpClientDemo {

    public static void main(String[] args) {
        SpringApplication.run(McpClientDemo.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("🚀 Starting MCP Client...");

            // =================================================================
            // OPTION B: LOCAL SERVER (Active for this Lab)
            // =================================================================
            var serverParams = ServerParameters.builder("npx")
                    .args(List.of("-y", "@modelcontextprotocol/server-filesystem", "."))
                    .build();

            var transport = new StdioClientTransport(serverParams, McpJsonMapper.getDefault());

            var client = McpClient.sync(transport)
                    .requestTimeout(Duration.ofSeconds(10))
                    .build();

            client.initialize();
            System.out.println("✅ Connected to MCP Server!");

            // List Available Tools
            System.out.println("\n🛠️  Tools Discovery:");
            client.listTools().tools().forEach(tool -> 
                System.out.println("   - " + tool.name() + ": " + tool.description())
            );

            // Execute the NEW Tool (read_text_file)
            System.out.println("\n📖  Reading 'pom.xml' using read_text_file...");
            try {
                // UPDATE: Changed "read_file" to "read_text_file"
                var readRequest = new CallToolRequest(
                    "read_text_file", 
                    Map.of("path", "pom.xml")
                );

                CallToolResult result = client.callTool(readRequest);
                
                var contentList = result.content();
                if (!contentList.isEmpty() && contentList.get(0) instanceof TextContent text) {
                    String content = text.text();
                    System.out.println("--- CONTENT PREVIEW ---");
                    int previewLength = Math.min(content.length(), 200);
                    System.out.println(content.substring(0, previewLength) + "...");
                    System.out.println("--- END PREVIEW ---");
                } else {
                    System.out.println("⚠️  No text content returned.");
                }

            } catch (Exception e) {
                System.err.println("❌ Failed to execute tool: " + e.getMessage());
            } finally {
                client.close();
            }
        };
    }
}