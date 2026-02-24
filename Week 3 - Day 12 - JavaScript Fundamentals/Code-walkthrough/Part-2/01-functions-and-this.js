// =============================================================================
// DAY 12 — PART 2 | File 1: Functions & the 'this' Keyword
// File: 01-functions-and-this.js
//
// Topics covered:
//   1. Function declarations
//   2. Function expressions
//   3. Arrow functions
//   4. 'this' keyword and context
// =============================================================================


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — Function Declarations
// ─────────────────────────────────────────────────────────────────────────────

// A function declaration uses the 'function' keyword and a name.
// It is HOISTED — you can call it BEFORE it appears in the file (see 02-closures-and-hoisting.js)

function greet(name) {
  return `Hello, ${name}!`;
}

console.log(greet("Jordan")); // "Hello, Jordan!"

// Multiple parameters and a default parameter value
function createCourseMessage(course, instructor, level = "beginner") {
  return `${course} is taught by ${instructor}. Recommended level: ${level}.`;
}

console.log(createCourseMessage("JavaScript", "Alex"));              // uses default
console.log(createCourseMessage("Spring Boot", "Maria", "advanced")); // overrides default

// Functions can return any type — including objects and arrays
function buildStudent(name, scores) {
  const average = scores.reduce((sum, s) => sum + s, 0) / scores.length;
  return {
    name,          // shorthand property — same as name: name
    scores,
    average: Math.round(average * 10) / 10,
    passing: average >= 60
  };
}

const studentRecord = buildStudent("Jamie", [78, 92, 85, 66, 90]);
console.log(studentRecord);
// { name: "Jamie", scores: [...], average: 82.2, passing: true }

// Functions without a return statement return undefined
function logMessage(msg) {
  console.log(`[LOG] ${msg}`);
  // no return → implicit return undefined
}

console.log(logMessage("Server started")); // logs the message, then prints undefined

// Rest parameters — collects remaining arguments into an array
function sum(...numbers) {
  return numbers.reduce((total, n) => total + n, 0);
}

console.log(sum(1, 2, 3));         // 6
console.log(sum(10, 20, 30, 40));  // 100


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Function Expressions
// ─────────────────────────────────────────────────────────────────────────────

// A function expression assigns an anonymous function to a variable.
// NOT hoisted — cannot be called before its declaration in the file.

const multiply = function(a, b) {
  return a * b;
};

console.log(multiply(6, 7)); // 42

// Named function expression — useful for recursion and stack traces
const factorial = function calcFactorial(n) {
  if (n <= 1) return 1;
  return n * calcFactorial(n - 1);  // can reference itself by the internal name
};

console.log(factorial(5)); // 120

// Functions are first-class values in JavaScript:
// you can store them in arrays, objects, or pass them to other functions

const mathOps = {
  add:      function(a, b) { return a + b; },
  subtract: function(a, b) { return a - b; },
  multiply: function(a, b) { return a * b; }
};

console.log(mathOps.add(10, 3));      // 13
console.log(mathOps.subtract(10, 3)); // 7

// Passing a function as an argument (callback pattern)
function applyOperation(a, b, operationFn) {
  return operationFn(a, b);
}

console.log(applyOperation(8, 4, mathOps.multiply)); // 32
console.log(applyOperation(9, 3, function(a, b) { return a / b; })); // 3


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Arrow Functions
// ─────────────────────────────────────────────────────────────────────────────

// Arrow functions — introduced in ES6. Shorter syntax.
// Key difference: arrow functions do NOT have their own 'this' binding.

// Full syntax
const divide = (a, b) => {
  return a / b;
};

// Concise body — single expression, implicit return (no 'return' keyword needed)
const square = x => x * x;
const add    = (a, b) => a + b;

console.log(divide(20, 4)); // 5
console.log(square(9));     // 81
console.log(add(3, 7));     // 10

// Returning an object literal — must wrap in parentheses to avoid { } being
// interpreted as a function body
const makePoint = (x, y) => ({ x, y });
console.log(makePoint(3, 4)); // { x: 3, y: 4 }

// Arrow functions shine as callbacks — far less typing than function expressions
const temperatures = [32, 18, 25, 41, 10, 37];

const aboveFreezing = temperatures.filter(t => t > 0);
const inCelsius     = aboveFreezing.map(t => ((t - 32) * 5) / 9);
const warmDays      = inCelsius.filter(c => c > 20);

console.log(aboveFreezing); // [32, 18, 25, 41, 10, 37]
console.log(inCelsius.map(n => Math.round(n))); // [0, -8, -4, 5, -12, 3]

// Immediately Invoked Function Expression (IIFE)
// A function that calls itself right away — used to create a private scope
const result = (() => {
  const privateData = "I can't be accessed from outside";
  return `Processed: ${privateData}`;
})();
console.log(result);


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — 'this' Keyword and Context
// ─────────────────────────────────────────────────────────────────────────────
// 'this' refers to the CALLING CONTEXT — the object that "owns" the function call.
// The value of 'this' is determined at CALL TIME, not at definition time.
// (Arrow functions are the exception — they capture 'this' from their enclosing scope)

// ── 4a. Global context ────────────────────────────────────────────────────────
// In a browser, 'this' at the top level is the window object.
// In Node.js strict mode, 'this' at the top level is {} (empty module object).
// console.log(this); // {} in Node.js module; window in browser

// ── 4b. 'this' inside an object method ───────────────────────────────────────
const userProfile = {
  username: "dev_sarah",
  level:    "Senior",
  greet() {
    // 'this' refers to userProfile because userProfile called this method
    return `Hi, I'm ${this.username} — a ${this.level} developer.`;
  },
  getContext() {
    return this; // returns the entire userProfile object
  }
};

console.log(userProfile.greet());
// "Hi, I'm dev_sarah — a Senior developer."

// ── 4c. Losing 'this' — the classic gotcha ────────────────────────────────────
const greetFn = userProfile.greet;  // extract the method into a variable
// When called as a plain function (not via userProfile), 'this' is no longer userProfile
// In strict mode this would throw TypeError (this is undefined)
// In non-strict mode 'this' is the global object — neither is what we want

// ── 4d. Fixing 'this' with bind() ─────────────────────────────────────────────
const boundGreet = userProfile.greet.bind(userProfile);
console.log(boundGreet()); // "Hi, I'm dev_sarah — a Senior developer." — works!

// bind() creates a NEW function permanently tied to the given 'this'
function introduceWith(greeting, punctuation) {
  return `${greeting}, I'm ${this.username}${punctuation}`;
}
const sarahIntro = introduceWith.bind(userProfile, "Hello");
console.log(sarahIntro("!"));  // "Hello, I'm dev_sarah!"
console.log(sarahIntro(".")); // "Hello, I'm dev_sarah."

// ── 4e. call() and apply() ────────────────────────────────────────────────────
// call — invoke immediately with explicit 'this' + individual arguments
console.log(introduceWith.call(userProfile, "Hey", "!"));  // "Hey, I'm dev_sarah!"

// apply — invoke immediately with explicit 'this' + arguments as an ARRAY
console.log(introduceWith.apply(userProfile, ["Greetings", "."])); // "Greetings, I'm dev_sarah."

// ── 4f. 'this' inside a class method ─────────────────────────────────────────
class BankAccount {
  constructor(owner, balance) {
    this.owner   = owner;
    this.balance = balance;
  }

  deposit(amount) {
    this.balance += amount;
    return `${this.owner} deposited $${amount}. New balance: $${this.balance}`;
  }

  withdraw(amount) {
    if (amount > this.balance) {
      return `Insufficient funds. Balance: $${this.balance}`;
    }
    this.balance -= amount;
    return `${this.owner} withdrew $${amount}. New balance: $${this.balance}`;
  }

  // Arrow function method — captures 'this' from the constructor context
  // Safe to use as a callback because 'this' won't be lost
  getStatement = () => {
    return `Account holder: ${this.owner} | Balance: $${this.balance}`;
  };
}

const account = new BankAccount("Jordan", 1000);
console.log(account.deposit(500));         // "Jordan deposited $500. New balance: $1500"
console.log(account.withdraw(200));        // "Jordan withdrew $200. New balance: $1300"
console.log(account.withdraw(2000));       // "Insufficient funds. Balance: $1300"

// The arrow method getStatement is safe to pass as a callback
const printStatement = account.getStatement; // extract, same gotcha as 4c...
console.log(printStatement()); // Works! Arrow function captured 'this' at construction time

// ── 4g. Arrow functions and 'this' — the key rule ─────────────────────────────
const timer = {
  label:  "Pomodoro",
  seconds: 0,

  // Regular method — 'this' is correctly 'timer' here
  start() {
    console.log(`Starting ${this.label}...`);

    // If we used a regular function in setInterval, 'this' would be lost
    // Arrow function captures 'this' from the enclosing start() method
    const tick = () => {
      this.seconds++;
      if (this.seconds <= 3) {
        console.log(`${this.label}: ${this.seconds}s elapsed`);
      }
    };

    // Simulate 3 ticks
    tick(); tick(); tick();
    console.log(`Final: ${this.seconds} seconds`);
  }
};

timer.start();
// Starting Pomodoro...
// Pomodoro: 1s elapsed
// Pomodoro: 2s elapsed
// Pomodoro: 3s elapsed
// Final: 3 seconds
