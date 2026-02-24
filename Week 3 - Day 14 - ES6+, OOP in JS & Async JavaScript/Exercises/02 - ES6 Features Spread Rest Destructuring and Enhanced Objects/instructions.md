# Exercise 02: ES6 Features — Default Parameters, Spread, Rest, Destructuring, and Enhanced Objects

## Objective
Practice the core ES6+ syntax features that make JavaScript code more concise: default function parameters, spread/rest operators, array and object destructuring, and enhanced object literal shorthand.

## Background
ES6 introduced a wave of quality-of-life improvements to JavaScript syntax. These features don't add new runtime capabilities — you could always write the equivalent verbose code — but they dramatically reduce boilerplate and improve readability. They appear everywhere in modern JavaScript, React, and Node.js codebases.

## Requirements
1. In `script.js`, write a function `greet(name, greeting = "Hello")` that logs `"[greeting], [name]!"`. Call it once with both arguments and once with only a name (using the default greeting).
2. Write a function `sum(...numbers)` using a **rest parameter** that returns the sum of all arguments passed. Test it with `sum(1, 2, 3)` and `sum(10, 20, 30, 40)`.
3. Declare an array `const fruits = ["Apple", "Banana", "Cherry"]`. Use the **spread operator** to create a new array `moreFruits` that contains all items from `fruits` plus `"Date"` and `"Elderberry"` at the end. Log `moreFruits`.
4. Use the **spread operator** to merge two objects: `const defaults = { theme: "light", lang: "en", fontSize: 14 }` and `const userPrefs = { theme: "dark", fontSize: 16 }`. Merge them into `const settings` so that `userPrefs` values override `defaults`. Log `settings`.
5. **Array destructuring**: destructure `const [first, second, ...rest] = moreFruits` and log `first`, `second`, and `rest`.
6. **Object destructuring**: given `const user = { id: 1, name: "Alice", role: "admin", country: "US" }`, destructure `name`, `role`, and `country` into variables in a single line. Log all three.
7. **Nested destructuring**: given `const config = { server: { host: "localhost", port: 3000 }, db: { name: "mydb" } }`, extract `host` and `port` from `server` in a single destructuring statement. Log both.
8. **Destructuring in function parameters**: write a function `formatUser({ name, role, country = "Unknown" })` that returns the string `"[name] ([role]) from [country]"`. Call it with the `user` object from Requirement 6.
9. **Enhanced object literals**: given variables `const x = 10, y = 20`, create an object `point` using shorthand property names (not `{ x: x, y: y }`). Add a shorthand method `toString()` that returns `"(10, 20)"`. Log `point` and `point.toString()`.

## Hints
- Rest parameters (`...args`) must be the **last** parameter in the function signature. Spread (`...array`) is used at the call site or in an expression.
- Object spread merges left-to-right: later properties overwrite earlier ones — so place the override object **after** the defaults object.
- Destructuring can provide default values inline: `const { role = "guest" } = user`.
- In enhanced object literals, a method like `toString() { ... }` is shorthand for `toString: function() { ... }`.

## Expected Output

```
Hello, World!
Hi, Alice!
6
100
[ 'Apple', 'Banana', 'Cherry', 'Date', 'Elderberry' ]
{ theme: 'dark', lang: 'en', fontSize: 16 }
Apple
Banana
[ 'Cherry', 'Date', 'Elderberry' ]
Alice
admin
US
host: localhost  port: 3000
Alice (admin) from US
{ x: 10, y: 20, toString: [Function: toString] }
(10, 20)
```
