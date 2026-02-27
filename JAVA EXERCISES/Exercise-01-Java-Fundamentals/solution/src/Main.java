import java.util.Scanner;

/**
 * Exercise 01 — Java Fundamentals  (SOLUTION)
 * Personal Finance Tracker
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Personal Finance Tracker ===");

        // --- TODO 1 solution ---
        double[] expenses   = new double[10];
        String[] categories = new String[10];
        int count = 0;

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your monthly income: $");
        double income = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        System.out.println("\nEnter expenses (type 'done' as category to finish):");

        // --- TODO 2 solution: do-while loop ---
        do {
            System.out.print("Category (Food/Rent/Transport/Entertainment/Other): ");
            String cat = scanner.nextLine().trim();

            if (cat.equalsIgnoreCase("done")) break;

            System.out.print("Amount: $");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            expenses[count]   = amount;
            categories[count] = cat;
            count++;
        } while (count < 10);

        // --- TODO 3 solution: compute totals ---
        double total       = 0.0;
        double maxAmount   = 0.0;
        String maxCategory = "N/A";

        for (int i = 0; i < count; i++) {
            total += expenses[i];
            if (expenses[i] > maxAmount) {
                maxAmount   = expenses[i];
                maxCategory = categories[i];
            }
        }
        double average = (count > 0) ? total / count : 0.0;

        // --- TODO 4 solution: enhanced switch for emoji labels ---
        for (int i = 0; i < count; i++) {
            categories[i] = switch (categories[i].toLowerCase()) {
                case "food"          -> "🍔 Food";
                case "rent"          -> "🏠 Rent";
                case "transport"     -> "🚗 Transport";
                case "entertainment" -> "🎬 Entertainment";
                default              -> "📦 Other";
            };
        }
        // Update maxCategory label too
        maxCategory = switch (maxCategory.toLowerCase()) {
            case "food"          -> "🍔 Food";
            case "rent"          -> "🏠 Rent";
            case "transport"     -> "🚗 Transport";
            case "entertainment" -> "🎬 Entertainment";
            default              -> "📦 Other";
        };

        // --- TODO 5 solution: budget status ---
        double remaining = income - total;
        String status = (remaining >= 0) ? "UNDER BUDGET ✓" : "OVER BUDGET ✗";

        // --- TODO 6 solution: formatted report ---
        System.out.println("\n======== MONTHLY SUMMARY ========");
        System.out.printf("%-17s %s%n",      "Name:",            name);
        System.out.printf("%-17s $%,.2f%n",  "Monthly Income:",  income);
        System.out.printf("%-17s $%,.2f%n",  "Total Expenses:",  total);
        System.out.printf("%-17s $%,.2f%n",  "Remaining:",       remaining);
        System.out.printf("%-17s $%,.2f (%s)%n", "Highest Expense:", maxAmount, maxCategory);
        System.out.printf("%-17s $%,.2f%n",  "Average Expense:", average);
        System.out.printf("%-17s %s%n",      "Budget Status:",   status);
        System.out.println("=================================");

        scanner.close();
    }
}
