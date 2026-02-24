# Exercise 07: Concurrency Utilities — CountDownLatch and Semaphore

## Objective
Coordinate multiple threads using `CountDownLatch` and control concurrent access to limited resources with `Semaphore`.

## Background
`java.util.concurrent` provides higher-level building blocks that are more expressive than raw `synchronized` blocks. `CountDownLatch` is initialized with a count; threads call `await()` to block until other threads each call `countDown()` to decrement the count to zero — useful for "wait for N things to finish" patterns (like a starting gun or a barrier). `Semaphore` controls access to a resource with a fixed number of permits — `acquire()` takes a permit (blocking if none are available) and `release()` returns one, making it ideal for rate-limiting concurrent access.

> **Awareness**: Two other important concurrency tools worth knowing about (no coding required):
> - **ForkJoinPool** — a work-stealing thread pool designed for divide-and-conquer tasks (used internally by parallel streams and `CompletableFuture.supplyAsync`)
> - **Virtual Threads (Project Loom, Java 21+)** — lightweight threads managed by the JVM, not the OS; can scale to millions of threads; created with `Thread.ofVirtual().start(task)`

## Requirements

1. **CountDownLatch — starting gate**: Simulate a race where 4 runner threads wait for a starting signal.
   - Create a `CountDownLatch startSignal = new CountDownLatch(1)`
   - Create 4 threads each named `"Runner-N"` that call `startSignal.await()`, then print `"Runner-N is running!"`
   - After starting all runner threads, sleep 200 ms (simulate pre-race setup), then call `startSignal.countDown()` to release all runners at once

2. **CountDownLatch — completion barrier**: Simulate 3 worker threads that each do some work and signal completion.
   - Create a `CountDownLatch done = new CountDownLatch(3)`
   - Create 3 threads each named `"Worker-N"` that sleep a short duration, print `"Worker-N finished"`, then call `done.countDown()`
   - In main, print `"Waiting for workers..."`, call `done.await()`, then print `"All workers done — proceeding."`

3. **Semaphore — resource pool**: Simulate a database connection pool with only 2 connections available.
   - Create a `Semaphore connectionPool = new Semaphore(2)` (2 permits = 2 simultaneous connections)
   - Launch 5 threads each named `"Client-N"`. Each thread calls `connectionPool.acquire()`, prints `"Client-N: connected (permits left: " + connectionPool.availablePermits() + ")"`, sleeps 150 ms to simulate work, then calls `connectionPool.release()` and prints `"Client-N: disconnected"`
   - At most 2 clients should be connected at any time

## Hints
- `CountDownLatch` is single-use — once the count reaches 0, `await()` returns immediately for all future callers and the latch cannot be reset
- `latch.countDown()` can be called safely from any thread and never blocks
- `semaphore.availablePermits()` may fluctuate during the print statement — that's fine; approximate values are expected
- `Semaphore(permits, true)` creates a *fair* semaphore that grants permits in FIFO order — the default is non-fair

## Expected Output

```
=== CountDownLatch: Starting Gate ===
All runners ready. Ready... Set...
GO!
Runner-1 is running!
Runner-2 is running!
Runner-3 is running!
Runner-4 is running!

=== CountDownLatch: Completion Barrier ===
Waiting for workers...
Worker-1 finished
Worker-2 finished
Worker-3 finished
All workers done — proceeding.

=== Semaphore: Connection Pool (max 2) ===
Client-1: connected (permits left: 1)
Client-2: connected (permits left: 0)
Client-1: disconnected
Client-3: connected (permits left: 0)
Client-2: disconnected
Client-4: connected (permits left: 0)
Client-3: disconnected
Client-5: connected (permits left: 0)
Client-4: disconnected
Client-5: disconnected

=== Awareness: ForkJoinPool & Virtual Threads ===
ForkJoinPool: work-stealing pool used by parallel streams and CompletableFuture.
  Ideal for divide-and-conquer CPU-bound tasks.
  Common pool: ForkJoinPool.commonPool()

Virtual Threads (Java 21+ / Project Loom):
  Lightweight threads managed by the JVM — not OS threads.
  Can scale to millions. Created with: Thread.ofVirtual().start(task)
  Ideal for high-throughput I/O-bound workloads.
```

> Note: client connection/disconnection order will vary; at most 2 connections will be active simultaneously.
