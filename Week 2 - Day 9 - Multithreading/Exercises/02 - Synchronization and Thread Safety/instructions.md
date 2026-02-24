# Exercise 02: Synchronization and Thread Safety

## Objective
Use the `synchronized` keyword and `volatile` to protect shared state and eliminate race conditions when multiple threads access the same data.

## Background
When two threads read and write the same variable without coordination, results are unpredictable — a classic **race condition**. Java's `synchronized` keyword guarantees that only one thread at a time executes a marked block or method. `volatile` ensures that reads and writes to a variable go directly to main memory, making simple flag variables visible across threads. Without these tools, a shared counter incremented by 10 threads may lose updates silently.

## Requirements

1. **Demonstrate the race condition**: Create a class `UnsafeCounter` with a plain `int count` field and an `increment()` method that does `count++`. Launch 5 threads, each calling `increment()` 1000 times. Join all threads and print the final count. (It should frequently be less than 5000.)

2. **Fix with synchronized method**: Create a class `SafeCounter` with `private int count` and a `synchronized void increment()` method. Repeat the same 5-thread / 1000-increments test. Print the final count — it should always be exactly `5000`.

3. **Synchronized block on a lock object**: Create a class `BlockCounter` with a `private final Object lock = new Object()` and a non-synchronized `increment()` method that uses `synchronized(lock) { count++; }`. Run the same test and verify the result is `5000`.

4. **volatile flag**: Create a class `WorkerThread extends Thread`. Give it a `volatile boolean running = true` flag. Its `run()` method loops while `running` is true, incrementing a local counter. In `main`, start the thread, sleep 50 ms, then set `running = false` and `join()` the thread. Print `"Worker stopped after N iterations"`.

## Hints
- `count++` is NOT atomic — it is three bytecode instructions (read, add, write), so two threads can interleave them and lose an increment
- `synchronized` on a method uses `this` as the monitor; a `synchronized` block lets you choose the lock object
- `volatile` only guarantees visibility, not atomicity — it's suitable for simple flag checks but NOT for `count++`
- For the race condition demo, you may need to run it a few times to observe the wrong answer; some machines are fast enough to occasionally get the right answer by luck

## Expected Output

```
=== Race Condition Demo (Unsafe) ===
Unsafe final count (expected 5000): 4823   ← will vary

=== Synchronized Method Demo (Safe) ===
Safe final count (expected 5000): 5000

=== Synchronized Block Demo (BlockCounter) ===
BlockCounter final count (expected 5000): 5000

=== Volatile Flag Demo ===
Worker stopped after 184023 iterations
```

> Note: The unsafe count and worker iteration count will vary on every run.
