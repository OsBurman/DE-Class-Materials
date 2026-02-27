# Day 4 Application — OOP Part 1: Bank Account System

## Overview

You'll build a **Bank Account System** — a Java console application that models bank accounts with deposits, withdrawals, and balance tracking. This exercises classes, objects, constructors, access modifiers, static members, and the `this` keyword.

---

## Learning Goals

- Design and implement classes with proper encapsulation
- Create objects using default and parameterized constructors
- Use constructor chaining with `this()`
- Apply access modifiers (`private`, `public`) correctly
- Use `static` fields and methods for shared data
- Use the `final` modifier for constants
- Distinguish between static and instance members

---

## Project Structure

```
starter-code/
└── src/
    ├── Main.java               ← entry point
    ├── BankAccount.java        ← TODO: complete this class
    └── SavingsAccount.java     ← TODO: extends BankAccount (basic)
```

---

## Part 1 — Complete `BankAccount.java`

**Task 1 — Fields**
Declare these fields with appropriate access modifiers:
- `accountNumber` — `private String` (set once, never changes)
- `accountHolderName` — `private String`
- `balance` — `private double`
- `totalAccounts` — `private static int` (counts all accounts ever created)
- `INTEREST_RATE` — `public static final double` = `0.035`

**Task 2 — Default constructor**
Set `accountHolderName` to `"Unknown"`, `balance` to `0.0`, auto-generate `accountNumber` as `"ACC-" + totalAccounts`, and increment `totalAccounts`.

**Task 3 — Parameterized constructor**
Accept `accountHolderName` and an initial `balance`. Use **constructor chaining** — call `this()` first to run the default constructor logic, then override the name and balance.

**Task 4 — Getters and setters**
- `getBalance()`, `getAccountNumber()`, `getAccountHolderName()` — public getters
- `setAccountHolderName(String name)` — public setter (validate: reject empty/null strings)
- No setter for `balance` or `accountNumber` (encapsulated, changed only through methods)

**Task 5 — `deposit(double amount)`**
- `public` method
- Validate: amount must be > 0, otherwise throw `IllegalArgumentException`
- Add to balance

**Task 6 — `withdraw(double amount)`**
- Validate: amount > 0 AND amount ≤ balance
- Subtract from balance, return `true` if successful, `false` if insufficient funds

**Task 7 — `applyInterest()`**
A `public` instance method that adds `balance * INTEREST_RATE` to the balance.

**Task 8 — `getTotalAccounts()`**
A `public static` method returning `totalAccounts`. Notice: it's `static` because it belongs to the class, not any single instance.

**Task 9 — `toString()`**
Override and return a formatted summary string using `String.format()`.

---

## Part 2 — Complete `SavingsAccount.java`

For now, just add one extra field: `minimumBalance` (`private double`), add it to the constructor, and override `withdraw()` so that the balance cannot drop below `minimumBalance`.

---

## Part 3 — Complete `Main.java`

**Task 10**
- Create 3 `BankAccount` objects (mix of both constructors)
- Create 1 `SavingsAccount`
- Perform deposits and withdrawals, print results
- Attempt an invalid withdrawal (below minimum or > balance) and handle it
- Print `BankAccount.getTotalAccounts()` after all accounts are created
- Demonstrate that `INTEREST_RATE` is accessed as `BankAccount.INTEREST_RATE`

---

## Stretch Goals

1. Add a `transactionHistory` as an `ArrayList<String>` field and log every deposit/withdrawal.
2. Add a `transfer(BankAccount target, double amount)` method.
3. Override `toString()` to include last 5 transactions.

---

## Submission Checklist

- [ ] Fields are `private`; accessed via public getters/setters
- [ ] Default and parameterized constructors implemented
- [ ] Constructor chaining with `this()` demonstrated
- [ ] `static` field and method present
- [ ] `static final` constant used
- [ ] `this` keyword used in constructor/setter
- [ ] Input validation in deposit/withdraw
- [ ] `getTotalAccounts()` returns correct count after creating multiple objects
