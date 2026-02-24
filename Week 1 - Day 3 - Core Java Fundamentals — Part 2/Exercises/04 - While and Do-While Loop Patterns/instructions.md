# Exercise 04: While and Do-While Loop Patterns

## Objective
Understand when to use a `while` loop vs a `do-while` loop, and practice controlling loop execution with a condition variable.

## Background
A `while` loop checks its condition **before** each iteration — if the condition is false from the start, the body never runs. A `do-while` loop checks its condition **after** each iteration — the body always runs **at least once**. This difference matters in scenarios like menus (always show at least once) or input validation (always prompt at least once).

## Requirements

1. **While loop — digit sum**: Given `int number = 1534`:
   - Use a `while` loop to repeatedly extract the last digit (using `% 10`) and add it to a running sum, then remove that digit (using `/ 10`).
   - Continue until `number` becomes 0.
   - Print the digit sum: `"Digit sum of 1534 : 13"`

2. **While loop — powers of 2**: Starting from `1`, use a `while` loop to print all powers of 2 that are **less than 1000** (i.e., 1, 2, 4, 8, 16 ... 512), one per line.

3. **Do-while loop — input simulation**: Simulate a PIN entry. Set `int correctPin = 1234` and create an `int[]` of attempts: `{9999, 0000, 1234}`. Use a `do-while` loop that:
   - Takes the next attempt from the array (use an index variable)
   - Prints `"Attempting PIN: [attempt]"`
   - Continues looping while the attempt does not equal `correctPin` AND there are more attempts
   - After the loop, print either `"Access granted!"` or `"Access denied!"` depending on whether the last attempt matched

4. **Key difference comment**: After both loops, add a multi-line comment explaining in 2 sentences why `do-while` is better than `while` for the PIN scenario.

## Hints
- `number % 10` gives the units digit. `number / 10` drops the units digit.
- For powers of 2: start with `int power = 1;` and double it each iteration with `power *= 2`.
- For the do-while, the condition goes at the **bottom** after `} while (condition);` — note the semicolon.
- To check if there are more attempts: track an index variable and check `index < attempts.length`.

## Expected Output
```
=== While: Digit Sum ===
Digit sum of 1534 : 13

=== While: Powers of 2 < 1000 ===
1
2
4
8
16
32
64
128
256
512

=== Do-While: PIN Attempts ===
Attempting PIN: 9999
Attempting PIN: 0
Attempting PIN: 1234
Access granted!
```
