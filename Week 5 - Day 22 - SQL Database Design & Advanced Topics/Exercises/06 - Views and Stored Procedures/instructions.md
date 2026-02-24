# Exercise 06: Views and Stored Procedures

## Objective
Create database **views** to simplify complex queries and **stored functions/procedures** to encapsulate reusable logic in PostgreSQL's PL/pgSQL language.

## Background

### Views
A **view** is a named, saved query that behaves like a virtual table. Instead of rewriting a complex `SELECT` every time, you query the view.

```sql
CREATE VIEW view_name AS
SELECT ...;

-- Query it like a table:
SELECT * FROM view_name WHERE ...;
```

### Stored Functions (PostgreSQL)
A **function** returns a value (or set of rows) and can be called in a `SELECT`. A **procedure** (`CREATE PROCEDURE`) runs logic without returning a result (called with `CALL`).

```sql
CREATE OR REPLACE FUNCTION function_name(param type)
RETURNS return_type
LANGUAGE plpgsql
AS $$
BEGIN
    -- logic
    RETURN value;
END;
$$;
```

Run `setup.sql` first — it creates and seeds the full library schema.

## Requirements

**Part 1 — Views**

1. Create a view called `v_available_books` that lists books where `stock > 0`. Include: `book_id`, `title`, `author full name` (concatenated), `stock`, `price`.

2. Create a view called `v_active_loans` that shows all loans where `return_date IS NULL`. Include: `loan_id`, `member full name`, `book title`, `loan_date`.

3. Create a view called `v_member_loan_count` that returns each member's `member_id`, full name, and total number of loans (`loan_count`).

4. Query `v_available_books` to list only books costing less than `$13.00`, ordered by price.

5. Use `CREATE OR REPLACE VIEW` to update `v_available_books` to also include the `published_year` column.

6. Drop the view `v_member_loan_count`.

**Part 2 — Stored Functions**

7. Create a function `get_member_loans(p_member_id INTEGER)` that returns a table of (`loan_id INTEGER`, `book_title VARCHAR`, `loan_date DATE`, `return_date DATE`) for the given member.  
   Call it with: `SELECT * FROM get_member_loans(1);`

8. Create a function `count_loans_by_member(p_member_id INTEGER)` that returns a single `INTEGER` — the total number of loans for that member.  
   Call it with: `SELECT count_loans_by_member(2);`

9. Create a function `is_book_available(p_book_id INTEGER)` that returns `BOOLEAN` — `TRUE` if the book's `stock > 0`, otherwise `FALSE`.  
   Test with: `SELECT is_book_available(1);`

**Part 3 — Stored Procedure (DML)**

10. Create a procedure `checkout_book(p_member_id INTEGER, p_book_id INTEGER)` that:
    - Inserts a new row into `loans` with today's date
    - Decrements `books.stock` by 1 for that book
    - Raises an exception if `stock = 0` before the checkout  
    Call with: `CALL checkout_book(1, 2);`

## Hints
- In PL/pgSQL, `RAISE EXCEPTION 'message %', variable;` raises a runtime error.
- `RETURNS TABLE (col1 type1, col2 type2)` lets a function return multiple rows.
- `RETURN QUERY SELECT ...;` populates the returned table.
- Drop a view with `DROP VIEW view_name;`.
