public class GradeCalculator {

    public static void main(String[] args) {

        int score = 82;

        // Guard clause: reject invalid scores before any further processing
        if (score < 0 || score > 100) {
            System.out.println("Invalid score!");
            return; // exit main immediately
        }

        // Determine letter grade using else-if chain (highest range first)
        String grade;
        if (score >= 90) {
            grade = "A";
        } else if (score >= 80) {
            grade = "B";
        } else if (score >= 70) {
            grade = "C";
        } else if (score >= 60) {
            grade = "D";
        } else {
            grade = "F";
        }

        // Determine feedback message based on the grade already assigned
        String feedback;
        if (grade.equals("A")) {
            feedback = "Excellent work!";
        } else if (grade.equals("B")) {
            feedback = "Good job!";
        } else if (grade.equals("C")) {
            feedback = "Passing, but room to improve.";
        } else if (grade.equals("D")) {
            feedback = "At risk — seek help soon.";
        } else {
            feedback = "Did not pass. Please retake.";
        }

        System.out.println("Score : " + score);
        System.out.println("Grade : " + grade);
        System.out.println("Feedback: " + feedback);
    }
}
