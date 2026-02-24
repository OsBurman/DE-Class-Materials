# Day 10 — Part 2 Walkthrough Script
## Garbage Collection · File I/O · Serialization · Debugging
**Estimated time: ~90 minutes**

---

## Opening (2 min)

> "Part 2 today covers four topics that separate junior from mid-level engineers — not because they're hard to learn, but because most people skip them. GC, I/O, serialization, and debugging are things you live with every day in production. Let's build real intuition for each one."

---

## FILE 1 — `01-garbage-collection.java` (~22 min)

### 1.1 — Draw the JVM Heap on the Board (3 min)

```
┌─────────────────────────────────────────────────┐
│                    JVM HEAP                     │
│                                                 │
│  ┌─────────────────────────────┐                │
│  │       YOUNG GENERATION      │                │
│  │  ┌──────────┐  ┌───┐  ┌───┐│                │
│  │  │   Eden   │  │ S0│  │ S1││                │
│  │  │  (new    │  │   │  │   ││                │
│  │  │ objects) │  │Sur│  │Sur││                │
│  │  └──────────┘  └───┘  └───┘│                │
│  └─────────────────────────────┘                │
│                                                 │
│  ┌─────────────────────────────┐                │
│  │       OLD GENERATION        │                │
│  │  (objects that survived     │                │
│  │   multiple Minor GCs)       │                │
│  └─────────────────────────────┘                │
│                                                 │
│  METASPACE (outside heap — class metadata)      │
└─────────────────────────────────────────────────┘
```

**[ASK]** "What happens when Eden fills up?"
- Minor GC runs. Survivors move to S0/S1. After ~15 cycles, promoted to Old Gen.
- Major/Full GC happens when Old Gen fills up — much more expensive.

**[ACTION]** Write on board: `Minor GC: fast, frequent | Full GC: slow, rare`

---

### 1.2 — GC Algorithm Evolution (3 min)

Walk through the header table in the file. Key talking points:

- **Serial** (single-threaded) — only for tiny embedded apps
- **Parallel** (multi-threaded) — throughput-focused, default before Java 9
- **G1** (default Java 9+) — splits heap into regions; aims for predictable pause times
- **ZGC / Shenandoah** — sub-millisecond pauses; for latency-sensitive services

**[ASK]** "In a REST API handling 10,000 requests/second, what's the danger of a Full GC?"
→ It pauses ALL application threads (Stop-the-World). A 500ms GC pause = 5,000 requests backed up.

---

### 1.3 — `demonstrateGCBasics()` (3 min)

- `product = null` makes the object **eligible** — no live reference can reach it.
- `System.gc()` is a **hint**, not a command. The JVM can ignore it.
- Explain why `finalize()` was deprecated in Java 9 — unpredictable timing, causes GC pauses.

**⚠️ WATCH OUT:** "Never rely on GC timing. If you have a resource (file, socket, DB connection), close it explicitly in a `finally` block or try-with-resources. Don't count on the GC."

---

### 1.4 — `demonstrateReferenceTypes()` (7 min)

Draw this on the board:

```
STRONG  → Object NEVER collected while reference exists
SOFT    → Collected only when JVM is under memory pressure  (use for: caches)
WEAK    → Collected at NEXT GC cycle even with memory available  (use for: canonical maps)
PHANTOM → Object already finalized; reference enqueued for cleanup callback
```

Walk through each in code:
- **Weak reference** — after `System.gc()`, `weakRef.get()` returns `null`
- **Soft reference** — `softRef.get()` still returns the object unless OOM approaching
- **Phantom reference** — used for cleanup hooks (native resources, direct buffers)
- **WeakHashMap** — demonstrate auto-eviction: once key is GC'd, entry disappears

**[ASK]** "Where would you use SoftReference in a real app?"
→ Image thumbnail cache. JVM keeps images as long as memory allows, drops them before OOM.

---

### 1.5 — `demonstrateMemoryLeak()` (4 min)

- Show `LeakyEventBus` — static `List<Observer>` grows forever; observer objects never collected.
- Show `SafeEventBus` fix — `List<WeakReference<Observer>>` means references don't prevent GC.
- Mention `ThreadLocal.remove()` — classic leak in servlet containers where threads are pooled.

**[ACTION]** "If you ever work with a service that's 'fine for 2 hours then falls over', look for static collections, listeners that are never removed, or ThreadLocals that aren't cleaned up."

---

### 1.6 — JVM Tuning Awareness (2 min)

Point to the flags in the header. Key ones to remember:

| Flag | Purpose |
|------|---------|
| `-Xms256m -Xmx1g` | initial / max heap |
| `-XX:+UseG1GC` | use G1 (default Java 9+) |
| `-XX:MaxGCPauseMillis=200` | G1 pause target |
| `-XX:+HeapDumpOnOutOfMemoryError` | save heap on OOM |
| `-verbose:gc` | print GC events |

"These go in your Dockerfile's `JAVA_OPTS` or your `application.yml`. Spring Boot exposes them via environment variables."

---

## FILE 2 — `02-file-io.java` (~20 min)

### 2.1 — Stream Layering Diagram (3 min)

Draw on board:

```
FileWriter (raw char output to disk)
    └── BufferedWriter (adds an in-memory buffer — one large write instead of many small)
            └── PrintWriter (adds println, printf convenience)

FileReader (raw char input from disk)
    └── BufferedReader (buffers reads, adds readLine())
```

**Key rule #1:** Always use a buffered wrapper. Writing one char at a time to disk without a buffer means one OS call per character — extremely slow.

**Key rule #2:** Always use try-with-resources. Resources must be closed even if an exception is thrown.

---

### 2.2 — `demonstrateFileWriter()` + `demonstrateFileReader()` (5 min)

- Show `FileWriter(file, StandardCharsets.UTF_8)` — always specify charset. Never rely on the system default (breaks on different OSes).
- Show try-with-resources — compiler generates `finally { writer.close(); }` automatically.
- Show append mode `(file, charset, true)` — second `true` appends instead of overwriting.
- Show char-by-char read: `(ch = fr.read()) != -1` — classic pattern, appears in interviews.

**[ASK]** "Why do we check for `-1` instead of `0` or `null`?"
→ `read()` returns an `int`, not a `char`. `-1` is the sentinel value for end-of-stream. `char` can't represent -1, hence the `int` return type.

---

### 2.3 — `demonstrateBufferedReaderWriter()` (4 min)

- `BufferedWriter.newLine()` — platform-safe: `\r\n` on Windows, `\n` on Unix/Mac. Always use this instead of hardcoding `\n`.
- `readLine()` returns `null` at EOF, not an empty string. Show the `while ((line = br.readLine()) != null)` idiom.
- `br.lines()` — wraps readLine in a Stream. Compose with `.filter()`, `.map()`, etc.

**⚠️ WATCH OUT:** "The biggest `readLine()` mistake: `while (!br.readLine().equals(""))` — this throws NPE at EOF. Always check for `null` first."

---

### 2.4 — `demonstrateNIOFiles()` (5 min)

Show the modern NIO.2 API. For most use cases, use this instead of java.io:

```java
// One-liner read and write
String content = Files.readString(path);
Files.writeString(path, content, StandardOpenOption.APPEND);
List<String> lines = Files.readAllLines(path);
```

- `Files.lines()` is lazy — returns a Stream<String>. Ideal for large files (doesn't load all into memory).
- `Files.copy()`, `Files.move()`, `Files.delete()` for file operations.
- `Files.list(dir)` — list directory contents as a Stream<Path>.

**[ASK]** "When would you use `Files.lines()` instead of `Files.readAllLines()`?"
→ 1GB log file. `readAllLines()` loads all into a `List<String>` in RAM. `Files.lines()` streams one line at a time.

---

### 2.5 — `demonstrateTryWithResources()` (3 min)

Show the anti-pattern (manual close skipped on exception) vs the correct try-with-resources.

"Multiple resources in one try — they're closed in **reverse order**: `br` first, then `bw`. This is important for chained streams."

**→ TRANSITION:** "Next topic: Serialization — turning objects into bytes."

---

## FILE 3 — `03-serialization.java` (~18 min)

### 3.1 — What Is Serialization? (2 min)

Draw on board:

```
Java Object ──[ ObjectOutputStream ]──→ byte[] ──[ write to file / send over network ]
byte[]      ──[ ObjectInputStream  ]──→ Java Object (reconstructed)
```

**Use cases:** Session storage in distributed systems, message queues (Kafka with Java serialization), RPC frameworks, deep copy trick.

**Java vs JSON:**
| | Java Serialization | JSON (Jackson) |
|---|---|---|
| Format | Binary | Human-readable |
| Speed | Fast | Slightly slower |
| Versioning | Fragile | Flexible |
| Language | Java only | Universal |
| Recommendation | Legacy/avoid | **Use this** |

---

### 3.2 — `demonstrateBasicSerialization()` (4 min)

Walk through:
- Class must `implement Serializable` — it's a marker interface (no methods).
- `ObjectOutputStream` → `BufferedOutputStream` → `FileOutputStream` (layering again).
- `(CustomerOrder) ois.readObject()` — unchecked cast. `readObject()` returns `Object`.
- Same identity test: `==` is false (different heap objects), `.equals()` should be true.

---

### 3.3 — `demonstrateSerialVersionUID()` (3 min)

**[ASK]** "What happens if you add a new field to `CustomerOrder` and then try to deserialize an old file?"
→ If no `serialVersionUID`, JVM auto-generates one based on class structure. Adding a field changes the UID → `InvalidClassException: local class incompatible`.

Rule: **Always declare `private static final long serialVersionUID = 1L;`**

Bump it to `2L` only when you make a breaking change you cannot recover from.

---

### 3.4 — `demonstrateTransientFields()` (3 min)

- `transient` = skip this field during serialization.
- `password` and `authToken` are null after deserialization — not saved to file/network.
- Common uses: passwords, session tokens, derived/computed fields, heavyweight resources (DB connections), logger references.

**[ASK]** "Why might you mark a Logger field as transient?"
→ `Logger` is not `Serializable`. Without `transient`, serialization would throw `NotSerializableException`.

---

### 3.5 — `demonstrateJsonAlternative()` (3 min)

Show the Jackson pattern (comment in the code):

```java
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(order);        // Object → JSON string
CustomerOrder copy = mapper.readValue(json, CustomerOrder.class);  // JSON → Object
```

**Recommendation:** "In any new Spring Boot project, use Jackson with `@JsonProperty` annotations and `ObjectMapper`. Java serialization is a legacy API — you'll see it in existing codebases but shouldn't write new code with it."

**→ TRANSITION:** "Now let's talk about what you do when everything is on fire — debugging."

---

## FILE 4 — `04-debugging-techniques.java` (~25 min)

### 4.1 — Debugging Mindset (2 min)

Before touching the keyboard:

1. **Read the full error message** — don't skim to Google
2. **Form a hypothesis** — "I think the order is null at line 47 because the DB query returned nothing"
3. **Verify, don't guess** — set a breakpoint, check the actual value
4. **Understand the root cause** — fix the cause, not the symptom

---

### 4.2 — `demonstrateLogging()` (6 min)

Draw the log level ladder on the board:

```
TRACE  ← most verbose (every method call, loop iteration)
DEBUG  ← variable values, query parameters
INFO   ← key business events (order placed, user logged in)
WARN   ← unexpected but recoverable (retry attempt, slow query)
ERROR  ← failure that needs attention (payment failed, DB unreachable)
FATAL  ← (Log4j2 only) application cannot continue
```

**Why not `System.out.println`?**
- No timestamp
- No log level — can't filter
- Can't be configured without redeployment
- Mixed with stdout of other libraries

Show the SLF4J pattern:
```java
log.debug("Processing order: {}", orderId);        // lazy — {} placeholder
log.info("Order {} placed, amount={}", id, amt);
log.error("Payment failed for {}", id, exception); // always pass exception as last arg
```

**⚠️ WATCH OUT:** `log.info("Order: " + order.toString())` — string concatenation happens even if INFO is disabled. Always use `{}` placeholders.

**[ASK]** "In production, which log level would you leave on?"
→ INFO (and WARN + ERROR). DEBUG/TRACE would flood the logs and degrade performance.

---

### 4.3 — `demonstrateCommonBugs()` (7 min)

Walk through each bug live:

**NPE:**
- Java 17+ prints "Cannot invoke String.toUpperCase() because <local4> is null" — tell them which variable.
- Fix: `Optional.ofNullable().orElse()` or explicit null check.

**Off-by-one:**
- `i <= arr.length` vs `i < arr.length` — draw a 5-element array, show index 5 doesn't exist.
- In `for` loops: `<` means "up to but not including length".

**Integer division:**
- `7 / 2 = 3` in Java (not 3.5). Cast one operand to `double` first.

**String `==`:**
- Reinforce String pool knowledge from Part 1 — `new String()` bypasses the pool.
- Rule: **Always use `.equals()` for content comparison**.

**ConcurrentModificationException:**
- Draw the fail-fast iterator model: iterator has a `modCount`; if the list changes, modCount increments → CME.
- Fix: `removeIf()`, `iterator.remove()`, or collect to a new list.

---

### 4.4 — `demonstrateStackTraceReading()` (4 min)

Run this section and show the actual stack trace output. Walk through it:

```
RuntimeException: Service failed: could not load order   ← outer wrapper
    at DebuggingTechniques.serviceLayer(...)              ← service layer caught & wrapped
    at DebuggingTechniques.demonstrateStackTraceReading(...)
    ...
Caused by: IllegalStateException: Database connection timed out  ← ROOT CAUSE
    at DebuggingTechniques.repositoryLayer(...)           ← where it originated
```

**[ACTION]** Draw arrows on the board:
1. First read the outermost exception — the context
2. Find "Caused by:" — this is the **root cause**
3. Scan for YOUR package lines — library frames are noise
4. Line number after `:` → go there in your IDE

---

### 4.5 — `demonstrateIntellijTips()` (6 min)

Cover the key IntelliJ debugger features — walk through as a live demo if possible:

**Breakpoints:**
- Click the gutter → red dot appears
- Right-click the dot → add condition: `amount == 0.0 && approved == true`
- Exception breakpoint: pause on every NPE before any catch block

**Stepping:**
| Key | What it does |
|-----|------|
| F8 | Step over — run the line, don't enter the method |
| F7 | Step into — enter the method being called |
| F9 | Resume — run to next breakpoint |
| Shift+F8 | Step out — finish current method, return to caller |

**Evaluate Expression (Alt+F8):**
"This is underused. At a breakpoint, you can run arbitrary code — call methods, inspect collections, even modify variable values."

**Watches:**
"Drag any expression to the Watches panel. It re-evaluates at every step. Great for monitoring `queue.size()` or `order.getStatus()`."

**Hot Swap:**
"Modify a method body while paused → Build → Applied immediately. No restart. Only works for method bodies — can't add new fields or change class structure."

---

## Wrap-Up — Day 10 Quick-Fire Q&A (5 min)

Ask the class one question from each topic today:

1. **GC** — "What's the difference between a `WeakReference` and a `SoftReference`?"
   → Weak: collected at next GC. Soft: only collected under memory pressure (better for caches).

2. **File I/O** — "Why do we wrap `FileReader` in a `BufferedReader`?"
   → Buffering reduces OS calls. Without it, each `read()` = one system call = very slow.

3. **Serialization** — "What does `transient` do?"
   → Skips that field during serialization. Used for passwords, loggers, derived fields.

4. **Debugging** — "What's wrong with `while (!br.readLine().equals(""))`?"
   → NPE when readLine() returns null at EOF. Should check `!= null` first.

5. **Challenge** — "Your Spring Boot app is running fine for 2 hours, then OutOfMemoryError. Where do you start?"
   → Enable `-XX:+HeapDumpOnOutOfMemoryError`, restart, reproduce the OOM, then analyze the heap dump in VisualVM or Eclipse MAT to find which class/collection is growing unbounded.

---

## Handoff to Exercises

> "Exercises for Part 2 focus on: writing a file-based order log using BufferedWriter, round-tripping an object through Java serialization and verifying `transient` fields, and implementing a tiny debugging challenge where you're given a buggy method and must identify all three bugs."
