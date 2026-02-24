# Exercise 06: CompletableFuture Async Workflows

## Objective
Compose non-blocking asynchronous pipelines using `CompletableFuture` — chaining transformations, combining futures, and handling errors gracefully.

## Background
`CompletableFuture<T>` (Java 8+) is the modern alternative to `Future<T>`. While `Future.get()` blocks the calling thread, `CompletableFuture` lets you chain callbacks that run when the result arrives, without blocking. This enables highly readable async pipelines: fetch data → transform it → combine with another fetch → handle errors. The common API includes `supplyAsync`, `thenApply`, `thenCompose`, `thenCombine`, `allOf`, `exceptionally`, and `handle`.

## Requirements

1. **supplyAsync and thenApply**: Create a `CompletableFuture<String>` using `supplyAsync(() -> "hello world")`. Chain a `thenApply(s -> s.toUpperCase())` and another `thenApply(s -> "Result: " + s)`. Block with `join()` and print the result.

2. **thenCompose** (flat-map for futures): Simulate a two-step async lookup:
   - Step 1: `supplyAsync(() -> 42)` — returns a user ID
   - Step 2: `thenCompose(id -> CompletableFuture.supplyAsync(() -> "User-" + id + " profile loaded"))` — uses the ID to "fetch" a profile
   - Print the result with `join()`

3. **thenCombine** (zip two independent futures):
   - `futureA = supplyAsync(() -> "Price: $99")` (simulates fetching product price)
   - `futureB = supplyAsync(() -> "Stock: 14 units")` (simulates fetching stock level)
   - Combine: `futureA.thenCombine(futureB, (a, b) -> a + " | " + b)` and print with `join()`

4. **allOf** (wait for many futures): Submit 4 async tasks that each return a string like `"Task-N done"`. Use `CompletableFuture.allOf(futures...)` to wait for all, then collect and print each result.

5. **exceptionally** (error handling): Create a `CompletableFuture` that `supplyAsync` throws a `RuntimeException("Simulated failure")`. Chain `.exceptionally(ex -> "Recovered: " + ex.getMessage())`. Print the result with `join()` — it should print the recovery message, not throw.

6. **handle** (result + error in one callback): Create a future that may or may not succeed based on a flag. Use `.handle((result, ex) -> ex != null ? "Error: " + ex.getMessage() : "Success: " + result)` to produce a safe final value regardless. Print both the failure-path and success-path outcomes.

## Hints
- `supplyAsync(supplier)` uses the common `ForkJoinPool` by default — no need to manage threads manually
- `thenApply` is like stream's `map` — it transforms the value; `thenCompose` is like `flatMap` — the function itself returns a future
- `CompletableFuture.allOf()` returns `CompletableFuture<Void>` — call `.join()` on it, then get each individual future's result with `.join()`
- `exceptionally` only fires if the stage completed exceptionally; on the happy path it passes the value through unchanged

## Expected Output

```
=== supplyAsync + thenApply ===
Result: HELLO WORLD

=== thenCompose ===
User-42 profile loaded

=== thenCombine ===
Price: $99 | Stock: 14 units

=== allOf ===
Task-1 done
Task-2 done
Task-3 done
Task-4 done

=== exceptionally ===
Recovered: Simulated failure

=== handle ===
Failure path: Error: handle failure
Success path: Success: all good
```
