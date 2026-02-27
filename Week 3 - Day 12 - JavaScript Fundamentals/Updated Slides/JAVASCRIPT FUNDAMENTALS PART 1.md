SLIDE 1 — Title Slide
Slide content: "JavaScript Fundamentals" / "Variables, Data Types, Arrays & More" / Your name & date
Script (1 min): "Welcome back, everyone. Today we're digging into the core building blocks of JavaScript — the concepts that everything you write will be built on. By the end of this session you'll know how to declare variables properly, understand JavaScript's different data types, and start working with arrays. Let's get into it."

SLIDE 2 — Today's Agenda
Slide content: Bullet list of: JavaScript Basics & Syntax · Variables (var/let/const) & Scope · Data Types · Type Coercion & Conversion · Arrays & Array Methods · Template Literals
Script (1 min): "Here's our roadmap. Each topic builds on the last, so we'll go in order. I encourage you to follow along in your browser console or code editor as we go."

SLIDE 3 — JavaScript Basics & Syntax
Slide content:

Case-sensitive language
Statements end with ; (recommended)
Comments: // single line and /* multi-line */
Code runs top-to-bottom
Whitespace doesn't matter (but readability does)

Script (3 min): "Before we write real code, let's establish the ground rules. JavaScript is case-sensitive — myVariable and MyVariable are completely different things. Always watch your casing.
Semicolons end statements. JavaScript will often work without them due to Automatic Semicolon Insertion — but write them anyway. It prevents a class of subtle, hard-to-trace bugs.
Comments are written with // for a single line or /* */ for multiple lines. Use them — explain your thinking.
JavaScript runs line by line, top to bottom, unless something like a function call tells it to jump. Keep that mental model."

SLIDE 4 — Variables: What Are They?
Slide content:

Variables store data so you can reuse it
Think: labeled boxes
Three keywords: var, let, const

jsvar name = "Alice";
let age = 25;
const PI = 3.14;
Script (2 min): "A variable is a named container for a value — like a labeled box. You put something in, name it, and reference the name whenever you need it. In JavaScript you have three ways to declare a variable. They each behave differently, and picking the right one matters."

SLIDE 5 — var — The Old Way
Slide content:

Function-scoped (NOT block-scoped)
Can be redeclared AND reassigned
Gets "hoisted" — exists before the line it's written
⚠️ Avoid in modern code

jsvar x = 10;
var x = 20; // No error — bad practice!
console.log(x); // 20
Script (3 min): "var is JavaScript's original variable keyword. It has two dangerous quirks. First, it's function-scoped — declare it inside an if block or a loop and it leaks out. Second, it's hoisted — JavaScript moves var declarations to the top of their scope before running. The variable technically exists before you even wrote it, just without a value. This causes confusing bugs.
You'll see var in older code, so you need to recognize it. But never write it yourself. Use let or const."

SLIDE 6 — let — Block-Scoped
Slide content:

Block-scoped { }
Can be reassigned, NOT redeclared in same scope
Use when the value will change

jslet score = 0;
score = 10;    // ✅ fine
let score = 5; // ❌ SyntaxError
Script (2 min): "let is for values that will change — a counter, a score, a user's current input. It's block-scoped, meaning it lives and dies within its curly braces. Declare it inside a loop — it stays in the loop. This is the predictable, sensible behavior you want."

SLIDE 7 — const — Constants
Slide content:

Block-scoped
Cannot be reassigned or redeclared
Must be assigned at declaration
Default choice for values that won't change

jsconst TAX_RATE = 0.08;
TAX_RATE = 0.09; // ❌ TypeError

const user = { name: "Alice" };
user.name = "Bob"; // ✅ Object properties CAN change
Script (3 min): "const is for values that don't change. Try to reassign it — JavaScript throws an error. But here's the nuance beginners always hit: const freezes the binding, not the value. So you can't point user at a new object, but you CAN change user.name. The box is locked in place — but what's inside can be rearranged.
My rule: default to const. If you need to reassign, switch to let. Never use var."

SLIDE 8 — Scope Visual
Slide content: Diagram with three nested boxes:

🌍 Global Scope — accessible everywhere
📦 Function Scope — inside a function only
🧱 Block Scope — inside { } only
Label: var → function scope | let/const → block scope

Script (2 min): "Scope determines where in your code a variable is visible. Global — outside everything, available anywhere. Function — inside a function, stays there. Block — inside curly braces, stays there. var respects function boundaries but NOT block boundaries. let and const respect both. Scope bugs are incredibly common. Understanding this saves you hours of confusion."

SLIDE 9 — Data Types: Two Categories
Slide content:

Primitives (stored by value): string, number, boolean, null, undefined, symbol, bigint
Objects/Reference Types (stored by reference): object, array, function

Script (2 min): "JavaScript has two categories of data. Primitives are simple, immutable values. Copy one and you get an independent copy — change one, the other is untouched. Objects — which includes arrays and functions — are stored by reference. Two variables can point to the same object in memory. Change it through one variable, and the other sees the change too. Let's go through the primitives."

SLIDE 10 — Primitive Types
Slide content:
js// String
let name = "Alice";

// Number (integers AND decimals — one type)
let age = 30;
let price = 9.99;

// Boolean
let isLoggedIn = true;

// Null — intentional absence of value
let selected = null;

// Undefined — declared but not assigned
let result;
console.log(result); // undefined

// BigInt
const huge = 9007199254740991n;
Script (4 min): "String — any text in quotes or backticks. Number — JavaScript has just ONE number type for both integers and decimals. Fair warning: 0.1 + 0.2 doesn't equal exactly 0.3 in JavaScript. Floating-point math quirk — you'll encounter it eventually.
Boolean — true or false. Used constantly in conditions.
Null — intentional emptiness. A developer deliberately writes null to say 'there's nothing here.'
Undefined — JavaScript's way of saying 'this was declared but never given a value.' The key difference: null is deliberate, undefined is 'not yet.'
Symbol and BigInt are advanced — just know they exist."

SLIDE 11 — The typeof Operator
Slide content:
jstypeof "hello"       // "string"
typeof 42            // "number"
typeof true          // "boolean"
typeof undefined     // "undefined"
typeof null          // "object"  ← ⚠️ famous JS bug!
typeof {}            // "object"
typeof []            // "object"
typeof function(){}  // "function"
Script (2 min): "typeof checks and returns the type of a value as a string. Straightforward — except for one infamous gotcha: typeof null returns 'object'. null is NOT an object — this is a decades-old bug in JavaScript that was never fixed because fixing it would break too much of the web. Just memorize it."

SLIDE 12 — Type Coercion (Implicit)
Slide content:

JavaScript automatically converts types — sometimes unexpectedly

js"5" + 3       // "53" — + with a string = concatenation!
"5" - 3       // 2    — subtraction forces number
true + 1      // 2
false + 1     // 1
null + 1      // 1
"5" == 5      // true  ← coercion before comparing
"5" === 5     // false ← strict, no coercion

Always use ===

Script (4 min): "Coercion is when JavaScript automatically converts a type — and it can bite you hard.
+ is the worst offender. If either side is a string, JavaScript does string concatenation. '5' + 3 → '53'. Not 8. Subtraction has no string equivalent, so JavaScript converts — '5' - 3 → 2.
This is why == is dangerous. It coerces before comparing. '5' == 5 returns true — they're not the same thing! === is strict equality. No coercion. It checks value AND type. '5' === 5 is false. Always use ===. No exceptions."

SLIDE 13 — Type Conversion (Explicit)
Slide content:
js// To Number
Number("42")       // 42
Number("abc")      // NaN
parseInt("42px")   // 42
parseFloat("3.14") // 3.14

// To String
String(42)         // "42"

// To Boolean — Falsy values → false:
// 0, "", null, undefined, NaN, false
// Everything else → true
Boolean(0)         // false
Boolean("hello")   // true
Script (3 min): "Explicit conversion is when YOU decide to change a type — much safer than leaving it to JavaScript.
Number() converts to a number, or gives you NaN (Not a Number) if it can't. parseInt and parseFloat are useful for strings like '42px' — they pull the numeric part out.
Know your falsy values cold: 0, empty string "", null, undefined, NaN, and false. Every other value is truthy. This matters constantly in if-statements and conditionals."

SLIDE 14 — Arrays: The Basics
Slide content:

Ordered lists of values
Any type, even mixed
Zero-indexed — first element is index 0
Created with [ ]

jsconst fruits = ["apple", "banana", "cherry"];
console.log(fruits[0]);     // "apple"
console.log(fruits[2]);     // "cherry"
console.log(fruits.length); // 3
Script (2 min): "Arrays store multiple values in one variable, in a specific order. Instead of fruit1, fruit2, fruit3 — you have one fruits array. Access elements with bracket notation. And remember: zero-indexed. The first item is at [0], not [1]. This trips up almost every beginner at least once."

SLIDE 15 — Adding & Removing Elements
Slide content:
jsconst colors = ["red", "green", "blue"];

colors.push("yellow");   // add to end
colors.pop();            // remove from end
colors.unshift("pink");  // add to beginning
colors.shift();          // remove from beginning
colors[1] = "orange";   // replace an element
Script (3 min): "push and pop work on the END of the array — like a stack of plates, you put on top and take off the top. unshift and shift work on the BEGINNING — push to front, remove from front. You can also directly replace any element using bracket notation and assignment."

SLIDE 16 — Essential Array Methods
Slide content:
jsconst nums = [1, 2, 3, 4, 5];

nums.forEach(n => console.log(n));     // loop, no return

const doubled = nums.map(n => n * 2);  // [2,4,6,8,10]

const evens = nums.filter(n => n % 2 === 0); // [2, 4]

const firstBig = nums.find(n => n > 3);     // 4

nums.includes(3);  // true
nums.indexOf(3);   // 2 (returns -1 if not found)
Script (5 min): "These six methods will handle the vast majority of your array work.
forEach — loops through every item, runs a function for each. You do something with each item. No new array returned.
map — transforms every item and returns a brand new array. Square every number, format every name — that's a map. Original array untouched.
filter — returns a new array with only the items that PASS your test. Want only even numbers? filter.
find — returns the FIRST item that passes your test. Perfect for searching.
includes — yes/no: is this value in the array?
indexOf — where is this value? Returns its index, or -1 if not found.
Learn these six cold."

SLIDE 17 — slice, splice, and join
Slide content:
jsconst letters = ["a", "b", "c", "d", "e"];

// slice — copies, non-destructive
letters.slice(1, 3);  // ["b", "c"] — original unchanged

// splice — modifies in-place, destructive
letters.splice(2, 1); // removes "c"
// letters: ["a", "b", "d", "e"]

// join — array → string
["a", "b", "c"].join("-"); // "a-b-c"
Script (3 min): "slice vs splice — beginners confuse these constantly.
slice makes a copy of a portion. Non-destructive. Original array is untouched.
splice modifies the original array directly — destructive. You tell it: start here, remove this many, and optionally insert these values. It's the Swiss Army knife for in-place edits.
join converts an array into a string with a separator. Very useful for building readable output."

SLIDE 18 — Template Literals
Slide content:

Backticks ` instead of quotes
Embed any expression with ${ }
Native multi-line support

jsconst name = "Alice";
const age = 30;

// Old way:
"Hello, " + name + "! You are " + age + ".";

// Template literal:
`Hello, ${name}! You are ${age}.`;

// Expression:
`Next year you'll be ${age + 1}.`;

// Multi-line:
`<div>
  <h1>Hello, ${name}</h1>
</div>`
Script (3 min): "Template literals use backticks — the key in the top-left corner under Escape. Inside them, write ${...} and JavaScript evaluates whatever's inside and drops the result right into the string. Variable, calculation, function call — anything works.
No more chaining + signs to build strings. It's cleaner, more readable, and less error-prone.
They also support multi-line strings natively — just hit Enter. You'll use template literals constantly."

SLIDE 19 — Putting It All Together (Live Demo)
Slide content:
jsconst students = [
  { name: "Alice", grade: 92 },
  { name: "Bob",   grade: 74 },
  { name: "Carol", grade: 88 },
  { name: "Dave",  grade: 65 }
];

const passing = students.filter(s => s.grade >= 70);
const names   = passing.map(s => s.name);
const message = `Passing students: ${names.join(", ")}`;

console.log(message);
// "Passing students: Alice, Bob, Carol"
Script (4 min): "Let's use everything from today in one example. We have an array of student objects. We filter for passing grades — 70 and above — giving us Alice, Bob, and Carol. We map over those to extract just the names. We join those names into a comma-separated string and drop it into a template literal.
Notice we chained filter and map — filter returns an array, so we immediately call map on it. This chain pattern is everywhere in real JavaScript code. Clean, readable, powerful."

SLIDE 20 — Common Mistakes to Avoid
Slide content:

❌ Using var — use const/let
❌ Using == — use ===
❌ Forgetting zero-indexing
❌ Using splice when you meant slice
❌ Confusing null and undefined
❌ Assuming typeof null === "null" — it's "object"

Script (2 min): "Quick rundown of the mistakes that catch every beginner on these exact topics. Using var. Using double-equals. Off-by-one indexing errors. Mutating an array with splice when you just wanted a copy. Null vs undefined confusion. And the typeof null bug. You've now been warned about all of them — so no excuses."

SLIDE 21 — Key Takeaways
Slide content:

const > let > var
7 primitives: string, number, boolean, null, undefined, symbol, bigint
Always ===, never ==
Convert types explicitly: Number(), String(), Boolean()
Arrays: zero-indexed, zero excuses
Master: map, filter, forEach, find, push, pop
Template literals: backticks + ${ }

Script (2 min): "Here's what sticks. Default to const. Use let when values change. Forget var exists. Know your seven primitive types. Use === always. Know your falsy values. Arrays start at zero — always. And template literals will make your string code dramatically cleaner. These aren't trivia — they're patterns you'll write in every JavaScript project you ever build."

SLIDE 22 — Practice Exercises
Slide content:

Declare variables with const, let, and var. Try reassigning each — what happens?
Create an array of 5 numbers. Use .map() to square each one.
Create an array of names. Use .filter() to return only names longer than 4 characters.
Build a template literal showing a user's name, age, and a calculated birth year.
Run typeof on 5+ different values and note each result — include null.

Script (2 min): "Type these out — don't just read them. The muscle memory matters. If you can do all five without looking at your notes, you've got a solid grip on today's material. We'll do a quick review at the top of next session — bring your questions."

SLIDE 23 — Q&A & Resources
Slide content:

Questions?
📖 MDN Web Docs: developer.mozilla.org
🛠 Practice: codepen.io or jsfiddle.net
Next lesson: [Your next topic]

Script (1 min): "That's today's session. Everything we covered is foundational — it's in every JavaScript codebase you'll ever work in. MDN Web Docs is your reference Bible — bookmark it. CodePen and JSFiddle let you practice right in your browser with zero setup. What questions do you have? Nothing is too basic."
