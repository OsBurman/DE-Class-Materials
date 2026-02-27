PRE-CLASS SETUP (5 minutes before)
Write on the board: "JavaScript runs the web — today you'll understand how it really works."
Open a browser console to demo live examples throughout.

SEGMENT 1: OPENING & ROADMAP (5 minutes)
Slide 1 — Title Slide
Content: "JavaScript Fundamentals — Under the Hood" | Today's Date | Your Name
Script:
"Good morning everyone. Today is one of those lessons where things that might have felt confusing before are going to start clicking. We've covered some basics already, and today we're going to go deeper — we're talking about how JavaScript actually thinks. By the end of this hour you'll understand why code sometimes behaves in ways that seem weird, and more importantly, you'll know how to control it.
Here's what we're covering today: functions in three different forms, the mysterious this keyword, closures and scope, hoisting, strict mode, truthy and falsy values, control flow, loops, and error handling. That's a lot — but it all connects, and we're going to move through it together."

SEGMENT 2: FUNCTIONS — THREE SYNTAXES (12 minutes)
Slide 2 — "What Is a Function?"
Content: One-liner definition. Visual: a box labeled "input → function → output." A single simple example of calling a function.
Script:
"Let's start with functions because everything else today builds on them. You already know functions exist. Today I want you to know that there are three ways to write them in JavaScript, and they are not identical. The differences matter. Let's go through each one."

Slide 3 — Function Declarations
Content: Code block showing a named function declaration. Callout box: "Hoisted — can be called before it appears in the file."
javascriptfunction greet(name) {
  return "Hello, " + name;
}

console.log(greet("Maria")); // "Hello, Maria"
Script:
"The first form is called a function declaration. You use the function keyword, give it a name, and define it. This is the most traditional way. Here's the key thing about declarations: they are hoisted. I'll explain hoisting fully in a few minutes, but for now just know that JavaScript reads your entire file before running it, and it lifts function declarations to the top. That means you can call greet() on line 1 even if you wrote the function on line 50. This is unique to this syntax."

Slide 4 — Function Expressions
Content: Code block showing a function stored in a variable. Callout: "NOT hoisted — must be defined before calling."
javascriptconst greet = function(name) {
  return "Hello, " + name;
};

console.log(greet("Maria")); // works fine
Script:
"The second form is a function expression. Instead of declaring a standalone function, you're storing a function as a value inside a variable. Notice the subtle difference — same function keyword, but it's assigned to const greet. This one is not hoisted. If you try to call greet() before this line, JavaScript will throw an error. Function expressions treat the function like any other value — a number, a string, a function. That's a powerful idea."

Slide 5 — Arrow Functions
Content: Three code blocks showing arrow function with multiple params, one param, and no body braces (implicit return). Callout: "Shorter syntax. Behaves differently with this."
javascript// Standard arrow
const greet = (name) => {
  return "Hello, " + name;
};

// Implicit return (single expression)
const greet = name => "Hello, " + name;

// No parameters
const sayHi = () => "Hi!";
Script:
"The third form is the arrow function, introduced in ES6. The syntax is shorter and cleaner. You drop the function keyword and add a fat arrow =>. If your function only has one expression to return, you can skip the curly braces and the return keyword — JavaScript returns it implicitly. If you have one parameter, you can even drop the parentheses. Arrow functions are everywhere in modern JavaScript. But here's the critical thing — and I want you to remember this because it comes up in literally every job interview — arrow functions handle the this keyword differently than the other two. We'll see exactly how in a few minutes."
[Live demo: Open console, type all three versions, show they produce the same output but highlight the syntax differences.]

Slide 6 — Function Syntax Comparison Table
Content: Three-column table: Declaration | Expression | Arrow. Rows: Syntax, Hoisted?, Own this?, Best used for.
Script:
"Here's your cheat sheet. Declarations are hoisted and have their own this. Expressions are not hoisted and have their own this. Arrow functions are not hoisted and do not have their own this — they inherit it from wherever they were written. Keep this table in your notes. You'll refer back to it."

SEGMENT 3: HOISTING (5 minutes)
Slide 7 — "What Is Hoisting?"
Content: Visual metaphor — a crane lifting code to the top of a file. Two side-by-side code blocks: what you write vs. what JavaScript sees.
Script:
"Okay, let's talk about hoisting properly. Hoisting is JavaScript's behavior of moving declarations to the top of their scope before execution. I want to be precise here: JavaScript does not physically move your code. What it does is process your file in two phases. Phase one: it scans everything and registers all declarations. Phase two: it runs the code. So function declarations get registered in phase one, which is why you can call them before they appear."

Slide 8 — Hoisting: Variables
Content: Code blocks showing var vs let/const hoisting behavior. Show the undefined result for var and the ReferenceError for let.
javascriptconsole.log(a); // undefined (not an error!)
var a = 5;

console.log(b); // ReferenceError
let b = 5;
Script:
"var declarations are hoisted but their values are not. So JavaScript knows a exists, but hasn't assigned 5 to it yet — it gives you undefined. With let and const, the variable is also technically hoisted, but it lives in what's called the Temporal Dead Zone — you can't touch it until the line where you declared it, or you get a ReferenceError. This is one of the big reasons modern JavaScript prefers let and const over var. The errors are more predictable and helpful."
[Live demo: paste both examples in the console, show the different outputs.]

SEGMENT 4: SCOPE, CLOSURES & LEXICAL SCOPE (10 minutes)
Slide 9 — Scope: The Big Picture
Content: Nested boxes visual — Global scope contains Function scope contains Block scope. Brief bullet definitions for each.
Script:
"Scope is about where variables live and where they can be accessed. Think of it like physical rooms. Global scope is the whole building — everything can see it. Function scope is a private room — variables inside a function can't be seen from outside. Block scope, created with let and const inside curly braces like an if or a for loop, is like a closet inside that room. This nesting is called the scope chain — when JavaScript looks for a variable, it starts in the current scope and walks outward until it finds it or runs out of places to look."

Slide 10 — Lexical Scope
Content: Code example of a nested function. Arrow showing the inner function looking outward.
javascriptfunction outer() {
  const message = "Hello from outer";

  function inner() {
    console.log(message); // can access message
  }

  inner();
}
Script:
"Lexical scope means that a function's scope is determined by where it is written in the code, not where it is called from. That inner function can see message because it was written inside outer. This is the foundation for one of JavaScript's most powerful features: closures."

Slide 11 — Closures
Content: Code example of a function returning an inner function. Callout: "The inner function remembers the variables from where it was born."
javascriptfunction makeCounter() {
  let count = 0;

  return function() {
    count++;
    return count;
  };
}

const counter = makeCounter();
console.log(counter()); // 1
console.log(counter()); // 2
console.log(counter()); // 3
Script:
"A closure is what happens when a function remembers the variables from its birth environment even after that environment has finished running. Look at this example. makeCounter runs and returns an inner function. Normally, when a function finishes, its local variables are gone. But here, count stays alive because the returned function still has a reference to it. Every time we call counter(), it remembers and updates count. This is a closure. Closures are how you create private variables in JavaScript, how you build things like counters, timers, and module patterns. They show up everywhere in real codebases."
[Live demo: Run this in the console. Show that calling counter() multiple times increments. Then show that makeCounter() called again creates a fresh independent counter.]

Slide 12 — Closures: Real-World Use Case
Content: Simple example showing a closure used to create a private variable (e.g., a bank account balance that can't be accessed directly).
Script:
"Here's a practical angle. If I want a variable that nobody outside my function can tamper with, I use a closure. The variable lives in the outer function's scope, and only the returned inner function can access or modify it. This is as close to true 'private' data as you can get without using classes. You'll see this pattern constantly."

SEGMENT 5: THE this KEYWORD (8 minutes)
Slide 13 — "What Is this?"
Content: One-liner: "this refers to the object that is calling the function right now." Visual: a spotlight pointing at different objects.
Script:
"Alright, this. This is the one that trips everyone up, and it trips people up because the answer to 'what is this?' depends on how and where the function is called, not where it's written. Let's walk through the main contexts."

Slide 14 — this in Four Contexts
Content: Four small code blocks, each labeled: 1) Global context, 2) Method on an object, 3) Arrow function, 4) With new.
javascript// 1. Global context
console.log(this); // window (browser) or global (Node)

// 2. Object method
const user = {
  name: "Alex",
  greet() {
    console.log(this.name); // "Alex"
  }
};
user.greet();

// 3. Arrow function — inherits this
const user2 = {
  name: "Sam",
  greet: () => {
    console.log(this.name); // undefined — arrow has no own this
  }
};
user2.greet();

// 4. Constructor with new
function Person(name) {
  this.name = name;
}
const p = new Person("Jordan");
console.log(p.name); // "Jordan"
Script:
"In the global context, this is the global object — the window in a browser. Inside an object method written as a regular function, this is the object to the left of the dot when you call the method. So user.greet() — this is user. Easy.
Now watch what happens with an arrow function. Arrow functions do not have their own this. They look up and inherit this from the surrounding lexical scope — wherever the arrow function was written. In user2.greet, the arrow function was written in the global scope, so this is the global object, not user2. This is a very common bug. If you're writing a method on an object that needs to reference this, use a regular function, not an arrow.
Finally, when you use new with a constructor function, this refers to the brand new object being created."
[Live demo: Show the object method example, then swap it to an arrow function and watch this.name break.]

Slide 15 — Losing this & Fixing It
Content: Code showing this being lost when a method is passed as a callback. Solutions: .bind(), arrow functions, saving this in a variable.
Script:
"this gets lost when you detach a method from its object — like passing it as a callback. The fix: .bind(user) creates a new function permanently tied to that object. Or you use an arrow function as the callback, which inherits this from the outer method. These are patterns you'll use constantly in real work."

SEGMENT 6: STRICT MODE (3 minutes)
Slide 16 — Strict Mode
Content: "use strict"; at top. Two-column list: Things strict mode prevents on the left, Why it matters on the right.
Script:
"Strict mode is a setting you turn on by putting the string 'use strict' at the very top of your file or function. It tells JavaScript to be stricter about what it lets you do. In strict mode, using a variable without declaring it throws an error instead of silently creating a global. Duplicate parameter names are forbidden. this inside a plain function call is undefined instead of the global object, which actually makes bugs more obvious. Modern JavaScript modules have strict mode on by default. Think of it as turning on safety rails. There's almost no reason not to use it."

SEGMENT 7: TRUTHY & FALSY VALUES (4 minutes)
Slide 17 — Falsy Values (All Six)
Content: Big clear list of all six falsy values: false, 0, "" (empty string), null, undefined, NaN. Title: "These are the only falsy values in JavaScript."
Script:
"In JavaScript, every value is either truthy or falsy. This matters enormously in control flow — in if statements, loops, and ternaries. Here are the only six falsy values. Everything else — every number except 0, every non-empty string, every object, every array, even an empty array and empty object — is truthy. An empty array [] is truthy. An empty object {} is truthy. That surprises people every time. The rule is simple: if it's not on this list, it's truthy."

Slide 18 — Truthy/Falsy in Practice
Content: Code examples showing short-circuit evaluation with && and ||, and a ternary. Practical real-world example like checking if a username exists.
javascriptconst username = "";

// Falsy check
if (!username) {
  console.log("Please enter a username");
}

// Short-circuit: only runs if user exists
const user = getUser();
user && user.login();

// Default value with ||
const displayName = username || "Guest";
Script:
"Here's where truthy/falsy gets practical. Short-circuit evaluation — && only continues if the left side is truthy. || returns the first truthy value it finds. This lets you write user && user.login() instead of a full if block. Or username || 'Guest' to provide a default value. You'll write code like this every single day."

SEGMENT 8: CONTROL FLOW & LOOPS (6 minutes)
Slide 19 — Control Flow Statements
Content: Code blocks for if/else if/else, ternary operator, and switch. Brief when-to-use guidance under each.
Script:
"Control flow is how your program makes decisions. You know if/else already. Let's talk about when to reach for alternatives. The ternary operator — condition ? valueIfTrue : valueIfFalse — is perfect for simple single-line decisions, especially when assigning a value. Use it for clean, readable one-liners, not for complex logic. switch is great when you're comparing one value against many possible matches. It's cleaner than a long chain of else if when every branch checks the same variable."

Slide 20 — Loops
Content: Four loop types with short code examples: for, while, for...of, for...in. Callout notes on when to use each.
javascript// Classic for — when you need the index
for (let i = 0; i < 5; i++) { ... }

// while — when you don't know iterations in advance
while (condition) { ... }

// for...of — iterate values of arrays/strings
for (const item of array) { ... }

// for...in — iterate keys of objects
for (const key in object) { ... }
Script:
"Four loops, four use cases. The classic for loop is your workhorse when you need the index. while runs as long as a condition is true — use this when you don't know upfront how many iterations you need, like waiting for user input. for...of is modern and clean for iterating the values of an array or any iterable. for...in iterates the keys of an object. The most common mistake I see is using for...in on an array — don't. It can give you unexpected results because it iterates enumerable properties, not just indices. Use for...of or a classic for for arrays."

Slide 21 — Loop Control: break & continue
Content: Short code examples of break and continue with explanations.
Script:
"break exits the loop entirely. continue skips the rest of the current iteration and jumps to the next one. These give you fine-grained control without restructuring your whole loop."

SEGMENT 9: ERROR HANDLING (5 minutes)
Slide 22 — Why Error Handling Matters
Content: Image or icon of a crashed app. Quote: "Unhandled errors crash your program. Handled errors let it recover gracefully."
Script:
"Errors happen. Network requests fail. Users give unexpected input. Functions get called with the wrong type. The question isn't whether errors will occur — it's whether your code handles them or lets them take down your whole application. JavaScript gives us try/catch for exactly this."

Slide 23 — try / catch / finally
Content: Full syntax block with try, catch, and finally. Labels pointing to each section explaining what it does.
javascripttry {
  // code that might throw an error
  const data = JSON.parse(userInput);
  console.log(data);
} catch (error) {
  // runs if an error is thrown
  console.error("Invalid JSON:", error.message);
} finally {
  // always runs, error or not
  console.log("Done processing");
}
Script:
"Inside try, you put the code that might fail. If anything inside try throws an error, JavaScript immediately jumps to catch and hands you the error object. The error.message property gives you a human-readable description of what went wrong. finally runs no matter what — whether there was an error or not. It's perfect for cleanup code, like closing a database connection or hiding a loading spinner. finally is optional but powerful."

Slide 24 — Throwing Your Own Errors
Content: Code block using throw new Error("message"). Note: "You can throw any value, but Error objects are best practice."
javascriptfunction divide(a, b) {
  if (b === 0) {
    throw new Error("Cannot divide by zero");
  }
  return a / b;
}

try {
  console.log(divide(10, 0));
} catch (err) {
  console.error(err.message);
}
Script:
"You can also throw your own errors intentionally. If a function receives invalid input that would cause a problem, don't just let it silently fail or return a weird value — throw a descriptive error. Use throw new Error('your message'). When someone calls your function and something goes wrong, they'll catch a clear, meaningful error instead of cryptic behavior."

SEGMENT 10: PUTTING IT ALL TOGETHER (5 minutes)
Slide 25 — Everything Connects
Content: A simple diagram connecting today's concepts: Scope → Closures → this → Functions → Error Handling → Control Flow. Visual web showing how they interrelate.
Script:
"Let's zoom out for a second. These aren't isolated topics. Functions define scope. Scope enables closures. Closures use lexical scope rules. Arrow functions change how this works. this depends on how functions are called. Hoisting affects when functions and variables are available. Control flow and loops are powered by truthy/falsy logic. Error handling wraps all of it. Once these pieces lock together in your mind, you'll read code differently — you'll see why it works, not just that it works."

Slide 26 — Live Coding Demo: Bring It Together
Content: Slide title only — "Live Demo" — with the code from the demo written here for your reference.
javascript"use strict";

function makeWallet(initialBalance) {
  let balance = initialBalance; // closure variable

  return {
    deposit: (amount) => {
      if (typeof amount !== "number" || amount <= 0) {
        throw new Error("Invalid deposit amount");
      }
      balance += amount;
      return balance;
    },
    getBalance: function() {
      return balance;
    }
  };
}

try {
  const wallet = makeWallet(100);
  console.log(wallet.getBalance()); // 100
  wallet.deposit(50);
  console.log(wallet.getBalance()); // 150
  wallet.deposit(-20); // throws error
} catch (err) {
  console.error("Wallet error:", err.message);
}
Script:
"Let's code something that uses everything from today. A simple wallet. Strict mode is on. makeWallet is a function declaration. Inside it, balance is a private variable — nobody can access it directly. We return an object with two methods. deposit is an arrow function — notice I'm not using this inside it, so arrow is fine here. getBalance is a regular method. It uses a closure to access balance. We validate input and throw a real error if something's wrong. The whole call is wrapped in try/catch. Go through this at home line by line. Every concept from today is in here."
[Type this live, don't paste it. Make a deliberate small mistake — maybe forget strict mode or use let wrong — and show how the error message helps you debug.]

SEGMENT 11: CLOSING & Q&A (5 minutes)
Slide 27 — Key Takeaways
Content: Seven clean bullet points — one per major concept. No code. Plain language summary.

Function declarations are hoisted; expressions and arrows are not
Arrow functions inherit this from their surrounding scope; regular functions define their own
Closures let inner functions remember outer variables even after the outer function returns
JavaScript hoists declarations, not initializations — let/const throw errors if accessed early
Strict mode catches silent mistakes and should be your default
Only six values are falsy — everything else is truthy
try/catch/finally lets your program fail gracefully and recover

Script:
"Seven things I want to stick with you. Read these tonight. If any of them still feel fuzzy, that's normal — these are concepts that deepen with practice, not just reading. Your homework is to open a blank JavaScript file, turn on strict mode, write a closure, write the same function in all three syntaxes, and wrap something in a try/catch. Just play."

Slide 28 — Coming Up Next / Questions
Content: Brief teaser of next lesson topics. Large "Questions?" text. Your contact or office hours info.
Script:
"In our next session we'll be building on today — we're going deeper into objects, prototypes, and the class syntax, which is where this is going to come back and you'll be glad you understand it now.
Any questions? And if something hits you after class — write it down, bring it next time, or message me. There are no bad questions on this stuff. These are the concepts that senior developers still google. You're not behind for finding them hard. You're right on track."

---

