# Day 22 — Part 1 Walkthrough Script
# SQL Database Design: DDL, Constraints, Normalization & ER Diagrams

**Files referenced:**
- `01-ddl-create-alter-drop.sql`
- `02-normalization-and-er-design.sql`

**Total time:** ~90 minutes  
**Session split:** DDL & Constraints (45 min) → Normalization & ER Design (45 min)

---

## Segment 1 — Day 22 Opener: From Querying to Designing (5 min)

> "Good morning. Yesterday we learned how to READ from a database. SELECT, WHERE, GROUP BY, JOINs. That's important — but today we take a step back and ask a different question: how do you build a database in the first place?"

> "A bad database design causes problems that no amount of clever SQL can fix. Querying the wrong data model is like trying to navigate with a broken map — the harder you try, the more lost you get."

> "Today we cover two related skills: DDL — the SQL commands that create and modify database structure — and normalization — the theory and practice that tells you WHAT structure to create."

*Open `01-ddl-create-alter-drop.sql`*

---

## BLOCK A — DDL, Data Types & Constraints

---

## Segment 2 — DDL Overview (5 min)

*Navigate to Section 1*

> "SQL is split into sub-languages. Yesterday was DQL — Data Query Language, just SELECT. Today starts with DDL — Data Definition Language. DDL commands define and modify the STRUCTURE of the database."

> "The four key DDL commands: CREATE, ALTER, DROP, and TRUNCATE. Let's look at what each does."

> "CREATE makes something new — a database, a table, an index, a view. ALTER modifies something that already exists — add a column, rename a column, change a constraint. DROP permanently deletes something. TRUNCATE removes all rows from a table but keeps the structure."

> "**Watch out:** DDL is AUTO-COMMITTED in most databases. That means the moment you run `DROP TABLE`, it's gone. No ROLLBACK. In MySQL, SQL Server, or Oracle, that DROP is permanent. PostgreSQL is the exception — you can wrap DDL in a transaction and roll it back. But don't rely on that — treat every DROP as irreversible."

---

## Segment 3 — Creating a Database (3 min)

*Navigate to Section 2*

> "Creating a database is usually done once by a DBA or during app setup. The key parameter is encoding — always UTF8 in production. This ensures you can store international characters: accented letters, Chinese, Arabic, emoji."

> "The connection command `\c ecommerce` is a psql client command, not SQL. It switches your active connection to that database. In pgAdmin you just double-click the database name."

> "For our walkthrough today we're using the current database and creating tables inside it. Run the DROP TABLE block at the top — those `IF EXISTS` clauses protect you if the tables don't exist yet."

---

## Segment 4 — SQL Data Types (8 min)

*Navigate to Section 3 comment block*

> "Before we create tables, let's talk about data types. Choosing the right data type matters for storage efficiency, query performance, and data integrity."

> "**Numeric types:** For whole numbers, use INTEGER for most things — IDs, counts, quantities. BIGINT if you expect to exceed 2 billion rows. SMALLINT for small ranges — enum-like values, display order. SERIAL is PostgreSQL's shorthand for 'auto-incrementing integer' — it creates a sequence behind the scenes."

> "**NUMERIC(precision, scale):** use this for money. NEVER store currency in FLOAT or REAL — floating point has rounding errors. NUMERIC(10, 2) stores up to 8 digits before the decimal and exactly 2 after. $9,999,999.99 is the max — that covers most pricing scenarios."

> "**Text types:** VARCHAR(n) is the most common — variable-length with an upper limit. TEXT is unlimited length in PostgreSQL — useful for descriptions, addresses, long content. CHAR(n) pads with spaces to a fixed width — mostly a legacy type, rarely needed in new code."

> "**Date and time:** DATE for dates only. TIMESTAMP for date + time without timezone. TIMESTAMPTZ — date + time WITH timezone awareness — this is the production standard. Always store timestamps with timezone in your tables. When users in different countries use your system, TIMESTAMPTZ ensures times are stored in UTC and displayed in each user's local time."

> "**Boolean:** TRUE or FALSE. Watch out — SQL booleans can also be NULL, which means 'unknown'. `WHERE is_active = TRUE` won't return rows where is_active is NULL."

> "**UUID:** universally unique identifier. 128-bit random value. Useful as a primary key when you don't want sequential IDs (which expose how many records you have and can be guessed). Spring Boot generates these with UUID.randomUUID()."

> "Quick question: if I'm storing a product description that could be one sentence or ten paragraphs, which type should I use?"

*(Expected: TEXT — variable, no known upper limit)*

---

## Segment 5 — CREATE TABLE and Constraints (15 min)

*Navigate to Section 4*

> "Now let's build real tables. We're building an e-commerce schema: customers, categories, products, orders, and order_items."

*Run Section 4a — customers table*

> "Read through the customers table with me. Every column has a type AND a constraint."

> "PRIMARY KEY — the most important constraint. It enforces uniqueness AND NOT NULL. Every table should have a primary key. SERIAL generates the next integer automatically."

> "NOT NULL — this column must always have a value. Watch out: an empty string '' satisfies NOT NULL. If you want to prevent empty strings too, use a CHECK constraint: `CHECK (LENGTH(TRIM(first_name)) > 0)`."

> "UNIQUE — no two rows can have the same value. Email addresses must be unique per customer. The database enforces this with an internal index."

> "DEFAULT — the value to use when none is provided. `DEFAULT NOW()` sets created_at to the current timestamp automatically. You don't need to pass it in your INSERT."

> "CHECK — a custom validation rule. `CHECK (status IN ('active', 'inactive', 'suspended'))` means the database itself will reject any other value. Not just your application code — the DATABASE. This is your last line of defense."

*Navigate to Section 4c — products table*

> "Products introduces FOREIGN KEY. The `category_id` column references `categories.category_id`. The database enforces this: you CANNOT insert a product with a category_id that doesn't exist in categories."

> "`ON DELETE RESTRICT` means: if you try to delete a category that has products, the database will refuse. The category is protected by its products."

> "Compare that to `ON DELETE CASCADE` on orders — if you delete a customer, their orders are deleted automatically. Different business rule, different ON DELETE behavior."

*Navigate to Section 4d — orders table with named constraints*

> "Named constraints. Notice `CONSTRAINT chk_orders_status CHECK (...)`. Giving constraints names makes error messages readable. Without a name, the error would say 'violates constraint 23843_18_1_not_null'. With a name, it says 'violates constraint chk_orders_status'. Much easier to debug."

> "The multi-column CHECK at the bottom is powerful: `shipped_date IS NULL OR shipped_date >= order_date`. This enforces a business rule — an order can't ship before it was placed — directly in the database."

*Navigate to Section 4e — order_items*

> "order_items has a COMPOSITE PRIMARY KEY: `PRIMARY KEY (order_id, product_id)`. This means the combination must be unique. Order 1 can have product 5. Order 2 can also have product 5. But order 1 cannot have product 5 twice."

> "Quick question: why do we store `unit_price` in order_items instead of just looking up `products.price`?"

*(Expected: because product prices change over time — historical orders must preserve the price at the time of purchase)*

---

## Segment 6 — ALTER TABLE (8 min)

*Navigate to Section 5*

> "Tables evolve as requirements change. ALTER TABLE is how you modify an existing table without dropping and recreating it."

*Run through 5a–5k, narrating each*

> "5a: Add a new column. The column is added to every existing row with its default value — if no default, it's NULL."

> "5d and 5e: Removing a default and setting NOT NULL. If you want to add a NOT NULL column to a table that already has data, you have to: first add the column as nullable, then fill in the data, then add the NOT NULL constraint. You can't add a NOT NULL column to a non-empty table in one step — because the existing rows would have NULL in that column."

> "5f: Renaming a column — clean, simple. Your ORM and application code will need to be updated too."

> "5h: Adding a named constraint after creation — this is how you add validation to an existing table without recreating it. The database checks all existing rows against the new constraint when you add it."

> "**Watch out:** On large production tables, ALTER TABLE can lock the table for the duration of the operation — no reads or writes allowed. For big tables, use a tool like pg_repack or online schema change tools. Something to be aware of for production deployments."

---

## Segment 7 — DROP and TRUNCATE (4 min)

*Navigate to Section 6*

> "DROP removes the object entirely. TRUNCATE removes all data but keeps the structure."

> "`IF EXISTS` is your friend — prevents errors when the table might not exist yet. Always use it in setup scripts."

> "CASCADE is dangerous. `DROP TABLE categories CASCADE` will also drop every table that has a foreign key pointing to categories — products, for example. And products' orders. And those orders' order_items. One command can destroy your entire database."

> "TRUNCATE vs DELETE: TRUNCATE is faster because it doesn't process rows individually. It also resets SERIAL counters with `RESTART IDENTITY`. DELETE is slower but more surgical — you can add a WHERE clause. DELETE also fires triggers (more on triggers in Part 2). TRUNCATE does not."

---

## BLOCK B — Normalization & ER Design

*Open `02-normalization-and-er-design.sql`*

---

## Segment 8 — Data Modeling Principles (5 min)

*Navigate to Section 1*

> "Before we write any SQL, let's talk about the process of designing a database. Database design has three levels of abstraction."

> "Conceptual: what are the main 'things' in your business domain? For e-commerce: customers, products, orders. No SQL yet — just ideas and relationships."

> "Logical: what attributes does each entity have? What are the relationships between them? A customer has a name and email. A customer PLACES orders. Each order CONTAINS products."

> "Physical: how does this translate to actual SQL? What types? What constraints? This is the CREATE TABLE we write."

> "Good modeling eliminates redundancy, enforces integrity, and supports your queries efficiently. A database that has all the data but in the wrong structure is almost as bad as having no data at all."

---

## Segment 9 — ER Diagrams (7 min)

*Navigate to Section 2*

> "Entity-Relationship diagrams are the language of database design. They communicate the structure visually before writing any SQL."

> "Three things in an ER diagram: entities — things we track, drawn as rectangles. Attributes — properties of entities, drawn as ovals. Relationships — how entities connect, drawn as diamonds or lines."

> "The most important concept on an ER diagram is CARDINALITY — how many of each entity participates in the relationship."

> "One-to-one: one customer has one loyalty card. Rare in practice."

> "One-to-many: one customer places many orders. The 'many' side holds the foreign key. This is the most common relationship."

> "Many-to-many: many orders contain many products. You cannot represent this with just two tables and a foreign key — you need a BRIDGE TABLE. That's `order_items` — it has a foreign key to orders AND a foreign key to products."

*Point to the ASCII diagram in Section 2*

> "Look at this diagram. Read it left to right. Customer to Order is one-to-many. Order to OrderItem is one-to-many. OrderItem to Product is many-to-one. Product to Category is many-to-one."

> "When you're designing a new feature, always draw this first. Even on a whiteboard or a napkin. It prevents a lot of painful refactoring later."

> "Tools I recommend: dbdiagram.io is excellent — you write a text-based schema and it draws the diagram for you. DBeaver can auto-generate an ER diagram from an existing database."

---

## Segment 10 — The Unnormalized Problem (5 min)

*Navigate to Section 3*

> "Let's see WHY normalization exists — by looking at data that violates it."

*Point to the flat table in the comment block*

> "Imagine storing order data in a spreadsheet. Every order is one row. The items column contains 'Laptop:$999.00:2, Mouse:$29.99:1' — multiple values crammed in one cell."

> "How do you query that? How do you ask 'how many Laptops were sold this month?' You can't — not with SQL. SQL can't parse values inside a string."

> "And look at Alice Smith — her name and email appear in rows 1001 and 1002. If she changes her email, you have to find and update every row. Miss one, and now you have two different emails for Alice. Which is right? You don't know."

> "These are called anomalies — update anomalies, insert anomalies, delete anomalies. Normalization eliminates them."

---

## Segment 11 — 1NF, 2NF, 3NF (15 min)

**1NF:**

*Navigate to Section 4*

> "First Normal Form. Three rules: atomic values, unique rows, no repeating groups."

*Point to orders_unnormalized*

> "This table violates 1NF — product_names and quantities are comma-separated lists. One cell, multiple values. Atomic means 'indivisible' — each cell holds exactly one piece of information."

*Run the INSERT into orders_1nf, then run the SELECT*

> "Now each item is its own row. We can filter by product name. We can count quantities. The composite primary key (order_id, product_name) ensures we don't get duplicate rows."

> "But we still have a problem. Alice's email appears three times. If we update one, the others are stale. We need 2NF."

**2NF:**

*Navigate to Section 5*

> "Second Normal Form: no partial dependencies. Every non-key column must depend on the ENTIRE primary key — not just part of it."

> "In orders_1nf, the PK is (order_id, product_name). But customer_email only depends on order_id — not on the product. That's a partial dependency. And product_price only depends on product_name, not on the order."

> "The fix is decomposition — split the table into smaller tables where each table's columns depend on its entire key."

*Point to the four 2NF tables*

> "We now have customers_2nf, products_2nf, order_headers_2nf, and order_items_2nf. Alice's email exists in exactly one place. If she changes it, one UPDATE fixes it everywhere."

*Run the JOIN query at the bottom of Section 5*

> "We get the same information back via JOINs. More tables, more JOINs — but the data is clean and consistent."

**3NF:**

*Navigate to Section 6*

> "Third Normal Form: no transitive dependencies. All columns must depend directly on the primary key — not on another non-key column."

> "Imagine adding category_name and tax_rate to the products table. Then: product_id → category_id → tax_rate. tax_rate doesn't depend on the product — it depends on the category. That's transitive."

> "The fix: pull category_name and tax_rate into their own table — product_categories. Now products only stores category_id as a foreign key. The tax rate lives in one place."

*Run the verification SELECT*

> "We still get category_name and tax_rate in our query — via JOIN — but the data is stored only once."

---

## Segment 12 — Normalization Summary and Wrap-Up (5 min)

*Navigate to Section 7 — summary table*

> "Let me give you the one-liner for each normal form."

> "1NF: one value per cell, one row per thing, primary key exists."

> "2NF: 1NF plus every column depends on the WHOLE key, not part of it."

> "3NF: 2NF plus every column depends DIRECTLY on the key — no middlemen."

> "A common exam trick: 'The key, the whole key, and nothing but the key.' That's 3NF."

> "One final thought: sometimes you intentionally break normalization for performance. Read-heavy analytics databases often denormalize — store redundant data — so queries don't need expensive JOINs. This is called a data warehouse or OLAP schema. But for transactional applications like web apps, aim for 3NF."

> "This afternoon we build on this foundation with DML — actually inserting and updating data — and then advanced topics: indexes, views, stored procedures, transactions, and query optimization."

---

## Instructor Q&A Prompts

1. **"What's the difference between TRUNCATE and DELETE?"**  
   *(Expected: TRUNCATE removes all rows fast, resets sequences, no WHERE clause, doesn't fire triggers. DELETE is row-by-row, supports WHERE, fires triggers, can be slower on large tables.)*

2. **"Why is NUMERIC(10,2) better than FLOAT for storing prices?"**  
   *(Expected: FLOAT is approximate — binary floating point can't represent all decimal fractions exactly. NUMERIC is exact. 0.1 + 0.2 in float = 0.30000000000000004.)*

3. **"A table has columns: employee_id (PK), department_id, department_name. Is it in 3NF? Why or why not?"**  
   *(Expected: No — department_name transitively depends on employee_id via department_id. Fix: move department_name to a departments table.)*

4. **"When would you use ON DELETE CASCADE vs ON DELETE RESTRICT?"**  
   *(Expected: CASCADE when child records don't make sense without the parent — orders → order_items. RESTRICT when you want to protect important data — categories → products, so you don't accidentally delete a category and orphan products.)*
