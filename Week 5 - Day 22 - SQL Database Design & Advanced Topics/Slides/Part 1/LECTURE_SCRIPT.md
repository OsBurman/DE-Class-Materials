# Day 22 Part 1 — DDL, Data Types, Constraints, Normalization & ER Diagrams
## Lecture Script — 60 Minutes

---

**Before class:** Have a SQL client open and connected to a local database. You'll live-type the `CREATE TABLE` and `ALTER TABLE` examples. Pre-load the unnormalized orders table on the board or slide for the normalization walkthrough.

---

## [00:00–03:00] Opening — From Querying Data to Defining It

Welcome back. Yesterday was all about reading data — SELECT, WHERE, JOINs, aggregates. You got very good at asking questions of a database that already exists.

Today we answer a different question: how does that database get built in the first place? Who creates the tables? Who decides that `price` should be `DECIMAL(8,2)` instead of a string? Who sets the rule that you can't insert an order for a customer that doesn't exist? That's what today is about.

Part 1 covers the schema side — Data Definition Language, data types, constraints, normalization, and ER diagrams. Part 2 covers the data side — inserting and modifying data, indexes, views, stored procedures, transactions, and query optimization.

By the end of the day, you won't just be able to query a database — you'll be able to design and build one from scratch. Let's get into it.

---

## [03:00–10:00] Slides 2–3: DDL and CREATE TABLE

### What is DDL? [Slide 2]

Data Definition Language — DDL — is the subset of SQL that defines structure. The three primary commands are CREATE, ALTER, and DROP.

Here's the mental model: when you write SELECT, you're asking the database a question about its data. When you write CREATE TABLE, you're handing the database a blueprint and saying "build this structure." DDL is about blueprints.

One critical difference from the SELECT queries you wrote yesterday: DDL is auto-committed in most databases. That means when you run `DROP TABLE`, it happens immediately and permanently. You can't roll it back with a simple ROLLBACK. In a production environment, DDL changes on large tables can also cause table locks — the table becomes unavailable while the change applies. Always test DDL on a staging database before running it in production. I want that instinct in your head from day one.

The four DDL commands in our toolbox today are CREATE, ALTER, DROP, and TRUNCATE. Let's start building.

### CREATE TABLE [Slide 3]

The syntax is straightforward. You give the table a name, then inside parentheses you list each column with its name, its data type, and any constraints. Let me type the first example live.

`CREATE TABLE authors` — open paren — `author_id INT NOT NULL AUTO_INCREMENT` — comma — `first_name VARCHAR(100) NOT NULL` — comma — `last_name VARCHAR(100) NOT NULL` — comma — `birth_year SMALLINT` — note no NOT NULL, so this is optional — `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP` — then at the table level: `PRIMARY KEY (author_id)` — close paren, semicolon.

Run it. Table created.

A few naming conventions to lock in now. Tables: `snake_case`, plural noun — `authors`, `books`, `loan_items`. Primary key column: singular table name followed by `_id` — so the PK in the `authors` table is `author_id`. Foreign key columns: the same name as the PK they reference — so the `author_id` column in the `books` table has the same name as `author_id` in `authors`. Consistent naming makes your JOINs readable and your schema self-documenting.

---

## [10:00–20:00] Slides 4–6: Data Types and Constraints 1–2

### Data Types [Slide 4]

Choosing the right data type matters for three reasons: storage efficiency, data integrity, and query performance. Let me walk through the categories.

**Numeric types.** `INT` is your workhorse — four bytes, handles up to about 2.1 billion. That's plenty for most IDs. If you're building something at Twitter-scale with billions of rows, use `BIGINT`. If you need a small integer — like a rating between 1 and 5, or a year — use `SMALLINT` or even `TINYINT` to save space.

The most important rule in the number category: **always use `DECIMAL` for money**. Never `FLOAT` or `DOUBLE`. A FLOAT is an approximate number — it can't represent 0.1 exactly in binary. If you store a price as FLOAT and do arithmetic on it, you'll get rounding errors that compound over millions of transactions. `DECIMAL(10,2)` means ten digits total, two after the decimal point — perfect for a price like `99999999.99`.

**String types.** `VARCHAR(n)` is what you'll use most. It stores up to n characters but only uses as much space as the actual value. `CHAR(n)` is fixed-length — use it for things that are always the same length, like a two-letter country code or a fixed-format status flag. `TEXT` is for long content like descriptions or notes — it can't be fully indexed, so don't use it for columns you'll frequently search on.

**Date and time.** `DATE` is just a date — `2024-07-04`. `DATETIME` includes time. `TIMESTAMP` is also date plus time, but it stores the value as UTC and can auto-update on insert — great for `created_at` and `updated_at` columns.

### Constraints — PRIMARY KEY, NOT NULL, UNIQUE [Slide 5]

Constraints are rules the database enforces on your data. They live in the schema, which means they run before any application code touches the data. A constraint is the last line of defense against bad data.

`PRIMARY KEY` is the most important. Every table should have one. It uniquely identifies each row and cannot be NULL. You can define it at the column level for a single-column PK — `book_id INT PRIMARY KEY` — or at the table level for a composite PK. Composite PKs make sense for junction tables — like `book_authors`, where the combination of `book_id` and `author_id` together is unique, even though neither column alone is.

`NOT NULL` is simple: this column must always have a value. Apply it to any column where a missing value doesn't make sense. A book without a title is meaningless. An order without a customer is meaningless. Be deliberate — if you make a column nullable, you're explicitly saying "this can be unknown."

`UNIQUE` says no two rows can have the same value in this column. An ISBN should be unique. An email address should be unique. The difference from PRIMARY KEY is that UNIQUE allows one NULL — because NULL is "unknown", and two unknowns can't be confirmed to be duplicates. PRIMARY KEY is UNIQUE plus NOT NULL together.

### Constraints — FOREIGN KEY, CHECK, DEFAULT [Slide 6]

`FOREIGN KEY` is how you enforce relationships between tables. If `books.author_id` has a foreign key referencing `authors.author_id`, then you cannot insert a book with an author_id that doesn't exist in the authors table. And you can't delete an author who still has books — unless you configure what happens.

The `ON DELETE` and `ON UPDATE` clauses control that. `RESTRICT` is the safest: prevent the parent from being deleted if children exist. `CASCADE` automatically deletes children when the parent is deleted — useful for cleanup, dangerous if misused. `SET NULL` sets the FK to NULL when the parent goes away — appropriate when the child can exist independently. Pick the right option thoughtfully.

`CHECK` constrains the value of a column based on a condition. `CHECK (price >= 0)` prevents negative prices. `CHECK (rating BETWEEN 1 AND 5)` enforces a valid rating scale. CHECK constraints run on every INSERT and UPDATE — if the condition fails, the row is rejected.

`DEFAULT` provides a fallback value when you don't specify one. `DEFAULT CURRENT_TIMESTAMP` on a `created_at` column means you never have to manually supply the creation time — the database does it automatically. `DEFAULT 'active'` on a status column means new rows start in the active state.

These six constraints — PRIMARY KEY, FOREIGN KEY, UNIQUE, NOT NULL, CHECK, DEFAULT — are your toolkit for enforcing data integrity at the database level. Use them. Don't rely solely on application code to keep your data clean.

---

## [20:00–29:00] Slides 7–8: ALTER TABLE and DROP/TRUNCATE

### ALTER TABLE [Slide 7]

Schemas aren't static. You'll add columns, change types, add constraints, rename things. `ALTER TABLE` is your tool for all of that.

Adding a column: `ALTER TABLE books ADD COLUMN page_count SMALLINT`. The new column is added to every existing row — it will be NULL for all of them unless you specify a DEFAULT. If you add a NOT NULL column to a table with existing data and no DEFAULT, most databases will reject it because existing rows can't satisfy the NOT NULL constraint. Solution: add the column as nullable first, populate the data, then add the NOT NULL constraint.

Modifying a column type: `MODIFY COLUMN` in MySQL, `ALTER COLUMN` in PostgreSQL. Be careful — widening a VARCHAR is safe. Narrowing it, or changing a string to an integer, can fail or truncate data.

Adding a constraint after the fact: `ALTER TABLE books ADD CONSTRAINT chk_price CHECK (price >= 0)`. You can name your constraints, which makes them easier to drop later. Always name your constraints — `fk_books_author`, `chk_price_positive` — rather than letting the database generate a name.

Dropping a column is permanent. No warning, no confirmation. Before you drop a column in production, make sure no application code references it. If you have any doubt, rename it first and run the system for a few days.

### DROP and TRUNCATE [Slide 8]

Three operations for removing data — and they're very different.

`DELETE FROM loans` removes all rows but logs each deletion. It can be rolled back. It's slow on large tables because every row is individually logged. Use it when you need to selectively remove rows, or when you need the safety of rollback.

`TRUNCATE TABLE loans` removes all rows in one fast operation. It resets the AUTO_INCREMENT counter back to 1. It cannot be rolled back in most databases. Use it when you want to clear a table completely — like resetting test data.

`DROP TABLE loans` removes the rows AND the structure — column definitions, indexes, constraints, everything. After DROP, the table doesn't exist anymore.

The dependency order matters: you must drop or truncate child tables before parent tables. If `order_lines` has a FK to `orders`, you can't drop `orders` while `order_lines` exists. Drop `order_lines` first. PostgreSQL's `CASCADE` option handles this automatically, but use it with care.

---

## [29:00–45:00] Slides 9–12: Normalization

### Why Normalization? [Slide 9]

Let me show you what happens when you don't normalize. I'll put an unnormalized orders table on the screen.

Columns: `order_id`, `customer_name`, `customer_email`, `product1`, `qty1`, `product2`, `qty2`. And maybe `product3`, `qty3`... where does it stop? This is called a "wide table" design and it has three categories of problems.

**Insertion anomaly:** You can't record a product until someone orders it. The product data lives inside the order row — there's no separate products table.

**Update anomaly:** Alice Smith changes her email address. How many rows do you update? Every row that contains an order by Alice. If you miss one, you now have inconsistent data — Alice has two email addresses in your database.

**Deletion anomaly:** You cancel order 1001. You delete that row. If it was the only order that contained "Book A", the information that "Book A" exists is now gone from your database. Deleting an order accidentally deleted a product record.

Normalization eliminates all three of these. Let's walk through the three normal forms.

### 1NF [Slide 10]

First Normal Form has one core rule: every cell must contain an atomic value — one thing, not a list, not a group.

Our table has `product1`, `product2`, `product3` — that's a repeating group. It also has some cells where products are stored as a comma-separated string — that violates atomicity.

To achieve 1NF, we expand the repeating groups into individual rows. Instead of one row per order with multiple product columns, we have one row per order-product combination. The table now has: `order_id`, `customer`, `product`, `quantity`. Customer Alice with two products gets two rows.

We also need a primary key. The combination of `(order_id, product)` uniquely identifies each row.

Better. But notice customer's name is now repeated for every row of the same order. If Alice changes her name, we still have the update anomaly. 1NF solved the repeating group problem but introduced a new issue. That's 2NF's job.

### 2NF [Slide 11]

Second Normal Form: in a table with a composite primary key, every non-key column must depend on the **entire** primary key — not just part of it.

Our PK is `(order_id, product)`. Ask for each non-key column: does it depend on order_id alone, or on product alone, or only on the combination?

`quantity` — depends on both. If you change the order or change the product, the quantity might change. That's a full dependency. ✅

`customer` — depends only on `order_id`. Knowing the product doesn't tell you anything about who placed the order. That's a partial dependency. ❌

Fix: split. Take `customer` out and put it in its own `orders` table with `order_id` as the PK. The `order_lines` table keeps `(order_id, product, quantity)`. Now `orders` has a foreign key relationship to `order_lines`.

Update anomaly solved: Alice's name lives in exactly one row in the `orders` table.

### 3NF [Slide 12]

Third Normal Form: every non-key column must depend directly on the primary key — not on another non-key column.

This is called eliminating transitive dependencies. A transitive dependency looks like: `order_id → zip_code → city`. The city depends on the zip code, not on the order ID directly.

In our schema, the deeper issue is that `customer` is still a plain name string. What we need is a `customers` table with a `customer_id` PK. Then `orders` stores `customer_id` (FK) instead of the name. Now customer details — name, email, address — live in one place.

Same logic for products. The product name should live in a `products` table. The `order_lines` table stores `product_id` (FK) and `quantity`, and also `unit_price` — which we intentionally store here because the price at time of purchase might differ from the current product price. That's a deliberate, documented denormalization.

And now we have our fully normalized schema: `customers`, `products`, `orders`, `order_lines`. Every fact stored once. No update anomalies. No insertion anomalies. No deletion anomalies.

The normalization ladder: 1NF removes repeating groups and non-atomic values. 2NF removes partial dependencies on composite keys. 3NF removes transitive dependencies between non-key columns. Most production schemas target 3NF. There are higher normal forms — BCNF, 4NF, 5NF — but they're beyond today's scope and rarely needed in practice.

---

## [45:00–57:00] Slides 13–15: ER Diagrams and Data Modeling

### ER Diagrams [Slides 13–14]

An Entity-Relationship diagram is a visual map of your schema before you write any SQL. It's the thing you draw on a whiteboard before you write a single CREATE TABLE statement.

Three building blocks: entities (tables), attributes (columns), and relationships.

In the notation we'll use — called Crow's Foot notation, because the "many" end of a relationship looks like a crow's foot — you represent entities as rectangles with their column names listed inside. Relationships are shown as lines connecting entities. The symbols at each end of the line indicate cardinality: a straight vertical bar means "exactly one," a circle means "zero or one," and the forked symbol means "many."

How to read the diagram on the slide: a single bar on the customer end and a fork on the orders end — one customer to many orders. A circle indicates the relationship is optional from that side — a customer can exist with zero orders, but each order must have exactly one customer.

Why draw the diagram before writing SQL? Because it's much cheaper to erase a line on a whiteboard than to DROP TABLE and rebuild your schema after you've already populated it with data. ER diagrams let you spot missing tables, wrong cardinality, and forgotten relationships before you write a single line of DDL.

Tools: dbdiagram.io lets you type your schema as text and see the ER diagram instantly — great for quick design. MySQL Workbench and DBeaver can both generate ER diagrams from an existing database — useful for understanding a schema you inherited.

### Data Modeling Principles [Slide 15]

Quick tour of the principles that separate good schema design from bad.

Use surrogate keys for almost everything. An `INT AUTO_INCREMENT` or UUID primary key is stable — it never changes. A natural key like an email address or a phone number seems convenient until the person changes their email. Then every table with that FK needs to update. Surrogate keys avoid cascading updates.

Soft delete: instead of `DELETE`ing a row, add an `is_deleted BOOLEAN` column and set it to TRUE. This preserves history, maintains FK integrity, and allows recovery if someone deletes something by mistake. The tradeoff is that you need to add `WHERE is_deleted = FALSE` to all your queries.

Always add `created_at` and `updated_at` timestamps to every table. You will thank yourself when you need to audit what changed and when.

Apply constraints at the database level, not just in your application. Your application code will have bugs. New team members will write scripts that bypass the application layer. The database is the source of truth — let it enforce the rules.

---

## [57:00–60:00] Slide 16: Summary

Let me give you the Part 1 summary in thirty seconds.

DDL defines structure: CREATE, ALTER, DROP. Data types matter — DECIMAL for money, appropriate sizes for everything. Six constraints enforce integrity at the database level — PRIMARY KEY, FOREIGN KEY, UNIQUE, NOT NULL, CHECK, DEFAULT. Normalization removes redundancy: 1NF for atomic values, 2NF for full dependency on composite keys, 3NF for no transitive dependencies. ER diagrams are blueprints — draw them before you build.

Take a five-minute break. In Part 2 we fill the tables with data using DML, and then get into indexes, views, stored procedures, transactions, and query optimization.

---

*End of Part 1 — 60 minutes*
