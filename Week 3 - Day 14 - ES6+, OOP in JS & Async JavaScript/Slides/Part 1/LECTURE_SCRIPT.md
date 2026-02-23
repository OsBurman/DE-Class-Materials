# Week 3 - Day 14 (Thursday): ES6+, OOP in JS & Async JavaScript
## Part 1 — Lecture Script (60 Minutes)

---

**[00:00–02:00] Opening — JavaScript OOP Is Different**

Good morning. Today is a big one — we're going deeper into JavaScript than we've been before.

On Day 12 you learned the language fundamentals. Variables, functions, closures, the `this` keyword. Day 13 you put it to work — DOM manipulation, events, built a to-do app. Today we go under the hood of the language itself. Two major topics: object-oriented programming in JavaScript, and ES6+ syntax features that you'll use in every single file you write going forward.

I want to start with a statement that might be surprising: JavaScript OOP is not the same as Java OOP. You've spent weeks in Java where `class` is the fundamental building block of everything. In JavaScript, classes are newer — they were added in ES6 in 2015 — and they're syntactic sugar over something older and actually more fundamental: the prototype system.

This distinction matters because when you're debugging a JavaScript app, reading a stack trace, or reviewing Angular or React source code, you'll see the underlying prototype mechanism. If you don't understand it, those things look like magic. If you do understand it, everything makes sense.

Let's build the mental model correctly from the start.

---

**[02:00–08:00] The Prototype Chain**

Every object in JavaScript has a hidden internal reference called `[[Prototype]]`. When you access a property on an object and the property isn't found ON that object, JavaScript automatically looks at the object's prototype. If it's not there, it looks at the prototype's prototype. This continues up the chain until either the property is found or we reach `null` — the end of every chain.

This chain is called the prototype chain, and it's the mechanism behind all JavaScript inheritance.

Let me show you the most direct form of prototypal inheritance, without classes at all:

```javascript
const animal = {
  speak() { console.log(`${this.name} makes a sound`); }
};
const dog = Object.create(animal);
dog.name = "Rex";
dog.speak(); // "Rex makes a sound"
```

When we call `dog.speak()`, JavaScript first looks at `dog` itself. Not there. Then it looks at dog's prototype — the `animal` object. Found. It calls the function with `this` set to `dog`, so `this.name` is "Rex."

Here's the thing — `speak` was never copied to `dog`. There's one function shared by all objects that use `animal` as their prototype. That's the memory efficiency model JavaScript is built on. Every instance doesn't get its own copy of every method.

`Object.getPrototypeOf(dog) === animal` — this returns `true`. We can inspect the chain.

`dog.hasOwnProperty("name")` — `true`, name is on dog directly.
`dog.hasOwnProperty("speak")` — `false`, speak is inherited.

The chain looks like this: dog → animal → Object.prototype → null. `Object.prototype` is the root of all prototype chains — it's where `toString()`, `hasOwnProperty()`, `valueOf()` all live.

One critical rule: never add properties to `Object.prototype`. It affects every single object in your entire application. This is called prototype pollution and it's a real security vulnerability in npm packages.

---

**[08:00–14:00] Constructor Functions**

Before 2015, before ES6 classes, JavaScript developers did OOP with constructor functions. You will see this in legacy codebases, Node.js modules, and library source code. Let me show you the pattern so it's not mysterious.

By convention, constructor functions start with an uppercase letter — just like class names. When you call them with `new`, something specific happens.

```javascript
function Animal(name, sound) {
  this.name  = name;
  this.sound = sound;
}
Animal.prototype.speak = function() {
  console.log(`${this.name} says ${this.sound}`);
};
```

Methods go on `Animal.prototype` — not inside the constructor. Why? Because everything inside the constructor body gets copied to every instance. If `speak` were inside the constructor, every `new Animal()` would get its own copy. By putting it on the prototype, all instances share one function.

`new Animal("Rex", "woof")` does four things:
1. Creates an empty object `{}`
2. Sets that object's prototype to `Animal.prototype`
3. Runs the `Animal` function with `this` pointing to the new object
4. Returns the new object

Before ES6, inheriting from another constructor was even messier. You had to call the parent constructor with `.call(this)`, then manually set up the prototype chain with `Object.create`. It worked, but it was verbose and error-prone.

In 2015, ES6 gave us `class` syntax that does all of that automatically, correctly, every time. Let's see it.

---

**[14:00–20:00] ES6 Classes**

ES6 classes are syntactic sugar over what I just showed you. Under the hood, the browser is still setting up prototype chains. The `class` keyword just makes it clean and readable.

```javascript
class Animal {
  #sound;
  constructor(name, sound) {
    this.name  = name;
    this.#sound = sound;
  }
  speak() {
    console.log(`${this.name} says ${this.#sound}`);
  }
  get description() {
    return `${this.name} (${this.#sound})`;
  }
  static create(name, sound) {
    return new Animal(name, sound);
  }
}
```

A few things to notice. The `constructor` method is the old constructor function — it runs when you do `new Animal(...)`. Other methods — `speak()`, `description` getter — go on the prototype automatically. You don't have to manually assign to `Animal.prototype` anymore.

`#sound` is a private field. The `#` prefix is enforced by the JavaScript engine. If you try to access `cat.#sound` from outside the class body, you get a SyntaxError at parse time — before the code even runs. This is different from the old convention of using an underscore prefix, like `_sound`. Underscore was just a social convention saying "please don't touch this." The engine didn't enforce it. Anyone could still read and write `cat._sound`. With `#`, it's genuinely inaccessible from outside.

`static create(name, sound)` — static methods live on the class itself, not on instances. You call it as `Animal.create("Dog", "woof")`, not as `dog.create(...)`. Use static methods for factory patterns, utility functions that belong with the class conceptually but don't need instance data.

One difference from Java that matters: JavaScript classes are NOT hoisted. You can use a function declaration before it's defined in the file. You cannot use a class before its definition. Also, the class body runs in strict mode automatically — so `this` is `undefined` if you call a method without a proper receiver, rather than being the global object.

---

**[20:00–26:00] Inheritance with extends and super**

Inheritance in ES6 is readable and correct:

```javascript
class Dog extends Animal {
  #tricks;
  constructor(name) {
    super(name, "woof"); // MUST call this before using 'this'
    this.#tricks = [];
  }
  learn(trick) {
    this.#tricks.push(trick);
    return this; // enables method chaining
  }
  speak() {
    super.speak();
    console.log(`${this.name} wags its tail.`);
  }
}
```

`extends Animal` sets up the prototype chain — `Dog.prototype`'s prototype is `Animal.prototype`. That's the full inheritance.

In the constructor, `super(name, "woof")` calls `Animal`'s constructor. This is mandatory in any derived class. The rule is: you must call `super()` before you touch `this` in a derived constructor. The reason: `this` is only initialized after the parent constructor runs. If you try to use `this` before calling `super`, you get a ReferenceError.

In `speak()`, I'm calling `super.speak()` first — this runs Animal's version, which logs "Rex says woof." Then I add Dog-specific behavior. This is method overriding, and calling the parent version with `super.method()` is the standard extension pattern.

Method chaining: `dog.learn("sit").learn("shake").learn("roll over")` works because `learn()` returns `this` — the instance itself. Each call returns the same object, so you can keep calling methods on it. You see this in jQuery, Axios, and other fluent APIs.

`dog instanceof Dog` is true. `dog instanceof Animal` is also true — Dog IS-A Animal. The `instanceof` operator checks the prototype chain.

---

**[26:00–30:00] Static Members, Private Fields, and OOP Patterns**

Static members and private fields let you write genuinely encapsulated classes. You've seen both already. Let me add one more OOP concept: what do you do when single inheritance isn't enough?

JavaScript allows only one `extends` per class. But a `User` might need both serialization behavior AND validation behavior AND authentication behavior. You can't inherit from three parents.

The solution is mixins:
```javascript
const Serializable = {
  serialize() { return JSON.stringify(this); }
};
const Validatable = {
  validate() {
    return Object.entries(this).every(([, v]) => v !== null && v !== undefined);
  }
};
class User {
  constructor(name, email) {
    this.name  = name;
    this.email = email;
  }
}
Object.assign(User.prototype, Serializable, Validatable);
const user = new User("Alice", "alice@example.com");
user.serialize(); // '{"name":"Alice","email":"alice@example.com"}'
user.validate();  // true
```

`Object.assign(User.prototype, ...)` copies the mixin methods onto the prototype. Instances can now call them.

The broader principle here is "composition over inheritance." Deep inheritance trees are rigid. If you have `Animal → Mammal → Canine → Dog → ServiceDog → GuideDog`, changing `Animal` could break all six levels. Composition — building objects from focused, independently testable pieces — avoids that fragility. React's whole component model is built on this principle.

---

**[30:00–36:00] Default Parameters, Enhanced Object Literals, Destructuring**

Let's move into ES6+ syntax. These are features you'll use in every single file. They're not conceptually complex but they make code dramatically cleaner.

Default parameters. Before ES6, you wrote `name = name || "World"` — and that's actually wrong if you want to pass `false` or `0` as a legitimate value, because those are falsy and would trigger the default. ES6 gives you explicit defaults:

```javascript
function greet(name = "World", greeting = "Hello") {
  return `${greeting}, ${name}!`;
}
```

The default only activates when the argument is `undefined`. Not when it's `null`, not when it's `0`, not when it's `false`. Only `undefined`. That's the explicit, clear behavior. You can even use expressions as defaults — `function makeId(prefix = "user", ts = Date.now())` — the expression is evaluated fresh each time the function is called.

Enhanced object literals — three shortcuts. Property shorthand: when the variable name matches the property name, you can write just `{ name }` instead of `{ name: name }`. Method shorthand: write `greet() {}` instead of `greet: function() {}`. Computed property names: `{ [dynamicKey]: value }` — the key is computed from the expression in brackets.

Destructuring. This is huge. Let me walk through it:

Object destructuring:
```javascript
const { name, age } = user;           // basic extraction
const { name: userName } = user;      // rename
const { score = 0 } = user;           // default if undefined
const { db: { host, port } } = config; // nested
const { name: n, ...rest } = user;    // rest collects the remainder
```

Array destructuring:
```javascript
const [first, second] = arr;
const [, , third]     = arr; // skip elements with empty slots
const [head, ...tail] = arr;
[a, b] = [b, a];             // variable swap — no temp variable
```

Function parameter destructuring — this is the most practical use:
```javascript
function render({ color = "black", size = 16, label }) { ... }
```

Instead of accessing `opts.color`, `opts.size` inside the function, you destructure right at the parameter. Cleaner, and you get defaults for free.

---

**[36:00–42:00] Spread and Rest**

Spread and rest both use the `...` syntax but do opposite things. Spread takes a collection and expands it into individual elements. Rest takes individual elements and collects them into an array.

Arrays:
```javascript
const combined = [...arr1, ...arr2]; // merge arrays
const copy     = [...arr];           // shallow copy
Math.max(...nums);                   // spread into function arguments
```

Objects:
```javascript
const config = { ...defaults, ...overrides };
// Later spread wins — overrides replace defaults
const updated = { ...user, lastLogin: new Date() };
// Add a property to a copy without mutating the original
```

There's a critical gotcha with object spread: it's **shallow**. If `user.address` is an object, `{ ...user }` doesn't create a new address object — both the original and the copy point to the SAME address object. Changing `copy.address.city` changes `original.address.city`. This catches people. For a deep clone, you have `structuredClone(obj)` in modern environments, or `JSON.parse(JSON.stringify(obj))` for simple data.

Rest in function parameters:
```javascript
function sum(...numbers) {
  return numbers.reduce((total, n) => total + n, 0);
}
sum(1, 2, 3, 4, 5); // 15
```

Rest must be the last parameter. `function(a, b, ...rest)` is valid. `function(...rest, a)` is a SyntaxError.

---

**[42:00–50:00] ES Modules**

ES Modules are how modern JavaScript organizes code across files. Every module has its own scope — nothing leaks to the global namespace.

Two export types. Named exports — multiple per file:
```javascript
// math.js
export const PI = 3.14159;
export function add(a, b)      { return a + b; }
export function multiply(a, b) { return a * b; }
```

Default export — one per file:
```javascript
// utils.js
export default function formatDate(date) {
  return date.toLocaleDateString("en-US");
}
```

Import variations:
```javascript
import { add, multiply, PI }   from "./math.js";     // named
import { add as addNums }      from "./math.js";     // rename
import * as MathUtils          from "./math.js";     // namespace
import formatDate              from "./utils.js";    // default
import formatDate, { add }     from "./module.js";   // both
```

Dynamic import — this is how lazy loading works:
```javascript
async function loadChart() {
  const { renderChart } = await import("./chart.js");
  renderChart(data);
}
```

`import()` returns a Promise. The module is only downloaded when that code runs. This is how React and Angular achieve code splitting — they wrap heavy components in dynamic imports so the browser only downloads what's actually needed.

In HTML, you need `<script type="module" src="app.js">` — modules are deferred by default and have their own scope.

A quick note on CommonJS — the older Node.js system: `const { add } = require("./math")` and `module.exports = { add }`. You'll see this in older Node.js code and npm packages. The two systems can coexist but can't directly import each other without configuration.

When you use React or Angular, a bundler — Vite or the Angular CLI — takes all your modules and produces optimized bundles. Tree shaking removes unused exports from the final bundle. Code splitting puts heavy chunks in separate files loaded on demand. You configure almost none of this manually; the frameworks handle it. But knowing the terms helps you understand build output and diagnose build-time errors.

---

**[50:00–56:00] Map and Set**

Two more data structures that ES6 added. You'll reach for these more as your applications get more complex.

`Map` is a key-value store — like a plain object — but with important upgrades. First: the key can be ANY type. A string, a number, a boolean, an object, even a DOM element. Plain objects only support string or Symbol keys. Second: iteration order is guaranteed to match insertion order. Third: `.size` gives you the count directly. Fourth: no prototype pollution — a Map has no inherited properties.

```javascript
const map = new Map([
  ["host", "localhost"],
  ["port", 5432]
]);
map.get("port");   // 5432
map.has("host");   // true
map.set("ssl", false);
map.size;          // 3
for (const [key, value] of map) {
  console.log(`${key}: ${value}`);
}
```

When do you use Map over a plain object? When your keys aren't strings. DOM element as a key — tracking click counts per element. Class instances as keys — a cache keyed by request objects. When you need guaranteed insertion-order iteration. When the collection changes frequently and you want clean add/remove semantics.

`Set` is a collection of unique values. Adding a duplicate is silently ignored.

```javascript
const set = new Set([1, 2, 3, 2, 1]); // {1, 2, 3}
set.add(4);
set.has(1);    // true
set.size;      // 4
```

The killer use case: `[...new Set(array)]` — one line to de-duplicate an array while preserving insertion order.

Performance: `set.has(value)` is O(1). `array.includes(value)` is O(n). For checking whether a large set of values contains a given value, Set is dramatically faster.

---

**[56:00–60:00] Advanced ES6+ Survey and Summary**

Last section — a rapid survey of more advanced ES6+ features. These appear in framework source code and libraries. Know what they are; depth comes with use.

Symbol: a primitive type that produces a guaranteed-unique value. `const id = Symbol("id")`. You can use it as a property key that won't collide with anything else. Well-known symbols like `Symbol.iterator` let you make objects work with `for...of` loops and spread.

Generator functions: pauseable functions using `function*` and `yield`. They produce values lazily on demand. An infinite Fibonacci sequence, a paginated data fetcher, a turn-based game state machine — generators model these elegantly.

WeakMap and WeakSet: like Map and Set but keys must be objects, and references are held weakly — meaning if nothing else references the key object, the garbage collector can clean it up. Used for caching computed data about DOM elements, storing private data that should disappear when the object does.

Proxy: lets you intercept any operation on an object — get, set, delete, function calls. You define a handler with trap methods. Vue 3's entire reactivity system is built on Proxy. React's Immer library uses Proxy to enable writing "mutations" that are actually immutable updates.

Reflect: a companion to Proxy. Provides a clean, functional API for performing object operations programmatically.

These are advanced tools. You won't use them on Day 1. But when you see them in Angular's decorator metadata system, or in a Redux middleware, or in Vue's reactivity internals, you'll know what you're looking at.

Let me do a quick summary: JavaScript OOP is prototypal. Classes are clean sugar over the prototype system. Inheritance uses `extends` and `super`. Private fields with `#` are genuinely enforced. For multiple behaviors, use mixins or composition. Default parameters, destructuring, spread/rest, and enhanced object literals appear in virtually every modern JS file. ES Modules organize code into file-scoped units; bundlers optimize them. Map for ordered key-value with any key type; Set for unique values with O(1) membership.

Part 2 is Async JavaScript. We'll go from callbacks through Promises to async/await, and then build real HTTP requests with the Fetch API. See you in a few minutes.

---

*[END OF PART 1 — 60 MINUTES]*
