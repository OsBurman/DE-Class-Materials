# Day 12 — Part 1 Walkthrough Script
## JavaScript Fundamentals — Variables, Types, Arrays & Template Literals
**Estimated time:** ~90 minutes  
**File covered:** `Part-1/01-variables-types-arrays.js`

---

## Opening — Why JavaScript? (5 min)

`[ACTION]` Open a browser tab. Open DevTools (F12 or Cmd+Opt+J). Show the Console tab.

> "JavaScript is the only programming language that runs natively in every browser on the planet. HTML structures the page, CSS styles it — JavaScript makes it interactive. When you click a button and something happens without a page reload, that's JavaScript."

> "JavaScript also runs on servers via Node.js, which is what we'll use today to run our code outside the browser. Same language, different environment."

`[ACTION]` In the DevTools Console, type `2 + 2` and press Enter. Show students the `4` output.

> "This is a JavaScript REPL — Read, Evaluate, Print, Loop. It runs JS immediately. It's your best friend for experimenting."

`[ACTION]` Then type `document.body.style.background = "red"` — watch the page turn red. Undo with `document.body.style.background = ""`.

> "That just changed the visual appearance of this live webpage in real time. That's the power of JavaScript."

→ **TRANSITION:** "Let's open our code file and start from the top."

`[ACTION]` Open `01-variables-types-arrays.js`.

---

## SECTION 1 — JavaScript Basics & Syntax (8 min)

`[ACTION]` Scroll to Section 1.

> "Before we write complex code, let's look at the absolute basics of JavaScript's syntax."

### Comments

> "Two kinds of comments. Single-line with `//` — everything after the slashes is ignored. Multi-line between `/*` and `*/`. Use comments liberally while learning — your future self will thank you."

### `console.log`

> "`console.log()` is your print statement. Unlike Java's `System.out.println`, you won't type this every day in production — but during learning and debugging you'll type it constantly."

`[ASK]` "What do you think `console.log('Hello, JavaScript!')` outputs?"  
*Answer: Exactly what you'd expect — `Hello, JavaScript!`*

### `typeof`

`[ACTION]` Scroll to the `typeof` block.

> "JavaScript is a dynamically typed language — unlike Java, you don't declare the type of a variable. The type is determined at runtime. `typeof` lets you inspect what type a value is."

Walk through each `typeof` call:
- `typeof 42` → `"number"` — all numbers are the same type in JS, whether integer or decimal
- `typeof "hello"` → `"string"` — single or double quotes both work
- `typeof true` → `"boolean"`
- `typeof undefined` → `"undefined"` — a variable that hasn't been assigned
- `typeof null` → `"object"` 

⚠️ **WATCH OUT** — `typeof null === "object"` is a famous 25-year-old bug in JavaScript. `null` is NOT an object — it was mistyped in the original 1995 implementation and they can never fix it without breaking the entire web. The correct check for null is `value === null`.

- `typeof []` → `"object"` — arrays are objects in JavaScript. Use `Array.isArray()` to check for arrays.

---

## SECTION 2 — Variables: var, let, const (15 min)

`[ACTION]` Scroll to Section 2.

> "JavaScript has three ways to declare variables. Historically only `var` existed. ES6 in 2015 added `let` and `const`, and the community immediately adopted them. Today, `var` is legacy code."

### var — Function Scope

> "The biggest problem with `var` is that it's function-scoped — not block-scoped. Let me show you what that means."

`[ACTION]` Walk through `demonstrateVarScope()`.

> "Inside that function, we have an `if (true)` block. Normally you'd expect a variable declared inside `{ }` to be invisible outside the block — that's how Java works. But watch what `var` does — `blockVar` is accessible OUTSIDE the if-block. It only respects function boundaries, not `{ }` block boundaries."

⚠️ **WATCH OUT** — `var` also allows re-declaration with no error. Point to `var courseName = "Full-Stack"` then `var courseName = "Advanced Java"`. In Java this would be a compile error. In JavaScript with `var`, it silently overwrites. This makes bugs very hard to find.

### let — Block Scope

> "`let` fixes both of those problems. It's block-scoped — once you're outside the `{ }`, the variable doesn't exist."

`[ACTION]` Point to the `let blockScoped` inside the if-block. Comment out the `console.log` inside and uncomment the one outside.

> "If we try to access `blockScoped` outside the block — ReferenceError. The variable doesn't exist there. This is the behaviour we want."

### const — Block Scope, No Reassignment

> "`const` is `let` with one additional rule: you cannot reassign the variable after it's declared."

`[ACTION]` Point to `const MAX_STUDENTS = 30`. Uncomment the `MAX_STUDENTS = 31` line briefly.

> "TypeError. Can't reassign a const. But here's the subtlety — `const` prevents reassignment of the variable, not mutation of the value."

`[ACTION]` Point to the `instructor` object.

> "`instructor.level = "Lead"` works fine — we're mutating the object that `instructor` points to, not changing what `instructor` points to. But `instructor = { name: "Bob" }` would fail — that reassigns the variable."

`[ACTION]` Draw on board:

```
const instructor  →  { name: "Sarah", level: "Senior" }
                             ↑
                   instructor.level = "Lead" changes THIS
                   instructor = {} would break THIS ARROW
```

`[ASK]` "So what should your default variable declaration be?"  
*Answer: `const`. Use `let` when you need to reassign. Never use `var`.*

---

## SECTION 3 — Data Types (15 min)

`[ACTION]` Scroll to Section 3.

> "JavaScript has 8 data types. Seven are primitives — simple, immutable values. One is the Object type, which is everything else."

### Primitives

> "Primitives are stored by value. When you copy them, you get an independent copy."

Walk through each:
- **Number** — JS has ONE number type for both integers and decimals. No `int`, no `float`, no `double`.
- **BigInt** — for integers beyond `Number.MAX_SAFE_INTEGER` (about 9 quadrillion). Note the `n` suffix.
- **String** — single quotes, double quotes, or backticks (template literals — Section 6).
- **Boolean** — `true` / `false`. Note lowercase — `True` would be a ReferenceError.
- **undefined** — declared but not assigned. JavaScript puts this in automatically.
- **null** — intentional absence of a value. You explicitly set this.
- **Symbol** — unique identifier, rarely needed at beginner level.

`[ASK]` "What's the difference between `undefined` and `null`?"  
*Answer: `undefined` means a variable exists but has no value yet — JS assigns it automatically. `null` means you intentionally set the value to nothing. In practice: `undefined` is 'not yet set', `null` is 'explicitly empty'.*

### Objects (Reference Types)

`[ACTION]` Point to the `student` object.

> "Objects hold key/value pairs. Keys are strings (or Symbols). Values can be anything — numbers, strings, booleans, arrays, even other objects."

> "Two ways to access properties: dot notation `student.firstName` and bracket notation `student['lastName']`. Dot notation is cleaner. Bracket notation lets you use a variable as the key — `student[someVariable]`."

### Primitive vs Reference — The Copy Behaviour

`[ACTION]` Point to the copy-by-value block.

> "This is one of JavaScript's biggest gotchas. Watch carefully."

Walk through:
```js
let a = 10;
let b = a;    // b gets a COPY of 10
b = 20;
console.log(a); // 10 — a didn't change
```

> "That's primitives — independent copies."

Now the reference:
```js
let original = { score: 100 };
let copy = original;    // copy points to THE SAME OBJECT
copy.score = 999;
console.log(original.score); // 999 — original changed too!
```

`[ACTION]` Draw on board:
```
Primitives:
a → [10]        b → [20]   (separate boxes in memory)

Objects:
original ──┐
            ├─→ { score: 999 }
copy     ──┘   (both arrows point to same box)
```

> "When you assign an object to another variable, you're copying the pointer — the memory address — not the object itself. Both variables see the same object."

> "To make a true independent copy: use spread `{ ...original }` or `Object.assign({}, original)`. Note: these are SHALLOW copies — nested objects are still shared. Deep cloning needs `structuredClone()` or a library."

---

## SECTION 4 — Type Coercion & Conversion (12 min)

`[ACTION]` Scroll to Section 4.

> "Type coercion is JavaScript converting values between types automatically — often when you didn't ask it to. This is one of the most notorious parts of the language."

### Implicit Coercion

`[ACTION]` Walk through the coercion examples one by one. Ask the class to guess each output before revealing it.

`[ASK]` "What does `"3" + 4` produce?"  
*Answer: `"34"` — the `+` operator sees a string and decides to concatenate.*

`[ASK]` "What about `"3" - 1`?"  
*Answer: `2` — the `-` operator has no string meaning, so it converts `"3"` to a number.*

> "This is why `+` is dangerous for arithmetic when inputs might be strings. Always convert inputs to numbers explicitly before doing math."

`[ACTION]` Highlight the `==` vs `===` block.

> "Loose equality `==` triggers coercion before comparing. `0 == false` is `true` because `false` becomes `0`. `"" == false` is `true`. This makes bugs incredibly hard to track down."

> "Strict equality `===` compares type AND value without any coercion. `0 === false` is `false` — they're different types. Always use `===` in production code."

⚠️ **WATCH OUT** — This trips up every JavaScript beginner. Loose equality with `==` creates rules that seem random. `null == undefined` is `true` but `null == 0` is `false`. The spec has an entire decision tree for `==` comparisons. Just use `===` and skip the decision tree entirely.

### Explicit Conversion

> "When you WANT to convert types, do it explicitly and intentionally."

Walk through:
- `Number("42")` → 42, `Number("hello")` → `NaN`
- `parseInt("42px")` → 42 — useful for parsing CSS values
- `String(123)` → `"123"`
- `Boolean(0)` → `false`, `Boolean("")` → `false`, `Boolean([])` → `true`

⚠️ **WATCH OUT** — `Boolean([])` is `true`. An empty array is truthy. An empty string `""` is falsy. An empty object `{}` is truthy. These are counterintuitive but important to know.

`[ASK]` "What would `Boolean('false')` return?"  
*Answer: `true` — it's a non-empty string. The string `'false'` is truthy. Only the actual boolean `false` is falsy.*

---

## SECTION 5 — Arrays & Array Methods (25 min)

`[ACTION]` Scroll to Section 5.

> "Arrays are ordered, zero-indexed lists. Unlike Java, a JavaScript array can hold any mix of types — strings, numbers, objects, other arrays — in the same array. Usually you don't want that, but you can."

### Creating & Accessing

> "Zero-indexed — first element is `[0]`, last is `[length - 1]`. Same as Java."

### Mutating Methods (modify original)

Walk through:
- `push` / `pop` — add/remove from the end
- `unshift` / `shift` — add/remove from the start  
- `splice(index, deleteCount, ...items)` — insert/remove anywhere
- `sort` with a comparator — important: without a comparator, `sort()` converts to strings first, so `[10, 9, 2].sort()` gives `[10, 2, 9]`. Always pass `(a, b) => a - b` for numbers.
- `reverse` — reverses in place

⚠️ **WATCH OUT** — `.sort()` with no argument sorts alphabetically even for numbers. `[1, 10, 2].sort()` → `[1, 10, 2]` (treats as "1", "10", "2"). Always use the comparator `(a, b) => a - b` for ascending numeric sort.

### Non-Mutating Methods (return new array)

> "These don't change the original — they return a new array or value. Prefer these in most situations — it's safer and more predictable."

- `slice(start, end)` — end is EXCLUSIVE (not included)
- `concat` — joins arrays, returns new array
- `indexOf` / `includes` — finding elements

### Higher-Order Array Methods

> "These are where JavaScript arrays really shine. Each one accepts a **callback function** — a function you pass as an argument that runs on each element."

`[ACTION]` Draw on board:
```
examScores.map( callback )
                 ↑
         called once per element
         receives (value, index, array)
         return value becomes new element
```

Walk through each method with the exam scores array:

**`forEach`** — "Like a for-loop but cleaner. Use when you just want to DO something with each element — print it, send it somewhere. Doesn't return a value."

**`map`** — "Transforms each element. Returns a new array of the same length with each element replaced by the return value of your callback. Perfect for converting one array into another."

`[ASK]` "What would `[1, 2, 3].map(x => x * 2)` return?"  
*Answer: `[2, 4, 6]`*

**`filter`** — "Keeps only elements where the callback returns `true`. Returns a new array that may be shorter."

`[ASK]` "What would `[1, 2, 3, 4, 5].filter(x => x % 2 === 0)` return?"  
*Answer: `[2, 4]` — only the even numbers.*

**`find`** — "Returns the first element that passes the test. Returns `undefined` if nothing matches."

**`some` / `every`** — "Boolean answers about the whole array. `some` = 'does at least one element pass?'. `every` = 'do all elements pass?'"

**`reduce`** — "The Swiss Army knife of array methods. Reduces the entire array to a single value — could be a number, string, object, anything. Walk through the accumulator pattern carefully."

`[ACTION]` Draw on board:
```
[45, 72, 88, 91].reduce((acc, cur) => acc + cur, 0)

Step 1:  acc=0,   cur=45  → return 45
Step 2:  acc=45,  cur=72  → return 117
Step 3:  acc=117, cur=88  → return 205
Step 4:  acc=205, cur=91  → return 296
Final result: 296
```

> "The second argument to `reduce` — the `0` — is the initial value of the accumulator. Always provide it. Without it, the first element becomes the accumulator and you'll get surprising results with empty arrays."

**Spread with arrays:**
> "The spread operator `...` unpacks an array. You can use it to copy arrays or combine them without mutating either."

**Destructuring:**
> "Array destructuring lets you unpack elements into variables in one line. The `...rest` pattern captures everything after the named variables."

---

## SECTION 6 — Template Literals (8 min)

`[ACTION]` Scroll to Section 6.

> "Template literals use backticks `` ` `` instead of quotes. They have three superpowers over regular strings."

### Interpolation

> "Embed any JavaScript expression inside `${ }`. The expression is evaluated and converted to a string automatically."

`[ACTION]` Show the `welcome` and `scoreReport` examples.

> "The key word is expression — not just variables. You can put function calls, arithmetic, ternaries, anything that evaluates to a value."

### Multi-line Strings

> "Regular strings can't span multiple lines — you'd need `\n` escape characters. Template literals just wrap across lines naturally. Perfect for HTML templates, email bodies, SQL queries."

### Comparison

`[ACTION]` Show the `oldStyle` vs `newStyle` comparison side by side.

> "They produce identical output. But the template literal version is dramatically easier to read, especially as strings get longer. Default to template literals for any string with embedded values."

---

## Wrap-Up Q&A (5 min)

**Q1:** "What are the 6 falsy values in JavaScript?"  
*Answer: `false`, `0`, `""` (empty string), `null`, `undefined`, `NaN`*

**Q2:** "You have a `const scores = [90, 85, 72]`. You do `const copy = scores`. You then do `copy.push(100)`. What does `scores` look like?"  
*Answer: `[90, 85, 72, 100]` — both variables point to the same array.*

**Q3:** "`"5" == 5` and `"5" === 5` — what does each return?"  
*Answer: `true` and `false`. Loose equality coerces; strict equality does not.*

**Q4:** "What does `[1, 2, 3].map(x => x * x).filter(x => x > 4)` return?"  
*Answer: `[9]` — first maps to `[1, 4, 9]`, then filters to keep only values > 4.*

---

→ **TRANSITION TO EXERCISES:**
> "For your exercises you'll practice variable declarations, explore type coercion surprises in the console, and chain array methods to solve data transformation problems. Pay special attention to `map`, `filter`, and `reduce` — you'll use these every day as a JavaScript developer."
