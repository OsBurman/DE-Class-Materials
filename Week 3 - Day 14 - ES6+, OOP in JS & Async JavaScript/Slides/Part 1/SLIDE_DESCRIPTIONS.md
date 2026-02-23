# Week 3 - Day 14 (Thursday): ES6+, OOP in JS & Async JavaScript
## Part 1: OOP in JavaScript and ES6+ Features — Slide Descriptions

**Section Goal:** Bridge students from DOM-manipulation JavaScript to the deeper language features that power modern frameworks — prototypal inheritance, ES6 classes, destructuring, modules, Map/Set, and a survey of advanced ES6+ capabilities.

**Part 1 Learning Objectives:**
- Implement object-oriented patterns in JavaScript using both constructor functions and ES6 class syntax
- Understand and explain the prototype chain
- Apply ES6+ syntax features: destructuring, spread/rest, default parameters, enhanced object literals
- Organize code with ES Modules (import/export)
- Work with Map and Set data structures
- Recognize advanced ES6+ features: Symbol, generators, WeakMap/WeakSet, Proxy, Reflect

---

## Slide 1: Title Slide

**Title:** ES6+, OOP in JavaScript & Async Programming
**Subtitle:** Part 1: Prototypal Inheritance, Classes, and Modern Language Features
**Day:** Week 3 - Day 14 (Thursday)
**Notes for instructor:** Connect to Day 13 — yesterday we used event handlers, closures, and the `class`-like to-do app pattern. Today we go under the hood.

---

## Slide 2: JavaScript OOP — It's Prototypal, Not Classical

**Title:** JavaScript OOP — Objects Inherit from Objects

**Content:**
Coming from Java, you expect classes to be the fundamental building block of OOP. JavaScript is different. JavaScript is a **prototype-based** language — objects can directly inherit properties and methods from other objects, without a class acting as a blueprint in between.

This is not a limitation — it's a different (and in some ways more flexible) model. Understanding it explains every quirk of JavaScript OOP.

**The key conceptual difference:**
| Java / C# (Classical) | JavaScript (Prototypal) |
|---|---|
| Classes are blueprints | Objects are templates |
| `new` instantiates a class | `new` links to a prototype object |
| Inheritance through class hierarchy | Inheritance through prototype chain |
| Compile-time class structure | Runtime object delegation |

**The critical fact:** ES6 `class` syntax is **syntactic sugar** over JavaScript's prototype system. The underlying mechanism is still prototypal. You can verify this:

```javascript
class Animal { speak() { console.log("..."); } }

typeof Animal              // "function" — classes ARE functions
typeof Animal.prototype    // "object"
Animal.prototype.speak     // the speak method lives here — not copied to every instance
```

**Why methods live on the prototype (not on instances):**
If every `new Animal()` instance had its own copy of `speak()`, you'd have 1,000 function copies in memory for 1,000 instances. With the prototype, there's ONE `speak()` shared by all instances. This is the memory efficiency model JavaScript was designed around.

**Connection forward:** TypeScript (Day 15) adds static types to these exact class patterns. React class components (Week 4) use ES6 class syntax. Angular components use TypeScript classes with decorators.

---

## Slide 3: The Prototype Chain — How Property Lookup Works

**Title:** The Prototype Chain — Every Object Has a Parent

**Content:**
When you access a property on an object, JavaScript doesn't just check the object itself — it walks the **prototype chain**:

1. Check own properties of the object
2. Check the object's `[[Prototype]]` (its parent object)
3. Check the prototype's prototype
4. Continue up until `Object.prototype`
5. If still not found: return `undefined`

**Visualizing the chain:**
```
dog instance
  └─ [[Prototype]] → Dog.prototype       (has: learn(), #tricks)
       └─ [[Prototype]] → Animal.prototype  (has: speak())
            └─ [[Prototype]] → Object.prototype  (has: toString(), hasOwnProperty(), ...)
                 └─ [[Prototype]] → null          (end of chain)
```

**Code demonstrating prototype-based inheritance:**
```javascript
const animal = {
  speak() { console.log(`${this.name} makes a sound`); }
};

const dog = Object.create(animal);  // dog's [[Prototype]] IS the animal object
dog.name = "Rex";

dog.speak();  // NOT on dog → looks up chain → found on animal: "Rex makes a sound"

Object.getPrototypeOf(dog) === animal;  // true
dog.hasOwnProperty("name");    // true  — name is an own property
dog.hasOwnProperty("speak");   // false — speak is inherited
```

**Key methods for working with prototypes:**
```javascript
Object.getPrototypeOf(obj)          // read the prototype (preferred)
Object.setPrototypeOf(obj, proto)   // set prototype (avoid — slow)
Object.create(proto)                // create object with given prototype
Object.create(null)                 // no prototype at all — pure map
obj.isPrototypeOf(other)            // check if obj is in other's chain
```

**⚠️ Never modify `Object.prototype`:** Every object in the entire runtime inherits from `Object.prototype`. Adding properties to it breaks the whole page. This is called prototype pollution and is a genuine security vulnerability.

---

## Slide 4: Constructor Functions — The Pre-ES6 OOP Pattern

**Title:** Constructor Functions — Before ES6 Classes

**Content:**
Before `class` syntax (ES6, 2015), JavaScript OOP used **constructor functions**. You will encounter this pattern in legacy code, library internals, Node.js modules, and technical interviews. Understanding it makes `class` syntax completely transparent.

**Constructor function pattern:**
```javascript
// Convention: constructor functions start with uppercase (like classes)
function Animal(name, sound) {
  this.name = name;    // own property — copied to each instance
  this.sound = sound;
}

// Methods go on the prototype — shared by ALL instances
Animal.prototype.speak = function() {
  console.log(`${this.name} says ${this.sound}`);
};

Animal.prototype.toString = function() {
  return `Animal(${this.name})`;
};
```

**What `new` does — four steps:**
1. Creates a new empty object `{}`
2. Sets the object's `[[Prototype]]` to `Animal.prototype`
3. Executes `Animal` with `this` = the new empty object
4. Returns the new object (unless the function explicitly returns a different object)

```javascript
const dog = new Animal("Rex", "woof");
dog.speak();           // "Rex says woof"
dog instanceof Animal; // true
```

**Inheritance before `class` — verbose but functional:**
```javascript
function Dog(name) {
  Animal.call(this, name, "woof"); // call parent constructor with this
  this.tricks = [];
}
// Wire up the prototype chain
Dog.prototype = Object.create(Animal.prototype);
Dog.prototype.constructor = Dog; // restore the constructor reference
```

This works, but it's verbose, error-prone, and cryptic for newcomers. This exact frustration drove the `class` syntax proposal. Reading old JavaScript — jQuery plugins, Backbone.js, early Angular 1 — means reading constructor function OOP.

---

## Slide 5: ES6 Class Syntax — Cleaner OOP

**Title:** ES6 Classes — Same Prototype System, Better Syntax

**Content:**
ES6 classes make JavaScript OOP readable for developers from Java, C#, and Python. Remember: the underlying mechanism is still prototypal. Classes are functions. Methods live on the prototype.

**Full class anatomy:**
```javascript
class Animal {
  // Private field declaration (ES2022) — must declare before use
  #sound;

  constructor(name, sound) {
    this.name = name;      // public instance property
    this.#sound = sound;   // private field
  }

  // Instance method — lives on Animal.prototype (shared by all instances)
  speak() {
    console.log(`${this.name} says ${this.#sound}`);
  }

  // Getter — access like a property, no ()
  get description() {
    return `${this.name} (${this.#sound})`;
  }

  // Setter — validate before assignment
  set sound(value) {
    if (typeof value !== "string") throw new TypeError("Sound must be a string");
    this.#sound = value;
  }

  // Static method — on the class itself, not on instances
  static create(name, sound) {
    return new Animal(name, sound);
  }
}

const cat = new Animal("Whiskers", "meow");
cat.speak();                // "Whiskers says meow"
cat.description;            // "Whiskers (meow)" — getter
cat.sound = "purr";         // setter — validates
Animal.create("Dog", "woof"); // static factory method
cat.#sound;                 // SyntaxError — private
```

**Important differences from Java classes:**
- Classes are NOT hoisted — a class can't be used before its definition (unlike function declarations)
- Class bodies execute in **strict mode** automatically
- The class itself is a function — `typeof Animal === "function"`
- There is NO method overloading — last definition wins

---

## Slide 6: Inheritance — extends and super

**Title:** Inheritance with extends and super

**Content:**
`extends` sets up the prototype chain in one keyword. `super` calls the parent class.

```javascript
class Dog extends Animal {
  #tricks;

  constructor(name) {
    super(name, "woof");  // MUST call super() before using 'this' in derived class
    this.#tricks = [];
  }

  learn(trick) {
    this.#tricks.push(trick);
    return this;           // method chaining — returns the instance
  }

  // Override parent's speak()
  speak() {
    super.speak();         // "Rex says woof"
    console.log(`${this.name} wags its tail happily.`);
  }

  get trickList() {
    return [...this.#tricks]; // return a copy, not the original
  }
}

const dog = new Dog("Rex");
dog.learn("sit").learn("shake").learn("roll over"); // method chaining

dog instanceof Dog;     // true
dog instanceof Animal;  // true — Dog IS an Animal

dog.speak();
// "Rex says woof"
// "Rex wags its tail happily."
```

**`super` rules:**
- In a derived class constructor: `super()` must be called before any `this` access
- In a derived class method: `super.method()` calls the parent class's version
- Multi-level: `super()` in Dog calls Animal's constructor; Animal could also extend from a base class

**Multi-level inheritance:**
```javascript
class ServiceDog extends Dog {
  #role;
  constructor(name, role) {
    super(name);   // calls Dog constructor → which calls Animal constructor
    this.#role = role;
  }
}
```

**When to STOP inheriting:** More than 2–3 levels of inheritance usually signals a design problem. Deep hierarchies are rigid — changing a parent class breaks all descendants. Prefer composition: build objects from smaller, focused pieces.

---

## Slide 7: Static Members and Private Fields

**Title:** Static Members and Private Fields — Class-Level State and Encapsulation

**Content:**
**Static members** belong to the CLASS itself, not to any instance. Called on the class name, not on objects.

```javascript
class Counter {
  static #count = 0;           // private static field
  static maxInstances = 100;   // public static field

  constructor(label) {
    Counter.#count++;
    if (Counter.#count > Counter.maxInstances) {
      throw new Error("Too many Counter instances");
    }
    this.id    = Counter.#count;
    this.label = label;
  }

  static getCount() { return Counter.#count; }
  static reset()    { Counter.#count = 0; }
}

const a = new Counter("a"); // a.id = 1
const b = new Counter("b"); // b.id = 2
Counter.getCount();          // 2 — called on class
a.getCount();                // TypeError — not available on instances
```

**Private fields `#` — truly encapsulated:**
```javascript
class BankAccount {
  #balance = 0;  // private — only accessible inside this class body

  deposit(amount) {
    if (amount <= 0) throw new RangeError("Deposit must be positive");
    this.#balance += amount;
  }

  withdraw(amount) {
    if (amount > this.#balance) throw new Error("Insufficient funds");
    this.#balance -= amount;
  }

  get balance() { return this.#balance; } // controlled read access
}

const acct = new BankAccount();
acct.deposit(100);
console.log(acct.balance);  // 100 — via getter
acct.#balance = 9999;       // SyntaxError — genuinely enforced by the engine
```

**`#private` vs `_private` (old convention):**
Before private fields, developers used `_balance` (underscore prefix) as a social convention meaning "don't touch this." There was nothing enforcing it — any code could still read or write `obj._balance`. The `#` syntax is engine-enforced. You get a `SyntaxError` at parse time, not a silent data corruption bug at runtime.

---

## Slide 8: OOP Patterns — Mixins and Composition

**Title:** OOP Patterns — Beyond Single Inheritance

**Content:**
JavaScript classes support **single inheritance** only — one `extends` per class. For behavior that spans multiple unrelated class hierarchies, two patterns help.

**Mixins — inject behavior via Object.assign:**
```javascript
const Serializable = {
  serialize()            { return JSON.stringify(this); },
  toJSON()               { return { ...this }; }
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

// Mix in behaviors after class definition
Object.assign(User.prototype, Serializable, Validatable);

const user = new User("Alice", "alice@example.com");
user.serialize();  // '{"name":"Alice","email":"alice@example.com"}'
user.validate();   // true
```

**Composition with factory functions — build from pieces:**
```javascript
// No classes at all — just objects assembled from focused factory functions
function createLogger(prefix) {
  return { log: (msg) => console.log(`[${prefix}] ${msg}`) };
}
function createStorage(key) {
  return {
    save:  (data) => localStorage.setItem(key, JSON.stringify(data)),
    load:  ()     => JSON.parse(localStorage.getItem(key) || "null")
  };
}

function createUserService() {
  const logger  = createLogger("UserService");
  const storage = createStorage("currentUser");
  return { logger, storage, /*...*/ };
}
```

**The "composition over inheritance" principle:**
Deep class hierarchies are rigid — a change to a parent class can silently break all descendants. Composing behavior from small, focused, independently-testable pieces produces more flexible and maintainable code. React functional components embrace this: compose small components and custom hooks, not class hierarchies.

---

## Slide 9: Default Parameters and Enhanced Object Literals

**Title:** Default Parameters and Enhanced Object Literals

**Content:**
Two ES6 features that appear in virtually every modern JavaScript file.

**Default parameters:**
```javascript
// Pre-ES6 — brittle (falsy values like "" or 0 trigger the default unintentionally)
function greet(name, greeting) {
  name     = name     || "World";
  greeting = greeting || "Hello";
  return `${greeting}, ${name}!`;
}

// ES6 — explicit and safe
function greet(name = "World", greeting = "Hello") {
  return `${greeting}, ${name}!`;
}

greet();                  // "Hello, World!"
greet("Alice");           // "Hello, Alice!"
greet("Bob", "Hi");       // "Hi, Bob!"
greet(undefined, "Hey");  // "Hey, World!" — undefined triggers default
greet(null, "Hey");       // "Hey, null!"  — null does NOT trigger default
```

**Defaults can be any expression:**
```javascript
function makeId(prefix = "user", ts = Date.now()) {
  return `${prefix}_${ts}`;
}
function createEl(tag = "div", parent = document.body) { /* ... */ }
```

**Enhanced object literals — three shortcuts:**
```javascript
const name = "Alice";
const age  = 30;

// 1. Property shorthand: { name: name } → { name }
const user = { name, age };

// 2. Method shorthand: greet: function() {} → greet() {}
const service = {
  greet()        { return `Hello, ${this.name}`; },
  async getData() { return await fetch("/api/data").then(r => r.json()); }
};

// 3. Computed property names — dynamic keys from expressions
const field = "email";
const update = {
  [field]:           "alice@example.com",   // { email: "alice@..." }
  [`prev_${field}`]: "old@example.com",     // { prev_email: "old@..." }
  [`on${field[0].toUpperCase() + field.slice(1)}`]: handler // { onEmail: handler }
};
```

These patterns appear constantly: React props, Redux action creators, Angular services, API response shaping.

---

## Slide 10: Destructuring — Unpacking Values Elegantly

**Title:** Destructuring — Objects and Arrays

**Content:**
Destructuring assigns values from objects or arrays to named variables in a single, readable statement.

**Object destructuring:**
```javascript
const user = { name: "Alice", age: 30, role: "admin", city: "NYC" };

// Basic extraction
const { name, age } = user;

// Rename: { originalKey: newVariableName }
const { name: userName, role: userRole } = user;

// Default values (if property is undefined)
const { name, score = 0, tier = "free" } = user;

// Nested destructuring
const config = { db: { host: "localhost", port: 5432 } };
const { db: { host, port } } = config;

// Rest — collect remaining keys into a new object
const { name: n, ...rest } = user;  // rest = { age: 30, role: "admin", city: "NYC" }
```

**Array destructuring:**
```javascript
const colors = ["red", "green", "blue", "yellow"];

const [first, second]          = colors;  // "red", "green"
const [, , third]              = colors;  // skip elements — "blue"
const [head, ...tail]          = colors;  // head="red", tail=["green","blue","yellow"]

// Swap variables — no temp variable needed
let a = 1, b = 2;
[a, b] = [b, a];  // a=2, b=1

// Function returning multiple values
function minMax(arr) {
  return [Math.min(...arr), Math.max(...arr)];
}
const [min, max] = minMax([3, 1, 4, 1, 5, 9]);
```

**Function parameter destructuring — the most practical use:**
```javascript
// Instead of: function render(opts) { const { color, size } = opts; }
function render({ color = "black", size = 16, label }) {
  console.log(`${label}: ${size}px in ${color}`);
}
render({ label: "Title", color: "red" }); // size defaults to 16

// API response unpacking
async function loadUser(id) {
  const { name, email, posts } = await fetchUser(id);
}
```

**Real-world patterns:**
```javascript
const [count, setCount]          = useState(0);       // React hook — array
const { data, loading, error }   = useQuery(GET_USER); // React query — object
const { params: { id } }         = useRouteMatch();    // Router — nested
```

---

## Slide 11: Spread and Rest Operators

**Title:** Spread ... and Rest ... — Same Symbol, Opposite Roles

**Content:**
Both use `...` but operate in opposite directions:
- **Spread:** expands a collection into individual elements (from one → many)
- **Rest:** collects individual elements into a collection (from many → one)

**Spread with arrays:**
```javascript
const fruits = ["apple", "banana"];
const vegs   = ["carrot", "pea"];

const food       = [...fruits, ...vegs];             // combine
const copy       = [...fruits];                      // shallow copy
const withMango  = ["mango", ...fruits, "kiwi"];     // insert at any position

// Spread into function arguments
Math.max(...[1, 5, 3, 9, 2]);  // same as Math.max(1, 5, 3, 9, 2)
```

**Spread with objects:**
```javascript
const defaults = { theme: "light", lang: "en", fontSize: 16 };
const overrides = { theme: "dark", fontSize: 18 };

// Later spread wins for duplicate keys — overrides replace defaults
const config = { ...defaults, ...overrides };
// { theme: "dark", lang: "en", fontSize: 18 }

// Adding / overriding one property
const updated = { ...user, lastLogin: new Date() };
```

**⚠️ Spread is SHALLOW — nested objects are still references:**
```javascript
const original = { name: "Alice", address: { city: "NYC" } };
const copy     = { ...original };

copy.name         = "Bob"; // safe — primitive, own property on copy
copy.address.city = "LA";  // MUTATES original.address.city too!
// Both original and copy share the SAME address object
```
For deep cloning: `structuredClone(obj)` (modern) or `JSON.parse(JSON.stringify(obj))` (strings/numbers only).

**Rest in function parameters:**
```javascript
function sum(...numbers) {
  return numbers.reduce((total, n) => total + n, 0);
}
sum(1, 2, 3, 4, 5); // 15

// Mix regular params with rest — rest must be last
function log(level, ...messages) {
  messages.forEach(msg => console.log(`[${level}] ${msg}`));
}
```

**Rest in destructuring:**
```javascript
const [first, second, ...rest] = [1, 2, 3, 4, 5];
// first=1, second=2, rest=[3,4,5]

const { name, email, ...metadata } = user;
// Separates core fields from optional metadata
```

---

## Slide 12: ES Modules — import and export

**Title:** ES Modules — Organizing Code Across Files

**Content:**
ES Modules (ESM) are the official JavaScript standard for splitting code across files. Each module has its own scope — no accidental global variables.

**Named exports — multiple per file:**
```javascript
// math.js
export const PI = 3.14159;
export function add(a, b)      { return a + b; }
export function multiply(a, b) { return a * b; }
export class Calculator {
  constructor() { this.history = []; }
  compute(a, op, b) { /* ... */ }
}
```

**Default export — one per file:**
```javascript
// utils.js
export default function formatDate(date) {
  return date.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
}
```

**Import syntax — all variations:**
```javascript
import { add, multiply, PI }    from "./math.js"; // named imports
import { add as addNums }       from "./math.js"; // rename on import
import * as MathUtils           from "./math.js"; // namespace import
import formatDate               from "./utils.js"; // default import
import formatDate, { add, PI }  from "./module.js"; // both default and named
```

**Dynamic import — lazy loading (returns a Promise):**
```javascript
// Only load the chart library when actually needed
async function showChart(data) {
  const { renderChart } = await import("./chart.js");
  renderChart(data);
}

// Or: load analytics module only after user interaction
button.addEventListener("click", async () => {
  const { trackEvent } = await import("./analytics.js");
  trackEvent("settings_opened");
});
```

**In the browser — `type="module"`:**
```html
<script type="module" src="app.js"></script>
<!-- type="module" scripts are automatically deferred — no defer attribute needed -->
<!-- Module scope is strict mode by default -->
<!-- Each file is fetched once, cached, and shared -->
```

**CommonJS comparison (Node.js / older code you'll see):**
```javascript
// CommonJS (Node.js default before v12)
const { add } = require("./math");
module.exports = { add, multiply };

// ESM in Node.js — use .mjs extension or "type": "module" in package.json
import { add } from "./math.js";
```

---

## Slide 13: Module Bundling — From Development to Production

**Title:** Module Bundling — What Happens at Build Time

**Content:**
In development, modern browsers load ES modules directly. In production, a **bundler** combines, optimizes, and transforms your modules into one or more output files.

**Why bundle?**
- **Fewer HTTP requests** — one bundle file vs hundreds of individual module files
- **Transpilation** — convert modern JS (ES2022) to browser-compatible JS (Babel)
- **Tree shaking** — remove exports that are never imported (dead code elimination)
- **Minification** — strip whitespace, shorten variable names, compress output
- **Code splitting** — create multiple chunks, load only what the current page needs

**Tree shaking example:**
```javascript
// math.js — three exports
export function add(a, b)      { return a + b; }
export function multiply(a, b) { return a * b; }
export function subtract(a, b) { return a - b; }

// app.js — only uses add
import { add } from "./math.js";
add(2, 3);

// After bundling with tree shaking:
// multiply and subtract are NOT in the bundle — dead code eliminated
```

**Major bundlers:**
| Bundler | Typical Usage | Characteristic |
|---|---|---|
| **Webpack** | Create React App (legacy), enterprise | Most configurable, slower |
| **Vite** | React (Vite template), Angular 17+ | Very fast dev server (esbuild) |
| **Rollup** | Library authoring | Best tree-shaking |
| **esbuild** | Used inside Vite/other tools | Fastest raw compilation |

**What you'll see in practice:** When you run `npm run build` in a React (Vite) or Angular project next week, a bundler runs. The output `dist/` folder will contain minified `.js` chunks. When you see filenames like `main.abc123.js`, that's a content-hashed chunk for cache busting. You won't configure bundlers directly — frameworks handle it — but understanding their output makes debugging production issues much easier.

---

## Slide 14: Map — Key-Value Pairs Done Right

**Title:** Map — When Plain Objects Aren't Enough

**Content:**
A `Map` stores ordered key-value pairs with significant advantages over plain objects.

**Map vs plain object:**
| Feature | Plain Object | Map |
|---|---|---|
| Key types | String or Symbol only | **ANY type** — objects, DOM elements, functions |
| Key order | Not guaranteed (numeric keys sort) | **Insertion order** guaranteed |
| Size | `Object.keys(obj).length` | `map.size` |
| Prototype keys | Inherits from Object.prototype | None — clean by default |
| JSON support | `JSON.stringify()` | Needs conversion first |

**Core API:**
```javascript
const map = new Map();

map.set("name", "Alice");          // string key
map.set(42, "the answer");         // number key
map.set(true, "boolean key");      // boolean key

const el = document.querySelector("#btn");
map.set(el, { clicks: 0 });        // DOM element as key!
map.set({ id: 1 }, "user data");   // object as key

map.get("name");   // "Alice"
map.has(42);       // true
map.delete(true);
map.size;          // current count
map.clear();       // empty all entries
```

**Initialization and iteration:**
```javascript
const config = new Map([
  ["host", "localhost"],
  ["port", 5432],
  ["ssl",  false]
]);

// for...of preserves insertion order
for (const [key, value] of config) {
  console.log(`${key}: ${value}`);
}

config.forEach((value, key) => console.log(key, "→", value)); // note: value first in forEach

// Convert to plain object
const obj = Object.fromEntries(config); // { host: "localhost", port: 5432, ssl: false }

// Convert plain object to Map
const m = new Map(Object.entries(obj));
```

**When to prefer Map over Object:**
- Keys are not strings (DOM elements, class instances, functions as keys)
- Guaranteed insertion-order iteration is needed
- Frequently adding/removing entries
- Need `map.size` without extra computation
- No prototype pollution risk needed

---

## Slide 15: Set — Collections of Unique Values

**Title:** Set — Unique Values and Efficient Membership

**Content:**
A `Set` is a collection of **unique values** — adding a duplicate is silently ignored.

**Core API:**
```javascript
const set = new Set();

set.add(1);
set.add(2);
set.add(2);      // ignored — already present
set.add("two");
set.add({ x: 1 }); // objects by reference — each {} is unique

set.has(1);      // true
set.has(3);      // false
set.delete(1);
set.size;        // 3
set.clear();
```

**Initialize from any iterable:**
```javascript
const nums = new Set([1, 2, 3, 2, 1]); // Set { 1, 2, 3 } — duplicates removed
```

**The most common use case — de-duplication:**
```javascript
const tags = ["js", "react", "js", "typescript", "react", "js"];
const uniqueTags = [...new Set(tags)];
// ["js", "react", "typescript"] — order preserved, duplicates removed
```

**Set operations (manual — no operators yet in most runtimes):**
```javascript
const a = new Set([1, 2, 3, 4]);
const b = new Set([3, 4, 5, 6]);

const union        = new Set([...a, ...b]);                    // {1,2,3,4,5,6}
const intersection = new Set([...a].filter(x => b.has(x)));   // {3,4}
const difference   = new Set([...a].filter(x => !b.has(x)));  // {1,2}
const symmetric    = new Set(
  [...a].filter(x => !b.has(x)).concat([...b].filter(x => !a.has(x)))
);
```

**Iteration:**
```javascript
const roles = new Set(["admin", "editor", "viewer"]);
for (const role of roles) { console.log(role); }   // insertion order
[...roles].map(r => r.toUpperCase());               // spread to array, then map
Array.from(roles);                                  // convert to array
```

**Performance advantage:** `set.has(value)` runs in O(1) average time. `array.includes(value)` runs in O(n). For membership checks on large collections, Set is dramatically faster.

**WeakSet preview:** Same idea as WeakMap — only objects as values, weakly held, no iteration. Used to mark objects without preventing garbage collection.

---

## Slide 16: Advanced ES6+ — Know They Exist

**Title:** Advanced ES6+ Features — A Guided Survey

**Content:**
The following features appear in framework source code, libraries, and interviews. Day 14 gives you awareness; deeper study comes with use.

**Symbol — guaranteed unique primitive values:**
```javascript
const id  = Symbol("id");    // new unique symbol
const id2 = Symbol("id");    // different symbol despite same description
id === id2;                  // false — always unique

const user = { name: "Alice" };
user[id] = 123;              // Symbol property — won't collide with string properties
user[id];                    // 123
"id" in user;                // false — Symbol key is distinct from string "id"

// Well-known symbols — customize class behavior
class Range {
  constructor(from, to) { this.from = from; this.to = to; }
  [Symbol.iterator]() {     // makes Range work with for...of and spread
    let current = this.from;
    const last  = this.to;
    return { next() {
      return current <= last
        ? { value: current++, done: false }
        : { value: undefined, done: true };
    }};
  }
}
[...new Range(1, 5)]; // [1, 2, 3, 4, 5]
```

**Generator functions — lazy, pauseable sequences:**
```javascript
function* fibonacci() {
  let [a, b] = [0, 1];
  while (true) {
    yield a;             // pause execution, return a
    [a, b] = [b, a + b];
  }
}
const fib = fibonacci();
fib.next().value; // 0
fib.next().value; // 1
fib.next().value; // 1
fib.next().value; // 2
// Infinite sequence — only computes values on demand
```

**WeakMap and WeakSet — GC-friendly associations:**
```javascript
const cache = new WeakMap();
// If the DOM element is removed, this entry is automatically garbage-collected
cache.set(domElement, { computedValue: expensiveCalc() });
// WeakMap: no size, no iteration, object keys only — used for private data, caching
// WeakSet: track visited objects without preventing GC
```

**Proxy — intercept any object operation:**
```javascript
const handler = {
  set(target, prop, value) {
    if (prop === "age" && !Number.isInteger(value)) throw new TypeError("Age must be integer");
    target[prop] = value;
    return true;
  },
  get(target, prop) {
    return prop in target ? target[prop] : `Property '${prop}' not found`;
  }
};
const person = new Proxy({}, handler);
person.age = 30;      // ok
person.age = "old";   // TypeError
person.name;          // "Property 'name' not found"
```
Vue 3's reactivity system is entirely built on Proxy. React's Immer library (used in Redux Toolkit) uses Proxy for immutable state.

**Reflect — clean, programmatic object operations:**
```javascript
Reflect.has(obj, "key");         // same as "key" in obj
Reflect.ownKeys(obj);            // all own keys including Symbol keys
Reflect.deleteProperty(obj, k);  // programmatic delete
// Used alongside Proxy to forward operations to the target object cleanly
```

---

## Slide 17: Part 1 Summary + Part 2 Preview

**Title:** Part 1 Summary — OOP and ES6+ Foundations

**Content:**

**What we covered:**
- JavaScript OOP is **prototypal** — objects inherit from objects via the prototype chain. `class` syntax is sugar over this mechanism.
- **Constructor functions** — the pre-ES6 pattern. Still found in legacy code, libraries, and interviews.
- **ES6 classes** — clean OOP: `constructor`, instance methods on the prototype, `extends`/`super`, `static`, `#private` fields.
- **Single inheritance only** → use **mixins** (`Object.assign`) or **composition** for multiple behaviors.
- **Default parameters** — safe fallbacks; `undefined` triggers them, `null` does not.
- **Destructuring** — clean extraction from objects and arrays, with renaming, defaults, nesting, and rest.
- **Spread** — expands; **rest** — collects. Both are **shallow** for objects.
- **ES Modules** — named and default exports, dynamic `import()`, module scope.
- **Bundlers** — Webpack, Vite, Rollup combine and optimize modules for production.
- **Map** — ordered key-value pairs, any key type. **Set** — unique values, O(1) membership.
- **Advanced survey** — Symbol (unique keys), generators (lazy sequences), WeakMap/WeakSet (GC-friendly), Proxy (intercept operations), Reflect (clean OOP operations).

**Part 2 preview — Async JavaScript:**
JavaScript is single-threaded, yet it handles file I/O, HTTP requests, timers, and events without freezing. Part 2 explains exactly how: the event loop, callback model, Promises, async/await, and the Fetch API.

**Day 15 (tomorrow) — TypeScript:** Today's classes become type-safe. Interfaces, generics, type annotations, decorators, and `tsconfig.json`.
