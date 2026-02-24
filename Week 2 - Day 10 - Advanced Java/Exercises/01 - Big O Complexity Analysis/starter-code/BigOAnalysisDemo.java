import java.util.Arrays;

public class BigOAnalysisDemo {

    // ── O(1): Constant time ─────────────────────────────────────────────────
    // TODO: Write method getFirst(int[] arr) that returns arr[0].
    //       Add a comment explaining why this is O(1).

    // ── O(n): Linear time ───────────────────────────────────────────────────
    // TODO: Write method linearSearch(int[] arr, int target) that loops
    //       through arr and returns the index where arr[i] == target,
    //       or -1 if not found. Add a comment explaining why this is O(n).

    // ── O(n²): Quadratic time ───────────────────────────────────────────────
    // TODO: Write method bubbleSort(int[] arr) using two nested for loops.
    //       Each pass bubbles the largest unsorted element to the end.
    //       Swap adjacent elements when arr[j] > arr[j+1].
    //       Add a comment explaining why nested loops produce O(n²).

    // ── O(log n): Logarithmic time ──────────────────────────────────────────
    // TODO: Write method binarySearch(int[] sorted, int target) using
    //       low/high/mid pointers. Each iteration halves the search space.
    //       Track a 'steps' counter and print "Steps taken: N" inside the method.
    //       Return the index where sorted[mid] == target, or -1 if not found.

    // ── O(n²) Space: matrix allocation ─────────────────────────────────────
    // TODO: Write method buildMatrix(int n) that allocates a new int[n][n]
    //       and prints "Matrix allocated: N x N = N*N cells"

    public static void main(String[] args) {
        // ── O(1) ────────────────────────────────────────────────────────────
        System.out.println("=== O(1): getFirst ===");
        int[] data = {10, 20, 30, 40, 50, 60, 70};
        // TODO: Call getFirst(data) and print "First element: <result>"


        // ── O(n) ────────────────────────────────────────────────────────────
        System.out.println("\n=== O(n): linearSearch ===");
        // TODO: Call linearSearch(data, 50) and print
        //       "Found at index: N" or "Not found"


        // ── O(n²) ───────────────────────────────────────────────────────────
        System.out.println("\n=== O(n²): bubbleSort ===");
        int[] toSort = {7, 3, 1, 9, 5, 8, 2};
        // TODO: Call bubbleSort(toSort), then print "Sorted: " + Arrays.toString(toSort)


        // ── O(log n) ────────────────────────────────────────────────────────
        System.out.println("\n=== O(log n): binarySearch ===");
        int[] sorted = {2, 11, 19, 23, 35, 47, 52, 68, 71, 83, 90, 95, 100, 104, 117, 123};
        // TODO: Call binarySearch(sorted, 23) and print "Target 23 found at index: N"


        // ── O(n²) Space ─────────────────────────────────────────────────────
        System.out.println("\n=== O(n²) Space: buildMatrix ===");
        // TODO: Call buildMatrix(4)


        // ── Summary table ────────────────────────────────────────────────────
        System.out.println("\n=== Complexity Summary ===");
        // TODO: Print the complexity table from the instructions using printf or println
        //       with columns: Algorithm, Big O Time, Big O Space
    }
}
