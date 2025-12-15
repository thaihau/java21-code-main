# 🧩 Advanced Java & AI Workshop

A ready-to-use **Java 21** development container powered by `ghcr.io/richard-learning/java21-base:stable`.

This workspace is pre-configured for **Advanced Java**, **Microservices (Kafka/Postgres)**, and **AI Integration (MCP)**. Now fully **multi-platform** — supports both **AMD64** (PC) and **ARM64** (Apple Silicon / M-series Macs).

-----

## 🚀 Quick Start

### Option A: From Zip (Classroom Standard)

1.  **Install** Docker Desktop.
2.  **Install** VS Code and the **Dev Containers** extension.
3.  **Get** the zipped repository from your trainer.
4.  **Unzip** the folder to your desktop.
5.  **Open** the folder in VS Code.
6.  Press **F1 → Dev Containers: Reopen in Container**.
    *(This will download Java 21, Node.js, and start Kafka/Postgres automatically.)*

### Option B: Your Own Repo (Optional)

If you want to save your progress to GitHub:

1.  Open Terminal (`Ctrl` + `` ` ``).
2.  Run these commands:
    ```bash
    git init
    git add .
    git commit -m "Lab setup"
    ```
3.  Create a **new empty repository** on your GitHub.
4.  Link and push:
    ```bash
    git remote add origin <YOUR_NEW_REPO_URL>
    git branch -M main
    git push -u origin main
    ```

### ✅ Verification

Once the container loads, verify the environment:

```bash
# 1. Check Java Version (Should be 21)
java -version

# 2. Check Node.js (Required for Lab 05)
npx --version

# 3. Check Services (Kafka & Postgres)
docker ps
```

-----

## 📚 Lab Modules

| Module | Description | Key Tech |
| :--- | :--- | :--- |
| **01** | `lab-01-effective-java` | Generics, Functional Programming, Concurrency |
| **02** | `lab-02-modern-java` | Record, Sealed classes, virtual thread |
| **03** | `lab-03-kafka-basics` | Kafka Producers/Consumers, Spring Kafka |
| **04** | `lab-04-microservices` | Spring Boot, JPA, Postgres & Kafka Integration |
| **05** | `lab-05-mcp` | Model Context Protocol (MCP) Client |

-----

## 🧰 Pre-installed VS Code Extensions

| Category | Extension | ID |
| :--- | :--- | :--- |
| Java Pack | Extension Pack for Java | `vscjava.vscode-java-pack` |
| Java Tools | Debugger for Java | `vscjava.vscode-java-debug` |
| Java Tools | Test Runner for Java | `vscjava.vscode-java-test` |
| Utilities | YAML Support | `redhat.vscode-yaml` |
| Docker | Docker Support | `ms-azuretools.vscode-docker` |

*(All included automatically via `devcontainer.json`.)*

-----

## 🗂️ Folder Structure

```text
java21-code/
 ├─ .devcontainer/
 │   └─ devcontainer.json    # Configures Java 21 + Node + Kafka
 ├─ .vscode/
 ├─ lab-01-effective-java/   # Module 1 Code
 ├─ lab-02-modern-java/      # Module 2 Code
 ├─ lab-03-kafka-basics/     # Module 3 Code
 ├─ lab-04-microservices/    # Module 4 Code
 ├─ lab-05-mcp/              # Module 5 Code
 ├─ docker-compose.yml       # Definitions for Kafka & Postgres
 ├─ pom.xml                  # Root Maven Build File
 ├─ .gitignore
 └─ README.md
```

-----

## 🛠️ Build & Run

To build all labs at once to ensure your environment is healthy:

```bash
mvn clean install -DskipTests
```

To run a specific lab (e.g., Lab 05):

```bash
cd lab-05-mcp
mvn spring-boot:run
```

-----