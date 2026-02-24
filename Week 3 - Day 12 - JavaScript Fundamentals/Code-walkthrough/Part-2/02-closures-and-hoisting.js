// =============================================================================
// DAY 12 — PART 2 | File 2: Closures & Hoisting
// File: 02-closures-and-hoisting.js
//
// Topics covered:
//   1. Closures and lexical scope
//   2. Hoisting (var, let, const, function declarations)
// =============================================================================


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — Closures and Lexical Scope
// ─────────────────────────────────────────────────────────────────────────────
//
// LEXICAL SCOPE: A function can access variables from the scope in which it was
// DEFINED — not the scope from which it was CALLED.
//
// CLOSURE: A function that "closes over" (remembers and accesses) variables from
// its outer scope, even after that outer scope has finished executing.

// ── 1a. Basic closure ─────────────────────────────────────────────────────────

function makeCounter(startValue = 0) {
  let count = startValue;  // 'count' lives in makeCounter's scope

  // The returned function is a closure — it remembers 'count'
  return function() {
    count++;
    return count;
  };
}

const counterA = makeCounter();
const counterB = makeCounter(10); // starts at 10

console.log(counterA()); // 1
console.log(counterA()); // 2
console.log(counterA()); // 3
console.log(counterB()); // 11  — counterB has its OWN independent 'count'
console.log(counterA()); // 4   — counterA keeps its own state

// 'count' is NOT directly accessible from outside — it's private
// console.log(count); // ReferenceError

// ── 1b. Closure with multiple returned functions — the module pattern ──────────

function createBankAccount(initialBalance) {
  let balance = initialBalance;  // private variable
  const transactionLog = [];

  return {
    deposit(amount) {
      if (amount <= 0) throw new Error("Deposit must be positive");
      balance += amount;
      transactionLog.push(`+$${amount}`);
      return balance;
    },
    withdraw(amount) {
      if (amount > balance) throw new Error("Insufficient funds");
      balance -= amount;
      transactionLog.push(`-$${amount}`);
      return balance;
    },
    getBalance() {
      return balance; // reads from closed-over 'balance'
    },
    getHistory() {
      return [...transactionLog]; // return a copy so history can't be mutated externally
    }
  };
}

const account = createBankAccount(500);
console.log(account.deposit(200));   // 700
console.log(account.withdraw(50));   // 650
console.log(account.getBalance());   // 650
console.log(account.getHistory());   // ["+$200", "-$50"]
// account.balance is undefined — the variable is private via closure!
console.log(account.balance);        // undefined

// ── 1c. Closure for function factories ────────────────────────────────────────

function makeMultiplier(factor) {
  return n => n * factor;  // arrow function closes over 'factor'
}

const double  = makeMultiplier(2);
const triple  = makeMultiplier(3);
const times10 = makeMultiplier(10);

console.log(double(5));   // 10
console.log(triple(5));   // 15
console.log(times10(7));  // 70

// Useful for partially applying arguments:
function makeGreeter(greeting) {
  return name => `${greeting}, ${name}!`;
}

const sayHello  = makeGreeter("Hello");
const sayBonjour = makeGreeter("Bonjour");

console.log(sayHello("Alice"));   // "Hello, Alice!"
console.log(sayBonjour("Alice")); // "Bonjour, Alice!"

// ── 1d. The classic closure-in-a-loop gotcha ──────────────────────────────────
// This is one of the most famous JavaScript interview questions

// BROKEN — all callbacks share the SAME 'i' variable (var is function-scoped)
console.log("--- var in loop (broken) ---");
const brokenCallbacks = [];
for (var i = 0; i < 3; i++) {
  brokenCallbacks.push(function() { return i; });
}
// By the time these run, the loop has finished and i === 3
console.log(brokenCallbacks[0]()); // 3 — not 0!
console.log(brokenCallbacks[1]()); // 3
console.log(brokenCallbacks[2]()); // 3

// FIX 1 — use 'let' — creates a new binding per iteration
console.log("--- let in loop (fixed) ---");
const fixedCallbacks = [];
for (let j = 0; j < 3; j++) {
  fixedCallbacks.push(function() { return j; });
}
console.log(fixedCallbacks[0]()); // 0 ✓
console.log(fixedCallbacks[1]()); // 1 ✓
console.log(fixedCallbacks[2]()); // 2 ✓

// FIX 2 — IIFE to create a new scope per iteration (pre-ES6 pattern)
console.log("--- IIFE fix (classic) ---");
const classicFix = [];
for (var k = 0; k < 3; k++) {
  classicFix.push((function(captured) {
    return function() { return captured; };
  })(k));
}
console.log(classicFix[0]()); // 0 ✓
console.log(classicFix[1]()); // 1 ✓

// ── 1e. Memoization — closures for performance ────────────────────────────────

function memoize(fn) {
  const cache = {};  // closed over — persists between calls
  return function(...args) {
    const key = JSON.stringify(args);
    if (cache[key] !== undefined) {
      console.log(`[cache hit] ${key}`);
      return cache[key];
    }
    console.log(`[computing] ${key}`);
    cache[key] = fn(...args);
    return cache[key];
  };
}

function slowSquare(n) {
  // Simulates expensive computation
  return n * n;
}

const fastSquare = memoize(slowSquare);
console.log(fastSquare(5));  // [computing] [5]  → 25
console.log(fastSquare(5));  // [cache hit] [5]  → 25 (instant)
console.log(fastSquare(10)); // [computing] [10] → 100


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Hoisting
// ─────────────────────────────────────────────────────────────────────────────
//
// Hoisting: JavaScript's engine scans the file BEFORE executing it.
// Certain declarations are "moved" (hoisted) to the top of their scope.
// Only the DECLARATION is hoisted — not the initialisation / assignment.

// ── 2a. Function declaration hoisting — fully hoisted ─────────────────────────

// Calling BEFORE the declaration — works because the whole function is hoisted
console.log(formatCurrency(42.5)); // "$42.50" ← called before it's defined below

function formatCurrency(amount) {
  return `$${amount.toFixed(2)}`;
}

console.log(formatCurrency(9.99)); // "$9.99" ← also works after

// ── 2b. var hoisting — declaration hoisted, value is undefined ────────────────

console.log(typeof courseTitle);   // "undefined" — var is hoisted but not initialized
// console.log(courseTitle);       // This would output undefined, not a ReferenceError

var courseTitle = "JavaScript Fundamentals";
console.log(courseTitle);          // "JavaScript Fundamentals"

// What the engine sees (conceptually):
// var courseTitle;          ← hoisted to top
// ...
// courseTitle = "JavaScript Fundamentals"; ← stays here

// ── 2c. let and const hoisting — the Temporal Dead Zone (TDZ) ─────────────────

// 'let' and 'const' ARE hoisted, but they're not initialized.
// Accessing them before declaration throws ReferenceError.
// The gap between the start of the scope and the declaration is called the TDZ.

// console.log(studentId); // ReferenceError: Cannot access 'studentId' before initialization
const studentId = "S-1042";
console.log(studentId); // "S-1042"

// ── 2d. Function expression and arrow function hoisting ───────────────────────

// Function expressions are NOT hoisted (they're assigned to variables, not declarations)

// console.log(calculateGPA); // undefined (if var) or ReferenceError (if let/const)
// console.log(calculateGPA()); // TypeError: calculateGPA is not a function

const calculateGPA = function(scores) {
  const avg = scores.reduce((s, n) => s + n, 0) / scores.length;
  return (avg / 25).toFixed(2); // scale 0-100 to 0-4.0
};

console.log(calculateGPA([85, 92, 78, 90])); // "3.45"

// Arrow functions behave the same — assigned to a variable, so not fully hoisted
// const fn = () => {}; // ← not accessible before this line

// ── 2e. Hoisting summary ──────────────────────────────────────────────────────
//
//  Declaration type          Hoisted?   Initialized?   Use before?
//  ─────────────────────────────────────────────────────────────────
//  function declaration       YES        YES (full)     ✓ Safe
//  var                        YES        NO (undefined) ⚠ returns undefined
//  let                        YES        NO (TDZ)       ✗ ReferenceError
//  const                      YES        NO (TDZ)       ✗ ReferenceError
//  function expression (var)  YES        NO (undefined) ✗ TypeError on call
//  arrow function (const)     YES        NO (TDZ)       ✗ ReferenceError
//
// BEST PRACTICE: Declare variables and functions before you use them.
// Even though function declarations are hoisted, writing them before their
// call sites makes code far easier to read.
