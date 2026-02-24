# Exercise 02: ALTER and DROP Statements

## Objective
Practice modifying an existing schema with `ALTER TABLE` (add, modify, rename, and drop columns; add constraints) and removing objects with `DROP TABLE`.

## Background
Schemas evolve over time. New requirements demand new columns; old designs need fixes; deprecated tables must be removed. The `ALTER TABLE` statement lets you make these changes without recreating a table from scratch.

Run `setup.sql` first — it creates the library tables from Exercise 01 and inserts a few rows of seed data so you can observe how structural changes interact with existing data.

## Requirements

**Part A — Add and modify columns**

1. Add a column `biography TEXT` to the `authors` table (nullable, no default).
2. Add a column `available_stock INTEGER NOT NULL DEFAULT 0` to the `books` table.
3. Add a column `phone VARCHAR(20)` to the `members` table.
4. Rename the column `phone` in `members` to `phone_number`.
5. Change the data type of `authors.nationality` from `VARCHAR(50)` to `VARCHAR(80)`.

**Part B — Add constraints after table creation**

6. Add a `UNIQUE` constraint named `uq_books_isbn` on `books(isbn)`.  
   *(Hint: the column already has a constraint from `CREATE TABLE`; use `ALTER TABLE ... DROP CONSTRAINT` on the old one first if you get a conflict, or simply try adding a named one.)*
7. Add a `CHECK` constraint named `chk_members_phone` on `members` that ensures `phone_number` either `IS NULL` or has a length ≥ 7.
8. Add a `NOT NULL` constraint to `books.price`.

**Part C — Drop columns and tables**

9. Drop the column `biography` from the `authors` table.
10. Create a temporary table called `temp_audit_log` with columns `log_id SERIAL PRIMARY KEY`, `action TEXT`, `logged_at TIMESTAMP DEFAULT NOW()`.
11. Drop the `temp_audit_log` table entirely.

## Hints
- `ALTER TABLE t ADD COLUMN col datatype constraint;`
- `ALTER TABLE t DROP COLUMN col;`
- `ALTER TABLE t RENAME COLUMN old TO new;`
- `ALTER TABLE t ALTER COLUMN col TYPE new_type;`
- `ALTER TABLE t ALTER COLUMN col SET NOT NULL;`
- `ALTER TABLE t ADD CONSTRAINT name CHECK (expr);`
- `DROP TABLE tablename;`
- Dropping a column that is referenced by a foreign key requires `CASCADE`.

## Expected Behaviour
After completing all steps, running `\d books` should show `available_stock` and `price NOT NULL` in the column list. Running `\d members` should show `phone_number` (not `phone`). `temp_audit_log` should not exist.
