# Part 2: Strings, Operators & Code Documentation
## Lecture Script (60 Minutes)

---

## LECTURE SCRIPT - PART 2

*[0 minutes]*

Good morning everyone, welcome back! Great work this morning on JVM architecture, primitives, and variables. You're building a solid foundation. This afternoon, we're covering three things that are critical for every Java developer: working with text through Strings, performing operations with operators, and writing professional, well-documented code.

By the end of this session, you'll understand how Java handles strings—and why understanding immutability matters. You'll know all the operators you'll use constantly. And you'll write code that other developers—including future you—can actually understand.

Let's dive in.

*[2 minutes]*

## Part 2 Learning Objectives

Here are our goals for this session:
- Work confidently with the String data type and its methods
- Understand String immutability and why it matters
- Use StringBuilder and StringBuffer when appropriate
- Apply mathematical, logical, and comparison operators correctly
- Write clear, professional code with meaningful comments
- Use Javadoc to document your code like a pro

These skills are fundamental. You'll use Strings in every single program you write. You'll use operators constantly—they're the language's verbs. And professional documentation? That's what separates working developers from students.

*[4 minutes]*

## Strings: The Most Used Data Type

Now, we've covered primitives. But I want to introduce you to the String type, because it's special. Strings are sequences of characters—text. Names, messages, data, file paths—everything textual is a String.

Here's the technical thing: String is not a primitive type. It's a class, an object. Technically, it wraps an array of characters. But here's the important part: it's so common, so central to Java, that it feels like a primitive. You use it constantly.

What makes Strings special is something called immutability. Immutable means "cannot be changed." Once you create a String, you cannot change it. Not its content, not its characters—nothing. This seems weird. "Wait, Emily, I can do `s = s + " world";` and that works, so didn't I change it?"

No. And that's the crucial insight. You didn't change the String. You created a new String, and your variable now points to the new one.

This sounds like an academic detail, but it's not. It affects performance. It affects how you think about string building. It affects memory. So we're going to understand immutability deeply.

*[6 minutes]*

## String Declaration and Initialization

Let me show you the basics. Declaring a String:

```java
String name;
```

Simple, right? You declare it like any variable. Initializing:

```java
String name = "John";
```

Notice the double quotes. That's critical. Single quotes are for `char`. Double quotes are for `String`.

```java
char single = 'A';  // One character
String word = "Apple";  // Text
```

Now, technically, you can also create a String like this:

```java
String s = new String("text");
```

But this is almost never what you do. The simpler way is just:

```java
String s = "text";
```

The JVM is smart enough to create the String object for you. And empty strings:

```java
String empty = "";
```

All of these are valid. The last one—empty string—is common when initializing variables you'll populate later.

*[8 minutes]*

## String Immutability - The Critical Concept

Now here's where it gets interesting. Strings are immutable. Let me show you what that means:

```java
String s = "Hello";
s = s + " World";
```

After line 1, you have a String pointing to "Hello" in memory. After line 2, what happened?

The JVM created a *new* String object containing "Hello World". Your variable `s` now points to this new string. The original "Hello" object is still in memory, but nothing's pointing to it anymore. Eventually, garbage collection cleans it up.

Why does this matter? Three reasons.

First: thread safety. If strings could change, you'd have problems when multiple threads access the same string. Immutability eliminates this problem.

Second: security. Strings are used for sensitive data. Passwords, tokens. If strings could change, another thread could modify them under your feet.

Third: performance. The JVM can optimize immutable strings. It can cache them. It knows "hello" is always the same "hello".

The consequence is this: if you're building a string by repeatedly concatenating—say, in a loop—you're creating many, many intermediate String objects. This creates garbage. This impacts performance.

That's where StringBuilder comes in. We'll get there. First, let me show you what you can do with Strings.

*[10 minutes]*

## String Methods - Part 1

Strings have methods. Lots of them. These are the ones you'll use constantly:

`.length()` returns the length of the string. How many characters?

```java
String s = "Hello";
int len = s.length();  // 5
```

`.charAt(index)` gets the character at a specific index. Remember, indices start at 0.

```java
char first = s.charAt(0);  // 'H'
char second = s.charAt(1);  // 'e'
```

`.substring(start)` extracts a portion of the string, from the start index to the end:

```java
String sub = s.substring(1);  // "ello"
```

`.substring(start, end)` extracts from start up to (but not including) end:

```java
String sub = s.substring(1, 4);  // "ell"
```

`.toUpperCase()` converts to uppercase:

```java
String upper = s.toUpperCase();  // "HELLO"
```

`.toLowerCase()` converts to lowercase:

```java
String lower = s.toLowerCase();  // "hello"
```

`.trim()` removes leading and trailing whitespace:

```java
String padded = "  hello  ";
String trimmed = padded.trim();  // "hello"
```

Important: All of these return new Strings. The original is unchanged. Because immutable.

*[12 minutes]*

## String Methods - Part 2

More methods you'll use:

`.equals(other)` compares two strings for equality:

```java
String s1 = "hello";
String s2 = "hello";
boolean same = s1.equals(s2);  // true
```

`.equalsIgnoreCase(other)` does the same but ignores case:

```java
boolean same = s1.equalsIgnoreCase("HELLO");  // true
```

`.contains(substring)` checks if the string contains a substring:

```java
boolean has = s.contains("ell");  // true
```

`.startsWith(prefix)` checks if it starts with a prefix:

```java
boolean starts = s.startsWith("Hel");  // true
```

`.endsWith(suffix)` checks if it ends with a suffix:

```java
boolean ends = s.endsWith("lo");  // true
```

`.indexOf(character)` finds the index of a character. Returns -1 if not found:

```java
int index = s.indexOf('l');  // 2
int notFound = s.indexOf('z');  // -1
```

`.replace(oldChar, newChar)` replaces characters:

```java
String replaced = s.replace('l', 'L');  // "HeLLo"
```

`.split(delimiter)` splits the string into an array. We'll talk about arrays later, but here's the idea:

```java
String csv = "apple,banana,orange";
String[] fruits = csv.split(",");
// fruits[0] = "apple"
// fruits[1] = "banana"
// fruits[2] = "orange"
```

Very useful for parsing data.

*[14 minutes]*

## A Critical Comparison: == vs .equals()

Here's a pitfall that catches everyone. Comparing strings:

```java
String s1 = new String("hello");
String s2 = new String("hello");
if (s1 == s2) {
  // This is FALSE
}
if (s1.equals(s2)) {
  // This is TRUE
}
```

Why the difference?

`==` compares references. It asks: "Do these variables point to the same object in memory?"

`s1` and `s2` are different objects, so `s1 == s2` is false.

`.equals()` compares content. It asks: "Do these strings have the same characters?"

Both have "hello", so `s1.equals(s2)` is true.

Here's the rule: **Always use `.equals()` to compare strings.** Never use `==` for content comparison. Only use `==` to check if a string is `null`.

```java
String s = null;
if (s == null) {
  // This is correct
}
if (s.equals(null)) {
  // This throws an exception!
}
```

This is a common bug in junior code. Internalize this: `.equals()` for content, `==` for null checks.

*[16 minutes]*

## String Concatenation

Building strings. You can use the `+` operator:

```java
String greeting = "Hello" + " " + "World";
```

You can use `.concat()`:

```java
String greeting = "Hello".concat(" World");
```

You can use string templates (Java 15+), which are cleaner:

```java
String name = "Alice";
String greeting = "Hello %s".formatted(name);
```

But here's the critical thing: if you're concatenating many strings, especially in a loop, *don't use `+`*. Each `+` creates a new String. In a loop of 1000 iterations? You create 1000 intermediate strings. Garbage.

```java
// BAD: Creates many string objects
String result = "";
for (int i = 0; i < 1000; i++) {
  result += i;  // New string each time!
}
```

Instead, use StringBuilder:

```java
// GOOD: Efficient
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
  sb.append(i);
}
String result = sb.toString();
```

This is a real performance difference. In small cases it doesn't matter. In large cases, it's critical.

*[18 minutes]*

## StringBuilder: Mutable String Building

StringBuilder is the solution to the String immutability problem. It's mutable. You can change it. Every operation happens in place, not creating new objects.

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.append(" ");
sb.append("World");
String result = sb.toString();  // "Hello World"
```

The key method is `.append(x)`. It adds to the end. And it returns the StringBuilder itself, so you can chain:

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello").append(" ").append("World");
String result = sb.toString();
```

Cleaner.

Here's the pattern for building strings in loops:

```java
StringBuilder sb = new StringBuilder();
for (int i = 1; i <= 10; i++) {
  sb.append("Item ").append(i).append("\n");
}
String output = sb.toString();
```

At the end, call `.toString()` to get the final String.

*[20 minutes]*

## StringBuilder Methods

What can you do with StringBuilder?

`.append(x)` adds to the end. Works with any type: strings, numbers, objects.

```java
sb.append("Count: ").append(42);
```

`.insert(index, x)` inserts at a specific position:

```java
sb.insert(5, "inserted");
```

`.delete(start, end)` removes a range:

```java
sb.delete(0, 5);  // Delete first 5 characters
```

`.reverse()` reverses the content:

```java
sb.reverse();
```

`.length()` returns the current length:

```java
int len = sb.length();
```

`.toString()` converts to String when you're done:

```java
String result = sb.toString();
```

`.setCharAt(index, character)` changes a character:

```java
sb.setCharAt(0, 'X');
```

All of these modify the StringBuilder in place. No creating intermediate objects. This is why it's efficient.

*[22 minutes]*

## StringBuffer vs StringBuilder

There's another class called StringBuffer. It does the same thing as StringBuilder, but it's synchronized. What does that mean?

Synchronized means thread-safe. It's safe for multiple threads to use the same StringBuffer at the same time.

StringBuilder is not thread-safe. If multiple threads use the same StringBuilder, you get corruption.

So which do you use?

StringBuffer: Only if you're writing multi-threaded code and actually sharing the buffer between threads.

StringBuilder: In all other cases.

In practice, StringBuffer is rarely used anymore. It's older code. New code: StringBuilder.

```java
// Modern: StringBuilder
StringBuilder sb = new StringBuilder();

// Legacy: StringBuffer (rarely used)
StringBuffer sb = new StringBuffer();
```

Performance-wise? StringBuilder is faster because it doesn't synchronize. If you ever have to choose, and you're not sure about threading, choose StringBuilder. If it causes issues later, you change it. But 99% of the time, it's StringBuilder.

*[24 minutes]*

## When to Use Each String Type

Let me summarize:

`String`: Default choice. Most of the time. When you're not building strings repeatedly.

```java
String name = "Alice";
String greeting = "Hello, " + name;
```

`StringBuilder`: When building strings in loops or repeated concatenation. When performance matters.

```java
StringBuilder output = new StringBuilder();
for (int i = 0; i < 1000; i++) {
  output.append(data[i]);
}
String result = output.toString();
```

`StringBuffer`: Multi-threaded code where the buffer is shared. Rare.

The rule: String by default. If you're doing repeated concatenation and performance is important, use StringBuilder.

In most beginner code? It's all String. StringBuilder becomes important in intermediate code.

*[26 minutes]*

## Introduction to Operators

Now, operators. These are the verbs of programming. They perform operations on data.

Java has several categories:
- Arithmetic operators: math
- Comparison operators: comparing values
- Logical operators: combining boolean conditions
- Assignment operators: assigning values
- Ternary operator: inline conditionals

Let's learn them, because you'll use them constantly.

*[28 minutes]*

## Arithmetic Operators

Five basic arithmetic operators:

`+` is addition:
```java
int sum = 5 + 3;  // 8
```

`-` is subtraction:
```java
int difference = 5 - 3;  // 2
```

`*` is multiplication:
```java
int product = 5 * 3;  // 15
```

`/` is division:
```java
int quotient = 6 / 2;  // 3
```

`%` is modulo, the remainder after division:
```java
int remainder = 7 % 3;  // 1
```

All with the numbers work. But here's what trips people up: order of operations.

Multiplication, division, and modulo happen before addition and subtraction.

```java
int result = 2 + 3 * 4;  // 14, not 20
// Because 3 * 4 = 12 first, then 2 + 12 = 14
```

If you want different order, use parentheses:

```java
int result = (2 + 3) * 4;  // 20
```

Parentheses are clear. Professional code uses them liberally, even when not strictly necessary.

*[30 minutes]*

## Division: A Subtle Gotcha

Division has a subtle behavior. When you divide two integers, the result is an integer:

```java
int result = 7 / 2;  // 3, not 3.5
```

You get 3, not 3.5. The decimal part is thrown away.

Why? Because both operands are `int`, so the result is `int`. Integers can't have decimals.

If you want a decimal result, you need to use decimal types:

```java
double result = 7 / 2;  // Still 3, not 3.5
double result = 7.0 / 2;  // 3.5
double result = (double) 7 / 2;  // 3.5
```

Notice line 2: even though the result variable is `double`, if both operands are `int`, the division is `int` division. 7 / 2 = 3, and then that 3 becomes 3.0.

Line 3: We cast 7 to `double` first, so now one operand is `double`. The division is decimal division. 7.0 / 2 = 3.5.

This is a common bug. You expect 3.5, you get 3. Remember: division type depends on the operands, not the result variable.

*[32 minutes]*

## Increment and Decrement Operators

Two shorthand operators:

`++` increments by 1:
```java
int x = 5;
x++;  // x is now 6
```

`--` decrements by 1:
```java
int x = 5;
x--;  // x is now 4
```

But there's a subtlety. There are two forms:

Pre-increment: `++x`
Post-increment: `x++`

They look almost the same, but they behave differently in context.

```java
int x = 5;
int y = ++x;  // Pre: increment first, then use
// x is 6, y is 6

int x = 5;
int y = x++;  // Post: use first, then increment
// x is 6, y is 5
```

In the pre case, x is incremented to 6, then assigned to y, so y is 6.

In the post case, x is assigned to y while still 5, then x is incremented to 6.

In loops, you usually don't care:

```java
for (int i = 0; i < 10; i++) {
  // Do something
}
```

Here, `i++` increments at the end of each iteration. Whether it's pre or post doesn't matter because we don't use the value.

But in expressions, pre and post matter. Most beginners use post and don't think about it. As you get more experienced, you'll choose based on what makes sense.

*[34 minutes]*

## Compound Assignment Operators

Shorthand for common patterns:

`+=` means "add and assign":
```java
int x = 5;
x += 3;  // Same as x = x + 3, now x is 8
```

`-=` means "subtract and assign":
```java
int x = 5;
x -= 2;  // x = x - 2, now x is 3
```

`*=` means "multiply and assign":
```java
int x = 5;
x *= 2;  // x = x * 2, now x is 10
```

`/=` means "divide and assign":
```java
int x = 10;
x /= 2;  // x = x / 2, now x is 5
```

`%=` means "modulo and assign":
```java
int x = 10;
x %= 3;  // x = x % 3, now x is 1
```

These are just shorthand. Functionally identical to the long form. Use whichever you prefer. Some people find them clearer, some find them cryptic. Both are fine.

*[36 minutes]*

## Comparison Operators

These compare two values and return a boolean: true or false.

`==` means "equal to":
```java
int a = 5, b = 5;
boolean isEqual = a == b;  // true
```

`!=` means "not equal to":
```java
boolean isNotEqual = a != b;  // false
```

`<` means "less than":
```java
boolean isLess = 3 < 5;  // true
```

`>` means "greater than":
```java
boolean isGreater = 5 > 3;  // true
```

`<=` means "less than or equal to":
```java
boolean isLessOrEqual = 5 <= 5;  // true
```

`>=` means "greater than or equal to":
```java
boolean isGreaterOrEqual = 5 >= 3;  // true
```

The result is always a boolean. You use these in if statements, while loops, all over.

```java
if (age >= 18) {
  // Can vote
}
```

*[38 minutes]*

## Logical Operators

These combine boolean values.

`&&` is AND. True only if both are true:
```java
boolean result = true && true;  // true
boolean result = true && false;  // false
boolean result = false && false;  // false
```

`||` is OR. True if at least one is true:
```java
boolean result = true || false;  // true
boolean result = false || false;  // false
```

`!` is NOT. Flips the boolean:
```java
boolean result = !true;  // false
boolean result = !false;  // true
```

Combining them:

```java
int age = 25;
boolean hasLicense = true;
if (age >= 18 && hasLicense) {
  // Can drive
}
```

This is true only if both conditions are true. Age at least 18 *and* has a license.

```java
if (isWeekend || isHoliday) {
  // No work today
}
```

This is true if either is true. It's weekend *or* holiday.

```java
if (!isRaining) {
  // Go outside
}
```

This is true if it's NOT raining.

You can combine arbitrarily:

```java
if ((age >= 18 && hasLicense) || isSupervised) {
  // Can drive if both conditions, or if supervised
}
```

Parentheses make intent clear.

*[40 minutes]*

## Short-Circuit Evaluation

Here's a performance detail. Logical operators short-circuit.

With `&&`, if the first part is false, the second part isn't even evaluated:

```java
if (false && someExpensiveFunction()) {
  // someExpensiveFunction() is never called!
}
```

Why? Because the result is definitely false. If the first part is false, AND can't be true, so why bother checking the second part?

With `||`, if the first part is true, the second part isn't evaluated:

```java
if (true || someExpensiveFunction()) {
  // someExpensiveFunction() is never called!
}
```

Why? Because the result is definitely true. If the first part is true, OR is definitely true.

This is an optimization, but it's also sometimes a *requirement* for correctness:

```java
int[] arr = null;
if (arr != null && arr.length > 0) {
  // Safe! If arr is null, the second part isn't evaluated
  // So we don't get a null pointer exception
}
```

If arr is null, `arr != null` is false, so the second part isn't evaluated. We don't try to access `arr.length` on a null array.

*[42 minutes]*

## Operator Precedence

When you have a complex expression, which operations happen first?

```java
int result = 2 + 3 * 4 - 1;
```

Highest to lowest:

1. Unary: `++`, `--`, `!`
2. Multiplicative: `*`, `/`, `%`
3. Additive: `+`, `-`
4. Relational: `<`, `>`, `<=`, `>=`
5. Equality: `==`, `!=`
6. Logical AND: `&&`
7. Logical OR: `||`
8. Assignment: `=`, `+=`, etc.

So in that expression:
1. 3 * 4 = 12 (multiplicative first)
2. 2 + 12 = 14 (additive)
3. 14 - 1 = 13 (additive)

Result: 13.

You don't need to memorize this. Just use parentheses to be explicit:

```java
int result = (2 + 3) * (4 - 1);  // 15
```

Clear. Explicit. Professional.

Good rule: If you have to think about precedence, use parentheses. Clarity beats cleverness.

*[44 minutes]*

## The Ternary Operator

One more operator. It's conditional—like a mini if-else on one line.

Syntax: `condition ? valueIfTrue : valueIfFalse`

```java
int age = 20;
String status = age >= 18 ? "adult" : "minor";
```

If age is at least 18, status is "adult". Otherwise, it's "minor".

Another example:

```java
int a = 5, b = 10;
int max = a > b ? a : b;  // max is 10
```

If a is greater than b, max is a. Otherwise, max is b. Effectively, it's the max.

The ternary operator is useful for simple decisions. It's compact. But it can make code hard to read if overused:

```java
int result = x > 5 ? y > 10 ? 1 : 2 : z < 5 ? 3 : 4;
```

Don't do that. It's unreadable. Use ternary for simple cases only. For complex logic, use if-else blocks.

*[46 minutes]*

## Code Documentation - Why It Matters

Now, documentation. This matters as much as the code itself.

Code is read far more often than it's written. You write it once. But you—and others—read it many times. Weeks from now. Years from now. When you've forgotten why you wrote something.

Your teammates need to understand your code. Your future self needs to understand your code.

Professional code has clear, helpful comments. Not to explain obvious things, but to explain intent. Why did I make this choice? What's non-obvious here?

Java supports two types of comments: regular comments and Javadoc comments. Both matter.

*[48 minutes]*

## Single-Line Comments

Single-line comments start with `//`:

```java
// This is a comment
int x = 5;  // x is an integer
```

Everything after `//` on that line is a comment. Not executed.

When do you use them? To explain *why*, not *what*.

Good comment:

```java
// Multiply by 1000 to convert seconds to milliseconds
int ms = seconds * 1000;
```

This explains the intent. Why are we multiplying by 1000? To convert units.

Bad comment:

```java
// Multiply by 1000
int ms = seconds * 1000;
```

This just restates the code. "Multiply by 1000." Yes, I can see that from the code.

Another good comment:

```java
// Exclude expired items from the list
for (Item item : items) {
  if (!item.isExpired()) {
    result.add(item);
  }
}
```

This explains what the loop does conceptually.

Bad comment:

```java
// Loop through items
for (Item item : items) {
```

That's obvious from the code.

Write comments for future readers—the non-obvious logic, the design decisions, the gotchas.

*[50 minutes]*

## Multi-Line Comments

Multi-line comments span multiple lines:

```java
/*
 * This method processes user data
 * and returns a formatted result
 */
public String formatUser(User user) {
  // ...
}
```

Syntax: `/*` starts the comment, `*/` ends it.

Use these for larger explanations. Documenting sections of code. File headers.

```java
/*
 * UserService.java
 * Handles user authentication and profile management
 * Author: Emily Osborne
 * Created: 2024
 */
```

Both single-line and multi-line comments are regular comments. They explain logic.

Javadoc is different. It's for documentation generation.

*[52 minutes]*

## Javadoc Comments

Javadoc is a special comment format that generates HTML documentation automatically.

Syntax: `/**` starts, `*/` ends.

```java
/**
 * Calculates the sum of two numbers
 * @param a the first number
 * @param b the second number
 * @return the sum of a and b
 */
public int add(int a, int b) {
  return a + b;
}
```

Use Javadoc for:
- Public classes
- Public methods
- Public fields
- APIs others will use

Key tags:
- `@param paramName` - Describes a parameter
- `@return` - Describes the return value
- `@throws ExceptionType` - Documents exceptions
- `@author` - Who wrote it
- `@deprecated` - Marks as outdated
- `@see` - References related code
- `@version` - Version info
- `@since` - When it was added

Example:

```java
/**
 * Finds the user with the given ID
 * 
 * @param id the user ID (must be positive)
 * @return the User object, or null if not found
 * @throws IllegalArgumentException if id is not positive
 * @author Emily Osborne
 * @since 1.0
 */
public User findById(int id) {
  if (id <= 0) {
    throw new IllegalArgumentException("ID must be positive");
  }
  // implementation
}
```

Professional. Complete. Helpful.

*[54 minutes]*

## Commenting Best Practices

Rules for good comments:

First: Comment *why*, not *what*. Readers can read code. They need to understand intent.

Second: Keep comments accurate. Outdated comments are worse than no comments. They're lies.

Third: Don't comment obvious code.

```java
// Bad
int x = 5;  // Set x to 5

// Good
final int TIMEOUT_MS = 5000;  // Wait 5 seconds before retrying
```

Fourth: Use Javadoc for public APIs. Use regular comments for complex logic.

Fifth: Clean code + good comments = excellent code. Bad code + good comments is still bad code. Write clean code first.

Sixth: Comments are for readers. Be helpful. Be clear. Be concise.

*[56 minutes]*

## Your First Program Structure

Now you can write programs. Let me show you the basic structure:

```java
public class HelloWorld {
  public static void main(String[] args) {
    System.out.println("Hello, World!");
  }
}
```

This is the minimum. A public class. A main method. That's where execution starts.

`public` means accessible from anywhere.

`class` defines the class.

`HelloWorld` is the class name. Must match the filename.

`public static void main(String[] args)` is the main method. This is where Java starts running your code. `public` means accessible. `static` means it belongs to the class, not an instance. `void` means it returns nothing. `main` is the name. `String[] args` are command-line arguments.

`System.out.println()` prints output. `System` is a built-in class. `out` is the standard output stream. `println()` prints a line.

*[58 minutes]*

## Putting It All Together

Here's a complete program using what you learned:

```java
public class Calculator {
  public static void main(String[] args) {
    // Declare and initialize variables
    int a = 10;
    int b = 20;
    
    // Arithmetic operation
    int sum = a + b;
    System.out.println("Sum: " + sum);
    
    // Logical operation
    String status = a > b ? "a is larger" : "b is larger";
    System.out.println(status);
    
    // String operations
    String name = "Alice";
    System.out.println("Hello, " + name + "!");
  }
}
```

This program:
- Declares variables
- Performs arithmetic
- Uses the ternary operator
- Uses strings
- Prints output

It's complete. It compiles. It runs. You can save it, compile it, run it.

Save as `Calculator.java`. Compile: `javac Calculator.java`. Run: `java Calculator`.

*[60 minutes]*

---

## End of Part 2

That's Part 2! You've learned:

- Strings: Immutable, methods, comparison with `.equals()`
- StringBuilder: Mutable, efficient for building
- Arithmetic, comparison, logical operators
- Operator precedence
- Comments and Javadoc
- Your first program structure

Strings and operators are fundamental. You'll use them in every program. Documentation is how you write professional code.

Next, we move to control flow—if statements, loops, arrays. The building blocks of program logic.

Great work today. You've built a solid foundation. See you soon!

---
