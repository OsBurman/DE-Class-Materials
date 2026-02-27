# Java Exercises — Complete Curriculum

A comprehensive set of 12 Java exercises that take you from the fundamentals all the way to building a full, multi-class application. Each exercise is a self-contained Java program with starter code (with TODO comments) and a complete solution.

---

## Exercise Index

| # | Exercise | Key Topics | Difficulty |
|---|----------|------------|------------|
| 01 | [Java Fundamentals](./Exercise-01-Java-Fundamentals/) | Variables, data types, operators, control flow, loops | ⭐ Beginner |
| 02 | [Methods & Arrays](./Exercise-02-Methods-and-Arrays/) | Methods, overloading, 1D/2D arrays, sorting | ⭐ Beginner |
| 03 | [OOP Part 1 — Classes & Encapsulation](./Exercise-03-OOP-Classes-and-Encapsulation/) | Classes, constructors, encapsulation, `this`, `toString` | ⭐⭐ Beginner+ |
| 04 | [OOP Part 2 — Inheritance & Polymorphism](./Exercise-04-OOP-Inheritance-and-Polymorphism/) | Inheritance, abstract classes, interfaces, polymorphism | ⭐⭐ Intermediate |
| 05 | [Collections & Generics](./Exercise-05-Collections-and-Generics/) | ArrayList, HashMap, HashSet, Comparable, Comparator, Generics | ⭐⭐ Intermediate |
| 06 | [Exception Handling](./Exercise-06-Exception-Handling/) | try/catch/finally, custom exceptions, try-with-resources | ⭐⭐ Intermediate |
| 07 | [File I/O](./Exercise-07-File-IO/) | FileReader/Writer, BufferedReader, NIO Files API, serialization | ⭐⭐⭐ Intermediate+ |
| 08 | [Lambdas & Streams](./Exercise-08-Lambdas-and-Streams/) | Lambdas, functional interfaces, Stream API, Optional | ⭐⭐⭐ Intermediate+ |
| 09 | [Multithreading](./Exercise-09-Multithreading/) | Thread, Runnable, synchronized, ExecutorService, CompletableFuture | ⭐⭐⭐ Advanced |
| 10 | [Design Patterns](./Exercise-10-Design-Patterns/) | Singleton, Factory, Observer, Builder, Strategy | ⭐⭐⭐ Advanced |
| 11 | [Generics & Data Structures](./Exercise-11-Generics-and-Data-Structures/) | Generic classes, bounded types, wildcards, Stack, Queue, BST | ⭐⭐⭐ Advanced |
| 12 | [Full Application — Capstone](./Exercise-12-Full-Application/) | All concepts combined in a complete Bank Management System | ⭐⭐⭐⭐ Expert |

---

## How to Use These Exercises

### Folder Structure

Each exercise follows this pattern:
```
Exercise-XX-Name/
├── README.md          ← Instructions, context, and TODO checklist
├── starter-code/
│   └── src/           ← Incomplete Java files with TODO comments
└── solution/
    └── src/           ← Complete, fully working solution
```

### Running a Java File

**Compile and run (from the `src/` directory):**
```bash
cd Exercise-01-Java-Fundamentals/starter-code/src
javac Main.java
java Main
```

**Multi-file exercises:**
```bash
cd Exercise-03-OOP-Classes-and-Encapsulation/starter-code/src
javac *.java
java Main
```

### Java Version

These exercises are written for **Java 17+**. You can verify your version:
```bash
java --version
```

---

## Learning Path

```
Fundamentals (Ex 01-02)
        ↓
OOP Core (Ex 03-04)
        ↓
Collections & Error Handling (Ex 05-06)
        ↓
I/O & Modern Java (Ex 07-08)
        ↓
Concurrency & Patterns (Ex 09-10)
        ↓
Advanced Generics (Ex 11)
        ↓
Capstone Project (Ex 12)
```

---

## Tips for Students

- **Read the README** for each exercise before touching any code.
- Work through the **TODOs in order** — they build on each other.
- **Don't peek at the solution** until you've made a genuine attempt.
- Every starter file compiles — your job is to fill in the logic.
- Run the program after each TODO to verify your progress.
