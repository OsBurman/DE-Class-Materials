public class DeadlockDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Deadlock demonstration ──────────────────────────────────
        System.out.println("=== Deadlock Demonstration ===");

        final Object lockA = new Object();
        final Object lockB = new Object();

        // Thread-1: acquires lockA, then tries lockB — opposite order from Thread-2
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("[Thread-1] acquired LockA");
                try { Thread.sleep(50); } catch (InterruptedException e) {
                    System.out.println("[Thread-1] interrupted before acquiring LockB");
                    return;
                }
                try {
                    synchronized (lockB) {             // waits here — Thread-2 holds lockB
                        System.out.println("[Thread-1] acquired LockB");
                    }
                } catch (Exception e) {
                    System.out.println("[Thread-1] interrupted while waiting for LockB");
                }
            }
        }, "Thread-1");

        // Thread-2: acquires lockB, then tries lockA — opposite order from Thread-1 → DEADLOCK
        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("[Thread-2] acquired LockB");
                try { Thread.sleep(50); } catch (InterruptedException e) {
                    System.out.println("[Thread-2] interrupted before acquiring LockA");
                    return;
                }
                try {
                    synchronized (lockA) {             // waits here — Thread-1 holds lockA
                        System.out.println("[Thread-2] acquired LockA");
                    }
                } catch (Exception e) {
                    System.out.println("[Thread-2] interrupted while waiting for LockA");
                }
            }
        }, "Thread-2");

        // Watchdog: interrupts deadlocked threads after 500ms to prevent infinite hang
        Thread watchdog = new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            System.out.println("Watchdog: detected deadlock — interrupting threads");
            t1.interrupt();
            t2.interrupt();
        });
        watchdog.setDaemon(true); // daemon so it won't prevent JVM exit

        t1.start();
        t2.start();
        watchdog.start();
        t1.join(2000);  // wait up to 2s for each thread to finish after interruption
        t2.join(2000);

        // ── Part 2: Fixed — both threads acquire locks in the SAME order ─────
        System.out.println("\n=== Deadlock Fixed (Consistent Lock Ordering) ===");

        // Both threads: lockA first, then lockB — no circular dependency possible
        Thread fixed1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("[Thread-1] acquired LockA");
                synchronized (lockB) {
                    System.out.println("[Thread-1] acquired LockB");
                }
            }
            System.out.println("[Thread-1] completed successfully");
        }, "Thread-1");

        Thread fixed2 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("[Thread-2] acquired LockA");
                synchronized (lockB) {
                    System.out.println("[Thread-2] acquired LockB");
                }
            }
            System.out.println("[Thread-2] completed successfully");
        }, "Thread-2");

        fixed1.start();
        fixed2.start();
        fixed1.join();
        fixed2.join();

        // ── Part 3: Livelock explanation ─────────────────────────────────────
        System.out.println("\n=== What is Livelock? ===");
        System.out.println("Livelock: threads are NOT blocked — they are actively running.");
        System.out.println("However, they keep reacting to each other and making no real progress.");
        System.out.println("Example: two threads each detect the other is waiting and yield,");
        System.out.println("         but both yield at the same time, endlessly.");
        System.out.println("Unlike deadlock (frozen), livelock consumes CPU but accomplishes nothing.");
        System.out.println("Fix: introduce randomized back-off delays so threads don't react simultaneously.");
    }
}
