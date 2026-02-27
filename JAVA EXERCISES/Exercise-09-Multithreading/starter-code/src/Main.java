import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * Exercise 09 — Multithreading
 * Concurrent Download Simulator — STARTER CODE
 */
public class Main {

    /** Simulates downloading a file (sleeps for a random duration). */
    static String simulateDownload(String filename) {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(200 + new Random().nextInt(400));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long ms = System.currentTimeMillis() - start;
        return filename + " downloaded in " + ms + "ms";
    }

    // ------------------------------------------------------------------
    // ProgressTracker — thread-safe counter
    // ------------------------------------------------------------------
    static class ProgressTracker {
        private int count = 0;

        // TODO 2a — make increment() synchronized so multiple threads
        // can safely call it at the same time
        void increment() {
            count++; // ← not thread-safe yet!
        }

        // TODO 2b — make getCount() synchronized
        int getCount() {
            return count;
        }
    }

    // ------------------------------------------------------------------
    // TODO 1 — DownloadTask implements Runnable
    // Fields: filename (String), tracker (ProgressTracker)
    // In run(): print "[thread-name] Starting: <filename>"
    // call simulateDownload(filename)
    // call tracker.increment()
    // print "[thread-name] Done: <filename> ✓"
    // ------------------------------------------------------------------
    static class DownloadTask /* TODO: implements Runnable */ {
        String filename;
        ProgressTracker tracker;

        DownloadTask(String filename, ProgressTracker tracker) {
            this.filename = filename;
            this.tracker = tracker;
        }

        // TODO: add public void run() method
    }

    // ------------------------------------------------------------------
    // TODO 3 — runSequential
    // Create one Thread per filename using DownloadTask.
    // Start all threads, then join() each one.
    // Print "Total downloads tracked: <count>" at the end.
    // ------------------------------------------------------------------
    static void runSequential(List<String> files) throws InterruptedException {
        System.out.println("\n=== Sequential Threads ===");
        ProgressTracker tracker = new ProgressTracker();
        // TODO: create threads, start them, join them
        System.out.println("Total downloads tracked: " + tracker.getCount());
    }

    // ------------------------------------------------------------------
    // TODO 4 — runWithExecutor
    // Create an ExecutorService with a fixed thread pool of `poolSize`.
    // Submit each DownloadTask to the executor.
    // Shutdown the executor and await termination (up to 30 seconds).
    // ------------------------------------------------------------------
    static void runWithExecutor(List<String> files, int poolSize) throws InterruptedException {
        System.out.println("\n=== ExecutorService (pool=" + poolSize + ") ===");
        ProgressTracker tracker = new ProgressTracker();
        // TODO: create ExecutorService, submit tasks, shutdown & awaitTermination
        System.out.println("Total downloads tracked: " + tracker.getCount());
    }

    // ------------------------------------------------------------------
    // TODO 5 — runWithCallable
    // For each filename, create a Callable<String> that calls simulateDownload.
    // Submit all Callables to a cached thread pool.
    // Use Future<String>.get() to collect and print each result.
    // Shutdown the executor.
    // ------------------------------------------------------------------
    static void runWithCallable(List<String> files) throws Exception {
        System.out.println("\n=== Callable & Future ===");
        // TODO: create Callables, submit, get() results, print them
    }

    // ------------------------------------------------------------------
    // TODO 6 — runWithCompletableFuture
    // For each filename, create a CompletableFuture using supplyAsync
    // that calls simulateDownload(filename).
    // Chain .thenApply(result -> result.toUpperCase())
    // Chain .thenAccept(result -> System.out.println(" CF: " + result))
    // Wait for ALL futures to complete with CompletableFuture.allOf(...).join()
    // ------------------------------------------------------------------
    static void runWithCompletableFuture(List<String> files) {
        System.out.println("\n=== CompletableFuture ===");
        // TODO: build CompletableFuture chain for each file, wait for all
    }

    // ------------------------------------------------------------------
    // TODO 7 — runWithLatch
    // Create a CountDownLatch(files.size()).
    // For each filename, start a thread that:
    // 1. calls simulateDownload(filename)
    // 2. prints the result
    // 3. calls latch.countDown()
    // Call latch.await() in the main thread.
    // Print "All <n> downloads complete. Latch released!" after await returns.
    // ------------------------------------------------------------------
    static void runWithLatch(List<String> files) throws InterruptedException {
        System.out.println("\n=== CountDownLatch ===");
        // TODO: create latch, spawn threads with countDown(), await()
    }

    // ------------------------------------------------------------------
    // Main — do not modify
    // ------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        List<String> files = List.of("report.pdf", "photo.jpg", "data.csv", "video.mp4");

        runSequential(files);
        runWithExecutor(files, 2);
        runWithCallable(files);
        runWithCompletableFuture(files);
        runWithLatch(files);
    }
}
