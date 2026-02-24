import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * DAY 9 — PART 2 | CompletableFuture
 * ─────────────────────────────────────────────────────────────────────────────
 * Future<T> has a problem: get() blocks the calling thread.
 * You can't chain "when this finishes, do that" without blocking.
 *
 * CompletableFuture solves this with a PIPELINE model:
 *   - Schedule async computation
 *   - Attach callbacks that run when the result is ready
 *   - Combine multiple futures
 *   - Handle errors without try/catch at every step
 *
 * Think of it as a stream pipeline, but for async work:
 *   supplyAsync → thenApply → thenApply → thenAccept
 *
 * KEY THREAD BEHAVIOUR:
 *   supplyAsync  → runs on ForkJoinPool.commonPool() (or supplied Executor)
 *   thenApply    → runs on same thread as previous stage (if already done) OR on FJ pool
 *   thenApplyAsync → always submits to ForkJoinPool / supplied Executor
 */
public class CompletableFutureDemo {

    // Simulated external service (slow database, HTTP call, etc.)
    static String fetchUserFromDB(int userId) {
        simulateLatency(150);
        return "User{id=" + userId + ", name=Alice, tier=PREMIUM}";
    }

    static List<String> fetchOrdersForUser(String user) {
        simulateLatency(100);
        return Arrays.asList("Order#101", "Order#102", "Order#103");
    }

    static String enrichOrderWithProduct(String order) {
        simulateLatency(50);
        return order + " [ProductInfo attached]";
    }

    static void simulateLatency(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws Exception {
        demonstrateSupplyAsyncAndRunAsync();
        demonstrateThenApplyChain();
        demonstrateThenAcceptAndThenRun();
        demonstrateThenCompose();
        demonstrateThenCombine();
        demonstrateAllOfAnyOf();
        demonstrateErrorHandling();
        demonstrateRealWorldPipeline();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — supplyAsync and runAsync
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSupplyAsyncAndRunAsync() throws Exception {
        System.out.println("=== supplyAsync and runAsync ===");

        // supplyAsync — runs a Supplier<T> on ForkJoinPool, returns CompletableFuture<T>
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Fetching data...");
            simulateLatency(100);
            return "data from remote service";
        });

        System.out.println("Main thread free while async task runs...");
        String result = future.get();   // blocks only when we actually need the result
        System.out.println("Got: " + result);

        // runAsync — Runnable, no return value, returns CompletableFuture<Void>
        CompletableFuture<Void> logTask = CompletableFuture.runAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Logging event asynchronously");
        });
        logTask.get();  // wait for it to complete

        // Custom executor — don't use default FJP for I/O-heavy work
        ExecutorService ioPool = Executors.newFixedThreadPool(4);
        CompletableFuture<String> ioFuture = CompletableFuture.supplyAsync(
                () -> "result from I/O task",
                ioPool
        );
        System.out.println("Custom executor: " + ioFuture.get());
        ioPool.shutdown();

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — thenApply: transform the result (like Stream.map)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateThenApplyChain() throws Exception {
        System.out.println("=== thenApply chain ===");

        // Each thenApply receives the previous result and returns a new result
        CompletableFuture<String> pipeline = CompletableFuture
                .supplyAsync(() -> "  hello world  ")           // String
                .thenApply(String::trim)                        // "hello world"
                .thenApply(String::toUpperCase)                 // "HELLO WORLD"
                .thenApply(s -> "Processed: " + s);            // "Processed: HELLO WORLD"

        System.out.println(pipeline.get());

        // thenApplyAsync — guarantees the transformation runs on a new thread
        CompletableFuture<Integer> lengthFuture = CompletableFuture
                .supplyAsync(() -> fetchUserFromDB(42))
                .thenApplyAsync(user -> {
                    System.out.println("[" + Thread.currentThread().getName() + "] Processing user");
                    return user.length();
                });
        System.out.println("User string length: " + lengthFuture.get());

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — thenAccept and thenRun
    // thenAccept — consume result, return CompletableFuture<Void>
    // thenRun    — run action after completion, no access to result
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateThenAcceptAndThenRun() throws Exception {
        System.out.println("=== thenAccept and thenRun ===");

        // thenAccept: you get the value but return nothing
        CompletableFuture<Void> acceptFuture = CompletableFuture
                .supplyAsync(() -> fetchUserFromDB(1))
                .thenAccept(user -> System.out.println("Sending welcome email to: " + user));
        acceptFuture.get();   // wait for email to be "sent"

        // thenRun: you don't get the value — just run an action after completion
        CompletableFuture<Void> runFuture = CompletableFuture
                .supplyAsync(() -> "some computation")
                .thenRun(() -> System.out.println("Computation finished — no result needed"));
        runFuture.get();

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — thenCompose: sequential dependent async operations (flatMap)
    // Problem with thenApply for async: returns CompletableFuture<CompletableFuture<T>>
    // thenCompose "flattens" this — you return a CF<T>, you get back a CF<T>
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateThenCompose() throws Exception {
        System.out.println("=== thenCompose (sequential async) ===");

        // BAD: using thenApply when you need to return a CompletableFuture
        // CompletableFuture<CompletableFuture<List<String>>> nested = ...  // confusing!

        // GOOD: thenCompose — step 2 starts after step 1 completes
        CompletableFuture<List<String>> ordersFuture = CompletableFuture
                .supplyAsync(() -> fetchUserFromDB(42))          // CF<String>
                .thenCompose(user ->                              // user is the String
                        CompletableFuture.supplyAsync(() -> fetchOrdersForUser(user)));  // CF<List>

        List<String> orders = ordersFuture.get();
        System.out.println("Orders: " + orders);
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — thenCombine: two INDEPENDENT futures, combine their results
    // Both run in parallel — thenCombine triggers when BOTH complete
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateThenCombine() throws Exception {
        System.out.println("=== thenCombine (parallel independent tasks) ===");

        long start = System.currentTimeMillis();

        // These two run in parallel
        CompletableFuture<String> userFuture = CompletableFuture
                .supplyAsync(() -> fetchUserFromDB(1));

        CompletableFuture<String> configFuture = CompletableFuture
                .supplyAsync(() -> {
                    simulateLatency(80);
                    return "Config{maxOrders=10, currency=USD}";
                });

        // Combine both results when both are done
        CompletableFuture<String> combined = userFuture.thenCombine(
                configFuture,
                (user, config) -> "User: " + user + " | Config: " + config
        );

        System.out.println(combined.get());
        System.out.println("Elapsed: " + (System.currentTimeMillis() - start) + "ms " +
                "(sequential would be ~230ms)");
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — allOf and anyOf
    // allOf — wait for ALL futures to complete
    // anyOf — proceed when ANY future completes (first wins)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateAllOfAnyOf() throws Exception {
        System.out.println("=== allOf and anyOf ===");

        // allOf — useful when you need ALL results before proceeding
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> { simulateLatency(100); return "Result-1"; });
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> { simulateLatency(150); return "Result-2"; });
        CompletableFuture<String> cf3 = CompletableFuture.supplyAsync(() -> { simulateLatency(50);  return "Result-3"; });

        CompletableFuture<Void> allDone = CompletableFuture.allOf(cf1, cf2, cf3);
        allDone.get();   // blocks until all three are done
        // allOf returns Void — get individual results from the original futures
        System.out.println("allOf results: " + cf1.get() + ", " + cf2.get() + ", " + cf3.get());

        // Convenient pattern: collect all results into a list
        List<CompletableFuture<String>> futures = Arrays.asList(
                CompletableFuture.supplyAsync(() -> "A"),
                CompletableFuture.supplyAsync(() -> "B"),
                CompletableFuture.supplyAsync(() -> "C")
        );
        CompletableFuture<List<String>> allResults = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<String> results = new ArrayList<>();
                    for (CompletableFuture<String> f : futures) results.add(f.join());
                    return results;
                });
        System.out.println("Collected: " + allResults.get());

        // anyOf — first to complete wins (useful for redundant calls / timeouts)
        CompletableFuture<String> fast = CompletableFuture.supplyAsync(() -> { simulateLatency(50);  return "fast server"; });
        CompletableFuture<String> slow = CompletableFuture.supplyAsync(() -> { simulateLatency(500); return "slow server"; });
        Object winner = CompletableFuture.anyOf(fast, slow).get();
        System.out.println("anyOf winner: " + winner);
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 — Error Handling
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateErrorHandling() throws Exception {
        System.out.println("=== Error Handling ===");

        // exceptionally — recover from failure with a default value
        CompletableFuture<String> withFallback = CompletableFuture
                .supplyAsync(() -> {
                    throw new RuntimeException("Service unavailable");
                })
                .exceptionally(ex -> {
                    System.out.println("Error caught: " + ex.getMessage());
                    return "default value";  // recovery
                });
        System.out.println("exceptionally: " + withFallback.get());

        // handle — processes BOTH success and failure (like a merged try-catch)
        // BiFunction<result, exception> — one of them will be null
        CompletableFuture<String> handled = CompletableFuture
                .supplyAsync(() -> {
                    if (Math.random() > 0.5) throw new RuntimeException("Random failure");
                    return "success result";
                })
                .handle((result, ex) -> {
                    if (ex != null) {
                        System.out.println("handle: caught exception — " + ex.getMessage());
                        return "handled fallback";
                    }
                    return "handle: " + result;
                });
        System.out.println(handled.get());

        // whenComplete — side effect on either success or failure (does not transform result)
        CompletableFuture<String> withLogging = CompletableFuture
                .supplyAsync(() -> "operation result")
                .whenComplete((result, ex) -> {
                    if (ex != null) System.out.println("AUDIT: operation failed — " + ex.getMessage());
                    else            System.out.println("AUDIT: operation succeeded — " + result);
                });
        withLogging.get();

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 — Real-World Pipeline
    // Scenario: HTTP request handler — fetch user, fetch orders, enrich, format
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateRealWorldPipeline() throws Exception {
        System.out.println("=== Real-World Async Pipeline ===");

        long start = System.currentTimeMillis();

        CompletableFuture<String> response = CompletableFuture
                // Step 1: Fetch user from DB
                .supplyAsync(() -> fetchUserFromDB(42))

                // Step 2: Fetch orders for that user (depends on user)
                .thenCompose(user -> CompletableFuture.supplyAsync(() -> fetchOrdersForUser(user)))

                // Step 3: Enrich each order (run all enrichments in parallel)
                .thenCompose(orders -> {
                    List<CompletableFuture<String>> enriched = new ArrayList<>();
                    for (String order : orders) {
                        enriched.add(CompletableFuture.supplyAsync(() -> enrichOrderWithProduct(order)));
                    }
                    return CompletableFuture.allOf(enriched.toArray(new CompletableFuture[0]))
                            .thenApply(v -> {
                                List<String> results = new ArrayList<>();
                                for (CompletableFuture<String> f : enriched) results.add(f.join());
                                return results;
                            });
                })

                // Step 4: Format the final response
                .thenApply(enrichedOrders -> {
                    StringBuilder sb = new StringBuilder("=== API Response ===\n");
                    for (String o : enrichedOrders) sb.append("  ").append(o).append("\n");
                    return sb.toString();
                })

                // Step 5: Always log, regardless of success/failure
                .whenComplete((result, ex) -> {
                    if (ex != null) System.out.println("Request failed: " + ex.getMessage());
                    else            System.out.println("Request completed in " +
                            (System.currentTimeMillis() - start) + "ms");
                });

        System.out.println(response.get());
    }
}
