import java.util.concurrent.atomic.*;

/**
 * DAY 9 — PART 1 | Synchronization and Thread Safety
 * ─────────────────────────────────────────────────────────────────────────────
 * THREAD SAFETY: Code is thread-safe if it behaves correctly when multiple
 * threads access shared data concurrently — no corrupted state, no lost updates.
 *
 * THE PROBLEM: Race Condition
 * When multiple threads read-modify-write shared data without coordination,
 * the final result depends on the unpredictable scheduling order of threads.
 *
 * SOLUTIONS:
 * 1. synchronized method      — locks the entire method on the object's monitor
 * 2. synchronized block       — locks only a critical section (finer-grained)
 * 3. volatile keyword         — ensures visibility of changes across threads
 * 4. Atomic classes           — lock-free thread-safe operations on single variables
 */
public class SynchronizationAndThreadSafety {

    public static void main(String[] args) throws InterruptedException {
        demonstrateRaceCondition();
        demonstrateSynchronizedMethod();
        demonstrateSynchronizedBlock();
        demonstrateVolatile();
        demonstrateAtomicVariables();
        demonstrateWaitNotify();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — The Race Condition (the problem)
    // Multiple threads increment a shared counter — result should be 20,000
    // but without synchronization, we get less due to lost updates
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateRaceCondition() throws InterruptedException {
        System.out.println("=== Race Condition (UNSAFE) ===");

        UnsafeCounter unsafe = new UnsafeCounter();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) unsafe.increment();
        }, "T1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) unsafe.increment();
        }, "T2");

        t1.start(); t2.start();
        t1.join();  t2.join();

        // Expected: 20,000 — actual will be LESS (lost updates) almost every run
        System.out.println("Unsafe counter result (expected 20000): " + unsafe.getCount());
        System.out.println("⚠️  Result is non-deterministic — run it multiple times!");
        System.out.println();
    }

    // No synchronization — increment is NOT atomic (3 steps: read, add, write)
    static class UnsafeCounter {
        private int count = 0;

        void increment() { count++; }   // count++ is NOT atomic!
        int getCount()   { return count; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Synchronized Method (locks the whole method)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSynchronizedMethod() throws InterruptedException {
        System.out.println("=== Synchronized Method (SAFE) ===");

        SafeCounter safe = new SafeCounter();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) safe.increment();
        }, "T1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) safe.increment();
        }, "T2");

        t1.start(); t2.start();
        t1.join();  t2.join();

        System.out.println("Synchronized counter result (expected 20000): " + safe.getCount());
        System.out.println();
    }

    static class SafeCounter {
        private int count = 0;

        // synchronized means: only ONE thread can execute this at a time (per instance)
        synchronized void increment() { count++; }
        synchronized int getCount()   { return count; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Synchronized Block (finer-grained locking)
    // Lock only the critical section, not the whole method
    // Better performance when only PART of the method needs protection
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSynchronizedBlock() throws InterruptedException {
        System.out.println("=== Synchronized Block (SAFE, fine-grained) ===");

        BankAccount account = new BankAccount("Alice", 1000.0);

        // Two threads try to withdraw simultaneously
        Thread t1 = new Thread(() -> account.withdraw(300, "T1"), "T1");
        Thread t2 = new Thread(() -> account.withdraw(800, "T2"), "T2");

        t1.start(); t2.start();
        t1.join();  t2.join();

        System.out.println("Final balance: " + account.getBalance());
        System.out.println();
    }

    static class BankAccount {
        private final String owner;
        private double balance;
        private final Object lock = new Object();   // dedicated lock object

        BankAccount(String owner, double initialBalance) {
            this.owner   = owner;
            this.balance = initialBalance;
        }

        void withdraw(double amount, String requester) {
            // Non-critical work can happen outside the synchronized block
            System.out.println(requester + " requesting $" + amount + " from " + owner + "'s account");

            synchronized (lock) {   // ← only one thread in this block at a time
                if (balance >= amount) {
                    // simulate some processing time — without sync, both threads would pass the check
                    try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    balance -= amount;
                    System.out.println(requester + " withdrew $" + amount + " | New balance: $" + balance);
                } else {
                    System.out.println(requester + " REJECTED — insufficient funds ($" + balance + " < $" + amount + ")");
                }
            }   // lock released here
        }

        double getBalance() { return balance; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — volatile keyword
    // Guarantees VISIBILITY: a write to a volatile variable is immediately
    // visible to all other threads. Does NOT provide atomicity.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateVolatile() throws InterruptedException {
        System.out.println("=== volatile ===");

        // Without volatile, the JVM may cache 'running' in a register per thread
        // and the worker thread might never see the change from the main thread.
        VolatileFlag flag = new VolatileFlag();

        Thread worker = new Thread(() -> {
            System.out.println("Worker: waiting for signal...");
            while (flag.isRunning()) {
                // busy-wait — not recommended in real code, but illustrates the point
            }
            System.out.println("Worker: received stop signal");
        }, "Worker");

        worker.start();
        Thread.sleep(100);
        System.out.println("Main: setting running = false");
        flag.stop();        // worker will see this immediately due to volatile
        worker.join();

        System.out.println();
    }

    static class VolatileFlag {
        // Without volatile, the worker may spin forever (cached value)
        // With volatile, the write is flushed to main memory immediately
        private volatile boolean running = true;

        boolean isRunning() { return running; }
        void stop()         { running = false; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Atomic Variables (java.util.concurrent.atomic)
    // Lock-free, thread-safe operations using hardware CAS (Compare-And-Swap)
    // Faster than synchronized for single-variable operations
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateAtomicVariables() throws InterruptedException {
        System.out.println("=== Atomic Variables ===");

        AtomicInteger atomicCounter = new AtomicInteger(0);
        AtomicLong    requestCount  = new AtomicLong(0);
        AtomicBoolean flag          = new AtomicBoolean(false);

        // AtomicInteger — safe counter without synchronized
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) atomicCounter.incrementAndGet();
        }, "T1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) atomicCounter.incrementAndGet();
        }, "T2");
        t1.start(); t2.start();
        t1.join();  t2.join();

        System.out.println("AtomicInteger result (expected 20000): " + atomicCounter.get());

        // Useful atomic operations:
        System.out.println("getAndIncrement: " + atomicCounter.getAndIncrement());  // returns old value, increments
        System.out.println("incrementAndGet: " + atomicCounter.incrementAndGet());  // increments, returns new value
        System.out.println("addAndGet(5):    " + atomicCounter.addAndGet(5));       // add 5, return new value
        System.out.println("compareAndSet:   " + atomicCounter.compareAndSet(atomicCounter.get(), 99));  // CAS
        System.out.println("current value:   " + atomicCounter.get());              // 99

        // AtomicReference — for non-primitive objects
        AtomicReference<String> atomicRef = new AtomicReference<>("initial");
        System.out.println("AtomicReference before: " + atomicRef.get());
        atomicRef.compareAndSet("initial", "updated");   // only updates if current == "initial"
        System.out.println("AtomicReference after:  " + atomicRef.get());

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — wait() / notify() / notifyAll()
    // Low-level coordination between threads using a shared object's monitor
    // - wait()      : releases lock and suspends until notify/notifyAll
    // - notify()    : wakes ONE waiting thread
    // - notifyAll() : wakes ALL waiting threads
    // All three MUST be called from within a synchronized block/method
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateWaitNotify() throws InterruptedException {
        System.out.println("=== wait() / notify() / notifyAll() ===");

        MessageBox box = new MessageBox();

        Thread producer = new Thread(() -> {
            String[] messages = {"Hello", "How are you?", "Goodbye"};
            for (String msg : messages) {
                box.put(msg);
                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            box.put(null);  // signal end
        }, "Producer");

        Thread consumer = new Thread(() -> {
            String msg;
            while ((msg = box.take()) != null) {
                System.out.println("Consumer received: " + msg);
            }
            System.out.println("Consumer: got end-of-stream signal");
        }, "Consumer");

        consumer.start();
        producer.start();
        producer.join();
        consumer.join();

        System.out.println();
    }

    // Simple single-slot message box using wait/notify
    static class MessageBox {
        private String  message;
        private boolean hasMessage = false;

        synchronized void put(String msg) {
            while (hasMessage) {   // wait while box is full
                try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            message    = msg;
            hasMessage = true;
            System.out.println("Producer put: " + msg);
            notifyAll();   // wake up any waiting consumers
        }

        synchronized String take() {
            while (!hasMessage) {  // wait while box is empty
                try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            String result = message;
            message    = null;
            hasMessage = false;
            notifyAll();   // wake up any waiting producers
            return result;
        }
    }
}
