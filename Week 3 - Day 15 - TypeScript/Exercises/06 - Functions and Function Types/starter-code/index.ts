// Exercise 06: Functions and Function Types

// TODO 1: Write function multiply(a: number, b: number = 2): number
//         Log multiply(4) → 8  and  multiply(3, 5) → 15


// TODO 2: Write function joinStrings(separator: string, ...words: string[]): string
//         that joins words with the separator.
//         Log joinStrings("-", "one", "two", "three") → "one-two-three"


// TODO 3: Declare type MathOperation = (a: number, b: number) => number
//         Assign add and subtract function expressions to variables of this type.
//         Log add(10, 3) → 13  and  subtract(10, 3) → 7


// TODO 4: Write function applyOperation(a: number, b: number, op: MathOperation): number
//         Pass add and subtract as the third argument. Log both results.
//         Format: "applyOperation add: 20"  /  "applyOperation subtract: 4"


// TODO 5: Write function findFirst<T>(arr: T[], predicate: (item: T) => boolean): T | undefined
//         Test with [5, 15, 3, 20] → find first > 10
//         Test with ["Bob", "Alice", "Carol"] → find first starting with "A"
//         Log both results.


// TODO 6: Write overloaded function format:
//         Overload 1: format(value: string): string  → "str: " + value
//         Overload 2: format(value: number): string  → "num: " + value.toFixed(2)
//         (Write overload signatures then implementation signature)
//         Log format("hello") and format(3.14159)


// TODO 7: Write function makeAdder(n: number): (x: number) => number
//         It should return a closure that adds n to x.
//         Create add5 = makeAdder(5) and log add5(10) → 15
