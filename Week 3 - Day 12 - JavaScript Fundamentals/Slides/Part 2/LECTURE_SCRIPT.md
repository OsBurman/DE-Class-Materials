# Week 3 - Day 12 (Tuesday): JavaScript Fundamentals
## Part 2 — Lecture Script (60 Minutes)

---

**[00:00–02:00] Welcome Back and Part 2 Overview**

Welcome back from the break. In Part 1 we covered the foundation: variables, data types, coercion, arrays, template literals, functions, closures. You now have the building blocks of JavaScript.

Part 2 is where we go deeper. We're going to look at the `this` keyword — which is JavaScript's most confusing feature and also one of the most commonly tested in technical interviews. We'll revisit closures with more practical patterns. We'll go deep on hoisting. We'll cover strict mode, objects and destructuring, advanced error handling, and we'll do a preview of the event loop and ES6 modules.

By the end of today, you'll have a complete picture of JavaScript as a language. Tomorrow in Day 13 you'll use JavaScript to manipulate the DOM — the actual HTML elements on the page. That's where the magic becomes visible. But you need today's foundation to make sense of that.

Let's start with `this`.

---

**[02:00–08:00] The `this` Keyword — The Context Problem**

`this` is JavaScript's most infamous feature. Let me give you the key principle first, then we'll look at examples.

In Java, `this` always refers to the current class instance. Period. It's simple and predictable.

In JavaScript, `this` refers to the **execution context** — the object that a function is being called on. And crucially: `this` is determined at **call time**, not at definition time. Where you write a function doesn't matter. How you call it does.

Let me show you the four ways `this` gets set. I'll go from simplest to most complex.

**First: implicit binding.** When you call a method on an object, `this` is that object. Let me type this: `const user = { name: "Alice", greet() { console.log(this.name); } }`. When I call `user.greet()`, `this` is the `user` object. It works exactly like Java. That's the easy case.

**Second: the common bug.** Watch what happens when I extract that method. `const fn = user.greet`. Now I call `fn()`. What do you expect? ... Undefined. Why? Because I'm no longer calling it ON `user`. I'm calling it as a plain function. There's no object before the dot. So `this` gets the "default binding" — in strict mode that's `undefined`, in non-strict mode it's the global `window` object. Either way, `this.name` doesn't exist and you get `undefined`.

This is the bug that bites everyone: you pass an object method as a callback, and it loses its `this`. You've extracted the function from the object.

**Third: arrow functions.** Arrow functions do NOT have their own `this`. They inherit `this` lexically from their enclosing scope — wherever the arrow function was defined.

Let me show why this matters. Classic problem: I have an object with a `start` method that uses `setTimeout`. I want the callback to use `this.message`. If I write `setTimeout(function() { console.log(this.message); }, 1000)` — regular function — `this` inside that callback is `undefined` in strict mode. The callback runs standalone, not on the object.

If I change it to an arrow function: `setTimeout(() => { console.log(this.message); }, 1000)` — now `this` is inherited from the `start` method's context, which is the object. It works.

Arrow functions are the modern solution to the callback `this` problem. This is exactly why React uses arrow functions for event handlers.

---

**[08:00–12:00] Explicit `this` — call, apply, bind**

Sometimes you need to explicitly control what `this` is. Three methods do this: `call`, `apply`, and `bind`.

`call` invokes a function immediately with a specified `this` and individual arguments. Let me write a standalone function: `function introduce(greeting) { console.log(\`${greeting}, I'm ${this.name}\`); }`. This function uses `this.name`, but it's not on any object.

`introduce.call({ name: "Alice" }, "Hello")` — the first argument to `call` is the `this` value, the rest are the function's arguments. Output: "Hello, I'm Alice." I can reuse the same function with different objects.

`apply` is identical to `call` except it takes arguments as an array. `introduce.apply({ name: "Bob" }, ["Hi"])` — same result with "Bob". The mnemonic: **call** = comma-separated, **apply** = array.

`bind` is different. It doesn't call the function immediately. It returns a NEW function with `this` permanently locked in. `const greetAlice = introduce.bind({ name: "Alice" }, "Hello")`. Now `greetAlice` is a function I can call later with `this` always being Alice.

The most practical use of `bind`: event handlers in classes. If I have a class with a `handleClick` method, and I want to use it as an event listener, I do `button.addEventListener("click", this.handleClick.bind(this))` in the constructor. This permanently binds `this` to the class instance so it works correctly when the browser calls it.

The modern alternative is the arrow function class field: `handleClick = () => { ... }` — each instance gets its own arrow function, which inherits `this` lexically. Both patterns are valid.

---

**[12:00–16:00] Functions as First-Class Values**

One of JavaScript's most powerful features is that functions are first-class citizens. This means functions are just values — like numbers or strings — and can be stored, passed around, and returned.

You can store a function in a variable: `const greet = name => \`Hello, ${name}!\``. Already doing this with arrow functions.

You can pass a function as an argument: `[1, 2, 3].map(n => n * 2)`. The `map` method takes a function as an argument. That function is called a **callback**.

You can return a function from another function. Let me show this — it's called a **factory function**:

`function multiplier(factor) { return number => number * factor; }`. I call `multiplier(2)` and I get back a function. That returned function is a closure — it closes over `factor`. So `const double = multiplier(2)` gives me a `double` function, and `const triple = multiplier(3)` gives me a `triple` function. They're distinct functions created by the same factory, each with their own enclosed `factor` value.

Functions that take or return other functions are called **higher-order functions**. `map`, `filter`, `reduce`, `setTimeout`, `addEventListener` — all higher-order functions. This pattern is everywhere in JavaScript.

Callbacks are the fundamental pattern for async operations — "do this work, and when you're done, call this function with the result." Day 14 replaces callback chains with Promises and async/await, but the mental model starts here.

---

**[16:00–22:00] Hoisting — The Full Picture**

Let me now give you the full picture of hoisting.

JavaScript engines process code in two phases. First, the **creation phase** — the engine scans through your code and does some setup work. Second, the **execution phase** — your code actually runs.

During the creation phase, the engine does three things for declarations: it registers `var` variables (initializing them to `undefined`), it registers function declarations completely (both name and body), and it registers `let`/`const` variables (but marks them as uninitialized — the Temporal Dead Zone).

What this means practically:

**Function declarations are fully hoisted.** I can call `greet("Alice")` on line 1 of my file, even if the `greet` function is defined on line 50. JavaScript already knows the full function from the creation phase.

**`var` declarations are hoisted as `undefined`.** If I access a `var` variable before its assignment, I don't get a ReferenceError — I get `undefined`. That's confusing. You expected an error to tell you something's wrong, but you got `undefined` instead, and now you're debugging a mysterious behavior.

**`let` and `const` are in the Temporal Dead Zone.** If I access them before their declaration, I get `ReferenceError: Cannot access 'x' before initialization`. This is the GOOD failure mode — it tells you exactly what's wrong.

Here's the practical takeaway: use `const` and `let`. If you accidentally access them before assignment, you get a clear error. With `var`, you get `undefined` and a debugging mystery.

Function expressions and arrow functions follow their variable's rules. `const fn = () => {}` — if you call `fn` before this line, you get a ReferenceError because `const` is in TDZ. `var fn = () => {}` — if you call `fn` before this line, you get `TypeError: fn is not a function`, because `fn` is `undefined` at that point (not yet assigned the function).

The mental model: variable names are "registered" early, but values are assigned when the code actually runs.

---

**[22:00–28:00] Advanced Closures — Memoization and the Module Pattern**

Let's go deeper on closures with two practical patterns.

**Memoization.** This is an optimization technique — caching the results of expensive computations. If a function is called with the same arguments multiple times, why compute the result every time? Store it in a cache the first time, then return the cached value.

Here's how closures make this work: `function memoize(fn) { const cache = {}; return function(...args) { const key = JSON.stringify(args); if (key in cache) return cache[key]; cache[key] = fn(...args); return cache[key]; } }`.

The `cache` object is private to the returned function — enclosed in the closure. It persists across calls because the closure holds a reference to the outer `memoize` scope. This is React's `useMemo` hook under the hood — same concept.

**The Module Pattern.** Before ES6 modules, JavaScript had no built-in module system. Developers used a pattern called the Immediately Invoked Function Expression — IIFE, pronounced "iffy" — to create private scope.

The syntax looks like this: `const cart = (() => { let items = []; return { add: (item) => items.push(item), getTotal: () => items.reduce((sum, i) => sum + i.price, 0) }; })()`.

The outer parentheses make the function into an expression instead of a declaration. The `()` at the end immediately calls it. The result is a returned object with public methods. The `items` array is private — enclosed in the IIFE's scope. You can't access `cart.items` from outside. You call `cart.add(item)` and `cart.getTotal()`.

This IS the closure-as-encapsulation pattern. ES6 classes and modules largely replaced this, but you'll see IIFEs in legacy code.

**The loop closure trap.** This is a classic interview question. I'll create an array of functions in a loop:

`const funcs = []; for (var i = 0; i < 3; i++) { funcs.push(() => console.log(i)); }`. Now I call each function: `funcs.forEach(fn => fn())`. What do I get? Three, three, three. All functions log 3.

Why? Because `var i` is function-scoped — there's ONE `i` variable shared by all three closures. By the time the functions run, the loop has completed and `i` is 3.

Fix: change `var` to `let`. `let` creates a new binding for each loop iteration — each closure captures its own `i`. Output: 0, 1, 2.

---

**[28:00–32:00] Strict Mode**

Let me talk about strict mode — `"use strict"`.

Strict mode was introduced in ES5 to fix some of JavaScript's most error-prone behaviors. You enable it by putting the string `"use strict"` at the top of a file or function — yes, a string. It's backward-compatible because old JavaScript engines just see a string literal and ignore it.

The most important thing strict mode does: it turns accidental globals into errors. Without strict mode, if I type `undeclaredVariable = 10` — that variable wasn't declared, I just made a typo — JavaScript silently creates a global variable. With strict mode, that's a `ReferenceError`. The error tells me I made a mistake. Without strict mode, I have a bug and no error.

Second important behavior: `this` inside a regular standalone function call is `undefined` in strict mode, not `window`. This prevents accidentally polluting the global object.

Strict mode also bans duplicate parameter names in functions, bans the `with` statement (which had serious performance and security issues), and makes writing to non-writable properties throw a TypeError instead of silently failing.

Here's the good news for modern developers: if you're using ES6 modules — which you will be in React, Angular, any modern framework — your code is ALREADY in strict mode automatically. Modules are always strict. Classes are always strict. So you get these protections without even needing to add `"use strict"` explicitly.

Where you DO need to add it: plain `.js` files not using ES6 modules, or Node.js scripts without the module type. Good habit to just always add it to file-level JavaScript.

---

**[32:00–38:00] Objects and Destructuring**

Let me cover objects and destructuring — you'll use these every day.

Objects in JavaScript are key-value pairs. The object literal syntax is the standard: `const person = { name: "Alice", age: 30, greet() { return \`Hi, I'm ${this.name}\`; } }`. Notice the method shorthand — `greet()` instead of `greet: function()`.

Accessing properties: dot notation is preferred — `person.name`. Bracket notation for dynamic keys or special characters — `person["name"]`, or when the key is in a variable: `person[dynamicKey]`.

**Destructuring** is one of ES6's best features. Instead of `const name = person.name; const age = person.age;` — two lines, repetitive — you write: `const { name, age } = person`. One line, extract multiple properties. The variable names must match the property names.

You can rename while destructuring: `const { name: fullName } = person` — now you have a variable called `fullName` with the value of `person.name`.

You can provide defaults: `const { role = "user" } = person` — if `person.role` is undefined, `role` defaults to "user".

**Function parameter destructuring** is the pattern you'll use most in React: `function UserCard({ name, age, role = "user" }) { ... }`. Instead of receiving a `props` object and accessing `props.name`, `props.age`, you destructure the properties right in the parameter list.

**Array destructuring** extracts by position: `const [first, second] = ["apple", "banana"]`. Skip elements with empty commas: `const [, , third] = arr`. Rest in arrays: `const [head, ...tail] = arr`.

One trick I want to show: variable swapping. `let a = 1, b = 2; [a, b] = [b, a]`. Done. No temp variable needed. Clean and elegant.

**Object spread**: `const updated = { ...person, age: 31 }` — creates a new object with all of `person`'s properties, but overrides `age`. This is the non-mutating update pattern. Critical in React state management — you never mutate state directly, you create a new object.

---

**[38:00–42:00] Truthy/Falsy Patterns and Short-Circuit Evaluation**

Let me revisit truthy/falsy with practical patterns.

The AND operator `&&` short-circuits on the first falsy value. If the first operand is falsy, it returns that value immediately without evaluating the second. `null && someFunction()` — `someFunction` never gets called. This is useful for conditional execution: `isLoggedIn && showDashboard()` — only calls `showDashboard` if `isLoggedIn` is truthy.

The OR operator `||` short-circuits on the first truthy value. `config.port || 3000` — if `config.port` is truthy, returns it; otherwise 3000. Classic default value pattern.

But remember the `||` vs `??` distinction. `||` triggers on ANY falsy value — including `0` and `""`. `const count = data.count || 0` — if `data.count` is `0` (a valid count), `||` replaces it with `0` from the right side... that's fine here, but what if the default was 5? Then `||` would replace a valid `0` with `5`. That's a bug.

`??` only triggers on `null` and `undefined`. `const count = data.count ?? 0` — `0` is kept as `0`; only if `data.count` is `null` or `undefined` do you get the default. Use `??` when `0`, `false`, or `""` are valid values that should not trigger the default.

**Guard clauses** are a code structure pattern: instead of nesting your logic inside if-else blocks, use early returns to exit when conditions aren't met. `if (!user) return null;` — if no user, exit immediately. `if (!user.isActive) return "User inactive";` — exit if inactive. Then the happy path code is at the top level, not indented into multiple if-else branches.

The double NOT `!!` casts any value to boolean: `!!null` is `false`, `!!"hello"` is `true`. Useful when you need an explicit boolean.

---

**[42:00–46:00] Advanced Error Handling**

Let's upgrade our error handling knowledge from Part 1.

The built-in `Error` class can be extended to create custom error types. I'll write:
```
class ValidationError extends Error {
  constructor(message, field) {
    super(message);
    this.name = "ValidationError";
    this.field = field;
  }
}
```

Now I can throw a `ValidationError` with a `field` property — telling callers which field failed validation. `throw new ValidationError("Email is invalid", "email")`.

When catching, I can check which kind of error it is using `instanceof`: `if (error instanceof ValidationError) { ... }`. This is like Java's multiple catch blocks — you handle different error types differently.

Critical rule: **never swallow errors**. An empty catch block — `catch(e) {}` — is one of the worst things you can write. It silently hides errors. At minimum, log the error. Better: re-throw errors you don't know how to handle so they propagate up. Only catch errors you can actually recover from.

Another best practice: use errors for exceptional situations, not for normal control flow. Don't throw an error when a user doesn't find a search result — that's a normal outcome. Throw an error when a database connection fails.

`finally` always runs, whether there was an error or not. Use it for cleanup: closing file handles, hiding loading spinners, releasing resources. `connection?.close()` in a finally block ensures the connection closes even if the query threw an error.

I'll also mention: async error handling — catching errors from `fetch`, Promises, and `async/await` — is Day 14's territory. The `try/catch` structure is the same, but the async context has nuances we'll explore then.

---

**[46:00–50:00] The Event Loop Preview**

I want to give you a mental model that will pay off enormously in Day 14. The event loop.

JavaScript is single-threaded. There's one call stack. It runs one thing at a time. Yet browsers handle HTTP requests, timers, user clicks, and animations — all seemingly in parallel. How?

The browser has multiple threads. The JavaScript engine only uses one for your code, but the browser's other threads handle timers, network requests, and DOM events. When you call `setTimeout`, you're not running a timer in JavaScript — you're handing the timer to the browser's Timer API, in a different thread.

When that timer completes, the browser puts your callback function in the **callback queue**. The **event loop** watches both the call stack and the callback queue. When the call stack is empty — when your synchronous code has finished — the event loop moves the next callback from the queue onto the stack.

This is why `setTimeout(() => console.log("A"), 0)` — even with 0 milliseconds — doesn't run immediately. It goes through the browser's Timer API, then into the callback queue, and only runs when the call stack is clear. If you have 100ms of synchronous code, that "0ms timer" waits 100ms.

The model: synchronous code runs first, always. Async callbacks wait in the queue until the stack clears.

This model explains why long-running synchronous code freezes the browser — it never empties the call stack, so no event callbacks (including user click handlers) can run. And it sets the stage for Day 14 where you'll learn Promises and async/await — which are built on top of this exact mechanism.

---

**[50:00–54:00] ES6 Modules**

Modern JavaScript code is organized into modules — separate files that import and export functionality.

You export things from a file using the `export` keyword. There are two types of exports: **named exports** (you can have many per file) and **default exports** (exactly one per file).

Named export: `export function add(a, b) { return a + b; }`. Multiple named exports per file are fine.

Default export: `export default function greet(name) { return \`Hello, ${name}!\`; }`. Only one default per file.

Importing named exports: `import { add } from "./math.js"`. The name in `{}` must match the export. You can rename: `import { add as plus } from "./math.js"`.

Importing the default: `import greet from "./greet.js"`. No curly braces, and you can use any name you want — it doesn't have to match the export name.

To use modules in the browser, your script tag needs `type="module"`: `<script type="module" src="app.js"></script>`. This also automatically puts your code in strict mode.

Modules have their own scope — variables declared in one module don't leak into other modules. No more global variable collisions.

You'll use modules constantly in React and Angular — every React component is a module with a default export. When we get to Week 4, the import/export syntax will be second nature.

---

**[54:00–58:00] Debugging JavaScript**

Let me walk you through debugging tools — the skills that turn a developer from frustrated to effective.

**DevTools Sources panel.** Open DevTools, go to the Sources tab. You can navigate to any JavaScript file your page loads. Click a line number to set a breakpoint. When code execution reaches that line, it pauses. You can then inspect every variable in scope in the right panel, step forward line by line, step into function calls, or step out of the current function.

**The `debugger` statement.** Add `debugger;` directly in your code. When DevTools is open and execution hits that line, it pauses — just like a breakpoint. Remove it before committing to version control.

**Console techniques beyond `console.log`:**

`console.table(array)` displays an array of objects as a formatted table — incredibly readable for data.

`console.time("label")` and `console.timeEnd("label")` measure how long a block of code takes.

`console.group("name")` and `console.groupEnd()` group related log lines — collapsible in the console.

One debugging footgun: when you `console.log` an object, the console shows you a live reference to the object — if the object changes after the log, you see the changed version. To snapshot the object, log `JSON.parse(JSON.stringify(obj))` — the JSON round-trip creates a deep copy.

**Most common errors:**

`TypeError: Cannot read properties of undefined` — you're accessing a property on something that's null or undefined. Fix: add an if-check or use optional chaining `?.`.

`ReferenceError: x is not defined` — the variable name doesn't exist in scope. Check for typos and make sure you declared it with `const` or `let`.

When you see an error in the console, click the file/line reference in the error — DevTools will jump right to the problematic line.

---

**[58:00–60:00] Day 12 Complete — Setting Up for Day 13**

Let me close today with the big picture of what you've accomplished.

You started this morning knowing HTML and CSS. You're ending tonight knowing JavaScript — the full core language. Variables and scope, types and coercion, arrays and functional methods, functions three ways, `this` and its binding rules, closures and their practical patterns, hoisting, strict mode, objects and destructuring, error handling, the event loop model, modules, and debugging. That is an enormous amount in one day.

Let me give you the connections that tie everything together. JavaScript runs in the browser and is loaded via `<script defer>`. It uses `const`/`let` for block-scoped variables and `===` for comparison, always. Arrow functions solve the `this` binding problem in callbacks. Closures enable private state and are the foundation of React hooks. The event loop is why asynchronous code works without blocking the UI.

Tomorrow in Day 13, you apply all of this to the DOM — the live HTML document. You'll write `document.querySelector(".my-class")` — using CSS selectors from Day 11. You'll change element content, add and remove elements, and respond to clicks, form submissions, and keyboard input. Everything becomes interactive.

Day 14 is where async JavaScript clicks into place — Promises, async/await, and the Fetch API to call real web services.

Day 15 is TypeScript — adding static types to JavaScript, which you'll use throughout the rest of the course.

Great work today. If you have time tonight, play in the browser console — try some of the array methods, experiment with closures, break things intentionally. The best way to solidify this is to write code until errors feel like information, not obstacles.

See you tomorrow.

---

*[END OF PART 2 — 60 MINUTES]*
