# Exercise 05: Enhanced For Loop Array Statistics

## Objective
Use the enhanced for loop (for-each) to iterate over an array and compute statistics without managing an index variable.

## Background
The enhanced `for` loop (`for (Type item : collection)`) was introduced in Java 5 to simplify iteration when you don't need the index. It's cleaner than a standard for loop when you just want to visit every element. In this exercise you'll use it to compute the sum, average, minimum, and maximum of a set of exam scores.

## Requirements

Given the following array (already declared in starter file):
```java
int[] scores = {88, 72, 95, 64, 81, 90, 73, 88, 56, 79};
```

1. Use an enhanced for loop to compute the **sum** of all scores. Print: `"Sum     : [value]"`

2. Compute and print the **average** (as a `double`). Divide the sum by `scores.length`. Print: `"Average : [value]"`

3. Use an enhanced for loop to find the **minimum** score. Initialize a variable to the first element before the loop. Print: `"Min     : [value]"`

4. Use an enhanced for loop to find the **maximum** score. Initialize a variable to the first element before the loop. Print: `"Max     : [value]"`

5. Use an enhanced for loop to count how many scores are **above the average**. Print: `"Above avg: [count] out of [scores.length]"`

6. Add a comment explaining why you **cannot** use an enhanced for loop to modify array elements in place (e.g., to double every score).

## Hints
- Initialize `min` and `max` to `scores[0]` (the first element) before the loop, not to `0` or `Integer.MAX_VALUE` (though those also work).
- `scores.length` gives the number of elements — no need to count manually.
- To compute a double average from two ints: cast one of them: `(double) sum / scores.length`.
- The enhanced for loop gives you the **value** of each element, not its index — you cannot assign back to the array through the loop variable.

## Expected Output
```
=== Exam Score Statistics ===
Scores  : [88, 72, 95, 64, 81, 90, 73, 88, 56, 79]
Sum     : 786
Average : 78.6
Min     : 56
Max     : 95
Above avg: 5 out of 10
```
