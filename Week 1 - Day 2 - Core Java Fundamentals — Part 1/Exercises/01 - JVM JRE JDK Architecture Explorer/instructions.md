# Exercise 01: JVM/JRE/JDK Architecture Explorer

## Objective
Explain the relationship between the JVM, JRE, and JDK by completing a conceptual analysis document and writing a short Java program that demonstrates where Java code "lives" at runtime.

## Background
Before writing a single line of Java, you need to understand the platform that runs it. The JDK, JRE, and JVM are three nested components — each one contains the previous. Developers install the JDK, the JRE bundles the runtime libraries, and the JVM is the engine that executes bytecode on the host machine.

## Requirements

1. In `JvmArchitecture.java`, add a `main` method that prints the following JVM runtime properties using `System.getProperty()`:
   - `java.version` — the Java version currently running
   - `java.vendor` — the JVM vendor
   - `java.home` — the path to the JRE installation
   - `os.name` — the operating system name
   - `user.dir` — the current working directory

2. In `architecture-notes.md`, fill in every `[YOUR ANSWER]` placeholder by answering the questions about JVM, JRE, and JDK.

3. In `JvmArchitecture.java`, after printing the system properties, print a separator line (`---`) and then print:
   - Whether the program is running on a 32-bit or 64-bit JVM. Use `System.getProperty("sun.arch.data.model")` to get this value.

4. Add a single-line comment above each `System.out.println` call explaining what property it prints.

## Hints
- `System.getProperty(String key)` returns a `String` — you can concatenate it directly in a `println`.
- The JDK *contains* the JRE; the JRE *contains* the JVM. Think of them as nested containers.
- The `java.home` property points to where the JRE is installed — this is different from where *your code* lives.
- You don't need to import anything extra; `System` is always available in Java.

## Expected Output
```
=== JVM Runtime Information ===
Java Version : 21.0.2
Java Vendor  : Eclipse Adoptium
Java Home    : /Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
OS Name      : Mac OS X
Working Dir  : /Users/yourname/project
---
JVM Architecture: 64-bit
```
*(Exact values will differ by machine — the format and labels must match.)*
