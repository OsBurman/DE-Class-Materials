# Day 22 Part 2 — DML, Indexes, Views, Stored Procedures, Triggers, Transactions & Optimization
## Slide Descriptions

**Total slides: 17**

---

### Slide 1 — Title Slide

**Title:** DML, Indexes, Views, Stored Procedures & Transactions
**Subtitle:** INSERT · UPDATE · DELETE · Indexes · Views · Stored Procedures · Triggers · Transactions · ACID · Query Optimization
**Day:** Week 5 — Day 22 | Part 2 of 2

**Objectives listed on slide:**
- Write `INSERT`, `UPDATE`, and `DELETE` statements safely
- Understand how indexes work and when to use them
- Create views for reusable queries
- Write stored procedures and user-defined functions
- Implement triggers for automatic data reactions
- Use transactions with `BEGIN`, `COMMIT`, and `ROLLBACK`
- Explain ACID properties and isolation levels
- Read an execution plan and identify optimization opportunities
- Understand database backup and recovery strategies

---

### Slide 2 — INSERT Statement

**Title:** INSERT — Adding Data

**Code block:**
```sql
-- Single row insert — explicit column list (always preferred)
INSERT INTO customers (full_name, email)
VALUES ('Alice Smith', 'alice@example.com');

-- Multiple rows in one statement (more efficient than multiple single inserts)
INSERT INTO customers (full_name, email)
VALUES
    ('Bob Jones',    'bob@example.com'),
    ('Carol White',  'carol@example.com'),
    ('David Brown',  'david@example.com');

-- INSERT ... SELECT — copy data from another table
INSERT INTO archived_orders (order_id, customer_id, order_date, total_amount)
SELECT order_id, customer_id, order_date, total_amount
FROM   orders
WHERE  order_date < '2023-01-01';

-- INSERT with explicit PK (only when you know the value)
INSERT INTO genres (genre_id, name)
VALUES (1, 'Fiction'),
       (2, 'Non-Fiction'),
       (3, 'Science Fiction'),
       (4, 'Biography');

-- UPSERT — insert if not exists, update if it does (MySQL)
INSERT INTO products (product_id, title, price)
VALUES (42, 'Clean Code', 39.99)
ON DUPLICATE KEY UPDATE price = 39.99;

-- PostgreSQL equivalent
INSERT INTO products (product_id, title, price)
VALUES (42, 'Clean Code', 39.99)
ON CONFLICT (product_id) DO UPDATE SET price = EXCLUDED.price;
```

**Rules:**
- Always specify the column list — if column order in the table changes, your insert won't break
- Columns with `DEFAULT` or `AUTO_INCREMENT` / `GENERATED` can be omitted
- `NOT NULL` columns without `DEFAULT` must be provided

---

### Slide 3 — UPDATE Statement

**Title:** UPDATE — Modifying Existing Data

**Code block:**
```sql
-- Update a single row (always use WHERE — see warning)
UPDATE customers
SET    email = 'alice.smith@newdomain.com'
WHERE  customer_id = 1;

-- Update multiple columns at once
UPDATE products
SET    price    = price * 0.9,   -- 10% discount
       updated_at = CURRENT_TIMESTAMP
WHERE  genre_id = 3;

-- Update with a subquery
UPDATE orders
SET    status = 'overdue'
WHERE  status = 'active'
AND    due_date < CURRENT_DATE;

-- Update using a JOIN (MySQL syntax)
UPDATE orders o
JOIN   customers c ON o.customer_id = c.customer_id
SET    o.status = 'vip_order'
WHERE  c.tier = 'vip';

-- PostgreSQL UPDATE ... FROM syntax
UPDATE orders
SET    status = 'vip_order'
FROM   customers
WHERE  orders.customer_id = customers.customer_id
AND    customers.tier = 'vip';
```

**⚠️ The most dangerous SQL mistake:**
```sql
-- This updates EVERY ROW in the table — no WHERE clause
UPDATE customers SET email = 'oops@example.com';

-- Safe practice: test your WHERE clause with SELECT first
SELECT * FROM customers WHERE customer_id = 1;   -- verify the target
UPDATE customers SET email = '...' WHERE customer_id = 1;  -- then update
```

---

### Slide 4 — DELETE Statement

**Title:** DELETE — Removing Data Safely

**Code block:**
```sql
-- Delete a specific row
DELETE FROM customers
WHERE  customer_id = 42;

-- Delete multiple rows matching a condition
DELETE FROM orders
WHERE  status = 'cancelled'
AND    order_date < '2022-01-01';

-- Delete with a subquery (customers who have never ordered)
DELETE FROM customers
WHERE  customer_id NOT IN (
    SELECT DISTINCT customer_id FROM orders WHERE customer_id IS NOT NULL
);

-- PostgreSQL DELETE USING (equivalent of MySQL's JOIN delete)
DELETE FROM orders
USING  customers
WHERE  orders.customer_id = customers.customer_id
AND    customers.is_deleted = TRUE;

-- Safe practice: use SELECT first to preview what will be deleted
SELECT COUNT(*) FROM customers WHERE customer_id = 42;
DELETE FROM customers WHERE customer_id = 42;
```

**DELETE vs TRUNCATE vs DROP (reminder from Part 1):**

| Command | Removes | Rollback | Resets AUTO_INCREMENT |
|---|---|---|---|
| `DELETE FROM t WHERE ...` | Matching rows | ✅ Yes | No |
| `DELETE FROM t` (no WHERE) | All rows | ✅ Yes | No |
| `TRUNCATE TABLE t` | All rows | ❌ No | ✅ Yes |
| `DROP TABLE t` | Rows + structure | ❌ No | N/A |

**Soft delete preference:** In production, consider `UPDATE ... SET is_deleted = TRUE` instead of physical delete — preserves audit history and FK integrity.

---

### Slide 5 — Indexes — How They Work

**Title:** Indexes — Making Queries Fast

**Concept:** An index is a separate data structure that the database maintains alongside your table to speed up lookups.

**Without an index — full table scan:**
```
Query: SELECT * FROM customers WHERE email = 'alice@example.com'

Without index: database reads EVERY ROW — O(n) rows examined
With index:    database jumps directly to the row — O(log n) lookups
```

**B-tree index internals (simplified):**
```
Table: customers (1,000,000 rows)

B-tree index on email:
         [M]
        /   \
      [D-J]  [N-Z]
     /  |  \   ...
  [D] [E] [F-J]
  ...
  → alice@example.com found in ~20 comparisons instead of 1,000,000
```

**What an index costs:**
- ✅ Faster `SELECT` with `WHERE`, `JOIN ON`, `ORDER BY`, `GROUP BY`
- ❌ Slower `INSERT`, `UPDATE`, `DELETE` — index must be maintained on every write
- ❌ Additional disk storage

**Rule of thumb:** An index is worth it when reads greatly outnumber writes on that column, or when the column is used in frequent, large-table searches.

---

### Slide 6 — Index Types and Usage

**Title:** Index Types — Choosing the Right One

**Creating and dropping indexes:**
```sql
-- Standard (B-tree) index
CREATE INDEX idx_customers_email ON customers(email);

-- Unique index — also enforces uniqueness (equivalent to UNIQUE constraint)
CREATE UNIQUE INDEX idx_products_isbn ON products(isbn);

-- Composite index — covers queries filtering on (last_name, first_name)
CREATE INDEX idx_customers_name ON customers(last_name, first_name);

-- Partial index (PostgreSQL) — index a subset of rows
CREATE INDEX idx_active_orders ON orders(customer_id)
WHERE status = 'active';   -- only indexes active orders

-- Full-text index (MySQL) — for LIKE '%keyword%' and full-text search
CREATE FULLTEXT INDEX idx_products_description ON products(description);

-- Drop an index
DROP INDEX idx_customers_email ON customers;    -- MySQL
DROP INDEX idx_customers_email;                  -- PostgreSQL
```

**Composite index column order matters:**
```sql
-- Index on (last_name, first_name)
-- This query CAN use the index:
WHERE last_name = 'Smith'
WHERE last_name = 'Smith' AND first_name = 'Alice'

-- This query CANNOT use the index efficiently:
WHERE first_name = 'Alice'   -- first_name is not the leftmost column
```

**When to index:**
- ✅ Primary keys (automatic)
- ✅ Foreign key columns (speeds up JOIN operations)
- ✅ Columns in frequent `WHERE` filters on large tables
- ✅ Columns in `ORDER BY` on large result sets
- ❌ Small tables (full scan is faster than index lookup overhead)
- ❌ Columns with very low cardinality (e.g., boolean `is_deleted` — only 2 values)
- ❌ Columns rarely used in queries

---

### Slide 7 — Views

**Title:** Views — Named, Reusable Queries

**Concept:** A view is a saved SELECT statement stored under a name. It behaves like a virtual table — you can SELECT from it, JOIN it, and filter it — but it stores no data itself.

**Code block:**
```sql
-- Create a view: active orders with customer details
CREATE VIEW active_orders_detail AS
SELECT o.order_id,
       o.order_date,
       c.full_name      AS customer_name,
       c.email,
       o.total_amount
FROM   orders    o
JOIN   customers c ON o.customer_id = c.customer_id
WHERE  o.status = 'active';

-- Query the view just like a table
SELECT * FROM active_orders_detail
WHERE  total_amount > 100
ORDER BY order_date DESC;

-- Update a view definition
CREATE OR REPLACE VIEW active_orders_detail AS
SELECT o.order_id, o.order_date, c.full_name, o.total_amount, o.status
FROM   orders o
JOIN   customers c ON o.customer_id = c.customer_id
WHERE  o.status IN ('active', 'pending');

-- Drop a view
DROP VIEW IF EXISTS active_orders_detail;
```

**Use cases:**

| Use Case | Example |
|---|---|
| **Simplify complex joins** | Create a view that pre-joins 4 tables; applications query the view |
| **Security / access control** | Expose a view with only non-sensitive columns to certain users |
| **Consistent business logic** | Define "active customer" once in a view; every query uses the view |
| **Backward compatibility** | Rename a table but keep a view with the old name for old queries |

**Materialized views (PostgreSQL, Oracle):**
> A materialized view physically stores the query result. It must be manually refreshed (`REFRESH MATERIALIZED VIEW name`) but reads are much faster because no join is re-executed. Useful for expensive aggregation queries on reporting tables.

---

### Slide 8 — Stored Procedures

**Title:** Stored Procedures — Reusable Database Logic

**Concept:** A stored procedure is a named, saved block of SQL (and procedural logic) stored in the database and executed by calling its name. Think of it as a function that lives in the database.

**MySQL stored procedure syntax:**
```sql
DELIMITER //

CREATE PROCEDURE place_order(
    IN  p_customer_id  INT,
    IN  p_product_id   INT,
    IN  p_quantity     INT,
    OUT p_order_id     INT
)
BEGIN
    DECLARE v_price DECIMAL(8,2);

    -- Get product price
    SELECT price INTO v_price
    FROM   products
    WHERE  product_id = p_product_id;

    -- Create the order
    INSERT INTO orders (customer_id, order_date, status, total_amount)
    VALUES (p_customer_id, CURRENT_DATE, 'active', v_price * p_quantity);

    -- Capture the new order ID
    SET p_order_id = LAST_INSERT_ID();

    -- Add the order line
    INSERT INTO order_lines (order_id, product_id, quantity, unit_price)
    VALUES (p_order_id, p_product_id, p_quantity, v_price);
END //

DELIMITER ;

-- Call the procedure
CALL place_order(1, 42, 2, @new_order_id);
SELECT @new_order_id;
```

**Stored functions — return a single value:**
```sql
DELIMITER //
CREATE FUNCTION get_customer_order_count(p_customer_id INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count
    FROM orders WHERE customer_id = p_customer_id;
    RETURN v_count;
END //
DELIMITER ;

-- Use in a query like any function
SELECT full_name, get_customer_order_count(customer_id) AS order_count
FROM   customers;
```

**Procedures vs Functions:**

| | Procedure | Function |
|---|---|---|
| Returns | Nothing (uses OUT params) | A single value |
| Called with | `CALL procedure_name(...)` | Used in SQL expressions |
| Can contain | DML + transactions | Usually no transactions |

---

### Slide 9 — Triggers and Sequences

**Title:** Triggers — Automatic Reactions to Data Events

**Concept:** A trigger is code that runs automatically when a specified event occurs on a table: BEFORE or AFTER an INSERT, UPDATE, or DELETE.

**Code block:**
```sql
-- Trigger: automatically set updated_at on every UPDATE
DELIMITER //
CREATE TRIGGER trg_products_updated_at
BEFORE UPDATE ON products
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END //
DELIMITER ;

-- Trigger: audit log — record every DELETE from orders
CREATE TABLE orders_audit (
    audit_id    INT         AUTO_INCREMENT PRIMARY KEY,
    order_id    INT,
    deleted_by  VARCHAR(100),
    deleted_at  DATETIME,
    old_status  VARCHAR(20)
);

DELIMITER //
CREATE TRIGGER trg_orders_delete_audit
AFTER DELETE ON orders
FOR EACH ROW
BEGIN
    INSERT INTO orders_audit (order_id, deleted_at, old_status)
    VALUES (OLD.order_id, CURRENT_TIMESTAMP, OLD.status);
END //
DELIMITER ;
```

**OLD and NEW references:**
- `OLD.column_name` — value before the change (available in UPDATE and DELETE triggers)
- `NEW.column_name` — value after the change (available in INSERT and UPDATE triggers)

**Trigger timing:**

| | BEFORE | AFTER |
|---|---|---|
| Use for | Validate/modify data before it's written | Audit logs, cascade actions |
| Can modify NEW values | ✅ Yes | ❌ No |

**Sequences and AUTO_INCREMENT:**
```sql
-- MySQL: AUTO_INCREMENT (column-level)
CREATE TABLE items (item_id INT AUTO_INCREMENT PRIMARY KEY, ...);
INSERT INTO items (name) VALUES ('widget');  -- item_id = 1, 2, 3...

-- PostgreSQL: SERIAL or GENERATED ALWAYS AS IDENTITY
CREATE TABLE items (item_id SERIAL PRIMARY KEY, ...);
-- Or: item_id INT GENERATED ALWAYS AS IDENTITY

-- PostgreSQL explicit sequence
CREATE SEQUENCE order_seq START 1000 INCREMENT 1;
SELECT nextval('order_seq');   -- get next value
```

---

### Slide 10 — Transactions — BEGIN, COMMIT, ROLLBACK

**Title:** Transactions — Atomic Units of Work

**Concept:** A transaction is a group of SQL statements that execute as a single unit. Either all succeed, or none of them take effect.

**Classic example — bank transfer:**
```sql
-- Transfer $500 from account 101 to account 202
-- These two updates MUST both succeed or both fail

START TRANSACTION;   -- BEGIN in PostgreSQL

UPDATE accounts
SET    balance = balance - 500
WHERE  account_id = 101;

-- Simulate: what if the server crashes here?
-- Without a transaction, account 101 is debited but 202 is never credited.

UPDATE accounts
SET    balance = balance + 500
WHERE  account_id = 202;

-- All checks pass — make it permanent
COMMIT;

-- If anything went wrong:
-- ROLLBACK;   -- undoes both updates
```

**E-commerce order example:**
```sql
START TRANSACTION;

-- 1. Create order record
INSERT INTO orders (customer_id, order_date, status, total_amount)
VALUES (1, CURRENT_DATE, 'active', 79.98);

SET @order_id = LAST_INSERT_ID();

-- 2. Add order line
INSERT INTO order_lines (order_id, product_id, quantity, unit_price)
VALUES (@order_id, 42, 2, 39.99);

-- 3. Reduce inventory
UPDATE products
SET    stock_qty = stock_qty - 2
WHERE  product_id = 42;

-- 4. Verify stock wasn't driven negative
IF (SELECT stock_qty FROM products WHERE product_id = 42) < 0 THEN
    ROLLBACK;
ELSE
    COMMIT;
END IF;
```

**SAVEPOINT — partial rollback:**
```sql
START TRANSACTION;
INSERT INTO orders (...) VALUES (...);
SAVEPOINT after_order;

INSERT INTO order_lines (...) VALUES (...);
-- Something goes wrong with the line item:
ROLLBACK TO SAVEPOINT after_order;  -- undo line item but keep order

COMMIT;  -- commit just the order
```

---

### Slide 11 — ACID Properties

**Title:** ACID — The Four Guarantees of Transactions

| Property | Meaning | Example |
|---|---|---|
| **Atomicity** | All operations in a transaction succeed, or none do | Bank transfer: both debit AND credit happen, or neither does |
| **Consistency** | A transaction brings the database from one valid state to another | CHECK constraints, FK constraints, and business rules are all satisfied after the transaction |
| **Isolation** | Concurrent transactions don't interfere with each other | Two users booking the same seat simultaneously — one wins, one fails cleanly |
| **Durability** | Once committed, data survives crashes, power loss, and restarts | Committed data is written to disk (WAL — Write-Ahead Log) |

**Memory device:** Think of a transaction like sending money via a bank wire transfer. It either fully arrives (Atomicity), the balances stay valid (Consistency), other account holders don't see half-transferred states (Isolation), and once confirmed, it's permanently in the recipient's account (Durability).

**What enforces ACID:**
- **Atomicity** → transaction log + rollback mechanism
- **Consistency** → constraints (PK, FK, CHECK, NOT NULL), application logic
- **Isolation** → locking and MVCC (Multi-Version Concurrency Control)
- **Durability** → Write-Ahead Logging (WAL) — writes to a durable log before modifying data pages

---

### Slide 12 — Isolation Levels

**Title:** Isolation Levels — Controlling Concurrent Access

**Three read phenomena you want to avoid:**

| Phenomenon | Description |
|---|---|
| **Dirty Read** | Transaction A reads data written by Transaction B that hasn't committed yet. If B rolls back, A read invalid data. |
| **Non-Repeatable Read** | Transaction A reads a row. Transaction B updates that row. A reads the row again — gets a different value. |
| **Phantom Read** | Transaction A runs a range query. Transaction B inserts rows matching the range. A re-runs the query — gets extra rows. |

**Four isolation levels (SQL standard):**

| Level | Dirty Read | Non-Repeatable | Phantom | Performance |
|---|---|---|---|---|
| `READ UNCOMMITTED` | ❌ Possible | ❌ Possible | ❌ Possible | Fastest |
| `READ COMMITTED` | ✅ Prevented | ❌ Possible | ❌ Possible | Fast |
| `REPEATABLE READ` | ✅ Prevented | ✅ Prevented | ❌ Possible | Moderate |
| `SERIALIZABLE` | ✅ Prevented | ✅ Prevented | ✅ Prevented | Slowest |

**Default isolation levels:**
- MySQL InnoDB: `REPEATABLE READ`
- PostgreSQL: `READ COMMITTED`
- SQL Server: `READ COMMITTED`

**Setting isolation level:**
```sql
-- MySQL
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;
...
COMMIT;

-- PostgreSQL
BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;
...
COMMIT;
```

**Practical guidance:** Most applications work fine with `READ COMMITTED`. Use `SERIALIZABLE` only when absolute correctness of concurrent reads is required — it significantly reduces throughput because transactions queue up.

---

### Slide 13 — Query Optimization and EXPLAIN

**Title:** Query Optimization — Understanding How the Database Executes Your SQL

**The query optimizer:**
Every database has a query optimizer that reads your SQL and decides *how* to execute it — which indexes to use, which table to read first, whether to use a nested loop or hash join. You can see its plan with `EXPLAIN`.

**EXPLAIN example:**
```sql
EXPLAIN SELECT o.order_id, c.full_name
FROM   orders o
JOIN   customers c ON o.customer_id = c.customer_id
WHERE  o.status = 'active'
AND    o.order_date > '2024-01-01';
```

**Reading EXPLAIN output (MySQL):**
```
id | select_type | table     | type  | possible_keys            | key              | rows    | Extra
1  | SIMPLE      | o         | ref   | idx_status, idx_date     | idx_status       | 10,234  | Using index condition
1  | SIMPLE      | c         | eq_ref| PRIMARY                  | PRIMARY          | 1       |
```

**Key columns to watch:**
- `type`: Access method — `const`/`eq_ref` (fast) → `ref`/`range` (ok) → `index` (scan) → `ALL` (full table scan — bad on large tables)
- `key`: Which index was actually chosen
- `rows`: Estimated rows examined — lower is better
- `Extra`: "Using filesort" and "Using temporary" are warnings — they indicate expensive operations

**EXPLAIN ANALYZE (PostgreSQL) / EXPLAIN FORMAT=JSON:**
```sql
EXPLAIN ANALYZE SELECT ...;  -- shows actual vs estimated rows
```

---

### Slide 14 — Query Optimization Techniques

**Title:** Making Queries Faster

**Code block — common optimization patterns:**
```sql
-- ❌ Slow: function on indexed column prevents index use
SELECT * FROM orders WHERE YEAR(order_date) = 2024;

-- ✅ Fast: range on the raw column uses the index
SELECT * FROM orders
WHERE order_date >= '2024-01-01' AND order_date < '2025-01-01';

-- ❌ Slow: leading wildcard disables index
SELECT * FROM products WHERE name LIKE '%book%';

-- ✅ Better: trailing wildcard uses index
SELECT * FROM products WHERE name LIKE 'book%';
-- For full-text search: use FULLTEXT index + MATCH...AGAINST

-- ❌ Slow: SELECT * brings back all columns — more data over the wire
SELECT * FROM order_lines WHERE order_id = 42;

-- ✅ Fast: select only what you need
SELECT product_id, quantity, unit_price FROM order_lines WHERE order_id = 42;

-- ❌ Slow: OR on different columns (can't use a single index)
SELECT * FROM customers WHERE first_name = 'Alice' OR last_name = 'Alice';

-- ✅ Better: UNION of two indexed queries
SELECT * FROM customers WHERE first_name = 'Alice'
UNION
SELECT * FROM customers WHERE last_name = 'Alice';

-- ❌ Slow: correlated subquery runs N times
SELECT name, (SELECT AVG(price) FROM products p2 WHERE p2.genre_id = p.genre_id) as avg
FROM products p;

-- ✅ Fast: JOIN + GROUP BY runs once
SELECT p.name, g_avg.avg_price
FROM   products p
JOIN   (SELECT genre_id, AVG(price) AS avg_price FROM products GROUP BY genre_id) g_avg
       ON p.genre_id = g_avg.genre_id;
```

---

### Slide 15 — Database Backup and Recovery Basics

**Title:** Backup & Recovery — Protecting Your Data

**Why backups matter:**
> "The only backup that matters is one you've tested." — every DBA ever

**Backup types:**

| Type | What it captures | How it works |
|---|---|---|
| **Full backup** | Entire database at a point in time | `mysqldump --all-databases > backup.sql` |
| **Incremental backup** | Only changes since last backup | Binary log shipping (MySQL), WAL archiving (PostgreSQL) |
| **Logical backup** | SQL statements to recreate data | `mysqldump`, `pg_dump` — portable, slow for large DBs |
| **Physical backup** | Raw data files | `mysqlbackup`, `pg_basebackup` — faster, same DB version required |

**MySQL dump and restore:**
```sql
-- Backup a single database
mysqldump -u root -p library > library_backup_2024-07-04.sql

-- Backup all databases
mysqldump -u root -p --all-databases > all_backup.sql

-- Restore
mysql -u root -p library < library_backup_2024-07-04.sql
```

**PostgreSQL:**
```bash
# Backup
pg_dump library > library_backup.sql
pg_dump -Fc library > library_backup.dump   # compressed custom format

# Restore
psql library < library_backup.sql
pg_restore -d library library_backup.dump
```

**Backup best practices:**
- Automate backups — never rely on manual processes
- Test restores regularly — an untested backup is worthless
- Store backups off-site / in cloud storage (S3, etc.)
- Retain multiple generations (daily for 7 days, weekly for 4 weeks, monthly for 1 year)
- Use point-in-time recovery (PITR) for critical systems — restore to any minute, not just snapshot times

---

### Slide 16 — Transaction Management + Error Handling in Procedures

**Title:** Combining Transactions with Error Handling

**Complete example — stored procedure with transaction and rollback:**
```sql
DELIMITER //
CREATE PROCEDURE transfer_stock(
    IN p_from_product INT,
    IN p_to_product   INT,
    IN p_quantity     INT,
    OUT p_result      VARCHAR(100)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 'ERROR: Transaction rolled back';
    END;

    START TRANSACTION;

    -- Deduct from source
    UPDATE products
    SET    stock_qty = stock_qty - p_quantity
    WHERE  product_id = p_from_product;

    -- Verify no negative stock
    IF (SELECT stock_qty FROM products WHERE product_id = p_from_product) < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Insufficient stock';
    END IF;

    -- Add to destination
    UPDATE products
    SET    stock_qty = stock_qty + p_quantity
    WHERE  product_id = p_to_product;

    COMMIT;
    SET p_result = 'SUCCESS';
END //
DELIMITER ;
```

---

### Slide 17 — Part 2 Summary

**Title:** Day 22 Complete — Advanced SQL Reference

**DML quick reference:**
```sql
INSERT INTO t (col1, col2) VALUES (val1, val2);
INSERT INTO t (col1, col2) VALUES (a,b), (c,d), (e,f);  -- batch insert

UPDATE t SET col = val [, col2 = val2] WHERE condition;  -- ALWAYS use WHERE
DELETE FROM t WHERE condition;                            -- ALWAYS use WHERE
```

**Index cheat sheet:**
```sql
CREATE INDEX idx_name ON table(column);
CREATE UNIQUE INDEX idx_name ON table(column);
CREATE INDEX idx_name ON table(col1, col2);    -- composite
DROP INDEX idx_name ON table;
```

**View syntax:**
```sql
CREATE [OR REPLACE] VIEW view_name AS SELECT ...;
DROP VIEW IF EXISTS view_name;
```

**Transaction template:**
```sql
START TRANSACTION;
-- DML statements
IF error THEN ROLLBACK; ELSE COMMIT; END IF;
```

**ACID recap:** Atomicity · Consistency · Isolation · Durability

**Isolation levels (low → high):** READ UNCOMMITTED → READ COMMITTED → REPEATABLE READ → SERIALIZABLE

**Optimization checklist:**
- [ ] Run EXPLAIN on slow queries
- [ ] Add indexes on FK columns and frequently filtered columns
- [ ] Avoid functions on indexed columns in WHERE
- [ ] Avoid leading wildcards in LIKE
- [ ] Select only needed columns — no `SELECT *`
- [ ] Replace correlated subqueries with JOINs where possible

**Up next — Day 23:** REST & API Tools — HTTP protocol, HTTP methods, status codes, RESTful API design, Postman, Swagger/OpenAPI
