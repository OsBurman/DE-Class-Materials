// Exercise 10: The this Keyword and Context — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Default Binding
// ─────────────────────────────────────────────

console.log("--- default binding ---");

// Default binding rule: when a function is called as a plain function (no object, no new,
// no call/apply/bind), `this` defaults to the global object in non-strict mode
// and `undefined` in strict mode.
function showThisNonStrict() {
  console.log(`typeof this in non-strict: ${typeof this}`); // "object" (globalThis)
}

function showThisStrict() {
  "use strict";
  console.log(`typeof this in strict: ${typeof this}`); // "undefined"
}

showThisNonStrict();
showThisStrict();

// ─────────────────────────────────────────────
// PART 2: Implicit Binding (Method Call)
// ─────────────────────────────────────────────

console.log("\n--- implicit binding ---");

const person = {
  name: "Alice",
  greet() {
    return `Hello, I am ${this.name}`;
  },
};

// When called as person.greet(), `this` is implicitly set to `person`
console.log(`person.greet(): ${person.greet()}`); // "Hello, I am Alice"

// Extracting the method loses the implicit binding.
// greetFn is now just a plain function reference — `this` falls back to default binding.
const greetFn = person.greet;
console.log(`greetFn() (lost binding): ${greetFn()}`); // "Hello, I am undefined"
// Fix: use greetFn.bind(person) or person.greet.bind(person)

// ─────────────────────────────────────────────
// PART 3: Explicit Binding — call and apply
// ─────────────────────────────────────────────

console.log("\n--- call and apply ---");

function introduce(greeting, punctuation) {
  return `${greeting}, I am ${this.name}${punctuation}`;
}

const alice = { name: "Alice" };
const bob   = { name: "Bob" };

// call(thisArg, arg1, arg2, ...) — arguments passed individually
console.log(`introduce.call(alice, "Hi", "!")    → ${introduce.call(alice, "Hi", "!")}`);

// apply(thisArg, [arg1, arg2]) — arguments passed as an ARRAY
// Mnemonic: Apply = Array
console.log(`introduce.apply(bob, ["Hey","..."]) → ${introduce.apply(bob, ["Hey", "..."])}`);

// ─────────────────────────────────────────────
// PART 4: bind — Permanently Bound Function
// ─────────────────────────────────────────────

console.log("\n--- bind ---");

// bind returns a NEW function with `this` permanently set to alice.
// The second argument ("Hello") is a partially applied argument — it's always prepended.
const introduceAlice = introduce.bind(alice, "Hello");

console.log(`introduceAlice(".") → ${introduceAlice(".")}`); // "Hello, I am Alice."
console.log(`introduceAlice("!") → ${introduceAlice("!")}`); // "Hello, I am Alice!"

// bind wins: even if you use .call() on a bound function, the original bind takes precedence
console.log(`rebind attempt → ${introduceAlice.call(bob, "!")}`); // still Alice!

// ─────────────────────────────────────────────
// PART 5: Constructor Binding (new)
// ─────────────────────────────────────────────

console.log("\n--- constructor ---");

// `new` does four things:
// 1. Creates a new empty object
// 2. Sets `this` inside the constructor to that new object
// 3. Executes the constructor body (assigning properties to `this`)
// 4. Returns the new object (unless the constructor explicitly returns another object)
function Animal(name, sound) {
  this.name  = name;
  this.sound = sound;
  this.speak = function() {
    return `${this.name} says ${this.sound}`;
  };
}

const dog = new Animal("Dog", "Woof");
const cat = new Animal("Cat", "Meow");

console.log(`dog.speak() → ${dog.speak()}`);
console.log(`cat.speak() → ${cat.speak()}`);

// ─────────────────────────────────────────────
// PART 7: Arrow vs Regular — Side-by-Side
// (doing this before Part 6 so setInterval output appears last)
// ─────────────────────────────────────────────

console.log("\n--- arrow vs regular ---");

const obj = { value: 42 };

// Regular function: `this` is determined at call time → obj
obj.regularFn = function() { return this.value; };

// Arrow function: `this` is captured from the LEXICAL SCOPE at definition time.
// At the module top level in Node.js, `this` is the module's `exports` object ({}),
// not `obj`. So `this.value` is undefined.
obj.arrowFn = () => this.value;

console.log(`regularFn() → ${obj.regularFn()}`); // 42
console.log(`arrowFn() → ${obj.arrowFn()}`);      // undefined — arrow sees module `this`, not obj

// ─────────────────────────────────────────────
// PART 6: Arrow Function — Lexical this (async output, appears last)
// ─────────────────────────────────────────────

// NOTE: setInterval/setTimeout are async — this output appears after all synchronous code.
console.log("\n--- timer (arrow in setInterval) ---");

const timer = { seconds: 0 };

timer.start = function() {
  // The arrow function captures `this` from `timer.start`'s execution context, which is `timer`.
  // If we used a regular function here: function() { this.seconds... } — `this` inside
  // setInterval's callback would be the global object (or undefined in strict mode),
  // NOT `timer`, so `this.seconds` would not update timer.seconds.
  const intervalId = setInterval(() => {
    timer.seconds += 1; // using `this` here would work too (this === timer in arrow)
    console.log(timer.seconds);
  }, 10);

  setTimeout(() => clearInterval(intervalId), 50); // stop after ~50ms (≈3-5 ticks)
};

timer.start();
