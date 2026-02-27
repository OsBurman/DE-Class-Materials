import java.util.Scanner;

/**
 * Exercise 01 — Java Fundamentals
 * Personal Finance Tracker
 *
 * Fill in each TODO to complete the application.
 * The program compiles as-is — your job is to add the logic.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Personal Finance Tracker ===");

        // TODO 1: Declare and initialize variables
        // - String name (read from scanner)
        // - double income (read from scanner)
        // - double[] expenses (max 10 entries)
        // - String[] categories (parallel array — same length as expenses)
        // - String[] catNames (names typed by the user)
        // - int count (how many expenses have been entered, starts at 0)
        String name = "";
        double income = 0.0;
        double[] expenses = new double[10];
        String[] categories = new String[10];
        int count = 0;

        System.out.print("Enter your name: ");
        // your code here

        System.out.print("Enter your monthly income: $");
        // your code here

        System.out.println("\nEnter expenses (type 'done' as category to finish):");

        // TODO 2: Use a do-while loop to read expenses until the user types "done".
        // Each iteration:
        // a) Print "Category (Food/Rent/Transport/Entertainment/Other): "
        // b) Read the category string
        // c) If category equals "done" (ignore case), break out of the loop
        // d) Print "Amount: $" and read the amount
        // e) Store the amount in expenses[count] and the category in categories[count]
        // f) Increment count
        // Stop early if count reaches 10.
        // your code here

        // TODO 3: Use a for loop (0 to count) to compute:
        // - double total (sum of all expenses)
        // - double maxAmount (highest single expense)
        // - String maxCategory (category of the highest expense)
        // - double average (total / count, or 0 if count == 0)
        double total = 0.0;
        double maxAmount = 0.0;
        String maxCategory = "N/A";
        double average = 0.0;
        // your code here

        // TODO 4: Use an enhanced switch to map a category name to a short label.
        // Hint: iterate categories[0..count], switch on the category string (ignore
        // case):
        // "food" → "🍔 Food"
        // "rent" → "🏠 Rent"
        // "transport" → "🚗 Transport"
        // "entertainment" → "🎬 Entertainment"
        // default → "📦 Other"
        // Store the result back in categories[i] so the report shows the emoji label.
        // your code here

        // TODO 5: Calculate remaining balance and determine budget status.
        // - double remaining = income - total
        // - String status:
        // if remaining >= 0 → "UNDER BUDGET ✓"
        // else → "OVER BUDGET ✗"
        double remaining = 0.0;
        String status = "";
        // your code here

        // TODO 6: Print the formatted summary report using System.out.printf.
        // Match this layout exactly:
        //
        // ======== MONTHLY SUMMARY ========
        // Name: Alice
        // Monthly Income: $3,500.00
        // Total Expenses: $2,100.00
        // Remaining: $1,400.00
        // Highest Expense: $1,200.00 (Rent)
        // Average Expense: $525.00
        // Budget Status: UNDER BUDGET ✓
        // =================================
        //
        // Hint: use printf("%-17s $%,.2f%n", "Label:", value)
        // your code here

        scanner.close();
    }
}
