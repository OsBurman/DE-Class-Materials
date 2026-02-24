# Exercise 01: Big O Complexity Analysis

## Objective
Identify and analyze the time and space complexity of common algorithm patterns using Big O notation.

## Background
Big O notation describes how an algorithm's resource usage (time or memory) grows as input size `n` increases. It focuses on the *worst-case* growth rate and drops constants and lower-order terms. Knowing the complexity of your code lets you choose between approaches before you benchmark: O(1) is ideal, O(n) is linear, O(n²) signals nested loops, O(log n) signals halving (binary search, balanced trees).

## Requirements

1. **O(1) — Constant time**: Write a method `getFirst(int[] arr)` that returns the first element. Call it and print the result. Explain in a comment why it is O(1).

2. **O(n) — Linear time**: Write a method `linearSearch(int[] arr, int target)` that iterates through the array and returns the index of `target`, or `-1` if not found. Call it with an array of 7 elements. Print `"Found at index: N"` or `"Not found"`.

3. **O(n²) — Quadratic time**: Write a method `bubbleSort(int[] arr)` using two nested loops. Print the sorted array. Explain in a comment why nested loops produce O(n²).

4. **O(log n) — Logarithmic time**: Write a method `binarySearch(int[] sorted, int target)` that halves the search space on each step. Print the index of the found element. Add a `steps` counter and print how many comparisons were needed for n=16.

5. **Space complexity demonstration**: Write a method `buildMatrix(int n)` that allocates an `n × n` 2D `int` array (O(n²) space) and print `"Matrix allocated: n x n = n*n cells"`. Contrast with a variable that uses O(1) space.

6. **Complexity comparison table**: After all demos, print a summary table:
   ```
   Algorithm         | Big O Time | Big O Space
   getFirst          | O(1)       | O(1)
   linearSearch      | O(n)       | O(1)
   bubbleSort        | O(n²)      | O(1)
   binarySearch      | O(log n)   | O(1)
   buildMatrix       | O(n²)      | O(n²)
   ```

## Hints
- O(1) means the runtime does NOT depend on the input size at all — accessing an array index is always one operation
- O(log n) algorithms work by repeatedly dividing the search space in half — after k steps you have n/2^k elements left; solve for k
- Nested loops where both iterate up to n give n × n = n² operations in the worst case
- Space complexity counts *extra* memory your algorithm allocates, not the input itself

## Expected Output

```
=== O(1): getFirst ===
First element: 10

=== O(n): linearSearch ===
Found at index: 4

=== O(n²): bubbleSort ===
Sorted: [1, 2, 3, 5, 7, 8, 9]

=== O(log n): binarySearch ===
Target 23 found at index: 3
Steps taken: 2

=== O(n²) Space: buildMatrix ===
Matrix allocated: 4 x 4 = 16 cells

=== Complexity Summary ===
Algorithm         | Big O Time | Big O Space
getFirst          | O(1)       | O(1)
linearSearch      | O(n)       | O(1)
bubbleSort        | O(n²)      | O(1)
binarySearch      | O(log n)   | O(1)
buildMatrix       | O(n²)      | O(n²)
```
