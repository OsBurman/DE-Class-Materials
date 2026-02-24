# Exercise 01: Type Annotations, Inference, and Simple Types

## Objective
Practice adding explicit type annotations to variables and function parameters, and observe how TypeScript infers types automatically.

## Background
TypeScript extends JavaScript by letting you declare the *type* of every variable and parameter. When you don't annotate, TypeScript often infers the type from the assigned value. Together, annotations and inference catch bugs at compile time rather than at runtime.

## Requirements

1. Declare a variable `username` with type `string` and assign it your name. Log it.
2. Declare a variable `age` with type `number` and assign a numeric value. Log it.
3. Declare a variable `isEnrolled` with type `boolean` and assign `true`. Log it.
4. Declare a variable `score` **without** a type annotation; assign the number `98.6`. Hover over (or `tsc --noEmit`) to confirm TypeScript inferred `number`. Log the inferred-type variable.
5. Write a function `greet(name: string): string` that returns `"Hello, " + name + "!"`. Call it and log the result.
6. Write a function `add(a: number, b: number): number` that returns the sum. Call it with two numbers and log the result.
7. Write a function `isAdult(age: number): boolean` that returns `true` if `age >= 18`. Call it with `20` and `15` and log both results.
8. Attempt to assign a number to `username` (e.g., `username = 42`) and observe the **compiler error**. Leave this line commented out with a note explaining the error TypeScript would show.

## Hints
- Type annotations go after the variable name with a colon: `let x: string = "hello"`
- When a value is assigned at declaration, TypeScript infers the type — no annotation needed
- Function return types are written after the parameter list: `function f(): number { ... }`
- Run with `npx ts-node index.ts` or compile with `tsc index.ts && node index.js`

## Expected Output
```
Username: Alice
Age: 30
Is enrolled: true
Score (inferred number): 98.6
Hello, Alice!
3 + 4 = 7
isAdult(20): true
isAdult(15): false
```
