# Exercise 10: The `this` Keyword and Context

## Objective
Understand how `this` is determined by the **call site** in regular functions, how `call`, `apply`, and `bind` let you set `this` explicitly, and why arrow functions behave differently.

## Background
`this` is one of the most misunderstood parts of JavaScript because its value is not set at function definition time — it is determined by how the function is **called**. There are four binding rules: default, implicit (method call), explicit (`call`/`apply`/`bind`), and new (constructor). Arrow functions are the exception: they inherit `this` from the surrounding lexical scope and ignore call-site rules entirely.

## Requirements

1. **Default binding:**
   - Write a plain function `showThis()` that logs `typeof this`.
   - Call it as a regular function (not a method). In non-strict mode it logs `"object"` (global); in strict mode it logs `"undefined"`.
   - Show both. Add comments explaining the rule.

2. **Implicit binding (method call):**
   - Create an object `person` with `name: "Alice"` and a method `greet()` that returns `` `Hello, I am ${this.name}` ``.
   - Call `person.greet()` — log the result.
   - Then extract the method: `const greetFn = person.greet` and call `greetFn()`.
   - Show that `greetFn()` logs `"Hello, I am undefined"` because the implicit `this` binding is lost.
   - Add a comment explaining why.

3. **Explicit binding with `call` and `apply`:**
   - Write a function `introduce(greeting, punctuation)` that returns `` `${greeting}, I am ${this.name}${punctuation}` ``.
   - Create objects `alice = { name: "Alice" }` and `bob = { name: "Bob" }`.
   - Call with `call`: `introduce.call(alice, "Hi", "!")` → `"Hi, I am Alice!"`
   - Call with `apply`: `introduce.apply(bob, ["Hey", "..."])` → `"Hey, I am Bob..."`
   - Explain in a comment the difference between `call` and `apply`.

4. **`bind` — creating a bound function:**
   - Use `bind` to create `introduceAlice = introduce.bind(alice, "Hello")`.
   - Call `introduceAlice(".")` → `"Hello, I am Alice."`
   - Call `introduceAlice("!")` → `"Hello, I am Alice!"`
   - Show that `alice`'s `this` is permanently locked — even if you try to rebind it with `.call(bob)`, `alice` still wins.

5. **Constructor binding (`new`):**
   - Write a constructor function `Animal(name, sound)` that sets `this.name` and `this.sound`, and has a method `speak()` that returns `` `${this.name} says ${this.sound}` ``.
   - Create `const dog = new Animal("Dog", "Woof")` and `const cat = new Animal("Cat", "Meow")`.
   - Log `dog.speak()` and `cat.speak()`.
   - Add a comment explaining what `new` does to `this`.

6. **Arrow function — lexical `this`:**
   - Create an object `timer` with a property `seconds: 0` and a method `start()`.
   - Inside `start()`, use `setInterval` with an **arrow function** callback that increments `this.seconds` and logs it.
   - Run for a short time (use `setTimeout` to clear the interval after ~50ms).
   - Explain in a comment why an arrow function is needed here (and what would happen with a regular function).

7. **Arrow vs regular — side-by-side:**
   - Create `const obj = { value: 42 }`.
   - Add `obj.regularFn = function() { return this.value; }`.
   - Add `obj.arrowFn = () => this.value` (defined at module/global scope — `this` is `undefined` in strict mode Node, so `this.value` is `undefined`).
   - Log both results and add a comment explaining the difference.

## Hints
- The four `this` binding rules in priority order (highest wins): `new` > `bind`/`call`/`apply` > method call > default.
- When a method is **extracted** from an object and called as a plain function, implicit binding is lost — use `bind` to preserve it.
- `call(thisArg, arg1, arg2)` — arguments listed individually. `apply(thisArg, [arg1, arg2])` — arguments in an array. Mnemonic: **a**pply = **a**rray.
- Arrow functions defined at the module top level in Node.js have `this === module.exports` (an empty object `{}`), not the global object.

## Expected Output

```
--- default binding ---
typeof this (non-strict): object
typeof this (strict): undefined

--- implicit binding ---
person.greet(): Hello, I am Alice
greetFn() (lost binding): Hello, I am undefined

--- call and apply ---
introduce.call(alice, "Hi", "!")    → Hi, I am Alice!
introduce.apply(bob, ["Hey","..."]) → Hey, I am Bob...

--- bind ---
introduceAlice(".") → Hello, I am Alice.
introduceAlice("!") → Hello, I am Alice!
rebind attempt → Hello, I am Alice!  (bind wins)

--- constructor ---
dog.speak() → Dog says Woof
cat.speak() → Cat says Meow

--- arrow vs regular ---
regularFn() → 42
arrowFn() → undefined  (arrow captures outer `this`, not obj)

--- timer (arrow in setInterval) ---
1
2
3
(cleared after ~50ms)
```
