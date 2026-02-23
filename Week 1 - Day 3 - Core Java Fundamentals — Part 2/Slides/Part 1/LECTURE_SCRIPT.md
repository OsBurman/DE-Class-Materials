# Part 1: Control Flow & Loops
## Lecture Script (60 Minutes)

---

## LECTURE SCRIPT - PART 1

*[0 minutes]*

Good morning! Welcome to Day 3, Part 1. Yesterday, you learned the fundamentals: primitives, variables, operators. Today, we're covering something equally fundamental: control flow and loops. These are the building blocks of every program you'll ever write.

Control flow is how you direct a program's execution. It's how you say, "Do this if X is true. Repeat this 10 times. Do something based on user input." Without control flow, every program would just execute line by line with no decisions.

Today, you're going to learn how to make your programs smart—how to make decisions and repeat tasks. This is where programming gets interesting.

*[2 minutes]*

## Learning Objectives

Here's what we're covering today:
- Understand how if-else conditional logic works
- Use switch statements for multiple conditions
- Implement for loops for known repetitions
- Implement while and do-while loops for unknown repetitions
- Use break and continue for loop control
- Choose the right control flow structure for the right situation

By the end of today, you'll write programs that actually make decisions and repeat tasks. You'll understand when to use each structure and how to combine them.

*[4 minutes]*

## What is Control Flow?

Let me explain control flow. In the simplest sense, every program is a sequence of statements executed one after the other:

```
Line 1
Line 2
Line 3
```

This is sequential execution. It's default behavior. But real programs need to make decisions. They need to do different things based on conditions.

Control flow is the ability to direct where execution goes. Do we execute this block or that one? Do we repeat this code? How many times?

There are three kinds of control flow:

First: **Sequential.** Code runs line by line. Default behavior.

Second: **Conditional.** Code runs based on a condition being true or false. If statements. Switch statements.

Third: **Loops.** Code repeats based on a condition. For loops. While loops.

Every single program you'll ever write uses all three. And understanding them deeply is critical.

*[6 minutes]*

## Introduction to if Statements

Let's start with if. If is the foundation of conditional logic.

Basic idea: If something is true, do something.

```java
if (condition) {
  // Code here runs if condition is true
}
```

The condition is a boolean expression. It evaluates to true or false. If true, the code block executes. If false, it's skipped.

Example:

```java
int age = 20;
if (age >= 18) {
  System.out.println("You can vote");
}
```

Age is 20. Condition `age >= 18` is true. So the message prints.

The braces { } define the code block. Everything inside the braces executes if the condition is true.

Quick note: You can write an if without braces:

```java
if (age >= 18)
  System.out.println("You can vote");
```

But don't do this. Always use braces. It's clearer and prevents bugs when you add more code later.

*[8 minutes]*

## if-else Statements

Now, what if you want to do something when the condition is false? That's where else comes in.

```java
if (condition) {
  // Code if true
} else {
  // Code if false
}
```

Exactly one block executes. Never both.

Example:

```java
int age = 15;
if (age >= 18) {
  System.out.println("You can vote");
} else {
  System.out.println("Too young to vote");
}
```

Age is 15. Condition is false. So the else block executes. Message: "Too young to vote."

This is your fundamental decision-making tool. Most programs are full of if-else statements.

*[10 minutes]*

## if-else-if Chains

But what if you have multiple conditions? That's where if-else-if comes in.

```java
if (score >= 90) {
  System.out.println("A");
} else if (score >= 80) {
  System.out.println("B");
} else if (score >= 70) {
  System.out.println("C");
} else {
  System.out.println("F");
}
```

Here's how it works: We check the first condition. If true, that block executes and we skip the rest. If false, we move to the next condition. We keep going until one is true or we reach the else.

So if score is 85:
- First condition (>= 90): False, continue.
- Second condition (>= 80): True, execute. Print "B".
- Rest is skipped.

Only one block executes. That's the key insight.

*[12 minutes]*

## Nested if Statements

You can also nest if statements. Put an if inside another if.

```java
if (age >= 18) {
  if (hasLicense) {
    System.out.println("You can drive");
  }
}
```

This says: If age is 18 or over, check if they have a license. Only if both are true does the message print.

But here's the thing: You don't need to nest. You can use logical operators instead:

```java
if (age >= 18 && hasLicense) {
  System.out.println("You can drive");
}
```

This is cleaner. Avoid deep nesting when you can. It makes code hard to read.

*[14 minutes]*

## Common if Mistakes

Let me show you some common mistakes, because I see them all the time.

**Mistake 1: Using = instead of ==**

```java
if (x = 5) { }  // WRONG
if (x == 5) { }  // RIGHT
```

The first assigns 5 to x. It doesn't compare. It's a syntax error or creates a bug. Single equals is assignment. Double equals is comparison. Always double-check.

**Mistake 2: Forgetting braces**

```java
if (x > 5)
  System.out.println("Big");
  System.out.println("Always");
```

Without braces, only the first line is part of the if. The second line always executes. This is a common bug. Always use braces.

**Mistake 3: Semicolon after if**

```java
if (x > 5); {
  System.out.println("Big");
}
```

The semicolon ends the if statement. The braces are a separate block that always executes. Bug. Never put a semicolon after the condition.

These are easy mistakes when you're starting. Just be careful.

*[16 minutes]*

## Introduction to switch Statements

Now, if you have many conditions on the same variable, there's a better way: switch.

```java
switch (day) {
  case 1:
    System.out.println("Monday");
    break;
  case 2:
    System.out.println("Tuesday");
    break;
  case 3:
    System.out.println("Wednesday");
    break;
  default:
    System.out.println("Unknown day");
}
```

Here's how it works. We evaluate the value in the switch parentheses (day). Then we check each case. If the value matches a case, we execute that code. The break exits the switch.

default executes if no case matches. It's optional.

Why use switch instead of if-else-if? It's often clearer. It's more efficient when you have many conditions. And it emphasizes that you're checking one value against multiple options.

*[18 minutes]*

## Switch Details and Fall-Through

An important concept: fall-through. If you don't have a break, code continues to the next case.

```java
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

If day is 6:
- Case 6 matches
- Prints "Saturday"
- No break, so continues
- Prints "Sunday"

If day is 7:
- Case 7 matches
- Prints "Sunday"
- Break exits
- Doesn't print "Weekday"

Fall-through is usually a bug. Always include break. Sometimes it's useful intentionally, like the example above where weekend days share logic. But it's rare.

*[20 minutes]*

## Introduction to Loops

Now, loops. Loops let you repeat code multiple times. This is incredibly powerful.

Why? Imagine you want to print 1 through 1000. Without loops, you'd have 1000 print statements. That's ridiculous. With loops, you write code once, and it executes multiple times.

```java
for (int i = 1; i <= 1000; i++) {
  System.out.println(i);
}
```

Ten lines become one. And more importantly, the code adapts. If you need 10,000, you just change one number.

There are several loop types. They're all equally powerful but suited to different situations.

*[22 minutes]*

## for Loop Basics

The for loop is the most common. It's for situations where you know how many iterations you need.

Syntax:

```java
for (int i = 0; i < 10; i++) {
  // Code here
}
```

Three parts:
- **init:** `int i = 0`. Initialize a counter variable. Usually i starts at 0.
- **condition:** `i < 10`. Loop continues while this is true. When it's false, loop exits.
- **increment:** `i++`. After each iteration, do this. Usually increment i by 1.

Here's execution:
1. Initialize i to 0
2. Check if 0 < 10. True, so enter loop.
3. Execute body
4. Increment i to 1
5. Check if 1 < 10. True, so repeat
6. ... (continue for i = 2, 3, ..., 9)
7. Check if 10 < 10. False, so exit

Result: Loop runs 10 times, with i being 0 through 9.

*[24 minutes]*

## for Loop Examples and Variations

Classic example:

```java
for (int i = 0; i < 10; i++) {
  System.out.println(i);
}
```

Prints 0 through 9.

You can start at different numbers:

```java
for (int i = 1; i <= 10; i++) {
  System.out.println(i);
}
```

Prints 1 through 10. Note: i <= 10, not i < 10.

You can increment by different amounts:

```java
for (int i = 0; i < 100; i += 10) {
  System.out.println(i);
}
```

Prints 0, 10, 20, ..., 90.

You can count backwards:

```java
for (int i = 10; i > 0; i--) {
  System.out.println(i);
}
```

Prints 10 down to 1.

The key is: You control initialization, condition, and increment. Lots of flexibility.

One more thing: The variable i exists only in the loop. After the loop ends, i is gone. This is scope. It's good practice.

*[26 minutes]*

## while Loop

While is for situations where you don't know how many iterations you need. You just know a condition.

```java
while (condition) {
  // Code here
}
```

The loop continues while the condition is true. When it becomes false, the loop exits.

Example:

```java
int count = 0;
while (count < 5) {
  System.out.println(count);
  count++;
}
```

This prints 0 through 4. It's similar to a for loop, but we manually handle initialization and increment outside the loop.

More practical example: Reading user input until they quit.

```java
String input = "";
while (!input.equals("quit")) {
  System.out.println("Enter command (quit to exit):");
  input = scanner.nextLine();  // Get user input
  // Process input
}
```

We don't know how many commands the user will enter. So we use while.

*[28 minutes]*

## do-while Loop

There's a variant: do-while. The difference is subtle but important.

```java
do {
  // Code here
} while (condition);
```

The code executes first, then the condition is checked. This means the body always executes at least once.

Example: Menu-driven program.

```java
int choice;
do {
  System.out.println("1. Start");
  System.out.println("2. Help");
  System.out.println("3. Exit");
  choice = getInput();
} while (choice != 3);
```

The menu is displayed. User makes a choice. We loop if they didn't choose exit.

The menu always displays at least once. That's guaranteed with do-while.

With while, we'd have to display the menu before the loop starts. With do-while, it's simpler.

Note: The semicolon after while is required! This is unique to do-while.

*[30 minutes]*

## for vs while vs do-while

When should you use each?

**for:** You know the count ahead of time.
```java
for (int i = 0; i < 10; i++)
```

**while:** You don't know the count. Condition is complex or event-driven.
```java
while (user hasn't quit)
```

**do-while:** You must execute at least once.
```java
do { display menu } while (user hasn't quit)
```

In practice, for is most common. Loops over arrays or ranges happen frequently. While is for more dynamic situations. Do-while is rare but useful for input validation.

They're all equally powerful. Use the right one for the situation.

*[32 minutes]*

## Enhanced for Loop (for-each)

There's a fourth loop type: enhanced for, also called for-each.

```java
for (int element : array) {
  System.out.println(element);
}
```

This is simpler than traditional for. It says: "For each element in the array, do this."

You don't track an index. You just get each element. It's cleaner.

Comparison:

Traditional for:
```java
for (int i = 0; i < array.length; i++) {
  System.out.println(array[i]);
}
```

Enhanced for:
```java
for (int element : array) {
  System.out.println(element);
}
```

Second is better when you don't need the index. Use it whenever you can. We'll see more when we discuss arrays today.

*[34 minutes]*

## break Statement

break exits a loop immediately.

```java
for (int i = 0; i < 100; i++) {
  if (i == 25) {
    System.out.println("Found 25!");
    break;  // Exit loop
  }
}
System.out.println("After loop");
```

Loop runs from 0 to 100. When i equals 25, we print a message and break. Loop exits. We skip directly to "After loop."

Why useful? Searching. If you find what you're looking for, no point continuing.

Also works in switch statements. That's why switch needs break—to exit the switch.

*[36 minutes]*

## continue Statement

continue skips the rest of the iteration and goes to the next one.

```java
for (int i = 0; i <= 10; i++) {
  if (i % 2 == 1) {  // If odd
    continue;  // Skip to next iteration
  }
  System.out.println(i);
}
```

Output: 0, 2, 4, 6, 8, 10 (only even numbers)

When i is odd, continue skips the print statement. Jumps to i++. Then loop continues.

Difference from break: break exits the loop. continue continues to the next iteration.

Use continue to skip cases you don't want. Use break to stop entirely.

*[38 minutes]*

## Nested Loops

You can put loops inside loops. This creates nested loops.

```java
for (int i = 1; i <= 3; i++) {
  for (int j = 1; j <= 3; j++) {
    System.out.print(i + " ");
  }
  System.out.println();
}
```

Output:
```
1 1 1
2 2 2
3 3 3
```

Outer loop runs 3 times (i = 1, 2, 3). For each outer iteration, inner loop runs 3 times. Total: 9 inner iterations.

Common use: 2D traversal. Think of a grid. Rows and columns. Outer loop is rows, inner is columns.

Warning: Performance matters. Nested loops are quadratic. 10 x 10 = 100 iterations. 100 x 100 = 10,000. 1000 x 1000 = 1,000,000. Grows fast.

*[40 minutes]*

## Infinite Loops

Be careful: infinite loops. These are loops that never exit.

```java
while (true) {
  System.out.println("Help!");
}
```

This prints "Help!" forever. Condition is always true. Loop never exits.

Common bugs:

```java
for (int i = 0; i < 10; i--) {
  System.out.println(i);
}
```

Wait, i--, not i++. So i starts at 0, decrements to -1, -2, ... Condition i < 10 is always true. Infinite loop.

If you get stuck in an infinite loop, press Ctrl+C in the terminal to kill the program.

Sometimes infinite loops are intentional. Server loops that run until you shut down. But usually they're bugs. Be careful.

*[42 minutes]*

## Common Loop Patterns - Summing

Let me show you some common patterns. You'll see these constantly.

**Pattern 1: Summing**

```java
int sum = 0;
for (int num : array) {
  sum += num;
}
System.out.println("Total: " + sum);
```

- Initialize sum to 0 (starting point)
- Loop through each element
- Add to sum
- Memorize this pattern

**Pattern 2: Counting**

```java
int count = 0;
for (int num : array) {
  if (num > 5) {
    count++;
  }
}
System.out.println("Count: " + count);
```

- Initialize counter to 0
- Increment when condition met
- Used for filtering

**Pattern 3: Finding**

```java
int index = -1;
for (int i = 0; i < array.length; i++) {
  if (array[i] == target) {
    index = i;
    break;
  }
}
if (index != -1) {
  System.out.println("Found at: " + index);
}
```

- Initialize index to -1 (not found marker)
- Break when found
- Check after loop if found

Memorize these. They're everywhere.

*[44 minutes]*

## Choosing the Right Control Flow

Let me summarize: When do you use what?

**Use if-else when:**
- You have one or two conditions
- Binary decision: do this or that

**Use switch when:**
- You have many cases on one variable
- Cleaner than many if-else-if

**Use for when:**
- You know the count
- Iterating over a range: 0 to 10, 1 to 100

**Use while when:**
- You don't know the count
- Condition is complex: "while user hasn't quit"

**Use do-while when:**
- You must execute at least once
- Input validation menus

**Use enhanced for when:**
- Iterating a collection
- You don't need the index

Good programs choose the right structure for the situation. It makes code clearer.

*[46 minutes]*

## Best Practices

A few best practices:

First: Use meaningful variable names. Not just i. Maybe `studentCount`. Not `x`. Maybe `userAge`.

Second: Indent consistently. It shows structure. Easy to read.

Third: Avoid deeply nested code. If you have 3 levels of nesting, step back. Can you simplify?

Fourth: Prefer enhanced for loops. Cleaner than traditional for when possible.

Fifth: Break complex conditions into variables:

```java
// Bad
if (age > 18 && hasLicense && noAccidents) { }

// Better
boolean canDrive = age > 18 && hasLicense && noAccidents;
if (canDrive) { }
```

More readable.

Sixth: Test boundary cases. If loop goes 0 to 10, does i == 0 work? Does i == 10? Off-by-one errors are common.

*[48 minutes]*

## Debugging Loops

If your loop isn't working, here's how to debug:

Print loop variables. What's i actually doing?

```java
for (int i = 0; i < 10; i++) {
  System.out.println("i is: " + i);
  // Your code
}
```

Check loop condition logic. Is it <= or <? Are you getting the count right?

Verify initialization. What should the starting value be?

Confirm increment/decrement. Are you changing the variable?

Look for off-by-one errors. Classic: Loop should go 1-10 but goes 1-9 or 1-11.

Use a debugger if you're really stuck. Most IDEs have step-through debugging.

*[50 minutes]*

## Advanced Pattern: Loop With Index and Value

One more pattern, then we're done:

```java
for (int i = 0; i < array.length; i++) {
  int value = array[i];
  System.out.println("Index " + i + ": " + value);
}
```

When you need both the index and the value, use traditional for. Enhanced for doesn't give you the index.

Or with streams (advanced):

```java
array.stream()
  .enumerate()  // hypothetical
  .forEach((i, value) -> System.out.println("Index " + i + ": " + value));
```

But streams are for later. For now, just know traditional for when you need both.

*[52 minutes]*

## Part 1 Summary

Let's recap:

- **if-else:** Make decisions based on conditions
- **switch:** Multiple cases on one value
- **for:** Count-based loops (best when you know count)
- **while:** Condition-based loops (best when you don't know count)
- **do-while:** Always execute at least once
- **Enhanced for:** Iterate collections cleanly
- **break:** Exit loop immediately
- **continue:** Skip to next iteration

Control flow is fundamental. Every program uses it. Master these concepts, and you can write any program.

*[54 minutes]*

## Key Takeaways

- Control flow directs program execution
- Use the right structure for the right situation
- Loops avoid code duplication
- Always have an exit condition
- Test boundary cases
- Performance matters with nested loops
- Clean code is clear code

*[56 minutes]*

## Practice Exercise

During lab time, write programs that:
1. Use if-else to make decisions (grade calculator: enter score, output letter grade)
2. Use loops for repetition (print times table: 1x1 through 10x10)
3. Combine loops and conditions (number guessing game: loop until correct)
4. Handle edge cases (what if user enters invalid input?)

Get comfortable with these structures. Ask for help. We're here for you.

*[60 minutes]*

---

## End of Part 1

Great work! You've learned control flow. Tomorrow, we're doing arrays. You'll see loops working with arrays extensively.

For now, practice these structures. They're the foundation of everything.

---
