# Exercise 05: Arrays, Tuples, and Enums

## Objective
Declare and manipulate typed arrays and tuples, and use TypeScript enums to represent named constant sets.

## Background
TypeScript adds types to JavaScript arrays, preventing mixed-type accidents. Tuples are fixed-length arrays where each position has a specific type. Enums give human-readable names to sets of numeric or string constants.

## Requirements

### Part A — Typed Arrays

1. Declare a `string[]` array of three fruit names. Log it and its length.
2. Declare a `number[]` array `scores = [85, 92, 78, 95, 88]`. Log the highest score using `Math.max(...scores)`.
3. Declare an `Array<boolean>` array `flags` and push three values (`true`, `false`, `true`). Log the array.
4. Attempt to push a number into `flags` — comment it out and note the error TypeScript shows.

### Part B — Tuples

5. Declare a tuple `type UserRecord = [string, number, boolean]` representing `[name, age, isActive]`. Create two `UserRecord` instances and log them.
6. Destructure a `UserRecord` into `name`, `age`, `isActive` variables and log each individually.
7. Declare an optional element tuple: `type RGBColor = [number, number, number, number?]` for `[r, g, b, alpha?]`. Create one with alpha and one without. Log both.

### Part C — Enums

8. Declare a **numeric enum** `Direction` with members `North`, `South`, `East`, `West` (default values 0–3). Log `Direction.North` (should be `0`) and `Direction[2]` (should be `"East"`).
9. Declare a **string enum** `LogLevel` with members `Info = "INFO"`, `Warn = "WARN"`, `Error = "ERROR"`. Write a function `log(level: LogLevel, message: string): void` that logs `[INFO] message`, `[WARN] message`, etc. Call it with all three levels.
10. Declare a `const enum` `Season { Spring, Summer, Autumn, Winter }`. Use it in a switch to return a description for each season. Call and log all four.

## Hints
- Tuple types enforce both the length and per-index types at compile time
- Enum members are accessed like `Direction.North`; reverse-mapped (numeric enums only) like `Direction[0]`
- `const enum` members are inlined by the compiler and produce no runtime object
- `Array<T>` and `T[]` are interchangeable — pick one style and be consistent

## Expected Output
```
Fruits: [ 'apple', 'banana', 'cherry' ] length: 3
Highest score: 95
Flags: [ true, false, true ]
UserRecord 1: [ 'Alice', 30, true ]
UserRecord 2: [ 'Bob', 25, false ]
name: Alice  age: 30  isActive: true
RGB no alpha: [ 255, 128, 0 ]
RGB with alpha: [ 255, 128, 0, 0.5 ]
Direction.North: 0
Direction[2]: East
[INFO] Server started
[WARN] Disk space low
[ERROR] Database connection failed
Spring: flowers blooming
Summer: hot and sunny
Autumn: leaves falling
Winter: cold and snowy
```
