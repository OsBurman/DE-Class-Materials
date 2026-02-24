import java.util.Arrays;

public class ArrayOperations {

    public static void main(String[] args) {

        // -------------------------------------------------------
        // Part 1 — Literal Initialization and Access
        // -------------------------------------------------------
        System.out.println("=== Part 1: Literal Init ===");
        int[] primes = {2, 3, 5, 7, 11, 13};

        // TODO: Print "Primes  : " followed by Arrays.toString(primes)
        // TODO: Print "First   : " followed by the first element
        // TODO: Print "Last    : " followed by the last element (use primes.length)
        // TODO: Print "Length  : " followed by primes.length

        System.out.println();

        // -------------------------------------------------------
        // Part 2 — Sized Initialization and Assignment
        // -------------------------------------------------------
        System.out.println("=== Part 2: Sized Init ===");

        // TODO: Declare String[] days = new String[5]
        // TODO: Assign "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        //       to indices 0, 1, 2, 3, 4 respectively
        // TODO: Print "Days    : " followed by Arrays.toString(days)
        // TODO: Change days[2] to "Midweek"
        // TODO: Print "Updated : " followed by Arrays.toString(days)

        System.out.println();

        // -------------------------------------------------------
        // Part 3 — Standard For Loop: Double Every Element
        // -------------------------------------------------------
        System.out.println("=== Part 3: Double Elements ===");
        int[] values = {3, 6, 9, 12, 15};

        // TODO: Print "Before  : " followed by Arrays.toString(values)
        // TODO: Use a standard for loop (for (int i = 0; i < values.length; i++))
        //       to multiply each element by 2: values[i] *= 2
        // TODO: Print "After   : " followed by Arrays.toString(values)

        System.out.println();

        // -------------------------------------------------------
        // Part 4 — Copy an Array (Manual)
        // -------------------------------------------------------
        System.out.println("=== Part 4: Array Copy ===");
        int[] original = {10, 20, 30, 40, 50};

        // TODO: Declare int[] copy = new int[original.length]
        // TODO: Use a for loop to copy each element: copy[i] = original[i]
        // TODO: Change copy[0] to 99
        // TODO: Print "Copy[0] changed to 99"
        // TODO: Print "copy    : " followed by Arrays.toString(copy)
        // TODO: Print "original: " followed by Arrays.toString(original)
        //       (original[0] should still be 10)
    }
}
