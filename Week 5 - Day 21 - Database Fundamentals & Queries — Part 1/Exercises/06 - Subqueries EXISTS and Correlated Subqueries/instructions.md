# Exercise 06: Subqueries, EXISTS, and Correlated Subqueries

## Objective
Practice embedding queries inside other queries using single-row subqueries, multi-row subqueries, correlated subqueries, and `EXISTS`/`NOT EXISTS`.

## Background
A subquery is a `SELECT` statement nested inside another SQL statement. Subqueries let you compute values or sets dynamically rather than hard-coding them. A correlated subquery references a column from the outer query, making it re-execute for each outer row. Run `setup.sql` first.

## Requirements
Write one SQL query for each task. Label each with a comment.

1. **Products above average price (single-row subquery)** — Find all products whose price is above the average price of all products. Show `product_name` and `price`, ordered by `price` descending.
2. **Most expensive product per category (single-row correlated subquery)** — For each product, use a correlated subquery to check if its price equals the max price in its own category. Return `category`, `product_name`, `price`.
3. **Customers who ordered 'Laptop Pro' (multi-row subquery)** — Find all customers who placed at least one order containing the product `'Laptop Pro'`. Use `IN` with a subquery.
4. **Products never ordered (multi-row subquery with NOT IN)** — Find all products that have never appeared in any `order_items` row. Show `product_name` and `category`.
5. **Customers who have placed an order (EXISTS)** — Use `EXISTS` to find all customers who have at least one row in the `orders` table.
6. **Customers who have NEVER placed an order (NOT EXISTS)** — Use `NOT EXISTS` to find customers with no orders (compare your result to the `LEFT JOIN IS NULL` approach from Ex 04).
7. **Orders above the customer's own average (correlated subquery)** — Find all orders whose total item cost is greater than the average order total for that same customer. Show `customer_id`, `order_id`, and the order's `total`.

## Hints
- A subquery in `WHERE price > (SELECT AVG(...))` is a single-row subquery — it must return exactly one value.
- `IN (SELECT col FROM ...)` works with multi-row subqueries that return one column.
- In a correlated subquery, the inner query references `outer_alias.column` — it re-runs for each outer row.
- `EXISTS (SELECT 1 FROM ... WHERE ...)` is often faster than `IN` on large datasets because it short-circuits on the first match.

## Expected Output

**Query 1** — products above avg price (~$196):
```
product_name   | price
---------------+--------
Standing Desk  | 799.00
Laptop Pro     | 999.99
Office Chair   | 349.00
Smart Watch    | 299.99
Coffee Maker   | 129.99
```
*(exact rows depend on your avg)*

**Query 4** — products never ordered:
```
product_name         | category
---------------------+-----------
Cotton T-Shirt       | Clothing
...
```
*(several products with no order_items rows)*

**Query 6** — customers who never ordered:
```
first_name | last_name
-----------+----------
Carol      | Williams
Frank      | Davis
Grace      | Taylor
Iris       | Lee
```
