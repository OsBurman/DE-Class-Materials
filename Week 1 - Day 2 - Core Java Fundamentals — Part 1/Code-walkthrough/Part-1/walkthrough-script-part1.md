# WEEK 1 - DAY 2 | PART 1 | SPEAKING SCRIPT
# Topics: JVM/JRE/JDK Architecture, Primitives & Data Types,
#         Variables/Literals/Constants, Type Conversion/Casting/Autoboxing

---

## HOW TO USE THIS SCRIPT

- **[ACTION]** = something you do on screen
- **[ASK]** = pause and ask the class before continuing
- **⚠️ WATCH OUT** = common mistake or confusion to flag
- **→ TRANSITION** = bridge to the next topic

---

---

# FILE 1: `01-jvm-jre-jdk.java`

---

## OPENING

"Good morning. Today we start writing Java. But before we write a single line of code, I want to spend a few minutes on something that will help you understand what's actually happening every time you run a Java program.

When people say 'Java runs everywhere', what does that actually mean? How does code you write on a Mac run the same way on Windows or Linux? That's what this first section is about."

---

## SECTION 1: The Three Layers — JDK, JRE, JVM

**[ACTION]** Open `01-jvm-jre-jdk.java`. Show the comment diagram at the top.

"Look at this diagram. Three nested boxes — JDK contains JRE, JRE contains JVM.

**JVM** — the Java Virtual Machine — is the innermost layer. It's the actual engine that runs your code. But here's the thing: the JVM is NOT Java code running on your machine directly. It's an interpreter that sits between your code and the operating system.

**JRE** — the Java Runtime Environment — is the JVM PLUS the standard libraries. Things like `System.out.println`, `ArrayList`, `String` — all of those classes live in the JRE. If someone just wants to RUN a Java program but not develop one, they install the JRE.

**JDK** — the Java Development Kit — is the JRE PLUS the tools you need to WRITE and BUILD Java. The compiler `javac`, the debugger, `javadoc` for generating documentation — all JDK tools."

**[ASK]** "On your computer right now — do you have the JDK or just the JRE installed?"

*Answer:* "You need the JDK — because you're developing. You need `javac` to compile."

---

## SECTION 2: How Your Code Actually Runs

**[ACTION]** Point to the HOW YOUR CODE RUNS comments.

"This is the compilation journey. Follow it step by step.

Step 1: You write `HelloWorld.java` — plain text that you and I can read.

Step 2: `javac` — that's the Java Compiler, a JDK tool — reads your `.java` file and produces a `.class` file. But this `.class` file is NOT machine code. It's **bytecode** — instructions that only the JVM understands.

Step 3: When you run the program, the JVM reads the `.class` file and translates those bytecode instructions into real machine instructions for YOUR specific CPU."

**[ASK]** "Why do this in two steps? Why not compile directly to machine code like C does?"

*Answer:* "Because machine code is different on every CPU and OS. By compiling to bytecode FIRST, the same `.class` file can run on any machine that has a JVM installed. That's the 'Write Once, Run Anywhere' promise."

⚠️ **WATCH OUT:** "Students sometimes think the `.class` file IS the compiled executable. It's not — it's still one step away from the metal. The JVM handles the final translation."

---

## SECTION 3: JIT Compiler

"There's one more piece — the JIT compiler, short for Just-In-Time. As your program runs, the JVM notices which parts of the bytecode execute most frequently — 'hot paths'. It compiles those specific sections to native machine code on the fly and caches them. So Java programs often get FASTER the longer they run. That's JIT in action."

---

## SECTION 4: JVM Memory Areas

**[ACTION]** Point to the memory areas comment section.

"You don't need to memorize all of these today, but you should be aware of two:

The **Heap** is where objects live. When you create a `new Student()`, it goes on the heap. The Garbage Collector watches the heap and frees objects no one is using anymore.

The **Stack** is for method calls and local variables. Every time a method runs, a 'frame' is pushed onto the stack. When the method returns, the frame is popped. It's LIFO — last in, first out."

---

## SECTION 5: Run the Code

**[ACTION]** Run `01-jvm-jre-jdk.java`.

"Look at the output. This is your JVM printing information ABOUT ITSELF — the Java version, the JVM name, which OS it's running on, how much heap memory is available.

The memory output shows three numbers: max heap (the upper limit), total allocated (what the OS has given to the JVM so far), and free heap (available space within what's been allocated). The JVM dynamically requests more memory from the OS as needed, up to the max."

→ **TRANSITION:** "Great — you understand the runtime. Now let's write actual Java. First up: the types of data we can work with — primitives."

---

---

# FILE 2: `02-primitives-and-datatypes.java`

---

## BEFORE THE CODE

"Java is a **statically typed** language. That means every variable must have a declared type, and that type can never change. This is different from JavaScript where a variable can hold a number today and a string tomorrow.

Java has two categories of types: **primitives** and **reference types**. Today we focus on primitives. There are exactly 8 of them."

---

## SECTION 1: Integer Types

**[ACTION]** Open `02-primitives-and-datatypes.java`. Show the table comment and the integer variables.

"Look at the table. Four integer types — byte, short, int, long — each holding a bigger range of numbers.

`int` is the one you'll use 95% of the time. It handles numbers from about negative 2 billion to positive 2 billion. That covers almost every real-world count or ID.

`long` is for when you need bigger numbers — population counts, timestamps in milliseconds. Notice the `L` suffix on the value — `8_100_000_000L`. That L is **required** — without it, Java tries to treat it as an `int` literal and it overflows."

**[ASK]** "What do you think the underscore in `1_500_000` does?"

*Answer:* "Nothing — it's purely a readability separator. Java ignores it. It's like the comma in '1,500,000' — just makes large numbers easier to read."

⚠️ **WATCH OUT:** "Forgetting the `L` on long literals. If you write `long x = 8100000000;` without the L, Java tries to parse it as an int — which it can't — and gives you a compile error."

---

## SECTION 2: Decimal Types

**[ACTION]** Point to float and double.

"`double` is your default for decimal numbers. It has ~15 digits of precision. `float` is half the size but only ~7 digits of precision — you almost never want to use float in modern code.

Notice the `f` suffix on the float literal — `9.99f`. That's required. Without it, Java treats `9.99` as a double literal, and assigning it to a float would lose precision — compiler won't allow it without a cast."

⚠️ **WATCH OUT:** "Never use `float` or `double` for money calculations. They're IEEE 754 floating-point numbers and can't represent some decimal values exactly. Use `BigDecimal` for currency."

---

## SECTION 3: Char and Boolean

**[ACTION]** Run the char section.

"See how `(int) grade` prints 65? Under the hood, `char` in Java is a 16-bit unsigned integer storing the Unicode code point. 'A' IS 65. This becomes important when you do character arithmetic — like `'A' + 1` gives you `'B'`."

"Boolean is simple — `true` or `false`. No 0 or 1 like in C. In Java, a boolean is NEVER an integer."

**[ASK]** "What do you think happens if you try to assign `1` to a boolean variable?"

*Answer:* "Compile error. Java doesn't do the C trick of treating 0 as false and 1 as true."

---

## SECTION 4: Default Values

**[ACTION]** Point to the default values comment.

"Two important rules here. Class-level fields get automatic defaults — int fields start at 0, booleans start at false, object references start at null.

But local variables — variables declared inside a method — DO NOT get defaults. If you declare `int x;` and try to use `x` before assigning it, the compiler gives you an error: 'variable x might not have been initialized.' Java won't let you use garbage memory."

→ **TRANSITION:** "Now that we know what types exist, let's look at how we declare variables and work with literals and constants."

---

---

# FILE 3: `03-variables-literals-constants.java`

---

## SECTION 1: Variables

**[ACTION]** Open `03-variables-literals-constants.java`. Walk through the variable declarations.

"A variable declaration has three parts: the type, the name, and optionally an initial value. You can separate declaration and assignment if needed — declare at the top, assign when you have the value.

Every variable has a **scope** — the region of code where it exists. Local variables (inside a method or block) live only within that `{ }` block. We'll talk more about scope when we get to OOP."

---

## SECTION 2: Literals

**[ACTION]** Walk through the integer literals.

"A literal is a fixed value you type directly in code. `100` is a decimal literal. But Java lets you write integers in different bases.

`0b01100100` — the `0b` prefix means binary. `0144` — leading `0` means octal. `0x64` — `0x` means hexadecimal.

All four are the same value: 100. When would you use hex? Networking, color values (like `0xFF5733`), bitwise operations. Binary is useful for bitmask flags."

**[ACTION]** Run the file and confirm all four print `100`.

**[ASK]** "Why might a developer write `0xFF` instead of `255`?"

*Accept:* "Because `0xFF` makes it obvious you're working with an 8-bit value — the visual representation maps directly to the bits."

---

## SECTION 3: Constants

**[ACTION]** Point to the class-level constants at the top of the file.

"Constants use the `final` keyword. Once assigned, the value can never change. `static final` at the class level is the Java equivalent of a global constant.

Naming convention: ALL_CAPS with underscores. This is a Java-wide standard — when you see `ALL_CAPS`, you know it's a constant.

Why use constants instead of just typing the number everywhere? Two reasons: readability — `TAX_RATE` tells you what 0.08 MEANS. And maintainability — if the tax rate changes, you change it in ONE place, not everywhere you used `0.08` in the code."

⚠️ **WATCH OUT:** "Try to assign a new value to a `final` variable. Show the compile error. Students sometimes think `final` does something at runtime — it's actually enforced at compile time."

→ **TRANSITION:** "Variables hold values. But what happens when you have a value of one type and you need it in a different type? That's type conversion and casting."

---

---

# FILE 4: `04-type-conversion-casting-autoboxing.java`

---

## SECTION 1: Widening Conversion

**[ACTION]** Open `04-type-conversion-casting-autoboxing.java`. Show the widening section.

"Widening conversion is automatic. Java will silently convert from a smaller type to a larger type because there's no risk of data loss. An `int` always fits in a `long`. A `long` always fits in a `double`.

Look at the output — `examScore` is 85 as an int. When we assign it to a double, it becomes `85.0`. Java added the decimal automatically."

**[ASK]** "Why is this called 'widening'?"

*Answer:* "Because the type gets wider — it can hold a larger range of values."

---

## SECTION 2: Narrowing — The Cast

**[ACTION]** Run the narrowing section.

"Narrowing is the opposite — going from a larger type to a smaller one. Java will NOT do this silently. You have to use an explicit cast: `(int) preciseScore`.

Look at the result: `92.75` became `92`. NOT 93 — Java doesn't round. It TRUNCATES. The decimal is simply chopped off.

Now look at the `bigNumber` example. `1_500_000_000_000` is too big for an `int`. When we cast, we lose bits — the result is garbage. The compiler lets you do it — you told it you knew the risk with the cast — but the output is meaningless."

⚠️ **WATCH OUT:** "Truncation vs rounding. Students expect `(int) 92.75` to give 93. It gives 92. If you need to round, use `Math.round()` first, then cast."

---

## SECTION 3: String Conversions

**[ACTION]** Walk through the String conversion code.

"This comes up ALL the time. You get user input as a String — because everything typed in a text box is a String — but you need to do math with it.

`Integer.parseInt()` converts a String to an int. `Double.parseDouble()` converts to double. Every primitive wrapper class has a `parseXxx()` method.

What if the String isn't a valid number? Run the `try/catch` block with `\"twenty\"`. See that `NumberFormatException`? Always validate or catch this when parsing user input."

---

## SECTION 4: Autoboxing

**[ACTION]** Show the autoboxing section.

"Here's something that trips up everyone coming from C or Python. Java Collections — ArrayList, HashMap — can only hold OBJECTS. They can't hold raw primitives. An `ArrayList<int>` is illegal.

So Java has Wrapper classes: `Integer` wraps `int`, `Double` wraps `double`, `Boolean` wraps `boolean`, and so on.

Autoboxing is Java automatically doing the wrapping for you. When you write `scores.add(95)`, you're adding an `int` literal to an `ArrayList<Integer>`. Java silently converts it to `new Integer(95)`. That's autoboxing."

---

## SECTION 5: Unboxing and the Null Trap

**[ACTION]** Show unboxing, then run the NullPointerException demo.

"Unboxing is the reverse — Java automatically unwraps an `Integer` back to an `int` when needed.

Now watch this. `Integer nullableScore = null;` — valid, because `Integer` is a reference type and references can be null. But then `int boom = nullableScore;` — Java tries to unbox null. It calls `.intValue()` on a null reference. `NullPointerException`.

This is one of the most common runtime exceptions you'll see when working with collections. Always check for null before unboxing."

---

## SECTION 6: Integer Cache Gotcha

**[ACTION]** Run the `==` comparison section.

"This is subtle but important. Look — `Integer x = 127` and `Integer y = 127`, and `x == y` is `true`. But `Integer p = 128` and `Integer q = 128`, and `p == q` is `false`.

Why? Java caches `Integer` objects for values -128 to 127. Both `x` and `y` point to the SAME cached object. `p` and `q` are outside the cache range — they're two different `Integer` objects.

`==` on objects compares **references** (memory addresses), not values. Always use `.equals()` to compare the values of objects."

**[ASK]** "Given what you just learned about `==` vs `.equals()` — what do you think happens when you compare two `String` objects with `==`?"

*Lead toward:* "Same trap — two `String` objects with the same characters can be different objects in memory. Always use `.equals()` for String comparison. We'll see this in Part 2."

---

## CLOSING PART 1

"Before break, let me summarize what you now know.

You understand what the JVM, JRE, and JDK are — not just the acronyms but what each layer does and why Java is platform-independent. You know the 8 primitive types and when to use each. You can declare variables, write literals in multiple formats, and create constants with `final`. And you understand how Java moves data between types — widening automatically, narrowing with an explicit cast, and autoboxing/unboxing when working with collections.

After break, we move to Part 2: Strings, StringBuilder, operators, and documentation."
