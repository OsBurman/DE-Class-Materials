# Part 1: Control Flow & Loops
## Slide Descriptions

---

### SLIDE 1: Welcome - Day 3 Part 1
**Visual:** Title slide
**Content:**
- Welcome to Day 3, Part 1
- Control flow: Making decisions in code
- Loops: Repeating code efficiently
- Critical for every program you'll write

---

### SLIDE 2: Learning Objectives - Part 1
**Visual:** Bulleted objectives
**Content:**
- Understand if-else conditional logic
- Use switch statements for multiple conditions
- Implement for loops for iterations
- Implement while and do-while loops
- Use break and continue statements
- Choose appropriate control flow structures

---

### SLIDE 3: What is Control Flow?
**Visual:** Program flow diagram
**Content:**
- Control flow: Directing program execution
- Sequential: Code runs line by line (default)
- Conditional: Code runs based on conditions
- Loops: Code repeats based on conditions
- Three main types: if, loops, switch
- Every program uses control flow

---

### SLIDE 4: Introduction to if Statements
**Visual:** If statement concept
**Content:**
- if: Execute code if condition is true
- Basic syntax:
  ```
  if (condition) {
    // Code here runs if true
  }
  ```
- Condition: boolean expression
- Result: true or false
- Braces: { } contain code block
- Without braces: Only next line executes

---

### SLIDE 5: if-else Statements
**Visual:** If-else flow
**Content:**
- if-else: Two paths of execution
- Syntax:
  ```
  if (condition) {
    // Runs if true
  } else {
    // Runs if false
  }
  ```
- Only one block executes
- else is optional
- One or the other, never both

---

### SLIDE 6: if-else-if Chains
**Visual:** Multiple conditions
**Content:**
- Multiple conditions: if-else-if
- Syntax:
  ```
  if (condition1) {
    // First
  } else if (condition2) {
    // Second
  } else if (condition3) {
    // Third
  } else {
    // Default
  }
  ```
- First true block executes
- Rest are skipped
- else is optional at end

---

### SLIDE 7: Nested if Statements
**Visual:** Nested blocks
**Content:**
- Nesting: if inside if
- Example:
  ```
  if (age >= 18) {
    if (hasLicense) {
      // Can drive
    }
  }
  ```
- Multiple conditions must all be true
- Can nest deeply, but limits readability
- Use logical operators (&&) instead

---

### SLIDE 8: Common if Mistakes
**Visual:** Pitfalls
**Content:**
- Mistake 1: Using = instead of ==
  ```
  if (x = 5) { }  // WRONG: assigns 5
  if (x == 5) { }  // RIGHT: compares
  ```
- Mistake 2: Forgetting braces
  ```
  if (x > 5)
    System.out.println("Big");
    System.out.println("Always");  // Always runs!
  ```
- Mistake 3: Semicolon after if
  ```
  if (x > 5); {  // WRONG: empty if
    System.out.println("Big");
  }
  ```

---

### SLIDE 9: Introduction to switch Statements
**Visual:** Switch concept
**Content:**
- switch: Multiple conditions, one value
- Alternative to many if-else-if chains
- Syntax:
  ```
  switch (value) {
    case 1:
      // code
      break;
    case 2:
      // code
      break;
    default:
      // code
  }
  ```
- Compares value to cases
- Executes matching case
- break exits the switch

---

### SLIDE 10: switch Statement Details
**Visual:** Switch flow
**Content:**
- value: Expression being tested
- case: Possible value to match
- break: Exit the switch
- default: If no case matches (optional)
- Falls through if no break
- Works with: int, String, enum
- More efficient than multiple if statements

---

### SLIDE 11: Switch Fall-Through
**Visual:** Fall-through example
**Content:**
- Without break: Code continues to next case
- Useful sometimes, usually a bug
- Example:
  ```
  switch (day) {
    case 6:
      System.out.println("Saturday");
    case 7:
      System.out.println("Sunday");
      break;
    default:
      System.out.println("Weekday");
  }
  ```
- Day 6: Prints both "Saturday" and "Sunday"
- Day 7: Prints only "Sunday"
- Other: Prints "Weekday"

---

### SLIDE 12: Introduction to Loops
**Visual:** Loop concept
**Content:**
- Loop: Repeat code multiple times
- Why? Avoid copy-paste, handle unknown quantities
- Types: for, while, do-while, enhanced for
- Exit: Condition becomes false or break
- Essential for processing collections
- Every program uses loops

---

### SLIDE 13: for Loop Basics
**Visual:** for loop anatomy
**Content:**
- for loop: Count-based repetition
- Syntax:
  ```
  for (init; condition; increment) {
    // Code here
  }
  ```
- init: Variable initialization (usually i = 0)
- condition: Loop continues while true
- increment: After each iteration
- Most common loop type

---

### SLIDE 14: for Loop Example
**Visual:** Code and execution
**Content:**
- Print 0 through 9:
  ```
  for (int i = 0; i < 10; i++) {
    System.out.println(i);
  }
  ```
- i starts at 0
- Loop while i < 10
- Increment i each time
- Prints: 0, 1, 2, ..., 9

---

### SLIDE 15: for Loop Details
**Visual:** Loop mechanics
**Content:**
- Counter variable: Usually int i
- Can start at any number: for (int i = 1; i <= 10; i++)
- Can increment by any amount: for (int i = 0; i < 100; i += 10)
- Can decrement: for (int i = 10; i > 0; i--)
- Scope: Variable i exists only in loop
- Common pattern: for (int i = 0; i < size; i++)

---

### SLIDE 16: while Loop
**Visual:** while loop structure
**Content:**
- while: Condition-based repetition
- Syntax:
  ```
  while (condition) {
    // Code here
  }
  ```
- Checks condition before each iteration
- Loop continues while true
- Use when repetitions unknown
- Can be infinite if condition never false

---

### SLIDE 17: while Loop Example
**Visual:** Code and output
**Content:**
- Sum numbers until negative:
  ```
  int sum = 0;
  int num = 0;
  while (num >= 0) {
    sum += num;
    // Get next number from user
  }
  System.out.println(sum);
  ```
- Continues while num is non-negative
- Stops on first negative number
- Number of iterations unknown

---

### SLIDE 18: do-while Loop
**Visual:** do-while structure
**Content:**
- do-while: Always executes at least once
- Syntax:
  ```
  do {
    // Code here
  } while (condition);
  ```
- Checks condition after iteration
- Body executes first, then check
- Useful for menus, input validation
- Note: Semicolon after while!

---

### SLIDE 19: do-while Example
**Visual:** Menu example
**Content:**
- Menu-driven program:
  ```
  int choice;
  do {
    System.out.println("1. Start");
    System.out.println("2. Help");
    System.out.println("3. Exit");
    choice = getInput();
  } while (choice != 3);
  ```
- Menu always shows once
- Loops until user selects exit
- Guaranteed at least one iteration

---

### SLIDE 20: for vs while vs do-while
**Visual:** Comparison table
**Content:**
- for: Best when count known
- while: Best when condition complex
- do-while: Best when must run once
- for: Most common in practice
- while: For event-driven loops
- do-while: For input validation
- All three are equally powerful

---

### SLIDE 21: Enhanced for Loop (for-each)
**Visual:** Enhanced for syntax
**Content:**
- Enhanced for: Simplified iteration over collections
- Syntax:
  ```
  for (int num : numbers) {
    System.out.println(num);
  }
  ```
- num: Each element in sequence
- numbers: Array or collection
- No index tracking needed
- Cleaner than traditional for loop
- Cannot modify collection while iterating

---

### SLIDE 22: Enhanced for Example
**Visual:** Code comparison
**Content:**
- Traditional for:
  ```
  for (int i = 0; i < arr.length; i++) {
    System.out.println(arr[i]);
  }
  ```
- Enhanced for:
  ```
  for (int element : arr) {
    System.out.println(element);
  }
  ```
- Second is cleaner
- Both do same thing
- Use enhanced for when possible

---

### SLIDE 23: Introduction to break
**Visual:** break concept
**Content:**
- break: Exit loop immediately
- Syntax: `break;`
- Can be in for, while, do-while, switch
- Jumps to code after the loop
- Useful for early exit conditions
- Different from return (which exits method)

---

### SLIDE 24: break Example
**Visual:** Search example
**Content:**
- Find first negative:
  ```
  for (int i = 0; i < numbers.length; i++) {
    if (numbers[i] < 0) {
      System.out.println("Found at index " + i);
      break;  // Exit loop
    }
  }
  ```
- Stops as soon as negative found
- Doesn't check remaining numbers
- Efficient for search patterns

---

### SLIDE 25: Introduction to continue
**Visual:** continue concept
**Content:**
- continue: Skip to next iteration
- Syntax: `continue;`
- Can be in for, while, do-while
- Skips remaining code in iteration
- Goes directly to next iteration
- Different from break (doesn't exit)

---

### SLIDE 26: continue Example
**Visual:** Skip pattern
**Content:**
- Print only even numbers:
  ```
  for (int i = 0; i <= 10; i++) {
    if (i % 2 == 1) {
      continue;  // Skip odd
    }
    System.out.println(i);
  }
  ```
- Output: 0, 2, 4, 6, 8, 10
- Odd numbers are skipped
- Increment still happens

---

### SLIDE 27: Nested Loops
**Visual:** Nested structure
**Content:**
- Nested: Loop inside loop
- Outer loop controls outer iteration
- Inner loop runs completely each outer iteration
- Example: 2D traversal
- Complexity: Be careful with performance
- Indentation: Shows structure

---

### SLIDE 28: Nested Loop Example
**Visual:** Multiplication table
**Content:**
- Multiplication table:
  ```
  for (int i = 1; i <= 10; i++) {
    for (int j = 1; j <= 10; j++) {
      System.out.print(i * j + " ");
    }
    System.out.println();
  }
  ```
- Outer loop: Rows (1-10)
- Inner loop: Columns (1-10)
- Prints 10x10 table

---

### SLIDE 29: Loop Performance Consideration
**Visual:** Performance impact
**Content:**
- Nested loops: Quadratic complexity
- Loop inside loop: n * n iterations
- 10 outer, 10 inner: 100 iterations
- 1000 outer, 1000 inner: 1,000,000 iterations
- Avoid deeply nested loops
- Think about whether you need it

---

### SLIDE 30: Infinite Loops
**Visual:** Caution warning
**Content:**
- Infinite loop: Condition always true
- Common mistakes:
  ```
  while (true) { }  // Infinite
  for (int i = 0; i < 10; i--) { }  // Infinite (decrements)
  ```
- Usually unintentional
- Program hangs
- Ctrl+C to kill in terminal
- Intentional: while (true) with break inside

---

### SLIDE 31: Choosing Control Flow
**Visual:** Decision guide
**Content:**
- Use if-else for: Binary or few conditions
- Use switch for: Many cases, same variable
- Use for for: Known count iterations
- Use while for: Unknown count, condition-based
- Use do-while for: Must run once
- Use enhanced for: Iterating collections

---

### SLIDE 32: Common Loop Patterns - Summing
**Visual:** Sum pattern
**Content:**
- Summing array:
  ```
  int sum = 0;
  for (int num : array) {
    sum += num;
  }
  System.out.println(sum);
  ```
- Initialize accumulator to 0
- Add each element
- Common pattern, memorize it

---

### SLIDE 33: Common Loop Patterns - Counting
**Visual:** Count pattern
**Content:**
- Count occurrences:
  ```
  int count = 0;
  for (int num : array) {
    if (num > 5) {
      count++;
    }
  }
  System.out.println(count);
  ```
- Initialize counter to 0
- Increment on condition
- Common for filtering

---

### SLIDE 34: Common Loop Patterns - Finding
**Visual:** Search pattern
**Content:**
- Find first match:
  ```
  int index = -1;
  for (int i = 0; i < array.length; i++) {
    if (array[i] == target) {
      index = i;
      break;
    }
  }
  ```
- Initialize to not found (-1)
- Break when found
- Common for searching

---

### SLIDE 35: Best Practices
**Visual:** Guidelines
**Content:**
- Use meaningful variable names
- Indent code consistently
- Avoid deeply nested code
- Prefer enhanced for loops
- Break complex conditions into variables
- Test boundary conditions
- Keep logic simple and clear

---

### SLIDE 36: Debugging Loops
**Visual:** Debugging tips
**Content:**
- Print loop variable values
- Check loop condition logic
- Verify initialization
- Confirm increment/decrement
- Look for off-by-one errors
- Use debugger stepping
- Test with small values first

---

### SLIDE 37: Part 1 Summary
**Visual:** Key concepts recap
**Content:**
- if-else: Make decisions
- switch: Multiple cases
- for: Count-based loops
- while: Condition-based loops
- do-while: At least once
- Enhanced for: Collections
- break/continue: Control flow

---

### SLIDE 38: Key Takeaways
**Visual:** Important reminders
**Content:**
- Control flow directs program execution
- Use appropriate structure for situation
- Loops repeat code efficiently
- Always have exit condition
- Test boundary cases
- Performance matters with nested loops
- Practice with examples

---

### SLIDE 39: Hands-On Practice
**Visual:** Exercise prompt
**Content:**
- Write programs that:
  1. Use if-else for decisions
  2. Use loops for repetition
  3. Combine loops and conditions
  4. Handle edge cases
- Run and test thoroughly
- Next: Arrays and collections

---

### SLIDE 40: Q&A Session
**Visual:** Question mark
**Content:**
- Control flow confusion?
- Loop logic questions?
- Break vs continue unclear?
- Ask now before arrays!

---

### SLIDE 41: Lab Time
**Visual:** Coding icon
**Content:**
- Write simple programs:
  - Grade calculator with if-else
  - Times table with loops
  - Number guessing with do-while
- Test your code
- Ask for help
- Get comfortable with control flow

---
