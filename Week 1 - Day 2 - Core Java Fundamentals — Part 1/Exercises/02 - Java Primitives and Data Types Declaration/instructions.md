# Exercise 02: Java Primitives and Data Types Declaration

## Objective
Declare and initialize variables of all eight Java primitive data types and print their values, demonstrating the range and characteristics of each type.

## Background
Java has exactly 8 primitive data types: `byte`, `short`, `int`, `long`, `float`, `double`, `char`, and `boolean`. Unlike objects, primitives are stored directly in memory (on the stack) and are not references. Understanding their sizes and value ranges is essential before writing any Java program.

## Requirements

1. Declare and initialize a variable for each of the 8 primitive types using representative values:
   - `byte` — use the maximum value for `byte` (`127`)
   - `short` — use `32000`
   - `int` — use `2_000_000` (use underscores in the literal for readability)
   - `long` — use `9_000_000_000L` (note the `L` suffix)
   - `float` — use `3.14f` (note the `f` suffix)
   - `double` — use `3.141592653589793`
   - `char` — use the character `'A'`
   - `boolean` — use `true`

2. Print each variable on its own line in the format:
   ```
   byte    : 127
   short   : 32000
   int     : 2000000
   long    : 9000000000
   float   : 3.14
   double  : 3.141592653589793
   char    : A
   boolean : true
   ```

3. After printing all values, print a blank line and then print the **default value** each primitive would have if it were an instance field (not a local variable):
   - `byte` default: `0`
   - `short` default: `0`
   - `int` default: `0`
   - `long` default: `0`
   - `float` default: `0.0`
   - `double` default: `0.0`
   - `char` default: `\u0000` (the null character, print as the string `\u0000`)
   - `boolean` default: `false`

4. Print a section header `=== Primitive Defaults (as instance fields) ===` before the defaults section.

## Hints
- `long` literals need an `L` suffix (uppercase is preferred); `float` literals need an `f` suffix.
- Java allows `_` inside numeric literals (e.g., `1_000_000`) for readability — the compiler ignores the underscores.
- The character `'A'` is stored as the Unicode code point `65` — try printing `(int) 'A'` to see it.
- Default values only apply to **instance fields** and **static fields**, not local variables (which must be initialized before use).

## Expected Output
```
=== Primitive Types ===
byte    : 127
short   : 32000
int     : 2000000
long    : 9000000000
float   : 3.14
double  : 3.141592653589793
char    : A
boolean : true

=== Primitive Defaults (as instance fields) ===
byte    default : 0
short   default : 0
int     default : 0
long    default : 0
float   default : 0.0
double  default : 0.0
char    default : \u0000
boolean default : false
```
