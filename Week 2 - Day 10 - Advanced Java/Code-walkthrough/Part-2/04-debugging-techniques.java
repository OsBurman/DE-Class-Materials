import java.util.*;
import java.util.logging.*;

/**
 * DAY 10 — PART 2 | Debugging Techniques and Tools
 * ─────────────────────────────────────────────────────────────────────────────
 * Debugging is the process of finding and fixing defects in code.
 * Experienced developers spend ~50% of their time debugging.
 *
 * TOOLKIT:
 *   1. Defensive assertions & logging — prevent bugs from hiding
 *   2. Breakpoint debugging (IntelliJ/VS Code) — inspect live state
 *   3. Stack trace reading — trace the root cause
 *   4. Common bug patterns — know what to look for
 *   5. Unit tests as debugging tools — isolate and reproduce
 *   6. JVM diagnostic tools (jstack, jmap, VisualVM)
 *
 * DEBUGGING MINDSET:
 *   - Never guess. Form a hypothesis, then verify it.
 *   - Smallest reproducible example first.
 *   - Read the error message COMPLETELY before writing any code.
 *   - Work backwards from the symptom to the cause.
 */
public class DebuggingTechniques {

    // Java's built-in logger (production apps use SLF4J + Logback/Log4j2)
    private static final Logger LOG = Logger.getLogger(DebuggingTechniques.class.getName());

    public static void main(String[] args) {
        demonstrateLogging();
        demonstrateCommonBugs();
        demonstrateStackTraceReading();
        demonstrateAssertions();
        demonstrateDebuggingChecklist();
        demonstrateIntellijTips();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — Logging Best Practices
    // System.out.println is NOT suitable for production debugging:
    //   - No timestamp, no level, no thread info
    //   - Can't be turned off without redeployment
    //   - Gets lost in container/cloud log streams
    // Use SLF4J + Logback in Spring Boot projects.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateLogging() {
        System.out.println("=== Logging Best Practices ===");

        // ── java.util.logging (built-in, no deps) ─────────────────────────────
        Logger logger = Logger.getLogger("com.company.OrderService");

        // Log levels (severity order):
        // FINEST < FINER < FINE < CONFIG < INFO < WARNING < SEVERE
        logger.fine("Processing order — debug detail (usually filtered out)");
        logger.info("Order ORD-001 received from customer alice@example.com");
        logger.warning("Payment retry attempted — attempt 2 of 3");
        logger.severe("Payment gateway unreachable — order ORD-001 failed");

        // ── What SLF4J / Logback looks like (Spring Boot standard) ───────────
        System.out.println("\nSLF4J + Logback usage (used in Spring Boot):");
        System.out.println("  private static final Logger log = LoggerFactory.getLogger(OrderService.class);");
        System.out.println();
        System.out.println("  log.debug(\"Processing order: {}\", orderId);          // {} = placeholder");
        System.out.println("  log.info(\"Order {} placed by {}\", orderId, userId);");
        System.out.println("  log.warn(\"Retry attempt {} for order {}\", attempt, orderId);");
        System.out.println("  log.error(\"Payment failed for order {}\", orderId, exception);");

        // ── Logging anti-patterns ─────────────────────────────────────────────
        System.out.println("\nLogging anti-patterns:");
        System.out.println("  ❌ log.info(\"Value: \" + obj.toString());  // string concat even if INFO disabled");
        System.out.println("  ✅ log.info(\"Value: {}\", obj);            // lazy — toString() only if logged");
        System.out.println();
        System.out.println("  ❌ log.error(\"Error happened\");           // useless — no context");
        System.out.println("  ✅ log.error(\"Payment failed for orderId={}, amount={}\", id, amt, ex);");
        System.out.println();
        System.out.println("  ❌ catch (Exception e) { e.printStackTrace(); }     // not structured");
        System.out.println("  ✅ catch (Exception e) { log.error(\"msg\", e); }   // structured + stack trace\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — Common Bug Patterns
    // Know these cold — you will encounter every one of them.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateCommonBugs() {
        System.out.println("=== Common Bug Patterns ===");

        // BUG 1: NullPointerException (most common Java bug)
        System.out.println("-- Bug 1: NullPointerException --");
        try {
            String result = getUserName(null);
            System.out.println(result.toUpperCase());   // NPE if result is null
        } catch (NullPointerException e) {
            System.out.println("  NPE caught — check Java 17+ helpful NPE messages: " +
                    "they tell you WHICH variable was null");
        }

        // Fix: Optional or null check
        String safe = Optional.ofNullable(getUserName(null)).orElse("UNKNOWN");
        System.out.println("  Safe version: " + safe);

        // BUG 2: Off-by-one error
        System.out.println("\n-- Bug 2: Off-by-one --");
        int[] arr = {10, 20, 30, 40, 50};
        System.out.print("  Elements: ");
        for (int i = 0; i < arr.length; i++) {      // i < arr.length (correct)
            System.out.print(arr[i] + " ");
        }
        System.out.println();
        // BAD: i <= arr.length → ArrayIndexOutOfBoundsException on last iteration
        try {
            int sum = 0;
            for (int i = 0; i <= arr.length; i++) {  // i <= arr.length (BUG!)
                sum += arr[i];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  ArrayIndexOutOfBounds: classic off-by-one (i <= length should be i < length)");
        }

        // BUG 3: Integer division truncation
        System.out.println("\n-- Bug 3: Integer Division --");
        int total = 7, count = 2;
        double wrong = total / count;           // both ints → integer division → 3.0
        double correct = (double) total / count; // cast first → 3.5
        System.out.println("  int/int: " + wrong + "  (should be 3.5!)");
        System.out.println("  (double)int/int: " + correct);

        // BUG 4: String == comparison
        System.out.println("\n-- Bug 4: String == instead of .equals() --");
        String a = new String("hello");
        String b = new String("hello");
        System.out.println("  a == b:      " + (a == b));      // false — different objects
        System.out.println("  a.equals(b): " + a.equals(b));   // true  — same content

        // BUG 5: ConcurrentModificationException
        System.out.println("\n-- Bug 5: ConcurrentModificationException --");
        List<String> items = new ArrayList<>(Arrays.asList("apple", "banana", "cherry", "date"));
        try {
            for (String item : items) {        // iterator-based for-each
                if (item.startsWith("b")) {
                    items.remove(item);        // modifying list while iterating → CME!
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("  CME: modifying list while iterating — use removeIf() or iterator.remove()");
        }
        // Fix:
        items.removeIf(item -> item.startsWith("b"));  // safe
        System.out.println("  After removeIf: " + items);

        // BUG 6: Mutable default argument / shared state
        System.out.println("\n-- Bug 6: Unintended Object Sharing --");
        List<String> original = new ArrayList<>(Arrays.asList("A", "B", "C"));
        List<String> copy = original;           // NOT a copy — same reference
        copy.add("D");
        System.out.println("  original after 'copy'.add: " + original);  // "A, B, C, D"!
        // Fix: new ArrayList<>(original) or List.copyOf(original)
        System.out.println("  Use: new ArrayList<>(original) for a real copy\n");
    }

    static String getUserName(String userId) {
        if (userId == null) return null;   // intentional null return for demo
        return "user-" + userId;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — Reading Stack Traces
    // Stack trace = snapshot of call stack at the moment of exception.
    // Read from TOP (where it happened) to BOTTOM (where the call originated).
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateStackTraceReading() {
        System.out.println("=== Reading Stack Traces ===");

        // Deliberately cause a chained exception to show the full stack trace format
        try {
            serviceLayer();
        } catch (Exception e) {
            System.out.println("Exception caught. Annotated stack trace:\n");

            // Print the trace so we can explain each part
            StackTraceElement[] trace = e.getStackTrace();

            System.out.println("Exception type: " + e.getClass().getName());
            System.out.println("Message:        " + e.getMessage());
            System.out.println("Stack (top = where it happened, bottom = where call started):");
            int linesToShow = Math.min(trace.length, 5);
            for (int i = 0; i < linesToShow; i++) {
                System.out.printf("  [%d] %s%n", i, trace[i]);
            }
            System.out.println();
        }

        System.out.println("Stack trace reading tips:");
        System.out.println("  1. Read the FIRST line of the trace — that's where it blew up");
        System.out.println("  2. Scan for YOUR package (com.company.xxx) — skip library frames");
        System.out.println("  3. 'Caused by:' shows the ORIGINAL cause after wrapping");
        System.out.println("  4. The number after ':' is the LINE in the source file\n");
    }

    static void serviceLayer() throws Exception {
        try {
            repositoryLayer();
        } catch (Exception e) {
            throw new RuntimeException("Service failed: could not load order", e);  // wraps original
        }
    }

    static void repositoryLayer() {
        throw new IllegalStateException("Database connection timed out");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — Assertions
    // assert statement: throw AssertionError if condition is false.
    // Enabled with -ea JVM flag (disabled by default in production).
    // Use for: invariant checking, pre/post-condition validation in dev/test
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateAssertions() {
        System.out.println("=== Assertions ===");

        // Java assert keyword (must run with -ea flag to activate)
        int price = 99;
        assert price > 0 : "Price must be positive, got: " + price;  // AssertionError if false
        System.out.println("Assert passed: price=" + price);

        // Assertions are OFF by default in production
        // Better: throw IllegalArgumentException for runtime validation
        System.out.println("In production code — validate with explicit exceptions:");
        System.out.println("  if (price <= 0) throw new IllegalArgumentException(\"price must be > 0\");");
        System.out.println("  Objects.requireNonNull(order, \"order must not be null\");");
        System.out.println("  Objects.checkIndex(index, array.length);  // Java 9+\n");

        // Objects.requireNonNull — standard pre-condition check
        try {
            Objects.requireNonNull(null, "order must not be null");
        } catch (NullPointerException e) {
            System.out.println("requireNonNull threw NPE: " + e.getMessage() + "\n");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Debugging Checklist (conceptual)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateDebuggingChecklist() {
        System.out.println("=== Debugging Checklist ===");

        System.out.println("When you hit a bug, follow this process:\n");
        System.out.println("  1. READ the error message completely (don't skip to Google)");
        System.out.println("     → Exception type, message, line number");
        System.out.println();
        System.out.println("  2. REPRODUCE it reliably");
        System.out.println("     → Smallest possible input/scenario that triggers the bug");
        System.out.println("     → Write a failing test first (this is TDD in reverse)");
        System.out.println();
        System.out.println("  3. LOCATE the root cause (not just the symptom)");
        System.out.println("     → Read the FULL stack trace — find your code in it");
        System.out.println("     → Walk backwards: what data led to this state?");
        System.out.println();
        System.out.println("  4. FORM a hypothesis — then verify it with a breakpoint or log");
        System.out.println("     → 'I think X is null because Y' → prove it");
        System.out.println();
        System.out.println("  5. FIX, then confirm the fix doesn't break other things");
        System.out.println("     → Run all tests, not just the one you added");
        System.out.println();
        System.out.println("  6. UNDERSTAND why it was wrong (not just why the fix works)");
        System.out.println("     → You should be able to explain the root cause to a colleague\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — IntelliJ Debugger Tips
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateIntellijTips() {
        System.out.println("=== IntelliJ Debugger Tips ===");

        System.out.println("BREAKPOINTS:");
        System.out.println("  • Line breakpoint         — click in gutter, Shift+click to disable");
        System.out.println("  • Conditional breakpoint  — right-click → condition: e.g. orderId.equals(\"ORD-5\")");
        System.out.println("  • Exception breakpoint    — Run → View Breakpoints → Java Exception → NullPointerException");
        System.out.println("  • Method breakpoint       — breaks on method entry/exit");
        System.out.println("  • Watch breakpoint        — breaks when a field value changes");
        System.out.println();
        System.out.println("STEPPING:");
        System.out.println("  F8  — Step Over  (execute current line, don't enter called method)");
        System.out.println("  F7  — Step Into  (enter the called method)");
        System.out.println("  F9  — Resume     (continue to next breakpoint)");
        System.out.println("  Shift+F8 — Step Out (finish current method, return to caller)");
        System.out.println();
        System.out.println("EVALUATE EXPRESSIONS:");
        System.out.println("  Alt+F8 — evaluate any expression live at the breakpoint");
        System.out.println("  e.g.: items.stream().filter(i -> i.contains(\"error\")).count()");
        System.out.println();
        System.out.println("WATCHES:");
        System.out.println("  Add expressions to the watch panel — they update at each step");
        System.out.println("  Useful for: order.total, queue.size(), user.getPermissions()");
        System.out.println();
        System.out.println("HOT SWAP:");
        System.out.println("  While paused at breakpoint, modify method body → Build → changes apply");
        System.out.println("  No restart needed for small fixes (field declarations cannot be hot-swapped)");
        System.out.println();
        System.out.println("REMOTE DEBUGGING:");
        System.out.println("  JVM flag: -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005");
        System.out.println("  IntelliJ: Run → Edit Configurations → Remote JVM Debug → port 5005");
        System.out.println("  Connect to running container or server and debug as if it were local\n");

        // ── Practical demo: show a bug you'd catch with a breakpoint ──────────
        System.out.println("--- Practical Breakpoint Scenario ---");
        List<Order> orders = Arrays.asList(
                new Order("ORD-1", 100.0, true),
                new Order("ORD-2", 200.0, false),
                new Order("ORD-3", 50.0, true),
                new Order("ORD-4", 0.0, true)   // BUG: zero-value order passes filter
        );

        double total = calculateApprovedTotal(orders);
        System.out.println("Approved total: $" + total + "  ← should be $150, not $150 (ORD-4 is $0 but approved)");
        System.out.println("Set a conditional breakpoint on the filter line: amount == 0.0 && approved == true");
        System.out.println("This would pause execution exactly on the problematic order.\n");
    }

    static double calculateApprovedTotal(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.approved)           // BUG: should also filter o.amount > 0
                .mapToDouble(o -> o.amount)
                .sum();
    }

    static class Order {
        String id;
        double amount;
        boolean approved;
        Order(String id, double amount, boolean approved) {
            this.id = id; this.amount = amount; this.approved = approved;
        }
    }
}
