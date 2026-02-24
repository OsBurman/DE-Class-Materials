# Exercise 01: CREATE TABLE with Data Types and Constraints

## Objective
Practice writing `CREATE TABLE` statements that use appropriate SQL data types and enforce data integrity with all six constraint types.

## Background
You are designing the database for a small library management system. Before any data can be stored, you must define the schema — the tables, their columns, the type of data each column holds, and the rules that keep that data valid. Run your statements in PostgreSQL (or any standard SQL database).

## Requirements
Write `CREATE TABLE` statements for the following four tables. Each table must use the data types and constraints specified.

1. **`authors` table** — columns:
   - `author_id`: auto-incrementing integer, primary key
   - `first_name`: variable-length string up to 50 chars, NOT NULL
   - `last_name`: variable-length string up to 50 chars, NOT NULL
   - `email`: variable-length string up to 100 chars, UNIQUE, NOT NULL
   - `birth_year`: integer, CHECK that value is between 1800 and 2010
   - `nationality`: variable-length string up to 50 chars, DEFAULT `'Unknown'`

2. **`genres` table** — columns:
   - `genre_id`: auto-incrementing integer, primary key
   - `genre_name`: variable-length string up to 50 chars, UNIQUE, NOT NULL

3. **`books` table** — columns:
   - `book_id`: auto-incrementing integer, primary key
   - `title`: variable-length string up to 200 chars, NOT NULL
   - `isbn`: fixed-length string of 13 chars, UNIQUE, NOT NULL
   - `author_id`: integer, FOREIGN KEY referencing `authors(author_id)`, NOT NULL
   - `genre_id`: integer, FOREIGN KEY referencing `genres(genre_id)`
   - `published_year`: integer, CHECK between 1000 and 2100
   - `price`: numeric with 10 digits total and 2 decimal places, CHECK > 0
   - `stock`: integer, DEFAULT 0, CHECK >= 0

4. **`members` table** — columns:
   - `member_id`: auto-incrementing integer, primary key
   - `username`: variable-length string up to 30 chars, UNIQUE, NOT NULL
   - `email`: variable-length string up to 100 chars, UNIQUE, NOT NULL
   - `joined_date`: date, DEFAULT the current date (`CURRENT_DATE`)
   - `membership_type`: variable-length string up to 20 chars, CHECK value IN `('standard', 'premium', 'student')`, DEFAULT `'standard'`

5. After creating all four tables, write a query using `information_schema.columns` or `\d tablename` to confirm the `books` table was created with the expected columns and constraints.

## Hints
- Use `SERIAL` (PostgreSQL) or `INT AUTO_INCREMENT` (MySQL) for auto-incrementing PKs.
- `CHAR(13)` stores a fixed-length string — ideal for ISBN where length is always 13.
- `NUMERIC(10,2)` means up to 10 digits total, 2 after the decimal point.
- `CHECK (col IN ('a','b','c'))` restricts the allowed values to a specific set.

## Expected Output
After running all statements, querying the table list should show:
```
table_name
-----------
authors
books
genres
members
```

Attempting to insert an invalid birth year should fail:
```sql
INSERT INTO authors (first_name, last_name, email, birth_year)
VALUES ('Test', 'User', 'test@test.com', 1750);
-- ERROR: new row violates check constraint "authors_birth_year_check"
```

Attempting to insert a duplicate email should fail:
```sql
-- ERROR: duplicate key value violates unique constraint "authors_email_key"
```
