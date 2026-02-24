public class ArrayStatistics {

    public static void main(String[] args) {

        int[] scores = {88, 72, 95, 64, 81, 90, 73, 88, 56, 79};

        System.out.println("=== Exam Score Statistics ===");
        System.out.println("Scores  : [88, 72, 95, 64, 81, 90, 73, 88, 56, 79]");

        // TODO: Declare an int variable 'sum' initialized to 0
        //       Use an enhanced for loop (for (int score : scores)) to add each score to sum
        //       Print: "Sum     : [sum]"

        // TODO: Compute the average as a double: (double) sum / scores.length
        //       Store in a double named 'average'
        //       Print: "Average : [average]"

        // TODO: Declare int 'min' initialized to scores[0]
        //       Use an enhanced for loop to find the minimum value
        //       Hint: inside the loop, if the current score < min, update min
        //       Print: "Min     : [min]"

        // TODO: Declare int 'max' initialized to scores[0]
        //       Use an enhanced for loop to find the maximum value
        //       Print: "Max     : [max]"

        // TODO: Declare int 'aboveAvg' initialized to 0
        //       Use an enhanced for loop to count scores strictly greater than average
        //       Print: "Above avg: [aboveAvg] out of [scores.length]"

        // TODO: Add a single comment here explaining why you cannot use an enhanced for loop
        //       to modify array elements (e.g., to double every value in scores)
    }
}
