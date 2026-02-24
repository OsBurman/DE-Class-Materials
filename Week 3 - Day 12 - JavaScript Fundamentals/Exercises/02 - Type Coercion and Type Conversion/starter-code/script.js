// Exercise 02: Type Coercion and Type Conversion
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Implicit Coercion
// ─────────────────────────────────────────────

console.log("--- Implicit Coercion ---");

// TODO: Log each expression and its result. Add an inline comment explaining the coercion.
// Format: console.log(`"5" + 3 → ${"5" + 3}  (your explanation)`);

// TODO: "5" + 3

// TODO: "5" - 3

// TODO: "5" * "2"

// TODO: true + 1

// TODO: false + "value"

// TODO: null + 1

// TODO: undefined + 1


// ─────────────────────────────────────────────
// PART 2: Loose (==) vs Strict (===) Equality
// ─────────────────────────────────────────────

console.log("\n--- Loose vs Strict Equality ---");

// TODO: Log the result of each comparison with a comment explaining why:
// 1. 0 == false
// 2. 0 === false
// 3. "" == false
// 4. null == undefined
// 5. null === undefined
// 6. NaN == NaN

// TODO: Add a 3-4 line comment block summarising when to always prefer ===


// ─────────────────────────────────────────────
// PART 3: Explicit Conversion
// ─────────────────────────────────────────────

console.log("\n--- Explicit Conversion: Number() ---");

// TODO: Use Number() to convert each value and log in format: Number("42") → 42
// Values: "42", "3.14", "", "abc", true, false, null, undefined


console.log("\n--- Explicit Conversion: String() ---");

// TODO: Use String() to convert each value and log in format: String(42) → "42"
// Values: 42, true, null, undefined


console.log("\n--- Explicit Conversion: Boolean() ---");

// TODO: Use Boolean() to convert each value and log in format: Boolean(0) → false
// Values: 0, "", null, undefined, NaN, "hello", 1, [], {}


// ─────────────────────────────────────────────
// PART 4: parseInt and parseFloat
// ─────────────────────────────────────────────

console.log("\n--- parseInt / parseFloat ---");

// TODO: Log Number("42px") — show it produces NaN
// TODO: Log parseInt("42px") — show it produces 42 (parses until non-numeric char)
// TODO: Log parseFloat("3.14abc") → 3.14
// TODO: Log parseInt("0xFF", 16) → 255  (second argument is the radix)


// ─────────────────────────────────────────────
// PART 5: isNaN vs Number.isNaN
// ─────────────────────────────────────────────

console.log("\n--- isNaN vs Number.isNaN ---");

// TODO: Log isNaN("hello"), isNaN(undefined), isNaN(NaN)
// TODO: Log Number.isNaN("hello"), Number.isNaN(undefined), Number.isNaN(NaN)
// TODO: Add a comment explaining why Number.isNaN is safer than isNaN
