# Day 22 Application — SQL Database Design: E-Commerce Database

## Overview

Design and build a fully normalized **e-commerce database** from scratch. You'll write DDL to create tables, define constraints, insert data, then write views, stored procedures, and transactions.

---

## Learning Goals

- Write DDL: `CREATE TABLE`, `ALTER TABLE`, `DROP TABLE`
- Apply all constraint types: `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `NOT NULL`, `CHECK`, `DEFAULT`
- Normalize a schema to 3NF
- Write `INSERT`, `UPDATE`, `DELETE` with cascades
- Create indexes, views, and stored procedures
- Use transactions with `BEGIN`, `COMMIT`, `ROLLBACK`

---

## Prerequisites

- PostgreSQL, MySQL, or SQLite
- Write your solution in `ecommerce.sql`
- Run each part in order (DDL → Data → Queries)

---

## Part 1 — Schema Design (ERD First!)

Before writing SQL, sketch an ERD with at least 6 entities:

```
customers(id, email, name, created_at)
products(id, name, description, price, stock_qty, category_id)
categories(id, name, parent_category_id)  ← self-referencing!
orders(id, customer_id, order_date, status, total)
order_items(id, order_id, product_id, quantity, unit_price)
reviews(id, customer_id, product_id, rating, comment, created_at)
```

---

## Part 2 — DDL: CREATE Tables (Tasks 1–6)

**Task 1** — Create `categories` table with a self-referencing FK for sub-categories.  
**Task 2** — Create `customers` with `UNIQUE` email, `NOT NULL` name, `DEFAULT NOW()` for created_at.  
**Task 3** — Create `products` with `CHECK (price > 0)`, `CHECK (stock_qty >= 0)`, FK to categories.  
**Task 4** — Create `orders` with `CHECK (status IN ('pending','processing','shipped','delivered','cancelled'))`.  
**Task 5** — Create `order_items`. Add a FK to orders with `ON DELETE CASCADE`.  
**Task 6** — Create `reviews` with `CHECK (rating BETWEEN 1 AND 5)`. Add a UNIQUE constraint on `(customer_id, product_id)` — one review per product per customer.

---

## Part 3 — Data: INSERT, UPDATE, DELETE (Tasks 7–9)

**Task 7** — Insert at least 3 categories, 5 products, 3 customers, and 2 orders with order items.  
**Task 8** — Write an `UPDATE` that applies a 10% discount to all products in a specific category.  
**Task 9** — Write a `DELETE` with a transaction: remove a customer and use `ROLLBACK` to undo it, then `COMMIT` to finalize it.

---

## Part 4 — Indexes & Views (Tasks 10–12)

**Task 10** — Create an index on `products.price` and another on `orders.customer_id`. Explain why.  
**Task 11** — Create a view `customer_order_summary` showing: customer name, total orders, total spent.  
**Task 12** — Create a view `product_ratings` showing: product name, avg rating, review count.

---

## Part 5 — Stored Procedure & Transaction (Tasks 13–14)

**Task 13** — Write a stored procedure `place_order(customer_id, product_id, quantity)` that:
  1. Checks if stock is available
  2. Creates an order and order_item record
  3. Decrements product stock
  4. Wraps everything in a transaction

**Task 14** — Write a trigger `update_order_total` that recalculates and updates `orders.total` whenever an `order_item` is inserted or updated.

---

## Submission Checklist

- [ ] All 6 tables created with correct constraints
- [ ] UNIQUE and CHECK constraints applied correctly
- [ ] Self-referencing FK on categories
- [ ] ON DELETE CASCADE on order_items
- [ ] Sample data inserted and queries produce results
- [ ] Transaction with ROLLBACK demonstrated
- [ ] At least 2 views created
- [ ] Stored procedure with transaction logic written
