// Exercise 06: Functions and Function Types — SOLUTION

// 1. Default parameter
function multiply(a: number, b: number = 2): number {
  return a * b;
}
console.log("multiply(4):", multiply(4));       // 8 — uses default b=2
console.log("multiply(3, 5):", multiply(3, 5)); // 15

// 2. Rest parameter — collects remaining args into a typed array
function joinStrings(separator: string, ...words: string[]): string {
  return words.join(separator);
}
console.log("joinStrings:", joinStrings("-", "one", "two", "three"));

// 3. Function type alias — functions are first-class values with types
type MathOperation = (a: number, b: number) => number;

const addFn: MathOperation = (a, b) => a + b;
const subtractFn: MathOperation = (a, b) => a - b;
console.log("add(10, 3):", addFn(10, 3));
console.log("subtract(10, 3):", subtractFn(10, 3));

// 4. Higher-order function — accepts a function as a parameter
function applyOperation(a: number, b: number, op: MathOperation): number {
  return op(a, b);
}
console.log("applyOperation add:", applyOperation(12, 8, addFn));
console.log("applyOperation subtract:", applyOperation(12, 8, subtractFn));

// 5. Generic function — works for any type T
function findFirst<T>(arr: T[], predicate: (item: T) => boolean): T | undefined {
  return arr.find(predicate);
}
console.log("findFirst > 10:", findFirst([5, 15, 3, 20], (n) => n > 10));
console.log("findFirst starts A:", findFirst(["Bob", "Alice", "Carol"], (s) => s.startsWith("A")));

// 6. Function overloads — two public signatures, one implementation
function format(value: string): string;
function format(value: number): string;
function format(value: string | number): string {
  if (typeof value === "string") return "str: " + value;
  return "num: " + value.toFixed(2);
}
console.log('format("hello"):', format("hello"));
console.log("format(3.14159):", format(3.14159));

// 7. Closure factory — returns a new function capturing `n`
function makeAdder(n: number): (x: number) => number {
  return (x) => x + n;
}
const add5 = makeAdder(5);
console.log("makeAdder(5)(10):", add5(10));
