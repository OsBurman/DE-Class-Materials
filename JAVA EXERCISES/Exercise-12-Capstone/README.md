# Exercise 12 — Capstone: Bank Management System

## Overview
Put **everything you've learned together** in a fully-featured Bank Management System. This application combines all major Java concepts into one realistic codebase.

## Concepts Covered (All-in-One)
| Concept | Where Used |
|---------|-----------|
| OOP / Inheritance | `Account` → `SavingsAccount`, `CheckingAccount` |
| Interfaces | `Transactable`, `Reportable` |
| Collections | `Map<String, Account>`, `List<Transaction>` |
| Generics | `TransactionResult<T>` |
| Custom Exceptions | `InsufficientFundsException`, `AccountNotFoundException` |
| File I/O | Persist transactions to CSV, load on startup |
| Lambdas & Streams | Analytics, filtering, sorting |
| Design Patterns | Singleton (Bank), Builder (Account creation) |
| Exception Handling | Try/catch/finally throughout |

## Setup
```bash
cd Exercise-12-Capstone/starter-code/src
javac *.java
java Main
```

## Files

| File | Description |
|------|-------------|
| `Account.java` | Abstract base class |
| `SavingsAccount.java` | Earns interest, limited withdrawals |
| `CheckingAccount.java` | Has overdraft limit |
| `Transaction.java` | Immutable record of one transaction |
| `Bank.java` | Singleton — manages all accounts |
| `BankException.java` | Custom checked exceptions |
| `Analytics.java` | Stream-based reporting |
| `Main.java` | Full demo driver |

## Your TODOs

### Account (abstract)
- Fields: `accountNumber`, `ownerName`, `balance`
- Abstract methods: `withdraw(double)`, `getAccountType()`
- Concrete: `deposit(double)` (validates > 0), `getBalance()`, `toString()`

### SavingsAccount extends Account
- Extra field: `interestRate` (e.g. 0.03 = 3%)
- `withdraw` — throw `InsufficientFundsException` if balance insufficient
- `applyInterest()` — balance += balance * interestRate

### CheckingAccount extends Account
- Extra field: `overdraftLimit`
- `withdraw` — allow up to balance + overdraftLimit (overdraft allowed)

### Bank (Singleton)
- `Map<String, Account>` to store accounts
- `createAccount(String ownerName, String type, double initialDeposit)` → returns new account
- `getAccount(String accountNumber)` → throws `AccountNotFoundException`
- `transfer(String fromAcc, String toAcc, double amount)` → uses existing withdraw/deposit
- `getAllAccounts()` → `Collection<Account>`

### Analytics (Streams)
- `totalDeposited(List<Transaction>)` → sum of DEPOSIT transactions
- `topSpenders(List<Transaction>, int n)` → top N accounts by withdrawal amount
- `transactionsByType(List<Transaction>)` → `Map<String, Long>` count by type
- `getAccountReport(Collection<Account>)` → sorted formatted string

## Expected Output
```
=== Bank Management System ===
Created: SAV-001 (Alice) | CHK-002 (Bob) | SAV-003 (Carol)
Alice balance: $5,150.00 (after 3% interest)
Bob overdraft withdrawal: $1,200.00 on $1,000.00 balance ✓
Transfer $500 Alice→Carol: ✓
--- Analytics ---
Total deposited: $16,000.00
Transactions by type: {DEPOSIT=3, WITHDRAW=2, TRANSFER=2}
Top spender: Bob
--- Account Report ---
CHK-002  Bob        Checking  $  -200.00
SAV-001  Alice      Savings   $ 4,650.00
SAV-003  Carol      Savings   $ 6,500.00
```
