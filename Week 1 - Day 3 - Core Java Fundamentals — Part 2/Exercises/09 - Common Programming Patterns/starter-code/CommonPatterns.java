import java.util.Arrays;

public class CommonPatterns {

    public static void main(String[] args) {

        // -------------------------------------------------------
        // Part 1 — Running Total Accumulator
        // -------------------------------------------------------
        System.out.println("=== Part 1: Accumulator ===");
        int[] sales = {120, 340, 210, 450, 180, 390, 275};
        System.out.println("Sales   : " + Arrays.toString(sales));

        // TODO: Declare int total = 0
        //       Use a for-each loop to add each sale to total
        //       Print "Total   : [total]"

        // TODO: Declare long product = 1L
        //       Use a for-each loop to multiply product by each sale
        //       Print "Product : [product]"

        System.out.println();

        // -------------------------------------------------------
        // Part 2 — Variable Swap
        // -------------------------------------------------------
        System.out.println("=== Part 2: Variable Swap ===");
        int a = 15, b = 42;

        // TODO: Print "Before  : a = [a], b = [b]"
        // TODO: Declare int temp, then perform the 3-line swap: temp = a; a = b; b = temp;
        // TODO: Print "After   : a = [a], b = [b]"

        System.out.println();

        // -------------------------------------------------------
        // Part 3 — Find Min and Max
        // -------------------------------------------------------
        System.out.println("=== Part 3: Min and Max ===");
        int[] temps = {72, 68, 85, 91, 63, 78, 88, 74};
        System.out.println("Temps   : " + Arrays.toString(temps));

        // TODO: Declare int min = temps[0], int max = temps[0]
        // TODO: Use a for-each loop:
        //           if current value < min, update min
        //           if current value > max, update max
        // TODO: Print "Min temp: [min]"
        // TODO: Print "Max temp: [max]"

        System.out.println();

        // -------------------------------------------------------
        // Part 4 — Count Occurrences
        // -------------------------------------------------------
        System.out.println("=== Part 4: Count Occurrences ===");
        int[] rolls = {3, 5, 2, 6, 3, 1, 3, 4, 6, 3};
        System.out.println("Rolls   : " + Arrays.toString(rolls));

        // TODO: Declare int countThrees = 0
        //       Use a for-each loop: if roll == 3, increment countThrees
        //       Print "Count of 3  : [countThrees]"

        // TODO: Declare int countAbove = 0
        //       Use a for-each loop: if roll > 3, increment countAbove
        //       Print "Greater than 3: [countAbove]"
    }
}
