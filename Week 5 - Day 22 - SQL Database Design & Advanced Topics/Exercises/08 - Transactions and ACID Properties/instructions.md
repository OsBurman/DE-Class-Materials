# Exercise 08: Transactions and ACID Properties

## Objective
Use explicit transaction control (`BEGIN`, `COMMIT`, `ROLLBACK`, `SAVEPOINT`) to group multiple SQL statements into atomic units of work and understand the four ACID properties.

## Background

### ACID Properties
| Property | Meaning |
|---|---|
| **Atomicity** | All operations in a transaction succeed, or none do. |
| **Consistency** | A transaction brings the database from one valid state to another — all constraints remain satisfied. |
| **Isolation** | Concurrent transactions behave as if they were executed serially. |
| **Durability** | Once committed, a transaction's changes survive system failures. |

### Transaction Control
```sql
BEGIN;                     -- start a transaction block
  UPDATE ...;
  INSERT ...;
COMMIT;                    -- persist all changes

-- or if something goes wrong:
ROLLBACK;                  -- undo all changes since BEGIN

SAVEPOINT sp_name;         -- create a partial rollback point
ROLLBACK TO SAVEPOINT sp_name;   -- undo to savepoint, keep the transaction open
RELEASE SAVEPOINT sp_name;       -- discard the savepoint (keep changes)
```

Run `setup.sql` first.

## Requirements

**Part 1 — Basic Commit and Rollback**

1. Start a transaction. Insert a new member (`'Tom', 'Brady', 'tbrady@email.com', 'Boston', 'USA', 'premium'`). Commit. Verify the member exists.

2. Start a transaction. Update member Tom Brady's `membership_type` to `'student'`. Then `ROLLBACK`. Verify the update did NOT persist.

3. Start a transaction. Delete member Tom Brady. Then `ROLLBACK`. Verify he still exists.

**Part 2 — Multi-statement Atomic Transaction**

4. Implement a complete **book checkout** as a single transaction:
   - Insert a loan record (member 1 borrows book 2, today).
   - Decrease `books.stock` by 1 for book 2.
   - Commit.
   - Verify: loan exists AND stock decreased.

5. Simulate a **failed checkout** (stock goes negative):
   - Begin a transaction.
   - Insert a loan for book 7 (stock = 0).
   - Attempt to update `stock = stock - 1` for book 7 (this will violate the `CHECK (stock >= 0)` constraint).
   - Catch the error: in PostgreSQL this automatically aborts the transaction. Execute `ROLLBACK`.
   - Verify book 7 still has stock = 0 and no new loan row exists for book 7.

**Part 3 — Savepoints**

6. Start a transaction. Insert a new genre `'Biography'`. Set a savepoint `sp_genre`.  
   Insert a second genre `'Self-Help'`. Set a savepoint `sp_genre2`.  
   Try to insert a duplicate genre `'Biography'` (should fail and abort the transaction sub-block).  
   `ROLLBACK TO SAVEPOINT sp_genre2`. Insert a valid genre `'Science'` instead.  
   Commit. Verify all three valid genres exist: `'Biography'`, `'Self-Help'`, `'Science'`.

7. Start a transaction. Insert a member. `SAVEPOINT sp_member`. Insert a loan for that member. `ROLLBACK TO SAVEPOINT sp_member`. Commit. Verify: member exists, loan does NOT exist.

**Part 4 — Isolation Levels (theory + demonstration)**

8. In a comment block, explain what **dirty reads**, **non-repeatable reads**, and **phantom reads** are, and which PostgreSQL isolation level prevents each.

9. Write the command to set the transaction isolation level to `SERIALIZABLE` and show a transaction block that transfers stock from book 1 to book 2 (take 2 from book 1, add 2 to book 2) using this isolation level.

## Hints
- In PostgreSQL, once a statement within an open transaction fails (e.g., a CHECK constraint), the entire transaction is in an aborted state. You must `ROLLBACK` before issuing new commands.
- `BEGIN; ... SAVEPOINT sp; ... ROLLBACK TO SAVEPOINT sp; ... COMMIT;` — the transaction is still open after rolling back to a savepoint.
- Isolation level: `BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;`
