## 👩‍💻 For Participants

Follow these steps to start coding immediately — no setup required beyond VS Code and Docker.

### Option 1 — Using GitHub Codespaces (Recommended)

1. Visit **[https://github.com/richard-learning/java21-code](https://github.com/richard-learning/java21-code)**
2. Click the green **“<> Code”** button → **“Open with Codespaces”** → **“New codespace”**
3. Wait ~1 minute for your environment to start
4. Verify Java:

   ```bash
   java -version
   ```
5. Run the sample:

   ```bash
   mvn exec:java -Dexec.mainClass=Hello
   ```

### Option 2 — Using Local VS Code + Docker

1. Install **Docker Desktop** and ensure it’s running
2. Install **Visual Studio Code**
3. Install the **Dev Containers** extension
4. Clone this repo:

   ```bash
   git clone https://github.com/richard-learning/java21-code.git
   ```
5. Open the folder in VS Code
6. Press **F1 → Dev Containers: Reopen in Container**
7. Once loaded, confirm Java:

   ```bash
   java -version
   ```
8. Build & run:

   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass=Hello
   ```

### ✅ You’re Ready!

Your Java 21 development container is now running.
Start adding classes in `src/main/java/` and rerun with Maven as needed.


