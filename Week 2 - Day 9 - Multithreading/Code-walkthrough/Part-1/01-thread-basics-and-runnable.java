/**
 * DAY 9 — PART 1 | Thread Basics and the Runnable Interface
 * ─────────────────────────────────────────────────────────────────────────────
 * A Thread is the smallest unit of execution within a process.
 * Java supports multithreading — multiple threads executing concurrently
 * within the same JVM, sharing the same heap memory.
 *
 * TWO WAYS TO CREATE A THREAD:
 *   1. Extend Thread class    — override run()
 *   2. Implement Runnable     — implement run(), pass to Thread constructor
 *
 * Runnable is generally preferred:
 *   ✅ Doesn't waste the single inheritance slot
 *   ✅ Separates task (what to do) from execution mechanism (when/how)
 *   ✅ Works with ExecutorService (Day 9 Part 2)
 *   ✅ Lambdas can implement Runnable directly (it's a functional interface)
 */
public class ThreadBasicsAndRunnable {

    public static void main(String[] args) throws InterruptedException {
        demonstrateExtendingThread();
        demonstrateRunnable();
        demonstrateLambdaRunnable();
        demonstrateThreadMethods();
        demonstrateThreadInterruption();
        demonstrateDaemonThreads();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Approach 1: Extending Thread
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateExtendingThread() throws InterruptedException {
        System.out.println("=== Extending Thread ===");

        // Define the thread by extending Thread and overriding run()
        Thread counterThread = new CounterThread("Counter-A", 1, 5);

        System.out.println("Before start() — state: " + counterThread.getState());  // NEW
        counterThread.start();  // ← start() creates the OS thread and calls run() on it
        System.out.println("After start()  — state: " + counterThread.getState());  // RUNNABLE or TERMINATED

        // join() — main thread waits here until counterThread finishes
        counterThread.join();
        System.out.println("After join()   — state: " + counterThread.getState());  // TERMINATED
        System.out.println();
    }

    // Thread subclass
    static class CounterThread extends Thread {
        private final int start;
        private final int end;

        CounterThread(String name, int start, int end) {
            super(name);   // sets the thread name — helpful for debugging
            this.start = start;
            this.end   = end;
        }

        @Override
        public void run() {
            // This runs on the new thread — NOT on the main thread
            for (int i = start; i <= end; i++) {
                System.out.println(getName() + " → " + i);
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Approach 2: Implementing Runnable (preferred)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateRunnable() throws InterruptedException {
        System.out.println("=== Implementing Runnable ===");

        // The Runnable defines the TASK
        Runnable printTask = new PrintTask("Hello from Runnable", 3);

        // The Thread is the VEHICLE that runs the task
        Thread thread = new Thread(printTask, "Runnable-Thread");
        thread.start();
        thread.join();  // wait for it to finish before continuing

        // Run same task on multiple threads — task is reusable
        System.out.println("-- Running same task on 3 threads --");
        Thread t1 = new Thread(printTask, "Worker-1");
        Thread t2 = new Thread(printTask, "Worker-2");
        Thread t3 = new Thread(printTask, "Worker-3");

        t1.start(); t2.start(); t3.start();
        t1.join();  t2.join();  t3.join();

        System.out.println();
    }

    static class PrintTask implements Runnable {
        private final String message;
        private final int    times;

        PrintTask(String message, int times) {
            this.message = message;
            this.times   = times;
        }

        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
                System.out.println(Thread.currentThread().getName() + ": " + message);
                try { Thread.sleep(30); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Lambda as Runnable (Runnable is a functional interface)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateLambdaRunnable() throws InterruptedException {
        System.out.println("=== Lambda as Runnable ===");

        // Runnable has exactly one abstract method: run()
        // So a lambda that takes no args and returns void IS a Runnable
        Runnable greet = () -> System.out.println(
                Thread.currentThread().getName() + ": Hello from lambda!");

        Thread t1 = new Thread(greet, "Lambda-1");
        Thread t2 = new Thread(greet, "Lambda-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Inline lambda — even shorter
        new Thread(() -> {
            for (int i = 5; i >= 1; i--) {
                System.out.println(Thread.currentThread().getName() + " countdown: " + i);
                try { Thread.sleep(40); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "Countdown-Thread").start();

        Thread.sleep(300);  // give the countdown thread time to finish
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Key Thread Methods
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateThreadMethods() throws InterruptedException {
        System.out.println("=== Thread Methods ===");

        // ----- Thread.sleep(ms) — pause the current thread -----
        System.out.println("Before sleep");
        Thread.sleep(100);   // main thread sleeps 100ms
        System.out.println("After sleep (100ms pause)");

        // ----- Thread.currentThread() — get reference to the running thread -----
        System.out.println("Current thread: " + Thread.currentThread().getName());
        System.out.println("Thread ID:      " + Thread.currentThread().getId());

        // ----- getName, setName, getPriority, setPriority -----
        Thread worker = new Thread(() -> {
            System.out.println("Running as: " + Thread.currentThread().getName() +
                    " | Priority: " + Thread.currentThread().getPriority());
        });
        worker.setName("Named-Worker");
        worker.setPriority(Thread.MAX_PRIORITY);  // 10 (default is 5, min is 1)
        worker.start();
        worker.join();

        // ----- join(ms) — wait with timeout -----
        Thread slow = new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("Slow thread finished");
        }, "Slow-Thread");
        slow.start();
        slow.join(200);   // main thread waits at most 200ms
        System.out.println("join(200ms) timed out — slow thread state: " + slow.getState());
        slow.join();      // now wait fully
        System.out.println("slow thread done");

        // ----- isAlive() -----
        Thread quickThread = new Thread(() -> {}, "Quick");
        System.out.println("isAlive before start: " + quickThread.isAlive());  // false
        quickThread.start();
        quickThread.join();
        System.out.println("isAlive after join:   " + quickThread.isAlive());  // false

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Thread Interruption
    // Cooperative cancellation — the thread must check and handle the interrupt
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateThreadInterruption() throws InterruptedException {
        System.out.println("=== Thread Interruption ===");

        Thread worker = new Thread(() -> {
            System.out.println("Worker: starting long task");
            for (int i = 0; i < 20; i++) {
                // Check the interrupted flag cooperatively
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Worker: interrupt detected at step " + i + " — shutting down cleanly");
                    return;  // or break out and clean up
                }
                System.out.println("Worker: step " + i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // sleep() clears the interrupted flag when it throws
                    // Re-set it so callers can see the interrupt
                    Thread.currentThread().interrupt();
                    System.out.println("Worker: interrupted during sleep — stopping");
                    return;
                }
            }
        }, "Interruptible-Worker");

        worker.start();
        Thread.sleep(150);    // let it run for a bit
        worker.interrupt();   // request the thread to stop
        worker.join();
        System.out.println("Worker state after interrupt + join: " + worker.getState());

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — Daemon Threads
    // Daemon threads: background service threads that die when all user threads finish
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateDaemonThreads() throws InterruptedException {
        System.out.println("=== Daemon Threads ===");

        // Non-daemon (user) thread — JVM waits for it to finish
        Thread userThread = new Thread(() -> {
            System.out.println("User thread: running");
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("User thread: done");
        }, "User-Thread");

        // Daemon thread — JVM does NOT wait for it; it dies when user threads end
        Thread daemonThread = new Thread(() -> {
            while (true) {  // infinite loop — only stopped when JVM exits
                System.out.println("Daemon: background heartbeat");
                try { Thread.sleep(60); } catch (InterruptedException e) { return; }
            }
        }, "Daemon-Thread");
        daemonThread.setDaemon(true);   // ← MUST call before start()

        System.out.println("Is daemon: " + daemonThread.isDaemon());  // true

        userThread.start();
        daemonThread.start();
        userThread.join();   // wait for the user thread
        // daemon thread is still running but JVM will terminate it when main exits
        System.out.println("User thread done — daemon will be killed shortly");

        System.out.println();
    }
}
