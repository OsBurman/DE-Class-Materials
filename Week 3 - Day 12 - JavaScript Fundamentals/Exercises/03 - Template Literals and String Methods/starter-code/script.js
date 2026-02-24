// Exercise 03: Template Literals and String Methods
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Template Literal Basics
// ─────────────────────────────────────────────

const firstName = "Ada";
const lastName = "Lovelace";
const year = 1815;

// TODO: Use a template literal to log: `Full name: Ada Lovelace`


// TODO: Use a template literal with an embedded expression to log:
//       `Born: 1815, approximately 211 years ago`
//       (calculate the years ago inside the template literal: 2026 - year)


// TODO: Write a multi-line template literal (using actual line breaks, not \n)
//       that outputs this 3-line address block:
//       123 Main Street
//       Springfield, IL
//       62701


// ─────────────────────────────────────────────
// PART 2: Case Conversion
// ─────────────────────────────────────────────

const sentence = "  JavaScript is Awesome!  ";

// TODO: Log sentence.toUpperCase()
// TODO: Log sentence.toLowerCase()
// TODO: Log sentence.trim()  (removes leading/trailing spaces)


// ─────────────────────────────────────────────
// PART 3: Search and Check Methods
// ─────────────────────────────────────────────

// Use the trimmed version for these — assign it to a variable first
// TODO: Declare const trimmed = sentence.trim();
// TODO: Log: trimmed.includes("Awesome")      → true
// TODO: Log: trimmed.startsWith("Java")        → true
// TODO: Log: trimmed.endsWith("!")             → true
// TODO: Log: trimmed.indexOf("is")             → the numeric index


// ─────────────────────────────────────────────
// PART 4: Extract and Replace
// ─────────────────────────────────────────────

// TODO: Log: trimmed.slice(0, 10)                          → "JavaScript"
// TODO: Log: trimmed.replace("Awesome", "Powerful")        → "JavaScript is Powerful!"

const csv = "one,two,three,four";
// TODO: Log: csv.replaceAll(",", " | ")                    → "one | two | three | four"


// ─────────────────────────────────────────────
// PART 5: Split and Join
// ─────────────────────────────────────────────

// TODO: Split csv by "," and log the resulting array
// TODO: Join the array with " - " and log the result


// ─────────────────────────────────────────────
// PART 6: Padding and Repeat
// ─────────────────────────────────────────────

// TODO: Log "hello".padStart(15, "*")   → "**********hello"
// TODO: Log "hello".padEnd(15, "-")     → "hello----------"
// TODO: Log "AB".repeat(3)              → "ABABAB"


// ─────────────────────────────────────────────
// PART 7: String to Number Round-Trip
// ─────────────────────────────────────────────

// TODO: Use a template literal to build the string "Price: $19.99"
//       (use a variable: const price = 19.99)
// TODO: Extract just "19.99" using .slice() on the built string
// TODO: Convert the extracted string to a Number
// TODO: Log the number and its typeof
