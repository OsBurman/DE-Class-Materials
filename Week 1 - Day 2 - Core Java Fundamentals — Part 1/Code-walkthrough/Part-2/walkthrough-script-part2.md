# WEEK 1 - DAY 2 | PART 2 | SPEAKING SCRIPT
# Topics: Strings & Operations, StringBuilder & StringBuffer,
#         Mathematical/Logical/Comparison Operators, Comments & Documentation

---

## HOW TO USE THIS SCRIPT

- **[ACTION]** = something you do on screen
- **[ASK]** = pause and ask the class before continuing
- **⚠️ WATCH OUT** = common mistake or confusion to flag
- **→ TRANSITION** = bridge to the next topic

---

---

# FILE 1: `01-strings-and-operations.java`

---

## OPENING

"Welcome back from break. Part 2 is all about the things you'll use in literally every Java program you ever write — Strings, operators, and clean documentation practices.

Let's start with String. String is different from the types we saw in Part 1. It's not a primitive — it's an object. But Java gives it special treatment so it feels like a primitive. You'll use it more than any other type."

---

## SECTION 1: Creating Strings — The String Pool

**[ACTION]** Open `01-strings-and-operations.java`. Point to the two ways of creating a String.

"There are two ways to create a String. The first — just using double quotes — is what you'll do 100% of the time. This stores the value in something called the **String Pool**, a special memory area where Java caches String literals. If two variables contain the same literal value, they share the same pooled object.

The second way — `new String(...)` — explicitly creates a new object on the heap, bypassing the pool. You almost never want this. I'm showing it so you understand the `==` vs `.equals()` problem."

---

## SECTION 2: == vs .equals() — THE Most Important String Rule

**[ACTION]** Run the `==` vs `.equals()` section.

"Look carefully. `a` and `b` both equal `"hello"` — they're pool objects — so `a == b` is `true`.

`c` was created with `new String("hello")` — it's a brand new object on the heap. `a == c` is `false` because they're different objects in memory. But `a.equals(c)` is `true` because the CONTENT is the same."

**[ASK]** "So when should you use `==` for Strings?"

*Answer:* "Almost never. Always use `.equals()` for String comparison. The only time `==` is safe with Strings is when you're comparing to `null` — like `if (name == null)`. For everything else, `.equals()`."

⚠️ **WATCH OUT:** "This is one of the top 5 most common Java interview questions. 'What's the difference between `==` and `.equals()` for Strings?' Write this in your notes right now. The answer: `==` compares references (memory addresses). `.equals()` compares content."

---

## SECTION 3: String Methods

**[ACTION]** Walk through the methods section, running it as you go.

"`length()` — how many characters. Notice it includes spaces.

`trim()` versus `strip()` — both remove leading and trailing whitespace. `strip()` is Java 11+ and is Unicode-aware. Use `strip()` in modern code.

`substring(5)` — from index 5 to the end. `substring(5, 9)` — from index 5 (inclusive) to 9 (exclusive). The end index is EXCLUSIVE in Java — remember that."

**[ASK]** "If a String is 10 characters long, what's the valid index range?"

*Answer:* "0 to 9. Java is zero-indexed. The last character is at `length() - 1`, not `length()`."

**[ACTION]** Run `indexOf("xyz")`.

"When something is not found, `indexOf` returns -1. Always check for -1 before using the result as an index. A very common bug: `str.substring(str.indexOf("x"))` — if 'x' isn't there, `indexOf` returns -1, and `substring(-1)` throws an exception."

**[ACTION]** Run the `split` example.

"`split` takes a regex pattern. Comma works fine for CSV. The result is a `String[]` array. Use `Arrays.toString()` to print arrays — just printing the array variable gives you a memory address reference."

⚠️ **WATCH OUT:** "When splitting on `.` (period), you must escape it: `split("\\.")`. The dot is a regex wildcard that matches any character. This is a very common gotcha when parsing version numbers or file extensions."

---

## SECTION 4: String is Immutable

**[ACTION]** Show the immutability demo at the bottom.

"This is crucial. Every String method returns a NEW String. The original is NEVER changed. Look — after calling `toUpperCase()`, the `original` variable still says 'hello'. If you want the uppercase version, you must ASSIGN the result: `original = original.toUpperCase()`."

→ **TRANSITION:** "String's immutability is by design — it makes Strings safe to share between threads and use as HashMap keys. But it means string building in a loop is expensive. That's why StringBuilder exists."

---

---

# FILE 2: `02-stringbuilder-and-stringbuffer.java`

---

## OPENING

"I want to show you a performance problem and then solve it. This is one of those things that won't bite you on a small project, but in real enterprise code — loops processing thousands of records — it matters a lot."

---

## SECTION 1: The Performance Problem

**[ACTION]** Open `02-stringbuilder-and-stringbuffer.java`. Point to the `result += i` loop.

"Every time you do `result += i`, Java is not modifying `result`. It creates a BRAND NEW String that is the combination of the old `result` and `i`, assigns it to `result`, and the old String becomes garbage.

In 10,000 iterations, you create 10,000 String objects. The Garbage Collector has to clean them all up. It's slow and wastes memory."

**[ACTION]** Run the file and show the timing comparison.

"See the difference? StringBuilder is dramatically faster. It modifies the same buffer in memory every time — no garbage."

**[ASK]** "Can you think of a real scenario where you'd build a large String in a loop?"

*Accept:* "Generating a CSV file, building an HTML email body, constructing a SQL query dynamically, building a log message."

---

## SECTION 2: StringBuilder Methods

**[ACTION]** Walk through each method with the running output.

"`append()` adds to the end. Works for Strings, ints, doubles, booleans — it calls `toString()` on whatever you pass.

`insert(7, ...)` puts content at a specific index. Watch how 'Java ' gets inserted in the middle.

`delete(start, end)` removes characters. Same inclusive-start, exclusive-end pattern as `substring()`.

`replace(start, end, str)` — combines delete and insert.

`reverse()` — flips the content. Useful for palindrome checks."

**[ACTION]** Show method chaining.

"See how we chain calls? `.append().append().append()`. This works because each `append()` returns `this` — the same `StringBuilder` object. This is called the **Builder pattern** — you'll see it a lot in Java APIs. We study it formally in OOP week."

⚠️ **WATCH OUT:** "Don't call `toString()` after every append — only at the end when you actually need a String. Calling `toString()` inside a loop defeats the purpose."

---

## SECTION 3: StringBuilder vs StringBuffer

**[ACTION]** Show the comparison summary.

"They're almost identical — same API, same methods. The only difference: `StringBuffer` is **thread-safe**. Every method is synchronized — it locks the object so only one thread can use it at a time.

`StringBuilder` has no such lock — it's faster, but not safe to share between threads.

Rule of thumb: use `StringBuilder` unless you're specifically writing multi-threaded code that shares a mutable String across threads. We'll cover threading in Week 2."

→ **TRANSITION:** "Strings built. Now let's talk about how we compute things — operators."

---

---

# FILE 3: `03-operators.java`

---

## OPENING

"Operators are the verbs of Java — they DO things to values. You know the math ones from school. Java has some additional ones that you'll use constantly."

---

## SECTION 1: Arithmetic Operators

**[ACTION]** Open `03-operators.java`. Run the arithmetic section.

"Addition, subtraction, multiplication — standard. Division: watch carefully."

**[ASK]** "What do you think `17 / 5` gives us?"

*After guesses:* "3. Not 3.4 — just 3. When BOTH operands are integers, Java performs integer division and truncates. To get the decimal result, at least one operand must be a double — either `17.0 / 5` or `(double)17 / 5`."

"Modulus — `%` — gives you the REMAINDER. `17 % 5 = 2` because 5 goes into 17 three times with 2 left over. The most common use: checking even or odd. `number % 2 == 0` means even."

---

## SECTION 2: Increment/Decrement

**[ACTION]** Run the increment section carefully.

"Two versions: post-increment `score++` and pre-increment `++score`.

Post-increment: give me the CURRENT value, THEN add 1. Pre-increment: add 1 FIRST, THEN give me the value.

Watch the output: `score++` prints 10 (the old value), then score becomes 11. `++score` increments to 12 first, then prints 12."

**[ASK]** "In practice — in a `for` loop like `for (int i = 0; i < 10; i++)` — does it matter whether you use `i++` or `++i`?"

*Answer:* "In a for loop's update expression, no — the result of the expression isn't used, so both do the same thing. But in an expression like `int x = i++` vs `int x = ++i`, it makes a difference."

---

## SECTION 3: Comparison Operators

**[ACTION]** Run the comparison section.

"All comparison operators return a boolean — `true` or `false`. Nothing else.

One thing to flag: `==` for comparing primitive values is perfectly fine. `85 == 85` works exactly as expected. The `==` vs `.equals()` issue only applies to OBJECTS like Strings and Integers."

---

## SECTION 4: Logical Operators

**[ACTION]** Run the logical operators section.

"Three logical operators: `&&` (AND), `||` (OR), `!` (NOT).

AND: BOTH sides must be true for the result to be true. Think: student is enrolled AND has paid fees.

OR: at least ONE side must be true. Think: student can use library card OR student ID.

NOT: flips the boolean. `!true` is `false`, `!false` is `true`."

---

## SECTION 5: Short-Circuit Evaluation

**[ACTION]** Show the null-safe example.

"This is critical for writing safe code. `&&` is short-circuit: if the LEFT side is `false`, Java doesn't evaluate the RIGHT side at all — the result is already determined to be `false`.

Look at this: `(name != null) && (name.length() > 5)`. If `name` is null, the left side is `false` — Java stops there, never calls `name.length()`. No NullPointerException.

If you reversed the order: `(name.length() > 5) && (name != null)` — Java evaluates left first, calls `.length()` on null, and crashes."

⚠️ **WATCH OUT:** "Always put the null check on the LEFT side of `&&`. This pattern will save you from NullPointerExceptions more times than you can count."

---

## SECTION 6: Ternary Operator

**[ACTION]** Run the ternary section.

"The ternary operator is a compact if-else. Syntax: `condition ? valueIfTrue : valueIfFalse`.

You can chain them — see the grade calculation. But don't nest more than two levels deep — it becomes unreadable. If it's getting complex, use a regular if-else instead."

→ **TRANSITION:** "The last file today is about something that isn't code you run — it's code you READ. Comments and documentation. This is one of the things that separates junior developers from seniors."

---

---

# FILE 4: `04-comments-and-documentation.java`

---

## OPENING

"Every company you work at will have a codebase written by people who don't work there anymore. You'll need to understand what their code does without being able to ask them. Good documentation is what makes that possible — or painful."

---

## SECTION 1: Javadoc Comments

**[ACTION]** Open `04-comments-and-documentation.java`. Show the class-level Javadoc at the top.

"Javadoc comments use the `/**` opening — two asterisks. They sit DIRECTLY above the thing they document — the class, method, or field. No blank lines between the comment and the declaration.

Javadoc isn't just comments you read in the source code. The `javadoc` tool — part of the JDK — processes these comments and generates the same HTML documentation you see at docs.oracle.com. Your IDE also uses them — hover over a method and the Javadoc pops up."

**[ACTION]** Show the `@param`, `@return`, and `@throws` tags on `calculateAverage`.

"`@param` documents each parameter. `@return` explains what the method returns. `@throws` documents what exceptions can be thrown and why. These three tags are the minimum for any public method."

**[ASK]** "Why document the edge case — `returns 0.0 if scores is empty`?"

*Answer:* "Because the caller needs to know. If they don't check the return value, they might use `0.0` and not realize it means 'no data'. Good documentation makes the contract explicit."

---

## SECTION 2: Types of Comments

**[ACTION]** Point to each comment type.

"Three types. Single-line `//` — use for short explanations, put them above the line or inline for very brief notes.

Multi-line `/* */` — for longer explanations. Also useful for quickly commenting out a block of code during debugging.

Javadoc `/** */` — only for things that are part of your public API. Classes, public methods, public fields."

---

## SECTION 3: Best Practices

**[ACTION]** Show the good vs bad comment examples.

"The number one rule: **comment WHY, not WHAT**. The code already tells you WHAT it does. A comment that just restates the code — `// add 1 to i` right next to `i++` — adds zero value.

A good comment explains WHY you made a choice that isn't obvious. Why did you cast to double before dividing? Why are you checking null before calling the method? Why this algorithm instead of a simpler one?

TODO and FIXME comments are a professional habit. They mark known issues and planned improvements. Many IDEs highlight them, and code review tools track them."

**[ASK]** "What's wrong with leaving commented-out code in your files when you commit?"

*Answer:* "It clutters the file, confuses readers, and the information is already in Git history anyway. Use version control — don't use comments as a backup system."

---

## CLOSING THE DAY

"Let me recap what you've covered today.

Part 1: You understand what the JVM, JRE, and JDK are and how they work together to run Java code. You know all 8 primitive types and how to use them. You can declare variables, write literals in multiple bases, and create constants. You understand widening, narrowing, autoboxing, and unboxing — and the null trap that comes with it.

Part 2: You can work with Strings and know every method you'll need in real projects. You understand WHY StringBuilder is faster and when to use it. You can write any expression using arithmetic, comparison, and logical operators — including the null-safe short-circuit pattern. And you know how to document your code with Javadoc so teammates can use it without reading the implementation.

Tomorrow — Day 3 — we go deeper into control flow: if-else, switch, loops, break and continue. The building blocks of logic. See you then."
