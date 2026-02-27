import java.util.Arrays;

/**
 * Exercise 02 — Methods & Arrays (SOLUTION)
 * Statistics Calculator
 */
public class Main {

    public static double average(int[] scores) {
        if (scores.length == 0) return 0.0;
        int sum = 0;
        for (int s : scores) sum += s;
        return (double) sum / scores.length;
    }

    public static double median(int[] scores) {
        if (scores.length == 0) return 0.0;
        int[] copy = Arrays.copyOf(scores, scores.length);
        Arrays.sort(copy);
        int mid = copy.length / 2;
        return (copy.length % 2 == 0)
            ? (copy[mid - 1] + copy[mid]) / 2.0
            : copy[mid];
    }

    public static int mode(int[] scores) {
        int[] copy = Arrays.copyOf(scores, scores.length);
        Arrays.sort(copy);
        int bestVal = copy[0], bestCount = 1, curCount = 1;
        for (int i = 1; i < copy.length; i++) {
            if (copy[i] == copy[i - 1]) {
                curCount++;
                if (curCount > bestCount) {
                    bestCount = curCount;
                    bestVal   = copy[i];
                }
            } else {
                curCount = 1;
            }
        }
        return bestVal;
    }

    public static double standardDeviation(int[] scores) {
        if (scores.length == 0) return 0.0;
        double mean = average(scores);
        double sumSq = 0;
        for (int s : scores) sumSq += Math.pow(s - mean, 2);
        return Math.sqrt(sumSq / scores.length);
    }

    public static String letterGrade(double avg) {
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }

    // Overloaded version for double[]
    public static double average(double[] scores) {
        if (scores.length == 0) return 0.0;
        double sum = 0;
        for (double s : scores) sum += s;
        return sum / scores.length;
    }

    public static void printGradeBook(String[] names, int[][] grades, String[] subjects) {
        System.out.println("=========== GRADE BOOK ===========");
        // Header
        System.out.printf("%-14s", "Student");
        for (String sub : subjects) System.out.printf("%6s", sub);
        System.out.printf("%6s  %5s%n", "Avg", "Grade");
        System.out.println("-".repeat(46));
        // Rows
        for (int i = 0; i < names.length; i++) {
            System.out.printf("%-14s", names[i]);
            double sum = 0;
            for (int j = 0; j < subjects.length; j++) {
                System.out.printf("%6d", grades[i][j]);
                sum += grades[i][j];
            }
            double avg = sum / subjects.length;
            System.out.printf("%6.1f  %5s%n", avg, letterGrade(avg));
        }
    }

    public static String topPerformer(String[] names, int[][] grades, int subjectIndex) {
        int best = -1;
        String top = "";
        for (int i = 0; i < names.length; i++) {
            if (grades[i][subjectIndex] > best) {
                best = grades[i][subjectIndex];
                top  = names[i];
            }
        }
        return top;
    }

    public static void main(String[] args) {
        String[] students = {"Alice", "Bob", "Carol", "Dave", "Eve"};
        String[] subjects = {"Math", "Science", "English"};
        int[][] grades = {
            {91, 88, 94},
            {85, 79, 82},
            {72, 91, 68},
            {60, 55, 73},
            {95, 97, 89}
        };

        int[] mathScores = new int[students.length];
        for (int i = 0; i < students.length; i++) mathScores[i] = grades[i][0];

        System.out.println("===== Score Analysis: Math =====");
        System.out.println("Scores:  " + Arrays.toString(mathScores));
        System.out.printf("Mean:    %.2f%n",   average(mathScores));
        System.out.printf("Median:  %.2f%n",   median(mathScores));
        System.out.printf("Mode:    %d%n",     mode(mathScores));
        System.out.printf("Min: %d   Max: %d   Range: %d%n",
            Arrays.stream(mathScores).min().getAsInt(),
            Arrays.stream(mathScores).max().getAsInt(),
            Arrays.stream(mathScores).max().getAsInt() - Arrays.stream(mathScores).min().getAsInt());
        System.out.printf("Std Dev: %.2f%n",   standardDeviation(mathScores));

        System.out.println();
        printGradeBook(students, grades, subjects);

        System.out.println();
        for (int j = 0; j < subjects.length; j++) {
            System.out.printf("Top in %-8s → %s%n", subjects[j], topPerformer(students, grades, j));
        }
    }
}
