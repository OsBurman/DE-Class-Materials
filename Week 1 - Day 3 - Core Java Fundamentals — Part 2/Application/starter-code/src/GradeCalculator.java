import java.util.Scanner;

/**
 * Calculates and reports grades for a set of scores.
 * Complete each TODO to implement the required logic.
 */
public class GradeCalculator {

    // TODO Task 1: calculateAverage(double[] scores)
    // Use a standard for loop (index-based) to sum scores, then divide by length.
    public double calculateAverage(double[] scores) {
        return 0.0; // replace with your implementation
    }


    // TODO Task 2: getLetterGrade(double average)
    // Use an if-else if-else chain for the grade ranges in instructions.md
    public String getLetterGrade(double average) {
        return ""; // replace with your implementation
    }


    // TODO Task 3: getGradeMessage(String letterGrade)
    // Use a switch statement. Include a default case.
    public String getGradeMessage(String letterGrade) {
        return ""; // replace with your implementation
    }


    // TODO Task 4: countPassing(double[] scores)
    // Use a while loop with an index variable (int i = 0; while (i < scores.length) {...})
    public int countPassing(double[] scores) {
        return 0; // replace with your implementation
    }


    // TODO Task 5: findHighestScore(double[] scores)
    // Use an enhanced for loop (for-each). Track the highest value seen so far.
    public double findHighestScore(double[] scores) {
        return 0.0; // replace with your implementation
    }


    // TODO Task 6: findFirstFailing(double[] scores)
    // Use a standard for loop with a break statement.
    // Return the INDEX of the first score below 60.0, or -1 if none found.
    public int findFirstFailing(double[] scores) {
        return -1; // replace with your implementation
    }


    // TODO Task 7: skipScoresBelow(double[] scores, double threshold)
    // Use a for loop with a continue statement to skip scores below the threshold.
    // Print each score that meets or exceeds the threshold.
    public void skipScoresBelow(double[] scores, double threshold) {
        // your implementation here
    }


    // TODO Task 8: buildGradeBook(String[] students, double[][] scores)
    // Use nested for loops to iterate the 2D array.
    // For each row (student), calculate the average and print their report.
    public void buildGradeBook(String[] students, double[][] scores) {
        // Hint: outer loop over students, inner loop over scores[i]
        // Reuse calculateAverage() by passing a single row
    }


    // TODO Task 9: doWhileMenuDemo()
    // Use a do-while loop. Read scores from the user until they enter -1.
    public void doWhileMenuDemo() {
        Scanner scanner = new Scanner(System.in);
        // Hint:
        // double input;
        // do {
        //     System.out.print("Enter a score (or -1 to quit): ");
        //     input = scanner.nextDouble();
        //     if (input != -1) { ... print letter grade ... }
        // } while (input != -1);
    }
}
