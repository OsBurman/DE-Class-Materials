# Exercise 02: try-catch-finally Bank Account Transactions

## Objective
Practice writing `try-catch-finally` blocks with multiple catch clauses and understand that `finally` always executes regardless of whether an exception was thrown.

## Background
A bank's transaction processing system must handle two failure modes: an account being overdrawn and invalid input amounts (negative or non-numeric). Every transaction attempt — success or failure — must log an audit entry. The `finally` block is the right tool for cleanup or logging that must always run, even when an exception occurs.

## Requirements

1. **Simulate the `BankAccount` class** (define it in the same file):
   - Fields: `String owner`, `double balance`
   - Constructor and getters
   - Method `deposit(double amount)`: throws `IllegalArgumentException` with message `"Deposit amount must be positive"` if `amount <= 0`; otherwise adds to balance
   - Method `withdraw(double amount)`: throws `IllegalArgumentException` for `amount <= 0`; throws `InsufficientFundsException` (your custom unchecked exception from this exercise — see requirement 2) if `amount > balance`; otherwise subtracts from balance

2. **Define `InsufficientFundsException`** as an unchecked exception (extends `RuntimeException`) with:
   - A constructor accepting `double amount` and `double balance`
   - Message: `"Cannot withdraw $[amount]: balance is $[balance]"` (format to 2 decimal places)

3. In `main`, create a `BankAccount("Alice", 500.00)` and perform these operations **each wrapped in its own try-catch-finally**:
   - Successful deposit of `200.00` → print `"Deposited $200.00. New balance: $700.00"`
   - Deposit of `-50.00` → catch `IllegalArgumentException`
   - Successful withdrawal of `100.00` → print `"Withdrew $100.00. New balance: $600.00"`
   - Withdrawal of `1000.00` → catch `InsufficientFundsException`
   - In **every** `finally` block, print: `"[Audit] Transaction attempt completed for Alice"`

## Hints
- The `finally` block runs even if the catch block returns or rethrows — it always executes
- `String.format("$%.2f", amount)` formats a double to 2 decimal places
- `InsufficientFundsException` should extend `RuntimeException`, not `Exception`, because it is a programming/domain logic error not requiring forced handling
- Order your catch blocks from most specific to most general — more specific subclasses must be caught before their parents

## Expected Output

```
=== Transaction 1: Valid deposit ===
Deposited $200.00. New balance: $700.00
[Audit] Transaction attempt completed for Alice

=== Transaction 2: Invalid deposit amount ===
Transaction failed: Deposit amount must be positive
[Audit] Transaction attempt completed for Alice

=== Transaction 3: Valid withdrawal ===
Withdrew $100.00. New balance: $600.00
[Audit] Transaction attempt completed for Alice

=== Transaction 4: Insufficient funds ===
Transaction failed: Cannot withdraw $1000.00: balance is $600.00
[Audit] Transaction attempt completed for Alice
```
