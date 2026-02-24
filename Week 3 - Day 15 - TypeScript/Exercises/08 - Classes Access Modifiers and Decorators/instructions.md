# Exercise 08: Classes in TypeScript — Access Modifiers and Decorators

## Objective
Write TypeScript classes with access modifiers (`public`, `private`, `protected`, `readonly`), use `implements` and `abstract`, and apply basic class and method decorators.

## Background
TypeScript classes add compile-time access control and abstract contracts on top of JavaScript's prototype system. Decorators (enabled via `experimentalDecorators` in tsconfig) are a meta-programming feature used heavily in Angular and NestJS to attach metadata and modify behaviour.

## Requirements

### Part A — Access Modifiers & readonly

1. Create a `class BankAccount` with:
   - `private balance: number` (constructor parameter, default 0)
   - `readonly accountNumber: string` (assigned in constructor)
   - `public deposit(amount: number): void` — adds amount to balance
   - `public withdraw(amount: number): void` — subtracts if balance is sufficient, else logs "Insufficient funds"
   - `public getBalance(): number` — returns the balance
   Create an account, deposit 500, withdraw 200, withdraw 1000, log the balance.

2. Create an `abstract class Shape` with:
   - `abstract area(): number`
   - `abstract perimeter(): number`
   - `describe(): string` — returns `"Area: X, Perimeter: Y"` (concrete method)
   Extend it with `class Circle` (radius) and `class Rectangle` (width, height).  
   Instantiate both and log their `describe()` output.

3. Demonstrate `protected`: add `protected category: string = "account"` to `BankAccount`. Create a subclass `SavingsAccount extends BankAccount` that overrides `describe()` and accesses `this.category`. Log it.

### Part B — Decorators (Overview)

4. Enable `experimentalDecorators` (you can note this needs to be set in tsconfig or add `// @ts-ignore` as a comment, then write the decorator). Write a **class decorator** `@Sealed` that calls `Object.seal` on the constructor and its prototype. Apply it to a `class Config { version = "1.0"; }`.

5. Write a **method decorator** `@Log` that logs `"Calling <methodName>"` before the method executes. Apply it to a method `sayHello()` on a class `Greeter`.

## Hints
- `private` is compile-time only; use `#field` for runtime privacy
- `readonly` prevents reassignment after construction
- `abstract` classes cannot be instantiated directly — only their concrete subclasses can
- Decorators require `"experimentalDecorators": true` in tsconfig.json

## Expected Output
```
Balance after deposits/withdrawals: 300
Insufficient funds
Circle: Area: 78.54, Perimeter: 31.42
Rectangle: Area: 24, Perimeter: 20
SavingsAccount category: account
Calling sayHello
Hello from Greeter!
```
