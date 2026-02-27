# Exercise 09 — Multithreading

## Overview
Build a **Concurrent Download Simulator** that models downloading files in parallel using Java's concurrency tools.

## Learning Objectives
- Create threads using `Thread` and `Runnable`
- Use `synchronized` methods/blocks and `volatile` fields
- Apply `ExecutorService` with thread pools
- Work with `Future<T>` and `Callable<T>`
- Chain async tasks with `CompletableFuture`
- Use `CountDownLatch` for thread coordination

## Setup
```bash
cd Exercise-09-Multithreading/starter-code/src
javac Main.java
java Main
```

## The Application
A `DownloadManager` coordinates simulated file downloads. Each download takes random time (sleep). You will implement the concurrency layer.

## Your TODOs

| # | Task | Concept |
|---|------|---------|
| 1 | `DownloadTask implements Runnable` | Basic `Thread` / `Runnable` |
| 2 | `ProgressTracker` — `increment()`, `getCount()` | `synchronized` methods |
| 3 | `runSequential(List<String>)` | Start + join threads manually |
| 4 | `runWithExecutor(List<String>, int)` | `ExecutorService`, `submit`, `shutdown` |
| 5 | `runWithCallable(List<String>)` | `Callable<String>`, `Future<String>` |
| 6 | `runWithCompletableFuture(List<String>)` | `CompletableFuture.supplyAsync` + `thenApply` |
| 7 | `runWithLatch(List<String>)` | `CountDownLatch` |

## Expected Output (order may vary due to threading)
```
=== Sequential ===
[Thread-0] Starting: report.pdf (1.2 MB)
[Thread-1] Starting: photo.jpg (3.4 MB)
...
[Thread-0] Done:     report.pdf  ✓  (312 ms)
...
Total downloads tracked: 4

=== ExecutorService (pool=2) ===
[pool-1-thread-1] Starting: ...
...

=== Callable / Future ===
Future result: "report.pdf downloaded in 234ms"
...

=== CompletableFuture ===
Async result: REPORT.PDF → processed
...

=== CountDownLatch ===
All 4 downloads complete. Latch released!
```

## Hints
- `Thread t = new Thread(runnable); t.start(); t.join();`
- `ExecutorService pool = Executors.newFixedThreadPool(n);`
- `Future<String> f = pool.submit(callable); f.get();`
- `CompletableFuture.supplyAsync(() -> download(url)).thenApply(String::toUpperCase)`
- `CountDownLatch latch = new CountDownLatch(n); latch.countDown(); latch.await();`
- `Thread.sleep(ms)` simulates download time — wrap in try/catch InterruptedException
