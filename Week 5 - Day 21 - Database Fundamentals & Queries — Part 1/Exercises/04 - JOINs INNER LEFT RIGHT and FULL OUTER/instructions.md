# Exercise 04: JOINs — INNER, LEFT, RIGHT, and FULL OUTER

## Objective
Practice joining related tables with `INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN`, and `FULL OUTER JOIN`, and understand when each type is appropriate.

## Background
Relational databases store related data in separate tables and use foreign keys to link them. A `JOIN` combines rows from two or more tables based on a matching condition. Different join types control whether unmatched rows are included or excluded. Run `setup.sql` first.

## Requirements
Write one SQL query for each task. Label each with a comment.

1. **Customer orders (INNER JOIN)** — Join `orders` to `customers`. Show `first_name`, `last_name`, `order_id`, and `order_date`. Only include customers who have at least one order.
2. **All customers with or without orders (LEFT JOIN)** — Show all customers and their `order_id`/`order_date`. Customers with no orders should still appear with `NULL` in the order columns.
3. **Count orders per customer (LEFT JOIN + aggregate)** — Show `first_name`, `last_name`, and a count of orders for every customer, including those with zero orders. Label the count `order_count`.
4. **Order items with product details (INNER JOIN)** — Join `order_items` to `products`. Show `order_id`, `product_name`, `quantity`, and `unit_price`.
5. **Customers with no orders (LEFT JOIN + IS NULL)** — Using a `LEFT JOIN`, find all customers who have never placed an order.
6. **All customers and all products (RIGHT JOIN demo)** — For the purpose of practising `RIGHT JOIN`, write a query that returns all products and any matching `order_items` rows. Products with no orders should still appear.
7. **FULL OUTER JOIN customers and orders** — Show all customers and all orders even when there is no match on either side. (In PostgreSQL/standard SQL this is `FULL OUTER JOIN`.)

## Hints
- `INNER JOIN` returns only matched rows; unmatched rows from either table are dropped.
- `LEFT JOIN` keeps all rows from the left table; unmatched right-side values are `NULL`.
- To find rows with no match, filter on `RIGHT_TABLE.pk IS NULL` after a `LEFT JOIN`.
- `FULL OUTER JOIN` = left join ∪ right join.

## Expected Output

**Query 5** — customers with no orders:
```
first_name | last_name | order_id
-----------+-----------+---------
Carol      | Williams  | NULL
Frank      | Davis     | NULL
Grace      | Taylor    | NULL
Iris       | Lee       | NULL
```
*(Your results may include more rows depending on the data.)*

**Query 2** — first 5 rows (LEFT JOIN, all customers):
```
first_name | last_name | order_id | order_date
-----------+-----------+----------+------------
Alice      | Smith     | 1        | 2024-01-05
Alice      | Smith     | 3        | 2024-02-03
Alice      | Smith     | 13       | 2024-07-04
Bob        | Johnson   | 2        | 2024-01-12
Bob        | Johnson   | 7        | 2024-04-02
```
