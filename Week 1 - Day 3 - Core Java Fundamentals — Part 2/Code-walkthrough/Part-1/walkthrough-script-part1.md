# Walkthrough Script — Part 1
## Day 3: Core Java Fundamentals — Part 2
### Files: `01-control-flow.java` · `02-loops.java` · `03-break-and-continue.java`

---

## OVERVIEW (Before Opening Any File)

**[ACTION]** Open your IDE. Have the three Part-1 files visible in the file explorer.

"Good morning everyone! Today is Day 3, and we're picking up right where we left off with Java fundamentals. Yesterday we covered data types, variables, operators, and Strings. Today we're going from 'knowing what values are' to 'making decisions about them' — which is where your code starts to feel alive.

By the end of this morning session, you'll be able to write programs that make decisions, repeat actions automatically, and exit loops at exactly the right moment. These three files cover everything you need for that.

We have three files today for Part 1. Let's go through them in order."

---

## FILE 1: `01-control-flow.java`

**[ACTION]** Open `01-control-flow.java`.

"The first file is all about control flow. Control flow simply means: *what path does your code take?* Without it, Java runs top to bottom, every line, every time. Control flow lets you say 'only run this code if...' or 'run this code when the value is...'"

---

### SECTION 1: Simple if statement

**[ACTION]** Scroll to Section 1.

"The simplest form is just an `if`. Look at this:"

```java
int temperature = 28;
if (temperature > 25) {
    System.out.println("It's a hot day — stay hydrated!");
}
```

"The condition is in parentheses. If it evaluates to `true`, the block runs. If it's `false`, we skip it entirely and continue with whatever comes next. Notice the curly braces — they define the block of code that's conditional."

**[ASK]** "What happens if I change `temperature` to `20`? Does anything print?"

"Nothing at all. The condition is false, so the whole block is skipped. That's the key insight — an `if` without an `else` can result in *nothing* happening."

---

### SECTION 2: if-else statement

**[ACTION]** Scroll to Section 2.

"An `else` gives you the alternative — what to do when the condition is false:"

```java
int studentScore = 55;
if (studentScore >= 60) {
    System.out.println("Result: PASS");
} else {
    System.out.println("Result: FAIL");
}
```

"Now we're guaranteed that *one* of these two branches always runs. With a score of 55, which branch executes?"

**[ASK]** "What does this print?"

"Exactly — FAIL. 55 is not >= 60."

⚠️ **WATCH OUT:** "Notice I wrote `>= 60`, not `> 60`. If passing is 60, a student who scores exactly 60 should pass. That off-by-one error is extremely common — always double-check your boundary conditions."

---

### SECTION 3: if-else-if chain

**[ACTION]** Scroll to Section 3.

"When you have more than two outcomes, chain your conditions with `else if`:"

```java
int examScore = 78;
if (examScore >= 90) { ... A }
else if (examScore >= 80) { ... B }
else if (examScore >= 70) { ... C }
else if (examScore >= 60) { ... D }
else { ... F }
```

"Java evaluates these from top to bottom and stops at the FIRST condition that's true."

**[ASK]** "78 is >= 70 AND it's >= 60. Which branch runs?"

"Grade C — because `examScore >= 70` is checked BEFORE `>= 60`, and once a match is found, the rest are skipped. Order matters!"

⚠️ **WATCH OUT:** "This is a very common trap. If you flipped the order and wrote `>= 60` before `>= 70`, a score of 78 would get a D. Always put your most specific (highest) conditions first."

---

### SECTION 4: Nested if statements

**[ACTION]** Scroll to Section 4.

"You can nest `if` statements inside other `if` statements — very common in real applications:"

```java
if (isLoggedIn) {
    if (isAdmin) { ... admin panel }
    else { ... restricted }
} else { ... please log in }
```

"Think of it like a bouncer at a club: first question — are you on the list? If yes, second question — are you a VIP? Each check only happens if the outer check passes."

⚠️ **WATCH OUT:** "Deep nesting — more than 2-3 levels — quickly becomes hard to read. We'll cover techniques later (early return, guard clauses) to flatten this. For now, just be aware that nesting is valid but can get messy."

---

### SECTION 5: Ternary operator

**[ACTION]** Scroll to Section 5.

"Before we get to switch, here's a one-line shorthand for a simple if-else:"

```java
String accessLevel = (age >= 18) ? "Adult access" : "Minor access";
```

"Question mark means 'if true, give me this'. Colon means 'otherwise, give me this'. Great for simple assignments. Not great when the logic is complex — readability always wins."

→ **TRANSITION:** "Okay — if-else handles conditions with ranges and true/false checks. But what about when you have a variable that could be one of many specific values? That's where `switch` shines."

---

### SECTION 6: Traditional switch statement

**[ACTION]** Scroll to Section 6.

"The `switch` statement compares a variable against specific case values:"

```java
String dayOfWeek = "Wednesday";
switch (dayOfWeek) {
    case "Monday": ... break;
    case "Wednesday": ... break;
    case "Friday": ... break;
    ...
    default: ...
}
```

"Think of it as a lookup table. Java checks each `case` in order and jumps to the one that matches. The `break` statement is critical — I'll show you why in a moment. The `default` block is like the `else` — it runs if nothing matched."

**[ASK]** "What prints for `dayOfWeek = \"Wednesday\"`?"

"'Hump day — halfway there!'"

---

### SECTION 7: Fall-through (intentional vs bug)

**[ACTION]** Scroll to Section 7.

"This is one of the most important things to understand about switch: **fall-through**."

```java
case 1:
    System.out.println("Deploy to production");
    // NO break here
case 2:
    System.out.println("Code review");
    break;
```

"When there's no `break`, Java doesn't stop — it falls through into the NEXT case. Here, with `priority = 2`, execution starts at case 2 and falls through to case 3."

⚠️ **WATCH OUT:** "Accidental fall-through (forgetting `break`) is one of the most common Java bugs. It's especially tricky because the code compiles and runs — it just doesn't do what you expected. Always ask yourself: does each case have a `break`? If you're intentionally falling through, add a comment so the next developer knows it's on purpose."

→ **TRANSITION:** "Modern Java (version 14+) gave us a much cleaner syntax that eliminates fall-through entirely. Let's look at switch expressions."

---

### SECTION 8 & 9: switch expression (Java 14+)

**[ACTION]** Scroll to Section 8.

"The arrow syntax in switch expressions changes the game:"

```java
String activity = switch (season) {
    case "Spring"  -> "Go hiking";
    case "Summer"  -> "Hit the beach";
    ...
    default        -> "Stay indoors";
};
```

"Three differences from the traditional switch: **arrow instead of colon**, **no break needed** (no fall-through), and it **returns a value** that you can assign to a variable. Much cleaner."

**[ACTION]** Scroll to Section 9.

"You can even handle multiple values per case with a comma:"

```java
case 1, 3, 5, 7, 8, 10, 12 -> 31;
```

"This is the new idiomatic way to write switch in modern Java. If your project is on Java 14+, prefer this over the traditional form."

---

### SECTION 10: switch on String

**[ACTION]** Scroll to Section 10.

"Quick note: `switch` works on `int`, `char`, `String` (since Java 7), and enums. You can't switch on `double` or `float`. Our role-based example here is a pattern you'll see constantly in real applications."

→ **TRANSITION:** "Excellent — we've covered all the conditional tools: `if-else`, `if-else-if`, ternary, traditional `switch`, and modern switch expressions. Now let's move to the second file: loops."

---

## FILE 2: `02-loops.java`

**[ACTION]** Open `02-loops.java`.

"Loops let us repeat a block of code without copy-pasting it. Java has four loop types — we'll look at each one and, importantly, when to prefer each."

---

### SECTION 1: Standard for loop

**[ACTION]** Scroll to Section 1.

"The for loop has three parts in the parentheses:"

```java
for (int i = 10; i >= 1; i--) { ... }
```

"**Initialization** — runs once before the loop starts. **Condition** — checked before each iteration; if false, loop ends. **Update** — runs after each iteration body."

**[ASK]** "What does `i--` do? What if I wrote `i++` instead?"

"The loop would count up from 10 and never stop — infinite loop! The update step controls direction."

**[ACTION]** Scroll to the menu items loop.

"This is the canonical pattern: iterate from 0 to `length - 1`. Notice I use `i + 1` in the print statement to show a 1-based number to users, even though the array uses 0-based indexing."

⚠️ **WATCH OUT:** "The classic off-by-one error: writing `i <= menuItems.length` instead of `i < menuItems.length`. That gives you an `ArrayIndexOutOfBoundsException` because index 5 doesn't exist in a 5-element array. Always use `< length`."

---

### SECTION 2: while loop

**[ACTION]** Scroll to Section 2.

"The while loop is simpler — just a condition. It's best when you don't know the number of iterations upfront:"

```java
while (accountBalance >= withdrawalAmount) {
    accountBalance -= withdrawalAmount;
    ...
}
```

"We don't know how many withdrawals will fit. We just keep going while there's enough balance."

**[ASK]** "Starting with $500 and withdrawing $120 each time — how many withdrawals happen?"

"Four: 500 → 380 → 260 → 140 → 20. At $20, the condition is false, so the loop ends."

⚠️ **WATCH OUT:** "With while loops, YOU are responsible for the update step. If you forget to update `accountBalance` inside the loop, the balance never changes and you have an infinite loop. The for loop forces you to write the update — that's one reason it's good for index-based iteration."

---

### SECTION 3: do-while loop

**[ACTION]** Scroll to Section 3.

"The do-while is the rarest of the four loops. The key difference:"

```java
do {
    // body
} while (condition);
```

"The body runs FIRST, condition is checked AFTER. So it always executes at least once."

**[ACTION]** Scroll to the 'runs at least once' demo.

"Watch this — `counter` is 100, and the condition is `counter < 10`. Normally a while loop wouldn't run at all. But a do-while:"

**[ASK]** "What does this print?"

"It prints the message once. The body ran before the condition was checked."

"Classic real-world use cases: show a menu at least once before asking if the user wants to continue, or prompt for a valid password at least once."

---

### SECTION 4: Enhanced for-each loop

**[ACTION]** Scroll to Section 4.

"The for-each is your cleanest loop when you just need every element without caring about the index:"

```java
for (String student : students) {
    System.out.println("Hello, " + student + "!");
}
```

"Read this as 'for each String called `student` in the `students` array'. No index, no bounds check, no off-by-one possible."

**[ASK]** "What's the trade-off? When can't you use for-each?"

"Two limitations: you can't access the index (no `i`), and you can't modify the array's elements through the loop variable. If you need to do either of those, use a regular for loop."

---

### SECTION 5: Nested loops

**[ACTION]** Scroll to Section 5.

"Nested loops are loops inside loops. The inner loop runs completely for each iteration of the outer loop."

```java
for (int row = 1; row <= 5; row++) {
    for (int col = 1; col <= 5; col++) {
        System.out.printf("%4d", row * col);
    }
    System.out.println();
}
```

**[ASK]** "How many total times does the inner loop body execute?"

"5 × 5 = 25 times. For each of the 5 rows, the inner loop runs 5 times."

⚠️ **WATCH OUT:** "This is where nested loops can hurt performance. If the outer loop runs N times and the inner loop runs N times, you have N² iterations. For small data that's fine. For large datasets (thousands of rows), N² can be very slow. We'll cover algorithmic complexity later in the course."

---

### SECTION 6: Infinite loop awareness

**[ACTION]** Scroll to Section 6.

"This section is important — I've commented out the truly infinite loop so we don't accidentally run it. Look at the examples. What makes each one infinite?"

"The first: `while(true)` — condition is always true, no exit. The second: `i >= 0` with `i++` — i will grow forever and never be negative."

"The controlled version at the bottom shows the legitimate pattern: `while(true)` with an internal `break`. We'll cover `break` in detail right after this — it's actually the next file."

→ **TRANSITION:** "You now have all four loop types. The last piece is controlling loops mid-execution — skipping iterations or exiting early. That's `break` and `continue`."

---

## FILE 3: `03-break-and-continue.java`

**[ACTION]** Open `03-break-and-continue.java`.

"This file is all about fine-tuning how loops execute. Two statements: `break` exits the loop immediately. `continue` skips the rest of the current iteration and moves to the next one."

---

### SECTION 1: break — exit early

**[ACTION]** Scroll to Section 1.

"Classic use case: searching. You have a list of products and you want to find 'Keyboard':"

```java
for (int i = 0; i < products.length; i++) {
    if (products[i].equals(target)) {
        foundIndex = i;
        break;
    }
}
```

"Without `break`, even after finding it, Java would keep looping through all remaining products. `break` says 'I'm done — get out of the loop now.'"

**[ASK]** "If 'Keyboard' is at index 2 and the array has 5 elements — how many iterations actually run?"

"Three (indices 0, 1, 2). The `break` fires at index 2 and we never check 3 or 4."

---

### SECTION 2: break in error handling

**[ACTION]** Scroll to Section 2.

"Another very common pattern: process items until you hit an error state, then stop:"

```java
if (status.equals("ERROR")) {
    System.out.println("Halting!");
    break;
}
```

"This is a safety valve. In real systems you'd probably also log the error and trigger an alert."

---

### SECTION 3 & 4: continue — skip an iteration

**[ACTION]** Scroll to Section 3.

"Contrast `break` with `continue`. This only prints passing scores:"

```java
for (int score : testScores) {
    if (score < 60) {
        continue;  // skip this iteration
    }
    System.out.println("Passing score: " + score);
}
```

"When the score is below 60, `continue` fires: we jump immediately to the next iteration. The `println` below it never runs for failing scores."

**[ASK]** "Could I rewrite this WITHOUT continue, using just an if statement?"

"Yes — `if (score >= 60) { System.out.println(...); }`. Both work. `continue` tends to read more cleanly when the 'skip' condition is the important part and you want to express rejection upfront."

---

### SECTION 5: break vs continue — side by side

**[ACTION]** Scroll to Section 5.

"Look at this comparison carefully:"

```
break at 5:    1 2 3 4
continue at 5: 1 2 3 4 6 7 8 9 10
```

**[ASK]** "Before I say the output — what does each one print?"

"`break` stops the whole loop when `i == 5`. `continue` just skips the 5 and keeps going."

⚠️ **WATCH OUT:** "Students sometimes confuse these. A memory trick: `break` breaks the loop (ends it), `continue` continues to the next round (skips current)."

---

### SECTION 6: Labeled break (nested loops)

**[ACTION]** Scroll to Section 6.

"Here's a subtle but important limitation of plain `break`: in nested loops, `break` only exits the **innermost** loop. If you want to exit the outer loop too, you need a **label**."

```java
outerLoop:
for (int row = 0; row < grid.length; row++) {
    for (int col = 0; col < grid[row].length; col++) {
        if (grid[row][col] < 0) {
            break outerLoop;  // exits BOTH loops
        }
    }
}
```

"The label is just a name followed by a colon, placed before the loop you want to exit. The `break outerLoop` exits that specific loop — and since it's the outer one, you're completely done."

**[ASK]** "Without the label, what would plain `break` do here?"

"It would exit the inner loop but the outer loop would continue — you'd move to the next row and keep searching. With the label, the moment we find a negative, we're completely done."

---

### SECTION 7: Labeled continue

**[ACTION]** Scroll to Section 7.

"Labeled `continue` is similar — it skips to the next iteration of the labeled (outer) loop:"

```java
rowLoop:
for (int row = 0; ...) {
    for (int col = 0; ...) {
        if (matrix[row][col] == 0) {
            continue rowLoop;  // skip to next row
        }
    }
    System.out.println("Row complete");
}
```

"If we find a zero anywhere in a row, we skip the rest of that row entirely and move to the next one. The 'Row complete' message only prints for rows with no zeros."

⚠️ **WATCH OUT:** "Labeled break and continue are relatively rare in real code — they can be hard to read. In practice, many developers prefer to extract the nested loops into a separate method and use a plain `return` to exit early. But you will see labeled breaks in competitive programming and occasionally in production code, so you need to recognize them."

---

### SECTION 8: break in switch

**[ACTION]** Scroll to Section 8.

"Finally — a quick reminder that `break` inside a `switch` statement prevents fall-through. We covered this in the first file, but it's worth reinforcing: `break` in a switch and `break` in a loop do the same thing conceptually — they exit the enclosing structure."

→ **TRANSITION:** "That's it for Part 1! You now have the full toolkit: making decisions with `if-else` and `switch`, repeating with all four loop types, and controlling loop execution with `break` and `continue`. After the break, we'll apply these skills to work with arrays — and then we'll see some real programming patterns that use everything together."

---

## SELF-CHECK ✅

- [x] Control flow: if, if-else, if-else-if chain, nested if, ternary
- [x] switch: traditional (with fall-through demo), switch expression (Java 14+), multiple labels, String switch
- [x] Loops: for (counting up, counting down, stepping), while (unknown iterations, sentinel), do-while (at least once), for-each (array + List)
- [x] Nested loops demonstrated
- [x] Infinite loop awareness
- [x] break: early exit from search, error handling, break vs continue comparison
- [x] continue: skip failing scores, skip evens
- [x] Labeled break: exit nested loop grid search
- [x] Labeled continue: skip row with zero
- [x] All Learning Objectives addressed: control flow ✓, loops ✓, break/continue ✓
