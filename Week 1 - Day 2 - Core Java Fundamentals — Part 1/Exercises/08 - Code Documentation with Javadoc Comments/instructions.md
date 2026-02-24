# Exercise 08: Code Documentation with Comments and Javadoc

## Objective
Write proper Java code documentation using single-line comments, multi-line block comments, and Javadoc comments on a utility class.

## Background
Professional Java code is documented at two levels: **inline comments** (`//` and `/* */`) that explain *how* and *why* code works, and **Javadoc comments** (`/** */`) that describe *what* a class or method does at the API level. Javadoc comments use special tags like `@param`, `@return`, and `@throws` and can be compiled into HTML documentation using the `javadoc` tool. Well-written comments are a mark of a professional developer.

## Requirements

You will write a `MathUtils` utility class with three static methods. Your primary task is to **add all comments and Javadoc** — the starter file has the method signatures and bodies already written, but the documentation is missing.

1. **Class-level Javadoc** — add a Javadoc comment above the `MathUtils` class that includes:
   - A one-sentence description of the class
   - `@author` tag with your name (use `Student` as the value)
   - `@version` tag with `1.0`

2. **Method Javadoc for `add(int a, int b)`** — add Javadoc that includes:
   - One-sentence description
   - `@param a` with description
   - `@param b` with description
   - `@return` describing what is returned

3. **Method Javadoc for `divide(double numerator, double denominator)`** — add Javadoc that includes:
   - One-sentence description
   - `@param numerator` with description
   - `@param denominator` with description
   - `@return` describing what is returned
   - `@throws ArithmeticException` describing when this is thrown

4. **Method Javadoc for `isPrime(int number)`** — add Javadoc that includes:
   - One-sentence description
   - `@param number` with description
   - `@return` describing the boolean result

5. **Inline comments in `isPrime`** — add a single-line `//` comment above every meaningful code block inside the method body explaining *what* each block does (at minimum: the edge case check, the loop setup, and the loop body).

6. **Multi-line comment** — add a `/* */` block comment at the top of the `main` method in `DocumentationDemo` (the demo class) explaining in 2–3 sentences what the program demonstrates.

7. **Run the program** — the `main` method calls all three methods and prints results. Do not change the logic, only add comments.

## Hints
- Javadoc comments start with `/**` (two asterisks), not `/*` (one asterisk).
- Each line inside a Javadoc comment is conventionally prefixed with ` * ` (space-asterisk-space).
- `@param`, `@return`, and `@throws` tags go at the end of the Javadoc block, after the description.
- Single-line comments with `//` should say *why* or *what*, not just restate the code (e.g., `// check if number is less than 2` is good; `// if n < 2` is not useful).
- The `javadoc` command-line tool can generate HTML docs from `/** */` comments automatically.

## Expected Output
```
=== MathUtils Demo ===
add(3, 7)             : 10
add(-5, 12)           : 7
divide(10.0, 4.0)     : 2.5
divide(7.0, 2.0)      : 3.5
isPrime(2)            : true
isPrime(7)            : true
isPrime(9)            : false
isPrime(1)            : false
```
