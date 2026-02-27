import java.util.Arrays;

/**
 * Exercise 02 — Methods & Arrays
 * Statistics Calculator
 *
 * All methods are stubs — fill in each TODO to make the program work.
 */
public class Main {

    // ----------------------------------------------------------------
    // TODO 1: Implement average(int[] scores)
    // Sum all elements, divide by length. Return 0.0 if empty.
    // ----------------------------------------------------------------
    public static double average(int[] scores) {
        return 0.0; // your code here
    }

    // ----------------------------------------------------------------
    // TODO 2: Implement median(int[] scores)
    // 1. Copy the array (Arrays.copyOf) so you don't mutate the original
    // 2. Sort the copy (Arrays.sort)
    // 3. If even length: average of two middle elements
    // If odd length: middle element
    // ----------------------------------------------------------------
    public static double median(int[] scores) {
        return 0.0; // your code here
    }

    // ----------------------------------------------------------------
    // TODO 3: Implement mode(int[] scores)
    // Return the value that appears most often.
    // Tie-break: return the smaller value.
    // Hint: sort a copy, then iterate counting consecutive equal elements.
    // ----------------------------------------------------------------
    public static int mode(int[] scores) {
        return 0; // your code here
    }

    // ----------------------------------------------------------------
    // TODO 4: Implement standardDeviation(int[] scores)
    // Formula: sqrt( sum((x - mean)^2) / n )
    // Use Math.sqrt() and Math.pow()
    // ----------------------------------------------------------------
    public static double standardDeviation(int[] scores) {
        return 0.0; // your code here
    }

    // ----------------------------------------------------------------
    // TODO 5: Implement letterGrade(double avg)
    // 90-100 → "A", 80-89 → "B", 70-79 → "C", 60-69 → "D", <60 → "F"
    // ----------------------------------------------------------------
    public static String letterGrade(double avg) {
        return "?"; // your code here
    }

    // ----------------------------------------------------------------
    // TODO 6: Overload average() to accept double[] instead of int[]
    // Same logic as TODO 1 but with doubles.
    // ----------------------------------------------------------------
    public static double average(double[] scores) {
        return 0.0; // your code here
    }

    // ----------------------------------------------------------------
    // TODO 7: Implement printGradeBook(String[] names, int[][] grades, String[]
    // subjects)
    // Print a table like:
    //
    // =========== GRADE BOOK ===========
    // Student Math Sci Eng Avg Grade
    // ------------------------------------------
    // Alice 91 88 94 91.0 A
    //
    // - grades[i][j] = student i's score in subject j
    // - Calculate each student's average across all subjects
    // - Print average and letter grade in the last two columns
    // ----------------------------------------------------------------
    public static void printGradeBook(String[] names, int[][] grades, String[] subjects) {
        // your code here
    }

    // ----------------------------------------------------------------
    // TODO 8: Implement topPerformer(String[] names, int[][] grades, int
    // subjectIndex)
    // Return the name of the student with the highest score in the given subject.
    // ----------------------------------------------------------------
    public static String topPerformer(String[] names, int[][] grades, int subjectIndex) {
        return ""; // your code here
    }

    // ----------------------------------------------------------------
    // Main — do NOT modify; run after implementing the TODOs above
    // ----------------------------------------------------------------
    public static void main(String[] args) {
        // Dataset
        String[] students = { "Alice", "Bob", "Carol", "Dave", "Eve" };
        String[] subjects = { "Math", "Science", "English" };

        int[][] grades = {
                { 91, 88, 94 }, // Alice
                { 85, 79, 82 }, // Bob
                { 72, 91, 68 }, // Carol
                { 60, 55, 73 }, // Dave
                { 95, 97, 89 } // Eve
        };

        // Extract Math scores for analysis
        int[] mathScores = new int[students.length];
        for (int i = 0; i < students.length; i++) {
            mathScores[i] = grades[i][0];
        }

        // --- Score Analysis ---
        System.out.println("===== Score Analysis: Math =====");
        System.out.println("Scores:  " + Arrays.toString(mathScores));
        System.out.printf("Mean:    %.2f%n", average(mathScores));
        System.out.printf("Median:  %.2f%n", median(mathScores));
        System.out.printf("Mode:    %d%n", mode(mathScores));
        System.out.printf("Min: %d   Max: %d   Range: %d%n",
                Arrays.stream(mathScores).min().getAsInt(),
                Arrays.stream(mathScores).max().getAsInt(),
                Arrays.stream(mathScores).max().getAsInt() - Arrays.stream(mathScores).min().getAsInt());
        System.out.printf("Std Dev: %.2f%n", standardDeviation(mathScores));

        // --- Grade book ---
        System.out.println();
        printGradeBook(students, grades, subjects);

        // --- Top performers ---
        System.out.println();
        for (int j = 0; j < subjects.length; j++) {
            System.out.printf("Top in %-8s → %s%n", subjects[j], topPerformer(students, grades, j));
        }
    }
}
