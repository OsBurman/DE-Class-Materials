# Walkthrough Script — Part 2
## Day 3: Core Java Fundamentals — Part 2
### Files: `01-arrays.java` · `02-multidimensional-arrays.java` · `03-common-patterns.java`

---

## OVERVIEW (After Break — Before Opening Any File)

**[ACTION]** Have all three Part-2 files visible in the file explorer.

"Welcome back! In Part 1 we learned to control the flow of our programs — making decisions with `if-else` and `switch`, repeating with all four loop types, and steering loops with `break` and `continue`.

Part 2 is where we bring those skills to bear on one of the most fundamental data structures in programming: **arrays**. Then we'll close with patterns — named, reusable templates that appear in virtually every program you'll ever write.

After today, when someone says 'write a program that finds the highest score in a list', you'll have an exact mental template for that. Let's get into it."

---

## FILE 1: `01-arrays.java`

**[ACTION]** Open `01-arrays.java`.

"An array is a fixed-size, ordered container that holds elements of the **same type**. The key word is *ordered* — every element has a numbered slot called an **index**, and indices start at zero, not one."

---

### SECTION 1: Declaration

**[ACTION]** Scroll to Section 1.

"Declaration tells Java: 'I'm going to have an array of this type.' But it doesn't create any storage yet. Notice the brackets go after the type:"

```java
int[] temperatures;
```

"You'll also see `int temperatures[]` occasionally — that's valid Java inherited from C, but it's not idiomatic. We always write `type[] name`."

⚠️ **WATCH OUT:** "Just declaring does not initialize. If you try to use `temperatures[0]` right after declaration without initializing, you'll get a compile error. Declaration and initialization are two separate steps."

---

### SECTION 2: Static initialization

**[ACTION]** Scroll to Section 2.

"When you know all the values upfront, use curly braces:"

```java
String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
```

"Java counts the elements and sets the size automatically. Elements go into slots 0 through 4."

**[ASK]** "If I want the last element and I don't know the array length off the top of my head — how do I access it?"

"`daysOfWeek[daysOfWeek.length - 1]`. Because the last index is always one less than the length."

⚠️ **WATCH OUT:** "Students often write `daysOfWeek[daysOfWeek.length]` — that's off by one. An array of 5 elements has valid indices 0, 1, 2, 3, 4. Index 5 doesn't exist."

---

### SECTION 3: Dynamic initialization

**[ACTION]** Scroll to Section 3.

"When you know the size but not the values yet, use `new`:"

```java
int[] scores = new int[5];
```

"This allocates 5 slots in memory, all set to their default value. Java always initializes arrays — it never leaves garbage values."

**[ASK]** "What are the default values for each type?"

"Point to the comment table: `int` → 0, `double` → 0.0, `boolean` → false, `String` (Object reference) → null."

⚠️ **WATCH OUT:** "The null default for Object types is a common source of `NullPointerException`. If you create a `String[]` and forget to assign values, any method you call on those Strings will throw NPE."

---

### SECTION 4: length property

**[ACTION]** Scroll to Section 4.

"The `length` property — note: no parentheses, unlike `String.length()` which is a method:"

```java
scores.length   // field, no ()
"hello".length()  // method, needs ()
```

"This trips up everyone at least once."

**[ASK]** "Why does String use `.length()` (a method) but arrays use `.length` (a field)?"

"It's a historical quirk of Java's design. Arrays were a language-level construct, not a class. String is a class. Just memorize the difference — no parentheses on arrays."

---

### SECTION 5: Modifying elements

**[ACTION]** Scroll to Section 5.

"Modifying is simple — same as reading but on the left side of the assignment:"

```java
daysOfWeek[1] = "TUESDAY (modified)";
```

"One critical constraint: **arrays are fixed size**. You can change values, but you cannot add or remove slots. If you need a growable collection, that's `ArrayList` — we'll cover that next week."

---

### SECTION 6: Iterating arrays

**[ACTION]** Scroll to Section 6.

"Two main iteration styles. Standard for loop when you need the index:"

```java
for (int i = 0; i < scores.length; i++) {
    System.out.printf("scores[%d] = %d%n", i, scores[i]);
}
```

"Enhanced for-each when you just need the values:"

```java
for (int score : scores) {
    total += score;
}
```

**[ASK]** "When would you prefer the standard for loop over for-each?"

"When you need the index (to print 'item 1, item 2...'), when you need to modify elements in place, or when you need to compare adjacent elements."

---

### SECTION 7: ArrayIndexOutOfBoundsException

**[ACTION]** Scroll to Section 7.

"This is one of the most common runtime errors in Java. The code compiles — but it crashes when you run it because you're asking for a slot that doesn't exist:"

```java
System.out.println(scores[10]);  // scores has length 5
```

"The JVM will tell you exactly what went wrong: `Index 10 out of bounds for length 5`. Always check your bounds."

⚠️ **WATCH OUT:** "This is a RUNTIME exception, not a compile-time one. Your IDE won't catch it. Always double-check loop bounds and hardcoded indices."

→ **TRANSITION:** "Arrays also come with a powerful utility class that handles common operations for you."

---

### SECTION 8: Arrays utility class

**[ACTION]** Scroll to Section 8.

"The `java.util.Arrays` class has a set of static methods that would be tedious to write yourself."

Walk through each:

- **`Arrays.toString()`**: "Without this, printing an array gives you something like `[I@7852e922` — the memory address. `toString()` gives you the readable version."
- **`Arrays.sort()`**: "Sorts ascending, in-place — modifies the original array. Uses a highly optimized algorithm (dual-pivot quicksort for primitives)."
- **`Arrays.binarySearch()`**: "Very fast search — but the array MUST be sorted first. If it's not sorted, results are unpredictable."
- **`Arrays.fill()`**: "Fills all or part of an array with a value."
- **`Arrays.copyOf()`**: "Creates a new array — doesn't modify the original. Can truncate or extend. Extensions are zero-padded."
- **`Arrays.equals()`**: "Compares contents element by element."

⚠️ **WATCH OUT:** "Never use `==` to compare arrays:"

```java
int[] a = {1, 2, 3};
int[] b = {1, 2, 3};
System.out.println(a == b);       // FALSE — compares memory addresses!
System.out.println(Arrays.equals(a, b));  // TRUE — compares contents
```

"This is one of the classic Java gotchas."

---

### SECTIONS 9 & 10: Arrays in methods

**[ACTION]** Scroll to Section 9.

"Arrays are objects, passed by reference. That means when you pass an array to a method, the method operates on the original — changes are visible to the caller."

**[ASK]** "Look at `doubleAllValues(a)`. After the call, what does array `a` contain?"

"Each value doubled — because the method received a reference to the same memory location."

**[ACTION]** Scroll to Section 10.

"Methods can also return arrays. Look at `generateEvens` — it creates a brand new array and returns it. This is the pattern for factory-style methods."

→ **TRANSITION:** "1D arrays are the foundation. Now let's move to 2D arrays — rows and columns."

---

## FILE 2: `02-multidimensional-arrays.java`

**[ACTION]** Open `02-multidimensional-arrays.java`.

"A 2D array is essentially an array of arrays. Think of it as a spreadsheet: you have rows and columns. The type signature doubles up the brackets: `int[][]`."

---

### SECTION 1: Declaration and static initialization

**[ACTION]** Scroll to Section 1.

"Our example is a seating chart:"

```java
int[][] seatingChart = {
    {101, 102, 103, 104},
    {201, 202, 203, 204},
    {301, 302, 303, 304}
};
```

"To access an element, you need TWO indices: `[row][col]`. So `seatingChart[1][3]` is row 1, column 3."

**[ASK]** "What is `seatingChart[2][1]`?"

"302 — back row (index 2), second seat (index 1)."

"Key point: `seatingChart.length` gives you the number of ROWS (3). To get the number of columns, you need `seatingChart[0].length` — the length of a specific row."

"And `Arrays.deepToString()` is the 2D equivalent of `Arrays.toString()` — always use `deep` versions for multi-dimensional arrays."

---

### SECTION 2: Dynamic initialization

**[ACTION]** Scroll to Section 2.

"When you know the dimensions but not the values:"

```java
int[][] gradeGrid = new int[4][3];  // 4 rows, 3 columns
```

"All 12 cells default to 0. Then you fill them in."

---

### SECTION 3: Nested loop iteration

**[ACTION]** Scroll to Section 3.

"The outer loop walks through rows, the inner loop walks through columns — the classic nested loop pattern for 2D data:"

```java
for (int student = 0; student < gradeFilled.length; student++) {
    for (int assignment = 0; assignment < gradeFilled[student].length; assignment++) {
        int grade = gradeFilled[student][assignment];
        ...
    }
}
```

"Walk through the table output. Notice I'm formatting it with `printf` to align the columns — that's the `%-3d` format specifier (left-justified, 3 chars wide)."

⚠️ **WATCH OUT:** "Don't hardcode the dimensions. Use `gradeFilled.length` for rows and `gradeFilled[student].length` for columns. This makes your code work correctly for any size input."

---

### SECTION 4: For-each on 2D arrays

**[ACTION]** Scroll to Section 4.

"With for-each, the outer loop variable is a `int[]` — a whole row:"

```java
for (int[] row : gradeFilled) {
    for (int grade : row) {
        ...
    }
}
```

**[ASK]** "Why is the outer variable `int[]` and not `int`?"

"Because the outer array holds rows, and each row is itself a 1D `int[]` array."

---

### SECTION 6: 3D arrays

**[ACTION]** Scroll to Section 6.

"3D arrays are less common but show up in gaming (3D grids), simulation, image processing (width × height × color channels). Think of it as 'classrooms, each with rows, each with seats':"

```java
int[][][] classrooms = { ... };
classrooms[classroom][row][seat]
```

"In practice, beyond 2D you'll often use a different data structure (List of Lists, objects). But know it exists."

---

### SECTION 7: Jagged arrays

**[ACTION]** Scroll to Section 7.

"Here's something Java does that many languages don't: rows in a 2D array can have **different lengths**. That gives you a 'jagged' shape."

"Look at how Pascal's Triangle is built — row 0 has 1 element, row 4 has 5 elements. Each row is allocated separately with `new int[row + 1]`."

**[ASK]** "What would `pascal[3][2]` be?"

"Let's work it out: row 3 is `{1, 3, 3, 1}`, so index 2 is 3."

⚠️ **WATCH OUT:** "With jagged arrays you can't assume all rows have the same length. Always use `array[row].length` for the inner bound, never the outer `array.length`."

---

### SECTION 8: Flattening

**[ACTION]** Scroll to Section 8.

"A very common real-world operation — taking 2D data and putting it in a 1D array. Two-pass approach: first count the total elements, then fill."

→ **TRANSITION:** "Excellent — you now understand 1D and 2D arrays inside out. The last file ties everything together: programming patterns. These are the templates that experienced developers reach for automatically."

---

## FILE 3: `03-common-patterns.java`

**[ACTION]** Open `03-common-patterns.java`.

"A programming pattern is a named, reusable solution to a problem that comes up repeatedly. When someone asks you to 'sum all values' or 'find the largest element', experienced developers immediately recognize the pattern. After today, you will too."

---

### PATTERN 1: Accumulator

**[ACTION]** Scroll to Pattern 1.

"The accumulator pattern: start with an identity value, then combine every element into it:"

```java
double totalRevenue = 0.0;
for (double revenue : monthlyRevenue) {
    totalRevenue += revenue;
}
```

⚠️ **WATCH OUT:** "The starting value is critical. For addition: start at 0. For multiplication (factorial): start at 1. If you initialize to 0 and multiply, your answer will always be 0."

"Watch the factorial — `factorial = 1`, not 0."

**[ASK]** "What's 0! (zero factorial)? It's 1 — and the pattern handles that correctly because we start at 1 and the loop doesn't execute for `n = 0`."

---

### PATTERN 2: Counter

**[ACTION]** Scroll to Pattern 2.

"The counter pattern: count how many elements satisfy a condition. Always starts at 0, increments when the condition is true:"

```java
int countExcellent = 0;
for (int rating : surveyRatings) {
    if (rating == 5) countExcellent++;
}
```

"Here we have three counters for three categories. This demonstrates that you can run multiple patterns in a single loop pass — one pass, three counters."

**[ASK]** "Why not use three separate loops?"

"Performance and readability. Three loops means three passes through the data. One loop is cleaner and faster — especially on large datasets."

---

### PATTERN 3: Linear Search

**[ACTION]** Scroll to Pattern 3.

"The linear search pattern is something you'll write hundreds of times in your career:"

```java
int foundAt = -1;
for (int i = 0; i < employeeIds.length; i++) {
    if (employeeIds[i].equals(searchId)) {
        foundAt = i;
        break;
    }
}
if (foundAt != -1) { ... }
```

"Two key decisions: initialize to -1 as a 'not found' sentinel, and `break` once found."

**[ASK]** "Why -1? Could we use any other value?"

"-1 is convention because it's not a valid array index. You could use `Integer.MIN_VALUE` or a boolean flag, but -1 is the universal convention in Java."

⚠️ **WATCH OUT:** "Don't forget the `break`! Without it, you'd find the right element but keep looping through the rest of the array. Not a bug for correctness (you'd still have the right index), but it wastes time."

---

### PATTERN 4: Min / Max Finder

**[ACTION]** Scroll to Pattern 4.

"Min/max: initialize with the FIRST element, then compare every remaining element:"

```java
int minPrice = stockPrices[0];
int maxPrice = stockPrices[0];
for (int i = 1; i < stockPrices.length; i++) {
    if (stockPrices[i] < minPrice) minPrice = stockPrices[i];
    if (stockPrices[i] > maxPrice) maxPrice = stockPrices[i];
}
```

**[ASK]** "Why start the loop at index 1?"

"Because we already handled index 0 when we initialized `minPrice` and `maxPrice`. Starting at 1 avoids comparing the first element against itself."

⚠️ **WATCH OUT:** "The most common mistake is initializing `min` to 0 or a 'large number'. If all values are positive and you start min at 0, it will never update because no value is less than 0. Always start with `array[0]`."

"Also — what if the array is empty? You'd get an `ArrayIndexOutOfBoundsException` on `stockPrices[0]`. In real code you'd always check `length > 0` first."

---

### PATTERN 5: Flag Pattern

**[ACTION]** Scroll to Pattern 5.

"The flag is a boolean that answers 'was this condition ever true during the loop?'"

```java
boolean hasLargeTransaction = false;
for (int amount : transactionAmounts) {
    if (amount > 1000) {
        hasLargeTransaction = true;
        break;
    }
}
```

"The flag starts `false`. If we ever find a large transaction, we set it `true` and break. After the loop, the flag tells us the answer."

**[ASK]** "Why break after setting the flag?"

"Once we know there's at least one large transaction, we don't need to keep looking. Early exit is more efficient."

---

### PATTERN 6: Combined Patterns

**[ACTION]** Scroll to Pattern 6.

"This is the most important section. Real programs combine multiple patterns in a single loop. Let's look at the class statistics example:"

```java
for (int score : classScores) {
    sum += score;                              // accumulator
    if (score > highest) highest = score;     // max finder
    if (score < lowest) lowest = score;       // min finder
    if (score >= 60) passingCount++;          // counter
    if      (score >= 90) countA++;           // multiple counters
    else if (score >= 80) countB++;
    ...
}
```

"One pass through the data. Five things happening simultaneously. This is the mindset of an efficient programmer — don't iterate the same data multiple times if you can avoid it."

**[ASK]** "If I split these into five separate loops, would the result be different?"

"The result would be the same — but it would be five times as many iterations. For a small array it doesn't matter. For a dataset with a million records, it absolutely does."

---

### PATTERN 7: Index Tracking

**[ACTION]** Scroll to Pattern 7.

"The last pattern: filtering into a new array using a write index. Two-pass approach:"

```java
// Pass 1: count positives
int positiveCount = 0;
for (int val : rawData) {
    if (val > 0) positiveCount++;
}

// Pass 2: fill new array
int[] positives = new int[positiveCount];
int writeIndex = 0;
for (int val : rawData) {
    if (val > 0) {
        positives[writeIndex] = val;
        writeIndex++;
    }
}
```

"The `writeIndex` tracks where to write the next accepted element. It only advances when we accept an element — so it stays behind `i` whenever we skip values."

**[ASK]** "Is there a way to do this in one pass?"

"Only if you know an upper bound for the result size. You'd allocate the worst-case size, fill it, then copy to the right size at the end. `ArrayList` handles this automatically — one of the reasons it's so useful."

→ **TRANSITION:** "And that's Part 2! You now have arrays — 1D and 2D — and seven named patterns that give you a vocabulary for solving problems. This afternoon, try combining them in the exercises. Before then, any questions?"

---

## SELF-CHECK ✅

- [x] Arrays: declaration, static init, dynamic init, default values, length, element access, modify
- [x] Arrays iteration: standard for (with index), for-each
- [x] ArrayIndexOutOfBoundsException demonstrated and explained
- [x] Arrays utility class: toString, sort, binarySearch, fill, copyOf, copyOfRange, equals
- [x] Passing arrays to methods (by reference — modifies original)
- [x] Returning arrays from methods
- [x] 2D arrays: declaration, static init, dynamic init, [row][col] access, nested iteration
- [x] 2D for-each (outer variable is `int[]`)
- [x] 3D array overview
- [x] Jagged arrays — different row lengths, Pascal's Triangle
- [x] deepToString for 2D printing
- [x] Pattern 1: Accumulator (sum, product, String building)
- [x] Pattern 2: Counter (single and multiple counters)
- [x] Pattern 3: Linear search (-1 sentinel, break on find)
- [x] Pattern 4: Min/Max finder (initialize from array[0])
- [x] Pattern 5: Flag pattern
- [x] Pattern 6: Combined patterns in one pass
- [x] Pattern 7: Index tracking / filter to new array
- [x] Learning Objectives:
  - Create and manipulate arrays ✓
  - Implement control flow logic ✓ (used throughout all patterns)
  - Use loops effectively for iteration ✓ (all loops used in patterns)
  - Apply control flow to solve programming problems ✓ (Pattern 6 especially)
