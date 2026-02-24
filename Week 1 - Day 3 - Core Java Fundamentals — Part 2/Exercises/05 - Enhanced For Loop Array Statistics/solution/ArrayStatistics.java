public class ArrayStatistics {

    public static void main(String[] args) {

        int[] scores = {88, 72, 95, 64, 81, 90, 73, 88, 56, 79};

        System.out.println("=== Exam Score Statistics ===");
        System.out.println("Scores  : [88, 72, 95, 64, 81, 90, 73, 88, 56, 79]");

        // --- Sum ---
        int sum = 0;
        for (int score : scores) {
            sum += score;                           // accumulate each value
        }
        System.out.println("Sum     : " + sum);     // 786

        // --- Average ---
        // Cast sum to double so division doesn't truncate
        double average = (double) sum / scores.length;
        System.out.println("Average : " + average); // 78.6

        // --- Minimum ---
        int min = scores[0];                        // seed with first element, not 0
        for (int score : scores) {
            if (score < min) {
                min = score;
            }
        }
        System.out.println("Min     : " + min);     // 56

        // --- Maximum ---
        int max = scores[0];
        for (int score : scores) {
            if (score > max) {
                max = score;
            }
        }
        System.out.println("Max     : " + max);     // 95

        // --- Count above average ---
        int aboveAvg = 0;
        for (int score : scores) {
            if (score > average) {
                aboveAvg++;
            }
        }
        System.out.println("Above avg: " + aboveAvg + " out of " + scores.length); // 5 out of 10

        // NOTE on enhanced for limitation:
        // The loop variable (e.g. 'score') is a COPY of the array element.
        // Writing  score *= 2  only modifies the local copy — it does NOT
        // change the value stored in the scores[] array.  To modify elements
        // in place you must use a traditional indexed for loop:
        //   for (int i = 0; i < scores.length; i++) { scores[i] *= 2; }
    }
}
