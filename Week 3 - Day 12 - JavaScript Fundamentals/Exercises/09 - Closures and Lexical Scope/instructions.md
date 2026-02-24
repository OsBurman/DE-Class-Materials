# Exercise 09: Closures and Lexical Scope

## Objective
Understand lexical scoping and closures by building functions that retain access to their enclosing scope's variables even after the outer function has returned.

## Background
A **closure** is a function that "closes over" the variables in its surrounding lexical scope. Every time a function is created in JavaScript, it captures a reference to the variables available in the scope where it was defined — not where it is called. This enables powerful patterns like private state, factory functions, and the module pattern.

## Requirements

1. **Basic closure — counter factory:**
   - Write a function `makeCounter(start = 0)` that returns an object with three methods: `increment()`, `decrement()`, and `getCount()`.
   - Each call to `increment()` adds 1 to the internal count; `decrement()` subtracts 1; `getCount()` returns the current count.
   - The internal count variable must NOT be accessible from outside the returned object.
   - Create two independent counters `counterA` and `counterB` and demonstrate they have separate state.

2. **Closure retaining outer variable:**
   - Write a function `makeAdder(x)` that returns a function which adds `x` to its argument.
   - Create `add5 = makeAdder(5)` and `add10 = makeAdder(10)`.
   - Log `add5(3)` → 8, `add10(3)` → 13, `add5(add10(2))` → 17.
   - Explain in a comment how `x` is captured.

3. **The classic loop-closure bug:**
   - Show the bug: create an array of 3 functions using a `for` loop with `var i`.
     Call each function and show they all log `3` (not 0, 1, 2).
   - Fix 1: use `let` instead of `var`. Show the fixed output: 0, 1, 2.
   - Fix 2: use an IIFE inside the `var` loop to capture the current value. Show it also outputs 0, 1, 2.

4. **Module pattern — private state:**
   - Use an IIFE to create a `bankAccount` module with:
     - A private variable `balance` starting at 0.
     - A public API object with methods: `deposit(amount)`, `withdraw(amount)`, `getBalance()`.
     - `withdraw` should log an error message and refuse if the amount exceeds the balance.
   - Demonstrate: deposit 100, deposit 50, withdraw 30, withdraw 200 (should be refused), log final balance.

5. **Closure in a callback:**
   - Write a function `delayedGreeting(name)` that uses `setTimeout` (delay 0ms for immediate execution in the event loop) to log `"Hello, Alice!"` using the closed-over `name`.
   - Explain in a comment why `name` is still accessible inside the callback even though `delayedGreeting` has returned.

6. **Scope chain:**
   - Write nested functions demonstrating the scope chain:
     ```
     function outer() {
       const a = "outer";
       function middle() {
         const b = "middle";
         function inner() { /* can see a, b, and its own vars */ }
         inner();
       }
       middle();
     }
     ```
   - Inside `inner`, log `a`, `b`, and a variable `c = "inner"` to prove all three scope levels are visible.

## Hints
- A closure captures a **reference** to the variable, not a copy of its value at the time of creation — this is why the loop bug happens with `var` (all closures share the same `i`).
- Using `let` in a `for` loop creates a **new binding** of `i` for each iteration, so each closure captures a different variable.
- The module pattern uses an IIFE to create a private scope; the returned object is the only way to interact with the private state.
- `setTimeout(fn, 0)` schedules `fn` to run after the current call stack clears — the enclosing function will have already returned, yet `name` is still alive in the closure.

## Expected Output

```
--- counter factory ---
counterA: 0 → increment → 1 → increment → 2 → decrement → 1
counterB: 0 → increment → 1   (independent from counterA)

--- makeAdder ---
add5(3) → 8
add10(3) → 13
add5(add10(2)) → 17

--- loop closure bug (var) ---
3
3
3

--- fix 1: let ---
0
1
2

--- fix 2: IIFE ---
0
1
2

--- bank account ---
Deposited $100. Balance: $100
Deposited $50. Balance: $150
Withdrew $30. Balance: $120
Cannot withdraw $200: insufficient funds. Balance: $120
Final balance: $120

--- scope chain ---
inner can see a: outer
inner can see b: middle
inner owns c: inner

--- delayed greeting ---
Hello, Alice!
```
