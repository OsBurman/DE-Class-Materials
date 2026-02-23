# Week 2 - Day 10, Part 1: Big O, Design Patterns & Java Memory Model
## 60-Minute Lecture Script

---

### [00:00–02:00] Welcome & Framing

Good morning everyone, and welcome to Day 10 — the final Friday of Week 2 and the last day of core Java before we pivot to frontend next week. Take a second to appreciate how far you've come. Monday of Week 1, you were writing your first `Hello World`. Today, you understand OOP, exception handling, lambdas and streams, multithreading with CompletableFuture — that's a serious body of knowledge.

Today is different in character from the other days. We're not learning new APIs. We're learning to *think* about code the way senior engineers do. Three big topics: algorithm complexity — how do you measure whether your code is fast enough to scale? Design patterns — how do you structure code so your team can understand and maintain it? And the Java Memory Model — what actually happens in memory when your program runs?

These three areas come up in nearly every technical Java interview. When a company asks you to "analyze the time complexity" or "what design pattern would you use here" or "explain Stack vs Heap," this is what they want. So pay close attention. Let's get started.

---

### [02:00–04:00] What is Big O Notation?

Let's start with Big O. Here's the core question: if your application works fine with 100 users today, will it still work with 100,000 users tomorrow? Big O is how we answer that mathematically.

Big O notation describes how the *runtime* or *memory usage* of an algorithm grows relative to the size of its input. We call the input size `n`. And we ask: if n doubles, what happens to my algorithm's performance?

Here's the crucial point — Big O is not about seconds. It's not "this runs in 50 milliseconds." It's about *shape* — does runtime grow linearly with input? Quadratically? Does it stay constant regardless of input size?

And we always drop constants and lower-order terms. If your algorithm does `3n² + 5n + 2` operations, the Big O is `O(n²)`. Why? Because as n gets very large, the `n²` term completely dominates. The constants become irrelevant at scale.

The question Big O answers: "If my input doubles, what happens?" And the answer shapes how your application performs under real-world load.

---

### [04:00–08:00] The Six Complexity Classes

There are six complexity classes you need to know cold. Let me walk through them from best to worst.

`O(1)` — constant time. Doesn't matter how big your input is, the operation takes the same amount of time. HashMap.get() is O(1). Array access by index is O(1). These are your best-case scenarios.

`O(log n)` — logarithmic. This is the complexity of binary search and tree-based operations like TreeMap. Every step cuts the problem in half. For n equals one million, log₂ of a million is only 20. Twenty comparisons to find an element in a million-element sorted array. Incredibly efficient.

`O(n)` — linear. You visit every element once. A single loop through an array. ArrayList.contains() — it checks every element until it finds a match. Performance grows directly proportional to input size.

`O(n log n)` — linearithmic. This is the complexity of efficient sorting algorithms — merge sort, and under the hood, Java's `Collections.sort()`. You can't generally sort faster than this comparison-based.

`O(n²)` — quadratic. Nested loops. Bubble sort. If you write a loop inside a loop both iterating to n, you have `n × n = n²` operations. With n equals 1,000, that's a million operations. With n equals 10,000, a hundred million. It doesn't scale.

`O(2ⁿ)` — exponential. Every additional input doubles the work. Naive recursive Fibonacci is the classic example. With n equals 40, that's over a trillion operations. These are practical only for tiny inputs.

Here's a concrete comparison for n equals 1,000: O(log n) is about 10 operations. O(n) is 1,000. O(n²) is 1,000,000. O(2ⁿ) at n=40 is already a trillion. The gap between these classes isn't subtle — it's the difference between fast and completely unusable at scale.

---

### [08:00–12:00] Reading Code to Find Complexity

Now, how do you look at code and determine its Big O? There are patterns that make this systematic.

Single loop — O(n). If you have one loop that iterates through your entire input, that's linear. You're touching every element once.

Nested loops — O(n²). If you have a loop inside a loop, and both iterate to n, that's quadratic. n iterations of the outer loop times n iterations of the inner loop equals n².

Halving the problem — O(log n). If each iteration cuts your search space in half, that's logarithmic. Binary search does this — you look at the middle element, determine which half the target is in, discard the other half, repeat.

Two sequential (not nested) loops — still O(n). Not O(2n). Because in Big O, we drop constants. Two loops of n is 2n, which simplifies to O(n).

Here's a practical example. You want to check if an array has any duplicate values. Approach one: two nested loops, compare every pair. That's O(n²). Approach two: create a HashSet, iterate once, check membership on each element. HashSet.contains() is O(1), so the whole thing is O(n) — linear. You traded some memory for a much faster algorithm.

This is the time-space trade-off. Approach two is O(n) time but O(n) space — the HashSet grows with your input. Approach one is O(n²) time but O(1) space — no extra memory. In an interview, you acknowledge both and explain the trade-off. Showing that reasoning demonstrates experience.

Let me also show you the Java collection complexities because this comes up constantly. ArrayList get by index is O(1) — direct memory access. LinkedList get by index is O(n) — it has to traverse from the head. That's why you'd choose ArrayList for random access. HashMap get and put are O(1) average — the hash function takes you directly to the bucket. TreeMap get and put are O(log n) — it's a balanced tree. So HashMap is faster for lookup, but TreeMap maintains sorted order.

---

### [12:00–14:00] Introduction to Design Patterns

Let's shift to design patterns. A design pattern is a reusable solution to a commonly occurring problem in software design. The concept was formalized in 1994 by the "Gang of Four" — Gamma, Helm, Johnson, and Vlissides — in their influential book.

Here's why they matter for you as a developer. First, vocabulary. If I say "we should use an Observer pattern here," senior developers immediately understand the structure — the relationship between subject and observers, the publish-subscribe semantics. You skip the entire explanation. Second, they're proven solutions. You're not inventing from scratch; you're applying a solution that has worked reliably across decades of software development. Third, they make intent clear. Code that uses a named pattern communicates its purpose to anyone who reads it later.

Today we cover five: Singleton and Factory and Builder from the Creational category — patterns about how objects are created. And Observer and Strategy from Behavioral — patterns about how objects communicate.

I want to emphasize one thing upfront: patterns are a vocabulary, not a mandate. Don't reach for a pattern when your problem doesn't call for it. The worst code I've seen is over-engineered with patterns that aren't needed. Use patterns to solve problems, not to demonstrate that you know patterns.

---

### [14:00–20:00] Singleton Pattern

Singleton: ensure a class has only one instance, and provide a global access point to it.

When does this make sense? When only one instance is logically correct — a configuration manager that reads from a config file (you want everyone reading the same config), a thread pool (you want one pool managing all tasks), a registry or cache shared across the application.

Here's the naive implementation that you'll see in textbooks and that doesn't work correctly in multithreaded code:

```java
public class Config {
    private static Config instance;
    private Config() {}
    public static Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }
}
```

The bug: two threads can both check `instance == null` at the same time, both see null, and both create an instance. You end up with two instances — defeating the entire purpose. You've seen this exact race condition pattern in Day 9.

The professional solution is the Bill Pugh Holder idiom:

```java
public class Config {
    private Config() {}
    private static class Holder {
        static final Config INSTANCE = new Config();
    }
    public static Config getInstance() {
        return Holder.INSTANCE;
    }
}
```

Why is this thread-safe? Java's class loading guarantees that a class is only initialized once. The `Holder` inner class is only loaded when `getInstance()` is first called. The static field `INSTANCE` is initialized exactly once by the class loader — no synchronization needed, no race condition possible. This is lazy initialization for free.

The even simpler modern approach: use an enum.

```java
public enum Config {
    INSTANCE;
    public String getDbUrl() { return "jdbc:postgresql://..."; }
}
```

Enum ensures single instantiation by definition. It's serialization-safe. It's reflection-safe. For most simple Singleton use cases, this is the cleanest solution.

One common mistake: overusing Singleton. Don't make your UserService a Singleton just because it's convenient to access globally. That creates hidden global state that's difficult to test and impossible to mock. In Week 5, Spring's dependency injection solves this properly — it manages single instances without you needing to implement the pattern yourself.

---

### [20:00–26:00] Factory and Builder Patterns

Factory pattern: centralize object creation, decouple the caller from the concrete class.

The problem: your code needs a Shape object. The specific shape depends on user input — "circle", "rectangle", "triangle". If you put that switch statement in every class that needs a Shape, you have duplication. When you add "pentagon," you have to update every class.

The Factory centralizes that creation logic:

```java
public class ShapeFactory {
    public static Shape create(String type) {
        switch (type) {
            case "circle":    return new Circle();
            case "rectangle": return new Rectangle();
            default: throw new IllegalArgumentException("Unknown shape: " + type);
        }
    }
}
```

Now callers write `ShapeFactory.create(userInput)` and never think about which class is instantiated. When you add "triangle," you update the factory and nothing else changes.

You use factory-style creation constantly in Java without realizing it. Spring's ApplicationContext is a factory — `getBean("userService")` returns the correct implementation. JDBC's `DriverManager.getConnection()` is a factory — it returns the right driver implementation based on your JDBC URL.

Builder pattern: readable construction for objects with many optional parameters.

The problem is constructor hell. Imagine `new Connection(host, port, user, pass, timeout, maxPool, minPool, autoCommit, fetchSize, isolation)`. What's the sixth parameter? You'd have to look at the documentation every time. And what if most parameters are optional?

The Builder solves this:

```java
Connection conn = new Connection.Builder("localhost")
    .port(5432)
    .user("admin")
    .password("secret")
    .maxPoolSize(10)
    .autoCommit(false)
    .build();
```

That reads like a sentence. You only set the parameters you care about. Defaults handle the rest. And `build()` can validate the configuration before construction — throwing an exception if required fields are missing.

You use Builder constantly in Java. `StringBuilder.append().append().toString()`. `HttpRequest.newBuilder().uri(...).header(...).build()`. Stream's method chaining is Builder-influenced. Lombok's `@Builder` annotation generates the entire Builder class automatically — in Week 5 you'll use this with your Spring entities.

---

### [26:00–34:00] Observer and Strategy Patterns

Observer pattern: when one object changes, notify all dependents automatically. Also called publish-subscribe.

The classic structure: a Subject (or Publisher) maintains a list of Observers (or Subscribers). When the Subject's state changes, it calls `update()` on all registered Observers.

```java
public interface Observer {
    void update(String event, Object data);
}

public class EventBus {
    private List<Observer> observers = new ArrayList<>();
    public void subscribe(Observer o) { observers.add(o); }
    public void unsubscribe(Observer o) { observers.remove(o); }
    public void publish(String event, Object data) {
        for (Observer o : observers) o.update(event, data);
    }
}
```

When `publish("ORDER_PLACED", order)` is called, every subscribed observer — EmailNotifier, SmsNotifier, InventoryUpdater, AuditLogger — all get notified. The EventBus doesn't know anything about what the observers do. It just knows they all implement Observer and calls update().

This decoupling is powerful. Want to add a new notification type? Write a new Observer class, subscribe it. The EventBus doesn't change. The other observers don't change.

You see Observer everywhere. Java's ActionListener for UI events — Observer pattern. Spring's ApplicationEventPublisher — Observer pattern. Kafka, which you'll cover in Week 8, is essentially Observer pattern at distributed scale. React's state management (conceptually) — Observer pattern. When you learn Angular's `EventEmitter` — Observer pattern.

Strategy pattern: encapsulate a family of algorithms and make them interchangeable.

The problem: you have multiple ways to accomplish the same task, and you want to choose between them at runtime without if-else chains.

```java
public interface SortStrategy {
    void sort(int[] data);
}

public class Sorter {
    private SortStrategy strategy;
    public Sorter(SortStrategy strategy) { this.strategy = strategy; }
    public void setStrategy(SortStrategy s) { this.strategy = s; }
    public void sort(int[] data) { strategy.sort(data); }
}

Sorter sorter = new Sorter(new MergeSort());
sorter.sort(largeArray);          // O(n log n)

sorter.setStrategy(new BubbleSort());
sorter.sort(tinyArray);           // Simple sort for small input
```

The Sorter doesn't know which algorithm is running. You swap strategies without changing the Sorter.

Here's the beautiful part: with Java 8 lambdas, Strategy becomes trivial to implement. `Comparator` is a Strategy — you pass in a comparison function. `Collections.sort(list, (a, b) -> a.getName().compareTo(b.getName()))` — that lambda IS the strategy. You're selecting an algorithm at runtime with one line of code.

---

### [34:00–40:00] Java Memory Model — Stack and Heap

Let's shift to memory. When your Java program runs, the JVM divides memory into two main regions: the Stack and the Heap. Understanding this distinction is fundamental to understanding Java.

The Stack holds method call frames and local variables. When you call a method, a new frame is pushed onto the stack with space for its local variables and parameters. When the method returns, the frame is popped — the variables are gone. It's automatic, extremely fast (Last-In-First-Out, very cache-friendly), and each thread has its own private stack.

The Heap holds all objects — everything created with the `new` keyword. Instance variables, arrays, String contents — all on the heap. The heap is shared across all threads (which is why Day 9's synchronization was necessary). It's managed by the Garbage Collector.

Here's the key semantic that trips up beginners: when you write `Person p = new Person("Alice")`, the Person object is on the heap. The variable `p` is on the stack. `p` holds a *reference* — a memory address — pointing to the heap object. It's like a pointer in C, but Java manages the dereferencing for you.

This has important implications. When you pass an object to a method:

```java
void rename(Person p) {
    p.setName("Bob");   // Modifies the heap object — VISIBLE to caller
    p = new Person("Charlie");  // Reassigns the local stack variable — NOT visible to caller
}
```

The first line modifies the actual object on the heap — the caller sees this change. The second line reassigns the local stack reference — the caller's reference still points to the original object. Java passes references by value. You can mutate what the reference points to. You cannot change where the caller's reference points.

---

### [40:00–46:00] Stack Frames and Heap Objects in Detail

Let me trace through a concrete example to make this tangible.

```java
public static void main(String[] args) {
    int count = 0;
    List<String> names = new ArrayList<>();
    names.add("Alice");
    count = names.size();
}
```

The `main` stack frame holds: `args` (a reference to the String array on the heap), `count` (a primitive int — value 0, stored directly on the stack), `names` (a reference to an ArrayList object on the heap).

When `new ArrayList<>()` executes, an ArrayList object is created on the heap, and the reference is stored in `names` on the stack.

When `names.add("Alice")` executes, the string "Alice" — actually, since it's a literal, it goes to the String pool in the heap. The ArrayList now has an internal array holding a reference to "Alice".

When `main()` returns, the `count`, `names` reference, and `args` reference are all popped off the stack automatically. The ArrayList object on the heap now has no live references — the GC will collect it eventually.

The String pool deserves special mention. String literals like `"hello"` are interned — stored once in a special region of the heap and shared. If you write `String s1 = "hello"` and `String s2 = "hello"` in two places, both `s1` and `s2` point to the same String object. This is why `s1 == s2` is true for string literals — they're the same object. But `new String("hello")` bypasses the pool and creates a new heap object — `s1 == s3` would be false even with the same content. This is why you always use `.equals()` for String comparison, never `==`.

---

### [46:00–52:00] Java Memory Model — Visibility and Happens-Before

The full Java Memory Model (JMM) is about more than Stack and Heap — it defines the rules for how threads interact through shared memory.

Modern CPUs have caches. Each CPU core has its own L1 and L2 caches. When a thread writes a value to a shared variable, that write might be in the CPU's cache and not yet flushed to main memory. Another thread on a different core reads from main memory or its own cache — and might see a stale value.

This is exactly what the `volatile` keyword and `synchronized` from Day 9 address. They're not just about mutual exclusion — they're about *memory visibility*.

The JMM formalizes this with the concept of "happens-before." If action A happens-before action B, then all memory effects of A are guaranteed to be visible to B.

Key happens-before guarantees: Entering a `synchronized` block happens-before executing any code inside it. Exiting a `synchronized` block happens-before any thread subsequently entering the same synchronized block on the same monitor. Writing to a `volatile` variable happens-before any subsequent read of that variable. `Thread.start()` happens-before any action in the started thread.

Why does this matter? Without these guarantees, the JVM and CPU are free to reorder operations for performance. A write in one thread might appear in a different order to another thread. These reorderings are invisible in single-threaded code — they don't change observable behavior. But in multithreaded code, they can produce bizarre bugs that only appear on multi-core machines under load.

When you use `synchronized` or `volatile` correctly, the JMM guarantees visibility and ordering. When you don't, you're at the mercy of hardware reordering and caching behavior that changes between JVM implementations and CPU architectures.

---

### [52:00–56:00] Common Memory Mistakes

A few critical mistakes to avoid.

String concatenation in a loop:

```java
// WRONG — O(n²) — each + creates a new String
String result = "";
for (String s : list) result += s;

// RIGHT — O(n) — StringBuilder reuses its internal buffer
StringBuilder sb = new StringBuilder();
for (String s : list) sb.append(s);
String result = sb.toString();
```

Every time you use `+` on a String, Java creates a new String object — allocating memory, copying content. In a loop of n iterations, you're creating n String objects. The total work is 1 + 2 + 3 + ... + n, which is O(n²). StringBuilder maintains a resizable char array and appends in-place. Same result, O(n) time.

Integer comparison with `==`:

```java
Integer a = 200;
Integer b = 200;
System.out.println(a == b);      // false — different objects
System.out.println(a.equals(b)); // true  — correct
```

Java caches Integer objects from -128 to 127. For values in that range, `==` accidentally works because they're the same cached object. For values outside that range, each autoboxing creates a new object. This inconsistency catches people off guard. Rule: use `.equals()` for all object comparison.

Passing objects to methods — remember what's by value:

The reference is copied, not the object. Mutating fields of the object through the copied reference affects the original. Reassigning the reference in the method does not affect the caller.

---

### [56:00–60:00] Summary & Part 2 Preview

Let me summarize what we've covered in Part 1.

Big O — you now have the vocabulary to analyze algorithm complexity. O(1) constant, O(log n) logarithmic, O(n) linear, O(n log n) linearithmic, O(n²) quadratic, O(2ⁿ) exponential. Read code: single loop is linear, nested loops are quadratic, halving is logarithmic. Know your Java collection complexities — HashMap is O(1), TreeMap is O(log n), ArrayList get is O(1), LinkedList get is O(n).

Design Patterns — Singleton for single-instance resources, use Bill Pugh Holder or enum for thread safety. Factory to centralize and decouple object creation. Builder for readable multi-parameter construction with validation. Observer for publish-subscribe event notification. Strategy for interchangeable algorithms — and with lambdas, Strategy is effortless.

Memory Model — Stack holds local variables and method frames, per-thread, auto-managed, fast. Heap holds all objects, shared, GC-managed, configurable. References are stack variables pointing to heap objects — Java passes references by value. The JMM's happens-before rules underlie why synchronized and volatile work.

After the break, Part 2 covers: exactly how the Garbage Collector works and which algorithm Java uses, JVM tuning flags for production, the four reference types that let you interact with GC, File I/O with Readers and Writers and try-with-resources, object serialization, and professional debugging with IntelliJ — breakpoints, step-through, watch expressions, conditional breakpoints.

Take a break, come back ready. We're finishing Week 2 strong.

---
