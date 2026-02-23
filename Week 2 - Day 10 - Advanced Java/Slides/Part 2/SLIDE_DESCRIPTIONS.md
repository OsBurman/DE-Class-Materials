# Week 2 - Day 10, Part 2: GC, File I/O, Serialization & Debugging
## Slide Descriptions (60-minute lecture)

---

### Slide 1: Title Slide
**Title:** "JVM Internals, File I/O & Debugging"
**Subtitle:** "Garbage Collection, Reference Types, Files & Professional Debugging"
**Visual:** JVM diagram with GC sweeper graphic, file system icon, and a debugger breakpoint marker.
**Speaker note:** Welcome back to Part 2 of Day 10. We're covering how Java manages memory automatically (GC), how to tune the JVM when GC causes issues, the four reference types that let you interact with GC, how to read and write files, how to persist objects with serialization, and how to professionally debug Java applications in IntelliJ.

---

### Slide 2: Garbage Collection — Why It Exists
**Content:**
- **Manual memory management (C/C++):** Programmer calls `malloc`/`free`. Forget to free? Memory leak. Free too early? Crash.
- **Java's approach:** GC automatically reclaims memory from objects that are no longer reachable
- **Definition of "unreachable":** No live reference on any thread's stack (or reachable from a stack reference) points to the object
- **Benefits:** Eliminates most memory leaks and dangling pointer bugs
- **Tradeoffs:** GC introduces pause times (application stops briefly), overhead, and non-deterministic cleanup timing

```java
void createObjects() {
    String s1 = new String("temp");   // Created on heap
    String s2 = new String("data");   // Created on heap
    s1 = null;  // "temp" is now unreachable — eligible for GC
}
// When method returns: s2 also unreachable — both eligible for GC
```
- **GC is not immediate:** Eligible objects are collected on the GC's schedule, not instantly
- **You cannot force GC:** `System.gc()` is a hint, not a command — JVM may ignore it
**Visual:** Heap diagram with some objects highlighted in grey (unreachable) while others remain connected to roots (live references on stack).
**Speaker note:** In C, every `malloc` must have a matching `free`. Forget one, and you have a memory leak. In Java, GC handles this. The trade-off is you give up precise control. `System.gc()` is often called a mistake because it doesn't guarantee collection and can actually hurt performance by triggering unnecessary pauses.

---

### Slide 3: GC Roots & Reachability
**Content:**
- **GC Roots:** The starting points GC uses to determine what's reachable (live)
  - Local variables and parameters on active thread stacks
  - Static variables of loaded classes
  - Active Java threads themselves
  - JNI references
- **Reachability traversal:** GC starts from roots, traverses all references — any object not reached is garbage
- **Object graph:**
```
Stack roots
  └─ OrderService (live)
       ├─ CustomerRepository (live)
       │    └─ List<Customer> (live)
       │         ├─ Customer@101 (live)
       │         └─ Customer@102 (live — part of list)
       └─ abandoned Order@200 ← only reference was cleared → GARBAGE
```
- **Circular references are collected:** Unlike reference counting (Python), Java's GC handles cycles
```java
class Node { Node next; }
Node a = new Node();
Node b = new Node();
a.next = b;
b.next = a;  // Circular reference
a = null;
b = null;    // Both unreachable from roots — GC collects despite cycle
```
**Visual:** Object graph with roots on left, live objects connected, orphaned objects (garbage) shown in red with dashed connections.
**Speaker note:** One of Java GC's advantages over reference counting is handling circular references. If A points to B and B points to A, but nothing outside points to either of them, reference counting (used in Python, Swift's ARC) keeps them alive. Java's tracing GC starts from roots and anything not reached is garbage — circles and all.

---

### Slide 4: The Generational Heap & GC Algorithms
**Content:**
**Generational Hypothesis:** Most objects die young — short-lived temporaries are the majority.

**Young Generation (Minor GC — fast, frequent):**
- **Eden:** New objects born here
- **Survivor S0/S1:** Objects that survived one GC cycle moved here
- **Minor GC:** Collects Eden + one Survivor space, moves survivors to other Survivor space
- **Promotion:** Objects surviving multiple minor GCs promoted to Old Gen

**Old Generation / Tenured (Major/Full GC — slower, less frequent):**
- Long-lived objects (caches, static data, long-running services)
- When Old Gen fills → Full GC — can be slow (100ms–seconds)
- **GC pause:** Application threads pause during stop-the-world GC

**GC Algorithms (Java options):**
| Algorithm | Flag | Best For | Pause |
|-----------|------|---------|-------|
| Serial GC | `-XX:+UseSerialGC` | Single-core, small apps | High |
| Parallel GC | `-XX:+UseParallelGC` | Batch/throughput apps | Medium |
| G1 GC (default Java 9+) | `-XX:+UseG1GC` | Most server apps | Low |
| ZGC (Java 15+) | `-XX:+UseZGC` | Low-latency (<1ms pauses) | Very Low |
| Shenandoah | `-XX:+UseShenandoahGC` | Ultra-low latency | Very Low |

**Visual:** Heap diagram divided into Young Gen (Eden + S0/S1) and Old Gen, with arrows showing object promotion flow as they survive GC cycles.
**Speaker note:** G1 GC has been the default since Java 9 for good reason — it balances throughput and pause times well for most server applications. ZGC is impressive — sub-millisecond pauses even on terabyte heaps — but has more overhead. For most Java backend development you'll do, the default G1 GC works fine and you only tune it when profiling reveals GC is a bottleneck.

---

### Slide 5: JVM Memory Tuning Flags
**Content:**
**Essential JVM Flags:**

```bash
# Heap size — most important tuning parameters
-Xms512m        # Initial heap size (512 MB at start)
-Xmx2g          # Maximum heap size (2 GB ceiling)
# Rule: Set -Xms == -Xmx in production to avoid heap resizing pauses

# GC algorithm selection
-XX:+UseG1GC            # G1 GC (default Java 9+, usually fine)
-XX:+UseZGC             # ZGC — ultra-low latency (Java 15+)
-XX:+UseParallelGC      # Parallel — best throughput for batch apps

# Young generation sizing
-XX:NewRatio=2          # Old:Young ratio (2 = Old is 2x Young)
-XX:NewSize=256m        # Fixed young generation size

# GC logging (invaluable for debugging)
-Xlog:gc*:file=gc.log:time,uptime:filecount=5,filesize=20m

# Stack size per thread
-Xss512k                # 512KB stack per thread (default ~512KB-1MB)

# Metaspace (class metadata)
-XX:MaxMetaspaceSize=256m  # Cap class metadata space
```

**Common tuning scenarios:**
- App runs out of memory → increase `-Xmx`
- GC pauses are too long → try G1 or ZGC, tune region sizes
- Many short-lived objects → increase Young Gen
- Large cache / long-lived data → increase Old Gen

**JVM analysis tools:** `jstat`, `jmap`, `jconsole`, IntelliJ Profiler, VisualVM
**Visual:** Terminal/command prompt graphic showing JVM flags being applied, with a memory usage graph responding to changes.
**Speaker note:** In production Spring Boot apps, you'd typically set `-Xms` and `-Xmx` equal to prevent resizing pauses, then let G1 GC do its job. Only dive into advanced tuning when GC logs show actual problems. Premature JVM tuning is a common trap — profile first, tune second.

---

### Slide 6: Reference Types — Four Levels of Strength
**Content:**
- **Java has four reference types** that interact differently with GC
- Normal references (`Person p = new Person()`) are **strong references** — GC will NEVER collect while strongly reachable

| Type | GC Behavior | Use Case |
|------|------------|---------|
| Strong | Never collected while referenced | Normal usage (everything you've done so far) |
| Soft | Collected only when JVM is low on memory | Memory-sensitive caches (image cache, result cache) |
| Weak | Collected at NEXT GC cycle if only weakly reachable | Canonicalized mappings, listeners that shouldn't prevent GC |
| Phantom | Object already finalized, pre-collection notification | Cleanup actions before collection, resource management |

**Import:** `java.lang.ref.SoftReference`, `WeakReference`, `PhantomReference`
**Visual:** A strength spectrum from left (Strong — never collected) to right (Phantom — collected first), with each type labeled.
**Speaker note:** You'll use strong references 99% of the time. Soft and Weak references are specialized tools for caches and memory-sensitive structures. PhantomReference is rarely used directly — it's more of a framework tool. The key insight: by using a WeakReference, you're telling the JVM "if you need memory, you can collect this."

---

### Slide 7: Soft and Weak References in Practice
**Content:**
**SoftReference — memory-sensitive cache:**
```java
import java.lang.ref.SoftReference;

// Image cache — holds onto images until memory pressure
Map<String, SoftReference<BufferedImage>> imageCache = new HashMap<>();

// Store
imageCache.put("logo.png", new SoftReference<>(loadImage("logo.png")));

// Retrieve — always check for null (may have been collected)
SoftReference<BufferedImage> ref = imageCache.get("logo.png");
BufferedImage img = (ref != null) ? ref.get() : null;
if (img == null) {
    img = loadImage("logo.png");  // Reload if collected
    imageCache.put("logo.png", new SoftReference<>(img));
}
```

**WeakReference — prevent memory leaks in listeners:**
```java
import java.lang.ref.WeakReference;

// WeakHashMap: key collected if no strong references to key
Map<Widget, String> tooltips = new WeakHashMap<>();
Widget w = new Widget();
tooltips.put(w, "Click me!");  // tooltip tied to widget's lifetime
w = null;  // Widget eligible for GC; WeakHashMap entry removed automatically
```

**Key rule:** Always check `.get()` for null — GC may have collected the referent
```java
WeakReference<Connection> weakConn = new WeakReference<>(connection);
Connection c = weakConn.get();  // Could be null if GC ran
if (c != null) {
    c.execute(query);
} else {
    // Reconnect
}
```
**Visual:** Diagram showing SoftReference and WeakReference boxes with dashed lines to their referents (indicating "soft" connection), contrasted with solid arrows for strong references.
**Speaker note:** The most practical use of weak references is avoiding memory leaks with listeners. If you register a callback on a long-lived object (like an EventBus), a strong reference in the listener list prevents GC of the listener. WeakReference lets the listener be collected when the application no longer needs it. Android's memory leak patterns often involve exactly this scenario.

---

### Slide 8: File I/O — The Character Stream Hierarchy
**Content:**
- **Two I/O family trees in Java:**
  - **Byte streams:** `InputStream`/`OutputStream` (raw bytes — images, audio, network)
  - **Character streams:** `Reader`/`Writer` (text with encoding — today's focus)
- **Character streams automatically handle encoding** (UTF-8, UTF-16, etc.)

**Writer hierarchy:**
```
Writer (abstract)
├── OutputStreamWriter  (bridge: bytes → chars, specify charset)
│    └── FileWriter     (writes chars to file — uses default charset)
└── BufferedWriter      (wraps any Writer, adds buffering for performance)
     └── PrintWriter    (adds print/println convenience methods)
```

**Reader hierarchy:**
```
Reader (abstract)
├── InputStreamReader   (bridge: bytes → chars, specify charset)
│    └── FileReader     (reads chars from file — uses default charset)
└── BufferedReader      (wraps any Reader, adds buffering + readLine())
```

**Rule of thumb:**
- Use `FileReader`/`FileWriter` for simple text files
- Always wrap with `BufferedReader`/`BufferedWriter` for performance
- Use `InputStreamReader(stream, StandardCharsets.UTF_8)` when charset matters
**Visual:** Tree diagram showing the Writer and Reader hierarchies with arrows indicating "wraps" relationships.
**Speaker note:** The wrapping pattern (Decorator pattern — structurally) is consistent throughout Java I/O. FileWriter gives you file access, BufferedWriter gives you buffering, PrintWriter gives you convenience. You stack them like Russian dolls. This is important because FileWriter without buffering writes one character at a time to disk — catastrophically slow.

---

### Slide 9: Reading Files with BufferedReader
**Content:**
**Reading a text file — the correct pattern:**
```java
import java.io.*;
import java.nio.charset.StandardCharsets;

// try-with-resources ensures file is ALWAYS closed (even on exception)
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt", StandardCharsets.UTF_8))) {

    String line;
    while ((line = reader.readLine()) != null) {  // null means EOF
        System.out.println(line);
    }

} catch (IOException e) {
    System.err.println("Error reading file: " + e.getMessage());
}
// reader.close() called automatically — even if exception thrown
```

**Reading entire file at once (Java 11+):**
```java
import java.nio.file.*;

// Simplest for small files
String content = Files.readString(Path.of("data.txt"), StandardCharsets.UTF_8);

// Read all lines into a List
List<String> lines = Files.readAllLines(Path.of("data.txt"), StandardCharsets.UTF_8);

// Stream lines (memory-efficient for large files)
try (Stream<String> lines = Files.lines(Path.of("data.txt"))) {
    lines.filter(l -> l.startsWith("ERROR"))
         .forEach(System.out::println);
}
```
- **`java.nio.file.Files`:** Modern API — prefer this over raw streams for most file operations
- **Always specify charset explicitly** — `StandardCharsets.UTF_8` avoids platform-specific issues
**Visual:** Code block with annotations highlighting try-with-resources structure, readLine() loop, and the null-check termination condition.
**Speaker note:** Note the `readLine()` returns null at end-of-file, not an exception. This is a common confusion point. Also notice we specify `StandardCharsets.UTF_8` explicitly — relying on the platform default charset is a bug waiting to happen when code runs on different systems. The Java NIO `Files` API (Java 11+) is much cleaner for most use cases.

---

### Slide 10: Writing Files with BufferedWriter
**Content:**
**Writing a text file — the correct pattern:**
```java
import java.io.*;
import java.nio.charset.StandardCharsets;

// APPEND = false means overwrite; true means append
try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("output.txt", StandardCharsets.UTF_8, false))) {

    writer.write("Line 1: Hello, World!");
    writer.newLine();   // Platform-appropriate line separator (\n or \r\n)
    writer.write("Line 2: Writing files in Java");
    writer.newLine();
    writer.flush();     // Optional — try-with-resources flush on close anyway

} catch (IOException e) {
    System.err.println("Error writing file: " + e.getMessage());
}
```

**Writing with PrintWriter (printf-style formatting):**
```java
try (PrintWriter pw = new PrintWriter(
        new BufferedWriter(new FileWriter("report.txt")))) {
    pw.printf("%-20s %5d%n", "Alice", 42000);
    pw.printf("%-20s %5d%n", "Bob", 38500);
    pw.println("End of report");
}
```

**Modern NIO alternative:**
```java
// Write string to file (overwrites)
Files.writeString(Path.of("output.txt"), "Hello!\n", StandardCharsets.UTF_8);

// Write lines
List<String> lines = List.of("Line 1", "Line 2", "Line 3");
Files.write(Path.of("output.txt"), lines, StandardCharsets.UTF_8);
```
**Visual:** Annotated code block showing try-with-resources, FileWriter append flag, BufferedWriter.newLine() importance, and flush/close behavior.
**Speaker note:** Two things to notice: `newLine()` is platform-safe — on Windows it writes `\r\n`, on Linux `\n`. If you hardcode `\n`, your file might have issues on Windows. And try-with-resources automatically flushes and closes the writer — that's why buffered writes actually reach the disk. Without closing/flushing, the buffer might never get written.

---

### Slide 11: try-with-resources for I/O — The Right Way
**Content:**
**The problem without try-with-resources:**
```java
// WRONG — resource may leak on exception
BufferedReader reader = null;
try {
    reader = new BufferedReader(new FileReader("data.txt"));
    String line;
    while ((line = reader.readLine()) != null) {
        process(line);
    }
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (reader != null) {
        try {
            reader.close();  // Even this can throw!
        } catch (IOException e) {
            e.printStackTrace();  // Suppressed exception mess
        }
    }
}
```

**The fix — try-with-resources (Java 7+):**
```java
// RIGHT — reader.close() called automatically, even on exception
try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        process(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}
```

**Multiple resources — both closed in reverse order:**
```java
try (BufferedReader in  = new BufferedReader(new FileReader("in.txt"));
     BufferedWriter out = new BufferedWriter(new FileWriter("out.txt"))) {
    String line;
    while ((line = in.readLine()) != null)
        out.write(line.toUpperCase() + "\n");
}
```
- **Rule:** Any class implementing `AutoCloseable` can be used in try-with-resources
**Visual:** Side-by-side comparison of old verbose finally block vs clean try-with-resources. Red for old, green for new.
**Speaker note:** try-with-resources isn't just cleaner — it's actually more correct. The old pattern has a subtle bug: if `process(line)` throws an exception AND `reader.close()` throws, the original exception is suppressed and you see the wrong error. try-with-resources handles this correctly with "suppressed exceptions" — the original exception propagates and the close exception is attached as a suppressed exception.

---

### Slide 12: Serialization — Persisting Objects
**Content:**
- **Serialization:** Converting a Java object into a byte stream (for storage or network transfer)
- **Deserialization:** Reconstructing the object from the byte stream
- **Enable it:** Implement `java.io.Serializable` (marker interface — no methods required)

```java
import java.io.*;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;  // Version identifier
    private String name;
    private int salary;
    private transient String password;  // transient — NOT serialized

    public Employee(String name, int salary, String password) {
        this.name = name; this.salary = salary; this.password = password;
    }
    // getters...
}
```

**Serializing (writing object to file):**
```java
Employee emp = new Employee("Alice", 75000, "secret123");

try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("employee.ser"))) {
    oos.writeObject(emp);  // Entire object graph serialized
}
```

**Deserializing (reading object from file):**
```java
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("employee.ser"))) {
    Employee loaded = (Employee) ois.readObject();
    System.out.println(loaded.getName());    // "Alice"
    System.out.println(loaded.getPassword()); // null — transient not serialized
}
```
**Visual:** Diagram showing Employee object → ObjectOutputStream → byte stream file (`employee.ser`) → ObjectInputStream → reconstructed Employee object. The `password` field shown as excluded.
**Speaker note:** Two things to remember: `serialVersionUID` is a version ID. If you change the class structure and the version doesn't match, deserialization throws `InvalidClassException`. Mark `transient` any field that shouldn't be saved — passwords, database connections, computed values. Note that serialization can be a security risk — deserializing untrusted data is dangerous and has been exploited in many Java CVEs. In Week 5+ when you learn Spring, prefer JSON/Jackson over Java serialization.

---

### Slide 13: Serialization — Gotchas & Real-World Context
**Content:**
**Common serialization issues:**

**1. serialVersionUID mismatch:**
```java
// If you serialize Employee without serialVersionUID, Java generates one
// If you add a field later and re-run, generated UID changes
// Deserialization fails: InvalidClassException
// FIX: Always declare serialVersionUID explicitly
private static final long serialVersionUID = 1L;
```

**2. Non-serializable fields:**
```java
// If a field's type is not Serializable, NotSerializableException at runtime
private Connection dbConnection;  // Connection is not Serializable — must mark transient!
private transient Connection dbConnection;  // FIX
```

**3. The entire object graph must be serializable:**
```java
class Order implements Serializable {
    Customer customer;  // Customer must ALSO implement Serializable
    List<Item> items;   // ArrayList IS Serializable, but Item must be too
}
```

**Real-world context:**
- **HTTP Session caching:** Tomcat serializes HttpSession for clustering — your session objects must be Serializable
- **Java RMI:** Remote method invocation serializes parameters
- **Modern alternatives:** JSON (Jackson/Gson) is preferred for most use cases:
  - Human-readable
  - Language-agnostic
  - Safer (no arbitrary class instantiation)
  - Standard in REST APIs (Week 5+)
**Visual:** Network diagram showing Tomcat cluster serializing sessions, and REST API diagram showing JSON preference over Java serialization.
**Speaker note:** In modern Spring Boot development, you'll rarely use Java serialization directly. Jackson (JSON) is the standard for REST APIs. But serialization comes up in: JPA caching, session replication, and legacy systems. You need to know it exists and how it works, even if you don't use it daily.

---

### Slide 14: Debugging — Professional Tools & Techniques
**Content:**
- **Debugging:** Systematically finding and fixing defects in code
- **Print debugging:** `System.out.println()` is the first instinct — it works but doesn't scale
- **Professional debugging:** Using a debugger to inspect program state at any point without modifying code

**IntelliJ IDEA Debugger — Core Operations:**

| Action | IntelliJ Shortcut | What it Does |
|--------|------------------|-------------|
| Set Breakpoint | Click left gutter (or F9) | Pause execution at this line |
| Start Debug | Shift+F9 | Run with debugger attached |
| Step Over | F8 | Execute current line, stay in method |
| Step Into | F7 | Enter the method being called |
| Step Out | Shift+F8 | Finish current method, return to caller |
| Resume | F9 | Run until next breakpoint |
| Evaluate Expression | Alt+F8 | Run arbitrary expression in current context |

- **Breakpoint:** A marker telling the debugger to pause execution at that line
- **When paused:** Inspect all variables in scope, call stack, heap objects — like freezing time
**Visual:** Screenshot-style mockup of IntelliJ debugger with breakpoint marked, variables panel showing values, call stack panel, and debug toolbar highlighted.
**Speaker note:** Once you get comfortable with the debugger, you'll never go back to print statements for anything complex. The ability to pause execution, inspect every variable, evaluate expressions, and step through code line by line is transformative. Every professional developer uses their IDE debugger daily. Let me walk you through the key workflows.

---

### Slide 15: Breakpoints — Types & Strategies
**Content:**
**Line Breakpoints (most common):**
```java
public int processOrder(Order order) {
    validate(order);           // Line 42 — set breakpoint here
    double total = calculate(order);  // Pause here to inspect 'total'
    return save(order, total);
}
```
- Set by clicking the left gutter in IntelliJ
- Execution pauses BEFORE the line executes

**Conditional Breakpoints — pause only when condition is true:**
- Right-click breakpoint → Condition: `order.getId() == 42`
- Invaluable for loops — pause only on the specific iteration causing the bug:
```java
for (Order order : orders) {
    process(order);  // Only pause when order.getTotal() < 0
}
// Conditional breakpoint: order.getTotal() < 0
```

**Exception Breakpoints — pause whenever exception is thrown:**
- Run → View Breakpoints → `+` → Java Exception Breakpoints → choose exception type
- Pause on `NullPointerException` to see exactly where it originates

**Method Breakpoints:**
- Pause when entering OR exiting a specific method
- Useful for third-party code you can't modify

**Logpoints (IntelliJ "Print" breakpoints):**
- Log a message without pausing — like System.out.println but without modifying code
**Visual:** IntelliJ gutter diagram showing different breakpoint icons (regular, conditional, exception, method breakpoints), each with a legend.
**Speaker note:** Conditional breakpoints are hugely valuable in loops. When you're debugging a list processing bug and the list has 10,000 items, setting a breakpoint inside the loop and manually clicking Resume 3,000 times isn't practical. Set a conditional breakpoint that only fires when the condition that causes the bug is true.

---

### Slide 16: Inspecting State — Variables, Watch Expressions & Call Stack
**Content:**
**Variables panel (automatically populated when paused):**
- Shows all local variables and their current values in scope
- Can expand objects to see all their fields
- Can modify variable values in-place to test hypotheses

**Watch Expressions — expressions evaluated live:**
```java
// You can add any valid Java expression
order.getItems().size()
order.getTotal() * 1.1
customer.getName().toUpperCase()
```
- Updated every time execution pauses
- Add via Variables panel → `+` icon

**Call Stack panel:**
- Shows the chain of method calls that led to current position
- Click any frame to inspect variables at that point in the call chain
- Critical for understanding "how did I get here?"

**Evaluate Expression (Alt+F8):**
```java
// Run any code in current context while paused
Collections.sort(order.getItems(), Comparator.comparing(Item::getPrice))
order.applyDiscount(0.1)  // Test logic without modifying code
```
- Doesn't modify the program permanently — sandbox evaluation
- Invaluable for testing fixes without restarting

**Display Variables tip:** Right-click variable → Add to Watches, or hover for inline value display
**Visual:** Annotated screenshot mockup of IntelliJ debug panels: Variables, Watches, Frames (call stack), each labeled with what they show.
**Speaker note:** The call stack is your "how did I get here?" panel. When you have a NullPointerException 10 levels deep, the call stack shows you exactly which method called which, all the way from main(). Click each frame and the variables panel updates to show that frame's local variables. You're time-traveling through the execution.

---

### Slide 17: Debugging Common Java Bugs
**Content:**
**Bug 1: NullPointerException**
```java
// NPE at line 42: customer.getAddress().getCity()
// Question: is customer null? or is getAddress() returning null?
// Debugger: pause at line 42, inspect 'customer' and 'customer.getAddress()'
```
- Set exception breakpoint on `NullPointerException` → see the exact object that's null
- Java 14+ helpful NullPointerException messages: "Cannot invoke getCity() because getAddress() returned null"

**Bug 2: Logic error in condition**
```java
// Bug: applying discount to orders OVER $100 instead of UNDER
if (order.getTotal() > 100) {  // Should be < 100
    applyDiscount(order);
}
// Debugger: watch order.getTotal() and evaluate 'order.getTotal() < 100' vs '> 100'
```

**Bug 3: Off-by-one in loop**
```java
for (int i = 0; i <= list.size(); i++) {  // Bug: should be i < list.size()
    process(list.get(i));  // IndexOutOfBoundsException on last iteration
}
// Debugger: inspect i and list.size() at each iteration
```

**Bug 4: Unexpected state in multithreaded code**
- Add logging at suspicious points (Day 9 synchronization issues)
- Use Thread.currentThread().getName() to identify which thread is executing
- IntelliJ Thread Dump: analyze all thread states simultaneously

**Debugging strategy:**
1. Reproduce the bug reliably
2. Form a hypothesis about the cause
3. Set breakpoints to test the hypothesis
4. Inspect state, confirm or refute hypothesis
5. Fix, verify, write a test to prevent regression

**Visual:** Four code blocks showing each bug type with annotations showing what the debugger reveals.
**Speaker note:** The scientific method applies to debugging — hypothesis, test, result. Don't randomly change code hoping the bug disappears. Form a specific hypothesis ("I think customer is null when the order has no billing address"), then design your debugging to test it. Setting breakpoints randomly is like guessing. Strategic breakpoints based on a hypothesis is debugging professionally.

---

### Slide 18: Debugging with Logging
**Content:**
- **Logging is not a substitute for the debugger** but is essential in production (can't attach a debugger to prod)
- **Java logging frameworks:** java.util.logging (built-in), Log4j, SLF4J + Logback (standard in Spring — Day 26)

**SLF4J + Logback (preview):**
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public Order processOrder(Order order) {
        log.debug("Processing order: id={}, total={}", order.getId(), order.getTotal());
        try {
            validate(order);
            log.info("Order {} validated successfully", order.getId());
            Order saved = save(order);
            log.info("Order {} saved: {}", order.getId(), saved);
            return saved;
        } catch (ValidationException e) {
            log.warn("Order {} failed validation: {}", order.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing order {}", order.getId(), e);
            throw new RuntimeException("Order processing failed", e);
        }
    }
}
```

**Log levels (low → high severity):** `TRACE < DEBUG < INFO < WARN < ERROR`
- **DEBUG:** Detailed diagnostic info (enabled in dev, disabled in prod)
- **INFO:** Normal operation events
- **WARN:** Unexpected but recoverable situations
- **ERROR:** Errors requiring attention
**Visual:** Log level spectrum from TRACE (verbose) to ERROR (critical), with icons for appropriate use cases.
**Speaker note:** Logging at the right level is a skill. Don't log everything at INFO — INFO should be meaningful operational events. Don't log nothing at DEBUG — DEBUG should give you enough information to diagnose issues in production without a debugger. The SLF4J parameterized logging `{}` syntax is important — it doesn't build the string unless the log level is enabled, so no performance penalty.

---

### Slide 19: Common Beginner Mistakes — Part 2
**Content:**

**Mistake 1: Closing streams in wrong order / forgetting to close**
```java
// WRONG — FileWriter flush may never happen if exception occurs
BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));
bw.write("data");
// If exception here, close() never called — data in buffer never written to disk!

// RIGHT — always try-with-resources
try (BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"))) {
    bw.write("data");
}  // Automatically flushed and closed
```

**Mistake 2: Catching Exception instead of IOException**
```java
// WRONG — too broad, hides bugs
try (BufferedReader br = new BufferedReader(new FileReader("f.txt"))) {
    String line = br.readLine();
    int value = Integer.parseInt(line);  // NumberFormatException — shouldn't be here!
} catch (Exception e) {  // Catches both IOException AND NumberFormatException
    System.err.println("Error");  // Which error? Unknown
}

// RIGHT — catch the specific exception you expect
try (BufferedReader br = new BufferedReader(new FileReader("f.txt"))) {
    String line = br.readLine();
    int value = Integer.parseInt(line);
} catch (IOException e) {
    System.err.println("File error: " + e.getMessage());
} catch (NumberFormatException e) {
    System.err.println("Invalid number in file: " + e.getMessage());
}
```

**Mistake 3: Serializing without serialVersionUID**
```java
// Without this, Java auto-generates UID based on class structure
// Add a field? UID changes. Old saved objects become unreadable.
private static final long serialVersionUID = 1L;  // Always declare explicitly
```
**Visual:** Three code blocks with wrong approaches marked in red, correct approaches in green.
**Speaker note:** The "catch Exception" antipattern is insidious because it appears safe — nothing blows up. But it masks bugs. A NumberFormatException in the middle of I/O code looks like an I/O error. You spend hours debugging the wrong thing. Catch the most specific exception type possible.

---

### Slide 20: Week 2 Complete — Java Fundamentals Mastery
**Content:**
**Week 2 Complete Progression:**
| Day | Topic | Key Skills Acquired |
|-----|-------|-------------------|
| Day 6 | Collections & Generics | ArrayList, HashMap, Set, Comparator, generics type safety |
| Day 7 | Exception Handling & I/O | try-catch-finally, custom exceptions, checked vs unchecked |
| Day 8 | Lambdas, Streams & DateTime | Functional interfaces, stream pipelines, LocalDate/Time |
| Day 9 | Multithreading | Thread pools, synchronization, CompletableFuture, deadlock prevention |
| Day 10 | Advanced Java | Big O, design patterns, memory model, GC, file I/O, debugging |

**What you can now do:**
- ✅ Analyze algorithm performance (Big O) and choose efficient data structures
- ✅ Apply 5 core design patterns (Singleton, Factory, Builder, Observer, Strategy)
- ✅ Understand Java memory (Stack/Heap), avoid common memory bugs
- ✅ Understand GC mechanics, choose appropriate reference types
- ✅ Read and write files professionally with try-with-resources
- ✅ Serialize objects and understand trade-offs vs JSON
- ✅ Debug Java applications professionally using IntelliJ debugger

**Week 3 Preview:** HTML & CSS (Day 11), JavaScript Fundamentals (Day 12), DOM Manipulation (Day 13), ES6+/Async JS (Day 14), TypeScript (Day 15). Frontend shift begins Monday!

**Visual:** Week 2 completion celebration visual, skill badges listed, preview of HTML/CSS/JavaScript icons for Week 3.
**Speaker note:** Week 2 is a significant milestone. You've covered all the core Java you need to write professional-grade backend code — not just "Java that works" but Java that scales, Java that communicates intent, Java that manages resources correctly. Week 3 pivots entirely to frontend. You'll apply similar thinking (complexity, patterns, debugging) in a JavaScript context. Patterns like Observer, Strategy, and Builder appear in React and Angular too — under different names but the same concepts.

---
