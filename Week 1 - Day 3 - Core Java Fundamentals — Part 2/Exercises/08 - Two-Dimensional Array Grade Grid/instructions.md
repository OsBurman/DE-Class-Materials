# Exercise 08 - Two-Dimensional Array Grade Grid

## Objective
Declare, initialize, and traverse a two-dimensional array using nested `for` loops, access elements with `grid[row][col]`, and compute per-row averages.

---

## Background
- A 2D array is an "array of arrays": `int[][] grid = new int[3][4]` creates 3 rows of 4 columns.
- Literal initialization: `int[][] grades = {{85, 90, 78}, {92, 88, 76}, {70, 65, 80}}`.
- `grades.length` — number of rows.
- `grades[r].length` — number of columns in row `r` (useful for jagged arrays).
- Standard traversal: outer loop over rows, inner loop over columns.

---

## Requirements

### Part 1 — Print the Raw Grid
- Declare `int[][] grades = {{85, 90, 78}, {92, 88, 76}, {70, 65, 80}}`.
- Use nested `for` loops to print each row as: `Row 0: 85  90  78`
  - Use `System.out.printf("%-5d", grades[r][c])` for aligned columns.
  - Print a newline after each row.

### Part 2 — Row Averages
- For each row, compute the average score (as a `double`).
- Print: `Row 0 average: 84.33`
- Format the average to 2 decimal places using `System.out.printf("Row %d average: %.2f%n", r, avg)`.

### Part 3 — Find the Highest Score in the Grid
- Traverse the entire 2D array with nested loops.
- Find the overall highest score and record which row and column it is in.
- Print: `Highest score: 92 at row 1, col 0`

### Part 4 — Column Totals
- Compute the total of each column across all rows.
- Print: `Col 0 total: 247`

---

## Hints
- Seed the "highest score" search variable with `grades[0][0]`, not `0`.
- For row averages: sum all elements in the row, then divide by `grades[r].length`.
- For column totals: the outer loop iterates over columns (`c`), the inner loop over rows (`r`).
- `%-5d` in `printf` left-justifies an integer in a field of width 5.

---

## Expected Output
```
=== Part 1: Raw Grid ===
Row 0: 85   90   78   
Row 1: 92   88   76   
Row 2: 70   65   80   

=== Part 2: Row Averages ===
Row 0 average: 84.33
Row 1 average: 85.33
Row 2 average: 71.67

=== Part 3: Highest Score ===
Highest score: 92 at row 1, col 0

=== Part 4: Column Totals ===
Col 0 total: 247
Col 1 total: 243
Col 2 total: 234
```
