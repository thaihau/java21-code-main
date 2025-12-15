## 🧑‍🏫 For Trainers

This section helps trainers verify, reset, and prepare the environment before a class session.

### 🧩 1. Verify Container Health

Run these commands inside the container to confirm everything works:

```bash
java -version
mvn -v
```

Both should show valid Java 21 and Maven versions.

---

### 🔁 2. Reset the Environment

If a participant’s environment becomes unstable:

```bash
# Rebuild container from VS Code Command Palette
F1 → Dev Containers: Rebuild Container
```

or

```bash
# Remove build artifacts
rm -rf target
```

---

### 🧱 3. Validate Folder Structure

Ensure the following before each session:

```
src/main/java/Hello.java
pom.xml
.devcontainer/devcontainer.json
.vscode/settings.json
```

---

### 🚀 4. Run a Sanity Check

```bash
mvn compile
mvn exec:java -Dexec.mainClass=Hello
```

Expected output:

```
Hello from devcontainer!
```

---

### 💡 5. Tips

* Keep your GHCR image (`ghcr.io/richard-learning/java21-base:stable`) up-to-date.
* For Codespaces-based classes, prebuild once using:

  ```bash
  gh codespace create -r richard-learning/java21-code
  ```

  so participants load faster.
* Encourage students to **fork** the repo before editing to avoid permission issues.
