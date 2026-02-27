SLIDE 1 — Title Slide
Slide Content:

Title: Java Multithreading: From Basics to Modern Concurrency
Subtitle: Threads, Synchronization, Executors, CompletableFuture & More
Your name / date / course name

Script (1 min):

"Welcome everyone. Today we're doing a deep dive into one of the most powerful and tricky parts of Java — multithreading and concurrency. By the end of this class, you'll understand how to create threads, keep data safe when multiple threads share it, avoid nasty bugs like deadlocks, and use modern tools like CompletableFuture and thread pools. This is a big topic, so let's move with intention."


SLIDE 2 — Agenda
Slide Content:

Thread Basics & the Thread Class
The Runnable Interface
Thread Lifecycle & States
Synchronization & Thread Safety
Deadlock & Livelock
Producer-Consumer Problem
Concurrent Collections
ExecutorService & Thread Pools
CompletableFuture
Core Concurrency Utilities
ScheduledExecutorService
Awareness: ForkJoinPool & Virtual Threads


SECTION 1 — Thread Basics & the Thread Class
Time: ~5 minutes
SLIDE 3 — What is a Thread?
Slide Content:

A thread is the smallest unit of execution within a process
Java programs start with one thread: the main thread
Multithreading = multiple threads running concurrently within one program
Benefits: responsiveness, performance on multi-core CPUs
Diagram: One process box → multiple thread arrows inside it

Script:

"Think of your program as a restaurant. The process is the restaurant itself. Threads are the individual waiters. One waiter — things move slow. Multiple waiters — more gets done at the same time. Java gives us tools to create and manage those waiters. The JVM starts with one thread — main — and from there you can spin up more."


SLIDE 4 — Creating Threads: The Thread Class
Slide Content:
javaclass MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}

// Usage:
MyThread t = new MyThread();
t.start(); // NOT t.run()!

start() creates a new thread and calls run() on it
Calling run() directly just runs it on the current thread — a common mistake!

Script:

"To create a thread by extending Thread, you override the run() method — that's the code the thread will execute. You then call start(), NOT run(). This is a mistake beginners make constantly. start() tells the JVM to create a new thread and execute run() on it. Calling run() directly just runs it on the same thread — no concurrency at all."


SECTION 2 — Runnable Interface
Time: ~4 minutes
SLIDE 5 — Runnable vs Thread
Slide Content:
javaclass MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Task running in: " + Thread.currentThread().getName());
    }
}

// Usage:
Thread t = new Thread(new MyTask());
t.start();

// Lambda version (preferred):
Thread t2 = new Thread(() -> System.out.println("Lambda thread!"));
t2.start();

Prefer Runnable — Java only allows single inheritance; using Runnable keeps your class flexible
Runnable separates the task from the thread mechanism

Script:

"Rather than extending Thread, it's usually better to implement Runnable. Why? Because Java doesn't allow multiple inheritance. If you extend Thread, you can't extend anything else. Runnable is just a functional interface with one method — run(). This means you can also write it as a lambda, which is much cleaner. The task is now separate from the thread — better design."


SECTION 3 — Thread Lifecycle & States
Time: ~5 minutes
SLIDE 6 — Thread States Diagram
Slide Content:

Diagram (draw as a state machine):

NEW → RUNNABLE → RUNNING → TERMINATED
From RUNNING: can go to BLOCKED, WAITING, TIMED_WAITING → back to RUNNABLE


States defined in Thread.State enum

Script:

"Every thread goes through a lifecycle. It starts in NEW state — the object exists but start() hasn't been called. Once you call start(), it becomes RUNNABLE — eligible to run. The JVM scheduler picks it up and it enters the RUNNING state. From there it can finish and go to TERMINATED, or it can pause. BLOCKED means it's waiting to acquire a lock. WAITING means it's waiting indefinitely for something — like another thread to notify it. TIMED_WAITING is the same but with a timeout — like Thread.sleep(1000)."


SLIDE 7 — Key Thread Methods
Slide Content:
MethodDescriptionstart()Begins execution of the threadrun()The task to execute (don't call directly)sleep(ms)Pauses current thread for ms millisecondsjoin()Waits for this thread to finishinterrupt()Signals thread to stop what it's doingisAlive()Returns true if thread hasn't terminatedgetName() / setName()Thread identification
Script:

"join() is particularly important — it lets one thread wait for another to finish. Say you spawn a thread to do a computation, and you need the result before continuing — you call join() on it. interrupt() doesn't kill a thread — it sets a flag. The thread needs to check isInterrupted() or handle InterruptedException to respond to it."


SECTION 4 — Synchronization & Thread Safety
Time: ~7 minutes
SLIDE 8 — The Race Condition Problem
Slide Content:
javaclass Counter {
    int count = 0;
    void increment() { count++; } // NOT thread-safe!
}

count++ is actually 3 operations: read → modify → write
Two threads can interleave these steps → wrong result
This is a race condition

Script:

"Here's where things get dangerous. You'd think count++ is one operation. It's not. It's read the value, add one, write it back. If two threads do this simultaneously, they might both read the same value, both add 1, and write back the same result — so you get one increment instead of two. Run this a million times and you get a wrong answer. This is called a race condition."


SLIDE 9 — synchronized Keyword
Slide Content:
javaclass Counter {
    int count = 0;

    synchronized void increment() {
        count++; // Now thread-safe
    }
    
    // Or use a synchronized block:
    void decrement() {
        synchronized(this) {
            count--;
        }
    }
}

synchronized on a method uses the object's intrinsic lock
Only one thread can hold the lock at a time
Synchronized blocks give finer control

Script:

"The synchronized keyword is Java's built-in locking mechanism. When a method is synchronized, only one thread can execute it at a time on a given object. Other threads block and wait. Synchronized blocks let you lock only the critical section — the smallest amount of code that actually needs protection — which improves performance."


SLIDE 10 — volatile Keyword & Atomic Variables
Slide Content:

volatile ensures a variable is always read from/written to main memory (not CPU cache)
Solves visibility problem, NOT atomicity

javavolatile boolean running = true;

For atomic operations, use java.util.concurrent.atomic:

javaAtomicInteger count = new AtomicInteger(0);
count.incrementAndGet(); // Thread-safe, no lock needed
Script:

"There's another subtle issue — visibility. Threads may cache variables locally. One thread changes a value, but another thread reads a stale cached copy. volatile fixes this — it forces reads/writes to go to main memory. BUT it doesn't fix atomicity. count++ is still not safe with just volatile. For atomic integer operations, Java gives us AtomicInteger, AtomicLong, and friends — these use low-level CPU instructions that are both visible AND atomic, without the overhead of synchronization."


SECTION 5 — Deadlock & Livelock
Time: ~5 minutes
SLIDE 11 — Deadlock
Slide Content:

Deadlock: Two or more threads waiting for each other's locks forever
Four conditions (Coffman): Mutual Exclusion, Hold & Wait, No Preemption, Circular Wait

java// Thread 1: locks A then tries B
// Thread 2: locks B then tries A
// → Both wait forever

Prevention: Always acquire locks in the same order

Script:

"Deadlock is when two threads are stuck waiting on each other and neither can proceed. Thread 1 holds lock A, wants lock B. Thread 2 holds lock B, wants lock A. Both wait forever. Your program freezes — no exceptions, no crashes, just silence. The classic fix is lock ordering: always acquire multiple locks in the same global order throughout your code. If everyone follows the same order, circular waiting can't happen."


SLIDE 12 — Livelock & Starvation
Slide Content:

Livelock: Threads are active but keep responding to each other and make no progress

Like two people in a hallway both stepping the same direction repeatedly


Starvation: A thread never gets CPU time because others keep taking priority
Prevention: Use fair locks, timeouts, backoff strategies

Script:

"Livelock is sneakier than deadlock. The threads aren't blocked — they're actively running. But they keep reacting to each other in a way that prevents any real progress. Imagine two threads that detect a conflict and both back off, then both retry, then both back off again — forever. Starvation is when one thread perpetually loses the scheduler lottery to higher-priority threads. Java's ReentrantLock supports a fairness flag to help with this."


SECTION 6 — Producer-Consumer Problem
Time: ~5 minutes
SLIDE 13 — The Pattern
Slide Content:

Classic concurrency pattern: producers generate data, consumers process it
Shared buffer/queue between them
Challenges: don't produce when buffer is full, don't consume when empty

javaBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);

// Producer
queue.put(item);    // blocks if full

// Consumer  
int item = queue.take(); // blocks if empty

BlockingQueue handles the wait/notify logic for you!

Script:

"The producer-consumer problem comes up everywhere — web servers processing requests, logging pipelines, message queues. You have threads producing data and other threads consuming it, sharing a buffer in the middle. The old way was to use wait() and notify() with synchronized blocks — tricky to get right. The modern way is BlockingQueue. It handles all the blocking logic for you. put() waits if the queue is full. take() waits if it's empty. Use this."


SECTION 7 — Concurrent Collections
Time: ~4 minutes
SLIDE 14 — Thread-Safe Collections
Slide Content:
ClassDescriptionConcurrentHashMapThread-safe map, high-performance (segment locking)CopyOnWriteArrayListSafe for reads; writes copy entire listLinkedBlockingQueueBlocking queue for producer-consumerConcurrentLinkedQueueNon-blocking queueCollections.synchronizedList()Wraps any list (less efficient)

Never use: HashMap + manual sync, Vector, Hashtable (legacy)

Script:

"Standard collections like ArrayList and HashMap are NOT thread-safe. Never share them between threads without synchronization. Java gives you java.util.concurrent collections that are built for concurrency. ConcurrentHashMap is your go-to for shared maps — it's far more performant than wrapping a HashMap in synchronized because it uses segment-level locking, not a single global lock. CopyOnWriteArrayList is great when you have lots of reads and rare writes — reads are lock-free."


SECTION 8 — ExecutorService & Thread Pools
Time: ~6 minutes
SLIDE 15 — Why Thread Pools?
Slide Content:

Creating a thread is expensive (memory, OS resources)
Thread pools maintain a set of reusable worker threads
Benefits: controlled resource usage, task queuing, reuse

javaExecutorService executor = Executors.newFixedThreadPool(4);

executor.submit(() -> {
    System.out.println("Task running!");
});

executor.shutdown(); // Always shut down!
Script:

"Creating a new thread for every task is like hiring a new employee for every single task and firing them when done. It's wasteful. Thread pools keep a fixed set of threads alive and ready to pick up tasks from a queue. ExecutorService is the interface; Executors has factory methods. newFixedThreadPool(4) gives you exactly 4 worker threads. Always call shutdown() when done — otherwise your JVM won't exit."


SLIDE 16 — ExecutorService Types & Future
Slide Content:
java// Common pool types:
Executors.newFixedThreadPool(n)      // Fixed number of threads
Executors.newCachedThreadPool()      // Grows/shrinks as needed
Executors.newSingleThreadExecutor()  // One thread, ordered execution

// Getting results back:
Future<Integer> future = executor.submit(() -> compute());
Integer result = future.get(); // blocks until done

Future.get() blocks — use carefully
future.cancel(), future.isDone() are available

Script:

"When you submit a Callable (a task that returns a value), you get back a Future. Think of it as a receipt for a result that isn't ready yet. You can call future.get() to wait for and retrieve the result — but it blocks the calling thread. Future is OK, but as you'll see in a moment, CompletableFuture is much more powerful."


SECTION 9 — CompletableFuture
Time: ~8 minutes
SLIDE 17 — CompletableFuture Basics
Slide Content:
javaCompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
    return fetchDataFromDB(); // runs in ForkJoinPool by default
});

cf.thenApply(data -> process(data))
  .thenAccept(result -> System.out.println(result))
  .exceptionally(ex -> { System.out.println("Error: " + ex); return null; });

Non-blocking, composable async pipelines
Runs in ForkJoinPool.commonPool() by default

Script:

"CompletableFuture is Java's answer to modern async programming. Unlike Future, it's non-blocking and composable. supplyAsync runs a task asynchronously. thenApply transforms the result when it's ready — like map in streams. thenAccept consumes the result. exceptionally handles errors in the chain. The beauty is you chain these together into a readable pipeline without blocking any threads."


SLIDE 18 — CompletableFuture Advanced
Slide Content:
java// Run two tasks in parallel, combine results:
CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> " World");

CompletableFuture<String> combined = cf1.thenCombine(cf2, (a, b) -> a + b);

// Wait for all to complete:
CompletableFuture.allOf(cf1, cf2).join();

// Wait for first to complete:
CompletableFuture.anyOf(cf1, cf2).join();

// Custom thread pool:
ExecutorService pool = Executors.newFixedThreadPool(4);
CompletableFuture.supplyAsync(() -> work(), pool);

thenCompose() — flat-maps (chain dependent futures)
handle() — handles both result and exception in one step

Script:

"The real power comes from combining futures. thenCombine lets you run two independent tasks in parallel and merge their results. allOf waits for all futures to complete — useful for fan-out/fan-in patterns. anyOf takes the first one to finish. By default CompletableFuture uses the common ForkJoinPool, but you can supply your own executor as the second argument to supplyAsync for better control. thenCompose is like flatMap — use it when your callback itself returns a CompletableFuture."


SECTION 10 — Core Concurrency Utilities
Time: ~6 minutes
SLIDE 19 — CountDownLatch
Slide Content:
javaCountDownLatch latch = new CountDownLatch(3); // Count of 3

// In 3 worker threads:
latch.countDown(); // each calls this when done

// Main thread waits:
latch.await(); // blocks until count reaches 0
System.out.println("All workers done!");

One-time use: count goes down, never resets
Use case: Wait for N tasks to complete before proceeding

Script:

"CountDownLatch is like a starting gate. You initialize it with a count. Worker threads call countDown() when they finish. The waiting thread calls await() and blocks until the count hits zero. Once it hits zero, it can't be reset. Use case: wait for 5 microservice calls to complete before rendering a page."


SLIDE 20 — CyclicBarrier & Semaphore
Slide Content:
CyclicBarrier — All threads wait until everyone reaches the barrier, then all proceed together. Can be reused!
javaCyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("All ready!"));
barrier.await(); // each thread calls this
Semaphore — Controls access to a limited number of resources
javaSemaphore semaphore = new Semaphore(3); // 3 permits
semaphore.acquire(); // get a permit (blocks if none)
try { useResource(); }
finally { semaphore.release(); } // always release!
Script:

"CyclicBarrier is like CountDownLatch but reusable — all threads wait at the barrier and then all proceed together. Great for phased computation. Semaphore is like a bouncer at a club with limited capacity. You have N permits. A thread must acquire one to proceed. When done, it releases it for others. Use it to throttle access to a database connection pool, rate-limit API calls, etc."


SLIDE 21 — ReentrantLock & ReadWriteLock
Slide Content:
javaReentrantLock lock = new ReentrantLock(true); // fair=true
lock.lock();
try { criticalSection(); }
finally { lock.unlock(); } // ALWAYS in finally!

// ReadWriteLock: multiple readers OR one writer
ReadWriteLock rwLock = new ReentrantReadWriteLock();
rwLock.readLock().lock();   // Multiple threads can hold this
rwLock.writeLock().lock();  // Exclusive

ReentrantLock > synchronized when you need: fairness, try-lock, timed lock, interruptible lock

Script:

"ReentrantLock gives you everything synchronized does, plus more. The tryLock() method lets you attempt to acquire a lock and bail out if you can't — useful for avoiding deadlocks. Always release in a finally block. ReadWriteLock is great for read-heavy scenarios — many threads can read simultaneously, but writing is exclusive. This can dramatically improve performance when reads dominate."


SECTION 11 — ScheduledExecutorService
Time: ~3 minutes
SLIDE 22 — Scheduling Tasks
Slide Content:
javaScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

// Run once after delay:
scheduler.schedule(() -> System.out.println("Delayed!"), 5, TimeUnit.SECONDS);

// Run repeatedly with fixed delay between end of one and start of next:
scheduler.scheduleWithFixedDelay(task, 0, 10, TimeUnit.SECONDS);

// Run at fixed rate (regardless of task duration):
scheduler.scheduleAtFixedRate(task, 0, 10, TimeUnit.SECONDS);

scheduler.shutdown();

Replaces Timer and TimerTask (legacy)

Script:

"ScheduledExecutorService is your built-in job scheduler. Use it for health checks, cleanup tasks, polling, anything that runs on a schedule. scheduleAtFixedRate fires every N seconds regardless of how long the task takes. scheduleWithFixedDelay waits N seconds after the task completes. If a task takes longer than the rate period in scheduleAtFixedRate, the next execution is delayed rather than overlapping — good to know."


SECTION 12 — Awareness: ForkJoinPool & Virtual Threads
Time: ~4 minutes
SLIDE 23 — ForkJoinPool
Slide Content:

Designed for divide-and-conquer tasks (recursive parallelism)
Uses work-stealing: idle threads steal tasks from busy threads
Powers Stream.parallel() and CompletableFuture by default

javaForkJoinPool pool = ForkJoinPool.commonPool();
// Or custom:
ForkJoinPool custom = new ForkJoinPool(4);

Use RecursiveTask<T> (returns result) or RecursiveAction (no result)

Script:

"ForkJoinPool is the engine under the hood of parallel streams and CompletableFuture. It uses a clever work-stealing algorithm — when a thread finishes its own tasks, it steals tasks from the back of other threads' queues. It's optimized for tasks that split themselves into sub-tasks recursively. You don't often use it directly, but understanding it explains why parallel streams work well on independent data and poorly on shared mutable state."


SLIDE 24 — Virtual Threads (Project Loom — Java 21+)
Slide Content:

Platform threads = 1:1 with OS threads → expensive, limited to ~thousands
Virtual threads = many-to-few mapping onto carrier threads → millions possible

java// Java 21+
Thread vt = Thread.ofVirtual().start(() -> {
    System.out.println("I'm a virtual thread!");
});

// With ExecutorService:
ExecutorService vtPool = Executors.newVirtualThreadPerTaskExecutor();

Virtual threads block cheaply — blocking I/O doesn't block the carrier thread
Best for: high-concurrency I/O-bound workloads (web servers, DB calls)
NOT a silver bullet for CPU-bound tasks

Script:

"Project Loom, delivered in Java 21, is a paradigm shift. Traditional platform threads are expensive OS threads — you realistically run hundreds or low thousands. Virtual threads are lightweight threads managed by the JVM. You can run millions of them. When a virtual thread blocks on I/O, it gets unmounted from its carrier thread — so the carrier is free to run other virtual threads. This makes the old 'one thread per request' model viable at massive scale again. You write simple blocking code but get async scalability. This is the future of Java concurrency for I/O-bound apps."


SLIDE 25 — Summary & Key Takeaways
Slide Content:
TopicKey PointThread / RunnableUse Runnable/lambda; call start() not run()Thread LifecycleNEW → RUNNABLE → RUNNING → BLOCKED/WAITING → TERMINATEDSynchronizationsynchronized, volatile, AtomicIntegerDeadlockLock ordering; use tryLock()Producer-ConsumerUse BlockingQueueConcurrent CollectionsConcurrentHashMap, CopyOnWriteArrayListThread PoolsExecutorService; always shutdown()CompletableFutureComposable async; thenApply, thenCombine, exceptionallyUtilitiesCountDownLatch, Semaphore, ReentrantLock, ReadWriteLockSchedulingScheduledExecutorServiceAwarenessForkJoinPool powers parallel streams; Virtual Threads = Java 21+
Script (2 min):

"Let's bring it all together. You now know how to create threads, how they live and die, and how to keep shared data safe. You know the scary bugs — deadlock and livelock — and how to avoid them. You've seen the producer-consumer pattern solved elegantly with BlockingQueue. Thread pools give you efficiency, CompletableFuture gives you composability, and utilities like CountDownLatch and Semaphore give you coordination superpowers. In future lessons we'll go even deeper. For now — experiment, break things, and debug. That's how concurrency is learned."


SLIDE 26 — Q&A / Practice Exercises
Slide Content:
Try these:

Create two threads that increment a shared counter 10,000 times each — observe the race condition, then fix it
Implement a producer-consumer system using LinkedBlockingQueue
Fetch data from 3 "services" in parallel using CompletableFuture.allOf, combine results
Create a rate limiter using Semaphore that allows max 3 concurrent operations
Simulate a deadlock, then fix it using lock ordering

---

