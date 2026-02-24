# Day 22 — SQL Database Design & Advanced Topics — Review Guide

**Week 5 — Day 22 | Covers:** DDL · Data types · Constraints · Normalization · ER diagrams · DML · Indexes · Views · Stored procedures · Triggers · Sequences · Transactions · ACID · Isolation levels · Query optimization · Execution plans · Backup & recovery

---

## Section 1 — DDL Overview

Data Definition Language (DDL) defines and modifies database *structure*, not data.

| Command | Purpose | Auto-committed |
|---|---|---|
| `CREATE` | Create databases, tables, views, indexes | ✅ Yes |
| `ALTER` | Modify existing structure | ✅ Yes |
| `DROP` | Delete structure and all its data permanently | ✅ Yes |
| `TRUNCATE` | Remove all rows, keep structure | ✅ Yes (most DBs) |

> **Production caution:** DDL is auto-committed — it cannot be rolled back. `ALTER TABLE` on a large table can lock it during the operation. Always test DDL changes in a staging environment first.

---

## Section 2 — CREATE TABLE Syntax

```sql
CREATE TABLE table_name (
    column_name  data_type  [column_constraints],
    ...
    [table_level_constraints]
);

-- Example
CREATE TABLE products (
    product_id   INT           NOT NULL AUTO_INCREMENT,
    title        VARCHAR(300)  NOT NULL,
    price        DECIMAL(8,2)  NOT NULL CHECK (price >= 0),
    genre_id     INT,
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id),
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE SET NULL
);

-- Safe creation
CREATE TABLE IF NOT EXISTS products (...);
```

---

## Section 3 — SQL Data Types

**Numeric:**

| Type | Bytes | Use |
|---|---|---|
| `TINYINT` | 1 | 0–255 or –128–127; ratings, flags |
| `SMALLINT` | 2 | Years, small counts |
| `INT` / `INTEGER` | 4 | Most IDs and counts |
| `BIGINT` | 8 | High-volume IDs, large numbers |
| `DECIMAL(p,s)` | Variable | **Always use for money** — exact |
| `FLOAT` / `DOUBLE` | 4/8 | Approximate — avoid for money |

**String:**

| Type | Use |
|---|---|
| `CHAR(n)` | Fixed-length (country codes, status flags) |
| `VARCHAR(n)` | Variable-length — most general purpose strings |
| `TEXT` | Long content — not easily indexable |

**Date/Time:**

| Type | Stores |
|---|---|
| `DATE` | `'2024-07-04'` |
| `DATETIME` | `'2024-07-04 14:30:00'` — no timezone |
| `TIMESTAMP` | Date + time stored as UTC; auto-updates |

> **Rule:** Use `DECIMAL` for money, `TIMESTAMP` for `created_at` / `updated_at`, `VARCHAR` for variable strings you need to index.

---

## Section 4 — All Six Constraints

### PRIMARY KEY
```sql
-- Single column
product_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY

-- Composite (table level)
PRIMARY KEY (order_id, product_id)
```
- Uniquely identifies each row; cannot be NULL; every table should have one.

### FOREIGN KEY
```sql
FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
    ON DELETE RESTRICT    -- prevent orphaning (safest default)
    ON UPDATE CASCADE     -- propagate PK changes
```

| Action | Effect |
|---|---|
| `RESTRICT` | Reject delete/update if children exist |
| `CASCADE` | Automatically delete/update children |
| `SET NULL` | Set FK to NULL when parent is removed |

### UNIQUE
```sql
email VARCHAR(150) NOT NULL UNIQUE
-- OR at table level:
UNIQUE (email)
```
- No duplicate values; allows one NULL (NULL ≠ NULL).

### NOT NULL
```sql
title VARCHAR(300) NOT NULL
```
- Column must always have a value.

### CHECK
```sql
price DECIMAL(8,2) CHECK (price >= 0)
rating TINYINT CHECK (rating BETWEEN 1 AND 5)
```
- Validates value against a condition on every INSERT and UPDATE.

### DEFAULT
```sql
status     VARCHAR(20)  NOT NULL DEFAULT 'active'
created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
```
- Supplies a fallback value when the column is omitted.

---

## Section 5 — ALTER TABLE

```sql
-- Add column
ALTER TABLE t ADD COLUMN col type [constraints];

-- Modify column type (MySQL)
ALTER TABLE t MODIFY COLUMN col new_type [constraints];

-- Rename column (MySQL 8+, PostgreSQL)
ALTER TABLE t RENAME COLUMN old_name TO new_name;

-- Drop column
ALTER TABLE t DROP COLUMN col;

-- Add constraint
ALTER TABLE t ADD CONSTRAINT name FOREIGN KEY (col) REFERENCES other(col);
ALTER TABLE t ADD CONSTRAINT name CHECK (col > 0);

-- Drop constraint
ALTER TABLE t DROP CONSTRAINT name;         -- PostgreSQL
ALTER TABLE t DROP CHECK name;              -- MySQL
ALTER TABLE t DROP FOREIGN KEY name;        -- MySQL

-- Rename table
RENAME TABLE old TO new;                    -- MySQL
ALTER TABLE old RENAME TO new;              -- PostgreSQL
```

> **Tip:** Always name your constraints (`fk_orders_customer`, `chk_price_positive`). Named constraints are much easier to drop later.

---

## Section 6 — DROP and TRUNCATE

```sql
DROP TABLE IF EXISTS t;             -- removes table + data + indexes
DROP DATABASE db;                   -- removes entire database

TRUNCATE TABLE t;                   -- removes all rows, keeps structure, resets AUTO_INCREMENT
```

| Command | Removes | Rollback | Speed |
|---|---|---|---|
| `DELETE FROM t` | All rows (logged) | ✅ Yes | Slow |
| `TRUNCATE TABLE t` | All rows (minimal log) | ❌ No | Fast |
| `DROP TABLE t` | Rows + structure | ❌ No | Immediate |

> Dependency order: drop/truncate child tables before parent tables (or use `CASCADE` in PostgreSQL).

---

## Section 7 — Normalization

### Why Normalize?

Eliminates three data anomalies:
- **Insertion anomaly:** Can't add data without unrelated data
- **Update anomaly:** Changing one fact requires multiple row updates
- **Deletion anomaly:** Deleting one row accidentally removes other facts

### 1NF — First Normal Form
**Rule:** Atomic values, no repeating column groups, must have a primary key.

```
BAD:  order_id | customer | product1 | qty1 | product2 | qty2
GOOD: order_id | customer | product  | quantity
      (one row per order-product combination, PK = order_id + product)
```

### 2NF — Second Normal Form
**Rule:** Must be 1NF + every non-key column depends on the ENTIRE composite primary key.

```
Composite PK: (order_id, product)
customer → depends only on order_id ❌ (partial dependency)
Fix: move customer to its own orders table with order_id as PK
```

### 3NF — Third Normal Form
**Rule:** Must be 2NF + no transitive dependencies (non-key → non-key).

```
order_id → zip_code → city
city depends on zip_code, not directly on order_id ❌
Fix: move zip_code → city to a separate zip_codes table
```

**Summary:**

| Normal Form | Eliminates |
|---|---|
| 1NF | Repeating groups, non-atomic values |
| 2NF | Partial dependencies on composite PK |
| 3NF | Transitive dependencies between non-key columns |

---

## Section 8 — ER Diagrams

**Crow's Foot notation:**

| Symbol | Meaning |
|---|---|
| `──│` | Exactly one |
| `──○` | Zero or one |
| `──<` | Many |
| `──│<` | One or many (mandatory many) |
| `──○<` | Zero or many (optional many) |

**How to read relationships:**
- `customers ──│──○< orders` → one customer has zero or more orders
- `orders ──│──│< order_lines` → one order has one or more order_lines

**ER diagram tools:** dbdiagram.io · draw.io · MySQL Workbench · DBeaver (reverse-engineer from existing DB) · Lucidchart

---

## Section 9 — Data Modeling Principles

**Naming conventions:**

| Object | Convention | Example |
|---|---|---|
| Table | `snake_case`, plural | `customers`, `order_lines` |
| PK column | `table_singular_id` | `customer_id`, `order_id` |
| FK column | Same name as referenced PK | `customer_id` in orders |
| Boolean column | `is_` or `has_` prefix | `is_deleted`, `has_subscription` |
| Timestamps | `created_at`, `updated_at` | Applied to every table |

**Surrogate vs natural keys:**
- **Surrogate key:** `INT AUTO_INCREMENT` or UUID — stable, meaningless, always safe to join on
- **Natural key:** Email, SSN, ISBN — meaningful but can change in the real world → cascading updates

> Prefer surrogate keys for almost all tables.

**Soft delete:**
```sql
ALTER TABLE customers
    ADD COLUMN is_deleted  BOOLEAN  NOT NULL DEFAULT FALSE,
    ADD COLUMN deleted_at  DATETIME;

-- Query active records
SELECT * FROM customers WHERE is_deleted = FALSE;
```

---

## Section 10 — INSERT Statement

```sql
-- Single row — always specify column list
INSERT INTO customers (full_name, email)
VALUES ('Alice Smith', 'alice@example.com');

-- Multiple rows
INSERT INTO genres (genre_id, name)
VALUES (1, 'Fiction'), (2, 'Non-Fiction'), (3, 'Science Fiction');

-- INSERT ... SELECT
INSERT INTO archive_orders SELECT * FROM orders WHERE order_date < '2023-01-01';

-- UPSERT (MySQL)
INSERT INTO products (product_id, title, price) VALUES (1, 'Book', 29.99)
ON DUPLICATE KEY UPDATE price = 29.99;

-- UPSERT (PostgreSQL)
INSERT INTO products (product_id, title, price) VALUES (1, 'Book', 29.99)
ON CONFLICT (product_id) DO UPDATE SET price = EXCLUDED.price;
```

---

## Section 11 — UPDATE Statement

```sql
-- Always include WHERE
UPDATE customers
SET    email = 'newemail@example.com'
WHERE  customer_id = 1;

-- Multiple columns
UPDATE products
SET    price = price * 0.9,
       updated_at = CURRENT_TIMESTAMP
WHERE  genre_id = 3;

-- Safe practice: run SELECT first to verify target
SELECT * FROM customers WHERE customer_id = 1;   -- verify
UPDATE customers SET email = '...' WHERE customer_id = 1;  -- then update
```

> **⚠️ Danger:** `UPDATE t SET col = val` with no WHERE updates EVERY ROW. Always include a WHERE clause.

---

## Section 12 — DELETE Statement

```sql
-- Delete specific rows
DELETE FROM customers WHERE customer_id = 42;

-- Delete with date range
DELETE FROM orders
WHERE  status = 'cancelled' AND order_date < '2022-01-01';

-- Verify before deleting
SELECT COUNT(*) FROM customers WHERE customer_id = 42;
DELETE FROM customers WHERE customer_id = 42;
```

> FK constraints prevent deleting a parent row when children exist (with `ON DELETE RESTRICT`). Respect the dependency order or use soft deletes.

---

## Section 13 — Indexes

**How they work:** B-tree index turns O(n) full table scan into O(log n) lookup.

**Cost:** Faster reads; slower writes (index must be maintained on INSERT/UPDATE/DELETE); extra disk space.

```sql
CREATE INDEX idx_customers_email ON customers(email);
CREATE UNIQUE INDEX idx_products_isbn ON products(isbn);
CREATE INDEX idx_orders_status ON orders(status, order_date);  -- composite
DROP INDEX idx_customers_email ON customers;  -- MySQL
DROP INDEX idx_customers_email;               -- PostgreSQL
```

**Composite index leftmost rule:** Index on `(last_name, first_name)` helps queries filtering on `last_name` or `(last_name, first_name)` — NOT queries filtering only on `first_name`.

**Index when:**
- ✅ FK columns (speeds up JOINs)
- ✅ Frequent `WHERE` filter columns on large tables
- ✅ `ORDER BY` columns for large result sets

**Don't index when:**
- ❌ Small tables
- ❌ Very low cardinality columns (boolean, enum with 2–3 values)
- ❌ Columns rarely used in queries

---

## Section 14 — Views

```sql
-- Create view
CREATE VIEW active_orders AS
SELECT o.order_id, c.full_name, o.total_amount
FROM   orders o
JOIN   customers c ON o.customer_id = c.customer_id
WHERE  o.status = 'active';

-- Use it
SELECT * FROM active_orders WHERE total_amount > 100;

-- Update view definition
CREATE OR REPLACE VIEW active_orders AS ...;

-- Drop
DROP VIEW IF EXISTS active_orders;
```

**Use cases:** Simplify complex joins · Security (expose subset of columns) · Consistent business logic · Backward compatibility after table renames.

**Materialized view (PostgreSQL):** Stores the result physically; must be manually refreshed. Faster reads for expensive aggregations.
```sql
CREATE MATERIALIZED VIEW monthly_revenue AS SELECT ...;
REFRESH MATERIALIZED VIEW monthly_revenue;
```

---

## Section 15 — Stored Procedures and Functions

```sql
-- Stored procedure (MySQL)
DELIMITER //
CREATE PROCEDURE procedure_name(IN param1 type, OUT param2 type)
BEGIN
    DECLARE local_var type;
    -- SQL statements
END //
DELIMITER ;

-- Call it
CALL procedure_name(value, @out_param);
SELECT @out_param;

-- Stored function (returns a single value)
DELIMITER //
CREATE FUNCTION function_name(param type)
RETURNS return_type
DETERMINISTIC
BEGIN
    RETURN value;
END //
DELIMITER ;

-- Use in a query
SELECT function_name(column) FROM table;
```

**Procedure vs Function:**

| | Procedure | Function |
|---|---|---|
| Returns | OUT parameters | Single value |
| Called with | `CALL name(...)` | In SQL expression |
| Can contain | DML + transactions | Usually read-only |

---

## Section 16 — Triggers

```sql
-- BEFORE UPDATE trigger — auto-update timestamp
DELIMITER //
CREATE TRIGGER trg_products_updated_at
BEFORE UPDATE ON products
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END //
DELIMITER ;

-- AFTER DELETE trigger — audit log
CREATE TRIGGER trg_orders_audit
AFTER DELETE ON orders
FOR EACH ROW
BEGIN
    INSERT INTO orders_audit (order_id, deleted_at)
    VALUES (OLD.order_id, CURRENT_TIMESTAMP);
END //
```

**OLD vs NEW:**
- `OLD.col` — value before the change (UPDATE, DELETE)
- `NEW.col` — value after the change (INSERT, UPDATE)

**Timing:**

| | BEFORE | AFTER |
|---|---|---|
| Use for | Validate/modify incoming data | Audit logs, cascade side effects |
| Can modify `NEW` | ✅ Yes | ❌ No |

---

## Section 17 — Sequences and AUTO_INCREMENT

```sql
-- MySQL: AUTO_INCREMENT
CREATE TABLE orders (order_id INT AUTO_INCREMENT PRIMARY KEY, ...);

-- PostgreSQL: SERIAL (shorthand)
CREATE TABLE orders (order_id SERIAL PRIMARY KEY, ...);

-- PostgreSQL: GENERATED AS IDENTITY (SQL standard, preferred)
CREATE TABLE orders (
    order_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, ...
);

-- PostgreSQL: explicit sequence
CREATE SEQUENCE order_seq START 1000 INCREMENT 1;
SELECT nextval('order_seq');
```

---

## Section 18 — Transactions

```sql
START TRANSACTION;   -- or BEGIN (PostgreSQL)

-- ... DML statements ...

COMMIT;     -- make all changes permanent
-- or
ROLLBACK;   -- undo all changes since START TRANSACTION

-- Savepoints
SAVEPOINT point_name;
ROLLBACK TO SAVEPOINT point_name;  -- partial undo
RELEASE SAVEPOINT point_name;
```

**Pattern — transaction with error handling:**
```sql
START TRANSACTION;

UPDATE accounts SET balance = balance - 500 WHERE account_id = 101;
UPDATE accounts SET balance = balance + 500 WHERE account_id = 202;

-- If all good:
COMMIT;
-- If anything fails:
-- ROLLBACK;
```

---

## Section 19 — ACID Properties

| Property | Meaning |
|---|---|
| **A**tomicity | All operations succeed, or none do |
| **C**onsistency | Transaction moves DB from one valid state to another; all constraints satisfied |
| **I**solation | Concurrent transactions don't interfere with each other |
| **D**urability | Committed data survives crashes (Write-Ahead Log) |

---

## Section 20 — Isolation Levels

**Three read phenomena:**

| Phenomenon | Description |
|---|---|
| **Dirty Read** | Reading uncommitted data from another transaction |
| **Non-Repeatable Read** | Reading the same row twice gets different values |
| **Phantom Read** | Re-running a range query returns new rows |

**Isolation levels:**

| Level | Dirty | Non-Repeatable | Phantom | Default in |
|---|---|---|---|---|
| `READ UNCOMMITTED` | ❌ | ❌ | ❌ | (rare) |
| `READ COMMITTED` | ✅ | ❌ | ❌ | PostgreSQL, SQL Server |
| `REPEATABLE READ` | ✅ | ✅ | ❌ | MySQL InnoDB |
| `SERIALIZABLE` | ✅ | ✅ | ✅ | (manual selection) |

```sql
-- Set for current session (MySQL)
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- Set for one transaction (PostgreSQL)
BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

---

## Section 21 — Query Optimization and EXPLAIN

```sql
EXPLAIN SELECT * FROM orders WHERE status = 'active';
EXPLAIN ANALYZE SELECT ...;   -- PostgreSQL: shows actual execution time
EXPLAIN FORMAT=JSON SELECT ...; -- MySQL: detailed JSON output
```

**EXPLAIN key columns:**
- `type`: `const`/`eq_ref` (best) → `ref`/`range` → `index` → `ALL` (full scan — bad)
- `key`: index actually chosen
- `rows`: estimated rows scanned
- `Extra`: "Using filesort" or "Using temporary" = performance warnings

**Optimization patterns:**

| Anti-pattern | Fix |
|---|---|
| `WHERE YEAR(date_col) = 2024` | `WHERE date_col >= '2024-01-01' AND date_col < '2025-01-01'` |
| `WHERE name LIKE '%keyword%'` | Use FULLTEXT index or trailing wildcard: `'keyword%'` |
| `SELECT *` | Name only needed columns |
| Correlated subquery | Replace with `JOIN + GROUP BY` |
| OR on different columns | Use `UNION` of two indexed queries |

---

## Section 22 — Backup and Recovery Basics

```bash
# MySQL — logical backup
mysqldump -u root -p database_name > backup.sql
mysqldump -u root -p --all-databases > all_backup.sql

# MySQL — restore
mysql -u root -p database_name < backup.sql

# PostgreSQL — logical backup
pg_dump database_name > backup.sql
pg_dump -Fc database_name > backup.dump    # compressed

# PostgreSQL — restore
psql database_name < backup.sql
pg_restore -d database_name backup.dump
```

**Backup types:**

| Type | Description |
|---|---|
| **Full** | Complete snapshot of the entire database |
| **Incremental** | Only changes since last backup (binary logs / WAL) |
| **Logical** | SQL statements (portable, slower restore) |
| **Physical** | Raw data files (faster, same DB version required) |

**Best practices:**
- Automate backups — never rely on manual runs
- Test restores regularly — verify the backup is actually usable
- Store off-site (S3, separate region)
- Retain generations: daily → weekly → monthly
- Use Point-In-Time Recovery (PITR) for critical systems

---

## Section 23 — Common Mistakes and Fixes

| Mistake | Problem | Fix |
|---|---|---|
| `UPDATE t SET col = val` with no WHERE | Updates every row | Always include a WHERE clause |
| `DELETE FROM t` with no WHERE | Deletes every row | Always include a WHERE clause; use TRUNCATE if you mean to clear all |
| DDL in production without staging test | Table lock, data loss | Test DDL on staging first |
| Forgetting to index FK columns | JOIN becomes a full scan | `CREATE INDEX` on every FK column |
| Leading wildcard in LIKE `'%term'` | Can't use index | Use trailing wildcard or FULLTEXT index |
| Using `FLOAT` for money | Rounding errors accumulate | Use `DECIMAL(p,s)` |
| No `COMMIT` after transaction | Changes lost when session ends | Explicitly `COMMIT` every transaction |
| Storing passwords in plain text | Security disaster | Hash with bcrypt (covered in Day 29) |
| `SELECT *` in application code | Fragile, over-fetches | Name specific columns |

---

## Section 24 — Quick Reference Cheat Sheet

```sql
-- DDL
CREATE TABLE t (col type constraints, ..., [table constraints]);
ALTER TABLE t ADD COLUMN col type;
ALTER TABLE t DROP COLUMN col;
DROP TABLE IF EXISTS t;
TRUNCATE TABLE t;

-- DML
INSERT INTO t (col1, col2) VALUES (v1, v2);
UPDATE t SET col = val WHERE condition;   -- ← always WHERE
DELETE FROM t WHERE condition;            -- ← always WHERE

-- Index
CREATE INDEX idx_name ON t(col);
CREATE UNIQUE INDEX idx_name ON t(col);
DROP INDEX idx_name ON t;

-- View
CREATE [OR REPLACE] VIEW vname AS SELECT ...;
DROP VIEW IF EXISTS vname;

-- Transaction
START TRANSACTION;  -- or BEGIN
COMMIT;             -- or ROLLBACK;
SAVEPOINT name;
ROLLBACK TO SAVEPOINT name;
```

---

## Section 25 — Looking Ahead — Day 23

| Day 23 Topic | What It Covers |
|---|---|
| **HTTP protocol** | Methods (GET/POST/PUT/PATCH/DELETE), status codes, headers |
| **RESTful APIs** | Resource naming, statelessness, versioning |
| **Postman** | Testing and automating API requests |
| **Swagger/OpenAPI** | Documenting APIs with machine-readable specs |

> Day 22 built the database layer. Day 23 covers the API layer — how applications expose and consume that data over HTTP.
