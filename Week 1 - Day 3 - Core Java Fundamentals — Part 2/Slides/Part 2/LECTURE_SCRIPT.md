# Part 2: Arrays & Programming Patterns
## Lecture Script (60 Minutes)

---

## LECTURE SCRIPT - PART 2

*[0 minutes]*

Welcome to Part 2! This afternoon, we're covering arrays. Arrays are one of the most fundamental data structures in programming. You'll use them constantly. In every language. In every type of program.

An array is a collection of elements, all the same type, stored together in memory. Think of it like a list. Or a shelf of boxes. Or a row of lockers. Each locker holds something. Each has a number (the index). To get what's inside, you know the number.

By the end of today, you'll understand arrays deeply. You'll know how to create them, access them, iterate through them, and use them to solve problems. Let's go.

*[2 minutes]*

## Learning Objectives

Here's what we're covering:
- Declare arrays with proper syntax
- Initialize arrays with values
- Access and modify array elements by index
- Iterate through arrays using different loop types
- Work with multi-dimensional arrays (2D arrays)
- Apply common array programming patterns
- Choose between arrays and other data structures

Arrays are fundamental. Everything builds from here. Master arrays, and everything else gets easier.

*[4 minutes]*

## What Are Arrays?

An array is a collection. All elements are the same type. All elements are ordered—they have positions. Positions are called indices. The first element is at index 0. The second at index 1. And so on.

Why arrays? Because they're efficient. They're fast. They're simple. And they're everywhere.

Consider: You have a list of 100 student scores. Without arrays, you'd need 100 variables:

```
int score1 = 85;
int score2  90;
int score3 = 78;
// ... 97 more
```

That's ridiculous. With arrays:

```
int[] scores = new int[100];
```

One variable. Holds 100 values.

Arrays are a fundamental data structure. Understanding them well is critical.

*[6 minutes]*

## Array Declaration

Let's start with the basics: declaring an array.

```java
int[] numbers;
```

Read this as: "numbers is an array of integers."

Breakdown:
- `int`: The type of elements
- `[]`: Indicates array
- `numbers`: The variable name

You can also write:

```java
int numbers[];
```

This works, but the first way is preferred. The bracket belongs with the type, not the variable.

At this point, we've declared the variable. But we haven't allocated memory. No actual array exists yet. Just the variable that will hold a reference to an array.

*[8 minutes]*

## Array Initialization

To create the actual array, we use the `new` keyword:

```java
int[] numbers = new int[10];
```

Now an array of 10 integers is created. Memory is allocated.

What happens next? All elements are initialized to default values:
- 0 for numeric types (int, double, etc.)
- false for boolean
- null for objects

So after creation, numbers[0] through numbers[9] all exist and are 0.

The size is fixed. You can't grow it later. If you need 10, you allocate 10. Not 9, not 11.

*[10 minutes]*

## Array Initialization with Values

You can also initialize with values:

```java
int[] numbers = {1, 2, 3, 4, 5};
```

The size is determined by how many values you provide. Five values, five elements. The compiler figures out the size.

This is shorthand. It's equivalent to:

```java
int[] numbers = new int[5];
numbers[0] = 1;
numbers[1] = 2;
numbers[2] = 3;
numbers[3] = 4;
numbers[4] = 5;
```

Obviously, the shorthand is better. Use it when you know the values.

*[12 minutes]*

## Accessing Array Elements

To get or set an element, use the index in square brackets:

```java
int[] arr = {10, 20, 30, 40, 50};
int first = arr[0];   // Get first element: 10
int third = arr[2];   // Get third element: 30
arr[1] = 99;          // Set second element to 99
```

Index starts at 0. First element is arr[0]. Second is arr[1]. And so on.

Last element? If the array has 5 elements, indices are 0 through 4. Last element is arr[4], which is arr[length - 1].

Accessing outside the valid range throws an exception: `ArrayIndexOutOfBoundsException`. If you try arr[10] on a 5-element array, crash.

*[14 minutes]*

## Array Length

Every array has a `.length` property. It tells you how many elements are in the array.

```java
int[] arr = {1, 2, 3, 4, 5};
System.out.println(arr.length);  // Prints 5
```

This is read-only. You can't change it. It's determined when the array is created.

Why is this useful? Loops! Almost every loop over an array uses the length:

```java
for (int i = 0; i < arr.length; i++) {
  System.out.println(arr[i]);
}
```

This ensures we don't go out of bounds. We loop from 0 to length - 1. Exactly the valid range.

*[16 minutes]*

## Common Array Mistakes

Let me highlight common mistakes:

**Mistake 1: Index out of bounds**

```java
int[] arr = {1, 2, 3};
int x = arr[3];  // ERROR!
```

Array has 3 elements: indices 0, 1, 2. Index 3 doesn't exist. Crash.

**Mistake 2: Null pointer exception**

```java
int[] arr = null;
arr[0] = 5;  // ERROR!
```

arr is null. It doesn't reference an array. Can't access arr[0]. Crash.

**Mistake 3: Missing size**

```java
int[] arr = new int[];  // ERROR!
```

`new int[]` doesn't specify size. Java doesn't know how many elements. Syntax error.

Test your boundaries. Check for null. Know your array sizes.

*[18 minutes]*

## Iterating Through Arrays - for Loop

The traditional for loop is perfect for arrays:

```java
for (int i = 0; i < arr.length; i++) {
  System.out.println(arr[i]);
}
```

Execution:
- i starts at 0
- Condition: i < arr.length
- Each iteration: print arr[i], then increment i
- When i reaches arr.length, condition is false, loop exits

Why this pattern? It's flexible. You can access elements. You know the index. You can iterate forward, backward, skip elements, whatever.

```java
// Backwards
for (int i = arr.length - 1; i >= 0; i--)

// Skip every other
for (int i = 0; i < arr.length; i += 2)
```

This is the most powerful way to iterate.

*[20 minutes]*

## Iterating Through Arrays - Enhanced for Loop

When you just need each element and don't care about the index:

```java
for (int num : arr) {
  System.out.println(num);
}
```

Read as: "For each num in arr, do this."

It's simpler. Cleaner. No index tracking. Automatic.

Disadvantage: You don't have the index. If you need it, use traditional for.

Also, you can't modify the array while iterating with enhanced for. Well, you can, but it's tricky. Use traditional for if you're modifying.

When possible, use enhanced for. It's clearer.

*[22 minutes]*

## Iterating Through Arrays - while Loop

You can also use while:

```java
int i = 0;
while (i < arr.length) {
  System.out.println(arr[i]);
  i++;
}
```

This works. But for is simpler. All the initialization and increment is in one place. With while, it's spread out. Use for.

The only time while is better: When the loop condition is complex or the increment isn't simple. But for arrays, for loops are the way.

*[24 minutes]*

## Arrays of Strings

Arrays can hold any type, including Strings:

```java
String[] names = {"Alice", "Bob", "Charlie"};
System.out.println(names[0]);  // Alice
names[1] = "Bobby";  // Change element
```

Same rules apply. Index from 0. Access with brackets. Length with .length. Iterate same way.

Strings are objects, but you don't need to create them individually. The initialization handles it.

*[26 minutes]*

## Arrays of Objects

Arrays can hold objects:

```java
Person[] people = new Person[10];
people[0] = new Person("Alice", 25);
people[1] = new Person("Bob", 30);
System.out.println(people[0].getName());
```

Create array. Each element is null initially. Create objects and assign to array slots.

We'll cover this more with OOP. For now, know: Arrays of objects work. You must create the objects explicitly.

*[28 minutes]*

## Introduction to Multi-Dimensional Arrays

Arrays can hold arrays. This gives you 2D arrays (and 3D, 4D, etc. though those are rare).

2D array:

```java
int[][] matrix = new int[3][4];
```

Read as: "matrix is an array of 3 arrays, each of size 4."

Think of it as a grid: 3 rows, 4 columns.

```
[0][0]  [0][1]  [0][2]  [0][3]
[1][0]  [1][1]  [1][2]  [1][3]
[2][0]  [2][1]  [2][2]  [2][3]
```

*[30 minutes]*

## 2D Array Initialization and Access

Initialize with values:

```java
int[][] matrix = {
  {1, 2, 3},
  {4, 5, 6},
  {7, 8, 9}
};
```

Access elements:

```java
int value = matrix[0][1];  // First row, second column: 2
matrix[1][2] = 99;  // Third row, third column: 99
```

First index: row. Second index: column. Both zero-indexed.

How many rows? `matrix.length`. How many columns in row i? `matrix[i].length`. (Jagged arrays can have different row lengths.)

*[32 minutes]*

## Iterating 2D Arrays

Most common: nested for loops.

```java
for (int i = 0; i < matrix.length; i++) {
  for (int j = 0; j < matrix[i].length; j++) {
    System.out.println(matrix[i][j]);
  }
}
```

Outer loop: Rows (i). Inner loop: Columns (j). Access each element: matrix[i][j].

With enhanced for:

```java
for (int[] row : matrix) {
  for (int value : row) {
    System.out.println(value);
  }
}
```

First loop: Each row. Second loop: Each element in that row. Cleaner.

*[34 minutes]*

## Array Copying

Important distinction: Reference vs. copy.

Reference copy (shallow copy):

```java
int[] a = {1, 2, 3};
int[] b = a;  // b and a point to same array
b[0] = 99;
System.out.println(a[0]);  // 99! Changed!
```

b doesn't have its own array. It's just another reference to a's array. Modifications affect both.

True copy (deep copy):

```java
int[] b = new int[a.length];
for (int i = 0; i < a.length; i++) {
  b[i] = a[i];
}
```

Or shorter: `int[] b = a.clone();`

Now b has its own array. Modifications to b don't affect a.

This distinction matters. Be aware of what you're doing.

*[36 minutes]*

## Common Array Pattern 1: Finding Maximum

```java
int[] arr = {3, 7, 2, 9, 1};
int max = arr[0];
for (int num : arr) {
  if (num > max) {
    max = num;
  }
}
System.out.println("Max: " + max);  // 9
```

- Start with first element as max
- Loop through all elements
- If current is larger, update max
- End with maximum value

Memorize this pattern. It's everywhere.

*[38 minutes]*

## Common Array Pattern 2: Linear Search

```java
int[] arr = {3, 7, 2, 9, 1};
int target = 7;
boolean found = false;
for (int i = 0; i < arr.length; i++) {
  if (arr[i] == target) {
    System.out.println("Found at index " + i);
    found = true;
    break;
  }
}
if (!found) {
  System.out.println("Not found");
}
```

- Loop through elements
- Check if current equals target
- If found, return index and stop
- If loop completes, not found

This is searching. It's a core pattern.

*[40 minutes]*

## Common Array Pattern 3: Filtering

```java
int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
int[] evens = new int[arr.length];
int count = 0;
for (int num : arr) {
  if (num % 2 == 0) {
    evens[count] = num;
    count++;
  }
}
// Now evens[0] through evens[count-1] are even numbers
```

- Create new array (or resize after)
- Track actual count of matches
- Copy matching elements
- Result: Filtered array

Filtering is selecting elements based on criteria.

*[42 minutes]*

## Common Array Pattern 4: Reversing

```java
int[] arr = {1, 2, 3, 4, 5};
for (int i = 0; i < arr.length / 2; i++) {
  int temp = arr[i];
  arr[i] = arr[arr.length - 1 - i];
  arr[arr.length - 1 - i] = temp;
}
// Now arr is {5, 4, 3, 2, 1}
```

- Swap first and last
- Move inward
- Use temporary variable to hold one value while swapping
- Only loop to middle (otherwise you undo the swaps)

*[44 minutes]*

## Sorting Arrays

Java provides `Arrays.sort()`:

```java
import java.util.Arrays;

int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
Arrays.sort(arr);
// arr is now {1, 1, 2, 3, 4, 5, 6, 9}
```

This is sorted in ascending order. It's efficient (O(n log n)). Use this in practice.

You can also implement bubble sort yourself:

```java
for (int i = 0; i < arr.length; i++) {
  for (int j = 0; j < arr.length - 1 - i; j++) {
    if (arr[j] > arr[j + 1]) {
      int temp = arr[j];
      arr[j] = arr[j + 1];
      arr[j + 1] = temp;
    }
  }
}
```

This is educational but less efficient. Use Arrays.sort().

*[46 minutes]*

## Arrays vs ArrayLists

Quick preview: There's another way to store collections called ArrayList.

**Arrays:**
- Fixed size at creation
- Fast access by index
- Must manage size yourself
- Good for: Known size, performance critical

**ArrayLists:**
- Dynamic size (grows as needed)
- Slight overhead
- Easy to add/remove
- Good for: Unknown size, convenience

```java
ArrayList<Integer> list = new ArrayList<>();
list.add(1);
list.add(2);
list.remove(0);
```

We'll cover ArrayList more. For now, know arrays are simpler and faster for fixed collections.

*[48 minutes]*

## Multi-Dimensional Array Example: Tic-Tac-Toe

Real-world example: Tic-tac-toe board.

```java
String[][] board = {
  {"X", "O", " "},
  {" ", "X", " "},
  {"O", " ", "X"}
};
```

3x3 grid. Each cell is a String: "X", "O", or " " (empty).

To check a cell: `board[row][col]`
To place: `board[row][col] = "X"`

To check for winner: Iterate and check rows, columns, diagonals.

Common pattern in games: 2D arrays for boards/grids.

*[50 minutes]*

## Null Values in Arrays

Arrays of objects can contain null:

```java
String[] names = new String[3];
names[0] = "Alice";
names[1] = null;  // Empty slot
```

null means "no object here." Different from empty string.

```java
if (names[1] == null) {
  System.out.println("Empty");
}
```

If you access a null object:

```java
names[1].toUpperCase();  // NullPointerException!
```

Crash. Always check for null before using.

*[52 minutes]*

## Variable-Length Arguments (varargs)

Methods can accept variable number of arguments:

```java
public void printNumbers(int... numbers) {
  for (int num : numbers) {
    System.out.println(num);
  }
}

// Call with any number of arguments
printNumbers(1, 2, 3);
printNumbers(10, 20);
```

`int... numbers` means numbers is an array of ints. You can pass 1, 2, 10, or any number.

Inside the method, it's just an array. Iterate with for loop.

Useful for flexible method signatures.

*[54 minutes]*

## Passing Arrays to Methods

Arrays are passed by reference:

```java
public void modifyArray(int[] arr) {
  arr[0] = 99;  // Modifies the original array
}

int[] myArray = {1, 2, 3};
modifyArray(myArray);
System.out.println(myArray[0]);  // 99
```

Methods can modify the original array. Be aware. Document if your method modifies its parameters. This is different from primitives, which are passed by value.

*[56 minutes]*

## Part 2 Summary

Let's recap:
- Arrays: Collections of same type
- Declaration: `int[] arr;`
- Initialization: `new int[10]` or `{1, 2, 3}`
- Access: `arr[index]`
- Iteration: for, enhanced for, while
- Multi-dimensional: `int[][]`
- Common patterns: max, search, filter, reverse, sort
- Passing: Arrays are passed by reference
- Choose wisely: Arrays for fixed size, ArrayList for dynamic

Arrays are fundamental. Practice them. Memorize the patterns.

*[60 minutes]*

---

## End of Part 2

Excellent work! You've learned control flow and arrays. These are the foundation of programming.

Tomorrow, we start OOP. Classes and objects. You're building toward full application development.

For now, practice arrays. Work with them in lab. Get comfortable. They're everywhere.

---
