# Exercise 02 — Methods & Arrays

## Overview

Build a **Statistics Calculator** that reads a list of student test scores and computes a full statistical report. You will practice writing reusable methods and working with both 1D and 2D arrays.

---

## Concepts Covered

- Defining and calling static methods
- Method parameters and return types
- Method overloading (same name, different signatures)
- `void` vs return-value methods
- 1D arrays — declaration, initialization, iteration
- 2D arrays — the grade book
- Sorting arrays with `Arrays.sort()`
- `Arrays` utility class
- Passing arrays to methods

---

## Application Features

1. Store scores for multiple students across multiple subjects
2. Calculate: mean, median, mode, min, max, range, standard deviation for a score set
3. Use overloaded methods to work with both `int[]` and `double[]`
4. Print a grade book table for all students
5. Find the top and bottom performer per subject

---

## TODOs

- [ ] **TODO 1** — Implement `double average(int[] scores)` — returns the mean
- [ ] **TODO 2** — Implement `double median(int[] scores)` — sort a copy, return middle value
- [ ] **TODO 3** — Implement `int mode(int[] scores)` — return the most frequent value
- [ ] **TODO 4** — Implement `double standardDeviation(int[] scores)` — √(variance)
- [ ] **TODO 5** — Implement `String letterGrade(double avg)` — A/B/C/D/F
- [ ] **TODO 6** — Overload `average` to accept `double[]` instead of `int[]`
- [ ] **TODO 7** — Implement `printGradeBook(String[] names, int[][] grades, String[] subjects)` — formatted table
- [ ] **TODO 8** — Implement `topPerformer(String[] names, int[][] grades, int subjectIndex)` — returns name

---

## Running the Program

```bash
cd starter-code/src
javac Main.java
java Main
```

## Sample Output

```
===== Score Analysis: Math =====
Scores:  [72, 85, 91, 68, 85, 77, 95, 62, 85, 88]
Mean:    80.80
Median:  85.00
Mode:    85
Min:     62   Max: 95   Range: 33
Std Dev: 10.21

=========== GRADE BOOK ===========
Student       Math   Sci   Eng   Avg   Grade
------------------------------------------
Alice           91    88    94  91.0     A
Bob             85    79    82  82.0     B
...
```
