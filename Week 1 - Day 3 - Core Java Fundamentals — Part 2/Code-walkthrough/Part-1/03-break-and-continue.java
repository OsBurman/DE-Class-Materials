/**
 * 03-break-and-continue.java
 * Day 3 — Core Java Fundamentals Part 2
 *
 * Topics covered:
 *   - break statement (exit a loop early)
 *   - continue statement (skip current iteration)
 *   - break in switch (prevent fall-through)
 *   - Labeled break (exit specific outer loop from nested loops)
 *   - Labeled continue (skip to next iteration of outer loop)
 */
public class BreakAndContinue {

    public static void main(String[] args) {

        // =========================================================
        // SECTION 1: break — exit a loop immediately
        // =========================================================

        // break jumps OUT of the nearest enclosing loop (or switch)
        // Common use case: stop searching once you find what you need

        System.out.println("--- Linear Search with break ---");
        String[] products = {"Laptop", "Mouse", "Keyboard", "Monitor", "Headphones"};
        String target = "Keyboard";
        int foundIndex = -1;  // -1 = not found (convention)

        for (int i = 0; i < products.length; i++) {
            System.out.println("Checking: " + products[i]);
            if (products[i].equals(target)) {
                foundIndex = i;
                break;  // ← stop looping — no need to check further
            }
        }

        if (foundIndex != -1) {
            System.out.println("Found \"" + target + "\" at index " + foundIndex);
        } else {
            System.out.println("\"" + target + "\" not found.");
        }

        // =========================================================
        // SECTION 2: break in a while loop (sentinel / early exit)
        // =========================================================

        System.out.println("\n--- Processing Orders (break on error) ---");
        String[] orderStatuses = {"SHIPPED", "DELIVERED", "PENDING", "ERROR", "PROCESSING"};

        for (String status : orderStatuses) {
            if (status.equals("ERROR")) {
                System.out.println("Error encountered — halting order processing!");
                break;  // stop all further processing
            }
            System.out.println("Processing order with status: " + status);
        }

        // =========================================================
        // SECTION 3: continue — skip the rest of current iteration
        // =========================================================

        // continue jumps to the NEXT iteration of the loop
        // The loop does NOT exit — it just skips the current pass

        System.out.println("\n--- Print Only Passing Scores (continue) ---");
        int[] testScores = {45, 72, 38, 89, 61, 55, 94, 29, 70};

        for (int score : testScores) {
            if (score < 60) {
                continue;  // ← skip this score — do NOT print it
            }
            System.out.println("Passing score: " + score);
        }

        // =========================================================
        // SECTION 4: continue in a for loop — skip even numbers
        // =========================================================

        System.out.println("\n--- Odd Numbers 1-15 (continue skips evens) ---");
        for (int i = 1; i <= 15; i++) {
            if (i % 2 == 0) {
                continue;  // skip even numbers
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // =========================================================
        // SECTION 5: break vs continue — side by side comparison
        // =========================================================

        System.out.println("\n--- break vs continue comparison ---");

        System.out.print("break at 5:    ");
        for (int i = 1; i <= 10; i++) {
            if (i == 5) break;          // stops entirely at 5
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("continue at 5: ");
        for (int i = 1; i <= 10; i++) {
            if (i == 5) continue;       // skips 5 but keeps going
            System.out.print(i + " ");
        }
        System.out.println();

        // =========================================================
        // SECTION 6: Labeled break — exit a specific outer loop
        // =========================================================

        // Problem: in nested loops, plain break only exits the INNER loop
        // A label lets you break out of a specific (usually outer) loop

        // Label syntax:   LABEL_NAME: for (...) { ... break LABEL_NAME; ... }

        System.out.println("\n--- Find First Negative in 2D Grid (labeled break) ---");
        int[][] grid = {
            {  5,  3,  8 },
            {  2,  7,  4 },
            { 10, -1,  6 },  // -1 is here — row 2, col 1
            {  9,  0,  3 }
        };

        int negRow = -1, negCol = -1;

        outerLoop:                              // ← label applied to the outer for
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                System.out.println("Checking grid[" + row + "][" + col + "] = " + grid[row][col]);
                if (grid[row][col] < 0) {
                    negRow = row;
                    negCol = col;
                    break outerLoop;            // ← exits BOTH loops at once
                }
            }
        }

        if (negRow != -1) {
            System.out.println("First negative found at [" + negRow + "][" + negCol + "]");
        } else {
            System.out.println("No negatives found.");
        }

        // =========================================================
        // SECTION 7: Labeled continue — skip to next outer iteration
        // =========================================================

        // Labeled continue jumps to the UPDATE step of the labeled loop
        // Used to skip an entire inner-loop cycle from within the inner loop

        System.out.println("\n--- Skip Rows Containing Zero (labeled continue) ---");
        int[][] matrix = {
            { 1, 2, 3 },
            { 4, 0, 6 },    // contains 0 → skip whole row
            { 7, 8, 9 }
        };

        rowLoop:
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] == 0) {
                    System.out.println("Row " + row + " contains 0 — skipping entire row");
                    continue rowLoop;  // ← skip to next row
                }
            }
            // This line only runs if we didn't hit a 0
            System.out.println("Row " + row + " is complete: all values non-zero");
        }

        // =========================================================
        // SECTION 8: break in switch (quick reminder)
        // =========================================================

        // break inside switch prevents fall-through to the next case
        System.out.println("\n--- HTTP Status (switch with break) ---");
        int statusCode = 404;

        switch (statusCode) {
            case 200:
                System.out.println("200 OK — request succeeded");
                break;   // ← WITHOUT this break, it would also print 301's message
            case 301:
                System.out.println("301 Moved Permanently — resource relocated");
                break;
            case 404:
                System.out.println("404 Not Found — resource doesn't exist");
                break;
            case 500:
                System.out.println("500 Internal Server Error — server-side issue");
                break;
            default:
                System.out.println("Unknown status code: " + statusCode);
        }
    }
}
