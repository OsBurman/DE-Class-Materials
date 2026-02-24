# Exercise 05: String Operations and Methods

## Objective
Use the most common `String` methods to inspect, compare, and transform text data.

## Background
In Java, `String` is an immutable class in `java.lang` — every method that appears to "modify" a String actually returns a new String. You'll work with a product description string and explore the full range of built-in String methods that you'll use daily as a Java developer.

## Requirements

Given the following starting value (already declared in the starter file):
```java
String product = "  Java Programming: A Complete Guide  ";
```

1. **Inspection methods** — print the results of:
   - `length()` — total number of characters (including spaces)
   - `charAt(7)` — the character at index 7
   - `indexOf("Programming")` — the index where "Programming" first appears
   - `contains("Guide")` — whether the string contains the word "Guide"
   - `isEmpty()` — whether the string is empty
   - `startsWith("Java")` — whether it starts with "Java" (check the raw string before trimming)

2. **Transformation methods** — print the results of:
   - `trim()` — removes leading and trailing whitespace
   - `toUpperCase()` on the trimmed string
   - `toLowerCase()` on the trimmed string
   - `replace("Guide", "Reference")` on the trimmed string
   - `substring(5, 23)` on the trimmed string (extract `"Programming: A Comp"`)

3. **Splitting** — split the trimmed string on `": "` (colon-space) and print each part on its own line, prefixed with `"Part 1:"` and `"Part 2:"`.

4. **Comparison** — given two strings `"hello"` and `"HELLO"`:
   - Compare with `equals()` — print result
   - Compare with `equalsIgnoreCase()` — print result
   - Compare with `compareTo()` — print the numeric result

5. **Concatenation** — build a sentence using `+` operator: `"Language: " + "Java" + " | Version: " + 21` and print it. Note that Java automatically converts the int `21` to a String in concatenation.

## Hints
- All String methods return a **new String** — the original `product` variable is never modified.
- `trim()` only removes ASCII whitespace (spaces, tabs, newlines at the edges).
- `indexOf()` returns `-1` if the substring is not found.
- `compareTo()` returns 0 if equal, negative if the first string comes before alphabetically, positive if after.
- String indices are zero-based: index `0` is the first character.

## Expected Output
```
=== String Inspection ===
Length         : 38
charAt(7)      : r
indexOf(...)   : 7
contains(...)  : true
isEmpty()      : false
startsWith()   : false

=== String Transformation ===
trim()         : Java Programming: A Complete Guide
toUpperCase()  : JAVA PROGRAMMING: A COMPLETE GUIDE
toLowerCase()  : java programming: a complete guide
replace()      : Java Programming: A Complete Reference
substring(5,23): Programming: A Com

=== Split on ": " ===
Part 1: Java Programming
Part 2: A Complete Guide

=== String Comparison ===
equals()           : false
equalsIgnoreCase() : true
compareTo()        : 32

=== Concatenation ===
Language: Java | Version: 21
```
