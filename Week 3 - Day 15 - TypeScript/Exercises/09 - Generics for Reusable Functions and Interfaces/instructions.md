# Exercise 09: Generics for Reusable Functions and Interfaces

## Objective
Write generic functions, interfaces, and classes that work safely with any type, apply type constraints with `extends`, and understand how generics enable reusable, type-safe code.

## Background
Generics let you write code that works with *any* type while still enforcing type safety. Instead of using `any` (which discards type information), a generic `<T>` is a placeholder resolved at call time. TypeScript infers the concrete type from the arguments you pass.

## Requirements

1. Write a generic function `identity<T>(value: T): T` that returns what it receives. Call it with a string, a number, and a boolean, letting TypeScript infer `T` each time. Log all three.

2. Write a generic function `firstElement<T>(arr: T[]): T | undefined` that returns the first element or `undefined` for an empty array. Test with a number array and an empty array. Log both.

3. Write a generic function `pair<A, B>(first: A, second: B): [A, B]` that returns a tuple. Call it with `("Alice", 30)` and with `(true, ["x","y"])`. Log both.

4. Create a `interface Repository<T>` with:
   - `findById(id: number): T | undefined`
   - `findAll(): T[]`
   - `save(item: T): void`
   Implement it as `class InMemoryRepository<T extends { id: number }>` that stores items in a private `items: T[]` array. Test with `{ id: 1, name: "Widget" }` and `{ id: 2, name: "Gadget" }`. Call `findAll()` and `findById(1)`.

5. Write a generic function `filterItems<T>(arr: T[], predicate: (item: T) => boolean): T[]`. Test it filtering strings by length > 4 and numbers by even check. Log both results.

6. Write a constrained generic `function getProperty<T, K extends keyof T>(obj: T, key: K): T[K]`. Call it with `{ name: "Bob", age: 25 }` and the keys `"name"` and `"age"`. Log both.

## Hints
- TypeScript infers the generic type parameter from the argument: `identity("hello")` infers `T = string`
- Multiple type parameters: `<A, B>` — each is resolved independently
- Constraints: `T extends SomeType` limits T to types that have SomeType's shape
- `keyof T` produces a union of the string literal types of T's own keys

## Expected Output
```
identity string: hello
identity number: 42
identity boolean: true
firstElement numbers: 10
firstElement empty: undefined
pair 1: [ 'Alice', 30 ]
pair 2: [ true, [ 'x', 'y' ] ]
findAll: [ { id: 1, name: 'Widget' }, { id: 2, name: 'Gadget' } ]
findById(1): { id: 1, name: 'Widget' }
filterStrings length>4: [ 'banana', 'cherry' ]
filterNumbers even: [ 2, 4, 6 ]
getProperty name: Bob
getProperty age: 25
```
