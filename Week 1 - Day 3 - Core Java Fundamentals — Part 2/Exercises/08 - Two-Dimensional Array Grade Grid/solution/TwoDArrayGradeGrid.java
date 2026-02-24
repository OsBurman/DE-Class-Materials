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

        for (int r = 0; r < grades.length; r++) {
            System.out.print("Row " + r + ": ");
            for (int c = 0; c < grades[r].length; c++) {
                System.out.printf("%-5d", grades[r][c]); // left-align in width-5 field
            }
            System.out.println();
        }

        System.out.println();

        // -------------------------------------------------------
        // Part 2 — Row Averages
        // -------------------------------------------------------
        System.out.println("=== Part 2: Row Averages ===");

        for (int r = 0; r < grades.length; r++) {
            int rowSum = 0;
            for (int c = 0; c < grades[r].length; c++) {
                rowSum += grades[r][c];
            }
            double avg = (double) rowSum / grades[r].length; // cast prevents integer division
            System.out.printf("Row %d average: %.2f%n", r, avg);
        }

        System.out.println();

        // -------------------------------------------------------
        // Part 3 — Find the Highest Score
        // -------------------------------------------------------
        System.out.println("=== Part 3: Highest Score ===");

        int highest = grades[0][0];     // seed with a real value, not 0
        int highRow  = 0;
        int highCol  = 0;

        for (int r = 0; r < grades.length; r++) {
            for (int c = 0; c < grades[r].length; c++) {
                if (grades[r][c] > highest) {
                    highest = grades[r][c];
                    highRow = r;
                    highCol = c;
                }
            }
        }

        System.out.println("Highest score: " + highest + " at row " + highRow + ", col " + highCol);

        System.out.println();

        // -------------------------------------------------------
        // Part 4 — Column Totals
        // -------------------------------------------------------
        System.out.println("=== Part 4: Column Totals ===");

        // Outer loop iterates columns; inner loop iterates rows
        for (int c = 0; c < grades[0].length; c++) {
            int colTotal = 0;
            for (int r = 0; r < grades.length; r++) {
                colTotal += grades[r][c];
            }
            System.out.println("Col " + c + " total: " + colTotal);
        }
    }
}
