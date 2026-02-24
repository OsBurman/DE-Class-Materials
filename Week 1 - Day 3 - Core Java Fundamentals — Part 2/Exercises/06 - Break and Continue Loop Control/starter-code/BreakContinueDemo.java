public class BreakContinueDemo {

    public static void main(String[] args) {

        // -------------------------------------------------------
        // Part 1 — break: Find the First Prime
        // -------------------------------------------------------
        System.out.println("=== Break: First Prime ===");
        int[] numbers = {4, 6, 9, 13, 18, 21, 25, 29};

        // TODO: Loop through 'numbers' with a for-each loop
        //       For each number, check whether it is prime
        //       Hint: use a boolean 'isPrime = true', then try dividing
        //             by integers from 2 up to Math.sqrt(num); if any divide
        //             evenly set isPrime = false and break the inner check
        //       If isPrime is true:
        //           Print "First prime found: [number]"
        //           break out of the outer loop

        System.out.println();

        // -------------------------------------------------------
        // Part 2 — continue: Print Only Odd Numbers
        // -------------------------------------------------------
        System.out.println("=== Continue: Odd Numbers 1-15 ===");

        // TODO: Write a for loop from i = 1 to i <= 15
        //       If i is even, use 'continue' to skip it
        //       Otherwise print i followed by a space (System.out.print(i + " "))
        // TODO: After the loop, print a blank line (System.out.println())

        System.out.println();

        // -------------------------------------------------------
        // Part 3 — Labeled Break: Search a 2D Array
        // -------------------------------------------------------
        System.out.println("=== Labeled Break: 2D Search ===");
        int[][] grid = {{3, 7, 2}, {8, 1, 9}, {4, 6, 5}};
        int target = 9;

        // TODO: Declare int variables 'foundRow' and 'foundCol', both initialized to -1
        // TODO: Write a labeled outer loop (label: outer)
        //       Outer loop: for (int r = 0; r < grid.length; r++)
        //           Inner loop: for (int c = 0; c < grid[r].length; c++)
        //               If grid[r][c] == target:
        //                   Set foundRow = r and foundCol = c
        //                   break outer;
        // TODO: Print "Found [target] at row [foundRow], col [foundCol]"
    }
}
