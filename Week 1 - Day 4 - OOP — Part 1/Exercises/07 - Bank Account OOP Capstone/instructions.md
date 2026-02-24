# Exercise 07: OOP Capstone — Bank Account

## Objective
Combine classes and objects, constructors (with chaining), access modifiers, static members, `final`, and `this` in a single realistic model of a bank account system.

---

## Background
You are modelling a simplified bank account system. Each account has a unique account number assigned automatically, an owner name, and a balance. Deposit and withdrawal operations must validate their inputs. A static counter tracks total accounts opened.

---

## Requirements

### Part 1 — The `BankAccount` class
1. **Constants & static members**:
   - `public static final double MINIMUM_BALANCE = 0.0`
   - `private static int nextAccountNumber = 1000` (auto-incremented)
   - `public static int getAccountCount()` — returns how many accounts exist

2. **Instance fields** (all `private`):
   - `private final int accountNumber` — assigned from `nextAccountNumber` in the constructor, then `nextAccountNumber` is incremented
   - `private String ownerName`
   - `private double balance`

3. **Constructors**:
   - `BankAccount(String ownerName)` — creates an account with `balance = 0.0`. Chain to the two-arg constructor using `this(ownerName, 0.0)`.
   - `BankAccount(String ownerName, double initialBalance)` — sets all fields; assigns and increments `nextAccountNumber`.

4. **Methods**:
   - `void deposit(double amount)` — if `amount <= 0`, print `"Deposit amount must be positive."` and return. Otherwise add to balance and print `"Deposited $[amount]. New balance: $[balance]"`.
   - `void withdraw(double amount)` — if `amount <= 0`, print `"Withdrawal amount must be positive."` and return. If `balance - amount < MINIMUM_BALANCE`, print `"Insufficient funds."` and return. Otherwise subtract and print `"Withdrew $[amount]. New balance: $[balance]"`.
   - `double getBalance()`, `String getOwnerName()`, `int getAccountNumber()`
   - `void setOwnerName(String ownerName)` — uses `this.ownerName = ownerName`
   - `toString()` returns `"Account #[number] | Owner: [name] | Balance: $[balance]"`

### Part 2 — `main` method (in `BankAccountDemo`)
1. Print `"Accounts open: " + BankAccount.getAccountCount()` (expect 0).
2. Create `acc1 = new BankAccount("Diana")` and `acc2 = new BankAccount("Ethan", 500.0)`.
3. Print count (expect 2), then print both accounts.
4. Perform on `acc1`: deposit 200, deposit 150, withdraw 100.
5. Perform on `acc2`: withdraw 600 (insufficient), withdraw 200.
6. Try an invalid deposit on `acc1`: `deposit(-50)`.
7. Print both accounts in their final state.
8. Print `"Minimum balance: $" + BankAccount.MINIMUM_BALANCE`.

---

## Hints
- The one-arg constructor should call `this(ownerName, 0.0)` — the two-arg constructor does all the real work.
- `accountNumber` is `final` — assign it as `this.accountNumber = nextAccountNumber++` in one line.
- Format currency with `String.format("$%.2f", balance)` or use plain concatenation — either is fine.
- The `withdraw` guard checks the *result* of the subtraction: `if (balance - amount < MINIMUM_BALANCE)`.

---

## Expected Output
```
Accounts open: 0
Accounts open: 2
Account #1000 | Owner: Diana | Balance: $0.0
Account #1001 | Owner: Ethan | Balance: $500.0
Deposited $200.0. New balance: $200.0
Deposited $150.0. New balance: $350.0
Withdrew $100.0. New balance: $250.0
Insufficient funds.
Withdrew $200.0. New balance: $300.0
Deposit amount must be positive.
Account #1000 | Owner: Diana | Balance: $250.0
Account #1001 | Owner: Ethan | Balance: $300.0
Minimum balance: $0.0
```
