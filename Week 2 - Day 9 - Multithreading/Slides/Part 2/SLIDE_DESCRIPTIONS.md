# Week 2 - Day 9, Part 2: Thread Pools, Concurrency Utilities & CompletableFuture
## Slide Descriptions (60-minute lecture)

### Slides 1-2: Welcome & Recap
**Slide 1: Title Slide**
- Title: "Modern Multithreading: Pools, Utilities & Async"
- Subtitle: "Higher-Level Concurrency Without Manual Synchronization"
- Visual: Thread pool with workers, async workflow
- Welcome back after break

**Slide 2: Part 1 Recap & Today's Focus**
- Quick review: Threads, synchronization, race conditions, deadlock
- Key insight: Manual synchronization is error-prone
- Shift: From low-level threading to high-level frameworks
- Today's tools: ExecutorService (thread pools), concurrent collections, concurrency utilities, CompletableFuture
- Promise: Write concurrent code without explicitly synchronizing
- Integration: Lambdas (Day 8) power these concurrency patterns

### Slides 3-8: ExecutorService & Thread Pools
**Slide 3: The Problem with Manual Thread Creation**
- Naive approach: Create new Thread() for each task
- Issues:
  - Thread creation is expensive (~1ms, memory overhead)
  - Unlimited threads cause resource exhaustion (OOM)
  - No reuse—threads created, run, discarded
  - No way to limit concurrent tasks
- Real-world: Web server needs to handle 10,000 requests without creating 10,000 threads
- Solution: Thread pool (reusable threads that wait for work)

**Slide 4: ExecutorService & Thread Pools**
- ExecutorService: Interface for managing thread pool
- Thread pool: Fixed number of reusable threads
- Pattern: Submit tasks (Runnable or Callable), pool manages execution
- Creation: Use Executors utility class factory methods:
  - `Executors.newFixedThreadPool(n)`: n threads, reused
  - `Executors.newCachedThreadPool()`: Create as needed, reuse if idle
  - `Executors.newSingleThreadExecutor()`: One thread (sequential)
  - `Executors.newScheduledThreadPool(n)`: For scheduled tasks
- Lifecycle: Submit → queued → executed by available thread → reused
- Shutdown: `executor.shutdown()` gracefully stops accepting tasks, waits for completion
- Example: ProcessingImages with pool of 4 workers

**Slide 5: Submitting Tasks & Futures**
- Method 1: `execute(Runnable)` - submit task, no result (fire and forget)
- Method 2: `submit(Callable)` - submit task, returns Future
- Future: Represents result of async computation, not yet available
- Operations on Future:
  - `get()`: Block until result available (can timeout)
  - `isDone()`: Check if computation complete
  - `cancel()`: Attempt to cancel execution
  - `get(timeout, unit)`: Get with timeout, throws TimeoutException if not done
- Callable<T>: Like Runnable but returns T and throws checked exceptions
- Example: Callable computing sum of array
- Code: Submitting task, retrieving future, getting result

**Slide 6: CompletionService & Handling Results**
- Issue: Multiple futures, which to wait for first?
- CompletionService: Takes futures, returns results as they complete
- Usage:
  ```java
  CompletionService<Result> service = new ExecutorCompletionService<>(executor);
  service.submit(task1);
  service.submit(task2);
  Future<Result> future = service.take();  // Blocks until one completes
  Result result = future.get();
  ```
- Advantage: Process results in completion order, not submission order
- Use case: Many async tasks, handle results as available (web scraping)

**Slide 7: ExecutorService Lifecycle & Graceful Shutdown**
- States: Running → Shutdown → Terminated
- `shutdown()`: No new tasks accepted, existing tasks complete normally
- `shutdownNow()`: Immediately stop, return list of pending tasks
- `awaitTermination(timeout, unit)`: Block until all tasks complete (with timeout)
- Common pattern:
  ```java
  executor.shutdown();
  if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
      executor.shutdownNow();  // Force stop if didn't finish in time
  }
  ```
- Resource leak: Forgetting to shutdown leaves thread pool running forever
- Best practice: Use try-with-resources (if available) or finally block

**Slide 8: Choosing the Right Pool Size**
- Fixed pool size: Depends on work type
- CPU-bound tasks: pool size = number of cores (use Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
- I/O-bound tasks: Can have more threads than cores (waiting doesn't waste CPU)
- Rule of thumb: I/O-bound ~2-3x cores, CPU-bound ~cores
- Tradeoffs: Larger pool = more memory, context switching overhead; smaller = underutilization
- Monitoring: ThreadPoolExecutor provides stats (active count, queue size)

### Slides 9-15: Concurrent Collections
**Slide 9: The Problem with Regular Collections**
- ArrayList, HashMap not thread-safe
- Multiple threads modifying simultaneously: corruption
- Solution 1: Collections.synchronizedList(list) - entire list synchronized (slow)
- Solution 2: Manual synchronization - cumbersome, error-prone
- Solution 3: Concurrent collections - designed for concurrency

**Slide 10: ConcurrentHashMap**
- Thread-safe alternative to HashMap
- Mechanism: Segment-based locking (not entire map locked)
- Multiple threads can modify different segments simultaneously
- Iterators: Weakly consistent (may see changes during iteration, but no fail-fast exception)
- Operations: put(), get(), remove(), containsKey() all thread-safe
- Performance: Better than synchronized HashMap for concurrent reads/writes
- Use case: Caching, shared configuration, multi-threaded lookups
- Example: Thread-safe cache with concurrent access

**Slide 11: CopyOnWriteArrayList**
- Thread-safe ArrayList
- Mechanism: Copy-on-write (modification creates copy, doesn't block readers)
- Readers: Never blocked, see consistent snapshot
- Writers: Incur copy overhead, exclusive access
- When to use: Many readers, few writers (and copy size manageable)
- When NOT to use: Many writers, high throughput (copy overhead prohibitive)
- Use case: Event listeners (read often, modify rarely)
- Iteration safe: Snapshot taken at iteration start, safe during modification

**Slide 12: BlockingQueue**
- Thread-safe queue for producer-consumer pattern
- Operations:
  - `put(item)`: Add item, block if full
  - `take()`: Remove and return, block if empty
  - `offer(item, timeout)`: Add with timeout
  - `poll(timeout)`: Remove with timeout
- Coordination: Thread coordination without explicit wait/notify
- Implementations: LinkedBlockingQueue, ArrayBlockingQueue, PriorityBlockingQueue
- Use case: Producer adds items, consumer processes; queue handles blocking
- Example: Work queue processing system

**Slide 13: Real-World Producer-Consumer Example**
- Scenario: Download URLs (producers), process HTML (consumers)
- Traditional: Manual wait/notify (complex)
- With BlockingQueue:
  ```java
  BlockingQueue<String> urls = new LinkedBlockingQueue<>(100);
  
  // Producer thread
  new Thread(() -> {
      for (String url : urlList) {
          urls.put(url);  // Block if queue full
      }
  }).start();
  
  // Consumer thread
  new Thread(() -> {
      while (true) {
          String url = urls.take();  // Block if queue empty
          processUrl(url);
      }
  }).start();
  ```
- Simplicity: No synchronized blocks, no explicit coordination
- Efficiency: Queue handles blocking elegantly

**Slide 14: ConcurrentLinkedQueue & Other Collections**
- ConcurrentLinkedQueue: Non-blocking, lock-free queue (efficient for high-throughput)
- ConcurrentSkipListMap: Thread-safe sorted map (like TreeMap)
- ConcurrentSkipListSet: Thread-safe sorted set
- Collections.synchronizedSet(): Synchronized Set wrapper (all operations blocked)
- When to use synchronized wrappers: Rarely (concurrent collections usually better)
- Atomic classes: AtomicInteger, AtomicLong, AtomicBoolean (atomic operations without locking)
- Example: AtomicInteger counter (compare-and-swap for efficiency)

**Slide 15: Atomic Variables**
- AtomicInteger, AtomicLong: Atomic operations without synchronization
- Operations: incrementAndGet(), decrementAndGet(), getAndSet()
- Mechanism: Compare-and-swap (CAS) instruction, lock-free
- Performance: Better than synchronized for single variable
- Use case: Counters, flags that multiple threads update
- Example:
  ```java
  AtomicInteger count = new AtomicInteger(0);
  count.incrementAndGet();  // Thread-safe
  ```
- Limitation: Only works for single variable, not multiple

### Slides 16-22: Concurrency Utilities
**Slide 16: CountDownLatch**
- Synchronization barrier: One thread waits for N others
- Mechanism: Internal counter, decrement on latch.countDown(), latch.await() blocks until zero
- One-time use: Cannot be reset
- Use case: Waiting for multiple operations to complete
- Example: Waiting for all worker threads to finish
  ```java
  CountDownLatch latch = new CountDownLatch(3);
  // 3 worker threads
  for (int i = 0; i < 3; i++) {
      new Thread(() -> {
          doWork();
          latch.countDown();  // Signal done
      }).start();
  }
  latch.await();  // Main thread waits for all 3
  System.out.println("All workers done");
  ```

**Slide 17: Semaphore**
- Resource pool with limited access
- Mechanism: Internal permit count, acquire decreases, release increases
- Use case: Limiting concurrent access (only 5 threads at a time)
- Example: Database connection pool (limited connections)
  ```java
  Semaphore semaphore = new Semaphore(5);  // 5 permits
  for (int i = 0; i < 20; i++) {
      new Thread(() -> {
          semaphore.acquire();  // Wait if all permits taken
          try {
              useConnection();
          } finally {
              semaphore.release();
          }
      }).start();
  }
  ```
- Fair vs unfair: Ordered vs random permit allocation

**Slide 18: CyclicBarrier**
- Synchronization point for multiple threads
- Mechanism: N threads arrive at barrier, all block until N threads reach barrier
- Resettable: Can be reused for multiple rounds
- Use case: Iterative parallel computation (all workers sync at round end)
- Example: Simulation with rounds
  ```java
  CyclicBarrier barrier = new CyclicBarrier(3);
  // 3 threads, each does work then waits at barrier
  for (int i = 0; i < 3; i++) {
      new Thread(() -> {
          doRoundWork();
          barrier.await();  // All wait here
      }).start();
  }
  ```

**Slide 19: Phaser (Advanced Alternative)**
- Advanced version of CyclicBarrier, more flexible
- Supports dynamic number of parties
- Multiple phases with different participant counts
- Use case: Complex multi-phase parallel computation
- Note: CyclicBarrier simpler for most use cases, Phaser for advanced

**Slide 20: Locks and Conditions (java.util.concurrent.locks)**
- Lock interface: More flexible than synchronized (not limited to method/block scope)
- ReentrantLock: Fair or unfair locking, can acquire multiple times (reentrant)
- Condition: Condition variables for wait/notify patterns (more flexible than Object.wait/notify)
- Usage:
  ```java
  Lock lock = new ReentrantLock();
  Condition condition = lock.newCondition();
  
  lock.lock();
  try {
      while (!condition_met) {
          condition.await();  // Wait for signal
      }
      // do work
      condition.signalAll();  // Wake all waiters
  } finally {
      lock.unlock();
  }
  ```
- When to use: Complex coordination (better than synchronized + Object.wait/notify)

**Slide 21: Common Beginner Mistakes #1-2**

**Mistake 1: Forgetting to Shut Down ExecutorService**
- Wrong:
  ```java
  ExecutorService executor = Executors.newFixedThreadPool(5);
  executor.submit(task);
  // Program exits, but thread pool threads still running!
  ```
- Right:
  ```java
  ExecutorService executor = Executors.newFixedThreadPool(5);
  try {
      executor.submit(task);
  } finally {
      executor.shutdown();
  }
  ```
- Consequence: Thread leak, resource exhaustion over time
- Best practice: Always shutdown in finally or try-with-resources

**Mistake 2: Ignoring Future Results & Exceptions**
- Wrong:
  ```java
  Future<Integer> future = executor.submit(() -> calculateSum());
  // Never call get()
  ```
- Issue: Exceptions are silent (never checked)
- Right:
  ```java
  Future<Integer> future = executor.submit(() -> calculateSum());
  try {
      Integer result = future.get();
      // use result
  } catch (ExecutionException e) {
      // Task threw exception
      e.getCause().printStackTrace();
  }
  ```
- Consequence: Silent failures, no visibility into problems

**Slide 22: Real-World Example: Parallel Data Processing**
- Scenario: Process 1 million records in parallel, 4-core machine
- Naive: One thread per record (resource exhaustion)
- Correct: Thread pool of 4 workers, queue tasks
- Code:
  ```java
  ExecutorService executor = Executors.newFixedThreadPool(4);
  List<Future<Result>> futures = new ArrayList<>();
  for (Record record : records) {
      Future<Result> future = executor.submit(() -> processRecord(record));
      futures.add(future);
  }
  List<Result> results = new ArrayList<>();
  for (Future<Result> future : futures) {
      results.add(future.get());
  }
  executor.shutdown();
  ```
- Performance: Scales with cores, efficient resource use

### Slides 23-30: CompletableFuture & Async Workflows
**Slide 23: CompletableFuture Basics**
- Future: Get result later (blocking)
- CompletableFuture: Can complete with result later, or programmatically (more control)
- Creation methods:
  - `CompletableFuture.supplyAsync(Supplier)`: Run async, return result
  - `CompletableFuture.runAsync(Runnable)`: Run async, no result
  - `CompletableFuture.completedFuture(value)`: Already-completed future
- Completion: Either get result via `get()` or register callback via `thenAccept()`
- Example:
  ```java
  CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> calculateSum());
  future.thenAccept(result -> System.out.println("Result: " + result));
  ```

**Slide 24: Chaining Operations with thenApply & thenAccept**
- `thenApply(Function)`: Transform result
- `thenAccept(Consumer)`: Consume result
- `thenRun(Runnable)`: Run after completion, ignoring result
- Chaining: Callbacks compose
- Example:
  ```java
  CompletableFuture.supplyAsync(() -> fetchData())
      .thenApply(data -> processData(data))
      .thenAccept(result -> printResult(result));
  ```
- Non-blocking: Original thread doesn't wait, callbacks run when ready
- Executor control: Can specify executor for each stage

**Slide 25: Combining Multiple Futures**
- `thenCombine()`: Combine results of two futures
- `thenAcceptBoth()`: Accept both results
- `allOf()`: Wait for all futures to complete
- `anyOf()`: Wait for any future to complete
- Example (allOf):
  ```java
  CompletableFuture<String> f1 = fetchUserData(id);
  CompletableFuture<String> f2 = fetchOrderData(id);
  CompletableFuture<Void> combined = CompletableFuture.allOf(f1, f2);
  combined.thenRun(() -> System.out.println("Both fetched"));
  ```
- Example (thenCombine):
  ```java
  f1.thenCombine(f2, (userData, orderData) -> 
      userData + " | " + orderData
  ).thenAccept(System.out::println);
  ```

**Slide 26: Exception Handling in CompletableFuture**
- `exceptionally(Function)`: Handle exception, provide fallback
- `handle(BiFunction)`: Handle both result and exception
- `whenComplete(BiConsumer)`: Cleanup action, doesn't transform
- Example (exceptionally):
  ```java
  CompletableFuture.supplyAsync(() -> riskyOperation())
      .exceptionally(ex -> {
          System.err.println("Error: " + ex.getMessage());
          return defaultValue;
      })
      .thenAccept(System.out::println);
  ```
- Example (handle):
  ```java
  CompletableFuture.supplyAsync(() -> operation())
      .handle((result, ex) -> {
          if (ex != null) return errorResult;
          return result;
      })
      .thenAccept(System.out::println);
  ```

**Slide 27: Real-World Async Example: REST API Client**
- Scenario: Fetch data from multiple APIs, combine results
- Traditional: Sequential fetches (slow)
- With CompletableFuture: Parallel, non-blocking
- Code:
  ```java
  CompletableFuture<User> userFuture = 
      CompletableFuture.supplyAsync(() -> fetchUser(id));
  CompletableFuture<List<Order>> ordersFuture = 
      CompletableFuture.supplyAsync(() -> fetchOrders(id));
  
  userFuture.thenCombine(ordersFuture, (user, orders) -> 
      new UserWithOrders(user, orders)
  )
  .thenAccept(result -> renderUI(result))
  .exceptionally(ex -> {
      showError(ex);
      return null;
  });
  ```
- Benefit: Non-blocking, composable, clean async code

**Slide 28: CompletableFuture vs Traditional Callbacks**
- Traditional callback hell:
  ```java
  fetchUser(id, user -> {
      fetchOrders(user.id, orders -> {
          renderUI(user, orders);  // Callback nesting
      });
  });
  ```
- CompletableFuture:
  ```java
  CompletableFuture.supplyAsync(() -> fetchUser(id))
      .thenCompose(user -> fetchOrders(user.id))
      .thenAccept(orders -> renderUI(orders));  // Linear, readable
  ```
- Readability: Linear flow vs nested callbacks
- Error handling: More straightforward with exceptionally/handle

**Slide 29: Advanced Patterns: Custom Executors**
- Default: Uses ForkJoinPool (work-stealing)
- Custom executor:
  ```java
  ExecutorService customPool = Executors.newFixedThreadPool(10);
  CompletableFuture.supplyAsync(
      () -> expensiveComputation(),
      customPool  // Custom executor
  )
  .thenAccept(System.out::println);
  ```
- Why: Control thread pool size, naming, shutdown
- Performance: Tuning executor for workload

**Slide 30: Summary & Forward Preview**
- ExecutorService: Thread pool, reusable threads, simple async
- Concurrent collections: Thread-safe data structures without manual sync
- Concurrency utilities: CountDownLatch, Semaphore, CyclicBarrier for coordination
- CompletableFuture: Modern async workflows, composable, readable
- Pattern: Higher-level abstractions reduce manual synchronization errors
- Day 10 (Friday): Advanced Java will cover memory model, GC, design patterns (not threading)
- Week 3+: Frontend (JS promises similar to CompletableFuture), Spring (uses ExecutorService internally)
- Production: These tools enable scalable, responsive applications

### Slides 31-35: Awareness & Integration
**Slide 31: ForkJoinPool & Parallel Streams**
- ForkJoinPool: Divide-and-conquer parallelism (work-stealing threads)
- Used internally by parallel streams (Day 8 recall)
- Recursively splits task, steals work from idle threads
- Use case: Recursive parallel algorithms
- Awareness: parallel streams use ForkJoinPool; now understand what's underneath
- When to use: Complex divide-and-conquer (rare for business logic)

**Slide 32: Virtual Threads (Project Loom, Java 19+)**
- Traditional threads: ~1 thread per core, context switching overhead
- Virtual threads: Millions of lightweight threads, mapped to platform threads
- Syntax:
  ```java
  Thread.ofVirtual().start(runnable);  // or ExecutorService.newVirtualThreadPerTaskExecutor()
  ```
- Benefit: Can handle massive concurrency (1M threads) without resource exhaustion
- Awareness: Emerging feature, not yet standard for business apps
- Future: Likely standard for I/O-heavy applications

**Slide 33: Thread Safety Best Practices**
- Practice 1: Prefer immutable objects (thread-safe by definition)
- Practice 2: Use executor services (don't create threads manually)
- Practice 3: Prefer concurrent collections (don't synchronize manually)
- Practice 4: Use CompletableFuture for async workflows
- Practice 5: Avoid shared mutable state (pass data through queues)
- Practice 6: Use synchronization only when necessary (document why)
- Practice 7: Test with multiple threads (use tools like ThreadStresser)

**Slide 34: Real-World Integration: Web Server Pattern**
- Scenario: HTTP server accepting requests
- Traditional: New thread per request (unlimited, resource exhaustion)
- Modern: Thread pool handling requests
- Pattern:
  ```java
  ExecutorService executor = Executors.newFixedThreadPool(50);
  ServerSocket server = new ServerSocket(8080);
  while (true) {
      Socket client = server.accept();
      executor.submit(() -> handleClient(client));
  }
  ```
- Scale: Handles thousands of clients with 50 threads
- Complexity hidden: Synchronized collections, queues, all internal

**Slide 35: Synthesis & Week 2 Completion**
- Week 2 progression: Day 7 (Exception handling & I/O) → Day 8 (Lambdas & Streams) → Day 9 (Multithreading)
- Lambdas enable concise runnable/callable tasks
- Streams use parallel streams (ForkJoinPool underneath)
- Multithreading makes streams parallel execution possible
- Concurrency utilities and CompletableFuture make async code readable
- Production applications: Thread pools, async workflows, concurrent collections standard
- Next: Week 3 (Frontend) shifts to JavaScript/TypeScript (similar async patterns: Promises)
- Capstone: Application combining backend multithreading with frontend async

