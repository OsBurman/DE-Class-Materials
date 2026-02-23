# Week 2 - Day 9, Part 2: Thread Pools, Concurrency Utilities & CompletableFuture
## 60-Minute Lecture Script

### [00:00-02:00] Welcome & Part 1 Recap
Welcome back! We're in the second half of Day 9 on multithreading, and this is where things get really practical. In Part 1, we built the foundation—you learned about threads, synchronization, race conditions, deadlock. Now, we're going to take everything you learned and see how professional developers actually handle concurrency in real applications.

Here's the key insight: manually synchronizing code is hard. It's error-prone, it's complex, and it's not how production systems work. Instead, Java provides higher-level abstractions that handle synchronization for you. Think of it like this—in Part 1, you were managing threads like you'd manage raw memory in C. Now, we're moving to frameworks that abstract away the complexity, just like Java's garbage collector abstracts away memory management.

Today we'll cover: thread pools and ExecutorService, concurrent collections that are safe without explicit synchronization, coordination utilities like CountDownLatch and Semaphore, and the crown jewel—CompletableFuture for writing clean, composable async code. Let's dive in.

### [02:00-04:00] The Problem with Manual Thread Creation
So, why can't we just keep creating new threads like we did in Part 1? Let me paint a picture. Your web server needs to handle 10,000 concurrent requests. Your first thought: create a new thread for each request, right? Well, here's what happens.

Thread creation is expensive. It's not just allocation—when you `new Thread()`, you're allocating about 1MB of stack memory per thread. Creating a thread takes roughly 1 millisecond. So 10,000 threads? That's 10 seconds just to create them, plus 10GB of memory. Your server crashes before it even finishes creating threads.

Second problem: even if you had infinite memory, unlimited threads mean unlimited context switching. The CPU spends more time switching between threads than actually running code. You've got contention, cache misses, complete system slowdown.

Third problem: there's no way to limit concurrency. With unlimited threads, you have unlimited resource consumption. No backpressure. No graceful degradation.

The solution? Thread pools. Instead of creating a thread per task, you maintain a fixed pool of reusable threads. Tasks queue up, and workers pull from the queue. You can handle 10,000 requests with just 50 threads, reused over and over. It's elegant, it's efficient, and it's the foundation of every production system.

### [04:00-06:00] ExecutorService & Thread Pools Concept
So what's an ExecutorService? It's an interface in `java.util.concurrent` that manages a thread pool for you. You submit tasks, and the service executes them using its pool of threads.

Here's the mental model: You have a task queue and a pool of worker threads. When you submit a task, it goes into the queue. Any available worker thread grabs the next task and executes it. When done, the thread goes back and waits for the next task. Tasks queue up if all threads are busy. Once you're done, you shutdown the executor gracefully.

The beauty is this: you don't create threads. You don't manually coordinate. You just submit tasks and let the ExecutorService handle distribution. It manages the thread lifecycle, the queue, everything.

Java provides factory methods through the `Executors` utility class to create common pool types. `Executors.newFixedThreadPool(5)` creates a pool with exactly 5 threads. They're created upfront, they reuse tasks, and when you shutdown, they gracefully terminate.

The flow is simple: submit task → queued → available thread executes → thread reuses → loop until shutdown. You describe what work needs to happen, and the framework handles when and where it happens.

### [06:00-12:00] Creating & Using ExecutorService
Let's get concrete. Here's how you create an executor:

```java
ExecutorService executor = Executors.newFixedThreadPool(5);
```

That creates 5 threads, ready to go. Now you submit work. The simplest way is with `execute()`:

```java
executor.execute(() -> System.out.println("Running task"));
```

The lambda is a Runnable—no parameters, no return value. The executor runs it asynchronously on one of the 5 threads. Your main thread continues immediately.

Here's the thing: you don't know which thread runs your task. You don't know when. It happens at some point. And that's okay because your lambda shouldn't care about threading details.

But what if you need a result? That's where `submit()` comes in:

```java
Future<Integer> future = executor.submit(() -> 42);
```

That's a Callable (note the lambda has a return value). The executor returns a Future—a handle to the result. You can ask it: is the result ready? Get the result. Cancel the task. Here's how:

```java
if (!future.isDone()) {
    System.out.println("Still computing...");
}
Integer result = future.get();  // Blocks until ready
```

`get()` blocks. Your thread waits. Once the async task completes, get() returns. If the task throws an exception, get() rethrows it wrapped in ExecutionException. Crucially, you can use `get(timeout, TimeUnit)` to wait only a certain amount:

```java
try {
    Integer result = future.get(2, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    System.out.println("Took too long");
}
```

Then you shutdown:

```java
executor.shutdown();  // No new tasks accepted, wait for existing to finish
executor.awaitTermination(10, TimeUnit.SECONDS);  // Wait up to 10 seconds
```

Common pattern: submit many tasks, collect futures, retrieve results later. Let me show you:

```java
List<Future<Integer>> futures = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    futures.add(executor.submit(() -> expensiveComputation(i)));
}
List<Integer> results = new ArrayList<>();
for (Future<Integer> future : futures) {
    results.add(future.get());  // Collect all results
}
executor.shutdown();
```

Notice what we're doing here: we're launching 10 async tasks. We continue. Once all are submitted, we collect results. This is fundamentally different from sequential execution. We're leveraging parallelism.

### [12:00-18:00] Built-in Executor Types
The Executors class provides several factory methods. `newFixedThreadPool(n)` is the most common—you get exactly n threads, no more, no less. Great for I/O-bound work or CPU-bound work where you want to limit resource usage.

There's `newCachedThreadPool()`. This one creates threads as needed and caches them for reuse. If you submit a task and all threads are busy, it creates a new one. If a thread sits idle for 60 seconds, it terminates. Why use this? When you don't know the workload in advance. The pool scales dynamically. But be careful—if requests come in faster than processing, you can create unlimited threads. It's flexible but risky without limits.

`newSingleThreadExecutor()` creates a pool with one thread. Tasks execute sequentially. Why not just use a single thread manually? Because ExecutorService provides consistent semantics, error handling, shutdown management. You're guaranteed sequential processing, but the framework handles all the boilerplate.

And there's `newScheduledThreadPool(n)` for periodic or delayed tasks. Instead of implementing delay logic yourself, you schedule work:

```java
executor.schedule(() -> doWork(), 5, TimeUnit.SECONDS);  // In 5 seconds
executor.scheduleAtFixedRate(() -> doWork(), 0, 2, TimeUnit.SECONDS);  // Every 2 seconds
```

Each serves a purpose. For typical business applications, newFixedThreadPool is your go-to. Now, how many threads? The rule of thumb: for CPU-bound work, use cores (Runtime.getRuntime().availableProcessors()). For I/O-bound, use 2-3 times cores. Why? CPU-bound means threads block waiting for CPU time. More threads just cause context switching overhead. I/O-bound means threads block waiting for I/O. While one thread waits for disk or network, another can run. So more threads are beneficial.

### [18:00-24:00] Concurrent Collections
Now, let's talk about coordinating access to shared data. In Part 1, we synchronized everything. But synchronization is expensive and limited. What if we could use data structures specifically designed for concurrency?

That's concurrent collections. Traditional collections like HashMap and ArrayList aren't thread-safe. Multiple threads modifying them simultaneously causes corruption—lost updates, internal inconsistency. One approach: synchronize all accesses. But that's cumbersome:

```java
List<String> list = Collections.synchronizedList(new ArrayList<>());
synchronized (list) {
    for (String item : list) {  // Must synchronize iteration too
        System.out.println(item);
    }
}
```

That's not practical. The second approach: concurrent collections, specifically designed for multiple threads.

Let's start with ConcurrentHashMap. It's a thread-safe HashMap alternative. Here's the trick: instead of locking the entire map, it uses segment-based locking. The map is divided into segments; each segment has its own lock. So multiple threads can modify different segments simultaneously. This is called lock striping. Read operations often don't block at all. Performance is dramatically better than synchronized HashMap.

Here's how you use it:

```java
Map<String, Integer> map = new ConcurrentHashMap<>();
map.put("alice", 100);
Integer value = map.get("alice");  // Thread-safe
```

No explicit synchronization needed. Under the hood, ConcurrentHashMap handles it. Iterators are weakly consistent—they might see updates made during iteration, but they won't throw ConcurrentModificationException. That's a trade-off for performance.

Similarly, CopyOnWriteArrayList is a thread-safe ArrayList. It uses copy-on-write: when you modify the list, it creates a copy, modifies the copy, then swaps the reference. Reads are never blocked; they see a consistent snapshot. Writes incur copy overhead. When should you use it? When reads vastly outnumber writes. Event listeners are a classic example—many readers subscribe, few modify the list.

```java
List<EventListener> listeners = new CopyOnWriteArrayList<>();
// Many threads iterating (no locks)
for (EventListener listener : listeners) {
    listener.onEvent(event);
}
// Few threads adding/removing
listeners.add(newListener);
```

Then there's BlockingQueue. This is crucial for producer-consumer patterns. A queue is thread-safe and has blocking operations:

```java
BlockingQueue<String> queue = new LinkedBlockingQueue<>();
queue.put("item");  // Blocks if queue is full
String item = queue.take();  // Blocks if queue is empty
```

put() and take() block—they don't spin, they don't poll. The queue coordinates threads elegantly. No explicit wait/notify needed. Here's a classic producer-consumer:

```java
BlockingQueue<String> queue = new LinkedBlockingQueue<>();

// Producer
new Thread(() -> {
    for (String item : items) {
        queue.put(item);  // Block if full
    }
}).start();

// Consumer
new Thread(() -> {
    String item = queue.take();  // Block if empty
    processItem(item);
}).start();
```

The queue handles synchronization. The producer blocks if the queue fills up. The consumer blocks if the queue is empty. It's simple, elegant, and robust. This pattern is everywhere in production systems—work queues, message processing, any pipeline pattern.

### [24:00-30:00] Coordination Utilities
Beyond collections, there are utilities specifically for coordinating threads. These are in `java.util.concurrent` and are incredibly useful.

Let's start with CountDownLatch. Imagine you have 10 worker threads, and you want the main thread to wait until all 10 finish. CountDownLatch does this:

```java
CountDownLatch latch = new CountDownLatch(3);  // Wait for 3 threads

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        doWork();
        latch.countDown();  // Signal done
    }).start();
}

latch.await();  // Block until all 3 countDown()
System.out.println("All workers done");
```

The latch has an internal counter. countDown() decrements it. await() blocks until it reaches zero. It's one-time use—once zero, it stays zero. You can't reset it. Great for one-off synchronization.

Semaphore is different. It's like a bouncer at a club. Only N people can be inside. Everyone else waits. Use it to limit concurrent access:

```java
Semaphore semaphore = new Semaphore(5);  // Allow 5 concurrent

for (int i = 0; i < 20; i++) {
    new Thread(() -> {
        semaphore.acquire();  // Wait if full
        try {
            useExpensiveResource();
        } finally {
            semaphore.release();  // Free up spot
        }
    }).start();
}
```

Only 5 threads can be inside the critical section at a time. Others wait. This is perfect for connection pools—you have 5 database connections; only 5 threads can use them simultaneously. The others queue up.

CyclicBarrier is another synchronization point. All threads arrive at the barrier and wait. Once N threads arrive, all are released:

```java
CyclicBarrier barrier = new CyclicBarrier(3);

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        phase1Work();
        barrier.await();  // Wait for all 3
        phase2Work();
    }).start();
}
```

Unlike CountDownLatch, CyclicBarrier resets. You can use it for multiple phases. All threads do phase 1, wait at the barrier, then all do phase 2 together. Great for iterative parallel algorithms.

These three—CountDownLatch, Semaphore, CyclicBarrier—are your coordination toolkit. Learn them well.

### [30:00-36:00] Common Mistakes with ExecutorService
Let me highlight two critical mistakes I see constantly.

**Mistake 1: Forgetting to shutdown.** You create an executor, submit tasks, but forget to shutdown:

```java
ExecutorService executor = Executors.newFixedThreadPool(5);
executor.submit(task);
System.out.println("Done");
// Program exits, but threads still running!
```

Your program might exit, but those threads are still alive, consuming resources. If the main method finishes, the JVM hangs because non-daemon threads keep it alive. The threads are doing nothing, just sitting there. It's a resource leak.

The fix: always shutdown. Use try-finally:

```java
ExecutorService executor = Executors.newFixedThreadPool(5);
try {
    executor.submit(task);
} finally {
    executor.shutdown();
}
```

Or, graceful shutdown with timeout:

```java
executor.shutdown();
if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
    executor.shutdownNow();  // Force stop if it takes too long
}
```

shutdown() stops accepting new tasks and waits for existing tasks to finish. shutdownNow() cancels pending tasks and interrupts running ones. awaitTermination() waits. Combine them for robust shutdown.

**Mistake 2: Ignoring Future results and exceptions.** You submit a task and never check:

```java
Future<Integer> future = executor.submit(() -> {
    // What if this throws an exception?
    return riskyOperation();
});

// ... later, or never
// future.get() never called
```

Exceptions are silent. The task fails, but your code never knows. You're debugging, everything looks normal, and you can't find the problem because the exception is hidden.

The fix: always get the result:

```java
Future<Integer> future = executor.submit(() -> riskyOperation());
try {
    Integer result = future.get();  // May throw ExecutionException
    // use result
} catch (ExecutionException e) {
    System.err.println("Task failed: " + e.getCause());
}
```

ExecutionException wraps the original exception. getCause() unwraps it. Now you see what went wrong. Even if you don't care about the result, call get() for exception handling:

```java
executor.submit(() -> importantSideEffect());
try {
    // This future completes with null
    executor.submit(() -> importantSideEffect()).get();
} catch (ExecutionException e) {
    System.err.println("Error: " + e.getCause());
}
```

Actually, wait. There's a better way for this—we're about to learn it.

### [36:00-42:00] CompletableFuture: The Future Evolution
Future has a problem: get() is blocking. You submit async work, but to retrieve the result, you block. That defeats some of the purpose of async.

CompletableFuture fixes this. It's a Future that you can complete programmatically, and it supports callbacks. Imagine:

```java
CompletableFuture<Integer> future = new CompletableFuture<>();
// ... later, on some thread
future.complete(42);  // Trigger callbacks, wake waiters
```

Or, use supplyAsync for async computation:

```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 
    expensiveComputation()
);
```

That runs expensiveComputation() on an executor thread. The future completes when the computation finishes. Now here's the cool part—instead of blocking with get(), you register a callback:

```java
future.thenAccept(result -> 
    System.out.println("Result: " + result)
);
```

The callback runs when the future completes. Your thread doesn't block. If the computation finishes later, the callback still runs. Non-blocking async code.

You can chain operations:

```java
CompletableFuture.supplyAsync(() -> fetchUserData(id))
    .thenApply(userData -> processUserData(userData))
    .thenAccept(processedData -> renderUI(processedData));
```

Each stage transforms the result. thenApply() transforms (Function). thenAccept() consumes (Consumer). thenRun() runs ignoring result (Runnable). They compose beautifully. The whole chain is non-blocking.

Compare to traditional callbacks:

```java
// Callback hell
fetchUserData(id, userData -> {
    processUserData(userData, processedData -> {
        renderUI(processedData);  // Nested, hard to read
    });
});
```

CompletableFuture:

```java
// Fluent, readable
CompletableFuture.supplyAsync(() -> fetchUserData(id))
    .thenApply(userData -> processUserData(userData))
    .thenAccept(processedData -> renderUI(processedData));
```

Linear, composable, clean. This is the modern way to write async code.

### [42:00-48:00] Combining Futures & Exception Handling
Often you need multiple async operations. For example, fetch user data AND fetch their orders. Both take time. You want to do them in parallel, then combine results.

CompletableFuture provides tools:

```java
CompletableFuture<User> userFuture = 
    CompletableFuture.supplyAsync(() -> fetchUser(id));
CompletableFuture<List<Order>> ordersFuture = 
    CompletableFuture.supplyAsync(() -> fetchOrders(id));

// Wait for both, combine
userFuture.thenCombine(ordersFuture, (user, orders) -> 
    new UserWithOrders(user, orders)
)
.thenAccept(result -> renderUI(result));
```

thenCombine() waits for both futures, then combines with a function. It's non-blocking. Both async operations run in parallel. Once both complete, the combiner function runs.

allOf() waits for all futures in a collection:

```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");
CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "C");

CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2, f3);
all.thenRun(() -> System.out.println("All done"));
```

Note: allOf returns Void, so you can't directly get results. But you can then get them individually.

anyOf() waits for the first to complete:

```java
CompletableFuture<?> first = CompletableFuture.anyOf(f1, f2, f3);
first.thenAccept(result -> System.out.println("First done: " + result));
```

What about exceptions? supplyAsync might throw:

```java
CompletableFuture.supplyAsync(() -> riskyOperation())
    .exceptionally(ex -> {
        System.err.println("Error: " + ex.getMessage());
        return defaultValue;  // Fallback
    })
    .thenAccept(System.out::println);
```

exceptionally() handles exceptions. The function returns a fallback value. The future continues with that fallback. If you want to handle both success and exception:

```java
CompletableFuture.supplyAsync(() -> riskyOperation())
    .handle((result, ex) -> {
        if (ex != null) {
            System.err.println("Error: " + ex.getMessage());
            return defaultValue;
        }
        return result;
    })
    .thenAccept(System.out::println);
```

handle() receives both result (null if exception) and exception (null if success). You handle both cases. This is cleaner for complex exception logic. You can also use whenComplete() for cleanup:

```java
CompletableFuture.supplyAsync(() -> operation())
    .whenComplete((result, ex) -> {
        if (ex != null) 
            logError(ex);
        else 
            logSuccess(result);
    });
```

whenComplete() doesn't transform; it's just a hook for side effects.

### [48:00-54:00] Real-World CompletableFuture Example
Let's put it together. Imagine an API that fetches user data from a service, processes it, and caches the result. All async.

```java
public CompletableFuture<UserData> getUserDataAsync(String userId) {
    return CompletableFuture.supplyAsync(() -> {
        UserData cached = cache.get(userId);
        if (cached != null) return cached;
        return null;  // Not cached
    })
    .thenCompose(cached -> {
        if (cached != null) {
            // Already cached, return immediately
            return CompletableFuture.completedFuture(cached);
        }
        // Fetch from service
        return CompletableFuture.supplyAsync(() -> 
            serviceClient.fetchUser(userId)
        );
    })
    .thenApply(userData -> {
        // Cache result
        cache.put(userId, userData);
        return userData;
    })
    .exceptionally(ex -> {
        logger.error("Failed to fetch user: " + userId, ex);
        return defaultUser;
    });
}
```

Notice thenCompose(). Unlike thenApply(), thenCompose() chains another CompletableFuture. If your function returns a CompletableFuture, thenCompose() unwraps it. Here, if cached, we return already-completed future. If not, we fetch and return a future that will complete later. thenCompose() flattens this.

The flow: check cache → if cached, return; if not, fetch → cache result → on error, return default.

This runs entirely async. The caller calls this method and gets a future back. They can attach callbacks, chain operations, all without blocking.

Now imagine we need to combine this with order fetching:

```java
CompletableFuture<UserData> userFuture = getUserDataAsync(userId);
CompletableFuture<List<Order>> ordersFuture = getOrdersAsync(userId);

userFuture.thenCombine(ordersFuture, (userData, orders) -> 
    new UserWithOrders(userData, orders)
)
.thenAccept(combined -> renderUI(combined))
.exceptionally(ex -> {
    showErrorDialog("Failed to load user: " + ex.getMessage());
    return null;
});
```

Parallel fetching, combined result, rendered. All async, all non-blocking. This is how production web applications handle concurrent operations.

### [54:00-58:00] Common Mistakes with Concurrency
Let me highlight mistakes I see repeatedly.

**Mistake: Blocking in callbacks.** Your callback does expensive work:

```java
CompletableFuture.supplyAsync(() -> operation())
    .thenAccept(result -> {
        expensiveComputation(result);  // Blocks the callback thread
        renderUI();
    });
```

If expensiveComputation takes 10 seconds, the callback thread is blocked for 10 seconds. If you're using a shared executor (default is ForkJoinPool), you're potentially blocking work from other tasks. Fix: submit expensive work to executor:

```java
.thenAcceptAsync(result -> {
    expensiveComputation(result);  // On executor thread
    renderUI();
}, customExecutor);
```

thenAcceptAsync() runs the callback on an executor thread.

**Mistake: Forgetting to handle exceptions.** Exceptions in futures are silent:

```java
CompletableFuture.supplyAsync(() -> riskyOperation())
    .thenAccept(result -> System.out.println(result));
    // If riskyOperation throws, nothing happens!
```

The future fails silently. You never see the error. Always add exception handling:

```java
.thenAccept(result -> System.out.println(result))
.exceptionally(ex -> {
    logger.error("Failed", ex);
    return null;
});
```

**Mistake: Assuming execution order.** You chain operations and assume one runs before the next:

```java
CompletableFuture.supplyAsync(() -> operation1());
CompletableFuture.supplyAsync(() -> operation2());
// No guarantee operation2 waits for operation1!
```

These are independent. Both run simultaneously. To ensure sequence:

```java
CompletableFuture.supplyAsync(() -> operation1())
    .thenCompose(res1 -> CompletableFuture.supplyAsync(() -> operation2(res1)));
```

Now operation2 depends on operation1's result.

### [58:00-60:00] Summary & Transition
So, we've covered a lot. ExecutorService manages thread pools, so you don't create threads manually. Concurrent collections provide thread-safe data structures without explicit synchronization. CountDownLatch, Semaphore, CyclicBarrier coordinate threads. CompletableFuture makes async code clean and composable.

Here's the overarching pattern: abstract away manual threading. Use high-level tools. Get concurrency right. These tools are standard—ExecutorService, concurrent collections, CompletableFuture—they're everywhere in production Java.

Tomorrow, Day 10 (Friday), we're shifting topics. We'll cover Advanced Java: Big O analysis, design patterns, memory models, garbage collection. We're moving away from threading to other advanced concepts. Multithreading is the foundation for scalable systems; tomorrow we dig into optimization and architecture.

For the capstone project in Week 6, you'll build a web server with concurrent requests. You'll use ExecutorService. You'll probably use concurrent collections. These tools aren't theoretical; they're essential.

For now, practice. Write some concurrent code. Create an executor, submit tasks, handle futures, attach callbacks. Build intuition. Next week, we start Spring and databases. The backend gets real.

Great work on Day 9. You've covered a lot today. See you tomorrow!

