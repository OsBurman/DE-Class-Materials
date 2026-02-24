/**
 * Day 7 - Part 1: Exception Hierarchy & try-catch-finally
 *
 * EXCEPTION HIERARCHY (simplified Throwable tree):
 *
 *   java.lang.Object
 *       └── java.lang.Throwable
 *               ├── java.lang.Error                    ← JVM-level problems — do NOT catch
 *               │       ├── OutOfMemoryError
 *               │       ├── StackOverflowError
 *               │       └── VirtualMachineError
 *               └── java.lang.Exception                ← Application-level problems — handle these
 *                       ├── IOException                ← CHECKED  — must declare or catch
 *                       ├── SQLException               ← CHECKED
 *                       ├── ClassNotFoundException     ← CHECKED
 *                       └── RuntimeException           ← UNCHECKED — optional to catch
 *                               ├── NullPointerException
 *                               ├── ArrayIndexOutOfBoundsException
 *                               ├── IllegalArgumentException
 *                               ├── NumberFormatException
 *                               ├── ArithmeticException
 *                               └── ClassCastException
 *
 * CHECKED vs UNCHECKED:
 *   Checked   — The compiler FORCES you to handle them (IOException, SQLException, etc.)
 *               These represent "foreseeable" problems your code should gracefully recover from.
 *   Unchecked — RuntimeException and its subclasses. Compiler doesn't require handling.
 *               Usually represent programming bugs (null pointer, bad array index).
 *   Error     — JVM problems you generally cannot recover from. Never catch these.
 */

import java.io.FileNotFoundException;
import java.io.IOException;

public class ExceptionHierarchyAndTryCatch {

    // =========================================================================
    // SECTION 1 — Observing Common Exception Types
    // =========================================================================

    /**
     * Demonstrates that RuntimeExceptions are unchecked — no throws declaration needed.
     * These represent programming bugs we should fix, not just handle.
     */
    static void demonstrateUncheckedExceptions() {
        System.out.println("=== Unchecked Exceptions (RuntimeException subclasses) ===");

        // NullPointerException
        try {
            String name = null;
            System.out.println(name.length()); // NPE thrown here
        } catch (NullPointerException e) {
            System.out.println("Caught NPE: " + e.getClass().getSimpleName());
            System.out.println("  → Always check for null before calling methods on a reference.");
        }

        // ArrayIndexOutOfBoundsException
        try {
            int[] scores = {90, 85, 78};
            System.out.println(scores[5]); // index 5 doesn't exist
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught AIOOBE: index 5 is out of bounds for length 3");
        }

        // NumberFormatException
        try {
            int age = Integer.parseInt("twenty-five"); // not a valid integer string
        } catch (NumberFormatException e) {
            System.out.println("Caught NFE: \"twenty-five\" cannot be parsed as int");
        }

        // ArithmeticException (integer division by zero)
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("Caught ArithmeticException: " + e.getMessage());
        }

        // ClassCastException
        try {
            Object obj = "Hello";
            Integer num = (Integer) obj; // String cannot be cast to Integer
        } catch (ClassCastException e) {
            System.out.println("Caught ClassCastException: can't cast String to Integer");
        }

        System.out.println();
    }

    /**
     * Demonstrates checked exceptions — the compiler requires you declare or handle them.
     * IOException and its subclasses are the most common checked exceptions.
     */
    static void demonstrateCheckedExceptions() throws IOException {
        System.out.println("=== Checked Exceptions ===");

        // FileNotFoundException is a subclass of IOException — it IS-A checked exception.
        // We must either catch it or declare 'throws IOException' on the method signature.
        try {
            // We're deliberately using a path that doesn't exist to trigger FileNotFoundException
            java.io.FileReader fr = new java.io.FileReader("/tmp/does-not-exist.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Caught FileNotFoundException: " + e.getMessage());
            System.out.println("  → The compiler required us to handle this. We can't ignore it.");
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 2 — try-catch Basics: Single and Multiple Catch Blocks
    // =========================================================================

    /**
     * A simple bank account withdrawal that can throw multiple exceptions.
     * Shows how to order catch blocks — most specific first, most general last.
     */
    static void demonstrateTryCatchOrdering() {
        System.out.println("=== try-catch: Multiple Catch Blocks (most specific first) ===");

        String[] inputs = {"100", "not-a-number", null, "-50"};

        for (String input : inputs) {
            System.out.print("Input: \"" + input + "\" → ");
            try {
                // Step 1: this can throw NullPointerException if input is null
                String trimmed = input.trim();

                // Step 2: this can throw NumberFormatException if not numeric
                int amount = Integer.parseInt(trimmed);

                // Step 3: business logic check — negative amount isn't allowed
                if (amount < 0) {
                    throw new IllegalArgumentException("Withdrawal amount cannot be negative: " + amount);
                }

                System.out.println("Valid withdrawal: $" + amount);

            } catch (NullPointerException e) {
                // Most specific — this only catches NPE
                System.out.println("ERROR: input was null");
            } catch (NumberFormatException e) {
                // Catches number format problems
                System.out.println("ERROR: \"" + input + "\" is not a valid number");
            } catch (IllegalArgumentException e) {
                // Catches our business logic error
                System.out.println("ERROR: " + e.getMessage());
            }
            // ⚠️ If we put catch(Exception e) first, it would swallow ALL exceptions —
            //    the more specific catches below it would be unreachable (compile error).
        }
        System.out.println();
    }

    // =========================================================================
    // SECTION 3 — Multi-catch: One Handler for Multiple Exception Types
    // =========================================================================

    /**
     * Java 7+ allows catching multiple unrelated exception types in a single catch block.
     * Use this when you'd handle both exceptions the same way.
     */
    static void demonstrateMultiCatch() {
        System.out.println("=== Multi-catch (Java 7+): catch (TypeA | TypeB e) ===");

        String[] inputs = {"42", "abc", null};

        for (String input : inputs) {
            try {
                // Could throw NPE (null) or NumberFormatException (non-numeric)
                int value = Integer.parseInt(input.trim());
                System.out.println("Parsed: " + value);
            } catch (NullPointerException | NumberFormatException e) {
                // Handle both with the same logic — avoids code duplication
                System.out.println("Invalid input [" + e.getClass().getSimpleName() + "]: " + input);
            }
        }
        System.out.println();
    }

    // =========================================================================
    // SECTION 4 — finally: Code That Always Runs
    // =========================================================================

    /**
     * finally runs whether an exception is thrown or not.
     * The classic use case: releasing resources (DB connections, file handles, etc.)
     * NOTE: With try-with-resources (Part 2), finally is less needed for resources —
     *       but it's still useful for cleanup logic like closing dialogs, resetting state, etc.
     */
    static void demonstrateFinally() {
        System.out.println("=== finally: Always Executes ===");

        // Case 1: No exception — try runs to completion, then finally runs
        System.out.println("--- Case 1: No exception ---");
        try {
            System.out.println("  try: Opening database connection");
            System.out.println("  try: Executing query");
            System.out.println("  try: Success!");
        } catch (RuntimeException e) {
            System.out.println("  catch: This won't print");
        } finally {
            System.out.println("  finally: Closing database connection (ALWAYS runs)");
        }

        System.out.println();

        // Case 2: Exception thrown — catch runs, then finally runs
        System.out.println("--- Case 2: Exception thrown ---");
        try {
            System.out.println("  try: Opening database connection");
            System.out.println("  try: Executing query...");
            throw new RuntimeException("Connection timeout");
        } catch (RuntimeException e) {
            System.out.println("  catch: " + e.getMessage());
        } finally {
            System.out.println("  finally: Closing database connection (ALWAYS runs)");
        }

        System.out.println();

        // Case 3: finally and return — ⚠️ WATCH OUT
        System.out.println("--- Case 3: return inside try vs finally ---");
        System.out.println("  Result of getValueWithReturn(): " + getValueWithReturn());
        System.out.println();
    }

    /**
     * ⚠️ Classic gotcha: return in finally OVERRIDES return in try.
     * The try block attempts to return "from try" but finally's return wins.
     * In practice: avoid returning from finally — it suppresses exceptions too!
     */
    static String getValueWithReturn() {
        try {
            return "from try";   // this would be returned...
        } finally {
            return "from finally"; // ...but finally's return OVERRIDES it!
        }
    }

    // =========================================================================
    // SECTION 5 — Throwing Exceptions: throw vs throws
    // =========================================================================

    /**
     * 'throw'  = the statement that CREATES and FIRES an exception instance
     * 'throws' = the keyword in a method SIGNATURE that DECLARES checked exceptions
     *
     * This method validates a user's age for account registration.
     */
    static void validateAge(int age) {
        // throw: we actively create and throw an exception right here
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative: " + age);
        }
        if (age < 18) {
            throw new IllegalArgumentException("Must be at least 18 to register. Provided: " + age);
        }
        if (age > 150) {
            throw new IllegalArgumentException("Age " + age + " is unrealistically large.");
        }
        System.out.println("  Age " + age + " is valid.");
    }

    /**
     * processFile declares 'throws IOException' — it's a checked exception so the caller
     * must handle or re-declare it. The 'throws' declaration is a contract to callers.
     */
    static void processFile(String filename) throws IOException {
        if (filename == null || filename.isBlank()) {
            // We can also throw checked exceptions manually
            throw new IOException("Filename cannot be null or empty");
        }
        System.out.println("  Processing file: " + filename);
        // Imagine real file operations here...
    }

    static void demonstrateThrowingExceptions() {
        System.out.println("=== throw vs throws ===");

        // Testing validateAge — unchecked, no try-catch required (but good practice)
        int[] testAges = {25, -5, 15, 200};
        for (int age : testAges) {
            try {
                System.out.print("  validateAge(" + age + "): ");
                validateAge(age);
            } catch (IllegalArgumentException e) {
                System.out.println("REJECTED — " + e.getMessage());
            }
        }
        System.out.println();

        // Testing processFile — checked IOException, must catch or re-throw
        String[] files = {"report.txt", "", null};
        for (String f : files) {
            try {
                processFile(f);
            } catch (IOException e) {
                System.out.println("  IOException: " + e.getMessage());
            }
        }
        System.out.println();
    }

    // =========================================================================
    // SECTION 6 — Re-throwing and Exception Chaining (wrapping cause)
    // =========================================================================

    /**
     * Sometimes you catch a low-level exception and want to re-throw it
     * as a higher-level exception that makes more sense to the caller.
     * Always chain the original as the 'cause' so the full stack trace is preserved.
     */
    static void loadUserFromDatabase(int userId) throws Exception {
        try {
            // Simulate a low-level SQL problem
            if (userId <= 0) {
                throw new RuntimeException("DB query failed: invalid user ID " + userId);
            }
            System.out.println("  Loaded user #" + userId + " from database.");
        } catch (RuntimeException e) {
            // Wrap the low-level exception in a higher-level one
            // Pass 'e' as the CAUSE so the original stack trace is preserved
            throw new Exception("Failed to load user with ID: " + userId, e);
        }
    }

    static void demonstrateExceptionChaining() {
        System.out.println("=== Exception Chaining (getCause) ===");

        int[] userIds = {42, -1};
        for (int id : userIds) {
            try {
                loadUserFromDatabase(id);
            } catch (Exception e) {
                System.out.println("  Caught high-level exception: " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("    Root cause: " + e.getCause().getMessage());
                }
            }
        }
        System.out.println();
    }

    // =========================================================================
    // SECTION 7 — Catching Exception and Throwable (when and why NOT to)
    // =========================================================================

    static void demonstrateBroadCatching() {
        System.out.println("=== Catching Exception (broad) — use sparingly ===");

        try {
            // Imagine this calls 5 different methods that could throw different things
            String result = Integer.parseInt("oops") + "";
            System.out.println(result);
        } catch (Exception e) {
            // ⚠️ This catches EVERYTHING including RuntimeException and all its subclasses.
            // Useful at application boundaries (e.g. main method, REST controllers) to log
            // and respond gracefully — but avoid it deep in business logic.
            System.out.println("  Caught broad Exception: [" + e.getClass().getSimpleName() + "] " + e.getMessage());
            System.out.println("  ⚠️  In real code, log this with a logger, don't just print.");
        }

        System.out.println();
        System.out.println("  ⚠️  NEVER catch Throwable or Error — you'd be catching OutOfMemoryError!");
        System.out.println("      The JVM is in an unknown state. There's nothing useful you can do.");
        System.out.println();
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║    Day 7 - Part 1: Exception Hierarchy & try-catch   ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        demonstrateUncheckedExceptions();
        demonstrateCheckedExceptions();
        demonstrateTryCatchOrdering();
        demonstrateMultiCatch();
        demonstrateFinally();
        demonstrateThrowingExceptions();
        demonstrateExceptionChaining();
        demonstrateBroadCatching();
    }
}
