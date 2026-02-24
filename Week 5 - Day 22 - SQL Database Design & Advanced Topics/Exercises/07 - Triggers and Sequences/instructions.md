# Exercise 07: Triggers and Sequences

## Objective
Create custom **sequences** for controlled ID generation and **triggers** that automatically fire database logic in response to `INSERT`, `UPDATE`, or `DELETE` events.

## Background

### Sequences
A **sequence** is a database object that generates unique numeric values. PostgreSQL's `SERIAL` type uses a sequence internally, but you can also create explicit sequences for fine-grained control.

```sql
CREATE SEQUENCE seq_name
    START WITH 1000
    INCREMENT BY 1
    MINVALUE 1000
    NO MAXVALUE;

-- Use it:
SELECT nextval('seq_name');   -- advance & return next value
SELECT currval('seq_name');   -- return current value (within session)
```

### Triggers
A **trigger** automatically calls a **trigger function** before or after a data event on a table.

```sql
-- Step 1: Create the trigger function (must return TRIGGER)
CREATE OR REPLACE FUNCTION trg_fn_name()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    -- NEW contains the new row (for INSERT/UPDATE)
    -- OLD contains the old row (for UPDATE/DELETE)
    RETURN NEW;   -- must return NEW for BEFORE triggers on INSERT/UPDATE
END;
$$;

-- Step 2: Create the trigger
CREATE TRIGGER trg_name
BEFORE INSERT ON table_name
FOR EACH ROW EXECUTE FUNCTION trg_fn_name();
```

Run `setup.sql` first.

## Requirements

**Part 1 — Sequences**

1. Create a sequence called `seq_loan_ref` that starts at `10000`, increments by `1`, and has no maximum value.
2. Insert a new loan using `nextval('seq_loan_ref')` as the `loan_id` (override the default). Use member 1, book 2, today's date.
3. Insert a second loan using `nextval('seq_loan_ref')`. Verify both IDs are sequential (10000, 10001).
4. Query `currval('seq_loan_ref')` to confirm the current value.
5. Create a sequence `seq_member_code` starting at `5000`, incrementing by `10` (so codes are 5000, 5010, 5020...). Retrieve the next three values.

**Part 2 — Audit trigger (AFTER INSERT)**

6. Create an `audit_log` table with columns: `log_id SERIAL PRIMARY KEY`, `table_name VARCHAR(50)`, `action VARCHAR(10)`, `record_id INTEGER`, `changed_at TIMESTAMP DEFAULT NOW()`.
7. Create a trigger function `trg_fn_log_loan_insert()` that inserts a row into `audit_log` after each new loan is inserted. Use: `table_name = 'loans'`, `action = 'INSERT'`, `record_id = NEW.loan_id`.
8. Create an `AFTER INSERT` trigger `trg_log_loan_insert` on the `loans` table.
9. Insert a new loan (member 2, book 3, today) and verify a row appears in `audit_log`.

**Part 3 — Business-rule trigger (BEFORE INSERT)**

10. Create a trigger function `trg_fn_check_stock()` that fires **before** a loan is inserted and raises an exception if `books.stock = 0` for the book being borrowed.
11. Attach it as a `BEFORE INSERT` trigger `trg_check_stock` on `loans`.
12. Test it: attempt to insert a loan for book 7 (stock = 0) and observe the error. Then insert one for book 1 (stock > 0) to confirm success.

**Part 4 — Update trigger (AFTER UPDATE)**

13. Create a trigger `trg_log_loan_return` that fires **after** a loan row is updated when `return_date` changes from `NULL` to a non-NULL value. It should log `action = 'RETURN'` to `audit_log`.
14. Update a loan to set `return_date = CURRENT_DATE`. Confirm the audit log entry.

## Hints
- `NEW` and `OLD` are special records available inside trigger functions.
- For `BEFORE` triggers on `INSERT`, always `RETURN NEW` (or `RETURN NULL` to cancel the operation).
- For `AFTER` triggers, the return value is ignored — use `RETURN NULL`.
- A trigger on `UPDATE` fires for every updated row; use `IF OLD.return_date IS NULL AND NEW.return_date IS NOT NULL THEN` to be selective.
