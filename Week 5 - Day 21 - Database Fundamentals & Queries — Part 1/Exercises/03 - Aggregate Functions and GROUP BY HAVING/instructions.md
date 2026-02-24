# Exercise 03: Aggregate Functions and GROUP BY HAVING

## Objective
Practice summarising data with `COUNT`, `SUM`, `AVG`, `MIN`, `MAX` and controlling grouped results with `GROUP BY` and `HAVING`.

## Background
Aggregate functions collapse many rows into a single summary value. `GROUP BY` lets you compute aggregates per group, and `HAVING` filters those groups — the equivalent of `WHERE` but for aggregated results. Run `setup.sql` first.

## Requirements
Write one SQL query for each task. Label each with a comment.

1. **Total products** — Count the total number of rows in the `products` table.
2. **Count by category** — Count how many products exist in each category; show `category` and `product_count`, ordered by `product_count` descending.
3. **Price stats** — Show the `MIN`, `MAX`, and `AVG` price across all products (round average to 2 decimal places).
4. **Total stock value per category** — Calculate `SUM(price * stock)` as `stock_value` for each category, ordered by `stock_value` descending.
5. **Categories with more than 2 products** — Reuse the count-by-category query but add a `HAVING` clause to show only categories with more than 2 products.
6. **High-value categories** — Show categories where the average product price exceeds $100, including `category` and `avg_price` (rounded to 2 dp), ordered by `avg_price` descending.
7. **Order counts per customer** — Join `orders` to `customers` and count orders per customer; show `first_name`, `last_name`, and `order_count`, ordered by `order_count` descending.
8. **Total revenue per order** — Sum `quantity * unit_price` as `total` for each `order_id` in `order_items`; show `order_id` and `total`, ordered by `total` descending.

## Hints
- Aggregate functions (`COUNT`, `SUM`, `AVG`, `MIN`, `MAX`) ignore `NULL` values.
- Every non-aggregated column in `SELECT` must appear in `GROUP BY`.
- `HAVING` filters after aggregation; `WHERE` filters before aggregation.
- Use `ROUND(AVG(price), 2)` to limit decimal places.

## Expected Output

**Query 2** — products per category:
```
category    | product_count
------------+--------------
Stationery  | 2
Sports      | 2
Kitchen     | 2
Furniture   | 2
Books       | 2
Electronics | 3
Clothing    | 2
```

**Query 5** — categories with > 2 products:
```
category    | product_count
------------+--------------
Electronics | 3
```

**Query 6** — avg price > $100:
```
category   | avg_price
-----------+-----------
Furniture  | 574.00
Electronics| 459.99
Kitchen    | 109.99
```
