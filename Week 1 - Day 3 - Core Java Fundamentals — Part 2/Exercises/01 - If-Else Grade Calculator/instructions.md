# Exercise 01: If-Else Grade Calculator

## Objective
Use if-else and else-if chains to translate a numeric score into a letter grade and a performance message.

## Background
Conditional logic is the backbone of every program that makes decisions. A grade calculator is a classic real-world scenario: given a student's numeric score (0–100), determine which letter grade bucket it falls into and output a personalized feedback message.

## Requirements

1. Declare an `int` variable named `score` with the value `82`.

2. Using an **if-else-if** chain, determine the letter grade:
   - 90–100 → `"A"`
   - 80–89  → `"B"`
   - 70–79  → `"C"`
   - 60–69  → `"D"`
   - 0–59   → `"F"`
   Store the result in a `String` variable named `grade`.

3. Using a **nested if-else** inside the grade block (or a separate if-else after), determine the feedback message:
   - A → `"Excellent work!"`
   - B → `"Good job!"`
   - C → `"Passing, but room to improve."`
   - D → `"At risk — seek help soon."`
   - F → `"Did not pass. Please retake."`
   Store the result in a `String` variable named `feedback`.

4. Print the score, grade, and feedback in the format shown in Expected Output.

5. Also handle an **invalid score** case: add an additional check (before the grade chain) using a simple `if` that prints `"Invalid score!"` and returns early (or skips the rest) if `score < 0 || score > 100`. Demonstrate this by temporarily testing with `-5` (you can comment it back to `82` for the final run).

## Hints
- `else if` is not a separate keyword — it's `else` immediately followed by `if`.
- Order matters: put the highest range first so each condition is checked in descending order.
- You can nest an `if` inside an `else if` block, or use a second `if-else` chain after the grade is determined.
- A `return` statement inside `main` will exit the method immediately — useful for the invalid score guard.

## Expected Output
```
Score : 82
Grade : B
Feedback: Good job!
```
*(If score is -5)*
```
Invalid score!
```
