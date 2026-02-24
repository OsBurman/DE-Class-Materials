import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.stream.Collectors;

public class CompletableFutureDemo {

    public static void main(String[] args) {

        // ── Part 1: supplyAsync + thenApply chain ───────────────────────────
        System.out.println("=== supplyAsync + thenApply ===");

        // TODO: Create a CompletableFuture<String> using supplyAsync(() -> "hello world")
        //       Chain thenApply(s -> s.toUpperCase())
        //       Chain thenApply(s -> "Result: " + s)
        //       Call join() and print the result


        // ── Part 2: thenCompose ─────────────────────────────────────────────
        System.out.println("\n=== thenCompose ===");

        // TODO: Create a CompletableFuture<Integer> via supplyAsync(() -> 42)
        //       Chain thenCompose(id -> CompletableFuture.supplyAsync(
        //           () -> "User-" + id + " profile loaded"))
        //       Call join() and print the result


        // ── Part 3: thenCombine ─────────────────────────────────────────────
        System.out.println("\n=== thenCombine ===");

        // TODO: Create futureA = supplyAsync(() -> "Price: $99")
        //       Create futureB = supplyAsync(() -> "Stock: 14 units")
        //       Combine with futureA.thenCombine(futureB, (a, b) -> a + " | " + b)
        //       Call join() and print the result


        // ── Part 4: allOf ───────────────────────────────────────────────────
        System.out.println("\n=== allOf ===");

        // TODO: Create a List<CompletableFuture<String>> of 4 futures,
        //       each returning "Task-N done" (N = 1-4)
        //       Use CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join()
        //       to wait for all, then iterate the list and print each result with join()


        // ── Part 5: exceptionally ───────────────────────────────────────────
        System.out.println("\n=== exceptionally ===");

        // TODO: Create a CompletableFuture via supplyAsync that throws:
        //       throw new RuntimeException("Simulated failure")
        //       Chain .exceptionally(ex -> "Recovered: " + ex.getMessage())
        //       Call join() and print the result


        // ── Part 6: handle ──────────────────────────────────────────────────
        System.out.println("\n=== handle ===");

        // TODO: Create a failure future via supplyAsync that throws:
        //       throw new RuntimeException("handle failure")
        //       Chain .handle((result, ex) -> ex != null ? "Error: " + ex.getMessage()
        //                                                 : "Success: " + result)
        //       Print "Failure path: " + result of join()

        // TODO: Create a success future via supplyAsync(() -> "all good")
        //       Apply the same .handle() logic
        //       Print "Success path: " + result of join()
    }
}
