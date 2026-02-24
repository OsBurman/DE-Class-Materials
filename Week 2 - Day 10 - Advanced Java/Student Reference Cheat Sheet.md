# Day 10 — Advanced Java
## Quick Reference Guide

---

## 1. Big O — Time Complexity Reference

| Notation | Name | Example |
|----------|------|---------|
| O(1) | Constant | Array index access, HashMap get |
| O(log n) | Logarithmic | Binary search, TreeMap get |
| O(n) | Linear | Linear search, iterate list |
| O(n log n) | Log-linear | Merge sort, Arrays.sort |
| O(n²) | Quadratic | Bubble/insertion sort, nested loops |
| O(2ⁿ) | Exponential | Recursive Fibonacci |
| O(n!) | Factorial | Permutation generation |

### Common Data Structure Operations

| Structure | Access | Search | Insert | Delete |
|-----------|--------|--------|--------|--------|
| Array | O(1) | O(n) | O(n) | O(n) |
| ArrayList | O(1) | O(n) | O(1)* | O(n) |
| LinkedList | O(n) | O(n) | O(1)† | O(1)† |
| HashMap | — | O(1)* | O(1)* | O(1)* |
| TreeMap | — | O(log n) | O(log n) | O(log n) |
| HashSet | — | O(1)* | O(1)* | O(1)* |
| Stack/Queue | O(1) | O(n) | O(1) | O(1) |

*amortised / average case  †given a reference to the node

---

## 2. Design Patterns — Creational

### Singleton
Ensure exactly one instance of a class exists.

```java
// Thread-safe lazy initialization (Bill Pugh idiom)
public class DatabaseConnection {
    private DatabaseConnection() {}     // private constructor

    private static class Holder {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }

    public static DatabaseConnection getInstance() {
        return Holder.INSTANCE;
    }
}

// Enum Singleton (simplest thread-safe option)
public enum AppConfig {
    INSTANCE;
    private final Properties props = new Properties();
    public String get(String key) { return props.getProperty(key); }
}
```

### Factory Method
Delegate object creation to subclasses or factory methods.

```java
public interface Shape { double area(); }
public class Circle  implements Shape { ... }
public class Square  implements Shape { ... }

public class ShapeFactory {
    public static Shape create(String type) {
        return switch (type.toLowerCase()) {
            case "circle" -> new Circle(5.0);
            case "square" -> new Square(4.0);
            default       -> throw new IllegalArgumentException("Unknown: " + type);
        };
    }
}
Shape s = ShapeFactory.create("circle");
```

### Builder
Construct complex objects step-by-step; avoid telescoping constructors.

```java
public class User {
    private final String name;     // required
    private final String email;    // required
    private final int age;         // optional
    private final String phone;    // optional

    private User(Builder b) {
        this.name  = b.name;
        this.email = b.email;
        this.age   = b.age;
        this.phone = b.phone;
    }

    public static class Builder {
        private final String name;
        private final String email;
        private int age;
        private String phone;

        public Builder(String name, String email) {
            this.name = name; this.email = email;
        }
        public Builder age(int age)       { this.age   = age;   return this; }
        public Builder phone(String ph)   { this.phone = ph;    return this; }
        public User build()               { return new User(this); }
    }
}

User user = new User.Builder("Alice", "alice@example.com")
    .age(30)
    .phone("555-1234")
    .build();
```

---

## 3. Design Patterns — Behavioral

### Observer
Allow multiple objects to react to changes in another object.

```java
import java.util.*;

interface Observer { void update(String event); }

class EventBus {
    private final List<Observer> observers = new ArrayList<>();

    public void subscribe(Observer o)   { observers.add(o); }
    public void unsubscribe(Observer o) { observers.remove(o); }

    public void publish(String event) {
        for (Observer o : observers) o.update(event);
    }
}

// Usage
EventBus bus = new EventBus();
bus.subscribe(e -> System.out.println("Logger: " + e));
bus.subscribe(e -> sendEmail(e));
bus.publish("USER_CREATED");
```

### Strategy
Define a family of algorithms; make them interchangeable at runtime.

```java
interface SortStrategy {
    void sort(int[] data);
}

class BubbleSort  implements SortStrategy { public void sort(int[] d) { ... } }
class MergeSort   implements SortStrategy { public void sort(int[] d) { ... } }

class Sorter {
    private SortStrategy strategy;
    public void setStrategy(SortStrategy s) { this.strategy = s; }
    public void sort(int[] data) { strategy.sort(data); }
}

Sorter sorter = new Sorter();
sorter.setStrategy(new MergeSort());
sorter.sort(data);
// Switch at runtime:
sorter.setStrategy(new BubbleSort());
```

---

## 4. Design Patterns — Structural

### Decorator
Add behaviour to an object dynamically without altering its class.

```java
interface TextFormatter { String format(String text); }

class PlainText   implements TextFormatter { public String format(String t) { return t; } }
class BoldWrapper implements TextFormatter {
    private final TextFormatter inner;
    BoldWrapper(TextFormatter f) { this.inner = f; }
    public String format(String t) { return "<b>" + inner.format(t) + "</b>"; }
}
class ItalicWrapper implements TextFormatter {
    private final TextFormatter inner;
    ItalicWrapper(TextFormatter f) { this.inner = f; }
    public String format(String t) { return "<i>" + inner.format(t) + "</i>"; }
}

TextFormatter tf = new ItalicWrapper(new BoldWrapper(new PlainText()));
tf.format("Hello");   // <i><b>Hello</b></i>
```

---

## 5. JVM Memory Model

```
┌──────────────────────────────────────────────────────────┐
│  JVM Process                                              │
│  ┌───────────────────────────────────────────────────┐   │
│  │  Heap  (shared across all threads)                │   │
│  │  ┌─────────────┐  ┌───────────────────────────┐  │   │
│  │  │  Young Gen  │  │  Old Gen (Tenured)         │  │   │
│  │  │  ┌────┐     │  │  Long-lived objects        │  │   │
│  │  │  │Eden│     │  │  survive GC here           │  │   │
│  │  │  └────┘     │  └───────────────────────────-┘  │   │
│  │  │  S0 / S1    │                                   │   │
│  │  └─────────────┘                                   │   │
│  └───────────────────────────────────────────────────┘   │
│                                                           │
│  ┌──────────┐  ┌────────────┐  ┌──────────────────────┐  │
│  │  Stack   │  │  Stack     │  │  Metaspace (Java 8+)  │  │
│  │ Thread 1 │  │  Thread 2  │  │  Class metadata,      │  │
│  │ (frames) │  │  (frames)  │  │  static fields,       │  │
│  └──────────┘  └────────────┘  │  interned strings     │  │
│                                └──────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

| Region | Stores | Per-thread? |
|--------|--------|-------------|
| **Heap** | Object instances, arrays | No (shared) |
| **Stack** | Method frames, local vars, references | Yes |
| **Metaspace** | Class bytecode, static fields, method code | No (shared) |
| **PC Register** | Current bytecode instruction pointer | Yes |

---

## 6. Stack vs Heap

```java
public void example() {
    int x = 5;                     // x → Stack (primitive local)
    String s = "hello";            // s reference → Stack; "hello" object → Heap
    Person p = new Person("Alice"); // p reference → Stack; Person object → Heap
}
// When method returns: Stack frame popped, local vars gone
// Object on Heap: survives until no references remain → eligible for GC
```

---

## 7. Garbage Collection

**Mark-and-Sweep:**
1. **Mark** — trace all reachable objects from GC Roots
2. **Sweep** — reclaim memory of unmarked (unreachable) objects

**GC Roots:** local variables in active method frames, static fields, JNI references.

### GC Collectors (choose via JVM flag)

| Collector | Flag | Best For |
|-----------|------|----------|
| Serial GC | `-XX:+UseSerialGC` | Single-core, small heaps |
| Parallel GC | `-XX:+UseParallelGC` | Throughput-focused (Java 8 default) |
| G1 GC | `-XX:+UseG1GC` | Low-pause, large heaps (Java 9+ default) |
| ZGC | `-XX:+UseZGC` | Ultra-low latency (< 1ms pauses) |
| Shenandoah | `-XX:+UseShenandoahGC` | Low-pause (OpenJDK) |

---

## 8. Reference Types

```java
import java.lang.ref.*;

// Strong reference — default; prevents GC
Object obj = new Object();

// Soft reference — GC may collect when memory is low
SoftReference<Image> soft = new SoftReference<>(loadImage());
Image img = soft.get();   // null if GC collected it

// Weak reference — GC collects at next cycle (WeakHashMap uses this)
WeakReference<Widget> weak = new WeakReference<>(widget);

// Phantom reference — enqueued after object finalized; used for cleanup
ReferenceQueue<Object> queue = new ReferenceQueue<>();
PhantomReference<Object> phantom = new PhantomReference<>(obj, queue);
```

---

## 9. Useful JVM Flags

```bash
-Xms512m                   # initial heap size
-Xmx2g                     # maximum heap size
-Xss256k                   # thread stack size
-XX:+PrintGCDetails        # verbose GC output
-XX:+HeapDumpOnOutOfMemoryError  # dump heap on OOM
-XX:HeapDumpPath=/tmp/dump.hprof
-verbose:gc                # simple GC logging
-XX:+UseG1GC               # choose G1 collector
```

---

## 10. Serialization

```java
import java.io.*;

// Must implement Serializable (marker interface)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;   // version control for deserialization
    private String name;
    private transient String password;   // transient — NOT serialized
}

// Write to file
try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.ser"))) {
    oos.writeObject(user);
}

// Read from file
try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.ser"))) {
    User restored = (User) ois.readObject();
}
```

---

## 11. Enums

```java
public enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }
}

// Enum with fields and methods
public enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS  (4.869e+24, 6.0518e6);

    private final double mass;
    private final double radius;

    Planet(double mass, double radius) { this.mass = mass; this.radius = radius; }

    public double surfaceGravity() { return 6.67300E-11 * mass / (radius * radius); }
}

// Switch on enum
switch (day) {
    case MONDAY, TUESDAY -> startWork();
    case SATURDAY, SUNDAY -> relax();
}
Day.valueOf("MONDAY");   // enum constant from string
Day.values();            // all constants as array
day.name();              // "MONDAY"
day.ordinal();           // 0
```
