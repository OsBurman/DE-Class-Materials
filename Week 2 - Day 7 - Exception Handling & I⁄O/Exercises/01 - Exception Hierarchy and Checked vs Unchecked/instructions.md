# Exercise 01: Exception Hierarchy and Checked vs Unchecked Exceptions

## Objective
Understand Java's exception hierarchy by catching specific and general exception types, and distinguish between checked exceptions (must be handled at compile time) and unchecked exceptions (runtime, optional handling).

## Background
Every Java exception is a subclass of `Throwable`. The two main branches are `Error` (JVM-level, unrecoverable) and `Exception`. Under `Exception`, `RuntimeException` and its subclasses are **unchecked** — the compiler doesn't force you to handle them. Everything else under `Exception` is **checked** — the compiler requires you to either catch it or declare `throws` on the method. Understanding this hierarchy is foundational to writing robust Java programs.

## Requirements

1. **Unchecked — ArithmeticException**: Call a method `divide(int a, int b)` that returns `a / b`. Call it with `divide(10, 0)` and catch the `ArithmeticException`. Print: `"Caught ArithmeticException: / by zero"`

2. **Unchecked — NumberFormatException**: Call `Integer.parseInt("not-a-number")` inside a try block. Catch `NumberFormatException` and print: `"Caught NumberFormatException: [message]"`

3. **Unchecked — NullPointerException**: Create a `String` variable assigned to `null`, then call `.length()` on it inside a try block. Catch `NullPointerException` and print: `"Caught NullPointerException: Cannot invoke method on null"`

4. **Checked — IOException**: Call a method `readMissingFile()` that does `new FileReader("does_not_exist.txt")`. This throws a checked `IOException` — the compiler will force you to handle it. Catch `IOException` and print: `"Caught IOException: does_not_exist.txt (No such file or directory)"`

5. **Multi-catch**: Write a method `parseAndDivide(String numStr, int divisor)` that parses `numStr` to an int and then divides 100 by it. In a single catch block, catch both `NumberFormatException` and `ArithmeticException` using multi-catch syntax (`|`). Call it twice:
   - `parseAndDivide("abc", 5)` — triggers NumberFormatException
   - `parseAndDivide("0", 0)` — triggers ArithmeticException

6. **Catching by supertype**: Catch an `ArrayIndexOutOfBoundsException` using `RuntimeException` as the catch type (its superclass). Print the actual class name using `e.getClass().getSimpleName()`.

## Hints
- `IOException` is in `java.io` — you'll need the import
- Multi-catch syntax: `catch (ExceptionA | ExceptionB e)`
- You can get the full exception message with `e.getMessage()`
- `e.getClass().getSimpleName()` returns just the class name without the package prefix

## Expected Output

```
=== Unchecked: ArithmeticException ===
Caught ArithmeticException: / by zero

=== Unchecked: NumberFormatException ===
Caught NumberFormatException: For input string: "not-a-number"

=== Unchecked: NullPointerException ===
Caught NullPointerException: Cannot invoke method on null

=== Checked: IOException ===
Caught IOException: does_not_exist.txt (No such file or directory)

=== Multi-catch ===
parseAndDivide("abc", 5): Caught NumberFormatException or ArithmeticException: For input string: "abc"
parseAndDivide("0", 0): Caught NumberFormatException or ArithmeticException: / by zero

=== Catching by Supertype ===
Caught as RuntimeException, actual type: ArrayIndexOutOfBoundsException
```
