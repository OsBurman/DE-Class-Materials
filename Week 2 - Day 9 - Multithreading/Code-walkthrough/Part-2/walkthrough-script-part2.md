# Day 9 — Part 2 Walkthrough Script (~90 min)
## Producer-Consumer, Concurrent Collections, ExecutorService, CompletableFuture, Concurrency Utilities

---

## PRE-CLASS SETUP

- Open the 5 Part 2 files side by side (or use tabs)
- Have the Java concurrency cheat sheet visible
- Board drawing ready: thread pool diagram (box of threads + queue of tasks)

---

## OPENING (5 min)

> "Yesterday we ended Part 1 with the fundamentals — creating threads, synchronizing them, and
> avoiding deadlocks. The problem is: all of that is still quite low-level.
>
> In production Java, you almost never call `new Thread()` directly.
> You use the tools we're covering today — which are the abstractions that production
> Java applications are actually built on."

[ACTION] Draw on board:
```
Part 1 (low-level):      new Thread()   synchronized   wait/notify
Part 2 (high-level):  ExecutorService   BlockingQueue   CompletableFuture
                       ConcurrentHashMap   CountDownLatch   Semaphore
```

---

## FILE 1 — Producer-Consumer (`01-producer-consumer.java`) — 15 min

### Opening Hook

[ASK] "Before we look at the code — who can describe the producer-consumer problem in one sentence?"

> Producers generate work items. Consumers process them. The challenge: producers and consumers
> run at different speeds — how do we coordinate them without spinning?

[ACTION] Draw the two-buffer approaches:
```
wait/notify version:    [Producer] → [ManualBuffer (array + head/tail)] → [Consumer]
BlockingQueue version:  [Producer] → [ArrayBlockingQueue(3)]            → [Consumer]
```

---

### Approach 1: ManualBuffer with wait/notify

[ACTION] Point to `runWithWaitNotify()` and `ManualBuffer`

> "This is what you'd write from scratch. A circular array, explicit synchronization, and
> `wait()`/`notifyAll()` to block when full or empty."

Walk through:
- `put()`: `while (count == capacity) wait()` — why `while` not `if`?
  [ASK] "Why do we loop on the while instead of using an if?" → Spurious wakeups
- `notifyAll()` after put — wakes the consumer
- `take()`: `while (count == 0) wait()` — same pattern
- Sentinel `null` value signals end-of-stream to consumer

⚠️ WATCH OUT: `notifyAll()` wakes ALL waiting threads — they all re-check the condition in the `while` loop. Only one will proceed; the rest go back to waiting. This is correct but potentially wasteful at scale.

---

### Approach 2: BlockingQueue

[ACTION] Show `runWithBlockingQueue()`

> "Now look how simple this becomes with `ArrayBlockingQueue`."

- `queue.put()` — blocks automatically if full (no synchronized, no wait)
- `queue.take()` — blocks automatically if empty
- "DONE" sentinel — producer sends it last; consumer checks for it

[ASK] "What did `BlockingQueue` replace?" → The manual synchronized block + wait/notifyAll in ManualBuffer

→ TRANSITION: "Multiple producers and consumers work the same way — just use a poison pill per consumer."

---

### Approach 2b: Multiple Producers/Consumers

[ACTION] Show `runMultipleProducersConsumers()`

- `poll(500, MILLISECONDS)` — timeout-based; returns `null` if nothing arrives
- Why `null` check + a `done` flag instead of just relying on poison pill?
  → Allows clean shutdown even if a message is missed

[ASK] "Why do we need one poison pill per consumer, not just one?"
→ Each consumer needs to receive it individually to stop

---

## FILE 2 — Concurrent Collections (`02-concurrent-collections.java`) — 15 min

### Opening

[ACTION] Write on board: `HashMap` in multi-threaded context = ☠️

> "A regular HashMap is designed for single-threaded use. Two threads writing simultaneously
> can corrupt internal state — you might get an infinite loop, wrong size, or lost entries."

[ASK] "What happens when two threads call `map.put()` at the same time?" 
→ They might both resize the backing array simultaneously → corruption

---

### ConcurrentHashMap

[ACTION] Walk through `demonstrateConcurrentHashMap()`

Key operations that are ATOMIC:
- `putIfAbsent(key, value)` — check-and-set without separate get/put
- `computeIfAbsent(key, fn)` — create default value only if missing (great for lazy init)
- `compute(key, (k, v) -> newValue)` — atomic read-modify-write
- `merge(key, value, (old, new) -> combined)` — combine existing + new value atomically

⚠️ WATCH OUT: Compound operations are NOT atomic even with ConcurrentHashMap:
```java
// WRONG — get and put are individually atomic but not together:
if (!map.containsKey("key")) map.put("key", value);

// RIGHT — use atomic operation:
map.putIfAbsent("key", value);
```

---

### CopyOnWriteArrayList

[ACTION] Walk through `demonstrateCopyOnWriteArrayList()`

> "Every write creates a fresh copy of the underlying array. Readers always see a consistent
> snapshot — they never see a half-modified list."

- Great for: listener registries, observer lists (many reads, rare writes)
- Terrible for: high-write scenarios (copying is expensive)

[ASK] "When would you choose CopyOnWriteArrayList over ConcurrentHashMap?" 
→ When you need a list (ordered, duplicates allowed) and writes are rare

---

### BlockingQueue Variants

[ACTION] Walk through `demonstrateBlockingQueues()`

Key variants:
| Type | Capacity | Notes |
|------|----------|-------|
| `ArrayBlockingQueue` | Fixed | Best when you want backpressure |
| `LinkedBlockingQueue` | Optional (default max) | More flexible |
| `PriorityBlockingQueue` | Unbounded | Elements must be Comparable |

- `offer(item, timeout)` — try to insert, give up after timeout
- `drainTo(list)` — batch remove all available items atomically

---

### Collections.synchronizedXxx wrappers

[ACTION] Show the last section briefly

> "These are the legacy approach — wrapping an existing collection with a synchronized view."

⚠️ WATCH OUT: Iteration still requires external synchronization:
```java
List<String> sync = Collections.synchronizedList(list);
synchronized (sync) {      // ← still needed during iteration!
    for (String s : sync) { ... }
}
```
> "Prefer ConcurrentHashMap and CopyOnWriteArrayList. Use wrappers only when migrating old code."

→ TRANSITION: "Now we've solved the data structure problem. Next: thread lifecycle management."

---

## FILE 3 — ExecutorService & Thread Pools (`03-executor-service-and-thread-pools.java`) — 20 min

### Opening Hook

[ASK] "What's wrong with creating a new thread for every incoming HTTP request?"

> - Thread creation is expensive (~1ms, OS kernel call)
> - No bound on thread count — 10,000 requests = 10,000 threads = OOM
> - No reuse of threads between requests

[ACTION] Draw on board:
```
Without pool:   Request → new Thread() → dies after use   (repeat × 10000)

With pool:      Request → [Queue] → [Thread 1]
                                    [Thread 2]   (reused)
                                    [Thread 3]
```

---

### Pool Types

[ACTION] Walk through Sections 1–3

| Factory method | Threads | Queue | Best for |
|----------------|---------|-------|----------|
| `newFixedThreadPool(n)` | n fixed | Unbounded | CPU-bound, known concurrency |
| `newCachedThreadPool()` | 0..∞ | None | Short I/O tasks, burst traffic |
| `newSingleThreadExecutor()` | 1 | Unbounded | Guaranteed serial order |

⚠️ WATCH OUT: `newCachedThreadPool()` can create thousands of threads if tasks pile up.
If tasks are slow, the pool grows without bound — potential OOM on high load.

---

### execute vs submit

[ACTION] Walk through Section 4 header comments

> "Two ways to give work to an executor:"

```java
executor.execute(runnable);     // Runnable — void, no result, no checked exceptions
executor.submit(callable);      // Callable — returns Future<T>, can throw checked exceptions
```

---

### Future and Callable

[ACTION] Walk through `demonstrateCallableAndFuture()`

- `Callable<T>`: like `Runnable` but returns `T` and can throw
- `Future<T>`: handle for the future result
  - `get()` — blocks until done
  - `get(timeout, unit)` — blocks at most timeout
  - `isDone()`, `isCancelled()`, `cancel(mayInterrupt)`

[ASK] "What happens if the Callable throws an exception? How does Future handle it?"
→ `get()` throws `ExecutionException` wrapping the original — use `getCause()`

---

### invokeAll / invokeAny

[ACTION] Walk through Section 5

- `invokeAll(list)` — runs all, blocks until ALL finish, returns `List<Future>`
- `invokeAny(list)` — runs all, returns first successful result, cancels the rest

[ASK] "When would you use invokeAny in production?" → Redundant calls, fallback servers

---

### ScheduledExecutorService

[ACTION] Walk through Section 6

```java
scheduler.schedule(task, 300, MILLISECONDS);          // once, after 300ms
scheduler.scheduleAtFixedRate(task, 0, 1, SECONDS);   // every 1s (clock-based)
scheduler.scheduleWithFixedDelay(task, 0, 1, SECONDS);// 1s gap after each run ends
```

[ASK] "What's the difference between `scheduleAtFixedRate` and `scheduleWithFixedDelay`?"
→ FixedRate: next run starts 1s after PREVIOUS START (heartbeat/cron semantics)
→ FixedDelay: next run starts 1s after PREVIOUS END (polling/retry semantics)

---

### Shutdown Patterns

[ACTION] Walk through Section 7

> "Always shut down your executor. Threads it owns will keep the JVM running forever."

```java
// Pattern — always use this:
pool.shutdown();                           // no new tasks
pool.awaitTermination(30, SECONDS);        // wait up to 30s for clean exit
```

→ TRANSITION: "ExecutorService manages threads. But Future.get() still blocks. Can we do better?"

---

## FILE 4 — CompletableFuture (`04-completable-future.java`) — 20 min

### Opening

[ACTION] Write on board:
```
Future<T> problem:
   result = future.get();   // blocks main thread — defeats the purpose

CompletableFuture solution:
   supplyAsync(supplier)
     .thenApply(transform)       // runs when supply is done
     .thenAccept(consume)        // runs when transform is done
     // main thread never blocked!
```

[ASK] "Has anyone used JavaScript Promises or async/await?" 
→ CompletableFuture is Java's equivalent of a Promise chain

---

### supplyAsync and runAsync

[ACTION] Walk through Section 1

- `supplyAsync(Supplier)` → runs on `ForkJoinPool.commonPool()` by default
- `runAsync(Runnable)` → no return value, returns `CompletableFuture<Void>`
- Custom executor: `supplyAsync(supplier, myPool)` — use for I/O work (not FJP)

⚠️ WATCH OUT: `ForkJoinPool.commonPool()` is shared across the whole JVM.
For I/O-bound tasks, pass your own executor to avoid starving CPU tasks.

---

### thenApply Chain

[ACTION] Walk through Section 2

> "Each thenApply receives the previous result and returns a new result — like Stream.map"

```java
CompletableFuture
    .supplyAsync(() -> "  hello world  ")
    .thenApply(String::trim)           // "hello world"
    .thenApply(String::toUpperCase)    // "HELLO WORLD"
    .thenApply(s -> "Done: " + s)      // "Done: HELLO WORLD"
```

- `thenApply` — same thread as previous stage if already done
- `thenApplyAsync` — always dispatches to pool thread

---

### thenAccept / thenRun

[ACTION] Walk through Section 3

| Method | Gets result? | Returns? | Use when |
|--------|-------------|----------|----------|
| `thenApply` | ✅ | value | transform result |
| `thenAccept` | ✅ | Void | consume (side effect) |
| `thenRun` | ❌ | Void | action after completion |

---

### thenCompose (flatMap for async)

[ACTION] Walk through Section 4

[ASK] "If fetchOrders() returns a `CompletableFuture<List>`, what happens if you use `thenApply`?"
→ You get `CompletableFuture<CompletableFuture<List>>` — nested, unwieldy

> "thenCompose flattens it — you return a CF<T>, you get back a CF<T>"

```java
CF.supplyAsync(() -> fetchUser(id))           // CF<String>
  .thenCompose(user ->
      CF.supplyAsync(() -> fetchOrders(user))) // CF<List> — not CF<CF<List>>
```

---

### thenCombine — two independent parallel tasks

[ACTION] Walk through Section 5

> "When two tasks don't depend on each other — run them in parallel and combine."

```java
// userFuture and configFuture run at the SAME TIME
userFuture.thenCombine(configFuture, (user, config) -> combine(user, config))
```

[ASK] "Why is thenCombine faster than doing the two fetches sequentially with thenCompose?"
→ Both start immediately; total time = max of the two, not sum

---

### allOf / anyOf

[ACTION] Walk through Section 6

- `allOf(cf1, cf2, cf3)` — wait for ALL; returns `CF<Void>` (collect results manually)
- `anyOf(cf1, cf2, cf3)` — first to complete wins; useful for redundant calls

⚠️ WATCH OUT: `allOf` returns `CF<Void>` — you must still call `.get()` on individual futures.
Use the `thenApply` + `join()` pattern shown in the code to collect all results.

---

### Error Handling

[ACTION] Walk through Section 7

| Method | Gets result? | Gets exception? | Transforms? |
|--------|-------------|-----------------|-------------|
| `exceptionally` | ❌ (only on error) | ✅ | ✅ (recovery value) |
| `handle` | ✅ (or null) | ✅ (or null) | ✅ |
| `whenComplete` | ✅ (or null) | ✅ (or null) | ❌ (side effect only) |

[ASK] "When would you use handle instead of exceptionally?"
→ When you want to transform both success and failure paths in one place

---

### Real-World Pipeline

[ACTION] Walk through Section 8 — read through the pipeline with students

> "This is what production async code looks like:
> fetch user → fetch orders (depends on user) → enrich all orders in parallel → format response
> → always log completion"

[ASK] "Which step uses thenCompose? Which uses thenCombine? Why?"
→ User→Orders = thenCompose (sequential dependency)
→ If we had two independent fetches = thenCombine (parallel independence)

→ TRANSITION: "One last category: high-level coordination tools for thread synchronization."

---

## FILE 5 — Concurrency Utilities (`05-concurrency-utilities.java`) — 15 min

### Opening

[ACTION] Write on board:
```
Problem:      "Wait for N things to happen"   → CountDownLatch
Problem:      "All threads sync at a point"   → CyclicBarrier
Problem:      "Limit to N concurrent threads" → Semaphore
Problem:      "Need more lock control"        → ReentrantLock
Problem:      "Many readers, few writers"     → ReadWriteLock
```

---

### CountDownLatch

[ACTION] Walk through Section 1

> "Think of it as a gate. All threads that call `await()` wait at the gate.
> When `countDown()` is called n times, the gate opens — all waiting threads proceed."

- One-time use — cannot be reset
- `await(timeout, unit)` — don't wait forever

[ASK] "Name a real situation where you'd use this in a Spring Boot app."
→ Wait for database connection pool, cache warm-up, Kafka consumer startup

---

### CyclicBarrier

[ACTION] Walk through Section 2

> "Similar to CountDownLatch, but REUSABLE. All threads wait at the barrier;
> when all arrive, they all proceed together. Then the barrier resets for the next round."

- Optional barrier action runs once when all threads arrive
- `BrokenBarrierException` — thrown if a thread is interrupted while waiting

[ASK] "What would break if one of the 3 workers threw an exception while waiting at the barrier?"
→ `BrokenBarrierException` — all other waiting threads are released with this exception

---

### Semaphore

[ACTION] Walk through Section 3

> "A semaphore controls how many threads can enter a section at the same time.
> Think of it as a bouncer with N wristbands — only N threads can hold a wristband."

- `acquire()` — take a permit (blocks if none available)
- `release()` — return a permit
- `tryAcquire()` — non-blocking; returns `false` immediately if unavailable
- `availablePermits()` — check current count

[ASK] "How would you implement a simple HTTP rate limiter — max 10 concurrent calls — using Semaphore?"
→ `new Semaphore(10)`, `acquire()` before call, `release()` in finally

---

### ReentrantLock

[ACTION] Walk through Section 4 — highlight the try/finally pattern

> "ReentrantLock gives you everything synchronized gives you, plus:
>   - `tryLock()` — don't wait, just fail fast
>   - `lockInterruptibly()` — can be interrupted while waiting
>   - Multiple `Condition`s — named wait/notify queues"

⚠️ WATCH OUT: If you forget `finally { lock.unlock(); }`, the lock is NEVER released.
That's why `synchronized` is safer for simple cases — it auto-releases on exit.

[ASK] "When would you prefer ReentrantLock over synchronized?"
→ Need `tryLock`, `lockInterruptibly`, multiple conditions, or fairness guarantee

---

### ReadWriteLock

[ACTION] Walk through Section 5

> "Perfect for read-heavy, write-rare data structures — like a config cache or in-memory index."

```
readLock  — any number can hold simultaneously (safe to read in parallel)
writeLock — exclusive, blocks all readers AND other writers
```

[ASK] "Why can't we just use synchronized for this?" 
→ synchronized allows only ONE thread at a time — even concurrent readers block each other

---

### StampedLock (brief)

[ACTION] Walk through Section 6 in 2 minutes

> "StampedLock's optimistic read is faster than ReadWriteLock — you read without acquiring
> a lock, then validate. If a write happened during your read, you retry with a real lock."
> "Useful for ultra-high-throughput scenarios. But it's harder to use correctly — prefer
> ReadWriteLock unless profiling shows a bottleneck."

---

### ForkJoinPool (awareness)

[ACTION] Walk through Section 7

> "ForkJoinPool is the engine behind `parallelStream()` and `CompletableFuture.supplyAsync()`.
> You don't usually create it yourself — but it's good to know it's there."

- `RecursiveTask<T>` — divide-and-conquer that returns a value
- Work-stealing: idle threads steal from busy threads' work queues
- `ForkJoinPool.commonPool()` — shared, parallelism = (CPU cores - 1)

[ASK] "What call can you add to a stream to make it use ForkJoinPool?"
→ `.parallelStream()` or `stream().parallel()`

⚠️ WATCH OUT: Parallel streams use `commonPool()` — so do CompletableFutures by default.
If you block in a parallel stream (waiting for I/O), you starve CompletableFuture tasks.
Solution: don't block in parallel streams, or use a custom pool for one of them.

---

### Virtual Threads (Java 21+)

[ACTION] Walk through Section 8

[ASK] "What's the main cost of creating a regular (platform) thread?"
→ ~1MB stack, OS kernel call, context switch overhead → limits you to ~thousands

> "Virtual threads are managed by the JVM, not the OS.
> Creating a million of them is fine. They map onto carrier threads (OS threads)."

Key rules:
```
✅ Use for I/O-bound tasks — blocking is cheap, JVM parks the virtual thread
✅ Use Executors.newVirtualThreadPerTaskExecutor() as a drop-in for cached pools
✅ Use ReentrantLock inside virtual threads (not synchronized)
❌ Don't use for CPU-bound — still needs real CPU time
❌ Avoid large ThreadLocals — you might have millions of threads
```

[ASK] "Why does synchronized block 'pin' the carrier thread but ReentrantLock doesn't?"
→ `synchronized` is a JVM intrinsic tied to the OS thread — JVM can't park the carrier
→ `ReentrantLock` is a Java library construct — JVM can park and reassign the carrier

---

## WRAP-UP (5 min)

[ACTION] Draw the big picture on the board:

```
Low-level ──────────────────────────────── High-level
   new Thread()    →    ExecutorService    →   CompletableFuture
   synchronized    →    ReentrantLock      →   ReadWriteLock
   wait/notify     →    BlockingQueue      →   CountDownLatch / CyclicBarrier
   HashMap         →    ConcurrentHashMap  →   CopyOnWriteArrayList
```

**Quick-fire Q&A:**

1. "When do you choose `ConcurrentHashMap` over `Collections.synchronizedMap`?"
   → Always, unless maintaining legacy API contract

2. "What's the difference between `shutdown()` and `shutdownNow()`?"
   → `shutdown`: drain queue gracefully; `shutdownNow`: interrupt running tasks immediately

3. "If two CompletableFutures are independent, how do you run them in parallel?"
   → `thenCombine` (two tasks) or `allOf` (many tasks)

4. "What tool would you use to ensure no more than 5 threads access a database at once?"
   → `Semaphore(5)`

5. "You have 4 workers that each do one phase of processing, and all must finish before
    the next phase begins. Which tool?"
   → `CyclicBarrier(4)`

---

## ASSIGNMENT PREVIEW

> "Your assignment will use these APIs in combination:
> - A fixed thread pool to process N tasks concurrently
> - ConcurrentHashMap to aggregate results safely
> - CompletableFuture to chain async steps
> - CountDownLatch to ensure the main thread waits for all workers"

---

*End of Day 9 Part 2 Script*
