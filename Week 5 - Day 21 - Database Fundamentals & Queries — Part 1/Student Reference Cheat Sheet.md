# Day 21 — Database Fundamentals & SQL Queries — Review Guide

**Week 5 — Day 21 | Covers:** RDBMS concepts · SELECT · WHERE · Logical operators · LIKE · IN · BETWEEN · ORDER BY · LIMIT · DISTINCT · NULL handling · Aggregates · GROUP BY · HAVING · JOINs (all types) · Subqueries · Correlated subqueries · EXISTS · CASE · Calculated columns

---

## Reference Schema

```sql
customers   (customer_id, first_name, last_name, email, city, country, created_at)
products    (product_id, name, category_id, price, stock_qty)
categories  (category_id, name)
orders      (order_id, customer_id, order_date, status, total_amount)
order_items (item_id, order_id, product_id, quantity, unit_price)
employees   (employee_id, first_name, last_name, department, salary, manager_id)
```

---

## Section 1 — RDBMS Concepts

A **Relational Database Management System** stores data in tables with defined relationships.

| Term | Definition |
|---|---|
| **Table** | A collection of related data in rows and columns |
| **Row / Record** | A single data entry in a table |
| **Column / Field** | A named attribute with a defined data type |
| **Primary Key (PK)** | Uniquely identifies each row; cannot be NULL or duplicate |
| **Foreign Key (FK)** | A column that references the PK of another table |
| **Schema** | The structure definition of all tables, types, and relationships |

Popular RDBMS systems: **PostgreSQL** (open-source, feature-rich), **MySQL / MariaDB** (web-app staple), **Oracle** (enterprise), **SQL Server** (Microsoft ecosystem), **SQLite** (embedded, file-based).

---

## Section 2 — SQL Sub-Languages

| Sub-language | Purpose | Key Commands |
|---|---|---|
| **DQL** — Data Query Language | Read data | `SELECT` |
| **DDL** — Data Definition Language | Define structure | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` |
| **DML** — Data Manipulation Language | Modify data | `INSERT`, `UPDATE`, `DELETE` |
| **DCL** — Data Control Language | Permissions | `GRANT`, `REVOKE` |

> Day 21 focuses entirely on **DQL**. DDL and DML are covered in Day 22.

---

## Section 3 — SELECT Structure & Execution Order

**Full SELECT clause order (how you write it):**
```sql
SELECT   [DISTINCT] columns
FROM     table
[JOIN    other_table ON condition]
WHERE    row_filter_condition
GROUP BY grouping_columns
HAVING   group_filter_condition
ORDER BY sort_columns [ASC | DESC]
LIMIT    n [OFFSET m];
```

**Logical execution order (how the database processes it):**
```
1. FROM / JOIN    → identify source tables and combine them
2. WHERE          → filter individual rows
3. GROUP BY       → collapse rows into groups
4. HAVING         → filter groups
5. SELECT         → choose columns and compute expressions
6. DISTINCT       → remove duplicate rows
7. ORDER BY       → sort the result set
8. LIMIT / OFFSET → trim the result to a page
```

> **Why it matters:** You cannot use a `SELECT` alias in a `WHERE` clause because WHERE runs before SELECT. You cannot filter on aggregate functions in WHERE — use HAVING instead.

---

## Section 4 — Comparison Operators

```sql
=         -- equal
!=  or <>  -- not equal
<         -- less than
>         -- greater than
<=        -- less than or equal
>=        -- greater than or equal
IS NULL   -- checks for null value (NOT: = NULL)
IS NOT NULL -- checks for non-null value
```

```sql
-- Correct
WHERE status = 'active'
WHERE salary > 50000
WHERE manager_id IS NULL

-- WRONG — never works for NULL
WHERE manager_id = NULL
```

---

## Section 5 — Logical Operators: AND, OR, NOT

```sql
-- AND: both conditions must be true
WHERE country = 'USA' AND status = 'active'

-- OR: at least one condition must be true
WHERE status = 'shipped' OR status = 'completed'

-- NOT: inverts a condition
WHERE NOT (status = 'cancelled')
WHERE country NOT IN ('USA', 'Canada')
```

**Precedence order:** `NOT` → `AND` → `OR`

```sql
-- This means: (A AND B) OR C — probably NOT what you want
WHERE city = 'New York' AND status = 'active' OR country = 'Canada'

-- Use parentheses to make intent explicit:
WHERE (city = 'New York' AND status = 'active') OR country = 'Canada'
```

---

## Section 6 — LIKE — Pattern Matching

| Pattern | Matches |
|---|---|
| `'A%'` | Anything starting with A |
| `'%son'` | Anything ending with son |
| `'%oak%'` | Anything containing oak |
| `'_at'` | Any three-character value ending in at (cat, bat, hat) |
| `'J_n'` | Three characters: J, any one char, n (Jan, Jon, Jin) |

```sql
WHERE last_name LIKE 'Sm%'          -- starts with Sm
WHERE email LIKE '%@gmail.com'      -- ends with @gmail.com
WHERE name LIKE '%pro%'             -- contains pro (case-insensitive in MySQL)
WHERE phone LIKE '___-___-____'     -- matches NNN-NNN-NNNN format
WHERE name NOT LIKE '%[Tt]est%'     -- does not contain Test or test
```

> **Performance:** A leading wildcard (`'%something'`) cannot use an index — full table scan. Trailing wildcard (`'something%'`) uses an index normally. For full-text search at scale, use a dedicated search engine.

---

## Section 7 — IN and BETWEEN

**IN — match any value in a list:**
```sql
WHERE status IN ('shipped', 'completed', 'delivered')
WHERE country IN ('USA', 'Canada', 'Mexico')
WHERE product_id NOT IN (1, 2, 3)

-- IN with a subquery (covered in Section 18)
WHERE customer_id IN (SELECT customer_id FROM orders WHERE status = 'vip')
```

> **NOT IN with NULL gotcha:** If the list contains any NULL value, `NOT IN` returns zero rows. This commonly happens with subqueries. Fix: add `WHERE col IS NOT NULL` inside the subquery, or use `NOT EXISTS` instead.

**BETWEEN — inclusive range:**
```sql
WHERE price BETWEEN 10.00 AND 50.00      -- includes 10.00 and 50.00
WHERE order_date BETWEEN '2024-01-01' AND '2024-12-31'
WHERE salary NOT BETWEEN 50000 AND 80000
```

> Both endpoints are **inclusive**. `BETWEEN 10 AND 50` is equivalent to `>= 10 AND <= 50`.

---

## Section 8 — ORDER BY, LIMIT, OFFSET

**ORDER BY:**
```sql
ORDER BY last_name ASC          -- ascending (default)
ORDER BY salary DESC            -- descending
ORDER BY department ASC, salary DESC  -- multi-column: department first, then by salary within each dept
ORDER BY created_at DESC        -- most recent first
```

**NULL sort behavior:**
- PostgreSQL/Oracle: NULLs sort last with ASC (add `NULLS FIRST` to change)
- MySQL: NULLs sort first with ASC

**LIMIT and OFFSET:**
```sql
LIMIT 10                         -- first 10 rows
LIMIT 10 OFFSET 20               -- rows 21–30 (page 3 of 10-per-page)
```

**Pagination formula:**
```
OFFSET = (page_number - 1) × page_size
```

```sql
-- Page 1: LIMIT 10 OFFSET 0
-- Page 2: LIMIT 10 OFFSET 10
-- Page 3: LIMIT 10 OFFSET 20
```

> For large tables, keyset pagination (`WHERE id > last_seen_id`) is faster than OFFSET-based pagination because it avoids scanning skipped rows.

---

## Section 9 — DISTINCT

```sql
SELECT DISTINCT country FROM customers;
SELECT DISTINCT status FROM orders;
SELECT DISTINCT department, city FROM employees;  -- unique combinations of both columns
```

```sql
-- DISTINCT inside COUNT
SELECT COUNT(DISTINCT country) AS unique_countries FROM customers;
```

> **Caution with JOINs:** If your query joins a one-to-many relationship, adding DISTINCT can hide a JOIN problem (multiplied rows). Debug the JOIN first, then add DISTINCT if genuinely needed.

---

## Section 10 — NULL Handling

**Three-valued logic:** A comparison with NULL returns `UNKNOWN`, not TRUE or FALSE.

```sql
-- These never return any rows:
WHERE salary = NULL
WHERE salary != NULL

-- Correct:
WHERE salary IS NULL
WHERE salary IS NOT NULL
```

**COALESCE — use a fallback value:**
```sql
-- Use 0 if salary is NULL, otherwise use actual salary
SELECT first_name, COALESCE(salary, 0) AS salary FROM employees;

-- First non-NULL wins
SELECT COALESCE(phone, mobile, 'No contact') AS contact FROM customers;
```

**NULLIF — return NULL when two values are equal (avoids division by zero):**
```sql
-- Returns NULL instead of crashing when total_orders = 0
SELECT revenue / NULLIF(total_orders, 0) AS avg_order_value FROM summary;
```

---

## Section 11 — Aggregate Functions

| Function | Behavior | NULL handling |
|---|---|---|
| `COUNT(*)` | Counts all rows | Includes NULLs |
| `COUNT(col)` | Counts non-NULL values in column | Ignores NULLs |
| `COUNT(DISTINCT col)` | Counts distinct non-NULL values | Ignores NULLs |
| `SUM(col)` | Sum of all values | Ignores NULLs |
| `AVG(col)` | Mean of all values | Ignores NULLs (affects result!) |
| `MIN(col)` | Minimum value | Ignores NULLs |
| `MAX(col)` | Maximum value | Ignores NULLs |

```sql
SELECT
    COUNT(*)              AS total_rows,
    COUNT(email)          AS rows_with_email,
    COUNT(DISTINCT country) AS unique_countries,
    SUM(total_amount)     AS total_revenue,
    AVG(total_amount)     AS avg_order,
    MIN(order_date)       AS first_order,
    MAX(order_date)       AS latest_order
FROM orders;
```

---

## Section 12 — GROUP BY

```sql
-- One row per unique value of the GROUP BY column(s)
SELECT   country, COUNT(*) AS count
FROM     customers
GROUP BY country
ORDER BY count DESC;

-- Multi-column GROUP BY — one row per unique (department, city) combination
SELECT   department, city, COUNT(*) AS headcount, AVG(salary) AS avg_sal
FROM     employees
GROUP BY department, city
ORDER BY department, avg_sal DESC;
```

**Rule:** Every column in SELECT that is NOT wrapped in an aggregate function must appear in GROUP BY.

---

## Section 13 — HAVING

```sql
-- HAVING filters groups (after GROUP BY runs)
SELECT   country, COUNT(*) AS count
FROM     customers
GROUP BY country
HAVING   COUNT(*) > 50          -- keep only countries with 50+ customers

-- WHERE + GROUP BY + HAVING together
SELECT   department, COUNT(*) AS count, AVG(salary) AS avg_sal
FROM     employees
WHERE    salary > 40000         -- filter individual rows BEFORE grouping
GROUP BY department
HAVING   AVG(salary) > 65000    -- filter groups AFTER grouping
ORDER BY avg_sal DESC;
```

---

## Section 14 — HAVING vs WHERE

| Feature | `WHERE` | `HAVING` |
|---|---|---|
| Filters | Individual rows | Groups of rows |
| Runs | Before GROUP BY | After GROUP BY |
| Can reference | Any column value | Aggregate function results |
| Can reference | Columns in the table | Columns in GROUP BY or aggregated |

> **Summary:** WHERE filters rows before you group. HAVING filters groups after you group. They are complementary, not alternatives — you can use both in the same query.

---

## Section 15 — Table Relationships

| Relationship | Example | How it works |
|---|---|---|
| **One-to-Many** | One customer → many orders | FK in the "many" table: `orders.customer_id → customers.customer_id` |
| **Many-to-Many** | Many orders ↔ many products | Junction table: `order_items` holds FKs to both |
| **One-to-One** | Employee ↔ employee_details | FK in either table, often with a UNIQUE constraint |

**Schema FK relationships:**
```
categories (1) ──< products (many)         via products.category_id
customers  (1) ──< orders   (many)         via orders.customer_id
orders     (1) ──< order_items (many)      via order_items.order_id
products   (1) ──< order_items (many)      via order_items.product_id
employees  (1) ──< employees (self-ref)    via employees.manager_id
```

---

## Section 16 — INNER JOIN

Returns only rows that have a match on both sides.

```sql
SELECT o.order_id, c.first_name, c.last_name, o.total_amount
FROM   orders    o
JOIN   customers c ON o.customer_id = c.customer_id
ORDER BY o.order_date DESC;
```

- `JOIN` is shorthand for `INNER JOIN` — they are identical
- Rows with no matching partner on either side are excluded
- The `ON` clause specifies which columns to match (almost always a FK → PK relationship)

---

## Section 17 — LEFT JOIN and RIGHT JOIN

```sql
-- LEFT JOIN: all customers, NULL in order columns if no orders exist
SELECT   c.first_name, c.last_name, o.order_id
FROM     customers  c
LEFT JOIN orders    o ON c.customer_id = o.customer_id;

-- Find customers with NO orders (NULL detection pattern)
SELECT   c.first_name, c.last_name
FROM     customers  c
LEFT JOIN orders    o ON c.customer_id = o.customer_id
WHERE    o.order_id IS NULL;

-- RIGHT JOIN is equivalent to swapping table order with LEFT JOIN
-- These are identical:
FROM customers c LEFT  JOIN orders o ON c.customer_id = o.customer_id
FROM orders    o RIGHT JOIN customers c ON c.customer_id = o.customer_id
-- Prefer LEFT JOIN for consistency
```

**Key pattern:** `LEFT JOIN ... WHERE right_table.pk IS NULL` → find unmatched rows in the left table.

---

## Section 18 — FULL OUTER JOIN

```sql
-- All rows from both tables, NULLs where no match
SELECT c.first_name, o.order_id
FROM   customers  c
FULL OUTER JOIN orders o ON c.customer_id = o.customer_id;

-- Find ALL unmatched rows (orphaned on either side)
SELECT c.customer_id, o.order_id
FROM   customers  c
FULL OUTER JOIN orders o ON c.customer_id = o.customer_id
WHERE  c.customer_id IS NULL OR o.order_id IS NULL;
```

> MySQL does not support `FULL OUTER JOIN`. Workaround: `LEFT JOIN UNION ALL RIGHT JOIN WHERE left_table.id IS NULL`.

---

## Section 19 — CROSS JOIN and Self JOIN

```sql
-- CROSS JOIN: cartesian product — every row × every row
SELECT a.size_label, b.color_name
FROM   sizes  a
CROSS JOIN colors b;
-- Rows = (count of sizes) × (count of colors)

-- Self JOIN: join a table to itself using two aliases
SELECT e.first_name AS employee,
       m.first_name AS manager
FROM   employees e
LEFT JOIN employees m ON e.manager_id = m.employee_id;
-- LEFT JOIN to include employees with no manager (top of hierarchy)
```

---

## Section 20 — Multiple JOINs

```sql
-- 4-table join: full order detail
SELECT o.order_id,
       o.order_date,
       c.first_name || ' ' || c.last_name AS customer,
       p.name                              AS product,
       oi.quantity,
       oi.unit_price,
       oi.quantity * oi.unit_price         AS line_total
FROM      orders      o
JOIN      customers   c   ON o.customer_id  = c.customer_id
JOIN      order_items oi  ON o.order_id     = oi.order_id
JOIN      products    p   ON oi.product_id  = p.product_id
ORDER BY  o.order_date DESC;
```

**Rules:**
1. Each `JOIN` introduces one table and needs its own `ON` condition
2. Qualify every column with a table alias when joining 3+ tables
3. You can mix JOIN types — e.g., `INNER JOIN customers` but `LEFT JOIN discount_codes`

---

## Section 21 — Subqueries

**Single-row subquery (returns one value):**
```sql
-- Products above the average price
SELECT name, price
FROM   products
WHERE  price > (SELECT AVG(price) FROM products);
```

**Multi-row subquery (returns a list — use IN):**
```sql
-- Customers who have placed at least one order
SELECT first_name, last_name
FROM   customers
WHERE  customer_id IN (SELECT DISTINCT customer_id FROM orders);

-- Products never ordered (add IS NOT NULL to be safe with NOT IN)
SELECT name FROM products
WHERE  product_id NOT IN (
    SELECT product_id FROM order_items WHERE product_id IS NOT NULL
);
```

**Derived table (subquery in FROM):**
```sql
-- Average of per-customer order counts
SELECT AVG(order_count) AS avg_orders
FROM (
    SELECT customer_id, COUNT(*) AS order_count
    FROM   orders
    GROUP BY customer_id
) AS customer_summary;
```

---

## Section 22 — Correlated Subqueries

A correlated subquery references the outer query and executes once per outer row.

```sql
-- Employees earning above their own department's average
SELECT first_name, last_name, department, salary
FROM   employees e
WHERE  salary > (
    SELECT AVG(salary)
    FROM   employees
    WHERE  department = e.department   -- ← references outer row
);
```

**Execution pattern:**
```
For each row in outer query:
  1. Plug outer row's column value into inner query
  2. Run inner query
  3. Compare result to outer row
  4. Keep or discard the row
```

> **Performance:** Executes O(n) times — once per outer row. For large tables, prefer an equivalent JOIN + GROUP BY. Always check the query execution plan.

---

## Section 23 — EXISTS and NOT EXISTS

```sql
-- Customers who have placed at least one order
SELECT first_name, last_name
FROM   customers c
WHERE  EXISTS (
    SELECT 1 FROM orders o WHERE o.customer_id = c.customer_id
);

-- Customers who have NEVER ordered
SELECT first_name, last_name
FROM   customers c
WHERE  NOT EXISTS (
    SELECT 1 FROM orders o WHERE o.customer_id = c.customer_id
);
```

**EXISTS vs IN:**

| | `IN (subquery)` | `EXISTS (subquery)` |
|---|---|---|
| When to use | Simple value list | Existence check, especially correlated |
| NULL safety | `NOT IN` is dangerous with NULLs | `NOT EXISTS` handles NULLs correctly |
| Performance | Can be slow on large subquery results | Stops at first match — often faster |

> **Rule of thumb:** Prefer `NOT EXISTS` over `NOT IN` whenever the subquery could return NULLs.

---

## Section 24 — CASE Statements

**Simple CASE (compare a column to fixed values):**
```sql
SELECT order_id,
       CASE status
           WHEN 'pending'   THEN 'Awaiting Payment'
           WHEN 'completed' THEN 'Delivered'
           WHEN 'cancelled' THEN 'Refunded'
           ELSE                  'In Progress'
       END AS status_label
FROM orders;
```

**Searched CASE (evaluate any boolean condition):**
```sql
SELECT name, price,
       CASE
           WHEN price >= 100 THEN 'Premium'
           WHEN price >= 50  THEN 'Standard'
           WHEN price >= 10  THEN 'Budget'
           ELSE                   'Clearance'
       END AS price_tier
FROM products;
```

**CASE in aggregates (conditional counting / pivoting):**
```sql
SELECT
    EXTRACT(MONTH FROM order_date) AS month,
    COUNT(*)                                              AS total,
    COUNT(CASE WHEN status = 'completed' THEN 1 END)     AS completed,
    COUNT(CASE WHEN status = 'cancelled' THEN 1 END)     AS cancelled,
    SUM(CASE WHEN status = 'completed' THEN total_amount ELSE 0 END) AS revenue
FROM   orders
GROUP BY EXTRACT(MONTH FROM order_date)
ORDER BY month;
```

> Conditions are evaluated top-to-bottom; the first matching WHEN wins. Always include an ELSE clause — without it, non-matching rows return NULL.

---

## Section 25 — Calculated Columns

```sql
-- Arithmetic
SELECT order_id, quantity, unit_price,
       quantity * unit_price         AS line_total,
       quantity * unit_price * 0.1   AS tax_amount
FROM   order_items;

-- String operations
SELECT customer_id,
       CONCAT(first_name, ' ', last_name)  AS full_name,
       UPPER(last_name)                    AS last_name_upper,
       LENGTH(email)                       AS email_length
FROM   customers;

-- Date operations
SELECT order_id, order_date,
       EXTRACT(YEAR  FROM order_date) AS year,
       EXTRACT(MONTH FROM order_date) AS month,
       CURRENT_DATE - order_date      AS days_since_order
FROM   orders;
```

> Calculated columns are evaluated at runtime — they are not stored. Give them aliases for clean column headers in results.

---

## Section 26 — Common Mistakes and Fixes

| Mistake | Symptom | Fix |
|---|---|---|
| `WHERE salary = NULL` | Always returns zero rows | Use `IS NULL` |
| `WHERE COUNT(*) > 10` | Syntax error — aggregate in WHERE | Move to `HAVING` |
| SELECTing a column that is not in GROUP BY | Error or wrong result | Add column to GROUP BY or wrap in aggregate |
| `NOT IN (subquery)` with NULLs in subquery | Zero rows returned | Add `WHERE col IS NOT NULL` in subquery, or use `NOT EXISTS` |
| Missing table alias prefix with multi-table query | "Ambiguous column" error | Prefix all column references with their alias |
| Missing `ON` clause in a JOIN | Accidental CROSS JOIN / error | Always write an explicit `ON` condition |
| DISTINCT after a JOIN without fixing the JOIN | Hides a JOIN bug | Fix the root JOIN duplication issue first |
| SELECT * in production code | Fragile — breaks when schema changes; returns data you don't need | Name each column explicitly |

---

## Section 27 — Quick Reference Cheat Sheet

```sql
-- Aggregate + GROUP BY + HAVING template
SELECT   col, AGG(col2) AS alias
FROM     table
WHERE    row_condition
GROUP BY col
HAVING   AGG(col2) > n
ORDER BY alias DESC;

-- JOIN template (chain as many as needed)
FROM     table1 t1
JOIN     table2 t2  ON t1.fk_col  = t2.pk_col
LEFT JOIN table3 t3 ON t2.fk_col2 = t3.pk_col

-- Subquery in WHERE
WHERE col > (SELECT AGG(col) FROM table)
WHERE col IN (SELECT col FROM table WHERE condition)

-- NOT EXISTS template
WHERE NOT EXISTS (
    SELECT 1 FROM other_table WHERE other_table.fk = outer_table.pk
)

-- CASE template
CASE
    WHEN condition1 THEN value1
    WHEN condition2 THEN value2
    ELSE default_value
END AS alias

-- NULL-safe fallback
COALESCE(col, fallback_value)

-- Pagination
LIMIT page_size OFFSET (page_number - 1) * page_size
```

---

## Section 28 — Looking Ahead — Day 22

| Day 22 Topic | What It Covers |
|---|---|
| **DDL** | `CREATE TABLE`, `ALTER TABLE`, `DROP TABLE`, column data types |
| **Constraints** | PRIMARY KEY, FOREIGN KEY, UNIQUE, NOT NULL, CHECK, DEFAULT |
| **Normalization** | 1NF, 2NF, 3NF — designing clean, non-redundant schemas |
| **DML** | `INSERT INTO`, `UPDATE`, `DELETE FROM` — writing data |
| **Transactions** | `BEGIN`, `COMMIT`, `ROLLBACK`, ACID properties |
| **Indexes** | How B-tree indexes speed up queries; when to index |
| **Views** | Virtual tables — `CREATE VIEW` |

> Day 21 was all about **reading** data. Day 22 is all about **defining** and **writing** data.
