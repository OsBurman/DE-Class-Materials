public class TwoDArrayGradeGrid {

    public static void main(String[] args) {

        int[][] grades = {
            {85, 90, 78},
            {92, 88, 76},
            {70, 65, 80}
        };

        // -------------------------------------------------------
        // Part 1 — Print the Raw Grid
        // -------------------------------------------------------
        System.out.println("=== Part 1: Raw Grid ===");

        // TODO: Write nested for loops (outer: rows, inner: cols)
        //       At the start of each row print "Row [r]: "
        //       For each element use: System.out.printf("%-5d", grades[r][c])
        //       After each row, print a newline: System.out.println()

        System.out.println();

        // -------------------------------------------------------
        // Part 2 — Row Averages
        // -------------------------------------------------------
        System.out.println("=== Part 2: Row Averages ===");

        // TODO: For each row r:
        //           Declare int rowSum = 0
        //           Inner loop to add each grades[r][c] to rowSum
        //           Compute: double avg = (double) rowSum / grades[r].length
        //           Print using: System.out.printf("Row %d average: %.2f%n", r, avg)

        System.out.println();

        // -------------------------------------------------------
        // Part 3 — Find the Highest Score
        // -------------------------------------------------------
        System.out.println("=== Part 3: Highest Score ===");

        // TODO: Declare int highest = grades[0][0], int highRow = 0, int highCol = 0
        // TODO: Use nested loops to scan every element
        //       If grades[r][c] > highest: update highest, highRow, highCol
        // TODO: Print "Highest score: [highest] at row [highRow], col [highCol]"

        System.out.println();

        // -------------------------------------------------------
        // Part 4 — Column Totals
        // -------------------------------------------------------
        System.out.println("=== Part 4: Column Totals ===");

        // TODO: Outer loop over columns c (0 to grades[0].length - 1)
        //           Declare int colTotal = 0
        //           Inner loop over rows r (0 to grades.length - 1)
        //               Add grades[r][c] to colTotal
        //           Print "Col [c] total: [colTotal]"
    }
}
