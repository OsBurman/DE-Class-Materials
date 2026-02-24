# Exercise 01: Creating Threads with Thread Class and Runnable Interface

## Objective
Create and manage threads using the `Thread` class directly and the `Runnable` interface, and observe thread lifecycle states.

## Background
Java represents a thread of execution with `java.lang.Thread`. You can spawn a thread two ways: extend `Thread` and override `run()`, or implement `Runnable` and pass it to a `Thread` constructor. The second approach is preferred because it separates the task from the execution mechanism and allows the class to extend something else. Every thread moves through a lifecycle: **NEW → RUNNABLE → (BLOCKED/WAITING/TIMED_WAITING) → TERMINATED**.

## Requirements

1. **Thread subclass**: Create an inner class `CountdownThread extends Thread`. Its `run()` method should print `"[CountdownThread] T-minus N"` for N from 5 down to 1, sleeping 100 ms between each line.

2. **Runnable implementation**: Create an inner class `MessagePrinter implements Runnable`. Its `run()` method should accept a message prefix (stored as a constructor field) and print `"[prefix] Message N"` for N from 1 to 4, sleeping 80 ms between each line.

3. **Starting threads**: In `main()`:
   - Create and `start()` one `CountdownThread`
   - Create two `Thread` objects wrapping two `MessagePrinter` instances with different prefixes (`"Alpha"`, `"Beta"`)
   - Start both `MessagePrinter` threads

4. **Thread names**: Before starting any thread, set a custom name with `setName()`. Print each thread's name using `getName()`.

5. **join()**: After starting all three threads, call `join()` on each so `main` waits for them all to finish, then print `"All threads finished."`.

6. **Thread state observation**: Before calling `start()` on the `CountdownThread`, print its state (`thread.getState()`). After calling `start()`, print its state again immediately.

## Hints
- `Thread.sleep(ms)` throws `InterruptedException` — wrap it in a try-catch inside `run()`
- Calling `start()` does not immediately run `run()`; the JVM schedules the thread
- `thread.getState()` returns a `Thread.State` enum value — `NEW`, `RUNNABLE`, `TERMINATED`, etc.
- Thread output will interleave non-deterministically — that's expected and correct

## Expected Output

```
CountdownThread state before start: NEW
CountdownThread state after start: RUNNABLE
[CountdownThread] T-minus 5
[Alpha] Message 1
[Beta] Message 1
[CountdownThread] T-minus 4
[Alpha] Message 2
[Beta] Message 2
[CountdownThread] T-minus 3
[Alpha] Message 3
[Beta] Message 3
[CountdownThread] T-minus 2
[Alpha] Message 4
[Beta] Message 4
[CountdownThread] T-minus 1
All threads finished.
```

> Note: The exact interleaving of lines will vary each run because threads are scheduled by the OS. The exact ordering shown above is one possible outcome.
