// Exercise 03: Template Literals and String Methods — SOLUTION
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Template Literal Basics
// ─────────────────────────────────────────────

const firstName = "Ada";
const lastName = "Lovelace";
const year = 1815;

// Interpolate two variables into a single string with backticks
console.log(`Full name: ${firstName} ${lastName}`);

// Embed an arithmetic expression directly inside ${ }
console.log(`Born: ${year}, approximately ${2026 - year} years ago`);

// Multi-line template literal — actual newlines are preserved in the string
const address = `123 Main Street
Springfield, IL
62701`;
console.log(address);

// ─────────────────────────────────────────────
// PART 2: Case Conversion
// ─────────────────────────────────────────────

const sentence = "  JavaScript is Awesome!  ";

console.log(sentence.toUpperCase()); // spaces included
console.log(sentence.toLowerCase());
console.log(sentence.trim());        // strips leading and trailing whitespace only

// ─────────────────────────────────────────────
// PART 3: Search and Check Methods
// ─────────────────────────────────────────────

const trimmed = sentence.trim(); // "JavaScript is Awesome!"

// includes/startsWith/endsWith all return booleans
console.log(`includes "Awesome": ${trimmed.includes("Awesome")}`);
console.log(`startsWith "Java": ${trimmed.startsWith("Java")}`);
console.log(`endsWith "!": ${trimmed.endsWith("!")}`);
// indexOf returns the 0-based position of the first match; -1 if not found
console.log(`indexOf "is": ${trimmed.indexOf("is")}`);

// ─────────────────────────────────────────────
// PART 4: Extract and Replace
// ─────────────────────────────────────────────

// slice(start, end) — end is exclusive; characters 0-9 → "JavaScript"
console.log(`slice(0,10): ${trimmed.slice(0, 10)}`);

// replace replaces only the FIRST occurrence
console.log(`replace: ${trimmed.replace("Awesome", "Powerful")}`);

const csv = "one,two,three,four";
// replaceAll replaces every occurrence (ES2021+)
console.log(`replaceAll commas: ${csv.replaceAll(",", " | ")}`);

// ─────────────────────────────────────────────
// PART 5: Split and Join
// ─────────────────────────────────────────────

const parts = csv.split(","); // split string into array by delimiter
console.log(`split csv:`, parts);

// join is the inverse of split — joins array elements into a string
console.log(`join with " - ": ${parts.join(" - ")}`);

// ─────────────────────────────────────────────
// PART 6: Padding and Repeat
// ─────────────────────────────────────────────

// padStart(targetLength, padString) — pads from the left
console.log(`padStart(15, "*"): ${"hello".padStart(15, "*")}`);

// padEnd(targetLength, padString) — pads from the right
console.log(`padEnd(15, "-"): ${"hello".padEnd(15, "-")}`);

// repeat(n) — concatenates the string n times
console.log(`repeat(3): ${"AB".repeat(3)}`);

// ─────────────────────────────────────────────
// PART 7: String to Number Round-Trip
// ─────────────────────────────────────────────

const price = 19.99;
const priceString = `Price: $${price}`; // double $ — first is the literal $, second starts template expression
console.log(`Price string: ${priceString}`);

// "Price: $19.99" — characters 0-8 are "Price: $", so slice from index 9 to get "19.99"
const extracted = priceString.slice(9);
const asNumber = Number(extracted);

console.log(`Extracted number: ${asNumber}`);
console.log(`typeof extracted: ${typeof asNumber}`);
