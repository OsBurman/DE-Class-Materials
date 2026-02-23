# Week 2 - Day 10, Part 1: Big O, Design Patterns & Java Memory Model
## Slide Descriptions (60-minute lecture)

---

### Slide 1: Title Slide
**Title:** "Advanced Java: Complexity, Patterns & Memory"
**Subtitle:** "Week 2 Friday — Thinking Like a Professional Developer"
**Visual:** Split graphic — a complexity graph curve on the left, a UML-style pattern diagram on the right, with a JVM memory model diagram in the center.
**Speaker note:** Welcome to Day 10 — the final day of Java fundamentals. Today we zoom out from writing code that works to writing code that scales, code that communicates intent to your team, and code that uses memory wisely. Three big topics: Big O complexity analysis, design patterns, and the Java Memory Model. These are the concepts that separate junior developers from mid-level and senior engineers in interviews and on the job.

---

### Slide 2: Day 10 Roadmap & Week 2 Recap
**Content:**
- **Week 2 so far:** Collections (Day 6) → Exception Handling & I/O (Day 7) → Lambdas & Streams (Day 8) → Multithreading (Day 9)
- **Today's two parts:**
  - Part 1: Algorithm complexity (Big O), design patterns, Java Memory Model & Stack vs Heap
  - Part 2: Garbage collection, JVM tuning, reference types, File I/O, serialization, debugging
- **Why this matters:** Every professional Java interview touches at least two of today's topics
- **Connection:** Day 9's thread pools and concurrent collections perform differently at scale — today you'll understand *why*
**Visual:** Timeline bar showing Week 2 days, with Day 10 highlighted as the capstone.
**Speaker note:** This is the last pure Java day. Week 3 starts frontend (HTML/CSS). Everything we cover today is "interview territory" — Big O, design patterns, and memory management are consistently asked in Java technical screens. Pay close attention.

---

### Slide 3: What is Big O Notation?
**Content:**
- **Definition:** A mathematical notation describing how algorithm runtime or memory usage grows relative to input size
- **What it measures:** Growth rate — not exact time, but how time scales
- **Why we care:** An O(n²) algorithm on 1,000 items = 1,000,000 operations. On 1,000,000 items = 1,000,000,000,000 operations (unusable)
- **Key insight:** Big O describes the *worst case* upper bound unless specified otherwise
- **Notation examples:**
  - `f(n) = 3n² + 5n + 2` → Big O: `O(n²)` (drop constants and lower-order terms)
- **The question Big O answers:** "If my input doubles, what happens to my program's performance?"
**Visual:** A simple graph with input size n on x-axis and operations on y-axis — curves labeled O(1), O(log n), O(n), O(n²).
**Speaker note:** Big O is not about milliseconds. It's about shape — how does your algorithm behave as the problem gets bigger? A fast O(n²) algorithm will always lose to a slow O(n log n) algorithm given a large enough input.

---

### Slide 4: Common Complexity Classes — The Big Six
**Content:**
| Complexity | Name | Example |
|-----------|------|---------|
| O(1) | Constant | HashMap.get(), array access by index |
| O(log n) | Logarithmic | Binary search, TreeMap operations |
| O(n) | Linear | Single loop through array, ArrayList.contains() |
| O(n log n) | Linearithmic | Merge sort, Collections.sort() |
| O(n²) | Quadratic | Nested loops (bubble sort, brute-force search) |
| O(2ⁿ) | Exponential | Recursive Fibonacci without memoization |

- **Visual order (best to worst):** O(1) < O(log n) < O(n) < O(n log n) < O(n²) < O(2ⁿ)
- **Reality check:** For n = 1,000:
  - O(log n) ≈ 10 operations
  - O(n) = 1,000 operations
  - O(n²) = 1,000,000 operations
**Visual:** Color-coded table with green for good (O(1), O(log n)), yellow for acceptable (O(n), O(n log n)), red for problematic (O(n²), O(2ⁿ)).
**Speaker note:** Memorize this table. These six classes come up in every technical interview. When an interviewer asks "what's the time complexity?", they want one of these answers.

---

### Slide 5: Analyzing Code — Reading Big O in Practice
**Content:**
**Rule 1: Single loop = O(n)**
```java
for (int i = 0; i < n; i++) {
    doWork();   // O(n) — one unit of work per element
}
```

**Rule 2: Nested loops = O(n²)**
```java
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        doWork();   // O(n²) — n × n iterations
    }
}
```

**Rule 3: Halving the problem = O(log n)**
```java
int low = 0, high = n;
while (low <= high) {
    int mid = (low + high) / 2;
    low = mid + 1;  // or high = mid - 1   → O(log n)
}
```

**Rule 4: Drop constants — O(2n) = O(n)**
```java
for (int i = 0; i < n; i++) { doWork(); }
for (int j = 0; j < n; j++) { doWork(); }
// Two loops = O(2n) = O(n) — constants are dropped
```
**Visual:** Each code block labeled with its complexity class.
**Speaker note:** The goal isn't memorizing formulas — it's reading code and spotting the pattern. One loop? Linear. Two nested loops? Quadratic. Each iteration cuts the problem in half? Logarithmic. These patterns are all you need for 90% of interview questions.

---

### Slide 6: Space Complexity & Practical Analysis
**Content:**
- **Space complexity:** How much *extra memory* the algorithm uses as n grows
- **O(1) space:** Algorithm uses fixed extra memory regardless of input (in-place sort)
- **O(n) space:** Creates a new structure proportional to input (copying an array, recursion call stack)

**Example — space analysis:**
```java
// O(1) space — just two pointers, no extra storage
public int sum(int[] arr) {
    int total = 0;
    for (int x : arr) total += x;
    return total;
}

// O(n) space — new list proportional to input
public List<Integer> doubled(List<Integer> input) {
    List<Integer> result = new ArrayList<>(input.size()); // n extra slots
    for (int x : input) result.add(x * 2);
    return result;
}
```

**Common collection complexities (Java):**
| Operation | ArrayList | LinkedList | HashMap |
|-----------|-----------|------------|---------|
| get(i) | O(1) | O(n) | O(1) avg |
| add(end) | O(1) amortized | O(1) | O(1) avg |
| contains() | O(n) | O(n) | O(1) avg |
| remove(i) | O(n) | O(1) at known node | O(1) avg |
**Visual:** Table with the Java collection operations color-coded for quick reference.
**Speaker note:** Space vs time trade-off is real. Caching results uses more memory but reduces time. Sorting in-place saves memory but may be slower. Understanding both dimensions helps you make informed decisions.

---

### Slide 7: Big O in Interviews — What Interviewers Really Want
**Content:**
**The two-part answer every interviewer expects:**
1. State the time complexity
2. State the space complexity

**Walk-through example — checking for duplicates:**
```java
// Approach 1 — nested loops
public boolean hasDuplicates(int[] arr) {
    for (int i = 0; i < arr.length; i++)
        for (int j = i + 1; j < arr.length; j++)
            if (arr[i] == arr[j]) return true;  // O(n²) time, O(1) space
    return false;
}

// Approach 2 — HashSet
public boolean hasDuplicates(int[] arr) {
    Set<Integer> seen = new HashSet<>();
    for (int x : arr) {
        if (seen.contains(x)) return true;
        seen.add(x);
    }
    return false;  // O(n) time, O(n) space — trade memory for speed
}
```
- **Trade-off:** Approach 2 is faster but uses extra memory — both answers are valid, context matters
- **Pro tip:** Always mention the trade-off, not just the "better" answer
**Visual:** Side-by-side comparison with time and space complexity labeled for each approach.
**Speaker note:** In a real interview, showing you understand the trade-off is worth more than just arriving at the "optimal" solution. Thinking out loud about time vs space shows maturity. Practice saying "The time complexity is O(n) and the space complexity is O(n) because of the HashSet."

---

### Slide 8: Introduction to Design Patterns
**Content:**
- **Definition:** Reusable solutions to commonly occurring software design problems
- **Origin:** "Gang of Four" book (1994) — Gamma, Helm, Johnson, Vlissides
- **Why patterns matter:**
  - Common vocabulary — say "I used the Observer pattern" and senior devs immediately understand
  - Proven solutions — you're not inventing from scratch
  - Maintainability — patterns make intent clear

**Three categories:**
| Category | Purpose | Examples |
|----------|---------|---------|
| Creational | How objects are created | Singleton, Factory, Builder |
| Structural | How objects are composed | Adapter, Decorator, Facade |
| Behavioral | How objects communicate | Observer, Strategy, Command |

- **Today's focus:** Singleton, Factory, Builder (creational) + Observer, Strategy (behavioral)
- **Key principle:** Patterns are a *vocabulary*, not a mandate — don't force them where they don't fit
**Visual:** Three-column diagram showing each pattern category with icons (factory for creational, building blocks for structural, arrows for behavioral).
**Speaker note:** Design patterns are one of the most-asked interview topics. "Describe the Singleton pattern and when you'd use it" is a classic Java interview question. Knowing five patterns deeply is better than knowing twenty patterns shallowly.

---

### Slide 9: Singleton Pattern
**Content:**
- **Problem:** Some objects should only ever exist once — database connection pool, application config, thread pool manager
- **Solution:** Control instantiation so only one instance is created, provide global access point
- **Naive (broken) implementation:**
```java
public class Config {
    private static Config instance;  // BAD — not thread-safe
    private Config() {}
    public static Config getInstance() {
        if (instance == null)
            instance = new Config();  // Race condition!
        return instance;
    }
}
```
- **Thread-safe implementation (Bill Pugh — preferred):**
```java
public class Config {
    private Config() {}

    private static class Holder {
        static final Config INSTANCE = new Config();  // Initialized once by classloader
    }

    public static Config getInstance() {
        return Holder.INSTANCE;  // Thread-safe, lazy, no synchronization overhead
    }
}
```
- **Enum Singleton (modern Java):**
```java
public enum Config {
    INSTANCE;
    public String getDbUrl() { return "jdbc:..."; }
}
```
**Visual:** UML class diagram showing Singleton with private constructor, static instance, and public getInstance() method.
**Speaker note:** Singleton is one of the most misused patterns. The key insight is that the naive version has a race condition — two threads can both see `instance == null` and both create an instance. The Bill Pugh holder idiom exploits Java's class loading guarantees to be thread-safe without synchronization. The enum version is the most bulletproof but less common in legacy code.

---

### Slide 10: Factory Pattern
**Content:**
- **Problem:** Creating objects whose type isn't known until runtime, or when the creation logic is complex and you want to centralize it
- **Solution:** A factory method or class that creates objects — callers ask for objects without knowing which class is instantiated

**Simple Factory:**
```java
public class ShapeFactory {
    public static Shape create(String type) {
        switch (type) {
            case "circle":    return new Circle();
            case "rectangle": return new Rectangle();
            case "triangle":  return new Triangle();
            default: throw new IllegalArgumentException("Unknown: " + type);
        }
    }
}

// Caller
Shape s = ShapeFactory.create("circle");
s.draw();  // Caller doesn't know which class is used
```

**Factory Method Pattern (OOP approach):**
```java
public abstract class Logger {
    public abstract LogWriter createWriter();  // Factory method — subclass decides

    public void log(String msg) {
        createWriter().write(msg);  // Uses factory method
    }
}

public class FileLogger extends Logger {
    public LogWriter createWriter() { return new FileWriter(); }
}
```
- **When to use:** Object creation is complex, type varies at runtime, want to decouple creation from usage
**Visual:** UML diagram showing Factory class creating different Product implementations, with caller only seeing the Product interface.
**Speaker note:** The key principle is "program to an interface, not an implementation." The factory hides which class is being created. Spring's ApplicationContext is essentially a factory — you say `getBean("myService")` and Spring returns the correct implementation. You'll use factory-style patterns constantly in Spring.

---

### Slide 11: Builder Pattern
**Content:**
- **Problem:** Objects with many optional parameters — constructors become unwieldy
  - `new Pizza(12, true, false, true, false, true, "thin", "marinara")` — what does each boolean mean?
- **Solution:** A separate Builder class with fluent, readable method chaining

**Classic Builder:**
```java
public class Pizza {
    private final int size;
    private final boolean cheese, pepperoni, mushrooms;
    private final String crust;

    private Pizza(Builder builder) {
        this.size = builder.size;
        this.cheese = builder.cheese;
        this.pepperoni = builder.pepperoni;
        this.mushrooms = builder.mushrooms;
        this.crust = builder.crust;
    }

    public static class Builder {
        private int size;
        private boolean cheese, pepperoni, mushrooms;
        private String crust = "regular";

        public Builder size(int size)           { this.size = size; return this; }
        public Builder cheese()                 { this.cheese = true; return this; }
        public Builder pepperoni()              { this.pepperoni = true; return this; }
        public Builder mushrooms()              { this.mushrooms = true; return this; }
        public Builder crust(String crust)      { this.crust = crust; return this; }
        public Pizza build()                    { return new Pizza(this); }
    }
}

// Usage — reads like natural language
Pizza p = new Pizza.Builder()
    .size(12)
    .cheese()
    .pepperoni()
    .crust("thin")
    .build();
```
- **Real-world Java examples:** `StringBuilder`, `HttpRequest.newBuilder()`, Lombok's `@Builder`
**Visual:** Builder call chain with arrows showing how each method returns the Builder (enabling chaining), culminating in build().
**Speaker note:** You'll see Builder constantly in real Java code — HttpClient, StringBuilder, Stream operations. Lombok's @Builder annotation auto-generates the entire Builder class with one annotation. When you see `.withX().withY().build()` or `.doA().doB().execute()`, that's the Builder pattern.

---

### Slide 12: Observer Pattern
**Content:**
- **Problem:** One object changes state; many other objects need to be notified and update themselves
- **Solution:** Subject (publisher) maintains a list of Observers (subscribers); notifies all on state change
- **Also called:** Publish-Subscribe, Event Listener

```java
// Observer interface
public interface Observer {
    void update(String event, Object data);
}

// Subject
public class EventBus {
    private List<Observer> observers = new ArrayList<>();

    public void subscribe(Observer o)   { observers.add(o); }
    public void unsubscribe(Observer o) { observers.remove(o); }

    public void publish(String event, Object data) {
        for (Observer o : observers)
            o.update(event, data);  // Notify all observers
    }
}

// Concrete observers
public class EmailNotifier implements Observer {
    public void update(String event, Object data) {
        System.out.println("Email: " + event + " → " + data);
    }
}

// Usage
EventBus bus = new EventBus();
bus.subscribe(new EmailNotifier());
bus.subscribe(new SmsNotifier());
bus.publish("ORDER_PLACED", orderId);  // Both notified
```
- **Java built-in examples:** ActionListener in Swing, Java Beans PropertyChangeListener
- **Modern equivalent:** Reactive streams (RxJava, Spring WebFlux) are Observer pattern at scale
**Visual:** UML with Subject holding Observer list, arrows pointing to multiple ConcreteObserver implementations.
**Speaker note:** Observer is everywhere in GUI frameworks, event systems, and messaging. When you learn Spring later (Day 24-25), Spring's ApplicationEventPublisher is Observer pattern. React's state management — Observer pattern. Kafka (Day 39) — Observer pattern. This is one of the most practically useful patterns.

---

### Slide 13: Strategy Pattern
**Content:**
- **Problem:** You have multiple algorithms for the same task; want to switch between them at runtime without if/else chains
- **Solution:** Define a family of algorithms, encapsulate each, make them interchangeable

```java
// Strategy interface
public interface SortStrategy {
    void sort(int[] data);
}

// Concrete strategies
public class BubbleSort implements SortStrategy {
    public void sort(int[] data) { /* O(n²) */ }
}
public class MergeSort implements SortStrategy {
    public void sort(int[] data) { /* O(n log n) */ }
}
public class QuickSort implements SortStrategy {
    public void sort(int[] data) { /* O(n log n) avg */ }
}

// Context — holds a strategy, delegates to it
public class Sorter {
    private SortStrategy strategy;

    public Sorter(SortStrategy strategy) { this.strategy = strategy; }
    public void setStrategy(SortStrategy s) { this.strategy = s; }

    public void sort(int[] data) {
        strategy.sort(data);  // Delegates — context doesn't know which algo runs
    }
}

// Usage — switch strategy at runtime
Sorter sorter = new Sorter(new MergeSort());
sorter.sort(largeArray);

sorter.setStrategy(new BubbleSort());  // Switch strategy
sorter.sort(smallArray);
```
- **Lambda shortcut (Java 8+):** Functional interfaces make Strategy trivial — `Comparator.comparing()` is Strategy
**Visual:** UML showing Context → Strategy interface, with three ConcreteStrategy implementations pointing up to the interface.
**Speaker note:** Strategy is the pattern behind `Comparator` — you pass in a comparison strategy. `Collections.sort(list, comparator)` — that comparator is a Strategy. Spring Security's authentication strategies, payment processors with different gateways — Strategy is everywhere. With lambdas, you implement a strategy in one line.

---

### Slide 14: Design Patterns — Common Mistakes
**Content:**

**Mistake 1: Overusing Singleton**
```java
// WRONG — making everything a Singleton
public class UserService {
    private static UserService instance;  // Why? Multiple UserService instances are fine
    // ...
}
```
- **Rule:** Use Singleton only when exactly one instance makes logical sense (config, registry, thread pool)
- **Problem:** Hidden global state makes testing difficult; hard to inject mocks

**Mistake 2: Forgetting to make Singleton thread-safe**
```java
// WRONG — race condition
public static Config getInstance() {
    if (instance == null) {
        instance = new Config();  // Two threads can both reach here simultaneously
    }
    return instance;
}
// RIGHT — use Bill Pugh Holder or enum
```

**Mistake 3: Over-engineering with patterns**
- Don't use Factory when you only have one implementation
- Don't use Strategy when you'll never switch algorithms
- Rule: Patterns solve problems. Don't create problems to use patterns.

**Mistake 4: Forgetting Builder validation**
```java
// RIGHT — validate in build()
public Pizza build() {
    if (size <= 0) throw new IllegalStateException("Size must be positive");
    return new Pizza(this);  // Validate before constructing
}
```
**Visual:** Anti-pattern code blocks with red X marks, correct patterns with green checkmarks.
**Speaker note:** Overusing Singleton is probably the #1 pattern mistake I see from new developers. If you see a class where everything is a static Singleton, that's a code smell — it becomes very hard to test and very hard to refactor. Prefer dependency injection (Spring handles this for you in Week 5).

---

### Slide 15: Java Memory Model — The Two Regions
**Content:**
- **JVM memory is divided into two main regions:** Stack and Heap
- **Stack:**
  - Stores: local variables, method call frames, primitive values, references (not objects)
  - Allocation: Automatic — push when method called, pop when method returns
  - Size: Fixed per thread (typically 512KB–1MB)
  - Access: Extremely fast (LIFO, CPU cache-friendly)
  - Thread-safe: Each thread has its own stack
  - Error: `StackOverflowError` (infinite recursion)

- **Heap:**
  - Stores: All objects (`new` keyword), instance variables, arrays
  - Allocation: Dynamic — allocated with `new`, reclaimed by Garbage Collector
  - Size: Configurable (default hundreds of MB, can be set with JVM flags)
  - Access: Slower than stack (dynamic allocation, GC overhead)
  - Shared: All threads share the heap (concurrency concerns from Day 9)
  - Error: `OutOfMemoryError` (heap exhausted)

**Quick rule:**
- `int x = 5;` → x lives on the stack
- `int[] arr = new int[10];` → reference `arr` on stack, array object on heap
**Visual:** Side-by-side diagram: Stack on left (frames stacked like pancakes with method calls), Heap on right (scattered object bubbles). Arrow from stack reference pointing to heap object.
**Speaker note:** This is one of the most commonly misunderstood aspects of Java. The reference variable lives on the stack — but the object it points to lives on the heap. When you pass an object to a method, you're passing the reference (stack), not the object (heap). The object doesn't move — only the reference is copied.

---

### Slide 16: Stack Memory — Method Call Frames
**Content:**
**What happens when a method is called:**
```java
public class StackDemo {
    public static void main(String[] args) {
        int x = 10;              // x pushed to main frame on stack
        int result = square(x);  // square() frame pushed on stack
        System.out.println(result);
    }

    public static int square(int n) {  // n pushed to square frame
        int sq = n * n;                // sq pushed to square frame
        return sq;                     // square frame popped — sq, n gone
    }
}
```
**Stack evolution:**
1. `main` frame: `{x=10, result=?}`
2. `square` frame pushed: `{n=10, sq=100}` on top of main
3. `square` returns → frame popped, result = 100
4. `main` continues: `{x=10, result=100}`

**StackOverflowError — infinite recursion:**
```java
public int infiniteRecurse(int n) {
    return infiniteRecurse(n + 1);  // Stack grows forever → StackOverflowError
}
```
- **Each thread has its own stack** → thread-safe for local variables
- **Stack size:** `-Xss` JVM flag (e.g., `-Xss2m` = 2MB stack per thread)
**Visual:** Animated-style diagram showing stack frames being pushed and popped as methods are called and return.
**Speaker note:** This is why recursive algorithms can cause StackOverflowError — every recursive call pushes another frame. With multithreading (Day 9), each thread has its own private stack, which is why local variables are thread-safe. Only heap objects (shared state) need synchronization.

---

### Slide 17: Heap Memory — Objects & References
**Content:**
**Object lifecycle on the heap:**
```java
String name = new String("Alice");  // "Alice" object on heap, 'name' ref on stack
int[] numbers = {1, 2, 3, 4, 5};   // Array on heap, 'numbers' ref on stack

name = "Bob";  // 'name' ref now points to new "Bob" object; "Alice" is unreferenced
               // GC will eventually collect "Alice"
```

**Reference variables vs objects:**
```java
Person p1 = new Person("Alice");   // p1 (stack) → Person@100 (heap)
Person p2 = p1;                    // p2 (stack) → Person@100 (heap) — SAME object!

p2.setName("Bob");
System.out.println(p1.getName()); // "Bob" — both refs point to same object!
```

**Heap generations (preview for GC slide):**
- **Young Generation:** Newly created objects live here initially
  - Eden space: Where objects are first allocated
  - Survivor spaces (S0, S1): Objects that survived minor GC
- **Old Generation (Tenured):** Long-lived objects promoted from Young
- **Metaspace (Java 8+):** Class metadata, replaces PermGen

**Visual:** Heap diagram divided into Young Gen (with Eden and Survivor spaces) and Old Gen (Tenured). Stack on the left with arrows pointing into the heap.
**Speaker note:** The generational heap design is the key to understanding why GC is efficient — most objects die young ("infant mortality hypothesis"). Creating thousands of short-lived objects (like StringBuilder in a loop) is fine because they're quickly collected. The problem is long-lived objects that fill up the old generation and trigger expensive full GC.

---

### Slide 18: String Pool — A Special Heap Region
**Content:**
- **String Pool (String Intern Pool):** A special region in the heap for string literals
- **String literals are pooled:** `"hello"` appears once, all references share it

```java
String s1 = "hello";        // Pulls from string pool
String s2 = "hello";        // Same object in pool — no new allocation
String s3 = new String("hello"); // Forces new heap object — NOT from pool

System.out.println(s1 == s2);    // true — same pool object
System.out.println(s1 == s3);    // false — different heap objects
System.out.println(s1.equals(s3)); // true — same content

String s4 = s3.intern();    // Force s3 into pool
System.out.println(s1 == s4);    // true
```

**Why Strings are immutable:**
- Immutability makes pooling safe — if one reference modifies a pooled String, others would see the change
- Immutability makes Strings inherently thread-safe
- **Always use `.equals()` for String comparison, never `==`**
  - `==` compares references (memory addresses)
  - `.equals()` compares content

**Visual:** Heap diagram showing String Pool as a region, with `s1` and `s2` arrows pointing to same "hello" in pool, `s3` arrow pointing to separate "hello" object.
**Speaker note:** This is a classic Java interview question. "Why is `s1 == s2` true for string literals but `s1 == s3` is false when using `new String()`?" The answer is the string pool. And this leads directly into why you should ALWAYS use `.equals()` for string comparison — never `==`.

---

### Slide 19: Stack vs Heap — Practical Summary
**Content:**
| Dimension | Stack | Heap |
|-----------|-------|------|
| Stores | Local vars, primitives, references | All objects |
| Allocation | Auto (method call/return) | Manual (`new`) / GC reclaims |
| Lifetime | Until method returns | Until no references remain |
| Speed | Faster (LIFO, cache-friendly) | Slower (dynamic, GC overhead) |
| Thread safety | Each thread has own stack | Shared across all threads |
| Size limit | Small (~1MB per thread) | Large (configurable) |
| Overflow error | StackOverflowError | OutOfMemoryError |

**Putting it together — trace this code:**
```java
public static void main(String[] args) {     // main frame: args (stack ref → heap array)
    int count = 0;                           // count: stack
    List<String> names = new ArrayList<>();  // names: stack ref → ArrayList on heap
    names.add("Alice");                      // "Alice" String on heap
    count = names.size();                    // count updated: stack
}
// main returns: count, names ref cleaned from stack
// ArrayList and Strings: eligible for GC (no more references)
```
**Visual:** Annotated code with arrows to either "STACK" or "HEAP" labels for each declaration. Clear color coding (blue = stack, orange = heap).
**Speaker note:** At the end of main(), `count` and the `names` reference are popped off the stack. The ArrayList object and the "Alice" String on the heap are now unreachable — the garbage collector will eventually reclaim that memory. This handoff between manual stack cleanup and GC-driven heap cleanup is fundamental to how Java manages memory.

---

### Slide 20: Java Memory Model (JMM) — Visibility & Ordering
**Content:**
- **Java Memory Model:** Defines how threads interact through memory — rules for visibility and ordering of shared variable writes
- **Problem (from Day 9):** CPU caches mean a thread may not see changes made by another thread
- **JMM guarantees:** Under what conditions a write by one thread is guaranteed to be visible to another

**Happens-before relationship:**
- If action A happens-before action B, then A's results are visible to B
- **`synchronized` establishes happens-before:** Exiting a synchronized block happens-before entering the same monitor
- **`volatile` establishes happens-before:** A write to a volatile variable happens-before any subsequent read of that variable
- **Thread start happens-before:** `Thread.start()` happens-before any action in the started thread
- **Thread join happens-before:** All actions in a thread happen-before `Thread.join()` returns

```java
// Visibility without synchronization — broken
boolean ready = false;
int value = 0;

// Thread 1 writes
value = 42;
ready = true;   // NOT guaranteed visible to Thread 2!

// Thread 2 reads
while (!ready) {}
System.out.println(value);  // Might print 0!
```

**Fix:** Use `volatile` for flags, `synchronized` for compound operations (from Day 9).
**Visual:** Diagram showing CPU1 with its cache, CPU2 with its cache, and main memory in the middle. Arrows showing stale values when not synchronized, correct values when synchronized.
**Speaker note:** The JMM is the formal specification behind everything we covered in Day 9's synchronization discussion. "Happens-before" is the contract Java gives you. If you use synchronized or volatile correctly, the JMM guarantees visibility and ordering. If you don't, you get undefined behavior — not just race conditions, but reordering surprises that only appear on multi-core machines.

---

### Slide 21: Common Beginner Mistakes — Memory
**Content:**

**Mistake 1: Thinking Java passes objects by reference**
```java
public void changeName(Person p) {
    p = new Person("Bob");  // Creates NEW object, reassigns LOCAL ref p
    // Original reference outside the method is UNCHANGED
}
// ACTUALLY CORRECT: Java passes the reference BY VALUE (copy of reference)
// Modifying p's fields (p.setName) WILL affect the original — mutating ≠ reassigning
```

**Mistake 2: String concatenation in loops**
```java
// WRONG — O(n²) — each + creates a new String object on heap
String result = "";
for (String s : list) result += s;

// RIGHT — O(n) — StringBuilder reuses buffer
StringBuilder sb = new StringBuilder();
for (String s : list) sb.append(s);
String result = sb.toString();
```

**Mistake 3: == vs .equals() for objects**
```java
Integer a = 128;
Integer b = 128;
System.out.println(a == b);       // false — Integer cache only covers -128..127
System.out.println(a.equals(b));  // true — correct content comparison
```
- **Rule:** Always use `.equals()` for object equality. `==` only for primitive comparison or intentional identity check.
**Visual:** Three code blocks with wrong approach crossed out, correct approach highlighted.
**Speaker note:** The Integer cache gotcha is a classic interview question. Java caches Integer objects from -128 to 127 for efficiency, so `Integer a = 100; Integer b = 100; a == b` is `true`. But `Integer a = 200; Integer b = 200; a == b` is `false`. It's inconsistent and confusing — always use `.equals()` for objects.

---

### Slide 22: Part 1 Summary & Transition
**Content:**
**Big O — covered:**
- O(1), O(log n), O(n), O(n log n), O(n²), O(2ⁿ) — know them cold
- Reading code to identify complexity (loops, halving, nesting)
- Time vs space trade-offs
- Java collection complexities

**Design Patterns — covered:**
- Singleton: One instance (Bill Pugh Holder, Enum)
- Factory: Centralize object creation, decouple caller from class
- Builder: Readable construction for complex objects
- Observer: Publish-subscribe, event notification
- Strategy: Interchangeable algorithms, functional interfaces

**Memory Model — covered:**
- Stack: Local variables, method frames, fast, auto-managed, per-thread
- Heap: Objects, shared, GC-managed, configurable
- String pool, reference semantics, JMM happens-before

**Part 2 preview:**
- How GC actually works and which algorithm Java uses
- JVM flags for tuning (-Xmx, -Xms, GC algorithm selection)
- Reference types (WeakReference, SoftReference, PhantomReference)
- File I/O (Reader/Writer/Buffered variants), serialization
- Debugging with IntelliJ — breakpoints, step through, watch expressions

**Visual:** Summary checklist with checkmarks, and Part 2 topics as a preview list.
**Speaker note:** Everything in Part 1 connects to Part 2. The heap knowledge you just learned is prerequisite for understanding GC. The reference type system builds on knowing the difference between stack references and heap objects. Take a quick break and come back ready for the second half.

---
