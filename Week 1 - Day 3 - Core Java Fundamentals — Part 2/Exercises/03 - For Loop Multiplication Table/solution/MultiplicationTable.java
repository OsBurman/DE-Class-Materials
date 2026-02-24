public class MultiplicationTable {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: Single for loop — multiples of 7
        // =====================================================================
        System.out.println("=== Multiples of 7 ===");

        // Standard for loop: init (i=1), condition (i<=10), update (i++)
        for (int i = 1; i <= 10; i++) {
            System.out.println("7 x " + i + " = " + (7 * i));
        }

        // =====================================================================
        // SECTION 2: Nested for loops — 5x5 multiplication table
        // =====================================================================
        System.out.println();
        System.out.println("=== 5x5 Multiplication Table ===");

        // Outer loop: one iteration per row
        for (int i = 1; i <= 5; i++) {
            // Inner loop: one iteration per column in the current row
            for (int j = 1; j <= 5; j++) {
                // %3d right-aligns the integer in a 3-character field for neat column alignment
                System.out.printf("%3d", i * j);
            }
            // After finishing all columns for this row, move to the next line
            System.out.println();
        }

        // =====================================================================
        // SECTION 3: Decrementing for loop — countdown
        // =====================================================================
        System.out.println();
        System.out.println("=== Countdown ===");

        // Decrementing loop: start at 10, stop when i < 1, subtract 1 each iteration
        for (int i = 10; i >= 1; i--) {
            System.out.print(i + " "); // print on the same line with a trailing space
        }
        System.out.println(); // newline after the countdown numbers
        System.out.println("Blast off!");
    }
}
