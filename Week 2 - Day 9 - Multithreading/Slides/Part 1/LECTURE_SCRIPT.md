# Week 2 - Day 9, Part 1: Thread Basics, Synchronization & Deadlock
## 60-Minute Lecture Script

---

[00:00-02:00] **Welcome & Context**

Good morning! We've reached one of the most important and, honestly, most challenging topics in software engineering: multithreading.

Yesterday, you learned about lambdas, streams, and the DateTime API. Great stuff. But today, we're diving into something that's been around since the beginning of Java, and it's still one of the most critical concepts for writing scalable applications.

Why does multithreading matter? Because modern computers have multiple cores. Your laptop probably has 4, 8, maybe 16 cores. A server might have 64. A single-threaded program uses only one core. That's like having a 16-lane highway and driving in only one lane. Multithreading lets you use all those cores and handle multiple tasks concurrently.

Today's lecture is divided into two parts. Part 1—what we're doing right now—covers the fundamentals: how to create threads, what thread safety means, and the synchronization problems you need to avoid. Part 2 will show you the modern tools that handle threading for you: thread pools, concurrent collections, asynchronous workflows. But to use those tools effectively, you need to understand what's happening under the hood. That's what Part 1 teaches.

Let's get started.

---

[02:00-04:00] **The Problem We're Solving**

Picture this scenario. You're building a web server. A thousand users connect simultaneously, each making a request. How do you handle that?

In the old days, the solution was: one process per request. A new process for each connection. But processes are heavy—they take seconds to create, they consume memory, they're isolated from each other. Inefficient.

The modern solution: multiple threads within a single process. Threads are lightweight, they share memory, they're fast to create. One thread per request, all within the same process, all sharing the same resources. Problem solved.

Or consider this: your user interface thread needs to remain responsive to user input. But you're downloading a large file in the background. How do you do both at once? Threads. One thread handles UI, another downloads the file. They run concurrently.

Or you're processing data from a queue: some threads add data, others consume it. You need coordination and parallelism. Threads again.

Modern applications are fundamentally concurrent. Understanding multithreading is not optional—it's essential.

---

[04:00-06:00] **What is a Thread?**

A thread is a lightweight process. More specifically, it's a unit of execution within a process.

Think of your program as a process. Before today, everything you've written has been single-threaded: one line of execution, one instruction at a time. A thread is an independent line of execution.

Here's the key difference between threads and processes: Threads exist within a process and share the same memory. All threads in your program can access the same variables, the same objects. They're tightly coupled, sharing resources. Processes are isolated—each process has its own memory, its own universe.

Why does that matter? Threads are lightweight. Creating a new process takes 10-100 milliseconds and requires significant memory overhead. Creating a new thread takes about 1 millisecond and requires only about 64 kilobytes. You can have thousands of threads in a single program. You can't have thousands of processes.

But there's a catch: because threads share memory, they can interfere with each other. If two threads modify the same variable at the same time, chaos can ensue. This is the core problem we spend most of today on: coordinating threads so they work together safely.

---

[06:00-12:00] **Creating Threads: Thread Class & Runnable Interface**

Okay, let's create a thread. There are two approaches: extend the Thread class or implement the Runnable interface. The Java community strongly prefers the second approach—Runnable—and I'll explain why.

First, the Thread approach:

```java
class MyThread extends Thread {
    public void run() {
        System.out.println("Thread is running");
    }
}

MyThread thread = new MyThread();
thread.start();
```

Simple enough. We extend Thread, override the run() method with the code we want the thread to execute, create an instance, and call start(). That's all.

But here's the issue: What if MyThread needs to extend something else? Java has single inheritance. If you extend Thread, you can't extend any other class. That severely limits your design flexibility.

Enter Runnable:

```java
Runnable task = new Runnable() {
    public void run() {
        System.out.println("Thread is running");
    }
};

Thread thread = new Thread(task);
thread.start();
```

We create an object that implements Runnable (a Runnable is just something with a run() method), pass it to Thread's constructor, and call start().

Now, remember lambdas? This is where they shine. Runnable is a functional interface—it has exactly one abstract method: run(). So:

```java
Thread thread = new Thread(() -> System.out.println("Thread is running"));
thread.start();
```

One line. Crystal clear. That's modern Java.

The advantages of Runnable:
1. Doesn't consume your inheritance budget
2. Can implement multiple interfaces
3. Lambda-compatible
4. Separates the task from the thread

Rule: Always use Runnable (or Callable, which we'll see later). Never extend Thread.

Now, critically important: there's a common mistake. People write:

```java
// WRONG
Thread thread = new Thread(() -> System.out.println("Running"));
thread.run();  // Executes in current thread, not a new thread!
```

vs

```java
// RIGHT
Thread thread = new Thread(() -> System.out.println("Running"));
thread.start();  // Creates new thread
```

Calling `run()` directly just executes the code synchronously in your current thread. There's no parallelism. Calling `start()` is what actually creates a new thread and runs it concurrently. The difference is critical.

---

[12:00-18:00] **Thread Lifecycle & States**

Every thread has a lifecycle. It starts, it runs, it stops. Knowing the states helps you understand what's happening.

**New:** You've created the Thread object but haven't called start() yet. It's not running, it's just... created.

**Runnable:** You've called start(). The thread is ready to run. It's not necessarily executing right now—it's waiting for the scheduler to give it CPU time. But it's eligible to run.

**Running:** The scheduler has given this thread CPU time, and it's actively executing. On a single-core machine, only one thread is ever truly running at a moment. On multi-core, multiple threads can be running simultaneously.

**Blocked/Waiting:** The thread is waiting. Maybe it's waiting for I/O (reading a file). Maybe it's waiting for a lock (more on that soon). Maybe it called `Thread.sleep()` and is sleeping for a specified time. It's not consuming CPU; it's just... waiting.

**Terminated:** The thread's run() method completed, or it threw an exception. The thread is dead.

The flow:

```
New → Runnable ↔ Running ↔ Blocked → Terminated
```

The scheduler controls the transitions between Runnable, Running, and Blocked. Your code can request state transitions with methods like `sleep()` or `join()`.

Speaking of which: `Thread.sleep(1000)` pauses the current thread for 1000 milliseconds. The thread goes to the Blocked state and won't run until the sleep duration expires.

And `thread.join()` waits for another thread to complete. The calling thread blocks until the specified thread terminates.

---

[18:00-24:00] **The Core Problem: Race Conditions**

Now we get to the hard part: shared mutable state.

Here's a scenario. We have a simple counter, and two threads increment it:

```java
class Counter {
    int count = 0;
    void increment() {
        count++;
    }
}

Counter counter = new Counter();
Thread t1 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) counter.increment();
});
Thread t2 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) counter.increment();
});

t1.start();
t2.start();
t1.join();
t2.join();

System.out.println(count);  // Should be 2000, right?
```

We'd expect count to be 2000. Each thread increments 1000 times. But when you run this, you might get 1500. Or 1800. Or 2000 (sometimes). The result is unpredictable.

Why? Because `count++` is not a single atomic operation. It's three operations:

1. Read the current value of count (say, it's 5)
2. Add 1 (now we have 6)
3. Write 6 back to count

If two threads do this concurrently:

```
Time | Thread A          | Thread B          | count
-----|-------------------|-------------------|------
1    | read count (5)    |                   | 5
2    |                   | read count (5)    | 5
3    | add 1 → 6         |                   | 5
4    |                   | add 1 → 6         | 5
5    | write 6           |                   |       6
6    |                   | write 6           | 6
```

Both threads wrote 6, so count is 6. But we should have incremented twice, so count should be 7. We lost an update.

This is a **race condition**. The threads are "racing" to update the same data, and the result depends on the timing. The exact same code produces different results on different runs.

This is the core problem of multithreading. Shared mutable data is a liability.

---

[24:00-30:00] **Synchronization: The synchronized Keyword**

The solution: ensure that only one thread modifies shared data at a time. That's **mutual exclusion**.

Enter the `synchronized` keyword:

```java
class Counter {
    int count = 0;
    synchronized void increment() {
        count++;
    }
}
```

Now, when Thread A calls `increment()`, it acquires a lock. Thread B tries to call `increment()`, but the lock is held, so Thread B blocks, waiting for the lock. When Thread A finishes and exits the method, the lock is released. Thread B acquires the lock and runs `increment()`.

The result: only one thread modifies count at a time. Increments happen serially, one after another. No race condition. count will be exactly 2000.

How does this work? Every object in Java has an intrinsic lock (also called a monitor lock). When you mark a method as synchronized, the lock is the object itself (this). Before entering the method, the thread must acquire the lock. After exiting, the lock is released.

You can also synchronize a block of code:

```java
int count = 0;
void increment() {
    synchronized(this) {
        count++;
    }
}
```

This is equivalent to synchronizing the entire method. But why would you use a block instead? Because you can synchronize only the critical section—the code that actually accesses shared data. If your method does other things, you don't want to hold the lock the entire time.

Example:

```java
void processAndIncrement() {
    // This takes a long time
    String data = expensiveComputation();
    
    // Only this needs the lock
    synchronized(this) {
        count++;
    }
}
```

Without the block, the lock would be held during the expensive computation, blocking other threads unnecessarily.

---

[30:00-36:00] **Visibility & the volatile Keyword**

Okay, so `synchronized` prevents race conditions via mutual exclusion. But there's another problem: visibility.

Imagine Thread A modifies a variable, and Thread B reads it. Does Thread B immediately see the change?

Not necessarily. Modern CPUs have caches. When Thread A modifies count, the change might be cached locally on Core A and not immediately visible to Core B. Thread B reads count but sees the stale cached value.

This is a **visibility problem**. The data is inconsistent across threads.

The solution: memory barriers. When using `synchronized`, there's an implicit memory barrier. Before acquiring a lock, the thread refreshes its cache from main memory. When releasing, it flushes its cache. This ensures visibility.

But what if you don't want the full overhead of synchronization? What if you just have a single variable that multiple threads read and write, with no compound operations?

Enter `volatile`:

```java
volatile boolean stopRequested = false;

Thread t1 = new Thread(() -> {
    while (!stopRequested) {
        // do work
    }
});

Thread t2 = new Thread(() -> {
    stopRequested = true;  // Request stop
});
```

Marking a variable as `volatile` forces a memory barrier on every read and write. Thread A writes to stopRequested, the write is flushed to memory, and Thread B immediately sees it.

But `volatile` does NOT provide mutual exclusion. If you do:

```java
volatile int count;
count++;  // NOT SAFE
```

You still have a race condition. `volatile` only ensures visibility, not atomicity.

Use `volatile` when:
- Single field updates (no compound operations)
- Many reads, few writes (volatile has overhead)
- No consistency with other fields

Otherwise, use `synchronized`.

---

[36:00-42:00] **Deadlock: The Danger of Multiple Locks**

Now, here's where things get scary. What happens if you have multiple locks?

Example: Bank transfer between two accounts.

```java
class Account {
    int balance = 0;
    synchronized void transfer(Account recipient, int amount) {
        this.balance -= amount;
        recipient.balance += amount;
    }
}
```

Seems safe. But what happens if Thread A transfers from Account 1 to Account 2, and Thread B transfers from Account 2 to Account 1?

```
Time | Thread A                           | Thread B
-----|------------------------------------|-----------
1    | Acquires lock on Account 1         |
2    |                                    | Acquires lock on Account 2
3    | Tries to acquire lock on Account 2 | (waiting...)
4    | (blocked, Account 2 is locked)     |
5    | (still blocked)                    | Tries lock on Account 1
6    | (still blocked)                    | (blocked, Account 1 is locked)
```

Thread A holds Account 1 and waits for Account 2. Thread B holds Account 2 and waits for Account 1. Neither can proceed. They're stuck indefinitely.

This is a **deadlock**. Both threads are blocked, waiting for locks they'll never acquire because the other thread holds them.

Deadlock is a complete program hang. The only solution is to kill the process and restart.

To cause a deadlock, all four of these conditions must be present:

1. **Mutual exclusion:** Resources cannot be shared (locks are exclusive).
2. **Hold and wait:** Threads hold resources while waiting for others.
3. **No preemption:** Cannot forcibly take held resources.
4. **Circular wait:** A → B → A cycle of resource waits.

Remove any one, and deadlock is impossible.

---

[42:00-48:00] **Preventing Deadlock**

Strategy 1: **Lock ordering.** Always acquire locks in the same order across all code.

```java
void transfer(Account a, Account b, int amount) {
    // Always lock the account with the smaller ID first
    Account first = a.id < b.id ? a : b;
    Account second = a.id < b.id ? b : a;
    
    synchronized(first) {
        synchronized(second) {
            a.balance -= amount;
            b.balance += amount;
        }
    }
}
```

Now, Thread A and Thread B both try to lock in the same order (Account 1 then Account 2, or vice versa depending on IDs). No circular wait. No deadlock.

This works, but requires discipline. Every piece of code that touches multiple accounts must follow the same lock order.

Strategy 2: **Timeouts.** Don't wait indefinitely; if you can't acquire a lock in time, abort and retry.

```java
synchronized(first) {
    if (second.tryLock(1, TimeUnit.SECONDS)) {
        // Got both locks
    } else {
        // Timeout, release first, retry later
        // Thread continues, no deadlock, but no progress either
    }
}
```

This prevents indefinite blocking, but it's still not great—you're stuck in retry loops.

Strategy 3: **Use high-level constructs.** Don't write your own synchronization. Use thread-safe utilities designed to prevent deadlock. This is what Part 2 covers: BlockingQueue, ConcurrentHashMap, etc. These are battle-tested and deadlock-free.

Best practice: Use Strategy 3. Avoid manual synchronization when possible. Let the platform handle it.

---

[48:00-54:00] **Livelock & Starvation**

Deadlock is when threads are blocked. But what if threads keep running but never make progress? That's **livelock**.

Example: Two threads trying to acquire locks with retry logic:

```java
while (true) {
    synchronized(lockA) {
        if (tryLock(lockB)) {
            doWork();
            break;
        }
        // Failed to get lockB, release lockA to prevent deadlock
    }
    // Retry
}
```

Thread A gets lockA, fails to get lockB, releases lockA. Thread B gets lockA, fails to get lockB, releases lockA. Both retry forever. They're active but never make progress.

CPU usage: High (threads constantly running and retrying). This is worse than deadlock—at least deadlock doesn't waste CPU.

**Starvation** is different: a thread never gets CPU time because higher-priority threads always occupy the CPU.

Example:

```java
Thread main = new Thread(() -> {
    for (int i = 0; i < 10000; i++) {  // Never yields
        // high priority work
    }
});
main.setPriority(Thread.MAX_PRIORITY);

Thread worker = new Thread(() -> {
    // This might never run
});
worker.setPriority(Thread.MIN_PRIORITY);
```

The main thread hogs the CPU, and the worker thread never gets a turn.

Prevention: Avoid extreme priorities. Use thread pools (Part 2) that handle priority fairly.

---

[54:00-58:00] **Common Mistakes & Thread Interruption**

**Mistake 1: Calling run() instead of start().**

```java
// WRONG
new Thread(() -> System.out.println("Running")).run();

// RIGHT
new Thread(() -> System.out.println("Running")).start();
```

Only `start()` creates a new thread. Calling `run()` directly executes synchronously.

**Mistake 2: Forgetting to synchronize shared data.**

```java
// WRONG
class Counter {
    int count = 0;  // Shared, not synchronized
    void increment() { count++; }
}

// RIGHT
class Counter {
    int count = 0;
    synchronized void increment() { count++; }
}
```

Without synchronization, race conditions are inevitable.

One more important topic: **Thread interruption.** If you want a thread to stop gracefully, you can interrupt it:

```java
Thread thread = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
        // do work
    }
});

thread.start();
// ... later ...
thread.interrupt();  // Request stop
```

Calling `interrupt()` sets a flag on the thread. The thread can check this flag with `isInterrupted()` and exit gracefully. Some methods like `sleep()` and `join()` throw `InterruptedException` if interrupted.

This is a cooperative approach: the thread can choose to respect the interruption request or ignore it. It's not forceful termination (which is dangerous and deprecated).

---

[58:00-60:00] **Wrap-Up & Transition**

So, threads enable parallelism, but shared mutable data creates problems. Race conditions lose updates. Visibility issues cause inconsistency. Deadlock causes infinite hang.

Solutions: Synchronization (synchronized keyword), visibility (volatile), lock ordering (prevent deadlock).

But here's the dirty secret: manual synchronization is hard. It's error-prone. Race conditions are non-deterministic and hard to debug. Deadlock is subtle.

That's why Part 2 focuses on letting the framework handle it. Thread pools manage threads for you. BlockingQueues handle producer-consumer coordination. CompletableFuture handles asynchronous workflows. Concurrent collections are thread-safe without you writing synchronized code.

But you need to understand what's happening under the hood. Now you do.

Take a break. When we come back, we'll see how to write multithreaded applications without drowning in synchronization details.

See you in a few minutes for Part 2!

