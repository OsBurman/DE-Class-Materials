# Week 2 - Day 10, Part 2: GC, File I/O, Serialization & Debugging
## 60-Minute Lecture Script

---

### [00:00–02:00] Welcome Back & Recap

Welcome back. In Part 1 we built analytical tools — Big O for performance, design patterns for structure, Stack and Heap for memory understanding. Now we go deeper into the JVM itself. How does Java automatically clean up memory? What happens when GC runs? How do you tune it when things go wrong? And then we shift to practical skills: reading and writing files correctly, serializing objects, and debugging professionally.

Part 2 is very practical. By the end, you'll know how to handle file I/O safely, how to use the IntelliJ debugger instead of print statements, and you'll have the mental model for what the JVM is doing with your objects between allocations and collections. Let's go.

---

### [02:00–06:00] Garbage Collection — The Big Picture

In C and C++, you allocate memory with `malloc` and free it with `free`. Forget to call `free`, and you have a memory leak — memory accumulates, eventually your program runs out and crashes. Free too early — while another pointer still references it — and you have a dangling pointer, which causes crashes and security vulnerabilities.

Java eliminated this class of bugs with the Garbage Collector. The GC automatically identifies objects that are no longer reachable from your program and reclaims their memory. You never call `free`. You never manually manage the heap.

The fundamental concept is *reachability*. An object is "live" if there's a path of references from any "GC root" to that object. GC roots are: local variables on any active thread's stack, static variables of loaded classes, and active thread objects themselves. If the GC traverses all references from all roots and an object isn't reachable, that object is garbage — it can be collected.

Here's the subtle but important point: an object becomes garbage not when you're done using it, but when no live reference points to it. `myObject = null` makes the previous object potentially eligible for GC — but the GC decides when to actually collect it, not you. `System.gc()` is a hint that the JVM may completely ignore. Never rely on the timing of GC for correctness.

One significant advantage over reference counting (which Python uses): Java's GC handles circular references. If object A holds a reference to B and B holds a reference to A, but nothing outside the cycle points to either, the GC traces from roots, never reaches A or B, and collects both. Reference counting sees each object with a count of one and keeps them alive forever.

---

### [06:00–12:00] Generational Heap and GC Algorithms

The most important insight in modern GC design is the "generational hypothesis": most objects die young. Think about what your code actually creates — temporary strings in loops, intermediate stream results, method return values. The vast majority of objects are created, used briefly, and never referenced again. A small number of objects — caches, long-running service objects, configuration — live for the entire application lifetime.

The JVM exploits this by dividing the heap into generations. The Young Generation is where new objects are born. It's divided into Eden space (birth zone), and two Survivor spaces (S0 and S1). The Old Generation (or Tenured space) holds long-lived objects.

When Eden fills up, a Minor GC runs. It's fast because it only scans the Young Generation, which is small and has mostly garbage. Objects that survived are moved to a Survivor space. Objects that survive multiple Minor GCs are "promoted" to the Old Generation.

The Old Generation fills more slowly. When it does, a Major GC (or Full GC) runs — that's slower because it scans the entire heap. Full GC pauses are the ones that cause performance complaints in production — "our service has these random 500ms latency spikes" — that's usually Full GC.

For the GC algorithm itself, G1 GC has been the Java default since Java 9 and is what you'll encounter in most modern Spring Boot applications. G1 divides the heap into equal-sized regions (typically 1–32MB each) that can be assigned as Eden, Survivor, or Old as needed. It prioritizes collecting regions with the most garbage first — that's where "Garbage First" gets its name. G1 aims for predictable pause times with a configurable target — `-XX:MaxGCPauseMillis=200` tells G1 to aim for 200ms pauses.

ZGC and Shenandoah are newer low-latency collectors that do most work concurrently with your application running, achieving sub-millisecond pauses even on multi-terabyte heaps. They're impressive but have more overhead. For most business applications you'll build, G1 is fine.

---

### [12:00–16:00] JVM Tuning Flags

When your application has performance issues related to GC, there are flags to tune it. In practice, you only reach for these after profiling confirms GC is the problem — premature tuning wastes time.

The two most fundamental flags: `-Xms` sets the initial heap size, `-Xmx` sets the maximum heap size. In production, best practice is to set them equal — `-Xms2g -Xmx2g`. Why? If they're different, when the heap needs to grow (it expands up to -Xmx), there's a brief pause for reallocation. Keeping them equal eliminates that. It also makes memory usage predictable for your container or VM.

For GC algorithm selection: `-XX:+UseG1GC` for G1 (default in Java 9+), `-XX:+UseZGC` for ultra-low latency in Java 15+, `-XX:+UseParallelGC` for maximum throughput in batch processing jobs where pause time doesn't matter.

GC logging is invaluable when diagnosing issues: `-Xlog:gc*:file=gc.log:time,uptime`. This produces a detailed log of every GC event — when it happened, how long it paused, before/after heap usage. Tools like GCEasy.io can analyze these logs and give you a readable report.

For stack size: `-Xss512k` sets the stack size per thread. If you're hitting StackOverflowError and it's not from infinite recursion, you might need to increase this. If you're running many threads and memory is tight, reducing this saves memory — each thread has its own stack.

Common tuning scenario: your Spring Boot application runs fine in testing but has random slow responses in production. You check GC logs and see Full GC events taking 2 seconds every few minutes. First check: are you holding onto too many large objects in the Old Generation? Maybe a cache growing without bounds. Second check: is heap big enough — increase -Xmx. Third: if pauses are still unacceptable, try ZGC. Always measure before and after any JVM tuning change.

---

### [16:00–22:00] Reference Types

We've been using strong references throughout this entire course. `Person p = new Person()` — that's a strong reference. The GC will never collect a strongly reachable object. Period.

But Java gives you three weaker reference types that let you hold references that GC can override when needed. These are in `java.lang.ref` and you import them explicitly.

SoftReference: GC collects soft referents when — and only when — the JVM is low on memory. If there's plenty of heap available, soft references are kept alive. Under memory pressure, they're collected. This is perfect for memory-sensitive caches.

Imagine you're building an image viewer. You've loaded 200 images into memory for fast display. You don't want to reload from disk every time, but you also don't want to crash with OutOfMemoryError if the user opens more images. Store them in SoftReferences. The JVM keeps them as long as memory allows, and automatically evicts them when memory is needed. Your code falls back to reloading from disk when `.get()` returns null.

The pattern is always the same: call `.get()` and check for null. The referent might have been collected since you last checked.

WeakReference: GC collects weak referents at the next GC cycle if they're only weakly reachable. "Only weakly reachable" means the only remaining path from GC roots to the object goes through a WeakReference. If there's any strong reference elsewhere, it stays alive.

The practical use case: `WeakHashMap`. This is a HashMap where the keys are weak references. When a key object has no strong references elsewhere, the WeakHashMap entry is automatically removed. 

Classic use case: you're building a tooltip system. You want to associate tooltip text with UI widget objects. If you use a regular HashMap, the map holds a strong reference to the widget key, and the widget can never be GC'd as long as the map lives. With WeakHashMap, when your UI removes the widget, the entry automatically disappears from the map — no memory leak.

PhantomReference: this is the most advanced and rarely used directly. You add a PhantomReference to a ReferenceQueue, and after the object is finalized but before its memory is reclaimed, the PhantomReference appears in the queue. This lets you perform cleanup actions precisely when an object is about to be collected. Framework authors use this for resource cleanup (closing native handles, releasing off-heap memory). Application developers rarely need it — `try-with-resources` and `AutoCloseable` are the right tool for deterministic cleanup.

---

### [22:00–28:00] File I/O — Reading with BufferedReader

Now let's talk about reading and writing files. Java has two parallel I/O hierarchies: byte streams for binary data (images, audio, arbitrary bytes) and character streams for text (with encoding handling). Today we focus on character streams — Readers and Writers.

The key class for reading text files is BufferedReader, wrapped around a FileReader. Why BufferedReader? FileReader alone reads one character at a time from the operating system. Every character triggers a system call. System calls are slow. BufferedReader reads a large chunk (typically 8KB) into memory at once, then serves characters from that buffer. You get the same data, but hundreds of times fewer system calls.

Here's the canonical pattern:

```java
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt", StandardCharsets.UTF_8))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
}
```

Three things to notice. First, `try-with-resources` — the reader is in the try clause, so it's automatically closed when the block exits, even if an exception is thrown. Always use this pattern for I/O. Second, `readLine()` returns null at end of file, not an exception — the null check `!= null` is your loop terminator. Third, `StandardCharsets.UTF_8` — always specify charset explicitly. Don't rely on the platform default. Code that works on your Mac might produce garbled output on a Linux server with a different default encoding.

For modern Java (11+), the NIO Files API is even cleaner for most cases:

```java
// Read entire file — perfect for small files
String content = Files.readString(Path.of("data.txt"), StandardCharsets.UTF_8);

// Read all lines into a List
List<String> lines = Files.readAllLines(Path.of("data.txt"), StandardCharsets.UTF_8);

// Stream lines with lazy reading — memory-efficient for large files
try (Stream<String> stream = Files.lines(Path.of("data.txt"))) {
    stream.filter(line -> line.startsWith("ERROR")).forEach(System.out::println);
}
```

The `Files.lines()` version is particularly nice — it integrates with the Stream API you learned in Day 8 and reads lazily, so you don't load the entire file into memory at once.

---

### [28:00–34:00] File I/O — Writing with BufferedWriter

Writing follows the same structure. BufferedWriter wraps FileWriter for efficiency. FileWriter alone writes one character at a time to the OS. BufferedWriter accumulates output in a buffer and writes in chunks, which is dramatically faster.

```java
try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("output.txt", StandardCharsets.UTF_8, false))) {
    writer.write("Hello, World!");
    writer.newLine();
    writer.write("Second line");
    writer.newLine();
} catch (IOException e) {
    System.err.println("Write error: " + e.getMessage());
}
```

The third argument to FileWriter is the append flag — `false` means overwrite, `true` means append to existing content.

Important: use `writer.newLine()` instead of `"\n"`. `newLine()` writes the platform-appropriate line separator — `\r\n` on Windows, `\n` on Linux and macOS. If you hardcode `\n`, files opened in Notepad on Windows might appear as one long line.

The try-with-resources block automatically flushes and closes the BufferedWriter when it exits. This flush step is critical — the buffer might hold data that hasn't been written to disk yet. Without closing (and therefore flushing), you could write 100 lines of data and have an empty file. Lesson: always use try-with-resources with I/O.

For formatted output, wrap in PrintWriter:

```java
try (PrintWriter pw = new PrintWriter(
        new BufferedWriter(new FileWriter("report.txt")))) {
    pw.printf("%-20s %8.2f%n", "Alice", 75000.50);
    pw.printf("%-20s %8.2f%n", "Bob", 68000.00);
}
```

And the modern NIO approach:

```java
Files.writeString(Path.of("output.txt"), "Hello!\n", StandardCharsets.UTF_8);

List<String> lines = List.of("Line 1", "Line 2", "Line 3");
Files.write(Path.of("output.txt"), lines, StandardCharsets.UTF_8);
```

For most new code, prefer the `Files` API — it's cleaner, handles charsets correctly by default, and is harder to misuse.

---

### [34:00–40:00] Serialization and Deserialization

Serialization is the process of converting a Java object into a byte stream that can be stored in a file, sent over a network, or cached. Deserialization is reconstructing the object from that byte stream. Java builds this in — to serialize a class, implement the `Serializable` marker interface.

```java
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int salary;
    private transient String password;  // NOT serialized
    // ...
}
```

Two keywords to understand. `serialVersionUID` is a version identifier. When you deserialize an object, Java checks if the class's current serialVersionUID matches the one embedded in the serialized bytes. If they don't match, it throws `InvalidClassException`. If you don't declare it explicitly, Java auto-generates it from the class structure. Add a field? Generated UID changes. Now you can't read old serialized files. Always declare it explicitly as `1L` and only update it when making breaking schema changes.

`transient` marks fields that should NOT be serialized. Use it for: passwords (don't serialize credentials), database connections (not serializable, and meaningless to restore), computed/cached values, anything that should be freshly initialized.

Writing an object:

```java
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("employee.ser"))) {
    oos.writeObject(employee);
}
```

Reading it back:

```java
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("employee.ser"))) {
    Employee loaded = (Employee) ois.readObject();
}
```

The entire object graph is serialized — if Employee has a List of Orders, and each Order has a List of Items, all of those are serialized. Every class in the graph must implement Serializable or be marked transient.

Where does Java serialization appear in the real world? Tomcat and other servlet containers serialize HttpSession objects for clustering — when a request hits a different server, it deserializes your session. JPA second-level caches serialize entities. RMI (Remote Method Invocation) serializes method arguments.

But here's the honest context: for most modern development, you'll use JSON instead of Java serialization. Jackson (which you'll use extensively with Spring in Week 5) is faster, human-readable, language-agnostic, and significantly safer. Java deserialization of untrusted data has been exploited in multiple critical CVEs — arbitrary code execution through carefully crafted byte streams. The Java security team has been hardening it, but the lesson is: never deserialize data from untrusted sources with Java's built-in serialization.

---

### [40:00–46:00] Debugging with IntelliJ — Breakpoints and Core Operations

Debugging. Every developer debugs. The difference between a junior and a senior developer isn't whether they have bugs — everyone has bugs. It's how efficiently they find them.

Print-statement debugging works. `System.out.println("got here")`, `System.out.println("value is " + x)`. For trivial issues, it's fine. But it has limits: you have to modify code, recompile, run again. You can only see what you printed. After debugging, you have to remember to remove the prints.

The debugger gives you something qualitatively different: you can pause execution at any point and inspect *everything* — all local variables, all fields of all objects, the full call stack, any expression you can write in Java. Without modifying code. Without recompiling. It's like freezing time and exploring your program's state at that exact moment.

In IntelliJ, to set a breakpoint: click the left gutter next to any line of code. A red circle appears. The next time you run the application in debug mode (Shift+F9 instead of Shift+F10), execution will pause just before that line executes.

When paused, you see the Variables panel on the left — every local variable in scope, with its current value. Click the arrow next to an object to expand it and see all its fields. The Frames panel shows the call stack — which method called which, going all the way back to `main()`. Click any frame and the Variables panel updates to show that frame's variables.

The debug toolbar gives you navigation controls:
- **F8** — Step Over: execute the current line and move to the next line in the same method. If the current line is a method call, it executes the entire method and lands on the next line.
- **F7** — Step Into: if the current line calls a method, enter that method and pause at its first line.
- **Shift+F8** — Step Out: finish executing the current method and return to the caller.
- **F9** — Resume: run until the next breakpoint.

---

### [46:00–52:00] Advanced Debugging — Conditional Breakpoints and Evaluate

Once you're comfortable with basic breakpoints, these tools will save you enormous time.

Conditional Breakpoints: right-click a breakpoint and set a condition. The breakpoint only fires when the condition evaluates to true. This is invaluable for loops.

Imagine you're processing a list of 10,000 orders and one of them causes a NullPointerException. Without a conditional breakpoint, you'd hit the breakpoint on every iteration — press F9 ten thousand times. With a conditional breakpoint: condition `order.getId() == 5432` — only pauses on the specific order causing the problem. Or `order.getTotal() < 0` — only pauses when the total is negative. You jump straight to the problematic case.

Exception Breakpoints: in the Run menu → View Breakpoints → click `+` → Java Exception Breakpoints → type `NullPointerException`. Now whenever any NullPointerException is thrown anywhere in your code, the debugger pauses at the exact line — before the exception propagates, while all the variables are still in scope. No more "NullPointerException at line 47" with no context.

Evaluate Expression (Alt+F8 when paused): opens a dialog where you can type and execute any Java expression in the current scope. Want to call a method and see its result? Want to test a fix without restarting? Want to check what `list.subList(3, 7)` returns? Type it and execute. This is incredibly powerful for hypothesis testing — you can verify your fix works before you modify the code.

The call stack gives you the "how did I get here?" answer. A NullPointerException 10 frames deep — click each frame up the stack to understand what was happening in each caller. At which level did the null value originate? The frame view makes this navigable.

My recommended debugging workflow: when you encounter a bug, don't start randomly adding print statements. Think scientifically. What's your hypothesis about the cause? "I think the customer object is null when the order has no billing address." Now test that — set a breakpoint where the customer is used, add a condition `customer == null`, run, see if it fires. If your hypothesis is wrong, form a new one. Systematic debugging finds bugs in minutes instead of hours.

---

### [52:00–56:00] Debugging Strategy and Logging

Let me give you a practical debugging workflow that works for any bug.

Step one: reproduce it reliably. A bug you can't reproduce consistently is almost impossible to debug. Find the exact inputs and state that trigger it. If it only happens "sometimes," find the condition that makes "sometimes" into "always."

Step two: form a hypothesis. Based on the symptoms, what do you think is wrong? "I think the calculation is using the wrong unit." "I think the list is empty when it shouldn't be." Be specific. Vague hypotheses lead to random debugging.

Step three: set breakpoints to test the hypothesis, not to explore randomly. If your hypothesis is "the list is empty," set a breakpoint right before the list is used and inspect its size. That tests your hypothesis directly.

Step four: confirm or refute. If the list isn't empty, your hypothesis was wrong. Form a new one. If it is empty, trace back to where it should have been populated.

Step five: fix, verify, write a test. Once you've identified the cause, fix it, verify with the debugger that the fix works, and write a unit test that would have caught this bug. This last step is crucial — the bug shouldn't be possible to reintroduce.

For production debugging where you can't attach a debugger: logging is your tool. SLF4J with Logback is the standard in Spring Boot applications, which you'll see in Day 26. The key practices:

Log at the right level. INFO for significant operational events. DEBUG for detailed diagnostic information (disable in production when not needed). WARN for unexpected but recoverable situations. ERROR for failures that need attention.

Use parameterized logging: `log.debug("Order {} processed in {}ms", orderId, elapsed)` — the string is only built if debug is enabled, so no performance penalty.

Log at service boundaries — when you receive input, when you call external services, when you produce output. These logs let you trace data flow through your system.

---

### [56:00–60:00] Week 2 Complete — Summary and Looking Ahead

Let's close out Week 2.

In Part 2 we covered: Garbage Collection — the GC starts from roots, traces reachable objects, collects everything else. Most objects die young — the generational heap exploits this. G1 GC is the default. Under memory pressure, tune with -Xmx, -Xms, and GC algorithm flags.

Reference types — Strong (never collected while live), Soft (collected under memory pressure, perfect for caches), Weak (collected at next GC, perfect for WeakHashMap and listener patterns), Phantom (post-finalization hook, framework-level).

File I/O — wrap FileReader/FileWriter in Buffered variants for performance. Always specify charset explicitly (StandardCharsets.UTF_8). Always use try-with-resources. readLine() returns null at EOF. newLine() for platform-safe line endings. For modern Java, prefer the NIO Files API.

Serialization — implement Serializable, always declare serialVersionUID, use transient for fields that shouldn't persist. In practice, prefer JSON (Jackson) for most new development.

Debugging — use conditional breakpoints instead of hitting resume thousands of times. Exception breakpoints for NPE hunting. Evaluate Expression to test hypotheses without restarting. Call stack to trace the "how did I get here?" question. Systematic hypothesis-driven debugging beats random print statements every time.

You've now completed an entire professional Java curriculum — Week 1 fundamentals, Week 2 advanced. OOP, collections, exceptions, functional programming, concurrency, performance analysis, design patterns, memory management, file I/O, debugging. That's a solid Java skill set.

Week 3 starts Monday with HTML and CSS. We're pivoting to the frontend. The analytical skills transfer — Big O applies to JavaScript, Observer pattern appears in React and Angular, debugging tools differ but the mindset is the same. We're not starting over; we're expanding.

Enjoy the weekend. You've earned it.

---
