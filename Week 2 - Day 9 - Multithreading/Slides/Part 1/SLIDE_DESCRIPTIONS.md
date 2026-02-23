# Week 2 - Day 9, Part 1: Thread Basics, Synchronization & Deadlock
## Slide Descriptions (60-minute lecture)

### Slides 1-2: Welcome & Problem Statement
**Slide 1: Title Slide**
- Title: "Multithreading in Java"
- Subtitle: "Concurrent Execution, Synchronization & Thread Safety"
- Visual: Multiple threads flowing in parallel, synchronized symbols
- Welcome message for Day 9

**Slide 2: Problem Statement**
- Modern processors have multiple cores (4, 8, 16+ cores common)
- Single-threaded programs use only one core (75% CPU wasted)
- Real-world scenarios require concurrent operations:
  - Web server handling 1000 simultaneous requests
  - User interface remaining responsive while background work happens
  - Downloading multiple files at once
  - Monitoring user input while processing data
- Question: "How can we write programs that do multiple things at once?"
- Teaser: Threads allow concurrent execution on multi-core systems

### Slides 3-8: Fundamentals of Threads
**Slide 3: What is a Thread?**
- Definition: Lightweight process, unit of execution within a program
- Threads exist within a process, share process memory/resources
- Each thread has its own stack, instruction pointer, registers
- Multiple threads in one process run concurrently (or in parallel on multi-core)
- Contrast: Process is heavy (separate memory, isolated), Thread is light (shared memory)
- Visual: Process with multiple threads, each with own stack
- Key property: Threads are independent execution paths that can run simultaneously
- Real-world: Browser process with multiple threads for UI, networking, rendering

**Slide 4: Thread vs Process**
- Process: Heavy, isolated memory, separate from other processes
- Thread: Light, shared memory with other threads in same process
- Creating thread: Relatively fast (~1ms), many can coexist
- Creating process: Slow (~10-100ms), limited by system resources
- Memory: Process 1MB+ overhead each; Thread ~64KB each
- Context switching: Thread switching faster than process switching
- Communication: Threads trivial (shared memory), Processes requires IPC (complex)
- Use threads for concurrent work within same program

**Slide 5: The Java Thread Class**
- `java.lang.Thread` class represents a thread
- Creating thread: Extend Thread or implement Runnable (prefer latter)
- Methods: start(), run(), join(), sleep(), interrupt()
- Properties: Name, priority (1-10, 5 is normal), isDaemon flag
- Thread states: New, Runnable, Running, Blocked, Terminated
- Important: Call `start()` not `run()` (start() creates new thread, run() executes in current thread)
- Example: Creating Thread by extending Thread class
- Code example: `thread.start();` starts concurrent execution

**Slide 6: The Runnable Interface (Preferred Approach)**
- `Runnable` interface: Single method `run()` that thread executes
- Why better than extending Thread: Java single inheritance, can implement multiple interfaces
- Functional interface: `run()` is the only abstract method (can use lambdas in Java 8+)
- Creating runnable: `new Thread(new MyRunnable())` or lambda `new Thread(() -> {...})`
- Lambda syntax: `new Thread(() -> System.out.println("Running"))`
- Advantage: Cleaner, more flexible, doesn't consume inheritance hierarchy
- Pattern: Prefer Runnable (or callable/Future) over extending Thread
- Real-world: Almost all Java code uses Runnable, not Thread subclass

**Slide 7: Thread Lifecycle & States**
- **New**: Thread object created, not yet started (`new Thread(...)`)
- **Runnable**: After `start()` called, thread ready to run (may or may not be executing)
- **Running**: Thread actively executing (only one thread per core can truly run at once)
- **Blocked/Waiting**: Thread waiting (I/O, lock, sleep, wait())
- **Terminated**: Thread completed or exception thrown
- Transitions: New → Runnable (start) → Running/Blocked (scheduler) → Terminated (complete)
- Scheduler: OS scheduler decides which runnable threads get CPU time
- Visual: State diagram showing transitions
- Code example: Different methods cause different state transitions

**Slide 8: Thread Creation & Starting**
- Wrong: Extending Thread
  ```java
  class MyThread extends Thread {
      public void run() { System.out.println("Running"); }
  }
  new MyThread().start();  // Correct
  ```
- Correct: Implementing Runnable
  ```java
  Runnable task = () -> System.out.println("Running");
  new Thread(task).start();
  ```
- Common mistake: Calling `run()` directly (executes in current thread, not new thread)
- With names: `new Thread(task, "Worker-1").start()`
- Multiple threads: Create multiple Thread objects with same Runnable
- Sleep: `Thread.sleep(1000)` pauses current thread

### Slides 9-15: Synchronization & Thread Safety
**Slide 9: The Race Condition Problem**
- Race condition: Multiple threads access shared data without coordination
- Result: Unpredictable, inconsistent behavior depending on thread timing
- Example: Two threads incrementing shared counter
  - Thread 1 reads count (5)
  - Thread 2 reads count (5)
  - Thread 1 writes count+1 (6)
  - Thread 2 writes count+1 (6)  ← Should be 7!
- Cause: Non-atomic operations (read-modify-write not single instruction)
- Consequence: Data corruption, lost updates, inconsistent state
- Visual: Timeline showing interleaved execution causing problem

**Slide 10: Visibility & Memory Barriers**
- Visibility problem: Changes to shared variable not immediately seen by other threads
- Each CPU core has cache; writes cached locally, not immediately visible to other cores
- Memory barrier: Synchronization mechanisms force cache flush/refresh
- volatile keyword: Forces memory barrier on read/write (simple solution for some cases)
- Without volatile: Thread 2 might see stale cached value, not Thread 1's update
- Performance trade-off: Memory barriers are expensive (flush CPU cache)
- Rule: Any shared mutable data needs synchronization

**Slide 11: synchronized Keyword Basics**
- synchronized: Acquires lock before entering critical section, releases on exit
- Mutual exclusion: Only one thread can hold lock at a time (others blocked)
- Prevents race condition: Only one thread modifying shared data at once
- Two forms:
  - Synchronized method: `synchronized void increment() { count++; }`
  - Synchronized block: `synchronized(lock) { count++; }`
- Lock object: Each object has intrinsic lock (use `this` or dedicated lock object)
- Atomicity: Entire synchronized block executes without interruption
- Code example: Counter with synchronized increment method

**Slide 12: Synchronized Methods & Blocks**
- Synchronized method: Lock is the object itself (`this`)
  ```java
  synchronized void increment() { count++; }  // Lock is this
  ```
- Synchronized block: Specify lock object explicitly
  ```java
  synchronized(lock) { count++; }  // Lock is lock object
  ```
- Advantage of block: Can synchronize only critical section, not entire method
- Performance: Less synchronization = better performance (lock held shorter)
- Pattern: Use synchronized block on subset of code, not entire method if possible
- Static synchronized: `static synchronized void method()` uses Class object lock
- Intrinsic locks: Each object has one lock, static methods use Class lock

**Slide 13: volatile Keyword**
- volatile: Ensures visibility (memory barrier) without mutual exclusion
- Use case: Single boolean flag or status variable read by multiple threads
- Example: `volatile boolean stopRequested = false;`
- Memory guarantees: Write to volatile variable happens-before read by other thread
- Does NOT prevent race condition: `volatile int count; count++;` still has race condition
- Use volatile when:
  - Single field updates (no compound operations)
  - No consistency with other fields needed
  - Reads far outweigh writes (volatile has overhead)
- Cheaper than synchronized: No lock acquisition/release, just memory barrier

**Slide 14: Deadlock Scenarios**
- Deadlock: Two or more threads block each other indefinitely
- Circular wait: Thread A waits for lock held by B, Thread B waits for lock held by A
- Classic example (Bank transfer):
  - Thread 1: Lock Account A, then wait for Account B lock
  - Thread 2: Lock Account B, then wait for Account A lock
  - Result: Deadlock, neither can proceed
- Conditions required (all must be present):
  1. Mutual exclusion: Resources cannot be shared
  2. Hold and wait: Thread holds resource while waiting for another
  3. No preemption: Cannot forcibly take held resource
  4. Circular wait: Cycle of threads waiting for resources
- Consequences: Complete program hang, requires restart
- Detection: Difficult at runtime; requires tools or manual inspection

**Slide 15: Preventing Deadlock**
- Strategy 1: Lock ordering (all threads acquire locks in same order)
  - Always lock A before B (prevents circular wait)
  - Code: Lock Account with smaller ID first
  - Requires discipline across codebase
- Strategy 2: Timeouts (abandon if lock not acquired in time)
  - `lock.tryLock(1, TimeUnit.SECONDS)` instead of blocking
  - If timeout: release locks, retry, or fail gracefully
  - Code example: With timeout and exception handling
- Strategy 3: Use higher-level constructs (avoid manual locks)
  - Use Queue, ConcurrentHashMap instead of custom synchronized code
  - Concurrency utilities handle deadlock prevention
- Strategy 4: Keep critical section short (minimize lock hold time)
  - Reduces probability and severity of deadlock
- Best practice: Use Strategy 3 (high-level utilities); avoid manual synchronization when possible

### Slides 16-20: Livelock & Thread Interruption
**Slide 16: Livelock Scenarios**
- Livelock: Threads keep running but make no progress (busy-waiting)
- Similar to deadlock: Program stuck, but threads active (not blocked)
- Example: Two threads trying to acquire locks with retry logic
  - Thread A acquires Lock1, Thread B acquires Lock2
  - Thread A tries Lock2, fails, releases Lock1 (to avoid deadlock)
  - Thread B tries Lock1, fails, releases Lock2 (to avoid deadlock)
  - Both retry forever, making no progress
- CPU overhead: Unlike deadlock (blocked, no CPU), livelock wastes CPU
- Detection: Harder than deadlock (threads appear active)
- Real-world: Retry logic with random backoff can cause livelock

**Slide 17: Starvation**
- Starvation: Thread never gets CPU time (different from deadlock/livelock)
- Cause: Lower-priority thread never runs because higher-priority threads always ready
- Example: Main thread spawns 10 max-priority threads; lower-priority thread starves
- Thread priority: 1 (MIN_PRIORITY) to 10 (MAX_PRIORITY), default 5 (NORM_PRIORITY)
- Scheduler: Typically preemptive (higher priority runs first)
- Prevention: Avoid extremely high priority threads; use default priority
- Fair scheduling: Use thread pools that handle priority balancing

**Slide 18: Thread Interruption**
- Interruption: Way to politely request thread to stop
- Method: `thread.interrupt()` sets interrupt flag on thread
- Checking: `Thread.currentThread().isInterrupted()` or `Thread.interrupted()`
- Response: Thread checks flag and voluntarily exits
- Cleared: `Thread.interrupted()` clears the flag
- InterruptedException: Some methods (sleep, join, wait) throw on interruption
- Pattern:
  ```java
  while (!Thread.currentThread().isInterrupted()) {
      // do work
  }
  ```
- Good practice: Respect interruption signal, don't ignore it

**Slide 19: Common Beginner Mistakes #1-2**

**Mistake 1: Calling run() Instead of start()**
- Wrong: `new Thread(task).run();` ← executes in current thread!
- Right: `new Thread(task).start();` ← creates new thread
- Why it matters: No parallelism, just sequential execution
- Symptom: No performance improvement, program blocked until run() completes
- Evidence: Call stack shows current thread, not new thread

**Mistake 2: Forgetting to Synchronize Shared Data**
- Wrong:
  ```java
  class Counter {
      int count = 0;  // Shared, not synchronized
      void increment() { count++; }
  }
  ```
- Right:
  ```java
  class Counter {
      int count = 0;
      synchronized void increment() { count++; }
  }
  ```
- Consequence: Race condition, lost updates, data corruption
- Hard to debug: Race condition typically manifests inconsistently
- Solution: Identify all shared mutable data, synchronize access

**Slide 20: Common Beginner Mistakes #3-4**

**Mistake 3: Holding Locks While Doing I/O**
- Wrong: Synchronized block containing file read/network call (slow)
- Impact: Other threads blocked for entire duration of I/O (seconds!)
- Right: Synchronize only when accessing shared data, not during I/O
- Pattern:
  ```java
  // WRONG
  synchronized void processFile() {
      String data = readFile();  // Network call, whole method blocked
      updateCounter(data);
  }
  // RIGHT
  void processFile() {
      String data = readFile();  // No lock
      synchronized(this) {
          updateCounter(data);  // Lock only for critical section
      }
  }
  ```

**Mistake 4: Synchronizing on Mutable Objects**
- Wrong: `synchronized(myList) { ... }` on list that gets reassigned
- Problem: Lock lost when reference changes
- Right: Use dedicated lock object `private final Object lock = new Object();`
- Pattern: Synchronize on final, stable lock object
- Immutability of reference matters, not contents of object

### Slides 21-25: Real-World Examples & Best Practices
**Slide 21: Real-World Example #1: Bank Account Transfer**
- Scenario: Transfer money between accounts atomically
- Naive attempt: Lock both accounts (can deadlock if order wrong)
- Solution: Always lock in order by account ID (prevents circular wait)
- Code:
  ```java
  void transferMoney(Account from, Account to, double amount) {
      Account first = from.id < to.id ? from : to;
      Account second = from.id < to.id ? to : from;
      synchronized(first) {
          synchronized(second) {
              from.balance -= amount;
              to.balance += amount;
          }
      }
  }
  ```
- Key insight: Lock ordering prevents deadlock

**Slide 22: Real-World Example #2: Producer-Consumer Queue**
- Scenario: Producer threads add items, consumer threads remove (preview of Part 2)
- Manual synchronization using synchronized keyword
- Issues: Busy-waiting if empty, inefficient signaling
- Solution preview: Wait/notify mechanism (semaphores, BlockingQueue in Part 2)
- Code shows synchronized block with explicit full/empty checking
- Visual: Producer/Consumer timeline with synchronization points

**Slide 23: Real-World Example #3: Singleton Pattern (Thread-Safe)**
- Scenario: Single global instance, multiple threads accessing
- Lazy approach: Create on first use, but thread-safe
- Double-checked locking pattern:
  ```java
  class Singleton {
      private static volatile Singleton instance;
      private Singleton() {}
      static Singleton getInstance() {
          if (instance == null) {
              synchronized(Singleton.class) {
                  if (instance == null) {
                      instance = new Singleton();
                  }
              }
          }
          return instance;
      }
  }
  ```
- Why double-check: First check avoids lock overhead after creation
- Why volatile: Ensures visibility across all threads

**Slide 24: Thread Safety Best Practices**
- Practice 1: Prefer immutability (immutable objects thread-safe by default)
- Practice 2: Use high-level concurrency utilities (Part 2 topics)
- Practice 3: Minimize critical section (hold locks shortest time)
- Practice 4: Avoid nested locks (prevents deadlock)
- Practice 5: Document what data needs synchronization (hard to see)
- Practice 6: Use ThreadLocal for thread-specific data (avoid sharing)
- Practice 7: Test with multiple threads (race conditions non-deterministic)
- Practice 8: Never return shared mutable data without synchronization

**Slide 25: Summary & Transition to Part 2**
- Threads enable concurrent execution on multi-core systems
- Shared mutable data requires synchronization (synchronized, volatile)
- Race conditions cause data corruption; synchronization prevents
- Deadlock possible with multiple locks; prevent via lock ordering
- Manual synchronization error-prone; high-level utilities better (Part 2)
- Best practice: Minimize synchronization, use immutability, prefer utilities
- Part 2 preview: ExecutorService, BlockingQueue, CompletableFuture handle threading for us
- Integration: Streams (Day 8) use parallel streams, which use threads under hood; now understand why synchronization matters

