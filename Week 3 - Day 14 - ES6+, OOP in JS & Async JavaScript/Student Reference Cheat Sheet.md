# Day 14 — ES6+, OOP in JavaScript & Async JavaScript
## Quick Reference Guide

---

## 1. Destructuring

```js
// Array destructuring
const [a, b, c] = [1, 2, 3];
const [first, , third] = [1, 2, 3];        // skip element
const [x = 10, y = 20] = [5];             // default values → x=5, y=20
const [head, ...tail] = [1, 2, 3, 4];     // rest → tail=[2,3,4]

// Swap variables
[a, b] = [b, a];

// Object destructuring
const { name, age } = user;
const { name: fullName, age: years } = user;     // rename
const { city = "Unknown" } = user;               // default value
const { address: { zip } } = user;               // nested
const { a: x2, ...rest } = obj;                  // rest

// In function params
function greet({ name, role = "guest" }) {
    return `Hello ${name}, you are a ${role}`;
}
greet({ name: "Alice", role: "admin" });
```

---

## 2. Spread & Rest

```js
// Spread — expand iterable into individual elements
const arr = [1, 2, 3];
const arr2 = [...arr, 4, 5];              // [1, 2, 3, 4, 5]
const merged = [...arr1, ...arr2];
const copy = [...arr];                    // shallow clone

const obj2 = { ...obj1, extra: true };   // shallow merge/clone
const updated = { ...user, age: 31 };    // override age

Math.max(...arr);

// Rest — collect remaining arguments into an array
function sum(first, ...rest) {
    return rest.reduce((a, b) => a + b, first);
}
sum(1, 2, 3, 4);  // 10
```

---

## 3. Classes

```js
class Animal {
    #name;                        // private field (ES2022)
    static count = 0;             // static field

    constructor(name) {
        this.#name = name;
        Animal.count++;
    }

    // Getter / Setter
    get name()         { return this.#name; }
    set name(value)    {
        if (!value) throw new Error("Name required");
        this.#name = value;
    }

    speak() { return `${this.#name} speaks`; }

    toString() { return `Animal(${this.#name})`; }

    static create(name) { return new Animal(name); }
}

class Dog extends Animal {
    #breed;

    constructor(name, breed) {
        super(name);              // must call before using 'this'
        this.#breed = breed;
    }

    speak() {
        return super.speak() + " — woof!";
    }
}

const dog = new Dog("Rex", "Husky");
dog.speak();              // "Rex speaks — woof!"
dog instanceof Dog;       // true
dog instanceof Animal;    // true
Animal.count;             // 1
Animal.create("Cat");     // new Animal("Cat")
```

---

## 4. Map & Set

```js
// Map — ordered key-value; any type as key
const map = new Map();
map.set("key", "value");
map.set(42, "number key");
map.set({}, "object key");
map.get("key");              // "value"
map.has("key");              // true
map.delete("key");
map.size;                    // number of entries
map.clear();

for (const [key, val] of map) { ... }
map.keys();    map.values();    map.entries();

// Create from array
const map2 = new Map([["a", 1], ["b", 2]]);

// Set — unique values (any type)
const set = new Set([1, 2, 2, 3, 3]);  // {1, 2, 3}
set.add(4);
set.has(2);        // true
set.delete(2);
set.size;
for (const val of set) { ... }
[...set]           // to array
new Set([...a, ...b])            // union
new Set([...a].filter(x => b.has(x)))  // intersection
new Set([...a].filter(x => !b.has(x))) // difference
```

---

## 5. Iterators & for...of / for...in

```js
// for...of — iterates VALUES (arrays, strings, Maps, Sets, generators)
for (const item of [1, 2, 3]) { ... }
for (const char of "hello") { ... }
for (const [key, val] of map) { ... }
for (const val of set) { ... }

// for...in — iterates KEYS/INDICES (plain objects, arrays)
for (const key in obj) { ... }   // includes inherited keys — use hasOwnProperty check
for (const i in arr) { ... }     // "0", "1", "2" as strings — prefer for...of for arrays
```

---

## 6. Modules (ES Modules)

```js
// Named exports — math.js
export const PI = 3.14159;
export function add(a, b) { return a + b; }
export class Calculator { ... }

// Default export — one per file
export default class UserService { ... }

// Named imports
import { PI, add } from "./math.js";
import { add as sum } from "./math.js";   // rename
import * as MathUtils from "./math.js";   // namespace

// Default import (name is chosen by importer)
import UserService from "./UserService.js";

// Both in one line
import UserService, { PI, add } from "./module.js";

// Dynamic import (lazy-loading, returns Promise)
const module = await import("./heavy.js");
module.default();
```

---

## 7. Promises

```js
// Create a Promise
const promise = new Promise((resolve, reject) => {
    setTimeout(() => {
        if (success) resolve({ data: "result" });
        else         reject(new Error("Something failed"));
    }, 1000);
});

// Consume
promise
    .then(data => console.log(data))          // on resolve
    .catch(err => console.error(err))         // on reject
    .finally(() => console.log("Done"));      // always

// Promise combinators
Promise.all([p1, p2, p3])         // resolves when ALL resolve; rejects if ANY rejects
Promise.allSettled([p1, p2, p3])  // waits for ALL; returns [{status, value/reason}]
Promise.race([p1, p2, p3])        // settles when FIRST settles (resolve or reject)
Promise.any([p1, p2, p3])         // resolves when FIRST resolves; rejects if ALL reject

// Promise.all with fetch
const [users, posts] = await Promise.all([
    fetch("/api/users").then(r => r.json()),
    fetch("/api/posts").then(r => r.json()),
]);
```

---

## 8. async / await

```js
// async function always returns a Promise
async function fetchUser(id) {
    try {
        const res  = await fetch(`/api/users/${id}`);   // pause until resolved
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const user = await res.json();
        return user;
    } catch (err) {
        console.error("fetchUser failed:", err);
        throw err;   // re-throw to let callers handle it
    }
}

// Arrow async
const getData = async (url) => {
    const res  = await fetch(url);
    return res.json();
};

// Top-level await (ES2022, in modules)
const data = await fetch("/api").then(r => r.json());

// Sequential vs parallel
// Sequential (each waits for previous)
const a = await getA();
const b = await getB();

// Parallel (both start together)
const [a, b] = await Promise.all([getA(), getB()]);
```

---

## 9. fetch API

```js
// GET
const res  = await fetch("/api/users");
const data = await res.json();

// POST
const res = await fetch("/api/users", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: "Alice", email: "alice@example.com" })
});
const created = await res.json();

// Error handling — fetch only rejects on network errors, NOT HTTP errors
if (!res.ok) throw new Error(`Request failed: ${res.status}`);

// Response methods
res.json()     // parse JSON body
res.text()     // parse as string
res.blob()     // parse as binary Blob
res.status     // 200, 404, 500, etc.
res.ok         // true if status 200–299
res.headers.get("Content-Type")
```

---

## 10. Error Handling in Async Code

```js
// async/await — use try/catch
async function loadData() {
    try {
        const res = await fetch("/api/data");
        if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`);
        return await res.json();
    } catch (err) {
        if (err.name === "AbortError") console.log("Request aborted");
        else console.error("Load failed:", err.message);
        return null;
    }
}

// Abort a fetch
const controller = new AbortController();
const timer = setTimeout(() => controller.abort(), 5000);  // 5s timeout

try {
    const res = await fetch("/api/slow", { signal: controller.signal });
} catch (err) {
    if (err.name === "AbortError") console.log("Timed out");
} finally {
    clearTimeout(timer);
}

// Unhandled promise rejection — always handle or rethrow
window.addEventListener("unhandledrejection", (event) => {
    console.error("Unhandled:", event.reason);
});
```

---

## 11. Optional Chaining & Nullish Coalescing

```js
// Optional chaining (?.) — short-circuit on null/undefined
const city  = user?.address?.city;
const first = users?.[0];
const len   = obj?.getLength?.();   // call only if method exists

// Nullish coalescing (??) — fallback only on null/undefined (not 0 or "")
const port  = config.port ?? 3000;   // 0 stays as 0
const name  = user.name ?? "Guest";

// Logical assignment
a ??= b;   // a = a ?? b  (assign b only if a is null/undefined)
a ||= b;   // a = a || b  (assign b if a is falsy)
a &&= b;   // a = a && b  (assign b if a is truthy)
```

---

## 12. Symbols, WeakMap & WeakRef

```js
// Symbol — unique, non-enumerable key
const id = Symbol("id");
const user = { [id]: 42, name: "Alice" };
user[id];                      // 42
Object.keys(user);             // ["name"] — Symbol hidden

// Well-known Symbols
class MyArray {
    [Symbol.iterator]() {
        let i = 0;
        return { next: () => ({ value: i++, done: i > 3 }) };
    }
}
[...new MyArray()]   // [0, 1, 2]

// WeakMap — keys are objects; entries GC'd when key unreachable
const cache = new WeakMap();
cache.set(domElement, computedData);

// WeakRef — hold a reference without preventing GC
const ref = new WeakRef(object);
const obj = ref.deref();   // undefined if GC'd
```
