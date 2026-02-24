# Exercise 03: Template Literals and String Methods

## Objective
Use ES6 template literals for string interpolation and multi-line strings, and apply key built-in string methods to search, transform, and manipulate text.

## Background
Before ES6, string building required messy concatenation with `+`. Template literals (backtick strings) allow embedded expressions and multi-line text. Combined with the rich set of built-in `String.prototype` methods, they make text processing clean and expressive.

## Requirements

1. **Template literal basics:**
   - Declare `const firstName = "Ada"` and `const lastName = "Lovelace"`.
   - Use a template literal to produce: `Full name: Ada Lovelace`
   - Declare `const year = 1815` and embed an expression inside a template literal to log: `Born: 1815, approximately ${2026 - year} years ago`
   - Write a **multi-line** template literal (no `\n` escape, no concatenation) that produces a 3-line address block and log it.

2. **Case conversion:**
   - Given `const sentence = "  JavaScript is Awesome!  "`, log the result of:
     - `.toUpperCase()`
     - `.toLowerCase()`
     - `.trim()` (strip the surrounding spaces)

3. **Search and check methods:**
   - Using the trimmed sentence (`"JavaScript is Awesome!"`), log:
     - `.includes("Awesome")` → true
     - `.startsWith("Java")` → true
     - `.endsWith("!")` → true
     - `.indexOf("is")` → the index number

4. **Extract and replace:**
   - `.slice(0, 10)` → `"JavaScript"`
   - `.replace("Awesome", "Powerful")` → `"JavaScript is Powerful!"`
   - `.replaceAll` — given `const csv = "one,two,three,four"`, replace all commas with ` | ` to get `"one | two | three | four"`.

5. **Split and join:**
   - Split `csv` by `,` into an array, log the array.
   - Join the array back with ` - ` as the separator and log the result.

6. **Padding and repeat:**
   - `.padStart(15, "*")` on `"hello"` → `"**********hello"`
   - `.padEnd(15, "-")` on `"hello"` → `"hello----------"`
   - `.repeat(3)` on `"AB"` → `"ABABAB"`

7. **String to number round-trip:**
   - Use a template literal to build the string `"Price: $19.99"`.
   - Extract just the number part with `.slice()` and convert it to a `Number`.
   - Log the result and its `typeof`.

## Hints
- Template literals use backticks `` ` `` not quotes. Expressions go inside `${ }`.
- Multi-line template literals preserve the actual newlines you type — no need for `\n`.
- `.slice(start, end)` — `end` is exclusive. `.slice(0, 10)` gives 10 characters.
- `.split()` returns an array; `.join()` on an array returns a string — they are inverses.

## Expected Output

```
Full name: Ada Lovelace
Born: 1815, approximately 211 years ago

123 Main Street
Springfield, IL
62701

JAVASCRIPT IS AWESOME!
javascript is awesome!
JavaScript is Awesome!

includes "Awesome": true
startsWith "Java": true
endsWith "!": true
indexOf "is": 11

slice(0,10): JavaScript
replace: JavaScript is Powerful!
replaceAll commas: one | two | three | four

split csv: [ 'one', 'two', 'three', 'four' ]
join with " - ": one - two - three - four

padStart(15, "*"): **********hello
padEnd(15, "-"): hello----------
repeat(3): ABABAB

Price string: Price: $19.99
Extracted number: 19.99
typeof extracted: number
```
