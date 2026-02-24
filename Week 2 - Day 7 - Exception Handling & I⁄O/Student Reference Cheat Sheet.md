# Day 7 — Exception Handling & I/O
## Quick Reference Guide

---

## 1. Exception Hierarchy

```
java.lang.Throwable
├── Error             (JVM-level; do NOT catch — OutOfMemoryError, StackOverflowError)
└── Exception
    ├── RuntimeException    (Unchecked — compiler does NOT require handling)
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   ├── ClassCastException
    │   ├── IllegalArgumentException
    │   ├── IllegalStateException
    │   └── ArithmeticException
    └── (Checked — compiler REQUIRES try-catch or throws declaration)
        ├── IOException
        │   ├── FileNotFoundException
        │   └── EOFException
        ├── SQLException
        └── ParseException
```

| | Checked | Unchecked |
|---|---|---|
| Compiler enforces handling | ✅ Yes | ❌ No |
| Extends | `Exception` (not `RuntimeException`) | `RuntimeException` |
| Represents | Recoverable external conditions | Programming bugs |

---

## 2. try-catch-finally

```java
try {
    int result = 10 / 0;          // ArithmeticException thrown here
} catch (ArithmeticException e) {
    System.out.println("Math error: " + e.getMessage());
} catch (NullPointerException e) {
    System.out.println("Null ref: " + e.getMessage());
} finally {
    System.out.println("Always runs — use for cleanup");
}
```

**Rules:**
- `finally` always executes — even if `catch` re-throws or returns
- Exception, not: `System.exit()` and JVM crash skip `finally`
- Catch blocks are checked **top-to-bottom** — put specific exceptions before general ones
- Catching `Exception` catches all checked + unchecked (avoid unless intentional)

---

## 3. Multi-catch (Java 7+)

```java
try {
    // ...
} catch (IOException | SQLException e) {    // ← one handler, multiple types
    logger.error("Data error", e);
}
// ⚠️ Multi-catch variable is implicitly final — cannot reassign e
```

---

## 4. try-with-resources (Java 7+)

Automatically closes resources that implement `AutoCloseable` — no need for `finally`.

```java
// Single resource
try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}  // br.close() called automatically — even if exception thrown

// Multiple resources (closed in reverse declaration order)
try (
    Connection conn = DriverManager.getConnection(url);
    PreparedStatement ps = conn.prepareStatement(sql)
) {
    ps.executeQuery();
}
```

---

## 5. throw vs throws

```java
// throw — actually throws an exception (inside method body)
public void setAge(int age) {
    if (age < 0) throw new IllegalArgumentException("Age cannot be negative: " + age);
    this.age = age;
}

// throws — declares that this method MAY throw checked exceptions (in signature)
public String readFile(String path) throws IOException {
    return Files.readString(Path.of(path));
}
```

---

## 6. Custom Exceptions

```java
// Checked custom exception
public class InsufficientFundsException extends Exception {
    private final double amount;

    public InsufficientFundsException(double amount) {
        super("Insufficient funds: need " + amount + " more");
        this.amount = amount;
    }

    public double getAmount() { return amount; }
}

// Unchecked custom exception
public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);   // ← wrapping original exception (exception chaining)
    }
}

// Usage
try {
    account.withdraw(500.0);
} catch (InsufficientFundsException e) {
    System.out.println(e.getMessage());      // "Insufficient funds: need 200.0 more"
    System.out.println(e.getAmount());       // 200.0
}
```

**Best practices:**
- Prefer unchecked for programming errors; checked for recoverable I/O scenarios
- Always include a `String message` constructor and a `Throwable cause` constructor
- Use exception chaining (`super(message, cause)`) to preserve the original stack trace

---

## 7. Exception Best Practices

```java
// ✅ Catch specific exceptions
catch (FileNotFoundException e) { ... }

// ❌ Swallowing exceptions — hides bugs
catch (Exception e) { }

// ✅ Log with stack trace
catch (Exception e) { logger.error("Failed", e); }

// ❌ Just printing message — loses location info
catch (Exception e) { System.out.println(e.getMessage()); }

// ✅ Re-throw wrapped in domain exception
catch (SQLException e) { throw new DataAccessException("DB error", e); }
```

---

## 8. Classic File I/O (java.io)

```java
// Writing to a file
try (FileWriter fw = new FileWriter("output.txt");
     BufferedWriter bw = new BufferedWriter(fw)) {
    bw.write("Hello, World!");
    bw.newLine();
    bw.write("Second line");
}

// Reading from a file
try (FileReader fr = new FileReader("output.txt");
     BufferedReader br = new BufferedReader(fr)) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}

// Reading with Scanner (also handles user input)
try (Scanner scanner = new Scanner(new File("data.txt"))) {
    while (scanner.hasNextLine()) {
        System.out.println(scanner.nextLine());
    }
}
```

**Always wrap `FileReader`/`FileWriter` in `Buffered*` — dramatically improves performance.**

---

## 9. NIO.2 — Modern File API (java.nio.file)

```java
import java.nio.file.*;

Path path = Path.of("data", "users.txt");   // relative
Path abs  = Path.of("/home/user/data.txt"); // absolute
Path home = Paths.get(System.getProperty("user.home"), "docs");

// Read
String content  = Files.readString(path);                     // whole file as String (Java 11+)
List<String> lines = Files.readAllLines(path);                // all lines as List
byte[] bytes    = Files.readAllBytes(path);

// Write (overwrites by default)
Files.writeString(path, "Hello");
Files.write(path, lines);
Files.write(path, "append me\n".getBytes(), StandardOpenOption.APPEND);

// Stream lines lazily (good for large files)
try (Stream<String> stream = Files.lines(path)) {
    stream.filter(l -> l.startsWith("ERROR")).forEach(System.out::println);
}

// File/directory operations
Files.exists(path);
Files.isDirectory(path);
Files.createDirectories(path);          // mkdir -p equivalent
Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
Files.move(src, dest);
Files.delete(path);
Files.deleteIfExists(path);

// List directory contents
try (Stream<Path> entries = Files.list(Paths.get("."))) {
    entries.forEach(System.out::println);
}
```

---

## 10. Path Operations

```java
Path p = Path.of("/Users/alice/documents/report.pdf");

p.getFileName();       // report.pdf
p.getParent();         // /Users/alice/documents
p.getRoot();           // /
p.getNameCount();      // 4
p.getName(0);          // Users
p.toString();          // /Users/alice/documents/report.pdf

// Resolve (combine paths)
Path base = Path.of("/Users/alice");
Path full = base.resolve("documents/notes.txt");
// → /Users/alice/documents/notes.txt

// Relativize
Path rel = base.relativize(full);
// → documents/notes.txt

// Normalize (resolve . and ..)
Path normalized = Path.of("/a/b/../c").normalize();
// → /a/c
```

---

## 11. Quick Reference: When to Use What

| Scenario | Recommended API |
|----------|----------------|
| Read entire file to String | `Files.readString(path)` |
| Read all lines to List | `Files.readAllLines(path)` |
| Process large file line-by-line | `Files.lines(path)` in try-with-resources |
| Write text to file | `Files.writeString(path, text)` |
| User console input | `Scanner(System.in)` |
| High-performance I/O | `BufferedReader` / `BufferedWriter` |
| Binary data | `Files.readAllBytes` / `FileInputStream` + `BufferedInputStream` |
