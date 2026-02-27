Core Java: Control Flow & Arrays
60-Minute Presentation Script
INTRODUCTION (5 minutes)
Slide 1: Title Slide
Content:

Core Java: Control Flow & Arrays
Your Name
Date
Script: "Welcome everyone! Today we're going to dive into some of the most fundamental concepts in Java programming - control flow statements and arrays. These are the building blocks that will allow you to write programs that make decisions, repeat actions, and work with collections of data. By the end of this session, you'll be able to write programs that can handle complex logic and process multiple pieces of information efficiently."

Slide 2: Today's Learning Objectives
Content:

Master conditional statements (if-else, switch)
Understand different types of loops
Work with break and continue
Create and manipulate arrays
Combine these concepts to solve real problems
Script: "Here's what we'll cover today. First, we'll look at how to make your programs make decisions using if-else and switch statements. Then we'll explore loops - the various ways to repeat code. We'll learn about break and continue to control our loops more precisely. Finally, we'll tackle arrays, which let us store and work with multiple values. And most importantly, we'll see how to combine all these concepts to solve actual programming problems."

PART 1: CONTROL FLOW STATEMENTS (15 minutes)
Slide 3: What is Control Flow?
Content:

Control flow = the order in which code executes
By default: top to bottom
Control flow statements let us change this order
Make decisions based on conditions
Script: "Let's start with control flow. By default, Java code runs from top to bottom, line by line. But what if we need our program to make decisions? What if we only want certain code to run under specific conditions? That's where control flow statements come in. They allow our programs to be intelligent and respond differently to different situations."

Slide 4: The if Statement
Content:

java
if (condition) {
    // code runs if condition is true
}
Script: "The most basic control flow statement is 'if'. The syntax is simple: the keyword 'if', followed by a condition in parentheses, then a code block in curly braces. If the condition evaluates to true, the code inside the braces executes. If it's false, Java skips that block entirely. Let me show you a practical example."

Slide 5: if Statement Example
Content:

java
int age = 20;

if (age >= 18) {
    System.out.println("You are an adult");
}
Output: "You are an adult"

Script: "Here we check if someone's age is 18 or greater. Since age is 20, the condition is true, so the message prints. If age were 16, nothing would print because the condition would be false and Java would skip the entire if block."

Slide 6: if-else Statement
Content:

java
if (condition) {
    // runs if true
} else {
    // runs if false
}
Script: "Often we want to do one thing if a condition is true, and something different if it's false. That's what else gives us - an alternative path. Exactly one of these blocks will execute, never both, never neither."

Slide 7: if-else Example
Content:

java
int score = 75;

if (score >= 60) {
    System.out.println("Pass");
} else {
    System.out.println("Fail");
}
Output: "Pass"

Script: "In this grading example, if the score is 60 or above, we print 'Pass'. Otherwise, we print 'Fail'. This ensures we always give feedback - every possible score has a result."

Slide 8: else-if Chain
Content:

java
if (condition1) {
    // code
} else if (condition2) {
    // code
} else if (condition3) {
    // code
} else {
    // default code
}
Script: "When we have multiple conditions to check, we use else-if chains. Java evaluates these from top to bottom and executes the first block where the condition is true, then skips all the rest. The final else is optional and acts as a catch-all for any case not covered above."

Slide 9: else-if Example
Content:

java
int grade = 85;

if (grade >= 90) {
    System.out.println("A");
} else if (grade >= 80) {
    System.out.println("B");
} else if (grade >= 70) {
    System.out.println("C");
} else {
    System.out.println("Below C");
}
Output: "B"

Script: "This is a classic grading system. We check from highest to lowest grade. Since 85 is not >= 90, we skip the first block. But it is >= 80, so we print 'B' and we're done - we don't check the remaining conditions. This is important: once a condition matches, we execute that block and skip all remaining else-ifs."

Slide 10: Switch Statement
Content:

Classic syntax:
java
switch (expression) {
    case value1:
        // code
        break;
    case value2:
        // code
        break;
    default:
        // code
}

Arrow syntax (Java 16+):
java
String result = switch (expression) {
    case value1 -> // single expression;
    case value2 -> {
        // block of code
        yield result;  // return value from block
    }
    default -> // single expression;
};
No break needed — no fall-through
Switch can be used as an expression (assigned to a variable)
Script: "Now let's look at switch statements. When you need to compare a variable against several specific values, switch is often cleaner than multiple if-else statements. The switch evaluates an expression once, then jumps to the matching case. In the classic syntax, each case ends with break to prevent 'falling through' to the next case. Java 16 introduced a cleaner arrow syntax — the arrow replaces the colon, no break is needed, and fall-through is impossible. Arrow syntax also lets switch work as an expression, meaning you can assign its result directly to a variable. You'll see both forms in real codebases, so it's important to recognize each one."

Slide 11: Switch Example
Content:

Classic syntax:
java
int day = 3;
String dayName;

switch (day) {
    case 1:
        dayName = "Monday";
        break;
    case 2:
        dayName = "Tuesday";
        break;
    case 3:
        dayName = "Wednesday";
        break;
    default:
        dayName = "Invalid day";
}
Output: dayName = "Wednesday"

Arrow syntax (Java 16+):
java
int day = 3;

String dayName = switch (day) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    case 3 -> "Wednesday";
    default -> "Invalid day";
};
Output: dayName = "Wednesday"

Script: "Here we convert a number to a day name using both forms. In the classic version, the switch jumps to case 3, sets dayName to 'Wednesday', then break exits. Without break, it would continue into case 4 and beyond — this is called fall-through and is usually a bug. The arrow syntax version does the same thing but is noticeably shorter and safer. The switch is now an expression — its result is assigned directly to dayName in one line. Each arrow case is automatically isolated, so there's no break needed and fall-through is not possible. The arrow form is the preferred style in modern Java."

Slide 12: When to Use if vs switch
Content:

Use if-else when:
Testing ranges (age >= 18)
Complex conditions (x > 0 && y < 10)
Boolean conditions
Use switch when:
Checking exact values
Many possible values to check
Cleaner readability
Script: "So when should you use each? Use if-else for ranges, complex conditions, and boolean tests. Use switch when checking a variable against multiple specific values — it's more readable for that scenario. And within switch, prefer the arrow syntax introduced in Java 16 when writing new code — it's cleaner, eliminates accidental fall-through, and lets you treat switch as an expression. You'll still encounter classic switch in existing codebases, so you need to know both. There's often overlap between if-else and switch, so choose what makes your code clearest."

PART 2: LOOPS (15 minutes)
Slide 13: What are Loops?
Content:

Loops repeat code multiple times
Avoid writing the same code over and over
Four types in Java:
for loop
while loop
do-while loop
enhanced for loop
Script: "Moving on to loops. Imagine you need to print numbers 1 through 100. You could write 100 print statements, but that's insane! Loops let us repeat code efficiently. Java gives us four types of loops, each useful in different situations."

Slide 14: The for Loop
Content:

java
for (initialization; condition; update) {
    // code to repeat
}
Initialization: runs once at start
Condition: checked before each iteration
Update: runs after each iteration
Script: "The for loop is perfect when you know how many times to loop. It has three parts separated by semicolons. First, initialization - typically declaring and setting a counter variable. Second, the condition - the loop continues while this is true. Third, the update - usually incrementing the counter. Let's see this in action."

Slide 15: for Loop Example
Content:

java
for (int i = 1; i <= 5; i++) {
    System.out.println("Count: " + i);
}
Output:

Count: 1
Count: 2
Count: 3
Count: 4
Count: 5
Script: "This loop prints numbers 1 through 5. First time through: i is initialized to 1, we check if 1 <= 5 (yes), we print, then i++ makes i equal 2. Second time: we check 2 <= 5 (yes), print, increment to 3. This continues until i becomes 6, then 6 <= 5 is false, and the loop exits."

Slide 16: The while Loop
Content:

java
while (condition) {
    // code to repeat
}
Checks condition BEFORE each iteration
Good when you don't know how many iterations needed
Script: "The while loop is simpler - it just has a condition. Before each iteration, it checks if the condition is true. If yes, it executes the body and checks again. If no, it exits. Use while when you don't know in advance how many times you'll loop - maybe you're waiting for user input or reading until end of file."

Slide 17: while Loop Example
Content:

java
int count = 1;

while (count <= 5) {
    System.out.println("Count: " + count);
    count++;
}
Same output as for loop example

Script: "This while loop does the same thing as our for loop example. We initialize count before the loop, check the condition at the start of each iteration, and manually increment count inside the loop body. Notice how the for loop is more compact for this scenario - everything's in one line."

Slide 18: The do-while Loop
Content:

java
do {
    // code to repeat
} while (condition);
Checks condition AFTER each iteration
Guarantees at least one execution
Script: "The do-while loop is like while, but with one key difference: it checks the condition at the end, not the beginning. This means the code inside always runs at least once, even if the condition is false from the start. Use this when you need the code to execute before you can determine whether to continue."

Slide 19: do-while Example
Content:

java
int num = 0;

do {
    System.out.println("Num: " + num);
    num++;
} while (num < 0);
Output: "Num: 0"
(runs once even though condition is false)

Script: "Look at this example. The condition num < 0 is false from the start since num is 0. But the do-while runs the code once anyway, printing 'Num: 0', then checks the condition and exits. A regular while loop with this condition would never execute at all."

Slide 20: Enhanced for Loop (for-each)
Content:

java
for (Type element : collection) {
    // use element
}
Simplified loop for arrays/collections
No index needed
Can't modify the collection
Script: "The enhanced for loop, also called for-each, is specifically for iterating through arrays and collections. You don't need an index counter - it automatically gives you each element one at a time. We'll see this more when we get to arrays in a few minutes. It's the cleanest way to process every item when you don't need to know the position."

Slide 21: Nested Loops
Content:

java
for (int i = 1; i <= 3; i++) {
    for (int j = 1; j <= 2; j++) {
        System.out.println(i + "," + j);
    }
}
Output: 1,1 1,2 2,1 2,2 3,1 3,2

Script: "You can put loops inside loops - these are nested loops. The inner loop completes all its iterations for each single iteration of the outer loop. Here, for each value of i (1, 2, 3), j goes through its full range (1, 2). This is essential for working with multi-dimensional data like tables or grids."

PART 3: BREAK AND CONTINUE (5 minutes)
Slide 22: Break Statement
Content:

java
break;
Immediately exits the loop
Common uses:
Found what you're searching for
Error condition
Early termination
Script: "Sometimes you need to exit a loop early, before it naturally completes. That's what break does - it immediately terminates the loop and jumps to the code after the loop. This is useful when you're searching for something and find it, or when you encounter an error condition."

Slide 23: Break Example
Content:

java
for (int i = 1; i <= 10; i++) {
    if (i == 5) {
        break;
    }
    System.out.println(i);
}
Output: 1 2 3 4
(stops when i reaches 5)

Script: "This loop would normally print 1 through 10. But when i equals 5, we hit the break statement. This immediately exits the loop entirely - we don't print 5, we don't continue to 6, we're done. Break is particularly useful in search scenarios where you can stop as soon as you find what you're looking for."

Slide 24: Continue Statement
Content:

java
continue;
Skips rest of current iteration
Jumps to next iteration
Loop continues running
Script: "Continue is different from break. Instead of exiting the loop entirely, continue just skips the rest of the current iteration and jumps to the next one. The loop keeps running. Use this when you want to skip certain values but keep processing others."

Slide 25: Continue Example
Content:

java
for (int i = 1; i <= 5; i++) {
    if (i == 3) {
        continue;
    }
    System.out.println(i);
}
Output: 1 2 4 5
(skips 3 but continues loop)

Script: "Here we print numbers 1 through 5, except we skip 3. When i equals 3, continue immediately jumps to the next iteration without executing the print statement. But the loop continues - we still process 4 and 5. This is perfect for filtering out certain values while processing the rest."

PART 4: ARRAYS (15 minutes)
Slide 26: What are Arrays?
Content:

Container for multiple values of same type
Fixed size (can't grow or shrink)
Elements accessed by index
Index starts at 0
Example: storing 5 test scores instead of 5 variables
Script: "Now let's talk about arrays. Imagine you need to store test scores for 100 students. You could create 100 separate variables, but that's unmaintainable. Arrays let us store multiple values of the same type in a single container. Each value has an index - its position in the array. And here's something crucial: indexes start at 0, not 1. So the first element is at index 0, the second at index 1, and so on."

Slide 27: Array Declaration and Creation
Content:

java
// Declaration
int[] numbers;

// Creation
numbers = new int[5];

// Combined
int[] scores = new int[10];
Creates an array with space for 5/10 integers

Script: "To create an array, we need two steps - declare and create. Declaration says what type of array we want. Creation actually allocates memory for it. We specify the size in square brackets. You can do these steps separately or combine them. Here, int[] numbers means we're declaring an integer array. new int[5] creates space for 5 integers. By default, numeric arrays are initialized with zeros."

Slide 28: Array Initialization
Content:

java
// Initialize with values
int[] numbers = {10, 20, 30, 40, 50};

// Or separately
int[] scores = new int[3];
scores[0] = 85;
scores[1] = 90;
scores[2] = 78;
Script: "There are two ways to populate an array with values. First, you can initialize it directly with values in curly braces - Java figures out the size automatically. Second, you can create an empty array then assign values one at a time using indexes. Remember, arrays use square brackets for indexing, not parentheses."

Slide 29: Accessing Array Elements
Content:

java
int[] scores = {85, 92, 78, 90};

System.out.println(scores[0]);  // 85
System.out.println(scores[2]);  // 78

scores[1] = 95;  // Change 92 to 95
Use index in square brackets
Reading: scores[index]
Writing: scores[index] = value
Script: "To read or write an array element, use the array name followed by the index in square brackets. scores[0] gives us the first element. To modify an element, just assign to that index. Remember, if your array has 4 elements, valid indexes are 0, 1, 2, 3. Trying to access index 4 will crash your program with an ArrayIndexOutOfBoundsException."

Slide 30: Array Length
Content:

java
int[] numbers = {10, 20, 30, 40, 50};

System.out.println(numbers.length);  // 5

// Use in loops
for (int i = 0; i < numbers.length; i++) {
    System.out.println(numbers[i]);
}
Script: "Every array has a length property that tells you how many elements it holds. This is critical for loops - use it instead of hard-coding the size. That way, if the array size changes, your loop still works. Notice we use i < numbers.length, not <=. Since indexes start at 0, the last valid index is length minus 1."

Slide 31: Iterating Arrays with for Loop
Content:

java
int[] scores = {85, 92, 78, 90, 88};

for (int i = 0; i < scores.length; i++) {
    System.out.println("Score " + (i+1) + 
                       ": " + scores[i]);
}
Output:

Score 1: 85
Score 2: 92
Score 3: 78
...
Script: "The traditional for loop is perfect when you need both the index and the value. Here we're printing each score with its position. We iterate from 0 to length-1, accessing each element with scores[i]. The i+1 in the output converts the 0-based index to human-friendly 1-based numbering."

Slide 32: Enhanced for Loop with Arrays
Content:

java
int[] scores = {85, 92, 78, 90, 88};

for (int score : scores) {
    System.out.println(score);
}
Simpler syntax
No index variable needed
Can't modify array elements
Script: "Remember the enhanced for loop? This is where it shines. For each score in scores, do something with that score. The syntax is cleaner because we don't need to manage an index variable. However, you can't modify the array elements through this loop, and you don't have access to the index. Use this when you just need to read every element."

Slide 33: Multi-dimensional Arrays
Content:

java
// 2D array: array of arrays
int[][] matrix = new int[3][4];  // 3 rows, 4 columns

// Initialize
int[][] grid = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};
Script: "Arrays can be multi-dimensional - essentially, arrays of arrays. A 2D array is like a table with rows and columns. We declare it with two sets of square brackets. new int[3][4] creates a table with 3 rows and 4 columns. You can also initialize a 2D array directly with nested curly braces, where each inner set represents one row."

Slide 34: Accessing 2D Arrays
Content:

java
int[][] grid = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

System.out.println(grid[0][0]);  // 1
System.out.println(grid[1][2]);  // 6
System.out.println(grid[2][1]);  // 8
First index = row
Second index = column
Script: "To access a 2D array element, use two indexes: first for the row, second for the column. grid[1][2] means row 1, column 2, which gives us the value 6. Think of it as grid[row][column]. Like regular arrays, both indexes start at 0."

Slide 35: Iterating 2D Arrays
Content:

java
int[][] grid = {
    {1, 2, 3},
    {4, 5, 6}
};

for (int i = 0; i < grid.length; i++) {
    for (int j = 0; j < grid[i].length; j++) {
        System.out.print(grid[i][j] + " ");
    }
    System.out.println();
}
Output:

1 2 3
4 5 6
Script: "To process every element in a 2D array, use nested loops. The outer loop iterates through rows, the inner loop through columns in each row. grid.length gives the number of rows, grid[i].length gives the number of columns in row i. This prints the array like a table."

Slide 36: Common Array Patterns
Content:

Sum all elements
Find maximum/minimum
Search for a value
Reverse an array
Copy an array
Script: "Let's look at some common patterns you'll use constantly. Summing all elements - use a loop with an accumulator variable. Finding max or min - loop through, comparing each element. Searching - loop until you find the target. These patterns combine loops and arrays in practical ways."

Slide 37: Sum Array Pattern
Content:

java
int[] numbers = {10, 20, 30, 40, 50};
int sum = 0;

for (int num : numbers) {
    sum += num;
}

System.out.println("Sum: " + sum);  // 150
Script: "Here's the sum pattern. Initialize an accumulator to 0, then add each element to it. This is one of the most fundamental patterns in programming. You'll use variations of this constantly - counting elements, averaging values, concatenating strings."

Slide 38: Find Maximum Pattern
Content:

java
int[] scores = {85, 92, 78, 90, 88};
int max = scores[0];  // assume first is max

for (int i = 1; i < scores.length; i++) {
    if (scores[i] > max) {
        max = scores[i];
    }
}

System.out.println("Highest: " + max);  // 92
Script: "To find the maximum, assume the first element is the largest, then check every other element. If we find something bigger, update our max. By the end, max holds the largest value. The same pattern works for minimum - just change the comparison to less than."

PART 5: PUTTING IT ALL TOGETHER (5 minutes)
Slide 39: Combining Concepts
Content: Real problem: Grade analyzer

Input: array of student scores
Determine: pass/fail for each
Calculate: class average
Find: highest score
Uses: arrays, loops, conditionals
Script: "Now let's combine everything we've learned. Imagine building a grade analyzer. We have an array of student scores. We need to determine pass or fail for each student, calculate the class average, and find the highest score. This requires arrays to store scores, loops to process them, and conditionals to make decisions. Let's see how it all comes together."

Slide 40: Complete Example
Content:

java
int[] scores = {85, 92, 78, 90, 55, 88};
int sum = 0;
int max = scores[0];
int passCount = 0;

for (int i = 0; i < scores.length; i++) {
    sum += scores[i];
    
    if (scores[i] > max) {
        max = scores[i];
    }
    
    if (scores[i] >= 60) {
        passCount++;
        System.out.println("Student " + (i+1) + 
                         ": Pass");
    } else {
        System.out.println("Student " + (i+1) + 
                         ": Fail");
    }
}

double average = (double) sum / scores.length;
System.out.println("Average: " + average);
System.out.println("Highest: " + max);
System.out.println(passCount + " students passed");
Script: "Here's the complete solution. We use one loop to do everything efficiently. For each score, we add it to our sum for the average, check if it's a new maximum, and use an if-else to determine pass or fail. After the loop, we calculate the average and print our results. This is a real-world pattern - processing an array once to gather multiple pieces of information."

Slide 41: Key Takeaways
Content:

if-else for decisions, switch for multiple values
Switch arrow syntax (Java 16+): cleaner, no break, usable as expression
for when you know iteration count, while when you don't
break exits loops, continue skips iteration
Arrays store multiple values, access by index (starts at 0)
Combine these concepts to solve real problems
Script: "Let's recap the key points. Use if-else and switch to make decisions in your code. For switch, you now know two forms — the classic syntax with break, and the modern arrow syntax from Java 16 that's cleaner and eliminates fall-through. Choose the right loop for your situation — for when you know the count, while when you don't. Control your loops precisely with break and continue. Arrays let you work with multiple values efficiently using indexes. And most importantly, these aren't isolated concepts — combine them to solve real programming problems."

Slide 42: Practice Challenges
Content:

Write a program to find all even numbers in an array
Create a times table using nested loops
Search an array for a specific value, report index
Calculate the average of only passing grades
Reverse an array's elements
Script: "Here are some challenges for you to practice. These will help solidify what you've learned today. Try to solve each one using the concepts we covered. Don't worry if you get stuck - that's part of learning. In our next session, we'll build on these foundations to explore more advanced Java concepts."

Slide 43: Questions & Next Steps
Content:

Questions?
Practice these concepts extensively
Next lesson: Methods and Object-Oriented Programming
Resources: [list your preferred resources]
Script: "That brings us to the end of today's lesson. Do you have any questions about control flow, loops, or arrays? Remember, programming is a skill that improves with practice. Try solving the challenges, experiment with the code examples, and don't be afraid to make mistakes - that's how you learn. In our next lesson, we'll explore methods and begin our journey into object-oriented programming. Thank you for your attention!"

ADDITIONAL TEACHING TIPS
Pacing:

Don't rush through examples - let students absorb each one
Pause after complex concepts for questions
If running short on time, prioritize hands-on examples over theory
Engagement:

Ask students to predict output before showing it
Have them spot errors in code examples
Encourage questions throughout, not just at the end
Common Student Mistakes to Address:

Using = instead of == in conditions
Forgetting break in classic switch statements (not needed in arrow syntax)
Off-by-one errors in loops and array access
Accessing arrays out of bounds
Demo Tips:

Have a Java IDE open to run examples live
Show compilation errors and how to fix them
Let students see your debugging process

---

## INSTRUCTOR NOTES

**Coverage note:** Switch expressions with arrow syntax (Java 14+, standardized in Java 16) are now covered alongside classic switch syntax in Slides 10–11, with both forms shown using the same day-name example for direct comparison.

**Unnecessary/Too Advanced:** Nothing to remove. Coverage is well-scoped.

**Density:** Well-paced with clear examples. No density concerns.
