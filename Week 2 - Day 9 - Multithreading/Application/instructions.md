# Day 9 Application — Multithreading: Concurrent Order Processor

## Overview

You'll build a **Concurrent Order Processor** — a simulation of a restaurant order system where multiple chefs (threads) process orders (tasks) simultaneously. This covers threads, synchronization, the producer-consumer pattern, `ExecutorService`, and `CompletableFuture`.

---

## Learning Goals

- Create threads using `Thread` class and `Runnable` interface
- Understand thread states and lifecycle
- Use `synchronized` to prevent race conditions
- Implement the producer-consumer pattern
- Use `ExecutorService` and thread pools
- Compose async workflows with `CompletableFuture`
- Use `CountDownLatch` for thread coordination

---

## Project Structure

```
starter-code/
└── src/
    ├── Main.java
    ├── OrderQueue.java          ← TODO: thread-safe shared queue
    ├── OrderProducer.java       ← TODO: Runnable producer
    ├── ChefWorker.java          ← TODO: Runnable consumer
    └── KitchenManager.java      ← TODO: ExecutorService + CompletableFuture
```

---

## Part 1 — Thread-Safe `OrderQueue`

**Task 1**  
Add a `private int ordersProcessed` counter and a `private List<String> completedOrders` list.  
Make `incrementProcessed()` and `getOrdersProcessed()` **synchronized**.  
Use a `LinkedList<String>` and wrap `add()` and `poll()` as `synchronized` methods `addOrder()` and `takeOrder()`.

---

## Part 2 — Producer & Consumer Runnables

**Task 2 — `OrderProducer` implements `Runnable`**  
In `run()`: loop 5 times — generate an order string like `"Order-[i]: [item]"` and call `queue.addOrder()`. Sleep 200ms between orders. Print each order placed.

**Task 3 — `ChefWorker` implements `Runnable`**  
In `run()`: loop and call `queue.takeOrder()`. If an order is available, "process" it (sleep 300ms to simulate cooking), increment the counter, and print `"[chefName] cooked: [order]"`. Stop after a set number of orders.

---

## Part 3 — `KitchenManager`

**Task 4 — `ExecutorService` thread pool**  
Create a fixed thread pool with 3 chefs (`Executors.newFixedThreadPool(3)`).  
Submit the producer and multiple `ChefWorker` tasks to the pool.  
Call `shutdown()` and `awaitTermination()`.

**Task 5 — `CountDownLatch`**  
Use a `CountDownLatch(3)` — each chef calls `latch.countDown()` when finished. The manager waits with `latch.await()` then prints `"All orders complete!"`.

**Task 6 — `CompletableFuture`**  
Write a `prepareSpecialDish(String dish)` method that returns `CompletableFuture<String>`.  
Chain: `supplyAsync` (simulated prep, sleep 500ms) → `thenApply` (add garnish) → `thenAccept` (print result).  
Call two of these and combine with `CompletableFuture.allOf()`.

---

## Part 4 — `Main.java`

Wire everything together: create the queue, start producer and workers via `KitchenManager`, run the special dish futures. Print total orders processed at the end.

---

## Stretch Goals

1. Introduce an artificial deadlock by having two threads each hold a lock and wait for the other's lock. Then explain and fix it.
2. Replace `synchronized` methods with a `ReentrantLock`.
3. Use `ScheduledExecutorService` to simulate a daily specials menu printing every 2 seconds.

---

## Submission Checklist

- [ ] Thread created via `Thread` class OR `Runnable`
- [ ] `synchronized` method used on shared state
- [ ] Producer-consumer pattern demonstrated
- [ ] `ExecutorService` with fixed thread pool used
- [ ] `shutdown()` and `awaitTermination()` called
- [ ] `CountDownLatch` used for coordination
- [ ] `CompletableFuture` with `supplyAsync`, `thenApply`, `thenAccept` chained
- [ ] `CompletableFuture.allOf()` used
