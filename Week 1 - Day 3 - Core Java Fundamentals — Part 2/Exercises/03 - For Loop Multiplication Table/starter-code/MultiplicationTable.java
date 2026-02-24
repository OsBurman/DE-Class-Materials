public class MultiplicationTable {

    public static void main(String[] args) {

        // =====================================================================
        // SECTION 1: Single for loop — multiples of 7
        // =====================================================================
        System.out.println("=== Multiples of 7 ===");

        // TODO: Write a for loop that iterates i from 1 to 10 (inclusive)
        //       Inside the loop, print: "7 x [i] = [7 * i]"

        // =====================================================================
        // SECTION 2: Nested for loops — 5x5 multiplication table
        // =====================================================================
        System.out.println();
        System.out.println("=== 5x5 Multiplication Table ===");

        // TODO: Write an outer for loop for rows (i from 1 to 5)
        //       Inside it, write an inner for loop for columns (j from 1 to 5)
        //       Inside the inner loop, print each product using System.out.printf("%3d", i * j)
        //       After the inner loop finishes (but inside the outer loop), print a newline: System.out.println()

        // =====================================================================
        // SECTION 3: Decrementing for loop — countdown
        // =====================================================================
        System.out.println();
        System.out.println("=== Countdown ===");

        // TODO: Write a for loop that starts at 10 and DECREMENTS down to 1
        //       Print each number followed by a space (no newline): System.out.print(i + " ")
        //       After the loop, print a newline and then "Blast off!"
    }
}
