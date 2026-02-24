import java.util.Arrays;

public class CommonPatterns {

    public static void main(String[] args) {

        // -------------------------------------------------------
        // Part 1 — Running Total Accumulator
        // -------------------------------------------------------
        System.out.println("=== Part 1: Accumulator ===");
        int[] sales = {120, 340, 210, 450, 180, 390, 275};
        System.out.println("Sales   : " + Arrays.toString(sales));

        int total = 0;
        for (int s : sales) {
            total += s;
        }
        System.out.println("Total   : " + total);                // 1965

        long product = 1L;                  // long to avoid int overflow
        for (int s : sales) {
            product *= s;
        }
        System.out.println("Product : " + product);              // 269168640000000

        System.out.println();

        // -------------------------------------------------------
        // Part 2 — Variable Swap
        // -------------------------------------------------------
        System.out.println("=== Part 2: Variable Swap ===");
        int a = 15, b = 42;
        System.out.println("Before  : a = " + a + ", b = " + b);

        int temp = a;   // step 1: save a
        a = b;          // step 2: overwrite a with b
        b = temp;       // step 3: restore original a into b
        System.out.println("After   : a = " + a + ", b = " + b);

        System.out.println();

        // -------------------------------------------------------
        // Part 3 — Find Min and Max
        // -------------------------------------------------------
        System.out.println("=== Part 3: Min and Max ===");
        int[] temps = {72, 68, 85, 91, 63, 78, 88, 74};
        System.out.println("Temps   : " + Arrays.toString(temps));

        int min = temps[0];     // seed from first element, not 0
        int max = temps[0];
        for (int t : temps) {
            if (t < min) min = t;
            if (t > max) max = t;
        }
        System.out.println("Min temp: " + min);     // 63
        System.out.println("Max temp: " + max);     // 91

        System.out.println();

        // -------------------------------------------------------
        // Part 4 — Count Occurrences
        // -------------------------------------------------------
        System.out.println("=== Part 4: Count Occurrences ===");
        int[] rolls = {3, 5, 2, 6, 3, 1, 3, 4, 6, 3};
        System.out.println("Rolls   : " + Arrays.toString(rolls));

        int countThrees = 0;
        for (int r : rolls) {
            if (r == 3) countThrees++;
        }
        System.out.println("Count of 3  : " + countThrees);     // 4

        int countAbove = 0;
        for (int r : rolls) {
            if (r > 3) countAbove++;
        }
        System.out.println("Greater than 3: " + countAbove);    // 4
    }
}
