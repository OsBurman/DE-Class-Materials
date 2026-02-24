import java.util.Arrays;

public class ArrayOperations {

    public static void main(String[] args) {

        // -------------------------------------------------------
        // Part 1 — Literal Initialization and Access
        // -------------------------------------------------------
        System.out.println("=== Part 1: Literal Init ===");
        int[] primes = {2, 3, 5, 7, 11, 13};

        System.out.println("Primes  : " + Arrays.toString(primes));
        System.out.println("First   : " + primes[0]);
        System.out.println("Last    : " + primes[primes.length - 1]); // length - 1 = last index
        System.out.println("Length  : " + primes.length);

        System.out.println();

        // -------------------------------------------------------
        // Part 2 — Sized Initialization and Assignment
        // -------------------------------------------------------
        System.out.println("=== Part 2: Sized Init ===");
        String[] days = new String[5];   // 5 slots, all null initially
        days[0] = "Monday";
        days[1] = "Tuesday";
        days[2] = "Wednesday";
        days[3] = "Thursday";
        days[4] = "Friday";

        System.out.println("Days    : " + Arrays.toString(days));
        days[2] = "Midweek";             // modify a single element
        System.out.println("Updated : " + Arrays.toString(days));

        System.out.println();

        // -------------------------------------------------------
        // Part 3 — Standard For Loop: Double Every Element
        // -------------------------------------------------------
        System.out.println("=== Part 3: Double Elements ===");
        int[] values = {3, 6, 9, 12, 15};

        System.out.println("Before  : " + Arrays.toString(values));
        for (int i = 0; i < values.length; i++) {
            values[i] *= 2;             // index access required to mutate the array
        }
        System.out.println("After   : " + Arrays.toString(values));

        System.out.println();

        // -------------------------------------------------------
        // Part 4 — Copy an Array (Manual)
        // -------------------------------------------------------
        System.out.println("=== Part 4: Array Copy ===");
        int[] original = {10, 20, 30, 40, 50};

        int[] copy = new int[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i];      // copy value by value — independent arrays
        }

        copy[0] = 99;                   // changing copy does NOT affect original
        System.out.println("Copy[0] changed to 99");
        System.out.println("copy    : " + Arrays.toString(copy));
        System.out.println("original: " + Arrays.toString(original)); // still [10, 20, 30, 40, 50]
    }
}
