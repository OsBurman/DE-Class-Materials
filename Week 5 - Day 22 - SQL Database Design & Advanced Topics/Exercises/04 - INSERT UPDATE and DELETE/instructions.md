# Exercise 04: INSERT, UPDATE, and DELETE

## Objective
Practice the full DML cycle: populating tables with `INSERT INTO`, modifying existing data with `UPDATE ... SET ... WHERE`, and removing rows with `DELETE FROM ... WHERE`.

## Background
DDL (Data Definition Language) defines structure; DML (Data Manipulation Language) manages the data inside that structure. The three core DML statements are:

| Statement | Purpose |
|---|---|
| `INSERT INTO` | Add new row(s) |
| `UPDATE` | Modify existing row(s) |
| `DELETE FROM` | Remove existing row(s) |

Always pair `UPDATE` and `DELETE` with a `WHERE` clause — omitting it affects **every** row.

Run `setup.sql` first — it creates and seeds the normalized library schema (members, authors, publishers, books, genres, book_genres, loans).

## Requirements

**Part A — INSERT**

1. Insert a new author: first name `'F. Scott'`, last name `'Fitzgerald'`, email `'fscott@lib.com'`.
2. Insert a new publisher: name `'Scribner'`, city `'New York'`.
3. Insert a new book: title `'The Great Gatsby'`, linked to the author and publisher you just inserted.
4. Insert two new members: `('Maria', 'Garcia', 'mgarcia@email.com', 'Madrid', 'Spain', 'premium')` and `('Liam', 'Chen', 'lchen@email.com', 'Toronto', 'Canada', 'student')`.
5. Insert a loan for `Maria Garcia` borrowing `'The Great Gatsby'` with `loan_date = CURRENT_DATE`.

**Part B — UPDATE**

6. Update `Maria Garcia`'s `membership_type` to `'standard'`.
7. Update the price of `'1984'` to `15.99` (the `books` table has a `price` column from setup).
8. Increase the `stock` of every book published before `2000` by `5`.
9. Update the `return_date` of the loan you inserted in step 5 to `CURRENT_DATE + INTERVAL '14 days'`.

**Part C — DELETE**

10. Delete the loan record for `'Liam Chen'` — but first check that there is one (there isn't one from the seed data, so insert a loan for Liam first, then delete it).
11. Delete all books whose `stock` is `0` — but first confirm with a `SELECT` that any exist. If none exist, update one to `stock = 0`, then delete it.
12. Attempt to delete an author that has associated books. Observe the foreign key error. Then use `ON DELETE` semantics discussion (comment only — no code change required).

## Hints
- `INSERT INTO t (col1, col2) VALUES (val1, val2);`
- Multi-row insert: `INSERT INTO t (col1, col2) VALUES (a, b), (c, d);`
- `UPDATE t SET col = val WHERE condition;`
- `DELETE FROM t WHERE condition;`
- To reference a row you just inserted without knowing the ID, use a subquery: `(SELECT author_id FROM authors WHERE email = 'fscott@lib.com')`
- `CURRENT_DATE + INTERVAL '14 days'` returns a date 14 days from today.

## Expected Output
After Part A:
```sql
SELECT first_name, last_name FROM authors ORDER BY author_id;
-- Should include F. Scott Fitzgerald as the last row.
```

After Part B:
```sql
SELECT title, price FROM books WHERE title = '1984';
-- price | 15.99
```
