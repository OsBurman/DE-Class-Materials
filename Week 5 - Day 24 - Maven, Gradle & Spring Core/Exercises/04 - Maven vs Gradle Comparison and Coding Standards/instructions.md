# Exercise 04: Maven vs Gradle Comparison and Coding Standards

## Objective
Compare Maven and Gradle across key dimensions and apply Java coding standards (naming conventions, package structure, documentation) to a poorly written class.

## Background
Choosing a build tool is a real architectural decision in every project. Maven and Gradle take fundamentally different approaches: declarative XML vs programmatic DSL. In addition to build tools, professional Java code must follow consistent naming conventions and documentation standards so that teams can read, review, and maintain code written by anyone.

## Requirements

### Part 1 — Maven vs Gradle Comparison Table
Complete the comparison table in `worksheet.md`. For each dimension, fill in both the Maven approach and the Gradle approach.

Dimensions to compare:
1. Configuration language/format
2. How dependencies are declared (syntax example for adding a dependency)
3. Build model (lifecycle phases vs task graph)
4. Default project structure
5. Performance (incremental builds, build cache, daemon)
6. Flexibility (can you add custom logic easily?)
7. IDE support
8. Best suited for (typical use cases)

### Part 2 — When to Choose Which
Answer the three scenario questions in `worksheet.md`:
1. You are starting a new Android mobile app. Which tool do you choose and why?
2. You are joining an existing enterprise Spring Boot project that already uses Maven. Should you migrate to Gradle? Why or why not?
3. Your build has complex, conditional logic (e.g., "if this environment variable is set, run a custom post-processing step"). Which tool handles this more naturally?

### Part 3 — Fix the Coding Standards Violations
The file `starter-code/BadlyWrittenCode.java` contains a class with **8 deliberate violations** of Java coding standards. Find and fix all 8 in `solution/WellWrittenCode.java`. For each fix, add a brief comment explaining what rule was violated.

**Violations to find:**
1. Class name violates naming convention
2. Instance variable name violates naming convention
3. Constant name violates naming convention
4. Method name violates naming convention
5. Local variable name is a single letter (not a loop counter)
6. Missing Javadoc on the public class
7. Missing Javadoc on a public method
8. Magic number used without a named constant

## Hints
- Java class names use `UpperCamelCase`; method and variable names use `lowerCamelCase`; constants use `SCREAMING_SNAKE_CASE`.
- Package names are all lowercase with dots: `com.library.service`
- Javadoc uses `/** ... */` above the class and each public method
- A "magic number" is a numeric literal embedded directly in logic (e.g., `if (score > 60)`) with no named constant explaining its meaning
- For the comparison table, think about what you *wrote* in Exercises 01–03

## Expected Output

The fixed class, when compiled and run, prints the same output as the broken class — the fixes are cosmetic (naming + docs), not functional:

```
Book title: Clean Code
Late fee: $1.50
Status: OVERDUE
```
