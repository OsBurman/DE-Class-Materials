// Exercise 01: Variables, Scope and Data Types — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Variable Declarations
// ─────────────────────────────────────────────

// const: cannot be reassigned
const courseName = "JavaScript Fundamentals";
console.log(`courseName: ${courseName}`);

// let: block-scoped and reassignable
let currentLesson = 1;
currentLesson = 2; // reassignment is allowed with let
console.log(`currentLesson after reassignment: ${currentLesson}`);

// var: function-scoped — leaks out of the if block
if (true) {
  var legacyVar = "I exist outside!"; // var is hoisted to function/global scope
}
console.log(`legacyVar outside block: ${legacyVar}`); // accessible — no error

// let: block-scoped — not accessible outside
if (true) {
  let blockLet = "I'm block-scoped"; // only lives inside this block
  // using blockLet here is fine
}
// console.log(blockLet); // ← would throw ReferenceError: blockLet is not defined
console.log("blockLet would throw ReferenceError outside its block");

// ─────────────────────────────────────────────
// PART 2: Primitive Data Types
// ─────────────────────────────────────────────

console.log("\n--- Primitive Types ---");

// String
const str = "Hello, World!";
console.log(`"${str}" → typeof: ${typeof str}`);

// Number
const num = 42;
console.log(`${num} → typeof: ${typeof num}`);

// BigInt — for integers beyond Number.MAX_SAFE_INTEGER
const big = 9007199254740993n;
console.log(`${big}n → typeof: ${typeof big}`);

// Boolean
const bool = true;
console.log(`${bool} → typeof: ${typeof bool}`);

// Undefined — declared but never assigned
let notAssigned;
console.log(`${notAssigned} → typeof: ${typeof notAssigned}`);

// Null — typeof returns "object", which is a known historical bug in JavaScript
const nothing = null;
console.log(`${nothing} → typeof: ${typeof nothing}  (known JS quirk — use === null to check)`);

// Symbol — always unique, even with the same description
const id = Symbol("id");
console.log(`${id.toString()} → typeof: ${typeof id}`);

// ─────────────────────────────────────────────
// PART 3: Scope Demonstration Function
// ─────────────────────────────────────────────

console.log("\n--- scopeDemo ---");

function scopeDemo() {
  for (let i = 0; i < 1; i++) {
    var funcScoped = "I'm function-scoped";   // var hoisted to function scope
    let blockScoped = "I'm block-scoped";     // let scoped to the for-block only
  }
  // funcScoped is accessible here because var is hoisted to the function
  console.log(`funcScoped inside function: ${funcScoped}`);
  // blockScoped is NOT accessible here — it would throw ReferenceError
  // console.log(blockScoped); // ← ReferenceError: blockScoped is not defined
}

scopeDemo();

// ─────────────────────────────────────────────
// PART 4: Object Type Check
// ─────────────────────────────────────────────

console.log("\n--- Object ---");

const person = {
  name: "Alice",
  age: 30,
  isStudent: false,
};

// typeof an object returns "object"
console.log(`person → typeof: ${typeof person}`);
// Each property has its own primitive type
console.log(`  name: ${person.name} → typeof: ${typeof person.name}`);
console.log(`  age: ${person.age} → typeof: ${typeof person.age}`);
console.log(`  isStudent: ${person.isStudent} → typeof: ${typeof person.isStudent}`);
