# Week 3 - Day 12 (Tuesday): JavaScript Fundamentals
## Part 2 Slide Descriptions

---

### Slide 1: Title Slide
**Title:** JavaScript Fundamentals — Part 2: Functions, `this`, & Advanced Concepts

**Content:**
- Subtitle: Closures · `this` Keyword · Hoisting · Strict Mode · Error Handling
- Week 3 — Day 12
- Topics: Functions Deep Dive · `this` & Context · Closures · Hoisting · Strict Mode · Control Flow

**Notes:**
Opening slide for Part 2. Students have completed the foundational JavaScript concepts in Part 1. Part 2 dives into the more nuanced aspects of the language — the `this` keyword, hoisting, strict mode, and advanced function concepts. These are the topics most commonly tested in technical interviews and the ones that cause the most confusion in real code. Frame this part as: "Now we peel back the layers and understand why JavaScript behaves the way it does, not just how to use it."

---

### Slide 2: The `this` Keyword — What Is It?
**Title:** Understanding `this` — JavaScript's Dynamic Context

**Content:**
- `this` refers to the execution context — the object that a function "belongs to" when it runs
- Unlike Java where `this` always refers to the current class instance, **JavaScript's `this` is determined at call time**, not at definition time
- **The value of `this` depends on HOW a function is called, not WHERE it is defined**
- Five `this` binding rules (in order of precedence):
  1. **`new` binding** — `this` = newly created object
  2. **Explicit binding** — `call()`, `apply()`, `bind()` — `this` = specified object
  3. **Implicit binding** — method call on an object — `this` = the object before the dot
  4. **Default binding** — standalone function call — `this` = `undefined` (strict mode) or `window` (non-strict)
  5. **Arrow functions** — `this` = lexical (the `this` of the enclosing scope — inherited, not dynamic)

```javascript
// Default binding:
function showThis() {
  console.log(this); // undefined (strict) or window (non-strict)
}
showThis();

// Implicit binding — method on an object:
const user = {
  name: "Alice",
  greet() {
    console.log(`Hello, I'm ${this.name}`); // this = user
  }
};
user.greet(); // "Hello, I'm Alice"

// Losing this — common bug:
const greetFn = user.greet;
greetFn(); // "Hello, I'm undefined" — this is no longer user!
```

**Notes:**
The `this` keyword is JavaScript's most commonly misunderstood feature. The crucial insight is that `this` is dynamic in regular functions — it's set when the function is called, not when it's defined. The "losing `this`" example is extremely important — when you extract a method from an object and call it standalone, `this` is no longer bound to the object. This happens constantly with event handlers and callbacks. Instructors should live-code the examples and ask "what do you expect `this` to be here?" before revealing the answer.

---

### Slide 3: `this` in Different Contexts
**Title:** `this` in Methods, Callbacks, Event Handlers, and Arrow Functions

**Content:**
- **Method context** — implicit binding works correctly:
  ```javascript
  const counter = {
    count: 0,
    increment() {
      this.count++; // this = counter
      console.log(this.count);
    }
  };
  counter.increment(); // 1
  ```
- **Callback problem** — `this` is lost:
  ```javascript
  const timer = {
    message: "Time's up!",
    start() {
      setTimeout(function() {
        console.log(this.message); // undefined — this is window/undefined
      }, 1000);
    }
  };
  ```
- **Arrow function fix** — `this` is lexically inherited from enclosing scope:
  ```javascript
  const timer = {
    message: "Time's up!",
    start() {
      setTimeout(() => {
        console.log(this.message); // "Time's up!" — arrow inherits outer this
      }, 1000);
    }
  };
  ```
- **Why arrow functions don't have their own `this`:** They capture `this` from where they are written (lexical), making them ideal for callbacks and event handlers inside methods.
- **Class methods and `this`:**
  ```javascript
  class Button {
    constructor(label) {
      this.label = label;
      // Bind in constructor to fix event handler issue:
      this.handleClick = this.handleClick.bind(this);
    }
    handleClick() {
      console.log(`${this.label} was clicked`);
    }
  }
  ```

**Notes:**
The callback problem (losing `this` in setTimeout, event handlers, array methods) is the most practical `this` pitfall. The arrow function solution is the modern fix — arrow functions inherit `this` lexically from their enclosing scope. This is precisely why React class components traditionally used arrow function class fields for event handlers. The `.bind(this)` pattern in the constructor is also valid and commonly seen. Arrow functions are NOT suitable as object methods when you need `this` to refer to the object — they'd inherit from the outer scope instead.

---

### Slide 4: Explicit `this` Binding — call, apply, and bind
**Title:** Controlling `this` with call(), apply(), and bind()

**Content:**
- **`call()`** — call a function with an explicit `this` and individual arguments:
  ```javascript
  function introduce(greeting, punctuation) {
    console.log(`${greeting}, I'm ${this.name}${punctuation}`);
  }

  const alice = { name: "Alice" };
  const bob = { name: "Bob" };

  introduce.call(alice, "Hello", "!");  // "Hello, I'm Alice!"
  introduce.call(bob, "Hi", ".");       // "Hi, I'm Bob."
  ```
- **`apply()`** — same as `call` but arguments passed as an array:
  ```javascript
  introduce.apply(alice, ["Hello", "!"]); // "Hello, I'm Alice!"

  // Useful with functions that take multiple args:
  const numbers = [5, 2, 8, 1, 9];
  Math.max.apply(null, numbers); // 9 (spread operator is cleaner: Math.max(...numbers))
  ```
- **`bind()`** — returns a NEW function with `this` permanently bound (doesn't call immediately):
  ```javascript
  const greetAlice = introduce.bind(alice, "Hello");
  greetAlice("!");  // "Hello, I'm Alice!" — this is permanently alice
  greetAlice("?");  // "Hello, I'm Alice?" — can still pass remaining args

  // Common use — event handler binding:
  button.addEventListener("click", this.handleClick.bind(this));
  ```
- **Memory aid:** `call` = comma-separated, `apply` = array, `bind` = borrowing (returns function)

**Notes:**
`call` and `apply` are most useful when borrowing methods from one object and using them with another. `Math.max.apply(null, array)` was the pre-spread way to find the max of an array. `bind` is the most commonly used of the three in modern code — especially for event handler binding in React class components and when passing a method as a callback. Arrow functions as class fields (`handleClick = () => {...}`) are an alternative to `.bind(this)` in constructors. Mnemonics help: "call comma, apply array."

---

### Slide 5: Functions as First-Class Citizens
**Title:** Functions Are First-Class Values

**Content:**
- In JavaScript, functions are **first-class citizens** — they can be:
  - Stored in variables: `const add = (a, b) => a + b;`
  - Passed as arguments: `[1,2,3].map(n => n * 2);`
  - Returned from other functions: `function multiplier(x) { return n => n * x; }`
  - Stored in arrays and objects: `const handlers = { click: () => {}, submit: () => {} };`
- **Higher-order functions** — functions that take or return other functions:
  ```javascript
  // Takes a function as argument:
  function repeat(n, action) {
    for (let i = 0; i < n; i++) {
      action(i);
    }
  }
  repeat(3, console.log); // 0, 1, 2

  // Returns a function:
  function multiplier(factor) {
    return number => number * factor;
  }
  const double = multiplier(2);
  const triple = multiplier(3);
  console.log(double(5));  // 10
  console.log(triple(5));  // 15
  ```
- **Callbacks** — functions passed as arguments, called when something happens:
  ```javascript
  function fetchData(url, onSuccess, onError) {
    // ...simulate async operation
    if (success) onSuccess(data);
    else onError(error);
  }
  fetchData("/api/users",
    data => console.log("Got data:", data),
    err => console.error("Failed:", err)
  );
  ```

**Notes:**
First-class functions are what enable functional programming patterns, callbacks, and React's component model. The `multiplier` example (returning a function from a function) is a direct closure pattern — each returned function closes over its `factor` value. Callbacks are the foundation of event handling and asynchronous programming. Note that callback-heavy code can lead to "callback hell" (deeply nested callbacks) — which is exactly what Promises and async/await (Day 14) solve. Students will use higher-order functions constantly: `map`, `filter`, `reduce`, `setTimeout`, `addEventListener` all take functions as arguments.

---

### Slide 6: `this` in Classes
**Title:** `this` in JavaScript Classes

**Content:**
- JavaScript `class` syntax (ES6) provides a cleaner way to create objects with shared methods
- `this` inside class methods refers to the instance — familiar from Java:
  ```javascript
  class Person {
    constructor(name, age) {
      this.name = name; // this = new instance
      this.age = age;
    }

    greet() {
      return `Hi, I'm ${this.name} and I'm ${this.age} years old.`;
    }

    isAdult() {
      return this.age >= 18;
    }
  }

  const alice = new Person("Alice", 30);
  console.log(alice.greet());   // "Hi, I'm Alice and I'm 30 years old."
  console.log(alice.isAdult()); // true
  ```
- **Event handler problem in classes:**
  ```javascript
  class Counter {
    constructor() {
      this.count = 0;
      // Solution 1: bind in constructor
      this.increment = this.increment.bind(this);
      // Solution 2: use arrow function class field (preferred)
    }

    // Solution 2: Arrow function class field — this is always the instance
    increment = () => {
      this.count++;
      console.log(this.count);
    }
  }
  const c = new Counter();
  document.querySelector("button").addEventListener("click", c.increment); // Works!
  ```
- Class syntax is covered fully in Day 14 (OOP in JS and ES6+)

**Notes:**
This slide bridges the `this` coverage to classes, giving students a preview of OOP in JavaScript. The key message: class methods behave as expected when called on an instance directly, but lose `this` when passed as callbacks (same problem as object methods). The arrow function class field syntax (`increment = () => { ... }`) is the modern solution — each instance gets its own arrow function that closes over the instance's `this`. This is the pattern React class components use for event handlers. Full class/OOP coverage is Day 14 — today is just enough to understand `this` in a class context.

---

### Slide 7: Hoisting — Deep Dive
**Title:** Hoisting in Depth — What the JavaScript Engine Does First

**Content:**
- JavaScript engines have two phases: **creation phase** (hoisting) and **execution phase**
- During the creation phase, the engine scans the code and:
  1. Creates the scope chain
  2. Registers all `var` declarations (initialized to `undefined`)
  3. Registers all function declarations (with their complete function body)
  4. Registers `let`/`const` in the scope but marks them as uninitialized (TDZ)

```javascript
// What you write:
console.log(a); // undefined
var a = 5;
console.log(a); // 5

greet("Alice"); // "Hello, Alice!" — function is fully hoisted
function greet(name) { return `Hello, ${name}!`; }

// What the engine sees (conceptually):
var a;              // hoisted, value = undefined
function greet(name) { return `Hello, ${name}!`; } // hoisted completely

console.log(a);     // undefined
a = 5;
console.log(a);     // 5
greet("Alice");     // works
```

- **Temporal Dead Zone (TDZ)** — `let`/`const` are hoisted but not accessible:
  ```javascript
  console.log(x); // ReferenceError: Cannot access 'x' before initialization
  let x = 10;
  // x exists in the scope (hoisted) but is in TDZ until the declaration line
  ```

- **Function expressions and arrow functions follow their variable's hoisting rules:**
  ```javascript
  add(2, 3); // TypeError: add is not a function  (var — hoisted as undefined)
  var add = (a, b) => a + b;

  multiply(2, 3); // ReferenceError (const — in TDZ)
  const multiply = (a, b) => a * b;
  ```

**Notes:**
The two-phase explanation (creation → execution) is the conceptual model that makes hoisting make sense. Many students have memorized "hoisting moves declarations to the top" without understanding why. The TDZ error message "Cannot access before initialization" is distinct from "is not defined" — TDZ means the variable exists in scope but hasn't been initialized. The key practical takeaway: prefer `const`/`let` (which give you ReferenceErrors for early access — the safe failure) over `var` (which silently gives you `undefined` — the confusing failure). Use function declarations for functions you want available throughout a module.

---

### Slide 8: Advanced Closures — Patterns and Pitfalls
**Title:** Closure Patterns: Memoization, Module Pattern, and Loop Traps

**Content:**
- **Memoization** — caching expensive computation results using closure:
  ```javascript
  function memoize(fn) {
    const cache = {}; // Enclosed — persists across calls
    return function(...args) {
      const key = JSON.stringify(args);
      if (key in cache) {
        console.log("Cache hit!");
        return cache[key];
      }
      cache[key] = fn(...args);
      return cache[key];
    };
  }

  const expensiveCalc = memoize((n) => {
    // Imagine this takes a long time
    return n * n;
  });
  expensiveCalc(5); // Computes: 25
  expensiveCalc(5); // Cache hit! Returns: 25 instantly
  ```
- **Module pattern** — encapsulate private state (pre-ES6 modules):
  ```javascript
  const cartModule = (() => {
    let items = []; // Private
    let total = 0;  // Private

    return {
      addItem: (item) => { items.push(item); total += item.price; },
      getTotal: () => total,
      getItems: () => [...items] // Return copy, not reference
    };
  })(); // IIFE — Immediately Invoked Function Expression

  cartModule.addItem({ name: "Book", price: 20 });
  console.log(cartModule.getTotal()); // 20
  console.log(cartModule.items); // undefined — private!
  ```
- **Loop closure trap** (revisited with solution patterns):
  ```javascript
  // Trap with var:
  const timers = [];
  for (var i = 0; i < 3; i++) {
    timers.push(() => console.log(i));
  }
  timers.forEach(fn => fn()); // 3, 3, 3 — all share the same i

  // Fix 1 — use let (creates a new binding per iteration):
  for (let i = 0; i < 3; i++) {
    timers.push(() => console.log(i));
  }
  timers.forEach(fn => fn()); // 0, 1, 2 ✅
  ```

**Notes:**
Memoization is a real-world closure pattern used in performance optimization — React's `useMemo` hook uses this concept. The module pattern (IIFE) was THE way to create private state before ES6 classes and modules — students will encounter it in legacy code. The IIFE (Immediately Invoked Function Expression) pattern `(() => { ... })()` is worth explaining: the outer parentheses make the function an expression (not a declaration), and the `()` at the end immediately calls it. The loop trap is revisited because it's a classic JavaScript interview question — the `let` fix works because `let` creates a new binding for each iteration of the loop, while `var` creates one shared binding.

---

### Slide 9: Truthy/Falsy Patterns — Practical Applications
**Title:** Truthy/Falsy in Practice — Patterns and Gotchas

**Content:**
- **Short-circuit evaluation** — operators stop evaluating as soon as result is determined:
  ```javascript
  // AND (&&): returns first falsy value, or last value if all truthy
  const result1 = "hello" && 42;     // 42 (both truthy, returns last)
  const result2 = "" && 42;          // "" (first is falsy, stops)
  const result3 = null && someFunc(); // null — someFunc never called!

  // OR (||): returns first truthy value, or last value if all falsy
  const name = userInput || "Anonymous";   // First truthy or "Anonymous"
  const port = config.port || 3000;        // Port or default 3000

  // Nullish coalescing (??): returns first non-null/undefined value
  const count = userData.count ?? 0;       // 0 only if count is null/undefined
  // If count is 0 (valid), || would replace it; ?? keeps it
  ```
- **Guard clauses** — early returns using falsy checks:
  ```javascript
  function processUser(user) {
    if (!user) return; // Guard clause — stops if user is null/undefined
    if (!user.name) return console.error("User needs a name");
    if (!user.isActive) return console.log("User is inactive");
    // Happy path — only reached if all checks pass
    console.log(`Processing ${user.name}...`);
  }
  ```
- **Conditional rendering pattern** (used in React):
  ```javascript
  // AND trick — only renders if condition is truthy:
  const element = isLoggedIn && <UserDashboard />;

  // Ternary — render one of two options:
  const button = isLoggedIn
    ? <LogoutButton />
    : <LoginButton />;
  ```
- **Double NOT (`!!`)** — convert any value to its boolean equivalent:
  ```javascript
  !!null       // false
  !!"hello"    // true
  !!0          // false
  !!"0"        // true — string "0" is truthy!
  ```

**Notes:**
Short-circuit evaluation is used constantly for conditional execution and defaults. The `&&` pattern for conditional execution is used heavily in React JSX (render component only if condition is truthy). The `||` vs `??` distinction is practically important — if `0`, `false`, or `""` are valid values (not just absent ones), use `??`. Guard clauses are a code quality pattern — they reduce nesting and make the "happy path" clear. The `!!` double negation is a way to explicitly cast to boolean — useful when you need to store a boolean derived from a truthy/falsy value.

---

### Slide 10: Control Flow — Advanced Patterns
**Title:** Advanced Control Flow: Optional Chaining, Nullish, and Pattern Matching

**Content:**
- **Optional chaining `?.`** — safe property/method access:
  ```javascript
  const user = null;
  user?.profile?.avatar;    // undefined — no error
  user?.getName();           // undefined — no error
  user?.["dynamic-key"];     // undefined — works with bracket notation too

  // With arrays:
  const first = arr?.[0];    // undefined if arr is null/undefined

  // Real-world: API response may have optional nested data
  const city = response?.data?.user?.address?.city ?? "Unknown";
  ```
- **Nullish coalescing assignment `??=`:**
  ```javascript
  let config = {};
  config.timeout ??= 5000; // Only sets if null/undefined
  config.timeout ??= 3000; // Already set — not overwritten
  console.log(config.timeout); // 5000
  ```
- **Logical OR assignment `||=` and AND assignment `&&=`:**
  ```javascript
  let name = "";
  name ||= "Anonymous"; // Sets because "" is falsy → "Anonymous"

  let isReady = true;
  isReady &&= checkStatus(); // Only evaluates if currently truthy
  ```
- **Destructuring with defaults:**
  ```javascript
  const { name = "Guest", role = "user", age } = userObj;
  const [first = 0, second = 0] = arr;
  ```
- **Pattern matching style with early returns (guard clauses):**
  ```javascript
  function getDiscount(user) {
    if (!user) return 0;
    if (user.isPremium) return 0.20;
    if (user.isMember) return 0.10;
    return 0.05;
  }
  ```

**Notes:**
Optional chaining (`?.`) and nullish coalescing (`??`) work beautifully together for safely navigating nested API responses. The chaining example with `response?.data?.user?.address?.city ?? "Unknown"` shows a real-world pattern students will write constantly when consuming REST APIs. The assignment operators (`??=`, `||=`, `&&=`) are ES2021 — newer but increasingly common. Destructuring with defaults is a clean way to handle missing properties. Guard clause pattern (multiple early returns) is preferred over deeply nested if-else structures — it's more readable and easier to test.

---

### Slide 11: Strict Mode — Details and Implications
**Title:** Strict Mode — Why It Exists and What It Changes

**Content:**
- **History:** Introduced in ES5 to fix problematic JavaScript behaviors without breaking existing code
- **Key behaviors changed by strict mode:**

```javascript
"use strict";

// 1. No implicit globals (most important):
undeclaredVar = 10; // ReferenceError — without strict, creates global silently

// 2. this is undefined in standalone functions (not global):
function fn() { console.log(this); } // undefined (not window)
fn();

// 3. Duplicate parameters are an error:
function bad(a, a) {} // SyntaxError

// 4. Writing to read-only properties throws:
const obj = Object.freeze({ x: 1 });
obj.x = 2; // TypeError (without strict, silently fails)

// 5. Deleting variables/functions is an error:
let x = 5;
delete x; // SyntaxError

// 6. Octal literals are an error:
const n = 012; // SyntaxError (octal — confusing legacy syntax)

// 7. with statement is banned:
with (obj) {} // SyntaxError (was a security/performance footgun)
```

- **Modern contexts that are automatically strict:**
  - ES6 modules (`import`/`export`)
  - Classes (`class` syntax)
  - Content run through bundlers like Webpack/Vite
- **When to explicitly add `"use strict"`:**
  - Plain `.js` files NOT using ES6 modules
  - Node.js scripts (without `"type": "module"` in package.json)

**Notes:**
The most impactful strict mode behavior is preventing implicit globals — the silent bug where a typo in a variable name creates a global variable instead of throwing an error. This can cause subtle, hard-to-find bugs (especially in loops or counters). The `this = undefined` behavior is why event handlers in classes lose their `this` without binding — in non-strict mode, `this` would be the global `window` object (also problematic). Students using React, Angular, or any modern framework are already in strict mode automatically via ES6 modules — they benefit from these protections without needing to add the directive. Emphasize: strict mode is not optional in professional code.

---

### Slide 12: Objects — Creation and Manipulation
**Title:** JavaScript Objects — Key-Value Stores and Prototypal Foundation

**Content:**
- Objects are collections of key-value pairs (properties and methods)
- **Creating objects:**
  ```javascript
  // Object literal (most common):
  const person = {
    name: "Alice",
    age: 30,
    greet() { // Method shorthand (ES6)
      return `Hi, I'm ${this.name}`;
    }
  };

  // Computed property names:
  const key = "name";
  const obj = { [key]: "Alice" }; // { name: "Alice" }
  ```
- **Accessing properties:**
  ```javascript
  person.name;        // Dot notation — preferred
  person["name"];     // Bracket notation — for dynamic keys
  person["first-name"]; // Required for keys with special characters
  ```
- **Destructuring** — extract multiple properties at once:
  ```javascript
  const { name, age, role = "user" } = person;
  // Rename during destructure:
  const { name: fullName } = person; // fullName = "Alice"
  ```
- **Object spread and Object.assign:**
  ```javascript
  const updated = { ...person, age: 31 }; // New object — non-mutating
  const merged = { ...defaults, ...overrides }; // Merge objects
  ```
- **Useful Object methods:**
  ```javascript
  Object.keys(person);    // ["name", "age", "greet"]
  Object.values(person);  // ["Alice", 30, function]
  Object.entries(person); // [["name","Alice"], ["age",30], ...]
  Object.freeze(person);  // Makes object immutable (shallow)
  ```

**Notes:**
Objects are the fundamental data structure in JavaScript — everything that isn't a primitive is an object (including arrays and functions). The shorthand method syntax (`greet() {}` instead of `greet: function() {}`) is standard in modern JavaScript. Destructuring with renaming and defaults is used constantly in function parameters and when consuming API data. Object spread is the non-mutating way to update objects — essential in React state management where you never mutate state directly. `Object.keys/values/entries` enable programmatic iteration over object properties. Full prototype chain and class-based OOP are covered in Day 14.

---

### Slide 13: Destructuring — Arrays and Objects
**Title:** Destructuring Assignment — Unpacking Values Elegantly

**Content:**
- **Array destructuring** — extract values by position:
  ```javascript
  const colors = ["red", "green", "blue"];
  const [first, second, third] = colors;
  console.log(first); // "red"

  // Skip elements:
  const [, , last] = colors; // "blue"

  // With rest:
  const [head, ...tail] = colors; // head="red", tail=["green","blue"]

  // Swap variables (elegant!):
  let a = 1, b = 2;
  [a, b] = [b, a]; // a=2, b=1 — no temp variable needed

  // Default values:
  const [x = 0, y = 0, z = 0] = [10, 20]; // z defaults to 0
  ```
- **Object destructuring** — extract values by name:
  ```javascript
  const user = { name: "Alice", age: 30, city: "NYC" };
  const { name, city } = user;

  // Rename + default:
  const { name: userName = "Anonymous", role = "user" } = user;

  // Nested destructuring:
  const { address: { street, zip } } = user;

  // Function parameter destructuring (very common in React):
  function UserCard({ name, age, role = "user" }) {
    return `${name} (${age}) — ${role}`;
  }
  ```
- **Mixed nesting:**
  ```javascript
  const data = {
    users: [
      { id: 1, name: "Alice" },
      { id: 2, name: "Bob" }
    ]
  };
  const { users: [firstUser] } = data;
  console.log(firstUser.name); // "Alice"
  ```

**Notes:**
Destructuring is used everywhere in modern JavaScript. The swap trick (`[a, b] = [b, a]`) is elegant and worth showing — it's a classic interview trick too. Function parameter destructuring is the standard React pattern for props: `function Button({ label, onClick, disabled = false })`. Nested destructuring can get complex — warn students that if nesting goes more than two levels, it may be clearer to use normal property access. Mixed array/object destructuring is useful for API responses (often objects with arrays of items). The renaming syntax `{ name: userName }` is the least intuitive part — "I want `name` but call it `userName` locally."

---

### Slide 14: Error Handling — Advanced Patterns
**Title:** Advanced Error Handling — Custom Errors and Error Patterns

**Content:**
- **Custom error classes:**
  ```javascript
  class ValidationError extends Error {
    constructor(message, field) {
      super(message); // Call Error constructor
      this.name = "ValidationError"; // Override default "Error"
      this.field = field; // Custom property
    }
  }

  class NetworkError extends Error {
    constructor(message, statusCode) {
      super(message);
      this.name = "NetworkError";
      this.statusCode = statusCode;
    }
  }

  function validateAge(age) {
    if (typeof age !== "number") throw new ValidationError("Age must be a number", "age");
    if (age < 0 || age > 150) throw new ValidationError("Age out of range", "age");
    return age;
  }
  ```
- **Catching specific error types:**
  ```javascript
  try {
    validateAge("thirty");
  } catch (error) {
    if (error instanceof ValidationError) {
      console.error(`Validation failed on ${error.field}: ${error.message}`);
    } else if (error instanceof NetworkError) {
      console.error(`Network error (${error.statusCode}): ${error.message}`);
    } else {
      throw error; // Re-throw unknown errors — don't swallow them
    }
  }
  ```
- **Error handling best practices:**
  - Never swallow errors with an empty `catch` block
  - Re-throw errors you can't handle
  - Use `finally` for cleanup (close connections, hide spinners)
  - Log errors with enough context to debug
  - Use custom error classes for domain-specific errors
  - Avoid using errors for control flow (normal conditions)

**Notes:**
Custom error classes are standard practice in professional code — they make error handling more specific and informative. The `instanceof` check for catching specific error types mirrors Java's multiple catch blocks. The "re-throw unknown errors" rule is critical — a `catch` block should only catch errors it knows how to handle; unknown errors should propagate. Async error handling (try/catch with async/await and `.catch()` with Promises) is covered in Day 14. Students should start forming the habit of always asking: "What can go wrong here?" before every operation that can fail.

---

### Slide 15: Working with Strings — Methods Reference
**Title:** String Methods — Essential Operations

**Content:**
- Strings are primitive but have rich methods via **auto-boxing**
- **Common string methods:**
  ```javascript
  const str = "  Hello, World!  ";

  // Case:
  str.toUpperCase();     // "  HELLO, WORLD!  "
  str.toLowerCase();     // "  hello, world!  "

  // Trimming whitespace:
  str.trim();            // "Hello, World!" — both ends
  str.trimStart();       // "Hello, World!  "
  str.trimEnd();         // "  Hello, World!"

  // Searching:
  str.includes("World"); // true
  str.startsWith("  He"); // true
  str.endsWith("!  ");   // true
  str.indexOf("o");      // 4 (first occurrence) or -1

  // Extraction:
  str.slice(2, 7);       // "Hello"
  str.slice(-3);         // "!  " — negative counts from end

  // Splitting and joining:
  "a,b,c".split(",");    // ["a", "b", "c"]
  ["a","b","c"].join("-"); // "a-b-c"

  // Replacing:
  str.replace("World", "JS"); // replaces first occurrence
  str.replaceAll("l", "L");   // replaces all

  // Padding and repeating:
  "5".padStart(3, "0");  // "005"
  "ha".repeat(3);        // "hahaha"

  // Checking with regex:
  "hello123".match(/\d+/);    // ["123"]
  "hello".test;               // (use regex.test(str) instead)
  /\d+/.test("hello123");     // true
  ```

**Notes:**
Strings are immutable in JavaScript — methods always return new strings, never modify the original. Auto-boxing is why you can call methods on string primitives — JavaScript temporarily wraps them in String objects. `slice` is preferred over `substring` (handles negative indices better). `split`/`join` are a natural pair — split a string into an array, transform the array, rejoin. `padStart` is used for zero-padding numbers (like formatting hours/minutes: `"5".padStart(2, "0")` → "05"). Regular expressions are their own topic — the `match` and `test` examples are the most common patterns. Students will use string methods constantly in form validation and data formatting.

---

### Slide 16: Numbers and Math — Key Methods
**Title:** Numbers and the Math Object

**Content:**
- **Number methods:**
  ```javascript
  const n = 3.14159;
  n.toFixed(2);          // "3.14" — returns string!
  n.toPrecision(4);      // "3.142"
  Number.isInteger(42);  // true
  Number.isInteger(3.14); // false
  Number.isNaN(NaN);     // true (safer than global isNaN)
  Number.isFinite(Infinity); // false
  Number.parseInt("42px");   // 42
  Number.parseFloat("3.14x"); // 3.14
  ```
- **Math object:**
  ```javascript
  Math.round(4.6);    // 5
  Math.floor(4.9);    // 4 — round DOWN
  Math.ceil(4.1);     // 5 — round UP
  Math.abs(-7);       // 7
  Math.max(1, 5, 3);  // 5
  Math.min(1, 5, 3);  // 1
  Math.pow(2, 10);    // 1024
  Math.sqrt(16);      // 4
  Math.random();      // 0 ≤ n < 1 — random float

  // Random integer between min and max (inclusive):
  function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }
  randomInt(1, 6); // Random dice roll
  ```
- **Floating point precision:**
  ```javascript
  0.1 + 0.2; // 0.30000000000000004 — IEEE 754 float issue
  // Fix: (0.1 + 0.2).toFixed(2) → "0.30" (as string)
  // Or: Math.round((0.1 + 0.2) * 100) / 100 → 0.3
  ```

**Notes:**
`toFixed` is commonly used for displaying currency — but it returns a string, not a number. The floating point precision issue (`0.1 + 0.2 !== 0.3`) is an IEEE 754 standard limitation, not a JavaScript bug — all languages using 64-bit floats have this. For financial calculations, use integer arithmetic (work in cents, not dollars) or a library like `decimal.js`. `Math.random()` for random integers is a classic interview question — the `randomInt` formula is worth memorizing. `Number.isNaN()` is safer than the global `isNaN()` because the global version coerces non-numbers first (`isNaN("hello")` returns `true` — confusing); `Number.isNaN("hello")` correctly returns `false`.

---

### Slide 17: The Event Loop — A Preview
**Title:** The Event Loop — Why JavaScript Is Single-Threaded but Non-Blocking

**Content:**
- JavaScript is **single-threaded** — it runs one operation at a time on one call stack
- But web browsers have multiple threads and APIs — JavaScript uses them asynchronously
- **The Call Stack** — where JavaScript tracks function calls (LIFO):
  ```javascript
  function first() { second(); }
  function second() { third(); }
  function third() { console.log("Hello"); }
  first(); // Call stack: first → second → third → console.log
  ```
- **The Event Loop and Callback Queue:**
  ```javascript
  console.log("1 - Start");

  setTimeout(() => {
    console.log("2 - Timeout"); // Goes to callback queue
  }, 0);

  console.log("3 - End");

  // Output: "1 - Start", "3 - End", "2 - Timeout"
  // Even with 0ms, setTimeout doesn't run immediately!
  ```
- **Why:** `setTimeout` sends the callback to the browser's Timer API. When the timer finishes, the callback enters the callback queue. The event loop only moves it to the call stack when the stack is empty.
- **Key mental model:**
  - Call stack: where synchronous code runs
  - Web APIs: browser features (timers, fetch, DOM events)
  - Callback queue: completed async callbacks waiting to run
  - Event loop: moves callbacks from queue to stack when stack is empty

**Notes:**
This slide is a PREVIEW — full async coverage (Promises, async/await, Fetch API) is Day 14. The goal here is to establish the mental model so Day 14 isn't a complete surprise. The `setTimeout(..., 0)` trick is a classic JavaScript interview question — it demonstrates that "0ms" doesn't mean "immediately," it means "as soon as the stack is clear." The key insight: JavaScript's non-blocking behavior is achieved not by running things simultaneously (it's single-threaded) but by deferring async work to browser APIs and running callbacks when the call stack is free. This model explains why long synchronous operations freeze the browser — they block the call stack, preventing event callbacks from running.

---

### Slide 18: JavaScript Modules — import and export
**Title:** ES6 Modules — Organizing Code into Files

**Content:**
- ES6 modules enable splitting code into separate files
- **Named exports:**
  ```javascript
  // math.js:
  export function add(a, b) { return a + b; }
  export function subtract(a, b) { return a - b; }
  export const PI = 3.14159;
  ```
- **Default export** — one per file:
  ```javascript
  // greet.js:
  export default function greet(name) {
    return `Hello, ${name}!`;
  }
  ```
- **Importing:**
  ```javascript
  // Named imports:
  import { add, subtract } from "./math.js";
  import { add as plus } from "./math.js"; // Alias

  // Default import (any name you want):
  import greet from "./greet.js";
  import sayHello from "./greet.js"; // Same function, different name

  // Import all named exports:
  import * as MathUtils from "./math.js";
  MathUtils.add(2, 3);
  ```
- **Using modules in HTML:**
  ```html
  <!-- type="module" enables ES6 module syntax -->
  <script type="module" src="app.js"></script>
  ```
- **Module characteristics:**
  - Automatically in strict mode
  - Each file has its own scope (no global pollution)
  - Imported bindings are live — not copies
  - Static analysis — imports/exports evaluated at parse time

**Notes:**
ES6 modules are how modern JavaScript (and all frameworks — React, Angular) is organized. `type="module"` in the script tag enables native browser support. The distinction between named and default exports matters: named exports are imported with `{}` and must match the export name (or be aliased), default exports are imported without `{}` and can be named anything. A common pattern: a module has one default export (the main thing it does) and several named exports (helpers). Deep module patterns and bundlers (Webpack, Vite) are covered when frameworks are introduced in Week 4.

---

### Slide 19: Debugging JavaScript
**Title:** Debugging — Tools, Techniques, and Best Practices

**Content:**
- **Browser DevTools — the primary debugging environment:**
  - **Console tab:** `console.log()`, `console.error()`, `console.table()`, `console.group()`
  - **Sources tab:** Set breakpoints, step through code, inspect variables
  - **Network tab:** Monitor HTTP requests (key in Week 5+)
  - **Elements tab:** Inspect DOM and CSS (Day 13+)

- **Using breakpoints:**
  ```javascript
  function calculateTotal(items) {
    debugger; // Pauses execution when DevTools is open
    const subtotal = items.reduce((sum, item) => sum + item.price, 0);
    return subtotal * 1.1;
  }
  ```
  Or set breakpoints in Sources tab by clicking line numbers

- **Console debugging tips:**
  ```javascript
  // Log with labels:
  console.log("items:", items); // Always label your logs

  // Log an object snapshot:
  console.log(JSON.parse(JSON.stringify(obj))); // Deep copy to prevent live reference

  // Time code execution:
  console.time("sorting");
  arr.sort();
  console.timeEnd("sorting"); // "sorting: 0.123ms"

  // Group related logs:
  console.group("User validation");
  console.log("Name:", name);
  console.log("Email:", email);
  console.groupEnd();
  ```
- **Common runtime errors and fixes:**
  - `TypeError: Cannot read properties of undefined` → check for null/undefined before access, use `?.`
  - `ReferenceError: x is not defined` → check spelling, scope, `let`/`const` TDZ
  - `SyntaxError` → check for missing brackets, commas, parentheses

**Notes:**
Debugging skills are often undertaught but critically important. The `debugger` statement is unknown to many students — it's extremely powerful. The Sources tab in DevTools lets you set conditional breakpoints, watch expressions, and step through code line by line — show this live. The `console.log(JSON.parse(JSON.stringify(obj)))` trick solves the issue where logging an object shows its CURRENT state (because objects are references) — the JSON round-trip creates a snapshot. `console.time`/`timeEnd` is useful for profiling. Emphasize: good developers are not those who never have bugs, but those who find and fix them efficiently.

---

### Slide 20: Day 12 Complete — Week 3 Roadmap
**Title:** Day 12 Complete — What's Coming in Week 3

**Content:**
**Today's JavaScript Fundamentals — Complete:**
- ✅ What JavaScript is and how it runs in browsers
- ✅ Variables: `const`, `let`, `var` — scope and hoisting
- ✅ Primitive types and `typeof`
- ✅ Type coercion and strict equality (`===`)
- ✅ Truthy/falsy and short-circuit patterns
- ✅ Operators, control flow, loops
- ✅ Arrays and array methods (`map`, `filter`, `reduce`)
- ✅ Template literals
- ✅ Functions: declarations, expressions, arrow functions
- ✅ Rest/spread operators
- ✅ `this` keyword: implicit, explicit, arrow functions
- ✅ `call()`, `apply()`, `bind()`
- ✅ Closures and lexical scope (module pattern, memoization)
- ✅ Hoisting in depth (TDZ, function declarations)
- ✅ Strict mode
- ✅ Objects and destructuring
- ✅ Error handling: try/catch, custom errors
- ✅ Event Loop preview
- ✅ ES6 Modules (import/export)
- ✅ Debugging with DevTools

**Week 3 Remaining:**
- 📅 **Day 13 (Tomorrow):** DOM Manipulation & Events — use JavaScript to interact with your HTML/CSS
- 📅 **Day 14 (Thursday):** ES6+, OOP in JS & Async JavaScript — Classes, Promises, async/await, Fetch API
- 📅 **Day 15 (Friday):** TypeScript — type safety for JavaScript

**Key Message:**
Days 13 and 14 are where JavaScript starts doing visible, interactive things. Everything you learned today is the foundation that makes those days possible.

**Notes:**
Use this closing slide to emphasize the journey: students went from zero JavaScript to understanding the language's core concepts, functional patterns, and some of its most complex behaviors (`this`, closures, hoisting) in one day. Frame Day 13 as the exciting application of today's work — they'll finally see JavaScript making web pages interactive. Mention that the concepts covered today (especially closures and `this`) are top interview topics and will come up in React (Week 4) repeatedly.
