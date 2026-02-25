// Day 12 Part 1 — JavaScript Fundamentals
// Topics: Variables (var/let/const), Data Types, Coercion, Arrays, Template Literals
// Run: node index.js

"use strict";

console.log("╔══════════════════════════════════════════════════════════════╗");
console.log("║  Day 12 Part 1 — JavaScript Fundamentals                    ║");
console.log("╚══════════════════════════════════════════════════════════════╝\n");

demoVariables();
demoDataTypes();
demoTypeCoercion();
demoArrays();
demoTemplateLiterals();

// ─────────────────────────────────────────────────────────────
// 1. Variables — var, let, const and Scope
// ─────────────────────────────────────────────────────────────
function demoVariables() {
  console.log("=== 1. Variables & Scope ===");

  // const — block-scoped, must be initialized, cannot be reassigned
  const PI = 3.14159;
  console.log(`  const PI = ${PI}  (cannot be reassigned)`);

  // let — block-scoped, reassignable
  let score = 0;
  score = 100;
  console.log(`  let score = ${score}  (block-scoped, reassignable)`);

  // var — function-scoped (avoid in modern JS)
  var legacy = "old school";
  console.log(`  var legacy = "${legacy}"  (function-scoped, hoisted)`);

  // Block scope demo
  {
    let blockVar = "inside block";
    console.log("  blockVar inside block:", blockVar);
  }
  // console.log(blockVar);  // ← ReferenceError: not accessible here

  // Hoisting with var vs let
  console.log("  hoistedVar before declaration:", typeof hoistedVar); // undefined, NOT error
  var hoistedVar = "hoisted";
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 2. Data Types
// ─────────────────────────────────────────────────────────────
function demoDataTypes() {
  console.log("=== 2. Data Types ===");

  // Primitives (immutable, stored by value)
  const str    = "Hello World";    // string
  const num    = 42.5;             // number (no int/float distinction)
  const bool   = true;             // boolean
  const nothing = null;            // null (intentional absence)
  let notAssigned;                 // undefined (declared, not assigned)
  const sym    = Symbol("id");     // symbol (unique key)
  const bigNum = 9007199254740993n; // BigInt

  console.log("  Primitives:");
  console.log(`    string:    "${str}"  → typeof = ${typeof str}`);
  console.log(`    number:    ${num}    → typeof = ${typeof num}`);
  console.log(`    boolean:   ${bool}   → typeof = ${typeof bool}`);
  console.log(`    null:      ${nothing}  → typeof = ${typeof nothing}  ← famous JS quirk!`);
  console.log(`    undefined: ${notAssigned}  → typeof = ${typeof notAssigned}`);
  console.log(`    symbol:    ${sym.toString()}`);
  console.log(`    BigInt:    ${bigNum}n`);

  // Reference types (mutable, stored by reference)
  const obj   = { name: "Alice", age: 25 };
  const arr   = [1, 2, 3];
  const fn    = function() {};
  console.log("\n  Reference types:");
  console.log(`    object:    ${JSON.stringify(obj)}  → typeof = ${typeof obj}`);
  console.log(`    array:     [${arr}]  → typeof = ${typeof arr}  (arrays ARE objects!)`);
  console.log(`    function:  → typeof = ${typeof fn}`);
  console.log(`    Array.isArray([]):  ${Array.isArray(arr)}`);
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 3. Type Coercion
// ─────────────────────────────────────────────────────────────
function demoTypeCoercion() {
  console.log("=== 3. Type Coercion ===");

  // Implicit coercion (automatic)
  console.log("  Implicit coercion:");
  console.log(`    "5" + 3  = ${"5" + 3}   ← string concatenation`);
  console.log(`    "5" - 3  = ${"5" - 3}   ← numeric subtraction`);
  console.log(`    true + 1 = ${true + 1}   ← true becomes 1`);
  console.log(`    false + 1= ${false + 1}  ← false becomes 0`);
  console.log(`    null + 1 = ${null + 1}   ← null becomes 0`);
  console.log(`    "" == 0  = ${"" == 0}  ← loose equality coerces`);
  console.log(`    "" === 0 = ${"" === 0} ← strict equality, no coercion`);

  // Explicit conversion
  console.log("\n  Explicit conversion:");
  console.log(`    Number("42")  = ${Number("42")}`);
  console.log(`    Number("")    = ${Number("")}   ← empty string → 0`);
  console.log(`    Number("abc") = ${Number("abc")} ← non-numeric → NaN`);
  console.log(`    String(100)   = "${String(100)}"`);
  console.log(`    Boolean(0)    = ${Boolean(0)}   ← falsy`);
  console.log(`    Boolean("hi") = ${Boolean("hi")} ← truthy`);
  console.log(`    parseInt("42px") = ${parseInt("42px")}`);
  console.log(`    parseFloat("3.14abc") = ${parseFloat("3.14abc")}`);

  // Falsy values
  const falsyValues = [0, "", null, undefined, NaN, false];
  console.log("\n  Falsy values:", falsyValues.filter(v => !v).map(v => String(v)));
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 4. Arrays & Array Methods
// ─────────────────────────────────────────────────────────────
function demoArrays() {
  console.log("=== 4. Arrays & Array Methods ===");

  const fruits = ["apple", "banana", "cherry", "date", "elderberry"];
  console.log("  fruits:", fruits);
  console.log("  fruits[1]:", fruits[1]);
  console.log("  fruits.length:", fruits.length);

  // Mutation methods
  const stack = [1, 2, 3];
  stack.push(4);        // add to end
  stack.unshift(0);     // add to start
  const last  = stack.pop();    // remove from end
  const first = stack.shift();  // remove from start
  console.log("\n  push/pop/shift/unshift result:", stack, `(popped: ${last}, shifted: ${first})`);

  // Non-mutating (return new array)
  const nums = [5, 1, 4, 2, 8, 3];
  console.log("\n  Original:", nums);
  console.log("  .slice(1,4):", nums.slice(1, 4));
  console.log("  .filter(n>3):", nums.filter(n => n > 3));
  console.log("  .map(n*2):", nums.map(n => n * 2));
  console.log("  .reduce(sum):", nums.reduce((acc, n) => acc + n, 0));
  console.log("  .find(n>4):", nums.find(n => n > 4));
  console.log("  .every(n>0):", nums.every(n => n > 0));
  console.log("  .some(n>7):", nums.some(n => n > 7));
  console.log("  .sort():", [...nums].sort((a, b) => a - b));
  console.log("  .includes(4):", nums.includes(4));
  console.log("  .indexOf(8):", nums.indexOf(8));
  console.log("  .flat():", [[1,2],[3,[4,5]]].flat(Infinity));
  console.log();
}

// ─────────────────────────────────────────────────────────────
// 5. Template Literals
// ─────────────────────────────────────────────────────────────
function demoTemplateLiterals() {
  console.log("=== 5. Template Literals ===");

  const name = "Alice";
  const score = 95;
  const passed = score >= 60;

  // Interpolation
  console.log(`  Hello, ${name}! Your score is ${score}.`);

  // Expressions inside ${}
  console.log(`  Status: ${passed ? "PASSED ✓" : "FAILED ✗"}`);
  console.log(`  Double score: ${score * 2}`);

  // Multi-line string
  const report = `
  ┌─────────────────────────────┐
  │  Student Report             │
  │  Name:  ${name.padEnd(18)}  │
  │  Score: ${String(score).padEnd(18)}  │
  │  Grade: ${ score >= 90 ? "A" : score >= 80 ? "B" : "C" }                   │
  └─────────────────────────────┘`;
  console.log(report);

  // Tagged template literal
  function highlight(strings, ...values) {
    return strings.reduce((result, str, i) => {
      const val = values[i] !== undefined ? `[${values[i]}]` : "";
      return result + str + val;
    }, "");
  }
  console.log("  Tagged:", highlight`Student ${name} scored ${score} and ${passed ? "passed" : "failed"}`);
  console.log("\n✓ JavaScript Fundamentals Part 1 demo complete.");
}
