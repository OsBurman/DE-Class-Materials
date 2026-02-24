// Exercise 09: Closures and Lexical Scope — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Counter Factory
// ─────────────────────────────────────────────

console.log("--- counter factory ---");

function makeCounter(start = 0) {
  // `count` is private — only the returned methods can access it
  let count = start;
  return {
    increment() { count += 1; return count; },
    decrement() { count -= 1; return count; },
    getCount()  { return count; },
  };
}

const counterA = makeCounter();
const counterB = makeCounter();

// counterA and counterB each have their own separate `count` variable
console.log(`counterA: ${counterA.getCount()} → increment → ${counterA.increment()} → increment → ${counterA.increment()} → decrement → ${counterA.decrement()}`);
console.log(`counterB: ${counterB.getCount()} → increment → ${counterB.increment()}   (independent from counterA)`);

// ─────────────────────────────────────────────
// PART 2: makeAdder
// ─────────────────────────────────────────────

console.log("\n--- makeAdder ---");

// makeAdder closes over `x` — each call creates a new closure with its own `x`
function makeAdder(x) {
  return n => x + n; // `x` is captured in the closure at creation time
}

const add5  = makeAdder(5);
const add10 = makeAdder(10);

console.log(`add5(3) → ${add5(3)}`);            // 5 + 3 = 8
console.log(`add10(3) → ${add10(3)}`);           // 10 + 3 = 13
console.log(`add5(add10(2)) → ${add5(add10(2))}`); // add10(2)=12, add5(12)=17

// ─────────────────────────────────────────────
// PART 3: The Classic Loop-Closure Bug
// ─────────────────────────────────────────────

console.log("\n--- loop closure bug (var) ---");

// var is function-scoped: there is only ONE `i` variable shared by all closures.
// By the time the functions are called, the loop has finished and i === 3.
const varFns = [];
for (var i = 0; i < 3; i++) {
  varFns.push(function() { console.log(i); }); // all three closures reference the SAME `i`
}
varFns.forEach(fn => fn()); // prints 3, 3, 3

console.log("\n--- fix 1: let ---");

// `let` creates a NEW binding of `i` for each loop iteration.
// Each closure captures a different variable with the value at that iteration.
const letFns = [];
for (let i = 0; i < 3; i++) {
  letFns.push(() => console.log(i)); // each arrow function has its own `i`
}
letFns.forEach(fn => fn()); // prints 0, 1, 2

console.log("\n--- fix 2: IIFE ---");

// Pre-ES6 workaround: the IIFE creates a new function scope per iteration,
// copying the current value of `i` into the parameter `j`.
const iifeFns = [];
for (var i = 0; i < 3; i++) {
  iifeFns.push((function(j) {
    return () => console.log(j); // j is a new variable per IIFE call
  })(i)); // immediately call the IIFE with current i
}
iifeFns.forEach(fn => fn()); // prints 0, 1, 2

// ─────────────────────────────────────────────
// PART 4: Module Pattern — Bank Account
// ─────────────────────────────────────────────

console.log("\n--- bank account ---");

// The IIFE executes immediately and returns a public API object.
// `balance` lives in the IIFE's closure — it cannot be accessed or changed
// from outside except through the returned methods.
const bankAccount = (() => {
  let balance = 0; // private state

  return {
    deposit(amount) {
      balance += amount;
      console.log(`Deposited $${amount}. Balance: $${balance}`);
    },
    withdraw(amount) {
      if (amount > balance) {
        console.log(`Cannot withdraw $${amount}: insufficient funds. Balance: $${balance}`);
        return;
      }
      balance -= amount;
      console.log(`Withdrew $${amount}. Balance: $${balance}`);
    },
    getBalance() { return balance; },
  };
})();

bankAccount.deposit(100);
bankAccount.deposit(50);
bankAccount.withdraw(30);
bankAccount.withdraw(200); // refused
console.log(`Final balance: $${bankAccount.getBalance()}`);

// ─────────────────────────────────────────────
// PART 5: Closure in a Callback
// ─────────────────────────────────────────────

console.log("\n--- delayed greeting ---");

function delayedGreeting(name) {
  // Even though delayedGreeting returns immediately, the callback passed to
  // setTimeout retains a reference to `name` through the closure.
  // When the event loop runs the callback later (after 0ms), `name` is still alive.
  setTimeout(() => {
    console.log(`Hello, ${name}!`);
  }, 0);
}

delayedGreeting("Alice");
// Note: setTimeout(fn, 0) runs AFTER the current synchronous code finishes,
// so "Hello, Alice!" may print after the scope chain section below.

// ─────────────────────────────────────────────
// PART 6: Scope Chain
// ─────────────────────────────────────────────

console.log("\n--- scope chain ---");

function outer() {
  const a = "outer"; // visible to middle and inner via scope chain

  function middle() {
    const b = "middle"; // visible to inner via scope chain

    function inner() {
      const c = "inner"; // own variable
      // inner can access a (outer's scope), b (middle's scope), and c (own scope)
      console.log(`inner can see a: ${a}`);
      console.log(`inner can see b: ${b}`);
      console.log(`inner owns c: ${c}`);
    }

    inner();
  }

  middle();
}

outer();
