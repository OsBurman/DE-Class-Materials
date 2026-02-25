package com.academy;

import java.util.Arrays;

/**
 * Day 3 Part 2 — Arrays: 1D, 2D, common patterns
 *
 * Theme: Student Score Tracker
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Day 3 Part 2 — Arrays Demo          ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        demo1DArrays();
        demoArrayAlgorithms();
        demo2DArrays();
        demoCommonPatterns();
    }

    static void demo1DArrays() {
        System.out.println("=== 1. 1D Array Declaration & Initialization ===");

        // Three ways to declare arrays
        int[] scores1 = new int[5];                        // all zeroes
        int[] scores2 = new int[]{88, 75, 92, 61, 78};    // explicit values
        int[] scores3 = {95, 82, 70, 65, 88};             // shorthand

        System.out.println("  Default int array: " + Arrays.toString(scores1));
        System.out.println("  Explicit values:   " + Arrays.toString(scores2));
        System.out.println("  Shorthand:         " + Arrays.toString(scores3));
        System.out.println("  Length: " + scores3.length);
        System.out.println("  First:  " + scores3[0]);
        System.out.println("  Last:   " + scores3[scores3.length - 1]);

        // Modify an element
        scores3[2] = 74;
        System.out.println("  After scores3[2]=74: " + Arrays.toString(scores3));
        System.out.println();
    }

    static void demoArrayAlgorithms() {
        System.out.println("=== 2. Common Array Algorithms ===");

        int[] scores = {88, 75, 92, 61, 78, 95, 54, 83};
        System.out.println("  Scores: " + Arrays.toString(scores));

        // Sum & average
        int sum = 0;
        for (int s : scores) sum += s;
        double avg = (double) sum / scores.length;
        System.out.printf("  Sum: %d | Average: %.1f%n", sum, avg);

        // Min and max
        int min = scores[0], max = scores[0];
        for (int s : scores) {
            if (s < min) min = s;
            if (s > max) max = s;
        }
        System.out.println("  Min: " + min + " | Max: " + max);

        // Sorting
        int[] sorted = Arrays.copyOf(scores, scores.length); // don't modify original
        Arrays.sort(sorted);
        System.out.println("  Sorted (asc): " + Arrays.toString(sorted));

        // Binary search (array must be sorted!)
        int idx = Arrays.binarySearch(sorted, 88);
        System.out.println("  Binary search for 88: found at index " + idx);

        // Count above average
        int aboveAvg = 0;
        for (int s : scores) if (s > avg) aboveAvg++;
        System.out.println("  Above average: " + aboveAvg + " students");
        System.out.println();
    }

    static void demo2DArrays() {
        System.out.println("=== 3. 2D Arrays (Grades Per Subject Per Student) ===");

        // [student][subject] → [4 students][3 subjects: Math, English, Science]
        int[][] grades = {
            {88, 92, 85},   // Alice
            {75, 68, 80},   // Bob
            {95, 88, 91},   // Carol
            {62, 70, 74}    // Dave
        };
        String[] students = {"Alice", "Carol", "Bob",  "Dave"};
        String[] subjects = {"Math ", "English", "Science"};

        // Print header
        System.out.printf("  %-8s", "Student");
        for (String sub : subjects) System.out.printf(" %-8s", sub);
        System.out.printf(" %-8s%n", "Average");

        // Print each student's grades
        for (int i = 0; i < grades.length; i++) {
            System.out.printf("  %-8s", students[i]);
            int total = 0;
            for (int j = 0; j < grades[i].length; j++) {
                System.out.printf(" %-8d", grades[i][j]);
                total += grades[i][j];
            }
            System.out.printf(" %-8.1f%n", (double) total / grades[i].length);
        }
        System.out.println();
    }

    static void demoCommonPatterns() {
        System.out.println("=== 4. Common Patterns ===");

        // Reverse an array
        int[] arr = {1, 2, 3, 4, 5};
        System.out.println("  Original: " + Arrays.toString(arr));
        for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
            int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
        }
        System.out.println("  Reversed: " + Arrays.toString(arr));

        // Check for duplicates
        int[] withDups = {3, 7, 2, 7, 5, 3, 8};
        System.out.println("  Check for duplicates in: " + Arrays.toString(withDups));
        for (int i = 0; i < withDups.length; i++) {
            for (int j = i + 1; j < withDups.length; j++) {
                if (withDups[i] == withDups[j]) {
                    System.out.println("    Duplicate found: " + withDups[i]);
                }
            }
        }

        // Copy array
        int[] original = {10, 20, 30};
        int[] copy = Arrays.copyOf(original, original.length);
        copy[0] = 99;
        System.out.println("  Original after copying and modifying copy: " + Arrays.toString(original));
        System.out.println("  Modified copy: " + Arrays.toString(copy));
        System.out.println("  (Arrays.copyOf creates a new array — not a reference!)");

        System.out.println("\n✓ Arrays demo complete.");
    }
}
