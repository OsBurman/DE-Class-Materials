# Exercise 04: ExecutorService, Thread Pools, and ScheduledExecutorService

## Objective
Submit tasks to managed thread pools using `ExecutorService`, retrieve results from `Callable` via `Future`, and schedule recurring tasks with `ScheduledExecutorService`.

## Background
Creating a raw `Thread` for every task is wasteful — threads are expensive OS resources. `ExecutorService` manages a pool of reusable threads and queues incoming tasks. You submit a `Runnable` (fire-and-forget) or a `Callable<T>` (returns a value) and get back a `Future<T>` you can block on for the result. `ScheduledExecutorService` extends this with `schedule()`, `scheduleAtFixedRate()`, and `scheduleWithFixedDelay()` for timer-like behaviour.

## Requirements

1. **Fixed thread pool with Runnable**: Create a `ExecutorService` with `Executors.newFixedThreadPool(3)`. Submit 6 tasks, each printing `"Task N executed by " + Thread.currentThread().getName()`. Shut down the pool with `shutdown()` and wait for completion with `awaitTermination(5, TimeUnit.SECONDS)`.

2. **Callable and Future**: Create a `newFixedThreadPool(2)`. Submit 4 `Callable<Integer>` tasks — each takes a number, sleeps 100 ms, and returns `number * number`. Collect all four `Future<Integer>` results into a list, then iterate the list calling `future.get()` to print `"Square of N = result"`. Shut down the pool.

3. **invokeAll()**: Use `ExecutorService.invokeAll(listOfCallables)` to submit a list of 3 callables that each return a `String` description of a "simulated report" (`"Report-1 generated"`, etc.). Print each result. Shut down.

4. **ScheduledExecutorService**: Create a `newScheduledThreadPool(1)`. Use `scheduleAtFixedRate` to print `"Heartbeat: " + Instant.now()` every 200 ms. Let it run for 700 ms (using `Thread.sleep(700)` in main), then `shutdownNow()`.

## Hints
- Always shut down an `ExecutorService` — if you forget, the JVM may not exit because pool threads are non-daemon
- `future.get()` blocks until the result is ready; it may throw `ExecutionException` if the callable threw — wrap in try-catch
- `invokeAll()` blocks until ALL submitted callables complete and returns `List<Future<T>>`
- `scheduleAtFixedRate(task, initialDelay, period, unit)` — the initial delay is how long to wait before the first execution

## Expected Output

```
=== Fixed Thread Pool (Runnable) ===
Task 1 executed by pool-1-thread-1
Task 2 executed by pool-1-thread-2
Task 3 executed by pool-1-thread-3
Task 4 executed by pool-1-thread-1
Task 5 executed by pool-1-thread-2
Task 6 executed by pool-1-thread-3

=== Callable and Future ===
Square of 2 = 4
Square of 3 = 9
Square of 4 = 16
Square of 5 = 25

=== invokeAll ===
Report-1 generated
Report-2 generated
Report-3 generated

=== ScheduledExecutorService ===
Heartbeat: 2025-01-28T10:30:00.123Z
Heartbeat: 2025-01-28T10:30:00.323Z
Heartbeat: 2025-01-28T10:30:00.523Z
Heartbeat: 2025-01-28T10:30:00.723Z
Scheduled pool shut down.
```

> Note: Thread names, exact timestamp values, and task ordering will vary.
