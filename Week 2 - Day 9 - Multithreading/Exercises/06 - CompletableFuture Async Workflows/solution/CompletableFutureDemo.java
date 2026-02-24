import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureDemo {

    public static void main(String[] args) {

        // ── Part 1: supplyAsync + thenApply chain ───────────────────────────
        System.out.println("=== supplyAsync + thenApply ===");

        // thenApply transforms the result synchronously (like Stream.map)
        String result1 = CompletableFuture
                .supplyAsync(() -> "hello world")
                .thenApply(String::toUpperCase)          // "HELLO WORLD"
                .thenApply(s -> "Result: " + s)          // "Result: HELLO WORLD"
                .join();                                  // block for final value
        System.out.println(result1);

        // ── Part 2: thenCompose ─────────────────────────────────────────────
        System.out.println("\n=== thenCompose ===");

        // thenCompose = flatMap for futures: the callback itself returns a CompletableFuture
        String result2 = CompletableFuture
                .supplyAsync(() -> 42)           // step 1: get user ID
                .thenCompose(id -> CompletableFuture.supplyAsync(
                        () -> "User-" + id + " profile loaded")) // step 2: fetch profile
                .join();
        System.out.println(result2);

        // ── Part 3: thenCombine ─────────────────────────────────────────────
        System.out.println("\n=== thenCombine ===");

        // thenCombine: two independent futures run in parallel; merge when both complete
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> "Price: $99");
        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(() -> "Stock: 14 units");

        String result3 = futureA
                .thenCombine(futureB, (a, b) -> a + " | " + b)
                .join();
        System.out.println(result3);

        // ── Part 4: allOf ───────────────────────────────────────────────────
        System.out.println("\n=== allOf ===");

        List<CompletableFuture<String>> tasks = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            final int n = i;
            tasks.add(CompletableFuture.supplyAsync(() -> "Task-" + n + " done"));
        }

        // allOf returns CompletableFuture<Void> — join() on it blocks until ALL complete
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<String> t : tasks) {
            System.out.println(t.join()); // each individual future is already done
        }

        // ── Part 5: exceptionally ───────────────────────────────────────────
        System.out.println("\n=== exceptionally ===");

        // exceptionally: fires only on failure; passes value through on success
        String result5 = CompletableFuture
                .supplyAsync(() -> { throw new RuntimeException("Simulated failure"); })
                .exceptionally(ex -> "Recovered: " + ex.getMessage())
                .join();
        System.out.println(result5);

        // ── Part 6: handle ──────────────────────────────────────────────────
        System.out.println("\n=== handle ===");

        // handle: receives both (result, exception) — exactly one will be non-null
        String failurePath = CompletableFuture
                .supplyAsync(() -> { throw new RuntimeException("handle failure"); })
                .handle((res, ex) -> ex != null ? "Error: " + ex.getMessage() : "Success: " + res)
                .join();
        System.out.println("Failure path: " + failurePath);

        String successPath = CompletableFuture
                .supplyAsync(() -> "all good")
                .handle((res, ex) -> ex != null ? "Error: " + ex.getMessage() : "Success: " + res)
                .join();
        System.out.println("Success path: " + successPath);
    }
}
