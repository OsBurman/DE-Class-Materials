/**
 * 02-multidimensional-arrays.java
 * Day 3 — Core Java Fundamentals Part 2
 *
 * Topics covered:
 *   - 2D array declaration
 *   - 2D static initialization
 *   - 2D dynamic initialization
 *   - Accessing 2D elements with [row][col]
 *   - Nested loop iteration over 2D arrays
 *   - length and [row].length
 *   - 3D arrays (brief overview)
 *   - Jagged arrays (rows of different lengths)
 *   - Arrays.deepToString() for printing
 */
import java.util.Arrays;

public class MultidimensionalArrays {

    public static void main(String[] args) {

        // =========================================================
        // SECTION 1: 2D array declaration and static initialization
        // =========================================================

        // Think of a 2D array as a table: rows and columns
        // Syntax: type[][] name = { {row0}, {row1}, {row2} }

        int[][] seatingChart = {
            {101, 102, 103, 104},   // Row 0 — front seats
            {201, 202, 203, 204},   // Row 1 — middle seats
            {301, 302, 303, 304}    // Row 2 — back seats
        };

        // Access element at [row][col]
        System.out.println("Front-left seat:  " + seatingChart[0][0]);   // 101
        System.out.println("Middle-right seat:" + seatingChart[1][3]);   // 204
        System.out.println("Back-center seat: " + seatingChart[2][1]);   // 302

        // length gives number of ROWS
        System.out.println("Number of rows:    " + seatingChart.length);      // 3
        // [row].length gives number of COLUMNS in that row
        System.out.println("Seats per row:     " + seatingChart[0].length);   // 4

        // Arrays.deepToString prints full 2D array as a string
        System.out.println("Full chart: " + Arrays.deepToString(seatingChart));

        // =========================================================
        // SECTION 2: 2D dynamic initialization
        // =========================================================

        // Allocate space for a 4x3 grid of student grades
        // All values default to 0
        int[][] gradeGrid = new int[4][3];  // 4 students, 3 assignments each

        System.out.println("\n--- Grade grid (default 0s) ---");
        System.out.println(Arrays.deepToString(gradeGrid));

        // Populate with assignment scores
        // Student 0: 88, 91, 76
        // Student 1: 95, 87, 92
        // Student 2: 72, 68, 80
        // Student 3: 84, 89, 93

        int[][] gradeFilled = {
            {88, 91, 76},
            {95, 87, 92},
            {72, 68, 80},
            {84, 89, 93}
        };

        // =========================================================
        // SECTION 3: Nested loop iteration over 2D array
        // =========================================================

        System.out.println("\n--- Student Grade Report ---");
        System.out.println("Student | A1  | A2  | A3  | Avg");
        System.out.println("--------|-----|-----|-----|-----");

        for (int student = 0; student < gradeFilled.length; student++) {
            int rowTotal = 0;

            System.out.printf("  %6d |", student + 1);  // student number (1-based)

            for (int assignment = 0; assignment < gradeFilled[student].length; assignment++) {
                int grade = gradeFilled[student][assignment];
                rowTotal += grade;
                System.out.printf(" %-3d |", grade);
            }

            double avg = (double) rowTotal / gradeFilled[student].length;
            System.out.printf(" %.1f%n", avg);
        }

        // =========================================================
        // SECTION 4: Iterating with enhanced for-each (2D)
        // =========================================================

        System.out.println("\n--- For-each over 2D array ---");
        // Outer for-each gives each row (a 1D int[] array)
        // Inner for-each gives each element in that row
        for (int[] row : gradeFilled) {
            for (int grade : row) {
                System.out.printf("%4d", grade);
            }
            System.out.println();
        }

        // =========================================================
        // SECTION 5: Modifying a specific cell
        // =========================================================

        System.out.println("\n--- Updating a grade ---");
        System.out.println("Student 3, Assignment 1 before: " + gradeFilled[2][0]);  // 72
        gradeFilled[2][0] = 79;  // re-graded to 79
        System.out.println("Student 3, Assignment 1 after:  " + gradeFilled[2][0]);  // 79

        // =========================================================
        // SECTION 6: 3D array (brief overview)
        // =========================================================

        // A 3D array: [depth][row][col]
        // Example: 3 classrooms, each with 2 rows, 3 seats per row
        int[][][] classrooms = {
            { {1, 2, 3}, {4, 5, 6} },      // Classroom 0
            { {7, 8, 9}, {10, 11, 12} },    // Classroom 1
            { {13, 14, 15}, {16, 17, 18} }  // Classroom 2
        };

        System.out.println("\n--- 3D array example ---");
        System.out.println("Classroom 1, Row 0, Seat 2: " + classrooms[1][0][2]);  // 9
        System.out.println("Full 3D: " + Arrays.deepToString(classrooms));

        // =========================================================
        // SECTION 7: Jagged arrays (rows of different lengths)
        // =========================================================

        // In Java, each row of a 2D array can have a DIFFERENT length
        // This is NOT possible in many other languages
        // Useful for: triangular data, Pascal's triangle, schedule slots

        System.out.println("\n--- Jagged array (Pascal's Triangle, 5 rows) ---");

        int[][] pascal = new int[5][];   // 5 rows, column count unspecified

        for (int row = 0; row < pascal.length; row++) {
            pascal[row] = new int[row + 1];   // row 0 has 1 element, row 1 has 2, etc.
            pascal[row][0] = 1;               // first element of every row is 1
            pascal[row][row] = 1;             // last element of every row is 1

            // Fill the middle values using the formula: C(n,k) = C(n-1,k-1) + C(n-1,k)
            for (int col = 1; col < row; col++) {
                pascal[row][col] = pascal[row - 1][col - 1] + pascal[row - 1][col];
            }
        }

        // Print Pascal's triangle — notice each row has a different length
        for (int[] row : pascal) {
            // Pad with spaces so it looks like a triangle
            System.out.print("  ".repeat(pascal.length - row.length));
            for (int val : row) {
                System.out.printf("%4d", val);
            }
            System.out.println();
        }

        // Jagged: each row's length is different
        System.out.println("\nRow lengths: ");
        for (int i = 0; i < pascal.length; i++) {
            System.out.println("  pascal[" + i + "].length = " + pascal[i].length);
        }

        // =========================================================
        // SECTION 8: Practical example — flattening a 2D array
        // =========================================================

        System.out.println("\n--- Flatten 2D array to 1D ---");
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        // Count total elements
        int totalElements = 0;
        for (int[] row : matrix) {
            totalElements += row.length;
        }

        int[] flat = new int[totalElements];
        int idx = 0;

        for (int[] row : matrix) {
            for (int val : row) {
                flat[idx++] = val;
            }
        }

        System.out.println("Matrix: " + Arrays.deepToString(matrix));
        System.out.println("Flat:   " + Arrays.toString(flat));
    }
}
