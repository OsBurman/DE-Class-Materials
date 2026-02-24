// ============================================================
// FILE: 04-comments-and-documentation.java
// TOPIC: Comments and Code Documentation
// ============================================================

/**
 * This is a JAVADOC comment — it documents the class.
 * Javadoc comments use /** ... * / syntax and appear directly above the
 * class, method, or field they describe.
 *
 * <p>Javadoc is processed by the {@code javadoc} tool (part of the JDK) to
 * generate HTML documentation — the same format you see at
 * https://docs.oracle.com/en/java/javase/</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     StudentGradeCalculator calc = new StudentGradeCalculator();
 *     double avg = calc.calculateAverage(new int[]{85, 92, 78});
 * </pre>
 *
 * @author  Scott Burman
 * @version 1.0
 * @since   2026-02-22
 */
public class CommentsAndDocumentation {

    // ── TYPES OF COMMENTS ─────────────────────────────────────────────

    // This is a SINGLE-LINE comment — use for short inline explanations
    // Put it ABOVE the line it explains, or inline after the code

    /*
     * This is a MULTI-LINE comment.
     * Use it for longer explanations or for temporarily disabling
     * a block of code during debugging.
     * Unlike Javadoc, it does NOT appear in generated documentation.
     */

    /**
     * The passing threshold for this course — 70%.
     * Declared as a constant to avoid magic numbers in calculations.
     */
    static final double PASSING_THRESHOLD = 70.0;

    /**
     * Calculates the average score from an array of integer scores.
     *
     * <p>Returns 0.0 if the array is empty to avoid division by zero.</p>
     *
     * @param scores  an array of student scores (each 0–100)
     * @return        the average score as a double, or 0.0 if scores is empty
     */
    public static double calculateAverage(int[] scores) {
        if (scores == null || scores.length == 0) {
            return 0.0; // guard clause — handle edge case early
        }

        int total = 0;
        for (int score : scores) {
            total += score; // accumulate sum
        }

        return (double) total / scores.length; // cast to double BEFORE dividing
    }

    /**
     * Determines whether a student has passed based on their average score.
     *
     * @param average  the student's average score
     * @return         {@code true} if average >= passing threshold, {@code false} otherwise
     * @see            #PASSING_THRESHOLD
     */
    public static boolean hasPassed(double average) {
        return average >= PASSING_THRESHOLD;
    }

    /**
     * Converts a numeric score to a letter grade.
     *
     * <p>Grade scale:</p>
     * <ul>
     *   <li>90–100 → A</li>
     *   <li>80–89  → B</li>
     *   <li>70–79  → C</li>
     *   <li>60–69  → D</li>
     *   <li>0–59   → F</li>
     * </ul>
     *
     * @param score  numeric score (0–100)
     * @return       letter grade as a String
     * @throws       IllegalArgumentException if score is outside 0–100
     */
    public static String letterGrade(double score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100. Got: " + score);
        }
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    public static void main(String[] args) {

        // ── DEMONSTRATE THE DOCUMENTED METHODS ───────────────────────
        int[] mariaScores = {88, 92, 79, 95, 84};
        int[] bobScores   = {55, 62, 48, 71, 60};
        int[] emptyScores = {};

        double mariaAvg = calculateAverage(mariaScores);
        double bobAvg   = calculateAverage(bobScores);
        double emptyAvg = calculateAverage(emptyScores);

        System.out.println("=== Grade Report ===");
        System.out.printf("Maria → Average: %.1f | Grade: %s | Passed: %b%n",
                mariaAvg, letterGrade(mariaAvg), hasPassed(mariaAvg));
        System.out.printf("Bob   → Average: %.1f | Grade: %s | Passed: %b%n",
                bobAvg, letterGrade(bobAvg), hasPassed(bobAvg));
        System.out.printf("Empty → Average: %.1f%n", emptyAvg);

        System.out.println();

        // ── COMMENT BEST PRACTICES ────────────────────────────────────
        System.out.println("=== Comment Best Practices ===");

        // ✅ GOOD: explains WHY, not WHAT (code already shows what)
        // Using (double) cast here because both variables are int;
        // without the cast, Java performs integer division and truncates the decimal.
        int totalPoints = 450;
        int maxPoints   = 500;
        double percentage = (double) totalPoints / maxPoints * 100;
        System.out.println("Score: " + percentage + "%");

        // ❌ BAD: restates the code in English — adds no value
        // int x = 5; // set x to 5

        // ✅ GOOD: explains a non-obvious algorithm step
        // Multiply by 100 first to avoid losing precision from integer division
        // before converting to percentage string with one decimal place
        System.out.printf("Formatted: %.1f%%%n", percentage);

        // ✅ GOOD: TODO comments flag work that still needs to be done
        // TODO: add support for weighted scores (labs vs exams)
        // FIXME: edge case when all scores are zero causes unexpected grade
        // NOTE: this calculation assumes a 100-point scale

        System.out.println();

        // ── WHAT NOT TO DO ────────────────────────────────────────────
        // ❌ AVOID: commented-out code in production
        // int oldCalc = total * 2 / 100;   // ← delete this, don't comment it out
        // Use Git to track old versions — that's what version control is for.

        // ❌ AVOID: misleading comments — worse than no comment at all
        // int total = 0; // initialize total to 1  ← WRONG comment, confusing
    }
}
