/**
 * DAY 9 — PART 1 | Thread Lifecycle and States
 * ─────────────────────────────────────────────────────────────────────────────
 * Every Java thread is in exactly ONE of these states at any moment:
 *
 * ┌─────────────────┬────────────────────────────────────────────────────────┐
 * │ State           │ Meaning                                                │
 * ├─────────────────┼────────────────────────────────────────────────────────┤
 * │ NEW             │ Thread object created; start() not yet called          │
 * │ RUNNABLE        │ start() called; running or ready to run on CPU         │
 * │ BLOCKED         │ Waiting to acquire a monitor lock (synchronized block) │
 * │ WAITING         │ Waiting indefinitely (wait(), join() with no timeout)  │
 * │ TIMED_WAITING   │ Waiting with a timeout (sleep(ms), join(ms), wait(ms)) │
 * │ TERMINATED      │ run() has returned or threw an uncaught exception      │
 * └─────────────────┴────────────────────────────────────────────────────────┘
 *
 * STATE TRANSITIONS:
 *
 *   NEW ──start()──▶ RUNNABLE ◀──────────────────────────────────────┐
 *                      │   ▲                                          │
 *             acquire  │   │ lock        released                     │
 *             lock     ▼   │ released                                 │
 *                    BLOCKED                                          │
 *                                                                     │
 *             wait() / join()                                         │
 *                      │                                notify /      │
 *                      ▼               WAITING ───────join ends ─────┘
 *
 *             sleep(ms) / join(ms) / wait(ms)                         │
 *                      │                                timeout /     │
 *                      ▼         TIMED_WAITING ────────notify ───────┘
 *
 *                    run() ends ──▶ TERMINATED
 */
public class ThreadLifecycleAndStates {

    public static void main(String[] args) throws InterruptedException {
        demonstrateNewAndRunnable();
        demonstrateTimedWaiting();
        demonstrateWaiting();
        demonstrateBlocked();
        demonstrateTerminated();
        printStateTransitionSummary();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATE: NEW → RUNNABLE
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateNewAndRunnable() throws InterruptedException {
        System.out.println("=== NEW → RUNNABLE ===");

        Thread t = new Thread(() -> {
            // while this body executes, thread is RUNNABLE
            System.out.println("Inside thread — state from another thread's view: RUNNABLE");
            int sum = 0;
            for (int i = 0; i < 10_000; i++) sum += i;  // keep it busy briefly
        }, "Worker");

        System.out.println("State after new Thread():  " + t.getState());  // NEW
        t.start();
        // The thread may already have started — state is RUNNABLE (or possibly TERMINATED if very fast)
        System.out.println("State after start():       " + t.getState());  // RUNNABLE
        t.join();
        System.out.println("State after join():        " + t.getState());  // TERMINATED
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATE: TIMED_WAITING (Thread.sleep, Thread.join with timeout, Object.wait with timeout)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateTimedWaiting() throws InterruptedException {
        System.out.println("=== TIMED_WAITING ===");

        Thread sleeper = new Thread(() -> {
            try {
                System.out.println("Sleeper: going to sleep for 500ms");
                Thread.sleep(500);   // ← TIMED_WAITING here
                System.out.println("Sleeper: awake");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Sleeper");

        sleeper.start();
        Thread.sleep(50);   // give sleeper a moment to fall asleep
        System.out.println("Sleeper state while sleeping: " + sleeper.getState());  // TIMED_WAITING
        sleeper.join();
        System.out.println("Sleeper state after waking:   " + sleeper.getState());  // TERMINATED
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATE: WAITING (Object.wait(), Thread.join() with no timeout)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateWaiting() throws InterruptedException {
        System.out.println("=== WAITING ===");

        Object lock = new Object();

        Thread waiter = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("Waiter: acquired lock, calling wait()");
                    lock.wait();    // ← WAITING: releases lock, waits indefinitely for notify
                    System.out.println("Waiter: notified, resuming");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Waiter");

        waiter.start();
        Thread.sleep(80);   // give waiter time to reach wait()
        System.out.println("Waiter state while waiting: " + waiter.getState());  // WAITING

        // Notify the waiter to wake up
        synchronized (lock) {
            lock.notify();
            System.out.println("Notifier: called notify()");
        }
        waiter.join();
        System.out.println("Waiter state after notify: " + waiter.getState());  // TERMINATED
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATE: BLOCKED (waiting to acquire a synchronized lock held by another thread)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateBlocked() throws InterruptedException {
        System.out.println("=== BLOCKED ===");

        Object resource = new Object();

        // Thread 1 holds the lock for a long time
        Thread holder = new Thread(() -> {
            synchronized (resource) {
                System.out.println("Holder: acquired lock, holding for 400ms");
                try { Thread.sleep(400); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.println("Holder: releasing lock");
            }
        }, "Lock-Holder");

        // Thread 2 tries to acquire the same lock — will be BLOCKED
        Thread blocker = new Thread(() -> {
            System.out.println("Blocker: trying to acquire lock...");
            synchronized (resource) {  // ← BLOCKED here until holder releases
                System.out.println("Blocker: got the lock!");
            }
        }, "Lock-Blocker");

        holder.start();
        Thread.sleep(50);   // give holder time to grab the lock
        blocker.start();
        Thread.sleep(50);   // give blocker time to start and get stuck

        System.out.println("Holder state:  " + holder.getState());   // TIMED_WAITING (sleeping inside sync)
        System.out.println("Blocker state: " + blocker.getState());  // BLOCKED

        holder.join();
        blocker.join();
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATE: TERMINATED
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateTerminated() throws InterruptedException {
        System.out.println("=== TERMINATED ===");

        Thread t = new Thread(() -> System.out.println("Quick task done!"), "Quick");
        t.start();
        t.join();
        System.out.println("State: " + t.getState());  // TERMINATED

        // A TERMINATED thread cannot be restarted
        try {
            t.start();  // throws IllegalThreadStateException
        } catch (IllegalThreadStateException e) {
            System.out.println("Cannot restart a TERMINATED thread: " + e.getMessage());
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SUMMARY TABLE
    // ─────────────────────────────────────────────────────────────────────────
    static void printStateTransitionSummary() {
        System.out.println("=== State Transition Summary ===");
        System.out.println("""
            NEW            → RUNNABLE       : start() called
            RUNNABLE       → BLOCKED        : tries to enter synchronized block, lock held by another
            BLOCKED        → RUNNABLE       : lock becomes available
            RUNNABLE       → WAITING        : wait() or join() (no timeout)
            WAITING        → RUNNABLE       : notify() / notifyAll() / join() target finishes
            RUNNABLE       → TIMED_WAITING  : sleep(ms) / wait(ms) / join(ms)
            TIMED_WAITING  → RUNNABLE       : timeout expires OR notify() / join() target finishes
            RUNNABLE       → TERMINATED     : run() returns or throws uncaught exception
            """);
    }
}
