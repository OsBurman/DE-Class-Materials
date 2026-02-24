public class BreakContinueDemo {

    public static void main(String[] args) {

        // -------------------------------------------------------
        // Part 1 — break: Find the First Prime
        // -------------------------------------------------------
        System.out.println("=== Break: First Prime ===");
        int[] numbers = {4, 6, 9, 13, 18, 21, 25, 29};

        for (int num : numbers) {
            boolean isPrime = num > 1;                  // numbers <= 1 are not prime
            for (int d = 2; d <= Math.sqrt(num); d++) {
                if (num % d == 0) {
                    isPrime = false;
                    break;                              // no need to check further divisors
                }
            }
            if (isPrime) {
                System.out.println("First prime found: " + num);
                break;                                  // stop searching once the first is found
            }
        }

        System.out.println();

        // -------------------------------------------------------
        // Part 2 — continue: Print Only Odd Numbers
        // -------------------------------------------------------
        System.out.println("=== Continue: Odd Numbers 1-15 ===");

        for (int i = 1; i <= 15; i++) {
            if (i % 2 == 0) continue;                  // skip even numbers entirely
            System.out.print(i + " ");
        }
        System.out.println();                           // newline after the sequence

        System.out.println();

        // -------------------------------------------------------
        // Part 3 — Labeled Break: Search a 2D Array
        // -------------------------------------------------------
        System.out.println("=== Labeled Break: 2D Search ===");
        int[][] grid = {{3, 7, 2}, {8, 1, 9}, {4, 6, 5}};
        int target = 9;

        int foundRow = -1;
        int foundCol = -1;

        outer:                                          // label for the outer loop
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == target) {
                    foundRow = r;
                    foundCol = c;
                    break outer;                        // exits BOTH loops immediately
                }
            }
        }

        System.out.println("Found " + target + " at row " + foundRow + ", col " + foundCol);
    }
}
