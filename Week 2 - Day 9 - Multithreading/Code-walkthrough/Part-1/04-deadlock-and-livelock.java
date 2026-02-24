/**
 * DAY 9 — PART 1 | Deadlock and Livelock
 * ─────────────────────────────────────────────────────────────────────────────
 * DEADLOCK: Two or more threads are permanently blocked, each waiting for a
 * lock held by the other. Nobody ever makes progress.
 *
 * FOUR CONDITIONS REQUIRED FOR DEADLOCK (Coffman Conditions):
 *   1. Mutual Exclusion   — resources can only be held by one thread
 *   2. Hold and Wait      — a thread holds a resource while waiting for another
 *   3. No Preemption      — held resources cannot be forcibly taken away
 *   4. Circular Wait      — T1 waits for T2's lock; T2 waits for T1's lock
 *
 * LIVELOCK: Threads are NOT blocked, but they continuously react to each other
 * without making progress. Like two people in a hallway stepping aside for each
 * other forever — polite, but still stuck.
 *
 * STARVATION: A thread is perpetually denied CPU time because higher-priority
 * threads always run first. Not covered in code here — addressed conceptually.
 */
public class DeadlockAndLivelock {

    public static void main(String[] args) throws InterruptedException {
        demonstrateDeadlock();
        System.out.println("─────────────────────────────────────────────────");
        demonstrateDeadlockPrevention_LockOrdering();
        System.out.println("─────────────────────────────────────────────────");
        demonstrateDeadlockPrevention_TryLock();
        System.out.println("─────────────────────────────────────────────────");
        demonstrateLivelock();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Classic Deadlock
    // Thread A: holds Lock1, waits for Lock2
    // Thread B: holds Lock2, waits for Lock1
    // Result: neither can proceed — deadlock!
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateDeadlock() throws InterruptedException {
        System.out.println("=== Deadlock Demo ===");
        System.out.println("NOTE: This demo uses a 3-second timeout to detect deadlock.");
        System.out.println("      In production, deadlocks can be permanent.\n");

        final Object lockA = new Object();
        final Object lockB = new Object();

        Thread threadA = new Thread(() -> {
            System.out.println("Thread A: acquiring Lock A...");
            synchronized (lockA) {
                System.out.println("Thread A: has Lock A");
                try { Thread.sleep(100); } catch (InterruptedException e) { return; }

                System.out.println("Thread A: waiting for Lock B...");
                synchronized (lockB) {   // ← Thread A will NEVER get here
                    System.out.println("Thread A: has both locks — DONE");
                }
            }
        }, "Thread-A");

        Thread threadB = new Thread(() -> {
            System.out.println("Thread B: acquiring Lock B...");
            synchronized (lockB) {
                System.out.println("Thread B: has Lock B");
                try { Thread.sleep(100); } catch (InterruptedException e) { return; }

                System.out.println("Thread B: waiting for Lock A...");
                synchronized (lockA) {   // ← Thread B will NEVER get here
                    System.out.println("Thread B: has both locks — DONE");
                }
            }
        }, "Thread-B");

        threadA.start();
        threadB.start();

        // Wait a bit, then declare deadlock detected
        threadA.join(3000);
        threadB.join(3000);

        if (threadA.isAlive() || threadB.isAlive()) {
            System.out.println("⚠️  DEADLOCK DETECTED — interrupting threads");
            threadA.interrupt();
            threadB.interrupt();
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Prevention Strategy 1: Consistent Lock Ordering
    // Always acquire locks in the same order → breaks circular wait
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateDeadlockPrevention_LockOrdering() throws InterruptedException {
        System.out.println("=== Prevention: Consistent Lock Ordering ===");

        final Object lock1 = new Object();
        final Object lock2 = new Object();

        // Both threads acquire locks in the SAME order: lock1 then lock2
        // No circular wait → no deadlock possible
        Thread threadA = new Thread(() -> {
            synchronized (lock1) {           // Step 1: always lock1 first
                System.out.println("Thread A: has lock1");
                try { Thread.sleep(50); } catch (InterruptedException e) { return; }
                synchronized (lock2) {       // Step 2: then lock2
                    System.out.println("Thread A: has lock1 + lock2 — DONE");
                }
            }
        }, "Thread-A");

        Thread threadB = new Thread(() -> {
            synchronized (lock1) {           // Step 1: SAME order — lock1 first
                System.out.println("Thread B: has lock1");
                try { Thread.sleep(50); } catch (InterruptedException e) { return; }
                synchronized (lock2) {       // Step 2: then lock2
                    System.out.println("Thread B: has lock1 + lock2 — DONE");
                }
            }
        }, "Thread-B");

        threadA.start();
        threadB.start();
        threadA.join(2000);
        threadB.join(2000);

        System.out.println("Both threads finished without deadlock ✓");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Prevention Strategy 2: tryLock with timeout (ReentrantLock)
    // If we can't get the second lock within a timeout, back off and retry
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateDeadlockPrevention_TryLock() throws InterruptedException {
        System.out.println("=== Prevention: tryLock with Timeout ===");

        java.util.concurrent.locks.ReentrantLock lockA = new java.util.concurrent.locks.ReentrantLock();
        java.util.concurrent.locks.ReentrantLock lockB = new java.util.concurrent.locks.ReentrantLock();

        Thread threadA = new Thread(() -> {
            while (true) {
                try {
                    if (lockA.tryLock(50, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println("Thread A: has lockA, trying lockB...");
                            Thread.sleep(50);
                            if (lockB.tryLock(50, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                                try {
                                    System.out.println("Thread A: has both locks — DONE");
                                    return;  // success
                                } finally {
                                    lockB.unlock();
                                }
                            } else {
                                System.out.println("Thread A: couldn't get lockB — backing off");
                            }
                        } finally {
                            lockA.unlock();
                        }
                    }
                    Thread.sleep(10);  // back off before retrying
                } catch (InterruptedException e) {
                    return;
                }
            }
        }, "Thread-A");

        Thread threadB = new Thread(() -> {
            while (true) {
                try {
                    if (lockB.tryLock(50, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println("Thread B: has lockB, trying lockA...");
                            Thread.sleep(50);
                            if (lockA.tryLock(50, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                                try {
                                    System.out.println("Thread B: has both locks — DONE");
                                    return;  // success
                                } finally {
                                    lockA.unlock();
                                }
                            } else {
                                System.out.println("Thread B: couldn't get lockA — backing off");
                            }
                        } finally {
                            lockB.unlock();
                        }
                    }
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }, "Thread-B");

        threadA.start();
        threadB.start();
        threadA.join(5000);
        threadB.join(5000);
        System.out.println("Both threads completed (with possible retries) ✓");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Livelock
    // Threads keep reacting to each other — active but not making progress
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateLivelock() throws InterruptedException {
        System.out.println("=== Livelock Demo ===");

        // Two people in a hallway — each politely steps aside for the other
        HallwayWalker person1 = new HallwayWalker("Alice", true);
        HallwayWalker person2 = new HallwayWalker("Bob",   false);

        int[] steps = {0};  // limit the demo
        int maxSteps = 8;

        Thread t1 = new Thread(() -> {
            while (!person1.hasPassed() && steps[0] < maxSteps) {
                // Person 1: if moving right and person 2 is also moving right — step left
                if (person1.isMovingRight() && person2.isMovingRight()) {
                    System.out.println(person1.getName() + ": stepping LEFT to let " + person2.getName() + " pass");
                    person1.stepLeft();
                    steps[0]++;
                } else {
                    person1.setPassed(true);
                    System.out.println(person1.getName() + ": passed!");
                }
                try { Thread.sleep(30); } catch (InterruptedException e) { return; }
            }
        }, "Alice-Thread");

        Thread t2 = new Thread(() -> {
            while (!person2.hasPassed() && steps[0] < maxSteps) {
                // Person 2: if moving left and person 1 is also moving left — step right
                if (!person2.isMovingRight() && !person1.isMovingRight()) {
                    System.out.println(person2.getName() + ": stepping RIGHT to let " + person1.getName() + " pass");
                    person2.stepRight();
                    steps[0]++;
                } else {
                    person2.setPassed(true);
                    System.out.println(person2.getName() + ": passed!");
                }
                try { Thread.sleep(30); } catch (InterruptedException e) { return; }
            }
        }, "Bob-Thread");

        t1.start();
        t2.start();
        t1.join(2000);
        t2.join(2000);

        if (!person1.hasPassed() && !person2.hasPassed()) {
            System.out.println("⚠️  LIVELOCK: Both threads active but neither made progress!");
        }

        System.out.println();
        System.out.println("=== Deadlock vs Livelock Summary ===");
        System.out.println("Deadlock:  threads are BLOCKED (sleeping, not consuming CPU)");
        System.out.println("Livelock:  threads are RUNNING (consuming CPU) but not progressing");
        System.out.println("Starvation: one thread is perpetually denied resources (not shown here)");
    }

    static class HallwayWalker {
        private final String  name;
        private volatile boolean movingRight;
        private volatile boolean passed = false;

        HallwayWalker(String name, boolean movingRight) {
            this.name         = name;
            this.movingRight  = movingRight;
        }

        String  getName()        { return name; }
        boolean isMovingRight()  { return movingRight; }
        boolean hasPassed()      { return passed; }
        void    setPassed(boolean v) { passed = v; }
        void    stepLeft()       { movingRight = false; }
        void    stepRight()      { movingRight = true; }
    }
}
