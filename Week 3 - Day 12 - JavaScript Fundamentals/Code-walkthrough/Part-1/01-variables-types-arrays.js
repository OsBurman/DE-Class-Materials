// =============================================================================
// DAY 12 — PART 1 | JavaScript Fundamentals
// File: 01-variables-types-arrays.js
//
// Topics covered:
//   1. JavaScript basics and syntax
//   2. Variables (var, let, const) and scope
//   3. Data types (primitives and objects)
//   4. Type coercion and type conversion
//   5. Arrays and array methods
//   6. Template literals
// =============================================================================


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — JavaScript Basics & Syntax
// ─────────────────────────────────────────────────────────────────────────────

// Single-line comment — ignored by the JavaScript engine
/*
  Multi-line comment — useful for longer explanations
  or for temporarily disabling blocks of code.
*/

// Statements end with a semicolon (optional but recommended)
console.log("Hello, JavaScript!"); // Outputs to the browser console or Node.js terminal

// JavaScript is case-sensitive
// 'name', 'Name', and 'NAME' are three completely different identifiers

// typeof — returns a string describing the type of a value
console.log(typeof 42);          // "number"
console.log(typeof "hello");     // "string"
console.log(typeof true);        // "boolean"
console.log(typeof undefined);   // "undefined"
console.log(typeof null);        // "object"  ← famous JS quirk (a historical bug)
console.log(typeof {});          // "object"
console.log(typeof []);          // "object"  ← arrays ARE objects in JS
console.log(typeof function(){}); // "function"


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 — Variables: var, let, const
// ─────────────────────────────────────────────────────────────────────────────

// ── 2a. var (function-scoped, avoid in modern code) ──────────────────────────
var courseName = "Full-Stack Engineering";  // function scope
var courseName = "Advanced Java";           // var allows re-declaration — no error!
console.log(courseName);                    // "Advanced Java"

function demonstrateVarScope() {
  var localVar = "I exist only inside this function";
  if (true) {
    var blockVar = "var ignores block scope — I leak out of the if-block!";
  }
  console.log(blockVar); // "var ignores block scope..." — accessible here
}
demonstrateVarScope();
// console.log(localVar); // ReferenceError — localVar is not defined outside the function

// ── 2b. let (block-scoped, reassignable) ─────────────────────────────────────
let studentCount = 24;
studentCount = 25;          // reassignment ✓
// let studentCount = 30;  // SyntaxError — cannot re-declare in same scope

if (true) {
  let blockScoped = "I only exist inside this if-block";
  console.log(blockScoped); // works fine here
}
// console.log(blockScoped); // ReferenceError — not accessible outside the block

// ── 2c. const (block-scoped, not reassignable) ───────────────────────────────
const MAX_STUDENTS = 30;
// MAX_STUDENTS = 31;   // TypeError — assignment to constant variable

// IMPORTANT: const with objects — the binding is constant, not the contents
const instructor = { name: "Sarah", level: "Senior" };
instructor.level = "Lead";      // ✓ mutating a property is allowed
// instructor = { name: "Bob" }; // TypeError — can't reassign the variable itself
console.log(instructor);        // { name: "Sarah", level: "Lead" }

// ── 2d. Scope summary ────────────────────────────────────────────────────────
// var   → function scope (or global if declared outside any function)
// let   → block scope  { }
// const → block scope  { }
//
// RULE OF THUMB: default to const; use let when you need to reassign; avoid var.


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3 — Data Types
// ─────────────────────────────────────────────────────────────────────────────

// ── 3a. Primitive types (immutable, stored by VALUE) ─────────────────────────

const age        = 28;             // Number  (JS has ONE number type — no int/float distinction)
const price      = 9.99;           // Number  (decimals use the same type)
const largeNum   = 9_007_199_254_740_991; // Number — max safe integer
const bigInt     = 9007199254740993n;     // BigInt — for integers beyond Number.MAX_SAFE_INTEGER
const greeting   = "Hello, world!";       // String
const singleQ    = 'Single quotes work too';
const isEnrolled = true;                  // Boolean
const isGraduated = false;
let   pendingGrade;                        // undefined — declared but not assigned
const deletedRecord = null;               // null — intentional absence of a value
const trackId    = Symbol("track");       // Symbol — unique & immutable identifier

console.log(Number.MAX_SAFE_INTEGER);     // 9007199254740991

// ── 3b. Reference types (mutable, stored by REFERENCE) ───────────────────────

// Object — key/value pairs
const student = {
  firstName: "Jamie",
  lastName:  "Chen",
  age:       22,
  enrolled:  true,
  address: {               // nested object
    city: "Austin",
    state: "TX"
  }
};

// Accessing object properties
console.log(student.firstName);           // dot notation → "Jamie"
console.log(student["lastName"]);         // bracket notation → "Chen"
console.log(student.address.city);        // chaining → "Austin"

// Array — ordered list (values can be mixed types)
const grades = [95, 87, 72, 100, 68];

// Function — also a first-class object in JavaScript
function sayHello() { return "Hello!"; }

// ── 3c. Primitive vs reference — the copy behaviour difference ────────────────

// Primitives — copy by value
let a = 10;
let b = a;   // b gets its own copy of 10
b = 20;
console.log(a); // 10 — a is unchanged

// Objects — copy by reference
let original = { score: 100 };
let copy = original;   // copy points to the SAME object in memory
copy.score = 999;
console.log(original.score); // 999 — original was also changed!

// To make an independent shallow copy, use spread or Object.assign:
let trulyCopied = { ...original };
trulyCopied.score = 0;
console.log(original.score); // 999 — original is safe now


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Type Coercion & Type Conversion
// ─────────────────────────────────────────────────────────────────────────────

// ── 4a. Implicit coercion — JavaScript converts automatically ─────────────────

// String + anything = String concatenation
console.log("Score: " + 95);      // "Score: 95"
console.log("3" + 4);             // "34"  ← not 7!
console.log("3" - 1);             // 2     ← minus triggers numeric conversion
console.log("6" * "2");           // 12    ← both coerced to numbers
console.log(true + 1);            // 2     ← true → 1
console.log(false + 1);           // 1     ← false → 0
console.log(null + 1);            // 1     ← null → 0
console.log(undefined + 1);       // NaN   ← undefined → NaN

// Loose equality (==) performs coercion before comparing
console.log(0 == false);          // true  ← coercion
console.log("" == false);         // true  ← coercion
console.log(null == undefined);   // true  ← special case
console.log(0 == "0");            // true  ← coercion

// Strict equality (===) — NO coercion, both type AND value must match
console.log(0 === false);         // false ← different types
console.log("5" === 5);           // false ← string !== number
// RULE: Always use === unless you explicitly need coercion (rare)

// ── 4b. Explicit (manual) type conversion ────────────────────────────────────

// To Number
console.log(Number("42"));        // 42
console.log(Number("3.14"));      // 3.14
console.log(Number(""));          // 0
console.log(Number("hello"));     // NaN
console.log(Number(true));        // 1
console.log(Number(false));       // 0
console.log(Number(null));        // 0
console.log(Number(undefined));   // NaN
console.log(parseInt("42px"));    // 42   — stops at first non-numeric character
console.log(parseFloat("3.14em")); // 3.14

// To String
console.log(String(123));         // "123"
console.log(String(true));        // "true"
console.log(String(null));        // "null"
console.log((255).toString(16));  // "ff"  — hex representation
console.log((8).toString(2));     // "1000" — binary representation

// To Boolean
console.log(Boolean(0));          // false
console.log(Boolean(""));         // false
console.log(Boolean(null));       // false
console.log(Boolean(undefined));  // false
console.log(Boolean(NaN));        // false
// ALL OTHER values are truthy:
console.log(Boolean(1));          // true
console.log(Boolean("hello"));    // true
console.log(Boolean([]));         // true  ← empty array is truthy!
console.log(Boolean({}));         // true  ← empty object is truthy!

// NaN — Not a Number — the only value in JS not equal to itself
console.log(NaN === NaN);         // false  ← use Number.isNaN() instead
console.log(Number.isNaN(NaN));   // true
console.log(Number.isNaN("hello")); // false — "hello" isn't NaN, it IS a string


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Arrays & Array Methods
// ─────────────────────────────────────────────────────────────────────────────

// ── 5a. Creating and accessing arrays ────────────────────────────────────────
const bootcampModules = ["HTML/CSS", "JavaScript", "React", "Java", "Spring Boot", "SQL"];

console.log(bootcampModules[0]);        // "HTML/CSS"  — zero-indexed
console.log(bootcampModules.length);    // 6
console.log(bootcampModules[bootcampModules.length - 1]); // "SQL" — last element

// ── 5b. Mutating methods (modify the original array) ─────────────────────────
const scores = [85, 92, 78];

scores.push(99);             // add to end   → [85, 92, 78, 99]
scores.pop();                // remove from end → [85, 92, 78]
scores.unshift(70);          // add to start  → [70, 85, 92, 78]
scores.shift();              // remove from start → [85, 92, 78]

scores.splice(1, 1, 88);    // at index 1, remove 1 item, insert 88
console.log(scores);         // [85, 88, 78]

scores.sort((a, b) => a - b);  // sort ascending (numeric comparator)
console.log(scores);            // [78, 85, 88]

scores.reverse();               // reverse in place
console.log(scores);            // [88, 85, 78]

// ── 5c. Non-mutating methods (return new array / value) ──────────────────────
const students = ["Alice", "Bob", "Carol", "Dave", "Eve"];

// slice(start, end) — returns a portion (end index NOT included)
const firstThree = students.slice(0, 3);
console.log(firstThree); // ["Alice", "Bob", "Carol"]
console.log(students);   // original unchanged

// concat — combines arrays without mutating
const newStudents = ["Frank", "Grace"];
const allStudents = students.concat(newStudents);
console.log(allStudents); // ["Alice", "Bob", "Carol", "Dave", "Eve", "Frank", "Grace"]

// indexOf / includes — finding elements
console.log(students.indexOf("Carol")); // 2
console.log(students.indexOf("Zara"));  // -1 (not found)
console.log(students.includes("Bob"));  // true

// join — array to string
console.log(bootcampModules.join(" → ")); // "HTML/CSS → JavaScript → React → Java → Spring Boot → SQL"

// ── 5d. Higher-order array methods (functional style) ────────────────────────
const examScores = [45, 72, 88, 91, 60, 55, 79, 95];

// forEach — iterate without returning anything
console.log("All scores:");
examScores.forEach((score, index) => {
  console.log(`  Student ${index + 1}: ${score}`);
});

// map — transform each element, returns new array of same length
const letterGrades = examScores.map(score => {
  if (score >= 90) return "A";
  if (score >= 80) return "B";
  if (score >= 70) return "C";
  if (score >= 60) return "D";
  return "F";
});
console.log(letterGrades); // ["F", "C", "B", "A", "D", "F", "C", "A"]

// filter — keep only elements where callback returns true
const passingScores = examScores.filter(score => score >= 60);
console.log(passingScores); // [72, 88, 91, 60, 79, 95]

// find — returns FIRST matching element (or undefined)
const firstFailure = examScores.find(score => score < 60);
console.log(firstFailure); // 45

// findIndex — returns index of FIRST match (or -1)
const firstFailureIndex = examScores.findIndex(score => score < 60);
console.log(firstFailureIndex); // 0

// some — true if AT LEAST ONE element passes the test
console.log(examScores.some(score => score === 100)); // false
console.log(examScores.some(score => score >= 90));   // true

// every — true if ALL elements pass the test
console.log(examScores.every(score => score >= 40));  // true
console.log(examScores.every(score => score >= 60));  // false

// reduce — accumulate a single value from the array
const totalScore = examScores.reduce((accumulator, currentScore) => {
  return accumulator + currentScore;
}, 0); // 0 is the initial value
const average = totalScore / examScores.length;
console.log(`Total: ${totalScore}, Average: ${average.toFixed(1)}`);

// flat — flatten nested arrays
const nested = [[1, 2], [3, 4], [5, 6]];
console.log(nested.flat()); // [1, 2, 3, 4, 5, 6]

// flatMap — map then flatten (one level deep)
const words = ["hello world", "foo bar"];
console.log(words.flatMap(s => s.split(" "))); // ["hello", "world", "foo", "bar"]

// Spread operator with arrays
const front = [1, 2, 3];
const back  = [7, 8, 9];
const combined = [...front, 4, 5, 6, ...back];
console.log(combined); // [1, 2, 3, 4, 5, 6, 7, 8, 9]

// Array destructuring
const [first, second, ...rest] = bootcampModules;
console.log(first);  // "HTML/CSS"
console.log(second); // "JavaScript"
console.log(rest);   // ["React", "Java", "Spring Boot", "SQL"]

// Array.from — create array from array-like / iterable
const letters = Array.from("hello");
console.log(letters); // ["h", "e", "l", "l", "o"]

const range = Array.from({ length: 5 }, (_, i) => i + 1);
console.log(range);   // [1, 2, 3, 4, 5]


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6 — Template Literals
// ─────────────────────────────────────────────────────────────────────────────

const firstName = "Jordan";
const cohort    = "Spring 2026";
const daysLeft  = 42;

// ── 6a. String interpolation with ${} ─────────────────────────────────────────
const welcome = `Welcome, ${firstName}! You are enrolled in the ${cohort} cohort.`;
console.log(welcome);

// ── 6b. Expressions inside ${} ───────────────────────────────────────────────
const scoreReport = `You have ${daysLeft} days left. That's ${daysLeft * 24} hours.`;
console.log(scoreReport);

// Ternary inside template literal
const passFail = (score) => `Score: ${score} — ${score >= 60 ? "PASS" : "FAIL"}`;
console.log(passFail(75)); // "Score: 75 — PASS"
console.log(passFail(45)); // "Score: 45 — FAIL"

// ── 6c. Multi-line strings ────────────────────────────────────────────────────
const emailBody = `
Dear ${firstName},

Your enrollment in ${cohort} has been confirmed.
${daysLeft} days until your first session.

Best regards,
The Admissions Team
`.trim(); // .trim() removes the leading/trailing newline
console.log(emailBody);

// ── 6d. Tagged template literals (advanced preview) ──────────────────────────
// A tag function lets you process a template literal programmatically
function highlight(strings, ...values) {
  return strings.reduce((result, str, i) => {
    const value = values[i] !== undefined ? `[${values[i]}]` : "";
    return result + str + value;
  }, "");
}
const course = "Spring Boot";
const duration = 5;
console.log(highlight`${course} runs for ${duration} days.`);
// "[Spring Boot] runs for [5] days."

// ── 6e. Comparing old-style concatenation vs template literals ────────────────
// Old (string concatenation — error-prone, hard to read)
const oldStyle = "Hello, " + firstName + "! Cohort: " + cohort + ". Days left: " + daysLeft;

// New (template literal — readable, easy to embed expressions)
const newStyle = `Hello, ${firstName}! Cohort: ${cohort}. Days left: ${daysLeft}`;

console.log(oldStyle === newStyle); // true — same result, but new style is better
