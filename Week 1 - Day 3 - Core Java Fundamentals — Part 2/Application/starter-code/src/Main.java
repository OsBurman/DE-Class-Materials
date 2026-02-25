import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== Grade Calculator ===\n");

                eCalculator calc = new GradeC lator();
                   
                ample scores for testing 
                le[] scores = {95.0, 82.5, 7 61.0, 0, 43.0, 78.5};
                 
                ODO: Call each method and print esults // Example:
        // double avg = calc.calculateAverage(scores);
        // System.out.println("Average: " + avg);
        // System.out.println("Letter Grade: " + calc.getLetterGrade(avg));
        // System.out.println("Message: " + calc.getGradeMessage(calc.getLetterGrade(avg)));
        // System.out.println("Passing count: " + calc.countPassing(scores));
        // System.out.println("Highest score: " + calc.findHighestScore(scores));
        // int failIdx = calc.findFirstFailing(scores);
        // System.out.println("First failing index: " + failIdx);
        // System.out.println("Scores at or above 75:");
        // calc.skipScoresBelow(scores, 75.0);


        // TODO: Set up the 2D grade book and call buildGradeBook
        String[] students = {"Alice", "Bob", "Carol"};
        double[][] gradeBook = {
            {92.0, 88.0, 95.0, 90.0},   // Alice
        // 
            {75.0, 70.0, 68.0, 72.0},   // Bob
            {85.0, 91.0, 87.0, 89.0}    // Carol
        };
        // calc.buildGradeBook(students, gradeBook);


        // 
        // : Call doWhileMenuDemo() to launch the interactive menu
        // calc.doWhileMenuDemo();
    }
}
        // 

        // 