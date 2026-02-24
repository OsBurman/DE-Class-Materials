/**
 * 02-loops.java
 * Day 3 — Core Java Fundamentals Part 2
 *
 * Topics covered:
 *   - Standard for loop
 *   - while loop
 *   - do-while loop
 *   - Enhanced for-each loop (arrays and collections)
 *   - Infinite loop awareness
 *   - Nested loops
 */
import java.util.List;
import java.util.ArrayList;

public class Loops {

    public static void main(String[] args) {

        // =========================================================
        // SECTION 1: Standard for loop
        // =========================================================

        // Syntax: for (initialization; condition; update)
        // Best used when you KNOW the number of iterations upfront

        System.out.println("--- Countdown ---");
        for (int i = 10; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println("Blast off!");

        // Typical use: iterating with an index
        System.out.println("\n--- Menu Items ---");
        String[] menuItems = {"Pizza", "Burger", "Pasta", "Salad", "Tacos"};

        for (int i = 0; i < menuItems.length; i++) {
            // i starts at 0, goes while i < length, increments by 1 each time
            System.out.println((i + 1) + ". " + menuItems[i]);
        }

        // Stepping by 2 — showing the update expression flexibility
        System.out.println("\n--- Even Numbers 0-20 ---");
        for (int i = 0; i <= 20; i += 2) {
            System.out.print(i + " ");
        }
        System.out.println();

        // =========================================================
        // SECTION 2: while loop
        // =========================================================

        // Syntax: while (condition) { ... }
        // Best used when the number of iterations is NOT known upfront
        // Condition is checked BEFORE the body runs — may run zero times

        System.out.println("\n--- ATM Withdrawal Simulation ---");
        double accountBalance = 500.00;
        double withdrawalAmount = 120.00;
        int withdrawalCount = 0;

        while (accountBalance >= withdrawalAmount) {
            accountBalance -= withdrawalAmount;
            withdrawalCount++;
            System.out.printf("Withdrawal #%d: $%.2f withdrawn | Balance: $%.2f%n",
                    withdrawalCount, withdrawalAmount, accountBalance);
        }
        System.out.println("Insufficient funds for another withdrawal.");

        // Input validation pattern (common while loop use case)
        System.out.println("\n--- Number Guessing (simulated) ---");
        int secretNumber = 42;
        int guess = 30;         // pretend the user is guessing
        int attempts = 0;

        while (guess != secretNumber) {
            attempts++;
            System.out.println("Attempt " + attempts + ": Guessed " + guess + " — wrong!");
            // Simulate user getting closer
            if (guess < secretNumber) {
                guess += 6;
            } else {
                guess -= 6;
            }
        }
        System.out.println("Correct! Found " + secretNumber + " in " + attempts + " attempts.");

        // =========================================================
        // SECTION 3: do-while loop
        // =========================================================

        // Syntax: do { ... } while (condition);
        // Body runs AT LEAST ONCE — condition checked AFTER body
        // Perfect for: menu-driven programs, retry logic

        System.out.println("\n--- Order Processing (do-while) ---");
        int ordersProcessed = 0;
        boolean moreOrders = true;

        do {
            ordersProcessed++;
            System.out.println("Processing order #" + ordersProcessed + "...");

            // Simulate: after 3 orders, no more to process
            if (ordersProcessed >= 3) {
                moreOrders = false;
            }
        } while (moreOrders);

        System.out.println("All orders processed. Total: " + ordersProcessed);

        // Contrast: do-while ALWAYS runs at least once, even if condition is false
        System.out.println("\n--- do-while runs at least once ---");
        int counter = 100;   // condition will be false immediately

        do {
            System.out.println("This runs once even though counter=" + counter + " is not < 10");
        } while (counter < 10);

        // =========================================================
        // SECTION 4: Enhanced for-each loop
        // =========================================================

        // Syntax: for (Type item : collection) { ... }
        // Cleaner, no index variable, no risk of off-by-one
        // Works with arrays and anything that implements Iterable (List, Set, etc.)
        // Limitation: cannot modify the collection, no access to the index

        System.out.println("\n--- Students List (for-each on array) ---");
        String[] students = {"Alice", "Bob", "Charlie", "Diana", "Eve"};

        for (String student : students) {
            System.out.println("Hello, " + student + "!");
        }

        // For-each on a List (ArrayList)
        System.out.println("\n--- Scores (for-each on List) ---");
        List<Integer> scores = new ArrayList<>();
        scores.add(85);
        scores.add(92);
        scores.add(76);
        scores.add(88);
        scores.add(95);

        int total = 0;
        for (int score : scores) {
            total += score;
            System.out.println("Score: " + score);
        }
        System.out.println("Total: " + total);
        System.out.printf("Average: %.1f%n", (double) total / scores.size());

        // =========================================================
        // SECTION 5: Nested loops
        // =========================================================

        // Inner loop runs COMPLETELY for each iteration of the outer loop

        System.out.println("\n--- Multiplication Table (nested for) ---");
        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 5; col++) {
                System.out.printf("%4d", row * col);  // %4d = right-aligned in 4 chars
            }
            System.out.println();  // newline after each row
        }

        // Nested while: processing a 2D data structure
        System.out.println("\n--- Classroom Seating (nested while) ---");
        int totalRows = 3;
        int totalSeats = 4;
        int currentRow = 1;

        while (currentRow <= totalRows) {
            int currentSeat = 1;
            while (currentSeat <= totalSeats) {
                System.out.print("R" + currentRow + "S" + currentSeat + " ");
                currentSeat++;
            }
            System.out.println();
            currentRow++;
        }

        // =========================================================
        // SECTION 6: Infinite loop awareness
        // =========================================================

        // An infinite loop runs forever — usually a bug, sometimes intentional
        // (e.g. server listening for requests)
        // Common causes: condition never becomes false, update step missing

        // ⚠️  DO NOT RUN — for illustration only
        /*
        while (true) {
            System.out.println("This never stops!");
            // Missing: no way to exit
        }

        for (int i = 0; i >= 0; i++) {  // i keeps growing — never < 0
            System.out.println("Also infinite: " + i);
        }
        */

        // Intentional controlled infinite loop — exits via break
        System.out.println("\n--- Server Loop (controlled infinite) ---");
        int requestsHandled = 0;

        while (true) {
            requestsHandled++;
            System.out.println("Handling request #" + requestsHandled);
            if (requestsHandled >= 3) {
                System.out.println("Shutting down server.");
                break;  // ← exits the loop (covered in depth in 03-break-and-continue.java)
            }
        }
    }
}
