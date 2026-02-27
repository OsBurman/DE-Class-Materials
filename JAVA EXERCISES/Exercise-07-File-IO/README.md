# Exercise 07 — File I/O

## Overview

Build a **Todo List Manager** with full file persistence. The app saves and loads todo items using multiple I/O strategies so you can compare each approach.

---

## Concepts Covered

- `FileReader` / `FileWriter` (character streams)
- `BufferedReader` / `BufferedWriter` (buffered wrappers)
- Reading/writing CSV files line by line
- NIO2 API: `Path`, `Files.writeString()`, `Files.readString()`, `Files.write()`
- Object serialization: `ObjectOutputStream` / `ObjectInputStream`
- `Serializable` interface and `serialVersionUID`
- Append mode with `FileWriter(file, true)`
- Exception handling for file operations

---

## TODOs

- [ ] **TODO 1** — Implement `TodoItem.fromCsv(String line)` — parse CSV line into object
- [ ] **TODO 2** — Implement `saveToCsv(List<TodoItem>, String)` — write CSV with header
- [ ] **TODO 3** — Implement `loadFromCsv(String)` — read and parse CSV, skip header
- [ ] **TODO 4** — Implement `saveToTextReport(List<TodoItem>, String)` — NIO Files API
- [ ] **TODO 5** — Implement `serializeItems` / `deserializeItems` — binary serialization
- [ ] **TODO 6** — Implement `appendToLog(String, String)` — append-mode file writing

---

## Running the Program

```bash
cd starter-code/src
javac Main.java
java Main
```

The program will create `todos.csv`, `todos_report.txt`, `todos.ser`, and `todos.log` in the current directory.
