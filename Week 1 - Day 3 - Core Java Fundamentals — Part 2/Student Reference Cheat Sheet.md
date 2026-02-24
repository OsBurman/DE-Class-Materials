# Day 3 Review — Core Java Fundamentals Part 2
## Quick Reference Guide

---

## 1. Control Flow — Conditionals

**if / else if / else:**
```java
if (score >= 90) {
    grade = "A";
} else if (score >= 80) {
    grade = "B";
} else if (score >= 70) {
    grade = "C";
} else {
    grade = "F";
}
// First matching condition executes; the rest are skipped
```

**switch statement:**
```java
switch (dayOfWeek) {
    case 1:
        System.out.println("Monday");
        break;         // REQUIRED — without it, execution falls through to next case
    case 2:
        System.out.println("Tuesday");
        break;
    default:
        System.out.println("Other");
}
```

**Switch expression (Java 14+) — preferred modern form:**
```java
String day = switch (dayOfWeek) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    case 3 -> "Wednesday";
    default -> "Other";
};
// No break needed; no fall-through; can return a value
```

**Ternary (one-line conditional):**
```java
String label = (age >= 18) ? "Adult" : "Minor";
```

---

## 2. Loops

**for loop — when you know the number of iterations:**
```java
for (int i = 0; i < 10; i++) {
    System.out.println(i);
}
// i = 0, 1, 2, ... 9 (stops before 10)
```

**enhanced for loop (for-each) — iterate over array or collection:**
```java
int[] numbers = {1, 2, 3, 4, 5};
for (int num : numbers) {
    System.out.println(num);
}
// No index available; cannot modify the array through num
```

**while loop — condition checked before each iteration:**
```java
int count = 0;
while (count < 5) {
    System.out.println(count);
    count++;
}
// If count starts >= 5, loop body never executes
```

**do-while loop — body executes at least once:**
```java
int count = 0;
do {
    System.out.println(count);
    count++;
} while (count < 5);
// Body always runs at least once — condition checked AFTER
```

**break and continue:**
```java
for (int i = 0; i < 10; i++) {
    if (i == 5) break;      // exit loop entirely
    if (i % 2 == 0) continue;  // skip rest of this iteration, go to next
    System.out.println(i);  // prints 1, 3 — then loop ends at i=5
}
```

**Choosing a loop:**
| Situation | Use |
|---|---|
| Know exact number of iterations | `for` |
| Iterating over array/collection | enhanced `for` |
| Condition unknown before starting | `while` |
| Must execute at least once | `do-while` |

---

## 3. Arrays

**Declaration and initialization:**
```java
// Declare and allocate (all elements default to 0 / false / null):
int[] scores = new int[5];          // [0, 0, 0, 0, 0]

// Declare with initial values:
int[] scores = {95, 87, 42, 78, 91};

// Two-step:
int[] scores;
scores = new int[5];
```

**Accessing elements:**
```java
scores[0]          // first element (zero-indexed)
scores[4]          // last element of a size-5 array
scores.length      // 5 — number of elements (not a method, it's a field)

// ArrayIndexOutOfBoundsException if you access scores[5] on a size-5 array
```

**Common array operations:**
```java
// Iterate with for loop
for (int i = 0; i < scores.length; i++) {
    System.out.println(scores[i]);
}

// Iterate with enhanced for
for (int score : scores) {
    System.out.println(score);
}

// Find max value
int max = scores[0];
for (int score : scores) {
    if (score > max) max = score;
}

// Sum and average
int sum = 0;
for (int score : scores) sum += score;
double avg = (double) sum / scores.length;   // cast to get decimal result
```

**Arrays utility class:**
```java
import java.util.Arrays;

Arrays.sort(scores);                          // sort in-place (ascending)
Arrays.toString(scores)                       // "[95, 87, 42, 78, 91]"
Arrays.fill(scores, 0)                        // set all elements to 0
Arrays.copyOf(scores, 3)                      // new array with first 3 elements
Arrays.copyOfRange(scores, 1, 4)              // elements at index 1, 2, 3
int idx = Arrays.binarySearch(scores, 87);    // requires sorted array first
```

---

## 4. Multi-Dimensional Arrays

```java
// 2D array declaration:
int[][] matrix = new int[3][4];          // 3 rows, 4 columns

// 2D array with values:
int[][] grid = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

// Access:
grid[0][0]    // row 0, col 0 → 1
grid[1][2]    // row 1, col 2 → 6
grid.length          // 3 (number of rows)
grid[0].length       // 3 (number of columns in row 0)

// Iterate:
for (int row = 0; row < grid.length; row++) {
    for (int col = 0; col < grid[row].length; col++) {
        System.out.print(grid[row][col] + " ");
    }
    System.out.println();
}
```

---

## 5. Common Programming Patterns

**Find min/max:**
```java
int min = arr[0];
for (int val : arr) {
    if (val < min) min = val;
}
```

**Linear search:**
```java
int target = 42;
int foundIndex = -1;
for (int i = 0; i < arr.length; i++) {
    if (arr[i] == target) {
        foundIndex = i;
        break;
    }
}
// foundIndex == -1 means not found
```

**Count occurrences:**
```java
int count = 0;
for (int val : arr) {
    if (val == target) count++;
}
```

**Reverse an array:**
```java
int left = 0;
int right = arr.length - 1;
while (left < right) {
    int temp = arr[left];
    arr[left] = arr[right];
    arr[right] = temp;
    left++;
    right--;
}
```

**Accumulate into a String:**
```java
StringBuilder sb = new StringBuilder();
for (int val : arr) {
    sb.append(val).append(", ");
}
// Remove trailing ", " if needed: sb.delete(sb.length()-2, sb.length())
String result = sb.toString();
```

---

## 6. switch vs if-else Decision Guide

| Situation | Prefer |
|---|---|
| Checking exact equality against fixed values | `switch` |
| Ranges (score >= 90) | `if-else` |
| Multiple conditions combined with && / \|\| | `if-else` |
| Known set of string/int/enum cases | `switch` |
| Complex branching with diverse conditions | `if-else` |

---

## 7. Common Pitfalls

| Mistake | Result |
|---|---|
| `arr[arr.length]` | `ArrayIndexOutOfBoundsException` |
| Forgetting `break` in switch | Fall-through to next case |
| Infinite `while` loop (condition never false) | Program hangs |
| Integer division: `7 / 2` | `3`, not `3.5` — cast first |
| Modifying array via enhanced for | No effect on original array |
| Comparing `String` with `==` inside conditions | Wrong result — use `.equals()` |
