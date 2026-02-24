# Exercise 07: Mathematical, Logical, and Comparison Operators

## Objective
Apply all categories of Java operators — arithmetic, comparison, logical, bitwise, assignment, and ternary — and understand operator precedence.

## Background
Java operators are the building blocks of all expressions. Arithmetic operators manipulate numbers; comparison operators return `boolean`; logical operators combine booleans; the ternary operator is a one-line conditional expression. Understanding operator precedence prevents subtle bugs — for example, `2 + 3 * 4` evaluates to `14`, not `20`, because `*` has higher precedence than `+`.

## Requirements

1. **Arithmetic operators** — given `int a = 17` and `int b = 5`:
   - Print the results of: `a + b`, `a - b`, `a * b`, `a / b`, `a % b`
   - Also demonstrate integer division truncation: explain (with a comment) why `17 / 5 = 3` not `3.4`
   - Print the result of `(double) a / b` to get the decimal result

2. **Comparison operators** — given `int x = 10` and `int y = 20`:
   - Print the results of: `x == y`, `x != y`, `x < y`, `x > y`, `x <= y`, `x >= y`

3. **Logical operators** — given `boolean p = true` and `boolean q = false`:
   - Print the results of: `p && q`, `p || q`, `!p`, `!q`
   - Demonstrate **short-circuit evaluation**: write a compound condition using `&&` where the second condition is only evaluated if the first is true (add a comment explaining what "short-circuit" means)

4. **Compound assignment operators** — starting with `int n = 10`:
   - Apply and print: `n += 5`, `n -= 3`, `n *= 2`, `n /= 4`, `n %= 3`
   - Print `n` after each operation

5. **Ternary operator** — given `int score = 74`:
   - Use the ternary operator to assign `"Pass"` if `score >= 70`, otherwise `"Fail"`, to a String variable named `grade`
   - Print: `"Score 74: [grade]"`

6. **Operator precedence** — evaluate and print the following WITHOUT changing the expressions (no extra parentheses):
   - `2 + 3 * 4` — explain in a comment why this is `14` not `20`
   - `10 - 4 / 2` — explain in a comment why this is `8` not `3`
   - `true || false && false` — explain in a comment why this is `true`

## Hints
- Integer division in Java always truncates toward zero — `17 / 5` is `3`, not `3.4`.
- The `%` operator gives the **remainder**: `17 % 5 = 2` because `17 = 3 * 5 + 2`.
- Short-circuit `&&`: if the left side is `false`, the right side is never evaluated.
- Short-circuit `||`: if the left side is `true`, the right side is never evaluated.
- Operator precedence (high to low for common operators): `*`, `/`, `%` → `+`, `-` → `<`, `>`, `<=`, `>=` → `==`, `!=` → `&&` → `||`.

## Expected Output
```
=== Arithmetic Operators (a=17, b=5) ===
a + b  : 22
a - b  : 12
a * b  : 85
a / b  : 3   (integer division — truncated)
a % b  : 2   (remainder)
(double)a/b : 3.4

=== Comparison Operators (x=10, y=20) ===
x == y : false
x != y : true
x < y  : true
x > y  : false
x <= y : true
x >= y : false

=== Logical Operators (p=true, q=false) ===
p && q : false
p || q : true
!p     : false
!q     : true

=== Compound Assignment (n starts at 10) ===
n += 5  → n = 15
n -= 3  → n = 12
n *= 2  → n = 24
n /= 4  → n = 6
n %= 3  → n = 0

=== Ternary Operator ===
Score 74: Pass

=== Operator Precedence ===
2 + 3 * 4       = 14
10 - 4 / 2      = 8
true || false && false = true
```
