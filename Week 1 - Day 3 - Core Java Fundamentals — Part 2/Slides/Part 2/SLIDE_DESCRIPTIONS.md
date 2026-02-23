# Part 2: Arrays & Programming Patterns
## Slide Descriptions

---

### SLIDE 1: Welcome - Day 3 Part 2
**Visual:** Transition slide
**Content:**
- Welcome back! Part 2: Arrays
- Collections of data
- Fundamental data structure
- Used everywhere in programming

---

### SLIDE 2: Learning Objectives - Part 2
**Visual:** Bulleted objectives
**Content:**
- Declare arrays with proper syntax
- Initialize arrays with values
- Access and modify array elements
- Iterate through arrays
- Use multi-dimensional arrays
- Apply common programming patterns
- Choose appropriate data structures

---

### SLIDE 3: What Are Arrays?
**Visual:** Array concept visualization
**Content:**
- Array: Collection of elements of same type
- Ordered: Elements have specific positions (indices)
- Fixed size: Created with specific length
- Indexed: Access by position (0, 1, 2, ...)
- Zero-indexed: First element at index 0
- Single block of memory
- Very efficient access

---

### SLIDE 4: Why Use Arrays?
**Visual:** Use cases
**Content:**
- Store multiple values efficiently
- Avoid creating many variables
- Process groups of data
- Work with unknown quantities
- Foundation for more complex data structures
- Required for most programs
- Essential for interviews

---

### SLIDE 5: Array Declaration
**Visual:** Syntax examples
**Content:**
- Two ways to declare:
  ```
  int[] numbers;  // Preferred
  int numbers[];  // Works but less common
  ```
- Type[] name
- Type: What kind of elements (int, double, String, etc.)
- []: Indicates array
- Name: Variable name
- At this point: Only declaration, no memory allocated

---

### SLIDE 6: Array Initialization with new
**Visual:** Memory allocation
**Content:**
- new keyword: Allocates memory
- Syntax: `int[] numbers = new int[10];`
- Breakdown:
  - int[] numbers: Declare array variable
  - new int[10]: Create array with 10 elements
- Size must be positive integer
- All elements initialized to default:
  - 0 for numeric types
  - false for boolean
  - null for objects
- Size is fixed after creation

---

### SLIDE 7: Array Initialization with Values
**Visual:** Initialization examples
**Content:**
- Initialize with values using braces:
  ```
  int[] numbers = {1, 2, 3, 4, 5};
  ```
- Size determined by values count
- Type must match elements
- Shorthand: Braces with values
- Can't change size after creation
- Common pattern: Known values at start

---

### SLIDE 8: Accessing Array Elements
**Visual:** Index access
**Content:**
- Access using index in brackets:
  ```
  int[] arr = {10, 20, 30, 40, 50};
  int first = arr[0];   // 10
  int third = arr[2];   // 30
  ```
- Index starts at 0
- Index ends at length - 1
- Out of bounds: ArrayIndexOutOfBoundsException
- Read and write: arr[2] = 99;

---

### SLIDE 9: Array Length
**Visual:** Length property
**Content:**
- .length property: Number of elements
- Syntax: `array.length`
- Read-only: Can't change it
- Last valid index: length - 1
- Example:
  ```
  int[] arr = {1, 2, 3, 4, 5};
  System.out.println(arr.length);  // 5
  int last = arr[arr.length - 1];  // 5
  ```
- Essential for loops: for (int i = 0; i < arr.length; i++)

---

### SLIDE 10: Common Array Mistakes
**Visual:** Pitfalls
**Content:**
- Mistake 1: Index out of bounds
  ```
  int[] arr = {1, 2, 3};
  int x = arr[3];  // ERROR! Only 0, 1, 2 valid
  ```
- Mistake 2: Null pointer exception
  ```
  int[] arr = null;
  arr[0] = 5;  // ERROR! arr doesn't exist
  ```
- Mistake 3: Forgetting size
  ```
  int[] arr = new int[];  // ERROR! Need size
  ```

---

### SLIDE 11: Iterating Through Arrays - for Loop
**Visual:** Loop examples
**Content:**
- Traditional for loop:
  ```
  for (int i = 0; i < arr.length; i++) {
    System.out.println(arr[i]);
  }
  ```
- Access each element
- Use index for modifications
- Can go backwards, skip, etc.
- Most flexible iteration

---

### SLIDE 12: Iterating Through Arrays - Enhanced for
**Visual:** for-each example
**Content:**
- Enhanced for loop:
  ```
  for (int num : arr) {
    System.out.println(num);
  }
  ```
- Cleaner when you don't need index
- Automatic iteration
- Can't modify array while iterating (usually)
- Read-only access to element
- Better when index not needed

---

### SLIDE 13: Iterating Through Arrays - while Loop
**Visual:** while iteration
**Content:**
- while loop:
  ```
  int i = 0;
  while (i < arr.length) {
    System.out.println(arr[i]);
    i++;
  }
  ```
- More manual than for
- Use when iteration logic complex
- Less common for arrays
- Works but prefer for loops

---

### SLIDE 14: Array of Strings
**Visual:** String array examples
**Content:**
- Arrays can hold Strings:
  ```
  String[] names = {"Alice", "Bob", "Charlie"};
  String first = names[0];  // "Alice"
  names[1] = "Bobby";  // Modify
  ```
- Same rules apply
- Access with index
- Iterate same way
- Common in programs

---

### SLIDE 15: Array of Objects
**Visual:** Object array concept
**Content:**
- Arrays can hold any type:
  ```
  Person[] people = new Person[10];
  people[0] = new Person("Alice");
  ```
- Initially null
- Must create objects
- Access properties: people[0].name
- More on this with OOP
- Powerful for managing collections

---

### SLIDE 16: Introduction to Multi-dimensional Arrays
**Visual:** 2D grid visualization
**Content:**
- Multi-dimensional: Array of arrays
- 2D array: Rows and columns
- Syntax: `int[][] matrix = new int[3][4];`
- First bracket: Rows
- Second bracket: Columns
- Think: Table or grid
- 3D, 4D possible but rare

---

### SLIDE 17: 2D Array Declaration and Initialization
**Visual:** 2D examples
**Content:**
- Declaration:
  ```
  int[][] matrix = new int[3][4];
  ```
- With values:
  ```
  int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
  };
  ```
- Jagged arrays: Different row lengths possible
- Access: matrix[row][col]

---

### SLIDE 18: Accessing 2D Array Elements
**Visual:** Index access examples
**Content:**
- Access element:
  ```
  int value = matrix[0][1];  // First row, second column
  matrix[1][2] = 99;  // Modify
  ```
- First index: Row
- Second index: Column
- Both zero-indexed
- Can iterate in nested loops
- Common: matrix[i][j]

---

### SLIDE 19: Iterating 2D Arrays
**Visual:** Nested loop pattern
**Content:**
- Nested for loops:
  ```
  for (int i = 0; i < matrix.length; i++) {
    for (int j = 0; j < matrix[i].length; j++) {
      System.out.println(matrix[i][j]);
    }
  }
  ```
- Outer: Rows
- Inner: Columns
- Access each element
- Print in grid format
- Quadratic complexity

---

### SLIDE 20: 2D Array with Enhanced for
**Visual:** Cleaner iteration
**Content:**
- Enhanced for:
  ```
  for (int[] row : matrix) {
    for (int value : row) {
      System.out.println(value);
    }
  }
  ```
- First loop: Each row
- Second loop: Each value in row
- Cleaner than indices
- No index access

---

### SLIDE 21: Array Copying
**Visual:** Copy concepts
**Content:**
- Shallow copy (reference):
  ```
  int[] a = {1, 2, 3};
  int[] b = a;  // b points to same array
  b[0] = 99;  // a[0] also changes
  ```
- Deep copy (new array):
  ```
  int[] b = new int[a.length];
  for (int i = 0; i < a.length; i++) {
    b[i] = a[i];
  }
  ```
- Or: `int[] b = a.clone();`
- Important distinction!

---

### SLIDE 22: Common Array Patterns - Finding Maximum
**Visual:** Max pattern
**Content:**
- Find maximum value:
  ```
  int max = arr[0];
  for (int num : arr) {
    if (num > max) {
      max = num;
    }
  }
  System.out.println("Max: " + max);
  ```
- Initialize to first element
- Compare each element
- Keep largest
- Memorize pattern

---

### SLIDE 23: Common Array Patterns - Linear Search
**Visual:** Search pattern
**Content:**
- Find element:
  ```
  int target = 5;
  for (int i = 0; i < arr.length; i++) {
    if (arr[i] == target) {
      System.out.println("Found at index " + i);
      return i;
    }
  }
  System.out.println("Not found");
  return -1;
  ```
- Check each element
- Return index when found
- Return -1 if not found

---

### SLIDE 24: Common Array Patterns - Filtering
**Visual:** Filter pattern
**Content:**
- Create new array with filtered elements:
  ```
  int[] evens = new int[arr.length];
  int count = 0;
  for (int num : arr) {
    if (num % 2 == 0) {
      evens[count] = num;
      count++;
    }
  }
  ```
- Create larger array initially
- Copy matching elements
- Track actual count
- Resize or ignore unused

---

### SLIDE 25: Common Array Patterns - Reversing
**Visual:** Reverse pattern
**Content:**
- Reverse array:
  ```
  for (int i = 0; i < arr.length / 2; i++) {
    int temp = arr[i];
    arr[i] = arr[arr.length - 1 - i];
    arr[arr.length - 1 - i] = temp;
  }
  ```
- Swap first and last
- Move toward middle
- Use temporary variable
- Only loop to middle

---

### SLIDE 26: Common Array Patterns - Summing
**Visual:** Sum pattern
**Content:**
- Sum array elements:
  ```
  int sum = 0;
  for (int num : arr) {
    sum += num;
  }
  System.out.println("Sum: " + sum);
  ```
- Initialize accumulator
- Add each element
- Simple and common
- Works for any numeric array

---

### SLIDE 27: Array vs ArrayList (Preview)
**Visual:** Comparison
**Content:**
- Array: Fixed size, fast access
- ArrayList: Dynamic size, slight overhead
- Arrays: int[], String[], Object[]
- ArrayList: ArrayList<Integer>, ArrayList<String>
- Arrays: Built-in, primitive friendly
- ArrayList: Part of Collections framework
- Use arrays for: Known size, performance critical
- Use ArrayList for: Dynamic size needed
- We'll cover ArrayList tomorrow

---

### SLIDE 28: Sorting Arrays - Bubble Sort (Concept)
**Visual:** Bubble sort visualization
**Content:**
- Bubble Sort: Simple algorithm
- Compare adjacent elements
- Swap if wrong order
- Repeat until sorted
- O(n²) complexity
- Not efficient for large arrays
- Good for learning
- Java has Arrays.sort() (better)

---

### SLIDE 29: Sorting Arrays - Java Built-in
**Visual:** Arrays.sort()
**Content:**
- Import: `import java.util.Arrays;`
- Usage: `Arrays.sort(arr);`
- Sorts in ascending order
- Modifies original array
- O(n log n) complexity
- Efficient and reliable
- Much better than manual sorting
- Use this in practice

---

### SLIDE 30: Multidimensional Array Real-World Example
**Visual:** Game board example
**Content:**
- Tic-tac-toe board:
  ```
  String[][] board = {
    {"X", "O", "X"},
    {"O", "X", " "},
    {"X", " ", "O"}
  };
  ```
- 3x3 grid
- Track state
- Check winner
- Common pattern in games

---

### SLIDE 31: Null Values in Arrays
**Visual:** Null concept
**Content:**
- Array of objects can be null:
  ```
  String[] names = new String[3];
  names[0] = "Alice";
  names[1] = null;  // Empty slot
  ```
- null: No object assigned
- Different from empty string ""
- Check: if (names[1] == null)
- NullPointerException if access null
- Use defensive programming

---

### SLIDE 32: Variable-Length Argument (varargs)
**Visual:** Varargs syntax
**Content:**
- Methods with variable arguments:
  ```
  public void printNumbers(int... numbers) {
    for (int num : numbers) {
      System.out.println(num);
    }
  }
  ```
- Call: printNumbers(1, 2, 3, 4, 5);
- Or: printNumbers(10, 20);
- Parameter is array
- Flexible method signatures
- Advanced but useful

---

### SLIDE 33: ArrayList Preview
**Visual:** ArrayList introduction
**Content:**
- Arrays: Fixed size
- ArrayList: Dynamic size
- Import: `import java.util.ArrayList;`
- Create: `ArrayList<Integer> list = new ArrayList<>();`
- Add: `list.add(5);`
- Access: `list.get(0);`
- Remove: `list.remove(0);`
- Size: `list.size();`
- More flexible than arrays

---

### SLIDE 34: When to Use Arrays
**Visual:** Array use cases
**Content:**
- Use arrays when:
  - Size is known and fixed
  - Need fast random access
  - Memory efficiency critical
  - Simple fixed collection
- Examples:
  - Scores of 10 students
  - Coordinates (x, y)
  - Configuration values
- Best for: Performance-critical, known size

---

### SLIDE 35: When to Use ArrayList
**Visual:** ArrayList use cases
**Content:**
- Use ArrayList when:
  - Size unknown or variable
  - Need to add/remove frequently
  - Convenience important
  - Part of collections processing
- Examples:
  - User input (unknown count)
  - Search results (variable count)
  - Shopping cart (add/remove items)
- Best for: Flexibility, dynamic data

---

### SLIDE 36: Passing Arrays to Methods
**Visual:** Parameter passing
**Content:**
- Arrays passed by reference:
  ```
  public void modifyArray(int[] arr) {
    arr[0] = 99;  // Modifies original
  }
  ```
- Changes affect original array
- Different from primitives
- Method can call: modifyArray(myArray);
- Be aware of side effects
- Document if method modifies array

---

### SLIDE 37: Array Bounds Best Practices
**Visual:** Safety guidelines
**Content:**
- Check bounds before access:
  ```
  if (index >= 0 && index < arr.length) {
    value = arr[index];
  }
  ```
- Defensive programming
- Prevent crashes
- Especially with user input
- Check edge cases
- Test boundary conditions

---

### SLIDE 38: Part 2 Summary
**Visual:** Key concepts recap
**Content:**
- Arrays: Collections of same type
- Declaration: int[] arr;
- Initialization: new int[10] or {1, 2, 3}
- Access: arr[index]
- Iteration: for, enhanced for, while
- Multi-dimensional: int[][]
- Common patterns: max, search, filter
- Choose: Array or ArrayList wisely

---

### SLIDE 39: Key Takeaways
**Visual:** Important reminders
**Content:**
- Arrays are zero-indexed
- Size is fixed at creation
- Out of bounds causes exception
- Multi-dimensional: Arrays of arrays
- Learn the common patterns
- Use Arrays.sort() for sorting
- Arrays best for: Known size
- ArrayList best for: Dynamic size
- Practice with examples

---

### SLIDE 40: Hands-On Exercises
**Visual:** Programming tasks
**Content:**
- Write programs that:
  1. Create and iterate arrays
  2. Find max/min values
  3. Search for elements
  4. Work with 2D arrays
  5. Copy and reverse arrays
- Test edge cases
- Get comfortable with patterns
- Ask for help

---

### SLIDE 41: Lab Time
**Visual:** Coding icon
**Content:**
- Work on array problems
- Implement search algorithm
- Build simple game (tic-tac-toe)
- Practice iteration patterns
- Instructors available
- Collaborate with peers
- Ask questions

---

### SLIDE 42: Q&A Session
**Visual:** Question mark
**Content:**
- Array questions?
- Multi-dimensional arrays unclear?
- Common patterns confusing?
- ArrayList curiosity?
- Ask now!

---

### SLIDE 43: What's Next
**Visual:** Preview
**Content:**
- Day 4: Object-Oriented Programming
- Classes and objects
- Methods and properties
- Encapsulation and access modifiers
- Foundation for all Java development

---

### SLIDE 44: Day 3 Complete
**Visual:** Completion graphic
**Content:**
- Part 1: Control flow and loops
- Part 2: Arrays and patterns
- Foundation: Strong
- You can now:
  - Write decision logic
  - Iterate efficiently
  - Work with collections
- Great work today!

---
