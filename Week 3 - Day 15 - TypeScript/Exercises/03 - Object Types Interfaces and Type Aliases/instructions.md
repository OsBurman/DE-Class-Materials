# Exercise 03: Object Types, Interfaces, Type Aliases, and Interface vs Type

## Objective
Define and use object shapes with both `interface` and `type` alias syntax, understand the practical differences between them, and practice extending/intersecting both.

## Background
TypeScript offers two ways to describe the shape of an object: `interface` and `type`. Both are widely used, but they have subtle differences in extensibility and semantics. In professional codebases, knowing which to choose — and being able to read both — is essential.

## Requirements

### Part A — Interfaces

1. Declare an `interface Person` with properties: `name: string`, `age: number`, and optional `email?: string`.
2. Create two objects that satisfy `Person`: one with `email`, one without. Log both.
3. Extend `Person` with an `interface Employee extends Person` that adds `company: string` and `salary: number`. Create an `Employee` object and log it.
4. Add a method signature to `Person`: `greet(): string`. Update your Person objects to include the method (or create a new object). Call `greet()` and log the result.

### Part B — Type Aliases

5. Create a `type Coordinate = { x: number; y: number }`. Create two Coordinate objects and log them.
6. Create an intersection type: `type Point3D = Coordinate & { z: number }`. Create a `Point3D` and log it.

### Part C — Interface vs Type

7. Demonstrate **declaration merging** (unique to `interface`): declare `interface Animal` twice — first with `name: string`, then again adding `sound: string`. Create an object that satisfies the merged interface. Log it.
8. Demonstrate that `type` **cannot** be re-declared (leave a comment showing the would-be duplicate and noting the error).
9. Log a clear comparison table comment explaining: when to use interface (extendable shapes, OOP-style), when to use type (unions, intersections, primitives, tuples).

## Hints
- `interface` supports declaration merging; `type` does not
- Use `extends` for interface inheritance; use `&` for type intersections
- `type` can represent union types (`type ID = string | number`) but `interface` cannot
- Optional properties use `?` after the property name

## Expected Output
```
Person without email: { name: 'Bob', age: 25 }
Person with email: { name: 'Alice', age: 30, email: 'alice@example.com' }
Employee: { name: 'Carol', age: 28, company: 'Acme', salary: 75000 }
greet: Hello, my name is Alice
Coordinate A: { x: 1, y: 2 }
Coordinate B: { x: 5, y: 10 }
Point3D: { x: 3, y: 4, z: 7 }
Merged Animal: { name: 'Dog', sound: 'Woof' }
```
