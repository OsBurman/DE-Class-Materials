// ============================================================
// Day 14 — Part 1  |  03-modules-map-set-advanced.js
// ES Modules, Map, Set, Symbol, Iterators, Generators,
// WeakMap, WeakSet, Proxy, Reflect
// ============================================================

"use strict";

// ============================================================
// 1. ES MODULES — Overview (syntax shown in comments)
// ============================================================
// NOTE: ES Modules require either:
//   • A bundler (Webpack, Vite, Rollup) — most common in frameworks
//   • A browser with <script type="module">
//   • Node.js with .mjs extension OR "type": "module" in package.json
//
// The examples below show the syntax. Run them in a module-aware
// environment (browser devtools with type="module" or a bundler).

/*
// ---- NAMED EXPORTS ----
// math.js
export const PI = 3.14159;
export function add(a, b) { return a + b; }
export function multiply(a, b) { return a * b; }

// Batch export at end of file (preferred style)
const subtract = (a, b) => a - b;
const divide   = (a, b) => a / b;
export { subtract, divide };

// Export with alias
export { subtract as minus, divide as div };


// ---- DEFAULT EXPORT ----
// One default export per module
// calculator.js
export default class Calculator {
  add(a, b) { return a + b; }
}

// Or a function
export default function formatCurrency(amount, currency = "USD") {
  return new Intl.NumberFormat("en-US", { style: "currency", currency })
    .format(amount);
}


// ---- NAMED IMPORTS ----
import { PI, add, multiply } from "./math.js";
console.log(add(2, 3));   // 5
console.log(PI);          // 3.14159

// Import with alias
import { add as addNumbers } from "./math.js";

// Import all as namespace object
import * as MathUtils from "./math.js";
console.log(MathUtils.add(2, 3));
console.log(MathUtils.PI);


// ---- DEFAULT IMPORT ----
// The local name is up to you — no braces
import Calculator from "./calculator.js";
import formatCurrency from "./calculator.js"; // different name is fine


// ---- MIXED IMPORT (default + named) ----
// utils.js
export default function mainHelper() {}
export const VERSION = "1.0.0";

import mainHelper, { VERSION } from "./utils.js";


// ---- RE-EXPORT (barrel files / index.js pattern) ----
// index.js — bundle multiple modules into one entry point
export { add, multiply } from "./math.js";
export { default as Calculator } from "./calculator.js";
export * from "./stringUtils.js";


// ---- DYNAMIC IMPORT ---- (returns a Promise)
// Load a module lazily (code splitting, conditional loading)
async function loadHeavyModule() {
  const module = await import("./heavy-module.js");
  module.doSomethingExpensive();
}

// Conditional
if (userIsAdmin) {
  const { AdminPanel } = await import("./AdminPanel.js");
}


// ---- import.meta ----
// Available inside ES Modules — info about the current module
console.log(import.meta.url);  // file URL of this module
*/

// Module bundling overview (comment):
// Bundlers (Vite, Webpack, Rollup) resolve all imports at build time,
// concatenate them into one or more output bundles, apply tree-shaking
// (remove unused exports), and produce optimized files for the browser.
// In development you work with many small files; in production you ship
// one or a few optimized bundles.


// ============================================================
// 2. MAP
// ============================================================
// Map is a key-value collection where keys can be ANY type
// (vs plain objects where keys must be strings/Symbols)

// --- 2a. Creating and Populating ---
const userMap = new Map();

// .set(key, value) — returns the Map (chainable)
userMap.set("alice", { age: 30, role: "admin" });
userMap.set("bob",   { age: 25, role: "viewer" });
userMap.set(1, "numeric key");          // number key
userMap.set(true, "boolean key");       // boolean key

const objKey = { id: 42 };
userMap.set(objKey, "object as key");   // object key (by reference)

// Initialize from array of [key, value] pairs
const priceMap = new Map([
  ["apple",  0.99],
  ["banana", 0.59],
  ["cherry", 3.49],
]);

// --- 2b. Reading ---
console.log(userMap.get("alice"));   // { age: 30, role: "admin" }
console.log(userMap.get(1));         // "numeric key"
console.log(userMap.get(objKey));    // "object as key"
console.log(userMap.get("zzz"));     // undefined

// --- 2c. Checking / Removing ---
console.log(userMap.has("alice")); // true
console.log(userMap.has("carol")); // false
console.log(userMap.size);         // 5

userMap.delete("bob");
console.log(userMap.size);         // 4

// --- 2d. Iterating ---
for (const [key, value] of priceMap) {
  console.log(`${key}: $${value}`);
}
// apple: $0.99  banana: $0.59  cherry: $3.49

// Keys, values, entries
console.log([...priceMap.keys()]);   // ["apple", "banana", "cherry"]
console.log([...priceMap.values()]); // [0.99, 0.59, 3.49]
console.log([...priceMap.entries()]); // [["apple", 0.99], ...]

priceMap.forEach((value, key) => {
  console.log(`${key} costs $${value}`);
});

// --- 2e. Map vs Object ---
// Use Map when:
//   • Keys are not strings/Symbols
//   • You need reliable insertion order
//   • Frequent add/delete operations (Map is faster for that)
//   • You need .size easily
// Use plain Object when:
//   • JSON serialization needed (Map doesn't serialize to JSON natively)
//   • You know keys will be strings
//   • You want prototype methods

// Convert Map ↔ Object
const obj = Object.fromEntries(priceMap);
console.log(obj); // { apple: 0.99, banana: 0.59, cherry: 3.49 }

const mapBack = new Map(Object.entries(obj));
console.log(mapBack.get("apple")); // 0.99

// --- 2f. Practical: Frequency Counter ---
function countWords(text) {
  const freq = new Map();
  for (const word of text.toLowerCase().split(/\s+/)) {
    freq.set(word, (freq.get(word) ?? 0) + 1);
  }
  return freq;
}
const wordCount = countWords("the quick brown fox jumps over the lazy fox");
console.log([...wordCount.entries()].sort((a, b) => b[1] - a[1]));
// [["the", 2], ["fox", 2], ...]


// ============================================================
// 3. SET
// ============================================================
// Set stores unique values of any type — no duplicates, no keys

// --- 3a. Creating ---
const fruits = new Set(["apple", "banana", "apple", "cherry", "banana"]);
console.log(fruits); // Set { "apple", "banana", "cherry" } — duplicates removed
console.log(fruits.size); // 3

// --- 3b. Add / Has / Delete ---
fruits.add("mango");
fruits.add("apple"); // already exists — ignored silently
console.log(fruits.has("mango")); // true
console.log(fruits.has("grape")); // false

fruits.delete("banana");
console.log(fruits.size); // 3

// --- 3c. Iterating ---
for (const fruit of fruits) {
  console.log(fruit);
}

console.log([...fruits]); // convert to array
console.log(Array.from(fruits));

fruits.forEach(f => console.log(f));

// --- 3d. Common Use Cases ---

// Deduplicate an array (most common use!)
const numbers = [1, 2, 2, 3, 4, 4, 4, 5];
const unique = [...new Set(numbers)];
console.log(unique); // [1, 2, 3, 4, 5]

// Set operations (not built in, but easy to implement)
const setA = new Set([1, 2, 3, 4]);
const setB = new Set([3, 4, 5, 6]);

// Union
const union = new Set([...setA, ...setB]);
console.log([...union]); // [1, 2, 3, 4, 5, 6]

// Intersection
const intersection = new Set([...setA].filter(x => setB.has(x)));
console.log([...intersection]); // [3, 4]

// Difference (in A but not B)
const difference = new Set([...setA].filter(x => !setB.has(x)));
console.log([...difference]); // [1, 2]

// Track visited / seen items
const visited = new Set();
function processItem(id) {
  if (visited.has(id)) {
    console.log(`Skipping duplicate: ${id}`);
    return;
  }
  visited.add(id);
  console.log(`Processing: ${id}`);
}
processItem("a"); // Processing: a
processItem("b"); // Processing: b
processItem("a"); // Skipping duplicate: a


// ============================================================
// 4. SYMBOL  (brief overview)
// ============================================================
// Symbols are unique, immutable primitive values — useful as keys
// that won't accidentally clash with other code

const id1 = Symbol("id");
const id2 = Symbol("id");
console.log(id1 === id2); // false — every Symbol() call is unique

// Use as unique object property key
const USER_ID = Symbol("userId");
const obj2 = {
  name: "Alice",
  [USER_ID]: 12345, // not visible in normal iteration
};
console.log(obj2[USER_ID]); // 12345
console.log(Object.keys(obj2)); // ["name"] — Symbol not included

// Well-known Symbols: built-in hooks for language behavior
// Symbol.iterator, Symbol.toPrimitive, Symbol.hasInstance, etc.
// (covered in Iterator section below)

// Global Symbol registry
const s1 = Symbol.for("shared");
const s2 = Symbol.for("shared");
console.log(s1 === s2); // true — same entry in registry


// ============================================================
// 5. ITERATOR PROTOCOL  (brief overview)
// ============================================================
// An object is *iterable* if it has a [Symbol.iterator]() method
// that returns an *iterator* (an object with a .next() method
// that returns { value, done })

// Manual iterator
function range(start, end) {
  let current = start;
  return {
    [Symbol.iterator]() {
      return this; // the object itself is the iterator
    },
    next() {
      if (current <= end) {
        return { value: current++, done: false };
      }
      return { value: undefined, done: true };
    }
  };
}

for (const n of range(1, 5)) {
  process.stdout.write(n + " "); // 1 2 3 4 5
}
console.log();

console.log([...range(10, 13)]); // [10, 11, 12, 13]

// Arrays, Strings, Maps, Sets are all built-in iterables
const strIter = "hello"[Symbol.iterator]();
console.log(strIter.next()); // { value: "h", done: false }
console.log(strIter.next()); // { value: "e", done: false }


// ============================================================
// 6. GENERATOR FUNCTIONS  (brief overview)
// ============================================================
// function* creates a generator — a function that can pause (yield)
// and resume, producing values lazily one at a time

function* count(start, end) {
  for (let i = start; i <= end; i++) {
    yield i; // pauses here, returns { value: i, done: false }
  }
  // implicit return { value: undefined, done: true }
}

const counter = count(1, 3);
console.log(counter.next()); // { value: 1, done: false }
console.log(counter.next()); // { value: 2, done: false }
console.log(counter.next()); // { value: 3, done: false }
console.log(counter.next()); // { value: undefined, done: true }

// Generators are iterable — works with for...of and spread
for (const n of count(1, 5)) {
  process.stdout.write(n + " "); // 1 2 3 4 5
}
console.log();

// Infinite sequence — safe because it's lazy
function* naturals() {
  let n = 1;
  while (true) yield n++;
}

function take(n, iterable) {
  const result = [];
  for (const val of iterable) {
    result.push(val);
    if (result.length === n) break;
  }
  return result;
}
console.log(take(5, naturals())); // [1, 2, 3, 4, 5]


// ============================================================
// 7. WEAKMAP & WEAKSET  (brief overview)
// ============================================================
// WeakMap/WeakSet hold *weak* references — they don't prevent
// garbage collection of their keys/values

// WeakMap:
//   • Keys MUST be objects (not primitives)
//   • Not iterable (no .forEach, no spread, no .size)
//   • Key-value pair is GC'd when the key object is no longer referenced elsewhere
const cache = new WeakMap();

function expensiveCompute(obj) {
  if (cache.has(obj)) {
    console.log("cache hit");
    return cache.get(obj);
  }
  const result = { ...obj, computed: Math.random() };
  cache.set(obj, result);
  return result;
}

let someObj = { id: 1 };
expensiveCompute(someObj); // computed
expensiveCompute(someObj); // cache hit
someObj = null; // now the WeakMap entry is eligible for GC

// WeakSet:
//   • Values MUST be objects
//   • Not iterable
//   • Useful for tracking "has this object been seen?"
const processed = new WeakSet();

function processOnce(obj) {
  if (processed.has(obj)) return "already done";
  processed.add(obj);
  return "processed";
}

const task = { name: "send email" };
console.log(processOnce(task)); // processed
console.log(processOnce(task)); // already done


// ============================================================
// 8. PROXY  (brief overview)
// ============================================================
// Proxy wraps an object and intercepts operations (get, set, delete…)
// using "traps" defined in a handler object

const handler = {
  get(target, key) {
    console.log(`GET: ${String(key)}`);
    return key in target ? target[key] : `Property "${String(key)}" not found`;
  },
  set(target, key, value) {
    if (typeof value !== "string") {
      throw new TypeError(`${String(key)} must be a string`);
    }
    target[key] = value;
    console.log(`SET: ${String(key)} = ${value}`);
    return true; // must return true to indicate success
  },
  deleteProperty(target, key) {
    console.log(`DELETE: ${String(key)}`);
    delete target[key];
    return true;
  }
};

const proxied = new Proxy({}, handler);
proxied.name = "Alice";        // SET: name = Alice
console.log(proxied.name);     // GET: name → "Alice"
console.log(proxied.missing);  // GET: missing → 'Property "missing" not found'

try {
  proxied.age = 30; // TypeError: age must be a string
} catch (e) {
  console.error(e.message);
}

// Practical: validation proxy
function createValidator(target, validations) {
  return new Proxy(target, {
    set(obj, prop, value) {
      if (prop in validations) {
        const error = validations[prop](value);
        if (error) throw new Error(error);
      }
      obj[prop] = value;
      return true;
    }
  });
}

const person = createValidator({}, {
  age: v => (typeof v !== "number" || v < 0) ? "age must be a non-negative number" : null,
  name: v => (typeof v !== "string" || v.length < 2) ? "name must be 2+ chars" : null,
});

person.name = "Alice"; // ok
person.age  = 30;      // ok
try { person.age = -5; } catch (e) { console.error(e.message); }
// age must be a non-negative number


// ============================================================
// 9. REFLECT  (brief overview)
// ============================================================
// Reflect provides static methods that mirror Proxy traps
// and make it easier to forward operations in a Proxy handler

// Without Reflect (fragile):
const handler2 = {
  set(target, key, value) {
    target[key] = value; // manually doing what the default would do
    return true;
  }
};

// With Reflect (clean — calls the default behavior):
const handler3 = {
  get(target, key, receiver) {
    console.log(`Intercepted GET: ${String(key)}`);
    return Reflect.get(target, key, receiver); // default behavior
  },
  set(target, key, value, receiver) {
    console.log(`Intercepted SET: ${String(key)} = ${JSON.stringify(value)}`);
    return Reflect.set(target, key, value, receiver); // default behavior
  }
};

const traced = new Proxy({ x: 10 }, handler3);
traced.y = 20;           // Intercepted SET: y = 20
console.log(traced.x);   // Intercepted GET: x → 10

// Reflect methods match Proxy traps 1-to-1:
// Reflect.get / Reflect.set / Reflect.has / Reflect.deleteProperty
// Reflect.apply / Reflect.construct / Reflect.defineProperty / etc.

console.log(Reflect.has({ a: 1 }, "a")); // true (like "a" in obj)
console.log(Reflect.ownKeys({ a: 1, [Symbol("s")]: 2 })); // ["a", Symbol(s)]
