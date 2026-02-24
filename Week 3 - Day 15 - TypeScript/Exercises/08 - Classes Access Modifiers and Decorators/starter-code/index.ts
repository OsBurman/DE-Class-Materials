// Exercise 08: Classes in TypeScript — Access Modifiers and Decorators
// Note: Decorators require "experimentalDecorators": true in tsconfig.json
// Run with: tsc --experimentalDecorators index.ts && node index.js
//       or: npx ts-node --experimentalDecorators index.ts

// ── PART A: Access Modifiers & readonly ───────────────────────────────────────

// TODO 1: Create class BankAccount with:
//         - private balance: number (default 0)
//         - readonly accountNumber: string (set in constructor)
//         - public deposit(amount): void — adds to balance
//         - public withdraw(amount): void — subtracts if sufficient; else logs "Insufficient funds"
//         - public getBalance(): number
//         Create an account, deposit 500, withdraw 200, try to withdraw 1000.
//         Log: "Balance after deposits/withdrawals: 300"


// TODO 2: Create abstract class Shape with:
//         - abstract area(): number
//         - abstract perimeter(): number
//         - concrete describe(): string → "Area: X, Perimeter: Y"
//
//         Extend with class Circle (constructor takes radius) and class Rectangle (width, height).
//         Instantiate both and log describe() for each.
//         Format: "Circle: Area: 78.54, Perimeter: 31.42"
//                 "Rectangle: Area: 24, Perimeter: 20"


// TODO 3: Add `protected category: string = "account"` to BankAccount.
//         Create class SavingsAccount extends BankAccount.
//         Override a describe() method that returns "SavingsAccount category: " + this.category
//         Log it.


// ── PART B: Decorators ────────────────────────────────────────────────────────

// TODO 4: Write class decorator @Sealed:
//         function Sealed(constructor: Function) {
//           Object.seal(constructor);
//           Object.seal(constructor.prototype);
//         }
//         Apply it to: @Sealed class Config { version = "1.0"; }
//         (Decorator is applied at class definition — no extra log needed)


// TODO 5: Write method decorator @Log:
//         It should log "Calling <methodName>" before the original method runs.
//         Apply it to sayHello() on class Greeter.
//         sayHello() should log "Hello from Greeter!"
//         Create an instance and call sayHello().
//         Expected output:
//           Calling sayHello
//           Hello from Greeter!
