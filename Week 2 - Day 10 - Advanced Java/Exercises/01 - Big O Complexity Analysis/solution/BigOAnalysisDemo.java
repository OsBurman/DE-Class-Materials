import java.util.Arrays;

public class BigOAnalysisDemo {

    // O(1) — result does not depend on array size; one operation regardless of n
    static int getFirst(int[] arr) {
        return arr[0];
    }

    // O(n) — in the worst case we visit every element once
    static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }

    // O(n²) — outer loop runs n times; inner loop runs up to n-1 times → n*(n-1)/2 ≈ n² comparisons
    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j]     = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }

    // O(log n) — each step halves the remaining search space; at most log₂(n) steps
    static int binarySearch(int[] sorted, int target) {
        int low = 0, high = sorted.length - 1, steps = 0;
        while (low <= high) {
            int mid = (low + high) / 2;
            steps++;
            if (sorted[mid] == target) {
                System.out.println("Steps taken: " + steps);
                return mid;
            } else if (sorted[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        System.out.println("Steps taken: " + steps);
        return -1;
    }

    // O(n²) space — allocates n*n cells; contrast with a simple int variable which is O(1) space
    static void buildMatrix(int n) {
        int[][] matrix = new int[n][n]; // n² cells allocated on the heap
        System.out.println("Matrix allocated: " + n + " x " + n + " = " + (n * n) + " cells");
    }

    public static void main(String[] args) {
        // ── O(1) ────────────────────────────────────────────────────────────
        System.out.println("=== O(1): getFirst ===");
        int[] data = {10, 20, 30, 40, 50, 60, 70};
        System.out.println("First element: " + getFirst(data));

        // ── O(n) ────────────────────────────────────────────────────────────
        System.out.println("\n=== O(n): linearSearch ===");
        int idx = linearSearch(data, 50);
        System.out.println(idx >= 0 ? "Found at index: " + idx : "Not found");

        // ── O(n²) ───────────────────────────────────────────────────────────
        System.out.println("\n=== O(n²): bubbleSort ===");
        int[] toSort = {7, 3, 1, 9, 5, 8, 2};
        bubbleSort(toSort);
        System.out.println("Sorted: " + Arrays.toString(toSort));

        // ── O(log n) ────────────────────────────────────────────────────────
        System.out.println("\n=== O(log n): binarySearch ===");
        int[] sorted = {2, 11, 19, 23, 35, 47, 52, 68, 71, 83, 90, 95, 100, 104, 117, 123};
        int found = binarySearch(sorted, 23);
        System.out.println("Target 23 found at index: " + found);

        // ── O(n²) Space ─────────────────────────────────────────────────────
        System.out.println("\n=== O(n²) Space: buildMatrix ===");
        buildMatrix(4);

        // ── Summary table ────────────────────────────────────────────────────
        System.out.println("\n=== Complexity Summary ===");
        System.out.printf("%-18s| %-12s| %s%n", "Algorithm", "Big O Time", "Big O Space");
        System.out.printf("%-18s| %-12s| %s%n", "getFirst",     "O(1)",     "O(1)");
        System.out.printf("%-18s| %-12s| %s%n", "linearSearch", "O(n)",     "O(1)");
        System.out.printf("%-18s| %-12s| %s%n", "bubbleSort",   "O(n²)",    "O(1)");
        System.out.printf("%-18s| %-12s| %s%n", "binarySearch", "O(log n)", "O(1)");
        System.out.printf("%-18s| %-12s| %s%n", "buildMatrix",  "O(n²)",    "O(n²)");
    }
}
