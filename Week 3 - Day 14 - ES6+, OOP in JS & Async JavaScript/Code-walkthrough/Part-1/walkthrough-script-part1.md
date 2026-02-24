# Day 14 — Part 1 Walkthrough Script
## ES6+, OOP in JavaScript
### Estimated time: 90 minutes

---

## Pre-Class Setup
- Open VS Code with the `Part-1/` folder visible in the explorer
- Have Node.js ready: `node 01-oop-classes-and-prototypes.js` should run cleanly
- Keep a blank browser tab open (Chrome DevTools → Console) for live demos
- Have the MDN page for `Map` bookmarked just in case

---

## PART 1A — OOP: Classes & Prototypes (35 min)
### File: `01-oop-classes-and-prototypes.js`

---

### [ACTION] Open `01-oop-classes-and-prototypes.js`. Scroll to Section 1.

---

### SECTION 1 — Constructor Functions & Prototype Chain (8 min)

**Say:**
> "Before ES6 classes existed, JavaScript had no `class` keyword. But it still had object-oriented programming — just done differently. Let's look at how it worked."

**[ACTION]** Highlight the `function Person(name, age)` constructor.

**Say:**
> "This is a constructor function. Notice the capital P — that's just a convention to signal 'call this with `new`'. When you call `new Person(...)`, JavaScript does four things automatically."

**[ACTION]** Write on board:
```
1. Creates a new empty object {}
2. Sets its [[Prototype]] to Person.prototype
3. Runs the constructor body with `this` = new object
4. Returns the new object
```

**[ASK]** "What do you think happens if you forget `new` and just call `Person('Alice', 30)`?"
> *(Let students answer — `this` becomes `undefined` in strict mode, or the global object in sloppy mode. Very common bug.)*

**[ACTION]** Show `Person.prototype.greet` being added after the function definition.

**Say:**
> "Notice we add `greet` to `Person.prototype` — NOT inside the constructor. That's the right pattern. If we put it inside the constructor, every new Person object would get its own copy of the function. By putting it on the prototype, all instances share one copy."

**[ACTION]** Run the code. Show `Object.getPrototypeOf(alice) === Person.prototype` → `true`.

**[ACTION]** Draw on board:
```
alice (instance)
  └── [[Prototype]] → Person.prototype
                          .greet()
                          .toString()  (inherited)
                          └── [[Prototype]] → Object.prototype
                                                .hasOwnProperty()
                                                .toString()
                                                └── [[Prototype]] → null
```

**Say:**
> "This is the prototype chain. When you call `alice.greet()`, JavaScript first looks on `alice` itself — not found. Then looks on `Person.prototype` — found! This is how inheritance works in JS: delegation up the chain."

---

### SECTION 2 — Class Syntax (8 min)

**Say:**
> "ES6 gave us the `class` keyword. Here's the important thing: **classes are syntactic sugar over prototypes**. Under the hood, everything is still the same prototype chain we just drew."

**[ACTION]** Highlight the `class Animal` definition.

**[ASK]** "What's the `constructor` method for?"
> *(It runs when you call `new Animal(...)` — sets up instance properties.)*

**[ACTION]** Show `Animal.prototype` has `speak` on it — same as before.

**[ACTION]** Move to `class Dog extends Animal`.

**Say:**
> "Inheritance with `extends`. The `super()` call in the constructor is mandatory — you must call it before using `this`. What does it do?"

**[ASK]** "What do you think `super(name)` does here?"
> *(Calls the parent class constructor with the given arguments — sets up `this.name`.)*

**[ACTION]** Show `super.speak()` inside `bark()`.

**Say:**
> "You can also call parent class methods using `super.methodName()`. That's how you extend behavior rather than completely replace it."

**⚠️ WATCH OUT:**
> "`class` declarations are NOT hoisted like function declarations. You can't use a class before it's defined in the file. Also, everything inside a class body runs in strict mode automatically."

---

### SECTION 3 — Static Methods & Private Fields (7 min)

**[ACTION]** Highlight `static create(...)` on the `BankAccount` class.

**Say:**
> "Static methods belong to the class itself, not to instances. They're often used for factory methods like this `create()` — a named constructor that can do validation before returning a new object."

**[ACTION]** Show `BankAccount.create(...)` vs `new BankAccount(...)`.

**[ACTION]** Highlight the `#balance` and `#pin` private fields.

**Say:**
> "The `#` prefix makes a field truly private — it's enforced by the engine itself, not just convention. You cannot access `#balance` from outside the class."

**[ACTION]** Try in the console: `account.#balance` → `SyntaxError: Private field '#balance' must be declared in an enclosing class`

**Say:**
> "This is much stronger than the old underscore convention (`_balance`). That was just an honor system. `#` is enforced."

**⚠️ WATCH OUT:**
> "Private fields MUST be declared at the top of the class body before they're used. You'll get a syntax error otherwise."

---

### SECTION 4 — OOP Patterns (4 min)

**[ACTION]** Scroll to the Mixin section quickly.

**Say:**
> "Two patterns worth knowing. First, mixins — a way to compose behaviors from multiple sources. JavaScript only has single inheritance, but with mixins you can mix in functionality from multiple sources."

**[ACTION]** Point to the `Serializable` and `Validatable` mixins.

**Say:**
> "The pattern is: write a mixin as a function that takes a superclass and returns an extended class. Then you can layer them."

**[ACTION]** Briefly show the Registry pattern at the end.

**Say:**
> "This one stores class instances centrally and throws if you try to add a duplicate ID. You'll see patterns like this in real apps — service registries, plugin systems, dependency injection containers."

→ **TRANSITION:** "Now let's move to the features that shipped alongside classes in ES6 — modern JavaScript syntax that you'll use every single day."

---

## PART 1B — ES6+ Features (30 min)
### File: `02-es6-features.js`

---

### [ACTION] Open `02-es6-features.js`. Jump to Section 1.

---

### SECTION 1 — Default Parameters (4 min)

**Say:**
> "Default parameters — seemingly simple, but with a gotcha."

**[ACTION]** Show `greet()` and run `greet(null, "?")`.

**[ASK]** "What do you expect `greet(null, "?")` to return?"

**Say:**
> "A lot of people assume null triggers the default. It doesn't. Only `undefined` triggers a default parameter. This trips up a ton of developers. Remember: `null` means 'intentionally no value', `undefined` means 'no value was provided'."

**[ACTION]** Show the `createPoint(x = 0, y = x)` example.

**Say:**
> "Defaults can reference earlier parameters. `y = x` means 'default y to whatever x is'. This makes default values dynamic and expressive."

---

### SECTION 2 — Rest Parameters (3 min)

**[ACTION]** Show `function sum(...numbers)`.

**Say:**
> "Rest collects all remaining arguments into a real Array. The `...` on a function parameter means 'give me the rest as an array'."

**⚠️ WATCH OUT:**
> "Rest must be the LAST parameter. `function foo(...a, b)` is a syntax error."

**[ASK]** "What's the difference between rest and the `arguments` object?"
> *(Rest is a real Array with `.map`, `.filter`, etc. `arguments` is array-like — has indices and `.length` but no array methods. Rest also doesn't capture pre-rest arguments.)*

---

### SECTION 3 — Spread Operator (5 min)

**Say:**
> "Same `...` syntax but in a different position — at the call site. Rest *collects*; spread *expands*."

**[ACTION]** Draw on board:
```
REST   →  collects args INTO an array       (function definition)
SPREAD →  expands an iterable INTO pieces   (function call / array / object)
```

**[ACTION]** Show array spread: `[...nums1, ...nums2]`, shallow copy.

**[ACTION]** Show the shallow copy gotcha: `clone.b.c = 99` still affects `original2.b.c`.

**Say:**
> "Spread does a shallow copy. Primitives are copied by value. But nested objects are still shared by reference. For a deep clone you need `structuredClone()` (modern) or `JSON.parse(JSON.stringify(obj))` (the old trick with caveats)."

**[ACTION]** Show object spread: `{ ...defaults, ...userPrefs }`.

**Say:**
> "The most common use in frontend development — especially React. You'll see this pattern constantly for immutable state updates: spread the old object, then override specific keys."

---

### SECTION 4 — Destructuring (8 min)

**[ACTION]** Show array destructuring basics.

**Say:**
> "Destructuring is pattern matching — you describe the shape of what you expect on the left side, and JavaScript extracts matching values from the right side."

**[ACTION]** Show the swap trick: `[p, q] = [q, p]`.

**[ASK]** "Why does this work? What's the right side evaluating to first?"
> *(Creates a new array with the current values of q and p, then destructures into p and q.)*

**[ACTION]** Move to object destructuring. Show rename syntax.

**Say:**
> "In object destructuring, `{ name: personName }` does NOT mean 'assign `personName` to `name`'. It means 'extract the `name` key, and store it in a variable called `personName`'. The pattern is always `{ sourceKey: newVariableName }`."

**⚠️ WATCH OUT:**
> "This is the #1 source of confusion with destructuring. `{ a: b }` is extracting key `a` and naming the variable `b`. It's backwards from what you might expect."

**[ACTION]** Show function parameter destructuring — `displayUser({ name, age, role = "guest" })`.

**Say:**
> "This is extremely common in React components and Node.js handler functions. You receive an object and immediately destructure the properties you care about, with defaults for optional ones. Much cleaner than `function foo(options)` then `options.name` throughout."

---

### SECTION 5 — Enhanced Object Literals (5 min)

**[ACTION]** Show shorthand properties side by side.

**Say:**
> "When your variable name matches the key name, you can just write it once. Very common when building objects from computed values."

**[ACTION]** Show computed property names.

**Say:**
> "The `[]` in an object literal is evaluated as an expression. This lets you use a variable or any expression as a key."

**[ACTION]** Show the `createApiConfig` function at the bottom.

**Say:**
> "This is a real-world example using everything at once: default parameter, object destructuring in the parameter, rest to catch unknown options, spread to merge headers, and computed keys for the timestamp field. This is the kind of code you'll write in production."

→ **TRANSITION:** "One more area to cover: how we organize code across files — ES Modules — and two powerful collection types."

---

## PART 1C — Modules, Map, Set & Advanced Features (20 min)
### File: `03-modules-map-set-advanced.js`

---

### [ACTION] Open `03-modules-map-set-advanced.js`. Start at Section 1 (the comment block).

---

### SECTION 1 — ES Modules (6 min)

**Say:**
> "ES Modules are how modern JavaScript organizes code across files. Let's look at the syntax — it's all commented out because running modules requires a bundler or a `type="module"` environment, but you need to know this cold."

**[ACTION]** Read through named exports, then default exports.

**[ACTION]** Draw on board:
```
EXPORT side          |  IMPORT side
---------------------|--------------------------------
export const X = ..  |  import { X } from "./file.js"
export function f()  |  import { f } from "./file.js"
export default Cls   |  import Cls from "./file.js"     (no braces)
export { a as b }    |  import { b } from "./file.js"
                     |  import * as NS from "./file.js" (namespace)
```

**Say:**
> "Two types: named exports (you can have many) and the default export (one per module). Named imports use curly braces, default imports don't."

**[ACTION]** Show the barrel/index.js pattern.

**Say:**
> "In larger projects you'll have an `index.js` that re-exports everything from a folder. This is called a barrel file. Instead of importing from five different deep paths, consumers just import from `"./components"` and the index sorts it out."

**[ACTION]** Show dynamic import.

**Say:**
> "Dynamic import returns a Promise. This is how code splitting works — you only load a module when you actually need it. Vite and Webpack use this for lazy routes in SPAs."

---

### SECTION 2 — Map (7 min)

**[ACTION]** Show `new Map()` and the `.set`, `.get`, `.has`, `.delete`, `.size` demo.

**Say:**
> "Map is like a plain object, but with superpowers. The keys can be anything — numbers, booleans, even other objects."

**[ACTION]** Show `userMap.set(objKey, "object as key")` and `userMap.get(objKey)`.

**Say:**
> "Plain objects can only use string or Symbol keys — everything else gets coerced to a string. Map keeps the actual reference."

**[ACTION]** Show iterating with `for...of` and destructuring.

**[ACTION]** Show the Map vs Object summary in comments.

**[ASK]** "When would you choose a Map over a plain object?"
> *(When keys aren't strings, when you need `.size` easily, when you're doing lots of add/delete, or when you need to preserve insertion order reliably.)*

**Say:**
> "The one big caveat: `JSON.stringify` doesn't serialize Maps. If you need to send data to an API, either convert to an object first with `Object.fromEntries(map)`, or just use a plain object."

**[ACTION]** Show the word frequency counter at the bottom.

---

### SECTION 3 — Set (4 min)

**[ACTION]** Show deduplication: `[...new Set(numbers)]`.

**Say:**
> "This is the most common use of Set in day-to-day code. One line to deduplicate an array."

**[ACTION]** Quickly show union, intersection, difference.

**Say:**
> "JavaScript doesn't have built-in set operations, but they're trivial to implement with spread and `.filter`. These come up in interview questions and real data manipulation tasks."

---

### SECTION 4 — Advanced Features (3 min — fly-over)

**Say:**
> "We have four more: Symbol, Iterator, Generator, WeakMap/WeakSet, Proxy, Reflect. These are less daily-use but important to know exist."

**[ACTION]** Briefly run through each, hitting one key point:
- **Symbol** → "Guaranteed unique keys. Used in library code and well-known hooks like `Symbol.iterator`."
- **Iterator** → "The protocol behind `for...of`. Any object with `[Symbol.iterator]()` works with spread, destructuring, and `for...of`."
- **Generator** → "Functions that can pause with `yield`. Great for lazy sequences and async flows. `async/await` is actually built on top of generators internally."
- **WeakMap/WeakSet** → "Weak references — entries are GC'd when the key object dies. Used in libraries for caching metadata without preventing garbage collection."
- **Proxy/Reflect** → "Intercept object operations. Used in Vue 3's reactivity system. `Reflect` mirrors the Proxy traps and is used to forward default behavior cleanly."

---

## Wrap-Up Q&A (5 min)

**[ASK]** "What's the difference between spread and rest? Give me a one-liner."
> *("Rest collects; spread expands.")*

**[ASK]** "You have an array with 500 items and you need only the unique ones. What's the shortest way?"
> *(`[...new Set(array)]`)*

**[ASK]** "In `const { name: n } = obj`, what does `n` refer to?"
> *(`n` is the variable holding `obj.name`. The key is `name`, the variable name is `n`.)*

**[ASK]** "Why might you choose Map over a plain object for tracking click counts per element?"
> *(Because DOM element references as keys — Map supports object keys, plain objects would coerce them to `[object HTMLElement]`)*

**[ASK]** "What's the difference between the default export and a named export?"
> *(One module can have many named exports but only one default export. Named imports use `{}`, default imports don't.)*

---

## Take-Home Exercise

1. Create a `class Shape` with a constructor taking `color` and a `describe()` method. Extend it with `class Circle` (adds `radius`) and `class Rectangle` (adds `width`, `height`). Add static factory methods `Circle.create(color, radius)` and `Rectangle.create(color, w, h)`.

2. Write a function `groupBy(array, keyFn)` that uses a `Map` to group array items by the result of `keyFn`. E.g. `groupBy([1,2,3,4,5], n => n % 2 === 0 ? "even" : "odd")` → `Map { "odd" => [1,3,5], "even" => [2,4] }`.

3. Write a module `stringUtils.js` with named exports `capitalize`, `camelCase`, and `slugify`. Write a second file that imports only `capitalize` and `slugify`.

4. Refactor this function using destructuring and defaults:
```js
function processOrder(order) {
  const id = order.id;
  const qty = order.qty || 1;
  const items = order.items;
  const firstName = order.customer.firstName;
  return `Order ${id}: ${qty}x (${firstName})`;
}
```
