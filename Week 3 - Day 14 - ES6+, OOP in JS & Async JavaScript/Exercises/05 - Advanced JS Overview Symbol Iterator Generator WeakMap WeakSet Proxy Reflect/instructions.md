# Exercise 05: Advanced JavaScript Overview — Symbol, Iterator, Generator, WeakMap, WeakSet, Proxy, Reflect

## Objective
Gain awareness of seven advanced JavaScript features through short, runnable demonstrations that show what each one does and when it is useful.

## Background
These features are less common in day-to-day application code but appear frequently in library internals, framework source code, and advanced patterns. You won't memorise every API today — the goal is to recognise each feature, understand its core purpose, and run a minimal working example so you can look it up confidently when you encounter it later.

## Requirements
1. **Symbol**: Create two Symbols with the same description `Symbol("id")`. Log whether they are strictly equal (`===`). Create an object with a Symbol as a property key and access it. Log the symbol's `.description`.
2. **Iterator protocol**: Create a plain object `counter` with a `[Symbol.iterator]` method that yields numbers 1 through 5 (use a `next()` function returning `{ value, done }`). Use `for...of` to iterate it and log each value.
3. **Generator function**: Write a `function*` generator called `fibonacci` that yields the first 7 Fibonacci numbers. Use `Array.from({ length: 7 }, () => gen.next().value)` or a `for...of` loop to collect and log them.
4. **WeakMap**: Create a `WeakMap` called `cache`. Create an object `objA = {}`. Set `cache.set(objA, "cached data")`. Log `cache.has(objA)` (`true`). Set `objA = null` and log `cache.has(objA)` (`false` — key was garbage-collected).
5. **WeakSet**: Create a `WeakSet` called `visited`. Create an object `page = { url: "/home" }`. Add it to `visited`. Log `visited.has(page)` (`true`). Set `page = null` and log `visited.has(null)` (`false`).
6. **Proxy**: Create a `handler` object with a `get` trap that logs `"Getting property: [key]"` before returning the value. Wrap `const person = { name: "Alice", age: 30 }` in a Proxy with this handler. Access `proxy.name` and `proxy.age` — both should trigger the trap log.
7. **Reflect**: Use `Reflect.ownKeys({ a: 1, b: 2 })` to log the own property keys of an object. Use `Reflect.has({ x: 5 }, "x")` to log whether the key `"x"` exists.

## Hints
- `Symbol()` always returns a **unique** value even with the same description string — this is why two Symbols with the same description are not `===`.
- An iterator object must have a `next()` method returning `{ value, done }`. The `done: true` signals the loop to stop.
- Generator functions use `function*` and `yield`. Calling the function returns a **generator object** — the function body doesn't run until you call `.next()`.
- `WeakMap` and `WeakSet` hold **weak references** — if no other code holds a reference to the key object, the garbage collector can reclaim it and the entry disappears.
- `Proxy` wraps an object and intercepts operations (get, set, deleteProperty, etc.) via **traps** defined in a handler object.

## Expected Output

```
Two Symbols equal: false
Symbol id description: id
Symbol key property: secret value
Counter via iterator: 1 2 3 4 5
First 7 Fibonacci: [0, 1, 1, 2, 3, 5, 8]
WeakMap has objA: true
WeakMap has null: false
WeakSet has page: true
WeakSet has null: false
Getting property: name
Proxy name: Alice
Getting property: age
Proxy age: 30
Reflect.ownKeys: [ 'a', 'b' ]
Reflect.has x: true
```
