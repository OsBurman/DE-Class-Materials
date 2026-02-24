# Day 21 Part 1 — Database Fundamentals & SQL Basics
## Slide Descriptions

**Total slides: 16**

---

### Reference Schema (used throughout today)

```
customers   (customer_id, first_name, last_name, email, city, country, created_at)
products    (product_id, name, category_id, price, stock_qty)
categories  (category_id, name)
orders      (order_id, customer_id, order_date, status, total_amount)
order_items (item_id, order_id, product_id, quantity, unit_price)
employees   (employee_id, first_name, last_name, department, salary, manager_id)
```

---

### Slide 1 — Title Slide

**Title:** Database Fundamentals & SQL Queries
**Subtitle:** RDBMS Concepts · SELECT · Filtering · Sorting · Pagination
**Day:** Week 5 — Day 21 | Part 1 of 2

**Objectives listed on slide:**
- Understand relational database concepts (tables, rows, columns, keys)
- Read and write SQL `SELECT` statements with confidence
- Filter results with `WHERE`, comparison operators, `LIKE`, `IN`, `BETWEEN`
- Combine conditions with `AND`, `OR`, `NOT`
- Sort results with `ORDER BY`
- Paginate results with `LIMIT` and `OFFSET`
- Eliminate duplicates with `DISTINCT`

---

### Slide 2 — What Is a Relational Database?

**Title:** RDBMS — The Foundation

**Left column — Core concepts:**
> A **Relational Database Management System (RDBMS)** stores data in **tables** (relations) made up of **rows** (records) and **columns** (attributes). Relationships between tables are expressed through keys.

**Key vocabulary table:**

| Term | Meaning |
|---|---|
| **Table** | A grid of related data — like a spreadsheet |
| **Row / Record** | One item — one customer, one order |
| **Column / Field** | One attribute of every row — name, price, date |
| **Primary Key** | Uniquely identifies each row in a table |
| **Foreign Key** | A column that references the primary key of another table |
| **Schema** | The structure definition — tables, columns, types, constraints |

**Right column — Visual table sample (customers):**
```
customer_id │ first_name │ last_name │ city
────────────┼────────────┼───────────┼──────────
1           │ Alice      │ Johnson   │ New York
2           │ Bob        │ Smith     │ London
3           │ Carol      │ Williams  │ Sydney
```

**Bottom callout:**
> RDBMS examples: **PostgreSQL**, **MySQL/MariaDB**, **Oracle Database**, **Microsoft SQL Server**, **SQLite**. SQL syntax is largely portable across all of them — minor dialect differences exist.

---

### Slide 3 — SQL Sub-Languages Overview

**Title:** SQL — Four Sub-Languages

**Overview table:**

| Sub-Language | Stands for | What it does | Examples |
|---|---|---|---|
| **DQL** | Data Query Language | Read data | `SELECT` |
| **DDL** | Data Definition Language | Define structure | `CREATE`, `ALTER`, `DROP` |
| **DML** | Data Manipulation Language | Modify data | `INSERT`, `UPDATE`, `DELETE` |
| **DCL** | Data Control Language | Manage access | `GRANT`, `REVOKE` |

**Callout box:**
> **Today's focus: DQL** — `SELECT` and everything you can do with it.
> DDL and DML are covered in full on Day 22.

**A note on SQL style:**
```sql
-- SQL keywords are traditionally written in UPPERCASE
-- Table and column names in lowercase (or snake_case)
-- Both are valid; consistency within a project is what matters

SELECT first_name, last_name    -- ✅ readable convention
FROM   customers
WHERE  city = 'New York';
```

---

### Slide 4 — Anatomy of a SELECT Statement

**Title:** SELECT — The Full Structure

**Complete query structure (with optional clauses marked):**
```sql
SELECT   [DISTINCT] column1, column2, ...   -- what columns to return
FROM     table_name                          -- which table
[JOIN    other_table ON condition]           -- link related tables (Part 2)
WHERE    condition                           -- filter rows BEFORE grouping
[GROUP BY column]                           -- aggregate rows into groups
[HAVING  condition]                         -- filter groups AFTER grouping
ORDER BY column [ASC | DESC]                -- sort the results
[LIMIT   n]                                 -- max rows to return
[OFFSET  n];                                -- skip first n rows
```

**Execution order callout (important — different from write order):**
```
FROM     → 1. Which table(s)?
WHERE    → 2. Which rows survive?
GROUP BY → 3. Collapse into groups
HAVING   → 4. Which groups survive?
SELECT   → 5. Which columns to show?
ORDER BY → 6. How to sort?
LIMIT    → 7. How many rows?
```

**Bottom note:** You write `SELECT` first but it executes last among the first five. This is why you cannot use a `SELECT` alias in a `WHERE` clause — `WHERE` runs before `SELECT` names things.

---

### Slide 5 — SELECT — Choosing Columns

**Title:** SELECT — Columns, Aliases, and Expressions

**Code block:**
```sql
-- All columns (use sparingly — avoid in production code)
SELECT * FROM customers;

-- Specific columns
SELECT first_name, last_name, email
FROM   customers;

-- Column alias with AS
SELECT first_name AS "First Name",
       last_name  AS "Last Name",
       email      AS "Contact Email"
FROM   customers;

-- Expressions and calculated columns
SELECT first_name,
       last_name,
       price,
       price * 1.08   AS price_with_tax,    -- 8% tax
       price * 0.9    AS discounted_price    -- 10% discount
FROM   products;

-- Concatenation (CONCAT or || depending on dialect)
SELECT CONCAT(first_name, ' ', last_name) AS full_name,
       city,
       country
FROM   customers;
```

**Tip box:**
> Avoid `SELECT *` in application code — it fetches columns you don't need, breaks if columns are added or reordered, and makes queries harder to understand. Always name your columns explicitly.

---

### Slide 6 — WHERE Clause and Comparison Operators

**Title:** WHERE — Filtering Rows

**Comparison operators table:**

| Operator | Meaning | Example |
|---|---|---|
| `=` | Equal | `status = 'active'` |
| `!=` or `<>` | Not equal | `status != 'cancelled'` |
| `<` | Less than | `price < 50` |
| `>` | Greater than | `price > 100` |
| `<=` | Less than or equal | `price <= 99.99` |
| `>=` | Greater than or equal | `stock_qty >= 10` |
| `IS NULL` | Value is NULL | `email IS NULL` |
| `IS NOT NULL` | Value is not NULL | `email IS NOT NULL` |

**Code examples:**
```sql
-- Text comparison — single quotes always, case-sensitive in most databases
SELECT first_name, last_name, city
FROM   customers
WHERE  country = 'USA';

-- Numeric comparison
SELECT name, price
FROM   products
WHERE  price < 25.00;

-- NULL checks — never use = NULL, always IS NULL
SELECT first_name, email
FROM   customers
WHERE  email IS NOT NULL;

-- Date comparison
SELECT order_id, order_date, total_amount
FROM   orders
WHERE  order_date >= '2024-01-01';
```

---

### Slide 7 — Logical Operators: AND, OR, NOT

**Title:** AND · OR · NOT — Combining Conditions

**Code block:**
```sql
-- AND — both conditions must be true
SELECT name, price, stock_qty
FROM   products
WHERE  price < 100
AND    stock_qty > 0;

-- OR — either condition can be true
SELECT first_name, last_name, country
FROM   customers
WHERE  country = 'USA'
OR     country = 'Canada';

-- NOT — inverts the condition
SELECT order_id, status
FROM   orders
WHERE  NOT status = 'cancelled';     -- same as: status != 'cancelled'

-- Combining AND and OR — USE PARENTHESES to make precedence explicit
-- AND binds tighter than OR (like multiplication vs addition)
SELECT * FROM orders
WHERE  status = 'pending'
AND    (total_amount > 500 OR customer_id = 42);

-- Without parentheses this would be misread as:
-- (status = 'pending' AND total_amount > 500) OR customer_id = 42
```

**Precedence callout:**
```
NOT   → highest (evaluated first)
AND   → middle
OR    → lowest (evaluated last)
```

> **Rule:** Whenever you mix `AND` and `OR`, add parentheses. Be explicit — don't rely on operator precedence rules.

---

### Slide 8 — LIKE Operator

**Title:** LIKE — Pattern Matching

**Wildcard reference:**

| Wildcard | Matches | Example |
|---|---|---|
| `%` | Zero or more characters | `'A%'` → Alice, Anderson, A |
| `_` | Exactly one character | `'A_ice'` → Alice (not Alce) |

**Code block:**
```sql
-- Starts with 'Al'
SELECT first_name, last_name
FROM   customers
WHERE  first_name LIKE 'Al%';
-- Returns: Alice, Albert, Alex, Alicia, ...

-- Ends with '.com'
SELECT email FROM customers
WHERE  email LIKE '%.com';

-- Contains 'phone' anywhere in the name
SELECT name FROM products
WHERE  name LIKE '%phone%';

-- Exactly 4 characters
SELECT name FROM products
WHERE  name LIKE '____';

-- Second character is 'o'
SELECT first_name FROM customers
WHERE  first_name LIKE '_o%';
-- Returns: Bob, Cody, Robert, ...

-- NOT LIKE — exclude matches
SELECT first_name, email
FROM   customers
WHERE  email NOT LIKE '%@gmail.com';
```

**Performance note:**
> `LIKE '%search%'` (leading wildcard) cannot use an index — it must scan every row. `LIKE 'search%'` (no leading wildcard) can use an index. Keep this in mind for large tables.

---

### Slide 9 — IN and BETWEEN Operators

**Title:** IN · BETWEEN — Cleaner Alternatives to Multiple ORs

**IN operator:**
```sql
-- Instead of this verbose OR chain:
WHERE country = 'USA' OR country = 'Canada' OR country = 'Mexico'

-- Use IN:
SELECT first_name, last_name, country
FROM   customers
WHERE  country IN ('USA', 'Canada', 'Mexico');

-- NOT IN — exclude a list
SELECT name, price FROM products
WHERE  category_id NOT IN (3, 7, 12);

-- IN with a subquery (preview — covered in detail in Part 2)
SELECT first_name, last_name
FROM   customers
WHERE  customer_id IN (SELECT customer_id FROM orders WHERE status = 'pending');
```

**BETWEEN operator:**
```sql
-- Numeric range — INCLUSIVE on both ends
SELECT name, price FROM products
WHERE  price BETWEEN 10.00 AND 50.00;
-- Equivalent to: price >= 10.00 AND price <= 50.00

-- Date range
SELECT order_id, order_date, total_amount
FROM   orders
WHERE  order_date BETWEEN '2024-01-01' AND '2024-03-31';

-- NOT BETWEEN
SELECT name, salary FROM employees
WHERE  salary NOT BETWEEN 50000 AND 80000;
```

**Callout:**
> `BETWEEN` is always **inclusive** on both ends. `BETWEEN 10 AND 50` includes both 10 and 50.

---

### Slide 10 — ORDER BY — Sorting Results

**Title:** ORDER BY — Controlling Sort Order

**Code block:**
```sql
-- Single column, ascending (default)
SELECT first_name, last_name, city
FROM   customers
ORDER BY last_name;               -- ASC is the default

-- Single column, descending
SELECT name, price FROM products
ORDER BY price DESC;              -- most expensive first

-- Multiple columns — primary sort, then tiebreaker
SELECT first_name, last_name, city, country
FROM   customers
ORDER BY country ASC, last_name ASC;   -- sort by country, then last name within each country

-- Mix of directions
SELECT name, price, stock_qty
FROM   products
ORDER BY price DESC, name ASC;    -- highest price first, alphabetical for same price

-- Sort by column position (use column alias or number — less readable)
SELECT first_name, last_name, price * 0.9 AS sale_price
FROM   products
ORDER BY sale_price DESC;         -- sort by the alias

-- NULL values: NULLs sort LAST by default in most databases with ASC
-- NULLS FIRST or NULLS LAST (PostgreSQL/Oracle)
ORDER BY email NULLS LAST;
```

**Best practice:**
> Always use `ORDER BY` when the order of results matters to your application. Without it, the database is free to return rows in any order — and that order can change between queries.

---

### Slide 11 — LIMIT and OFFSET — Pagination

**Title:** LIMIT · OFFSET — Getting Pages of Results

**Code block:**
```sql
-- Return only the first 10 rows
SELECT name, price FROM products
ORDER BY price DESC
LIMIT 10;

-- Skip the first 10, return the next 10 (page 2)
SELECT name, price FROM products
ORDER BY price DESC
LIMIT  10
OFFSET 10;

-- Page 3: skip 20, return 10
SELECT name, price FROM products
ORDER BY price DESC
LIMIT  10
OFFSET 20;

-- General formula for page-based pagination:
-- OFFSET = (page_number - 1) * page_size

-- TOP n syntax in SQL Server (different dialect)
-- SELECT TOP 10 name, price FROM products ORDER BY price DESC;

-- ROWNUM in Oracle (different dialect)
-- SELECT name FROM products WHERE ROWNUM <= 10;
```

**Pagination pattern:**
```
Page 1: LIMIT 10 OFFSET 0    (rows 1–10)
Page 2: LIMIT 10 OFFSET 10   (rows 11–20)
Page 3: LIMIT 10 OFFSET 20   (rows 21–30)
Page N: LIMIT 10 OFFSET (N-1)*10
```

**Performance callout:**
> `LIMIT`/`OFFSET` is easy to use but can be slow on very large tables because the database must scan and skip all the offset rows. For large datasets, consider keyset/cursor pagination: `WHERE id > last_seen_id ORDER BY id LIMIT 10`.

---

### Slide 12 — DISTINCT Keyword

**Title:** DISTINCT — Removing Duplicate Rows

**Code block:**
```sql
-- Without DISTINCT — returns every row including duplicates
SELECT country FROM customers;
-- USA, USA, UK, UK, UK, Canada, USA, Germany, ...

-- With DISTINCT — returns each unique value once
SELECT DISTINCT country FROM customers;
-- USA, UK, Canada, Germany, Australia, ...

-- DISTINCT on multiple columns — unique COMBINATION of all listed columns
SELECT DISTINCT city, country
FROM   customers
ORDER BY country, city;
-- Returns unique (city, country) pairs

-- Practical use cases
SELECT DISTINCT status FROM orders;            -- what statuses exist?
SELECT DISTINCT department FROM employees;     -- what departments exist?
SELECT DISTINCT category_id FROM products;     -- which categories have products?

-- DISTINCT with COUNT (counts unique values)
SELECT COUNT(DISTINCT country) AS countries_served
FROM   customers;
```

**When to use DISTINCT:**
- Exploring what values exist in a column ("what statuses are there?")
- De-duplicating results when joins produce repeated rows
- Counting unique values

**When NOT to use it reflexively:**
> If you find yourself adding `DISTINCT` to fix unexpected duplicates in a `JOIN`, the real fix is usually to examine your join conditions — there may be a data model issue or a missing join condition.

---

### Slide 13 — NULL Handling

**Title:** NULL — The Missing Value

**Key behaviors:**
```sql
-- NULL is not a value — it means "unknown" or "absent"
-- NULL != NULL  — comparing NULL to anything always returns UNKNOWN (not true/false)

-- ❌ WRONG — this never returns rows, not even NULLs
SELECT * FROM customers WHERE email = NULL;

-- ✅ CORRECT
SELECT * FROM customers WHERE email IS NULL;
SELECT * FROM customers WHERE email IS NOT NULL;

-- NULL in arithmetic — any expression involving NULL = NULL
SELECT 100 + NULL;         -- NULL (not 100)
SELECT name, price * NULL  -- NULL for every row
FROM products;

-- COALESCE — return first non-NULL value
SELECT first_name,
       COALESCE(email, 'no-email@unknown.com') AS contact_email
FROM   customers;

-- NULLIF — return NULL if two values are equal (avoid division by zero)
SELECT total_sales,
       total_sales / NULLIF(num_orders, 0) AS avg_order_value
FROM   monthly_stats;

-- NULL in ORDER BY — NULLs last by default in most databases
SELECT first_name, email FROM customers ORDER BY email;
```

**Conceptual note:**
> Think of NULL as "I don't know." Is `NULL > 5`? Unknown. Is `NULL = NULL`? Unknown. Three-valued logic: TRUE, FALSE, UNKNOWN.

---

### Slide 14 — Putting It Together — Multi-Condition Queries

**Title:** Combining Everything — Real-World Query Examples

**Example 1 — Product search with multiple filters:**
```sql
-- Products that are in stock, priced $10–$100, name contains 'pro'
SELECT name, price, stock_qty
FROM   products
WHERE  stock_qty > 0
AND    price BETWEEN 10.00 AND 100.00
AND    name LIKE '%pro%'
ORDER BY price ASC
LIMIT  20;
```

**Example 2 — Customer search with OR logic and pagination:**
```sql
-- US or UK customers whose email is confirmed (not null), alphabetical, page 2
SELECT first_name, last_name, email, country
FROM   customers
WHERE  country IN ('USA', 'UK')
AND    email IS NOT NULL
ORDER BY last_name ASC, first_name ASC
LIMIT  25
OFFSET 25;
```

**Example 3 — Orders report with date range:**
```sql
-- All non-cancelled orders in Q1 2024 worth over $200
SELECT order_id, order_date, status, total_amount
FROM   orders
WHERE  order_date BETWEEN '2024-01-01' AND '2024-03-31'
AND    status != 'cancelled'
AND    total_amount > 200.00
ORDER BY order_date DESC;
```

---

### Slide 15 — SQL Formatting Best Practices

**Title:** Writing Readable SQL

**Code block — before and after:**
```sql
-- ❌ Hard to read
select p.name,p.price,c.name from products p join categories c on p.category_id=c.category_id where p.price>50 and p.stock_qty>0 order by p.price desc limit 10

-- ✅ Professional formatting
SELECT   p.name        AS product_name,
         p.price,
         c.name        AS category
FROM     products      p
JOIN     categories    c  ON p.category_id = c.category_id
WHERE    p.price     > 50
AND      p.stock_qty > 0
ORDER BY p.price DESC
LIMIT    10;
```

**Style guidelines table:**

| Rule | Why |
|---|---|
| Keywords uppercase | Visual separation from identifiers |
| One clause per line | Easy to comment out a clause |
| Indent continuation lines | Shows which clause a line belongs to |
| Column aliases with `AS` | Explicit is better than implicit |
| End with `;` | Required when running multiple statements; good habit |
| Table aliases (short) | Reduces repetition in multi-table queries |
| Comments with `--` or `/* */` | Explain non-obvious logic |

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Recap — SQL Fundamentals & DQL

**Full syntax reference:**
```sql
SELECT [DISTINCT] col1 [AS alias], col2, expression AS alias
FROM   table_name
WHERE  condition1
AND    condition2
OR     condition3
ORDER BY col1 [ASC | DESC], col2 [ASC | DESC]
LIMIT  n
OFFSET n;
```

**Operator cheat sheet:**

| Operator | Example |
|---|---|
| `=  !=  <  >  <=  >=` | `price >= 10` |
| `IS NULL` / `IS NOT NULL` | `email IS NOT NULL` |
| `AND` · `OR` · `NOT` | `x = 1 AND y = 2` |
| `LIKE '%pattern%'` | `name LIKE '%phone%'` |
| `IN (list)` | `country IN ('USA','UK')` |
| `BETWEEN a AND b` | `price BETWEEN 10 AND 50` |
| `COALESCE(a, b)` | `COALESCE(email, 'none')` |
| `DISTINCT` | `SELECT DISTINCT city` |

**Up next — Part 2:**
- Aggregate functions: `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`
- `GROUP BY` and `HAVING`
- `JOIN` types: `INNER`, `LEFT`, `RIGHT`, `FULL OUTER`, `CROSS`, Self
- Subqueries and correlated subqueries
- `EXISTS` / `NOT EXISTS`
- `CASE` statements and calculated columns
