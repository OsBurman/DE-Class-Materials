// Exercise 09: Closures and Lexical Scope
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Counter Factory
// ─────────────────────────────────────────────

console.log("--- counter factory ---");

// TODO: Write makeCounter(start = 0) that returns an object with:
//   - increment(): adds 1 to internal count, returns new count
//   - decrement(): subtracts 1 from internal count, returns new count
//   - getCount():  returns current count
// The internal count variable must NOT be accessible from outside.

// TODO: Create counterA = makeCounter() and counterB = makeCounter()
//   - Show counterA: getCount(0) → increment → 1 → increment → 2 → decrement → 1
//   - Show counterB: getCount(0) → increment → 1  (independent from counterA)


// ─────────────────────────────────────────────
// PART 2: makeAdder — Closure Capturing a Parameter
// ─────────────────────────────────────────────

console.log("\n--- makeAdder ---");

// TODO: Write makeAdder(x) that returns a function (n) => x + n
//       x is captured in the closure at the time makeAdder is called.

// TODO: const add5 = makeAdder(5)
//       const add10 = makeAdder(10)
//       Log add5(3)           → 8
//       Log add10(3)          → 13
//       Log add5(add10(2))    → 17  (compose: add10(2)=12, then add5(12)=17)


// ─────────────────────────────────────────────
// PART 3: The Classic Loop-Closure Bug
// ─────────────────────────────────────────────

console.log("\n--- loop closure bug (var) ---");

// TODO: Create an array `varFns` of 3 functions using a for loop with `var i`.
//       Each function should log the value of i when called.
//       Call all three functions — they should ALL log 3 (the bug!).
//       Add a comment explaining why.


console.log("\n--- fix 1: let ---");

// TODO: Repeat the above but use `let i` instead of `var i`.
//       Each function should now log 0, 1, 2 respectively.
//       Explain in a comment why `let` fixes the bug.


console.log("\n--- fix 2: IIFE ---");

// TODO: Repeat with `var i` again, but wrap the function inside an IIFE
//       that receives `i` as a parameter, creating a new scope per iteration.
//       Each function should log 0, 1, 2.


// ─────────────────────────────────────────────
// PART 4: Module Pattern — Bank Account
// ─────────────────────────────────────────────

console.log("\n--- bank account ---");

// TODO: Create a bankAccount module using an IIFE:
//   const bankAccount = (() => {
//     let balance = 0;  // private — not accessible outside
//     return {
//       deposit(amount)  { ... },
//       withdraw(amount) { ... },  // refuse and log error if amount > balance
//       getBalance()     { return balance; },
//     };
//   })();
//
// Then:
//   bankAccount.deposit(100)    → "Deposited $100. Balance: $100"
//   bankAccount.deposit(50)     → "Deposited $50. Balance: $150"
//   bankAccount.withdraw(30)    → "Withdrew $30. Balance: $120"
//   bankAccount.withdraw(200)   → "Cannot withdraw $200: insufficient funds. Balance: $120"
//   Log final balance


// ─────────────────────────────────────────────
// PART 5: Closure in a Callback
// ─────────────────────────────────────────────

console.log("\n--- delayed greeting ---");

// TODO: Write function delayedGreeting(name) that calls setTimeout with delay 0
//       Inside the callback, log: `Hello, ${name}!`
//       Add a comment explaining why name is still accessible in the callback
//       even though delayedGreeting has already returned.

// TODO: Call delayedGreeting("Alice")


// ─────────────────────────────────────────────
// PART 6: Scope Chain
// ─────────────────────────────────────────────

console.log("\n--- scope chain ---");

// TODO: Write nested functions outer → middle → inner.
//   outer declares:  const a = "outer"
//   middle declares: const b = "middle"
//   inner declares:  const c = "inner"
//   Inside inner, log:
//     `inner can see a: outer`
//     `inner can see b: middle`
//     `inner owns c: inner`
// Call outer() to trigger everything.
