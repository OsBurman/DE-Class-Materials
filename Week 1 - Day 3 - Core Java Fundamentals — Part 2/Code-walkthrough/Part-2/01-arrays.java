/**
 * 01-arrays.java
 * Day 3 — Core Java Fundamentals Part 2
 *
 * Topics covered:
 *   - Array declaration (type[] name)
 *   - Static initialization  (int[] arr = {1, 2, 3})
 *   - Dynamic initialization (int[] arr = new int[n])
 *   - Default values in arrays
 *   - Accessing and modifying elements
 *   - Array length property
 *   - Iterating arrays (for, for-each)
 *   - Arrays utility class (sort, fill, copyOf, binarySearch, toString, equals)
 *   - Passing arrays to methods
 *   - Returning arrays from methods
 */
import java.util.Arrays;

public class ArraysDemo {

    public static void main(String[] args) {

        // =========================================================
        // SECTION 1: Array declaration
        // =========================================================

        // Preferred: type[] name   (bracket after type)
        int[] temperatures;

        // Also valid (C-style) but less idiomatic in Java:
        // int temperatures[];

        // Declaration alone does NOT allocate memory yet
        // temperatures[0] = 72;  // would cause a compile error — not initialized

        // =========================================================
        // SECTION 2: Static initialization (values known at compile time)
        // =========================================================

        // The array size is inferred from the number of values
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19};

        double[] prices = {9.99, 24.95, 4.50, 14.99};

        // Access elements by index (0-based)
        System.out.println("First day:  " + daysOfWeek[0]);  // Monday
        System.out.println("Last prime: " + primes[primes.length - 1]);  // 19
        System.out.println("Third price: $" + prices[2]);  // 4.5

        // =========================================================
        // SECTION 3: Dynamic initialization (size known, values not)
        // =========================================================

        // Allocate space now, fill values later
        int[] scores = new int[5];  // 5-element array, all initialized to 0

        // Default values by type:
        //   int / long / short / byte  → 0
        //   double / float             → 0.0
        //   boolean                    → false
        //   char                       → '\u0000' (null character)
        //   Object references          → null

        System.out.println("\n--- Default values (before assignment) ---");
        System.out.println("Default int:     " + scores[0]);  // 0

        boolean[] flags = new boolean[3];
        System.out.println("Default boolean: " + flags[0]);   // false

        String[] names = new String[3];
        System.out.println("Default String:  " + names[0]);   // null

        // Assign values by index
        scores[0] = 88;
        scores[1] = 95;
        scores[2] = 72;
        scores[3] = 84;
        scores[4] = 91;

        // =========================================================
        // SECTION 4: array.length property
        // =========================================================

        // length is a FIELD (not a method) — no parentheses
        System.out.println("\n--- Array lengths ---");
        System.out.println("Days of week: " + daysOfWeek.length);  // 5
        System.out.println("Scores array: " + scores.length);       // 5
        System.out.println("Primes array: " + primes.length);       // 8

        // Last element is always at index (length - 1)
        System.out.println("Last score: " + scores[scores.length - 1]);  // 91

        // =========================================================
        // SECTION 5: Modifying elements
        // =========================================================

        System.out.println("\n--- Before modification: " + daysOfWeek[1]);  // Tuesday
        daysOfWeek[1] = "TUESDAY (modified)";
        System.out.println("After modification:  " + daysOfWeek[1]);

        // Arrays are FIXED size — you cannot add or remove elements
        // To grow an array, you must create a new one (Arrays.copyOf handles this)

        // =========================================================
        // SECTION 6: Iterating arrays
        // =========================================================

        System.out.println("\n--- Standard for loop (with index) ---");
        for (int i = 0; i < scores.length; i++) {
            System.out.printf("scores[%d] = %d%n", i, scores[i]);
        }

        System.out.println("\n--- Enhanced for-each (no index needed) ---");
        int total = 0;
        for (int score : scores) {
            total += score;
        }
        System.out.printf("Total: %d | Average: %.1f%n", total, (double) total / scores.length);

        System.out.println("\n--- Iterating Strings (for-each) ---");
        for (String day : daysOfWeek) {
            System.out.println(day);
        }

        // =========================================================
        // SECTION 7: ArrayIndexOutOfBoundsException
        // =========================================================

        // Accessing an index that doesn't exist throws a runtime exception
        System.out.println("\n--- ArrayIndexOutOfBoundsException demo ---");
        try {
            System.out.println(scores[10]);  // scores only has indices 0-4
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught exception: " + e.getMessage());
            // Output: Caught exception: Index 10 out of bounds for length 5
        }

        // =========================================================
        // SECTION 8: Arrays utility class
        // =========================================================

        System.out.println("\n--- Arrays.toString() ---");
        int[] numbers = {42, 17, 85, 3, 56, 28, 9, 71};
        System.out.println("Before: " + Arrays.toString(numbers));

        System.out.println("\n--- Arrays.sort() ---");
        Arrays.sort(numbers);
        System.out.println("After sort: " + Arrays.toString(numbers));
        // Sorts in ascending order in-place (modifies the original array)

        System.out.println("\n--- Arrays.binarySearch() ---");
        // MUST sort before binary search
        int index = Arrays.binarySearch(numbers, 42);
        System.out.println("Index of 42: " + index);

        System.out.println("\n--- Arrays.fill() ---");
        int[] slots = new int[6];
        Arrays.fill(slots, -1);  // fill entire array with -1
        System.out.println("Filled: " + Arrays.toString(slots));

        Arrays.fill(slots, 1, 4, 99);  // fill indices 1 to 3 with 99
        System.out.println("Partial fill [1,4): " + Arrays.toString(slots));

        System.out.println("\n--- Arrays.copyOf() ---");
        int[] original = {10, 20, 30, 40, 50};
        int[] copy = Arrays.copyOf(original, original.length);  // exact copy
        int[] smaller = Arrays.copyOf(original, 3);             // truncated
        int[] larger = Arrays.copyOf(original, 7);              // padded with 0s
        System.out.println("Original: " + Arrays.toString(original));
        System.out.println("Exact copy: " + Arrays.toString(copy));
        System.out.println("Smaller:  " + Arrays.toString(smaller));
        System.out.println("Larger:   " + Arrays.toString(larger));

        System.out.println("\n--- Arrays.copyOfRange() ---");
        int[] segment = Arrays.copyOfRange(original, 1, 4);  // indices 1-3
        System.out.println("Range [1,4): " + Arrays.toString(segment));

        System.out.println("\n--- Arrays.equals() ---");
        int[] a = {1, 2, 3};
        int[] b = {1, 2, 3};
        int[] c = {3, 2, 1};
        System.out.println("a equals b: " + Arrays.equals(a, b));   // true
        System.out.println("a equals c: " + Arrays.equals(a, c));   // false
        // NEVER use == to compare arrays — it compares references, not contents

        // =========================================================
        // SECTION 9: Passing arrays to methods
        // =========================================================

        double[] sampleScores = {78.5, 92.0, 65.3, 88.7, 71.2};
        double avg = calculateAverage(sampleScores);
        System.out.printf("%nAverage of sample scores: %.2f%n", avg);

        // Arrays are passed by reference — changes inside the method affect the original
        System.out.println("\n--- Array modified by method ---");
        System.out.println("Before: " + Arrays.toString(a));
        doubleAllValues(a);
        System.out.println("After doubleAllValues: " + Arrays.toString(a));

        // =========================================================
        // SECTION 10: Returning arrays from methods
        // =========================================================

        int[] evenNumbers = generateEvens(5);
        System.out.println("\nFirst 5 even numbers: " + Arrays.toString(evenNumbers));
    }

    // Method that accepts an array and returns a scalar
    static double calculateAverage(double[] values) {
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return sum / values.length;
    }

    // Method that modifies an array in place (works on the original!)
    static void doubleAllValues(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] *= 2;
        }
    }

    // Method that creates and returns a new array
    static int[] generateEvens(int count) {
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = (i + 1) * 2;
        }
        return result;
    }
}
