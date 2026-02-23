# Week 3 - Day 12 (Tuesday): JavaScript Fundamentals
## Part 1 — Lecture Script (60 Minutes)

---

**[00:00–02:00] Welcome and the Big Picture**

Good morning everyone — welcome to Day 12! Yesterday you learned HTML and CSS, which are fantastic tools. But I want you to think about the websites you actually use every day — Gmail, Spotify, Instagram, Slack. Those aren't just styled HTML documents. When you click a button and something happens without the page reloading, when you see a notification counter go up in real time, when you drag and drop something — that is JavaScript. Today we start learning the language that makes all of that possible.

Here's a mental model I want you to hold onto all week: HTML is the skeleton of a web page. CSS is the skin and appearance. JavaScript is the muscles — the thing that actually makes it move and respond. And just like you learned the skeleton first, then added the skin, now we add the muscles.

I also want to give you the big picture of where JavaScript lives. It runs in every web browser — Chrome, Firefox, Safari, Edge — without installation. It also runs on the server side through Node.js, which is what enables JavaScript developers to write both frontend and backend code. You'll see Node.js in a later week. For now, we're focused on JavaScript in the browser.

---

**[02:00–06:00] How JavaScript Gets Into Your Pages**

Let's start with the mechanical question: how do you get JavaScript running on a web page?

You use the `<script>` tag inside your HTML. The simplest way is to write JavaScript directly inside a script tag in your HTML file. But just like CSS — where we prefer an external stylesheet — we prefer an external JavaScript file.

Let me show you the pattern you should always use. In your HTML, right before the closing `</body>` tag — or in the `<head>` with the `defer` attribute — you add: `<script src="app.js" defer></script>`. 

That `defer` attribute is important. Without it, the browser stops parsing your HTML the moment it hits the script tag, downloads and executes the JavaScript, and then continues with the rest of the HTML. If your script tries to select an HTML element that hasn't been parsed yet — boom, null reference error. With `defer`, the script downloads in the background while HTML parsing continues, and only executes after the entire HTML document has been parsed. So always use `defer`.

There's also an `async` attribute — that downloads in parallel too but executes immediately when downloaded, potentially interrupting HTML parsing. Use `async` only for scripts that are completely independent of everything else, like analytics.

Now — the browser console. This is your laboratory for the next several weeks. Open DevTools right now — F12 on Windows, Command-Option-J on Mac — and click the Console tab. Everything you type in here is live JavaScript. Let's start there. Type `console.log("Hello, JavaScript!")` and hit enter. You should see Hello, JavaScript! appear. This is your scratch pad. Use it constantly.

---

**[06:00–12:00] Variables — const, let, and var**

Alright, let's write some actual code. The first fundamental concept in any language is variables — named containers for storing values.

JavaScript has three keywords for declaring variables: `const`, `let`, and `var`. I'm going to tell you right now: `var` is the old way, and modern JavaScript doesn't use it. But you'll see it in legacy code, so you need to recognize it.

Let me walk you through the differences. `const` is for values that don't change — it's short for "constant." You declare it once, assign a value, and that's it. `let` is for values that need to change. `var` is the old function-scoped version that has confusing behavior we'll talk about.

Here's the rule I want you to default to: use `const` for everything. When you try to reassign and JavaScript throws an error, then switch to `let`. That constraint forces you to think carefully about mutability.

Let me type this in the console: `const name = "Alice"`. Now if I type `name = "Bob"`, I get a TypeError — Assignment to constant variable. Good, that's the constraint working.

Now `let count = 0`. I can do `count = 10` — that works. `count` is reassignable.

Here's where `const` gets subtle. If I do `const user = { name: "Alice" }` and then `user.name = "Bob"`, that works! Because `const` prevents reassigning the variable binding — meaning I can't say `user = { name: "Charlie" }`, that throws an error — but `const` doesn't prevent modifying the contents of the object the variable points to. The reference is constant, not the data inside the object. Keep that in mind.

Now, scope. This is where `var` shows its problems. Scope defines where a variable is accessible. `var` is function-scoped — it lives for the entire duration of the function it's declared in, ignoring block boundaries like `if` statements and `for` loops. That means if you declare `var count` inside an `if` block, it's accessible outside that block within the same function. That's surprising and causes bugs.

`let` and `const` are block-scoped — they live only within the nearest pair of curly braces `{}`. If you declare `let x` inside an `if` block, it doesn't exist outside that block. That's the behavior you expect. Use `let` and `const`, always.

---

**[12:00–18:00] Data Types — Primitives**

JavaScript has seven primitive data types. Let's go through them.

First, strings — text values. You can use single quotes, double quotes, or backticks. We'll talk about backticks in a few minutes — they're the best option for dynamic strings.

Second, numbers. Here's something different from Java: JavaScript has ONE number type. There's no separate integer and float. `42` and `3.14` are both just "number". This is convenient but can cause precision issues with very large integers — that's what `bigint` is for, but you'll rarely need it.

JavaScript has a special number value: `NaN` — Not a Number. It shows up when a numeric operation fails. `"hello" * 2` gives you `NaN`. And here's the quirk: `typeof NaN` is `"number"`. JavaScript knows it's a number-context result, just an invalid one.

Third, booleans — `true` and `false`. Familiar from every language.

Fourth and fifth: `null` and `undefined`. These are both "absence of value" but they mean different things. `undefined` means a variable was declared but never assigned — JavaScript put this here. `null` means a developer deliberately set this to empty. Use `null` when you intentionally want to say "this has no value." You'll get `undefined` when you forget to assign something.

Here's a famous JavaScript quirk: `typeof null` returns `"object"`. That's a bug from JavaScript's original 1995 implementation. It can't be fixed without breaking millions of websites, so it's been kept. Just remember: null is NOT an object. Use `=== null` to check for null specifically.

The `typeof` operator is how you check types at runtime. `typeof "hello"` returns `"string"`. `typeof 42` returns `"number"`. Very useful for debugging.

---

**[18:00–24:00] Type Coercion and Strict Equality**

JavaScript has a feature — or a quirk, depending on who you ask — called type coercion. The language will automatically convert types in certain situations. Let's look at some examples that will definitely surprise you.

In the console, type `"5" + 3`. What do you expect? You might expect 8. You get `"53"`. Why? Because the `+` operator is overloaded — it means addition for numbers AND concatenation for strings. When one operand is a string, JavaScript coerces the other to a string and concatenates.

Now type `"5" - 3`. You get 2. Subtraction isn't overloaded — it only means subtraction. So JavaScript coerces `"5"` to the number 5 and subtracts. This inconsistency — `+` vs `-` behaving differently — is one of JavaScript's rough edges.

Now here's the big one. JavaScript has two equality operators: double equals `==` and triple equals `===`.

Double equals performs type coercion before comparing. So `5 == "5"` returns `true`. `0 == false` returns `true`. `null == undefined` returns `true`. This seems convenient but causes deeply confusing bugs.

Triple equals — strict equality — compares type AND value without any coercion. `5 === "5"` is `false` — different types. `0 === false` is `false`. This is predictable, this is what you want.

The rule is simple and absolute: always use triple equals. Never use double equals. If you're doing a code review and you see `==`, that's a red flag.

For explicit type conversion — when you want to deliberately convert types — use `Number()`, `String()`, or `Boolean()`. `Number("42")` gives you `42`. `Number("abc")` gives you `NaN`. `parseInt("42px")` gives you `42` — it parses until it hits a non-numeric character. Very useful for form inputs, which always come in as strings.

---

**[24:00–28:00] Truthy and Falsy**

Closely related to coercion: truthy and falsy. In JavaScript, every value can be evaluated in a boolean context — not just true and false. Values that act like false are called "falsy." Everything else is "truthy."

There are exactly eight falsy values. Write these down: `false`, `0`, `-0`, `0n` (BigInt zero), the empty string `""`, `null`, `undefined`, and `NaN`. Those are the only eight. Everything else — including the string `"0"`, empty arrays `[]`, and empty objects `{}` — is truthy.

That last one trips people up. The string `"0"` is truthy because it's a non-empty string. The empty array `[]` is truthy because it's an object. When you're in a boolean context, JavaScript is just asking "is this a real value?" — and `[]` is a real array, even if it has nothing in it.

How do you use this? Conditional guards are the most common pattern. Instead of checking `if (username !== null && username !== undefined && username !== "")`, you can just write `if (username)` — because all three of those conditions make `username` falsy.

The OR operator `||` is used for default values. `const display = username || "Guest"` — if `username` is falsy (null, undefined, empty string), use "Guest".

But there's a newer, better operator for this specific use case: nullish coalescing `??`. The difference: `||` triggers on ANY falsy value including empty string. `??` only triggers when the value is `null` or `undefined`. If the user typed in an empty string and you want to keep that empty string, use `??` instead of `||`.

---

**[28:00–34:00] Control Flow — Conditionals and Loops**

JavaScript control flow is mostly similar to Java. Let me run through it quickly and highlight the differences.

`if`/`else if`/`else` works exactly as you'd expect. The condition is evaluated in a boolean context — so any truthy value passes.

`switch` is good for multiple discrete value comparisons. One important note: you need a `break` statement at the end of each case to prevent fallthrough — where JavaScript continues executing the next case's code. Sometimes intentional fallthrough is useful (two cases sharing the same handler), but always be explicit about it.

For loops: the classic `for (let i = 0; i < n; i++)` works exactly like Java. Notice I'm using `let` because `i` changes.

`while` loops work the same as Java. `do...while` executes at least once before checking the condition — useful for getting input.

Now the JavaScript-specific loops. `for...of` iterates over the VALUES of an iterable — arrays, strings, Sets, Maps. `for (const fruit of fruits)` gives you each fruit value. Notice `const` — we're not reassigning `fruit`, we're getting a new binding each iteration.

`for...in` iterates over the KEYS of an object. `for (const key in person)` gives you "name", "age", "city", etc. Do NOT use `for...in` on arrays — it can iterate over inherited enumerable properties in addition to array indices.

The rule: `for...of` for arrays and iterables, `for...in` for plain objects.

One more control flow feature I want to highlight: optional chaining with `?.`. Instead of writing `user && user.address && user.address.city` to safely access a nested property, you write `user?.address?.city`. If any part of the chain is null or undefined, the whole expression short-circuits to undefined instead of throwing a TypeError. This is ES2020 and it makes a huge difference in code clarity.

---

**[34:00–42:00] Arrays — Creation, Methods, and the Functional Trio**

Arrays in JavaScript are dynamic and can hold mixed types — unlike Java's typed, fixed-size arrays. Let me go through the essentials.

Creating an array: square bracket literal syntax is standard. `const fruits = ["apple", "banana", "cherry"]`. Zero-indexed — `fruits[0]` is "apple".

Adding and removing: `push` adds to the end, `pop` removes from the end. Both are efficient. `unshift` adds to the beginning, `shift` removes from the beginning — these are slower because all other elements have to be re-indexed.

Now I want to spend real time on three array methods that you will use every single day as a JavaScript developer: `map`, `filter`, and `reduce`.

`map` transforms every element and returns a new array of the same length. If I have `const numbers = [1, 2, 3, 4, 5]` and I want to double every number, I write `numbers.map(n => n * 2)` and I get `[2, 4, 6, 8, 10]`. The original array is untouched — `map` returns a new array.

`filter` keeps only the elements that match a condition and returns a new array that's shorter or the same length. `numbers.filter(n => n % 2 === 0)` gives me `[2, 4]`. Only the even numbers.

`reduce` accumulates all elements into a single value. It takes a callback with an accumulator and a current value, plus an initial value for the accumulator. `numbers.reduce((acc, curr) => acc + curr, 0)` sums all numbers to 15. The `0` is the starting value of `acc`.

These three — `map`, `filter`, `reduce` — are the functional programming foundation of JavaScript. When you get to React next week, you'll use `map` constantly to render lists of elements. When you fetch data from an API, you'll use `filter` to narrow down results. Get comfortable with these now.

Other useful array methods: `find` returns the first matching element (or `undefined`). `some` returns true if at least one element passes the test. `every` returns true if ALL elements pass. `includes` checks if a value exists.

`slice` extracts a portion without modifying the original. `splice` removes or inserts elements and MODIFIES the original array. This mutating vs non-mutating distinction matters — in React, you should never mutate arrays directly.

---

**[42:00–48:00] Template Literals**

Quick but important: template literals. These use backtick characters — that's the key in the top-left of your keyboard under Escape.

Old way of building dynamic strings: `"Hello, " + name + "! You are " + age + " years old."` — string concatenation with `+`. It works, but it's verbose and error-prone.

Template literals: `` `Hello, ${name}! You are ${age} years old.` `` — you embed any expression inside `${}`. The expression is evaluated and its value is inserted into the string. Cleaner, readable, less punctuation noise.

And inside those `${}` you can put any valid JavaScript expression — not just variables. You can do math: `${2 + 2}`. You can call methods: `${name.toUpperCase()}`. You can even use a ternary: `${age >= 18 ? "adult" : "minor"}`.

The other big advantage: multi-line strings. Old way required `\n` escape sequences or string concatenation across lines. Template literals just preserve your newlines. This is incredibly useful when you're building HTML strings dynamically or writing multi-line messages.

Use template literals as your default for any string that involves variables or spans multiple lines. Use regular quotes only for simple static strings.

---

**[48:00–54:00] Functions — Declarations, Expressions, Arrow Functions**

Let's talk about functions. You know functions from Java, but JavaScript has several ways to define them.

**Function declarations** — the classic syntax: `function greet(name) { return \`Hello, ${name}!\`; }`. These are fully hoisted — meaning JavaScript processes them before any code runs, so you can call `greet()` even before the function declaration appears in your file.

**Function expressions** — assigning a function to a variable: `const greet = function(name) { ... }`. These follow the rules of their variable — `const` means they can't be reassigned. They are NOT hoisted, so you can't call them before they're assigned.

**Arrow functions** — the modern syntax, and the one you'll see most in contemporary JavaScript and React. `const greet = (name) => \`Hello, ${name}!\`;`. Two things to notice: the fat arrow `=>`, and the lack of a `return` keyword. When an arrow function has a single expression in its body (no curly braces), it implicitly returns that expression. That's called the concise body.

If you need multiple statements, you use curly braces and an explicit `return`: `const greet = (name) => { const msg = \`Hello, ${name}!\`; return msg; }`.

If your function takes exactly one parameter, you can drop the parentheses: `const double = n => n * 2`. But if there are zero parameters or multiple parameters, you need the parentheses: `const sayHello = () => "Hello!"` and `const add = (a, b) => a + b`.

One gotcha: if you're returning an object literal in concise body syntax, you need to wrap it in parentheses to distinguish it from a block. `const makeUser = (name, age) => ({ name, age })` — the outer parentheses tell JavaScript this is an expression, not a block.

Default parameters are ES6 — `function greet(name = "World")` — if no argument is passed, `name` defaults to "World". 

---

**[54:00–58:00] Closures**

This is one of the most important concepts in JavaScript. Closures.

A closure is a function that retains access to the variables from its outer scope even after that outer function has finished executing.

Let me show you. I'll write a function called `makeCounter`. Inside it, I declare a variable `count = 0`. Then I return an inner function — that inner function increments `count` and returns it.

Now outside, I call `makeCounter()` and store the result in a variable: `const counter = makeCounter()`. The `makeCounter` function has returned and is done. But the `count` variable didn't disappear — the inner function that I returned holds a reference to it. That's the closure.

Now every time I call `counter()`, it increments and returns the count: 1, 2, 3. And nobody outside can access `count` directly — it's protected inside the closure. This is private state through closures.

Here's a practical example: I write a `createBankAccount` function that takes an initial balance. It returns an object with `deposit`, `withdraw`, and `getBalance` methods. The `balance` variable is private — you can only interact with it through those methods. You can't access `balance` directly from outside. This is encapsulation through closures — similar to private fields in Java.

Closures are why React hooks work, why module patterns work, why event handlers work correctly. When you write `useEffect(() => { ... })` in React next week, that callback is a closure. Understanding closures now will make Week 4 much less mysterious.

---

**[58:00–60:00] Part 1 Wrap-Up**

Let me quickly recap what we've covered in the last 60 minutes.

You now know what JavaScript is — the language of the web, running in every browser. You know how to load it with a `<script defer>` tag and how to use the console. You understand `const` vs `let` vs `var` — and that you should use `const` by default. You know the seven primitive types and the `typeof` operator. You know why `===` beats `==` every time. You've seen truthy and falsy values. You know control flow, loops, and the `for...of` vs `for...in` distinction. You can create and manipulate arrays with `map`, `filter`, and `reduce`. You're using template literals. You can write functions three ways — declaration, expression, arrow — and you understand the concise body. And you understand closures.

That is a lot. Take a breath. These fundamentals will be used every single day for the rest of the bootcamp.

Part 2 picks up with the `this` keyword — which is JavaScript's most complex feature — and we'll cover hoisting in depth, truthy/falsy patterns, strict mode, and error handling. See you in a few minutes.

---

*[END OF PART 1 — 60 MINUTES]*
