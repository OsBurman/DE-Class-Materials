# Day 12 — JavaScript Fundamentals
## Quick Reference Guide

---

## 1. Variables: var vs let vs const

| | `var` | `let` | `const` |
|---|---|---|---|
| Scope | Function | Block | Block |
| Hoisted | ✅ (as `undefined`) | ✅ (TDZ — unusable) | ✅ (TDZ) |
| Re-declare | ✅ | ❌ | ❌ |
| Re-assign | ✅ | ✅ | ❌ |
| **Use** | ❌ Avoid | ✅ Mutable values | ✅ Default choice |

**TDZ (Temporal Dead Zone):** `let`/`const` exist in scope from block start but throw `ReferenceError` if accessed before declaration.

---

## 2. Data Types

**Primitives** (immutable, stored by value):
```
number    — 64-bit float; includes NaN and Infinity
string    — immutable sequence of UTF-16 characters
boolean   — true / false
undefined — declared but not assigned
null      — explicit "no value" (typeof null === "object" — historical bug)
symbol    — unique identifier (Symbol('desc'))
bigint    — arbitrary precision integer (42n)
```

**Reference types** (stored by reference):
```
object  — key-value pairs  { name: "Alice" }
array   — ordered list     [1, 2, 3]
function — first-class object; typeof === "function"
```

```js
typeof 42           // "number"
typeof "hi"         // "string"
typeof true         // "boolean"
typeof undefined    // "undefined"
typeof null         // "object"   ← famous quirk
typeof {}           // "object"
typeof []           // "object"   — use Array.isArray() to check arrays
typeof function(){} // "function"
```

---

## 3. Type Coercion

```js
// Loose equality (==) coerces types — avoid
0   == false   // true
""  == false   // true
null == undefined  // true

// Strict equality (===) — always use this
0   === false  // false
""  === false  // false

// Explicit coercion
Number("42")      // 42
Number("")        // 0
Number("abc")     // NaN
Number(true)      // 1
String(42)        // "42"
Boolean(0)        // false
Boolean("")       // false
Boolean(null)     // false
parseInt("42px")  // 42
parseFloat("3.14abc") // 3.14
```

**Falsy values:** `false`, `0`, `-0`, `0n`, `""`, `null`, `undefined`, `NaN`  
Everything else is **truthy** (including `[]`, `{}`, `"0"`, `"false"`)

---

## 4. String Methods

```js
const s = "Hello, World!";

s.length                     // 13
s.toUpperCase()              // "HELLO, WORLD!"
s.toLowerCase()              // "hello, world!"
s.trim()                     // remove leading/trailing whitespace
s.trimStart()  s.trimEnd()
s.includes("World")          // true
s.startsWith("Hello")        // true
s.endsWith("!")              // true
s.indexOf("o")               // 4
s.lastIndexOf("o")           // 8
s.slice(7, 12)               // "World"   (negative indexing allowed)
s.substring(7, 12)           // "World"   (no negative index)
s.replace("World", "JS")     // "Hello, JS!"
s.replaceAll("l", "L")       // "HeLLo, WorLd!"
s.split(", ")                // ["Hello", "World!"]
s.split("")                  // array of chars
s.charAt(0)                  // "H"
s[0]                         // "H"
s.padStart(15, "*")          // "**Hello, World!"
s.padEnd(15, ".")            // "Hello, World!.."
s.repeat(3)                  // "Hello, World!Hello, World!Hello, World!"
"  spaces  ".trimStart()     // "spaces  "

// Template literals
const name = "Alice";
`Hello, ${name}! ${1 + 1} + 1 = ${1 + 1 + 1}`
// Multiline:
const html = `
  <div>
    <p>${name}</p>
  </div>
`;
```

---

## 5. Array Methods

```js
const arr = [3, 1, 4, 1, 5, 9, 2, 6];

// Mutating (modify original array)
arr.push(7)         // add to end; returns new length
arr.pop()           // remove from end; returns removed element
arr.unshift(0)      // add to front
arr.shift()         // remove from front
arr.splice(2, 1)    // splice(start, deleteCount, ...items) — modify in place
arr.sort()          // ⚠️ sorts as strings by default!
arr.sort((a, b) => a - b)  // numeric ascending
arr.reverse()
arr.fill(0, 2, 5)   // fill [2,5) with 0

// Non-mutating (return new value, original unchanged)
arr.slice(1, 4)                   // [1, 4, 1]    — [start, end)
arr.concat([10, 11])              // new array with elements appended
arr.map(x => x * 2)              // transform each element → new array
arr.filter(x => x > 3)           // keep matching elements → new array
arr.reduce((acc, x) => acc + x, 0) // fold into single value (0 is initial)
arr.find(x => x > 4)             // first matching element (or undefined)
arr.findIndex(x => x > 4)        // index of first match (or -1)
arr.some(x => x > 8)             // true if ANY element matches
arr.every(x => x > 0)            // true if ALL elements match
arr.includes(5)                  // true/false
arr.indexOf(1)                   // 1 (first occurrence)
arr.lastIndexOf(1)               // 3
arr.flat()                       // flatten one level
arr.flat(Infinity)               // fully flatten
arr.flatMap(x => [x, x * 2])     // map then flatten one level
arr.join(", ")                   // "3, 1, 4, 1, 5, 9, 2, 6"
[...new Set(arr)]                // remove duplicates
Array.from({length: 5}, (_, i) => i) // [0,1,2,3,4]
```

---

## 6. Object Basics

```js
const user = {
    name: "Alice",
    age: 30,
    "full name": "Alice Smith",   // keys with spaces need quotes
    greet() { return `Hi, I'm ${this.name}`; }  // method shorthand
};

// Access
user.name;               // dot notation
user["full name"];       // bracket notation (required for non-identifier keys)
user.greet();

// Add / update / delete
user.email = "alice@example.com";
user["age"] = 31;
delete user.email;

// Check key existence
"name" in user;          // true
user.hasOwnProperty("name");  // true

// Object methods
Object.keys(user)        // ["name", "age", "greet"]
Object.values(user)      // ["Alice", 30, ƒ]
Object.entries(user)     // [["name","Alice"], ["age",30], ...]
Object.assign({}, user, { role: "admin" })   // shallow merge
Object.freeze(user)      // prevent modifications
Object.isFrozen(user)    // true
const copy = { ...user } // spread — shallow clone
```

---

## 7. Functions

```js
// Function declaration — hoisted (usable before definition)
function add(a, b) { return a + b; }

// Function expression — not hoisted
const multiply = function(a, b) { return a * b; };

// Arrow function — no own 'this'; no 'arguments'; can't be constructor
const square = n => n * n;
const greet  = (name) => `Hello, ${name}!`;
const noop   = () => {};
const getObj = () => ({ x: 1 });   // wrap object literal in parens!

// Default parameters
function greet(name = "World") { return `Hello, ${name}!`; }

// Rest parameters
function sum(...nums) { return nums.reduce((a, b) => a + b, 0); }

// Spread operator
Math.max(...[3, 1, 4, 1, 5]);   // 5
const merged = [...arr1, ...arr2];
```

---

## 8. Closures

A closure is a function that **remembers the variables from its outer scope** even after that scope has exited.

```js
function makeCounter(start = 0) {
    let count = start;               // private via closure
    return {
        increment: () => ++count,
        decrement: () => --count,
        value:     () => count
    };
}

const counter = makeCounter(10);
counter.increment();   // 11
counter.increment();   // 12
counter.value();       // 12
// count is NOT accessible from outside
```

**Common use cases:** data encapsulation, factory functions, memoisation, event handlers that need persistent state.

---

## 9. `this` Keyword

| Context | `this` value |
|---------|-------------|
| Global scope (non-strict) | `window` / `global` |
| Global scope (strict mode) | `undefined` |
| Object method | The object the method is called on |
| Arrow function | Lexical `this` — inherited from enclosing scope |
| `new FunctionName()` | The new object being created |
| `fn.call(obj)` | `obj` |
| `fn.apply(obj, args)` | `obj` |
| `fn.bind(obj)` | Returns new function with `this` permanently set to `obj` |

```js
const obj = {
    name: "Alice",
    greet() { return `Hi, ${this.name}`; },
    greetArrow: () => `Hi, ${this.name}`  // ❌ 'this' is enclosing scope (not obj)
};

obj.greet();         // "Hi, Alice"
obj.greetArrow();    // "Hi, undefined"

const fn = obj.greet;
fn();                // "Hi, undefined"  (this === global in non-strict)
fn.call(obj);        // "Hi, Alice"
```

---

## 10. Prototype Chain

```js
// Every object has a hidden [[Prototype]] link
const arr = [1, 2, 3];
// arr → Array.prototype → Object.prototype → null

// Prototype chain for custom objects
function Animal(name) { this.name = name; }
Animal.prototype.speak = function() { return `${this.name} speaks`; };

const dog = new Animal("Rex");
dog.speak();         // "Rex speaks"  — found on Animal.prototype
dog.toString();      // "[object Object]"  — found on Object.prototype

// Check prototype chain
Object.getPrototypeOf(dog) === Animal.prototype;  // true
dog instanceof Animal;  // true
```

---

## 11. Nullish Coalescing & Optional Chaining

```js
// Nullish coalescing (??) — use right side only if left is null/undefined
const port = config.port ?? 3000;    // 0 would use 0 (not 3000)
const name = user.name ?? "Anonymous";

// Optional chaining (?.) — short-circuit if null/undefined
const city = user?.address?.city;        // undefined (no error)
const first = arr?.[0];                  // undefined if arr is nullish
const result = obj?.method?.();          // call only if method exists

// Combine
const zip = user?.address?.zip ?? "N/A";
```

---

## 12. Scope & Hoisting

```js
// var is hoisted and initialized to undefined
console.log(x);   // undefined (not ReferenceError)
var x = 5;

// Function declarations are fully hoisted
greet();          // "Hello"  — works before definition
function greet() { console.log("Hello"); }

// let/const are in TDZ — throw ReferenceError before declaration
console.log(y);   // ReferenceError: Cannot access 'y' before initialization
let y = 10;

// Block scope
{
    let blockVar = "only here";
    const blockConst = "only here too";
}
console.log(typeof blockVar); // "undefined" (not accessible, not ReferenceError with typeof)
```
