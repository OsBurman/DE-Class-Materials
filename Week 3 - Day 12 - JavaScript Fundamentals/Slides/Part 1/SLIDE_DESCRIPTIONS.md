# Week 3 - Day 12 (Tuesday): JavaScript Fundamentals
## Part 1 Slide Descriptions

---

### Slide 1: Title Slide
**Title:** JavaScript Fundamentals — Part 1: The Language Core

**Content:**
- Subtitle: Variables, Data Types, Arrays & the Building Blocks of JS
- Week 3 — Day 12
- Topics: Variables & Scope · Data Types & Coercion · Arrays · Template Literals

**Notes:**
Opening slide for the first JavaScript lecture of the bootcamp. Students have come through two days of frontend structure (HTML) and styling (CSS) and now encounter their first true programming language in the browser context. This slide sets the expectation: JavaScript is a fully featured programming language that runs in the browser (and on the server with Node.js), not just a scripting add-on. The tone should be energizing — students often find the jump to JavaScript exciting after the more static feel of HTML/CSS.

---

### Slide 2: Welcome to JavaScript — The Language of the Web
**Title:** What Is JavaScript and Why Does It Matter?

**Content:**
- JavaScript is the only programming language that runs natively in every web browser
- The three pillars of the web:
  - **HTML** — Structure (the skeleton)
  - **CSS** — Presentation (the skin)
  - **JavaScript** — Behavior (the muscles)
- JavaScript is also used server-side (Node.js), mobile (React Native), desktop (Electron), and embedded devices
- Originally created in 10 days by Brendan Eich at Netscape in 1995 — now the world's most widely used language
- ECMAScript (ES) is the official standard — we use ES6+ (modern JavaScript)
- JavaScript is:
  - **Interpreted** (or JIT-compiled) — no compile step needed in the browser
  - **Dynamically typed** — variables don't have fixed types
  - **Multi-paradigm** — supports procedural, OOP, and functional styles
  - **Single-threaded** with an event loop for async (covered in Day 14)

**Notes:**
Bridge from Day 11: "Yesterday you built the HTML skeleton and styled it with CSS. Today JavaScript brings it to life." Emphasize that unlike Java (compiled, statically typed, strongly typed), JavaScript was designed to be flexible and forgiving — which is both a strength and a source of bugs. The ECMAScript/ES6+ framing prepares students for the fact that they'll see ES5 syntax in older code and ES6+ in modern codebases. Note: full async/await, Promises, and Node.js server use are covered in Day 14.

---

### Slide 3: How JavaScript Runs in the Browser
**Title:** JavaScript in the Browser — Script Tags and the Console

**Content:**
- JavaScript is loaded via the `<script>` tag in HTML:
  ```html
  <!-- External file (preferred) -->
  <script src="app.js" defer></script>

  <!-- Inline (acceptable for small snippets) -->
  <script>
    console.log("Hello, JavaScript!");
  </script>
  ```
- `defer` attribute: script loads in parallel, executes after HTML parsing completes (recommended)
- `async` attribute: script loads in parallel, executes as soon as downloaded (use for independent scripts)
- Without `defer`/`async`: script blocks HTML parsing — causes slow page load (old approach: place `<script>` before `</body>`)
- **The Browser Console** — your best friend for learning:
  - Open: F12 (or Cmd+Option+J on Mac) → Console tab
  - `console.log()` — print values to console
  - `console.error()` — red error output
  - `console.warn()` — yellow warning output
  - `console.table()` — display arrays/objects as tables
  - `console.dir()` — inspect object structure
- JavaScript files use `.js` extension

**Notes:**
Instructors should have DevTools open throughout this lecture. The `defer` attribute should be standard practice — explain that the HTML must be fully parsed before JavaScript can manipulate the DOM (which connects back to Day 11's DOM slide). The browser console is the sandbox where students will experiment — encourage them to open it now and type along. Mention that the same JavaScript knowledge applies to Node.js (no `<script>` tag needed) but we focus on browser JS first.

---

### Slide 4: Variables — var, let, and const
**Title:** Declaring Variables: var, let, and const

**Content:**
- Variables are named containers for storing values
- Three keywords — with important differences:

| Keyword | Scope | Re-declarable | Re-assignable | Hoisted |
|---------|-------|---------------|---------------|---------|
| `var` | Function | ✅ Yes | ✅ Yes | ✅ Yes (undefined) |
| `let` | Block | ❌ No | ✅ Yes | ⚠️ (TDZ) |
| `const` | Block | ❌ No | ❌ No | ⚠️ (TDZ) |

```javascript
// var — function-scoped, avoid in modern JS
var name = "Alice";
var name = "Bob"; // Re-declaration allowed — confusing!

// let — block-scoped, use for values that change
let score = 0;
score = 10; // Re-assignment OK

// const — block-scoped, use for values that don't change
const MAX_SIZE = 100;
// MAX_SIZE = 200; // TypeError: Assignment to constant variable

// const with objects — the reference is constant, not the content
const user = { name: "Alice" };
user.name = "Bob"; // OK — modifying the object's property
// user = {}; // TypeError — can't reassign the variable
```

**Key rules:**
- Use `const` by default
- Use `let` when you need to reassign
- Avoid `var` — it has confusing function-scope and hoisting behavior

**Notes:**
This is one of the most impactful slides in the course — the const/let/var distinction trips up beginners constantly. The table format helps comparison. The `const` with objects point is subtle but important — emphasize that `const` prevents reassignment of the binding, not mutation of the value. The `var` keyword will appear in legacy code, so students need to recognize it, but they should not use it in new code. TDZ (Temporal Dead Zone) is mentioned in the table — explain briefly that `let` and `const` exist in TDZ from the start of their block until they're declared, causing a ReferenceError if accessed before declaration.

---

### Slide 5: Scope — Where Variables Live
**Title:** Variable Scope: Global, Function, and Block

**Content:**
- **Scope** defines where a variable is accessible
- **Global scope** — declared outside all functions/blocks, accessible everywhere:
  ```javascript
  const appName = "MyApp"; // Global
  function greet() {
    console.log(appName); // "MyApp" — accessible inside function
  }
  ```
- **Function scope** — `var` variables are accessible anywhere within their function:
  ```javascript
  function calculate() {
    var result = 42;
    if (true) {
      var result = 99; // Same variable! var ignores the if-block
    }
    console.log(result); // 99 — confusing!
  }
  ```
- **Block scope** — `let`/`const` are confined to the nearest `{}` block:
  ```javascript
  function calculate() {
    let result = 42;
    if (true) {
      let result = 99; // Different variable — block-scoped
      console.log(result); // 99
    }
    console.log(result); // 42 — original unchanged
  }
  ```
- **Lexical scope** — functions can access variables from their outer (enclosing) scope:
  ```javascript
  const greeting = "Hello";
  function outer() {
    const name = "Alice";
    function inner() {
      console.log(greeting + " " + name); // Accesses both outer scopes
    }
    inner();
  }
  ```

**Notes:**
Scope is foundational for understanding closures (later this lecture) and bugs. The `var` function-scope example is worth live-coding — students are often surprised that `var` inside an `if` block is accessible outside it. The lexical scope example is the first preview of closures — mention "we'll come back to this when we talk about closures." Scope bugs (especially with `var` in loops) are one of the most common sources of JavaScript errors.

---

### Slide 6: JavaScript Data Types — Primitives
**Title:** Primitive Data Types

**Content:**
- JavaScript has **8 data types** total — 7 primitives + 1 object type
- **Primitives** — immutable values, compared by value:

| Type | Example | Notes |
|------|---------|-------|
| `string` | `"hello"`, `'world'`, `` `template` `` | Text — can use single, double, or backtick quotes |
| `number` | `42`, `3.14`, `-7`, `Infinity`, `NaN` | JavaScript has ONE number type (no int/float distinction) |
| `bigint` | `9007199254740991n` | For integers larger than `Number.MAX_SAFE_INTEGER` |
| `boolean` | `true`, `false` | Logical values |
| `undefined` | `undefined` | Variable declared but not assigned |
| `null` | `null` | Intentional absence of value (a value you set) |
| `symbol` | `Symbol("id")` | Unique, immutable identifier (advanced — Day 14) |

```javascript
let message = "Hello";        // string
let count = 42;               // number
let price = 19.99;            // number
let isActive = true;          // boolean
let nothing = null;           // null (intentional empty)
let notDefined;               // undefined (no assignment)

// Checking types
console.log(typeof message);  // "string"
console.log(typeof count);    // "number"
console.log(typeof isActive); // "boolean"
console.log(typeof null);     // "object" — famous JS bug! null is NOT an object
console.log(typeof notDefined); // "undefined"
```

**Notes:**
Compare to Java: JavaScript has one number type (both integer and float), no `char`, no `byte`. `typeof null === "object"` is a well-known bug from JavaScript's original implementation — it cannot be fixed without breaking the web, so it was kept. The distinction between `undefined` (the language put this here because nothing was assigned) and `null` (a developer explicitly set this to empty) is important. `NaN` (Not a Number) is technically of type `number` — mention this quirk. `bigint` is rarely used in typical web dev but students may see it.

---

### Slide 7: Type Coercion and Type Conversion
**Title:** Type Coercion — JavaScript's Surprising Auto-Conversions

**Content:**
- **Type coercion** — JavaScript automatically converts types in certain operations
- **Implicit coercion** (automatic, can surprise you):
  ```javascript
  // String + number = string concatenation
  console.log("5" + 3);      // "53" — number coerced to string
  console.log("5" - 3);      // 2 — string coerced to number
  console.log("5" * "2");    // 10 — both coerced to numbers

  // Loose equality (==) coerces types before comparing
  console.log(5 == "5");     // true — coercion happens
  console.log(0 == false);   // true — 0 and false both falsy
  console.log(null == undefined); // true — special rule

  // Strict equality (===) — NO coercion, checks type AND value
  console.log(5 === "5");    // false — different types
  console.log(0 === false);  // false — different types
  ```
- **Explicit conversion** (intentional — always prefer this):
  ```javascript
  // To number
  Number("42")      // 42
  Number("abc")     // NaN
  parseInt("42px")  // 42 — stops at non-numeric character
  parseFloat("3.14abc") // 3.14

  // To string
  String(42)        // "42"
  (42).toString()   // "42"

  // To boolean
  Boolean(0)        // false
  Boolean("")       // false
  Boolean(null)     // false
  Boolean("hello")  // true
  Boolean(42)       // true
  ```
- **Golden rule:** Always use `===` (strict equality) — never `==`

**Notes:**
Type coercion is one of JavaScript's most notorious features — it enables confusing bugs and was the subject of the famous "WAT" talk by Gary Bernhardt (worth showing a clip). The `+` operator is overloaded for both addition and string concatenation, so it coerces to string when either operand is a string. All other arithmetic operators (`-`, `*`, `/`, `%`) coerce to number. The strict equality rule is non-negotiable — drill this into students. `parseInt` and `parseFloat` are useful when parsing user input from forms (which always comes as strings).

---

### Slide 8: Truthy and Falsy Values
**Title:** Truthy and Falsy — Boolean Context Conversions

**Content:**
- In JavaScript, every value has an inherent boolean quality
- **Falsy values** — the 8 values that evaluate to `false` in a boolean context:
  ```javascript
  false
  0
  -0
  0n          // BigInt zero
  ""          // Empty string
  null
  undefined
  NaN
  ```
- **Truthy values** — everything else, including:
  ```javascript
  true
  1, -1, 42   // Any non-zero number
  "hello"     // Non-empty string
  "0"         // The string "0" — truthy! (not the number 0)
  []          // Empty array — truthy!
  {}          // Empty object — truthy!
  function(){} // Functions
  ```
- **Practical applications:**
  ```javascript
  // Conditional guard
  const username = getUserInput(); // might be "" or null
  if (username) {
    console.log("Welcome, " + username);
  }

  // Default value with OR
  const displayName = username || "Guest";

  // Nullish coalescing — only null/undefined trigger default
  const displayName2 = username ?? "Guest"; // "" remains ""
  ```

**Notes:**
The truthy/falsy distinction is uniquely JavaScript — it doesn't exist in Java. The surprises are: `"0"` is truthy (non-empty string), `[]` is truthy (even empty array), `{}` is truthy. The OR (`||`) vs nullish coalescing (`??`) distinction is important — `||` uses falsy check so empty string triggers the default, while `??` only triggers for `null`/`undefined`. This is practical for form inputs where empty string has different meaning than null. Instructors should run each example in the console live.

---

### Slide 9: Operators
**Title:** JavaScript Operators

**Content:**
- **Arithmetic:** `+`, `-`, `*`, `/`, `%` (modulo), `**` (exponentiation)
  ```javascript
  console.log(10 % 3);  // 1 (remainder)
  console.log(2 ** 8);  // 256
  ```
- **Assignment:** `=`, `+=`, `-=`, `*=`, `/=`, `%=`, `**=`, `??=`, `||=`, `&&=`
  ```javascript
  let x = 10;
  x += 5;   // x = 15
  x **= 2;  // x = 225
  ```
- **Comparison:** `===`, `!==`, `>`, `<`, `>=`, `<=` (always use `===`/`!==`)
- **Logical:** `&&` (AND), `||` (OR), `!` (NOT), `??` (nullish coalescing)
  ```javascript
  const isAdult = age >= 18 && hasID === true;
  const role = user.role || "viewer";
  const name = user.name ?? "Anonymous"; // null/undefined only
  ```
- **Increment/Decrement:** `++`, `--` (prefix vs postfix)
  ```javascript
  let i = 5;
  console.log(i++); // 5 — returns THEN increments
  console.log(++i); // 7 — increments THEN returns
  ```
- **Ternary operator:** `condition ? valueIfTrue : valueIfFalse`
  ```javascript
  const label = score >= 60 ? "Pass" : "Fail";
  ```
- **Spread operator:** `...` (covered more in Day 14, previewed briefly)

**Notes:**
Students know most operators from Java. Highlight the differences: `===`/`!==` (strict), `**` (exponentiation, no `Math.pow` needed), `??` (nullish coalescing — new in ES2020), and the logical assignment operators (`??=`, `||=`, `&&=`). The prefix vs postfix increment distinction is a classic interview question. The ternary operator is very common in React JSX, so worth practicing now.

---

### Slide 10: Control Flow — Conditionals
**Title:** Control Flow: if/else, switch, and Ternary

**Content:**
- **if / else if / else:**
  ```javascript
  function getLetterGrade(score) {
    if (score >= 90) {
      return "A";
    } else if (score >= 80) {
      return "B";
    } else if (score >= 70) {
      return "C";
    } else if (score >= 60) {
      return "D";
    } else {
      return "F";
    }
  }
  ```
- **switch statement** — best for multiple discrete values:
  ```javascript
  const day = "Monday";
  switch (day) {
    case "Saturday":
    case "Sunday":
      console.log("Weekend!");
      break;
    case "Monday":
      console.log("Back to work...");
      break;
    default:
      console.log("Weekday");
  }
  ```
- **Ternary for simple conditions:**
  ```javascript
  const message = isLoggedIn ? "Welcome back!" : "Please log in";
  ```
- **Optional chaining `?.`** — safely access nested properties:
  ```javascript
  const city = user?.address?.city; // undefined if any part is null/undefined
  // Instead of: user && user.address && user.address.city
  ```

**Notes:**
Control flow is familiar from Java. JavaScript-specific highlights: switch uses strict comparison (`===`), `break` is required to prevent fallthrough (or use it intentionally for case grouping), and `default` is like Java's `default`. Optional chaining (`?.`) is ES2020 and is extremely useful — prevents the dreaded "Cannot read properties of undefined" error. It's worth showing the before/after: the long `&&` chain vs the clean `?.` syntax. Ternary is used heavily in React JSX.

---

### Slide 11: Control Flow — Loops
**Title:** Loops: for, while, for...of, and for...in

**Content:**
- **Classic for loop:**
  ```javascript
  for (let i = 0; i < 5; i++) {
    console.log(i); // 0, 1, 2, 3, 4
  }
  ```
- **while loop:**
  ```javascript
  let attempts = 0;
  while (attempts < 3) {
    console.log("Attempt:", attempts + 1);
    attempts++;
  }
  ```
- **do...while loop** — executes at least once:
  ```javascript
  let input;
  do {
    input = prompt("Enter a number:");
  } while (isNaN(input));
  ```
- **for...of** — iterate over iterable values (arrays, strings — preferred for arrays):
  ```javascript
  const fruits = ["apple", "banana", "cherry"];
  for (const fruit of fruits) {
    console.log(fruit); // apple, banana, cherry
  }
  ```
- **for...in** — iterate over object keys (use for objects, not arrays):
  ```javascript
  const person = { name: "Alice", age: 30, city: "NYC" };
  for (const key in person) {
    console.log(key + ": " + person[key]);
  }
  ```
- **Loop control:** `break` (exit loop), `continue` (skip to next iteration)

**Notes:**
The `for...of` vs `for...in` distinction is commonly confused. Key rule: `for...of` for arrays and iterables (gets values), `for...in` for objects (gets keys). Using `for...in` on arrays can cause issues (iterates inherited enumerable properties). `for...of` is the modern, safe choice for arrays. Array methods like `forEach`, `map`, `filter` (covered next slide) are preferred over manual loops in modern JavaScript. Note that `for...of` requires ES6.

---

### Slide 12: Arrays — Creation and Basic Methods
**Title:** Arrays — Ordered Collections of Values

**Content:**
- **Creating arrays:**
  ```javascript
  const empty = [];
  const numbers = [1, 2, 3, 4, 5];
  const mixed = [42, "hello", true, null, { id: 1 }]; // JS arrays can mix types
  const fromConstructor = new Array(3); // [empty × 3] — avoid this
  ```
- **Accessing and modifying:**
  ```javascript
  const fruits = ["apple", "banana", "cherry"];
  console.log(fruits[0]);    // "apple" (0-indexed)
  console.log(fruits.length); // 3
  fruits[1] = "mango";       // ["apple", "mango", "cherry"]
  fruits[10] = "grape";      // Creates sparse array — avoid
  ```
- **Adding and removing elements:**
  ```javascript
  fruits.push("kiwi");        // Add to END — returns new length
  fruits.pop();               // Remove from END — returns removed item
  fruits.unshift("lemon");    // Add to BEGINNING — returns new length
  fruits.shift();             // Remove from BEGINNING — returns removed item
  ```
- **Finding elements:**
  ```javascript
  fruits.indexOf("mango");    // 1 (index) or -1 if not found
  fruits.includes("cherry");  // true or false
  fruits.findIndex(item => item.startsWith("a")); // index of first match
  ```
- **Array information:**
  ```javascript
  Array.isArray(fruits);     // true — reliable type check
  ```

**Notes:**
JavaScript arrays are dynamic (no fixed size), zero-indexed, and can hold mixed types — contrast with Java's typed, fixed-size arrays. `push`/`pop` operate on the END (efficient), `unshift`/`shift` operate on the BEGINNING (slow for large arrays — must re-index all elements). `includes` is cleaner than `indexOf() !== -1` for existence checks. Sparse arrays (assigning to index beyond length) are a footgun — mention but discourage. `Array.isArray()` is the reliable way to check if something is an array (typeof returns "object" for arrays).

---

### Slide 13: Array Methods — Transforming and Filtering
**Title:** Powerful Array Methods: map, filter, reduce, and More

**Content:**
- **`map`** — transform every element, returns new array (same length):
  ```javascript
  const numbers = [1, 2, 3, 4, 5];
  const doubled = numbers.map(n => n * 2); // [2, 4, 6, 8, 10]
  const names = ["alice", "bob"];
  const capitalized = names.map(n => n.toUpperCase()); // ["ALICE", "BOB"]
  ```
- **`filter`** — keep elements matching condition, returns new array:
  ```javascript
  const evens = numbers.filter(n => n % 2 === 0); // [2, 4]
  const adults = users.filter(u => u.age >= 18);
  ```
- **`reduce`** — accumulate values to a single result:
  ```javascript
  const sum = numbers.reduce((acc, curr) => acc + curr, 0); // 15
  const max = numbers.reduce((acc, curr) => curr > acc ? curr : acc);
  ```
- **`find` and `findIndex`:**
  ```javascript
  const found = users.find(u => u.id === 3); // First match or undefined
  ```
- **`some` and `every`:**
  ```javascript
  numbers.some(n => n > 4);   // true — at least one
  numbers.every(n => n > 0);  // true — all must pass
  ```
- **`slice` and `splice`:**
  ```javascript
  numbers.slice(1, 3);       // [2, 3] — non-mutating copy
  numbers.splice(1, 2);      // removes 2 items at index 1 — MUTATES
  ```
- **`sort`, `reverse`, `join`, `flat`, `flatMap`:**
  ```javascript
  ["banana","apple"].sort();        // ["apple","banana"]
  [1,2,3].reverse();                // [3,2,1]
  ["a","b","c"].join("-");          // "a-b-c"
  [[1,2],[3,4]].flat();             // [1,2,3,4]
  ```

**Notes:**
`map`, `filter`, `reduce` are the functional programming trio — essential for modern JavaScript and React development. Emphasize that `map` and `filter` return NEW arrays (non-mutating) while `splice` and `sort` MUTATE the original. This is a critical distinction. `reduce` is powerful but can be confusing — spend extra time on the accumulator pattern. The `(acc, 0)` initial value for reduce is important to always include (avoids errors on empty arrays). These methods will be used constantly in React component rendering.

---

### Slide 14: Template Literals
**Title:** Template Literals — Modern String Formatting

**Content:**
- Template literals use backticks (`` ` ``) instead of quotes
- **String interpolation** — embed expressions directly:
  ```javascript
  const name = "Alice";
  const age = 30;

  // Old way (concatenation):
  const msg1 = "Hello, " + name + "! You are " + age + " years old.";

  // Modern way (template literal):
  const msg2 = `Hello, ${name}! You are ${age} years old.`;

  // Any expression inside ${}:
  const result = `${2 + 2} is four`;
  const status = `User is ${age >= 18 ? "adult" : "minor"}`;
  const upper = `${name.toUpperCase()} logged in`;
  ```
- **Multi-line strings** — template literals preserve newlines:
  ```javascript
  // Old way (ugly):
  const html1 = "<div>\n  <p>Hello</p>\n</div>";

  // Template literal:
  const html2 = `
    <div>
      <p>Hello, ${name}!</p>
    </div>
  `;
  ```
- **Tagged templates** (advanced — for reference):
  ```javascript
  // Used by libraries like styled-components in React
  const query = gql`
    query GetUser($id: ID!) {
      user(id: $id) { name }
    }
  `;
  ```

**Notes:**
Template literals are used everywhere in modern JavaScript — in React JSX, in API calls, in logging, in dynamic HTML generation. Students should default to template literals over string concatenation. The multi-line capability is especially useful for building HTML strings or SQL-like queries dynamically. Tagged templates are used by popular libraries (styled-components, GraphQL tag) — worth a mention but not deep dive now. Note: the backtick character is typically in the top-left of the keyboard (below Escape key).

---

### Slide 15: Functions — Declarations and Expressions
**Title:** Functions: Declarations, Expressions, and Arrow Functions

**Content:**
- **Function declaration** — hoisted, can be called before definition:
  ```javascript
  greet("Alice"); // Works! — hoisted

  function greet(name) {
    return `Hello, ${name}!`;
  }
  ```
- **Function expression** — assigned to variable, NOT hoisted:
  ```javascript
  // greet("Alice"); // ReferenceError — not hoisted

  const greet = function(name) {
    return `Hello, ${name}!`;
  };
  ```
- **Arrow function** — concise syntax, no own `this` binding:
  ```javascript
  // Full syntax:
  const greet = (name) => {
    return `Hello, ${name}!`;
  };

  // Single parameter — parentheses optional:
  const greet = name => `Hello, ${name}!`;

  // No parameters — parentheses required:
  const sayHello = () => "Hello!";

  // Returning object — wrap in parentheses:
  const makeUser = (name, age) => ({ name, age });
  ```
- **Default parameters:**
  ```javascript
  function greet(name = "World") {
    return `Hello, ${name}!`;
  }
  greet();       // "Hello, World!"
  greet("Alice") // "Hello, Alice!"
  ```

**Notes:**
Students know functions from Java but the syntax differences are significant. Key points: (1) Function declarations are hoisted (callable before they appear in code), expressions are not. (2) Arrow functions are the preferred syntax in modern JavaScript — they're shorter and don't create their own `this` binding (critical for `this` section later). (3) The concise body syntax (`=>` without braces) implicitly returns the expression. (4) Returning an object literal needs `()` wrapping to disambiguate from a block. Default parameters are ES6 — much cleaner than the old `name = name || "World"` pattern.

---

### Slide 16: Functions — Parameters, Rest, and Spread
**Title:** Functions: Rest Parameters, Spread, and Arguments

**Content:**
- **Rest parameters** — collect remaining arguments into an array:
  ```javascript
  function sum(...numbers) {
    return numbers.reduce((acc, n) => acc + n, 0);
  }
  sum(1, 2, 3);      // 6
  sum(1, 2, 3, 4, 5); // 15

  function logMessage(level, ...messages) {
    console.log(`[${level}]`, ...messages);
  }
  logMessage("INFO", "Server started", "Port 3000");
  ```
- **Spread operator** — expand an array/object into individual elements:
  ```javascript
  const arr1 = [1, 2, 3];
  const arr2 = [4, 5, 6];

  // Combine arrays:
  const combined = [...arr1, ...arr2]; // [1, 2, 3, 4, 5, 6]

  // Pass array as individual arguments:
  Math.max(...arr1); // 3 (instead of Math.max(1,2,3))

  // Copy array (shallow):
  const copy = [...arr1]; // [1, 2, 3] — new array

  // Spread object:
  const defaults = { theme: "light", lang: "en" };
  const userSettings = { ...defaults, theme: "dark" }; // override
  // { theme: "dark", lang: "en" }
  ```
- **The `arguments` object** (legacy — `var`/function declarations only):
  ```javascript
  function oldStyle() {
    console.log(arguments[0]); // Not available in arrow functions
  }
  ```

**Notes:**
Rest and spread use the same `...` syntax but in opposite contexts: rest COLLECTS (in function parameters), spread EXPANDS (in function calls and array/object literals). This distinction trips students up. Spread for copying arrays/objects creates shallow copies — nested objects are still referenced. The `arguments` object is legacy — students will see it in older code, but rest parameters are the modern replacement. Spread for objects is heavily used in React state management (creating new state objects without mutation).

---

### Slide 17: Closures and Lexical Scope
**Title:** Closures — Functions That Remember Their Scope

**Content:**
- A **closure** is a function that retains access to its outer (enclosing) scope even after that outer function has returned
- Every function in JavaScript forms a closure over its lexical environment
- **Basic closure example:**
  ```javascript
  function makeCounter() {
    let count = 0; // Private to makeCounter

    return function() { // The inner function closes over 'count'
      count++;
      return count;
    };
  }

  const counter = makeCounter();
  console.log(counter()); // 1
  console.log(counter()); // 2
  console.log(counter()); // 3
  // count is not accessible from outside — it's "enclosed"
  ```
- **Closure for data encapsulation:**
  ```javascript
  function createBankAccount(initialBalance) {
    let balance = initialBalance; // Private

    return {
      deposit: (amount) => { balance += amount; },
      withdraw: (amount) => { balance -= amount; },
      getBalance: () => balance
    };
  }

  const account = createBankAccount(1000);
  account.deposit(500);
  console.log(account.getBalance()); // 1500
  // balance is inaccessible directly — protected by closure
  ```
- **Common closure pitfall with `var` in loops:**
  ```javascript
  // Bug (var):
  for (var i = 0; i < 3; i++) {
    setTimeout(() => console.log(i), 100); // Prints: 3, 3, 3
  }
  // Fix (let — creates new binding per iteration):
  for (let i = 0; i < 3; i++) {
    setTimeout(() => console.log(i), 100); // Prints: 0, 1, 2
  }
  ```

**Notes:**
Closures are one of the most important and most asked-about concepts in JavaScript interviews. The key insight: a function carries its environment (the variables in scope when it was defined) with it. The counter example shows closures enabling private state. The bank account example shows the module-like encapsulation pattern. The loop bug is classic — it directly demonstrates why `var` is problematic (one shared function-scoped `i`) vs `let` (a new block-scoped binding per iteration). React hooks (useState, useEffect) use closures internally — this context will pay off in Week 4.

---

### Slide 18: Hoisting
**Title:** Hoisting — Variable and Function Declarations at Compile Time

**Content:**
- **Hoisting** — JavaScript moves (hoists) declarations to the top of their scope during the creation phase, before code executes
- **Function declarations** — fully hoisted (name AND body):
  ```javascript
  console.log(greet("Alice")); // "Hello, Alice!" — works before definition
  function greet(name) { return `Hello, ${name}!`; }
  ```
- **`var` declarations** — hoisted as `undefined`, assignment stays in place:
  ```javascript
  console.log(x); // undefined — not an error, but confusing
  var x = 5;
  console.log(x); // 5
  // JavaScript sees it as: var x; ... x = 5;
  ```
- **`let` and `const`** — hoisted but in the Temporal Dead Zone (TDZ):
  ```javascript
  console.log(y); // ReferenceError: Cannot access 'y' before initialization
  let y = 10;
  ```
- **Function expressions and arrow functions** — NOT hoisted (follow their variable's rules):
  ```javascript
  console.log(add(2, 3)); // TypeError: add is not a function (var) or ReferenceError (let/const)
  var add = (a, b) => a + b;
  ```

**Summary table:**

| Declaration | Hoisted? | Initial Value |
|-------------|----------|---------------|
| `function` | ✅ Fully | Complete function |
| `var` | ✅ | `undefined` |
| `let` / `const` | ✅ (TDZ) | ReferenceError if accessed |

**Notes:**
Hoisting explains behaviors that seem like magic or bugs. The key takeaway: prefer `const`/`let` and function declarations so hoisting is either safe (function declarations) or gives clear errors (TDZ). The `var` hoisting behavior is why undefined appears instead of a ReferenceError — it's been declared but not initialized. Students should understand hoisting to read older code, but shouldn't rely on it in new code. Common interview question: "What is hoisting?" Answer: declarations are processed before execution, but only function declarations are fully hoisted with their bodies.

---

### Slide 19: Error Handling — try/catch/finally
**Title:** Error Handling with try, catch, and finally

**Content:**
- **try/catch** — handle runtime errors gracefully:
  ```javascript
  try {
    const data = JSON.parse('{ invalid json }');
    console.log(data.name);
  } catch (error) {
    console.error("Parsing failed:", error.message);
    // error.name — "SyntaxError"
    // error.message — human-readable description
    // error.stack — full stack trace
  }
  ```
- **finally** — always executes, whether error occurred or not:
  ```javascript
  function fetchData() {
    let connection;
    try {
      connection = openConnection();
      return connection.query("SELECT * FROM users");
    } catch (error) {
      console.error("Query failed:", error.message);
      return null;
    } finally {
      connection?.close(); // Always clean up resources
    }
  }
  ```
- **Throwing custom errors:**
  ```javascript
  function divide(a, b) {
    if (b === 0) {
      throw new Error("Division by zero is not allowed");
    }
    return a / b;
  }

  // Built-in error types:
  throw new TypeError("Expected a string");
  throw new RangeError("Value out of allowed range");
  throw new ReferenceError("Variable not defined");
  ```
- Errors propagate up the call stack until caught
- Uncaught errors crash the script and appear in the browser console

**Notes:**
Error handling is familiar from Java. JavaScript differences: `catch` receives the error object (no type specification — catch all), `finally` is the same concept (resource cleanup). The `error.message` and `error.stack` properties are essential for debugging. `JSON.parse` is a great real-world try/catch example — it throws a SyntaxError on invalid JSON, which is a common scenario when working with APIs. The `throw` statement works with any value (strings, numbers, objects) but throwing Error instances is best practice because they include a stack trace. Async error handling (try/catch with async/await) is covered in Day 14.

---

### Slide 20: Strict Mode
**Title:** Strict Mode — Writing Safer JavaScript

**Content:**
- `"use strict"` enables strict mode — makes JavaScript more predictable and error-prone behaviors become errors
- **Enabling strict mode:**
  ```javascript
  // File-level:
  "use strict";
  // ... all code in this file is in strict mode

  // Function-level:
  function strictFunction() {
    "use strict";
    // ...
  }
  ```
- **What strict mode prevents:**
  ```javascript
  "use strict";

  // Using undeclared variables:
  x = 10; // ReferenceError (without strict: silently creates global variable)

  // Deleting variables/functions:
  let y = 5;
  delete y; // SyntaxError

  // Duplicate parameter names:
  function add(a, a) {} // SyntaxError

  // this inside regular functions:
  function whoAmI() { console.log(this); } // undefined (not global object)

  // Writing to non-writable properties:
  const obj = {};
  Object.defineProperty(obj, "x", { value: 42, writable: false });
  obj.x = 9; // TypeError
  ```
- **ES6 modules are automatically in strict mode** — all modern JS code with `import`/`export` is strict
- **Classes are automatically in strict mode**

**Notes:**
Strict mode was introduced in ES5 as an opt-in improvement. In practice, if students are using ES6 modules (import/export), classes, or any modern JavaScript framework (React, Angular), their code is already in strict mode. The most important behavior: undeclared variable assignment becomes an error instead of silently creating a global variable — this prevents a whole class of subtle bugs. `this` inside regular function calls being `undefined` instead of the global object is important for understanding `this` context (next slide). Instructors should mention that Node.js scripts and browsers without modules do NOT auto-enable strict mode — explicit `"use strict"` is needed.

---

### Slide 21: Part 1 Summary and Part 2 Preview
**Title:** Part 1 Recap — What We've Covered

**Content:**
**Part 1 Topics — JavaScript Fundamentals:**
- ✅ What JavaScript is and how it runs in the browser (script tags, defer, console)
- ✅ Variables — `const`, `let`, `var`; block vs function scope
- ✅ Primitive data types — string, number, boolean, null, undefined, bigint, symbol
- ✅ Type coercion (implicit) and type conversion (explicit); always use `===`
- ✅ Truthy and falsy values
- ✅ Operators — arithmetic, assignment, comparison, logical, ternary
- ✅ Control flow — if/else, switch, optional chaining (`?.`)
- ✅ Loops — for, while, for...of, for...in
- ✅ Arrays — creation, indexing, push/pop, map/filter/reduce
- ✅ Template literals — interpolation, multi-line strings
- ✅ Functions — declarations, expressions, arrow functions, default parameters
- ✅ Rest parameters and spread operator
- ✅ Closures and lexical scope
- ✅ Hoisting — function declarations fully hoisted; `let`/`const` TDZ
- ✅ Error handling — try/catch/finally, throw, Error types
- ✅ Strict mode

**Part 2 Preview:**
- 🔜 Functions deep-dive: `this` keyword and context
- 🔜 Closures revisited + practical patterns
- 🔜 Hoisting in detail
- 🔜 Truthy/falsy in depth
- 🔜 Control flow: advanced loops and error handling

**Notes:**
Use this slide to consolidate what students have learned and generate anticipation for Part 2. Ask the class: "What's one concept from Part 1 that you want to revisit in Part 2?" The `this` keyword is often the most anticipated/feared topic. Remind students that Day 13 covers DOM manipulation (where they'll use JavaScript to interact with HTML), and Day 14 covers async JavaScript — so everything in Parts 1 and 2 is the foundation for those practical applications.
