# Exercise 06 — Exception Handling

## Overview

Build an **ATM Simulator** that processes deposits, withdrawals, and transfers between accounts. Robust error handling protects against invalid inputs and business-rule violations.

---

## Concepts Covered

- `try / catch / finally` blocks
- Multiple `catch` blocks (specific before general)
- Custom checked exceptions (extend `Exception`)
- Custom unchecked exceptions (extend `RuntimeException`)
- `throws` declaration on methods
- `throw` keyword
- `try-with-resources` for auto-closing resources
- Exception chaining (`new Exception("msg", cause)`)
- `finally` for guaranteed cleanup

---

## Custom Exceptions to Create

| Exception | Type | Thrown when |
|---|---|---|
| `InsufficientFundsException` | checked | withdrawal > balance |
| `InvalidAmountException` | unchecked | amount ≤ 0 |
| `AccountNotFoundException` | checked | account id not found |
| `DailyLimitExceededException` | checked | withdrawal > daily limit |

---

## TODOs

### Custom Exceptions
- [ ] **TODO 1** — Create `InsufficientFundsException` (checked) with `shortfall` field
- [ ] **TODO 2** — Create `InvalidAmountException` (unchecked)
- [ ] **TODO 3** — Create `AccountNotFoundException` (checked)
- [ ] **TODO 4** — Create `DailyLimitExceededException` (checked)

### BankAccount.java
- [ ] **TODO 5** — Implement `deposit(double amount)` — throw `InvalidAmountException` if ≤ 0
- [ ] **TODO 6** — Implement `withdraw(double amount)` — throw `InvalidAmountException`, `DailyLimitExceededException`, `InsufficientFundsException`
- [ ] **TODO 7** — Implement `transfer(BankAccount target, double amount)` — delegates to withdraw/deposit, rethrows

### ATM.java
- [ ] **TODO 8** — Implement `processTransaction(…)` with proper `try/catch/finally`
- [ ] **TODO 9** — Use `try-with-resources` to write a transaction log with `TransactionLogger`

---

## Running the Program

```bash
cd starter-code/src
javac *.java
java Main
```
