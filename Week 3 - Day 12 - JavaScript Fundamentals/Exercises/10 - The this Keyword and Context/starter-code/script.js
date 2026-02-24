// Exercise 10: The this Keyword and Context
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Default Binding
// ─────────────────────────────────────────────

console.log("--- default binding ---");

// TODO: Write function showThisNonStrict() that logs `typeof this in non-strict: ${typeof this}`
//       (no "use strict" — in a plain call `this` is the global object)

// TODO: Write function showThisStrict() with "use strict" that logs
//       `typeof this in strict: ${typeof this}`
//       (in strict mode plain call `this` is undefined)

// TODO: Call both functions.
// TODO: Add a comment: explain the default binding rule.


// ─────────────────────────────────────────────
// PART 2: Implicit Binding (Method Call)
// ─────────────────────────────────────────────

console.log("\n--- implicit binding ---");

// TODO: Create object person = { name: "Alice", greet() { return `Hello, I am ${this.name}` } }
// TODO: Log person.greet()                  → "Hello, I am Alice"
// TODO: const greetFn = person.greet        (extract the method)
// TODO: Log greetFn()                       → "Hello, I am undefined"
// TODO: Add a comment: why does extracting the method lose `this`?


// ─────────────────────────────────────────────
// PART 3: Explicit Binding — call and apply
// ─────────────────────────────────────────────

console.log("\n--- call and apply ---");

// TODO: Write function introduce(greeting, punctuation) {
//         return `${greeting}, I am ${this.name}${punctuation}`;
//       }

// TODO: const alice = { name: "Alice" }
// TODO: const bob   = { name: "Bob" }

// TODO: Log introduce.call(alice, "Hi", "!")      → "Hi, I am Alice!"
// TODO: Log introduce.apply(bob, ["Hey", "..."]) → "Hey, I am Bob..."
// TODO: Add a comment: difference between call and apply


// ─────────────────────────────────────────────
// PART 4: bind — Permanently Bound Function
// ─────────────────────────────────────────────

console.log("\n--- bind ---");

// TODO: const introduceAlice = introduce.bind(alice, "Hello")
//       (first arg to bind = this, subsequent args = partially applied args)

// TODO: Log introduceAlice(".")  → "Hello, I am Alice."
// TODO: Log introduceAlice("!")  → "Hello, I am Alice!"

// TODO: Try to rebind: introduceAlice.call(bob, "!")
//       Log the result — show that bind wins over call
// TODO: Add a comment explaining why bind is permanent


// ─────────────────────────────────────────────
// PART 5: Constructor Binding (new)
// ─────────────────────────────────────────────

console.log("\n--- constructor ---");

// TODO: Write constructor function Animal(name, sound):
//   - this.name  = name
//   - this.sound = sound
//   - this.speak = function() { return `${this.name} says ${this.sound}`; }

// TODO: const dog = new Animal("Dog", "Woof")
// TODO: const cat = new Animal("Cat", "Meow")
// TODO: Log dog.speak() and cat.speak()
// TODO: Add a comment explaining what `new` does: creates a new object,
//       sets `this` to that object, and returns it


// ─────────────────────────────────────────────
// PART 6: Arrow Function — Lexical this
// ─────────────────────────────────────────────

// (This part uses setInterval — output will appear after the synchronous code finishes)

// TODO: Create object timer = { seconds: 0 }
// TODO: Add timer.start = function() {
//         const intervalId = setInterval(() => {
//           // TODO: increment this.seconds and log it
//         }, 10);
//         setTimeout(() => clearInterval(intervalId), 50);
//       }
// TODO: Call timer.start()
// TODO: Add a comment: why must the setInterval callback be an arrow function here?
//       (What would happen with a regular function instead?)


// ─────────────────────────────────────────────
// PART 7: Arrow vs Regular — Side-by-Side
// ─────────────────────────────────────────────

console.log("\n--- arrow vs regular ---");

// TODO: const obj = { value: 42 }
// TODO: obj.regularFn = function() { return this.value; }
//       (regular function — this is bound to obj at call time)
// TODO: obj.arrowFn = () => this.value
//       (arrow function — this is the outer/module this, NOT obj)

// TODO: Log `regularFn() → ${obj.regularFn()}`  → 42
// TODO: Log `arrowFn() → ${obj.arrowFn()}`      → undefined
// TODO: Add a comment explaining why the arrow function doesn't see obj's value
