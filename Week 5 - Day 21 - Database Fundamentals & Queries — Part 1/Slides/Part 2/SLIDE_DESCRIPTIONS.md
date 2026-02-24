# Day 21 Part 2 — Aggregates, JOINs, Subqueries & CASE
## Slide Descriptions

**Total slides: 17**

---

### Reference Schema (same as Part 1)

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

**Title:** Aggregates, JOINs, Subqueries & CASE
**Subtitle:** GROUP BY · Joins · Subqueries · Correlated · EXISTS · CASE
**Day:** Week 5 — Day 21 | Part 2 of 2

**Objectives listed on slide:**
- Use aggregate functions: `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`
- Group rows with `GROUP BY` and filter groups with `HAVING`
- Understand table relationships and join types
- Write `INNER`, `LEFT`, `RIGHT`, `FULL OUTER`, `CROSS`, and self joins
- Combine multiple tables in a single query
- Write single-row, multi-row, and correlated subqueries
- Use `EXISTS` and `NOT EXISTS` for existence checks
- Add conditional logic with `CASE` statements
- Build calculated columns and expressions

---

### Slide 2 — Aggregate Functions

**Title:** Aggregate Functions — Summarizing Data

**Function reference table:**

| Function | Returns | Notes |
|---|---|---|
| `COUNT(*)` | Number of rows | Includes NULLs |
| `COUNT(col)` | Number of non-NULL values | Ignores NULLs |
| `COUNT(DISTINCT col)` | Number of unique non-NULL values | |
| `SUM(col)` | Total of numeric column | Ignores NULLs |
| `AVG(col)` | Average of numeric column | Ignores NULLs |
| `MIN(col)` | Smallest value | Works on numbers, text, dates |
| `MAX(col)` | Largest value | Works on numbers, text, dates |

**Code block:**
```sql
-- Total number of customers
SELECT COUNT(*) AS total_customers FROM customers;

-- Only customers with a confirmed email
SELECT COUNT(email) AS customers_with_email FROM customers;

-- How many distinct countries do our customers come from?
SELECT COUNT(DISTINCT country) AS countries_served FROM customers;

-- Revenue summary
SELECT
    COUNT(*)              AS total_orders,
    SUM(total_amount)     AS revenue,
    AVG(total_amount)     AS avg_order_value,
    MIN(total_amount)     AS smallest_order,
    MAX(total_amount)     AS largest_order
FROM orders
WHERE status != 'cancelled';

-- Price range
SELECT MIN(price) AS lowest_price, MAX(price) AS highest_price
FROM products
WHERE stock_qty > 0;
```

---

### Slide 3 — GROUP BY

**Title:** GROUP BY — Aggregating by Category

**Concept:** `GROUP BY` collapses many rows into one per group, then applies aggregate functions to each group.

**Code block:**
```sql
-- How many customers per country?
SELECT   country,
         COUNT(*) AS customer_count
FROM     customers
GROUP BY country
ORDER BY customer_count DESC;

-- Result:
-- country    │ customer_count
-- ───────────┼───────────────
-- USA        │ 1,204
-- UK         │  487
-- Canada     │  341
-- Germany    │  298

-- Average order value by status
SELECT   status,
         COUNT(*)          AS order_count,
         AVG(total_amount) AS avg_value,
         SUM(total_amount) AS total_revenue
FROM     orders
GROUP BY status
ORDER BY total_revenue DESC;

-- Sales per product category
SELECT   c.name        AS category,
         COUNT(p.product_id) AS product_count,
         AVG(p.price)        AS avg_price
FROM     products  p
JOIN     categories c ON p.category_id = c.category_id
GROUP BY c.name
ORDER BY product_count DESC;
```

**Rule:** Every column in `SELECT` that is NOT inside an aggregate function **must appear** in `GROUP BY`.

---

### Slide 4 — HAVING — Filtering Groups

**Title:** HAVING — WHERE for Groups

**WHERE vs HAVING:**

| | `WHERE` | `HAVING` |
|---|---|---|
| Filters | Individual rows | Groups (after GROUP BY) |
| Can use | Column values | Aggregate function results |
| Executes | Before GROUP BY | After GROUP BY |

**Code block:**
```sql
-- Countries with MORE than 100 customers (can't use WHERE for this)
SELECT   country,
         COUNT(*) AS customer_count
FROM     customers
GROUP BY country
HAVING   COUNT(*) > 100
ORDER BY customer_count DESC;

-- Products that have been ordered at least 5 times
SELECT   p.name,
         COUNT(oi.item_id) AS times_ordered
FROM     products    p
JOIN     order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id, p.name
HAVING   COUNT(oi.item_id) >= 5
ORDER BY times_ordered DESC;

-- Departments with average salary > $70,000
SELECT   department,
         COUNT(*)        AS headcount,
         AVG(salary)     AS avg_salary
FROM     employees
GROUP BY department
HAVING   AVG(salary) > 70000
ORDER BY avg_salary DESC;
```

**Trick — using WHERE and HAVING together:**
```sql
-- Departments with 3+ employees earning over $50k, avg salary > $75k
SELECT   department, COUNT(*) AS count, AVG(salary) AS avg_sal
FROM     employees
WHERE    salary > 50000         -- filter rows FIRST
GROUP BY department
HAVING   COUNT(*) >= 3          -- then filter groups
ORDER BY avg_sal DESC;
```

---

### Slide 5 — Table Relationships

**Title:** Understanding Table Relationships

**Three relationship types:**

**One-to-Many (most common):**
```
customers (1) ──────< orders (many)
One customer can have many orders; each order belongs to one customer
FK: orders.customer_id → customers.customer_id
```

**Many-to-Many:**
```
orders (many) ──────< order_items >────── products (many)
One order can have many products; one product can appear in many orders
Junction/bridge table: order_items holds the FK to both sides
```

**One-to-One:**
```
employees (1) ──────── employee_details (1)
Used to split wide tables or separate sensitive data
```

**Visual of our schema relationships:**
```
categories ──< products ──< order_items >──── orders >──── customers
                                               
employees (self-referencing: manager_id → employee_id)
```

**Why this matters for JOINs:** When you JOIN two tables, you connect them on the column that holds the foreign key relationship. Understanding which table has the FK — and which direction the "one" and "many" are — tells you which JOIN type to use.

---

### Slide 6 — INNER JOIN

**Title:** INNER JOIN — Only Matching Rows

**Visual (Venn diagram described):** The intersection of two circles — only rows that have a match on both sides.

**Code block:**
```sql
-- Get orders WITH customer names (INNER JOIN — only matched pairs)
SELECT   o.order_id,
         o.order_date,
         o.total_amount,
         c.first_name,
         c.last_name
FROM     orders    o
INNER JOIN customers c  ON o.customer_id = c.customer_id
ORDER BY o.order_date DESC;

-- INNER JOIN is the default — keyword INNER is optional
SELECT o.order_id, c.first_name
FROM   orders o
JOIN   customers c ON o.customer_id = c.customer_id;

-- Products with their category names
SELECT p.name AS product, p.price, c.name AS category
FROM   products   p
JOIN   categories c ON p.category_id = c.category_id
ORDER BY c.name, p.name;
```

**What INNER JOIN excludes:**
- Orders with no matching customer (orphaned records)
- Customers with no orders

**When to use:** When you only want rows where the relationship exists on both sides.

---

### Slide 7 — LEFT JOIN and RIGHT JOIN

**Title:** LEFT JOIN / RIGHT JOIN — Include Non-Matching Rows

**Visual:** All rows from the left table; matching rows from the right; NULLs where no match.

**Code block:**
```sql
-- LEFT JOIN: ALL customers, even those with no orders
SELECT   c.first_name,
         c.last_name,
         COUNT(o.order_id) AS order_count
FROM     customers  c
LEFT JOIN orders    o  ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY order_count DESC;

-- Finding customers who have NEVER placed an order
SELECT   c.first_name, c.last_name, c.email
FROM     customers  c
LEFT JOIN orders    o  ON c.customer_id = o.customer_id
WHERE    o.order_id IS NULL;   -- NULLs in right-side columns = no match found

-- Products that have NEVER been ordered
SELECT   p.name, p.price
FROM     products    p
LEFT JOIN order_items oi ON p.product_id = oi.product_id
WHERE    oi.item_id IS NULL;

-- RIGHT JOIN is LEFT JOIN with the tables swapped (rarely used)
-- These two are equivalent:
FROM customers c LEFT  JOIN orders o ON ...
FROM orders    o RIGHT JOIN customers c ON ...
-- Prefer LEFT JOIN for consistency — always leads with the "primary" table
```

**Key pattern:** `LEFT JOIN ... WHERE right_table.id IS NULL` = "find rows in left table that have NO match in right table."

---

### Slide 8 — FULL OUTER JOIN

**Title:** FULL OUTER JOIN — Everything from Both Tables

**Visual:** All rows from both circles including the non-overlapping parts; NULLs fill in where no match.

**Code block:**
```sql
-- All customers AND all orders, whether matched or not
SELECT   c.first_name,
         c.last_name,
         o.order_id,
         o.total_amount
FROM     customers  c
FULL OUTER JOIN orders o ON c.customer_id = o.customer_id
ORDER BY c.last_name;

-- Result includes:
-- Matched pairs: customer + their orders
-- Customers with no orders: customer columns filled, order columns NULL
-- Orders with no customer: order columns filled, customer columns NULL

-- Find BOTH unmatched customers AND orphaned orders
SELECT c.customer_id, o.order_id
FROM   customers c
FULL OUTER JOIN orders o ON c.customer_id = o.customer_id
WHERE  c.customer_id IS NULL     -- orphaned orders
OR     o.order_id IS NULL;       -- customers with no orders
```

**Join type comparison:**

| Join Type | Returns |
|---|---|
| `INNER JOIN` | Only rows with matches on BOTH sides |
| `LEFT JOIN` | All left rows + matching right rows (NULLs where no match) |
| `RIGHT JOIN` | All right rows + matching left rows (NULLs where no match) |
| `FULL OUTER JOIN` | All rows from BOTH sides (NULLs where no match) |

**Note:** MySQL does not support `FULL OUTER JOIN` natively — use `LEFT JOIN UNION RIGHT JOIN` as a workaround.

---

### Slide 9 — CROSS JOIN and Self JOIN

**Title:** CROSS JOIN · Self JOIN — Special Cases

**CROSS JOIN — every combination of both tables:**
```sql
-- Every possible (size, color) combination for a product configurator
SELECT s.size_name, c.color_name
FROM   sizes  s
CROSS JOIN colors c
ORDER BY s.size_name, c.color_name;
-- 3 sizes × 5 colors = 15 rows

-- Practical use: generating test data, scheduling matrices
-- CAUTION: 1,000 rows × 1,000 rows = 1,000,000 rows — always be careful
```

**Self JOIN — a table joining to itself:**
```sql
-- Find each employee's manager name
-- employees table has: employee_id, first_name, manager_id
-- manager_id is a foreign key that points back to employee_id in the same table

SELECT e.first_name  || ' ' || e.last_name   AS employee,
       m.first_name  || ' ' || m.last_name   AS manager
FROM   employees e
LEFT JOIN employees m ON e.manager_id = m.employee_id
ORDER BY m.last_name, e.last_name;

-- LEFT JOIN because the CEO has no manager (manager_id IS NULL)
-- With INNER JOIN, the top-level employee(s) would be excluded
```

**When to use:**
- CROSS JOIN: generate all combinations (config options, scheduling)
- Self JOIN: hierarchical data (org charts, categories with parent IDs, comment threads)

---

### Slide 10 — Multiple JOINs in One Query

**Title:** Chaining Multiple JOINs

**Code block — 3-table join:**
```sql
-- Full order detail: order + customer + product names
SELECT o.order_id,
       o.order_date,
       c.first_name || ' ' || c.last_name AS customer_name,
       p.name        AS product_name,
       oi.quantity,
       oi.unit_price,
       oi.quantity * oi.unit_price AS line_total
FROM      orders      o
JOIN      customers   c  ON o.customer_id    = c.customer_id
JOIN      order_items oi ON o.order_id       = oi.order_id
JOIN      products    p  ON oi.product_id    = p.product_id
ORDER BY  o.order_date DESC, o.order_id, p.name;
```

**4-table join adding categories:**
```sql
SELECT o.order_id,
       c.last_name       AS customer,
       cat.name          AS category,
       p.name            AS product,
       oi.quantity,
       oi.unit_price
FROM      orders      o
JOIN      customers   c   ON o.customer_id  = c.customer_id
JOIN      order_items oi  ON o.order_id     = oi.order_id
JOIN      products    p   ON oi.product_id  = p.product_id
JOIN      categories  cat ON p.category_id  = cat.category_id
WHERE     o.status = 'completed'
ORDER BY  o.order_date DESC;
```

**Best practices:**
- Use table aliases (short: `o`, `c`, `p`) to reduce repetition
- Qualify every column name with its alias when joining 3+ tables — avoids ambiguity errors
- Arrange JOINs in logical order following the data flow
- Each JOIN needs its own `ON` condition

---

### Slide 11 — Subqueries — Introduction

**Title:** Subqueries — Queries Inside Queries

**Concept:** A subquery (inner query) runs first; its result is used by the outer query.

**Single-row subquery (returns exactly one value):**
```sql
-- Products priced above the average
SELECT name, price
FROM   products
WHERE  price > (SELECT AVG(price) FROM products)
ORDER BY price DESC;

-- Customers who placed the single largest order
SELECT first_name, last_name, email
FROM   customers
WHERE  customer_id = (
    SELECT customer_id FROM orders
    ORDER BY total_amount DESC
    LIMIT 1
);
```

**Multi-row subquery (returns multiple values — use IN):**
```sql
-- Customers who have placed at least one order
SELECT first_name, last_name
FROM   customers
WHERE  customer_id IN (
    SELECT DISTINCT customer_id
    FROM   orders
);

-- Products that have NEVER been ordered
SELECT name, price
FROM   products
WHERE  product_id NOT IN (
    SELECT DISTINCT product_id FROM order_items
);
```

**Subquery in FROM clause (derived table):**
```sql
-- Average of the per-customer order counts
SELECT AVG(order_count) AS avg_orders_per_customer
FROM (
    SELECT customer_id, COUNT(*) AS order_count
    FROM   orders
    GROUP BY customer_id
) AS customer_totals;
```

---

### Slide 12 — Correlated Subqueries

**Title:** Correlated Subqueries — Row-by-Row Evaluation

**Concept:** A correlated subquery references the outer query. It runs once per row of the outer query — like a nested loop.

**Code block:**
```sql
-- Employees who earn more than the average salary IN THEIR OWN DEPARTMENT
-- The inner query references e.department from the outer query
SELECT e.first_name,
       e.last_name,
       e.department,
       e.salary
FROM   employees e
WHERE  e.salary > (
    SELECT AVG(salary)
    FROM   employees
    WHERE  department = e.department   -- ← refers back to outer row
)
ORDER BY e.department, e.salary DESC;

-- For each order, show if it's above or below that customer's average
SELECT o.order_id,
       o.customer_id,
       o.total_amount,
       (SELECT AVG(total_amount) FROM orders WHERE customer_id = o.customer_id) AS customer_avg
FROM   orders o
ORDER BY o.customer_id;
```

**How it executes:**
```
For each row in employees (outer):
  → Run inner query with that row's department value
  → Compare that row's salary to the result
  → Keep row if condition is true
```

**Performance note:**
> Correlated subqueries are logically clear but can be slow — they execute once per outer row. For large tables, the same result is often achievable with a `JOIN` + `GROUP BY`, which performs much better. Always check the execution plan.

---

### Slide 13 — EXISTS and NOT EXISTS

**Title:** EXISTS / NOT EXISTS — Testing for Existence

**Concept:** EXISTS returns TRUE if the subquery produces at least one row. Does not need to return a specific value — just checks "does anything match?"

**Code block:**
```sql
-- Customers who HAVE placed at least one order
SELECT first_name, last_name
FROM   customers c
WHERE  EXISTS (
    SELECT 1                          -- the value doesn't matter
    FROM   orders o
    WHERE  o.customer_id = c.customer_id
);

-- Customers who have NEVER placed an order
SELECT first_name, last_name, email
FROM   customers c
WHERE  NOT EXISTS (
    SELECT 1
    FROM   orders o
    WHERE  o.customer_id = c.customer_id
);

-- Products that were ordered in 2024
SELECT name, price
FROM   products p
WHERE  EXISTS (
    SELECT 1
    FROM   order_items oi
    JOIN   orders o ON oi.order_id = o.order_id
    WHERE  oi.product_id = p.product_id
    AND    o.order_date >= '2024-01-01'
);
```

**EXISTS vs IN:**

| | `IN (subquery)` | `EXISTS (subquery)` |
|---|---|---|
| Works on | A list of values | Presence of rows |
| NULL handling | Problematic with NOT IN | Safe — NULLs don't cause issues |
| Performance | Can be slower with large subquery | Often faster — stops at first match |
| Readability | Clear for simple lists | Better for correlated checks |

**Best practice:** Prefer `NOT EXISTS` over `NOT IN` when NULLs might appear in the subquery result — `NOT IN` with any NULL in the list returns no rows (due to NULL propagation), which is almost never what you want.

---

### Slide 14 — CASE Statements

**Title:** CASE — Conditional Logic in SQL

**Two forms:**

**Simple CASE (compare one column to values):**
```sql
SELECT order_id,
       total_amount,
       CASE status
           WHEN 'pending'   THEN 'Awaiting Payment'
           WHEN 'paid'      THEN 'Processing'
           WHEN 'shipped'   THEN 'On Its Way'
           WHEN 'completed' THEN 'Delivered'
           WHEN 'cancelled' THEN 'Cancelled'
           ELSE                  'Unknown Status'
       END AS status_label
FROM orders;
```

**Searched CASE (evaluate conditions):**
```sql
-- Categorize orders by value
SELECT order_id,
       total_amount,
       CASE
           WHEN total_amount >= 500 THEN 'High Value'
           WHEN total_amount >= 100 THEN 'Standard'
           WHEN total_amount >= 25  THEN 'Small'
           ELSE                          'Micro'
       END AS order_tier
FROM orders
ORDER BY total_amount DESC;

-- Employee salary banding
SELECT first_name, last_name, department, salary,
       CASE
           WHEN salary >= 100000 THEN 'Senior'
           WHEN salary >= 70000  THEN 'Mid-Level'
           WHEN salary >= 50000  THEN 'Junior'
           ELSE                       'Entry'
       END AS level
FROM employees;
```

---

### Slide 15 — CASE in Aggregates — Pivoting Data

**Title:** CASE + GROUP BY — Pivoting and Conditional Counts

**Pattern:** Use `CASE` inside aggregate functions to count or sum by condition.

**Code block:**
```sql
-- Orders broken down by status — all in one row per month
SELECT
    EXTRACT(MONTH FROM order_date) AS month,
    COUNT(*)                        AS total_orders,
    COUNT(CASE WHEN status = 'completed' THEN 1 END) AS completed,
    COUNT(CASE WHEN status = 'cancelled' THEN 1 END) AS cancelled,
    COUNT(CASE WHEN status = 'pending'   THEN 1 END) AS pending,
    SUM(CASE WHEN status = 'completed'
             THEN total_amount ELSE 0 END)            AS completed_revenue
FROM   orders
WHERE  order_date >= '2024-01-01'
GROUP BY EXTRACT(MONTH FROM order_date)
ORDER BY month;

-- Result: one row per month showing breakdown across all statuses
-- month │ total │ completed │ cancelled │ pending │ revenue
-- ──────┼───────┼───────────┼───────────┼─────────┼────────
-- 1     │  432  │  389      │  27       │ 16      │ 48,221
-- 2     │  398  │  361      │  22       │ 15      │ 44,890
```

**This is called a "pivot" or "conditional aggregation" — powerful pattern for reporting queries.**

---

### Slide 16 — Calculated Columns and Expressions

**Title:** Calculated Columns — Math in SQL

**Code block:**
```sql
-- Arithmetic
SELECT name,
       price,
       price * 0.9               AS sale_price,
       price * 0.1               AS discount_amount,
       price * 1.08              AS price_with_tax
FROM   products;

-- String functions
SELECT UPPER(last_name)          AS last_name_upper,
       LOWER(email)              AS email_lower,
       LENGTH(first_name)        AS name_length,
       SUBSTRING(email, 1, 10)   AS email_preview,
       TRIM(first_name)          AS trimmed_name
FROM   customers;

-- Date functions
SELECT order_id,
       order_date,
       CURRENT_DATE                               AS today,
       CURRENT_DATE - order_date                  AS days_ago,
       EXTRACT(YEAR  FROM order_date)             AS order_year,
       EXTRACT(MONTH FROM order_date)             AS order_month,
       DATE_TRUNC('month', order_date)            AS first_of_month
FROM   orders;

-- Line total from order_items
SELECT order_id,
       product_id,
       quantity,
       unit_price,
       quantity * unit_price                      AS line_total,
       quantity * unit_price * 1.08               AS line_total_with_tax
FROM   order_items;
```

---

### Slide 17 — Part 2 Summary

**Title:** Day 21 Complete — Full SQL Query Reference

**Aggregate function reference:**
```sql
COUNT(*) · COUNT(col) · COUNT(DISTINCT col)
SUM(col) · AVG(col) · MIN(col) · MAX(col)
```

**GROUP BY + HAVING pattern:**
```sql
SELECT   col, AGG_FUNC(col2) AS alias
FROM     table
[WHERE   row_filter]            -- filter rows before grouping
GROUP BY col
HAVING   AGG_FUNC(col2) > n    -- filter groups after grouping
ORDER BY alias DESC;
```

**JOIN cheat sheet:**

| Type | Returns |
|---|---|
| `INNER JOIN` | Matched rows only |
| `LEFT JOIN` | All left + matched right (NULL if no match) |
| `RIGHT JOIN` | All right + matched left (NULL if no match) |
| `FULL OUTER JOIN` | All rows both sides |
| `CROSS JOIN` | Every combination |
| `Self JOIN` | Table joined to itself (aliases required) |

**Subquery placement:**
- In `WHERE` → filter with a computed value or list
- In `FROM` → use as a derived table
- In `SELECT` → compute a per-row value

**CASE syntax:**
```sql
CASE WHEN condition THEN value
     WHEN condition THEN value
     ELSE default_value
END AS alias
```

**Up next — Day 22:** DDL (`CREATE`, `ALTER`, `DROP`), constraints, normalization, DML (`INSERT`, `UPDATE`, `DELETE`), transactions, and indexes.
