# Day 12 — Part 2 Walkthrough Script
## JavaScript Fundamentals — Functions, Closures, Hoisting, Control Flow & Errors
**Estimated time:** ~90 minutes  
**Files covered:**
1. `Part-2/01-functions-and-this.js`
2. `Part-2/02-closures-and-hoisting.js`
3. `Part-2/03-control-flow-and-errors.js`

---

## Opening — The Second Half of JavaScript (3 min)

> "Part 1 was about data — how to store and transform values. Part 2 is about behaviour — how to structure logic, how functions really work under the hood, and how to handle the things that go wrong. These are the topics that separate junior developers from ones who truly understand the language."

---

## FILE 1 — `01-functions-and-this.js` (~30 min)

`[ACTION]` Open `01-functions-and-this.js`.

---

### 1.1 Function Declarations (6 min)

`[ACTION]` Scroll to Section 1.

> "A function declaration starts with the `function` keyword and a name. It's the most basic way to define reusable code in JavaScript. Unlike expressions, declarations are hoisted — but we'll cover that in File 2."

Walk through `greet`:
> "Parameters go in parentheses. The body goes in curly braces. The `return` keyword sends a value back to the caller. If you omit `return`, the function returns `undefined`."

Walk through `createCourseMessage` with default parameter:
> "Default parameter values are assigned when no argument is passed, or when `undefined` is passed. `null` does NOT trigger a default — only `undefined`. This is a common gotcha."

Walk through `buildStudent`:
> "Functions can return any value — including objects. Notice `name` alone on line — that's shorthand property notation. When the key name and variable name are the same, you only need to write it once."

`[ASK]` "What would `logMessage('Server started')` return?"  
*Answer: `undefined` — no return statement, so JavaScript returns undefined automatically.*

Walk through `sum` with rest parameters:
> "The `...numbers` rest parameter collects all remaining arguments into an actual array. It must be the last parameter. This replaces the old `arguments` object pattern."

---

### 1.2 Function Expressions (6 min)

`[ACTION]` Scroll to Section 2.

> "A function expression assigns a function to a variable. The function itself has no name — it's anonymous. One key difference from declarations: expressions are NOT hoisted. You cannot call them before the `const` or `let` that declares them."

Walk through `multiply`.

Walk through `factorial`:
> "Named function expression — the function has an internal name `calcFactorial` that it can use to call itself recursively. The name isn't accessible from outside. Useful for self-referencing and better stack traces in errors."

Walk through `mathOps`:
> "Functions are first-class values in JavaScript — you can store them in objects, arrays, pass them as arguments, return them from functions. This is the foundation of callback-based programming."

Walk through `applyOperation`:
> "We pass a function as an argument — this is a callback. `applyOperation(8, 4, mathOps.multiply)` — we're not calling `multiply` here, we're passing a reference to it. The function runs later, inside `applyOperation`."

`[ASK]` "What's the difference between `mathOps.multiply` and `mathOps.multiply()`?"  
*Answer: Without parentheses — a reference to the function. With parentheses — calling the function immediately.*

---

### 1.3 Arrow Functions (6 min)

`[ACTION]` Scroll to Section 3.

> "Arrow functions — introduced in ES6 — have a shorter syntax and one critical difference in how they handle `this`. Let's start with the syntax."

`[ACTION]` Board — arrow function shorthand rules:
```
Full:          (a, b) => { return a + b; }
Implicit:      (a, b) => a + b        (single expression, no braces, no return)
Single param:  x => x * x             (no parentheses needed)
No params:     () => "hello"
Return object: (x, y) => ({ x, y })   (must wrap in parens)
```

> "When you have a single expression in the body, you can drop the curly braces and the `return` keyword — it's implicitly returned. This makes array callbacks very concise."

Walk through the temperature chain:
> "Three methods chained — filter, then map, then filter again. Read it left-to-right: start with temperatures, keep those above freezing, convert to Celsius, keep the warm days. This is the power of arrow functions as callbacks."

Walk through the IIFE:
> "Immediately Invoked Function Expression. The function is defined AND called in the same line. The wrapping `()` around the function turn it from a declaration (which can't be anonymous) into an expression. Useful for creating a private scope."

---

### 1.4 'this' Keyword (12 min)

`[ACTION]` Scroll to Section 4.

> "This is the most confusing part of JavaScript for developers coming from Java. In Java, `this` always refers to the current object. In JavaScript, `this` is determined at CALL TIME — by how the function is invoked, not where it's defined."

`[ACTION]` Board:
```
How function is called         What 'this' is
─────────────────────────────  ──────────────────────────────────
obj.method()                   obj
function()  (strict mode)      undefined
function()  (sloppy mode)      global object (window in browser)
new Fn()                       new object being constructed
fn.call(obj, ...)              obj (explicitly set)
fn.apply(obj, [...])           obj (explicitly set)
fn.bind(obj)  → newFn          obj (permanently bound)
Arrow function                 'this' from outer lexical scope
```

Walk through `userProfile.greet()`:
> "`userProfile.greet()` — the dot before `greet` tells us the calling context. `this` is `userProfile`. That's why `this.username` works."

Walk through the "losing this" section:
> "But watch what happens when we extract the method into a variable. `const greetFn = userProfile.greet` — we have a reference to the same function, but now when we call `greetFn()`, there's no dot before it, no calling context. `this` is no longer `userProfile`. In strict mode, `this` is `undefined` — that will throw a TypeError."

`[ASK]` "So what do we do when we need to pass a method as a callback but keep the right `this`?"  
*Answer: Use `bind()` to permanently attach `this`.*

Walk through `bind`, `call`, `apply`:
> "`bind` returns a new function permanently bound to the given object — you call it later. `call` and `apply` invoke the function immediately with an explicit `this`. The only difference between `call` and `apply` is how extra arguments are passed — individually in `call`, as an array in `apply`."

Walk through `BankAccount` class:
> "In a class, `this` inside methods refers to the instance created by `new`. That's the same as Java. But notice `getStatement` is defined as an arrow function property — this is a class field syntax. Arrow function class fields capture `this` at construction time, making them safe to pass as callbacks."

Walk through `timer.start()` and the inner `tick` arrow:
> "This is the real-world use case for arrow functions inside methods. If `tick` were a regular function, it would lose the `this` reference to `timer` when called by something like `setInterval`. As an arrow function, it inherits `this` from `start()`, which is correctly `timer`."

⚠️ **WATCH OUT** — This is the #1 JavaScript interview question category. Know the four binding rules: default (global/undefined), implicit (dot notation), explicit (call/apply/bind), new (constructor). Arrow functions are the override — they ignore all four rules and use lexical `this`.

---

## FILE 2 — `02-closures-and-hoisting.js` (~25 min)

`[ACTION]` Open `02-closures-and-hoisting.js`.

---

### 2.1 Closures & Lexical Scope (15 min)

> "Closures are one of the most powerful and most misunderstood features of JavaScript. They're also a guarantee to appear in every JavaScript interview. Let's understand them thoroughly."

> "Lexical scope means: a function has access to variables from the scope in which it was **written** — not where it's called. JavaScript determines scope by where you physically write code in the source file."

Walk through `makeCounter`:
> "When `makeCounter(0)` runs, it creates a local variable `count`. Then it returns a function. That returned function forms a **closure** — it 'closes over' `count` and keeps a reference to it, even after `makeCounter` has finished executing."

`[ACTION]` Board:
```
makeCounter(0) executes and returns
         ↓
  count = 0  [in memory, not garbage collected]
         ↓
  returned function closes over count
         ↓
counterA() → count becomes 1 → returns 1
counterA() → count becomes 2 → returns 2
```

> "Two key observations: First, `count` is NOT garbage collected when `makeCounter` returns — the closure holds a reference to it. Second, `counterA` and `counterB` each have their OWN `count` — separate closures, separate private state."

Walk through `createBankAccount`:
> "This is the Module Pattern — using closures to create private state. `balance` and `transactionLog` are completely inaccessible from outside. The returned object gives you controlled access through methods. This is encapsulation without classes."

`[ASK]` "Is `account.balance` accessible?"  
*Answer: No — `account.balance` is `undefined`. The `balance` variable lives in the closure, not on the returned object.*

Walk through the closure-in-a-loop gotcha:
> "This is one of the most famous JavaScript interview questions. Let me set it up carefully."

`[ACTION]` Board — trace the `var` loop:
```
for (var i = 0; i < 3; i++) { ... }

var i is SHARED — one variable for the whole loop
After loop finishes: i === 3

All 3 callbacks close over the SAME i
When called: they all return 3
```

> "The fix: use `let`. Each iteration of a `let` loop creates a brand new binding — a separate `j` that the closure captures independently."

Walk through the memoize example:
> "Real-world closure use case. `memoize` wraps any function and caches its results. The `cache` object lives in the closure — persists between calls, but is invisible from outside. When called again with the same arguments, returns the cached result instantly."

---

### 2.2 Hoisting (10 min)

`[ACTION]` Scroll to Section 2.

> "Hoisting is what the JavaScript engine does before running your code. It scans the file and 'lifts' certain declarations to the top of their scope."

Walk through function declaration hoisting:
> "I'm calling `formatCurrency(42.5)` BEFORE the function declaration appears in the file. This works — function declarations are fully hoisted, meaning both the declaration and the function body are available from the very start of the scope."

> "This is why you often see utility functions at the bottom of a file in JavaScript projects — the main logic is at the top, and the helpers are below. Technically valid, but I'd argue it's better style to define before use."

Walk through `var` hoisting:
> "Var declarations are hoisted but only to `undefined` — not to their value. The assignment stays in place. So if you access a `var` before its assignment, you don't get a ReferenceError — you get `undefined`. This is sneaky and produces bugs that are very hard to find."

`[ACTION]` Board — what the engine does to `var`:
```
YOUR CODE                     ENGINE SEES
─────────────────────         ─────────────────────
console.log(x);               var x;           ← hoisted
var x = 10;                   console.log(x);  ← undefined (not 10)
console.log(x);               x = 10;
                              console.log(x);  ← 10
```

Walk through the TDZ:
> "Temporal Dead Zone. `let` and `const` ARE hoisted — the engine knows they exist — but they're not initialized. Accessing them before their declaration throws a ReferenceError. This is actually the better behaviour — it prevents the silent `undefined` bug that `var` creates."

`[ACTION]` Show the hoisting summary table.

> "Memorise the last column. Function declarations: safe to call before. `var`: returns undefined before (don't rely on this). `let`/`const`: ReferenceError before. Function expressions and arrow functions: depends on the variable type, but treat as ReferenceError."

---

## FILE 3 — `03-control-flow-and-errors.js` (~25 min)

`[ACTION]` Open `03-control-flow-and-errors.js`.

---

### 3.1 Strict Mode (3 min)

`[ACTION]` Point to `"use strict";` at the top.

> "A string literal as the very first statement in a file opts the entire file into strict mode. It must be the first executable statement — no code before it."

> "In modern JavaScript projects with ES Modules or TypeScript, strict mode is already on. But in plain JS files, you should add it manually."

Key strict mode behaviours:
- Assigning to an undeclared variable → ReferenceError instead of creating a global
- Writing to a read-only property → TypeError instead of silent failure
- `this` is `undefined` in plain function calls instead of the global object

⚠️ **WATCH OUT** — If you're running in Node.js CommonJS modules, `"use strict"` matters. In ES Modules (`.mjs` files or `"type": "module"` in package.json), strict mode is always on automatically.

---

### 3.2 Truthy & Falsy (5 min)

`[ACTION]` Scroll to Section 2.

> "JavaScript coerces values to booleans in any conditional context — `if`, `while`, `&&`, `||`, `!`. You need to know the 6 falsy values cold."

`[ACTION]` Board:
```
FALSY (all other values are truthy):
  false  |  0  |  ""  |  null  |  undefined  |  NaN
```

> "Common surprises: empty array `[]` is truthy, empty object `{}` is truthy, the string `"0"` is truthy, the string `"false"` is truthy. Many bugs come from expecting `[]` to be falsy — it isn't."

Walk through `processUsername`:
> "`if (!username)` catches `null`, `undefined`, and empty string all at once. This is idiomatic JavaScript. Compare to Java where you'd need separate null checks and string length checks."

Walk through short-circuit evaluation:
> "`&&` returns the first falsy value, or the last value if all are truthy. `||` returns the first truthy value, or the last value if all are falsy. This is how we write default values and safe property access."

Walk through `??` vs `||`:
> "The nullish coalescing operator `??` is like `||` but only falls through for `null` and `undefined` — not for `0`, `""`, or `false`. Use `??` when `0` or `""` is a valid value that shouldn't trigger the default."

Walk through optional chaining:
> "The `?.` operator short-circuits to `undefined` if the left side is `null` or `undefined`, instead of throwing a TypeError. Chain them to safely drill into nested objects."

`[ASK]` "Without optional chaining, what would `order.shipping.trackingId` do if `shipping` is undefined?"  
*Answer: TypeError — `Cannot read properties of undefined`.*

---

### 3.3 Control Flow (7 min)

Walk through `getLetterGrade`:
> "Notice the guard clause at the top — validate the input first, return early if invalid. This keeps the happy path unindented and easy to read. Better than wrapping everything in a big if/else."

Walk through ternary:
> "Ternary is a single-expression conditional — perfect for assignment. `condition ? valueIfTrue : valueIfFalse`. Avoid nesting ternaries more than two levels deep — readability tanks fast."

Walk through `describeHttpStatus`:
> "Switch is cleaner than `if/else if` for multiple equality comparisons against the same value. Each case MUST have a `return` or `break` — otherwise execution falls through to the next case."

Walk through `getSeasonActivity`:
> "This is intentional fall-through. Multiple cases with no break or return between them share the same handler. Useful when grouping related values."

⚠️ **WATCH OUT** — Forgetting a `break` or `return` in a switch case is a classic bug. Execution falls through to the next case silently. Always double-check your cases have break/return.

---

### 3.4 Loops (7 min)

Walk through each loop type quickly:

**Classic `for`** — "Use when you need the index. `i < array.length` — not `<=`!"

**`for...of`** — "The modern loop for arrays and any iterable. Cleaner than for with index. Use this for arrays when you don't need the index."

**`for...in`** — "Iterates over the KEYS of an object — you get string keys. Use for plain objects, not arrays."

⚠️ **WATCH OUT** — `for...in` on an array gives you the indices as string `"0"`, `"1"`, `"2"`, not numbers. And it can pick up inherited properties. Use `for...of` for arrays.

**`while`** — "Use when you don't know how many iterations you need. The login-attempt example: we don't know if the user will succeed on attempt 1, 2, or 3."

**`do...while`** — "Like while but runs the body at least once before checking the condition. The dice roll example: you always roll at least once."

Walk through `break` and `continue`:
> "The submissions array demo uses both. `continue` skips to the next iteration — like `return` inside a loop body but for the loop itself. `break` exits the loop entirely."

---

### 3.5 Error Handling (7 min)

`[ACTION]` Scroll to Section 5.

> "In JavaScript, any code can throw an error — a network request failing, JSON parsing bad input, calling a method on null. If an error is not caught, it crashes the program. `try/catch/finally` is how we handle errors gracefully."

Walk through `throw`:
> "You can throw anything — a string, a number, an object. But always throw an `Error` object or a subclass. Error objects have a `.message`, a `.name`, and a `.stack` property that shows you where in the code the error occurred."

Walk through `try/catch/finally` in `safeDivide`:
> "Code in the `try` block runs normally. If any line throws, execution immediately jumps to `catch`. The error object is bound to the parameter name — `error` here. `finally` runs no matter what — with or without an error."

`[ASK]` "What would happen if we had a `return result` inside the `try` block AND a `finally` block?"  
*Answer: `finally` still runs. The return value is held until `finally` completes.*

Walk through multiple error type checks:
> "`instanceof` lets you check the specific type of error. This is how you handle different error scenarios differently — invalid JSON gets one message, a missing field gets another."

> "The `throw error` at the bottom is a **re-throw**. If the error is some unexpected type we didn't plan for, we don't silently swallow it — we re-throw it so it propagates up and we don't hide bugs."

Walk through custom error classes:
> "Extend the built-in `Error` class to create domain-specific errors. Always call `super(message)` to set the `.message` property. Add your own custom fields. Now your error handler can use `instanceof ValidationError` to make precise decisions."

`[ACTION]` Show the error type cheat sheet at the bottom.

> "Know your error types. `TypeError` is the most common — calling something that isn't a function, accessing a property on `null` or `undefined`. `ReferenceError` is the second most common — using a variable that doesn't exist."

---

## Wrap-Up Q&A (5 min)

**Q1:** "What is a closure in one sentence?"  
*Answer: A function that retains access to variables from its outer scope even after that outer scope has finished executing.*

**Q2:** "What are the 6 falsy values?"  
*Answer: `false`, `0`, `""`, `null`, `undefined`, `NaN`.*

**Q3:** "Arrow functions vs regular functions — what's the key difference?"  
*Answer: Arrow functions don't have their own `this` — they capture `this` from the enclosing lexical scope. Also, they can't be used as constructors.*

**Q4:** "What does `"use strict"` prevent?"  
*Answer: Silently creating global variables, silently failing writes to read-only properties, and `this` being the global object in plain function calls (it becomes `undefined` instead).*

**Q5:** Write this on the board — ask students to predict the output:
```js
function makeAdder(x) {
  return function(y) {
    return x + y;
  };
}
const add5 = makeAdder(5);
console.log(add5(3));
console.log(add5(10));
```
*Answer: `8` and `15`. `x` is closed over as `5`. Each call provides a different `y`.*

---

→ **TRANSITION TO EXERCISES:**
> "Your exercises today cover two things: first, you'll build a closure-based counter with increment, decrement, and reset — similar to what we saw. Then you'll write a function that validates form input and uses try/catch to handle all the error cases cleanly. Both of these patterns you will write again and again in real projects."
