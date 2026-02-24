# Exercise 03: Deadlock Detection and Prevention

## Objective
Reproduce a classic deadlock between two threads, observe it freeze the program, then fix it by enforcing consistent lock acquisition order.

## Background
A **deadlock** occurs when two (or more) threads each hold a lock the other needs and both wait forever — neither can proceed. The four Coffman conditions must all hold simultaneously for deadlock to occur: mutual exclusion, hold-and-wait, no preemption, and circular wait. Eliminating the circular-wait condition by always acquiring locks in the same global order is the most practical fix in Java.

A **livelock** is subtly different: threads are actively running (not blocked), but repeatedly reacting to each other's actions in a way that makes no progress — like two people in a hallway both stepping aside in the same direction simultaneously.

## Requirements

1. **Simulate deadlock**: Create two `Object` lock instances: `lockA` and `lockB`.
   - Thread 1 (`"Thread-1"`) acquires `lockA` first, sleeps 50 ms, then tries to acquire `lockB`
   - Thread 2 (`"Thread-2"`) acquires `lockB` first, sleeps 50 ms, then tries to acquire `lockA`
   - Print `"[Thread-X] acquired LockA/LockB"` at each acquisition step
   - Add a `5000ms` timeout using a `Timer` or a watchdog thread to interrupt both deadlocked threads and print `"Watchdog: detected deadlock — interrupting threads"` so the program does not hang forever

2. **Fixed version**: Copy the same two-thread structure but this time both threads acquire `lockA` **before** `lockB` (consistent ordering). Demonstrate that both threads complete successfully and print `"[Thread-X] completed successfully"`.

3. **Livelock description**: After the fixed demo, print a formatted explanation of what livelock is and how it differs from deadlock (no code needed — just `System.out.println` paragraphs).

## Hints
- To avoid hanging the entire program in part 1, start a watchdog `Thread` that sleeps 500 ms then calls `t1.interrupt()` and `t2.interrupt()` — wrap the `synchronized` blocks in try-catch for `InterruptedException`
- The deadlock happens because Thread-1 holds lockA and waits for lockB while Thread-2 holds lockB and waits for lockA — circular dependency
- The fix is simply to make both threads acquire in the same order: always lockA, then lockB
- `Thread.getState()` returns `BLOCKED` when a thread is waiting to acquire a monitor

## Expected Output

```
=== Deadlock Demonstration ===
[Thread-1] acquired LockA
[Thread-2] acquired LockB
Watchdog: detected deadlock — interrupting threads
[Thread-1] interrupted while waiting for LockB
[Thread-2] interrupted while waiting for LockA

=== Deadlock Fixed (Consistent Lock Ordering) ===
[Thread-1] acquired LockA
[Thread-1] acquired LockB
[Thread-1] completed successfully
[Thread-2] acquired LockA
[Thread-2] acquired LockB
[Thread-2] completed successfully

=== What is Livelock? ===
Livelock: threads are NOT blocked — they are actively running.
However, they keep reacting to each other and making no real progress.
Example: two threads each detect the other is waiting and yield,
         but both yield at the same time, endlessly.
Unlike deadlock (frozen), livelock consumes CPU but accomplishes nothing.
Fix: introduce randomized back-off delays so threads don't react simultaneously.
```
