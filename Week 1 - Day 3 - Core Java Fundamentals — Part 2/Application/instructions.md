# Day 3 Application — Core Java Fundamentals Part 2: Grade Calculator

## Overview

You'll build a **Grade Calculator** — a Java console application that takes an array of test scores, processes them using control flow and loops, and outputs a formatted grade report. Every concept from today is exercised: conditionals, all loop types, break/continue, arrays, and multi-dimensional arrays.

---

## Learning Goals

- Use `if-else` and `switch` for conditional logic
- Implement `for`, `while`, `do-while`, and enhanced `for` loops
- Use `break` and `continue` appropriately
- Create, initialize, and iterate over single and 2D arrays
- Apply programming patterns to real data

---

## Prerequisites

- JDK 17+ installed
- Completion of Day 2 concepts

---

## Project Structure

```
starter-code/
└── src/
    ├── Main.java               ← entry point
    └── GradeCalculator.java    ← TODO: complete this class
```

---

## Part 1 — Complete `GradeCalculator.java`

**Task 1 — `calculateAverage(double[] scores)`**
Use a **standard `for` loop** to sum all scores, then return the average.

**Task 2 — `getLetterGrade(double average)`**
Use an **`if-else if-else` chain** to return the letter grade:
- 90–100 → `"A"`, 80–89 → `"B"`, 70–79 → `"C"`, 60–69 → `"D"`, below 60 → `"F"`

**Task 3 — `getGradeMessage(String letterGrade)`**
Use a **`switch` statement** on the letter grade to return a motivational message for each grade.

**Task 4 — `countPassing(double[] scores)`**
Use a **`while` loop** and a counter to count scores ≥ 60.

**Task 5 — `findHighestScore(double[] scores)`**
Use an **enhanced `for` loop** (`for-each`) to find and return the highest score.

**Task 6 — `findFirstFailing(double[] scores)`**
Use a **`for` loop with `break`** — return the index of the first score below 60. Return `-1` if none found.

**Task 7 — `skipScoresBelow(double[] scores, double threshold)`**
Use a **`for` loop with `continue`** — print only scores at or above the threshold, skipping the rest.

---

## Part 2 — Multi-dimensional Arrays

**Task 8 — `buildGradeBook(String[] students, double[][] scores)`**
You're given a 2D array where each row is a student's scores across 4 tests. Iterate the 2D array and for each student:
- Calculate their average (reuse `calculateAverage`)
- Print: `"[Name]: avg=[X] grade=[Y]"`

**Task 9 — `doWhileMenuDemo()`**
Implement a simple text menu using a **`do-while` loop** that:
1. Prints: `"Enter a score (or -1 to quit):"`
2. Reads a `double` from the keyboard using `Scanner`
3. Prints the letter grade for that score
4. Repeats until the user enters `-1`

---

## Part 3 — Complete `Main.java`

Create a `double[]` of at least 8 scores and call every method from `GradeCalculator`, printing the results clearly. Then create a 3-student grade book and call `buildGradeBook`.

---

## Stretch Goals

1. Add a `printHistogram(double[] scores)` method that prints a bar chart using `*` characters (one `*` per 10 points) for each score using nested loops.
2. Sort the scores array in ascending order using a **bubble sort** algorithm (nested `for` loops, no `Arrays.sort()`).
3. Find the median score by sorting and accessing the middle index.

---

## Submission Checklist

- [ ] Standard `for` loop used
- [ ] `while` loop used
- [ ] `do-while` loop used
- [ ] Enhanced `for` loop used
- [ ] `break` used to exit early
- [ ] `continue` used to skip elements
- [ ] `if-else` chain used
- [ ] `switch` statement used
- [ ] 1D array declared and iterated
- [ ] 2D array declared and iterated with nested loops
