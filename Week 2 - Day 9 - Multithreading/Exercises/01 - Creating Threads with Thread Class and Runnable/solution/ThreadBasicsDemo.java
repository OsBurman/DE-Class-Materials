public class ThreadBasicsDemo {

    // ── Thread subclass ─────────────────────────────────────────────────────
    static class CountdownThread extends Thread {
        @Override
        public void run() {
            for (int i = 5; i >= 1; i--) {
                System.out.println("[CountdownThread] T-minus " + i);
                try {
                    Thread.sleep(100); // pause 100ms between prints
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore interrupted status
                }
            }
        }
    }

    // ── Runnable implementation ─────────────────────────────────────────────
    static class MessagePrinter implements Runnable {
        private final String prefix;

        MessagePrinter(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 4; i++) {
                System.out.println("[" + prefix + "] Message " + i);
                try {
                    Thread.sleep(80); // slightly faster than the countdown thread
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // ── Part 1: Thread subclass ─────────────────────────────────────────
        CountdownThread countdownThread = new CountdownThread();
        countdownThread.setName("CountdownThread");

        // getState() before start() returns NEW
        System.out.println("CountdownThread state before start: " + countdownThread.getState());
        countdownThread.start();
        // State transitions to RUNNABLE immediately after start()
        System.out.println("CountdownThread state after start: " + countdownThread.getState());

        // ── Part 2: Runnable threads ────────────────────────────────────────
        Thread alphaThread = new Thread(new MessagePrinter("Alpha"), "Alpha");
        Thread betaThread  = new Thread(new MessagePrinter("Beta"),  "Beta");
        alphaThread.start();
        betaThread.start();

        // ── Part 3: join() — wait for all three threads before proceeding ───
        countdownThread.join();
        alphaThread.join();
        betaThread.join();

        System.out.println("All threads finished.");
    }
}
