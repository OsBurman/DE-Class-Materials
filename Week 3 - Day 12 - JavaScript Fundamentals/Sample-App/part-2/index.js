// Day 12 Part 2 — Functions, Closures, Hoisting, 'this', Strict Mode
// Run: node index.js

"use strict";

console.log("╔══════════════════════════════════════════════════════════════╗");
console.log("║  Day 12 Part 2 — Functions, Closures & Scope                ║");
console.log("╚══════════════════════════════════════════════════════════════╝\n");

demoFunctions();
demoThisKeyword();
demoClosures();
demoHoisting();
demoControlFlow();

// ─────────────────────────────────────────────────────────────
// 1. Functions — Declarations, Expressions, Arrow Functions
// ─────────────────────────────────────────────────────────────
function demoFunctions() {
  console.log("=== 1. Function Syntax Variants ===");

  // Function declaration (hoisted)
  function greet(name) {
    return `Hello, ${name}!`;
  }

  // Function expression (not hoisted)
  const square = function(n) { return n * n; };

  // Arrow function — concise, no own 'this'
  const cube  = n => n ** 3;
  const add   = (a, b) => a + b;
  const multi = (a, b) => {        // block body needs explicit return
    const result = a * b;
    return result;
  };

  console.log("  declaration greet('Alice'):", greet("Alice"));
  console.log("  expression  square(7):", square(7));
  console.log("  arrow cube(3):", cube(3));
  console.log("  arrow add(5,3):", add(5, 3));

  // Default parameters
  function connect(host = "localhost", port = 3000) {
    return `Connecting to ${host}:${port}`;
  }
  console.log("  defaults connect():", connect());
  console.log("  defaults connect('prod.server.com', 8080):", connect("prod.server.com", 8080));

  // Rest parameters
  function sum(...numbers) {
    return numbers.reduce((acc, n) => acc + n, 0);
  }
  console.log("  rest sum(1,2,3,4,5):", sum(1, 2, 3, 4, 5));

  // Spread operator
  const arr1 = [1, 2, 3];
  const arr2 = [4, 5, 6];
  console.log("  spread [...arr1,...arr2]:", [...arr1, ...arr2]);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 2. 'this' Keyword & Context
// ─────────────────────────────────────────────────────────────
function demoThisKeyword() {
  console.log("=== 2. 'this' Keyword ===");

  // In an object method, 'this' refers to the object
  const student = {
    name: "Alice",
    score: 92,
    describe() {            // regular method — 'this' is 'student'
      return `${this.name} scored ${this.score}`;
    },
    // Arrow function — 'this' is inherited from outer scope (NOT the object)
    describeArrow: () => {
      return `Arrow: this.name = ${typeof this === "undefined" ? "undefined (strict)" : "object"}`;
    }
  };
  console.log(" ", student.describe());
  console.log(" ", student.describeArrow());

  // call, apply, bind — manually set 'this'
  function introduce(greeting, punctuation) {
    return `${greeting}, I'm ${this.name}${punctuation}`;
  }
  const person = { name: "Bob" };
  console.log("  .call:", introduce.call(person, "Hello", "!"));
  console.log("  .apply:", introduce.apply(person, ["Hi", "."]));
  const bound = introduce.bind(person, "Hey");
  console.log("  .bind:", bound("?"));
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 3. Closures & Lexical Scope
// ─────────────────────────────────────────────────────────────
function demoClosures() {
  console.log("=== 3. Closures & Lexical Scope ===");

  // A closure remembers its outer scope after the outer function has returned
  function makeCounter(start = 0) {
    let count = start;              // private to makeCounter
    return {
      increment() { return ++count; },
      decrement() { return --count; },
      value()     { return count; }
    };
  }
  const counter = makeCounter(10);
  counter.increment(); counter.increment(); counter.increment();
  counter.decrement();
  console.log("  counter.value():", counter.value(), " (started at 10, +3 -1)");
  console.log("  count variable NOT accessible externally: count is", typeof count);

  // Factory function using closure
  function multiplier(factor) {
    return n => n * factor;    // closes over 'factor'
  }
  const double = multiplier(2);
  const triple = multiplier(3);
  console.log("  double(7):", double(7), "  triple(7):", triple(7));

  // Practical: memoization using closure
  function memoize(fn) {
    const cache = {};
    return function(n) {
      if (cache[n] === undefined) {
        cache[n] = fn(n);
        console.log(`  memoize: computed fib(${n})`);
      }
      return cache[n];
    };
  }
  const fib = memoize(function f(n) { return n <= 1 ? n : f(n-1) + f(n-2); });
  console.log("  fib(10):", fib(10));
  console.log("  fib(10) again:", fib(10), "← no recompute");
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 4. Hoisting
// ─────────────────────────────────────────────────────────────
function demoHoisting() {
  console.log("=== 4. Hoisting ===");

  // Function declarations are fully hoisted
  console.log("  hoistedFn() before declaration:", hoistedFn());
  function hoistedFn() { return "I am hoisted!"; }

  // var is hoisted (declaration only, not assignment)
  console.log("  varBefore before assignment:", varBefore); // undefined
  var varBefore = "assigned now";
  console.log("  varBefore after assignment:", varBefore);

  // let/const are in a "temporal dead zone" (TDZ) — accessing before declaration throws ReferenceError
  try {
    // console.log(letVar); // ← would throw ReferenceError
    let letVar = "let";
  } catch (e) { console.log("  let TDZ:", e.message); }

  console.log("  Rule: prefer const > let, avoid var");
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 5. Control Flow & Error Handling
// ─────────────────────────────────────────────────────────────
function demoControlFlow() {
  console.log("=== 5. Control Flow & Error Handling ===");

  // Truthy / falsy
  const values = [0, 1, "", "hello", null, undefined, NaN, [], {}];
  values.forEach(v => {
    const bool = v ? "truthy" : "falsy";
    console.log(`  ${JSON.stringify(v)} → ${bool}`);
  });

  // try / catch / finally
  console.log("\n  Error handling with try/catch:");
  function divide(a, b) {
    if (b === 0) throw new RangeError("Division by zero");
    return a / b;
  }
  try {
    console.log("  divide(10, 2) =", divide(10, 2));
    console.log("  divide(5, 0) =", divide(5, 0));
  } catch (e) {
    console.log(`  Caught ${e.constructor.name}: ${e.message}`);
  } finally {
    console.log("  finally always runs");
  }

  console.log("\n✓ JavaScript Fundamentals Part 2 demo complete.");
}
