// Exercise 05: Advanced JS Overview

// ── 1. SYMBOL ─────────────────────────────────────────────────────────────────

// TODO: Requirement 1
//   a) Create sym1 = Symbol("id") and sym2 = Symbol("id")
//      Log whether they are strictly equal (===)
//      Format: "Two Symbols equal: false"
//   b) Create an object obj with a Symbol key: { [sym1]: "secret value" }
//      Access and log the value using obj[sym1]
//      Format: "Symbol key property: secret value"
//   c) Log sym1.description  → "Symbol id description: id"


// ── 2. ITERATOR PROTOCOL ──────────────────────────────────────────────────────

// TODO: Requirement 2
//   Create an object `counter` whose [Symbol.iterator] method returns an iterator
//   that yields numbers 1, 2, 3, 4, 5 then signals done: true.
//   Use for...of on counter and log each value.
//   Format: "Counter via iterator: 1 2 3 4 5" (collect in array, join with space)


// ── 3. GENERATOR FUNCTION ─────────────────────────────────────────────────────

// TODO: Requirement 3
//   Write a generator function* fibonacci() that yields the first 7 Fibonacci
//   numbers: 0, 1, 1, 2, 3, 5, 8.
//   Collect them into an array and log it.
//   Format: "First 7 Fibonacci: [0,1,1,2,3,5,8]"


// ── 4. WEAKMAP ────────────────────────────────────────────────────────────────

// TODO: Requirement 4
//   Create a WeakMap `cache`. Create let objA = {}.
//   Set cache.set(objA, "cached data"). Log cache.has(objA) → true.
//   Set objA = null. Log cache.has(null) → false.
//   Format: "WeakMap has objA: true"  and  "WeakMap has null: false"


// ── 5. WEAKSET ────────────────────────────────────────────────────────────────

// TODO: Requirement 5
//   Create a WeakSet `visited`. Create let page = { url: "/home" }.
//   Add page to visited. Log visited.has(page) → true.
//   Set page = null. Log visited.has(null) → false.


// ── 6. PROXY ─────────────────────────────────────────────────────────────────

// TODO: Requirement 6
//   Create const person = { name: "Alice", age: 30 }.
//   Create a handler with a `get` trap that logs "Getting property: [key]"
//   then returns the actual value (use Reflect.get or target[key]).
//   Wrap person in a Proxy with this handler.
//   Access proxy.name and proxy.age and log the returned values.


// ── 7. REFLECT ────────────────────────────────────────────────────────────────

// TODO: Requirement 7
//   Log Reflect.ownKeys({ a: 1, b: 2 }) → "Reflect.ownKeys: [ 'a', 'b' ]"
//   Log Reflect.has({ x: 5 }, "x")      → "Reflect.has x: true"
