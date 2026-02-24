import java.util.*;

/**
 * DAY 10 — PART 1 | Big O Notation & Algorithm Complexity
 * ─────────────────────────────────────────────────────────────────────────────
 * Big O notation describes HOW an algorithm's runtime or memory usage grows
 * as the input size (n) grows — independent of hardware or language.
 *
 * We care about the WORST CASE and the DOMINANT TERM.
 *   3n² + 5n + 12  →  O(n²)   (drop constants and lower-order terms)
 *
 * COMMON COMPLEXITY CLASSES (best → worst):
 * ┌────────────┬─────────────────────────────────────────────────────────────┐
 * │ O(1)       │ Constant    — same time regardless of n                     │
 * │ O(log n)   │ Logarithmic — halves work each step (binary search)         │
 * │ O(n)       │ Linear      — one operation per element                     │
 * │ O(n log n) │ Linearithmic— divide-and-conquer sorts (merge, heap, quick) │
 * │ O(n²)      │ Quadratic   — nested loops over the same collection         │
 * │ O(2ⁿ)      │ Exponential — brute-force combinatorics / recursion         │
 * │ O(n!)      │ Factorial   — permutation enumeration                       │
 * └────────────┴─────────────────────────────────────────────────────────────┘
 */
public class BigOAndComplexity {

    public static void main(String[] args) {
        demonstrateO1();
        demonstrateOLogN();
        demonstrateON();
        demonstrateONLogN();
        demonstrateON2();
        demonstrateO2N();
        demonstrateSpaceComplexity();
        compareAlgorithmsOnRealData();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // O(1) — CONSTANT TIME
    // The operation takes the same time no matter how large the array is.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateO1() {
        System.out.println("=== O(1) — Constant Time ===");

        int[] prices = {12, 45, 7, 99, 23};

        // Array index access: always one operation, regardless of array size
        System.out.println("First price: " + prices[0]);     // O(1)
        System.out.println("Last price:  " + prices[prices.length - 1]);  // O(1)

        // HashMap get/put: amortized O(1)
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("apple", 100);    // O(1)
        inventory.put("banana", 50);    // O(1)
        System.out.println("Apple stock: " + inventory.get("apple"));  // O(1)

        System.out.println("No matter how many products exist — lookup is always instant.\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // O(log n) — LOGARITHMIC TIME
    // Input size doubles → only ONE more step needed.
    // Binary search: cuts the search space in half each iteration.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateOLogN() {
        System.out.println("=== O(log n) — Logarithmic Time ===");

        int[] sortedPrices = {5, 12, 18, 27, 34, 45, 67, 89, 102, 150};
        int target = 45;
        int steps = 0;

        int lo = 0, hi = sortedPrices.length - 1;
        while (lo <= hi) {
            steps++;
            int mid = lo + (hi - lo) / 2;   // avoids integer overflow vs (lo+hi)/2
            if (sortedPrices[mid] == target) {
                System.out.printf("Found %d at index %d after %d steps%n", target, mid, steps);
                break;
            } else if (sortedPrices[mid] < target) {
                lo = mid + 1;   // target is in the right half
            } else {
                hi = mid - 1;   // target is in the left half
            }
        }

        // Growth comparison
        System.out.println("\nBinary search steps needed:");
        System.out.printf("  n=1,000       → ~%d steps%n", (int)(Math.log(1_000) / Math.log(2)));
        System.out.printf("  n=1,000,000   → ~%d steps%n", (int)(Math.log(1_000_000) / Math.log(2)));
        System.out.printf("  n=1,000,000,000 → ~%d steps%n\n", (int)(Math.log(1_000_000_000) / Math.log(2)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // O(n) — LINEAR TIME
    // Must look at every element once (or a fixed number of times).
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateON() {
        System.out.println("=== O(n) — Linear Time ===");

        List<String> emails = Arrays.asList(
                "alice@co.com", "bob@co.com", "charlie@co.com", "dana@co.com"
        );

        // Finding the first @gmail.com — must scan each element
        String found = null;
        for (String email : emails) {    // O(n) — worst case checks every email
            if (email.endsWith("@gmail.com")) {
                found = email;
                break;
            }
        }
        System.out.println("Gmail found: " + found);  // null — not in list

        // Sum all values: must touch every element once
        int[] orders = {34, 12, 67, 8, 91, 45};
        int total = 0;
        for (int order : orders) total += order;   // O(n) — one pass
        System.out.println("Total orders value: " + total + "\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // O(n log n) — LINEARITHMIC TIME
    // Typical of efficient sorting algorithms.
    // Divide-and-conquer: split → sort halves → merge.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateONLogN() {
        System.out.println("=== O(n log n) — Merge Sort ===");

        int[] transactions = {67, 23, 89, 12, 45, 78, 34, 56};
        System.out.println("Before: " + Arrays.toString(transactions));
        mergeSort(transactions, 0, transactions.length - 1);
        System.out.println("After:  " + Arrays.toString(transactions));
        System.out.println("Arrays.sort() and Collections.sort() both use O(n log n) internally.\n");
    }

    static void mergeSort(int[] arr, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);       // sort left half   — log n levels
        mergeSort(arr, mid + 1, right);  // sort right half
        merge(arr, left, mid, right);    // merge            — O(n) per level
    }

    static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1, n2 = right - mid;
        int[] L = Arrays.copyOfRange(arr, left, mid + 1);
        int[] R = Arrays.copyOfRange(arr, mid + 1, right + 1);
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // ─────────────────────────────────────────────────────────────────────────
    // O(n²) — QUADRATIC TIME
    // Nested loops over the same input.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateON2() {
        System.out.println("=== O(n²) — Quadratic Time ===");

        // Bubble Sort: for every element, compare with every other element
        int[] prices = {64, 34, 25, 12, 22, 11, 90};
        int comparisons = 0;
        System.out.println("Before: " + Arrays.toString(prices));

        for (int i = 0; i < prices.length - 1; i++) {       // outer loop: n-1 passes
            for (int j = 0; j < prices.length - i - 1; j++) { // inner loop: n-i-1 comparisons
                comparisons++;
                if (prices[j] > prices[j + 1]) {
                    int temp = prices[j];
                    prices[j] = prices[j + 1];
                    prices[j + 1] = temp;
                }
            }
        }
        System.out.println("After:  " + Arrays.toString(prices));
        System.out.println("Comparisons made: " + comparisons + " (n=" + prices.length + ", n²=" +
                (prices.length * prices.length) + ")");

        // Another classic O(n²): find all pairs that sum to a target
        System.out.println("\nAll pairs summing to 90:");
        int[] nums = {10, 20, 30, 40, 50, 60, 70, 80};
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {   // nested loop = O(n²)
                if (nums[i] + nums[j] == 90)
                    System.out.println("  (" + nums[i] + ", " + nums[j] + ")");
            }
        }
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // O(2ⁿ) — EXPONENTIAL TIME
    // Each additional input doubles the work. Grows explosively.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateO2N() {
        System.out.println("=== O(2ⁿ) — Naive Fibonacci (Exponential) ===");

        // Naive recursive Fibonacci recalculates the same subproblems repeatedly
        // fib(5) calls fib(4) AND fib(3). fib(4) calls fib(3) AND fib(2). etc.
        System.out.println("fib(10) = " + fibNaive(10));   // 2^10 = 1024 calls

        // Compare: O(n) iterative version — linear, not exponential
        System.out.println("fib(10) iterative = " + fibLinear(10));

        System.out.println("\n2ⁿ growth:");
        System.out.printf("  n=10  → %,d operations (feasible)%n", (int) Math.pow(2, 10));
        System.out.printf("  n=30  → %,d operations (seconds)%n",  (int) Math.pow(2, 30));
        System.out.printf("  n=50  → %,d operations (age of universe)%n\n", (long) Math.pow(2, 50));
    }

    static long fibNaive(int n) {
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2);  // TWO recursive calls each time = O(2ⁿ)
    }

    static long fibLinear(int n) {
        if (n <= 1) return n;
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {   // O(n) — one pass
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SPACE COMPLEXITY
    // How much extra memory does the algorithm use as n grows?
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateSpaceComplexity() {
        System.out.println("=== Space Complexity ===");

        int n = 10;
        int[] inputArray = new int[n];

        // O(1) extra space — only a few variables, no extra data structures
        int sum = 0;
        for (int val : inputArray) sum += val;   // space: just 'sum' and 'val'

        // O(n) extra space — creates a new array proportional to input
        int[] copy = Arrays.copyOf(inputArray, n);   // O(n) space

        // O(n) space — recursive call stack for linear recursion
        // Each call frame lives on the stack until the base case returns
        System.out.println("Factorial(10) = " + factorial(10));  // 10 frames on stack

        System.out.println("Space complexity matters for large n — a 1 billion element O(n) space");
        System.out.println("algorithm needs ~8 GB just for longs.\n");
    }

    static long factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);   // n stack frames = O(n) space
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRACTICAL COMPARISON — same problem, different complexities
    // ─────────────────────────────────────────────────────────────────────────
    static void compareAlgorithmsOnRealData() {
        System.out.println("=== Practical Comparison: contains() on List vs Set ===");

        int n = 100_000;
        List<Integer> list = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < n; i++) { list.add(i); set.add(i); }

        // List.contains() = O(n) — scans from start
        long start = System.nanoTime();
        boolean foundInList = list.contains(n - 1);   // worst case: very end
        long listTime = System.nanoTime() - start;

        // HashSet.contains() = O(1) — hash lookup
        start = System.nanoTime();
        boolean foundInSet = set.contains(n - 1);
        long setTime = System.nanoTime() - start;

        System.out.printf("List.contains() O(n):  %,d ns%n", listTime);
        System.out.printf("Set.contains()  O(1):  %,d ns%n", setTime);
        System.out.println("Both found: " + foundInList + " / " + foundInSet);
        System.out.println("\nPractical rule: If you need frequent lookups, use a Set or Map.");
    }
}
