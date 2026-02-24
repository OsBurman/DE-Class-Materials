# Day 9 — Multithreading
## Quick Reference Guide

---

## 1. Thread Creation

Three main approaches:

```java
// 1. Extend Thread
class MyThread extends Thread {
    @Override
    public void run() { System.out.println("Thread: " + getName()); }
}
new MyThread().start();   // start() creates OS thread; run() alone does NOT

// 2. Implement Runnable (preferred — separates task from thread mechanism)
Runnable task = () -> System.out.println("Running: " + Thread.currentThread().getName());
Thread t = new Thread(task, "worker-1");
t.start();

// 3. Implement Callable<T> (returns a value; throws checked exceptions)
Callable<Integer> callable = () -> 42;
FutureTask<Integer> ft = new FutureTask<>(callable);
new Thread(ft).start();
int result = ft.get();   // blocks until done
```

---

## 2. Thread Lifecycle

```
NEW → start() → RUNNABLE ⇄ BLOCKED (waiting for monitor lock)
                         ⇄ WAITING  (Object.wait(), Thread.join(), LockSupport.park())
                         ⇄ TIMED_WAITING (sleep(ms), wait(ms))
                         → TERMINATED
```

| Method | Effect |
|--------|--------|
| `thread.start()` | Creates OS thread; schedules for execution |
| `Thread.sleep(ms)` | Pauses current thread; does NOT release locks |
| `thread.join()` | Calling thread waits for `thread` to finish |
| `thread.interrupt()` | Sets interrupt flag; wakes sleeping/waiting threads with `InterruptedException` |
| `Thread.currentThread()` | Returns reference to the running thread |
| `thread.setDaemon(true)` | JVM exits when only daemon threads remain |

---

## 3. synchronized

```java
// Synchronized method — locks on 'this' instance
public synchronized void increment() {
    count++;
}

// Synchronized block — more granular; can lock on any object
private final Object lock = new Object();

public void increment() {
    synchronized (lock) {
        count++;
    }
}

// Static synchronized — locks on the Class object
public static synchronized void classLevelOp() { ... }
```

**What synchronized guarantees:**
- **Mutual exclusion** — only one thread enters the synchronized block at a time
- **Visibility** — changes made inside the block are visible to other threads after lock release

---

## 4. volatile

```java
private volatile boolean running = true;

public void stop() { running = false; }

public void run() {
    while (running) { doWork(); }   // reads fresh value each iteration
}
```

- **Visibility only** — ensures all threads see the latest value (disables CPU cache optimisation)
- **NOT atomic** — `volatile int i; i++` is still a race condition (read + modify + write)
- Use `AtomicInteger`, `AtomicBoolean`, etc. for atomic compound operations

---

## 5. Atomic Classes (java.util.concurrent.atomic)

```java
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();        // atomic i++; returns new value
counter.getAndIncrement();        // returns old value, then increments
counter.addAndGet(5);             // atomic += 5
counter.compareAndSet(5, 10);     // CAS: if current==5, set to 10; returns boolean

AtomicBoolean flag    = new AtomicBoolean(false);
AtomicLong    counter = new AtomicLong(0L);
AtomicReference<String> ref = new AtomicReference<>("initial");
```

---

## 6. ReentrantLock vs synchronized

| | `synchronized` | `ReentrantLock` |
|---|---|---|
| Syntax | Keyword | Explicit `lock()` / `unlock()` |
| Try to acquire | Not possible | `tryLock()` / `tryLock(timeout)` |
| Interruptible | No | `lockInterruptibly()` |
| Fairness | No guarantee | `new ReentrantLock(true)` |
| Condition variables | `wait()` / `notify()` | `lock.newCondition()` |
| **Prefer** | Simple cases | Need advanced features |

```java
ReentrantLock lock = new ReentrantLock();

public void safeIncrement() {
    lock.lock();
    try {
        count++;
    } finally {
        lock.unlock();   // ALWAYS in finally to prevent lock leak
    }
}
```

---

## 7. Deadlock

Four necessary conditions (Coffman):
1. **Mutual exclusion** — resource held exclusively
2. **Hold and wait** — thread holds ≥1 lock and waits for another
3. **No preemption** — locks cannot be forcibly taken away
4. **Circular wait** — T1 waits for T2's lock; T2 waits for T1's lock

**Prevention:** Always acquire locks in the **same global order** across all threads.

```java
// Deadlock-prone
Thread A: lock(resourceA) → lock(resourceB)
Thread B: lock(resourceB) → lock(resourceA)

// Fix — consistent ordering
Thread A: lock(resourceA) → lock(resourceB)
Thread B: lock(resourceA) → lock(resourceB)  // same order
```

| Issue | Description |
|-------|-------------|
| **Deadlock** | Threads permanently blocked waiting for each other |
| **Livelock** | Threads keep changing state in response to each other but never progress |
| **Starvation** | A thread is perpetually denied access because others keep getting priority |

---

## 8. ExecutorService (Thread Pools)

```java
import java.util.concurrent.*;

// Fixed thread pool — reuse n threads
ExecutorService pool = Executors.newFixedThreadPool(4);

// Cached pool — grows/shrinks; good for many short tasks
ExecutorService pool = Executors.newCachedThreadPool();

// Single thread — tasks execute sequentially
ExecutorService pool = Executors.newSingleThreadExecutor();

// Scheduled — delayed or periodic tasks
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);

// Submit tasks
pool.execute(runnable);                              // fire-and-forget
Future<Integer> future = pool.submit(callable);     // get result later

// Shutdown gracefully
pool.shutdown();             // no new tasks; wait for current to finish
pool.awaitTermination(10, TimeUnit.SECONDS);
pool.shutdownNow();          // interrupt running tasks; best-effort
```

---

## 9. Future & CompletableFuture

```java
// Future — basic async result
Future<Integer> f = pool.submit(() -> expensiveComputation());
// ... do other work ...
int result = f.get();              // blocks until done
int result = f.get(5, TimeUnit.SECONDS);  // with timeout
f.isDone();   f.cancel(true);

// CompletableFuture — chainable, non-blocking
CompletableFuture<String> cf =
    CompletableFuture.supplyAsync(() -> fetchUser(id))         // runs on ForkJoinPool
        .thenApply(user -> user.getName())                      // transform result
        .thenApply(String::toUpperCase)
        .exceptionally(ex -> "UNKNOWN")                         // handle error
        .thenAccept(System.out::println);                       // consume result

// Combine multiple
CompletableFuture<Void> all  = CompletableFuture.allOf(cf1, cf2, cf3); // wait for all
CompletableFuture<Object> any = CompletableFuture.anyOf(cf1, cf2, cf3); // first to complete

// Run on specific executor
CompletableFuture.supplyAsync(() -> work(), myExecutor).thenApplyAsync(fn, myExecutor);
```

---

## 10. Concurrent Collections

| Collection | Thread-safe version | Notes |
|------------|-------------------|-------|
| `HashMap` | `ConcurrentHashMap` | Segment-level locking; prefer over `synchronizedMap` |
| `ArrayList` | `CopyOnWriteArrayList` | Copy on every write; best for read-heavy, rare writes |
| `LinkedList` | `ConcurrentLinkedQueue` | Lock-free queue |
| `LinkedList` | `LinkedBlockingQueue` | Blocking queue for producer-consumer |
| `TreeMap` | `ConcurrentSkipListMap` | Sorted, concurrent |
| `TreeSet` | `ConcurrentSkipListSet` | Sorted, concurrent |

```java
// Producer-Consumer with BlockingQueue
BlockingQueue<Task> queue = new LinkedBlockingQueue<>(100);  // capacity 100

// Producer
queue.put(task);           // blocks if full
queue.offer(task, 1, TimeUnit.SECONDS);  // timeout version

// Consumer
Task t = queue.take();     // blocks if empty
Task t = queue.poll(1, TimeUnit.SECONDS);  // timeout version
```

---

## 11. Coordination Utilities

```java
// CountDownLatch — wait for N operations to complete
CountDownLatch latch = new CountDownLatch(3);
// workers call:
latch.countDown();                    // decrement
// main thread calls:
latch.await();                        // block until count == 0

// CyclicBarrier — N threads wait at a barrier, then proceed together
CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("All ready!"));
barrier.await();   // each thread calls; all released when count reached

// Semaphore — limit concurrent access to a resource
Semaphore sem = new Semaphore(3);    // max 3 concurrent threads
sem.acquire();   try { work(); } finally { sem.release(); }
```

---

## 12. Thread Safety Patterns

```java
// Immutable class — always thread-safe (no shared mutable state)
public final class Point {
    private final int x;
    private final int y;
    public Point(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
}

// ThreadLocal — per-thread variable (no sharing at all)
private static final ThreadLocal<SimpleDateFormat> formatter =
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

formatter.get().format(new Date());   // each thread gets its own instance
```
