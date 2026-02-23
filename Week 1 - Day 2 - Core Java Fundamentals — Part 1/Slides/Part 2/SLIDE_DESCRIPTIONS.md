# Part 2: Strings, Operators & Code Documentation
## Slide Descriptions

---

### SLIDE 1: Welcome Back - Part 2 Begins
**Visual:** Transition slide
**Content:**
- Welcome back from lunch!
- Part 2: Strings, operators, documentation
- Let's keep the momentum going
- Writing your first Java program is close

---

### SLIDE 2: Learning Objectives - Part 2
**Visual:** Bulleted objectives
**Content:**
- Work with String data type and methods
- Understand String immutability
- Use StringBuilder and StringBuffer efficiently
- Apply mathematical, logical, and comparison operators
- Write clear, well-documented code
- Comment and document professionally

---

### SLIDE 3: Strings in Java
**Visual:** String concept
**Content:**
- String: Sequence of characters
- Reference type (not primitive)
- Technically, String is an object wrapping char array
- Used everywhere: Messages, names, data, output
- Immutable: Once created, can't be changed
- Most common data type after primitives

---

### SLIDE 4: String Declaration and Initialization
**Visual:** String examples
**Content:**
- Declare: `String name;`
- Initialize: `String name = "John";`
- Double quotes (not single)
- Single quotes for char, double for String
- Strings are objects: `String s = new String("text");`
- But usually simpler: `String s = "text";`
- Empty string: `String empty = "";`

---

### SLIDE 5: String Immutability
**Visual:** Immutability visualization
**Content:**
- String is immutable: Once created, cannot change
- Example:
  ```
  String s = "Hello";
  s = s + " World";  // Creates NEW string
  ```
- Original "Hello" still exists; new "Hello World" created
- `s` now references new string
- Why immutable? Thread safety, caching, security
- Consequence: Creating many strings creates garbage

---

### SLIDE 6: Common String Methods - Part 1
**Visual:** Method examples
**Content:**
- `.length()` - Return length
- `.charAt(index)` - Get character at index
- `.substring(start)` - Extract from index to end
- `.substring(start, end)` - Extract range
- `.toUpperCase()` - Convert to uppercase
- `.toLowerCase()` - Convert to lowercase
- `.trim()` - Remove leading/trailing whitespace
- These return new strings (immutable)

---

### SLIDE 7: Common String Methods - Part 2
**Visual:** Method examples
**Content:**
- `.equals(other)` - Compare strings
- `.equalsIgnoreCase(other)` - Compare, ignore case
- `.contains(sub)` - Check if contains substring
- `.startsWith(prefix)` - Check prefix
- `.endsWith(suffix)` - Check suffix
- `.indexOf(char)` - Find index of character
- `.replace(old, new)` - Replace substring
- `.split(delimiter)` - Split into array

---

### SLIDE 8: String Comparison - equals vs ==
**Visual:** Comparison pitfall
**Content:**
- `==` compares references (memory addresses)
- `.equals()` compares content
- Danger:
  ```
  String s1 = new String("hello");
  String s2 = new String("hello");
  s1 == s2;        // false (different objects)
  s1.equals(s2);   // true (same content)
  ```
- Always use `.equals()` for string content comparison
- Use `==` for null checks only

---

### SLIDE 9: String Concatenation
**Visual:** Concatenation examples
**Content:**
- `+` operator: `String s = "Hello" + " " + "World";`
- `.concat()`: `s.concat(" there")`
- String template (Java 15+): `String s = "Hello %s".formatted(name);`
- Inside loops: Avoid `+` (creates garbage)
- Use StringBuilder for loops (next slide)

---

### SLIDE 10: StringBuilder for Efficient String Building
**Visual:** StringBuilder explanation
**Content:**
- StringBuilder: Mutable string builder
- Not immutable like String
- Efficient: Doesn't create new object each time
- Use in loops and when concatenating many times
- Example:
  ```
  StringBuilder sb = new StringBuilder();
  for (int i = 0; i < 1000; i++) {
    sb.append(i);
  }
  String result = sb.toString();
  ```
- `.append()` adds to builder
- `.toString()` converts back to String

---

### SLIDE 11: StringBuilder Methods
**Visual:** Common methods
**Content:**
- `.append(x)` - Add to end
- `.insert(index, x)` - Insert at position
- `.delete(start, end)` - Remove range
- `.reverse()` - Reverse content
- `.length()` - Current length
- `.toString()` - Convert to String
- `.setCharAt(index, char)` - Change character
- Mutable: Operations modify the builder

---

### SLIDE 12: StringBuffer vs StringBuilder
**Visual:** Comparison table
**Content:**
- StringBuffer: Synchronized, thread-safe, older
- StringBuilder: Not synchronized, faster, modern
- StringBuffer: Rarely used today
- StringBuilder: Preferred in new code
- Performance: StringBuilder faster
- Use: StringBuilder unless in multi-threaded code with synchronization needs
- Most code: Use StringBuilder

---

### SLIDE 13: When to Use Each String Type
**Visual:** Decision guide
**Content:**
- `String`: Normal usage, when immutability desired
- `StringBuilder`: Building strings in loops or repeated concatenation
- `StringBuffer`: Multi-threaded code where thread safety needed (rare)
- Rule: String by default. StringBuilder if performance matters
- Example: Reading file and building output? Use StringBuilder

---

### SLIDE 14: Introduction to Operators
**Visual:** Operator categories
**Content:**
- Operators: Symbols performing operations
- Categories:
  - Arithmetic: +, -, *, /, %
  - Comparison: ==, !=, <, >, <=, >=
  - Logical: &&, ||, !
  - Assignment: =, +=, -=, etc.
  - Bitwise: &, |, ^, ~ (advanced, skip for now)
  - Ternary: condition ? true : false
- Learn in context of what they do

---

### SLIDE 15: Arithmetic Operators
**Visual:** Arithmetic examples
**Content:**
- `+`: Addition - `5 + 3` = 8
- `-`: Subtraction - `5 - 3` = 2
- `*`: Multiplication - `5 * 3` = 15
- `/`: Division - `6 / 2` = 3 (integer division if both ints)
- `%`: Modulo (remainder) - `7 % 3` = 1
- Order of operations: *, /, % before +, -
- Parentheses override: `(2 + 3) * 4` = 20
- Division by zero: Throws exception

---

### SLIDE 16: Arithmetic Operators - Division Detail
**Visual:** Division examples
**Content:**
- Integer division: `7 / 2` = 3 (not 3.5)
- Why? Both operands are int, result is int
- Decimal division: `7.0 / 2` = 3.5 (one is double)
- Cast if needed: `(double) 7 / 2` = 3.5
- Common mistake: Forgetting decimal types
- `int x = 7 / 2;` gives 3, not 3.5

---

### SLIDE 17: Increment and Decrement Operators
**Visual:** Inc/Dec examples
**Content:**
- `++`: Increment by 1
  - Pre-increment: `++x` (increment, then use)
  - Post-increment: `x++` (use, then increment)
- `--`: Decrement by 1
  - Pre-decrement: `--x` (decrement, then use)
  - Post-decrement: `x--` (use, then decrement)
- In loops: Usually doesn't matter which
- Difference matters when value is used
- Example:
  ```
  int x = 5;
  int y = x++;  // y = 5, x = 6
  int z = ++x;  // x = 7, z = 7
  ```

---

### SLIDE 18: Compound Assignment Operators
**Visual:** Compound operators
**Content:**
- `+=`: Add and assign - `x += 5` equals `x = x + 5`
- `-=`: Subtract and assign - `x -= 3` equals `x = x - 3`
- `*=`: Multiply and assign - `x *= 2` equals `x = x * 2`
- `/=`: Divide and assign - `x /= 2` equals `x = x / 2`
- `%=`: Modulo and assign - `x %= 3` equals `x = x % 3`
- Shorthand: Less typing, same result
- Preference: Use if familiar; both work

---

### SLIDE 19: Comparison Operators
**Visual:** Comparison examples
**Content:**
- `==`: Equal to - `5 == 5` is true
- `!=`: Not equal to - `5 != 3` is true
- `<`: Less than - `3 < 5` is true
- `>`: Greater than - `5 > 3` is true
- `<=`: Less than or equal - `5 <= 5` is true
- `>=`: Greater than or equal - `5 >= 3` is true
- Result: Always boolean (true or false)
- Use in if statements and loops

---

### SLIDE 20: Logical Operators
**Visual:** Logical operator truth tables
**Content:**
- `&&`: AND - true only if both true
  - `true && true` = true
  - `true && false` = false
  - `false && false` = false
- `||`: OR - true if at least one true
  - `true || false` = true
  - `false || false` = false
- `!`: NOT - opposite
  - `!true` = false
  - `!false` = true
- Short-circuit: && stops if first is false, || stops if first is true

---

### SLIDE 21: Combining Logical Operators
**Visual:** Complex logical examples
**Content:**
- Combining: `age >= 18 && hasLicense`
- Or: `isWeekend || isHoliday`
- Not: `!isRaining`
- Complex: `(age >= 18 && hasLicense) || isSupervised`
- Parentheses clarify intent
- De Morgan's Law: `!(A && B)` equals `!A || !B`
- Precedence: && before || before !=

---

### SLIDE 22: Operator Precedence
**Visual:** Precedence table
**Content:**
- Highest to lowest:
  1. Unary: ++, --, !
  2. Multiplicative: *, /, %
  3. Additive: +, -
  4. Relational: <, >, <=, >=
  5. Equality: ==, !=
  6. Logical AND: &&
  7. Logical OR: ||
  8. Assignment: =, +=, etc.
- Use parentheses when unclear
- Better: Explicit with parentheses than relying on precedence

---

### SLIDE 23: Ternary Operator
**Visual:** Ternary syntax
**Content:**
- Conditional operator: `condition ? trueValue : falseValue`
- Returns value based on condition
- Example: `int max = a > b ? a : b;`
- Example: `String status = isActive ? "online" : "offline";`
- Shorthand for simple if-else
- Abuse can make code hard to read
- Use sparingly; clarity > cleverness

---

### SLIDE 24: Code Documentation - Why It Matters
**Visual:** Documentation importance
**Content:**
- Code is read more often than written
- Future you: Will forget why you wrote something
- Teammates: Need to understand your code
- Documentation: Explains intent and logic
- Two types: Comments and Javadoc
- Both matter for professional code

---

### SLIDE 25: Single-Line Comments
**Visual:** Comment examples
**Content:**
- Syntax: `// comment text`
- Everything after // on that line is comment
- Not executed by Java
- Use: Explain *why*, not *what*
- Good: `// Multiply by 1000 to convert seconds to milliseconds`
- Bad: `// Multiply`
- Good practice: Explain non-obvious logic

---

### SLIDE 26: Multi-Line Comments
**Visual:** Multi-line comment example
**Content:**
- Syntax: `/* comment */ ` or `/* multi-line comment */`
- Everything between /* and */ is comment
- Can span multiple lines
- Use: Larger explanations, documenting sections
- Example:
  ```
  /*
   * This method calculates total price
   * including tax and shipping
   */
  ```
- Often used for file headers

---

### SLIDE 27: Javadoc Comments
**Visual:** Javadoc format
**Content:**
- Syntax: `/** javadoc comment */`
- Special comment format for documentation
- Generates HTML documentation automatically
- Use: Document classes, methods, fields
- Example:
  ```
  /**
   * Calculate the sum of two numbers
   * @param a first number
   * @param b second number
   * @return sum of a and b
   */
  public int add(int a, int b) { ... }
  ```
- Tags: @param, @return, @throws, @author, @deprecated

---

### SLIDE 28: Commenting Best Practices
**Visual:** Guidelines
**Content:**
- Comment *why*, not *what*
  - Good: `// Exclude items after expiration date`
  - Bad: `// Check if date > today`
- Keep comments accurate
- Outdated comments are worse than no comments
- Don't comment obvious code
  - Good for: Algorithms, non-obvious logic
  - Bad for: `int x = 5; // set x to 5`
- Use Javadoc for public APIs
- Professional: Clean code + good comments = excellent code

---

### SLIDE 29: Common Documentation Tags
**Visual:** Javadoc tags
**Content:**
- `@param paramName` - Parameter description
- `@return` - Return value description
- `@throws ExceptionType` - Exception thrown
- `@author` - Author of code
- `@deprecated` - Mark as outdated
- `@see` - Reference to related element
- `@version` - Version information
- `@since` - Version when added
- These standardize documentation

---

### SLIDE 30: Javadoc Example - Method
**Visual:** Complete example
**Content:**
```
/**
 * Calculates the factorial of a number
 * 
 * @param n the number (must be >= 0)
 * @return factorial of n
 * @throws IllegalArgumentException if n < 0
 */
public int factorial(int n) {
  if (n < 0) {
    throw new IllegalArgumentException("n must be >= 0");
  }
  // implementation...
}
```
- Clear, complete, professional

---

### SLIDE 31: Documentation in Your Code
**Visual:** Code structure with docs
**Content:**
- File header: What this file does
- Class documentation: Purpose of class
- Method documentation: What method does, parameters, return
- Inline comments: Complex logic
- No comments for trivial code
- Balance: Enough to understand, not noise

---

### SLIDE 32: String Practice Examples
**Visual:** Code examples
**Content:**
- Creating strings and using methods
- Comparing strings correctly
- Building strings efficiently
- Common patterns developers use

---

### SLIDE 33: Operator Practice Examples
**Visual:** Code examples
**Content:**
- Arithmetic with different types
- Comparison for conditionals
- Logical operators in complex expressions
- Ternary for simple decisions

---

### SLIDE 34: Your First Java Program Structure
**Visual:** Program template
**Content:**
```
public class HelloWorld {
  public static void main(String[] args) {
    System.out.println("Hello, World!");
  }
}
```
- `public class`: Your program's main class
- `main` method: Where execution starts
- `String[] args`: Command-line arguments
- `System.out.println()`: Print with newline

---

### SLIDE 35: Running Java Programs
**Visual:** Terminal steps
**Content:**
1. Save code in file: `HelloWorld.java`
2. Compile: `javac HelloWorld.java`
   - Produces: `HelloWorld.class`
3. Run: `java HelloWorld`
   - Produces: Output
- Separate compile and run steps
- Must match filename and class name

---

### SLIDE 36: Common Program Structure
**Visual:** Program organization
**Content:**
- Class declaration
- Member variables
- Constructor
- Methods
- Main method for testing
- Example structure (you'll see this pattern constantly)

---

### SLIDE 37: Printing Output
**Visual:** Output methods
**Content:**
- `System.out.println(x)` - Print with newline
- `System.out.print(x)` - Print without newline
- Can print any type: strings, numbers, objects
- String concatenation in print:
  ```
  System.out.println("The answer is " + 42);
  ```
- Debugging: Print values to understand flow

---

### SLIDE 38: Putting It All Together
**Visual:** Example program
**Content:**
```
public class Calculator {
  public static void main(String[] args) {
    int a = 10;
    int b = 20;
    int sum = a + b;
    System.out.println("Sum: " + sum);
    
    String status = a > b ? "a is larger" : "b is larger";
    System.out.println(status);
  }
}
```
- Variables, operators, strings
- Logic and output
- Complete, runnable program

---

### SLIDE 39: Part 2 Summary
**Visual:** Key concepts recap
**Content:**
- Strings: Sequences of characters, immutable
- StringBuilder: For efficient string building
- Arithmetic, comparison, logical operators
- Operator precedence affects evaluation
- Comments: Explain why, not what
- Javadoc: Professional documentation
- First program: Ready to write and run

---

### SLIDE 40: Key Takeaways
**Visual:** Important reminders
**Content:**
- Always use `.equals()` for string comparison
- Use StringBuilder for repeated concatenation
- Understand operator precedence
- Comment for future readers
- Javadoc for public APIs
- Professional code is readable and documented
- You can now write simple Java programs

---

### SLIDE 41: Hands-On Exercise
**Visual:** Exercise prompt
**Content:**
- Write a program that:
  1. Creates variables with different primitive types
  2. Performs arithmetic and logical operations
  3. Uses string concatenation
  4. Prints results
- Includes comments explaining logic
- Compiles and runs without error
- You'll do this next!

---

### SLIDE 42: Q&A Session
**Visual:** Question mark
**Content:**
- Questions about Strings?
- Operator confusion?
- Documentation unclear?
- First program anxious?
- Ask now—we're about to code
- Support available

---

### SLIDE 43: Next Steps
**Visual:** Preview of hands-on lab
**Content:**
- Lab time: Write your first program
- Instructors available for help
- Topics: Variables, operators, strings, output
- Fully working program: Victory!
- Commit to Git: Save your work
- Celebrate: You're a Java developer now

---

### SLIDE 44: End of Day 2
**Visual:** Completion graphic
**Content:**
- Part 1: JVM, primitives, variables, type conversion
- Part 2: Strings, operators, documentation
- Foundation: Solid
- Next week: Control flow, loops, arrays
- You've earned rest—good work!

---
