# Exercise 03: Variables, Literals, and Constants

## Objective
Practice declaring variables using different literal formats, defining named constants with `final`, and applying Java naming conventions.

## Background
A **variable** is a named storage location whose value can change. A **constant** is declared with the `final` keyword, meaning its value cannot be reassigned after initialization. **Literals** are the actual values you type directly into source code (e.g., `42`, `3.14f`, `'A'`, `true`, `0xFF`). Java supports several literal formats including decimal, hexadecimal (`0x`), octal (`0`), binary (`0b`), and scientific notation.

## Requirements

1. Declare the following **named constants** using `final` with appropriate naming conventions (all caps, underscores between words):
   - The max speed of light in m/s: `299_792_458` (int)
   - The mathematical constant π: `3.14159265358979` (double)
   - A status code for "OK": `200` (int)
   - A tax rate: `0.085` (double)
   - A newline character: `'\n'` (char)

2. Declare variables using **non-decimal literals**:
   - An `int` using a **hexadecimal** literal for the color red: `0xFF0000`
   - An `int` using a **binary** literal for the number 12: `0b00001100`
   - An `int` using an **octal** literal for the number 8: `010`
   - A `long` using **scientific notation** via a `double` cast for 1 million: cast `1e6` to `long`

3. Print each constant and variable on its own line showing both the name and its decimal value.

4. Demonstrate that `final` variables cannot be reassigned: add a comment (do NOT actually reassign) showing where a reassignment attempt would cause a compile error.

5. Declare a `String` variable called `greeting` with the value `"Hello, Java!"` and a `String` constant called `APP_NAME` with the value `"MyApp"`. Print both.

## Hints
- Java naming convention for constants: `ALL_CAPS_WITH_UNDERSCORES` (e.g., `MAX_SPEED`).
- Java naming convention for variables: `camelCase` (e.g., `taxRate`).
- Hexadecimal literals start with `0x` or `0X`; binary literals start with `0b` or `0B`; octal literals start with `0`.
- `1e6` is a `double` literal (scientific notation). Cast it: `(long) 1e6`.
- `final` can be used on local variables too, not just fields.

## Expected Output
```
=== Constants ===
SPEED_OF_LIGHT  : 299792458
PI              : 3.14159265358979
HTTP_OK         : 200
TAX_RATE        : 0.085
NEWLINE_CHAR    : (newline character — no visible output on this line)

=== Non-Decimal Literals ===
Red (hex 0xFF0000)  : 16711680
Binary 0b00001100   : 12
Octal 010           : 8
1e6 cast to long    : 1000000

=== String Variables ===
greeting : Hello, Java!
APP_NAME : MyApp
```
