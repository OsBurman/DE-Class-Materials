# Exercise 10: Utility Types and tsconfig.json Compiler Configuration

## Objective
Apply TypeScript's built-in utility types (`Partial`, `Required`, `Readonly`, `Pick`, `Omit`, `Record`, `ReturnType`) to transform existing types, and configure a `tsconfig.json` with important compiler options.

## Background
Utility types let you derive new types from existing ones without rewriting them. They are essential in real projects for building DTOs, view models, and API contracts. The `tsconfig.json` file controls how strictly TypeScript checks your code and where it emits output.

## Requirements

### Part A — Utility Types

Given this base type (already in starter-code):
```ts
interface User {
  id: number;
  name: string;
  email: string;
  age: number;
  isAdmin: boolean;
}
```

1. Use `Partial<User>` to create an `updatePayload` variable representing a partial update (only `name` and `age`). Log it.
2. Use `Required<Partial<User>>` to create a type where all fields are required again. Assign a fully-populated object and log it.
3. Use `Readonly<User>` to create a `frozenUser`. Attempt to reassign `frozenUser.name` — comment it out and note the error.
4. Use `Pick<User, "id" | "name" | "email">` to create a `UserSummary` type. Create a variable of that type and log it.
5. Use `Omit<User, "isAdmin" | "age">` to create a `PublicProfile` type (removes sensitive/internal fields). Create a variable and log it.
6. Use `Record<string, number>` to create a `scores` object mapping student names to grades. Add three entries and log.
7. Use `ReturnType<typeof someFunction>` to capture a function's return type without writing it manually. Demonstrate with a function `getConfig()` that returns `{ host: string; port: number }`. Declare a variable with type `ReturnType<typeof getConfig>` and log it.

### Part B — tsconfig.json

8. Create a `tsconfig.json` in the starter-code folder with these settings and add a comment (in the JSON) explaining each key option:
   - `"target": "ES2020"` — output JS version
   - `"module": "commonjs"` — module system (Node-compatible)
   - `"strict": true` — enables all strict type checks
   - `"outDir": "./dist"` — compiled JS output folder
   - `"rootDir": "./"` — source root
   - `"esModuleInterop": true` — interop for default imports
   - `"experimentalDecorators": true` — enables decorators
   - `"noImplicitAny": true` — disallows implicit `any`
   - `"strictNullChecks": true` — null/undefined must be handled explicitly

## Hints
- Utility types are generic: `Partial<T>`, `Pick<T, Keys>`, `Omit<T, Keys>`
- `Readonly` prevents reassignment but not mutation of nested objects
- `ReturnType<typeof fn>` uses `typeof` at the type level (different from runtime `typeof`)
- `tsconfig.json` uses JSON with comment support (`// comments` stripped by tsc)

## Expected Output
```
Partial update: { name: 'Alice Updated', age: 31 }
Required user: { id: 1, name: 'Alice', email: 'alice@example.com', age: 30, isAdmin: false }
Readonly user name: Alice
UserSummary: { id: 1, name: 'Alice', email: 'alice@example.com' }
PublicProfile: { id: 1, name: 'Alice', email: 'alice@example.com' }
Scores: { Alice: 95, Bob: 87, Carol: 92 }
Config: { host: 'localhost', port: 3000 }
```
