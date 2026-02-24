# Exercise 07: Type Casting and Type Assertions

## Objective
Use TypeScript's `as` keyword and the `satisfies` operator to assert types, safely cast between related types, and perform double assertions when necessary.

## Background
TypeScript's type inference is powerful, but sometimes you know more about a value's type than the compiler does — especially when working with DOM APIs, JSON responses, or values typed as `unknown`. Type assertions let you tell TypeScript "trust me, this is a `T`." The `satisfies` operator validates against a type without widening the variable's inferred type.

## Requirements

1. Cast an `unknown` value to `string` using `as`:
   ```ts
   const rawInput: unknown = "TypeScript";
   ```
   Assert it as `string`, call `.toUpperCase()`, and log the result.

2. DOM casting — select an element and assert its type:
   ```ts
   // In Node/ts-node context, simulate with:
   const elem = { id: "btn", disabled: false } as unknown as HTMLButtonElement;
   ```
   Access `elem.disabled` after the assertion and log it.

3. Demonstrate **double assertion** (escape hatch for incompatible types):
   ```ts
   const numericId: number = 123;
   ```
   Use `numericId as unknown as string` to force it to `string`, then log it. Add a comment warning that this bypasses type safety and should rarely be used.

4. Use `as const` to create a read-only literal tuple:
   ```ts
   const ROLES = ["admin", "user", "moderator"] as const;
   ```
   Log `ROLES[0]`. Try to push to `ROLES` — comment out and note the error.
   Also log `typeof ROLES[number]` as a comment showing the inferred union type.

5. Use the `satisfies` operator:
   ```ts
   type Palette = { [key: string]: [number, number, number] };
   const colors = {
     red:   [255, 0, 0],
     green: [0, 255, 0],
   } satisfies Palette;
   ```
   Access `colors.red[0]` — TypeScript keeps the tuple type (not widened to `number[]`). Log it.

6. Type assertion in a function: write `function parseJSON(json: string): unknown` that calls `JSON.parse`. Then call it with `'{"name":"Alice","age":30}'`, assert the result as `{ name: string; age: number }`, and log `name` and `age`.

## Hints
- `as Type` only works when the types are compatible or one is `unknown`/`any`
- Double assertion (`as unknown as T`) should be a last resort — prefer type guards
- `as const` makes all values `readonly` and infers literal types
- `satisfies` checks a value against a type without changing the variable's own inferred type

## Expected Output
```
Uppercased: TYPESCRIPT
Button disabled: false
Double asserted: 123
ROLES[0]: admin
colors.red[0]: 255
Parsed name: Alice  age: 30
```
