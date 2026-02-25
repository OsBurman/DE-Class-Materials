package com.academy;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;

/**
 * Day 9 Part 2 — Producer-Consumer, ExecutorService, CompletableFuture
 *
 * Theme: Async Order Processing System
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  Day 9 Part 2 — ExecutorService & CompletableFuture Demo    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        demoProducerConsumer();
        demoExecutorService();
        demoCompletableFuture();
    }

    // ─────────────────────────────────────────────────────────
    // 1. Producer-Consumer with BlockingQueue
    // ─────────────────────────────────────────────────────────
    static void demoProducerConsumer() throws InterruptedException {
        System.out.println("=== 1. Producer-Consumer Pattern ===");

        BlockingQueue<String> orderQueue = new LinkedBlockingQueue<>(5);
        AtomicBoolean done = new AtomicBoolean(false);

        // Producer thread
        Thread producer = new Thread(() -> {
            String[] orders = {"Order-1:Pizza", "Order-2:Burger", "Order-3:Sushi",
                               "Order-4:Pasta",  "Order-5:Salad"};
            for (String order : orders) {
                try {
                    orderQueue.put(order);
                    System.out.println("  [Producer] Queued: " + order);
                    Thread.sleep(50);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            done.set(true);
        }, "Producer");

        // Consumer thread
        Thread consumer = new Thread(() -> {
            while (!done.get() || !orderQueue.isEmpty()) {
                try {
                    String order = orderQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (order != null) System.out.println("  [Consumer] Processing: " + order);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "Consumer");

        producer.start(); consumer.start();
        producer.join(); consumer.join();
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 2. ExecutorService — Thread Pool
    // ─────────────────────────────────────────────────────────
    static void demoExecutorService() throws InterruptedException {
        System.out.println("=== 2. ExecutorService — Thread Pool ===");

        // Fixed thread pool of 3 workers
        ExecutorService executor = Executors.newFixedThreadPool(3);

        System.out.println("  Submitting 6 tasks to pool of 3 threads:");
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            Future<String> future = executor.submit(() -> {
                Thread.sleep(100);
                return "Task-" + taskId + " done by " + Thread.currentThread().getName();
            });
            futures.add(future);
        }

        for (Future<String> f : futures) {
            try { System.out.println("  → " + f.get()); }
            catch (ExecutionException e) { System.out.println("  Task failed: " + e.getMessage()); }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────
    // 3. CompletableFuture — Async Chaining
    // ─────────────────────────────────────────────────────────
    static void demoCompletableFuture() throws Exception {
        System.out.println("=== 3. CompletableFuture — Async Workflows ===");

        // Simple async task
        CompletableFuture<String> fetchUser = CompletableFuture.supplyAsync(() -> {
            sleep(100);
            return "user:Alice";
        });

        // Chain transformations
        CompletableFuture<String> enriched = fetchUser
            .thenApply(user -> user + " | dept:Engineering")       // transform
            .thenApply(user -> user + " | role:Senior Developer");  // transform again

        System.out.println("  Enriched: " + enriched.get());

        // Combine two futures
        CompletableFuture<String> getUserName = CompletableFuture.supplyAsync(() -> { sleep(80); return "Alice"; });
        CompletableFuture<Integer> getUserScore = CompletableFuture.supplyAsync(() -> { sleep(60); return 95; });

        CompletableFuture<String> combined = getUserName.thenCombine(getUserScore,
            (name, score) -> name + " scored " + score);
        System.out.println("  Combined: " + combined.get());

        // Run multiple in parallel and wait for all
        List<CompletableFuture<String>> tasks = List.of(
            CompletableFuture.supplyAsync(() -> { sleep(50); return "Result-A"; }),
            CompletableFuture.supplyAsync(() -> { sleep(80); return "Result-B"; }),
            CompletableFuture.supplyAsync(() -> { sleep(30); return "Result-C"; })
        );
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();
        List<String> results = tasks.stream().map(CompletableFuture::join).toList();
        System.out.println("  allOf results: " + results);

        // Error handling
        CompletableFuture<String> risky = CompletableFuture
            .supplyAsync(() -> { if (true) throw new RuntimeException("fetch failed"); return "ok"; })
            .exceptionally(ex -> "Fallback due to: " + ex.getMessage());
        System.out.println("  exceptionally: " + risky.get());

        System.out.println("\n✓ Multithreading Part 2 demo complete.");
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
