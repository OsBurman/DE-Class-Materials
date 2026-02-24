# Day 22 Part 1 — DDL, Data Types, Constraints, Normalization & ER Diagrams
## Slide Descriptions

**Total slides: 16**

---

### Running Example Schema

Throughout Part 1 we build a normalized `library` database from scratch, evolving it slide by slide so students see DDL as a design process.

---

### Slide 1 — Title Slide

**Title:** SQL Database Design & Advanced Topics
**Subtitle:** DDL · Data Types · Constraints · Normalization · ER Diagrams
**Day:** Week 5 — Day 22 | Part 1 of 2

**Objectives listed on slide:**
- Write `CREATE`, `ALTER`, and `DROP` statements
- Choose the right SQL data types for each column
- Apply all six constraint types: `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `NOT NULL`, `CHECK`, `DEFAULT`
- Understand and apply 1NF, 2NF, and 3NF normalization
- Read and draw basic Entity-Relationship (ER) diagrams
- Apply data modeling principles and naming conventions

---

### Slide 2 — DDL Overview

**Title:** Data Definition Language — Defining the Structure

**Concept:** DDL commands define, modify, and remove database structures — they operate on the *schema*, not on the data.

**Three primary DDL statements:**

| Statement | Purpose |
|---|---|
| `CREATE` | Create a new database object (database, table, index, view) |
| `ALTER` | Modify an existing database object |
| `DROP` | Delete a database object and all its data |

**Additional DDL commands:**

| Statement | Purpose |
|---|---|
| `TRUNCATE` | Remove all rows from a table, keep the structure |
| `RENAME` | Rename a table or column (syntax varies by DB) |

**Key DDL characteristic:**
> DDL statements are **auto-committed** in most databases — they cannot be rolled back with a simple `ROLLBACK`. ALTER TABLE on a large production table can lock it and cause downtime. Always test DDL changes in a staging environment first.

**Code block:**
```sql
-- Typical workflow
CREATE DATABASE library;
USE library;                        -- MySQL; PostgreSQL uses \c library

CREATE TABLE authors (...);         -- define structure
ALTER TABLE authors ADD COLUMN ...;  -- evolve structure
DROP TABLE authors;                 -- permanent deletion
```

---

### Slide 3 — CREATE DATABASE and CREATE TABLE Structure

**Title:** CREATE DATABASE & TABLE Syntax

**Code block:**
```sql
-- Create and select the database
CREATE DATABASE library;
USE library;           -- MySQL / MariaDB
-- \c library          -- PostgreSQL

-- Basic CREATE TABLE syntax
CREATE TABLE table_name (
    column_name  data_type  [constraints],
    column_name  data_type  [constraints],
    ...
    [table_level_constraints]
);

-- First real table
CREATE TABLE authors (
    author_id   INT           NOT NULL AUTO_INCREMENT,
    first_name  VARCHAR(100)  NOT NULL,
    last_name   VARCHAR(100)  NOT NULL,
    birth_year  SMALLINT,
    nationality VARCHAR(50),
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (author_id)
);

-- IF NOT EXISTS — prevent error if table already exists
CREATE TABLE IF NOT EXISTS authors (...);
```

**Naming conventions table on slide:**

| Object | Convention | Example |
|---|---|---|
| Database | `snake_case` | `library`, `online_store` |
| Table | `snake_case`, plural | `authors`, `books`, `loan_items` |
| Column | `snake_case` | `first_name`, `created_at` |
| PK column | `table_singular_id` | `author_id`, `book_id` |
| FK column | matches referenced PK | `author_id` in books table |

---

### Slide 4 — SQL Data Types

**Title:** Choosing the Right Data Type

**Numeric types:**

| Type | Storage | Range / Use |
|---|---|---|
| `TINYINT` | 1 byte | –128 to 127 (or 0–255 UNSIGNED) |
| `SMALLINT` | 2 bytes | –32,768 to 32,767 |
| `INT` / `INTEGER` | 4 bytes | –2.1B to 2.1B — most common for IDs |
| `BIGINT` | 8 bytes | Very large numbers; use for high-volume IDs |
| `DECIMAL(p,s)` | Variable | Exact decimal — **always use for money** (e.g., `DECIMAL(10,2)`) |
| `FLOAT` / `DOUBLE` | 4 / 8 bytes | Approximate — avoid for money |

**String types:**

| Type | Use |
|---|---|
| `CHAR(n)` | Fixed-length strings (country codes, status flags) |
| `VARCHAR(n)` | Variable-length strings up to n characters — most common |
| `TEXT` | Long text (descriptions, notes) — not indexable easily |

**Date and time types:**

| Type | Stores | Example |
|---|---|---|
| `DATE` | Date only | `'2024-07-04'` |
| `TIME` | Time only | `'14:30:00'` |
| `DATETIME` | Date + time (no timezone) | `'2024-07-04 14:30:00'` |
| `TIMESTAMP` | Date + time (stored as UTC) | Auto-updates, good for `created_at` |

**Boolean / Other:**
```sql
BOOLEAN / TINYINT(1)  -- MySQL uses TINYINT(1); PostgreSQL has BOOLEAN
JSON                  -- native JSON storage (MySQL 5.7+, PostgreSQL)
ENUM('val1','val2')   -- fixed set of allowed string values (use sparingly)
```

**Design rule:** Choose the smallest type that fits your data. Prefer `DECIMAL` over `FLOAT` for money. Prefer `VARCHAR` over `TEXT` for short strings you need to index.

---

### Slide 5 — Constraints Part 1: PRIMARY KEY, NOT NULL, UNIQUE

**Title:** Constraints — Enforcing Data Integrity

**Concept:** Constraints are rules enforced by the database engine that prevent invalid data from ever being inserted or updated.

**PRIMARY KEY:**
```sql
-- Column-level (single column PK)
CREATE TABLE books (
    book_id  INT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ...
);

-- Table-level (composite PK — two or more columns together form the key)
CREATE TABLE book_authors (
    book_id    INT NOT NULL,
    author_id  INT NOT NULL,
    PRIMARY KEY (book_id, author_id)   -- neither column alone is unique
);
```

**NOT NULL:**
```sql
CREATE TABLE books (
    title     VARCHAR(300)  NOT NULL,   -- required field
    subtitle  VARCHAR(300),              -- optional — NULLs allowed
    isbn      CHAR(13)      NOT NULL
);
```

**UNIQUE:**
```sql
CREATE TABLE books (
    book_id  INT         PRIMARY KEY,
    isbn     CHAR(13)    NOT NULL UNIQUE,   -- column-level
    slug     VARCHAR(200) NOT NULL,
    UNIQUE (slug)                            -- table-level — same effect
);

-- UNIQUE allows one NULL (in most databases)
-- PRIMARY KEY = UNIQUE + NOT NULL
```

---

### Slide 6 — Constraints Part 2: FOREIGN KEY, CHECK, DEFAULT

**FOREIGN KEY — referential integrity:**
```sql
CREATE TABLE books (
    book_id    INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(300)  NOT NULL,
    author_id  INT           NOT NULL,
    genre_id   INT,                       -- nullable FK — book may have no genre
    price      DECIMAL(8,2)  NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors(author_id)
        ON DELETE RESTRICT                -- prevent deleting author with books
        ON UPDATE CASCADE,                -- if author_id changes, update here too
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
        ON DELETE SET NULL                -- deleting a genre → NULL the FK, keep book
);
```

**FK action options:**

| Action | Effect |
|---|---|
| `RESTRICT` | Prevent the parent row from being deleted/updated if children exist |
| `CASCADE` | Delete/update children automatically when parent changes |
| `SET NULL` | Set the FK column to NULL when parent is deleted/updated |
| `NO ACTION` | Similar to RESTRICT (checked at end of transaction) |

**CHECK — validate column values:**
```sql
CREATE TABLE books (
    price         DECIMAL(8,2)  NOT NULL CHECK (price >= 0),
    publish_year  SMALLINT      CHECK (publish_year BETWEEN 1450 AND 2100),
    rating        TINYINT       CHECK (rating BETWEEN 1 AND 5)
);
```

**DEFAULT — supply a fallback value:**
```sql
CREATE TABLE loans (
    loan_id     INT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    status      VARCHAR(20)  NOT NULL DEFAULT 'active',
    loaned_at   DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date    DATE      NOT NULL DEFAULT (CURRENT_DATE + INTERVAL 14 DAY)
);
```

---

### Slide 7 — ALTER TABLE

**Title:** ALTER TABLE — Evolving Your Schema

**Code block:**
```sql
-- Add a new column
ALTER TABLE books
    ADD COLUMN page_count  SMALLINT;

-- Add multiple columns at once
ALTER TABLE books
    ADD COLUMN language   VARCHAR(10) NOT NULL DEFAULT 'en',
    ADD COLUMN cover_url  VARCHAR(500);

-- Modify column type or constraint
ALTER TABLE books
    MODIFY COLUMN title  VARCHAR(500) NOT NULL;    -- MySQL
-- ALTER TABLE books ALTER COLUMN title TYPE VARCHAR(500);  -- PostgreSQL

-- Rename a column
ALTER TABLE books
    RENAME COLUMN cover_url TO cover_image_url;    -- MySQL 8+ / PostgreSQL

-- Drop a column
ALTER TABLE books
    DROP COLUMN cover_image_url;

-- Add a constraint after table creation
ALTER TABLE books
    ADD CONSTRAINT chk_price CHECK (price >= 0);

ALTER TABLE books
    ADD CONSTRAINT fk_genre
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id);

-- Drop a constraint
ALTER TABLE books
    DROP CONSTRAINT chk_price;            -- PostgreSQL
-- ALTER TABLE books DROP CHECK chk_price;  -- MySQL

-- Rename the table itself
RENAME TABLE books TO library_books;      -- MySQL
-- ALTER TABLE books RENAME TO library_books;  -- PostgreSQL
```

---

### Slide 8 — DROP and TRUNCATE

**Title:** DROP, TRUNCATE — Removing Data and Structure

**Code block:**
```sql
-- DROP TABLE — removes table AND all data permanently
DROP TABLE loans;

-- Safe version — no error if table doesn't exist
DROP TABLE IF EXISTS loans;

-- DROP DATABASE — removes everything in the database
DROP DATABASE library;

-- TRUNCATE TABLE — removes ALL rows, keeps the structure
TRUNCATE TABLE loans;

-- TRUNCATE vs DELETE vs DROP comparison:
-- DELETE FROM loans;          -- removes all rows, logged, can be rolled back
-- TRUNCATE TABLE loans;       -- removes all rows, minimal logging, CANNOT be rolled back
-- DROP TABLE loans;           -- removes rows + column definitions + indexes

-- Dependency order matters — must drop/truncate child tables first
DROP TABLE loan_items;    -- child table
DROP TABLE loans;         -- parent table
-- Or use CASCADE (PostgreSQL):
DROP TABLE loans CASCADE;
```

**Summary table on slide:**

| Command | Removes | Rollback | Speed |
|---|---|---|---|
| `DELETE FROM t` | All rows | ✅ Yes | Slow (logged per row) |
| `TRUNCATE TABLE t` | All rows | ❌ No | Fast (minimal log) |
| `DROP TABLE t` | Rows + structure | ❌ No | Immediate |

---

### Slide 9 — Introduction to Normalization

**Title:** Normalization — Why Schema Design Matters

**What is normalization?**
The process of organizing database tables to:
1. Eliminate **redundant data** (same data stored in multiple places)
2. Eliminate **update anomalies** (changing data in one place but not another)
3. Ensure **data dependencies make sense** (related data is stored together)

**Three types of anomalies that normalization eliminates:**

| Anomaly | Problem |
|---|---|
| **Insertion anomaly** | Can't add data without adding unrelated data |
| **Update anomaly** | Changing one fact requires updating multiple rows |
| **Deletion anomaly** | Deleting one row accidentally removes other facts |

**Unnormalized example — the problem:**

```
ORDERS table (bad design):
order_id | customer_name | customer_email     | product1 | qty1 | product2 | qty2
1001     | Alice Smith   | alice@mail.com     | Book A   | 2    | Book B   | 1
1002     | Bob Jones     | bob@mail.com       | Book C   | 1    | NULL     | NULL
```

Problems:
- What if Alice changes her email? Must update every order row
- Can't store a product unless there's an order for it
- Columns like `product1`, `product2` ... limit how many products per order

We will fix this step by step through 1NF → 2NF → 3NF.

---

### Slide 10 — First Normal Form (1NF)

**Title:** 1NF — Atomic Values & No Repeating Groups

**1NF Rules:**
1. Every column must contain **atomic (indivisible) values** — no lists, arrays, or sets in a single cell
2. Every column must contain values of a **single type**
3. Each row must be **uniquely identifiable** (primary key exists)
4. **No repeating column groups** (e.g., `product1`, `product2`, `product3`)

**Converting to 1NF:**
```
BEFORE (violates 1NF):
order_id | customer     | products                     | quantities
1001     | Alice Smith  | "Book A, Book B"             | "2, 1"
1002     | Bob Jones    | "Book C"                     | "1"

AFTER 1NF — each fact gets its own row:
order_id | customer     | product  | quantity
1001     | Alice Smith  | Book A   | 2
1001     | Alice Smith  | Book B   | 1
1002     | Bob Jones    | Book C   | 1
```

**SQL implementation:**
```sql
-- 1NF compliant: one value per cell, PK on (order_id, product)
CREATE TABLE order_lines_1nf (
    order_id    INT          NOT NULL,
    customer    VARCHAR(100) NOT NULL,
    product     VARCHAR(200) NOT NULL,
    quantity    INT          NOT NULL,
    PRIMARY KEY (order_id, product)
);
```

**Still has problems:** `customer` is repeated for every product on an order. If Alice updates her name, every row must be updated → update anomaly. That's 2NF's job.

---

### Slide 11 — Second Normal Form (2NF)

**Title:** 2NF — Eliminate Partial Dependencies

**2NF Rules:**
1. Must already be in 1NF
2. Every non-key column must depend on the **entire primary key**, not just part of it

**Only applies when the primary key is composite (two or more columns).**

**The problem with our 1NF table:**

Primary key: `(order_id, product)`

- `quantity` → depends on both `order_id` AND `product` ✅ (full dependency)
- `customer` → depends only on `order_id`, not on `product` ❌ (partial dependency)

**Fix — split into separate tables:**

```
orders table: (order_id PK, customer)
order_lines table: (order_id FK, product, quantity) — PK = (order_id, product)
```

```sql
CREATE TABLE orders_2nf (
    order_id   INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customer   VARCHAR(100) NOT NULL
);

CREATE TABLE order_lines_2nf (
    order_id  INT          NOT NULL,
    product   VARCHAR(200) NOT NULL,
    quantity  INT          NOT NULL,
    PRIMARY KEY (order_id, product),
    FOREIGN KEY (order_id) REFERENCES orders_2nf(order_id)
);
```

**Still has a problem:** `customer` is a plain string — two customers named "Alice Smith" can't be distinguished. And what if a product name appears in multiple orders — its details (price, category) are missing. That's 3NF's territory.

---

### Slide 12 — Third Normal Form (3NF)

**Title:** 3NF — Eliminate Transitive Dependencies

**3NF Rules:**
1. Must already be in 2NF
2. Every non-key column must depend **directly on the primary key**, not on another non-key column

**Transitive dependency example:**

In `orders`: `order_id → customer_zip → customer_city`

- `customer_zip` depends on `order_id` (fine)
- `customer_city` depends on `customer_zip`, not on `order_id` directly ❌

Fix: move `customer_zip → customer_city` to its own table (zip codes lookup).

**Evolving to fully normalized tables:**

```sql
-- customers table — no more name strings in orders
CREATE TABLE customers (
    customer_id  INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    full_name    VARCHAR(200) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE
);

-- products table — product details live here, not in order_lines
CREATE TABLE products (
    product_id  INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(300)  NOT NULL,
    price       DECIMAL(8,2)  NOT NULL CHECK (price >= 0)
);

-- orders — customer reference only
CREATE TABLE orders (
    order_id    INT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customer_id INT  NOT NULL,
    order_date  DATE NOT NULL DEFAULT (CURRENT_DATE),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- order_lines — junction between orders and products
CREATE TABLE order_lines (
    order_id   INT           NOT NULL,
    product_id INT           NOT NULL,
    quantity   INT           NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(8,2)  NOT NULL,   -- price at time of order — intentionally denormalized
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id)   REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
```

**Quick reference:**

| Normal Form | Eliminates |
|---|---|
| 1NF | Repeating groups, non-atomic values |
| 2NF | Partial dependencies on composite PK |
| 3NF | Transitive dependencies (non-key → non-key) |

---

### Slide 13 — ER Diagrams — Entities and Attributes

**Title:** Entity-Relationship Diagrams

**What is an ER diagram?** A visual blueprint of a database schema showing entities (tables), their attributes (columns), and how they relate to each other.

**ER notation elements:**

| Symbol | Represents |
|---|---|
| Rectangle | Entity (table) |
| Ellipse / listed attribute | Attribute (column) |
| Underlined attribute | Primary key |
| Diamond | Relationship |
| Line | Connection between entity and relationship |

**Crow's Foot notation (most common in tools):**

```
──────|    one (exactly one)
──────<    many
────|──    one and only one (mandatory)
────○──    zero or one (optional)
────<──    one or many
────○<─    zero or many
```

**Simple entity notation for our library schema:**

```
┌──────────────────┐          ┌──────────────────┐
│    customers     │          │     orders       │
├──────────────────┤          ├──────────────────┤
│ *customer_id PK  │  1    *  │ *order_id PK     │
│  full_name       │──────────│  customer_id FK  │
│  email UNIQUE    │          │  order_date      │
└──────────────────┘          └──────────────────┘
```

**Cardinality examples:**
- One customer → zero or many orders (`1 ─○<`)
- One order → one or many order_lines (`1 ─|<`)
- One product → zero or many order_lines (`1 ─○<`)

---

### Slide 14 — ER Diagrams — Full Schema Diagram

**Title:** Complete Library ER Diagram

**Full schema text-based ER diagram:**

```
┌──────────────┐     ┌──────────────┐     ┌────────────────┐
│  customers   │     │    orders    │     │  order_lines   │
├──────────────┤     ├──────────────┤     ├────────────────┤
│ customer_id  │─┐   │ order_id     │─┐   │ order_id  FK   │
│ full_name    │ │1  │ customer_id  │ │1  │ product_id FK  │
│ email        │ └──<│ order_date   │ └──<│ quantity       │
└──────────────┘     └──────────────┘     │ unit_price     │
                                          └───────┬────────┘
                                                  │*
┌──────────────┐                         ┌────────┴────────┐
│   genres     │                         │    products     │
├──────────────┤                         ├─────────────────┤
│ genre_id     │─────────────────────────│ product_id      │
│ name         │*                       1│ title           │
└──────────────┘                         │ price           │
                                         │ genre_id FK     │
                                         └─────────────────┘
```

**Reading the diagram:**
- One customer has zero or more orders (1 → *)
- One order has one or more order_lines (1 → *)
- One product appears in zero or more order_lines (* ← 1)
- One genre has zero or more products (1 → *)

**ER diagram tools:** Draw.io (free), Lucidchart, dbdiagram.io, MySQL Workbench, DBeaver (auto-generates from existing schema), pgAdmin (PostgreSQL).

---

### Slide 15 — Data Modeling Principles

**Title:** Data Modeling Best Practices

**Naming conventions:**
```sql
-- Tables: snake_case, plural nouns
customers, orders, order_lines, products, genres

-- PKs: singular_table_id
customer_id, order_id, product_id

-- FKs: same name as the referenced PK
-- orders.customer_id references customers.customer_id (same name)

-- Boolean columns: is_active, has_subscription, is_deleted (soft delete)

-- Timestamps: created_at, updated_at (DATETIME or TIMESTAMP)
```

**Surrogate keys vs natural keys:**

| | Surrogate Key | Natural Key |
|---|---|---|
| What it is | System-generated ID (INT AUTO_INCREMENT, UUID) | Real-world identifier (email, SSN, ISBN) |
| Pros | Stable, never changes, simpler JOINs | Meaningful, no extra column needed |
| Cons | No intrinsic meaning | Changes in real world → cascading updates |
| **Use when** | Always, for most tables | Lookup/reference tables (e.g., ISO country codes) |

**Soft delete pattern:**
```sql
-- Instead of DELETE, mark rows as deleted
ALTER TABLE customers
    ADD COLUMN is_deleted    BOOLEAN  NOT NULL DEFAULT FALSE,
    ADD COLUMN deleted_at    DATETIME;

-- Query active customers only
SELECT * FROM customers WHERE is_deleted = FALSE;

-- This preserves history and FK integrity
```

**Key principles:**
- Store each fact **once** (normalization)
- Every table needs a clear, single-purpose PK
- Prefer explicit `NOT NULL` constraints over relying on application code
- Avoid `SELECT *` — name columns explicitly in application queries
- Document your schema with comments: `COMMENT 'unit_price at time of purchase — intentionally denormalized'`

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Complete — DDL Reference

**DDL quick reference:**
```sql
-- Create
CREATE TABLE t (col type [constraints], ..., [table constraints]);
CREATE DATABASE db;

-- Alter
ALTER TABLE t ADD COLUMN col type;
ALTER TABLE t MODIFY COLUMN col new_type;
ALTER TABLE t DROP COLUMN col;
ALTER TABLE t ADD CONSTRAINT name type (...);

-- Remove
DROP TABLE IF EXISTS t;
TRUNCATE TABLE t;
DROP DATABASE db;
```

**All six constraints:**

| Constraint | Purpose | Example |
|---|---|---|
| `PRIMARY KEY` | Unique, not null row identifier | `PRIMARY KEY (id)` |
| `FOREIGN KEY` | Enforce relationship to another table | `FOREIGN KEY (author_id) REFERENCES authors(author_id)` |
| `UNIQUE` | No duplicate values (nulls allowed) | `UNIQUE (email)` |
| `NOT NULL` | Column must always have a value | `name VARCHAR(100) NOT NULL` |
| `CHECK` | Validate column value against a condition | `CHECK (price >= 0)` |
| `DEFAULT` | Supply a fallback when value is omitted | `DEFAULT CURRENT_TIMESTAMP` |

**Normal form summary:**

| Form | Rule |
|---|---|
| 1NF | Atomic values, no repeating groups, has PK |
| 2NF | + No partial dependencies on composite PK |
| 3NF | + No transitive dependencies (non-key → non-key) |

**Up next — Part 2:** Writing data with `INSERT`, `UPDATE`, `DELETE` · Indexes · Views · Stored procedures · Triggers · Transactions · ACID · Isolation levels · Query optimization
