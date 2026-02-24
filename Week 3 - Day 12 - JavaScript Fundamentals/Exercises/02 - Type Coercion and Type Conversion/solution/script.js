// Exercise 02: Type Coercion and Type Conversion — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Implicit Coercion
// ─────────────────────────────────────────────

console.log("--- Implicit Coercion ---");

// + with a string operand → string concatenation; "5" is not converted to a number
console.log(`"5" + 3 → ${"5" + 3}          (+ with a string triggers concatenation)`);

// - has no string meaning → JS coerces "5" to the number 5 first
console.log(`"5" - 3 → ${"5" - 3}           (- forces numeric coercion)`);

// Both strings coerced to numbers for multiplication
console.log(`"5" * "2" → ${"5" * "2"}`);

// true coerces to 1 in a numeric context
console.log(`true + 1 → ${true + 1}          (true coerces to 1)`);

// + with a string → concatenation; false becomes the string "false"
console.log(`false + "value" → ${false + "value"}`);

// null coerces to 0 in numeric context
console.log(`null + 1 → ${null + 1}          (null coerces to 0)`);

// undefined → NaN in numeric context; any arithmetic with NaN → NaN
console.log(`undefined + 1 → ${undefined + 1}`);

// ─────────────────────────────────────────────
// PART 2: Loose (==) vs Strict (===) Equality
// ─────────────────────────────────────────────

console.log("\n--- Loose vs Strict Equality ---");

// == coerces: false → 0, then 0 == 0 → true
console.log(`0 == false  → ${0 == false}`);

// === no coercion: number vs boolean → false immediately
console.log(`0 === false → ${0 === false}`);

// == coerces: "" → 0, false → 0, then 0 == 0 → true
console.log(`"" == false → ${"" == false}`);

// Special rule: null == undefined is true (only these two equal each other with ==)
console.log(`null == undefined  → ${null == undefined}`);

// === no coercion: different types → false
console.log(`null === undefined → ${null === undefined}`);

// NaN is the only value that is not equal to itself
console.log(`NaN == NaN → ${NaN == NaN}`);

/*
 * Prefer === (strict equality) in almost all cases because:
 *  - It never silently converts types, so the comparison is predictable.
 *  - Bugs from unexpected coercions (like "" == false) are eliminated.
 *  - The only common exception: `value == null` intentionally catches both null and undefined.
 */

// ─────────────────────────────────────────────
// PART 3: Explicit Conversion
// ─────────────────────────────────────────────

console.log("\n--- Explicit Conversion: Number() ---");

console.log(`Number("42") → ${Number("42")}`);
console.log(`Number("3.14") → ${Number("3.14")}`);
console.log(`Number("") → ${Number("")}`);       // empty string → 0
console.log(`Number("abc") → ${Number("abc")}`); // non-numeric string → NaN
console.log(`Number(true) → ${Number(true)}`);   // true → 1
console.log(`Number(false) → ${Number(false)}`); // false → 0
console.log(`Number(null) → ${Number(null)}`);   // null → 0
console.log(`Number(undefined) → ${Number(undefined)}`); // undefined → NaN

console.log("\n--- Explicit Conversion: String() ---");

console.log(`String(42) → "${String(42)}"`);
console.log(`String(true) → "${String(true)}"`);
console.log(`String(null) → "${String(null)}"`);
console.log(`String(undefined) → "${String(undefined)}"`);

console.log("\n--- Explicit Conversion: Boolean() ---");

// The six falsy values
console.log(`Boolean(0) → ${Boolean(0)}`);
console.log(`Boolean("") → ${Boolean("")}`);
console.log(`Boolean(null) → ${Boolean(null)}`);
console.log(`Boolean(undefined) → ${Boolean(undefined)}`);
console.log(`Boolean(NaN) → ${Boolean(NaN)}`);
// Everything else is truthy — including empty arrays and objects
console.log(`Boolean("hello") → ${Boolean("hello")}`);
console.log(`Boolean(1) → ${Boolean(1)}`);
console.log(`Boolean([]) → ${Boolean([])}`);   // empty array is TRUTHY
console.log(`Boolean({}) → ${Boolean({})}`);   // empty object is TRUTHY

// ─────────────────────────────────────────────
// PART 4: parseInt and parseFloat
// ─────────────────────────────────────────────

console.log("\n--- parseInt / parseFloat ---");

// Number() fails on mixed strings — returns NaN
console.log(`Number("42px") → ${Number("42px")}`);

// parseInt reads leading digits and stops at the first non-numeric character
console.log(`parseInt("42px") → ${parseInt("42px")}`);

// parseFloat does the same but handles decimal points
console.log(`parseFloat("3.14abc") → ${parseFloat("3.14abc")}`);

// Second argument (radix) tells parseInt the base — 16 for hexadecimal
console.log(`parseInt("0xFF", 16) → ${parseInt("0xFF", 16)}`);

// ─────────────────────────────────────────────
// PART 5: isNaN vs Number.isNaN
// ─────────────────────────────────────────────

console.log("\n--- isNaN vs Number.isNaN ---");

// isNaN coerces its argument to a number FIRST, then checks
console.log(`isNaN("hello") → ${isNaN("hello")}`);       // Number("hello") → NaN → true
console.log(`isNaN(undefined) → ${isNaN(undefined)}`);   // Number(undefined) → NaN → true
console.log(`isNaN(NaN) → ${isNaN(NaN)}`);               // true (correct)

// Number.isNaN does NOT coerce — only returns true for the actual NaN value
console.log(`Number.isNaN("hello") → ${Number.isNaN("hello")}`);     // false (it's a string, not NaN)
console.log(`Number.isNaN(undefined) → ${Number.isNaN(undefined)}`); // false (it's undefined)
console.log(`Number.isNaN(NaN) → ${Number.isNaN(NaN)}`);             // true

// Number.isNaN is safer: it won't give false positives for strings and undefined
