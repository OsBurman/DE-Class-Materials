// Exercise 01: Type Annotations, Inference, and Simple Types — SOLUTION

// 1. Explicit string annotation
let username: string = "Alice";
console.log("Username:", username);

// 2. Explicit number annotation
let age: number = 30;
console.log("Age:", age);

// 3. Explicit boolean annotation
let isEnrolled: boolean = true;
console.log("Is enrolled:", isEnrolled);

// 4. No annotation — TypeScript infers `number` from the literal 98.6
let score = 98.6;
console.log("Score (inferred number):", score);

// 5. Function with typed parameter and typed return value
function greet(name: string): string {
  return "Hello, " + name + "!";
}
console.log(greet("Alice"));

// 6. Function adding two numbers — return type is explicit
function add(a: number, b: number): number {
  return a + b;
}
console.log(`3 + 4 = ${add(3, 4)}`);

// 7. Function returning a boolean
function isAdult(userAge: number): boolean {
  return userAge >= 18;
}
console.log("isAdult(20):", isAdult(20));
console.log("isAdult(15):", isAdult(15));

// 8. Type error demonstration (commented out):
// username = 42;
// Error: Type 'number' is not assignable to type 'string'.
// TypeScript knows `username` was declared as string and refuses the number assignment.
