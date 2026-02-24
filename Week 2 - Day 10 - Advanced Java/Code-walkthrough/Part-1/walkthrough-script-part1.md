# Day 10 — Part 1 Walkthrough Script (~90 min)
## Big O Notation, Design Patterns & Java Memory Model

---

## PRE-CLASS SETUP

- Open all three Part 1 files in tabs
- Have a blank whiteboard section ready for diagrams
- Optional: keep a Big O cheat sheet image visible on second screen

---

## OPENING (5 min)

> "Welcome to Day 10 — the last Java day before we move into the frontend world.
> Today is a bit different. We're not learning new syntax — we're learning how to
> THINK like a software engineer.
>
> Three topics: how to measure algorithm performance, how to structure code using proven patterns,
> and how Java manages memory. These three topics come up in nearly every technical interview —
> and they make you a dramatically better developer on the job."

[ACTION] Write on board:
```
Part 1:
  1. Big O — "How fast does my code scale?"
  2. Design Patterns — "How do I structure code so it's maintainable?"
  3. Java Memory Model — "Where do my variables actually live?"
```

---

## FILE 1 — Big O Notation (`01-big-o-and-complexity.java`) — 30 min

### Opening Hook

[ACTION] Write this on the board:
```
n = 1,000,000 elements

O(1):       1 operation
O(log n):   20 operations
O(n):       1,000,000 operations
O(n²):      1,000,000,000,000 operations
```

> "Look at the difference. O(n²) with a million elements means one TRILLION operations.
> At 1 billion ops/second, that's 16 minutes. O(log n)? Instant.
> This is why Big O matters — not just theoretically, but on real production systems."

[ASK] "Has anyone seen a slow production query or operation? What do you think caused it?"
→ Discuss: nested loops, missing indexes, naive search on large lists

---

### Header: Complexity Classes

[ACTION] Walk through the header comment table

> "This table shows all the common classes from best to worst. We'll see every single one today.
> The rule for simplifying Big O: drop constants, keep the dominant term."

Write on board:
```
T(n) = 3n² + 5n + 12  →  O(n²)
```
> "As n grows, the 3n² completely dwarfs the other terms. Constants don't matter — we care about
> SHAPE of growth, not exact numbers."

---

### O(1) — `demonstrateO1()`

[ACTION] Point to the array access and HashMap.get lines

> "Array index lookup — you give the JVM an index, it multiplies by element size,
> adds the base address — one arithmetic operation. Always. Whether the array has 5 items or 5 million."

> "HashMap.get() is also amortized O(1) — it hashes the key, finds the bucket, retrieves the value.
> The fact that there are a million entries doesn't slow it down."

[ASK] "What's the O notation for LinkedList.get(index) vs ArrayList.get(index)?"
→ LinkedList.get: O(n) — must walk the chain. ArrayList.get: O(1) — direct index access.

---

### O(log n) — `demonstrateOLogN()`

[ACTION] Draw binary search on the board:
```
[5, 12, 18, 27, 34, 45, 67, 89, 102, 150]
 lo=0                             hi=9
          mid=4 → 34 < 45 → go right
                    lo=5   hi=9
                    mid=7 → 89 > 45 → go left
                    lo=5   hi=6
                    mid=5 → found 45!
```

> "Each step HALVES the remaining search space. That's why doubling n only adds ONE step.
> A sorted list of 1 billion items needs only 30 comparisons."

[ACTION] Walk through the growth table printed by the code

⚠️ WATCH OUT: Binary search only works on SORTED data. Students often try to use it on unsorted arrays.

> "Java's `Arrays.binarySearch()` and `Collections.binarySearch()` are built-in O(log n) methods —
> but they require sorted input. If the array isn't sorted, they return garbage."

---

### O(n) — `demonstrateON()`

> "Linear means we touch each element once — or a fixed constant number of times.
> Finding an element in an unsorted list, summing values, printing every item — all O(n)."

[ASK] "Is O(3n) different from O(n)?" → No — drop the constant. Both are linear.

---

### O(n log n) — `demonstrateONLogN()`

> "This is the sweet spot for sorting. It's impossible to sort a comparison-based list faster
> than O(n log n) in the general case — that's a mathematical proof.
> Merge sort, heap sort, Java's Arrays.sort() — all O(n log n)."

[ACTION] Walk through the mergeSort/merge methods

> "Merge sort: recursively split the array in half — that's the log n part.
> At each level of recursion, merging takes O(n) work. Total: O(n log n)."

---

### O(n²) — `demonstrateON2()`

> "Two nested loops over the same data — classic O(n²).
> Bubble sort is the textbook example. For every element in the outer loop,
> we loop through the remaining elements in the inner loop."

[ASK] "Name three other examples of O(n²) in everyday code."
→ Nested for-each to find duplicates; comparing every pair; building a matrix from a list

[ACTION] Run it mentally: n=7, comparisons ~ 21. n=1000, comparisons ~ 500,000. n=10,000 ~ 50 million.

⚠️ WATCH OUT: The most common performance bug in student code — a loop inside a loop inside a Spring controller method that runs on every request. O(n²) per request at scale = disaster.

---

### O(2ⁿ) — `demonstrateO2N()`

[ACTION] Draw the call tree for fibNaive(5):
```
         fib(5)
        /      \
    fib(4)    fib(3)
   /    \      /   \
fib(3) fib(2) fib(2) fib(1)
  ...
```

> "See how fib(3) is calculated TWICE. fib(2) is calculated three times.
> Each call branches into two more — the tree doubles at every level."

> "The fix is memoization or dynamic programming — store results so you don't recalculate."

[ACTION] Compare the iterative version: O(n), one pass, no repeated work.

---

### Space Complexity — `demonstrateSpaceComplexity()`

> "Time isn't the only resource we care about. Space complexity measures how much EXTRA MEMORY
> the algorithm needs as n grows."

Walk through:
- `sum` loop: O(1) extra space — just a counter variable
- `Arrays.copyOf`: O(n) extra — creates a full copy
- Recursive `factorial(10)`: O(n) stack space — 10 frames live simultaneously

[ASK] "Why is O(n) recursive factorial worse than an O(1) iterative loop for space?"
→ Each recursive call adds a stack frame. 10,000 deep recursion = 10,000 stack frames → StackOverflow

---

### Practical Comparison — `compareAlgorithmsOnRealData()`

> "Let's make this concrete. We have 100,000 items. We check if the last item exists."

[ACTION] Walk through the timing output

> "List.contains() scans from element 0 to 99,999 — O(n). HashSet.contains() hashes the key —
> O(1). The difference is visible even in microseconds. At scale, with millions of lookups,
> this becomes the difference between a responsive app and a down app."

→ TRANSITION: "Now that we know HOW to measure code quality — let's look at HOW to structure it well."

---

## FILE 2 — Design Patterns (`02-design-patterns.java`) — 35 min

### Opening

[ASK] "Has anyone been in a codebase where you thought 'how did this even happen?' — spaghetti code,
copy-paste everywhere, nobody knows what anything does?"

> "Design patterns are the antidote. They're named, documented solutions to problems that
> every developer eventually faces. When you say 'this is a Singleton' or 'use the Strategy pattern here',
> every experienced developer on your team immediately knows what you mean."

---

### SINGLETON — `demonstrateSingleton()` and `AppConfig`

[ACTION] Write on board: "ONE instance, global access point"

> "The classic use case: a config manager, a connection pool, a logger.
> You want exactly one of them — creating two would mean inconsistent state."

Walk through `AppConfig`:
- Private constructor: "The `private` prevents `new AppConfig()` from anywhere outside"
- Inner `Holder` class: "This is the initialization-on-demand holder idiom — the BEST way to do Singleton in Java"
  - "The inner class is only loaded when `getInstance()` is first called"
  - "Class loading in Java is thread-safe — so no synchronization overhead"
- `getInstance()`: always returns `Holder.INSTANCE`

[ACTION] Run `demonstrateSingleton()` mentally:
- `config1` and `config2` both call `getInstance()` → same object
- `config1.set(...)` is visible through `config2.get()` — same map

⚠️ WATCH OUT: The double-checked locking pattern (`if (instance == null) { synchronized (...) ... }`) is often done wrong. The holder idiom is simpler and correct. Memorize it.

[ASK] "If two threads both call getInstance() simultaneously — is the holder idiom safe?"
→ Yes — JVM guarantees class loading happens once, atomically

---

### FACTORY — `demonstrateFactory()`

[ACTION] Write on board:
```
Client code →  NotifierFactory.create("EMAIL")  →  Notifier
               NotifierFactory.create("SMS")    →  Notifier
```

> "The client doesn't know about EmailNotifier or SmsNotifier. It asks the factory for
> 'something that can send a notification', and the factory decides which class to instantiate."

Walk through:
- `Notifier` interface: the contract
- `NotifierFactory.create()`: the switch on type
- Client code calls `send()` — doesn't import EmailNotifier at all

[ASK] "What happens when we add a WhatsApp notifier?" → Add one case to the switch, create the class.
The client code doesn't change at all. That's the power of this pattern.

⚠️ WATCH OUT: Students sometimes put business logic in the factory. Factories should ONLY decide
which object to create — nothing else.

---

### BUILDER — `demonstrateBuilder()`

[ACTION] Write on board:
```
// Anti-pattern: telescoping constructor
new User("Alice", "alice@co.com", true, false, null, 30, "US")
// What does 'true' mean? 'false'? 'null'?

// Builder: self-documenting
new User.Builder("Alice", "alice@co.com").age(30).country("US").emailVerified(true).build()
```

Walk through the `User` class:
- `name` and `email` are required (in `Builder` constructor)
- `age`, `country`, `emailVerified` are optional (defaults in Builder)
- Each setter returns `this` → enables method chaining (fluent API)
- `build()` validates before constructing

[ASK] "Why is the `User` constructor `private`?"
→ Forces callers to use the Builder — prevents inconsistent state (e.g., no name)

> "Lombok's `@Builder` annotation generates all of this for you in Spring Boot projects.
> Understanding the pattern helps you use Lombok correctly and debug when it doesn't work."

---

### OBSERVER — `demonstrateObserver()`

[ACTION] Draw on board:
```
StockFeed (Subject)
│
├── PriceAlertSystem  (Observer 1)
├── TradingDashboard  (Observer 2)
└── AuditLogger       (Observer 3)

When price changes → all observers notified automatically
```

Walk through:
- `PriceObserver` interface: the subscription contract
- `StockFeed.subscribe()` / `unsubscribe()` — adds/removes from observer list
- `updatePrice()` calls `notifyObservers()` → loops through list calling `onPriceChange()`

[ACTION] Run through the output mentally:
- $175 update: alert not triggered (< 180), dashboard shows it, audit logs it
- $185 update: alert fires (> 180), dashboard shows it, audit logs it
- $190 update: dashboard unsubscribed — only alert and audit respond

[ASK] "Where do you see the Observer pattern in the frameworks we've used?"
→ Java's EventListener, Spring's ApplicationEvent/ApplicationListener, React's state subscriptions,
   Angular's EventEmitter, RxJS Observables

> "In Java SE, this pattern is also called the 'publish-subscribe' or 'event listener' pattern."

---

### STRATEGY — `demonstrateStrategy()`

[ACTION] Write on board:
```
Context: ShoppingCart
Strategy interface: DiscountStrategy { double apply(double total) }

Strategy A: PercentageDiscount(10%)
Strategy B: FixedAmountDiscount($50)
Strategy C: MemberDiscount("GOLD")
Strategy D: lambda — inline custom rule
```

> "The cart code NEVER changes. We just swap which strategy it uses at runtime."

Walk through:
- `DiscountStrategy` interface: single method = functional interface = can use lambda
- Each strategy class encapsulates one algorithm
- `ShoppingCart.setDiscountStrategy()` swaps at runtime

[ACTION] Point to the lambda: `cart.setDiscountStrategy(total -> total > 500 ? total * 0.85 : total)`

> "Because DiscountStrategy is a functional interface (one abstract method), we can write
> a one-liner instead of a whole class. This is Strategy + lambdas working together."

[ASK] "Where else might you apply the Strategy pattern in a Spring Boot app?"
→ Sorting strategy for search results, payment method processing, data export format (CSV vs JSON vs XML)

→ TRANSITION: "Now that we understand algorithms and patterns — let's go under the hood and see where
all of this code actually lives in memory."

---

## FILE 3 — Java Memory Model (`03-java-memory-model.java`) — 20 min

### Opening

[ACTION] Draw on board:
```
JVM Memory:
┌─────────────────┐  ┌────────────────────────────┐  ┌──────────────┐
│    STACK         │  │          HEAP               │  │  METASPACE   │
│  (per thread)   │  │  (shared across threads)    │  │  (class info)│
│                 │  │                             │  │              │
│ • local vars    │  │ • all objects (new ...)     │  │ • bytecode   │
│ • primitives    │  │ • arrays                    │  │ • static vars│
│ • references    │  │ • instance variables        │  │ • class defs │
│ • call frames   │  │                             │  │              │
└─────────────────┘  └────────────────────────────┘  └──────────────┘
```

> "This is the foundation of everything. Understanding WHERE things live explains
> NullPointerExceptions, memory leaks, StackOverflowErrors, and pass-by-value behavior."

---

### Stack Memory — `demonstrateStackMemory()`

Walk through the stack frame concept:

> "When `demonstrateStackMemory()` runs, the JVM pushes a new frame onto the stack.
> Inside that frame: `quantity`, `price`, `inStock` — they live DIRECTLY in the frame.
>
> When we call `calculateTotal()`, another frame is pushed ON TOP.
> When `calculateTotal()` returns, that frame is POPPED. `subtotal` and `tax` cease to exist.
> When `demonstrateStackMemory()` returns, its frame pops. `quantity` ceases to exist."

[ASK] "What happens when you recurse without a base case?"
→ Each call pushes a new frame — stack grows until StackOverflowError

---

### Heap Memory — `demonstrateHeapMemory()`

[ACTION] Draw on board:
```
Stack                   Heap
┌────────────┐         ┌──────────────────────────┐
│ order ─────┼─────────▶ Order {                   │
│            │         │   orderId = "ORD-001"     │
│ sameOrder ─┼─────────▶   quantity = 3            │
└────────────┘         │   price = 49.99           │
                       └──────────────────────────┘
```

> "Two stack variables, one heap object. When `sameOrder.quantity = 10` runs,
> it modifies the single heap object — so `order.quantity` is also 10."

[ASK] "What value does `order.quantity` print after `sameOrder.quantity = 10`?"
→ 10. Same object on heap.

---

### Pass-by-Value — `demonstrateReferenceVsValue()`

> "Java is ALWAYS pass-by-value. This confuses people because they see object mutations
> being visible to the caller — but that's because the VALUE that was passed is the REFERENCE,
> and both references point to the same heap object."

Walk through three methods:
1. `tryToChangeInt(stock)` — copies the int value → original unchanged
2. `tryToChangeOrder(myOrder)` — copies the reference → both reference same heap object → mutation visible
3. `tryToReplaceOrder(myOrder)` — copies the reference, assigns a NEW object to local copy → original unchanged

[ASK] "After tryToReplaceOrder runs, what is myOrder.orderId?"
→ Still "ORD-XYZ" — the replacement only affected the local copy of the reference

---

### String Pool — `demonstrateStringPool()`

> "Strings are immutable in Java, and literals are very common — so the JVM maintains a
> pool of String objects on the heap. When you write `String s = \"hello\"`,
> the JVM checks the pool first. If it's already there, you get a reference to the existing object."

[ACTION] Point to `s1 == s2` (true) vs `s1 == s3` (false)

⚠️ WATCH OUT: `==` compares references (addresses), not content.
`s1 == s3` is false because `new String(...)` bypasses the pool.
`s1.equals(s3)` is true because .equals() compares character content.

> "**ALWAYS use .equals() for String comparisons. NEVER use ==.**
> This is one of the top Java interview questions and one of the most common bugs."

---

### Method Area / Static Variables — `demonstrateMethodAreaAndStatic()`

> "Static fields are NOT stored on the heap with instances. They live in Metaspace — the area
> for class metadata. There's exactly ONE copy, shared by all instances."

[ACTION] Walk through the Counter class and output

> "Each `new Counter()` creates a new heap object with its own `name` field.
> But `Counter.totalCreated` lives in Metaspace — one variable, incremented by every constructor call."

---

### Memory Errors — `demonstrateMemoryErrors()`

Walk through:
- `StackOverflowError`: "infinite recursion — stack fills up"
- `NullPointerException`: "you have a reference variable on the stack, but it points to null — no heap object"
- `OutOfMemoryError` (commented): "heap is full — GC can't reclaim enough"

[ASK] "If you see an NPE, what's the first thing you check?"
→ Which variable is null? Was it initialized? Did a method return null when you didn't expect it?

---

## WRAP-UP (5 min)

[ACTION] Quick board summary:

```
Big O:          O(1) < O(log n) < O(n) < O(n log n) < O(n²) < O(2ⁿ)
Patterns:       Singleton (1 instance), Factory (create by type), Builder (complex objects),
                Observer (notify subscribers), Strategy (swappable algorithms)
Memory:         Stack = frames + primitives (per thread)
                Heap  = all objects (shared)
                Metaspace = class info + statics
```

**Quick-fire Q&A:**

1. "What's the Big O of HashMap.containsKey()?" → O(1)
2. "You need to notify 10 services when an order is placed. Which pattern?" → Observer
3. "You have 15 optional fields in a class. Which pattern prevents a 15-arg constructor?" → Builder
4. "Where does `int x = 5;` live inside a method?" → Stack
5. "Where does `new ArrayList<>()` live?" → Heap (reference on stack, object on heap)

---

*End of Day 10 Part 1 Script*
