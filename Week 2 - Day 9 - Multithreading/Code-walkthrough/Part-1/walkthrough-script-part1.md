# Day 9 — Part 1 Walkthrough Script
## Thread Basics, Lifecycle, Synchronization, Deadlock & Livelock
**Duration:** ~90 minutes | **Files:** 4 Java demos

---

## Pre-Class Setup (5 min)

[ACTION] Open all four Part 1 files. Have a terminal ready to run them.

[ACTION] Write on the board:
```
"A thread is the smallest unit of execution."
"Multiple threads share the same heap — shared state is the danger."
"Thread safety = behaves correctly no matter when threads are scheduled."
```

[ASK] "Who here has seen a program produce different output on different runs, even with the same input?" Pause. "That's usually a threading bug. Today you'll understand exactly why that happens — and how to fix it."

---

## FILE 1: `01-thread-basics-and-runnable.java` (~22 min)

### Opening: Why Threads? (2 min)

[ACTION] Draw on the board:

```
Single-threaded:
  Main ─── task1 ─── task2 ─── task3 ─── done
             (blocked while each task runs)

Multi-threaded:
  Main ─── spawn T1 ─── spawn T2 ─── spawn T3 ─── wait all ─── done
                │              │              │
               task1          task2          task3
               (concurrent)
```

"Threads let us do multiple things at once. A web server uses threads to handle many requests simultaneously. A data pipeline uses threads to process and write data at the same time. Without threads, everything is sequential."

---

### Section 1 — Extending Thread (5 min)

[ACTION] Open `01-thread-basics-and-runnable.java`. Scroll to `demonstrateExtendingThread()`.

[ACTION] Walk through `CounterThread`:
- "We extend `Thread` and override `run()`. The `run()` method is the task."
- "The constructor calls `super(name)` — this sets the thread's name. Always name your threads. Makes debugging much easier."

[ACTION] Focus on `counterThread.start()`:
- "IMPORTANT: You call `start()`, NOT `run()`. If you call `run()` directly — it runs on the current thread, no new thread is created. That's a very common bug."

[ASK] "What state is the thread in before we call `start()`?" (NEW)

[ACTION] Show the state output before/after `start()` and after `join()`. Walk through the three states: NEW → RUNNABLE → TERMINATED.

[ACTION] Explain `join()`:
- "join() tells the main thread: 'wait here until this thread finishes'. Without it, main thread might reach the end of the program before the worker finishes."

⚠️ WATCH OUT — Never call `run()` directly. Always `start()`. This is the #1 threading beginner mistake.

---

### Section 2 — Implementing Runnable (5 min)

[ACTION] Scroll to `demonstrateRunnable()`.

"Approach 2 — and the one you should prefer."

[ACTION] Show the separation:
- "`PrintTask` implements `Runnable` — it defines the TASK."
- "`new Thread(printTask, "Runnable-Thread")` — the Thread is the VEHICLE."

"Why is this better? Because you're not consuming your one inheritance slot. And more importantly — look at this:"

[ACTION] Show the 3-thread example. "Same `printTask` object, three different `Thread` wrappers. The task is reusable."

[ASK] "What do you think the output ordering will be?" Let them guess. Then run it. "See — it's different every time. The OS decides which thread gets CPU time. That's the nature of concurrency."

---

### Section 3 — Lambda as Runnable (3 min)

[ACTION] Scroll to `demonstrateLambdaRunnable()`.

"Runnable has exactly ONE abstract method: `run()`. What does that make it?" (A functional interface!)

"So a lambda that takes no args and returns void IS a Runnable. This is how you'll write threads in modern Java — no boilerplate classes needed."

[ACTION] Show the inline lambda: `new Thread(() -> { ... }, "Countdown-Thread").start()`. "Create it and start it in one expression. Clean and concise."

---

### Section 4 — Thread Methods (5 min)

[ACTION] Scroll to `demonstrateThreadMethods()`.

Walk through each method:

- **`Thread.sleep(ms)`** — "Pause the current thread. Always requires handling `InterruptedException`."
- **`Thread.currentThread()`** — "Returns a reference to the thread you're currently running on. Very useful inside a Runnable to get the thread's name."
- **`setName / setPriority`** — "Priorities hint to the scheduler but are NOT guaranteed. Don't rely on priority for correctness."
- **`join(ms)` with timeout** — "Wait at most N milliseconds. If the thread is still running, you proceed. Check `isAlive()` to see if it finished."

⚠️ WATCH OUT — `join()` without a timeout can block forever if the thread never completes (e.g., it's in an infinite loop or deadlocked). Always consider timeouts.

---

### Section 5 — Interruption (5 min)

[ACTION] Scroll to `demonstrateThreadInterruption()`.

"Java doesn't support 'force killing' a thread. Instead, it uses a cooperative system — you ask the thread to stop, and the thread must check and respond."

[ACTION] Show `thread.interrupt()`. "This sets the interrupted flag on the thread."

[ACTION] Show the two check points:
1. `isInterrupted()` in the loop condition — "check before each step"
2. `catch (InterruptedException)` in sleep — "sleep throws if interrupted; you MUST re-set the flag with `Thread.currentThread().interrupt()` so callers can see it"

[ASK] "Why do we call `Thread.currentThread().interrupt()` inside the catch?" (Because catching InterruptedException clears the flag. If you don't re-set it, the interrupt is silently swallowed.)

⚠️ WATCH OUT — Never do `catch (InterruptedException e) { /* ignore */ }`. Always either re-interrupt or handle the shutdown explicitly.

---

### Section 6 — Daemon Threads (2 min)

[ACTION] Scroll to `demonstrateDaemonThreads()`.

"Two types of threads: user threads (the JVM waits for these) and daemon threads (the JVM does NOT wait — it kills them when all user threads finish)."

"Classic examples: garbage collector, heartbeat monitors, log flusher. Background services that should live only as long as the app is running."

[ACTION] Point out: "Must call `setDaemon(true)` BEFORE `start()`. After start, it's too late."

→ TRANSITION: "Now we know how to create and manage threads. But what happens when two threads both touch the same data? Let's find out the hard way first."

---

## FILE 2: `02-thread-lifecycle-and-states.java` (~15 min)

### Opening (2 min)

[ACTION] Draw the full state diagram on the board (from the file's comment block). Walk through each state name and what causes the transition.

"Java gives us a way to *observe* what state a thread is in. Let's see each one in action."

---

### Each State Demo (10 min)

[ACTION] Open `02-thread-lifecycle-and-states.java`. Go through each `demonstrate*()` method in order.

**NEW / RUNNABLE:**
- Show state before `start()` = NEW, after `start()` = RUNNABLE.
- "RUNNABLE doesn't mean actively on the CPU — it means 'ready to run or running'. The OS schedules it."

**TIMED_WAITING:**
- Show state while in `Thread.sleep()` = TIMED_WAITING.
- [ASK] "What other operations cause TIMED_WAITING?" (wait(ms), join(ms))

**WAITING:**
- Show state while in `lock.wait()` = WAITING.
- "Waiting indefinitely. Nothing happens until a notify."

**BLOCKED:**
- Show state while waiting for a held lock = BLOCKED.
- "BLOCKED is specifically about lock contention. WAITING is about explicit wait() calls. Don't confuse them."

**TERMINATED:**
- Show you can't restart a terminated thread.
- "Once terminated, that's it. Create a new Thread object if you need to run the task again."

---

### Summary Table (3 min)

[ACTION] Walk through `printStateTransitionSummary()`. Read each transition aloud and ask students to confirm they understand the trigger.

[ASK] "After `sleep(ms)` expires — what state does the thread go to?" (RUNNABLE — not necessarily immediately running, but ready to be scheduled)

→ TRANSITION: "Now we know *states* — next we need to talk about what goes wrong when threads share data."

---

## FILE 3: `03-synchronization-and-thread-safety.java` (~25 min)

### Opening: The Problem (3 min)

[ACTION] Before opening the file, write on the board:

```java
count++;
```

"How many CPU instructions is this? ONE? No — THREE:
1. READ count from memory into a register
2. ADD 1 to the register
3. WRITE register back to memory

If two threads both do this at the same time, they both read the SAME value, add 1, and write the SAME new value back. One increment is lost."

---

### Section 1 — Race Condition Demo (5 min)

[ACTION] Open `03-synchronization-and-thread-safety.java`. Scroll to `demonstrateRaceCondition()`.

[ACTION] Show `UnsafeCounter.increment()`. "Three steps. Not atomic. Race condition."

[ACTION] Walk through the setup: two threads, each incrementing 10,000 times. Expected: 20,000.

[ASK] "Before I run this — what result do you predict?" Let them answer.

[ACTION] Conceptually describe the run (or run it): "We'll get something like 17,842. Different every run. This is a data race."

⚠️ WATCH OUT — Race conditions are intermittent. Your code might work 99% of the time and fail 1%. That makes them extremely hard to reproduce in testing. Always think about thread safety upfront.

---

### Section 2 — Synchronized Method (4 min)

[ACTION] Scroll to `demonstrateSynchronizedMethod()`.

"Add `synchronized` to the method signature. Now only ONE thread can be inside this method at a time, per object instance."

[ACTION] Walk through `SafeCounter`. Point out both `increment()` AND `getCount()` are synchronized. "If only increment is synchronized but getCount isn't — a reading thread might see a stale value."

[ASK] "What's the tradeoff of using synchronized?" (Performance — threads have to queue up. Synchronized blocks are slower than unsynchronized code.)

---

### Section 3 — Synchronized Block (5 min)

[ACTION] Scroll to `demonstrateSynchronizedBlock()`.

"Synchronizing the whole method is heavy-handed. If only 10 lines of a 100-line method are critical, we only need to lock those 10 lines."

[ACTION] Walk through `BankAccount`:
- "A dedicated `lock` object — `private final Object lock = new Object()`. Best practice: use a dedicated lock, not `this`."
- "The non-critical logging happens OUTSIDE the synchronized block."
- "Inside the block: check balance → sleep (simulate processing) → deduct."

"Without synchronization, both threads pass the `balance >= amount` check before either has a chance to deduct. Both succeed. Account goes negative."

[ASK] "Why do we `try { Thread.sleep(10) }` inside the synchronized block in this demo?" (To make the race condition obvious — in real code, the processing time creates the window for the bug.)

---

### Section 4 — volatile (4 min)

[ACTION] Scroll to `demonstrateVolatile()`.

"volatile solves a different problem: visibility. Without volatile, each thread may cache a variable's value in a CPU register. Changes made by one thread might not be seen by another."

[ACTION] Draw on the board:
```
CPU Core 1 (Thread 1)  |  CPU Core 2 (Thread 2)
running = true (cached)|  running = true (cached)
sets running = false   |  still sees running = true!  ← stale cache
```

"volatile forces every read/write to go directly to main memory. No caching."

[ASK] "Does volatile make `count++` safe?" (NO! It ensures visibility but not atomicity. For atomic operations, you need synchronized or AtomicInteger.)

---

### Section 5 — Atomic Variables (3 min)

[ACTION] Scroll to `demonstrateAtomicVariables()`.

"For single-variable operations — increment, decrement, compare-and-swap — the `java.util.concurrent.atomic` package gives us lock-free thread safety using hardware primitives (CAS)."

"Faster than synchronized for single variables. Cannot be used for compound operations (checking balance AND deducting — that still needs a lock)."

Walk through: `incrementAndGet`, `getAndIncrement`, `addAndGet`, `compareAndSet`. "CAS: only update if current value matches the expected value. If another thread changed it in between, retry."

---

### Section 6 — wait/notify (2 min)

[ACTION] Scroll to `demonstrateWaitNotify()`. "This previews the producer-consumer pattern we'll build in Part 2. Notice the pattern:
- `wait()` inside a `while` loop (not `if` — spurious wakeups can happen)
- `notifyAll()` after making a change
- All inside `synchronized`"

→ TRANSITION: "Now the most dangerous bug in concurrent programming: deadlock."

---

## FILE 4: `04-deadlock-and-livelock.java` (~20 min)

### Opening: The Classic Scenario (3 min)

[ACTION] Draw on the board:

```
Thread A:  holds Lock1 ──wants──▶ Lock2
Thread B:  holds Lock2 ──wants──▶ Lock1
                ↑_________________________↑
                       CIRCULAR WAIT
```

"Each thread is waiting for what the other holds. Nobody will ever release — deadlock. The program is frozen but appears to be running."

"Run jstack against a deadlocked process and you'll see the cycle in the thread dump. In production, you might not notice for a while because the threads appear alive — just stuck."

---

### Section 1 — Deadlock Demo (5 min)

[ACTION] Open `04-deadlock-and-livelock.java`. Walk through `demonstrateDeadlock()`.

[ACTION] Trace through the sequence:
1. Thread A grabs `lockA`. Thread B grabs `lockB`.
2. Thread A sleeps 100ms. Thread B sleeps 100ms.
3. Thread A tries to grab `lockB` — BLOCKED (Thread B holds it).
4. Thread B tries to grab `lockA` — BLOCKED (Thread A holds it).
5. Both wait forever.

[ASK] "The demo has a 3-second timeout and then interrupts both threads. In production code, would that happen automatically?" (No. Without explicit detection, the threads hang permanently.)

⚠️ WATCH OUT — Deadlocks are not always this obvious. In real code they can involve 3+ threads and locks spread across different classes.

---

### Section 2 — Prevention: Lock Ordering (4 min)

[ACTION] Scroll to `demonstrateDeadlockPrevention_LockOrdering()`.

"The simplest solution: always acquire locks in the same order. If every thread that needs both locks always takes lock1 then lock2 — circular wait is impossible."

[ACTION] Walk through the code. Both threads: `synchronized(lock1)` → `synchronized(lock2)`. Same order.

"Thread A gets lock1 first. Thread B also wants lock1 — gets BLOCKED. Thread A finishes, releases lock1. Thread B acquires lock1, then lock2. Done. No deadlock."

[ASK] "What's the challenge with lock ordering in large systems?" (Hard to enforce across teams and codebases. A utility class might acquire locks in a different order than its caller expects.)

---

### Section 3 — Prevention: tryLock (3 min)

[ACTION] Scroll to `demonstrateDeadlockPrevention_TryLock()`.

"ReentrantLock has `tryLock(timeout)` — try to get the lock, but don't wait forever. If you can't get it in time, back off and retry."

[ACTION] Walk through the logic. "Gets lockA. Tries lockB with 50ms timeout. If it fails — releases lockA and backs off. Both threads might back off and retry — that's livelock territory, but with randomized back-off it usually resolves."

---

### Section 4 — Livelock (5 min)

[ACTION] Scroll to `demonstrateLivelock()`.

"Livelock is trickier: the threads ARE active, consuming CPU, but they're just reacting to each other without making progress."

[ACTION] Read through the hallway scenario. Alice steps left. Bob steps right. Both still blocked. Alice steps right. Bob steps left. Again. Again.

[ASK] "What's the fix for livelock?" (Randomization — add random backoff so threads don't keep mirroring each other. Prioritization — one party yields by rule, not by politeness.)

---

### Wrap-Up: Part 1 Summary (5 min)

[ACTION] Write this on the board:

```
Creating Threads:    extend Thread  OR  implement Runnable (prefer Runnable/lambda)
Starting:            always .start(), never .run()
Thread States:       NEW → RUNNABLE → [BLOCKED / WAITING / TIMED_WAITING] → TERMINATED
Race Condition:      read-modify-write on shared data → fix with synchronized / atomic
Deadlock:            circular lock wait → fix with lock ordering / tryLock
Livelock:            infinite reaction loop → fix with randomization
```

[ASK] "Quick fire — what's the difference between BLOCKED and WAITING?" (BLOCKED = waiting for a lock; WAITING = explicitly called wait(), joins without timeout)

[ASK] "What does `volatile` guarantee?" (Visibility — not atomicity)

[ASK] "You have a bank account class with a `transfer` method that calls `debit` and `credit` — each synchronized on their own account. What could go wrong?" (Deadlock — Thread A: debit A then credit B. Thread B: debit B then credit A.)

→ TRANSITION: "After the break: we solve the producer-consumer problem the right way, learn thread pools so we're not creating threads manually anymore, and build async workflows with CompletableFuture. See you in 10."

---

*End of Part 1 Script*
