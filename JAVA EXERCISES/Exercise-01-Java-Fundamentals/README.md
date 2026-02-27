# Exercise 01 — Java Fundamentals

## Overview

Build a **Personal Finance Tracker** that runs in the terminal. The user enters their income and a series of expenses, then the program categorizes them, calculates totals, and prints a formatted summary report.

---

## Concepts Covered

- Primitive data types: `int`, `double`, `boolean`, `char`
- Reference type: `String`
- Arithmetic, comparison, and logical operators
- `if / else if / else` statements
- `switch` statements (traditional and enhanced)
- `for`, `while`, and `do-while` loops
- Enhanced `for-each` loop
- `Scanner` for console input
- `String` formatting with `printf` / `String.format`

---

## Application Features

1. Greet the user and ask for their name and monthly income
2. Let the user enter up to 10 expenses (category + amount)
3. Categorize each expense (Food, Rent, Transport, Entertainment, Other)
4. Calculate: total expenses, remaining balance, highest expense, average expense
5. Print a formatted summary report
6. Warn the user if they are over budget (expenses > income)

---

## TODOs

- [ ] **TODO 1** — Declare variables for name (String), income (double), and expense arrays
- [ ] **TODO 2** — Use a `do-while` loop to read expenses until the user types "done"
- [ ] **TODO 3** — Use an enhanced `switch` to assign a category code from the category name
- [ ] **TODO 4** — Use a `for` loop to compute total, max, and average expenses
- [ ] **TODO 5** — Use `if/else` to determine budget status (under/over budget)
- [ ] **TODO 6** — Use `printf` to print the formatted summary report

---

## Running the Program

```bash
cd starter-code/src
javac Main.java
java Main
```

## Sample Output

```
=== Personal Finance Tracker ===
Enter your name: Alice
Enter your monthly income: $3500.00

Enter expenses (type 'done' to finish):
Category (Food/Rent/Transport/Entertainment/Other): Rent
Amount: $1200.00
Category: Food
Amount: $400.00
...

======== MONTHLY SUMMARY ========
Name:            Alice
Monthly Income:  $3,500.00
Total Expenses:  $2,100.00
Remaining:       $1,400.00
Highest Expense: $1,200.00 (Rent)
Average Expense: $525.00
Budget Status:   UNDER BUDGET ✓
=================================
```
