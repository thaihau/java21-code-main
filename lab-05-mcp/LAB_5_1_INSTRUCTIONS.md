# Lab 05: Building a Local MCP Client

### **The Scenario**
You are building an AI Coding Assistant that needs to analyze your project's `pom.xml`. Instead of hardcoding a file reader, you will use the **Model Context Protocol (MCP)**. This allows your agent to ask a standardized "Server" to read the file. Today it reads from a local disk; tomorrow, it could be a secure GitHub repo, with zero code changes.

### **Why MCP Matters**
AI is shifting from "Chatbots" to "Agents" that take action. Currently, connecting AI to data (Slack, Databases, Files) requires custom code for every service.
**MCP is the "USB-C port" for AI.** It standardizes these connections. You build a data connector once, and it instantly works with any MCP-compliant AI client (Spring AI, Claude, IntelliJ, etc.).

**Objective**
In this lab, you will build a Spring Boot **MCP Client**. You will spawn a local **Node.js MCP Server** (the official Filesystem Server) and command it to read your `pom.xml` using the standardized `read_text_file` tool.

**Estimated Duration:** 30 Minutes


-----

## **Part 1: Setup & Imports**

Open `src/main/java/com/richardlearning/mcp/McpClientApplication.java`.

**1. Find this TODO:**

```java
// TODO: Add necessary MCP imports here during the lab
```

**2. Add this code:**
Copy and paste the following imports to bring in the official Java SDK for MCP.

```java
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper; 
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
```

**3. What are these?**

  * **`McpClient`**: The main entry point. It manages the connection and sends requests to the server.
  * **`StdioClientTransport`**: The "pipe" that connects your Java app to the Node.js process using standard input/output.
  * **`McpJsonMapper`**: Critical for translating Java objects into the JSON format that the MCP server understands.
  * **`McpSchema.*`**: These are the standard data structures (Requests, Results, Content) defined by the protocol.

## **Part 2: Configuring the Server**

We need to tell our Java application how to start the MCP server. We will use `npx` (Node Package Execute) to download and run the official filesystem server on the fly.

**Action:** Locate `// STEP 1` and add this code:

```java
// We use 'npx' to auto-install and run the Node.js server immediately.
var serverParams = ServerParameters.builder("npx")
        .args(List.of("-y", "@modelcontextprotocol/server-filesystem", "."))
        .build();
```

  * **`npx`**: The command to run.
  * **`-y`**: "Yes", auto-confirm installation.
  * **`.`**: The argument telling the server it is allowed to access the current directory.

-----

## **Part 3: Initializing the Transport**

The "Transport" is the bridge between Java and the Node process. It pipes the Input/Output streams so they can talk.

**Action:** Locate `// STEP 2` and initialize the transport.

  * **Crucial:** You must pass `McpJsonMapper.getDefault()` as the second argument, or the transport won't know how to read the JSON messages.

<!-- end list -->

```java
var transport = new StdioClientTransport(serverParams, McpJsonMapper.getDefault());
```

-----

## **Part 4: Connecting the Client**

Now we wrap the transport in the high-level `McpClient`. We use a **Synchronous** client because it is easier to write and debug for CLI tools.

**Action:** Locate `// STEP 3` and add this block:

```java
var client = McpClient.sync(transport)
        .requestTimeout(Duration.ofSeconds(10))
        .build();

// Handshake with the server
client.initialize();
```

-----

## **Part 5: Tool Discovery**

Before we ask the server to do work, let's see what it is capable of. The `listTools()` method retrieves the catalog of available functions.

**Action:** Locate `// STEP 4` and add this loop:

```java
System.out.println("\n🛠️  Tools Discovery:");
client.listTools().tools().forEach(tool -> 
    System.out.println("   - " + tool.name() + ": " + tool.description())
);
```

-----

## **Part 6: Executing a Tool**

Now for the main event. We will use the `read_text_file` tool to read our own `pom.xml`. Locate the `// STEP 5` section inside the `try` block.

**Step 6.1: Create the Request**
First, we must construct a formal request object. This tells the server exactly which tool we want (`read_text_file`) and provides the necessary arguments (`path`).

**Action:** Replace `// TODO: Create a CallToolRequest...` with:

```java
var readRequest = new CallToolRequest(
    "read_text_file", 
    Map.of("path", "pom.xml")
);
```

  * **`read_text_file`**: We use this instead of the deprecated `read_file`.
  * **`Map.of(...)`**: Creates the dictionary of arguments required by the tool.

**Step 6.2: Call the Server**
Now we send the request over the transport. Since our client is synchronous, this line will block (wait) until the Node.js server finishes reading the file and sends the data back.

**Action:** Replace `// TODO: Execute the tool...` with:

```java
CallToolResult result = client.callTool(readRequest);
```

**Step 6.3: Process the Result**
The server returns a generic list of content (it could be text, images, or binary). We need to safely check if we got text back and then print it.

**Action:** Replace `// TODO: Parse and print...` with:

```java
var contentList = result.content();

// Java 16+ Pattern Matching: Checks if it's TextContent and casts it to variable 'text'
if (!contentList.isEmpty() && contentList.get(0) instanceof TextContent text) {
    String content = text.text();
    
    System.out.println("--- CONTENT PREVIEW ---");
    // We limit the output to 200 chars to keep the terminal clean
    int previewLength = Math.min(content.length(), 200);
    System.out.println(content.substring(0, previewLength) + "...");
    System.out.println("--- END PREVIEW ---");
} else {
    System.out.println("⚠️  No text content returned.");
}
```

**Step 6.4: Clean Up**
Finally, we must ensure the `finally` block closes the connection. This is critical because it sends a signal to the Node.js process to shut down. If you skip this, you might leave "zombie" node processes running in the background.

**Action:** Replace `// TODO: Close the client connection` with:

```java
client.close();
```
---

## **Part 7: Verification**

1.  Save the file.
2.  Run the application using the VS Code "Run" button or `mvn spring-boot:run`.

**Expected Output:**

```text
🚀 Starting MCP Client Lab...
✅ Connected to MCP Server!

🛠️  Tools Discovery:
   - read_text_file: Read the complete contents of a file...
   - write_file: Create a new file...
   ...

📖  Reading 'pom.xml'...
--- CONTENT PREVIEW ---
<project xmlns="http://maven.apache.org/POM/4.0.0"...
--- END PREVIEW ---
```