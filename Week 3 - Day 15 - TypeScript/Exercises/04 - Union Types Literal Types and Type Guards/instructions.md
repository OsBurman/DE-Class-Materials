# Exercise 04: Union Types, Literal Types, and Type Guards

## Objective
Use union types and literal types to model values that can be one of several specific types, then apply type guards to safely narrow the type at runtime.

## Background
A union type (`A | B`) tells TypeScript a value may be one of several types. Literal types (`"admin" | "user"`) narrow unions to exact values. Type guards are conditional checks (`typeof`, `instanceof`, `in`, or custom predicates) that tell TypeScript which branch of a union you're in.

## Requirements

1. Create a `type StringOrNumber = string | number`. Write a function `formatValue(val: StringOrNumber): string` that:
   - If `val` is a string: returns `"String: " + val.toUpperCase()`
   - If `val` is a number: returns `"Number: " + val.toFixed(2)`
   Use a `typeof` type guard. Call it with `"hello"` and `42.5` and log both.

2. Create a literal type `type Status = "pending" | "active" | "inactive"`. Write a function `describeStatus(s: Status): string` that returns a descriptive sentence for each. Call it with all three values and log.

3. Create two interfaces: `interface Circle { kind: "circle"; radius: number }` and `interface Rectangle { kind: "rectangle"; width: number; height: number }`. Create a `type Shape = Circle | Rectangle`. Write a function `area(shape: Shape): number` using a **discriminated union** (switch on `shape.kind`). Call it with a circle (r=5) and a rectangle (w=4, h=6) and log the areas.

4. Create an `interface Cat { meow(): void }` and `interface Dog { bark(): void }`. Write a custom type guard function `isCat(animal: Cat | Dog): animal is Cat` that checks for the `meow` property using the `in` operator. Write a function `makeSound(animal: Cat | Dog)` that uses this guard to call the right method. Create one Cat and one Dog object and test both.

5. Declare a variable `val: string | null | undefined`. Assign `null`. Use optional chaining and nullish coalescing to safely log its length or a fallback: `"length: " + (val?.length ?? "no value")`.

## Hints
- `typeof x === "string"` is the standard type guard for primitives
- Discriminated unions use a shared literal property (`kind`, `type`, `tag`) to distinguish members
- Custom type guard functions have the return type `x is SomeType`
- The `in` operator (`"property" in object`) checks for property existence and also narrows the type

## Expected Output
```
formatValue("hello"): String: HELLO
formatValue(42.5): Number: 42.50
pending → Waiting for approval
active → Currently active
inactive → Account deactivated
Circle area (r=5): 78.54
Rectangle area (4×6): 24
Cat says: meow!
Dog says: woof!
val is null/undefined: no value
```
